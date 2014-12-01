package imag.quizz.client.game;

import imag.quizz.client.ui.Window;
import imag.quizz.common.tool.Log;

/**
 * Created by Ribesg.
 */
public class Manager {

    private Window window;

    public Manager() {
        this.window = null;
    }

    public void setWindow(final Window window) {
        this.window = window;
    }

    public void onButtonClick(final String textClicked) {
        Log.info("Réponse sélectionnée : \"" + textClicked + '"');
    }
}
