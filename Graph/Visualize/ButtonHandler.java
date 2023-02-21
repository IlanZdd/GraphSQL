package Graph.Visualize;

import Graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class ButtonHandler {
    List<Button> buttons = new LinkedList<>();

    public ButtonHandler(FontMetrics fm) {
        int atThisPointY = ValueContainer.getOffset()/2;

        //draws the nodeType buttons on the top left
        int width = fm.stringWidth("  Mid-Node  ");
        int height = Math.max(fm.getHeight()*2, ValueContainer.getOffset());

        for (String label: new String[]{"Source", "Mid-Node", "Well", "External"}) {
            buttons.add(new Button(
                    new Point(ValueContainer.getOffset()/2, atThisPointY),
                    width, height, label,
                    true, true, true));
            atThisPointY += height+ValueContainer.getOffset()/2;
        }

        //draws the saving buttons in the bottom left
        buttons.add(new ButtonSave(
                new Point(ValueContainer.getOffset()/2,
                        ValueContainer.getCanvasHeight()-ValueContainer.getOffset()*2),
                ValueContainer.getOffset(), ValueContainer.getOffset()));

        //draws the colour button in the bottom left, above the saves
        buttons.add(new Button(
                new Point(ValueContainer.getOffset()/2, ValueContainer.getCanvasHeight()-ValueContainer.getOffset()*7/2),
                ValueContainer.getOffset(), ValueContainer.getOffset(), "colour",
                true, true, false));

        // draws the zoom in/out buttons on the bottom right
        buttons.add(new ButtonZoom(
                new Point(ValueContainer.getCanvasWidth()-ValueContainer.getOffset()*3/2,
                        ValueContainer.getCanvasHeight()-ValueContainer.getOffset()*2),
                ValueContainer.getOffset(), ValueContainer.getOffset()));
    }

    protected void updateButtonsWhenResized() {
        for (Button button : buttons) {
            if (!button.isOnTheLeft()) //widening the window requires to move the right buttons
                button.setTLPointX(ValueContainer.getCanvasWidth()-ValueContainer.getOffset()*2);
            if (!button.isOnTheTop()) { //elongating the window requires to move the bottom buttons
                if (button.getLabel().equalsIgnoreCase("colour")) //colour is in a special position
                    button.setTLPointY(ValueContainer.getCanvasHeight()-ValueContainer.getOffset()*7/2);
                else
                    button.setTLPointY(ValueContainer.getCanvasHeight() - ValueContainer.getOffset() * 2);
            }
        }
    }

    protected void render(Graphics2D g2d) {
        for (Button button : buttons)
            button.paint(g2d);
    }

    protected short clickedButton(Point mouse){
        for (Button button : buttons) {
            switch (button.contains(mouse)) {
                case 1 -> {
                    switch (button.getLabel().toLowerCase()) {
                        case "external" ->
                                ValueContainer.setSelectedType(Graph.nodeType.external_node, "external");
                        case "mid-node" ->
                                ValueContainer.setSelectedType(Graph.nodeType.mid_node, "mid-node");
                        case "source" ->
                                ValueContainer.setSelectedType(Graph.nodeType.source, "source");
                        case "well" ->
                                ValueContainer.setSelectedType(Graph.nodeType.well, "well");
                        case "colour" ->
                                ValueContainer.changeTheme();
                        case "save" ->
                                ValueContainer.setSavingMode();
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
        return 0; //no button in this coordinates
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
        if (ValueContainer.isHoveredButton()) {
            ValueContainer.setHoveredButton("");
            return 1;
        }
        return 0;
    }

    protected void addTextField(JTextField textField) {
        for (Button button: buttons) {
            if (button instanceof ButtonSave)
                ((ButtonSave) button).addTextField(textField);
        }
    }
}
