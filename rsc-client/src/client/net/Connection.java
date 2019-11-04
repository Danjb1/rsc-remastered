package client.net;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import client.RuneClient;

public class Connection extends SimpleChannelHandler implements ChannelPipelineFactory {

    private Logger logger = Logger.getLogger(getClass().getName());

    private final RuneClient client;
    private Channel channel;

    public Connection(final RuneClient client) {
        this.client = client;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();
        pipeline.addLast("decoder", new PacketDecoder());
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("handler", this);
        return pipeline;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Packet packet = (Packet) e.getMessage();
        if (packet == null) {
            return;
        }
        client.queuePacket(packet);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        if (e.getCause() instanceof ConnectException) {
            logger.log(Level.INFO, "Connection refused.", e);
            return;
        }
        logger.log(Level.WARNING, "Exception caught in network.", e.getCause());
    }

    public boolean connect(String hostname, int port) {
        try {
            ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                    Executors.newSingleThreadExecutor(), Executors.newCachedThreadPool(), 2));
            bootstrap.setPipelineFactory(this);
            channel = bootstrap.connect(new InetSocketAddress(hostname, port)).awaitUninterruptibly().getChannel();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error connecting to server.", e);
            return false;
        }
        return isConnected();
    }

    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
    }

    public boolean isConnected() {
        return channel != null ? channel.isConnected() : false;
    }

    /**
     * Sends a packet to the server.
     */
    public void sendPacket(Packet packet) {
        if (!channel.isConnected()) {
            logger.log(Level.WARNING, "Error sending packet #" + packet.getOpcode() + ". Not connected.");
            return;
        }
        channel.write(packet);
    }

}