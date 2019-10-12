package client.main;

import client.RuneClient;

public class RuneClientLauncher {

    public static void main(String[] args) {
        RuneClient rs = new RuneClient();
        rs.load();
        rs.run();
    }

}
