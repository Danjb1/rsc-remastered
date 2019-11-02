package org.openrsc.net;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.openrsc.Config;
import org.openrsc.net.codec.PacketDecoder;
import org.openrsc.net.codec.PacketEncoder;
import org.openrsc.task.TaskEngine;

/**
 * The main class of the OpenRSC Server.
 */
public class Server {

	private final Executor BOSS_EXECUTOR = Executors.newCachedThreadPool(); // The boss thread.
	private final Executor WORK_EXECUTOR = Executors.newFixedThreadPool(10); // The I/O worker threads.

	/**
	 * Creates a new instance.
	 * Calling this constructor is same with calling NioServerSocketChannelFactory(Executor, Executor, int) with 2 * the number of available processors in the machine.
	 * The number of available processors is obtained by Runtime.availableProcessors().
	 * https://docs.jboss.org/netty/3.2/api/org/jboss/netty/channel/socket/nio/NioServerSocketChannelFactory.html
	 */
	private final ServerBootstrap SERVER_BOOTSTRAP = new ServerBootstrap(new NioServerSocketChannelFactory(BOSS_EXECUTOR, WORK_EXECUTOR));

	private final TaskEngine taskEngine;
	
	public Server(TaskEngine taskEngine) {
		this.taskEngine = taskEngine;
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
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Startup aborted due to network error.", e);
			throw new Exception("Startup aborted due to network error.");
			//return;
		}
		
		Logger.getLogger(getClass().getName()).info("Server is ONLINE!");
	}

	public void shutdown(boolean error) {
		// Shutdown the network.
		SERVER_BOOTSTRAP.shutdown();

		// Stop the task engine.
		taskEngine.stop();
		
		// Terminate the jvm.
		System.exit(error ? 1 : 0);
	}

}
