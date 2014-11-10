package imag.quizz.common.protocol;

/**
 * There are 4 types of messages:
 * - General purpose message (GP)
 * - Client -> Server messages (CS)
 * - Server -> Client messages (SC)
 * - Server -> Server messages (SS)
 */
public enum Code {

    /**
     * [GP] Obvious.
     * Sent every 5 seconds. Requires a {@link #PONG} answer under 3 seconds
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
     * [CS] Client registration message.
     * Contains login and password's hash, separated by spaces.
     */
    REGISTER,

    /**
     * [CS] Client login message.
     * Contains login and password's hash, separated by spaces.
     */
    LOGIN,

    /**
     * [CS & SC]
     * <p>
     * Sent by Clients to request the list of games. Empty.
     * <p>
     * Sent by Servers to transmit the list of games.
     * Contains the list of games as a set of tab separated games.
     * A game is composed of the opponent name and the current score, separated by a space.
     */
    GAMES,

    /**
     * [CS] Client chooses a Game. Contains Game ID.
     */
    PLAY,

    /**
     * [SC] Server proposes multiple themes to Client.
     */
    THEMES,

    /**
     * [CS] Client answers to {@link #THEMES} message.
     */
    THEME,

    /**
     * [SC] Server asks the Client a question.
     * Contains the question and 4 answers.
     */
    QUESTION,
}
