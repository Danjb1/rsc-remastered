package org.openrsc;

import org.openrsc.util.IniFile;

/**
 * Software configurations.
 */
public class Config {

    private static final Config INSTANCE = new Config();

    private IniFile ini = new IniFile("./config.ini");

    public Config() {
    }

    private final boolean LOW_POWER_MODE = ini.getBoolean("Server", "low_power_mode", false);

    /**
     * True, if the server should run using minimum power settings. Please note that
     * using low power mode will override networking & task engine thread
     * configurations, which will cause the server to run at bare minimum power,
     * thus resulting in bare minimum performance.
     * 
     * Low power mode is for developers who wish to host RSC-Remastered on a
     * Raspberry pi.
     */
    public boolean isLowPowerMode() {
        return LOW_POWER_MODE;
    }

    private final int SERVER_PORT = ini.getInt("Server", "server_port", 7780);

    /**
     * The client listener port.
     */
    public int serverPort() {
        return SERVER_PORT;
    }

    private final int CONNECTION_LIMIT = ini.getInt("Server", "connection_limit", 3);

    /**
     * The maximum number of connections per address.
     */
    public int connectionLimit() {
        return CONNECTION_LIMIT;
    }

    private final int TASK_ENGINE_THREAD_COUNT = ini.getInt("Server", "task_engine_thread_count",
            Runtime.getRuntime().availableProcessors() * 2);

    /**
     * The number of worker threads the task engine will use to process game
     * tasks/events. Default is available runtime processors * 2.
     * 
     * Note: This value will be overridden to run on a single thread if
     * {@link #isLowPowerMode()} is enabled.
     */
    public int taskEngineThreadCount() {
        return TASK_ENGINE_THREAD_COUNT;
    }

    private final int IDLE_DISCONNECT = ini.getInt("Server", "idle_disconnect", 5);

    /**
     * Disconnect the user after {@value #IDLE_DISCONNECT} minutes if the client has
     * not been making action requests.
     */
    public int idleDisconnect() {
        return IDLE_DISCONNECT;
    }

    private final int USER_LIMIT = ini.getInt("Server", "user_limit", 2000);

    /**
     * The maximum number of client connections supported by the server.
     */
    public int userLimit() {
        return USER_LIMIT;
    }

    /**
     * A game tick is a specified measurement of time in which queued events will be
     * executed (600 milliseconds).
     */
    private final int TICK_RATE = ini.getInt("Server", "tick_rate", 600);

    public int tickRate() {
        return TICK_RATE;
    }

    private final String RSA_PUBLIC_KEY = ini.getString("Server", "rsa_public_key", null);

    /**
     * The public RSA key, used for encrypting passwords.
     */
    public String rsaPublicKey() {
        return RSA_PUBLIC_KEY;
    }

    private final String RSA_PRIVATE_KEY = ini.getString("Server", "rsa_private_key", null);

    /**
     * The private RSA key, used for decrypting passwords.
     */
    public String rsaPrivateKey() {
        return RSA_PRIVATE_KEY;
    }

    public static Config get() {
        return INSTANCE;
    }

}