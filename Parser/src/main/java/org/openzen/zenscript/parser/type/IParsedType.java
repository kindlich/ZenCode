/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zenscript.parser.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openzen.zenscript.codemodel.type.ITypeID;
import org.openzen.zenscript.codemodel.type.member.TypeMembers;
import org.openzen.zenscript.lexer.ZSTokenParser;
import org.openzen.zenscript.lexer.ZSTokenType;
import static org.openzen.zenscript.lexer.ZSTokenType.*;
import org.openzen.zenscript.linker.BaseScope;
import org.openzen.zenscript.parser.ParseException;
import org.openzen.zenscript.parser.definitions.ParsedFunctionHeader;
import org.openzen.zenscript.parser.definitions.ParsedGenericParameter;
import org.openzen.zenscript.shared.CodePosition;

/**
 *
 * @author Hoofdgebruiker
 */
public interface IParsedType {
	public static IParsedType parse(ZSTokenParser tokens) {
		IParsedType result = tryParse(tokens);
		if (result == null)
			throw new ParseException(tokens.getPosition(), "Type expected (got " + tokens.peek().content + ")");
		
		return result;
	}
	
	public static IParsedType tryParse(ZSTokenParser tokens) {
		int modifiers = 0;
		while (true) {
			if (tokens.optional(ZSTokenType.K_CONST) != null) {
				modifiers |= TypeMembers.MODIFIER_CONST;
			} else {
				break;
			}
		}
		
		IParsedType result;
		switch (tokens.peek().type) {
			case K_VOID:
				tokens.next();
				result = ParsedTypeBasic.VOID;
				break;
			case K_ANY:
				tokens.next();
				result = ParsedTypeBasic.ANY;
				break;
			case K_BOOL:
				tokens.next();
				result = ParsedTypeBasic.BOOL;
				break;
			case K_BYTE:
				tokens.next();
				result = ParsedTypeBasic.BYTE;
				break;
			case K_SBYTE:
				tokens.next();
				result = ParsedTypeBasic.SBYTE;
				break;
			case K_SHORT:
				tokens.next();
				result = ParsedTypeBasic.SHORT;
				break;
			case K_USHORT:
				tokens.next();
				result = ParsedTypeBasic.USHORT;
				break;
			case K_INT:
				tokens.next();
				result = ParsedTypeBasic.INT;
				break;
			case K_UINT:
				tokens.next();
				result = ParsedTypeBasic.UINT;
				break;
			case K_LONG:
				tokens.next();
				result = ParsedTypeBasic.LONG;
				break;
			case K_ULONG:
				tokens.next();
				result = ParsedTypeBasic.ULONG;
				break;
			case K_FLOAT:
				tokens.next();
				result = ParsedTypeBasic.FLOAT;
				break;
			case K_DOUBLE:
				tokens.next();
				result = ParsedTypeBasic.DOUBLE;
				break;
			case K_CHAR:
				tokens.next();
				result = ParsedTypeBasic.CHAR;
				break;
			case K_STRING:
				tokens.next();
				result = ParsedTypeBasic.STRING;
				break;
			case K_FUNCTION: {
				CodePosition position = tokens.getPosition();
				tokens.next();
				ParsedFunctionHeader header = ParsedFunctionHeader.parse(tokens);
				result = new ParsedFunctionType(position, header);
				break;
			}
			case T_IDENTIFIER: {
				CodePosition position = tokens.getPosition();
				List<ParsedNamedType.ParsedNamePart> name = new ArrayList<>();
				do {
					String namePart = tokens.required(ZSTokenType.T_IDENTIFIER, "identifier expected").content;
					List<IParsedType> generic = parseGenericParameters(tokens);
					name.add(new ParsedNamedType.ParsedNamePart(namePart, generic));
				} while (tokens.optional(ZSTokenType.T_DOT) != null);
				
				result = new ParsedNamedType(position, name);
				break;
			}
			default:
				return null;
		}
		
		outer: while (true) {
			switch (tokens.peek().type) {
				case T_DOT2: {
					tokens.next();
					IParsedType to = parse(tokens);
					result = new ParsedTypeRange(result, to);
					break;
				}
				case T_SQOPEN:
					tokens.next();
					int dimension = 1;
					while (tokens.optional(ZSTokenType.T_COMMA) != null)
						dimension++;
					
					if (tokens.optional(ZSTokenType.T_SQCLOSE) != null) {
						result = new ParsedTypeArray(result, dimension);
					} else if (tokens.isNext(T_LESS)) {
						tokens.next();
						ParsedGenericParameter parameter = ParsedGenericParameter.parse(tokens);
						tokens.required(T_GREATER, "> expected");
						result = new ParsedTypeGenericMap(parameter, result, modifiers);
						tokens.required(ZSTokenType.T_SQCLOSE, "] expected");
					} else {
						IParsedType keyType = parse(tokens);
						result = new ParsedTypeAssociative(keyType, result);
						tokens.required(ZSTokenType.T_SQCLOSE, "] expected");
					}
					break;
				case T_QUEST:
					tokens.next();
					result = result.withOptional();
					break;
				default:
					break outer;
			}
		}
		
		if (modifiers > 0)
			result = result.withModifiers(modifiers);
		
		return result;
	}
	
	public static List<IParsedType> parseGenericParameters(ZSTokenParser tokens) {
		if (!tokens.isNext(T_LESS))
			return null;
		
		tokens.pushMark();
		tokens.next();
		
		List<IParsedType> genericParameters = new ArrayList<>();
		do {
			IParsedType type = tryParse(tokens);
			if (type == null) {
				tokens.reset();
				return Collections.emptyList();
			}
			genericParameters.add(type);
		} while (tokens.optional(T_COMMA) != null);
		
		if (tokens.isNext(T_SHR)) {
			tokens.replace(T_GREATER.flyweight);
		} else if (tokens.isNext(T_USHR)) {
			tokens.replace(T_SHR.flyweight);
		} else if (tokens.isNext(T_SHRASSIGN)) {
			tokens.replace(T_GREATEREQ.flyweight);
		} else if (tokens.isNext(T_USHRASSIGN)) {
			tokens.replace(T_SHRASSIGN.flyweight);
		} else if (tokens.optional(T_GREATER) == null) {
			tokens.reset();
			return Collections.emptyList();
		}
		
		tokens.popMark();
		return genericParameters;
	}
	
	public static ITypeID[] compileList(List<IParsedType> typeParameters, BaseScope scope) {
		ITypeID[] result = null;
		if (typeParameters != null) {
			result = new ITypeID[typeParameters.size()];
			for (int i = 0; i < typeParameters.size(); i++) {
				result[i] = typeParameters.get(i).compile(scope);
			}
		}
		return result;
	}
	
	public IParsedType withOptional();
	
	public IParsedType withModifiers(int modifiers);
	
	public ITypeID compile(BaseScope scope);
}
