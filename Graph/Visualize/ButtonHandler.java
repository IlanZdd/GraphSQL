package Graph.Visualize;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class ButtonHandler {
    List<Button> buttons = new LinkedList<>();

    public ButtonHandler(FontMetrics fm) {
        int atThisPointY = ValueContainer.getPanelOffset()/2;

        int width = fm.stringWidth("  Mid-Node  ");
        for (String label: new String[]{"Source", "Mid-Node", "Well", "External"}) {
            buttons.add(new Button(
                    new Point(ValueContainer.getPanelOffset()/2, atThisPointY),
                    width, ValueContainer.getPanelOffset(), label,
                    true, true, true));
            atThisPointY += ValueContainer.getPanelOffset()*3/2;
        }

        buttons.add(new FloppyButton(
                new Point(ValueContainer.getPanelOffset()/2,
                        ValueContainer.getCanvasHeight()-ValueContainer.getPanelOffset()*2),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset()));

        //esc
        buttons.add(new Button(
                new Point(ValueContainer.getCanvasWidth()-ValueContainer.getPanelOffset()*3/2,
                        ValueContainer.getPanelOffset()/2),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset(), "X",
                false, false, true));
        //zoom in/out
        buttons.add(new ZoomButtons(
                new Point(ValueContainer.getCanvasWidth()-ValueContainer.getPanelOffset()*3/2,
                        ValueContainer.getCanvasHeight()-ValueContainer.getPanelOffset()*2),
                ValueContainer.getPanelOffset(), ValueContainer.getPanelOffset()));
    }

    protected void updateButtonsWhenResized() {
        for (Button button : buttons) {
            if (!button.isOnTheLeft())
                button.setTLpointX(ValueContainer.getCanvasWidth()-ValueContainer.getPanelOffset()*2);
            if (!button.isOnTheTop())
                button.setTLpointY(ValueContainer.getCanvasHeight()-ValueContainer.getPanelOffset()*2);
        }
    }

    protected void render(Graphics2D g2d) {
        for (Button button : buttons)
            button.paint(g2d);
    }
}
