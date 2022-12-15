package Graph.Visualize;

import Graph.Graph;

import java.awt.Color;
import java.awt.Point;
import java.awt.Font;
import java.util.ArrayList;

import static Graph.Visualize.Handler.graph;

public class ConstantHandler {
    //COLORI
    private static Color backgroundColor = new Color(25, 28, 33);
    //private static Color backgroundColor = new Color(43, 43, 43);
    private static Color primaryColor = new Color(122, 130, 227);
    private static Color secondaryColor = new Color(122, 183, 126);
    private static Color errorColor = new Color(225, 100, 83);
    private static Color writingColor = new Color(236, 238, 241);

    private static boolean darkTheme = true;
    //FONT
    private static Font font = new Font("Dialog", Font.PLAIN, 12);
    private static final Font panelFont = new Font("Dialog", Font.PLAIN, 12);
    private static final int[] fontSize = {7, 9, 12, 14, 16, 18};
    private static int zoomStatus = 2;

    //DIMENSIONI NODI
    private static int widthNode = 0;
    private static int length = 0;

    private static int heightNode = 0;
    private static final int[] textToWidthModifier = {4, 6, 8, 9, 11, 13};
    private static int offsetNode = 0;

    private static final ArrayList<String> selectedTree = new ArrayList<>();
    private static String selectedNode = "";

    //DIMENSIONI CANVAS
    private static int widthCanvas = 640;
    private static int heightCanvas = 640/12*9;

    //PANEL
    private static final int panelWidth = 640/3;
    private static final int offset = 20;
    private static Point camera = new Point(0,0);
    private static Graph.nodeType selectedType = Graph.nodeType.unknown;
    private static final ArrayList<String> nodesOfThisType = new ArrayList<>();

    private static boolean savingMode = false;

    protected static Color getBackgroundColor() {
        return backgroundColor;
    }
    protected static Color getPrimaryColor() {
        return primaryColor;
    }
    protected static Color getSecondaryColor() {
        return secondaryColor;
    }
    protected static Color getErrorColor() {
        return errorColor;
    }
    protected static Color getWritingColor() {
        return writingColor;
    }
    protected static Font getFont() {
        return font;
    }
    protected static int getWidthNode() {
        return widthNode;
    }
    protected static int getHeightNode() {
        return heightNode;
    }
    protected static int getOffsetNode() {
        return offsetNode;
    }
    protected static int getWidthCanvas() {
        return widthCanvas;
    }
    protected static int getHeightCanvas() {
        return heightCanvas;
    }

    protected static boolean isSavingMode() {
        return savingMode;
    }

    protected static void setSavingMode() {
        savingMode = !savingMode;
    }

    protected static int getPanelWidth() {
        return panelWidth;
    }

    protected static Font getPanelFont() {        return panelFont;    }

    protected static int getOffset() {
        return offset;
    }

    protected static Graph.nodeType getSelectedType() {
        return selectedType;
    }

    protected static void setSelectedType(Graph.nodeType selectedType) {
        ConstantHandler.selectedType = selectedType;
        nodesOfThisType.clear();
        nodesOfThisType.addAll(graph.listTablesOfType(selectedType));
    }

    protected static void setCanvasSize(int widthCanvas, int heightCanvas) {
        ConstantHandler.heightCanvas = heightCanvas;
        ConstantHandler.widthCanvas = widthCanvas;
    }

    protected static void setNodeSizeByTextLength(int length) {
        if (length*textToWidthModifier[zoomStatus] > widthNode) {
            ConstantHandler.length = length;
            ConstantHandler.widthNode = length * textToWidthModifier[zoomStatus] ;
            ConstantHandler.heightNode = widthNode / 2;
            ConstantHandler.offsetNode = widthNode / 4;
        }
    }

    protected static void setSelectedNode(String selectedNode) {
        ConstantHandler.selectedNode = selectedNode;
        selectedTree.clear();
        selectedTree.addAll(graph.getTreeByTable(selectedNode));
    }

    protected static Color getThisNodeColor (String name) {
        if (selectedNode.equalsIgnoreCase(name) || nodesOfThisType.contains(name))
            return getPrimaryColor();
        if (selectedTree.contains(name))
            return getSecondaryColor();
        return getWritingColor();
    }

    protected static Color getThisArcColor (String fromNode, String exitingFK) {
        if (graph.isArcInTableProblematic(fromNode, exitingFK))
            return getErrorColor();
        if (selectedNode.equalsIgnoreCase(fromNode) || nodesOfThisType.contains(fromNode))
            return getPrimaryColor();
        if (selectedTree.contains(fromNode))
            return getSecondaryColor();
        return getWritingColor();
    }

    protected static Color getThisNodeBackground (String name) {
        if (darkTheme) {
            if (selectedNode.equalsIgnoreCase(name) || nodesOfThisType.contains(name))
                return getPrimaryColor().darker().darker().darker();
            if (selectedTree.contains(name))
                return getSecondaryColor().darker().darker().darker();
            return getWritingColor().darker().darker().darker();
        } else {
            if (selectedNode.equalsIgnoreCase(name) || nodesOfThisType.contains(name))
                return  new Color(189, 236, 238);
            if (selectedTree.contains(name))
                return getSecondaryColor().brighter().brighter().brighter();
            return new Color(231, 236, 241);
        }
    }

    protected static Color getPanelColor () {
        if (darkTheme)
            return backgroundColor.brighter();
        else return new Color(208, 217, 231);
    }
    protected static Color getPanelPanelColor () {
        if (darkTheme)
            return backgroundColor.brighter().brighter();
        else return backgroundColor;
    }
    protected static Color getPanelBtnColor () {
        if (darkTheme)
            return new Color(70, 84, 105);
        else return getSecondaryColor().brighter().brighter().brighter();
    }

    protected static Color getScrollBarColor() {
        if (darkTheme)
            return primaryColor;
        else return primaryColor.brighter().brighter();
    }


    protected static int getCameraX() {
        return camera.x;
    }
    protected static int getCameraY() {
        return camera.y;
    }
    protected static void setCamera(Point camera) {
        ConstantHandler.camera = camera;
    }
    protected static void setCameraX(int x) {
        camera.move(x, getCameraY());
    }
    protected static void setCameraY(int y) {
        camera.move(getCameraX(), y);
    }

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
        widthNode = length * textToWidthModifier[zoomStatus];
        heightNode = widthNode / 2;
        offsetNode = widthNode / 4;
    }

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
}
