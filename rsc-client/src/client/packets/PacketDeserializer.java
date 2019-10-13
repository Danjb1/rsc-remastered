package client.packets;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class PacketDeserializer {

    public abstract Packet deserialize(DataInputStream in) throws IOException;

}
