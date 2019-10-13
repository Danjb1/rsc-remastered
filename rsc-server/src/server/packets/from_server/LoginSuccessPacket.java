package server.packets.from_server;

import server.packets.Packet;

public class LoginSuccessPacket extends Packet {

    public LoginSuccessPacket() {
        super(Packet.LOGIN_SUCCESS);
    }

}
