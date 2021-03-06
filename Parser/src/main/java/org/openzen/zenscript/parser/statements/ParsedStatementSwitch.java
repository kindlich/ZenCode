package org.openzen.zenscript.parser.statements;

import org.openzen.zencode.shared.CodePosition;
import org.openzen.zencode.shared.CompileException;
import org.openzen.zenscript.codemodel.FunctionHeader;
import org.openzen.zenscript.codemodel.GenericMapper;
import org.openzen.zenscript.codemodel.GenericName;
import org.openzen.zenscript.codemodel.WhitespaceInfo;
import org.openzen.zenscript.codemodel.annotations.AnnotationDefinition;
import org.openzen.zenscript.codemodel.definition.ZSPackage;
import org.openzen.zenscript.codemodel.partial.IPartialExpression;
import org.openzen.zenscript.codemodel.scope.ExpressionScope;
import org.openzen.zenscript.codemodel.scope.StatementScope;
import org.openzen.zenscript.codemodel.statement.InvalidStatement;
import org.openzen.zenscript.codemodel.statement.LoopStatement;
import org.openzen.zenscript.codemodel.statement.Statement;
import org.openzen.zenscript.codemodel.statement.SwitchStatement;
import org.openzen.zenscript.codemodel.type.TypeID;
import org.openzen.zenscript.codemodel.type.member.LocalMemberCache;
import org.openzen.zenscript.codemodel.type.member.TypeMemberPreparer;
import org.openzen.zenscript.parser.ParsedAnnotation;
import org.openzen.zenscript.parser.expression.ParsedExpression;

import java.util.List;

public class ParsedStatementSwitch extends ParsedStatement {
	private final String name;
	private final ParsedExpression value;
	private final List<ParsedSwitchCase> cases;

	public ParsedStatementSwitch(CodePosition position, ParsedAnnotation[] annotations, WhitespaceInfo whitespace, String name, ParsedExpression value, List<ParsedSwitchCase> cases) {
		super(position, annotations, whitespace);

		this.name = name;
		this.value = value;
		this.cases = cases;
	}

	@Override
	public Statement compile(StatementScope scope) {
		try {
			SwitchStatement result = new SwitchStatement(position, name, value.compile(new ExpressionScope(scope)).eval());
			SwitchScope innerScope = new SwitchScope(scope, result);

			for (ParsedSwitchCase switchCase : cases) {
				try {
					result.cases.add(switchCase.compile(result.value.type, innerScope));
				} catch (CompileException ex) {
					return result(new InvalidStatement(ex), scope);
				}
			}

			return result;
		} catch (CompileException ex) {
			return result(new InvalidStatement(ex), scope);
		}
	}

	private static class SwitchScope extends StatementScope {
		private final StatementScope outer;
		private final SwitchStatement target;

		public SwitchScope(StatementScope outer, SwitchStatement target) {
			this.outer = outer;
			this.target = target;
		}

		@Override
		public ZSPackage getRootPackage() {
			return outer.getRootPackage();
		}

		@Override
		public IPartialExpression get(CodePosition position, GenericName name) throws CompileException {
			IPartialExpression result = super.get(position, name);
			if (result == null) {
				return outer.get(position, name);
			} else {
				return result;
			}
		}

		@Override
		public LocalMemberCache getMemberCache() {
			return outer.getMemberCache();
		}

		@Override
		public TypeID getType(CodePosition position, List<GenericName> name) {
			return outer.getType(position, name);
		}

		@Override
		public LoopStatement getLoop(String name) {
			return name == null || name.equals(target.label) ? target : outer.getLoop(name);
		}

		@Override
		public FunctionHeader getFunctionHeader() {
			return outer.getFunctionHeader();
		}

		@Override
		public TypeID getThisType() {
			return outer.getThisType();
		}

		@Override
		public DollarEvaluator getDollar() {
			return outer.getDollar();
		}

		@Override
		public IPartialExpression getOuterInstance(CodePosition position) throws CompileException {
			return outer.getOuterInstance(position);
		}

		@Override
		public AnnotationDefinition getAnnotation(String name) {
			return outer.getAnnotation(name);
		}

		@Override
		public TypeMemberPreparer getPreparer() {
			return outer.getPreparer();
		}

		@Override
		public GenericMapper getLocalTypeParameters() {
			return outer.getLocalTypeParameters();
		}
	}
}
