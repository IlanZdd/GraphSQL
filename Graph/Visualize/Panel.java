package Graph.Visualize;

import Graph.*;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.Border;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Panel extends JPanel {

    enum showing {TABLE_INFO, HELP}
    protected showing screen = showing.TABLE_INFO;
    Rectangle sourceBtn;
    Rectangle midBtn;
    Rectangle wellBtn;
    Rectangle externalBtn;
    Rectangle changeColor;
    Rectangle zoomOut;
    Rectangle zoomIn;
    Rectangle help;
    Rectangle savingModeOn;
    Rectangle doSave;
    JTextField textField;
    String textFieldText = "";
    JTextArea info;
    JScrollPane scrollPane;
    String[] printedInfo = null;
    static int btnWidth = (ConstantHandler.getPanelWidth()-2*ConstantHandler.getOffset()-6)/2;
    static int btnHeight = 27;

    protected Panel() {
        super();
        setLayout(new FlowLayout());

        sourceBtn = new Rectangle(0,0,
                btnWidth, btnHeight);
        midBtn = new Rectangle(0,0,
                btnWidth, btnHeight);
        wellBtn = new Rectangle(0,0,
                btnWidth, btnHeight);
        externalBtn = new Rectangle(0,0,
                btnWidth, btnHeight);

        changeColor = new Rectangle(ConstantHandler.getOffset(),
                ConstantHandler.getHeightCanvas()-2*ConstantHandler.getOffset(),
                ConstantHandler.getOffset(), ConstantHandler.getOffset());
        zoomOut = new Rectangle(3*ConstantHandler.getOffset(),
                ConstantHandler.getHeightCanvas()-2*ConstantHandler.getOffset(),
                ConstantHandler.getOffset(), ConstantHandler.getOffset());
        zoomIn = new Rectangle(5*ConstantHandler.getOffset(),
                ConstantHandler.getHeightCanvas()-2*ConstantHandler.getOffset(),
                ConstantHandler.getOffset(), ConstantHandler.getOffset());
        help = new Rectangle(ConstantHandler.getPanelWidth()-2*ConstantHandler.getOffset(),
                ConstantHandler.getOffset(),
                ConstantHandler.getOffset(), ConstantHandler.getOffset());
        savingModeOn = new Rectangle(ConstantHandler.getOffset(),
                ConstantHandler.getHeightCanvas()-4*ConstantHandler.getOffset(),
                ConstantHandler.getOffset(), ConstantHandler.getOffset());
        doSave = new Rectangle(ConstantHandler.getPanelWidth()-2*ConstantHandler.getOffset(),
                ConstantHandler.getHeightCanvas()-4*ConstantHandler.getOffset(),
                ConstantHandler.getOffset(), ConstantHandler.getOffset());


        savingModeOn.setLocation(ConstantHandler.getOffset(),
                ConstantHandler.getHeightCanvas()-4*ConstantHandler.getOffset());

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(ConstantHandler.getPanelColor());
        setMaximumSize(new Dimension(ConstantHandler.getPanelWidth(),
                ConstantHandler.getHeightCanvas()));
        setMinimumSize(new Dimension(ConstantHandler.getPanelWidth(),
                ConstantHandler.getHeightCanvas()));
        setPreferredSize(new Dimension(ConstantHandler.getPanelWidth(),
                ConstantHandler.getHeightCanvas()));

        if (this.getComponentCount() == 0 || scrollPane == null ||
                !this.getComponent(0).equals(scrollPane)) {
            info = new JTextArea(){};
            scrollPane = new JScrollPane(info);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setComponentZOrder(scrollPane.getVerticalScrollBar(), 0);
            scrollPane.setComponentZOrder(scrollPane.getViewport(), 1);
            scrollPane.getVerticalScrollBar().setOpaque(false);
            scrollPane.setLayout(new ScrollPaneNewLayout());
            scrollPane.getVerticalScrollBar().setUI(new BarUI());
            add(scrollPane, 0);
        }
        if (this.getComponentCount() <= 1 || textField == null ||
                !this.getComponent(1).equals(textField)) {
            textField = new JTextField();
            textField.setOpaque(true);
            textField.setBorder(new TextFieldBorder());
            textField.setFont(ConstantHandler.getPanelFont());
            textField.setBackground(ConstantHandler.getPanelColor());
            textField.setForeground(ConstantHandler.getWritingColor());
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    textFieldText = textField.getText();
                }
            });
            textField.setText(textFieldText);
            add(textField);
        }
