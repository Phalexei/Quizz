package imag.quizz.server.network;

import imag.quizz.server.PlayerController;

/**
 * Created by Ribesg.
 */
public class PlayerConnectionManager extends ConnectionManager {

    public PlayerConnectionManager(final PlayerController controller, final int localPlayerPort, final int ownId) {
        super(controller, true, localPlayerPort, ownId);
    }
}
