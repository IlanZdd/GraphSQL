package Graph.Visualize;

import Graph.Graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Visualize extends JFrame {
    private final CanvasHandler canvasHandler;
    private final ButtonHandler buttonHandler;
    private final Canvas canvas;
    private boolean moving = false;

    public Visualize(Graph graph) {
        setTitle(graph.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        setPreferredSize(new Dimension(ValueContainer.getCanvasWidth(),
                ValueContainer.getCanvasHeight()));
        setMinimumSize(new Dimension(ValueContainer.getCanvasWidth(),
                ValueContainer.getCanvasHeight()));

        canvasHandler = new CanvasHandler(graph);
        FontMetrics fm = this.getFontMetrics(ValueContainer.getFont());
        buttonHandler = new ButtonHandler(fm);
        canvas = new Canvas(canvasHandler, buttonHandler);
        getContentPane().add(canvas).setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
        getContentPane().setBackground(ValueContainer.getBackgroundColor());

        setVisible(true);
    }

    private void createImage() {
        BufferedImage image = new BufferedImage(CanvasHandler.getImageWidth(), CanvasHandler.getImageHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        // To avoid drama, the camera is moved where the start of the saving rectangle is
        Point camera = new Point(ValueContainer.getCameraX(), ValueContainer.getCameraY());
        ValueContainer.setCamera(new Point(CanvasHandler.startOfRect().x,
                CanvasHandler.startOfRect().y));

        //  The background is created, nodes are painted upon it
        g2d.setColor(ValueContainer.getBackgroundColor());
        g2d.fillRect(CanvasHandler.startOfRect().x- ValueContainer.getCameraX(),
                CanvasHandler.startOfRect().y- ValueContainer.getCameraY(),
                CanvasHandler.getImageWidth(), CanvasHandler.getImageHeight());
        canvas.render(g2d, true);

        //  The image is saved with textField.png or graphName.png
        try {
            File saving = new File("image" + ".png");
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
    }
}
