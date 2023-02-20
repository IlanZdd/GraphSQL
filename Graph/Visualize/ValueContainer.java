package Graph.Visualize;

import Graph.Graph;

import java.awt.*;
import java.util.ArrayList;

import static Graph.Visualize.CanvasHandler.graph;

public class ValueContainer {
    //COLORS
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

    //GUI
    private static boolean savingMode = false;
    private static String selectedButton = "";
    private static String hoveredButton = "";
    private static String savingName = null;

    //FONT
    private static Font font = new Font("Dialog", Font.PLAIN, 12);
    private static final Font panelFont = new Font("Dialog", Font.PLAIN, 12);

    //DIMENSIONS NODE
    private static int nodeMaxLength = 0;
    private static int nodeWidth = 0;
    private static int nodeHeight = 0;
    private static int nodeOffset = 0;

    //DIMENSIONS CANVAS
    private static int canvasWidth = 640;
    private static int canvasHeight = 640/12*9;
    private static final int panelOffset = 20;

    //SELECTION
    private static Graph.nodeType selectedType = Graph.nodeType.unknown;
    private static String selectedNode = "";
    private static final ArrayList<String> selectedTree = new ArrayList<>();


    /*- COLORS -*/
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

    // pure colors
    protected static Color getBackgroundColor() {  return backgroundColor; }
    protected static Color getPrimaryColor() {  return primaryColor; }
    protected static Color getSecondaryColor() { return secondaryColor; }
    protected static Color getErrorColor() { return errorColor; }
    protected static Color getWritingColor() { return writingColor; }

    //object colors - canvas
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

    //object color - buttons
    protected static Color getBackgroundColorOfButton(String name) {
        if (darkTheme) {
            if ((name.equalsIgnoreCase("save"))) {
                if (savingMode && hoveredButton.equalsIgnoreCase("save"))
                    return getPrimaryColor().darker();
                if (savingMode)
                    return getPrimaryColor().darker().darker().darker();
            } else {
                if (selectedButton.equalsIgnoreCase(name) && hoveredButton.equalsIgnoreCase(name))
                    return getPrimaryColor().darker();
                if (selectedButton.equalsIgnoreCase(name))
                    return getPrimaryColor().darker().darker().darker();
            }
            if (hoveredButton.equalsIgnoreCase(name))
                return getSecondaryColor().darker().darker().darker();
        } else {
            if ((name.equalsIgnoreCase("save"))) {
                if (savingMode && hoveredButton.equalsIgnoreCase("save"))
                    return getPrimaryColor().brighter().brighter().brighter();
                if (savingMode)
                    return new Color(189, 236, 238);
            } else {
                if (selectedButton.equalsIgnoreCase(name) && hoveredButton.equalsIgnoreCase(name))
                    return getPrimaryColor().brighter();
                if (selectedButton.equalsIgnoreCase(name))
                    return new Color(189, 236, 238);
            }
            if (hoveredButton.equalsIgnoreCase(name))
                return getSecondaryColor().brighter().brighter().brighter();
        }
        return getBackgroundColor();
    }

    //object color - panel
    protected static Color getColorOfPanel() {
        if (darkTheme)
            return backgroundColor.brighter().brighter();
        else return backgroundColor;
    }
    protected static Color getScrollBarDragged() {
        if (darkTheme)
            return backgroundColor.brighter();
        else return new Color(208, 217, 231);
    }
    protected static Color getColorOfScrollBarHover() {
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
    protected static Font getPanelFont() { return panelFont; }

    /*- NODE DIMENSION -*/
    protected static int getNodeWidth() { return nodeWidth; }
    protected static int getNodeHeight() { return nodeHeight; }
    protected static int getNodeOffset() { return nodeOffset; }

    /*- CANVAS DIMENSION -*/
    protected static int getCanvasWidth() { return canvasWidth; }
    protected static int getCanvasHeight() { return canvasHeight; }
    protected static int getOffset() { return panelOffset; }

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

    /*- SAVING MODE -*/
    protected static boolean isSavingMode() { return savingMode; }
    protected static void setSavingMode() {
        savingMode = !savingMode;
        if (!savingMode && selectedButton.equalsIgnoreCase("save"))
            selectedButton = "";
    }
    public static String getSavingName() {
        return savingName;
    }
    public static void setSavingName(String savingName) {
        ValueContainer.savingName = savingName;
    }

    /*- SELECTIONS -*/
    protected static void cleanSelection() {
        ValueContainer.selectedType = Graph.nodeType.unknown;
        ValueContainer.selectedNode = "";
        ValueContainer.selectedTree.clear();
        ValueContainer.selectedButton = "";
    }

    // buttons
    protected static void setClickedButton (String button) {
        ValueContainer.selectedButton = button;
        ValueContainer.hoveredButton = button;
    }
    protected static void setHoveredButton (String button) {
        ValueContainer.hoveredButton = button;
    }
    protected static boolean isButtonHovered() {
        return !hoveredButton.equals("");
    }

    // nodes
    protected static Graph.nodeType getSelectedType() { return selectedType; }
    protected static void setSelectedType(Graph.nodeType selectedType, String button) {
        if (selectedType.equals(ValueContainer.getSelectedType())) {
            ValueContainer.cleanSelection();
            return;
        }
        ValueContainer.cleanSelection();
        ValueContainer.selectedButton = button;
        ValueContainer.selectedType = selectedType;
    }
    protected static void setSelectedNode(String selectedNode) {
        ValueContainer.cleanSelection();
        ValueContainer.selectedNode = selectedNode;
        selectedTree.addAll(graph.getTreeByTable(selectedNode));
    }
    protected static boolean isSelectedNode() {
        return !selectedNode.isEmpty();
    }
    protected static String getSelectedNode() {
        return selectedNode;
    }

    protected static String[] getNodeInfo(String name) {
        return graph.getTableInfo(name);
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
            CanvasHandler.modifier = 0.8;
        }
        ValueContainer.setClickedButton("");
    }
    protected static void zoomIn() {
        if (zoomStatus<fontSize.length-1){
            zoomStatus++;
            updateZoomValues();
            CanvasHandler.modifier = 1.3;
        }
        ValueContainer.setClickedButton("");
    }
    private static void updateZoomValues(){
        font = new Font(font.getName(), font.getStyle(), fontSize[zoomStatus]);
        nodeWidth = nodeMaxLength * textToWidthModifier[zoomStatus];
        nodeHeight = nodeWidth / 2;
        nodeOffset = nodeWidth / 4;
    }

}
