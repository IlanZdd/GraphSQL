package Graph.Visualize;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class NodeObj {
    private int x;
    private int y;
    private final String name;

    public NodeObj(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public int getOuterX(boolean right) {
        if (right)
            return getX() + ConstantHandler.getWidthNode() / 2;
        else
            return getX() - ConstantHandler.getWidthNode() / 2;
    }

    public int getOuterY(boolean bottom) {
        if (bottom)
            return getY() + ConstantHandler.getHeightNode() / 2;
        else
            return getY() - ConstantHandler.getHeightNode() / 2;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    protected void render(Graphics2D g2d) {
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        g2d.setFont(ConstantHandler.getFont());
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setStroke(new BasicStroke(1));

        g2d.setColor(ConstantHandler.getThisNodeBackground(name));
        g2d.fillOval(x-ConstantHandler.getWidthNode()/2-ConstantHandler.getCameraX(),
                y-ConstantHandler.getHeightNode()/2-ConstantHandler.getCameraY(),
                ConstantHandler.getWidthNode(), ConstantHandler.getHeightNode());

        g2d.setColor(ConstantHandler.getThisNodeColor(name));
        Shape circle = new Ellipse2D.Double(x-ConstantHandler.getWidthNode()/2.0-ConstantHandler.getCameraX(),
                y-ConstantHandler.getHeightNode()/2.0-ConstantHandler.getCameraY(),
                ConstantHandler.getWidthNode(), ConstantHandler.getHeightNode());
        g2d.draw(circle);
        circle = new Ellipse2D.Double(x-ConstantHandler.getWidthNode()/2.0-2-ConstantHandler.getCameraX(),
                y-ConstantHandler.getHeightNode()/2.0-2-ConstantHandler.getCameraY(),
                ConstantHandler.getWidthNode()+4, ConstantHandler.getHeightNode()+4);
        g2d.draw(circle);

        g2d.drawString(name, x - (float)fm.stringWidth(name)/2-ConstantHandler.getCameraX(),
                y-((float)fm.getHeight()/2)+fm.getAscent()-ConstantHandler.getCameraY());
    }
}
