/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zenscript.codemodel.expression;

import org.openzen.zenscript.codemodel.type.BasicTypeID;
import org.openzen.zenscript.codemodel.type.ITypeID;
import org.openzen.zenscript.shared.CodePosition;

/**
 *
 * @author Hoofdgebruiker
 */
public class IsExpression extends Expression {
	public final Expression value;
	public final ITypeID isType;
	
	public IsExpression(CodePosition position, Expression value, ITypeID type) {
		super(position, BasicTypeID.BOOL, value.thrownType);
		
		this.value = value;
		this.isType = type;
	}

	@Override
	public <T> T accept(ExpressionVisitor<T> visitor) {
		return visitor.visitIs(this);
	}

	@Override
	public Expression transform(ExpressionTransformer transformer) {
		Expression tValue = value.transform(transformer);
		return tValue == value ? this : new IsExpression(position, tValue, type);
	}
}
