package org.openrsc.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.openrsc.model.player.Player;

/**
 * Represents a non-player game character.
 *
 */
public class Npc extends Mob {

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

	/**
	 * A list of players that are within drawing distance.
	 * The npc will only have information for these entities.
	 */
	private final Set<Player> playerList = new HashSet<>();
	private final Queue<Player> playerRemovalQueue = new LinkedList<>();

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
	}

	/**
	 * Transforms the npc into a new type.
	 */
	public void transform(int type) {
		init(type);
	}

	@Override
	public void tick(final long currentTime) {
	}
	
	@Override
	public boolean isDead() {
		return false;
	}

	/**
	 * Loop through each player in the world and create a list of players within
	 * render distance. This allows NPCs to acknowledge and interact with nearby players.
	 */
	public void updateLocalPlayerList(Set<Player> globalPlayerList) {

		// Check if a local player has logged out.
		for (Player player : playerList) {
			if (!globalPlayerList.contains(player)) {
				playerRemovalQueue.add(player);
				continue;
			}
		}

		// Check for new players that should be added to the local list.
		boolean isLocal;
		for (Player player : globalPlayerList) {
			isLocal = getLocation().getDistance(player.getLocation()) < Constants.MAXIMUM_INTERACTION_DISTANCE;

			// Register a new local player.
			if (isLocal && !playerList.contains(player)) {
				playerList.add(player);
				continue;
			}

			// Unregister a local player.
			if (!isLocal && playerList.contains(player)) {
				playerRemovalQueue.add(player);
				continue;
			}
		}

		// Merge the list changes.
		while (!playerRemovalQueue.isEmpty()) {
			playerList.remove(playerRemovalQueue.poll());
		}
	}

	/**
	 * The list of players which are within interaction distance of the npc.
	 */
	public Set<Player> getLocalPlayers() {
		return playerList;
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
	 * @param movementRadius The maximum travel distance.
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
		return playerList.size();
	}
	
}