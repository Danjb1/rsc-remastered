package client.entityhandling.defs;

/**
 * Data relating to a door.
 */
public class DoorDef extends EntityDef {

    public String command1;

    public String command2;

    public int doorType;

    public int unknown;

    public int modelVar1;
    public int modelVar2;
    public int modelVar3;

    public String getCommand1() {
        return command1.toLowerCase();
    }

    public String getCommand2() {
        return command2.toLowerCase();
    }

    public int getDoorType() {
        return doorType;
    }

    public int getUnknown() {
        return unknown;
    }

    public int getHeight() {
        return modelVar1;
    }

    public int getFrontTexture() {
        return modelVar2;
    }

    public int getBackTexture() {
        return modelVar3;
    }

}
