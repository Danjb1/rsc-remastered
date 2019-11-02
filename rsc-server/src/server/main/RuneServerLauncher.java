package server.main;

import java.util.logging.Logger;

import server.RuneServer;

public class RuneServerLauncher {

    public static void main(String[] args) {
        try {
        	Logger.getLogger(RuneServerLauncher.class.getName()).info("Testing");
        	
        	RuneServer server = new RuneServer();
            server.load();
        	Logger.getLogger(RuneServerLauncher.class.getName()).info("Finished");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
