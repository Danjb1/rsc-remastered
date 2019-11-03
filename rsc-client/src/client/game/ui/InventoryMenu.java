package client.game.ui;

import client.game.render.InventoryMenuRenderer;
import client.game.render.MenuRenderer;

public class InventoryMenu extends Menu {

    private static final int HEADER_SPRITE_ID = 2001;

    @Override
    protected MenuRenderer createRenderer() {
        return new InventoryMenuRenderer(this);
    }

    @Override
    public int getHeaderSpriteId() {
        return HEADER_SPRITE_ID;
    }

}
