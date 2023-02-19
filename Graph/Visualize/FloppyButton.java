package Graph.Visualize;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class FloppyButton extends Button {
    private JTextField textField;
    public FloppyButton(Point TLpoint, int width, int height) {
        super(TLpoint, width, height, "save", true, true, false);
    }

    protected void addTextfield (JTextField textField) {
        this.textField = textField;
        textField.setVisible(false);
    }

    @Override
    protected void paint (Graphics2D g2d) {
        g2d.setColor(ValueContainer.getBackgroundColorOfButton(getLabel()));
        g2d.fillRect(getTLpoint().x, getTLpoint().y, getWidth(), getHeight());

        g2d.setColor(ValueContainer.getWritingColor());
        g2d.drawRect(getTLpoint().x, getTLpoint().y, getWidth(), getHeight());
        g2d.drawLine(getTLpoint().x+getWidth()/4, getTLpoint().y+getHeight()/4,
                getTLpoint().x+getWidth()*3/4, getTLpoint().y+getHeight()/4);
        g2d.drawRect(getTLpoint().x+getWidth()/4, getTLpoint().y+getHeight()/2,
                getWidth()/2, getHeight()/2);

        textField.setBounds(getTRpoint().x+ValueContainer.getPanelOffset()/2, getTLpoint().y,
                ValueContainer.getPanelOffset()*3, ValueContainer.getPanelOffset());
        textField.setVisible(ValueContainer.isSavingMode());
        textField.setEnabled(ValueContainer.isSavingMode());
        textField.setBackground(ValueContainer.getBackgroundColor());
        textField.setForeground(ValueContainer.getWritingColor());
        textField.repaint();

        if (ValueContainer.isSavingMode()) {
            FontMetrics fm = g2d.getFontMetrics(ValueContainer.getFont());
            g2d.setColor(ValueContainer.getBackgroundColorOfButton("confirmSave"));
            g2d.fillOval(getTLpoint().x+ValueContainer.getPanelOffset()*9/2, getTLpoint().y,
                    getWidth(), getHeight());
            g2d.setColor(ValueContainer.getWritingColor());
            g2d.drawOval(getTLpoint().x+ValueContainer.getPanelOffset()*9/2, getTLpoint().y, getWidth(), getHeight());
            g2d.drawString("v",
                    getTLpoint().x+ValueContainer.getPanelOffset()*9/2 + getWidth()/2 - fm.stringWidth("v")/2,
                            getTLpoint().y + getHeight() - fm.getAscent()/2);
        }
    }

    @Override
    public short contains(Point point) {
        if (super.contains(point) == 1) {
            return 1; //turn on/off saving mode
        }
        if (ValueContainer.isSavingMode() &&
            new Ellipse2D.Float(getTLpoint().x+ValueContainer.getPanelOffset()*9/2f, getTLpoint().y,
                    getWidth(), getHeight()).contains(point))
            return 2; //save image
        return 0;
    }
}
