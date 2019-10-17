package client.net;

import java.io.IOException;
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

}
