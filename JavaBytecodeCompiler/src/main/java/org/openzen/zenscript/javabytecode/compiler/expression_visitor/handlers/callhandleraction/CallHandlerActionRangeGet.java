package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.objectweb.asm.Type;
import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.codemodel.expression.Expression;
import org.openzen.zenscript.codemodel.expression.RangeExpression;
import org.openzen.zenscript.codemodel.type.ArrayTypeID;
import org.openzen.zenscript.codemodel.type.BasicTypeID;
import org.openzen.zenscript.codemodel.type.RangeTypeID;
import org.openzen.zenscript.codemodel.type.member.BuiltinID;
import org.openzen.zenscript.javabytecode.compiler.JavaExpressionVisitorJavaMembers;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;

public class CallHandlerActionRangeGet extends AbstractCallHandlerAction {
	public CallHandlerActionRangeGet(JavaExpressionVisitor ownerVisitor) {
		super(ownerVisitor, BuiltinID.ARRAY_INDEXGETRANGE, BuiltinID.STRING_RANGEGET);
	}

	@Override
	protected void handleParameters(CallExpression expression) {
		//No-op
	}

	@Override
	protected void handleInvocation(CallExpression expression) {
		final BuiltinID builtin = expression.member.getBuiltin();
		if (builtin == BuiltinID.ARRAY_INDEXGETRANGE) {
			handleArrayIndexGetRange(expression);
		} else if (builtin == BuiltinID.STRING_RANGEGET) {
			handleStringIndexGetRange(expression);
		}
	}

	private void handleStringIndexGetRange(CallExpression expression) {
		getBoundsFromIntRange(expression);
		javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.STRING_SUBSTRING);
	}

	private void handleArrayIndexGetRange(CallExpression expression) {

		ArrayTypeID type = (ArrayTypeID) expression.target.type;
		getBoundsFromIntRange(expression);

		if (type.elementType instanceof BasicTypeID) {
			switch ((BasicTypeID) type.elementType) {
				case BOOL:
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_COPY_OF_RANGE_BOOLS);
					break;
				case BYTE:
				case SBYTE:
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_COPY_OF_RANGE_BYTES);
					break;
				case SHORT:
				case USHORT:
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_COPY_OF_RANGE_SHORTS);
					break;
				case INT:
				case UINT:
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_COPY_OF_RANGE_INTS);
					break;
				case LONG:
				case ULONG:
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_COPY_OF_RANGE_LONGS);
					break;
				case FLOAT:
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_COPY_OF_RANGE_FLOATS);
					break;
				case DOUBLE:
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_COPY_OF_RANGE_DOUBLES);
					break;
				case CHAR:
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_COPY_OF_RANGE_CHARS);
					break;
				default:
					throw new IllegalArgumentException("Unknown basic type: " + type.elementType);
			}
		} else {
			javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_COPY_OF_RANGE_OBJECTS);
			javaWriter.checkCast(context.getInternalName(expression.target.type));
		}
	}

	private void getBoundsFromIntRange(CallExpression expression) {
		Expression argument = expression.arguments.arguments[0];
		if (argument instanceof RangeExpression) {
			RangeExpression rangeArgument = (RangeExpression) argument;
			rangeArgument.from.accept(ownerVisitor);
			rangeArgument.to.accept(ownerVisitor); // TODO: is ownerVisitor string.length ? if so, use the other substring method
		} else {
			argument.accept(ownerVisitor);
			javaWriter.dup();
			final String owner;
			if (argument.type instanceof RangeTypeID) {
				owner = context.getInternalName(argument.type);
			} else {
				owner = "zsynthetic/IntRange";
			}
			int tmp = javaWriter.local(Type.getType(owner));
			javaWriter.storeInt(tmp);
			javaWriter.getField(owner, "from", "I");
			javaWriter.loadInt(tmp);
			javaWriter.getField(owner, "to", "I");
		}
	}
}
