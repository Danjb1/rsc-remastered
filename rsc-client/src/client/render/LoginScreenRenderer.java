package client.render;

import java.awt.Graphics;

import client.Game;
import client.GamePanel;
import client.LoginScreen;
import client.LoginScreen.State;

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

    public static void render(GamePanel gamePanel, Graphics g, Game game,
            LoginScreen loginScreen) {

        LoginScreen.State state = loginScreen.getState();

        int x = gamePanel.getWidth() / 2 - LOGO_SPRITE_WIDTH / 2
                - LOGO_SPRITE_OFFSET_X;
        gamePanel.drawSprite(x, 50, SPRITE_ID_LOGO);

        if (state == State.MAIN_MENU) {
            // TODO
        } else if (state == State.NEW_USER_MENU) {
            // TODO
        } else if (state == State.LOGIN_MENU) {
            // TODO
        }
    }

}
