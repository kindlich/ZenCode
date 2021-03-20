package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.codemodel.expression.Expression;
import org.openzen.zenscript.codemodel.type.member.BuiltinID;
import org.openzen.zenscript.javabytecode.JavaBytecodeContext;
import org.openzen.zenscript.javabytecode.compiler.JavaModificationExpressionVisitor;
import org.openzen.zenscript.javabytecode.compiler.JavaWriter;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;

import java.util.EnumSet;

public abstract class AbstractCallHandlerAction {
	private final EnumSet<BuiltinID> supportedBuiltins;
	protected final JavaExpressionVisitor ownerVisitor;
	protected final JavaWriter javaWriter;
	protected final JavaBytecodeContext context;

	public AbstractCallHandlerAction(JavaExpressionVisitor ownerVisitor, BuiltinID supportedBuiltin, BuiltinID... additionalSupportedBuiltins) {
		this.ownerVisitor = ownerVisitor;
		this.javaWriter = ownerVisitor.javaWriter;
		this.supportedBuiltins = EnumSet.of(supportedBuiltin, additionalSupportedBuiltins);
		this.context = ownerVisitor.context;
	}

	public EnumSet<BuiltinID> getSupportedBuiltins() {
		return supportedBuiltins;
	}

	public void handleCallAction(CallExpression expression) {
		handleTarget(expression);
		handleParameters(expression);
		handleInvocation(expression);
	}

	protected void modify(Expression source, Runnable modification, JavaModificationExpressionVisitor.PushOption push) {
		ownerVisitor.modify(source, modification, push);
	}

	protected void handleTarget(CallExpression expression) {
		expression.target.accept(ownerVisitor);
	}

	protected void handleParameters(CallExpression expression) {
		for (Expression argument : expression.arguments.arguments) {
			argument.accept(ownerVisitor);
		}
	}

	protected abstract void handleInvocation(CallExpression expression);
}
