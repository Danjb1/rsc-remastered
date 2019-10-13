package client.packets;

public abstract class Packet {

    // Server Packet IDs
    public static final short LOGIN_SUCCESS = 0;
    public static final short KICK = 1;

    public final short id;

    public Packet(short id) {
        this.id = id;
    }

}
