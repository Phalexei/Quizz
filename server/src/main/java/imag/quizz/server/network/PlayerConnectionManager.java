package imag.quizz.server.network;

import imag.quizz.server.PlayerController;

public class PlayerConnectionManager extends ConnectionManager {

    public PlayerConnectionManager(final PlayerController controller, final int localPlayerPort, final long ownId) {
        super(controller, true, localPlayerPort, ownId);
    }
}
