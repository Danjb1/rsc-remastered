package client.entityhandling.defs;

/**
 * Data relating to an NPC.
 */
public class NpcDef extends EntityDef {

    public String command;

    public int[] sprites;

    public int hairColour;
    public int topColour;
    public int bottomColour;
    public int skinColour;

    public int camera1;
    public int camera2;

    public int walkModel;
    public int combatModel;
    public int combatSprite;

    public int hits;
    public int attack;
    public int defense;
    public int strength;

    public boolean attackable;
    public int respawnTime;
    public boolean aggressive;
    public ItemDropDef[] drops;

    public String getCommand() {
        return command;
    }

    public int getSprite(int index) {
        return sprites[index];
    }

    public int getHairColour() {
        return hairColour;
    }

    public int getTopColour() {
        return topColour;
    }

    public int getBottomColour() {
        return bottomColour;
    }

    public int getSkinColour() {
        return skinColour;
    }

    public int getCamera1() {
        return camera1;
    }

    public int getCamera2() {
        return camera2;
    }

    public int getWalkModel() {
        return walkModel;
    }

    public int getCombatModel() {
        return combatModel;
    }

    public int getCombatSprite() {
        return combatSprite;
    }

    public int getHitpoints() {
        return hits;
    }

    public int getAtt() {
        return attack;
    }

    public int getDef() {
        return defense;
    }

    public int getStr() {
        return strength;
    }

    public int[] getStats() {
        return new int[] { attack, defense, strength };
    }

    public boolean isAttackable() {
        return attackable;
    }

    public int respawnTime() {
        return respawnTime;
    }

    public boolean isAggressive() {
        return aggressive;
    }

}
