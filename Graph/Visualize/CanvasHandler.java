package Graph.Visualize;

import Graph.*;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.LinkedList;

public class CanvasHandler {
    protected static Graph graph;
    protected static double modifier = 1;
    protected Point fromSavePoint = new Point(640, 640/12*9);
    protected Point toSavePoint = new Point();
    protected LinkedList<CanvasNode> nodeHandler = new LinkedList<>();
    protected LinkedList<CanvasArc> arcHandler = new LinkedList<>();
    protected int savepointDimension = 4;
    protected InfoPanel infoPanel;
    protected boolean shouldDrawPanel;

    protected CanvasHandler(Graph graph) {
        CanvasHandler.graph = graph;
        addComponents();
    }

    private void addComponents() {
        int y = 50;

        //adds nodes according to topological order, from top to bottom
        for (String node : graph.sortTopological()) {
            addNode(new CanvasNode(ValueContainer.getCanvasWidth()/ 2, y, node));
            ValueContainer.setNodeSizeByTextLength(node.length());
            y += 50;
        }

        //adds arcs between nodes
        HashMap<String, Integer> map = new HashMap<>();
        for (CanvasNode fromNode : nodeHandler) {
            int totalExiting = graph.getForeignKeyNumberInTable(fromNode.getName());
            int indexExiting = 1;

            for (ForeignKeyColumn foreignKey : graph.getForeignKeysInTable(fromNode.getName())) {
                CanvasNode toNode;

                if ((toNode = getNode(foreignKey.getReferredTable())) != null) {
                    int indexEntering = 1;
                    if (map.get(toNode.getName()) == null) {
                        map.put(toNode.getName(), 1);
                    } else {
                        indexEntering = map.get(toNode.getName()) + 1;
                        map.put(toNode.getName(), indexEntering);
                    }
                    addArc(new CanvasArc(fromNode, toNode,
                            foreignKey.getName(), foreignKey.getReferredPrimaryKey(),
                            totalExiting, indexExiting,
                            graph.getEnteringArcNumberInTable(toNode.getName()), indexEntering));
                }
                ++indexExiting;
            }
        }
    }

    protected void render (Graphics2D g, boolean areWeSaving) {
        //moves nodes coordinates & saving points if there was a zoom in/out, resets modifier
        if (CanvasHandler.modifier != 1) {
            for (CanvasNode objectNode : nodeHandler) {
                objectNode.setX((int) (objectNode.getX()*modifier));
                objectNode.setY((int) (objectNode.getY()*modifier));
            }
            fromSavePoint.x = (int) (fromSavePoint.x*modifier);
            fromSavePoint.y = (int) (fromSavePoint.y*modifier);
            toSavePoint.x = (int) (toSavePoint.x*modifier);
            toSavePoint.y = (int) (toSavePoint.y*modifier);
            modifier = 1;
        }

        //renders nodes and arcs
        for (CanvasArc objectArc : arcHandler) {
            objectArc.render(g);
        }
        for (CanvasNode objectNode : nodeHandler) {
            if (objectNode.getName().equalsIgnoreCase(ValueContainer.getSelectedNode()))
                infoPanel.setNode(objectNode);
            objectNode.render(g);
        }

        //draws saving frame if in saving mode
        if (!areWeSaving && ValueContainer.isSavingMode())
            drawImageConfines(g);
    }

