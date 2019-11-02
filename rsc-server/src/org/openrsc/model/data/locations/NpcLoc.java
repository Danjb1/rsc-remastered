package server.entityhandling.locs;

public class NpcLoc {
    /**
     * The id of the Npc
     */
    public int id;
    /**
     * The Npcs x coord
     */
    public int startX;
    /**
     * The Npcs min x coord
     */
    public int minX;
    /**
     * The Npcs max x coord
     */
    public int maxX;
    /**
     * The Npcs y coord
     */
    public int startY;
    /**
     * The Npcs min y coord
     */
    public int minY;
    /**
     * The Npcs max y coord
     */
    public int maxY;

    public int getId() {
        return id;
    }

    public int startX() {
        return startX;
    }

    public int minX() {
        return minX;
    }

    public int maxX() {
        return maxX;
    }

    public int startY() {
        return startY;
    }

    public int minY() {
        return minY;
    }

    public int maxY() {
        return maxY;
    }
}
