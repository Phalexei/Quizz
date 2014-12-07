package imag.quizz.server.game;

import imag.quizz.common.protocol.Separator;
import imag.quizz.common.tool.Hash;
import org.apache.commons.lang3.Validate;

/**
 * Represents a Player.
 */
public final class Player extends Peer {

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
     * Id of the Game this Player is currently playing.
     */
    private long currentGameId;

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

    public Player(final long id, final int port, final String login, final String passwordHash) {
        super(id, port);
        this.login = login;
        this.passwordHash = passwordHash;
        this.isLoggedIn = true;
        this.currentGameId = -1;
        this.wonGames = 0;
        this.lostGames = 0;
        this.drawGames = 0;
    }

    private Player(final long id) {
        super(id, -1);
    }

    public String toMessageData(final int separatorLevel) {
        Validate.inclusiveBetween(1, Separator.AMOUNT, separatorLevel, "Invalid separator level");
        final String separator = Separator.get(separatorLevel);

        final StringBuilder builder = new StringBuilder();

        builder.append(this.id).append(separator);
        builder.append(this.login).append(separator);
        builder.append(Hash.encodeBase64(this.passwordHash)).append(separator);
        builder.append(this.isLoggedIn).append(separator);
        builder.append(this.currentGameId).append(separator);
        builder.append(this.wonGames).append(separator);
        builder.append(this.lostGames).append(separator);
        builder.append(this.drawGames);

        return builder.toString();
    }

    public static Player fromMessageData(final String playerData, final int separatorLevel) {
        Validate.inclusiveBetween(1, Separator.AMOUNT, separatorLevel, "Invalid separator level");
        final String separator = Separator.get(separatorLevel);

        final String[] split = playerData.split(separator);
        Validate.isTrue(split.length == 8, "Invalid data String");
        final long id = Long.parseLong(split[0]);
        final String login = split[1];
        final String passwordHash = Hash.decodeBase64(split[2]);
        final boolean isLoggedIn = Boolean.parseBoolean(split[3]);
        final long currentGameId = Long.parseLong(split[4]);
        final int wonGames = Integer.parseInt(split[5]);
        final int lostGames = Integer.parseInt(split[6]);
        final int drawGames = Integer.parseInt(split[7]);

        final Player player = new Player(id);
        player.login = login;
        player.passwordHash = passwordHash;
        player.isLoggedIn = isLoggedIn;
        player.currentGameId = currentGameId;
        player.wonGames = wonGames;
        player.lostGames = lostGames;
        player.drawGames = drawGames;

        return player;
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

    public long getCurrentGameId() {
        return this.currentGameId;
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

    public void setCurrentGameId(final long id) {
        this.currentGameId = id;
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
