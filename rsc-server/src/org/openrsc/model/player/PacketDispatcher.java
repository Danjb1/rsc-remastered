package org.openrsc.model.player;

import org.openrsc.net.packet.Packet;

/**
 * Contains the outgoing packets. Each packet can be be given specific
 * parameters.
 */
public class PacketDispatcher {

    /**
     * The player who receives the outgoing packet.
     */
    private final Player player;

    public PacketDispatcher(final Player player) {
        this.player = player;
    }

    /**
     * Sends a message to the client, as well as the player's privilege value.
     * 
     * @param string
     *            The message.
     */
    public void sendPublicMessage(String string) {
        Packet packet = new Packet(3);
        packet.putByte(player.getPrivileges().toInteger());
        packet.putString(string);
        player.getChannel().write(packet);
    }

    /**
     * Sends a message to the client.
     * 
     * @param string
     *            The message.
     */
    public void sendGameMessage(String string) {
        Packet packet = new Packet(3);
        packet.putByte(-1);
        packet.putString(string);
        player.getChannel().write(packet);
    }

    public void sendSectorUpdate() {
        Packet packet = new Packet(10);
        packet.putInt(player.getLocation().getX());
        packet.putInt(player.getLocation().getZ());
        packet.putByte(player.getLocation().getHeight());
        player.getChannel().write(packet);
    }

    /**
     * Sends a sound request to the client.
     * 
     * @param sound
     *            The sound file name, minus the file extension, because all files
     *            end with .wav
     */
    public void sendSoundRequest(String sound) {
        Packet packet = new Packet(5);
        packet.putString(sound);
        player.getChannel().write(packet);
    }

}