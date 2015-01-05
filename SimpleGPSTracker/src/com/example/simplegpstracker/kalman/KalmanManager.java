package com.example.simplegpstracker.kalman;

import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

public class KalmanManager {
	
    /**
     * Provider string assigned to predicted Location objects.
     */
    public static final String KALMAN_PROVIDER = "kalman";
	
	// Static constant
    private static final int THREAD_PRIORITY = 5;

    private static final double DEG_TO_METER = 111225.0;
    private static final double METER_TO_DEG = 1.0 / DEG_TO_METER;

    private static final double TIME_STEP = 5.0;
    private static final double COORDINATE_NOISE = 4.0 * METER_TO_DEG;
    private static final double ALTITUDE_NOISE = 10.0;
    
    /**
     * Three 1-dimension trackers, since the dimensions are independent and can avoid using matrices.
     */
    private Tracker1D mLatitudeTracker, mLongitudeTracker, mAltitudeTracker;
	
    private Location location;
	private Location mLastLocation;
	
	public KalmanManager(){
		
	}
	
	public void setParam(Location location){
		// Reusable
		this.location = location;
        final double accuracy = location.getAccuracy();
        double position, noise;

        // Latitude
        position = location.getLatitude();
        noise = accuracy * METER_TO_DEG;

        if (mLatitudeTracker == null) {

            mLatitudeTracker = new Tracker1D(TIME_STEP, COORDINATE_NOISE);
            mLatitudeTracker.setState(position, 0.0, noise);
        }

        mLatitudeTracker.update(position, noise);

        // Longitude
        position = location.getLongitude();
        noise = accuracy * Math.cos(Math.toRadians(location.getLatitude())) * METER_TO_DEG ;

        if (mLongitudeTracker == null) {

            mLongitudeTracker = new Tracker1D(TIME_STEP, COORDINATE_NOISE);
            mLongitudeTracker.setState(position, 0.0, noise);
        }

        mLongitudeTracker.update(position, noise);
        
        // Update last location
        if (mLastLocation == null ) {

            mLastLocation = new Location(location);
        }
	}
	
	public Location getKalmanLocation(){
		// Calculate prediction
        mLongitudeTracker.predict(0.0);

        /*if (mLastLocation.hasAltitude())
            mAltitudeTracker.predict(0.0);*/

        // Prepare location
        final Location location = new Location(KALMAN_PROVIDER);

        // Latitude
        mLatitudeTracker.predict(0.0);
        location.setLatitude(mLatitudeTracker.getPosition());

        // Longitude
        mLongitudeTracker.predict(0.0);
        location.setLongitude(mLongitudeTracker.getPosition());
        
	    
	    Log.i("DEBUG", " lon In kalman:" + Double.toString(location.getLatitude()));
		
		Log.i("DEBUG", " lon In kalman:" + Double.toString(location.getLongitude()));

        // Altitude
        /*if (mLastLocation.hasAltitude()) {

            mAltitudeTracker.predict(0.0);
            location.setAltitude(mAltitudeTracker.getPosition());
        }*/

        // Speed
        /*if (mLastLocation.hasSpeed())
            location.setSpeed(mLastLocation.getSpeed());

        // Bearing
        if (mLastLocation.hasBearing())
            location.setBearing(mLastLocation.getBearing());

        // Accuracy (always has)
        location.setAccuracy((float) (mLatitudeTracker.getAccuracy() * DEG_TO_METER));*/

        // Set times
        location.setTime(System.currentTimeMillis());

       /* if (Build.VERSION.SDK_INT >= 17)
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());*/
        
        return location;
	}

}
