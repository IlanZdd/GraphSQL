package Graph.Visualize;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Canvas extends JPanel {
    protected final CanvasHandler canvasHandler;
    protected final ButtonHandler buttonHandler;
    protected JTextField textField;
    protected JTextArea textArea;
    protected JScrollPane scrollPane;
    protected TableInfo infoPanel;

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

        if (infoPanel == null && ValueContainer.isSelectedNode()) {
            textArea = new JTextArea(){};
            scrollPane = new JScrollPane(textArea);
            infoPanel = new TableInfo(textArea, scrollPane);

            canvasHandler.addInfoPanel(infoPanel);

            // Sets the scrollBar with the customized scrollBar
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setComponentZOrder(scrollPane.getVerticalScrollBar(), 0);
            scrollPane.setComponentZOrder(scrollPane.getViewport(), 1);
            scrollPane.getVerticalScrollBar().setOpaque(false);
            scrollPane.setLayout(new ScrollPaneNewLayout());
            scrollPane.getVerticalScrollBar().setUI(new BarUI());
        }

        if (this.getComponentCount() == 1 && ValueContainer.isSelectedNode())
            this.add(scrollPane);

        render(g, false);
    }

    protected void render(Graphics g, boolean areWeSaving) {
        Graphics2D g2d = (Graphics2D) g;
        canvasHandler.render(g2d, areWeSaving);
        if (!areWeSaving) {
            buttonHandler.render(g2d);
            if (infoPanel != null && canvasHandler.shouldDrawPanel)
                infoPanel.render(g2d);
        }
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

    protected static class ScrollPaneNewLayout extends ScrollPaneLayout{
        @Override
        public void layoutContainer(Container parent) {
            JScrollPane scrollPane = (JScrollPane)parent;

            Rectangle availR = scrollPane.getBounds();
            availR.x = availR.y = 0;

            Insets insets = parent.getInsets();
            availR.x = insets.left;
            availR.y = insets.top;
            availR.width  -= insets.right + insets.left;
            availR.height -= insets.bottom  + insets.top;

            Rectangle vsbR = new Rectangle();
            vsbR.width  = 8;
            vsbR.height = availR.height;
            vsbR.x = availR.x + availR.width - vsbR.width;
            vsbR.y = availR.y;

            if (viewport != null) {
                viewport.setBounds(availR);
            }
            if(vsb != null) {
                vsb.setVisible(true);
                vsb.setBounds(vsbR);
            }
        }
    }

    protected static class BarUI extends BasicScrollBarUI {
        private final Dimension d = new Dimension();
        @Override protected JButton createDecreaseButton(int orientation) {
            return new JButton() {
                @Override public Dimension getPreferredSize() {
                    return d;
                }
            };
        }
        @Override protected JButton createIncreaseButton(int orientation) {
            return new JButton() {
                @Override public Dimension getPreferredSize() {
                    return d;
                }
            };
        }
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D)g.create();
            Color color;
            if(!c.isEnabled() || r.width>r.height) {
                return;
            } else if(isDragging) {
                color = ValueContainer.getColorOfPanel();
            }else {
                color = ValueContainer.getColorOfInfoPanel();
            }
            g2.setPaint(color);
            g2.fillRect(r.x,r.y,r.width,r.height);
            g2.dispose();
        }
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Color color;
            JScrollBar sb = (JScrollBar)c;
            if (!sb.isEnabled() || r.width>r.height) {
                return;
            } else if (isDragging) {
                color = ValueContainer.getColorOfScrollBar();
            } else if (isThumbRollover()) {
                color = ValueContainer.getColorOfPanelButton();
            } else {
                color = ValueContainer.getColorOfPanel();
            }
            g2.setPaint(color);
            g2.fillRoundRect(r.x,r.y,r.width,r.height,10,10);
            g2.setPaint(ValueContainer.getBackgroundColor());
            g2.drawRoundRect(r.x,r.y,r.width,r.height,10,10);
            g2.dispose();
        }
        @Override
        protected void setThumbBounds(int x, int y, int width, int height) {
            super.setThumbBounds(x, y, width, height);
            scrollbar.repaint();
        }
    }

}
