package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.javabytecode.compiler.JavaExpressionVisitorJavaMembers;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;

import static org.openzen.zenscript.codemodel.type.member.BuiltinID.*;

public class CallHandlerActionIntOperations extends AbstractCallHandlerAction {
	public CallHandlerActionIntOperations(JavaExpressionVisitor ownerVisitor) {
		super(ownerVisitor,
				BOOL_NOT,
				BOOL_AND,
				BOOL_OR,
				BOOL_XOR,
				BOOL_EQUALS,
				BOOL_NOTEQUALS,
				BYTE_NOT,
				SBYTE_NOT,
				SHORT_NOT,
				USHORT_NOT,
				INT_NOT,
				UINT_NOT,
				USIZE_NOT,
				SBYTE_NEG,
				SHORT_NEG,
				INT_NEG,
				BYTE_ADD_BYTE,
				SBYTE_ADD_SBYTE,
				SHORT_ADD_SHORT,
				USHORT_ADD_USHORT,
				INT_ADD_INT,
				UINT_ADD_UINT,
				USIZE_ADD_USIZE,
				BYTE_SUB_BYTE,
				SBYTE_SUB_SBYTE,
				SHORT_SUB_SHORT,
				USHORT_SUB_USHORT,
				INT_SUB_INT,
				UINT_SUB_UINT,
				USIZE_SUB_USIZE,
				BYTE_MUL_BYTE,
				SBYTE_MUL_SBYTE,
				SHORT_MUL_SHORT,
				USHORT_MUL_USHORT,
				INT_MUL_INT,
				UINT_MUL_UINT,
				USIZE_MUL_USIZE,
				SBYTE_DIV_SBYTE,
				SHORT_DIV_SHORT,
				INT_DIV_INT,
				USIZE_DIV_USIZE,
				SBYTE_MOD_SBYTE,
				SHORT_MOD_SHORT,
				INT_MOD_INT,
				USIZE_MOD_USIZE,
				BYTE_DIV_BYTE,
				USHORT_DIV_USHORT,
				UINT_DIV_UINT,
				BYTE_MOD_BYTE,
				USHORT_MOD_USHORT,
				UINT_MOD_UINT,
				BYTE_AND_BYTE,
				SBYTE_AND_SBYTE,
				SHORT_AND_SHORT,
				USHORT_AND_USHORT,
				INT_AND_INT,
				UINT_AND_UINT,
				USIZE_AND_USIZE,
				BYTE_OR_BYTE,
				SBYTE_OR_SBYTE,
				SHORT_OR_SHORT,
				USHORT_OR_USHORT,
				INT_OR_INT,
				UINT_OR_UINT,
				USIZE_OR_USIZE,
				BYTE_XOR_BYTE,
				SBYTE_XOR_SBYTE,
				SHORT_XOR_SHORT,
				USHORT_XOR_USHORT,
				INT_XOR_INT,
				UINT_XOR_UINT,
				USIZE_XOR_USIZE,
				BYTE_SHL,
				SBYTE_SHL,
				SHORT_SHL,
				USHORT_SHL,
				INT_SHL,
				UINT_SHL,
				USIZE_SHL,
				SBYTE_SHR,
				SHORT_SHR,
				INT_SHR,
				BYTE_SHR,
				SBYTE_USHR,
				USHORT_SHR,
				SHORT_USHR,
				INT_USHR,
				UINT_SHR,
				USIZE_SHR,
				INT_COUNT_LOW_ZEROES,
				UINT_COUNT_LOW_ZEROES,
				USIZE_COUNT_LOW_ZEROES,
				INT_COUNT_HIGH_ZEROES,
				UINT_COUNT_HIGH_ZEROES,
				USIZE_COUNT_HIGH_ZEROES,
				INT_COUNT_LOW_ONES,
				UINT_COUNT_LOW_ONES,
				USIZE_COUNT_LOW_ONES,
				INT_COUNT_HIGH_ONES,
				UINT_COUNT_HIGH_ONES,
				USIZE_COUNT_HIGH_ONES,
				LONG_NOT,
				ULONG_NOT,
				LONG_NEG,
				LONG_ADD_LONG,
				ULONG_ADD_ULONG,
				LONG_SUB_LONG,
				ULONG_SUB_ULONG,
				LONG_MUL_LONG,
				ULONG_MUL_ULONG,
				LONG_DIV_LONG,
				LONG_MOD_LONG,
				LONG_AND_LONG,
				ULONG_AND_ULONG,
				LONG_OR_LONG,
				ULONG_OR_ULONG,
				LONG_XOR_LONG,
				ULONG_XOR_ULONG,
				LONG_SHL,
				ULONG_SHL,
				LONG_SHR,
				LONG_USHR,
				ULONG_SHR,
				LONG_COUNT_LOW_ZEROES,
				ULONG_COUNT_LOW_ZEROES,
				LONG_COUNT_HIGH_ZEROES,
				ULONG_COUNT_HIGH_ZEROES,
				LONG_COUNT_LOW_ONES,
				ULONG_COUNT_LOW_ONES,
				LONG_COUNT_HIGH_ONES,
				ULONG_COUNT_HIGH_ONES,
				ULONG_DIV_ULONG,
				ULONG_MOD_ULONG,
				FLOAT_NEG,
				FLOAT_ADD_FLOAT,
				FLOAT_SUB_FLOAT,
				FLOAT_MUL_FLOAT,
				FLOAT_DIV_FLOAT,
				FLOAT_MOD_FLOAT,
				DOUBLE_NEG,
				DOUBLE_ADD_DOUBLE,
				DOUBLE_SUB_DOUBLE,
				DOUBLE_MUL_DOUBLE,
				DOUBLE_DIV_DOUBLE,
				DOUBLE_MOD_DOUBLE);
	}

