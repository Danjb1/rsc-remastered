package org.openrsc.model;

import org.openrsc.model.player.Player;

/**
 * Handles the execution of commands sent from the client.
 */
public class CommandHandler {

    public static void execute(Player player, String command) throws Exception {
        command = command.replaceFirst("/", "");

        // Check if the command includes multiple requests.
        if (command.contains("&")) {
            String[] commands = command.replaceAll(" & ", "&").split("&");
            for (int i = 0; i < commands.length; i++) {
                execute(player, commands[i]);
            }
            return;
        }

        // /yell command
        if (command.startsWith("yell") && !player.isMuted()) {
            if (player.hasYellThrottle()) {
                player.getPacketDispatcher().sendGameMessage("You can only yell once every 15 seconds.");
                return;
            }
            PlayerManager.getInstance().sendMessage("[Yell]" + player.getDisplayName() + ": " + command.substring(5));
            return;
        }

    }

}
