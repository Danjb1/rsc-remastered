package org.openrsc.model.net.packet;

import org.openrsc.model.player.Player;
import org.openrsc.net.packet.Packet;
import org.openrsc.net.packet.PacketHandler;

/**
 * There is no server reaction, no events, no response, etc. The client only
 * sends these packets to prevent an idle logout.
 */
public class SilentPacket implements PacketHandler {

    /**
     * Sent from the client once per second, if the user has touched a keyboard key
     * while the game window is in the foreground.
     */
    private final int KEYBOARD_TOUCH = 0;

    /**
     * Sent from the client once per second, if the mouse cursor has moved, or if a
     * mouse button has been clicked, while inside of the game window.
     */
    private final int MOUSE_MOVEMENT = 1;

    /**
     * Sent from client once per second, if the camera has moved.
     */
    private final int CAMERA_UPDATE = 2;

    @Override
    public void execute(Player player, Packet packet) {
        int resetIndex = packet.getByte();
        switch (resetIndex) {
        case KEYBOARD_TOUCH:
        case MOUSE_MOVEMENT:
        case CAMERA_UPDATE:
            break;
        }
    }

    @Override
    public boolean addToQueue() {
        return false;
    }

}