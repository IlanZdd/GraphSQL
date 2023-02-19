package Graph.Visualize;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Canvas extends JPanel {
    protected final CanvasHandler canvasHandler;
    protected final ButtonHandler buttonHandler;
    protected JTextField textField;

    protected Canvas(CanvasHandler canvasHandler, ButtonHandler buttonHandler) {
        super();
        this.canvasHandler = canvasHandler;
        this.buttonHandler = buttonHandler;
        this.setLayout(null);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ValueContainer.setCanvasSize(getWidth(), getHeight());
        setBackground(ValueContainer.getBackgroundColor());

        if (this.getComponentCount() == 0) {
            textField = new JTextField();
            add(textField);
            buttonHandler.addTextfield(textField);
            textField.setOpaque(true);
            textField.setBorder(new TextFieldBorder());
            textField.setFont(ValueContainer.getPanelFont());
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    ValueContainer.setSavingName(textField.getText()+e.getKeyChar());
                }
            });
            textField.setText(ValueContainer.getSavingName());
        }

        render(g, false);
    }

    protected void render(Graphics g, boolean areWeSaving) {
        Graphics2D g2d = (Graphics2D) g;
        canvasHandler.render(g2d, areWeSaving);
        if (!areWeSaving)
            buttonHandler.render(g2d);
        g2d.dispose();
    }


    protected static class TextFieldBorder implements Border {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(ValueContainer.getWritingColor());
            g.drawLine(x, y+height, x+width, y+height);
            g.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0,0,0,0);
        }
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
