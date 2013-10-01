package com.plumcreektechnology.tala0_0;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.plumcreektechnology.tala0_0.LocationService.LocationBinder;
import com.plumcreektechnology.tala0_0.PopUpAdapter.PopUpCallbacks;
import com.plumcreektechnology.tala0_0.SettingsFragment.OnOffReceiver;
import com.plumcreektechnology.tala0_0.SliderView.SliderReceiver;
//import com.plumcreektechnology.tala0_0.DirectionsFragment.DirectionsInterface;

/**
 * 
 * the main class for the entire application
 * starts the other services and tasks and maintains UI
 * @author Devin Frenze
 * @author Nora Hayes
 *
 */

public class TalaMain extends Activity implements SliderReceiver, OnOffReceiver, Tala_Constants, PopUpCallbacks /*DirectionsInterface*/ {

	private final String TAG = getClass().getName();
	private TreeMap<String, Integer> categoryRadii;
	private boolean onOff;
	private ListView popupList;
	
	//----- directions constants------
	private Handler directionsHandler;
	private String directionsMessage;
	private String instructions;
	private String startAddress;
	private String endAddress;
	//---- placedetails constants ----
	private Handler placeHandler;
	
	// -----------------------------------UTILITIES TO BIND----------------------------------------------
	private LocationService locationService;
	private boolean locationBound = false;
	private boolean displayPopup;
	private boolean creating;
	
	/**
	 * custom ServiceConnection implementation so we can change constants that indicate
	 * whether or not we are connected to the service and call onBound() method
	 * @author Devin Frenze
	 * @author Nora Hayes
	 */
	private ServiceConnection connection = new ServiceConnection() {

		/**
		 * set our binder, locationService, and locationBound variables
		 * then call onBound()
		 */
	    public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        LocationBinder binder = (LocationBinder) service;
	        locationService = binder.getService();
	        locationBound = true;
	        onBound();
	    }
	    
	    /**
	     * change our locationBound to false
	     */
		public void onServiceDisconnected(ComponentName arg0) {
			Log.d(TAG, "in on service disconnected");
			locationBound = false;
		}

	};

	/**
	 * custom Handler implementation to receive messages from our bound service
	 */
	@SuppressLint("HandlerLeak")
	Handler locationHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			locationUpdate(bundle.getDouble("latitude", 0.0),
					bundle.getDouble("longitude", 180.0));
		}
	};

