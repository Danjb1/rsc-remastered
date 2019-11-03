package client.game.ui;

import client.game.render.MapMenuRenderer;
import client.game.render.MenuRenderer;

public class MapMenu extends Menu {

    private static final int HEADER_SPRITE_ID = 2002;

    @Override
    protected MenuRenderer createRenderer() {
        return new MapMenuRenderer(this);
    }

    @Override
    public int getHeaderSpriteId() {
        return HEADER_SPRITE_ID;
    }

}
