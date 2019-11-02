package org.openrsc.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.openrsc.Config;
import org.openrsc.model.PlayerManager;
import org.openrsc.model.net.GameLoginHandler;
import org.openrsc.model.player.Player;
import org.openrsc.net.packet.Packet;
import org.openrsc.net.packet.PacketHandler;
import org.openrsc.net.packet.PacketManager;
import org.openrsc.util.GameUtils;

/**
 * The <code>ConnectionHandler</code> handles incoming packet data.
 */
public class ConnectionHandler extends SimpleChannelHandler {

	private ArrayList<String> connections = new ArrayList<String>();

	/**
	 * The login decoder instance.
	 */
	private final GameLoginHandler loginDecoder;

	public ConnectionHandler() {
		this.loginDecoder = new GameLoginHandler();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
		Packet packet = (Packet) event.getMessage();
		if (packet == null) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Received null packet from client.");
			return;
		}
		final int opcode = packet.getOpcode();

		// Ping Packet
		if (opcode == 0) {
			ctx.getChannel().write(new Packet(0).putLong(GameUtils.getCurrentTimeMillis()));
			return;
		}

		// Login packet
		if (opcode == 1) {
			loginDecoder.execute(ctx.getChannel(), packet);
			return;
		}

		// Game packet
		Player player = ((Player) ctx.getChannel().getAttachment());
		if (player == null) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Received packet from null player.");
			return;
		}

		// Get the packet instance
		PacketHandler packetInstance = PacketManager.get(opcode);
		if (packetInstance == null) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "No packet found for " + opcode);
			return;
		}

		// Keep the player from idle logout.
		player.updateLastPacketReceivedTime();

		// Reactor-based packet
		PacketManager.get(opcode).execute(player, packet);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent event) {
		String address = ctx.getChannel().getRemoteAddress().toString().split(":")[0];
		int count = 1;
		for (int i = 0; i < connections.size(); i++) {
			if (connections.get(i).equalsIgnoreCase(address)) {
				count++;
			}
		}
		if (count > Config.CONNECTION_LIMIT) {
			ctx.getChannel().close();
			return;
		}
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Connection [" + count + "/" + Config.CONNECTION_LIMIT + "] accepted from " + address);
		connections.add(address);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent event) {
		Player player = (Player) ctx.getChannel().getAttachment();
		if (PlayerManager.getInstance().contains(player)) {
			PlayerManager.getInstance().queueLogout(player);
		}
		String address = ctx.getChannel().getRemoteAddress().toString().split(":")[0];
		if (connections.contains(address)) {
			connections.remove(address);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent event) {
		if (!(event.getCause() instanceof IOException)) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception caught: ", event.getCause());
			return;
		}
		Player player = (Player) ctx.getAttachment();
		if (PlayerManager.getInstance().contains(player)) {
			PlayerManager.getInstance().queueLogout(player);
		}
		String address = ctx.getChannel().getRemoteAddress().toString().split(":")[0];
		if (connections.contains(address)) {
			connections.remove(address);
		}
	}

}
