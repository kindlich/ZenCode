package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.codemodel.expression.GetLocalVariableExpression;
import org.openzen.zenscript.javabytecode.JavaLocalVariableInfo;
import org.openzen.zenscript.javabytecode.compiler.JavaModificationExpressionVisitor;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;

import static org.openzen.zenscript.codemodel.type.member.BuiltinID.*;

public class CallHandlerActionIncrementDecrement extends AbstractCallHandlerAction {
	public CallHandlerActionIncrementDecrement(JavaExpressionVisitor ownerVisitor) {
		super(ownerVisitor,
				BYTE_INC,
				BYTE_DEC,
				SBYTE_INC,
				SBYTE_DEC,
				SHORT_INC,
				SHORT_DEC,
				USHORT_INC,
				USHORT_DEC,
				INT_INC,
				UINT_INC,
				USIZE_INC,
				INT_DEC,
				UINT_DEC,
				USIZE_DEC,
				LONG_INC,
				ULONG_INC,
				LONG_DEC,
				ULONG_DEC,
				FLOAT_INC,
				FLOAT_DEC,
				DOUBLE_INC,
				DOUBLE_DEC);

	}

	@Override
	protected void handleParameters(CallExpression expression) {
		//Nothing to do
	}

	@Override
	protected void handleTarget(CallExpression expression) {
		//Nothing to do
	}

	@Override
	protected void handleInvocation(CallExpression expression) {
		switch (expression.member.getBuiltin()) {
			case BYTE_INC:
				modify(expression.target, () -> {
					javaWriter.iConst1();
					javaWriter.iAdd();
					javaWriter.constant(0xFF);
					javaWriter.iAnd();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case BYTE_DEC:
				modify(expression.target, () -> {
					javaWriter.iConst1();
					javaWriter.iSub();
					javaWriter.constant(0xFF);
					javaWriter.iAnd();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case SBYTE_INC:
				modify(expression.target, () -> {
					javaWriter.iConst1();
					javaWriter.iAdd();
					javaWriter.i2b();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case SBYTE_DEC:
				modify(expression.target, () -> {
					javaWriter.iConst1();
					javaWriter.iSub();
					javaWriter.i2b();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case SHORT_INC:
				modify(expression.target, () -> {
					javaWriter.iConst1();
					javaWriter.iAdd();
					javaWriter.i2s();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case SHORT_DEC:
				modify(expression.target, () -> {
					javaWriter.iConst1();
					javaWriter.iSub();
					javaWriter.i2s();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case USHORT_INC:
				modify(expression.target, () -> {
					javaWriter.iConst1();
					javaWriter.iAdd();
					javaWriter.constant(0xFFFF);
					javaWriter.iAnd();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case USHORT_DEC:
				modify(expression.target, () -> {
					javaWriter.iConst1();
					javaWriter.iSub();
					javaWriter.constant(0xFFFF);
					javaWriter.iAnd();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case INT_INC:
			case UINT_INC:
			case USIZE_INC:
				if (expression.target instanceof GetLocalVariableExpression) {
					JavaLocalVariableInfo local = javaWriter.getLocalVariable(((GetLocalVariableExpression) expression.target).variable.variable);
					javaWriter.iinc(local.local);
					javaWriter.load(local);
				} else {
					modify(expression.target, () -> {
						javaWriter.iConst1();
						javaWriter.iAdd();
					}, JavaModificationExpressionVisitor.PushOption.AFTER);
				}
				break;
			case INT_DEC:
			case UINT_DEC:
			case USIZE_DEC:
				if (expression.target instanceof GetLocalVariableExpression) {
					JavaLocalVariableInfo local = javaWriter.getLocalVariable(((GetLocalVariableExpression) expression.target).variable.variable);
					javaWriter.iinc(local.local, -1);
					javaWriter.load(local);
				} else {
					modify(expression.target, () -> {
						javaWriter.iConst1();
						javaWriter.iSub();
					}, JavaModificationExpressionVisitor.PushOption.AFTER);
				}
				break;
			case LONG_INC:
			case ULONG_INC:
				modify(expression.target, () -> {
					javaWriter.constant(1L);
					javaWriter.iAdd();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case LONG_DEC:
			case ULONG_DEC:
				modify(expression.target, () -> {
					javaWriter.constant(1L);
					javaWriter.iSub();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case FLOAT_INC:
				modify(expression.target, () -> {
					javaWriter.constant(1f);
					javaWriter.iAdd();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case FLOAT_DEC:
				modify(expression.target, () -> {
					javaWriter.constant(1f);
					javaWriter.iSub();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case DOUBLE_INC:
				modify(expression.target, () -> {
					javaWriter.constant(1d);
					javaWriter.iAdd();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
				break;
			case DOUBLE_DEC:
				modify(expression.target, () -> {
					javaWriter.constant(1d);
					javaWriter.iSub();
				}, JavaModificationExpressionVisitor.PushOption.AFTER);
		}
	}
}
