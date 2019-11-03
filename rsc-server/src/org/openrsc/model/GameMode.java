package org.openrsc.model;

/**
 */
public enum GameMode {

	REGULAR(0),
	
	IRONMAN(1),
	
	HARDCORE_IRONMAN(2),
	
	ULTIMATE_IRONMAN(3);

	private int value;

	private GameMode(int value) {
		this.value = value;
	}

	/**
	 * @return An integer value representing the game mode.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value The game mode integer value.
	 * @return The game mode enum value.
	 */
	public static GameMode forValue(int value) {
		switch (value) {
		case 0:
			return REGULAR;
		case 1:
			return IRONMAN;
		case 2:
			return HARDCORE_IRONMAN;
		case 3:
			return ULTIMATE_IRONMAN;
		}
		return null;
	}

}
