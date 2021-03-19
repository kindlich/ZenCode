package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers;

import org.objectweb.asm.Label;
import org.openzen.zenscript.codemodel.CompareType;
import org.openzen.zenscript.codemodel.expression.CompareExpression;
import org.openzen.zenscript.javabytecode.compiler.JavaExpressionVisitorJavaMembers;
import org.openzen.zenscript.javabytecode.compiler.JavaWriter;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;

public class CompareHandler {

	private final JavaExpressionVisitor ownerVisitor;
	private final JavaWriter javaWriter;

	public CompareHandler(JavaExpressionVisitor ownerVisitor) {
		this.ownerVisitor = ownerVisitor;
		javaWriter = ownerVisitor.javaWriter;
	}


	public void visitCompare(CompareExpression expression) {
		if (expression.operator.getBuiltin() != null) {
			switch (expression.operator.getBuiltin()) {
				case BYTE_COMPARE:
					expression.left.accept(ownerVisitor);
					javaWriter.constant(0xFF);
					javaWriter.iAnd();
					expression.right.accept(ownerVisitor);
					javaWriter.constant(0xFF);
					javaWriter.iAnd();
					compareInt(expression.comparison);
					break;
				case USHORT_COMPARE:
					expression.left.accept(ownerVisitor);
					javaWriter.constant(0xFFFF);
					javaWriter.iAnd();
					expression.right.accept(ownerVisitor);
					javaWriter.constant(0xFFFF);
					javaWriter.iAnd();
					compareInt(expression.comparison);
					break;
				case SBYTE_COMPARE:
				case SHORT_COMPARE:
				case INT_COMPARE:
				case CHAR_COMPARE:
				case USIZE_COMPARE:
					expression.left.accept(ownerVisitor);
					expression.right.accept(ownerVisitor);
					compareInt(expression.comparison);
					break;
				case UINT_COMPARE:
				case USIZE_COMPARE_UINT:
					expression.left.accept(ownerVisitor);
					expression.right.accept(ownerVisitor);
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.INTEGER_COMPARE_UNSIGNED);
					compareGeneric(expression.comparison);
					break;
				case LONG_COMPARE:
					expression.left.accept(ownerVisitor);
					expression.right.accept(ownerVisitor);
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.LONG_COMPARE);
					compareGeneric(expression.comparison);
					break;
				case ULONG_COMPARE:
					expression.left.accept(ownerVisitor);
					expression.right.accept(ownerVisitor);
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.LONG_COMPARE_UNSIGNED);
					compareGeneric(expression.comparison);
					break;
				case ULONG_COMPARE_UINT:
					expression.left.accept(ownerVisitor);
					expression.right.accept(ownerVisitor);
					javaWriter.i2l();
					javaWriter.constant(0xFFFF_FFFFL);
					javaWriter.lAnd();
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.LONG_COMPARE_UNSIGNED);
					compareGeneric(expression.comparison);
					break;
				case ULONG_COMPARE_USIZE:
					expression.left.accept(ownerVisitor);
					expression.right.accept(ownerVisitor);
					javaWriter.i2l();
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.LONG_COMPARE_UNSIGNED);
					compareGeneric(expression.comparison);
					break;
				case FLOAT_COMPARE:
					expression.left.accept(ownerVisitor);
					expression.right.accept(ownerVisitor);
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.FLOAT_COMPARE);
					compareGeneric(expression.comparison);
					break;
				case DOUBLE_COMPARE:
					expression.left.accept(ownerVisitor);
					expression.right.accept(ownerVisitor);
					javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.DOUBLE_COMPARE);
					compareGeneric(expression.comparison);
					break;
				case STRING_COMPARE:
					expression.left.accept(ownerVisitor);
					expression.right.accept(ownerVisitor);
					javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.STRING_COMPARETO);
					compareGeneric(expression.comparison);
					break;
				case ENUM_COMPARE:
					expression.left.accept(ownerVisitor);
					expression.right.accept(ownerVisitor);
					javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.ENUM_COMPARETO);
					compareGeneric(expression.comparison);
					break;
				default:
					throw new UnsupportedOperationException("Unknown builtin comparator: " + expression.operator.getBuiltin());
			}
		} else {
			expression.left.accept(ownerVisitor);
			expression.right.accept(ownerVisitor);

			if (!ownerVisitor.checkAndExecuteMethodInfo(expression.operator, expression.type, expression))
				throw new IllegalStateException("Call target has no method info!");

			compareGeneric(expression.comparison);
		}
	}

	private void compareInt(CompareType comparator) {
		Label exit = new Label();
		Label isTrue = new Label();
		switch (comparator) {
			case EQ:
				javaWriter.ifICmpEQ(isTrue);
				break;
			case NE:
				javaWriter.ifICmpNE(isTrue);
				break;
			case GT:
				javaWriter.ifICmpGT(isTrue);
				break;
			case GE:
				javaWriter.ifICmpGE(isTrue);
				break;
			case LT:
				javaWriter.ifICmpLT(isTrue);
				break;
			case LE:
				javaWriter.ifICmpLE(isTrue);
				break;
			default:
				throw new IllegalStateException("Invalid comparator: " + comparator);
		}
		javaWriter.iConst0();
		javaWriter.goTo(exit);
		javaWriter.label(isTrue);
		javaWriter.iConst1();
		javaWriter.label(exit);
	}

	private void compareGeneric(CompareType comparator) {
		Label exit = new Label();
		Label isTrue = new Label();
		switch (comparator) {
			case EQ:
				javaWriter.ifEQ(isTrue);
				break;
			case NE:
				javaWriter.ifNE(isTrue);
				break;
			case GT:
				javaWriter.ifGT(isTrue);
				break;
			case GE:
				javaWriter.ifGE(isTrue);
				break;
			case LT:
				javaWriter.ifLT(isTrue);
				break;
			case LE:
				javaWriter.ifLE(isTrue);
				break;
			default:
				throw new IllegalStateException("Invalid comparator: " + comparator);
		}
		javaWriter.iConst0();
		javaWriter.goTo(exit);
		javaWriter.label(isTrue);
		javaWriter.iConst1();
		javaWriter.label(exit);
	}
}
