package org.openrsc.model;

/**
 * Game constants.
 */
public class Constants {

    /**
     * The client software build. This is used to reject login requests from
     * out-dated clients.
     */
    public static final double CLIENT_BUILD = 0.1;

    /**
     * This is the maximum distance in which the client will project ray collisions.
     */
    public static final int MAXIMUM_INTERACTION_DISTANCE = 48;

    /**
     * The default spawn location.
     */
    public static final Location DEFAULT_LOCATION = new Location(3200, 3200);

}
