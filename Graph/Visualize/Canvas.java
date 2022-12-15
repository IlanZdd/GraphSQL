package Graph.Visualize;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Canvas extends JPanel {
    Handler handler;

    public Canvas(Handler handler) {
        super();
        this.handler = handler;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ConstantHandler.setCanvasSize(getWidth(), getHeight());
        setBackground(ConstantHandler.getBackgroundColor());
        render(g, false);
    }

    public void render(Graphics g, boolean areWeSaving) {
        Graphics2D g2d = (Graphics2D) g;
        handler.render(g2d, areWeSaving);
        g2d.dispose();
    }
}
