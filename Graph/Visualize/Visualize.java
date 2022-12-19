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
    private final Handler handler;
    private final Panel panel;
    private final Canvas canvas;
    private boolean moving = false;

    public Visualize(Graph graph) {
        handler = new Handler(graph);
        canvas = new Canvas(handler);
        panel = new Panel();

        setTitle(graph.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        getContentPane().add(canvas).setPreferredSize(new Dimension(ValueContainer.getCanvasWidth(),
                getHeight()));
        getContentPane().add(panel).setPreferredSize(new Dimension(ValueContainer.getPanelWidth(),
                getHeight()));
        getContentPane().setBackground(ValueContainer.getBackgroundColor());

        setPreferredSize(new Dimension(ValueContainer.getCanvasWidth()+ ValueContainer.getPanelWidth(), ValueContainer.getCanvasHeight()));
        setMinimumSize(new Dimension(ValueContainer.getCanvasWidth()+ ValueContainer.getPanelWidth(), ValueContainer.getCanvasHeight()));
/*
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);*/
        MouseAdapter canvasMouseAdapter = new MouseAdapter() {
            private NodeObject clicked = null;
            private Point previous;
            private boolean movingSavingStart;
            private boolean movingSavingEnd;

            @Override
            public void mouseClicked(MouseEvent e) {
                // If a node is clicked, it will be highlighted, along with its arcs and tree
                if ((clicked = handler.getNodeInThisPoint(e.getX(), e.getY())) != null) {
                    ValueContainer.setSelectedNode(clicked.getName());
                    panel.updateINfo(handler.getInfo(clicked.getName()));
                } else {
                    // else, the selections are cleaned
                    ValueContainer.cleanSelection();
                    panel.printedInfo = null;
                }
                repaintAll();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //start of moving a node, saving point or camera
                super.mousePressed(e);
                if ((clicked = handler.getNodeInThisPoint(e.getX(), e.getY())) != null) {
                    //if a node is pressed, it's the start of dragging a node, and it will become selected
                    ValueContainer.setSelectedNode(clicked.getName());
                    panel.updateINfo(handler.getInfo(clicked.getName()));
                    repaintAll();

                } else if (handler.onSavingStart(e.getPoint()) && ValueContainer.isSavingMode()) {
                    movingSavingStart = true;
                } else if (handler.onSavingEnd(e.getPoint()) && ValueContainer.isSavingMode()) {
                    movingSavingEnd = true;
                }
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
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (moving) {
                    if (clicked != null) { //moving a node
                        clicked.setX(e.getX() + ValueContainer.getCameraX());
                        clicked.setY(e.getY() + ValueContainer.getCameraY());

                    } else if (movingSavingStart) { //moving a saving point
                        Handler.take_from_here.x = e.getX() + ValueContainer.getCameraX();
                        Handler.take_from_here.y = e.getY() + ValueContainer.getCameraY();
                    } else if (movingSavingEnd) {
                        Handler.take_to_here.x = e.getX() + ValueContainer.getCameraX();
                        Handler.take_to_here.y = e.getY() + ValueContainer.getCameraY();

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
        MouseAdapter panelMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (panel.amISaving(e.getPoint())) {
                    //clicked on confirm saving
                    createImage();
                    System.out.println("Saved");
                    panel.textFieldText = "";
                    ValueContainer.setSavingMode();
                    return;
                } else {

                    //clicked on a type button
                    ValueContainer.setSelectedType(panel.pressingOnSomething(e.getPoint()));
                    if (ValueContainer.getSelectedType() != Graph.nodeType.unknown) {
                        panel.printedInfo = null;
                    }
                }
                repaintAll();
            }
        };
        ComponentAdapter thisListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                JFrame c = (JFrame) e.getComponent();
                ValueContainer.setCanvasSize(c.getContentPane().getWidth() - ValueContainer.getPanelWidth(),
                        c.getContentPane().getHeight());
                setBackground(ValueContainer.getBackgroundColor());
                panel.setLocation(getWidth() - ValueContainer.getPanelWidth(), 0);
                panel.setPreferredSize(new Dimension(ValueContainer.getPanelWidth(),
                        canvas.getHeight()));
                panel.setMinimumSize(new Dimension(ValueContainer.getPanelWidth(),
                        canvas.getHeight()));
                panel.setMinimumSize(new Dimension(ValueContainer.getPanelWidth(),
                        canvas.getHeight()));

                repaintAll();
            }
        };

        panel.addMouseListener(panelMouseAdapter);
        canvas.addMouseMotionListener(canvasMouseAdapter);
        canvas.addMouseListener(canvasMouseAdapter);
        addComponentListener(thisListener);
        setVisible(true);
    }

    private void createImage() {
        BufferedImage image = new BufferedImage(Handler.getImageWidth(), Handler.getImageHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        // To avoid drama, the camera is moved where the start of the saving rectangle is
        Point camera = new Point(ValueContainer.getCameraX(), ValueContainer.getCameraY());
        ValueContainer.setCamera(new Point(Handler.startOfRect().x,
                Handler.startOfRect().y));

        //  The background is created, nodes are painted upon it
        g2d.setColor(ValueContainer.getBackgroundColor());
        g2d.fillRect(Handler.startOfRect().x- ValueContainer.getCameraX(),
                Handler.startOfRect().y- ValueContainer.getCameraY(),
                Handler.getImageWidth(), Handler.getImageHeight());
        canvas.render(g2d, true);

        //  The image is saved with textField.png or graphName.png
        try {
            File saving;
            if (panel.textField.getText().isBlank())
                 saving = new File("Image.png");
            else saving = new File(panel.textField.getText() + ".png");
            ImageIO.write(image, "png", saving);
            System.out.println("Image saved at " + saving.getAbsolutePath());
        } catch (IOException e) { throw new RuntimeException(e); }

        //  Everything is reset
        ValueContainer.setCamera(camera);
        repaintAll();
        g2d.dispose();
    }

    private void repaintAll() {
        //repaints the canvas and panel
        canvas.removeAll();
        canvas.repaint();
        panel.removeAll();
        panel.repaint();
    }
}
