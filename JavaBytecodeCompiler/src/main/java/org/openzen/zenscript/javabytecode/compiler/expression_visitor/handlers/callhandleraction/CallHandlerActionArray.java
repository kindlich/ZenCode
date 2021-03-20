package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.codemodel.expression.Expression;
import org.openzen.zenscript.codemodel.type.ArrayTypeID;
import org.openzen.zenscript.codemodel.type.BasicTypeID;
import org.openzen.zenscript.codemodel.type.TypeID;
import org.openzen.zenscript.codemodel.type.member.BuiltinID;
import org.openzen.zenscript.javabytecode.compiler.CompilerUtils;
import org.openzen.zenscript.javabytecode.compiler.JavaExpressionVisitorJavaMembers;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;
import org.openzen.zenscript.javashared.JavaClass;
import org.openzen.zenscript.javashared.JavaMethod;

import static org.openzen.zenscript.codemodel.type.member.BuiltinID.*;

public class CallHandlerActionArray extends AbstractCallHandlerAction {

	public CallHandlerActionArray(JavaExpressionVisitor ownerVisitor) {
		super(ownerVisitor,
				ARRAY_INDEXSET,
				ARRAY_EQUALS,
				ARRAY_NOTEQUALS,
				ARRAY_INDEXGET,
				ARRAY_CONTAINS,
				ARRAY_SAME,
				ARRAY_NOTSAME);
	}

	@Override
	protected void handleParameters(CallExpression expression) {
		if (needsParameters(expression)) {
			super.handleParameters(expression);
		}
	}

	@Override
	protected void handleInvocation(CallExpression expression) {
		final BuiltinID builtin = expression.member.getBuiltin();

		switch (builtin) {
			case ARRAY_INDEXSET: {
				//TODO multi-dim arrays?
				ArrayTypeID type = (ArrayTypeID) expression.target.type;
				javaWriter.arrayStore(context.getType(type.elementType));
				break;
			}
			case ARRAY_EQUALS:
			case ARRAY_NOTEQUALS:
				handleEqualityCheck(expression, builtin == BuiltinID.ARRAY_NOTEQUALS);
				break;
			case ARRAY_INDEXGET:
				handleIndexGet(expression);
				break;
			case ARRAY_CONTAINS:
				handleContains(expression);
				break;
			case ARRAY_SAME:
				javaWriter.objectsSame();
				break;
			case ARRAY_NOTSAME:
				javaWriter.objectsNotSame();
		}
	}

	private boolean needsParameters(CallExpression expression) {
		final BuiltinID builtin = expression.member.getBuiltin();
		return builtin != ARRAY_CONTAINS && builtin != ARRAY_INDEXGET;
	}

	private void handleEqualityCheck(CallExpression expression, boolean inverted) {
		ArrayTypeID type = (ArrayTypeID) expression.target.type;
		if (type.elementType instanceof BasicTypeID) {
			invokePrimitiveArrayCompareMethod(type);
		} else {
			javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_EQUALS_OBJECTS);
		}

		if (inverted) {
			javaWriter.invertBoolean();
		}
	}

	private void invokePrimitiveArrayCompareMethod(ArrayTypeID type) {
		switch ((BasicTypeID) type.elementType) {
			case BOOL:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_EQUALS_BOOLS);
				break;
			case BYTE:
			case SBYTE:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_EQUALS_BYTES);
				break;
			case SHORT:
			case USHORT:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_EQUALS_SHORTS);
				break;
			case INT:
			case UINT:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_EQUALS_INTS);
				break;
			case LONG:
			case ULONG:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_EQUALS_LONGS);
				break;
			case FLOAT:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_EQUALS_FLOATS);
				break;
			case DOUBLE:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_EQUALS_DOUBLES);
				break;
			case CHAR:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.ARRAYS_EQUALS_CHARS);
				break;
			default:
				throw new IllegalArgumentException("Unknown basic type: " + type.elementType);
		}
	}

	private void handleIndexGet(CallExpression expression) {
		//ArrayTypeID type = (ArrayTypeID) expression.target.type;

		final Expression[] arguments = expression.arguments.arguments;
		Type asmType = context.getType(expression.target.type);
		for (Expression argument : arguments) {
			asmType = Type.getType(asmType.getDescriptor().substring(1));
			argument.accept(ownerVisitor);
			javaWriter.arrayLoad(asmType);
		}
	}


	private void handleContains(CallExpression expression) {
		final Label loopStart = new Label();
		final Label loopEnd = new Label();
		final Label isTrue = new Label();
		final Label expressionEnd = new Label();

		final int counterLocation = javaWriter.local(int.class);
		javaWriter.iConst0();
		javaWriter.storeInt(counterLocation);

		javaWriter.label(loopStart);
		javaWriter.dup();
		javaWriter.arrayLength();

		javaWriter.loadInt(counterLocation);

		javaWriter.ifICmpLE(loopEnd);
		javaWriter.dup();
		javaWriter.loadInt(counterLocation);
		final TypeID itemType = expression.arguments.arguments[0].type;
		javaWriter.arrayLoad(context.getType(itemType));
		javaWriter.iinc(counterLocation);
		expression.arguments.arguments[0].accept(ownerVisitor);


		if (CompilerUtils.isPrimitive(itemType)) {
			//Compare non-int types beforehand
			if (itemType == BasicTypeID.LONG || itemType == BasicTypeID.ULONG) {
				javaWriter.lCmp();
				javaWriter.ifEQ(loopStart);
			} else if (itemType == BasicTypeID.FLOAT) {
				javaWriter.fCmp();
				javaWriter.ifEQ(loopStart);
			} else if (itemType == BasicTypeID.DOUBLE) {
				javaWriter.dCmp();
				javaWriter.ifEQ(loopStart);
			} else
				javaWriter.ifICmpNE(loopStart);
		} else {
			//If equals, use Object.equals in case of null
			javaWriter.invokeStatic(new JavaMethod(JavaClass.fromInternalName("java/util/Objects", JavaClass.Kind.CLASS), JavaMethod.Kind.STATIC, "equals", false, "(Ljava/lang/Object;Ljava/lang/Object;)Z", 0, false));
			javaWriter.ifEQ(loopStart);
		}

		javaWriter.label(isTrue);

		javaWriter.pop();
		javaWriter.iConst1();
		javaWriter.goTo(expressionEnd);

		javaWriter.label(loopEnd);
		javaWriter.pop();
		javaWriter.iConst0();
		javaWriter.label(expressionEnd);
	}
}
