package client.login;

import java.io.IOException;

import client.RuneClient;
import client.State;
import client.StateRenderer;
import client.game.Game;
import client.net.Connection;

public class LoginScreen extends State {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 43594;

    private LoginScreenRenderer renderer;

    public LoginScreen(RuneClient client) {
        super(client);

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

        try {
            Connection conn = new Connection(address, port);
            sendLoginPacket(conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendLoginPacket(Connection conn) {
        // TODO: send login details and check response
        Game game = new Game(client, conn);
        client.changeState(game);
        game.loggedIn();
    }

}
