package client.entityhandling.defs;

/**
 * Data relating to a tile of the game world.
 */
public class TileDef {

    public static final int TYPE_BRIDGE = 4;

    public int colour;
    public int unknown;
    public int objectType;

    public int getColour() {
        return colour;
    }

    public int getType() {
        return unknown;
    }

    public int getObjectType() {
        return objectType;
    }

}