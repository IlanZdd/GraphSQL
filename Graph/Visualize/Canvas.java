package Graph.Visualize;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Canvas extends JPanel {
    protected final CanvasHandler canvasHandler;
    protected final ButtonHandler buttonHandler;

    protected Canvas(CanvasHandler canvasHandler, ButtonHandler buttonHandler) {
        super();
        this.canvasHandler = canvasHandler;
        this.buttonHandler = buttonHandler;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ValueContainer.setCanvasSize(getWidth(), getHeight());
        setBackground(ValueContainer.getBackgroundColor());
        render(g, false);
    }

    protected void render(Graphics g, boolean areWeSaving) {
        Graphics2D g2d = (Graphics2D) g;
        canvasHandler.render(g2d, areWeSaving);
        buttonHandler.render(g2d);
        g2d.dispose();
    }
}
