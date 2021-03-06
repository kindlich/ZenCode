package org.openzen.zenscript.parser.statements;

import org.openzen.zencode.shared.CodePosition;
import org.openzen.zencode.shared.CompileException;
import org.openzen.zenscript.codemodel.WhitespaceInfo;
import org.openzen.zenscript.codemodel.expression.Expression;
import org.openzen.zenscript.codemodel.scope.ExpressionScope;
import org.openzen.zenscript.codemodel.scope.StatementScope;
import org.openzen.zenscript.codemodel.statement.*;
import org.openzen.zenscript.parser.ParsedAnnotation;
import org.openzen.zenscript.parser.expression.ParsedExpression;

import java.util.ArrayList;
import java.util.List;

public class ParsedStatementTryCatch extends ParsedStatement {
	public final String resourceName;
	public final ParsedExpression resourceInitializer;
	public final ParsedStatement statement;
	public final List<ParsedCatchClause> catchClauses;
	public final ParsedStatement finallyClause;

	public ParsedStatementTryCatch(
			CodePosition position,
			ParsedAnnotation[] annotations,
			WhitespaceInfo whitespace,
			String resourceName,
			ParsedExpression resourceInitializer,
			ParsedStatement statement,
			List<ParsedCatchClause> catchClauses,
			ParsedStatement finallyClause) {
		super(position, annotations, whitespace);

		this.resourceName = resourceName;
		this.resourceInitializer = resourceInitializer;
		this.statement = statement;
		this.catchClauses = catchClauses;
		this.finallyClause = finallyClause;
	}

	@Override
	public Statement compile(StatementScope scope) {
		try {
			Expression resourceInitializer = this.resourceInitializer == null ? null : this.resourceInitializer.compile(new ExpressionScope(scope)).eval();
			Statement statement = this.statement.compile(scope);
			List<CatchClause> catches = new ArrayList<>();
			for (ParsedCatchClause catchClause : catchClauses) {
				catches.add(catchClause.compile(scope));
			}

			Statement finallyClause = this.finallyClause == null ? null : this.finallyClause.compile(scope);
			VarStatement resource = null;
			if (resourceName != null) {
				resource = new VarStatement(position, new VariableID(), resourceName, resourceInitializer.type, resourceInitializer, true);
			}
			return result(new TryCatchStatement(position, resource, statement, catches, finallyClause), scope);
		} catch (CompileException ex) {
			return result(new InvalidStatement(ex), scope);
		}
	}
}
