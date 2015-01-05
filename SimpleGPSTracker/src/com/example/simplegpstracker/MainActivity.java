package com.example.simplegpstracker;

import java.util.Date;

import com.example.simplegpstracker.preference.PrefActivity;
import com.example.simplegpstracker.preference.PreferenceActivityP;
import com.example.simplegpstracker.utils.UtilsNet;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {
	//preferences
	private SharedPreferences preferences;
	private String provider;
	private String travelMode;
	private int refreshTime;
	private String status;
	
	private boolean isGPSEnabled;
	private boolean isNetworkEnabled;
	
	BroadcastReceiver br;

	Button bViewMap;
	Button bStartService;
	Button bStopService;
	Button bSendService;
	
	TextView tvProviders;
	TextView tvTravelMode;
	TextView tvRefreshTime;
	TextView tvGPSStatus;
	TextView tvNetworkStatus;
	
	LocationManager locationManager;

	Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        context = getApplicationContext();     
        bViewMap = (Button)findViewById(R.id.bViewMap);
        bViewMap.setOnClickListener(new ClickListener());
        bStartService = (Button)findViewById(R.id.bStartService);
        bStartService.setOnClickListener(new ClickListener());
        bStopService = (Button)findViewById(R.id.bStopService);
        bStopService.setOnClickListener(new ClickListener());
        bSendService = (Button)findViewById(R.id.bSendServer);
        bSendService.setOnClickListener(new ClickListener());
        
        tvProviders = (TextView) findViewById(R.id.tvProvidersValue);
        tvTravelMode = (TextView) findViewById(R.id.tvTravelModeValue);
        tvRefreshTime = (TextView) findViewById(R.id.tvRefreshTimeValue);
        tvGPSStatus = (TextView) findViewById(R.id.tvGPSValue);
        tvNetworkStatus = (TextView) findViewById(R.id.tvNetworkValue);
        
    	locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        
        br = new BroadcastReceiver()
        {   
        @Override
          public void onReceive( Context context, Intent intent )
          {
              getStatus();
              setStatusGPS();
              setStatusNetwork();
          }
        };
        context.registerReceiver(br, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	//Intent intentPref = new Intent(this, PrefActivity.class);
        	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            	Intent intentPref = new Intent(this, PrefActivity.class);
            	startActivity(intentPref);
            } else {
            	Intent intentPref = new Intent(this, PreferenceActivityP.class);
            	startActivity(intentPref);
            }
        	
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void getPreferences(){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        provider = preferences.getString("providers", "Network");
        travelMode = preferences.getString("travelMode", "walking");
        refreshTime = Integer.parseInt(preferences.getString("refreshTime", "5"));        
    }
    
    private void getStatus(){

        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.i("DEBUG:", "provider GPS" + isGPSEnabled);
    	isNetworkEnabled = locationManager
    			.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    	Log.i("DEBUG:", "provider Net" + isNetworkEnabled);
    }
    
    private void setStatusGPS(){
    	if(isGPSEnabled) {
        	tvGPSStatus.setText(getResources().getString(R.string.status_enabled));
        	tvGPSStatus.setTextColor(Color.GREEN);
        }
        else if(!isGPSEnabled) {
        	tvGPSStatus.setText(getResources().getString(R.string.status_disabled));
        	tvGPSStatus.setTextColor(Color.BLACK);
        }
        
    }
    
    private void setStatusNetwork(){   	
        if(isNetworkEnabled) {
        	tvNetworkStatus.setText(getResources().getString(R.string.status_enabled));
        	tvNetworkStatus.setTextColor(Color.GREEN);
        }
        else if(!isNetworkEnabled) {
        	tvNetworkStatus.setText(getResources().getString(R.string.status_disabled));
        	tvNetworkStatus.setTextColor(Color.BLACK);
        }
    }
    
    protected void onResume(){
    	super.onResume();
        getPreferences();
        getStatus();
        tvProviders.setText(provider);
        tvTravelMode.setText(travelMode);
        tvRefreshTime.setText(String.valueOf(refreshTime));
        setStatusGPS();
        setStatusNetwork();
    }

    private class ClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case R.id.bStartService:
				Intent iStartService = new Intent(context, TrackService.class);
				startService(iStartService);
				Toast toast_start = Toast.makeText(context, context.getResources().getString(R.string.service_start), Toast.LENGTH_SHORT); 
				toast_start.show(); 
				break;
			case R.id.bStopService:
				Intent iStopService = new Intent(context, TrackService.class);
				stopService(iStopService);
				break;
			case R.id.bViewMap:
				if(!UtilsNet.isOnline(getApplicationContext())){
					Toast toast = Toast.makeText(context, context.getResources().getString(R.string.network_off), Toast.LENGTH_SHORT); 
					toast.show();				
				}else if(UtilsNet.IsServiceRunning(context)){
					Toast toast = Toast.makeText(context, context.getResources().getString(R.string.service_started), Toast.LENGTH_SHORT); 
					toast.show();
				}else{
					Intent iMap = new Intent(context, ViewMapActivity1.class);
					startActivity(iMap);
				}
				break;
			case R.id.bSendServer:
				new Transmitter(context).send();
				break;
			default:
				break;
			}
		
		}
    	
    }   
    
    

}
