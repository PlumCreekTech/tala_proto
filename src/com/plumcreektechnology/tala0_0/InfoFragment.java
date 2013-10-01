package com.plumcreektechnology.tala0_0;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * class to display the details about a specific in a scrolling
 * read-only window that pops up on top of whatever application
 * is running
 * @author Devin Frenze
 * @author Nora Hayes
 */
public class InfoFragment extends DialogFragment {
	
	PlaceDetails details;
	
	/**
	 * sets the internal PLaceDetails object
	 * this is essential for displaying anything in the fragment
	 * @param pd the PlaceDetails to be displayed
	 */
	public void setDetails(PlaceDetails pd) {
		details = pd;
	}
	
	/**
	 * returns the status of the internal PlaceDetails object
	 * @return String "closed" or "open"
	 */
	private String isOpen() {
		if(details.getOpen()) return "open";
		else return "closed";
	}
	
	/**
	 * The system calls this only when creating the layout in a dialog.
	 * instantiates the display for the DialogFragment
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		// get data from arguments
		ScrollView scroll = new ScrollView(getActivity());
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
//		if(details.hasIcon()) {
//			
//		} 
		if (details.hasName()) {
			TextView name = new TextView(getActivity());
			name.setText(details.getName());
			name.setTextColor(Color.WHITE);
			name.setTextSize(22);
			layout.addView(name);
		} if (details.hasRating()) {
			TextView rating = new TextView(getActivity());
			rating.setText("Average rating is "+details.getRating());
			rating.setTextColor(Color.WHITE);
			layout.addView(rating);
		} if (details.hasOpen()) {
			TextView open = new TextView(getActivity());
			open.setText("Currently is "+ isOpen());
			open.setTextColor(Color.WHITE);
			layout.addView(open);
			
		} if (details.hasPriceLevel()) {
			TextView pLevel = new TextView(getActivity());
			pLevel.setText("Price is "+ details.getPriceLevel() +"/4");
			pLevel.setTextColor(Color.WHITE);
			layout.addView(pLevel);
		} if (details.hasReviewsText()) {
			TextView reviews = new TextView(getActivity());
			String[] sArray = details.getReviewsText();
			String reviewParagraph = "";
			for (int i=0; i<sArray.length; i++) {
				reviewParagraph += "\n"+sArray[i]+"\n";
			}
			reviews.setText("\nReviews : \n"+reviewParagraph);
			reviews.setTextColor(Color.WHITE);
			layout.addView(reviews);
		}
		scroll.addView(layout);
		
		// format and build and return the dialog
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
		builder.setTitle("PROXIMITY UPDATE");
		builder.setNeutralButton("DISMISS",
				(new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dismiss();
					}
				}));
		builder.setView(scroll);
		return builder.create();
	}

}
