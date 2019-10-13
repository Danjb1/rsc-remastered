package server.net;

import java.io.IOException;

import server.packets.Packet;

public class SendPacketTask implements Runnable {

    private Client client;
    private Packet packet;

    public SendPacketTask(Client client, Packet packet) {
        this.client = client;
        this.packet = packet;
    }

    @Override
    public void run() {
        try {

            if (!client.isDead()) {
                client.send(packet);
            }

        } catch (IOException e) {
            e.printStackTrace();
            client.kill();
        }
    }

}
