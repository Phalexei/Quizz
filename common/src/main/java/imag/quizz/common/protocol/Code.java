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
     * A game is composed of an ID, the opponent name and the current score,
     * separated by spaces.
     */
    GAMES,

    /**
     * [CS] Client chooses a game. Contains game ID.
     */
    PLAY,

    /**
     * [SC] Server proposes 4 themes to Client, separated by tabs.
     */
    THEMES,

    /**
     * [CS] Client answers to {@link #THEMES} message. Contains the number
     * (1, 2, 3 or 4) of the theme position in the {@link #THEMES} message.
     */
    THEME,

    /**
     * [SC] Server asks the Client a question.
     * Contains the question and 4 answers, separated by tabs.
     */
    QUESTION,

    /**
     * [CS] Client choose an answer. Contains the number
     * (1, 2, 3 or 4) of the answer position in the {@link #QUESTION} message.
     */
    ANSWER,

    /**
     * [CS] The user didn't choose an answer in time.
     */
    NOANSWER,

    /**
     * [SC] Server says Client has to wait for opponent.
     */
    WAIT,

    /**
     * [CS] User give up the game. Contains game ID.
     */
    DROP,

    /**
     * [SC] Game has ended, Server sends user score and opponent score (in
     * this order) to Client.
     */
    END,
    ;
}
