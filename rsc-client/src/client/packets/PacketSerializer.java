package client.packets;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class PacketSerializer {

    public abstract void serialize(
            DataOutputStream out, Packet packetBeforeCast) throws IOException;

}
