package org.openrsc.model;

import java.util.Set;

import org.openrsc.model.player.Player;

/**
 * A dynamic mobile entity with combat attributes.
 */
public abstract class Mob {

    /**
     * A unique id generated for the mob's current session.
     */
    private final int sessionId;

    /**
     * The mob's current location.
     */
    private Location location;

    /**
     * The mob's last known location.
     */
    private Location travelBack = new Location(-1, -1);

    // Combat mob
    private Mob opponent = null;

    // Number of ticks before next attack.
    private int combatDelay = 0;

    // Current health.
    private int healthCurrent = 10;

    // Maximum health.
    private int healthMaximum = 10;

    // Combat level.
    private int combatLevel = 0;

    // Create appropriate replacement variables.
    public int currentSprite;// TODO
    public int nextAnimation;// TODO
    public int animationCount[] = new int[12];// TODO

    // TODO A* PATH FINDER
    public int stepCount;// TODO
    public int movingStep;// TODO
    public int waypointCurrent;// TODO
    public int waypointsX[] = new int[10];// TODO
    public int waypointsZ[] = new int[10];// TODO

    public Mob(int sessionId) {
        this(sessionId, 0, 0);
    }

    public Mob(int sessionId, int x, int z) {
        this.sessionId = sessionId;
        this.location = new Location(x, z);
        this.travelBack = new Location(x, z);
    }

    /**
     * We use this to keep the Mob's last known location.
     */
    public void setTravelBack() {
        travelBack.set(getX(), getZ());
    }

    /**
     * Executed every 600 ms.
     */
    public abstract void tick(final long currentTime, Set<Player> globalPlayerList, Set<Npc> globalNpcList);

    /**
     * @return True, if the mob is dead.
     */
    public abstract boolean isDead();

    public int getLastX() {
        return travelBack.getX();
    }

    public int getLastZ() {
        return travelBack.getZ();
    }

    /**
     * A unique id generated for the mob's current session. This value is
     * incremental and does not get stored.
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * The entity's location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * The x coordinate of the entity's location.
     */
    public int getX() {
        return location.getX();
    }

    /**
     * The z coordinate of the entity's location.
     */
    public int getZ() {
        return location.getZ();
    }

    /**
     * Sets the entity's location.
     *
     * @param x
     *            The new x coordinate.
     * @param z
     *            The new z coordinate.
     */
    public void setLocation(int x, int z) {
        this.setTravelBack();
        location.set(x, z);
    }

    /**
     * Sets the entity's location.
     *
     * @param location
     *            The new location.
     */
    public void setLocation(Location location) {
        setLocation(location.getX(), location.getZ());
    }

    public Mob getOpponent() {
        return opponent;
    }

    public int getCombatDelay() {
        return combatDelay;
    }

    public void setCombatDelay(int combatDelay) {
        this.combatDelay = combatDelay;
    }

    public int getHealthCurrent() {
        return healthCurrent;
    }

    public void setHealthCurrent(int healthCurrent) {
        this.healthCurrent = healthCurrent;
    }

    public int getHealthMaximum() {
        return healthMaximum;
    }

    public void setHealthMaximum(int healthMaximum) {
        this.healthMaximum = healthMaximum;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    public void setCombatLevel(int combatLevel) {
        this.combatLevel = combatLevel;
    }

    // TODO
    public int getProjectileRange() {
        return 0;
    }

}
