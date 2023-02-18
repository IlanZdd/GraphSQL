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
    protected static Point take_from_here = new Point(640, 640/12*9);
    protected static Point take_to_here = new Point();
    protected LinkedList<NodeObject> nodeHandler = new LinkedList<>();
    protected LinkedList<ArcObject> arcHandler = new LinkedList<>();
    protected int anInt = 4;

    protected CanvasHandler(Graph graph) {
        CanvasHandler.graph = graph;
        addComponents();
    }

    private void addComponents() {
        int y = 50;

        //adds nodes according to topological order, from top to bottom
        for (String node : graph.sortTopological()) {
            addNode(new NodeObject(ValueContainer.getCanvasWidth()/ 2, y, node));
            ValueContainer.setNodeSizeByTextLength(node.length());
            y += 50;
        }

        //adds arcs between nodes
        HashMap<String, Integer> map = new HashMap<>();
        for (NodeObject fromNode : nodeHandler) {
            int totalExiting = graph.getForeignKeyNumberInTable(fromNode.getName());
            int indexExiting = 1;

            for (ForeignKeyColumn foreignKey : graph.getForeignKeysInTable(fromNode.getName())) {
                NodeObject toNode;

                if ((toNode = getNode(foreignKey.getReferredTable())) != null) {
                    int indexEntering = 1;
                    if (map.get(toNode.getName()) == null) {
                        map.put(toNode.getName(), 1);
                    } else {
                        indexEntering = map.get(toNode.getName()) + 1;
                        map.put(toNode.getName(), indexEntering);
                    }
                    addArc(new ArcObject(fromNode, toNode,
                            foreignKey.getName(), foreignKey.getReferredPrimaryKey(),
                            totalExiting, indexExiting,
                            graph.getEnteringArcNumberInTable(toNode.getName()), indexEntering));
                }
                ++indexExiting;
            }
        }
    }

    protected void render (Graphics2D g, boolean areWeSaving) {
        //moves nodes coordinates if there was a zoom in/out, resets modifier
        if (CanvasHandler.modifier != 1) {
            for (NodeObject objectNode : nodeHandler) {
                objectNode.setX((int) (objectNode.getX()*modifier));
                objectNode.setY((int) (objectNode.getY()*modifier));
            }
            take_from_here.x = (int) (take_from_here.x*modifier);
            take_from_here.y = (int) (take_from_here.y*modifier);
            take_to_here.x = (int) (take_to_here.x*modifier);
            take_to_here.y = (int) (take_to_here.y*modifier);
            modifier = 1;
        }

        //renders nodes and arcs
        for (ArcObject objectArc : arcHandler) {
            objectArc.render(g);
        }
        for (NodeObject objectNode : nodeHandler) {
            objectNode.render(g);
        }

        //draws saving frame if in saving mode
        if (!areWeSaving && ValueContainer.isSavingMode())
            drawImageConfines(g);
    }

    protected void drawImageConfines(Graphics2D g2d) {
        g2d.setColor(ValueContainer.getWritingColor());

        //draws a white cross for each savepoint
        g2d.drawLine(take_from_here.x- anInt- ValueContainer.getCameraX(),
                take_from_here.y- ValueContainer.getCameraY(),
                take_from_here.x+ anInt- ValueContainer.getCameraX(),
                take_from_here.y- ValueContainer.getCameraY());
        g2d.drawLine(take_from_here.x- ValueContainer.getCameraX(),
                take_from_here.y- anInt- ValueContainer.getCameraY(),
                take_from_here.x- ValueContainer.getCameraX(),
                take_from_here.y+ anInt- ValueContainer.getCameraY());

        g2d.drawLine(take_to_here.x- anInt- ValueContainer.getCameraX(),
                take_to_here.y- ValueContainer.getCameraY(),
                take_to_here.x+ anInt- ValueContainer.getCameraX(),
                take_to_here.y- ValueContainer.getCameraY());
        g2d.drawLine(take_to_here.x- ValueContainer.getCameraX(),
                take_to_here.y- anInt- ValueContainer.getCameraY(),
                take_to_here.x- ValueContainer.getCameraX(),
                take_to_here.y+ anInt- ValueContainer.getCameraY());

        //connects the save points with a dashed line
        float[] f = {2f, 0f, 2f};
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND, 1.0f, f, 2f));
        g2d.drawRect(Math.min(take_from_here.x, take_to_here.x)- ValueContainer.getCameraX(),
                Math.min(take_from_here.y, take_to_here.y)- ValueContainer.getCameraY(),
                getImageWidth(), getImageHeight());

        //draws the circles around the savepoint
        g2d.setColor(ValueContainer.getPrimaryColor());
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(new Ellipse2D.Double(take_from_here.x- anInt- ValueContainer.getCameraX(),
                take_from_here.y- anInt- ValueContainer.getCameraY(), anInt*2, anInt*2));
        g2d.draw(new Ellipse2D.Double(take_to_here.x- anInt- ValueContainer.getCameraX(),
                take_to_here.y- anInt- ValueContainer.getCameraY(), anInt*2, anInt*2));
    }

    protected boolean onSavingStart(Point point) {
        return new Ellipse2D.Double(take_from_here.x- anInt- ValueContainer.getCameraX(),
                take_from_here.y- anInt- ValueContainer.getCameraY(),
                anInt*2, anInt*2).contains(point.x, point.y);
    }
    protected boolean onSavingEnd(Point point) {
        return new Ellipse2D.Double(take_to_here.x- anInt- ValueContainer.getCameraX(),
                take_to_here.y- anInt- ValueContainer.getCameraY(), anInt*2, anInt*2)
                                        .contains(point.x, point.y);
    }

    protected static int getImageWidth() {
        return Math.max(take_to_here.x-take_from_here.x, take_from_here.x-take_to_here.x);
    }
    protected static int getImageHeight() {
        return Math.max(take_to_here.y-take_from_here.y, take_from_here.y-take_to_here.y);
    }

    protected static Point startOfRect() {
        Point toReturn = new Point();
        toReturn.x = Math.min(take_from_here.x, take_to_here.x);
        toReturn.y = Math.min(take_from_here.y, take_to_here.y);
        return toReturn;
    }

    protected void addNode(NodeObject object) {
        nodeHandler.add(object);
    }

    protected NodeObject getNode(String name) {
        for (NodeObject obj : nodeHandler) {
            if (obj.getName().equalsIgnoreCase(name))
                return obj;
        }
        return null;
    }

    protected void addArc(ArcObject object) {
        arcHandler.add(object);
    }

    protected NodeObject getNodeInThisPoint(int mX, int mY) {
        for (NodeObject node : nodeHandler) {
            if (mX > node.getOuterX(false) - ValueContainer.getCameraX() &&
                    mX < node.getOuterX(true)  - ValueContainer.getCameraX())
                if (mY > node.getOuterY(false)  - ValueContainer.getCameraY() &&
                        mY < node.getOuterY(true) - ValueContainer.getCameraY())
                    return node;
        }
        return null;
    }

    protected String[] getInfo(String name) {
        return graph.getTableInfo(name);
    }
}
