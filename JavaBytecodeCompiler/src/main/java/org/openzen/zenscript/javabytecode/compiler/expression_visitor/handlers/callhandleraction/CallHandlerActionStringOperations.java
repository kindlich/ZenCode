package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.javabytecode.compiler.JavaExpressionVisitorJavaMembers;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;

import static org.openzen.zenscript.codemodel.type.member.BuiltinID.*;

public class CallHandlerActionStringOperations extends AbstractCallHandlerAction {
	public CallHandlerActionStringOperations(JavaExpressionVisitor ownerVisitor) {
		super(ownerVisitor,
				STRING_ADD_STRING,
				STRING_INDEXGET,
				STRING_REMOVE_DIACRITICS,
				STRING_TRIM,
				STRING_TO_LOWER_CASE,
				STRING_TO_UPPER_CASE);
	}

	@Override
	protected void handleInvocation(CallExpression expression) {
		switch (expression.member.getBuiltin()) {
			case STRING_ADD_STRING:
				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.STRING_CONCAT);
				break;
			case STRING_INDEXGET:
				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.STRING_CHAR_AT);
				break;
			case STRING_REMOVE_DIACRITICS:
				throw new UnsupportedOperationException("Not yet supported!");
			case STRING_TRIM:
				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.STRING_TRIM);
				break;
			case STRING_TO_LOWER_CASE:
				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.STRING_TO_LOWER_CASE);
				break;
			case STRING_TO_UPPER_CASE:
				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.STRING_TO_UPPER_CASE);
				break;
		}
	}
}
