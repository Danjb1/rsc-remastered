package server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketUtils {

    /**
     * Closes a Socket safely.
     *
     * @param socket
     */
    public static void close(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ServerSocket serverSocket) {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