    protected void drawImageConfines(Graphics2D g2d) {
        g2d.setColor(ValueContainer.getWritingColor());

        //draws a white cross for each savepoint
        g2d.drawLine(fromSavePoint.x- savepointDimension - ValueContainer.getCameraX(),
                fromSavePoint.y- ValueContainer.getCameraY(),
                fromSavePoint.x+ savepointDimension - ValueContainer.getCameraX(),
                fromSavePoint.y- ValueContainer.getCameraY());
        g2d.drawLine(fromSavePoint.x- ValueContainer.getCameraX(),
                fromSavePoint.y- savepointDimension - ValueContainer.getCameraY(),
                fromSavePoint.x- ValueContainer.getCameraX(),
                fromSavePoint.y+ savepointDimension - ValueContainer.getCameraY());

        g2d.drawLine(toSavePoint.x- savepointDimension - ValueContainer.getCameraX(),
                toSavePoint.y- ValueContainer.getCameraY(),
                toSavePoint.x+ savepointDimension - ValueContainer.getCameraX(),
                toSavePoint.y- ValueContainer.getCameraY());
        g2d.drawLine(toSavePoint.x- ValueContainer.getCameraX(),
                toSavePoint.y- savepointDimension - ValueContainer.getCameraY(),
                toSavePoint.x- ValueContainer.getCameraX(),
                toSavePoint.y+ savepointDimension - ValueContainer.getCameraY());

        //connects the save points with a dashed line
        float[] f = {2f, 0f, 2f};
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,
                1.0f, f, 2f));
        g2d.drawRect(Math.min(fromSavePoint.x, toSavePoint.x)- ValueContainer.getCameraX(),
                Math.min(fromSavePoint.y, toSavePoint.y)- ValueContainer.getCameraY(),
                getImageWidth(), getImageHeight());

        //draws the circles around the savepoint
        g2d.setColor(ValueContainer.getPrimaryColor());
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(new Ellipse2D.Double(fromSavePoint.x- savepointDimension - ValueContainer.getCameraX(),
                fromSavePoint.y- savepointDimension - ValueContainer.getCameraY(),
                savepointDimension *2, savepointDimension *2));
        g2d.draw(new Ellipse2D.Double(toSavePoint.x- savepointDimension - ValueContainer.getCameraX(),
                toSavePoint.y- savepointDimension - ValueContainer.getCameraY(),
                savepointDimension *2, savepointDimension *2));
    }

    protected boolean onFromSavingPoint(Point point) {
        return new Ellipse2D.Double(fromSavePoint.x- savepointDimension - ValueContainer.getCameraX(),
                fromSavePoint.y- savepointDimension - ValueContainer.getCameraY(),
                savepointDimension *2, savepointDimension *2).contains(point.x, point.y);
    }
    protected boolean onToSavingPoint(Point point) {
        return new Ellipse2D.Double(toSavePoint.x- savepointDimension - ValueContainer.getCameraX(),
                toSavePoint.y- savepointDimension - ValueContainer.getCameraY(), savepointDimension *2, savepointDimension *2)
                                        .contains(point.x, point.y);
    }

    protected int getImageWidth() {
        return Math.max(toSavePoint.x- fromSavePoint.x, fromSavePoint.x- toSavePoint.x);
    }
    protected int getImageHeight() {
        return Math.max(toSavePoint.y- fromSavePoint.y, fromSavePoint.y- toSavePoint.y);
    }

    protected Point getTopLeftOfSavingArea() {
        Point toReturn = new Point();
        toReturn.x = Math.min(fromSavePoint.x, toSavePoint.x);
        toReturn.y = Math.min(fromSavePoint.y, toSavePoint.y);
        return toReturn;
    }

    protected void addNode(CanvasNode object) {
        nodeHandler.add(object);
    }

    protected CanvasNode getNode(String name) {
        for (CanvasNode obj : nodeHandler) {
            if (obj.getName().equalsIgnoreCase(name))
                return obj;
        }
        return null;
    }

    protected void addArc(CanvasArc object) {
        arcHandler.add(object);
    }

    protected CanvasNode getNodeInThisPoint(int mX, int mY) {
        for (CanvasNode node : nodeHandler) {
            if (mX > node.getOuterX(false) - ValueContainer.getCameraX() &&
                    mX < node.getOuterX(true)  - ValueContainer.getCameraX())
                if (mY > node.getOuterY(false)  - ValueContainer.getCameraY() &&
                        mY < node.getOuterY(true) - ValueContainer.getCameraY())
                    return node;
        }
        return null;
    }

    protected void addInfoPanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }
    protected void updatePanelX() {
        if (infoPanel != null) {
            infoPanel.updateX();
        }
    }

}
