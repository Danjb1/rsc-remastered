package client.game.ui;

import client.game.render.MenuRenderer;
import client.game.render.StatsMenuRenderer;

public class StatsMenu extends Menu {

    private static final int HEADER_SPRITE_ID = 2001;

    @Override
    protected MenuRenderer createRenderer() {
        return new StatsMenuRenderer(this);
    }

    @Override
    public int getHeaderSpriteId() {
        return HEADER_SPRITE_ID;
    }

}