// -----------------------------------LIFECYCLE----------------------------------------------
	/**
	 * onCreate behaves differently depending on whether the app is already instantiated.
	 * if the app is opening anew it will open the last open fragment.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tala_main);
		onOff = getSharedPreferences(PACKAGE, Context.MODE_PRIVATE).getBoolean(ON_OFF_KEY, false);
		
		if(onOff) {
			Intent intent = new Intent(this, LocationService.class);
			getApplicationContext().bindService(intent, connection, 0); // TODO bind to running service
			creating = true;
		} else {
			categoryRadii = new TreeMap<String, Integer>();
			
			if (checkPlayServices()) { // only add SettingsFragment (which might start the LocationService) if google play is installed
				SharedPreferences prefs = getSharedPreferences(KEY_MAIN_PREFERENCE, Context.MODE_PRIVATE);
				int fragmentId = prefs.getInt(LAST_OPEN_FRAGMENT_KEY, R.id.action_settings);
				switch(fragmentId) { // always assume that a fragment is active
				case(R.id.action_settings):
					fragAdder(new SettingsFragment(), false);
					prefs.edit().putInt(LAST_OPEN_FRAGMENT_KEY, R.id.action_settings); // add frag as last open
					break;
				case(R.id.action_temp):
					fragAdder(new TemporaryFragment(), false);
					prefs.edit().putInt(LAST_OPEN_FRAGMENT_KEY, R.id.action_temp); // add frag as last open
					break;
				case(R.id.action_map):
					MapFragment mapFrag = instantiateMapFragment();
					fragAdder(mapFrag, false);
					GoogleMap map = mapFrag.getMap();
					if(map!=null) map.setMyLocationEnabled(true);
					prefs.edit().putInt(LAST_OPEN_FRAGMENT_KEY, R.id.action_map); // add frag as last open
					break;
				}
			}
		}
		
		boolean popup = false;
		if(getIntent().hasExtra("popup")) {
			popup = getIntent().getBooleanExtra("popup", false);
			Log.d(TAG, "display popup "+popup);
		}
		displayPopup = popup;
	}
	
	/**
	 * when the app is asked to start, and it is already running, this method is called.
	 * it contains helpful log messages and also will display the popup if there is "popup" 
	 * extra is true
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// onCreate is SKIPPED if onNewIntent() is invoked
		// make sure on NewIntent() is skipped when onCreate() is invoked
		if(locationBound) Log.d(TAG, "location is bound");
		Log.d(TAG, "entered onNewIntent()");
		if( intent.getBooleanExtra("popup", false)) {
			// TODO display popup
			Log.d(TAG, "pop up is true");
			String[] sArray = intent.getStringArrayExtra("triggers");
			if(sArray== null) {
				Log.d(TAG, "sArray is null");
			} else {
				displayPopUp(sArray, false);
			}
		}
	}
	
	/**
	 * inflate the options menu xml file when activity is instantiated
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tala_main, menu);
		return true;
	}
	
	/**
	 * if the LocationService is currently running, unbind the service after calling onUnbound
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(locationBound) { // if the service is bound, unbind before quitting
			onUnbound();
			getApplicationContext().unbindService(connection); // TODO
		}
	}

// -----------------------------------FRAGMENT UTILITIES----------------------------------------------
	
	/**
	 * when a menu item is selected it starts its corresponding fragment
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemID = item.getItemId();
		switch(itemID) { // always assume that a fragment is active
		case(R.id.action_settings):
			fragReplacer(new SettingsFragment(), true);
			getSharedPreferences(KEY_MAIN_PREFERENCE, Context.MODE_PRIVATE).edit().putInt(LAST_OPEN_FRAGMENT_KEY, R.id.action_settings); // add frag as last open
			break;
		case(R.id.action_temp):
			fragReplacer(new DirectionsFragment(), true);
			getSharedPreferences(KEY_MAIN_PREFERENCE, Context.MODE_PRIVATE).edit().putInt(LAST_OPEN_FRAGMENT_KEY, R.id.action_temp); // add frag as last open

			break;
		case(R.id.action_map):
			MapFragment mapFrag = instantiateMapFragment();
			fragReplacer(mapFrag, true);
			GoogleMap map = mapFrag.getMap();
			if(map!=null) map.setMyLocationEnabled(true);
			getSharedPreferences(KEY_MAIN_PREFERENCE, Context.MODE_PRIVATE).edit().putInt(LAST_OPEN_FRAGMENT_KEY, R.id.action_map); // add frag as last open
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * private utility for adding fragments
	 * @param frag the Fragment to add
	 * @param backStack boolean for whether this action should be reversable via the back button
	 */
	private void fragAdder(Fragment frag, boolean backStack) {
		FragmentTransaction trans = getFragmentManager().beginTransaction();
		trans.add(R.id.main_parent, frag);
		if(backStack) trans.addToBackStack(null);
		trans.commit();
	}
	
	/**
	 * private utility for removing fragments
	 * @param frag Fragment to remove
	 * @param backStack boolean for whether this action should be reversable via the back button
	 */
	private void fragRemover(Fragment frag, boolean backStack) {
		FragmentTransaction trans = getFragmentManager().beginTransaction();
		trans.remove(frag);
		if(backStack) trans.addToBackStack(null);
		trans.commit();
	}
	
	/**
	 * private utility for replacing fragments
	 * @param frag Fragment to be added
	 * @param backStack boolean for whether this action should be reversable via the back button
	 */
	private void fragReplacer(Fragment frag, boolean backStack) {
		FragmentTransaction trans = getFragmentManager().beginTransaction();
		trans.replace(R.id.main_parent, frag);
		if(backStack) trans.addToBackStack(null);
		trans.commit(); 
	}

	/**
	 * private utility for instantiating the map fragment
	 * @return MapFragment
	 */
	private MapFragment instantiateMapFragment() {
		SharedPreferences prefs = getSharedPreferences(KEY_MAP_PREFERENCE, Context.MODE_PRIVATE);
		return MapFragment // instantiate defaults for map
				.newInstance((new GoogleMapOptions())
						.mapType(GoogleMap.MAP_TYPE_NORMAL)
						.camera((new CameraPosition.Builder().target(new LatLng(
								Double.parseDouble(prefs
										.getString(
												getLocationPreferenceKey(KEY_ITEM_LATITUDE),
												"90.0")),
								Double.parseDouble(prefs
										.getString(
												getLocationPreferenceKey(KEY_ITEM_LONGITUDE),
												"0.0")))))
								.zoom(prefs
										.getFloat(
												getLocationPreferenceKey(KEY_ITEM_ZOOM),
												10))
								.tilt(prefs
										.getFloat(
												getLocationPreferenceKey(KEY_ITEM_TILT),
												0))
								.bearing(
										prefs.getFloat(
												getLocationPreferenceKey(KEY_ITEM_BEARING),
												0)).build()));
	}
	
	/**
	 * generate consistent item keys for saving the lists state in SharedPreferences
	 * @param KEY to be used to format the preference key
	 * @return String
	 */
	protected static String getLocationPreferenceKey(String KEY) {
		return PACKAGE + "_" + KEY ;
	}
	
	/**
	 * private utility called when we need to display the popup
	 * @param triggers the places we are in proximity to
	 * @param dismiss whether, when the popup is dismissed, the app should also be dismissed
	 */
	private void displayPopUp(String[] triggers, boolean dismiss) {
		displayPopup = false;
		String[] names = new String[triggers.length];
		for(int i=0; i<triggers.length; i++) {
			names[i] = locationService.getReadableFromId(triggers[i]);
		}
		PopUpFragment popup = PopUpFragment.newInstance( triggers, names, dismiss);
		AlertDialog d = (AlertDialog) popup.getDialog();
		//popupList = d.getListView();
		((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate( new long[] {0, 100, 100, 100, 100, 100}, -1);
	    popup.show(getFragmentManager(), TAG);
	}
	
// -----------------------------------SETTINGS UTILITIES----------------------------------------------
	
	/**
	 * VERSION FOR HANDLER!!!
	 * this is called every time the switch is changed PLUS every time the
	 * settings fragment is loaded
	 * @param status boolean for if the location service will be on or off
	 */
	@Override
	public void onSwitchChanged(boolean status) {
		onOff = status;
		// TODO figure out if it is necessary to see if the service is running
		// supposedly, android won't let a service be activated twice
		/**
		 *  if the service should be running, and it is not, start it
		 *  if the service should not be running, and it is, stop it
		 *  if the service should be running and it is, do nothing
		 *  if the service should not be running, and it is not, do nothing
		 */
		boolean serviceRunning = isServiceRunning(LocationService.class);
		Intent intent = new Intent(getApplicationContext(), LocationService.class);
		if (status) {
			if (!serviceRunning) {
				startService(intent);
				getApplicationContext().bindService(intent, connection, 0); // TODO
				Log.d(TAG, "location service was not running, and was started");
			} else {
				getApplicationContext().bindService(intent, connection, 0); // TODO
				Log.d(TAG, "location service was running and continues to run");
			}
		} else if (serviceRunning) {
			onUnbound();
			getApplicationContext().unbindService(connection); // TODO
			stopService(intent);
			Log.d(TAG, "location service was running, and was stopped");
		} else
			Log.d(TAG, "service was not running and still is not running");
	}
	
	/**
	 * called every time a slider changes
	 * @param type String that is the category of the slider
	 * @param value distance in miles the radius of that category is set to
	 * @param active boolean for if it is active or inactive
	 */
	@Override
	public void sliderChanged(String type, int value, boolean active) {
		if(active) {
			categoryRadii.put(type, (int) (1609.34*value));
		} else {
			if(categoryRadii.containsKey(type)) categoryRadii.remove(type);
		}
		if(locationBound) locationService.setCategoryRadii(categoryRadii);
	}

// -----------------------------------LOCATION UTILITIES----------------------------------------------

	/**
	 * sample play services utility from
	 * http://www.androiddesignpatterns.com/2013/01/google-play-services-setup.html
	 * @return boolean
	 */
	private boolean checkPlayServices() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
				showErrorDialog(status);
			} else {
				Toast.makeText(this, "This device is not supported.",
						Toast.LENGTH_LONG).show();
				finish(); // end the service if device is not supported
			}
			return false;
		}
		return true;
	}

	/**
	 * private utility for showing an error popup when something goes wrong
	 * @param code the error code to enter
	 */
	private void showErrorDialog(int code) {
		GooglePlayServicesUtil.getErrorDialog(code, this,
				REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}
	 
	/**
	 * method called when results come back from activity that tells us if 
	 * Google Play Services is installed
	 * @param requestCode the code of our request to the activity
	 * @param resultCode the code o the result from the activity
	 * @param data Intent that contains relevant data, we don't use it but we pass it to the super
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  switch (requestCode) {
	    case REQUEST_CODE_RECOVER_PLAY_SERVICES:
	      if (resultCode == RESULT_CANCELED) {
	        Toast.makeText(this, "Google Play Services must be installed.",
	            Toast.LENGTH_SHORT).show();
	        finish();
	      }
	      return;
	  }
	  super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * when the service is bound, give it this activity to talk back to
	 * also, potentially display a popup
	 */
	public void onBound() {
		locationService.setHandler(locationHandler);
		
		if(creating) {
			creating = false;
			categoryRadii = locationService.getCategoryRadii();
			
			if(displayPopup) {
				// TODO display popup
				Log.d(TAG, "should display popup fragment from onCreate");
				String[] sArray = getIntent().getExtras()
						.getStringArray("triggers");
				Log.d(TAG, Arrays.toString(sArray));
				displayPopUp(sArray, true);
			} else if (checkPlayServices()) { // only add SettingsFragment (which might start the LocationService) if google play is installed
				SharedPreferences prefs = getSharedPreferences(KEY_MAIN_PREFERENCE, Context.MODE_PRIVATE);
				int fragmentId = prefs.getInt(LAST_OPEN_FRAGMENT_KEY, R.id.action_settings);
				switch(fragmentId) { // always assume that a fragment is active
				case(R.id.action_settings):
					fragAdder(new SettingsFragment(), false);
					prefs.edit().putInt(LAST_OPEN_FRAGMENT_KEY, R.id.action_settings); // add frag as last open
					break;
				case(R.id.action_temp):
					fragAdder(new TemporaryFragment(), false);
					prefs.edit().putInt(LAST_OPEN_FRAGMENT_KEY, R.id.action_temp); // add frag as last open
					break;
				case(R.id.action_map):
					MapFragment mapFrag = instantiateMapFragment();
					fragAdder(mapFrag, false);
					GoogleMap map = mapFrag.getMap();
					if(map!=null) map.setMyLocationEnabled(true);
					prefs.edit().putInt(LAST_OPEN_FRAGMENT_KEY, R.id.action_map); // add frag as last open
					break;
				}
			}
		} else locationService.setCategoryRadii(categoryRadii);
	}
	
	/**
	 * when the service is unbound, make sure we remember not to communicate to it
	 */
	public void onUnbound() {
		locationService.setHandler(null);
		locationBound=false;
	}
	
	/**
	 * toast made to know that the service is communicating with the handler
	 * @param latitude latitude of current location
	 * @param longitude longitude of current location
	 */
	public void locationUpdate(double latitude, double longitude) {
		
		//Toast.makeText(this, "location changed in activity "+latitude+" - "+longitude, Toast.LENGTH_SHORT).show();
	}
	
// -----------------------------------SERVICE UTILITIES----------------------------------------------
	
	/**
	 * method to see if our service is alread running (so we don't start a duplicate)
	 * @param myService the Class of service we want to look for
	 * @return boolean
	 */
	@SuppressWarnings("rawtypes")
	private boolean isServiceRunning(Class myService) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (myService.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

// -----------------------------------POP UP CALLBACKS----------------------------------------------	
	
	/**
	 * things to do when a remove button is pressed on the popup, currently inactive
	 * @param geoId String of the ID of the place to be removed
	 * @param v View that is the row to remove
	 */
	@Override
	public void removeButton(String geoId, View v) {
		Log.d(TAG, "removeButton callback");
		// popupList.removeView(v);
		// locationService.removeActiveId(geoId); // TODO pointers SHOULD match but hey, who knows
	}

	/**
	 * things to do when an info button is pressed on the popup.
	 * gets info about the relevant place.
	 * @param geoId String of the ID of the place to get more info about
	 */
	@Override
	public void infoButton(String geoId) {
		Log.d(TAG, "infoButton callback "+geoId);
		new GetPlacesDetails().execute(locationService.getReferenceFromId(geoId));
		//TODO initialize placeHandler and things
	}

	/**
	 * things to do when the visit button is pressed on popup.
	 * gets directions to the relevant place.
	 * @param geoId String of the ID to get directions to
	 */
	@Override
	public void visitButton(String geoId) {
		Log.d(TAG, "visitButton callback "+geoId);
		DirectionsQuery directionQ = new DirectionsQuery(locationService.getCurrentLocation(), locationService.getLocationOfPlace(geoId));
		directionsHandler = new Handler(){
			public void handleMessage(Message msg){
				Bundle bundle = msg.getData();
				directionsMessage = bundle.getString("route", "Route data doesn't exist, sorry.");
				instructions = bundle.getString("instructions", "no instructions, sorry");
				endAddress = bundle.getString("end address");
				
				directionsMessage = "Driving directions: \n" + instructions;
				fragReplacer(new DirectionsFragment(), false);
			}
		};
		new GetDirections(/*directionsHandler*/).execute(directionQ);
		// TODO get and diplay directions on the map with async task
		//TODO dismiss dialogfragment after clicking the visit button
	}
	
//	/**
//	 * Returns route summary so that user can read it.
//	 */
//	@Override
//	public String getDirectionsMessage() {
//		return directionsMessage;
//	}

// -------------------------------GET PLACES DETAILS--------------------------------------------
	
	/**
	 * asyncronous task to handle placing a Places Details request to Google's API
	 * @author Devin Frenze
	 * @author Nora Hayes
	 *
	 */
	public class GetPlacesDetails extends AsyncTask<String, Void, PlaceDetails>
			implements Tala_Constants {

		private String TAG = getClass().getName();

		/**
		 * place the request and parse it asyncronously
		 */
		@Override
		protected PlaceDetails doInBackground(String... params) {
			String refId = params[0];
			String urlString = makeUrl(refId);

			try {
				String json = getJSON(urlString);
				Log.d(TAG, "JSON ::: " + json);
				JSONObject object = new JSONObject(json);
				JSONObject results = object.getJSONObject("result");
				PlaceDetails placeDetails = PlaceDetails
						.jsonToPontoReferencia(results);
				return placeDetails;
			} catch (JSONException ex) {
				Logger.getLogger(LocationService.class.getName()).log(
						Level.SEVERE, null, ex);
			}
			return null;
		}

		/**
		 * when the request is complete show the returned data
		 */
		@Override
		protected void onPostExecute(PlaceDetails pd) {
			InfoFragment info = new InfoFragment();
			info.setDetails(pd);
			info.show(getFragmentManager(), TAG);
		}

		/**
		 * private utility for creating a properly formatted URL for the request
		 * @param refId unique reference ID of the place to query about
		 * @return String the URL properly formatted
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
		 * method to relay contents of getUrlContents
		 * @param url formatted URL to use to place the request
		 * @return String the results of the Places Details request
		 */
		protected String getJSON(String url) {
			return getUrlContents(url);
		}

		/**
		 * method to place and return the results of the request
		 * @param theUrl URL that is the request to the API
		 * @return
		 */
		private String getUrlContents(String theUrl) {
			StringBuilder content = new StringBuilder();
			try {
				URL url = new URL(theUrl);
				URLConnection urlConnection = url.openConnection();
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()),
						8);
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
	
	// -------------------------------GET DIRECTIONS--------------------------------------------

	/**
	 * asyncronous task to place request to Google Directions API
	 * @author Devin Frenze
	 * @author Nora Hayes
	 *
	 */
	public class GetDirections extends AsyncTask<DirectionsQuery, Void, Route>
			implements Tala_Constants {

		//Handler UIhandler;

		private String TAG = getClass().getName();

		public GetDirections(/*Handler UIhandler*/) {
			//this.UIhandler = UIhandler;
		}

		/**
		 * place the request and wait for the results asyncronously
		 */
		@Override
		protected Route doInBackground(DirectionsQuery... params) {
			DirectionsQuery dq = params[0];
			String urlString = makeUrl(dq.getOrigin(), dq.getDestination(),
					dq.getMode());
			Log.d(TAG, "url string is ::: " + urlString);
			try {
				String json = getJSON(urlString);
				Log.d(TAG, "JSON ::: " + json);
				JSONObject object = new JSONObject(json);
				JSONArray routes = object.getJSONArray("routes");
				Route route = Route.jsonToRoute(routes.getJSONObject(0));
				return route;
			} catch (JSONException ex) {
				Logger.getLogger(LocationService.class.getName()).log(
						Level.SEVERE, null, ex);
			}
			return null;
		}

		/**
		 * when the request is over, display a directions fragment with the returned data
		 */
		@Override
		protected void onPostExecute(Route route) {
//			Log.d(TAG, route.toString());
//			ArrayList<Step> steps = route.getLeg().getSteps();
//			String instructions = "";
//			for (int i = 0; i < steps.size(); i++) {
//				instructions += steps.get(i).getInstructions();
//			}
//
//			Message msg = Message.obtain();
//			Bundle data = new Bundle();
//			data.putString("instructions", instructions);
//			data.putString("end address", route.getLeg().getEndAddress());
//			msg.setData(data);
//			UIhandler.dispatchMessage(msg);
			
			DirectionsFragment dFrag = new DirectionsFragment();
			dFrag.setRoute(route);
			dFrag.show(getFragmentManager(), TAG);
			
		}

		/**
		 * private utility to properly format a URL to be placed as request
		 * @param origin starting point of travel
		 * @param destination ending point of travel
		 * @param mode method of travel
		 * @return String the properly formatted URL
		 */
		// https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
		private String makeUrl(Location origin, Location destination,
				String mode) {
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
		 * passes data to and from getUrlContents
		 * @param url String that is the properly formatted URL to place request
		 * @return String the results of the request
		 */
		protected String getJSON(String url) {
			return getUrlContents(url);
		}

		/**
		 * method that actually places request and waits for the response.
		 * @param theUrl the URL that represents the request
		 * @return String the contents of the API response
		 */
		private String getUrlContents(String theUrl) {
			StringBuilder content = new StringBuilder();
			try {
				URL url = new URL(theUrl);
				URLConnection urlConnection = url.openConnection();
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()),
						8);
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

}
