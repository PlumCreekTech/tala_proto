package com.plumcreektechnology.tala0_0;

import java.util.ArrayList;

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
 * a fragment that displays directions in a simple read-only
 * scrolling DialogFragment that sits on top of whatever application is running
 * @author Devin Frenze
 * @author Nora Hayes
 */
public class DirectionsFragment extends DialogFragment {
	
	Route route;

	public void setRoute(Route r) {
		route = r;
	}

	/**
	 * The system calls this only when creating the layout in a dialog.
	 * instantiates the fragment and gathers the data to display
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// get data from arguments
		ScrollView scroll = new ScrollView(getActivity());
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		// if(details.hasIcon()) {
		//
		// }
		
		TextView summary = new TextView(getActivity());
		summary.setText(route.getSummary());
		summary.setTextColor(Color.WHITE);
		summary.setTextSize(22);
		layout.addView(summary);
		
		Leg leg = route.getLeg();
		
		TextView disDur = new TextView(getActivity());
		disDur.setText(leg.getDistance() +" in "+leg.getDuration());
		disDur.setTextColor(Color.WHITE);
		layout.addView(disDur);
		
		ArrayList<Step> steps = leg.getSteps();
		
		String stepList = "";
		for( Step step : steps) {
			String instructions = step.getInstructions().replace("\n\n", "\n");
			if(instructions.charAt(instructions.length()-1)=='\n') instructions = instructions.substring(0, instructions.length()-1);
			if(instructions.contains("Destination")) {
				instructions += " in "+step.getDistance();
			} else instructions += " for "+step.getDistance();
			stepList += "\n"+instructions+"\n";
		}
		stepList = stepList.substring(0, stepList.length()-1);
		TextView temp = new TextView(getActivity());
		temp.setText(stepList);
		temp.setTextColor(Color.WHITE);
		layout.addView(temp);
		
		scroll.addView(layout);

		// format and build and return the dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
				AlertDialog.THEME_DEVICE_DEFAULT_DARK);
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
