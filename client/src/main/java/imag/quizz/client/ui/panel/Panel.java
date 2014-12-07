package imag.quizz.client.ui.panel;

import javax.swing.*;
import java.awt.*;

public abstract class Panel extends JPanel {

    protected Panel() {
    }

    protected Panel(LayoutManager layoutManager) {
        super(layoutManager);
    }

    /**
     * Override this method to display incoming errors
     * @param error the error message to display
     */
    public void showError(final String error) {
    }

    /**
     * Override this method to prevent panel changing
     * @return false if this panel is not ready to be changed
     */
    public boolean isReady() {
        return true;
    }
}
