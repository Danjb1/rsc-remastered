package org.openrsc.model.data.locations;

/**
 * Represents a static ground item spawn.
 */
public class ItemLoc {
    /**
     * The id of the gameObject
     */
    public int id;
    /**
     * The objects x coord
     */
    public int x;
    /**
     * The objects y coord
     */
    public int y;
    /**
     * Amount of item (stackables)
     */
    public int amount;
    /**
     * How long the item takes to spawn
     */
    public int respawnTime;

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAmount() {
        return amount;
    }

    public int getRespawnTime() {
        return respawnTime;
    }
}
