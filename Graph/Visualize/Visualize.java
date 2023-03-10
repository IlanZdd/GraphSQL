package Graph.Visualize;

import Graph.Graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Visualize extends JFrame {
    private final CanvasHandler canvasHandler;
    private final ButtonHandler buttonHandler;
    private final Canvas canvas;
    private boolean moving = false;

    public Visualize(Graph graph) {
        setTitle(graph.getName().toUpperCase() + " :: " + graph.getTableNumber() + " tables");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new GridLayout(1,0));
        setPreferredSize(new Dimension(ValueContainer.getCanvasWidth(),
                ValueContainer.getCanvasHeight()));
        setMinimumSize(new Dimension(ValueContainer.getCanvasWidth(),
                ValueContainer.getCanvasHeight()));
        getContentPane().setBackground(ValueContainer.getBackgroundColor());

        FontMetrics fm = this.getFontMetrics(ValueContainer.getPanelFont());
        canvasHandler = new CanvasHandler(graph);
        buttonHandler = new ButtonHandler(fm);
        canvas = new Canvas(canvasHandler, buttonHandler);

        getContentPane().add(canvas).setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));

        MouseAdapter canvasMouseAdapter = new MouseAdapter() {
            private CanvasNode clicked = null;
            private Point previous;
            private boolean movingSavingStart;
            private boolean movingSavingEnd;

            @Override
            public void mouseMoved(MouseEvent e) {
                //if I'm not moving any object on the canvas, hover is a thing
                if (!moving) {
                    String previousHover = ValueContainer.getHoveredButton();
                    if (buttonHandler.hoverOnButton(e.getPoint()) == 1 &&
                            !ValueContainer.getHoveredButton().equalsIgnoreCase(previousHover))
                        repaintAll();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // If a node is clicked, it will be highlighted, along with its arcs and tree
                short clickedButton = buttonHandler.clickedButton(e.getPoint());
                if (clickedButton == 0) { //no button was clicked
                    if ((clicked = canvasHandler.getNodeInThisPoint(e.getX(), e.getY())) != null) { //a node was clicked
                        ValueContainer.setSelectedNode(clicked.getName());
                    } else {
                        // else, the selections are cleaned
                        ValueContainer.cleanSelection();
                    }
                } else if (clickedButton == 2) //saving button was clicked 
                    createImage();
                
                repaintAll();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //start of moving a node, saving point or camera
                super.mousePressed(e);
                if ((clicked = canvasHandler.getNodeInThisPoint(e.getX(), e.getY())) != null) {
                    //if a node is pressed, it's the start of dragging a node, and it will become selected
                    ValueContainer.setSelectedNode(clicked.getName());
                    repaintAll();
                } else if (canvasHandler.onFromSavingPoint(e.getPoint()) && ValueContainer.isSavingMode()) {
                    movingSavingStart = true;
                } else if (canvasHandler.onToSavingPoint(e.getPoint()) && ValueContainer.isSavingMode()) {
                    movingSavingEnd = true;
                }
                
                canvasHandler.shouldDrawPanel = false; //don't draw the panel if something is moving
                moving = true;
                previous = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //we aren't dragging anymore
                moving = false;
                clicked = null;
                movingSavingEnd = false;
                movingSavingStart = false;
                
                //we can draw the panel again, and will if a node is selected
                canvasHandler.shouldDrawPanel = true;
                if (canvasHandler.infoPanel != null) repaintAll();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (moving) {
                    if (clicked != null) { //moving a node
                        clicked.setX(e.getX() + ValueContainer.getCameraX());
                        clicked.setY(e.getY() + ValueContainer.getCameraY());

                    } else if (movingSavingStart) { //moving a saving point
                        canvasHandler.fromSavePoint.x = e.getX() + ValueContainer.getCameraX();
                        canvasHandler.fromSavePoint.y = e.getY() + ValueContainer.getCameraY();
                    } else if (movingSavingEnd) {
                        canvasHandler.toSavePoint.x = e.getX() + ValueContainer.getCameraX();
                        canvasHandler.toSavePoint.y = e.getY() + ValueContainer.getCameraY();

                    } else {//moving the camera
                        ValueContainer.setCameraX((int) (ValueContainer.getCameraX() + (previous.getX() - e.getX())));
                        ValueContainer.setCameraY((int) (ValueContainer.getCameraY() + (previous.getY() - e.getY())));
                        previous = e.getPoint();
                    }

                    canvas.removeAll();
                    canvas.repaint();
                }
            }
        };
        ComponentAdapter thisListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                //window was resized, I need the things not anchored to top-left
                JFrame c = (JFrame) e.getComponent();
                ValueContainer.setCanvasSize(c.getContentPane().getWidth(),
                        c.getContentPane().getHeight());

                setBackground(ValueContainer.getBackgroundColor());

                buttonHandler.updateButtonsWhenResized();
                canvasHandler.updatePanelX();
                repaintAll();
            }
        };

        canvas.addMouseMotionListener(canvasMouseAdapter);
        canvas.addMouseListener(canvasMouseAdapter);
        addComponentListener(thisListener);

        setVisible(true);
    }

    private void createImage() {
        BufferedImage image = new BufferedImage(canvasHandler.getImageWidth(), canvasHandler.getImageHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        // To avoid drama, the camera is moved where the start of the saving rectangle is
        Point camera = new Point(ValueContainer.getCameraX(), ValueContainer.getCameraY());
        ValueContainer.setCamera(new Point(canvasHandler.getTopLeftOfSavingArea().x,
                canvasHandler.getTopLeftOfSavingArea().y));

        //  The background is created, nodes are painted upon it
        g2d.setColor(ValueContainer.getBackgroundColor());
        g2d.fillRect(canvasHandler.getTopLeftOfSavingArea().x- ValueContainer.getCameraX(),
                canvasHandler.getTopLeftOfSavingArea().y- ValueContainer.getCameraY(),
                canvasHandler.getImageWidth(), canvasHandler.getImageHeight());
        canvas.render(g2d, true);

        //  The image is saved with textField.png or graphName.png
        try {
            File saving;
            if (ValueContainer.getSavingName().isEmpty())
                saving = new File("image" + ".png");
            else saving= new File(ValueContainer.getSavingName() + ".png");
            ImageIO.write(image, "png", saving);
            System.out.println("Image saved at " + saving.getAbsolutePath());
        } catch (IOException e) { throw new RuntimeException(e); }

        //  Everything is reset
        ValueContainer.setCamera(camera);
        ValueContainer.setClickedButton("");
        repaintAll();
        g2d.dispose();
    }

    private void repaintAll() {
        //repaints the canvas and panel
        //canvas.removeAll();
        canvas.repaint();
    }
}
