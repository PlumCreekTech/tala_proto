package com.plumcreektechnology.tala0_0;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * holds all relevant results obtained from parsing a google directions API request
 * @author Devin Frenze
 * @author Nora Hayes
 */
public class Directions {
	private String status; // status of request
	private ArrayList<Route> routes; // arraylist of route results
	
	/**
	 * constructs a Directions object from the API response
	 * @param json the returned JSON object from the API response
	 * @return the constructed Directions object
	 */
	static Directions jsonToDirections(JSONObject json) {
		try {
			Directions result = new Directions();
			result.setStatus(json.getString("status"));
			result.setRoutes(new ArrayList<Route>());
			JSONArray routes = json.getJSONArray("routes");
			for(int i=0; i<routes.length(); i++) {
				result.addRoute(Route.jsonToRoute(routes.getJSONObject(i)));
			}
			return result;
		} catch (JSONException ex) {
			Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ArrayList<Route> getRoutes() {
		return routes;
	}
	public void setRoutes(ArrayList<Route> routes) {
		this.routes = routes;
	}
	public void addRoute(Route route) {
		routes.add(route);
	}
	@Override
	public String toString() {
		return "Directions [status=" + status + ", routes=" + routes + "]";
	}
	
}