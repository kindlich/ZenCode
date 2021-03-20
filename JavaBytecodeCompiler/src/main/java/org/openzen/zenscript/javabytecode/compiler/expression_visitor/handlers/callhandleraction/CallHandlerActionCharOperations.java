package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.javabytecode.compiler.JavaExpressionVisitorJavaMembers;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;

import static org.openzen.zenscript.codemodel.type.member.BuiltinID.*;

public class CallHandlerActionCharOperations extends AbstractCallHandlerAction {

	public CallHandlerActionCharOperations(JavaExpressionVisitor ownerVisitor) {
		super(ownerVisitor,
				CHAR_ADD_INT,
				CHAR_SUB_INT,
				CHAR_SUB_CHAR,
				CHAR_REMOVE_DIACRITICS,
				CHAR_TO_LOWER_CASE,
				CHAR_TO_UPPER_CASE);
	}

	@Override
	protected void handleInvocation(CallExpression expression) {
		switch (expression.member.getBuiltin()) {
			case CHAR_ADD_INT:
				javaWriter.iAdd();
				break;
			case CHAR_SUB_INT:
			case CHAR_SUB_CHAR:
				javaWriter.iSub();
				break;
			case CHAR_REMOVE_DIACRITICS:
				throw new UnsupportedOperationException("Not yet supported!");
			case CHAR_TO_LOWER_CASE:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.CHARACTER_TO_LOWER_CASE);
				break;
			case CHAR_TO_UPPER_CASE:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.CHARACTER_TO_UPPER_CASE);
				break;
		}
	}
}
