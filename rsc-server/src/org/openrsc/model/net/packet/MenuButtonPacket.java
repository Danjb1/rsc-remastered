package org.openrsc.model.net.packet;

import org.openrsc.model.player.Player;
import org.openrsc.net.packet.Packet;
import org.openrsc.net.packet.PacketHandler;

/**
 * A client menu button has been clicked and is expecting a server-side
 * reaction. Use this packet to handle incoming "action buttons", i.e.:
 * the server would disconnect a client for clicking the logout button.
 */
public class MenuButtonPacket implements PacketHandler {

    @Override
    public void execute(Player player, Packet packet) {
        // Read the incoming data.
        int parentId = packet.getByte();
        int buttonId = packet.getSmallInt();

        // Send a debug message.
        player.getPacketDispatcher().sendGameMessage("Menu Button: " + parentId + ", " + buttonId);
    }

    @Override
    public boolean addToQueue() {
        return false;
    }

}