package client.game.ui;

import client.game.render.FriendsMenuRenderer;
import client.game.render.MenuRenderer;

public class FriendsMenu extends Menu {

    private static final int HEADER_SPRITE_ID = 2005;

    @Override
    protected MenuRenderer createRenderer() {
        return new FriendsMenuRenderer(this);
    }

    @Override
    public int getHeaderSpriteId() {
        return HEADER_SPRITE_ID;
    }

}
