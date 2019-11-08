package client.net;

import java.io.DataInputStream;
import java.io.IOException;

import client.packets.Packet;
import client.packets.PacketDeserializer;
import client.packets.PacketRegistry;

public class PacketReaderThread implements Runnable {

    private DataInputStream in;

    private PacketListener packetListener;

    private boolean stopped;

    public PacketReaderThread(
            DataInputStream in, PacketListener packetListener) {

        this.in = in;
        this.packetListener = packetListener;
    }

    @Override
    public void run() {
        while (!stopped) {
            try {

                short id = in.readShort();
                PacketDeserializer deserializer =
                        PacketRegistry.getDeserializer(id);

                if (deserializer != null) {
                    Packet p = deserializer.deserialize(in);
                    packetListener.packetReceived(p);
                }

            } catch (IOException e) {
                e.printStackTrace();
                packetListener.packetReadError(e);
            }
        }
    }

    public void stop() {
        stopped = true;
    }

}
