package client;

import java.awt.Graphics;

public abstract class State {
    
    protected RsLauncher launcher;
    
    protected Input input = new Input();

    public State(RsLauncher launcher) {
        this.launcher = launcher;
    }
    
    public void pollInput() {}

    public void tick() {}

    public abstract void render(Graphics g);

    public Input getInput() {
        return input;
    }

}
