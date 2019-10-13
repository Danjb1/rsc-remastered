package client.packets;

import java.util.HashMap;
import java.util.Map;

import client.packets.deserializers.LoginSuccessPacketDeserializer;

public class PacketDeserializers {

    private static Map<Short, PacketDeserializer> registeredDeserializers =
            new HashMap<>();

    static {
        register(Packet.LOGIN_SUCCESS, new LoginSuccessPacketDeserializer());
    }

    private static void register(short id, PacketDeserializer serializer) {
        registeredDeserializers.put(id, serializer);
    }

    public static PacketDeserializer get(short id) {
        return registeredDeserializers.get(id);
    }

}
