package org.openrsc.net.packet;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrsc.model.net.packet.*;
import org.openrsc.model.player.Player;

/**
 * The {@link PacketManager} class is the central hub for all packet based
 * logic, this class contains purely static methods that will handle the setup
 * and construction of each individual packet detected by the server.
 */
public class PacketManager {

    // An List<T> containing all of the packets detected by the server.
    private static Map<Integer, PacketHandler> packets = new HashMap<>();

    /**
     * Gets all of the packets that were detected by the server at launch.
     *
     * @return A List of {@link PacketHandler}'s
     */
    public static PacketHandler get(int opcode) {
        return packets.get(opcode);
    }

    public static void loadPackets() {
        // #0 = RSA Handshake
        // #1 = Ping
        // #2 = Login Request
        packets.put(10, new SectorUpdatePacket());
        packets.put(12, new SectorUpdatePacket());
        packets.put(100, new SilentPacket());
        Logger.getLogger(PacketManager.class.getName()).log(Level.INFO, "Loaded " + packets.size() + " packets.");
    }

    public static void execute(Player player, Packet packet) {
        packets.get(packet.getOpcode()).execute(player, packet);
    }

}