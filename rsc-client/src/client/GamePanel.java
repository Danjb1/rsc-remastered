package client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private RsLauncher rs;
    private int width, height;
    private Canvas canvas;
    private BufferedImage image;

    public GamePanel(RsLauncher rs, int width, int height) {
        this.rs = rs;
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));

        canvas = new Canvas(width, height);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Clear pixel data
        canvas.clear();

        // Render the current state
        State state = rs.getState();
        if (state == null) {
            return;
        }
        state.render(canvas, g);
        
        // Draw canvas to the screen
        image.setRGB(0, 0, width, height, canvas.getPixels(), 0, width);
        g.drawImage(image, 0, 0, null);
    }

    public Canvas getCanvas() {
        return canvas;
    }

}
