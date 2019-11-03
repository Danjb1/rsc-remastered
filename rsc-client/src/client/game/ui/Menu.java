package client.game.ui;

import client.game.render.MenuRenderer;

public abstract class Menu {

    private boolean open;

    private MenuRenderer renderer;

    public Menu() {
        renderer = createRenderer();
    }

    protected abstract MenuRenderer createRenderer();

    public abstract int getHeaderSpriteId();

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public MenuRenderer getRenderer() {
        return renderer;
    }

}
