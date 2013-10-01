package com.plumcreektechnology.tala0_0;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * class that holds all of the data that would be returned from a PlaceDetails request
 * @author Devin Frenze
 * @author Nora Hayes
 *
 */
public class PlaceDetails {

	private String id;
	private String icon;
	private String name;
	private Boolean open;
	private int priceLevel = -1;
	private double rating=-1;
	private String[] reviewsText;
	private String url;
	
	/**
	 * builds the PlaceDetails object from a JSONObject
	 * @param pontoReferencia JSONObject containing the response of the API request
	 * @return PlaceDetails object for storage
	 */
	static PlaceDetails jsonToPontoReferencia(JSONObject pontoReferencia) {
		try {
			PlaceDetails result = new PlaceDetails();
			
			if (pontoReferencia.has("reviews")) {
				JSONArray reviews = (JSONArray) pontoReferencia.get("reviews");
				String[] reviewArray = new String[reviews.length()];
				for (int i = 0; i < reviews.length(); i++) {
					JSONObject temp = reviews.getJSONObject(i);
					reviewArray[i] = temp.getString("text") +"\n-"+ temp.getString("author_name");
				}
				result.setReviewsText(reviewArray);
			}
			
			result.setId(pontoReferencia.getString("id"));
			result.setIcon(pontoReferencia.getString("icon"));
			result.setName(pontoReferencia.getString("name"));
			
			if (pontoReferencia.has("opening_hours")) {
				JSONObject opening_hours = (JSONObject) pontoReferencia
						.get("opening_hours");
				result.setOpen(opening_hours.getBoolean("open_now"));
			}
			if(pontoReferencia.has("prive_level"))	result.setPriceLevel(pontoReferencia.getInt("price_level"));
			if(pontoReferencia.has("rating")) result.setRating(pontoReferencia.getDouble("rating"));
			result.setUrl(pontoReferencia.getString("url"));
			
			return result;
		} catch (JSONException ex) {
			Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "PlaceDetails [id=" + id + ", icon=" + icon + ", name=" + name
				+ ", open=" + open + ", priceLevel=" + priceLevel + ", rating="
				+ rating + ", reviewsText=" + Arrays.toString(reviewsText)
				+ ", url=" + url + "]";
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean hasId() {
		if(id!=null) return true;
		else return false;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public boolean hasIcon() {
		if(icon!=null) return true;
		else return false;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean hasName() {
		if(name!=null) return true;
		else return false;
	}
	public Boolean getOpen() {
		return open;
	}
	public void setOpen(Boolean open) {
		this.open = open;
	}
	public boolean hasOpen() {
		if(open!=null) return true;
		else return false;
	}
	public int getPriceLevel() {
		return priceLevel;
	}
	public void setPriceLevel(int priceLevel) {
		this.priceLevel = priceLevel;
	}
	public boolean hasPriceLevel() {
		if(priceLevel != -1) return true;
		else return false;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public boolean hasRating() {
		if(rating != -1) return true;
		else return false;
	}
	public String[] getReviewsText() {
		return reviewsText;
	}
	public void setReviewsText(String[] reviewsText) {
		this.reviewsText = reviewsText;
	}
	public boolean hasReviewsText() {
		if(reviewsText!=null) return true;
		else return false;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean hasUrl() {
		if(url!=null) return true;
		else return false;
	}
	
}
