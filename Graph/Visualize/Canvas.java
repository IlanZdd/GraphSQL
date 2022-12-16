package Graph.Visualize;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Canvas extends JPanel {
    protected final Handler handler;

    protected Canvas(Handler handler) {
        super();
        this.handler = handler;
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
        handler.render(g2d, areWeSaving);
        g2d.dispose();
    }
}
