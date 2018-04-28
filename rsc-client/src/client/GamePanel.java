package client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private int width;
    private int height;
    
    private Canvas canvas;
    private BufferedImage image;

    public GamePanel(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));

        canvas = new Canvas(width, height);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void render(State state) {

        Graphics g = getGraphics();
        
        // Render the state onto our Canvas
        canvas.clear();
        state.render(canvas, g);

        // Draw the Canvas to the screen
        image.setRGB(0, 0, width, height, canvas.getPixels(), 0, width);
        g.drawImage(image, 0, 0, null);
    }

}
