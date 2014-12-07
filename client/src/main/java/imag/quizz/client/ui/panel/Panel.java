package imag.quizz.client.ui.panel;

import javax.swing.*;
import java.awt.*;

public abstract class Panel extends JPanel {

    protected Panel() {

    }
    protected Panel(LayoutManager layoutManager) {
        super(layoutManager);
    }

    public abstract void showError(final String error);
}
