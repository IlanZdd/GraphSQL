package Graph.Visualize;
import java.awt.*;
public class CanvasArc {
    private final CanvasNode fromNode;
    private final CanvasNode toNode;
    private final String fromForeign;
    private final String toPrimary;
    private int offsetExiting;
    private int offsetEntering;
    protected CanvasArc(CanvasNode fromNode, CanvasNode toNode,
                        String fromForeign, String toPrimary, int exitingFKs, int indexExiting,
                        int enteringFKs, int indexEntering) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.fromForeign = fromForeign;
        this.toPrimary = toPrimary;

        // Exiting and entering arcs are spaced across the node,
        //  for a better readability of labels
        if (exitingFKs == 1 || (exitingFKs%2 == 1 && indexExiting == Math.ceil(exitingFKs/2.0))) {
            if (fromNode.getName().equalsIgnoreCase("Payment"))
                offsetExiting = 0;
        } else {
            offsetExiting = (exitingFKs/2-(indexExiting%(exitingFKs/2)))*
                    (ValueContainer.getNodeOffset()/exitingFKs);
            if (indexExiting <= exitingFKs/2) {
                offsetExiting *= -1;
            }
        }

        if (enteringFKs == 1 || (enteringFKs%2 == 1 && indexEntering == enteringFKs/2)) {
            offsetEntering = 0;
        } else {
            offsetEntering = (enteringFKs/2-(indexEntering%(enteringFKs/2)))*
                    (ValueContainer.getNodeOffset()/enteringFKs);
            if (indexEntering <= enteringFKs/2) {
                offsetEntering *= -1;
            }
        }
    }

    protected void render(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(ValueContainer.getColorOfArc(fromNode.getName(), fromForeign));
        g2d.setFont(ValueContainer.getFont());
        FontMetrics fm = g2d.getFontMetrics();
        int labelSpace = fm.stringWidth(fromForeign + " -> " + toPrimary);
        int spacing = 20;

        //draws arc from fromNode to toNode, with a preference for using the
        // shortest arc possible, and putting labels always on the fromNode exiting part
        int[] xPoints = new int[4];

        if (fromNode.getOuterX(false) > toNode.getOuterX(false)) {
            //             __s + lbl + s + fromNode
            // toNode + s__
            if ((fromNode.getOuterX(false)-labelSpace- 2*spacing) >=
                    (toNode.getOuterX(true)+ spacing) ) {
                xPoints[0] = fromNode.getOuterX(false);
                xPoints[3] = toNode.getOuterX(true);
                xPoints[1] = xPoints[0] - labelSpace - 2*spacing;
            // _s + lbl + s + fromNode
            // _____s + toNode
            } else {
                xPoints[0] = fromNode.getOuterX(false);
                xPoints[3] = toNode.getOuterX(false);
                xPoints[1] = Math.min(xPoints[3] - spacing, xPoints[0]-labelSpace- 2*spacing);
            }
        } else {
            // fromNode + s + lbl + s __
            //                      __ s + toNode
            if ((toNode.getOuterX(false)- spacing) >=
                    (fromNode.getOuterX(true)+labelSpace+ 2*spacing)) {
                xPoints[0] = fromNode.getOuterX(true);
                xPoints[3] = toNode.getOuterX(false);
                xPoints[1] = xPoints[0] + labelSpace + 2*spacing;
            // fromNode + s + lbl + s __
            //      toNode + s _____
            } else {
                xPoints[0] = fromNode.getOuterX(true);
                xPoints[3] = toNode.getOuterX(true);
                xPoints[1] = Math.max(xPoints[3] + spacing, xPoints[0]+labelSpace+2*spacing);
            }
        }
        xPoints[0] -= ValueContainer.getCameraX();
        xPoints[1] -= ValueContainer.getCameraX();
        xPoints[2] = xPoints[1];
        xPoints[3] -= ValueContainer.getCameraX();

        int[] yPoints = {fromNode.getY()+ offsetExiting - ValueContainer.getCameraY(),
                fromNode.getY()+ offsetExiting- ValueContainer.getCameraY(),
                toNode.getY()+offsetEntering- ValueContainer.getCameraY(),
                toNode.getY()+offsetEntering- ValueContainer.getCameraY()};

        g2d.drawPolyline(xPoints, yPoints, 4);

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        //writes fk -> referredPK on top of exitingLine
        if (xPoints[1] - xPoints[0] > 0){
            g2d.drawString(fromForeign + " -> " + toPrimary,
                    xPoints[0]+ spacing, yPoints[0]-1);
        } else {
            g2d.drawString(fromForeign + " -> " + toPrimary,
                    xPoints[0] - labelSpace- spacing, yPoints[0]-1);
        }

        //arrowHeads
        if (xPoints[1] - xPoints[3] < 0){
            g2d.drawLine(xPoints[3]-4, yPoints[3]-4, xPoints[3], yPoints[3]);
            g2d.drawLine(xPoints[3]-4, yPoints[3]+4, xPoints[3], yPoints[3]);
        } else {
            g2d.drawLine(xPoints[3], yPoints[3], xPoints[3]+4, yPoints[3]+4);
            g2d.drawLine(xPoints[3], yPoints[3], xPoints[3]+4, yPoints[3]-4);
        }
    }
}
