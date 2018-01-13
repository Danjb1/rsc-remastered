package client.states;

import java.awt.Graphics;

import client.Canvas;
import client.State;
import client.render.LoginScreenRenderer;

public class LoginScreen extends State {

    @Override
    public void render(Canvas canvas, Graphics g) {
        LoginScreenRenderer.render(canvas, g, this);
    }

}
