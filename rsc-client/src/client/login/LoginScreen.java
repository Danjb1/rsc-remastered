package client.login;

import client.RuneClient;
import client.State;
import client.StateRenderer;
import client.game.Game;

public class LoginScreen extends State {

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
            // Skip the login screen for now
            launcher.changeState(new Game(launcher));
        }
    }

}
