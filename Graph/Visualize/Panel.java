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
    protected enum showing {TABLE_INFO, HELP}
    protected showing screen = showing.TABLE_INFO;
    private final Rectangle sourceBtn, midBtn, wellBtn, externalBtn, changeColor, zoomOut, zoomIn, help, savingModeOn, doSave;
    protected JTextField textField;
    protected String textFieldText = "";
    private JTextArea info;
    private JScrollPane scrollPane;
    protected String[] printedInfo = null;
    private static int btnWidth = (ValueContainer.getPanelWidth()-2* ValueContainer.getPanelOffset()-6)/2;
    private static final int btnHeight = 27;

    protected Panel() {
        super();
        setLayout(new FlowLayout());

        sourceBtn = new Rectangle(0,0, btnWidth, btnHeight);
        midBtn = new Rectangle(0,0, btnWidth, btnHeight);
        wellBtn = new Rectangle(0,0, btnWidth, btnHeight);
        externalBtn = new Rectangle(0,0, btnWidth, btnHeight);

        help = new Rectangle(ValueContainer.getPanelWidth()-2* ValueContainer.getPanelOffset(),
                ValueContainer.getPanelOffset(),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset());
        changeColor = new Rectangle(ValueContainer.getPanelOffset(),
                ValueContainer.getCanvasHeight()-2* ValueContainer.getPanelOffset(),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset());
        zoomOut = new Rectangle(3* ValueContainer.getPanelOffset(),
                ValueContainer.getCanvasHeight()-2* ValueContainer.getPanelOffset(),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset());
        zoomIn = new Rectangle(5* ValueContainer.getPanelOffset(),
                ValueContainer.getCanvasHeight()-2* ValueContainer.getPanelOffset(),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset());

        savingModeOn = new Rectangle(ValueContainer.getPanelOffset(),
                ValueContainer.getCanvasHeight()-4* ValueContainer.getPanelOffset(),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset());
        savingModeOn.setLocation(ValueContainer.getPanelOffset(),
                ValueContainer.getCanvasHeight()-4* ValueContainer.getPanelOffset());
        doSave = new Rectangle(ValueContainer.getPanelWidth()-2* ValueContainer.getPanelOffset(),
                ValueContainer.getCanvasHeight()-4* ValueContainer.getPanelOffset(),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(ValueContainer.getColorOfPanel());
        setMaximumSize(new Dimension(ValueContainer.getPanelWidth(),
                ValueContainer.getCanvasHeight()));
        setMinimumSize(new Dimension(ValueContainer.getPanelWidth(),
                ValueContainer.getCanvasHeight()));
        setPreferredSize(new Dimension(ValueContainer.getPanelWidth(),
                ValueContainer.getCanvasHeight()));

        if (this.getComponentCount() == 0 || scrollPane == null ||
                !this.getComponent(0).equals(scrollPane)) {
            info = new JTextArea(){};
            scrollPane = new JScrollPane(info);

            // Sets the scrollBar with the customized scrollBar
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

            //Sets the text field with the customized style
            textField.setOpaque(true);
            textField.setBorder(new TextFieldBorder());
            textField.setFont(ValueContainer.getPanelFont());
            textField.setBackground(ValueContainer.getColorOfPanel());
            textField.setForeground(ValueContainer.getWritingColor());
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    textFieldText = textField.getText();
                }
            });
            textField.setText(textFieldText);
            add(textField);
        }

        render(g);
    }

    protected void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(ValueContainer.getPanelFont());
        FontMetrics fm = g2d.getFontMetrics();

        g2d.setColor(ValueContainer.getWritingColor());

        //Sets the util buttons locations
        changeColor.setLocation(ValueContainer.getPanelOffset(),
                ValueContainer.getCanvasHeight()-2* ValueContainer.getPanelOffset());
        zoomOut.setLocation(3* ValueContainer.getPanelOffset(),
                ValueContainer.getCanvasHeight()-2* ValueContainer.getPanelOffset());
        zoomIn.setLocation(5* ValueContainer.getPanelOffset(),
                ValueContainer.getCanvasHeight()-2* ValueContainer.getPanelOffset());
        help.setLocation(ValueContainer.getPanelWidth()-2* ValueContainer.getPanelOffset(),
                ValueContainer.getPanelOffset());

        // draws the util Buttons

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

        // Don't know if I'm showing this yet
        scrollPane.setVisible(false);

        // If we are on the first panel
        if (screen == showing.TABLE_INFO) {
            //write the help button label as ?
            g2d.drawString("?", help.x+help.width/2-fm.stringWidth("?")/2,
                    help.y+help.height-fm.getAscent()/2);

            //write the graph name and number of table
            String graphName = Handler.graph.getName();
            graphName = graphName.substring(
                    ((graphName.lastIndexOf('/') != -1) ? graphName.lastIndexOf('/')+1 :
                            ((graphName.lastIndexOf('\\') != -1) ? graphName.lastIndexOf('\\')+1 : 0)));
            graphName = graphName.substring(0,
                    (graphName.indexOf('.') != -1) ? graphName.indexOf('.') : graphName.length());
            g2d.drawString(graphName.toUpperCase() + " :: " + Handler.graph.getTableNumber() + " tables",
                    ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset()+fm.getHeight());

            //draw the buttons
            int atThisPointY = fm.getHeight()+2* ValueContainer.getPanelOffset();
            Rectangle container = new Rectangle(ValueContainer.getPanelOffset(), atThisPointY,
                    ValueContainer.getPanelWidth()-2* ValueContainer.getPanelOffset(), 60);
            g2d.draw(container);

            btnWidth = (ValueContainer.getPanelWidth()-2* ValueContainer.getPanelOffset()-6)/2;

            sourceBtn.setSize(btnWidth, btnHeight);
            midBtn.width = btnWidth;
            wellBtn.width = btnWidth;
            externalBtn.width = btnWidth;

            sourceBtn.setLocation(ValueContainer.getPanelOffset()+2, atThisPointY+2);
            midBtn.setLocation(ValueContainer.getPanelOffset()+4+btnWidth, atThisPointY+2);
            wellBtn.setLocation(ValueContainer.getPanelOffset()+2, atThisPointY+4+btnHeight);
            externalBtn.setLocation(ValueContainer.getPanelOffset()+4+btnWidth, atThisPointY+4+btnHeight);
            g2d.setColor(ValueContainer.getColorOfPanelButton());

            switch (ValueContainer.getSelectedType()) {
                case source -> g2d.fill(sourceBtn);
                case mid_node -> g2d.fill(midBtn);
                case well -> g2d.fill(wellBtn);
                case external_node -> g2d.fill(externalBtn);
            }
            g2d.setColor(ValueContainer.getWritingColor());

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

            atThisPointY += 6+2*btnHeight+ ValueContainer.getPanelOffset();

            // writes the info if a node is selected, or we are in help panel
            if (printedInfo != null && printedInfo.length > 0) {
                renderInfo(atThisPointY, printedInfo);
            }

            //draws the floppy disk and the saving part
            savingModeOn.setLocation(ValueContainer.getPanelOffset(),
                    ValueContainer.getCanvasHeight()-4* ValueContainer.getPanelOffset());
            g2d.draw(savingModeOn);
            g2d.draw(new Rectangle(savingModeOn.x+2, savingModeOn.y+2,
                    savingModeOn.width-4, 3));
            g2d.draw(new Rectangle(savingModeOn.x+2, savingModeOn.y+8,
                    savingModeOn.width-4, savingModeOn.height-8));

            textField.setBounds(savingModeOn.x+savingModeOn.width+4,
                    ValueContainer.getCanvasHeight()-4* ValueContainer.getPanelOffset(),
                    doSave.x-(savingModeOn.x+savingModeOn.width+8), ValueContainer.getPanelOffset());
            if (ValueContainer.isSavingMode()){
                doSave.setLocation(ValueContainer.getPanelWidth()-2* ValueContainer.getPanelOffset(),
                        ValueContainer.getCanvasHeight()-4* ValueContainer.getPanelOffset());
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
                    "Click and drag a node to move it", "",
                    "Click and drag the canvas to move the camera","",
                    "Open the saving mode by clicking on the floppy disk", "",
                    "In saving mode, move the blue circles to delimit the area you wish to capture", "",
                    "In saving mode, you can write a filename, before confirming by clicking the \"V\"", "",
                    "Click anywhere else to revert any selection", ""};

            renderInfo(help.y+help.height+ ValueContainer.getPanelOffset(), infoStrings);
        }
        g2d.dispose();
    }

    private void renderInfo(int atThisPointY, String[] toPrint) {
        info.setText(null);
        scrollPane.setBounds(new Rectangle(ValueContainer.getPanelOffset(), atThisPointY,
                ValueContainer.getPanelWidth() - 2 * ValueContainer.getPanelOffset(),
                ValueContainer.getCanvasHeight() - atThisPointY - 5* ValueContainer.getPanelOffset()));
        scrollPane.setBackground(ValueContainer.getColorOfInfoPanel());
        info.setBounds(0,0, scrollPane.getWidth()-scrollPane.getVerticalScrollBar().getWidth(), scrollPane.getHeight());
        info.setBorder(new MatteBorder(2,2,2,2+scrollPane.getVerticalScrollBar().getWidth(), ValueContainer.getColorOfInfoPanel()));
        info.setBackground(ValueContainer.getColorOfInfoPanel());
        info.setForeground(ValueContainer.getWritingColor());
        info.setFont(ValueContainer.getPanelFont());
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setVisible(true);

        // Writing the table info
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
            for (String s : toPrint)
                info.append(s + "\n" );
        }
        info.setEditable(false);
        info.setBackground(ValueContainer.getColorOfInfoPanel());
        scrollPane.setBackground(ValueContainer.getColorOfInfoPanel());
        //scroll back to top
        info.setCaretPosition(0);
        scrollPane.setVisible(true);
    }

    protected void updateINfo (String[] tableInfo) {
        printedInfo = tableInfo;
    }

    protected Graph.nodeType pressingOnSomething(Point point) {
        //clicking on help/back
        if (help.contains(point)) {
            if (screen == showing.HELP)
                screen = showing.TABLE_INFO;
            else screen = showing.HELP;
            return Graph.nodeType.unknown;
        }

        //changing color
        if (changeColor.contains(point)) {
            ValueContainer.changeTheme();
            return Graph.nodeType.unknown;
        }

        if (zoomIn.contains(point)) {
            ValueContainer.zoomIn();
            return Graph.nodeType.unknown;
        }
        if (zoomOut.contains(point)) {
            ValueContainer.zoomOut();
            return Graph.nodeType.unknown;
        }

        if (screen == showing.TABLE_INFO) {
            if (savingModeOn.contains(point)) {
                ValueContainer.setSavingMode();
                return Graph.nodeType.unknown;
            }
            if (sourceBtn.contains(point) && ValueContainer.getSelectedType() != Graph.nodeType.source)
                return Graph.nodeType.source;
            if (midBtn.contains(point) && ValueContainer.getSelectedType() != Graph.nodeType.mid_node)
                return Graph.nodeType.mid_node;
            if (externalBtn.contains(point) && ValueContainer.getSelectedType() != Graph.nodeType.external_node)
                return Graph.nodeType.external_node;
            if (wellBtn.contains(point) && ValueContainer.getSelectedType() != Graph.nodeType.well)
                return Graph.nodeType.well;
        }
        return  Graph.nodeType.unknown;
    }

    protected boolean amISaving(Point point) {
        return ValueContainer.isSavingMode() && doSave.contains(point)
                && screen == showing.TABLE_INFO;
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
