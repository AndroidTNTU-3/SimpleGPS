package com.example.simplegpstracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.simplegpstracker.GetPoliLine.PoliLoaderCallBack;
import com.example.simplegpstracker.db.GPSInfoHelper;
import com.example.simplegpstracker.entity.GPSInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.maps.android.*;

/////////////////////////////////////
//Get location point and show they on map
////////////////////////////////////

public class ViewMapActivity extends FragmentActivity implements PoliLoaderCallBack{
	LatLng newLatLng;
	LocationLoader locationLoader;
	Location location;
	PolylineOptions polyLineOptions = null;
	private SharedPreferences preferences;
	private String viewRouteParameter; 
 
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private GPSInfoHelper helper;
	private List<GPSInfo> list;
	private List<GPSInfo> list1;
	ArrayList<LatLng> points = null;
	ArrayList<LatLng> points1 = null;
	PolylineOptions polyLineOptions1 = new PolylineOptions();
	ArrayList<LatLng> points2 = null;
	Marker marker;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_map);
		
		//get parameter how to show the route on a map
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		viewRouteParameter = preferences.getString("viewRoute", "marker");
		Log.i("DEBUG", "view:" + viewRouteParameter);
		
	
		/////////////////////TEST ARRAY for getMapsApiDirectionsUrl()
		//newLatLng = new LatLng(49.54965588, 25.59697587);
		list1 = new ArrayList<GPSInfo>();
		list1.add(new GPSInfo(25.59697587, 49.54965588));
		list1.add(new GPSInfo(25.59693394, 49.54960761));
		list1.add(new GPSInfo(25.59685826, 49.54952958));
		list1.add(new GPSInfo(25.59681966, 49.54944831));
		list1.add(new GPSInfo(25.5967799, 49.54936681));
		list1.add(new GPSInfo(25.59672519, 49.54928989));
		list1.add(new GPSInfo(25.59663581, 49.54919522));
		list1.add(new GPSInfo(25.59661421, 49.54913938));
		//list1.add(new GPSInfo(25.59660384, 49.54905421));
		//list1.add(new GPSInfo(25.59657031, 49.54900006));
		//list1.add(new GPSInfo(25.59651712, 49.54895933));
		///////////////////
		points1 = new ArrayList<LatLng>();
		for(int i=1; i<list1.size(); i++){
			points1.add(new LatLng(list1.get(i).getLatitude(), list1.get(i).getLongitude()));
		}
		points2 = new ArrayList<LatLng>();

		////////Test Array
		
		//get data from database
        helper = new GPSInfoHelper(getApplicationContext());
        list = new ArrayList<GPSInfo>();
        list = helper.getGPSPoint();
        
		
  	
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		map = mapFragment.getMap();
		
		if (map == null) {
		      finish();
		      return;
		    }

		
		

				
		//show on map way as marker or track
			
			//because a parameter MAX_WAYPOINTS must be less 8 divide a array into parts
			ArrayList<GPSInfo> list8 = new ArrayList<GPSInfo>();
			for(int i=0; i<list.size() ; i++){
				
				list8 = new ArrayList<GPSInfo>();
				for(int j=0; j<8; j++){
					if ((j + i*7) < list.size()) list8.add(list.get(j + i*7));
				}
				addTrack(list8);
			}

			map.setInfoWindowAdapter(new InfoWindowAdapter() {

	            // Use default InfoWindow frame
	            @Override
	            public View getInfoWindow(Marker arg0) {
	                return null;
	            }

	            // Defines the contents of the InfoWindow
	            @Override
	            public View getInfoContents(Marker arg0) {

	                // Getting view from the layout file info_window_layout
	                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

	                // Getting the position from the marker
	                LatLng latLng = arg0.getPosition();

	                // Getting reference to the TextView to set latitude
	                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

	                // Getting reference to the TextView to set longitude
	                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

	                // Setting the latitude
	                tvLat.setText("Latitude:" + latLng.latitude);

	                // Setting the longitude
	                tvLng.setText("Longitude:"+ latLng.longitude);

	                // Returning the view containing InfoWindow contents
	                return v;

	            }
	        });
		
			map.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

				@Override
				public void onMapClick(LatLng clickCoords) {
					
					//map.clear();
					
					//get nearest point
					LatLng destPoint = PolyUtil.GetTargetPoint(clickCoords, points2, false, 5);
					// Creating an instance of MarkerOptions to set position
	                MarkerOptions markerOptions = new MarkerOptions();

	                // Setting position on the MarkerOptions
	                markerOptions.position(destPoint);

	                // Animating to the currently touched position
	                map.animateCamera(CameraUpdateFactory.newLatLng(destPoint));

	                // Adding marker on the GoogleMap
	                Marker marker = map.addMarker(markerOptions);

	                // Showing InfoWindow on the GoogleMap
	                marker.showInfoWindow();
					
				}}
			);
	}
	
	private void addMarkers1() {
		for(GPSInfo info: list1 ){
			newLatLng = new LatLng(info.getLatitude(), info.getLongitude());	
			if (map != null) {
				marker = map.addMarker(new MarkerOptions().position(newLatLng)
						.title("First Point"));				
			}
		}
	}
	
	private void addMarkers(ArrayList<LatLng> points) {
		if((points != null) && (points.size() != 0)){
		for(LatLng info: points ){	
			if (map != null) {
				marker = map.addMarker(new MarkerOptions().position(info)
						.title("First Point"));
			}
		}
		}
	}
	
	
	private void addTrack(ArrayList<GPSInfo> list8){
		//Get URL for multiple waypoints
		String url = getMapsApiDirectionsUrl(list8);
		
		//Get array of points from google directions
		GetPoliLine getPoly = new GetPoliLine();

		getPoly.setLoaderCallBack(this);
		getPoly.start(url);

				 
	}
	
	
	
	private String getMapsApiDirectionsUrl(ArrayList<GPSInfo> list8) {
		
		StringBuilder waypoints = null;
		
		waypoints = new StringBuilder();
		waypoints.append("waypoints=optimize:true|");
		
		//formation request for google server
		
		//@params: list - data from database
		//@params: list1 - data from testArray(real points)

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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//callback: driving track on map after get response from google server 
	@Override
	public void setPoli(List<List<HashMap<String, String>>> routes) {
		ArrayList<LatLng> points = null;
		//PolylineOptions polyLineOptions = null;
		PolylineOptions polyLineOptions = new PolylineOptions();
		// traversing through routes
		for (int i = 0; i < routes.size(); i++) {
			points = new ArrayList<LatLng>();
			polyLineOptions = new PolylineOptions();
			List<HashMap<String, String>> path = routes.get(i);

			for (int j = 0; j < path.size(); j++) {
				HashMap<String, String> point = path.get(j);

				double lat = Double.parseDouble(point.get("lat"));
				double lng = Double.parseDouble(point.get("lng"));
				LatLng position = new LatLng(lat, lng);
				points.add(position);
			}

			polyLineOptions.addAll(points);
			polyLineOptions.width(2);
			polyLineOptions.color(Color.BLUE);
			
		}

		//a array for click on map	
		for(LatLng p: polyLineOptions.getPoints()){
			points2.add(p);	
		}
		
		//moving camera to first point
		newLatLng = new LatLng(points2.get(0).latitude, points2.get(0).longitude);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng,15));
		
		if (viewRouteParameter.equals("marker")) addMarkers(points);
		else map.addPolyline(polyLineOptions);
	}

	

}
