package org.openrsc.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Manages the npc instances.
 */
public class NpcManager {

	private static final NpcManager INSTANCE = new NpcManager();

	/**
	 * An incremental value. Used to generate a unique id for each entity.
	 */
	private final AtomicInteger uid = new AtomicInteger(0);
	
	/**
	 * A list of registered entities.
	 */
	private final Set<Npc> currentList = new HashSet<Npc>();
	
	/**
	 * A list of entities which are pending insertion.
	 */
	private final Queue<Npc> insertQueue = new LinkedList<>();
	
	/**
	 * A list of entities which are pending removal.
	 */
	private final Queue<Npc> removeQueue = new LinkedList<>();

	public NpcManager() {
		Logger.getLogger(getClass().getName()).info("Initialized");
	}

	public void tick(long currentTime) {
		/*
		 * Execute the queued insertions.
		 */
		Npc queuedNpc = null;
		while (!insertQueue.isEmpty()) {
			queuedNpc = insertQueue.poll();
			currentList.add(queuedNpc);
			Logger.getLogger(getClass().getName()).info("Npc #" + queuedNpc.getSessionId() + " registered.");
		}

		/*
		 * Execute the queued removals.
		 */
		while (!removeQueue.isEmpty()) {
			queuedNpc = removeQueue.poll();
			currentList.remove(queuedNpc);
			Logger.getLogger(getClass().getName()).info("Npc #" + queuedNpc.getSessionId() + " unregistered.");
		}

		/*
		 * Execute the timer based game logic.
		 */
		for (Npc npc : currentList) {

			// Execute the tick update.
				npc.tick(currentTime);

		}

	}

	/**
	 * Creates a new npc instance.
	 * 
	 * @return The new npc instance.
	 */
	public Npc create(int type, int x, int z) {
		Npc npc = new Npc(type, uid.getAndIncrement(), x, z);
		return npc;
	}
	
	/**
	 * Adds a npc to the insertion queue.
	 * 
	 * @param npc
	 *            The npc to register.
	 */
	public void register(Npc npc) {
		insertQueue.add(npc);
	}

	/**
	 * Adds the npc into the removal queue.
	 * 
	 * @param npc
	 *            The npc to unregister.
	 */
	public void unregister(Npc npc) {
		removeQueue.add(npc);
	}

	/**
	 * @return True, if the list contains the given object.
	 */
	public boolean contains(Npc npc) {
		return currentList.contains(npc);
	}

	/**
	 * @return The npc with the matching <code>sessionId</code>.matching
	 *         <code>sessionId</code>.matching <code>sessionId</code>.
	 */
	public Npc getForSessionId(int sessionId) {
		for (Npc npc : currentList) {
			if (npc.getSessionId() == sessionId) {
				return npc;
			}
		}
		return null;
	}

	/**
	 * @return A lost of registered npcs. This does not include npcs from the
	 *         login queue.
	 */
	public Set<Npc> getList() {
		return currentList;
	}

	/**
	 * @return The number of users online.
	 */
	public int getCount() {
		return currentList.size();
	}

	@SuppressWarnings("unused")
	public void onShutdown() {
		for (Npc npc : currentList) {
		}
	}

	public static NpcManager getInstance() {
		return INSTANCE;
	}

}
