package imag.quizz.server;

import imag.quizz.common.Controller;
import imag.quizz.common.network.MessageHandler;
import imag.quizz.common.network.SocketHandler;
import imag.quizz.common.protocol.PingPongTask;
import imag.quizz.common.protocol.message.*;
import imag.quizz.common.tool.Log;
import imag.quizz.common.tool.SockUri;
import imag.quizz.server.game.Game;
import imag.quizz.server.game.Game.PlayerStatus;
import imag.quizz.server.game.Player;
import imag.quizz.server.game.QuestionBase.Question;
import imag.quizz.server.network.PlayerConnectionManager;
import imag.quizz.server.tool.IdGenerator;

import java.util.ArrayList;
import java.util.Random;

public class PlayerController extends MessageHandler implements Controller {

    private static final Random RANDOM = new Random();

    private final ServerController        serverController;
    private final PlayerConnectionManager connectionManager;

    private final long ownId;

    private final PingPongTask pingPongTask;

    /**
     * Constructor.
     *
     * @param serverController the Server controller
     * @param ownId this server's identifier
     */
    public PlayerController(final ServerController serverController, final int localPlayerPort, final long ownId) {
        super("PlayerController");
        this.serverController = serverController;
        this.connectionManager = new PlayerConnectionManager(this, localPlayerPort, ownId);

        this.ownId = ownId;

        this.pingPongTask = new PingPongTask(this, 3_000);
        this.pingPongTask.start();
    }

    public PlayerConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    @Override
    public void pingTimeout(final String uri) {
        Log.warn(uri + " ping timeout!");
        this.connectionManager.killConnection(uri);
    }

    @Override
    public void ping(final String uri) {
        this.connectionManager.send(uri, new PingMessage(this.ownId));
    }

