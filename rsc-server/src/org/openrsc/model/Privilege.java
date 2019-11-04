package org.openrsc.model;

/**
 * Represents the privileges of a user.
 */
public enum Privilege {

    /**
     * A regular user. No special privileges.
     */
    REGULAR(0),

    /**
     * A player moderator. Can kick, mute regular users.
     */
    MODERATOR(1),

    /**
     * A regular administrator. Can kick, mute, ban regular users or moderators.
     */
    ADMINISTRATOR(2),

    /**
     * A root administrator. Can kick, mute, ban, promote any type of user.
     */
    ROOT(3),

    /**
     * A github contributor. Has no special privileges, but gets a wrench icon near
     * their name in-game, as a special thanks for their contribution.
     */
    GITHUB_CONTRIBUTOR(4);

    /**
     * The integer representing this rights level.
     */
    private int value;

    /**
     */
    private Privilege(int value) {
        this.value = value;
    }

    /**
     */
    public int toInteger() {
        return value;
    }

    public static Privilege getForValue(int value) {
        return Privilege.values()[value];
    }

}
