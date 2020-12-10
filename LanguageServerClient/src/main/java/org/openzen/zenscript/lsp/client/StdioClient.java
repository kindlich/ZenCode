package org.openzen.zenscript.lsp.client;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class StdioClient {
	public static final int PORT = 6666;
	public static final String HOST = "localhost";

	public static void main(String[] args) {
		disableLoggingOnStdio();
		try {
			System.out.println("Connecting to " + HOST + " on " + PORT);
			final Socket socket = new Socket(HOST, PORT);
			final TransferThread sysInToNetwork = new TransferThread("SysInToNetwork", System.in, socket.getOutputStream());
			final TransferThread networkToSysout = new TransferThread("NetworkToSysout", socket.getInputStream(), System.out);
			networkToSysout.start();
			sysInToNetwork.start();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private static void disableLoggingOnStdio() {
		LogManager.getLogManager().reset();
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.OFF);
	}
}