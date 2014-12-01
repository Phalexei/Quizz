package imag.quizz.server;

/**
 *
 */
public class Main {

    public static final int SERVER_PORT = 26000;
    /**
     *
     */
    public static void main(String[] args) {

        if (args.length < 1) {
            throw new IllegalArgumentException("Usage : specify server number in argument");
        }

        int serverId = Integer.valueOf(args[0]);
        Server server = new Server(serverId);

        while (true) ;
    }
}
