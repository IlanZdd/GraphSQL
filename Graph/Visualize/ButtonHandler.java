package Graph.Visualize;

import Graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class ButtonHandler {
    List<Button> buttons = new LinkedList<>();

    public ButtonHandler(FontMetrics fm) {
        int atThisPointY = ValueContainer.getPanelOffset()/2;

        int width = fm.stringWidth("  Mid-Node  ");
        int height = Math.max(fm.getHeight()*2, ValueContainer.getPanelOffset());
        for (String label: new String[]{"Source", "Mid-Node", "Well", "External"}) {
            buttons.add(new Button(
                    new Point(ValueContainer.getPanelOffset()/2, atThisPointY),
                    width, height, label,
                    true, true, true));
            atThisPointY += height+ValueContainer.getPanelOffset()/2;
        }

        //save
        buttons.add(new saveButtons(
                new Point(ValueContainer.getPanelOffset()/2,
                        ValueContainer.getCanvasHeight()-ValueContainer.getPanelOffset()*2),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset()));

        //zoom in/out
        buttons.add(new ZoomButtons(
                new Point(ValueContainer.getCanvasWidth()-ValueContainer.getPanelOffset()*3/2,
                        ValueContainer.getCanvasHeight()-ValueContainer.getPanelOffset()*2),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset()));

        //colour
        /*buttons.add(new Button(
                new Point(ValueContainer.getCanvasWidth()-ValueContainer.getPanelOffset()*3/2, ValueContainer.getPanelOffset()/2),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset(), "colour",
                true, false, true));*/

        //colour2
        buttons.add(new Button(
                new Point(ValueContainer.getPanelOffset()/2, ValueContainer.getCanvasHeight()-ValueContainer.getPanelOffset()*7/2),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset(), "colour",
                true, true, false));

    }

    protected void updateButtonsWhenResized() {
        for (Button button : buttons) {
            if (!button.isOnTheLeft())
                button.setTLpointX(ValueContainer.getCanvasWidth()-ValueContainer.getPanelOffset()*2);
            if (!button.isOnTheTop()) {
                if (button.getLabel().equalsIgnoreCase("colour"))
                    button.setTLpointY(ValueContainer.getCanvasHeight()-ValueContainer.getPanelOffset()*7/2);
                else
                    button.setTLpointY(ValueContainer.getCanvasHeight() - ValueContainer.getPanelOffset() * 2);
            }
        }
    }

    protected void render(Graphics2D g2d) {
        for (Button button : buttons)
            button.paint(g2d);
        if (ValueContainer.isSavingMode()) {
        }
    }

    protected short clickedButton(Point mouse){
        for (Button button : buttons) {
            switch (button.contains(mouse)) {
                case 1 -> {
                    switch (button.getLabel().toLowerCase()) {
                        case "external" -> ValueContainer.setSelectedType(Graph.nodeType.external_node, "external");
                        case "mid-node" -> ValueContainer.setSelectedType(Graph.nodeType.mid_node, "mid-node");
                        case "source" -> ValueContainer.setSelectedType(Graph.nodeType.source, "source");
                        case "well" -> ValueContainer.setSelectedType(Graph.nodeType.well, "well");
                        case "colour" -> ValueContainer.changeTheme();
                        case "save" -> ValueContainer.setSavingMode();
                    }
                    return 1;
                }
                case 2 -> {
                    ValueContainer.setClickedButton("confirmSave");
                    return 2; }
                case 3 -> {
                    ValueContainer.setClickedButton("zoomIn");
                    ValueContainer.zoomIn();
                    return 1;
                }
                case 4 -> {
                    ValueContainer.setClickedButton("zoomOut");
                    ValueContainer.zoomOut();
                    return 1;
                }
            }
        }
        return 0;
    }
    protected short hoverOnButton(Point mouse){
        for (Button button : buttons) {
            switch (button.contains(mouse)) {
                case 1 -> {
                    ValueContainer.setHoveredButton(button.getLabel());
                    return 1;
                }
                case 2 -> {
                    ValueContainer.setHoveredButton("confirmSave");
                    return 1;
                }
                case 3 -> {
                    ValueContainer.setHoveredButton("zoomIn");
                    return 1;
                }
                case 4 -> {
                    ValueContainer.setHoveredButton("zoomOut");
                return 1;
                }
            }
        }
        if (ValueContainer.isButtonHovered()) {
            ValueContainer.setHoveredButton("");
            return 1;
        }
        return 0;
    }

    protected void addTextfield(JTextField textField) {
        for (Button button: buttons) {
            if (button instanceof saveButtons)
                ((saveButtons) button).addTextfield(textField);
        }
    }
}
