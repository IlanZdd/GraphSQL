package Graph.Visualize;

import Graph.Graph;

import java.awt.Color;
import java.awt.Point;
import java.awt.Font;
import java.util.ArrayList;

import static Graph.Visualize.Handler.graph;

public class ValueContainer {
    //COLORI
    private static Color backgroundColor = new Color(25, 28, 33);
    private static Color primaryColor = new Color(122, 130, 227);
    private static Color secondaryColor = new Color(122, 183, 126);
    private static Color errorColor = new Color(225, 100, 83);
    private static Color writingColor = new Color(236, 238, 241);
    private static boolean darkTheme = true;

    //ZOOM
    private static Point camera = new Point(0,0);
    private static int zoomStatus = 2;
    private static final int[] fontSize = {7, 9, 12, 14, 16, 18};
    private static final int[] textToWidthModifier = {4, 6, 8, 9, 11, 13};
    private static boolean savingMode = false;

    //FONT
    private static Font font = new Font("Dialog", Font.PLAIN, 12);
    private static final Font panelFont = new Font("Dialog", Font.PLAIN, 12);

    //DIMENSIONI NODI
    private static int nodeMaxLength = 0;
    private static int nodeWidth = 0;
    private static int nodeHeight = 0;
    private static int nodeOffset = 0;

    //DIMENSIONI CANVAS
    private static int canvasWidth = 640;
    private static int canvasHeight = 640/12*9;

    //PANEL
    private static final int panelWidth = 640/3;
    private static final int panelOffset = 20;

    //SELECTION
    private static Graph.nodeType selectedType = Graph.nodeType.unknown;
    private static String selectedNode = "";
    private static final ArrayList<String> selectedTree = new ArrayList<>();


    /*- get colors -*/
    protected  static void changeTheme() {
        if (darkTheme) {
            darkTheme = false;
            backgroundColor = new Color(255, 255, 255);
            primaryColor = new Color(29, 56, 126);
            secondaryColor = new Color(157, 76, 155);
            errorColor = new Color(225, 100, 83);
            writingColor = new Color(50, 52, 52);
        } else {
            darkTheme = true;
            backgroundColor = new Color(25, 28, 33);
            primaryColor = new Color(122, 130, 227);
            secondaryColor = new Color(122, 183, 126);
            errorColor = new Color(225, 100, 83);
            writingColor = new Color(236, 238, 241);
        }
    }
    protected static Color getBackgroundColor() {  return backgroundColor; }
    protected static Color getPrimaryColor() {  return primaryColor; }
    protected static Color getSecondaryColor() { return secondaryColor; }
    protected static Color getErrorColor() { return errorColor; }
    protected static Color getWritingColor() { return writingColor; }
    protected static Color getColorOfNode(String name) {
        if (selectedNode.equalsIgnoreCase(name) || graph.getTableType(name) == selectedType)
            return getPrimaryColor();
        if (selectedTree.contains(name))
            return getSecondaryColor();
        return getWritingColor();
    }
    protected static Color getColorOfArc(String fromNode, String exitingFK) {
        if (graph.isArcInTableProblematic(fromNode, exitingFK))
            return getErrorColor();
        if (selectedNode.equalsIgnoreCase(fromNode) || graph.getTableType(fromNode) == selectedType)
            return getPrimaryColor();
        if (selectedTree.contains(fromNode))
            return getSecondaryColor();
        return getWritingColor();
    }
    protected static Color getBackgroundColorOfNode(String name) {
        if (darkTheme) {
            if (selectedNode.equalsIgnoreCase(name) || graph.getTableType(name) == selectedType)
                return getPrimaryColor().darker().darker().darker();
            if (selectedTree.contains(name))
                return getSecondaryColor().darker().darker().darker();
            return getWritingColor().darker().darker().darker();
        } else {
            if (selectedNode.equalsIgnoreCase(name) || graph.getTableType(name) == selectedType)
                return  new Color(189, 236, 238);
            if (selectedTree.contains(name))
                return getSecondaryColor().brighter().brighter().brighter();
            return new Color(231, 236, 241);
        }
    }
    protected static Color getColorOfPanel() {
        if (darkTheme)
            return backgroundColor.brighter();
        else return new Color(208, 217, 231);
    }
    protected static Color getColorOfInfoPanel() {
        if (darkTheme)
            return backgroundColor.brighter().brighter();
        else return backgroundColor;
    }
    protected static Color getColorOfPanelButton() {
        if (darkTheme)
            return new Color(70, 84, 105);
        else return getSecondaryColor().brighter().brighter().brighter();
    }
    protected static Color getColorOfScrollBar() {
        if (darkTheme)
            return primaryColor;
        else return primaryColor.brighter().brighter();
    }

