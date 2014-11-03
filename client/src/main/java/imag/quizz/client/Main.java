package imag.quizz.client;

import imag.quizz.client.ui.Window;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 */
public class Main {

	/**
	 *
	 */
	public static void main(String[] args) {
		System.out.println("Hello Client!");

		// Create main window
		final Window window = new Window();

		// Map output to main window
		System.setOut(new PrintStream(new OutputStream() {
			
			private final PrintStream originalPrintStream = System.out;

			@Override
			public void write(int b) throws IOException {
				originalPrintStream.write(b);
				window.log("" + (char) b);
			}

			@Override
			public void write(byte[] b) throws IOException {
				super.write(b);
				window.log("\n");
			}
		}));

		System.out.println("Ready");
	}
}
