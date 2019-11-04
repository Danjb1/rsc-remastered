package client.game.render;

import client.Canvas;
import client.game.Game;
import client.game.ui.Menu;

public class MenuBarRenderer {

    public static final int MENU_BAR_OFFSET_X = 200;
    public static final int MENU_BAR_OFFSET_Y = 3;

    private static final int SPRITE_ID_MENU_BAR = 2000;

    private Game game;

    public MenuBarRenderer(Game game) {
        this.game = game;
    }

    public void render(Canvas canvas) {

        int x = game.getClient().getWidth() - MENU_BAR_OFFSET_X;
        int y = MENU_BAR_OFFSET_Y;

        canvas.drawSprite(x, y, SPRITE_ID_MENU_BAR);

        for (Menu menu : game.getMenus()) {
            if (menu.isOpen()) {
                menu.getRenderer().render(canvas, x, y);
            }
        }
    }

}
