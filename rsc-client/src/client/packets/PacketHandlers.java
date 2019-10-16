package client.packets;

import java.util.HashMap;
import java.util.Map;

import client.packets.handlers.LoginSuccessPacketHandler;

public class PacketHandlers {

    private static Map<Short, PacketHandler> registeredHandlers =
            new HashMap<>();

    static {
        register(Packet.LOGIN_SUCCESS, new LoginSuccessPacketHandler());
    }

    private static void register(short id, PacketHandler serializer) {
        registeredHandlers.put(id, serializer);
    }

    public static PacketHandler get(short id) {
        return registeredHandlers.get(id);
    }

}
