package client.login;

import java.io.IOException;
import java.net.Socket;

import client.RuneClient;
import client.State;
import client.StateRenderer;
import client.game.Game;
import client.net.Connection;

public class LoginScreen extends State {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 7780;

    private LoginScreenRenderer renderer;

    public LoginScreen(RuneClient launcher) {
        super(launcher);

        renderer = new LoginScreenRenderer(this);
    }

    @Override
    public StateRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void pollInput() {
        if (input.wasLeftClickReleased()) {
            // For now, just connect to the server immediately
            connect(SERVER_ADDRESS, SERVER_PORT);
        }
    }

    private void connect(String address, int port) {

        Connection conn = null;

        try {
            Socket socket = new Socket(address, port);
            conn = new Connection(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (conn != null) {
            launcher.changeState(new Game(launcher, conn));
        }
    }

}
