package com.plumcreektechnology.tala0_0;

/**
 * class that contains the data needed to place a request to the Google Places API
 * @author Devin Frenze
 * @author Nora Hayes
 *
 */
public class PlaceQuery {
	
	private double latitude;
	private double longitude;
	private String places;
	private int radius;
	
	/**
	 * constructs the PlaceQuery from the args
	 * @param latitude latitude current location
	 * @param longitude longitude of current location
	 * @param placesSpecification String of categories to check
	 * @param radius radius of proximity to use
	 */
	public PlaceQuery(double latitude, double longitude, String placesSpecification, int radius) {
		this.latitude=latitude;
		this.longitude=longitude;
		places = placesSpecification;
		this.radius = radius;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getPlaces() {
		return places;
	}
	public void setPlaces(String places) {
		this.places = places;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
}
