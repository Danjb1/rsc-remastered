package client.net;

import java.io.IOException;

import client.packets.Packet;

public interface PacketListener {

    void packetReceived(Packet p);

    void packetReadError(IOException e);

}
