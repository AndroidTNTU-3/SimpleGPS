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
		//list1.add(new GPSInfo(25.59672519, 49.54928989));
		list1.add(new GPSInfo(25.59663581, 49.54919522));
		list1.add(new GPSInfo(25.59661421, 49.54913938));
		list1.add(new GPSInfo(25.59660384, 49.54905421));
		list1.add(new GPSInfo(25.59657031, 49.54900006));
		list1.add(new GPSInfo(25.59651712, 49.54895933));
		////////Test Array
		
		//get data from database
        helper = new GPSInfoHelper(getApplicationContext());
        list = new ArrayList<GPSInfo>();
        list = helper.getGPSPoint();
		newLatLng = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
  	
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		map = mapFragment.getMap();
		
		if (map == null) {
		      finish();
		      return;
		    }
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng,15));
		
		
		//show on map way as marker or track
		if (viewRouteParameter.equals("marker")) addMarkers();
		else addTrack();
		
		//map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	}
	
	private void addMarkers() {
		for(GPSInfo info: list ){
			newLatLng = new LatLng(info.getLatitude(), info.getLongitude());	
			if (map != null) {
				marker = map.addMarker(new MarkerOptions().position(newLatLng)
						.title("First Point"));
				/*map.setOnMarkerClickListener(listener){
					
				}*/
				
				
				/*map.setInfoWindowAdapter(new InfoWindowAdapter() {

				      @Override
				      public View getInfoWindow(Marker marker) {
				        if (marker.getId().equals(ViewMapActivity.this.marker.getId())) {
				          TextView tv = new TextView(ViewMapActivity.this);
				          tv.setText(String.valueOf(marker.getPosition().latitude));
				          tv.setTextColor(Color.RED);
				          return tv;
				        } else
				          return null;
				      }

				      @Override
				      public View getInfoContents(Marker marker) {
				        TextView tv = new TextView(ViewMapActivity.this);
				        tv.setText("Test getInfoContents");
				        return tv;
				      }
				    });*/
			}
		}
	}
	
	
	private void addTrack(){
		//Get URL for multiple waypoints
		String url = getMapsApiDirectionsUrl();
		
		//Get array of points from google directions
		GetPoliLine getPoly = new GetPoliLine();

		getPoly.setLoaderCallBack(this);
		getPoly.start(url);

				 
	}
	
	private String getMapsApiDirectionsUrl() {
		
		StringBuilder waypoints = null;
		
		waypoints = new StringBuilder();
		waypoints.append("waypoints=optimize:true|");
		
		//formation request for google server
		
		//@params: list - data from database
		//@params: list1 - data from testArray(real points)
		for(GPSInfo info: list ){
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
        	    Log.i("DEBUG", " lat:" + String.valueOf(lat));
        	    Log.i("DEBUG", " lon:" + String.valueOf(lng));
        	    Log.i("DEBUG", " size:" + routes.size());
				points.add(position);
			}

			polyLineOptions.addAll(points);
			polyLineOptions.width(2);
			polyLineOptions.color(Color.BLUE);
			
		}
		
		map.addPolyline(polyLineOptions);
	}

	

}
