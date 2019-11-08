package client.packets.deserializers;

import java.io.DataInputStream;
import java.io.IOException;

import client.packets.Packet;
import client.packets.PacketDeserializer;
import client.packets.from_server.LoginSuccessPacket;

public class LoginSuccessPacketDeserializer extends PacketDeserializer {

    @Override
    public Packet deserialize(DataInputStream in) throws IOException {
        return new LoginSuccessPacket();
    }

}
