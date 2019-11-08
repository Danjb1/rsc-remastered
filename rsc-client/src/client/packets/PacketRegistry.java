package client.packets;

import java.util.HashMap;
import java.util.Map;

import client.packets.deserializers.LoginSuccessPacketDeserializer;
import client.packets.handlers.LoginSuccessPacketHandler;

public class PacketRegistry {

    private static Map<Short, PacketSerializer> registeredSerializers =
            new HashMap<>();

    private static Map<Short, PacketDeserializer> registeredDeserializers =
            new HashMap<>();

    private static Map<Short, PacketHandler> registeredHandlers =
            new HashMap<>();

    static {
        registerIncomingPacket(Packet.LOGIN_SUCCESS,
                new LoginSuccessPacketDeserializer(),
                new LoginSuccessPacketHandler());
    }

    private static void registerOutgoingPacket(
            short id, PacketSerializer serializer) {
        registeredSerializers.put(id, serializer);
    }

    private static void registerIncomingPacket(
            short id, PacketDeserializer deserializer, PacketHandler handler) {
        registeredDeserializers.put(id, deserializer);
        registeredHandlers.put(id, handler);
    }

    public static PacketSerializer getSerializer(short id) {
        return registeredSerializers.get(id);
    }

    public static PacketDeserializer getDeserializer(short id) {
        return registeredDeserializers.get(id);
    }

    public static PacketHandler getPacketHandler(short id) {
        return registeredHandlers.get(id);
    }

}
