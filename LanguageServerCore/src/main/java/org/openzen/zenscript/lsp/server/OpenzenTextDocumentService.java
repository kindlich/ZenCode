package org.openzen.zenscript.lsp.server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.openzen.zencode.shared.CodePosition;
import org.openzen.zencode.shared.LiteralSourceFile;
import org.openzen.zencode.shared.VirtualSourceFile;
import org.openzen.zenscript.codemodel.Module;
import org.openzen.zenscript.codemodel.PackageDefinitions;
import org.openzen.zenscript.codemodel.context.CompilingPackage;
import org.openzen.zenscript.codemodel.definition.ZSPackage;
import org.openzen.zenscript.lexer.ParseException;
import org.openzen.zenscript.lexer.ZSToken;
import org.openzen.zenscript.lexer.ZSTokenParser;
import org.openzen.zenscript.lsp.server.semantictokens.LSPSemanticTokenProvider;
import org.openzen.zenscript.parser.ParsedFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class OpenzenTextDocumentService implements TextDocumentService {
	private final LSPSemanticTokenProvider semanticTokenProvider;
	private final OpenzenLSPServer openzenLSPServer;
	private final Map<String, ParsedFile> openFiles = new HashMap<>();

	public OpenzenTextDocumentService(LSPSemanticTokenProvider semanticTokenProvider, OpenzenLSPServer openzenLSPServer) {
		this.semanticTokenProvider = semanticTokenProvider;
		this.openzenLSPServer = openzenLSPServer;
	}

	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
		Logger.getGlobal().log(Level.FINER, "completion(CompletionParams position)");

		return CompletableFuture.supplyAsync(() -> {
			final List<CompletionItem> result = new ArrayList<>();
			final CompletionItem completionItem = new CompletionItem("some_label");
			completionItem.setDetail(position.getPosition().toString());
			completionItem.setKind(CompletionItemKind.Snippet);
			completionItem.setInsertText("public function helloWorld() {\n\tprintln('Hello World');\n}");
			result.add(completionItem);
			return Either.forLeft(result);
		});
	}

	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
		Logger.getGlobal().log(Level.FINER, "didOpen(DidOpenTextDocumentParams params)", params);
		final TextDocumentItem textDocument = params.getTextDocument();
		parseAndUpdateCache(textDocument.getText(), textDocument.getUri());
	}

	private void parseAndUpdateCache(String text, String uri) {
		final LiteralSourceFile sourceFile = new LiteralSourceFile(uri, text);
		final CompilingPackage compilingPackage = new CompilingPackage(new ZSPackage(null, "test"), new Module("test"));
		ParsedFile parsedFile;
		final List<ParseException> parseExceptions = new ArrayList<>();

		try {
			final ZSTokenParser tokens = ZSTokenParser.create(sourceFile, null);

			parsedFile = ParsedFile.parse(compilingPackage, tokens);
			parseExceptions.addAll(tokens.getErrors());
		} catch (ParseException | IOException e) {
			Logger.getGlobal().log(Level.WARNING, "Got exception while opening", e);
			parsedFile = new ParsedFile(new VirtualSourceFile(uri));
			if (e instanceof ParseException) {
				parseExceptions.add((ParseException) e);
			}
		}
		parseExceptions.addAll(parsedFile.getErrors());

		openFiles.put(uri, parsedFile);


		final LanguageClient client = openzenLSPServer.getClient();
		if (client != null) {


			final List<Diagnostic> diagnostics = new ArrayList<>();
			for (ParseException error : parseExceptions) {

				final Position rangeStart = new Position(error.position.fromLine - 1, error.position.fromLineOffset);
				final Position rangeEnd = new Position(error.position.toLine - 1, error.position.toLineOffset);
				if (rangeEnd.equals(rangeStart)) {
					rangeEnd.setCharacter(rangeEnd.getCharacter() + 1);
				}


				Logger.getGlobal().log(Level.FINEST, "Got Exception! (message, position)", new Object[]{error.message, error.position});

				final Range range = new Range(rangeStart, rangeEnd);

				final String message = error.message;
				final DiagnosticSeverity severity = DiagnosticSeverity.Error;
				final String source = "LSP";

				final Diagnostic diagnostic = new Diagnostic(range, message, severity, source);
				diagnostics.add(diagnostic);
			}

			final PublishDiagnosticsParams publishDiagnosticsParams = new PublishDiagnosticsParams(uri, diagnostics);
			client.publishDiagnostics(publishDiagnosticsParams);
			Logger.getGlobal().log(Level.FINEST, "Published " + diagnostics.size() + " errors in " + uri, diagnostics);
		}
	}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {
		Logger.getGlobal().log(Level.FINER, "didChange(DidChangeTextDocumentParams params)");
		final VersionedTextDocumentIdentifier textDocument = params.getTextDocument();
		final List<TextDocumentContentChangeEvent> contentChanges = params.getContentChanges();
		if (contentChanges.size() > 0) {
			parseAndUpdateCache(contentChanges.get(0).getText(), textDocument.getUri());
		}
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
		Logger.getGlobal().log(Level.FINER, "didClose(DidCloseTextDocumentParams params)");

	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
		Logger.getGlobal().log(Level.FINER, "didSave(DidSaveTextDocumentParams params)");

	}

	@Override
	public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
		Logger.getGlobal().log(Level.FINER, "semanticTokensFull(SemanticTokensParams params)");
		System.err.println("SemanticTokensFull");
		return semanticTokenProvider.tokensFull(params);
	}

	@Override
	public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(DocumentHighlightParams params) {
		final ParsedFile parsedFile = openFiles.get(params.getTextDocument().getUri());
		final PackageDefinitions packageDefinitions = new PackageDefinitions();
		parsedFile.listDefinitions(packageDefinitions);
		final List<CodePosition> collect = packageDefinitions.getAll().stream()
				.map(highLevelDefinition -> highLevelDefinition.position.withLength(highLevelDefinition.name.length()))
				.collect(Collectors.toList());

		Logger.getGlobal().log(Level.FINER, "documentHighlight(DocumentHighlightParams params)", new Object[]{params, collect});
		return CompletableFuture.supplyAsync(() -> {
			final List<DocumentHighlight> result = new ArrayList<>();
			{
				for (CodePosition position : collect) {
					final Position start = new Position(position.fromLine, position.fromLineOffset);
					final Position end = new Position(position.toLine, position.toLineOffset);
					result.add(new DocumentHighlight(new Range(start, end), DocumentHighlightKind.Read));
				}


				final Position start = params.getPosition();
				final Position end = new Position(start.getLine(), start.getCharacter() + 5);
				final DocumentHighlight documentHighlight = new DocumentHighlight(new Range(start, end), DocumentHighlightKind.Write);
				result.add(documentHighlight);
			}
			return result;
		});
	}

	@Override
	public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
		Logger.getGlobal().log(Level.FINER, "definition(DefinitionParams params)");
		return CompletableFuture.supplyAsync(() -> {
			final String uri = params.getTextDocument().getUri();
			final Position start = new Position(1, 1);
			final Position end = new Position(1, 5);
			final Range range = new Range(start, end);
			final Location location = new Location(uri, range);

			return Either.forLeft(Collections.singletonList(location));
		});
	}
}
