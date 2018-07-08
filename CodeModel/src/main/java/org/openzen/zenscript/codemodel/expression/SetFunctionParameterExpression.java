/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zenscript.codemodel.expression;

import org.openzen.zencode.shared.CodePosition;
import org.openzen.zenscript.codemodel.FunctionParameter;

/**
 *
 * @author Hoofdgebruiker
 */
public class SetFunctionParameterExpression extends Expression {
	public final FunctionParameter parameter;
	public final Expression value;
	
	public SetFunctionParameterExpression(CodePosition position, FunctionParameter parameter, Expression value) {
		super(position, parameter.type, value.thrownType);
		
		this.parameter = parameter;
		this.value = value;
	}

	@Override
	public <T> T accept(ExpressionVisitor<T> visitor) {
		return visitor.visitSetFunctionParameter(this);
	}

	@Override
	public Expression transform(ExpressionTransformer transformer) {
		Expression tValue = value.transform(transformer);
		return tValue == value ? this : new SetFunctionParameterExpression(position, parameter, tValue);
	}
}
