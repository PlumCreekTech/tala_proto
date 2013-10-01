package com.plumcreektechnology.tala0_0;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * this is a placeholder fragment to use when more panels are needed to test the menu and navigation
 * @author Devin Frenze
 * @author Nora Hayes
 *
 */
public class TemporaryFragment extends Fragment {

	private PlaceSpecification placeSpecifier;
	
	/**
	 * interface built so that we can insure that the instantiating context implements methods we will use to communicate with it
	 * @author Devin Frenze
	 * @author Nora Hayes
	 *
	 */
	public interface PlaceSpecification {
		public String makePlaceSpecification();
	}
	
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		try {
//			placeSpecifier = (PlaceSpecification) activity;
//		} catch (ClassCastException e) {
//			throw new ClassCastException(activity.toString() + " must implement PlaceSpecification");
//		}
//	}
	
	/**
	 * inflates the fragment which contains a single phrase
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout =  inflater.inflate(R.layout.dir_frag, container, false);
		TextView tv = (TextView) layout.findViewById(R.id.temp_text);
		//tv.setText(placeSpecifier.makePlaceSpecification());
		tv.setText("DON'T TOUCH ME!");
		return layout;
	}
	
	
}
