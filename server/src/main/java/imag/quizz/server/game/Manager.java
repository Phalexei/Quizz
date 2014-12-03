package imag.quizz.server.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main game class.
 */
public final class Manager {

    /**
     * Games currently running for each Player.
     */
    private final Map<Player, List<Game>> playerGames;

    /**
     * Builds a Game Manager.
     */
    public Manager() {
        this.playerGames = new HashMap<>();
    }
}
