package client.entityhandling.defs;

/**
 * Data relating to an inventory item that has been dropped.
 */
public class ItemDropDef {

    public int id;
    public int amount;
    public int weight;

    public int getID() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public int getWeight() {
        return weight;
    }

}