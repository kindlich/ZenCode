/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zenscript.codemodel.expression;

import org.openzen.zenscript.codemodel.type.GlobalTypeRegistry;
import org.openzen.zenscript.codemodel.type.ITypeID;
import org.openzen.zenscript.shared.CodePosition;

/**
 *
 * @author Hoofdgebruiker
 */
public class RangeExpression extends Expression {
	public final Expression from;
	public final Expression to;
	
	public RangeExpression(CodePosition position, GlobalTypeRegistry registry, Expression from, Expression to) {
		super(position, registry.getRange(from.type, to.type), binaryThrow(position, from.thrownType, to.thrownType));
	
		this.from = from;
		this.to = to;
	}
	
	private RangeExpression(CodePosition position, ITypeID type, Expression from, Expression to, ITypeID thrownType) {
		super(position, type, thrownType);
		
		this.from = from;
		this.to = to;
	}

	@Override
	public <T> T accept(ExpressionVisitor<T> visitor) {
		return visitor.visitRange(this);
	}

	@Override
	public Expression transform(ExpressionTransformer transformer) {
		Expression tFrom = from.transform(transformer);
		Expression tTo = to.transform(transformer);
		return tFrom == from && tTo == to ? this : new RangeExpression(position, type, tFrom, tTo, thrownType);
	}
}
