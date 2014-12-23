package com.example.simplegpstracker.db;

import java.util.ArrayList;
import java.util.List;

import com.example.simplegpstracker.entity.GPSInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GPSInfoHelper {
	
	private SQLiteDatabase db;
	
	public GPSInfoHelper(Context context) {
        DbHelper openHelper = new DbHelper(context);
        db = openHelper.getWritableDatabase();
	}
	
    public long insert(GPSInfo gpsInfo) {   
        ContentValues values = getValues(gpsInfo);
        return db.insert(DbHelper.TRACKER_DB_TABLE, null, values);
    }
    
    private ContentValues getValues(GPSInfo gpsInfo) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.TRACKER_DB_ID, gpsInfo.getId());
        values.put(DbHelper.TRACKER_DB_LATITUDE, gpsInfo.getLatitude());
        values.put(DbHelper.TRACKER_DB_LONGITUDE, gpsInfo.getLongitude());
        values.put(DbHelper.TRACKER_DB_ACCURACY, gpsInfo.getAccuracy());
        values.put(DbHelper.TRACKER_DB_TITLE, gpsInfo.getTitle());
        values.put(DbHelper.TRACKER_DB_TIME, gpsInfo.getTime());
        return values;
    }
    
    public List<GPSInfo> getGPSPoint() {

    	List<GPSInfo> list = null;
		String sql = "SELECT * FROM " + DbHelper.TRACKER_DB_TABLE;
		
		Cursor cursor = db.rawQuery(sql, null);
		
		if (cursor.getCount() != 0) {

			list = new ArrayList<GPSInfo>();
			cursor.moveToFirst();
			do {
				GPSInfo gpsInfo = new GPSInfo();
				gpsInfo.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.TRACKER_DB_ID)));
				gpsInfo.setLatitude(cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_LATITUDE)));
				gpsInfo.setLongitude(cursor.getDouble(cursor.getColumnIndex(DbHelper.TRACKER_DB_LONGITUDE)));
				gpsInfo.setAccuracy(cursor.getFloat(cursor.getColumnIndex(DbHelper.TRACKER_DB_ACCURACY)));
				gpsInfo.setTitle(cursor.getString(cursor.getColumnIndex(DbHelper.TRACKER_DB_TITLE)));
				gpsInfo.setTime(cursor.getLong(cursor.getColumnIndex(DbHelper.TRACKER_DB_TIME)));
				list.add(gpsInfo);
			} while(cursor.moveToNext());	

		}
		
		if (cursor != null) cursor.close();
		return list;
	}

	public void cleanOldRecords() {
        db.delete(DbHelper.TRACKER_DB_TABLE, null, null);
    }
	
	public void closeDB() {
        db.close();
	}
}
