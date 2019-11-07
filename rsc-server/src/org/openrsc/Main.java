package org.openrsc;

import java.util.logging.Logger;

import org.openrsc.model.World;
import org.openrsc.model.event.impl.GameTickTaskEvent;
import org.openrsc.net.Server;
import org.openrsc.net.packet.PacketManager;

/**
 * The main class of the RS-Remastered server.
 */
public class Main {

    public static void main(String[] args) {
        Logger.getLogger(Main.class.getName()).info("Booting up..");
        new Main();
    }

    public Main() {
        Config.get();

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
