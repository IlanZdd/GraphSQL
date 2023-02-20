package Graph.Visualize;

import Graph.ForeignKeyColumn;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class InfoPanel {
    private CanvasNode node;
    private final Point TLpoint;

    private final JScrollPane scrollPane;
    private final JTextArea textArea;


    public InfoPanel(JTextArea textArea, JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
        this.textArea = textArea;
        this.node = null;

       TLpoint = new Point(ValueContainer.getCanvasWidth()*4/5 - ValueContainer.getOffset(), ValueContainer.getOffset());
    }

    protected void render (Graphics2D g2d) {
        if (ValueContainer.getSelectedNode().equals("")) return;

        g2d.setColor(ValueContainer.getPrimaryColor());

        //sets all the scroll pane/textArea stuff
        textArea.setText(null);
        scrollPane.setBounds(new Rectangle(TLpoint.x, TLpoint.y,
                ValueContainer.getCanvasWidth()/5, ValueContainer.getCanvasHeight()/2));
        scrollPane.setBackground(ValueContainer.getColorOfPanel());
        textArea.setBounds(0,0, scrollPane.getWidth()-scrollPane.getVerticalScrollBar().getWidth(), scrollPane.getHeight());
        textArea.setBorder(new MatteBorder(2,2,2,2+scrollPane.getVerticalScrollBar().getWidth(), ValueContainer.getColorOfPanel()));
        textArea.setBackground(ValueContainer.getColorOfPanel());
        textArea.setForeground(ValueContainer.getWritingColor());
        textArea.setFont(ValueContainer.getPanelFont());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setVisible(true);

        //writes the info in the panel
        renderInfo();

        textArea.setEditable(false);
        textArea.setBackground(ValueContainer.getColorOfPanel());
        scrollPane.setBackground(ValueContainer.getColorOfPanel());
        //scroll back to top
        textArea.setCaretPosition(0);
        scrollPane.setVisible(true);

        //draws a cute border to the scroll pane
        g2d.setColor(ValueContainer.getWritingColor());
        g2d.drawRect(TLpoint.x, TLpoint.y, scrollPane.getWidth(), scrollPane.getHeight());
        g2d.drawRect(TLpoint.x-2, TLpoint.y-2, scrollPane.getWidth()+4, scrollPane.getHeight()+4);
    }

    protected void setNode (CanvasNode node) {
        this.node = node;
    }

    private void renderInfo() {
        // Writing the table info
        String[] printedInfo = ValueContainer.getNodeInfo(node.getName());
        textArea.append("Table: " + printedInfo[0].toUpperCase());
        textArea.append("\n Type: " + printedInfo[1].toUpperCase());

        textArea.append("\n # of records: " + printedInfo[2]);

        textArea.append("\n ");
        int indexAtThisPoint = 3;
        if (CanvasHandler.graph.getPrimaryKeyNumberInTable(printedInfo[0]) > 0) {
            textArea.append("\nPrimary Keys: ");
            for (int i = indexAtThisPoint; i < indexAtThisPoint + CanvasHandler.graph.getPrimaryKeyNumberInTable(printedInfo[0]); ++i) {
                textArea.append("\n- " + printedInfo[i]);
            }

            indexAtThisPoint += CanvasHandler.graph.getPrimaryKeyNumberInTable(printedInfo[0]);
        }

        textArea.append("\n");
        if (CanvasHandler.graph.getForeignKeyNumberInTable(printedInfo[0]) > 0) {
            textArea.append("\nForeign Keys: ");
            for (int i = indexAtThisPoint; i < indexAtThisPoint + CanvasHandler.graph.getForeignKeyNumberInTable(printedInfo[0]); ++i) {
                textArea.append("\n- " + printedInfo[i].replace("->", " -> "));
            }
            indexAtThisPoint += CanvasHandler.graph.getForeignKeyNumberInTable(printedInfo[0]);
        }

        int nOfPlainColumns = CanvasHandler.graph.getColumnNumberInTable(printedInfo[0]) -
                CanvasHandler.graph.getForeignKeyNumberInTable(printedInfo[0]) -
                CanvasHandler.graph.getPrimaryKeyNumberInTable(printedInfo[0]);

        textArea.append("\n");
        if (nOfPlainColumns > 0) {
            textArea.append("\nPlain columns: ");
            for (int i = indexAtThisPoint; i < indexAtThisPoint + nOfPlainColumns; ++i) {
                textArea.append("\n- " + printedInfo[i]);
            }
        }

        textArea.append("\n");
        if (CanvasHandler.graph.hasProblematicArcs(printedInfo[0])) {
            for (ForeignKeyColumn fk : CanvasHandler.graph.listProblematicArcs().get(printedInfo[0])) {
                textArea.append("\n !Has cycle with " + fk.getReferredTable());
            }
        }
    }

    protected void updateX() {
        TLpoint.x = ValueContainer.getCanvasWidth()*4/5 - ValueContainer.getOffset();
    }

}
