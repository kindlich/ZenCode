package org.openzen.zenscript.lsp.server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.openzen.zencode.shared.LiteralSourceFile;
import org.openzen.zencode.shared.VirtualSourceFile;
import org.openzen.zenscript.codemodel.Module;
import org.openzen.zenscript.codemodel.context.CompilingPackage;
import org.openzen.zenscript.codemodel.definition.ZSPackage;
import org.openzen.zenscript.lexer.ParseException;
import org.openzen.zenscript.lexer.ZSTokenParser;
import org.openzen.zenscript.lsp.server.semantictokens.LSPSemanticTokenProvider;
import org.openzen.zenscript.parser.ParsedFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OpenzenTextDocumentService implements TextDocumentService {
	private final LSPSemanticTokenProvider semanticTokenProvider;
	private final Map<String, ParsedFile> openFiles = new HashMap<>();

	public OpenzenTextDocumentService(LSPSemanticTokenProvider semanticTokenProvider) {
		this.semanticTokenProvider = semanticTokenProvider;
	}

	@Override
	public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {

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
		final String text = params.getTextDocument().getText();
		final LiteralSourceFile sourceFile = new LiteralSourceFile(params.getTextDocument().getUri(), text);
		final CompilingPackage compilingPackage = new CompilingPackage(new ZSPackage(null, "test"), new Module("test"));
		try {
			final ZSTokenParser tokens = ZSTokenParser.create(sourceFile, null);
			final ParsedFile parse = ParsedFile.parse(compilingPackage, tokens);
			openFiles.put(params.getTextDocument().getUri(), parse);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {

	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {

	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {

	}

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
		System.err.println("SemanticTokensFull");
		return semanticTokenProvider.tokensFull(params);
	}

	@Override
	public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(DocumentHighlightParams params) {
		return CompletableFuture.supplyAsync(() -> {
			final List<DocumentHighlight> result = new ArrayList<>();
			{
				final Position start = params.getPosition();
				final Position end = new Position(start.getLine(), start.getCharacter() + 3);
				final DocumentHighlight documentHighlight = new DocumentHighlight(new Range(start, end), DocumentHighlightKind.Write);
				result.add(documentHighlight);
			}
			return result;
		});
	}
}
