package Graph.Visualize;

import Graph.*;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.LinkedList;

public class Handler {
    static Graph graph;
    static double modifier = 1;
    static Point take_from_here = new Point(640, 640/12*9);
    static Point take_to_here = new Point();
    LinkedList<NodeObj> nodeHandler = new LinkedList<>();
    LinkedList<ArcObj> arcHandler = new LinkedList<>();
    int anInt = 4;

    public Handler(Graph graph) {
        Handler.graph = graph;
        addComponents();
    }


    private void addComponents() {
        int y = 50;

        //adds nodes according to topological order, from top to bottom
        for (String node : graph.sortTopological()) {
            addNode(new NodeObj(ConstantHandler.getWidthCanvas()/ 2, y, node));
            ConstantHandler.setNodeSizeByTextLength(node.length());
            y += 50;
        }
        HashMap<String, Integer> map = new HashMap<>();

        //adds arcs between nodes
        for (NodeObj fromNode : nodeHandler) {
            int nOfFK = graph.getForeignKeyNumberInTable(fromNode.getName());
            int indexExiting = 1;
            for (ForeignKeyColumn foreignKey : graph.getForeignKeysInTable(fromNode.getName())) {
                NodeObj toNode;
                if ((toNode = getNode(foreignKey.getReferredTable())) != null) {
                    int indexEntering = 1;
                    if (map.get(toNode.getName()) == null) {
                        map.put(toNode.getName(), 1);
                    }
                    else {
                        indexEntering = map.get(toNode.getName()) + 1;
                        map.put(toNode.getName(), indexEntering);
                    }
                    addArc(new ArcObj(fromNode, toNode, foreignKey.getName(),
                            foreignKey.getReferredPrimaryKey(), nOfFK, indexExiting,
                            graph.getEnteringArcNumberInTable(toNode.getName()), indexEntering));
                }
                ++indexExiting;
            }
        }
    }

    protected void render (Graphics2D g, boolean areWeSaving) {
        //moves nodes coordinates if there was a zoom in/out, resets modifier
        if (Handler.modifier != 1) {
            for (NodeObj objectNode : nodeHandler) {
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
        for (ArcObj objectArc : arcHandler) {
            objectArc.render(g);
        }
        for (NodeObj objectNode : nodeHandler) {
            objectNode.render(g);
        }

        //draws saving frame if in saving mode
        if (!areWeSaving && ConstantHandler.isSavingMode())
            drawImageConfines(g);
    }

    protected void drawImageConfines(Graphics2D g2d) {
        g2d.setColor(ConstantHandler.getWritingColor());

        //draws a white cross for each savepoint
        g2d.drawLine(take_from_here.x- anInt-ConstantHandler.getCameraX(),
                take_from_here.y-ConstantHandler.getCameraY(),
                take_from_here.x+ anInt-ConstantHandler.getCameraX(),
                take_from_here.y-ConstantHandler.getCameraY());
        g2d.drawLine(take_from_here.x-ConstantHandler.getCameraX(),
                take_from_here.y- anInt-ConstantHandler.getCameraY(),
                take_from_here.x-ConstantHandler.getCameraX(),
                take_from_here.y+ anInt-ConstantHandler.getCameraY());

        g2d.drawLine(take_to_here.x- anInt-ConstantHandler.getCameraX(),
                take_to_here.y-ConstantHandler.getCameraY(),
                take_to_here.x+ anInt-ConstantHandler.getCameraX(),
                take_to_here.y-ConstantHandler.getCameraY());
        g2d.drawLine(take_to_here.x-ConstantHandler.getCameraX(),
                take_to_here.y- anInt-ConstantHandler.getCameraY(),
                take_to_here.x-ConstantHandler.getCameraX(),
                take_to_here.y+ anInt-ConstantHandler.getCameraY());

        //connects the savepoints with a dashed line
        float[] f = {2f, 0f, 2f};
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND, 1.0f, f, 2f));
        g2d.drawRect(Math.min(take_from_here.x, take_to_here.x)-ConstantHandler.getCameraX(),
                Math.min(take_from_here.y, take_to_here.y)-ConstantHandler.getCameraY(),
                getImageWidth(), getImageHeight());

        //draws the circles around the savepoint
        g2d.setColor(ConstantHandler.getPrimaryColor());
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(new Ellipse2D.Double(take_from_here.x- anInt-ConstantHandler.getCameraX(),
                take_from_here.y- anInt-ConstantHandler.getCameraY(), anInt*2, anInt*2));
        g2d.draw(new Ellipse2D.Double(take_to_here.x- anInt-ConstantHandler.getCameraX(),
                take_to_here.y- anInt-ConstantHandler.getCameraY(), anInt*2, anInt*2));

    }

    protected boolean onSavingStart(Point point) {
        return new Ellipse2D.Double(take_from_here.x- anInt-ConstantHandler.getCameraX(), take_from_here.y- anInt-ConstantHandler.getCameraY(),
                anInt*2, anInt*2)
                .contains(point.x, point.y);
    }
    protected boolean onSavingEnd(Point point) {
        return new Ellipse2D.Double(take_to_here.x- anInt-ConstantHandler.getCameraX(), take_to_here.y- anInt-ConstantHandler.getCameraY(), anInt*2, anInt*2)
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

    protected void addNode(NodeObj object) {
        nodeHandler.add(object);
    }

    protected NodeObj getNode(String name) {
        for (NodeObj obj : nodeHandler) {
            if (obj.getName().equalsIgnoreCase(name))
                return obj;
        }
        return null;
    }

    protected void addArc(ArcObj object) {
        arcHandler.add(object);
    }


    public NodeObj getNodeInThisCoords(int mX, int mY) {
        for (NodeObj node : nodeHandler) {
            if (mX > node.getX()-ConstantHandler.getWidthNode()/ 2 -ConstantHandler.getCameraX() &&
                    mX < node.getX()+ConstantHandler.getWidthNode()/ 2 -ConstantHandler.getCameraX())
                if (mY > node.getY()-ConstantHandler.getHeightNode()/ 2 -ConstantHandler.getCameraY() &&
                        mY < node.getY()+ConstantHandler.getHeightNode()/ 2 -ConstantHandler.getCameraY())
                    return node;
        }
        return null;
    }

    public String[] getInfo(String name) {
        return graph.getTableInfo(name);
    }
}
