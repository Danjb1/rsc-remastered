package org.openrsc.model.player;

import org.openrsc.net.packet.Packet;

/**
 * Contains the outgoing packets. Each packet can be be given specific parameters.
 */
public class PacketDispatcher {

	/**
	 * The player who receives the outgoing packet.
	 */
	private final Player player;

	public PacketDispatcher(final Player player) {
		this.player = player;
	}

	public void sendMessage(String string) {
		Packet packet = new Packet(3);
		packet.putByte(player.getPrivileges().toInteger());
		packet.putString(string);
		player.getChannel().write(packet);
	}

}