package client.game.render;

import client.Canvas;
import client.game.ui.Menu;

public abstract class MenuRenderer {

    private static final int MENU_HEADER_OFFSET_X = -48;

    protected Menu menu;

    public MenuRenderer(Menu menu) {
        this.menu = menu;
    }

    public void render(Canvas canvas, int x, int y) {
        renderHeader(canvas, x, y);
        renderContents(canvas, x, y);
    }

    private void renderHeader(Canvas canvas, int x, int y) {
        canvas.drawSprite(
                x + MENU_HEADER_OFFSET_X,
                y,
                menu.getHeaderSpriteId());
    }

    protected abstract void renderContents(Canvas canvas, int x, int y);

}
