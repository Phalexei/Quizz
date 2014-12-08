package imag.quizz.server.tool;

public final class IdGenerator {

    private static long lastPlayerIdentifier = 1_000L;

    public static long nextPlayer() {
        return ++IdGenerator.lastPlayerIdentifier;
    }

    /**
     * Never instantiate this tool class.
     */
    private IdGenerator() {
    }
}
