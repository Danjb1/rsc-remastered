package client.packets.from_server;

import client.packets.Packet;

public class LoginSuccessPacket extends Packet {

    public LoginSuccessPacket() {
        super(Packet.LOGIN_SUCCESS);
    }

}
