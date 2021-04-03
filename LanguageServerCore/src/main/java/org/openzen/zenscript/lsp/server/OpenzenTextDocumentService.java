package org.openzen.zenscript.lsp.server;

import com.google.common.collect.Streams;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.openzen.zencode.shared.CodePosition;
import org.openzen.zenscript.codemodel.PackageDefinitions;
import org.openzen.zenscript.lexer.ParseException;
import org.openzen.zenscript.lexer.ZSToken;
import org.openzen.zenscript.lexer.ZSTokenParser;
import org.openzen.zenscript.lsp.server.semantictokens.LSPSemanticTokenProvider;
import org.openzen.zenscript.parser.ParsedFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpenzenTextDocumentService implements TextDocumentService {
	private final LSPSemanticTokenProvider semanticTokenProvider;
	private final OpenzenLSPServer openzenLSPServer;
	private final Map<String, OpenFileInfo> openFiles = new HashMap<>();


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
		final OpenFileInfo from = OpenFileInfo.createFrom(text, uri);
		openFiles.put(uri, from);
		final LanguageClient client = openzenLSPServer.getClient();
		if (client != null) {
			client.publishDiagnostics(from.diagnosticsParams);
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
		Logger.getGlobal().log(Level.FINER, "documentHighlight(DocumentHighlightParams params)");
		final OpenFileInfo openFileInfo = openFiles.get(params.getTextDocument().getUri());
		return CompletableFuture.supplyAsync(() -> openFileInfo.getHighlightOnPosition(params.getPosition()));
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
