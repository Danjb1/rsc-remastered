package org.openrsc.model.net.packet;

import java.util.Random;

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

        // Testing the sound system.
        // Remove this code anytime.
        // Tells the client to play a random sound.
        player.getPacketDispatcher().sendSoundRequest(sounds[new Random().nextInt(sounds.length)]);
    }


    private static final String[] sounds = { "advance", "anvil", "chisel", "click", "closedoor",
            "coins", "takeobject", "victory", "combat1a", "combat1b", "combat2a",
            "combat2b", "combat3a", "combat3b", "cooking", "death", "dropobject", "eat",
            "filljug", "fish", "foundgem", "recharge", "underattack", "mechanical", "mine",
            "mix", "spellok", "opendoor", /* "out_of_ammo", */ "potato", "spellfail",
            "prayeroff", "prayeron", "prospect", "shoot", "retreat", "secretdoor" };
    
    @Override
    public boolean addToQueue() {
        return false;
    }

}