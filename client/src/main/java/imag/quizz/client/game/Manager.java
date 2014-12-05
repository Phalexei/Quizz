package imag.quizz.client.game;

import imag.quizz.client.network.ConnectionManager;
import imag.quizz.client.ui.Window;
import imag.quizz.common.Config;
import imag.quizz.common.tool.Log;

/**
 * Created by Ribesg.
 */
public class Manager {

    private Window window;
    private final Config config;
    private final ConnectionManager connectionManager;

    public Manager(Config config) {
        this.window = null;
        this.config = config;
        this.connectionManager
    }

    public void setWindow(final Window window) {
        this.window = window;
    }

    public void onButtonClick(final String textClicked) {
        Log.info("Réponse sélectionnée : \"" + textClicked + '"');
        this.window.lockButtons();
    }
}
