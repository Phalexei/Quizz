package imag.quizz.server.game;

import imag.quizz.common.network.SocketHandler;

/**
 * Represents a Player.
 */
public final class Player extends Client {

    /**
     * The Player's login.
     */
    private String login;

    /**
     * The hashed Player's password.
     */
    private String passwordHash;

    /**
     * If this Player has logged in.
     */
    private boolean isLoggedIn;

    /**
     * Amount of Games the Player won.
     */
    private int wonGames;

    /**
     * Amount of Games the Player lost.
     */
    private int lostGames;

    /**
     * Amount of Games the Player neither won nor lost.
     */
    private int drawGames;

    public Player(SocketHandler socketHandler) {
        super(socketHandler);
    }
}
