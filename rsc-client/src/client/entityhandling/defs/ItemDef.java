package client.entityhandling.defs;

/**
 * Data relating to an inventory item.
 */
public class ItemDef extends EntityDef {

    public String command;

    public int basePrice;

    public int sprite;

    public boolean stackable;

    public boolean wieldable;

    public int pictureMask;

    public String getCommand() {
        return command;
    }

    public int getSprite() {
        return sprite;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public boolean isStackable() {
        return stackable;
    }

    public boolean isWieldable() {
        return wieldable;
    }

    public int getPictureMask() {
        return pictureMask;
    }

}
