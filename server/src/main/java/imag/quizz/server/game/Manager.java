package imag.quizz.server.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main game class.
 */
public final class Manager {

    /**
     * Games currently running for each Client.
     */
    private final Map<Client, List<Game>> clientGames;

    /**
     * Builds a Game Manager.
     */
    public Manager() {
        this.clientGames = new HashMap<>();
    }
}