/*
        if (this.getComponentCount() <= 2 || sourceBtn == null ||
                !this.getComponent(2).equals(sourceBtn)) {
            sourceBtn = new RestyledButton("Source");
            add(sourceBtn);
        }*/
        render(g);
    }

    protected void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(ConstantHandler.getPanelFont());
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(ConstantHandler.getWritingColor());

        changeColor.setLocation(ConstantHandler.getOffset(),
                ConstantHandler.getHeightCanvas()-2*ConstantHandler.getOffset());
        zoomOut.setLocation(3*ConstantHandler.getOffset(),
                ConstantHandler.getHeightCanvas()-2*ConstantHandler.getOffset());
        zoomIn.setLocation(5*ConstantHandler.getOffset(),
                ConstantHandler.getHeightCanvas()-2*ConstantHandler.getOffset());
        help.setLocation(ConstantHandler.getPanelWidth()-2*ConstantHandler.getOffset(),
                ConstantHandler.getOffset());
        //help
        g2d.draw(help);

        //change color
        g2d.draw(changeColor);
        g2d.fillPolygon(new int[]{changeColor.x, changeColor.x+changeColor.width, changeColor.x+changeColor.width},
                new int[]{changeColor.y, changeColor.y, changeColor.y+changeColor.height}, 3);

        //zoom out
        g2d.draw(zoomOut);
        g2d.drawString("-", zoomOut.x+zoomOut.width/2-fm.stringWidth("-")/2,
                zoomOut.y+zoomOut.height-fm.getAscent()/2);

        //change zoom in
        g2d.draw(zoomIn);
        g2d.drawString("+", zoomIn.x+zoomIn.width/2-fm.stringWidth("+")/2,
                zoomIn.y+zoomIn.height-fm.getAscent()/2);

        scrollPane.setVisible(false);
        if (screen == showing.TABLE_INFO) {
            //help
            g2d.drawString("?", help.x+help.width/2-fm.stringWidth("?")/2,
                    help.y+help.height-fm.getAscent()/2);

            g2d.drawString(Handler.graph.getName().toUpperCase() + " :: " + Handler.graph.getTableNumber() + " tables", ConstantHandler.getOffset(), ConstantHandler.getOffset()+fm.getHeight());

            int atThisPointY = fm.getHeight()+2*ConstantHandler.getOffset();
            Rectangle container = new Rectangle(ConstantHandler.getOffset(), atThisPointY,
                    ConstantHandler.getPanelWidth()-2*ConstantHandler.getOffset(), 60);
            g2d.draw(container);

            btnWidth = (ConstantHandler.getPanelWidth()-2*ConstantHandler.getOffset()-6)/2;

            sourceBtn.setSize(btnWidth, btnHeight);
            midBtn.width = btnWidth;
            wellBtn.width = btnWidth;
            externalBtn.width = btnWidth;

            sourceBtn.setLocation(ConstantHandler.getOffset()+2, atThisPointY+2);
            midBtn.setLocation(ConstantHandler.getOffset()+4+btnWidth, atThisPointY+2);
            wellBtn.setLocation(ConstantHandler.getOffset()+2, atThisPointY+4+btnHeight);
            externalBtn.setLocation(ConstantHandler.getOffset()+4+btnWidth, atThisPointY+4+btnHeight);
            g2d.setColor(ConstantHandler.getPanelBtnColor());

            switch (ConstantHandler.getSelectedType()) {
                case source -> g2d.fill(sourceBtn);
                case mid_node -> g2d.fill(midBtn);
                case well -> g2d.fill(wellBtn);
                case external_node -> g2d.fill(externalBtn);
            }
            g2d.setColor(ConstantHandler.getWritingColor());

            g2d.draw(sourceBtn);
            g2d.draw(midBtn);
            g2d.draw(wellBtn);
            g2d.draw(externalBtn);

            g2d.drawString("Source", (int) (sourceBtn.getX() +btnWidth/2-fm.stringWidth("Source")/2),
                    (int) (sourceBtn.getY() +fm.getHeight()+fm.getAscent()/2));
            g2d.drawString("Mid", midBtn.x+btnWidth/2-fm.stringWidth("Mid")/2,
                    midBtn.y+fm.getHeight()+fm.getAscent()/2);
            g2d.drawString("Well", wellBtn.x+btnWidth/2-fm.stringWidth("Well")/2,
                    wellBtn.y+fm.getHeight()+fm.getAscent()/2);
            g2d.drawString("External", externalBtn.x+btnWidth/2-fm.stringWidth("External")/2,
                    externalBtn.y+fm.getHeight()+fm.getAscent()/2);

            atThisPointY += 6+2*btnHeight+ConstantHandler.getOffset();

            if (printedInfo != null && printedInfo.length > 0) {
                renderInfo(atThisPointY, printedInfo);
            }

            savingModeOn.setLocation(ConstantHandler.getOffset(),
                    ConstantHandler.getHeightCanvas()-4*ConstantHandler.getOffset());

            g2d.draw(savingModeOn);
            g2d.draw(new Rectangle(savingModeOn.x+2, savingModeOn.y+2,
                    savingModeOn.width-4, 3));
            g2d.draw(new Rectangle(savingModeOn.x+2, savingModeOn.y+8,
                    savingModeOn.width-4, savingModeOn.height-8));

            textField.setBounds(savingModeOn.x+savingModeOn.width+4,
                    ConstantHandler.getHeightCanvas()-4*ConstantHandler.getOffset(),
                    doSave.x-(savingModeOn.x+savingModeOn.width+8), ConstantHandler.getOffset());
            if (ConstantHandler.isSavingMode()){
                doSave.setLocation(ConstantHandler.getPanelWidth()-2*ConstantHandler.getOffset(),
                        ConstantHandler.getHeightCanvas()-4*ConstantHandler.getOffset());
                g2d.draw(doSave);
                g2d.drawString("V", doSave.x+doSave.width/2-fm.stringWidth("V")/2, doSave.y+doSave.height-fm.getAscent()/2);

                g2d.drawLine(textField.getX(), textField.getY()+textField.getHeight(),
                        textField.getX()+textField.getWidth(), textField.getY()+textField.getHeight());

                textField.setEnabled(true);
                textField.setVisible(true);
            } else {
                textField.setEnabled(false);
                textField.setVisible(false);
            }
        } else {
            g2d.drawString("<", help.x+help.width/2-fm.stringWidth("<")/2,
                    help.y+help.height-fm.getAscent()/2);
            String[] infoStrings;
            infoStrings = new String[]{"Help:", "",
                    "Click on a node to highlight it and its tree","",
                    "Click on a nodeType button to highlight those of selected type","",
                    "Click on the canvas to revert any selection", "",
                    "Click and drag a node to move it", "",
                    "Click and drag the canvas to move the camera","",
                    "In saving mode, move the blue circles to delimit the area to save"};

            renderInfo(help.y+help.height+ConstantHandler.getOffset(), infoStrings);
        }
        g2d.dispose();
    }

    private void renderInfo(int atThisPointY, String[] toPrint) {
        info.setText(null);
        scrollPane.setBounds(new Rectangle(ConstantHandler.getOffset(), atThisPointY,
                ConstantHandler.getPanelWidth() - 2 * ConstantHandler.getOffset(),
                ConstantHandler.getHeightCanvas() - atThisPointY - 5*ConstantHandler.getOffset()));
        scrollPane.setBackground(ConstantHandler.getPanelPanelColor());
        info.setBounds(0,0, scrollPane.getWidth()-scrollPane.getVerticalScrollBar().getWidth(), scrollPane.getHeight());
        info.setBorder(new MatteBorder(2,2,2,2+scrollPane.getVerticalScrollBar().getWidth(), ConstantHandler.getPanelPanelColor()));
        info.setBackground(ConstantHandler.getPanelPanelColor());
        info.setForeground(ConstantHandler.getWritingColor());
        info.setFont(ConstantHandler.getPanelFont());
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setVisible(true);

        if (screen == showing.TABLE_INFO) {
            info.append("Table: " + printedInfo[0].toUpperCase());
            info.append("\n Type: " + printedInfo[1].toUpperCase());

            info.append("\n # of records: " + printedInfo[2]);

            info.append("\n ");
            int indexAtThisPoint = 3;
            if (Handler.graph.getPrimaryKeyNumberInTable(printedInfo[0]) > 0) {
                info.append("\nPrimary Keys: ");
                for (int i = indexAtThisPoint; i < indexAtThisPoint + Handler.graph.getPrimaryKeyNumberInTable(printedInfo[0]); ++i) {
                    info.append("\n- " + printedInfo[i]);
                }

                indexAtThisPoint += Handler.graph.getPrimaryKeyNumberInTable(printedInfo[0]);
            }

            info.append("\n");
            if (Handler.graph.getForeignKeyNumberInTable(printedInfo[0]) > 0) {
                info.append("\nForeign Keys: ");
                for (int i = indexAtThisPoint; i < indexAtThisPoint + Handler.graph.getForeignKeyNumberInTable(printedInfo[0]); ++i) {
                    info.append("\n- " + printedInfo[i].replace("->", " -> "));
                }
                indexAtThisPoint += Handler.graph.getForeignKeyNumberInTable(printedInfo[0]);
            }

            int nOfPlainColumns = Handler.graph.getColumnNumberInTable(printedInfo[0]) -
                    Handler.graph.getForeignKeyNumberInTable(printedInfo[0]) -
                    Handler.graph.getPrimaryKeyNumberInTable(printedInfo[0]);

            info.append("\n");
            if (nOfPlainColumns > 0) {
                info.append("\nPlain columns: ");
                for (int i = indexAtThisPoint; i < indexAtThisPoint + nOfPlainColumns; ++i) {
                    info.append("\n- " + printedInfo[i]);
                }
            }

            info.append("\n");
            if (Handler.graph.hasProblematicArcs(printedInfo[0])) {
                for (ForeignKeyColumn fk : Handler.graph.listProblematicArcs().get(printedInfo[0])) {
                    info.append("\n !Has cycle with " + fk.getReferredTable());
                }
            }
        } else {
            for ( String s : toPrint)
                info.append(s + "\n" );
        }
        info.setEditable(false);
        info.setBackground(ConstantHandler.getPanelPanelColor());
        scrollPane.setBackground(ConstantHandler.getPanelPanelColor());
        scrollPane.setVisible(true);
        info.setCaretPosition(0);
    }

    protected void updateINfo (String[] tableInfo) {
        printedInfo = tableInfo;
    }

    protected Graph.nodeType pressingOnSomething(Point point) {
        if (help.contains(point)) {
            if (screen == showing.HELP)
                screen = showing.TABLE_INFO;
            else screen = showing.HELP;
            return Graph.nodeType.unknown;
        }

        if (changeColor.contains(point)) {
            ConstantHandler.changeTheme();
            return Graph.nodeType.unknown;
        }

        if (zoomIn.contains(point)) {
            ConstantHandler.zoomIn();
            return Graph.nodeType.unknown;
        }
        if (zoomOut.contains(point)) {
            ConstantHandler.zoomOut();
            return Graph.nodeType.unknown;
        }


        if (screen == showing.TABLE_INFO) {
            if (savingModeOn.contains(point)) {
                ConstantHandler.setSavingMode();
                return Graph.nodeType.unknown;
            }
            if (sourceBtn.contains(point) && ConstantHandler.getSelectedType() != Graph.nodeType.source)
                return Graph.nodeType.source;
            if (midBtn.contains(point) && ConstantHandler.getSelectedType() != Graph.nodeType.mid_node)
                return Graph.nodeType.mid_node;
            if (externalBtn.contains(point) && ConstantHandler.getSelectedType() != Graph.nodeType.external_node)
                return Graph.nodeType.external_node;
            if (wellBtn.contains(point) && ConstantHandler.getSelectedType() != Graph.nodeType.well)
                return Graph.nodeType.well;
        }
        return  Graph.nodeType.unknown;
    }

    protected boolean amISaving(Point point) {
        return ConstantHandler.isSavingMode() && doSave.contains(point)
                && screen == showing.TABLE_INFO;
    }

    public static class ScrollPaneNewLayout extends ScrollPaneLayout{
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

    public static class BarUI extends BasicScrollBarUI {
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
                color = ConstantHandler.getPanelColor();
            }else {
                color = ConstantHandler.getPanelPanelColor();
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
                color = ConstantHandler.getScrollBarColor();
            } else if (isThumbRollover()) {
                color = ConstantHandler.getPanelBtnColor();
            } else {
                color = ConstantHandler.getPanelColor();
            }
            g2.setPaint(color);
            g2.fillRoundRect(r.x,r.y,r.width,r.height,10,10);
            g2.setPaint(ConstantHandler.getBackgroundColor());
            g2.drawRoundRect(r.x,r.y,r.width,r.height,10,10);
            g2.dispose();
        }
        @Override
        protected void setThumbBounds(int x, int y, int width, int height) {
            super.setThumbBounds(x, y, width, height);
            scrollbar.repaint();
        }
    }

    public static class TextFieldBorder implements Border {

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(ConstantHandler.getWritingColor());
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

    public static class RestyledButton extends JButton {
        private final String text;
        public RestyledButton(String text) {
            this.text = text;
            setBorderPainted(false);
            setContentAreaFilled(false);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            setSize(btnWidth, btnHeight);
            setFont(ConstantHandler.getPanelFont());
            setForeground(ConstantHandler.getWritingColor());
            setText(text);
            setBorder(new MatteBorder(1,1,1,1,ConstantHandler.getWritingColor()));
            //setOpaque(true);

            Color color;

            if (getModel().isRollover()) {
                color = ConstantHandler.getPanelBtnColor();
            } else if (getModel().isSelected()) {
                color = ConstantHandler.getScrollBarColor();
            } else {
                color = ConstantHandler.getPanelColor();
            }
            setBackground(color);
            //g2d.setColor(color);
            //g2d.fill(this.getBounds());

            //g2d.setColor(ConstantHandler.getWritingColor());
            //g2d.draw(this.getBounds());
        }
    }
}
