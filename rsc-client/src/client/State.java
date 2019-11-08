package client;

public abstract class State {

    protected RuneClient client;

    protected Input input = new Input();

    public State(RuneClient launcher) {
        this.client = launcher;
    }

    public void start() {}

    public void destroy() {}

    public void pollInput() {}

    public void tick() {}

    public abstract StateRenderer getRenderer();

    public Input getInput() {
        return input;
    }

    public RuneClient getClient() {
        return client;
    }

}
