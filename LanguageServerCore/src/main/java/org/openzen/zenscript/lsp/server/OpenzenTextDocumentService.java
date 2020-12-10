package org.openzen.zenscript.lsp.server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OpenzenTextDocumentService implements TextDocumentService {
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
}
