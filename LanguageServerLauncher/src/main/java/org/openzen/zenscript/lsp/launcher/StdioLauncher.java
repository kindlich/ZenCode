package org.openzen.zenscript.lsp.launcher;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Simplest launcher.
 * Uses System in and System out to communicate with other processes
 */
public class StdioLauncher {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		disableLoggingOnStdio();
		new StreamBasedLauncher(System.in, System.out).startServer();
	}

	private static void disableLoggingOnStdio() {
		LogManager.getLogManager().reset();
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.OFF);
	}
}
