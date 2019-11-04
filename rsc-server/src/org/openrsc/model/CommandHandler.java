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
                player.getPacketDispatcher()
                        .sendMessage("You can only yell once every " + Constants.YELL_COMMAND_DELAY + " seconds.");
                return;
            }
            // PlayerManager.getInstance().sendGlobalMessage("[Yell]" +
            // player.getDisplayName() + ": " + command.substring(5));
            return;
        }

    }

}
