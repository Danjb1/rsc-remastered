package org.openrsc.model.net;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Random;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.openrsc.Config;
import org.openrsc.model.Constants;
import org.openrsc.model.PlayerManager;
import org.openrsc.model.Privilege;
import org.openrsc.model.player.Player;
import org.openrsc.net.packet.Packet;
import org.openrsc.util.GameUtils;

/**
 * Handles a client login request. Validates the account credentials and checks
 * for special world parameters, etc.
 */
public class GameLoginHandler {

    public GameLoginHandler() {
        // ..
    }

    private String usernameErrorReport = null;
    private String passwordErrorReport = null;

    public void execute(Channel channel, Packet packet) {

        // Check the client build.
        double clientBuild = packet.getDouble();

        // Read account credentials from the client.
        String username = packet.getString();

        // Read the secure password from the client.
        String password = packet.getString(); // TODO Use RSA

        // Invalid client build.
        if (clientBuild != Constants.CLIENT_BUILD) {
            sendLoginError(channel, "Please update your client");
            return;
        }

        // Server is full.
        if (PlayerManager.getInstance().getCount() >= Config.USER_LIMIT) {
            sendLoginError(channel, "Server is full");
            return;
        }

        // Username is not allowed.
        if (!isUsernameValid(username)) {
            sendLoginError(channel, usernameErrorReport);
            return;
        }

        // Password is not allowed.
        if (!isPasswordValid(password)) {
            sendLoginError(channel, passwordErrorReport);
            return;
        }

        // Account is not registered.
        if (!doesUserExist(username)) {
            sendLoginError(channel, "Account does not exist");
            return;
        }

        // Account is registered.
        // Get the database uuid.
        final int databaseId = new Random().nextInt(9999); // TODO load from database

        // Invalid username or password.
        if (!validateCredentials(databaseId, password)) {
            sendLoginError(channel, "Invalid username or password");
            return;
        }

        // The client's password is correct.
        // Get the display name.
        final String displayName = username; // TODO could be used for display name

        // Account is already logged in.
        if (isLoggedIn(databaseId)) {
            sendLoginError(channel, "Account is already logged in");
            return;
        }

        // Credentials are valid. Load the account privileges.
        Privilege privilege = getPrivilege(databaseId);
        // System.out.println("Login->privilege=" + privilege);

        // Account is banned.
        if (isBanned(databaseId)) {
            sendLoginError(channel, "Account is banned");
            return;
        }

        /*
         * Successful login.
         */

        // Create the player instance.
        final Player player = PlayerManager.getInstance().create(channel, databaseId, displayName);
        player.setPrivileges(privilege);

        // Send the login response to the client.
        Packet success = new Packet(2).putBoolean(true).putBase37(displayName).putInt(player.getSessionId())
                .putByte(player.getPrivileges().toInteger());
        channel.write(success).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                // Register the player.
                PlayerManager.getInstance().queueLogin(player);
            }
        });

    }

    /**
     * The login request has been denied. Tell the client which error, then close
     * the connection.
     */
    private void sendLoginError(Channel channel, String message) {
        Packet packet = new Packet(2).putBoolean(false).putString(message);
        channel.write(packet).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                channel.close();
            }
        });
    }

    /**
     * Checks if the username String is valid.
     */
    private boolean isUsernameValid(String username) {
        username = username.replaceAll("_", " ");
        CharacterIterator iterator = new StringCharacterIterator(username);
        for (char ch = iterator.first(); ch != CharacterIterator.DONE; ch = iterator.next()) {
            if (!GameUtils.isValidCharacter(ch)) {
                usernameErrorReport = "Username can only contain a-z 0-9.";
                return false;
            }
        }
        if (username.startsWith(" ")) {
            usernameErrorReport = "Username cannot start with spaces.";
            return false;
        }
        if (username.endsWith(" ")) {
            usernameErrorReport = "Username cannot end with spaces.";
            return false;
        }
        if (username.contains("  ")) {
            usernameErrorReport = "Cannot contain back to back spaces.";
            return false;
        }
        if (username.length() < 3) {
            usernameErrorReport = "Username too short.";
            return false;
        }
        if (username.length() > 12) {
            usernameErrorReport = "Username too long.";
            return false;
        }
        return true;
    }

    /**
     * Checks if the password String is valid.
     */
    private boolean isPasswordValid(String password) {
        CharacterIterator iterator = new StringCharacterIterator(password);
        for (char ch = iterator.first(); ch != CharacterIterator.DONE; ch = iterator.next()) {
            if (!GameUtils.isValidCharacter(ch)) {
                passwordErrorReport = "Password contains invalid characters.";
                return false;
            }
        }
        if (password.length() < 5) {
            passwordErrorReport = "Password too short.";
            return false;
        }
        if (password.length() > 25) {
            passwordErrorReport = "Password too long.";
            return false;
        }
        return true;
    }

    // Check if account has completed registration.
    private boolean doesUserExist(String username) {
        // TODO Auto-generated method stub
        return true;
    }

    // Check if username password combination is valid.
    private boolean validateCredentials(int databaseId, String password) {
        // TODO Auto-generated method stub
        return true;
    }

    // Check if user is already logged in.
    private boolean isLoggedIn(int databaseId) {
        boolean connected = PlayerManager.getInstance().getForAccountId(databaseId) != null;

        // TODO : Check if player is logged in to a different server
        return connected;
    }

    // Check if account is banned.
    private boolean isBanned(int databaseId) {
        // TODO Auto-generated method stub
        return false;
    }

    // Get user privilege.
    private Privilege getPrivilege(int databaseId) {
        // TODO Auto-generated method stub
        return Privilege.GITHUB_CONTRIBUTOR;
    }

}