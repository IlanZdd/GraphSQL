package Graph.Visualize;

import java.awt.*;

public class FloppyButton extends Button {
    public FloppyButton(Point TLpoint, int width, int height) {
        super(TLpoint, width, height, "", true, true, false);
    }

    @Override
    protected void paint (Graphics2D g2d) {
        g2d.setColor(ValueContainer.getWritingColor());

        g2d.drawRect(getTLpoint().x, getTLpoint().y, getWidth(), getHeight());
        g2d.drawLine(getTLpoint().x+getWidth()/4, getTLpoint().y+getHeight()/4,
                getTLpoint().x+getWidth()*3/4, getTLpoint().y+getHeight()/4);
        g2d.drawRect(getTLpoint().x+getWidth()/4, getTLpoint().y+getHeight()/2,
                getWidth()/2, getHeight()/2);
    }
}
