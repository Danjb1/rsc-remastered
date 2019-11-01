package client.login;

import client.Canvas;
import client.StateRenderer;

/**
 * Class responsible for rendering the login screen.
 *
 * @author Dan Bryce
 */
public class LoginScreenRenderer extends StateRenderer {

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

    public LoginScreenRenderer(LoginScreen loginScreen) {}

    @Override
    public void render(Canvas canvas) {
        int x = canvas.getWidth() / 2
                - LOGO_SPRITE_WIDTH / 2
                - LOGO_SPRITE_OFFSET_X;
        canvas.drawSprite(x, 50, SPRITE_ID_LOGO);
    }

}
