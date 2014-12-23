package com.example.simplegpstracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.example.simplegpstracker.db.GPSInfoHelper;
import com.example.simplegpstracker.entity.GPSInfo;

import android.app.Service;
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
	private SharedPreferences preferences;

	private int refreshTime;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    
    GPSInfoHelper helper;
    LocationLoader locationLoader;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
 
    @Override
    public void onCreate() {
        // cancel if already existed
    	preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	refreshTime = Integer.parseInt(preferences.getString("refreshTime", "5000"));
        helper = new GPSInfoHelper(getApplicationContext());
        helper.cleanOldRecords();
        locationLoader = new LocationLoader(getApplicationContext());
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, refreshTime * 1000);
    }
    
    public int onStartCommand(Intent intent, int flags, int startId) {       
        return START_STICKY;
      }
 
    class TimeDisplayTimerTask extends TimerTask {
 
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
 
                @Override
                public void run() {

            		Location location = locationLoader.getLocation();
            		if (location != null){
            		GPSInfo info = new GPSInfo();
            		
            		info.setId(1);
            		info.setLongitude(location.getLongitude());
            		info.setLatitude(location.getLatitude());
            		info.setAccuracy(location.getAccuracy());
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
    
    public void onDestroy() {
        super.onDestroy();
        helper.closeDB();
        mTimer.cancel();
        Log.d("DEBUG", "MyService onDestroy");
      }
}
