package org.openrsc.model;

public class Location {

	private int x;
	private int z;

	/**
	 * Creates a location instance at 0,0
	 */
	public Location() {
		this(0, 0);
	}

	/**
	 * Creates a location instance at <code>x</code>,<code>z</code>
	 */
	public Location(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public void set(int x, int z) {
		setX(x);
		setZ(z);
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public int getDistance(Location location) {
		return Math.abs(location.getX() - location.getX()) + Math.abs(location.getZ() - location.getZ());
	}

	public boolean inBounds(Location location, int distance) {
		return getDistance(location) <= distance;
	}


}