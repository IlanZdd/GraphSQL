package Graph.Visualize;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ZoomButtons extends Button{
    public ZoomButtons(Point TLpoint, int width, int height) {
        super(TLpoint, width, height, "", false, false, false);
    }

    @Override
    protected void paint (Graphics2D g2d) {
        g2d.setFont(ValueContainer.getPanelFont());
        FontMetrics fm = g2d.getFontMetrics();

        g2d.setColor(ValueContainer.getWritingColor());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(getTLpoint().x+getWidth()/2, getTLpoint().y+getHeight()/2,
                getBLpoint().x, getBLpoint().y);
        g2d.drawLine(getTLpoint().x-ValueContainer.getPanelOffset()*3/2+getWidth()/2, getTLpoint().y+getHeight()/2,
                getBLpoint().x-ValueContainer.getPanelOffset()*3/2, getBLpoint().y);

        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(ValueContainer.getBackgroundColor());
        g2d.fillOval(getTLpoint().x, getTLpoint().y, getWidth(), getHeight());
        g2d.fillOval(getTLpoint().x-ValueContainer.getPanelOffset()*3/2, getTLpoint().y, getWidth(), getHeight());

        g2d.setColor(ValueContainer.getWritingColor());
        g2d.drawOval(getTLpoint().x, getTLpoint().y, getWidth(), getHeight());
        g2d.drawOval(getTLpoint().x-ValueContainer.getPanelOffset()*3/2, getTLpoint().y, getWidth(), getHeight());

        g2d.drawString("+", getTLpoint().x + getWidth() /2 - fm.stringWidth("+")/2+1,
                getTLpoint().y + getHeight() - fm.getAscent()/2);
        g2d.drawString("-", getTLpoint().x-ValueContainer.getPanelOffset()*3/2 + getWidth() /2 - fm.stringWidth("-")/2,
                getTLpoint().y + getHeight() - fm.getAscent()/2);
    }

    @Override
    public short contains(Point point) {
        if (new Ellipse2D.Float(getTLpoint().x, getTLpoint().y, getWidth(), getHeight()).contains(point)) {
            ValueContainer.zoomIn();
            return 1;
        } else if (new Ellipse2D.Float(getTLpoint().x-ValueContainer.getPanelOffset()*3/2f, getTLpoint().y,
                getWidth(), getHeight()).contains(point)) {
            ValueContainer.zoomOut();
            return 1;
        }
        return 0;
    }
}
