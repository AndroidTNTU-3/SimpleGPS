package com.example.simplegpstracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.simplegpstracker.GetPoliLine.PoliLoaderCallBack;
import com.example.simplegpstracker.db.GPSInfoHelper;
import com.example.simplegpstracker.entity.GPSInfo;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class PointAdapter implements PoliLoaderCallBack{
	
	private SharedPreferences preferences;
	private String travelMode;
	private Context context;
	private GPSInfoHelper helper;
	private List<GPSInfo> list;
	private ArrayList<LatLng> points = null;
	PointAdapterCallBack pointAdapterCallBack;
	
	public static interface PointAdapterCallBack{
		public void drawPoli(ArrayList<LatLng> points);
	}
	
	PointAdapter(Context context){
		this.context = context;
		getPreference();
	}
	
	//check if dataBase not empty;
	public boolean isDataAccess(){
		
		//get data from database
		if(!getDataDB()) return false;
		return true;
	}

	private void getPreference() {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		//get traveling mode
		travelMode = preferences.getString("travelMode", "walking");		
	}
	
	private boolean getDataDB() {
		helper = new GPSInfoHelper(context);
        list = new ArrayList<GPSInfo>();
        list = helper.getGPSPoint();
        
        //to start compute we must have 2 points
        if(list.size() < 2){
			Toast toast = Toast.makeText(context, context.getResources().getString(R.string.message_base_empty), Toast.LENGTH_SHORT); 
			toast.show();
			helper.closeDB();
			return false;
        }
		return true;
	}
	
	public void startCompute(){
		
		//because a parameter MAX_WAYPOINTS must be less 8 divide a array into parts
		ArrayList<GPSInfo> list8 = new ArrayList<GPSInfo>();
		for(int i=0; i<list.size() ; i++){
						
			list8 = new ArrayList<GPSInfo>();
				for(int j=0; j<8; j++){
					if ((j + i*7) < list.size()) list8.add(list.get(j + i*7));
				}
				 
			//get computed point from the google server	
			//last point
			if(list8.size() > 2) getTrack(list8);
		}
	}
	
	private void getTrack(ArrayList<GPSInfo> list8){
		
		//1. Get URL for multiple waypoints
		String url = getMapsApiDirectionsUrl(list8);
			
		//2. Get array of points from google directions
		GetPoliLine getPoly = new GetPoliLine();
	
		//3. Set a callback. After parsing we will get a new points here in setPoli();
		getPoly.setLoaderCallBack(this);
		getPoly.start(url);
				 
	}
	
	private String getMapsApiDirectionsUrl(ArrayList<GPSInfo> list8) {
		
		StringBuilder waypoints = null;
		
		waypoints = new StringBuilder();
		waypoints.append("waypoints=optimize:true|");
		
		//formation request for google server

		for(GPSInfo info: list8 ){
			Log.i("DEBUG", " Thislat:" + Double.toString(info.getLongitude()));
			waypoints.append(String.valueOf(info.getLatitude()));
			waypoints.append(",");
			waypoints.append(String.valueOf(info.getLongitude()));
			waypoints.append("|");
		}
		
		waypoints.setLength(waypoints.length() - 1);
		waypoints.append("&");
		waypoints.append("sensor=true&mode=walking");
		
		String params = waypoints.toString();
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + params;
		 Log.i("DEBUG", " lat:" + url);
		return url;
		
	}
	
	//callback: driving track on map after get response from google server 
	@Override
	public void setPoli(List<List<HashMap<String, String>>> routes) {
		
		// traversing through routes
			
		for (int i = 0; i < routes.size(); i++) {
			points = new ArrayList<LatLng>();
			List<HashMap<String, String>> path = routes.get(i);

			for (int j = 0; j < path.size(); j++) {
				HashMap<String, String> point = path.get(j);

				double lat = Double.parseDouble(point.get("lat"));
				double lng = Double.parseDouble(point.get("lng"));
				LatLng position = new LatLng(lat, lng);
				points.add(position);
			}
				
		}
		pointAdapterCallBack.drawPoli(points);
	}
	
	//registering callback
		public void setPointAdapterCallBack(PointAdapterCallBack pointAdapterCallBack) {
			this.pointAdapterCallBack = pointAdapterCallBack;
		}
	

}
