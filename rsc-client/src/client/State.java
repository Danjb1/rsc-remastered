package client;

public abstract class State {

    protected RsLauncher launcher;

    protected Input input = new Input();

    public State(RsLauncher launcher) {
        this.launcher = launcher;
    }

    public void pollInput() {}

    public void tick() {}

    public abstract StateRenderer getRenderer();

    public Input getInput() {
        return input;
    }

}
