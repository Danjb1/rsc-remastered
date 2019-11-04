package org.openrsc.net.packet;

import org.openrsc.model.player.Player;

/**
 */
public interface PacketHandler {

    /**
     * Executes the packet.
     *
     * @param player
     *            The player that sent the packet.
     * @param packet
     *            The packet to execute.
     */
    public void execute(Player player, Packet packet);

    /**
     * @return True, if the packet execution should be queued until the next game
     *         tick.
     */
    public boolean addToQueue();

}
