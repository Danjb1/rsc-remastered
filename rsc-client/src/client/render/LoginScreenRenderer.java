package client.render;

import java.awt.Graphics;

import client.Canvas;
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

    public static void render(Canvas canvas, Graphics g, 
            LoginScreen loginScreen) {

        int x = canvas.getWidth() / 2 - LOGO_SPRITE_WIDTH / 2
                - LOGO_SPRITE_OFFSET_X;
        canvas.drawSprite(x, 50, SPRITE_ID_LOGO);
    }

}
