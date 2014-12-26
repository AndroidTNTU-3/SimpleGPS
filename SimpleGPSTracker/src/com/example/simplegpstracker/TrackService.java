package com.example.simplegpstracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.simplegpstracker.GetPoliLine.PoliLoaderCallBack;
import com.example.simplegpstracker.db.GPSInfoHelper;
import com.example.simplegpstracker.entity.GPSInfo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/////////////////////////////////////
//Service that call LocationLoader class
//and stored a location to database
////////////////////////////////////

public class TrackService extends Service {
	
	public static interface UnregisterCallBack{
		public void Unregister();
	}
	
	UnregisterCallBack unregisterCallBack;
	
	private SharedPreferences preferences;
	
	private int refreshTime = 5;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    
    GPSInfoHelper helper;
    LocationLoader locationLoader;
    SensorScanner sensor;
    Context context;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
 
    @Override
    public void onCreate() {
        // cancel if already existed
    	preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	refreshTime = Integer.parseInt(preferences.getString("refreshTime", "5"));
    	//refreshTime = preferences.getInt("refreshTime", 5);
    	Log.i("DEBUG", " Time:" + refreshTime);
        helper = new GPSInfoHelper(getApplicationContext());
        helper.cleanOldRecords();
        
        context = getApplicationContext();
        
        locationLoader = new LocationLoader(context, this);
        sensor = new SensorScanner(this);
        
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        
        ///// schedule task
        //check if any provider is enabled
        if(locationLoader.IsProviderEnable()) mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, refreshTime * 1000);
        //else service will be stopped
        else this.stopSelf();

    }
    
    public int onStartCommand(Intent intent, int flags, int startId) {       
        return START_STICKY;
      }
 
    class TimeDisplayTimerTask extends TimerTask {
 
        @Override
        public void run() {
            // run on another thread
        	//if(locationLoader.IsProviderEnable()) TrackService.this.stopSelf();
            mHandler.post(new Runnable() {
 
                @Override
                public void run() {

            		Location location = locationLoader.getLocation();
            		GPSInfo info = new GPSInfo();
            		info = sensor.GetSensorValue(context);
            		if (location != null){
            		
            		info.setId(1);
            		info.setLongitude(location.getLongitude());
            		info.setLatitude(location.getLatitude());
            		info.setAccuracy(location.getAccuracy());
            		info.setAcceleration(info.getAcceleration());
            		info.setTitle("Track1");
            		info.setTime(System.currentTimeMillis());
            		
            		helper.insert(info);
            		Log.i("DEBUG", "Inserted");
            		}
                }
 
            });
        }
 
        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }
    }
    
  //registering callback
  	public void setCallBack(UnregisterCallBack unregisterCallBack) {
  		this.unregisterCallBack = unregisterCallBack;
  	}
    
    public void onDestroy() {
        super.onDestroy();
        locationLoader.Unregister();
        sensor.Unregister();
        helper.closeDB();
        mTimer.cancel();
        Log.d("DEBUG", "MyService onDestroy");
      }
}
