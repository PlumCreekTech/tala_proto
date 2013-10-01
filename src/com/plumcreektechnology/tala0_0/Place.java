package com.plumcreektechnology.tala0_0;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * class that holds the relevant data about an individual place as returned by the Places API
 * @author Devin Frenze
 * @author Nora Hayes
 */
public class Place {
	private String id;
	private String icon;
	private String name;
	private String vicinity;
	private Double latitude;
	private Double longitude;
	private String[] types;
	private String reference;

	/**
	 * builds a place from a JSONObject
	 * @param pontoReferencia JSONObject as returned by API to use for parsing Place object
	 * @return Place
	 */
	static Place jsonToPontoReferencia(JSONObject pontoReferencia) {
		try {
			Place result = new Place();
			JSONObject geometry = (JSONObject) pontoReferencia.get("geometry");
			JSONObject location = (JSONObject) geometry.get("location");
			result.setLatitude((Double) location.get("lat"));
			result.setLongitude((Double) location.get("lng"));
			result.setIcon(pontoReferencia.getString("icon"));
			result.setName(pontoReferencia.getString("name"));
			result.setVicinity(pontoReferencia.getString("vicinity"));
			result.setId(pontoReferencia.getString("id"));
			result.setReference(pontoReferencia.getString("reference"));
			JSONArray jsonTypes = (JSONArray) pontoReferencia.getJSONArray("types");
			String[] types = new String[jsonTypes.length()];
			for(int i=0; i<jsonTypes.length(); i++) {
				types[i] = jsonTypes.getString(i);
			}
			result.setTypes(types);
			return result;
		} catch (JSONException ex) {
			Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	@Override
	public String toString() {
		return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", types="+ types.toString() + '}';
	}

}