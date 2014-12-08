package imag.quizz.server.game;

import java.util.*;

public final class Games {

    private final QuestionBase base;

    private final SortedMap<Long, Game>  games;
    private final Map<Player, Set<Game>> playerGames;

    public Games(final QuestionBase base) {
        this.base = base;
        this.games = new TreeMap<>();
        this.playerGames = new HashMap<>();
    }

    public Game newGame(final Player a, final Player b) {
        final Game game = new Game(this.base, a, b);
        this.games.put(game.getId(), game);
        this.addPlayerGame(a, game);
        this.addPlayerGame(b, game);
        return game;
    }

    private void addPlayerGame(final Player player, final Game game) {
        Set<Game> playerGames = this.playerGames.get(player);
        if (playerGames == null) {
            playerGames = new HashSet<>();
            this.playerGames.put(player, playerGames);
        }
        playerGames.add(game);
    }

    /**
     * Get the games of the specified player
     * Note: Should never return an empty set.
     * @param player the player
     * @return the player's games
     */
    public Set<Game> getByPlayer(final Player player) {
        return this.playerGames.get(player);
    }

    public SortedMap<Long, Game> getGames() {
        return this.games;
    }
}
