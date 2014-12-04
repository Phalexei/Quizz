package imag.quizz.server.game;

/**
 * Represents a Player.
 */
public final class Player extends Peer {

    /**
     * The Player's login.
     */
    private final String login;

    /**
     * The hashed Player's password.
     */
    private final String passwordHash;

    /**
     * If this Player has logged in.
     */
    private boolean isLoggedIn;

    /**
     * Game this Player is currently playing.
     */
    private Game currentGame;

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

    public Player(final int id, final int port, final String login, final String passwordHash) {
        super(id, port);
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public boolean isLoggedIn() {
        return this.isLoggedIn;
    }

    public Game getCurrentGame() {
        return this.currentGame;
    }

    public int getWonGames() {
        return this.wonGames;
    }

    public int getLostGames() {
        return this.lostGames;
    }

    public int getDrawGames() {
        return this.drawGames;
    }

    public void setLoggedIn(final boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public void setCurrentGame(final Game currentGame) {
        this.currentGame = currentGame;
    }

    public void setWonGames(final int wonGames) {
        this.wonGames = wonGames;
    }

    public void setLostGames(final int lostGames) {
        this.lostGames = lostGames;
    }

    public void setDrawGames(final int drawGames) {
        this.drawGames = drawGames;
    }

    @Override
    public Type getType() {
        return Type.PLAYER;
    }
}
