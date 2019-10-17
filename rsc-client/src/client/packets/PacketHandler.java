package client.packets;

import client.game.Game;

public abstract class PacketHandler {

    public abstract void apply(Packet packetBeforeCast, Game game);

}
