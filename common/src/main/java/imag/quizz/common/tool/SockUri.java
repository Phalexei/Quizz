package imag.quizz.common.tool;

import java.net.Socket;

public final class SockUri {

    public static String from(final Socket socket) {
        return socket.getInetAddress().getHostAddress() + ':' + socket.getPort();
    }
}
