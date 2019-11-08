package client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.packets.Packet;

public class Connection implements PacketListener {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private PacketReaderThread packetReaderThread;
    private List<Packet> packetsReceived = new ArrayList<>();
    private boolean closed;

    public Connection(String address, int port) throws IOException {

        socket = new Socket(address, port);

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        packetReaderThread = new PacketReaderThread(in, this);
    }

    public Runnable getPacketReaderThread() {
        return packetReaderThread;
    }

    @Override
    public void packetReceived(Packet p) {
        synchronized (packetsReceived) {
            packetsReceived.add(p);
        }
    }

    @Override
    public void packetReadError(IOException e) {
        packetReaderThread.stop();
        close();
    }

    public List<Packet> getPacketsReceived() {
        synchronized (packetsReceived) {
            List<Packet> packets = new ArrayList<Packet>(packetsReceived);
            packetsReceived.clear();
            return packets;
        }
    }

    public void close() {
        closed = true;
        SocketUtils.close(socket);
    }

    public boolean isClosed() {
        return closed;
    }

}
