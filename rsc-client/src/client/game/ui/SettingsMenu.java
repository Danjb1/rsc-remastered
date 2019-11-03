package client.game.ui;

import client.game.render.MenuRenderer;
import client.game.render.SettingsMenuRenderer;

public class SettingsMenu extends Menu {

    private static final int HEADER_SPRITE_ID = 2006;

    @Override
    protected MenuRenderer createRenderer() {
        return new SettingsMenuRenderer(this);
    }

    @Override
    public int getHeaderSpriteId() {
        return HEADER_SPRITE_ID;
    }

}
