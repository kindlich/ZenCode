package org.openzen.zenscript.parser.expression;

import org.openzen.zencode.shared.CodePosition;
import org.openzen.zencode.shared.CompileException;
import org.openzen.zenscript.codemodel.expression.Expression;
import org.openzen.zenscript.codemodel.expression.IsExpression;
import org.openzen.zenscript.codemodel.partial.IPartialExpression;
import org.openzen.zenscript.codemodel.scope.ExpressionScope;
import org.openzen.zenscript.codemodel.type.TypeID;
import org.openzen.zenscript.parser.type.IParsedType;

public class ParsedExpressionIs extends ParsedExpression {
	private final ParsedExpression expression;
	private final IParsedType type;

	public ParsedExpressionIs(CodePosition position, ParsedExpression expression, IParsedType type) {
		super(position);

		this.expression = expression;
		this.type = type;
	}

	@Override
	public IPartialExpression compile(ExpressionScope scope) throws CompileException {
		TypeID isType = type.compile(scope);
		Expression expression = this.expression.compile(scope.withHint(isType)).eval();
		return new IsExpression(position, expression, isType);
	}

	@Override
	public boolean hasStrongType() {
		return true;
	}
}
