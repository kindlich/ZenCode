package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.codemodel.type.member.BuiltinID;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;

public class CallHandlerActionFunction extends AbstractCallHandlerAction {

	public CallHandlerActionFunction(JavaExpressionVisitor ownerVisitor) {
		super(ownerVisitor,
				BuiltinID.FUNCTION_SAME,
				BuiltinID.FUNCTION_NOTSAME,
				BuiltinID.FUNCTION_CALL);
	}

	@Override
	protected void handleInvocation(CallExpression expression) {
		switch (expression.member.getBuiltin()) {
			case FUNCTION_SAME:
				javaWriter.objectsSame();
				break;
			case FUNCTION_NOTSAME:
				javaWriter.objectsNotSame();
				break;
			case FUNCTION_CALL:
				javaWriter.invokeInterface(context.getFunctionalInterface(expression.target.type));
				break;
		}
	}
}
