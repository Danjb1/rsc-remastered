package org.openrsc.model;

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

	// FIXME Figure out is Dan has a plan for these variables.
	public int admin;
	public long nameLong;
	public String name;
	public int serverIndex;
	public int mobIntUnknown;
	public int x;
	public int z;
	public int type;
	public int stepCount;
	public int currentSprite;
	public int nextAnimation;
	public int movingStep;
	public int waypointCurrent;
	public int waypointsX[] = new int[10];
	public int waypointsZ[] = new int[10];
	public int animationCount[] = new int[12];
	public String lastMessage;
	public int lastMessageTimeout;
	public int anInt162;
	public int anInt163;
	public int damageTaken;
	public int healthCurrent;
	public int healthMax;
	public int combatTimer;
	public int level = -1;
	public int colourHairType;
	public int colourTopType;
	public int colourBottomType;
	public int colourSkinType;
	public int attackingCameraInt;
	public int attackingMobIndex;
	public int attackingNpcIndex;
	public int projectileRange;
	public boolean unusedBool;
	public int unusedInt = -1;
	public int anInt179;

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
	public abstract void tick(long currentTime);

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
	 * A unique id generated for the mob's current session.
	 * This value is incremental and does not get stored.
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
	 * @param x The new x coordinate.
	 * @param z The new z coordinate.
	 */
	public void setLocation(int x, int z) {
		this.setTravelBack();
		location.set(x, z);
	}

	/**
	 * Sets the entity's location.
	 * @param location The new location.
	 */
	public void setLocation(Location location) {
		setLocation(location.getX(), location.getZ());
	}

}
