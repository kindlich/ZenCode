package org.openzen.zenscript.lsp.launcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class SocketLauncher {
	public static final int PORT = 6666;

	public static void main(String[] args)
			throws ExecutionException, InterruptedException, IOException {
		System.out.println("Listening on Port " + PORT);
		try (final ServerSocket serverSocket = new ServerSocket(PORT);
		final Socket socket = serverSocket.accept()) {
			System.out.println("Connected.");
			new StreamBasedLauncher(socket.getInputStream(), socket.getOutputStream()).startServer();
		}
	}
}
