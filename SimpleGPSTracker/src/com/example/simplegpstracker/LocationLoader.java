package com.example.simplegpstracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.simplegpstracker.TrackService.UnregisterCallBack;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/////////////////////////////////////
//Get current location 
////////////////////////////////////

public class LocationLoader implements LocationListener, UnregisterCallBack{
	
	private final static int MIN_TIME = 400;
	private final static int MIN_DISTANCE = 5;
	
	TrackService service;
	
	public static interface LocationLoaderCallBack{
		public void setLocation(String cityId);
	}
	
	LocationLoaderCallBack locationLoaderCallBack;
	
	  private LocationManager locationManager;
	  private String provider = "GPS";
	  private Context context;
	  private Location location;
	  private double latitude;
	  private double longitude; 
	  private SharedPreferences preferences;
	  	
	public LocationLoader(Context context, TrackService service){
		this.context = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.service = service;
		service.setCallBack(this);
	}
	
	private void setProvider(String providers) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);	
		if (providers.equals("GPS")) {
			//check if GPS enable
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) provider = LocationManager.GPS_PROVIDER;
			else {
				Toast toast = Toast.makeText(context, context.getResources().getString(R.string.gps_off), Toast.LENGTH_SHORT); 
				toast.show(); 
			}
		}
		else if (providers.equals("Network")) {
			//check if Network enable
			if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) provider = LocationManager.NETWORK_PROVIDER;
			else{
				Toast toast = Toast.makeText(context, context.getResources().getString(R.string.network_off), Toast.LENGTH_SHORT); 
				toast.show();
			}
			provider = LocationManager.NETWORK_PROVIDER;
		}
		else if (providers.equals("Best provider")){
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
		    provider = locationManager.getBestProvider(criteria, false);
		}
	}

	public Location getLocation(){
	// Get the location manager
		setProvider(preferences.getString("providers", "Best provider"));
		Log.i("DEBUG", " provider:" + preferences.getString("providers", "Best provider"));

		locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, this);
		
	    if (locationManager != null) {
	    	location = locationManager
                    .getLastKnownLocation(provider);
            if (location != null) {
            	latitude = location.getLatitude();
            	longitude = location.getLongitude();
        	    onLocationChanged(location);
        	        	       
            }    
	    }
	    //locationManager.removeUpdates(this);
	    return location;
	}
	
	public void LocationUpdate(){
		locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, this);
	}
	

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
	
	//unregistering updates after stop service
	@Override
	public void Unregister() {		
		locationManager.removeUpdates(this);
	}
	
}
