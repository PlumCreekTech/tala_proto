package com.plumcreektechnology.tala0_0;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * an asynchronous task to get Directions from Google Directions API
 * @author Devin Frenze
 * @author Nora Hayes
 */
public class GetDirections extends AsyncTask<DirectionsQuery, Void, Route>
		implements Tala_Constants {
	
	Handler UIhandler;

	private String TAG = getClass().getName();
	
	/**
	 * constructor takes a handler for passing data back to instantiater
	 * @param UIhandler a Handler object used to maintain communication with asynchronous tasks
	 */
	public GetDirections(Handler UIhandler){
		this.UIhandler = UIhandler;
	}

	/**
	 * do in background sequences the other methods, is the main method for the request
	 */
	@Override
	protected Route doInBackground(DirectionsQuery... params) {
		DirectionsQuery dq = params[0];
		String urlString = makeUrl(dq.getOrigin(), dq.getDestination(), dq.getMode());
		Log.d(TAG, "url string is ::: "+urlString);
		try {
			String json = getJSON(urlString);
			Log.d(TAG, "JSON ::: "+json);
			JSONObject object = new JSONObject(json);
			JSONArray routes = object.getJSONArray("routes");
			Route route = Route.jsonToRoute(routes.getJSONObject(0));
			return route;
		} catch (JSONException ex) {
			Logger.getLogger(LocationService.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return null;
	}

	/**
	 * after the task executes it uses the handler to communicate with the instantiater
	 */
	@Override
	protected void onPostExecute(Route route) {
		Log.d(TAG, route.toString());
		ArrayList<Step> steps = route.getLeg().getSteps();
		String instructions = "";
		for(int i=0; i<steps.size(); i++){
			instructions += steps.get(i).getInstructions();
		}
		
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putString("instructions", instructions);
		data.putString("end address", route.getLeg().getEndAddress());
		msg.setData(data);
		UIhandler.dispatchMessage(msg);
	}

	/**
	 * constructs the properly formatted URL from the given arguments
	 * @param origin the origin of travel
	 * @param destination the destination of travel
	 * @param mode the mode of travel
	 * @return String the properly formatted URL
	 */
	// https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
	private String makeUrl(Location origin, Location destination, String mode) {
		StringBuilder urlString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/directions/json?");
		urlString.append("origin=" + origin.getLatitude());
		urlString.append("," + origin.getLongitude());
		urlString.append("&destination=" + destination.getLatitude());
		urlString.append("," + destination.getLongitude());
		urlString.append("&sensor=true");
		urlString.append("&mode=" + mode);
		Log.d(TAG, urlString.toString());
		return urlString.toString();
	}

	/**
	 * method for calling getUrlContents
	 * @param url passes url to getUrlContents
	 * @return the string of the API response
	 */
	protected String getJSON(String url) {
		return getUrlContents(url);
	}

	/**
	 * places the API request and builds a string out of the response
	 * @param theUrl the URL to place the request with, preformatted
	 * @return String the API response to be parsed into a JSON object
	 */
	private String getUrlContents(String theUrl) {
		StringBuilder content = new StringBuilder();
		try {
			URL url = new URL(theUrl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()), 8);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Log.d(TAG, "get UrlContents ::: "+content.toString());
		return content.toString();
	}
}
