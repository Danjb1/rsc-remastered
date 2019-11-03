package client.game.ui;

import client.game.render.MagicMenuRenderer;
import client.game.render.MenuRenderer;

public class MagicMenu extends Menu {

    private static final int HEADER_SPRITE_ID = 2004;

    @Override
    protected MenuRenderer createRenderer() {
        return new MagicMenuRenderer(this);
    }

    @Override
    public int getHeaderSpriteId() {
        return HEADER_SPRITE_ID;
    }

}