    @Override
    public void handleMessage(final SocketHandler socketHandler, final Message message) {
        final String uri = SockUri.from(socketHandler.getSocket());
        if (this.connectionManager.linksToPeer(uri)) {
            // Known and logged in Player
            final Player player = (Player) this.connectionManager.getLinkedPeer(uri);

            // Variables used multiple times in the following switch
            final Game game;
            final boolean isPlayerA;

            switch (message.getCommand()) {
                case ANSWER:
                    final AnswerMessage answerMessage = (AnswerMessage) message;
                    game = this.serverController.getGames().getGames().get(player.getCurrentGameId());
                    isPlayerA = game.getPlayerA() == player;
                    if (isPlayerA && game.getPlayerAStatus() != PlayerStatus.ANSWER_QUESTION || !isPlayerA && game.getPlayerBStatus() != PlayerStatus.ANSWER_QUESTION) {
                        this.connectionManager.send(player, new NokMessage(this.ownId, "Le joueur n'a aucune question en attente", message));
                    } else {
                        if (this.serverController.isLeader()) {
                            final boolean correct = game.playerSelectAnswer(player, answerMessage.getChosenAnswer());
                            if (correct) {
                                this.connectionManager.send(player, new OkMessage(this.ownId, "Réponse correcte", message));
                            } else {
                                this.connectionManager.send(player, new OkMessage(this.ownId, "Réponse incorrecte", message));
                            }
                            final PlayerStatus status = isPlayerA ? game.getPlayerAStatus() : game.getPlayerBStatus();
                            if (status == PlayerStatus.WAIT) {
                                if (game.getCurrentQuestionA() == 9 && game.getCurrentQuestionB() == 9) {
                                    final int playerScore = isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                                    final int opponentScore = !isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                                    this.connectionManager.send(player, new EndMessage(this.ownId, playerScore, opponentScore));
                                    final Player opponent = game.getOpponent(player);
                                    if (opponent.getUri() != null) {
                                        this.connectionManager.send(opponent, new EndMessage(this.ownId, opponentScore, playerScore));
                                    }
                                } else if ((isPlayerA ? game.getCurrentQuestionB() : game.getCurrentQuestionA()) == 0) {
                                    this.connectionManager.send(player, new WaitMessage(this.ownId, "Attendez que l'adversaire choisisse un thème"));
                                } else {
                                    this.connectionManager.send(player, new WaitMessage(this.ownId, "Attendez que l'adversaire finisse de répondre"));
                                }
                            } else {
                                final Question question = game.getCurrentQuestion(player);
                                if (question != null) {
                                    this.connectionManager.send(player, new QuestionMessage(this.ownId, question.getQuestion(), question.getAnswers()));
                                } else {
                                    final int playerScore = isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                                    final int opponentScore = !isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                                    this.connectionManager.send(player, new EndMessage(this.ownId, playerScore, opponentScore));
                                }
                            }
                        }
                        this.serverController.leaderBroadcast(message);
                    }
                    break;
                case DROP:
                    game = this.serverController.getGames().getGames().get(player.getCurrentGameId());
                    isPlayerA = game.getPlayerA() == player;
                    if (this.serverController.isLeader()) {
                        game.playerDrop(player);
                        final int playerScore = isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                        final int opponentScore = !isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                        this.connectionManager.send(player, new EndMessage(this.ownId, playerScore, opponentScore));
                        final Player opponent = game.getOpponent(player);
                        if (opponent.getUri() != null) {
                            this.connectionManager.send(opponent, new EndMessage(this.ownId, opponentScore, playerScore));
                        }
                    }
                    this.serverController.leaderBroadcast(message);
                    break;
                case GAMES:
                    this.connectionManager.send(player, new GamesMessage(this.ownId, this.serverController.buildGamesData(player, this.serverController.getGames().getByPlayer(player))));
                    break;
                case LOGIN:
                    this.login(uri, socketHandler, message);
                    break;
                case LOGOUT:
                    this.serverController.leaderBroadcast(message);
                    player.setLoggedIn(false);
                    player.setUri(null);
                    break;
                case NEW:
                    final NewMessage newMessage = (NewMessage) message;
                    final String opponentLogin = newMessage.getOpponent();
                    if (opponentLogin == null) {
                        if (this.serverController.getPlayers().size() == 1) {
                            // No opponent available
                            this.connectionManager.send(player, new NokMessage(this.ownId, "Aucun adversaire trouvé", message));
                        } else {
                            final ArrayList<Player> potentialOpponents = new ArrayList<>(this.serverController.getPlayers().values());
                            potentialOpponents.remove(player);
                            final Player opponent = potentialOpponents.get(PlayerController.RANDOM.nextInt(potentialOpponents.size()));
                            this.newGame(player, opponent);
                        }
                    } else {
                        final Player opponent = this.serverController.getPlayers().get(opponentLogin);
                        if (opponent == null) {
                            this.connectionManager.send(player, new NokMessage(this.ownId, "Adversaire inconnu", message));
                        } else {
                            this.newGame(player, opponent);
                        }
                    }
                    break;
                case NOANSWER:
                    game = this.serverController.getGames().getGames().get(player.getCurrentGameId());
                    isPlayerA = game.getPlayerA() == player;
                    if (isPlayerA && game.getPlayerAStatus() != PlayerStatus.ANSWER_QUESTION || !isPlayerA && game.getPlayerBStatus() != PlayerStatus.ANSWER_QUESTION) {
                        this.connectionManager.send(player, new NokMessage(this.ownId, "Le joueur n'a aucune question en attente", message));
                    } else {
                        if (this.serverController.isLeader()) {
                            game.playerDoesntAnswer(player);
                            this.connectionManager.send(player, new NokMessage(this.ownId, "Réponse incorrecte", message));
                            final PlayerStatus status = isPlayerA ? game.getPlayerAStatus() : game.getPlayerBStatus();
                            if (status == PlayerStatus.WAIT) {
                                if (game.getCurrentQuestionA() == 9 && game.getCurrentQuestionB() == 9) {
                                    final int playerScore = isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                                    final int opponentScore = !isPlayerA ? game.getPlayerAScore() : game.getPlayerBScore();
                                    this.connectionManager.send(player, new EndMessage(this.ownId, playerScore, opponentScore));
                                    final Player opponent = game.getOpponent(player);
                                    if (opponent.getUri() != null) {
                                        this.connectionManager.send(opponent, new EndMessage(this.ownId, opponentScore, playerScore));
                                    }
                                } else if ((isPlayerA ? game.getCurrentQuestionB() : game.getCurrentQuestionA()) == 0) {
                                    this.connectionManager.send(player, new WaitMessage(this.ownId, "Attendez que l'adversaire choisisse un thème"));
                                } else {
                                    this.connectionManager.send(player, new WaitMessage(this.ownId, "Attendez que l'adversaire finisse de répondre"));
                                }
                            } else {
                                final Question question = game.getCurrentQuestion(player);
                                this.connectionManager.send(player, new QuestionMessage(this.ownId, question.getQuestion(), question.getAnswers()));
                            }
                        }
                        this.serverController.leaderBroadcast(message);
                    }
                    break;
                case PING:
                    this.connectionManager.send(player, new PongMessage(this.ownId, message));
                    break;
                case PLAY:
                    final PlayMessage playMessage = (PlayMessage) message;
                    game = this.serverController.getGames().getGames().get(playMessage.getGameId());
                    if (game == null) {
                        this.connectionManager.send(player, new NokMessage(this.ownId, "Partie inconnue", message));
                    } else if (game.getPlayerA() != player && game.getPlayerB() != player) {
                        this.connectionManager.send(player, new NokMessage(this.ownId, "Le joueur ne participe pas à cette partie", message));
                    } else {
                        player.setCurrentGameId(game.getId());
                        final PlayerStatus status;
                        isPlayerA = game.getPlayerA() == player;
                        if (isPlayerA) {
                            status = game.getPlayerAStatus();
                        } else {
                            status = game.getPlayerBStatus();
                        }
                        switch (status) {
                            case SELECT_THEME:
                                this.connectionManager.send(player, new ThemesMessage(this.ownId, game.getId(), isPlayerA ? game.getThemesA() : game.getThemesB()));
                                break;
                            case ANSWER_QUESTION:
                                final Question question = game.getCurrentQuestion(player);
                                this.connectionManager.send(player, new QuestionMessage(this.ownId, question.getQuestion(), question.getAnswers()));
                                break;
                            case WAIT:
                                if ((isPlayerA ? game.getCurrentQuestionB() : game.getCurrentQuestionA()) == 0) {
                                    this.connectionManager.send(player, new WaitMessage(this.ownId, "Attendez que l'adversaire choisisse un thème"));
                                } else {
                                    this.connectionManager.send(player, new WaitMessage(this.ownId, "Attendez que l'adversaire finisse de répondre"));
                                }
                                break;
                        }
                        this.serverController.leaderBroadcast(message);
                    }
                    break;
                case PONG:
                    this.pingPongTask.pong(uri);
                    break;
                case REGISTER:
                    this.register(uri, socketHandler, message);
                    break;
                case THEME:
                    final ThemeMessage themeMessage = (ThemeMessage) message;
                    game = this.serverController.getGames().getGames().get(player.getCurrentGameId());
                    isPlayerA = game.getPlayerA() == player;
                    if (isPlayerA && game.getPlayerAStatus() != PlayerStatus.SELECT_THEME || !isPlayerA && game.getPlayerBStatus() != PlayerStatus.SELECT_THEME) {
                        this.connectionManager.send(player, new NokMessage(this.ownId, "Le joueur n'a pas à choisir de thème", message));
                    } else {
                        if (this.serverController.isLeader()) {
                            final boolean opponentUnlocked = game.playerSelectTheme(player, themeMessage.getChosenTheme());
                            final Player opponent = game.getOpponent(player);
                            if (opponentUnlocked && opponent.getUri() != null) {
                                final Question question = game.getCurrentQuestion(opponent);
                                this.connectionManager.send(opponent, new QuestionMessage(this.ownId, question.getQuestion(), question.getAnswers()));
                            }
                            final Question question = game.getCurrentQuestion(player);
                            this.connectionManager.send(player, new QuestionMessage(this.ownId, question.getQuestion(), question.getAnswers()));
                        }
                        this.serverController.leaderBroadcast(message);
                    }
                    break;
                default:
                    this.connectionManager.send(player, new NokMessage(this.ownId, "Message inattendu", message));
                    break;
            }
        } else {
            // Unknown Player new connection or logged out client
            switch (message.getCommand()) {
                case LOGIN:
                    this.login(uri, socketHandler, message);
                    break;
                case REGISTER:
                    this.register(uri, socketHandler, message);
                    break;
                default:
                    this.connectionManager.send(uri, new NokMessage(this.ownId, "Message inattendu", message));
                    break;
            }
        }
    }

