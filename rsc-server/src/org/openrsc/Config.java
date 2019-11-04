package org.openrsc;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Engine configurations.
 */
public class Config {

    /**
     * The listener port.
     */
    public static final int SERVER_PORT = 7780;

    /**
     * The maximum number of connections per address.
     */
    public static final int CONNECTION_LIMIT = 3;

    /**
     * The Executor which will execute the boss threads.
     */
    public static final Executor NETTY_BOSS_EXECUTOR = Executors.newCachedThreadPool();

    /**
     * The Executor which will execute the I/O worker threads.
     */
    public static final Executor NETTY_WORK_EXECUTOR = Executors.newCachedThreadPool();

    /**
     * The maximum number of I/O worker threads
     */
    public static final int NETTY_MAXIMUM_WORKER_COUNT = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * The number of worker threads the task engine will use to process game
     * tasks/events.
     */
    public static final int TASK_ENGINE_THREAD_COUNT = 8;

    /**
     * Disconnect the user after {@value #IDLE_DISCONNECT} minutes if the client has
     * not been making action requests.
     */
    public static final int IDLE_DISCONNECT = 5;

    /**
     * The maximum server workload.
     */
    public static final int USER_LIMIT = 2000;

    /**
     * A game tick is a specified measurement of time in which queued events will be
     * executed (600 milliseconds).
     */
    public static final int TICK_RATE = 600;

    /**
     * A toggle for the A* path finder implementation. This is an approximation of
     * the distance between two points based on adding the horizontal distance and
     * vertical distances rather than computing the exact difference.
     */
    public static final boolean MANHATTAN_DISTANCE = true;

    /**
     * The public RSA key, used for encrypting passwords.
     */
    public static final String RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDlPjcLROnibGIGvRp4LuTyS6q7QwAFAyGwO5sqhBl1vL53QBydamS5HT7481OYhGYACjhqbNdylvq5N/wCjff4y24lp0wt42TeeI9iVtFqlFwbhRk4GEqpBoBwkDlcNi8OauooFvN3kTgMgQoN2g0q8dILNijKqZE52qpgZza6IwIDAQAB";

    /**
     * The private RSA key, used for decrypting passwords.
     */
    public static final String RSA_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOU+NwtE6eJsYga9Gngu5PJLqrtDAAUDIbA7myqEGXW8vndAHJ1qZLkdPvjzU5iEZgAKOGps13KW+rk3/AKN9/jLbiWnTC3jZN54j2JW0WqUXBuFGTgYSqkGgHCQOVw2Lw5q6igW83eROAyBCg3aDSrx0gs2KMqpkTnaqmBnNrojAgMBAAECgYALNC42g8i2gzuLPsTDaO9RlDck7dJbbFrCJTehLVGdOntIkr6TRvbjQTWpryK/yoC9scIuGWGyPQTF9qF/cnbS/1WFmifTmh4ZMAIsJ4QzTata3AGMqc7WqwiM30hi6zKedFFOvUg7PGynZ2F/U61E2OIwIqTrpOLuIikkQ6TIiQJBAPmq9rQk7qtISiG+fvj8yO7LKoRtBjF7Fq+jAVB3tlgV/EA15VXmaoY7jMdlTZgtLAZ6SYW2IPXe6uR/IfhVXuUCQQDrDqNEPjRvC0qPS1IB9+SN74fK2bLfQfx9c4qYxbU/FYCMZEBPKPu6B/Ei1TV+VKT9zXwWgfxHq/4ma/MAQpxnAkAvGf3pBn6webbintm9h5Mw2ctvqFHey+X/xLTexXb1L1CjnIdjqVC3ekyY4Ze9+eewYSm1vCKDwEZ4TTPPceuxAkEA0bbKla1GDLivOe+CaD0qDjRiG+pk+2mdArReOHVUgscFXLxo3/d5t300d9ZvlpmgZsy5ZD9uvOdHpjHzqQzFCwJAbNPvG+ZTo9gtC2kOjbTDEYNRsWT8ysM3VMZfVusMdvCxQmpRyWkJdu3e9OHVPLDOrFmiHMpY+gYgxBcdNvVa6w==";

}