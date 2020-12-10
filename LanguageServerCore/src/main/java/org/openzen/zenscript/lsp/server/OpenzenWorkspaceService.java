package org.openzen.zenscript.lsp.server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OpenzenWorkspaceService implements WorkspaceService {

	@Override
	public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams params) {
		return WorkspaceService.super.symbol(params);
	}

	@Override
	public void didChangeConfiguration(DidChangeConfigurationParams params) {

	}

	@Override
	public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {

	}

	@Override
	public void didChangeWorkspaceFolders(DidChangeWorkspaceFoldersParams params) {

	}
}
