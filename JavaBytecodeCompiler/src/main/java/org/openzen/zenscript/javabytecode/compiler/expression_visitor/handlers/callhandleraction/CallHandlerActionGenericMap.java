package org.openzen.zenscript.javabytecode.compiler.expression_visitor.handlers.callhandleraction;

import org.openzen.zenscript.codemodel.expression.CallExpression;
import org.openzen.zenscript.javabytecode.compiler.JavaExpressionVisitorJavaMembers;
import org.openzen.zenscript.javabytecode.compiler.expression_visitor.JavaExpressionVisitor;
import org.openzen.zenscript.javashared.JavaClass;
import org.openzen.zenscript.javashared.JavaMethod;

import static org.openzen.zenscript.codemodel.type.member.BuiltinID.*;

public class CallHandlerActionGenericMap extends AbstractCallHandlerAction {
	public CallHandlerActionGenericMap(JavaExpressionVisitor ownerVisitor) {
		super(ownerVisitor,
				GENERICMAP_GETOPTIONAL,
				GENERICMAP_PUT,
				GENERICMAP_CONTAINS,
				GENERICMAP_EQUALS,
				GENERICMAP_NOTEQUALS,
				GENERICMAP_ADDALL,
				GENERICMAP_SAME,
				GENERICMAP_NOTSAME);
	}

	@Override
	protected void handleInvocation(CallExpression expression) {
		switch (expression.member.getBuiltin()) {
			case GENERICMAP_GETOPTIONAL: {
				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.MAP_GET);
				break;
			}
			case GENERICMAP_PUT: {
				//FIXME dirty check for typeOfT
				if (expression.arguments.arguments.length == 1) {
					javaWriter.dup();
					javaWriter.invokeVirtual(JavaMethod.getVirtual(JavaClass.OBJECT, "getClass", "()Ljava/lang/Class;", 0));
					javaWriter.swap();
				}

				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.MAP_PUT);
				javaWriter.pop();
				break;
			}
			case GENERICMAP_CONTAINS:
				javaWriter.invokeVirtual(JavaExpressionVisitorJavaMembers.MAP_CONTAINS_KEY);
				break;
			case GENERICMAP_EQUALS:
			case GENERICMAP_NOTEQUALS:
				throw new UnsupportedOperationException("Not yet supported!");
			case GENERICMAP_ADDALL:
				javaWriter.invokeInterface(JavaExpressionVisitorJavaMembers.MAP_PUT_ALL);
				break;
			case GENERICMAP_SAME:
				javaWriter.objectsSame();
				break;
			case GENERICMAP_NOTSAME:
				javaWriter.objectsNotSame();
				break;
		}
	}
}
