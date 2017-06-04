package client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import client.render.GameRenderer;
import client.render.LoadingScreenRenderer;
import client.render.LoginScreenRenderer;

public class GamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int COLOUR_BLACK = 0;

    private Game game;
    private int width, height;
    private int[] pixels;
    private BufferedImage image;

    public GamePanel(Game game, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        addMouseListener(game);
        addMouseMotionListener(game);

        pixels = new int[width * height];
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        LoadingScreen loadingScreen = game.getLoadingScreen();
        LoginScreen loginScreen = game.getLoginScreen();

        // Clear pixel data
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = COLOUR_BLACK;
        }

        // Draw loading screen
        if (loadingScreen != null) {
            LoadingScreenRenderer.render(g, game.getFrame(), loadingScreen);
            return;

        // Draw login screen
        } else if (loginScreen != null) {
            LoginScreenRenderer.render(this, g, game, loginScreen);

        // Draw game
        } else {
            GameRenderer.render(game, this);
        }
        
        // Draw pixel data to the screen
        image.setRGB(0, 0, width, height, pixels, 0, width);
        g.drawImage(image, 0, 0, null);
    }

    public void drawSprite(int x, int y, int id) {

        Sprite sprite = game.getSprite(id);

        if (sprite.hasDrawOffset()) {
            x += sprite.getDrawOffsetX();
            y += sprite.getDrawOffsetY();
        }

        int targetIndex = x + y * width;
        int sourceIndex = 0;
        int spriteHeight = sprite.getHeight();
        int spriteWidth = sprite.getWidth();
        int screenRowIncrement = width - spriteWidth;
        int spriteRowIncrement = 0;

        /*
         * Bounds checking.
         *
         * If part of the Sprite is offscreen, this ensures that we only draw
         * the visible part of the image. Attempting to draw the full image
         * would result in parts of the image wrapping onto the next row of
         * pixels.
         */

        if (y < 0) {
            spriteHeight += y;
            sourceIndex -= y * spriteWidth;
            targetIndex -= y * width;
            y = 0;
        }

        if (y + spriteHeight >= height) {
            spriteHeight -= ((y + spriteHeight) - height) + 1;
        }

        if (x < 0) {
            spriteWidth += x;
            sourceIndex -= x;
            targetIndex -= x;
            spriteRowIncrement -= x;
            screenRowIncrement -= x;
            x = 0;
        }

        if (x + spriteWidth >= width) {
            int adjustment = ((x + spriteWidth) - width) + 1;
            spriteWidth -= adjustment;
            spriteRowIncrement += adjustment;
            screenRowIncrement += adjustment;
        }

        if (spriteWidth <= 0 || spriteHeight <= 0) {
            return;
        }
        
        setPixels(pixels, sprite.getPixels(),
                sourceIndex, targetIndex,
                spriteWidth, spriteHeight,
                screenRowIncrement, spriteRowIncrement);
    }

    /**
     * Copies a block of pixels from the source to the target.
     *
     * @param target Target pixel data.
     * @param source Source pixel data.
     * @param sourceIndex Starting index for the source array.
     * @param targetIndex Starting index for the target array.
     * @param sourceWidth Width of the source image.
     * @param sourceHeight Height of the source image.
     * @param targetRowIncrement
     *      Value to add to the target index after each row is copied.
     * @param sourceRowIncrement
     *      Value to add to the source index after each row is copied.
     */
    private void setPixels(
            int target[], int source[],
            int sourceIndex, int targetIndex,
            int sourceWidth, int sourceHeight,
            int targetRowIncrement, int sourceRowIncrement) {

        /*
         * The original source code copied multiple pixels at a time inside the
         * loop body, presumably intended as some kind of optimisation. Here I
         * have favoured simplicity over efficiency.
         */
        for (int y = 0; y < sourceHeight; y++) {
            for (int x = 0; x < sourceWidth; x++) {

                int colour = source[sourceIndex];
                if (colour != 0) {
                    target[targetIndex] = colour;
                }

                sourceIndex++;
                targetIndex++;
            }

            targetIndex += targetRowIncrement;
            sourceIndex += sourceRowIncrement;
        }
    }

}
