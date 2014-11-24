package imag.quizz.client;

import imag.quizz.client.ui.Window;
import imag.quizz.common.Config;
import imag.quizz.common.network.SocketHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map.Entry;

/**
 *
 */
public class Main {

    /**
     *
     */
    public static void main(String[] args) {
        System.out.println("Hello Client!");

        final Config config = new Config();
        System.out.println("Servers in config: ");
        for (final Entry<Integer, String> entry : config.getServers().entrySet()) {
            System.out.println("- " + entry.getKey() + " => " + entry.getValue());
        }

        System.exit(42);

        final SocketHandler handler = new SocketHandler("127.0.0.1", 26001);
        try {
            handler.connect();
        } catch (IOException e) {
            e.printStackTrace();  // TODO Implement method
        }

        // Create main window
        final Window window = new Window(handler);

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
