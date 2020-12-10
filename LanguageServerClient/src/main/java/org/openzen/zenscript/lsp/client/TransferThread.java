package org.openzen.zenscript.lsp.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

public class TransferThread extends Thread {

	private final InputStream inputStream;
	private final OutputStream outputStream;

	public TransferThread(String name, InputStream inputStream, OutputStream outputStream) {
		super(name);
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	@Override
	public void run() {
		int readChar;
		try {
			while ((readChar = inputStream.read()) > 0) {
				System.err.printf("[%s:%s] Read %s%n", getName(), LocalDateTime.now(), (char) readChar);
				outputStream.write(readChar);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
