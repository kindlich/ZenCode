package org.openzen.zenscript.lsp.launcher;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.openzen.zenscript.lsp.server.OpenzenLSPServer;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

public class StreamBasedLauncher {
	@Nonnull
	private final InputStream in;
	@Nonnull
	private final OutputStream out;

	public StreamBasedLauncher(@Nonnull InputStream in, @Nonnull OutputStream out) {
		this.in = in;
		this.out = out;
	}

	public void startServer() throws InterruptedException, ExecutionException {
		final OpenzenLSPServer server = new OpenzenLSPServer();
		final Launcher<LanguageClient> clientLauncher = LSPLauncher.createServerLauncher(server, in, out);

		final LanguageClient remoteProxy = clientLauncher.getRemoteProxy();
		server.connect(remoteProxy);

		clientLauncher.startListening().get();
	}
}
