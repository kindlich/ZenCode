/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zenscript.parser.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.openzen.zencode.shared.CodePosition;
import org.openzen.zencode.shared.CompileException;
import org.openzen.zencode.shared.CompileExceptionCode;
import org.openzen.zenscript.lexer.ZSTokenType;
import org.openzen.zenscript.codemodel.FunctionHeader;
import org.openzen.zenscript.codemodel.expression.CallArguments;
import org.openzen.zenscript.codemodel.expression.Expression;
import org.openzen.zenscript.codemodel.partial.IPartialExpression;
import org.openzen.zenscript.codemodel.type.ITypeID;
import org.openzen.zenscript.codemodel.type.member.DefinitionMemberGroup;
import org.openzen.zenscript.lexer.ZSTokenParser;
import org.openzen.zenscript.codemodel.scope.BaseScope;
import org.openzen.zenscript.codemodel.scope.ExpressionScope;
import org.openzen.zenscript.parser.type.IParsedType;

/**
 *
 * @author Hoofdgebruiker
 */
public class ParsedCallArguments {
	public static final ParsedCallArguments NONE = new ParsedCallArguments(null, Collections.emptyList());
	
	public static ParsedCallArguments parse(ZSTokenParser tokens) {
		List<IParsedType> typeArguments = null;
		if (tokens.optional(ZSTokenType.T_LESS) != null) {
			typeArguments = new ArrayList<>();
			do {
				IParsedType type = IParsedType.parse(tokens);
				typeArguments.add(type);
			} while (tokens.optional(ZSTokenType.T_COMMA) != null);
			tokens.required(ZSTokenType.T_GREATER, "> expected");
		}
		
		tokens.required(ZSTokenType.T_BROPEN, "( expected");
		
		List<ParsedExpression> arguments = new ArrayList<>();
		if (tokens.optional(ZSTokenType.T_BRCLOSE) == null) {
			do {
				arguments.add(ParsedExpression.parse(tokens));
			} while (tokens.optional(ZSTokenType.T_COMMA) != null);
			tokens.required(ZSTokenType.T_BRCLOSE, ") expected");
		}
		
		return new ParsedCallArguments(typeArguments, arguments);
	}
	
	public static ParsedCallArguments parseForAnnotation(ZSTokenParser tokens) {
		List<IParsedType> typeArguments = null;
		if (tokens.optional(ZSTokenType.T_LESS) != null) {
			typeArguments = new ArrayList<>();
			do {
				IParsedType type = IParsedType.parse(tokens);
				typeArguments.add(type);
			} while (tokens.optional(ZSTokenType.T_COMMA) != null);
			tokens.required(ZSTokenType.T_GREATER, "> expected");
		}
		
		List<ParsedExpression> arguments = new ArrayList<>();
		if (tokens.isNext(ZSTokenType.T_BROPEN)) {
			tokens.required(ZSTokenType.T_BROPEN, "( expected");
			if (tokens.optional(ZSTokenType.T_BRCLOSE) == null) {
				do {
					arguments.add(ParsedExpression.parse(tokens));
				} while (tokens.optional(ZSTokenType.T_COMMA) != null);
				tokens.required(ZSTokenType.T_BRCLOSE, ") expected");
			}
		}
		
		return new ParsedCallArguments(typeArguments, arguments);
	}
	
	private final List<IParsedType> typeArguments;
	public final List<ParsedExpression> arguments;
	
	public ParsedCallArguments(List<IParsedType> typeArguments, List<ParsedExpression> arguments) {
		this.typeArguments = typeArguments;
		this.arguments = arguments;
	}
	
	public CallArguments compileCall(
			CodePosition position, 
			ExpressionScope scope,
			ITypeID[] genericParameters,
			DefinitionMemberGroup member)
	{
		List<FunctionHeader> possibleHeaders = member.getMethodMembers().stream()
				.map(method -> method.member.header)
				.collect(Collectors.toList());
		return compileCall(position, scope, genericParameters, possibleHeaders);
	}
	
