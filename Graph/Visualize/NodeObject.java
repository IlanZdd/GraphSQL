package Graph.Visualize;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class NodeObject {
    private int x;
    private int y;
    private final String name;

    protected NodeObject(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }
    protected int getX() { return x; }
    protected void setX(int x) {
        this.x = x;
    }
    protected int getOuterX(boolean right) {
        if (right) return getX() + ValueContainer.getNodeWidth() / 2;
        else return getX() - ValueContainer.getNodeWidth() / 2;
    }

    protected int getY() {
        return y;
    }
    protected void setY(int y) {
        this.y = y;
    }
    protected int getOuterY(boolean bottom) {
        if (bottom) return getY() + ValueContainer.getNodeHeight() / 2;
        else return getY() - ValueContainer.getNodeHeight() / 2;
    }
    
    protected String getName() {
        return name;
    }

    protected void render(Graphics2D g2d) {
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        g2d.setFont(ValueContainer.getFont());
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setStroke(new BasicStroke(1));

        // Fill the circle's background
        g2d.setColor(ValueContainer.getBackgroundColorOfNode(name));
        g2d.fillOval(x- ValueContainer.getNodeWidth()/2- ValueContainer.getCameraX(),
                y- ValueContainer.getNodeHeight()/2- ValueContainer.getCameraY(),
                ValueContainer.getNodeWidth(), ValueContainer.getNodeHeight());

        // Draw the circles
        g2d.setColor(ValueContainer.getColorOfNode(name));
        Shape circle = new Ellipse2D.Double(
                getOuterX(false)- ValueContainer.getCameraX(),
                getOuterY(false)- ValueContainer.getCameraY(),
                ValueContainer.getNodeWidth(), ValueContainer.getNodeHeight());
        g2d.draw(circle);
        circle = new Ellipse2D.Double(
                getOuterX(false)-2- ValueContainer.getCameraX(),
                getOuterY(false)-2- ValueContainer.getCameraY(),
                ValueContainer.getNodeWidth()+4, ValueContainer.getNodeHeight()+4);
        g2d.draw(circle);

        // Writes the name
        g2d.drawString(name, x - (float)fm.stringWidth(name)/2- ValueContainer.getCameraX(),
                y-((float)fm.getHeight()/2)+fm.getAscent()- ValueContainer.getCameraY());
    }
}
