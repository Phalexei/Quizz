package imag.quizz.server.game;

/**
 * Represents a Client.
 */
public final class Client {

    /**
     * The Client's login.
     */
    private String login;

    /**
     * The hashed Client's password.
     */
    private String passwordHash;

    /**
     * If this Client has logged in.
     */
    private boolean isLoggedIn;

    /**
     * Amount of Games the Client won.
     */
    private int wonGames;

    /**
     * Amount of Games the Client lost.
     */
    private int lostGames;

    /**
     * Amount of Games the Client neither won nor lost.
     */
    private int drawGames;
}
