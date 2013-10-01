package com.plumcreektechnology.tala0_0;

import android.location.Location;

/**
 * class for holding data to submit for a google directions api request
 * @author Devin Frenze
 * @author Nora Hayes
 */
public class DirectionsQuery {
	
	private Location origin;
	private Location destination;
	private String mode;
	
	/**
	 * constructs a DirectionsQuery
	 * @param origin the origin of travel
	 * @param destination the destination of travel
	 * @param mode the mode of travel
	 */
	public DirectionsQuery(Location origin, Location destination, String mode) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.mode = mode;
	}
	
	/**
	 * constructs a DirectionsQuery by inserting default mode of travel
	 * @param origin the origin of travel
	 * @param destination the destination of travel
	 */
	public DirectionsQuery(Location origin, Location destination) {
		this(origin, destination, "driving");
	}
	
	public Location getOrigin() {
		return origin;
	}
	public void setOrigin(Location origin) {
		this.origin = origin;
	}
	public Location getDestination() {
		return destination;
	}
	public void setDestination(Location destination) {
		this.destination = destination;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
}