    /*- FONT -*/
    protected static Font getFont() { return font; }

    /*- NODE DIMENSION -*/
    protected static int getNodeWidth() { return nodeWidth; }
    protected static int getNodeHeight() { return nodeHeight; }
    protected static int getNodeOffset() { return nodeOffset; }

    /*- CANVAS DIMENSION -*/
    protected static int getCanvasWidth() { return canvasWidth; }
    protected static int getCanvasHeight() { return canvasHeight; }

    protected static void setCanvasSize(int widthCanvas, int heightCanvas) {
        ValueContainer.canvasHeight = heightCanvas;
        ValueContainer.canvasWidth = widthCanvas;
    }
    protected static void setNodeSizeByTextLength(int length) {
        if (length*textToWidthModifier[zoomStatus] > nodeWidth) {
            ValueContainer.nodeMaxLength = length;
            ValueContainer.nodeWidth = length * textToWidthModifier[zoomStatus] ;
            ValueContainer.nodeHeight = nodeWidth / 2;
            ValueContainer.nodeOffset = nodeWidth / 4;
        }
    }

    /*- PANEL DIMENSION -*/
    protected static int getPanelWidth() { return panelWidth; }
    protected static Font getPanelFont() { return panelFont; }
    protected static int getPanelOffset() { return panelOffset; }

    /*- SAVING MODE -*/
    protected static boolean isSavingMode() { return savingMode; }
    protected static void setSavingMode() { savingMode = !savingMode; }

    /*- SELECTIONS -*/
    protected static void cleanSelection() {
        ValueContainer.selectedType = Graph.nodeType.unknown;
        ValueContainer.selectedNode = "";
        ValueContainer.selectedTree.clear();
    }
    protected static Graph.nodeType getSelectedType() { return selectedType; }
    protected static void setSelectedType(Graph.nodeType selectedType) {
        ValueContainer.cleanSelection();
        ValueContainer.selectedType = selectedType;
    }
    protected static void setSelectedNode(String selectedNode) {
        ValueContainer.cleanSelection();
        ValueContainer.selectedNode = selectedNode;
        selectedTree.addAll(graph.getTreeByTable(selectedNode));
    }

    /*- CAMERA -*/
    protected static int getCameraX() { return camera.x; }
    protected static int getCameraY() { return camera.y; }
    protected static void setCamera(Point camera) { ValueContainer.camera = camera; }
    protected static void setCameraX(int x) { camera.move(x, getCameraY()); }
    protected static void setCameraY(int y) { camera.move(getCameraX(), y); }

    protected static void zoomOut() {
        if (zoomStatus>0){
            zoomStatus--;
            updateZoomValues();
            Handler.modifier = 0.8;
        }
    }
    protected static void zoomIn() {
        if (zoomStatus<fontSize.length-1){
            zoomStatus++;
            updateZoomValues();
            Handler.modifier = 1.3;
        }
    }
    private static void updateZoomValues(){
        font = new Font(font.getName(), font.getStyle(), fontSize[zoomStatus]);
        nodeWidth = nodeMaxLength * textToWidthModifier[zoomStatus];
        nodeHeight = nodeWidth / 2;
        nodeOffset = nodeWidth / 4;
    }

}
