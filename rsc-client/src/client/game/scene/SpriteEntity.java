package client.game.scene;

/**
 * An Entity present in the game world, represented by a Sprite.
 *
 * @author Dan Bryce
 */
public class SpriteEntity {

    private int id;
    private int x;
    private int y;
    private int z;
    private int width;
    private int height;
    private int translateX;

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTranslateX() {
        return translateX;
    }

}
