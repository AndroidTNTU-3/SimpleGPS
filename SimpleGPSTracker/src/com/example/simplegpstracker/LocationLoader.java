package com.example.simplegpstracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.simplegpstracker.TrackService.UnregisterCallBack;
import com.google.android.gms.maps.model.LatLng;

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
	
	//the minimum time interval for notifications, 
    //in milliseconds
	private final static int MIN_TIME = 400;
	
	//the minimum distance interval for notifications, in meters
	private final static int MIN_DISTANCE = 5;
		
	TrackService service;
	
	public static interface LocationLoaderCallBack{
		public void setLocation(String cityId);
	}
		
	  private LocationManager locationManager;
	  private String provider = "GPS";
	  private Context context;
	  private Location location;
	  private double latitude;
	  private double longitude; 
	  private SharedPreferences preferences;
	  	
	public LocationLoader(Context context, TrackService service){
		this.context = context;
		this.service = service;
		service.setCallBack(this);
		//get a selected provider 
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		provider = preferences.getString("providers", "Network");
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);	
	}
	
	private void setProvider(String providers) {
		
		if (providers.equals("GPS")) provider = LocationManager.GPS_PROVIDER;
		else if (providers.equals("Network")) provider = LocationManager.NETWORK_PROVIDER;
		else if (providers.equals("Best provider")){
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				provider = locationManager.getBestProvider(criteria, false);
		}
	}
	
	//check if selected provider enabled
	public boolean IsProviderEnable(){
		//check if GPS enable
		if(provider.equals("GPS")){
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return true;
			else {
				Toast toast = Toast.makeText(context, context.getResources().getString(R.string.gps_off), Toast.LENGTH_SHORT); 
				toast.show(); 
			}
		//check if Network enable
		} else if(provider.equals("Network")){
			if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) return true;
			else{
				Toast toast = Toast.makeText(context, context.getResources().getString(R.string.network_off), Toast.LENGTH_SHORT); 
				toast.show();
			}
		} else if(provider.equals("Best provider")) {
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) return true;
		}
		return false;		
	}

	public Location getLocation(){
	// Get the location manager
		setProvider(provider);
		Log.i("DEBUG", " provider:" + provider);

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
	    
	    Log.i("DEBUG", " lon:" + Double.toString(latitude));
		
		Log.i("DEBUG", " lat:" + Double.toString(longitude));
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
