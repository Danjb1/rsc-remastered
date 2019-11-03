package org.openrsc.model;

/**
 * Represents the privileges of a user.
 */
public enum Privilege {

	REGULAR(0),

	MODERATOR(1),

	ADMINISTRATOR(2),

	ROOT(3),

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
		switch (value) {
		case 0:
			return REGULAR;
		case 1:
			return MODERATOR;
		case 2:
			return ADMINISTRATOR;
		case 3:
			return ROOT;
		case 4:
			return GITHUB_CONTRIBUTOR;
		}
		return null;
	}

}
