package imag.quizz.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class Main {

    /**
     *
     */
    public static void main(String[] args) {
        System.out.println("Hello Server!");
        try {
            final ServerSocket server = new ServerSocket(26001);
            final Socket client = server.accept();
            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
            final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
            String mes;
            while ((mes = in.readLine()) != null) {
                if ("PING".equals(mes)) {
                    out.write("PONG");
                    out.newLine();
                    out.flush();
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
