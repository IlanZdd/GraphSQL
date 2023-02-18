package Graph.Visualize;

import java.awt.*;

public class Button {
    private Point TLpoint;
    private int width;
    private int height;
    private String label;
    private boolean isSquare;
    private boolean isOnTheLeft;
    private boolean isOnTheTop;

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
        g2d.setColor(ValueContainer.getWritingColor());
        g2d.setFont(ValueContainer.getPanelFont());
        FontMetrics fm = g2d.getFontMetrics();

        if (isSquare)
            g2d.drawRect(TLpoint.x, TLpoint.y, width, height);
        else
            g2d.drawOval(TLpoint.x, TLpoint.y, width, height);

        g2d.drawString(label, TLpoint.x + width/2 - fm.stringWidth(label)/2,
                TLpoint.y + height - fm.getAscent()/2);
    }

    public Point getTLpoint() { return TLpoint; }
    public Point getTRpoint() { return new Point(TLpoint.x+width, TLpoint.y); }
    public Point getBLpoint() { return new Point(TLpoint.x, TLpoint.y+height); }
    public Point getBRpoint() { return new Point(TLpoint.x+width, TLpoint.y+height); }

    public void setTLpoint(Point TLpoint) { this.TLpoint = TLpoint;}
    public void setTLpointX(int x) { this.TLpoint.x = x;}
    public void setTLpointY(int y) { this.TLpoint.y = y;}

    public boolean isOnTheLeft() { return isOnTheLeft; }
    public boolean isOnTheTop() { return isOnTheTop; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
