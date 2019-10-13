package client.packets;

import java.util.HashMap;
import java.util.Map;

public class PacketSerializers {

    private static Map<Short, PacketSerializer> registeredSerializers =
            new HashMap<>();

    static {
        // TODO: register serializers
    }

    private static void register(short id, PacketSerializer serializer) {
        registeredSerializers.put(id, serializer);
    }

    public static PacketSerializer get(short id) {
        return registeredSerializers.get(id);
    }

}
