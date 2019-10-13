package server.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import server.packets.Packet;
import server.packets.PacketSerializer;
import server.packets.PacketSerializers;

public class Client {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Client(Socket socket) throws IOException {
        this.socket = socket;

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void kill() {
        SocketUtils.close(socket);
    }

    public boolean isDead() {
        return socket.isClosed();
    }

    public String getAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public void send(Packet packet) throws IOException {
        PacketSerializer packetSerializer = PacketSerializers.get(packet.id);
        if (packetSerializer != null) {
            packetSerializer.serialize(out, packet);
        }
    }

}