	@Override
	protected void handleInvocation(CallExpression expression) {
		switch (expression.member.getBuiltin()) {
			case BOOL_NOT:
				javaWriter.invertBoolean();
				break;
			case BOOL_AND:
			case BYTE_AND_BYTE:
			case SBYTE_AND_SBYTE:
			case SHORT_AND_SHORT:
			case USHORT_AND_USHORT:
			case INT_AND_INT:
			case UINT_AND_UINT:
			case USIZE_AND_USIZE:
				javaWriter.iAnd();
				break;
			case BOOL_OR:
			case BYTE_OR_BYTE:
			case SBYTE_OR_SBYTE:
			case SHORT_OR_SHORT:
			case USHORT_OR_USHORT:
			case INT_OR_INT:
			case UINT_OR_UINT:
			case USIZE_OR_USIZE:
				javaWriter.iOr();
				break;
			case BOOL_XOR:
			case BOOL_NOTEQUALS:
			case BYTE_XOR_BYTE:
			case SBYTE_XOR_SBYTE:
			case SHORT_XOR_SHORT:
			case USHORT_XOR_USHORT:
			case INT_XOR_INT:
			case UINT_XOR_UINT:
			case USIZE_XOR_USIZE:
				javaWriter.iXor();
				break;
			case BOOL_EQUALS:
				javaWriter.iXor();
				javaWriter.iConst1();
				javaWriter.iXor();
				break;
			case BYTE_NOT:
			case SBYTE_NOT:
			case SHORT_NOT:
			case USHORT_NOT:
			case INT_NOT:
			case UINT_NOT:
			case USIZE_NOT:
				javaWriter.iNot();
				break;
			case SBYTE_NEG:
			case SHORT_NEG:
			case INT_NEG:
				javaWriter.iNeg();
				break;
			case BYTE_ADD_BYTE:
			case SBYTE_ADD_SBYTE:
			case SHORT_ADD_SHORT:
			case USHORT_ADD_USHORT:
			case INT_ADD_INT:
			case UINT_ADD_UINT:
			case USIZE_ADD_USIZE:
				javaWriter.iAdd();
				break;
			case BYTE_SUB_BYTE:
			case SBYTE_SUB_SBYTE:
			case SHORT_SUB_SHORT:
			case USHORT_SUB_USHORT:
			case INT_SUB_INT:
			case UINT_SUB_UINT:
			case USIZE_SUB_USIZE:
				javaWriter.iSub();
				break;
			case BYTE_MUL_BYTE:
			case SBYTE_MUL_SBYTE:
			case SHORT_MUL_SHORT:
			case USHORT_MUL_USHORT:
			case INT_MUL_INT:
			case UINT_MUL_UINT:
			case USIZE_MUL_USIZE:
				javaWriter.iMul();
				break;
			case SBYTE_DIV_SBYTE:
			case SHORT_DIV_SHORT:
			case INT_DIV_INT:
			case USIZE_DIV_USIZE:
				javaWriter.iDiv();
				break;
			case SBYTE_MOD_SBYTE:
			case SHORT_MOD_SHORT:
			case INT_MOD_INT:
			case USIZE_MOD_USIZE:
				javaWriter.iRem();
				break;
			case BYTE_DIV_BYTE:
			case USHORT_DIV_USHORT:
			case UINT_DIV_UINT:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.INTEGER_DIVIDE_UNSIGNED);
				break;
			case BYTE_MOD_BYTE:
			case USHORT_MOD_USHORT:
			case UINT_MOD_UINT:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.INTEGER_REMAINDER_UNSIGNED);
				break;
			case BYTE_SHL:
			case SBYTE_SHL:
			case SHORT_SHL:
			case USHORT_SHL:
			case INT_SHL:
			case UINT_SHL:
			case USIZE_SHL:
				javaWriter.iShl();
				break;
			case SBYTE_SHR:
			case SHORT_SHR:
			case INT_SHR:
				javaWriter.iShr();
				break;
			case BYTE_SHR:
			case SBYTE_USHR:
			case USHORT_SHR:
			case SHORT_USHR:
			case INT_USHR:
			case UINT_SHR:
			case USIZE_SHR:
				javaWriter.iUShr();
				break;
			case INT_COUNT_LOW_ZEROES:
			case UINT_COUNT_LOW_ZEROES:
			case USIZE_COUNT_LOW_ZEROES:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.INTEGER_NUMBER_OF_TRAILING_ZEROS);
				break;
			case INT_COUNT_HIGH_ZEROES:
			case UINT_COUNT_HIGH_ZEROES:
			case USIZE_COUNT_HIGH_ZEROES:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.INTEGER_NUMBER_OF_LEADING_ZEROS);
				break;
			case INT_COUNT_LOW_ONES:
			case UINT_COUNT_LOW_ONES:
			case USIZE_COUNT_LOW_ONES:
				javaWriter.iNot();
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.INTEGER_NUMBER_OF_TRAILING_ZEROS);
				break;
			case INT_COUNT_HIGH_ONES:
			case UINT_COUNT_HIGH_ONES:
			case USIZE_COUNT_HIGH_ONES:
				javaWriter.iNot();
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.INTEGER_NUMBER_OF_LEADING_ZEROS);
				break;
			case LONG_NOT:
			case ULONG_NOT:
				javaWriter.lNot();
				break;
			case LONG_NEG:
				javaWriter.lNeg();
				break;
			case LONG_ADD_LONG:
			case ULONG_ADD_ULONG:
				javaWriter.lAdd();
				break;
			case LONG_SUB_LONG:
			case ULONG_SUB_ULONG:
				javaWriter.lSub();
				break;
			case LONG_MUL_LONG:
			case ULONG_MUL_ULONG:
				javaWriter.lMul();
				break;
			case LONG_DIV_LONG:
				javaWriter.lDiv();
				break;
			case LONG_MOD_LONG:
				javaWriter.lRem();
				break;
			case LONG_AND_LONG:
			case ULONG_AND_ULONG:
				javaWriter.lAnd();
				break;
			case LONG_OR_LONG:
			case ULONG_OR_ULONG:
				javaWriter.lOr();
				break;
			case LONG_XOR_LONG:
			case ULONG_XOR_ULONG:
				javaWriter.lXor();
				break;
			case LONG_SHL:
			case ULONG_SHL:
				javaWriter.lShl();
				break;
			case LONG_SHR:
				javaWriter.lShr();
				break;
			case LONG_USHR:
			case ULONG_SHR:
				javaWriter.lUShr();
				break;
			case LONG_COUNT_LOW_ZEROES:
			case ULONG_COUNT_LOW_ZEROES:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.LONG_NUMBER_OF_TRAILING_ZEROS);
				break;
			case LONG_COUNT_HIGH_ZEROES:
			case ULONG_COUNT_HIGH_ZEROES:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.LONG_NUMBER_OF_LEADING_ZEROS);
				break;
			case LONG_COUNT_LOW_ONES:
			case ULONG_COUNT_LOW_ONES:
				javaWriter.lNot();
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.LONG_NUMBER_OF_TRAILING_ZEROS);
				break;
			case LONG_COUNT_HIGH_ONES:
			case ULONG_COUNT_HIGH_ONES:
				javaWriter.lNot();
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.LONG_NUMBER_OF_LEADING_ZEROS);
				break;
			case ULONG_DIV_ULONG:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.LONG_DIVIDE_UNSIGNED);
				break;
			case ULONG_MOD_ULONG:
				javaWriter.invokeStatic(JavaExpressionVisitorJavaMembers.LONG_REMAINDER_UNSIGNED);
				break;
			case FLOAT_NEG:
				javaWriter.fNeg();
				break;
			case FLOAT_ADD_FLOAT:
				javaWriter.fAdd();
				break;
			case FLOAT_SUB_FLOAT:
				javaWriter.fSub();
				break;
			case FLOAT_MUL_FLOAT:
				javaWriter.fMul();
				break;
			case FLOAT_DIV_FLOAT:
				javaWriter.fDiv();
				break;
			case FLOAT_MOD_FLOAT:
				javaWriter.fRem();
				break;
			case DOUBLE_NEG:
				javaWriter.dNeg();
				break;
			case DOUBLE_ADD_DOUBLE:
				javaWriter.dAdd();
				break;
			case DOUBLE_SUB_DOUBLE:
				javaWriter.dSub();
				break;
			case DOUBLE_MUL_DOUBLE:
				javaWriter.dMul();
				break;
			case DOUBLE_DIV_DOUBLE:
				javaWriter.dDiv();
				break;
			case DOUBLE_MOD_DOUBLE:
				javaWriter.dRem();
				break;
		}
	}
}