    private void login(final String uri, final SocketHandler socketHandler, final Message message) {
        final LoginMessage loginMessage = (LoginMessage) message;
        final String loginMessageLogin = loginMessage.getLogin();
        final String hashedPassword = loginMessage.getHashedPassword();
        if (!this.serverController.getPlayers().containsKey(loginMessageLogin)) {
            this.connectionManager.send(uri, new NokMessage(this.ownId, "Login inconnu", message));
        } else {
            final Player player = this.serverController.getPlayers().get(loginMessageLogin);
            if (player.getPasswordHash().equals(hashedPassword)) {
                player.setLoggedIn(true);
                this.connectionManager.learnConnectionPeerIdentity(player, socketHandler);
                message.setSourceId(player.getId());
                this.connectionManager.send(uri, new OkMessage(this.ownId, "Connexion réussie", message));
                this.connectionManager.send(uri, new GamesMessage(this.ownId, this.serverController.buildGamesData(player, this.serverController.getGames().getByPlayer(player))));
                this.serverController.leaderBroadcast(message);
            } else {
                this.connectionManager.send(uri, new NokMessage(this.ownId, "Mot de passe incorrect", message));
            }
        }
    }

    private void register(final String uri, final SocketHandler socketHandler, final Message message) {
        final RegisterMessage registerMessage = (RegisterMessage) message;
        final String registerMessageLogin = registerMessage.getLogin();
        if (this.serverController.getPlayers().containsKey(registerMessageLogin)) {
            this.connectionManager.send(uri, new NokMessage(this.ownId, "Login déjà pris", message));
        } else {
            if (this.serverController.isLeader()) {
                final long id = IdGenerator.nextPlayer();
                final Player player = new Player(id, uri, registerMessageLogin, registerMessage.getHashedPassword());
                this.connectionManager.learnConnectionPeerIdentity(player, socketHandler);
                this.serverController.getPlayers().put(registerMessageLogin, player);
                message.setSourceId(id);
                this.connectionManager.send(player, new OkMessage(this.ownId, message));
            }
            this.serverController.leaderBroadcast(message);
        }
    }

    private void newGame(final Player player, final Player opponent) {
        final Game game = this.serverController.getGames().newGame(player, opponent);
        player.setCurrentGameId(game.getId());
        this.connectionManager.send(player, new ThemesMessage(this.ownId, game.getId(), game.getThemesA()));
        if (opponent.getUri() != null) {
            final String gameData = this.serverController.buildGamesData(opponent, this.serverController.getGames().getByPlayer(opponent));
            this.connectionManager.send(opponent, new GamesMessage(this.ownId, gameData));
        }
        this.serverController.leaderBroadcast(new GameMessage(this.ownId, game.toMessageData()));
    }

    @Override
    public void lostConnection(final SocketHandler socketHandler) {
        final String uri = SockUri.from(socketHandler.getSocket());
        final Player player = (Player) this.connectionManager.getLinkedPeer(uri);
        this.pingPongTask.removeUri(uri);
        this.serverController.leaderBroadcast(new LogoutMessage(this.ownId, player.getLogin()));
        this.connectionManager.forgetConnection(uri);
    }
}