	public CallArguments compileCall(
			CodePosition position,
			ExpressionScope scope,
			ITypeID[] genericParameters,
			List<FunctionHeader> candidateFunctions)
	{
		if (this.typeArguments != null) {
			genericParameters = new ITypeID[typeArguments.size()];
			for (int i = 0; i < typeArguments.size(); i++)
				genericParameters[i] = typeArguments.get(i).compile(scope);
		}
		
		List<FunctionHeader> candidates = new ArrayList<>();
		for (FunctionHeader header : candidateFunctions) {
			if (isCompatibleWith(scope, header, genericParameters))
				candidates.add(header);
		}
		
		if (candidates.isEmpty()) {
			StringBuilder explanation = new StringBuilder();
			CallArguments arguments = compileCallNaive(position, scope);
			for (FunctionHeader candidate : candidateFunctions)
				explanation.append(candidate.explainWhyIncompatible(scope, arguments)).append("\n");
			throw new CompileException(position, CompileExceptionCode.CALL_NO_VALID_METHOD, "No compatible methods found: \n" + explanation.toString());
		}

		ExpressionScope innerScope = scope;
		if (candidates.size() == 1) {
			innerScope = scope.forCall(candidates.get(0));
		} else {
			candidates = candidates.stream()
					.filter(candidate -> candidate.typeParameters == null)
					.collect(Collectors.toList());
			
			if (candidates.isEmpty()) {
				throw new CompileException(position, CompileExceptionCode.CALL_NO_VALID_METHOD, "Could not determine call type parameters");
			}
		}
		
		List<ITypeID>[] predictedTypes = new List[arguments.size()];
		for (int i = 0; i < predictedTypes.length; i++)
			predictedTypes[i] = new ArrayList<>();
		
		for (FunctionHeader header : candidates) {
			for (int i = 0; i < arguments.size(); i++) {
				if (!predictedTypes[i].contains(header.parameters[i].type))
					predictedTypes[i].add(header.parameters[i].type);
			}
		}
		
		Expression[] cArguments = new Expression[arguments.size()];
		for (int i = 0; i < cArguments.length; i++) {
			IPartialExpression cArgument = arguments.get(i).compile(innerScope.withHints(predictedTypes[i]));
			cArguments[i] = cArgument.eval();
		}
		
		ITypeID[] typeParameters = genericParameters;
		if (typeParameters == null) {
			for (FunctionHeader candidate : candidates) {
				if (candidate.typeParameters != null) {
					typeParameters = new ITypeID[candidate.typeParameters.length];
					for (int i = 0; i < typeParameters.length; i++) {
						if (innerScope.genericInferenceMap.get(candidate.typeParameters[i]) == null)
							throw new CompileException(position, CompileExceptionCode.TYPE_ARGUMENTS_NOT_INFERRABLE, "Could not infer type parameter " + candidate.typeParameters[i].name);
						else
							typeParameters[i] = innerScope.genericInferenceMap.get(candidate.typeParameters[i]);
					}

					break;
				}
			}
		}
		
		return new CallArguments(typeParameters, cArguments);
	}
	
	
	public CallArguments compileCall(
			CodePosition position,
			ExpressionScope scope,
			ITypeID[] genericParameters,
			FunctionHeader function)
	{
		ExpressionScope innerScope = scope.forCall(function);
		
		List<ITypeID>[] predictedTypes = new List[arguments.size()];
		for (int i = 0; i < predictedTypes.length; i++) {
			predictedTypes[i] = new ArrayList<>();
			predictedTypes[i].add(function.parameters[i].type);
		}
		
		Expression[] cArguments = new Expression[arguments.size()];
		for (int i = 0; i < cArguments.length; i++) {
			IPartialExpression cArgument = arguments.get(i).compile(innerScope.withHints(predictedTypes[i]));
			cArguments[i] = cArgument.eval();
		}
		
		ITypeID[] typeParameters = genericParameters;
		if (typeParameters == null) {
			if (function.typeParameters != null) {
				typeParameters = new ITypeID[function.typeParameters.length];
				for (int i = 0; i < typeParameters.length; i++) {
					if (innerScope.genericInferenceMap.get(function.typeParameters[i]) == null)
						throw new CompileException(position, CompileExceptionCode.TYPE_ARGUMENTS_NOT_INFERRABLE, "Could not infer type parameter " + function.typeParameters[i].name);
					else
						typeParameters[i] = innerScope.genericInferenceMap.get(function.typeParameters[i]);
				}
			}
		}
		
		return new CallArguments(typeParameters, cArguments);
	}
	
	private CallArguments compileCallNaive(CodePosition position, ExpressionScope scope) {
		Expression[] cArguments = new Expression[arguments.size()];
		for (int i = 0; i < cArguments.length; i++) {
			IPartialExpression cArgument = arguments.get(i).compile(scope);
			cArguments[i] = cArgument.eval();
		}
		return new CallArguments(new ITypeID[0], cArguments);
	}
	
	private boolean isCompatibleWith(BaseScope scope, FunctionHeader header, ITypeID[] typeParameters) {
		if (arguments.size() != header.parameters.length)
			return false;
		
		for (int i = 0; i < arguments.size(); i++) {
			if (typeParameters == null && header.typeParameters != null && header.parameters[i].type.hasInferenceBlockingTypeParameters(header.typeParameters))
				return false;
			
			if (!arguments.get(i).isCompatibleWith(scope, header.parameters[i].type))
				return false;
		}
		
		return true;
	}
}
