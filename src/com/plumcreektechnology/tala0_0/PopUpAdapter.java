package com.plumcreektechnology.tala0_0;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * adapter for putting rows of place data into our popup
 * @author Devin Frenze
 * @author Nora Hayes
 */
public class PopUpAdapter extends ArrayAdapter<PopUpRow> {

	Context context; 
    int layoutResourceId;    
    PopUpRow rows[];
    private PopUpCallbacks callingActivity;
    
    /**
     * an interface defined to insure that the instantiating activity has the ability to respond to these method calls
     * @author Devin Frenze
     * @author Nora Hayes
     *
     */
	public interface PopUpCallbacks {
		public void removeButton(String geoId, View v);
		public void infoButton(String geoId);
		public void visitButton(String geoId);
	}
    
	/**
	 * constructor sets internal fields and checks that the instantiating context implements our interface
	 * @param context the context from which this object was built
	 * @param layoutResourceId the unique ID of the layout that will be populated
	 * @param data an array of Places and their associated data as PopUpRow objects
	 */
	public PopUpAdapter(Context context, int layoutResourceId, PopUpRow[] data) {
		super(context, layoutResourceId, data);
		try {
			callingActivity = (PopUpCallbacks) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement PopUpCallbacks");
		}
		this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.rows = data;
	}
	
	/**
	 * populates the layout with our data
	 */
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PopUpHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new PopUpHolder();
            holder.text = (TextView) row.findViewById(R.id.text);
            holder.remove = (Button)row.findViewById(R.id.remove);
            holder.info = (Button)row.findViewById(R.id.info);
            holder.visit = (Button)row.findViewById(R.id.visit);
            
            row.setTag(holder);
        }
        else
        {
            holder = (PopUpHolder)row.getTag();
        }
        
        PopUpRow popUp = rows[position];
        holder.text.setText(popUp.getName());
        
        holder.remove.setOnClickListener((new OnClickListener() {
        	
        	private String geoId;

			@Override
			public void onClick(View v) {
				callingActivity.removeButton(geoId, v);
			}

			public String getGeoId() {
				return geoId;
			}

			public OnClickListener setGeoId(String geoId) {
				this.geoId = geoId;
				return this;
			}

		}).setGeoId(popUp.getId()));

		holder.info.setOnClickListener((new OnClickListener() {

			private String geoId;

			@Override
			public void onClick(View v) {
				callingActivity.infoButton(geoId);
			}

			public String getGeoId() {
				return geoId;
			}

			public OnClickListener setGeoId(String geoId) {
				this.geoId = geoId;
				return this;
			}

		}).setGeoId(popUp.getId()));

		holder.visit.setOnClickListener((new OnClickListener() {

			private String geoId;

			@Override
			public void onClick(View v) {
				callingActivity.visitButton(geoId);
			}

			public String getGeoId() {
				return geoId;
			}

			public OnClickListener setGeoId(String geoId) {
				this.geoId = geoId;
				return this;
			}

		}).setGeoId(popUp.getId()));
		
        return row;
    }
    
	/**
	 * holds the data for a popup
	 * @author Devin Frenze
	 * @author Nora Hayes
	 *
	 */
    static class PopUpHolder
    {
        TextView text;
        Button remove;
        Button info;
        Button visit;
    }
	
	

}
