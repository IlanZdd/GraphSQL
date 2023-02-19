package Graph.Visualize;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class TableInfo{
    private NodeObject node;
    private Ellipse2D circleOnNode = null;
    private Ellipse2D circleOnPanel = null;
    private Point TLpoint;

    private JScrollPane scrollPane;
    private JTextArea info;


    public TableInfo (NodeObject node) {
        this.node = node;
        circleOnPanel = new Ellipse2D.Float(TLpoint.x, TLpoint.y+ValueContainer.getPanelOffset()*2, 4, 4);
        TLpoint = new Point(ValueContainer.getCanvasWidth()*4/5 - ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset());

        info = new JTextArea(){};
        scrollPane = new JScrollPane(info);
        // Sets the scrollBar with the customized scrollBar
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setComponentZOrder(scrollPane.getVerticalScrollBar(), 0);
        scrollPane.setComponentZOrder(scrollPane.getViewport(), 1);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.setLayout(new Panel.ScrollPaneNewLayout());
        scrollPane.getVerticalScrollBar().setUI(new Panel.BarUI());
    }

    protected void render (Graphics2D g2d) {
        g2d.setColor(ValueContainer.getPrimaryColor());

    }

    protected void setNode (NodeObject node) {
        this.node = node;
        movedNode();
    }

    protected void movedNode () {
        //node is over or under the panel, circle is on the left lower node
        if (node.getOuterX(true) > TLpoint.x - ValueContainer.getPanelOffset())
            circleOnNode = new Ellipse2D.Float(node.getX()+ node.getOuterX(false)/2f,
                    node.getY()+node.getOuterY(true)/2f, 4, 4);
        else //else circle is on the right lower node
            circleOnNode = new Ellipse2D.Float(node.getX()+ node.getOuterX(true)/2f,
                    node.getY()+node.getOuterY(true)/2f, 4, 4);
    }



    protected static class ScrollPaneNewLayout extends ScrollPaneLayout {
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
