package server.packets;

import java.util.HashMap;
import java.util.Map;

import server.packets.serializers.LoginSuccessPacketSerializer;

public class PacketSerializers {

    private static Map<Short, PacketSerializer> registeredSerializers =
            new HashMap<>();

    static {
        register(Packet.LOGIN_SUCCESS, new LoginSuccessPacketSerializer());
    }

    private static void register(short id, PacketSerializer serializer) {
        registeredSerializers.put(id, serializer);
    }

    public static PacketSerializer get(short id) {
        return registeredSerializers.get(id);
    }

}
