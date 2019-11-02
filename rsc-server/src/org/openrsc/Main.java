package org.openrsc;

import java.util.logging.Logger;

import org.openrsc.model.NpcManager;
import org.openrsc.model.PlayerManager;
import org.openrsc.model.World;
import org.openrsc.model.event.EventManager;
import org.openrsc.model.event.impl.GameTickTaskEvent;
import org.openrsc.net.Server;
import org.openrsc.net.packet.PacketManager;
import org.openrsc.task.TaskEngine;

/**
 */
public class Main {

	private final TaskEngine taskEngine;
	private final EventManager eventManager;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
        // Load world
        World.getInstance();

		// Load the packets.
		PacketManager.loadPackets();

		// Initialize the main thread.
		this.taskEngine = new TaskEngine(getThreadCount());
		
		// Bind to network.
		Server server = new Server(taskEngine);
		try {
			server.bind();
		} catch (Exception e) {
			e.printStackTrace();
			taskEngine.stop();
		}

		// Initialize the event manager.
		this.eventManager = new EventManager(taskEngine);
		taskEngine.start();

		// Register the tasks / events.
		this.eventManager.submit(new GameTickTaskEvent());
	}

	private int getThreadCount() {
		int threadCount = Runtime.getRuntime().availableProcessors();
		if (threadCount < 2) {
			threadCount = 2;
		}
		if (threadCount > 4) {
			threadCount = 4;
		}
		return threadCount;
	}

	/**
	 * Saves the state of the world.
	 */
	public void onShutdown() {
		Logger.getLogger(getClass().getName()).info("Shutting down.");
		PlayerManager.getInstance().onShutdown();
		NpcManager.getInstance().onShutdown();
	}

}
