package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.codemodel.type.member.BuiltinID;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;

public class CallHandlerActionObject extends AbstractCallHandlerAction {

	public CallHandlerActionObject(JavaExpressionVisitor ownerVisitor) {
		super(ownerVisitor, BuiltinID.OBJECT_SAME, BuiltinID.OBJECT_NOTSAME);
	}

	@Override
	protected void handleInvocation(CallExpression expression) {
		switch (expression.member.getBuiltin()) {
			case OBJECT_SAME:
				javaWriter.objectsSame();
				break;
			case OBJECT_NOTSAME:
				javaWriter.objectsNotSame();
				break;
		}
	}
}
