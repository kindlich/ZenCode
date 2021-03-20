package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.codemodel.type.AssocTypeID;
import org.openzen.zenscript.javabytecode.compiler.CompilerUtils;
import org.openzen.zenscript.javabytecode.compiler.JavaBoxingTypeVisitor;
import org.openzen.zenscript.javabytecode.compiler.JavaExpressionVisitorJavaMembers;
import org.openzen.zenscript.javabytecode.compiler.JavaUnboxingTypeVisitor;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;

import static org.openzen.zenscript.codemodel.type.member.BuiltinID.*;

public class CallHandlerActionAssociative extends AbstractCallHandlerAction {
	private final JavaBoxingTypeVisitor boxingTypeVisitor;
	private final JavaUnboxingTypeVisitor unboxingTypeVisitor;

	public CallHandlerActionAssociative(JavaExpressionVisitor ownerVisitor) {
		super(ownerVisitor,
				ASSOC_INDEXGET,
				ASSOC_GETORDEFAULT,
				ASSOC_INDEXSET,
				ASSOC_CONTAINS,
				ASSOC_EQUALS,
				ASSOC_NOTEQUALS,
				ASSOC_SAME,
				ASSOC_NOTSAME);

		boxingTypeVisitor = new JavaBoxingTypeVisitor(javaWriter);
		unboxingTypeVisitor = new JavaUnboxingTypeVisitor(javaWriter);
	}

	@Override
	protected void handleInvocation(CallExpression expression) {
		switch (expression.member.getBuiltin()) {
			case ASSOC_INDEXGET:
			case ASSOC_GETORDEFAULT: {
				AssocTypeID type = (AssocTypeID) expression.target.type;
				type.keyType.accept(type.keyType, boxingTypeVisitor);
				javaWriter.invokeInterface(JavaExpressionVisitorJavaMembers.MAP_GET);

				type.valueType.accept(type.valueType, unboxingTypeVisitor);
				if (!CompilerUtils.isPrimitive(type.valueType)) {
					javaWriter.checkCast(context.getType(type.valueType));
				}
				break;
			}
			case ASSOC_INDEXSET:
				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.MAP_PUT);
				javaWriter.pop();
				break;
			case ASSOC_CONTAINS:
				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.MAP_CONTAINS_KEY);
				break;
			case ASSOC_EQUALS:
				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.OBJECT_EQUALS);
				break;
			case ASSOC_NOTEQUALS:
				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.OBJECT_EQUALS);
				javaWriter.invertBoolean();
				break;
			case ASSOC_SAME:
				javaWriter.objectsSame();
				break;
			case ASSOC_NOTSAME:
				javaWriter.objectsNotSame();
				break;
		}
	}
}
