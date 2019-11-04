package org.openrsc.model.net.packet;

import org.openrsc.model.player.Player;
import org.openrsc.net.packet.Packet;
import org.openrsc.net.packet.PacketHandler;

/**
 * This is a temporary location update packet. It will be deleted whenever the
 * client-side mob support is complete.
 */
public class SectorUpdatePacket implements PacketHandler {

    @Override
    public void execute(Player player, Packet packet) {
        // Read the incoming data.
        int x = packet.getInt();
        int z = packet.getInt();
        int height = packet.getByte();
        
        // Apply the incoming data.
        player.getLocation().set(x, z);
        player.getLocation().setHeight(height);
        
        // Send a debug message.
        player.getPacketDispatcher().sendGameMessage("Sector Updated: " + x + "," + z + " (height = " + height + ")");
    }

    @Override
    public boolean addToQueue() {
        return false;
    }

}