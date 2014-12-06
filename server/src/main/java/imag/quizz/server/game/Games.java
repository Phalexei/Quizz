package imag.quizz.server.game;

import imag.quizz.server.tool.IdGenerator;

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
        final long id = IdGenerator.nextGame();
        final Game game = new Game(this.base, a, b);
        this.games.put(id, game);
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

    public Game getById(final long gameId) {
        return this.games.get(gameId);
    }

    /**
     * TODO Doc
     * Note: Should never return an empty set.
     * @param player
     * @return
     */
    public Set<Game> getByPlayer(final Player player) {
        return this.playerGames.get(player);
    }
}
