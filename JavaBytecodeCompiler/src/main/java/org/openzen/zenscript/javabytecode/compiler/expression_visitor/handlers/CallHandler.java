package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers;

import org.openzen.zenscript.codemodel.FunctionParameter;
import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.codemodel.expression.Expression;
import org.openzen.zenscript.codemodel.type.member.BuiltinID;
import org.openzen.zenscript.javabytecode.JavaBytecodeContext;
import org.openzen.zenscript.javabytecode.compiler.JavaBoxingTypeVisitor;
import org.openzen.zenscript.javabytecode.compiler.JavaUnboxingTypeVisitor;
import org.openzen.zenscript.javabytecode.compiler.JavaWriter;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction.*;

import java.util.EnumMap;

public class CallHandler {
	private final JavaExpressionVisitor ownerVisitor;
	private final JavaWriter javaWriter;
	private final JavaBytecodeContext context;
	private final JavaBoxingTypeVisitor boxingTypeVisitor;
	private final JavaUnboxingTypeVisitor unboxingTypeVisitor;

	private final EnumMap<BuiltinID, AbstractCallHandlerAction> callHandlers = new EnumMap<>(BuiltinID.class);

	public CallHandler(JavaExpressionVisitor ownerVisitor) {
		this.ownerVisitor = ownerVisitor;
		javaWriter = ownerVisitor.javaWriter;
		context = ownerVisitor.context;
		boxingTypeVisitor = new JavaBoxingTypeVisitor(javaWriter);
		unboxingTypeVisitor = new JavaUnboxingTypeVisitor(javaWriter);

		addCallHandlerActions();
	}

	private void addCallHandlerActions() {

		addCallHandlerAction(new CallHandlerActionIncrementDecrement(ownerVisitor));

		addCallHandlerAction(new CallHandlerActionIntOperations(ownerVisitor));
		addCallHandlerAction(new CallHandlerActionCharOperations(ownerVisitor));

		addCallHandlerAction(new CallHandlerActionOptionalNullCheck(ownerVisitor));
		addCallHandlerAction(new CallHandlerActionStringOperations(ownerVisitor));

		addCallHandlerAction(new CallHandlerActionArray(ownerVisitor));
		addCallHandlerAction(new CallHandlerActionRangeGet(ownerVisitor));

		addCallHandlerAction(new CallHandlerActionAssociative(ownerVisitor));
		addCallHandlerAction(new CallHandlerActionGenericMap(ownerVisitor));

		addCallHandlerAction(new CallHandlerActionFunction(ownerVisitor));
		addCallHandlerAction(new CallHandlerActionObject(ownerVisitor));
	}

	private void addCallHandlerAction(AbstractCallHandlerAction action) {
		for (BuiltinID supportedBuiltin : action.getSupportedBuiltins()) {
			if (callHandlers.containsKey(supportedBuiltin)) {
				throw new IllegalStateException("Two handlers for same builtin: " + supportedBuiltin);
			}
			callHandlers.put(supportedBuiltin, action);
		}
	}

	public void visitCall(CallExpression expression) {
		BuiltinID builtin = expression.member.getBuiltin();
		if (builtin != null) {
			if (!callHandlers.containsKey(builtin)) {
				throw new UnsupportedOperationException("Unsupported Builtin: " + builtin);
			}

			callHandlers.get(builtin).handleCallAction(expression);
			return;
		}

		expression.target.accept(ownerVisitor);

		ownerVisitor.handleTypeArguments(expression.member, expression.arguments);

		final Expression[] arguments = expression.arguments.arguments;
		final FunctionParameter[] parameters = expression.instancedHeader.parameters;

		final boolean variadic = expression.instancedHeader.isVariadicCall(expression.arguments) && ((arguments.length != parameters.length) || !parameters[parameters.length - 1].type
				.equals(arguments[arguments.length - 1].type));

		ownerVisitor.handleCallArguments(arguments, parameters, variadic);


		if (!ownerVisitor.checkAndExecuteMethodInfo(expression.member, expression.type, expression))
			throw new IllegalStateException("Call target has no method info!");

		if (expression.member.getTarget().header.getReturnType().isGeneric())
			javaWriter.checkCast(context.getInternalName(expression.type));

	}

}
