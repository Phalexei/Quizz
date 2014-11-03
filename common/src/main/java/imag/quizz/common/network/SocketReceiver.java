package imag.quizz.common.network;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketTimeoutException;

public class SocketReceiver extends AbstractRepeatingThread {

	private final BufferedReader reader;

	/* package */ SocketReceiver(final BufferedReader reader) {
		super("S-Receiver", 10);
		this.reader = reader;
	}

	@Override
	public void work() {
		String mes;
		try {
			while ((mes = this.reader.readLine()) != null) {
				// TODO Handle packet 'mes'
			}
		} catch (final SocketTimeoutException ignored) {
			// readLine() Timeout
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/* package */ void kill() {
		try {
			this.reader.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
