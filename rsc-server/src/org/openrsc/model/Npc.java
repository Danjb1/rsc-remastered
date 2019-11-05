package org.openrsc.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.openrsc.model.data.Resources;
import org.openrsc.model.player.Player;

/**
 * Represents a non-player game character.
 *
 */
public class Npc extends Mob {

    /**
     * A list of nearby entities.
     * The mob will only know about the existence of these entities.
     */
    private Set<Player> localPlayerList = null;
    private final Queue<Player> playerRemovalQueue = new LinkedList<>();
    private Set<Npc> localNpcList = null;
    private final Queue<Npc> npcRemovalQueue = new LinkedList<>();

    /**
     * The cache index for this npc.
     */
    private int type;

    /**
     * Where the Npc was spawned. Used for respawn, and retreating.
     */
    private Location origin;

    /**
     * How far the npc can walk from their origin location.
     */
    private int movementRadius = 0;

    /**
     * A retreat flag.
     */
    private boolean retreatToOrigin = false;

    public Npc(int sessionId, int type, int x, int z) {
        super(sessionId);
        this.origin = new Location(x, z);
        this.setTravelBack();

        // Initialize the npc
        this.init(type);
    }

    /**
     * Load the Npc definition and attach the data to the mob.
     */
    protected void init(int type) {
        this.type = type;
        setHealthMaximum(Resources.npcs[type].hits);
        setHealthCurrent(getHealthMaximum());
        setCombatLevel(1); // FIXME
    }

    /**
     * Transforms the npc into a new type.
     */
    public void transform(int type) {
        init(type);
    }

    @Override
    public void tick(final long currentTime, Set<Player> globalPlayerList, Set<Npc> globalNpcList) {
        this.updateLocalList(globalPlayerList, globalNpcList);
        // Npc is aggressive.
        // Loop through nearby players.
        // Find a low level player to attack.
        if (Resources.npcs[type].isAggressive()) {
            // TODO
        }
    }

    @Override
    public boolean isDead() {
        return false;
    }

    /**
     * Loop through each entity in the world and create a list of nearby entities.
     */
    private void updateLocalList(Set<Player> globalPlayerList, Set<Npc> globalNpcList) {
        boolean isLocal;

        // Check if an entity has been unregistered from the server.
        for (Player player : localPlayerList) {
            if (!globalPlayerList.contains(player)) {
                playerRemovalQueue.add(player);
                continue;
            }
        }

        // Check for new entities that should be added to the local list.
        for (Player player : globalPlayerList) {
            isLocal = getLocation().getDistance(player.getLocation()) < Constants.MAXIMUM_INTERACTION_DISTANCE;

            // Register a new entity.
            if (isLocal && !localPlayerList.contains(player)) {
                localPlayerList.add(player);
                continue;
            }

            // Unregister a entity.
            if (!isLocal && localPlayerList.contains(player)) {
                playerRemovalQueue.add(player);
                continue;
            }
        }

        // Merge the list changes.
        while (!playerRemovalQueue.isEmpty()) {
            localPlayerList.remove(playerRemovalQueue.poll());
        }


        // Check if an entity has been unregistered from the server.
        for (Npc npc : localNpcList) {
            if (!globalNpcList.contains(npc)) {
                npcRemovalQueue.add(npc);
                continue;
            }
        }

        // Check for new entities that should be added to the local list.
        for (Npc npc : globalNpcList) {
            isLocal = getLocation().getDistance(npc.getLocation()) < Constants.MAXIMUM_INTERACTION_DISTANCE;

            // Register a new entity.
            if (isLocal && !localNpcList.contains(npc)) {
                localNpcList.add(npc);
                continue;
            }

            // Unregister a entity.
            if (!isLocal && localNpcList.contains(npc)) {
                npcRemovalQueue.add(npc);
                continue;
            }
        }

        // Merge the list changes.
        while (!npcRemovalQueue.isEmpty()) {
            localNpcList.remove(npcRemovalQueue.poll());
        }
    }

    /**
     * The list of players available to this mob.
     */
    public Set<Player> getLocalPlayers() {
        return localPlayerList;
    }

    /**
     * The list of npc available to this mob.
     */
    public Set<Npc> getLocalNpcs() {
        return localNpcList;
    }

    public int getType() {
        return type;
    }

    /**
     * @return How far the Npc can walk from their origin location.
     */
    public int getMovementRadius() {
        return movementRadius;
    }

    /**
     * Limit the "AI" travel distance from the origin.
     *
     * @param movementRadius
     *            The maximum travel distance.
     */
    public void setMovementRadius(int movementRadius) {
        this.movementRadius = movementRadius;
    }

    /**
     * @return
     */
    public Location getOriginLocation() {
        return origin;
    }

    /**
     * Forces the Npc to retreat.
     */
    public void forceRetreat() {
        this.retreatToOrigin = true;
    }

    /**
     * @return True, if the Npc is retreating.
     */
    public boolean isRetreating() {
        return retreatToOrigin;
    }

    public int getLocalPlayerCount() {
        return localPlayerList.size();
    }

}