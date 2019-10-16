package client.net;

import java.io.DataInputStream;
import java.io.IOException;

import client.packets.Packet;
import client.packets.PacketDeserializer;
import client.packets.PacketDeserializers;
import client.packets.PacketHandler;
import client.packets.PacketHandlers;

public class PacketReaderThread implements Runnable {

    private DataInputStream in;

    private boolean closed;

    public PacketReaderThread(DataInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        while (!closed) {
            try {

                short id = in.readShort();
                PacketDeserializer deserializer = PacketDeserializers.get(id);

                if (deserializer != null) {

                    Packet p = deserializer.deserialize(in);

                    PacketHandler handler = PacketHandlers.get(id);
                    if (handler != null) {
                        handler.apply(p);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
