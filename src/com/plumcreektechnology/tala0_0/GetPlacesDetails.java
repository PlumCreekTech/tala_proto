package com.plumcreektechnology.tala0_0;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * an asynchronous task to retrieve details about a location
 * from the Place Details Google API
 * @author Devin Frenze
 * @author Nora Hayes
 */
public class GetPlacesDetails extends AsyncTask<String, Void, PlaceDetails>
		implements Tala_Constants {

	private String TAG = getClass().getName();

	/**
	 * gets details about a place specified by its reference ID
	 * and returns a PlaceDetails object unless there is an error
	 * @return PlaceDetails details about the location queried
	 */
	@Override
	protected PlaceDetails doInBackground(String... params) {
		String refId = params[0];
		String urlString = makeUrl(refId);

		try {
			String json = getJSON(urlString);
			Log.d(TAG, "JSON ::: "+json);
			JSONObject object = new JSONObject(json);
			JSONObject results = object.getJSONObject("result");
			PlaceDetails placeDetails = PlaceDetails
					.jsonToPontoReferencia(results);
			return placeDetails;
		} catch (JSONException ex) {
			Logger.getLogger(LocationService.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return null;
	}

	/**
	 * currently does nothing
	 */
	@Override
	protected void onPostExecute(PlaceDetails pd) {
		//TalaMain.fragReplacer(new InfoFragment(pd), false);
	}

	/**
	 * private method to formatting URLs from reference IDs
	 * @param refId the reference ID to use in a URL
	 * @return String that is the URL. ex: https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
	 */
	// https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
	private String makeUrl(String refId) {
		StringBuilder urlString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/details/json?");
		
		urlString.append("reference=" + refId);
		urlString.append("&sensor=true&key=" + API_KEY);
		Log.d(TAG, urlString.toString());
		return urlString.toString();
	}

	/**
	 * internal method for asynchronous task to call getUrlContents indirectly
	 * @param url url to use for API request
	 * @return String the results of the request
	 */
	protected String getJSON(String url) {
		return getUrlContents(url);
	}

	/**
	 * 
	 * @param theUrl to use in the request, preformatted
	 * @return String that is the contents of the API request
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
