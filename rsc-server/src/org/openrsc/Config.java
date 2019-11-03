package org.openrsc;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Engine configurations.
 */
public class Config {

	/**
	 * The listener port.
	 */
	public static final int SERVER_PORT = 7780;

	/**
	 * The maximum number of connections per address.
	 */
	public static final int CONNECTION_LIMIT = 3;

	/**
	 * The Executor which will execute the boss threads.
	 */
	public static final Executor NETTY_BOSS_EXECUTOR = Executors.newCachedThreadPool();

	/**
	 * The Executor which will execute the I/O worker threads.
	 */
	public static final Executor NETTY_WORK_EXECUTOR = Executors.newCachedThreadPool();

	/**
	 * The maximum number of I/O worker threads
	 */
	public static final int NETTY_MAXIMUM_WORKER_COUNT = Runtime.getRuntime().availableProcessors() * 2;

	/**
	 * The number of worker threads the task engine will use to process game tasks/events.
	 */
	public static final int TASK_ENGINE_THREAD_COUNT = 8;

	/**
	 * Disconnect the user after {@value #IDLE_DISCONNECT} minutes if the client has
	 * not been making action requests.
	 */
	public static final int IDLE_DISCONNECT = 5;

	/**
	 * The maximum server workload.
	 */
	public static final int USER_LIMIT = 2000;

	/**
	 * A game tick is a specified measurement of time in which queued events will be
	 * executed (600 milliseconds).
	 */
	public static final int TICK_RATE = 600;

	/**
	 * A toggle for the A* path finder implementation.
	 * This is an approximation of the distance between two points based on adding
	 * the horizontal distance and vertical distances rather than computing the
	 * exact difference.
	 */
	public static final boolean MANHATTAN_DISTANCE = true;

}