package server.main;

import server.RuneServer;

public class RuneServerLauncher {

    public static void main(String[] args) {

        RuneServer server = null;

        try {

            server = new RuneServer();
            server.load();
            server.run();

        } catch (Exception e) {

            // Exit cleanly if an error occurs
            if (server != null) {
                server.kill();
            }

            e.printStackTrace();
        }
    }

}
