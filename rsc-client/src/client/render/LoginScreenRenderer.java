package client.render;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import client.Canvas;
import client.RsLauncher;
import client.states.LoginScreen;

/**
 * Class responsible for rendering the login screen.
 * 
 * @author Dan Bryce
 */
public class LoginScreenRenderer {

    public static final int SPRITE_ID_LOGO = 2010;
    
    /**
     * Sprite offset of the Runescape logo.
     * 
     * This is hardcoded in XML.
     */
    public static final int LOGO_SPRITE_OFFSET_X = 22;
    
    /**
     * Width of the Runescape logo image.
     * 
     * Ideally we should detect this at runtime rather than hardcoding it.
     */
    public static final int LOGO_SPRITE_WIDTH = 438;

    private Canvas canvas;
    private BufferedImage image;

    public LoginScreenRenderer(LoginScreen loginScreen) {

        int width = RsLauncher.WINDOW_WIDTH;
        int height = RsLauncher.WINDOW_HEIGHT;
        
        canvas = new Canvas(width, height);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    
    public void render(Graphics g) {

        // First render to the canvas
        render(canvas);
        
        // Then copy the Canvas to an image
        image.setRGB(0, 0, image.getWidth(), image.getHeight(),
                canvas.getPixels(), 0, image.getWidth());
        
        // Finally, draw this image to the screen
        g.drawImage(image, 0, 0, null);
    }

    private void render(Canvas canvas) {
        canvas.clear();
        int x = canvas.getWidth() / 2 - LOGO_SPRITE_WIDTH / 2
                - LOGO_SPRITE_OFFSET_X;
        canvas.drawSprite(x, 50, SPRITE_ID_LOGO);
    }

}
