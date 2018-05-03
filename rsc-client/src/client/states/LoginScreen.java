package client.states;

import java.awt.Graphics;

import client.RsLauncher;
import client.State;
import client.render.LoginScreenRenderer;

public class LoginScreen extends State {

    private LoginScreenRenderer renderer;

    public LoginScreen(RsLauncher launcher) {
        super(launcher);
        
        renderer = new LoginScreenRenderer(this);
    }
    
    @Override
    public void render(Graphics g) {
        renderer.render(g);
    }

    @Override
    public void pollInput() {
        if (input.wasLeftClickReleased()) {
            // Skip the login screen for now
            launcher.changeState(new Game(launcher));
        }
    }
    
}
