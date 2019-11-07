package org.openrsc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.openrsc.model.player.Player;

/**
 * Manages the player instances.
 */
public class PlayerManager {

    private static final PlayerManager INSTANCE = new PlayerManager();

    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * An incremental value. Used to generate a unique id for each entity.
     */
    private final AtomicInteger uid = new AtomicInteger(0);

    /**
     * A list of registered entities.
     */
    private final Set<Player> currentList = new HashSet<Player>();

    /**
     * A list of entities which are pending insertion.
     */
    private final Queue<Player> insertQueue = new LinkedList<>();

    /**
     * A list of entities which are pending removal.
     */
    private final Queue<Player> removeQueue = new LinkedList<>();

    private int tickCounter = 0;

    public PlayerManager() {
    }

    public void tick(Set<Player> cachedPlayerList, Set<Npc> cachedNpcList) {
        final long currentTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        
        tickCounter++;
        boolean oneMinuteTimeLapse = tickCounter % 100 == 0;

        /*
         * OpenRSC doesn't have a 'PID' design like Jagex does for RuneScape. Instead,
         * we shuffle the {@link #currentList} to make the order of execution
         * unpredictable.
         */
        if (oneMinuteTimeLapse) {
            List<Player> randomList = new ArrayList<>();
            randomList.addAll(currentList);
            Collections.shuffle(randomList, new Random());
            currentList.clear();
            currentList.addAll(randomList);

            tickCounter = 0; // Reset tick counter
        }

        /*
         * Execute the queued insertions.
         */
        Player queuedPlayer = null;
        while (!insertQueue.isEmpty()) {
            queuedPlayer = insertQueue.poll();
            currentList.add(queuedPlayer);
            queuedPlayer.executeLogin();
            logger.info(queuedPlayer.getDisplayName() + " registered.");
        }

        /*
         * Execute the queued removals.
         */
        while (!removeQueue.isEmpty()) {
            queuedPlayer = removeQueue.poll();
            currentList.remove(queuedPlayer);
            queuedPlayer.executeLogout();
            logger.info(queuedPlayer.getDisplayName() + " unregistered.");
        }

        /*
         * Execute the timer based game logic.
         */
        for (Player player : currentList) {

            // Execute the tick update.
            player.tick(currentTime, cachedPlayerList, cachedNpcList);

            // Check for disconnected clients.
            if (!player.getChannel().isConnected()) {
                logger.info(player.getDisplayName() + " disconnected.");
                removeQueue.add(player);
            }

        }

    }

    /**
     * Creates a new player instance.
     *
     * @return The new player instance.
     */
    public Player create(Channel channel, int databaseId, String displayName) {
        Player player = new Player(channel, databaseId, uid.getAndIncrement(), displayName);
        return player;
    }

    /**
     * Adds a player to the login queue.
     */
    public void queueLogin(Player player) {
        logger.log(Level.INFO, player.getDisplayName() + " added to login queue.");
        insertQueue.add(player);
    }

    /**
     * Adds the player into the logout queue.
     */
    public void queueLogout(Player player) {
        logger.log(Level.INFO, player.getDisplayName() + " added to logout queue.");
        removeQueue.add(player);
    }

    /**
     * @return True, if the list contains the given object.
     */
    public boolean contains(Player player) {
        return currentList.contains(player);
    }

    /**
     * @return The player with the matching <code>accountId</code>.
     */
    public Player getForAccountId(int accountId) {
        for (Player player : currentList) {
            if (player.getAccountId() == accountId) {
                return player;
            }
        }
        return null;
    }

    /**
     * @return The player with the matching <code>sessionId</code>.
     */
    public Player getForSessionId(int sessionId) {
        for (Player player : currentList) {
            if (player.getSessionId() == sessionId) {
                return player;
            }
        }
        return null;
    }

    /**
     * @return A list of registered players. This does not include players from the
     *         login queue.
     */
    public Set<Player> getList() {
        return currentList;
    }

    /**
     * @return The number of users online.
     */
    public int getCount() {
        return currentList.size();
    }

    public void onShutdown() {
        for (Player player : currentList) {
            player.executeLogout();
        }
    }

    /**
     * Sends a game message to all players.
     * @param string The message to send.
     */
    public void sendMessage(String string) {
        for (Player player : currentList) {
            player.getPacketDispatcher().sendGameMessage(string);
        }
    }

    // TODO Auto-generated method stub
    public boolean checkPassword(String username, String password) {
        logger.info("TODO");
        return true;
    }

    // TODO Auto-generated method stub
    private void loadGame(Player user) {
        logger.info("TODO");
    }

    // TODO Auto-generated method stub
    private void saveGame(Player user) {
        logger.info("TODO");
    }

    public static PlayerManager getInstance() {
        return INSTANCE;
    }

}
