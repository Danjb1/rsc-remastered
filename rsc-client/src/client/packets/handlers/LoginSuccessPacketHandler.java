package client.packets.handlers;

import client.game.Game;
import client.packets.Packet;
import client.packets.PacketHandler;
import client.packets.from_server.LoginSuccessPacket;

public class LoginSuccessPacketHandler extends PacketHandler {

    @Override
    public void apply(Packet packetBeforeCast, Game game) {
        LoginSuccessPacket p = (LoginSuccessPacket) packetBeforeCast;
        game.loggedIn();
    }

}
