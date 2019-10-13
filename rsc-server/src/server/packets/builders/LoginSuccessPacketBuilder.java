package server.packets.builders;

import server.packets.Packet;
import server.packets.from_server.LoginSuccessPacket;

public class LoginSuccessPacketBuilder extends PacketBuilder {

    @Override
    public Packet build() {
        return new LoginSuccessPacket();
    }

}
