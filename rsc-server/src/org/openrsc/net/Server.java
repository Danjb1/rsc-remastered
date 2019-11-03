package org.openrsc.net;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.openrsc.Config;
import org.openrsc.model.World;
import org.openrsc.model.event.Event;
import org.openrsc.model.event.EventManager;
import org.openrsc.net.codec.PacketDecoder;
import org.openrsc.net.codec.PacketEncoder;
import org.openrsc.task.TaskEngine;

/**
 * Integrates the Netty api into the OpenRSC project.
 * The server class is tied together with the task event and event manager.
 */
public class Server {

	private static final Server INSTANCE = new Server();

	/**
	 * Creates a new instance.
	 * Calling this constructor is same with calling NioServerSocketChannelFactory(Executor, Executor, int) with 2 * the number of available processors in the machine.
	 * The number of available processors is obtained by Runtime.availableProcessors().
	 * https://docs.jboss.org/netty/3.2/api/org/jboss/netty/channel/socket/nio/NioServerSocketChannelFactory.html
	 */
	private final ServerBootstrap SERVER_BOOTSTRAP = new ServerBootstrap(new NioServerSocketChannelFactory(Config.NETTY_BOSS_EXECUTOR, Config.NETTY_WORK_EXECUTOR, Config.NETTY_MAXIMUM_WORKER_COUNT));

	private final TaskEngine taskEngine;
	private final EventManager eventManager;

	public Server() {
		// Initialize the main thread.
		this.taskEngine = new TaskEngine();

		// Initialize the event manager.
		this.eventManager = new EventManager(taskEngine);
	}

	public void bind() throws Exception {
		Logger.getLogger(getClass().getName()).info("Binding to port " + Config.SERVER_PORT + "..");

		try {
			// The ChannelPipelineFactory creates a new ChannelPipeline for each new
			// Channel.
			SERVER_BOOTSTRAP.setPipelineFactory((new ChannelPipelineFactory() {
				@Override
				public ChannelPipeline getPipeline() throws Exception {
					ChannelPipeline pipeline = pipeline();
					/*
					 * Add the packet encoder/decoder network protocol.
					 */
					pipeline.addLast("decoder", new PacketDecoder());
					pipeline.addLast("encoder", new PacketEncoder());

					/*
					 * Handles or intercepts network data and forwards it to the next handler in a
					 * ChannelPipeline.
					 */
					pipeline.addLast("handler", new ConnectionHandler());
					return pipeline;
				}
			}));

			//https://en.wikipedia.org/wiki/Nagles_algorithm
			SERVER_BOOTSTRAP.setOption("tcpNoDelay", true);

			// Bind the server bootstrap to the specified port.
			SERVER_BOOTSTRAP.bind(new InetSocketAddress(Config.SERVER_PORT));
			Logger.getLogger(getClass().getName()).info("Server is ONLINE!");
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Startup aborted due to network error.", e);
			throw new Exception("Startup aborted due to network error.");
		}

		// The network bind was successful.
		// Start the task engine.
		taskEngine.start();
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	/**
	 * Submits a new event to the event manager.
	 * @param task
	 */
	public void submitEvent(Event event) {
		eventManager.submit(event);
	}

	public void shutdown(boolean error) {
		Logger.getLogger(getClass().getName()).info("Shutting down..");

		// Shutdown the network.
		SERVER_BOOTSTRAP.shutdown();

		World.getInstance().onShutdown();

		// Stop the task engine.
		taskEngine.stop();

		// Terminate the jvm.
		System.exit(error ? 1 : 0);
	}

	public static Server getInstance() {
		return INSTANCE;
	}

}
