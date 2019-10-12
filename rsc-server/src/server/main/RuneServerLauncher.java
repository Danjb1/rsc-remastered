package server.main;

import server.RuneServer;

public class RuneServerLauncher {

    public static void main(String[] args) {
        RuneServer server = new RuneServer();
        server.load();
        server.run();
    }

}
