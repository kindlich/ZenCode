/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zenscript.parser.expression;

import org.openzen.zencode.shared.CodePosition;
import org.openzen.zencode.shared.CompileException;
import org.openzen.zencode.shared.CompileExceptionCode;
import org.openzen.zenscript.codemodel.HighLevelDefinition;
import org.openzen.zenscript.codemodel.expression.Expression;
import org.openzen.zenscript.codemodel.expression.TryConvertExpression;
import org.openzen.zenscript.codemodel.partial.IPartialExpression;
import org.openzen.zenscript.codemodel.type.DefinitionTypeID;
import org.openzen.zenscript.codemodel.scope.ExpressionScope;
import org.openzen.zenscript.codemodel.type.ITypeID;
import org.openzen.zenscript.parser.PrecompilationState;

/**
 *
 * @author Hoofdgebruiker
 */
public class ParsedTryConvertExpression extends ParsedExpression {
	private final ParsedExpression value;
	
	public ParsedTryConvertExpression(CodePosition position, ParsedExpression value) {
		super(position);
		
		this.value = value;
	}

	@Override
	public IPartialExpression compile(ExpressionScope scope) {
		Expression cValue = value.compile(scope).eval();
		if (scope.getFunctionHeader() == null)
			throw new CompileException(position, CompileExceptionCode.TRY_CONVERT_OUTSIDE_FUNCTION, "try? can only be used inside functions");
		
		HighLevelDefinition result = scope.getTypeRegistry().stdlib.getDefinition("Result");
		if (cValue.thrownType != null) {
			// this function throws
			DefinitionTypeID resultType = scope.getTypeRegistry().getForDefinition(result, cValue.type, cValue.thrownType);
			return new TryConvertExpression(position, resultType, cValue);
		} else {
			throw new CompileException(position, CompileExceptionCode.TRY_CONVERT_ILLEGAL_TARGET, "try? can only be used on expressions that throw");
		}
	}

	@Override
	public boolean hasStrongType() {
		return true;
	}

	@Override
	public ITypeID precompileForType(ExpressionScope scope, PrecompilationState state) {
		return null; // TODO: too lazy to implement
	}
}
