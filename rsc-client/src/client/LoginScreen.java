package client;

public class LoginScreen {

    public static enum State {
        MAIN_MENU,
        NEW_USER_MENU,
        LOGIN_MENU
    }

    private State state = State.MAIN_MENU;

    public State getState() {
        return state;
    }

}
