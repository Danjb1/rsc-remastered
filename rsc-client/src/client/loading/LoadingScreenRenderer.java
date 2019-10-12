package client.loading;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import client.Canvas;
import client.StateRenderer;

/**
 * Class responsible for rendering the loading screen.
 *
 * @author Dan Bryce
 */
public class LoadingScreenRenderer extends StateRenderer {

    private static final Color BAR_COLOUR = new Color(144, 192, 64);
    private static final Font LOADING_FONT = new Font("Helvetica", 0, 12);
    private static final int BAR_WIDTH = 277;
    private static final int BAR_HEIGHT = 20;
    private static final int OUTLINE_WIDTH = BAR_WIDTH + 3;
    private static final int OUTLINE_HEIGHT = BAR_HEIGHT + 3;

    private LoadingScreen loadingScreen;

    public LoadingScreenRenderer(LoadingScreen loadingScreen) {
        this.loadingScreen = loadingScreen;
    }

    @Override
    public void render(Canvas canvas) {

        Graphics g = canvas.getImage().createGraphics();

        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw loading bar
        int x = canvas.getWidth() / 2 - BAR_WIDTH / 2;
        int y = canvas.getHeight() / 2 - BAR_HEIGHT / 2;
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
