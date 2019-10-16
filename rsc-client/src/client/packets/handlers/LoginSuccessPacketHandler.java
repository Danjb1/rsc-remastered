package client.packets.handlers;

import client.packets.Packet;
import client.packets.PacketHandler;
import client.packets.from_server.LoginSuccessPacket;

public class LoginSuccessPacketHandler extends PacketHandler {

    @Override
    public void apply(Packet packetBeforeCast) {
        LoginSuccessPacket p = (LoginSuccessPacket) packetBeforeCast;
    }

}
