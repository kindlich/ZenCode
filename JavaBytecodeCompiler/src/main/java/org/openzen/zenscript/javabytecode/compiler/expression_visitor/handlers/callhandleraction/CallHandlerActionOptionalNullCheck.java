package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.objectweb.asm.Label;
import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.codemodel.type.member.BuiltinID;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;

public class CallHandlerActionOptionalNullCheck extends AbstractCallHandlerAction {

	public CallHandlerActionOptionalNullCheck(JavaExpressionVisitor ownerVisitor) {
		super(ownerVisitor, BuiltinID.OPTIONAL_IS_NULL, BuiltinID.OPTIONAL_IS_NOT_NULL);
	}

	@Override
	protected void handleParameters(CallExpression expression) {
		//Handled in handleInvocation
	}

	@Override
	protected void handleInvocation(CallExpression expression) {

		final Label isFalse = new Label();
		final Label end = new Label();

		if (expression.member.getBuiltin() == BuiltinID.OPTIONAL_IS_NULL)
			javaWriter.ifNonNull(isFalse);
		else
			javaWriter.ifNull(isFalse);
		javaWriter.iConst1();
		javaWriter.goTo(end);
		javaWriter.label(isFalse);
		javaWriter.iConst0();
		javaWriter.label(end);

	}
}
