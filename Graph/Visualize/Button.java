package Graph.Visualize;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Button {
    private final Point TLpoint;
    private final int width;
    private final int height;
    private final String label;
    private final boolean isSquare;
    private final boolean isOnTheLeft;
    private final boolean isOnTheTop;

    public Button(Point TLpoint, int width, int height, String label, boolean isSquare, boolean isOnTheLeft, boolean isOnTheTop) {
        this.TLpoint = TLpoint;
        this.width = width;
        this.height = height;
        this.label = label;
        this.isSquare = isSquare;
        this.isOnTheLeft = isOnTheLeft;
        this.isOnTheTop = isOnTheTop;
    }

    protected void paint (Graphics2D g2d) {
        g2d.setColor(ValueContainer.getBackgroundColorOfButton(label));
        g2d.setFont(ValueContainer.getPanelFont());
        FontMetrics fm = g2d.getFontMetrics();

        // draws a background (according to selected/hovering/none) and the borders
        if (isSquare) {
            g2d.fillRect(TLpoint.x, TLpoint.y, width, height);
            g2d.setColor(ValueContainer.getWritingColor());
            g2d.drawRect(TLpoint.x, TLpoint.y, width, height);
        }
        else {
            g2d.fillOval(TLpoint.x, TLpoint.y, width, height);
            g2d.setColor(ValueContainer.getWritingColor());
            g2d.drawOval(TLpoint.x, TLpoint.y, width, height);
        }

        // writes the label, for colour draws a triangle
        if (!label.equalsIgnoreCase("colour"))
            g2d.drawString(label, TLpoint.x + width/2 - fm.stringWidth(label)/2,
                TLpoint.y + height - fm.getAscent()/2);
        else g2d.fillPolygon(new int[]{TLpoint.x, getBLpoint().x, getBRpoint().x},
                new int[]{TLpoint.y, getBLpoint().y, getBRpoint().y}, 3);
    }

    public Point getTLpoint() { return TLpoint; }
    public Point getTRpoint() { return new Point(TLpoint.x+width, TLpoint.y); }
    public Point getBLpoint() { return new Point(TLpoint.x, TLpoint.y+height); }
    public Point getBRpoint() { return new Point(TLpoint.x+width, TLpoint.y+height); }

    public void setTLPointX(int x) { this.TLpoint.x = x;}
    public void setTLPointY(int y) { this.TLpoint.y = y;}

    public boolean isOnTheLeft() { return isOnTheLeft; }
    public boolean isOnTheTop() { return isOnTheTop; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public String getLabel() { return label; }

    public short contains (Point point) {
        if (isSquare)
            if (new Rectangle(getTLpoint().x, getTLpoint().y, width, height).contains(point))
                return 1;
        else
            if (new Ellipse2D.Float(getTLpoint().x, getTLpoint().y, width, height).contains(point))
                return 1;
        return 0;
    }
}
