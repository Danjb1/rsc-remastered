package server.packets.serializers;

import java.io.DataOutputStream;
import java.io.IOException;

import server.packets.Packet;
import server.packets.PacketSerializer;
import server.packets.from_server.LoginSuccessPacket;

public class LoginSuccessPacketSerializer extends PacketSerializer {

    @Override
    public void serialize(DataOutputStream out, Packet packetBeforeCast)
            throws IOException {
        LoginSuccessPacket p = (LoginSuccessPacket) packetBeforeCast;
        out.writeShort(p.id);
    }

}
