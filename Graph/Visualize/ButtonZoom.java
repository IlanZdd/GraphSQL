package Graph.Visualize;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ButtonZoom extends Button{
    public ButtonZoom(Point TLpoint, int width, int height) {
        super(TLpoint, width, height, "", false, false, false);
    }

    @Override
    protected void paint (Graphics2D g2d) {
        g2d.setFont(ValueContainer.getPanelFont());
        FontMetrics fm = g2d.getFontMetrics();

        // draws the magnifying glass stick
        g2d.setColor(ValueContainer.getWritingColor());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(getTLpoint().x+getWidth()/2, getTLpoint().y+getHeight()/2,
                getBLpoint().x, getBLpoint().y);
        g2d.drawLine(getTLpoint().x-ValueContainer.getOffset()*3/2+getWidth()/2, getTLpoint().y+getHeight()/2,
                getBLpoint().x-ValueContainer.getOffset()*3/2, getBLpoint().y);

        //draws the rest of the button
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(ValueContainer.getBackgroundColorOfButton("zoomIn"));
        g2d.fillOval(getTLpoint().x, getTLpoint().y, getWidth(), getHeight());
        g2d.setColor(ValueContainer.getBackgroundColorOfButton("zoomOut"));
        g2d.fillOval(getTLpoint().x-ValueContainer.getOffset()*3/2, getTLpoint().y, getWidth(), getHeight());

        g2d.setColor(ValueContainer.getWritingColor());
        g2d.drawOval(getTLpoint().x, getTLpoint().y, getWidth(), getHeight());
        g2d.drawOval(getTLpoint().x-ValueContainer.getOffset()*3/2, getTLpoint().y, getWidth(), getHeight());

        g2d.drawString("+", getTLpoint().x + getWidth() /2 - fm.stringWidth("+")/2+1,
                getTLpoint().y + getHeight() - fm.getAscent()/2);
        g2d.drawString("-", getTLpoint().x-ValueContainer.getOffset()*3/2 + getWidth() /2 - fm.stringWidth("-")/2,
                getTLpoint().y + getHeight() - fm.getAscent()/2);
    }

    @Override
    public short contains(Point point) {
        if (new Ellipse2D.Float(getTLpoint().x, getTLpoint().y, getWidth(), getHeight()).contains(point)) {
            return 3; //zoom in
        } else if (new Ellipse2D.Float(getTLpoint().x-ValueContainer.getOffset()*3/2f, getTLpoint().y,
                getWidth(), getHeight()).contains(point)) {
            return 4; //zoom out
        }
        return 0;
    }
}
