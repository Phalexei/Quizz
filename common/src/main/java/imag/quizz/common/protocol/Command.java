package imag.quizz.common.protocol;

/**
 * There are 4 types of messages:
 * - [GP] General purpose message
 * - [CS] Client -> Server messages
 * - [SC] Server -> Client messages
 * - [SS] Server -> Server messages
 *
 * Some messages have to be broadcasted to all servers for each server
 * to be able to keep track of all games and users. This messages are marked
 * with a [B].
 */
public enum Command {

    /**
     * [GP] Obvious.
     * Sent every 10 seconds. Requires a {@link #PONG} answer under 3 seconds
     * for an SS connection and under 5 seconds for a CS connection.
     */
    PING,

    /**
     * [GP] Obvious.
     */
    PONG,

    /**
     * [GP] Signals a success.
     */
    OK,

    /**
     * [GP] Signals a failure.
     */
    NOK,

    /**
     * [SS] Used by the leader to initialize a new Server with existing games
     * and users.
     */
    INIT,

    /**
     * [B][CS] Client registration message.
     * Contains login and password's hash, separated by
     * {@link Separator#LEVEL_1}s.
     */
    REGISTER,

    /**
     * [B][CS] Client login message.
     * Contains login and password's hash, separated by
     * {@link Separator#LEVEL_1}s.
     */
    LOGIN,

    /**
     * [B] Notifiy servers of player logout
     * Contains the player's ID
     */
    LOGOUT,

    /**
     * [CS & SC]
     * <p>
     * Sent by Clients to request the list of games. Empty.
     * <p>
     * Sent by Servers to transmit the list of games.
     * Contains the list of games as a set of {@link Separator#LEVEL_2}
     * separated games.
     * A game is composed of an ID, the opponent name and the current scores,
     * separated by {@link Separator#LEVEL_1}s.
     */
    GAMES,

    /**
     * [CS] Sent by Clients to create a new game. Can contain a user name,
     * in which case a new game will start against that opponent if it
     * exists, or be empty to select a random opponent.
     * If the provided user name doesn't exist or if there is no opponent,
     * the Client receives a {@link #NOK}.
     */
    NEW,

    /**
     * [SS] Sent by a Server to the Leader then to other Servers by the
     * Leader.
     * Contains all data about a newly created game as defined in the {@link
     * #INIT} documentation.
     */
    GAME,

    /**
     * [CS] Client chooses a game. Contains game ID.
     */
    PLAY,

    /**
     * [SC] Server proposes 4 themes to Client, separated by {@link
     * Separator#LEVEL_1}s.
     */
    THEMES,

    /**
     * [B][CS] Client answers to {@link #THEMES} message. Contains the number
     * (1, 2, 3 or 4) of the theme position in the {@link #THEMES} message.
     */
    THEME,

    /**
     * [SC] Server asks the Client a question.
     * Contains the question and 4 answers, separated by {@link
     * Separator#LEVEL_1}s.
     */
    QUESTION,

    /**
     * [B][CS] Client choose an answer. Contains the number
     * (1, 2, 3 or 4) of the answer position in the {@link #QUESTION}
     * message.
     */
    ANSWER,

    /**
     * [B][CS] The user didn't choose an answer in time.
     */
    NOANSWER,

    /**
     * [SC] Server says Client has to wait for opponent.
     */
    WAIT,

    /**
     * [B][CS] User give up the game. Contains user login and game ID,
     * separated by {@link Separator#LEVEL_1}.
     */
    DROP,

    /**
     * [SC] Game has ended, Server sends user score and opponent score (in
     * this order) to Client separated by {@link Separator#LEVEL_1}s.
     */
    END,
}
