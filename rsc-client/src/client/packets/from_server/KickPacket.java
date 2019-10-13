package client.packets.from_server;

import client.packets.Packet;

public class KickPacket extends Packet {

    public final byte reason;

    public KickPacket(byte reason) {
        super(Packet.KICK);

        this.reason = reason;
    }

}
