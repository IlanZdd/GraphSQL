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
        setSize(640, 640/12*9);
        setTitle(graph.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        getContentPane().add(canvas).setPreferredSize(new Dimension(ConstantHandler.getWidthCanvas(),
                getHeight()));
        getContentPane().add(panel).setPreferredSize(new Dimension(ConstantHandler.getPanelWidth(),
                ConstantHandler.getHeightCanvas()));
        getContentPane().setBackground(ConstantHandler.getBackgroundColor());

        setPreferredSize(new Dimension(ConstantHandler.getWidthCanvas()+ConstantHandler.getPanelWidth(), ConstantHandler.getHeightCanvas()));
        setMinimumSize(new Dimension(ConstantHandler.getWidthCanvas()+ConstantHandler.getPanelWidth(), ConstantHandler.getHeightCanvas()));
        MouseAdapter mouseAdapter = new MouseAdapter() {
            private NodeObj clicked = null;
            private Point previous;
            private boolean movingSavingStart;
            private boolean movingSavingEnd;

            @Override
            public void mouseClicked(MouseEvent e) {
                if ((clicked = handler.getNodeInThisCoords(e.getX(), e.getY())) != null) {
                    ConstantHandler.setSelectedNode(clicked.getName());
                    panel.screen = Panel.showing.TABLE_INFO;
                    panel.updateINfo(handler.getInfo(clicked.getName()));
                } else {
                    ConstantHandler.setSelectedNode("");
                    panel.printedInfo = null;
                }
                ConstantHandler.setSelectedType(Graph.nodeType.unknown);
                repaintAll();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if ((clicked = handler.getNodeInThisCoords(e.getX(), e.getY())) != null) {
                    ConstantHandler.setSelectedNode(clicked.getName());
                    ConstantHandler.setSelectedType(Graph.nodeType.unknown);
                    panel.updateINfo(handler.getInfo(clicked.getName()));
                    panel.screen = Panel.showing.TABLE_INFO;
                    repaintAll();
                } else if (handler.onSavingStart(e.getPoint()) && ConstantHandler.isSavingMode()) {
                    movingSavingStart = true;
                } else if (handler.onSavingEnd(e.getPoint()) && ConstantHandler.isSavingMode()) {
                    movingSavingEnd = true;
                }
                moving = true;
                previous = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                moving = false;
                clicked = null;
                panel.printedInfo = null;
                movingSavingEnd = false;
                movingSavingStart = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (moving) {
                    if (clicked != null) {
                        clicked.setX(e.getX() + ConstantHandler.getCameraX());
                        clicked.setY(e.getY() + ConstantHandler.getCameraY());
                        canvas.removeAll();
                        canvas.repaint();
                    } else if (movingSavingStart) {
                        Handler.take_from_here.x = e.getX() + ConstantHandler.getCameraX();
                        Handler.take_from_here.y = e.getY() + ConstantHandler.getCameraY();
                    } else if (movingSavingEnd) {
                        Handler.take_to_here.x = e.getX() + ConstantHandler.getCameraX();
                        Handler.take_to_here.y = e.getY() + ConstantHandler.getCameraY();
                    } else {
                        ConstantHandler.setCameraX((int) (ConstantHandler.getCameraX() + (previous.getX() - e.getX())));
                        ConstantHandler.setCameraY((int) (ConstantHandler.getCameraY() + (previous.getY() - e.getY())));
                        previous = e.getPoint();
                    }
                    canvas.removeAll();
                    canvas.repaint();
                }
            }
        };
        canvas.addMouseListener(mouseAdapter);
        MouseAdapter mouseAdapterPanel = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (panel.amISaving(e.getPoint())) {
                    createImage();
                    System.out.println("Saved");
                    return;
                }
                ConstantHandler.setSelectedType(panel.pressingOnSomething(e.getPoint()));
                if (ConstantHandler.getSelectedType() != Graph.nodeType.unknown) {
                    ConstantHandler.setSelectedNode("");
                    panel.printedInfo = null;
                }
                repaintAll();
            }
        };
        panel.addMouseListener(mouseAdapterPanel);
        canvas.addMouseMotionListener(mouseAdapter);
        addComponentListener(listener);
        setVisible(true);
    }

    ComponentAdapter listener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            JFrame c = (JFrame) e.getComponent();
            ConstantHandler.setCanvasSize(c.getWidth()-ConstantHandler.getPanelWidth(),
                    c.getContentPane().getHeight());
            setBackground(ConstantHandler.getBackgroundColor());
            panel.setLocation(getWidth()-ConstantHandler.getPanelWidth(), 0);
            panel.setPreferredSize(new Dimension(ConstantHandler.getPanelWidth(), getHeight()));
            repaintAll();
        }
    };

    private void createImage() {
        BufferedImage image = new BufferedImage(Handler.getImageWidth(), Handler.getImageHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        Point camera = new Point(ConstantHandler.getCameraX(), ConstantHandler.getCameraY());

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        ConstantHandler.setCamera(new Point(Handler.startOfRect().x,
                Handler.startOfRect().y));
        g2d.setColor(ConstantHandler.getBackgroundColor());
        g2d.fillRect(Handler.startOfRect().x-ConstantHandler.getCameraX(),
                Handler.startOfRect().y-ConstantHandler.getCameraY(),
                Handler.getImageWidth(), Handler.getImageHeight());
        canvas.render(g2d, true);

        try {
            File saving;
            if (panel.textField.getText().isBlank())
                 saving = new File("Image.png");
            else saving = new File(panel.textField.getText() + ".png");
            ImageIO.write(image, "png", saving);
            System.out.println("Image saved at " + saving.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ConstantHandler.setCamera(camera);
        repaintAll();
        g2d.dispose();
    }

    private void repaintAll() {
        panel.removeAll();
        panel.repaint();
        canvas.removeAll();
        canvas.repaint();
    }
}
