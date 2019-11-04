package org.openrsc;

import org.openrsc.model.World;
import org.openrsc.model.event.impl.GameTickTaskEvent;
import org.openrsc.net.Server;
import org.openrsc.net.packet.PacketManager;

/**
 * The main class of the RS-Remastered server.
 */
public class Main {

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        // Load world
        World.getInstance();

        // Load the packets.
        PacketManager.loadPackets();

        // Bind to network.
        try {
            Server.getInstance().bind();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Register the tasks / events.
        Server.getInstance().submitEvent(new GameTickTaskEvent());
    }

}
