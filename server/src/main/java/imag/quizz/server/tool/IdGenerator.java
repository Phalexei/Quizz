package imag.quizz.server.tool;

public final class IdGenerator {

    private static long lastPlayerIdentifier = 1_000L;
    private static long lastGameIdentifier   = 1_000_000L;

    public static long nextPlayer() {
        return ++IdGenerator.lastPlayerIdentifier;
    }

    public static long nextGame() {
        return ++IdGenerator.lastGameIdentifier;
    }

    /**
     * Never instantiate this tool class.
     */
    private IdGenerator() {
    }
}
