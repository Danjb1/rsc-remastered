package client.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import client.Canvas;
import client.states.LoadingScreen;

/**
 * Class responsible for rendering the loading screen.
 * 
 * @author Dan Bryce
 */
public class LoadingScreenRenderer {

    private static final Color BAR_COLOUR = new Color(144, 192, 64);
    private static final Font LOADING_FONT = new Font("Helvetica", 0, 12);
    private static final int BAR_WIDTH = 277;
    private static final int BAR_HEIGHT = 20;
    private static final int OUTLINE_WIDTH = BAR_WIDTH + 3;
    private static final int OUTLINE_HEIGHT = BAR_HEIGHT + 3;

    public static void render(Graphics g, Canvas canvas,
            LoadingScreen loadingScreen) {

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // Draw loading bar
        int x = width / 2 - BAR_WIDTH / 2;
        int y = height / 2 - BAR_HEIGHT / 2;
        g.setColor(BAR_COLOUR);
        g.drawRect(x - 2, y - 2, OUTLINE_WIDTH, OUTLINE_HEIGHT);
        g.fillRect(x, y,
                (BAR_WIDTH * loadingScreen.getProgress()) / 100,
                BAR_HEIGHT);

        // Draw loading message
        g.setColor(Color.WHITE);
        drawString(g, loadingScreen.getMessage(), LOADING_FONT,
                x + BAR_WIDTH / 2,
                y + BAR_HEIGHT / 2);
    }

    private static void drawString(Graphics g, String s,
            Font font, int x, int y) {
        FontMetrics fontMetrics = g.getFontMetrics(font);
        fontMetrics.stringWidth(s);
        g.setFont(font);
        g.drawString(s,
                x - fontMetrics.stringWidth(s) / 2,
                y + fontMetrics.getHeight() / 4);
    }

}
