package client;

public abstract class State {

    protected RuneClient launcher;

    protected Input input = new Input();

    public State(RuneClient launcher) {
        this.launcher = launcher;
    }

    public void start() {}

    public void pollInput() {}

    public void tick() {}

    public abstract StateRenderer getRenderer();

    public Input getInput() {
        return input;
    }

    public RuneClient getLauncher() {
        return launcher;
    }

}
