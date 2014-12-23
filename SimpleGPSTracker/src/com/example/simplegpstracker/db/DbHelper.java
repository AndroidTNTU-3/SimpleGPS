package com.example.simplegpstracker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


///////////////////////////////////////
//DB Helper: operation with DB tables//	
///////////////////////////////////////

public class DbHelper extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gpstrackerbase";
    public static final String TRACKER_DB_TABLE = "trackerBase";
    
    
    public static final String TRACKER_DB_ID = "cid";
    public static final String TRACKER_DB_LATITUDE = "latitude";
    public static final String TRACKER_DB_LONGITUDE = "longitude";
    public static final String TRACKER_DB_ACCURACY = "accuracy";
    public static final String TRACKER_DB_TITLE = "title";
    public static final String TRACKER_DB_TIME = "time";
    
    
    public static final String CREATE_DB_TRACKER_TABLE = "CREATE TABLE IF NOT EXISTS " + TRACKER_DB_TABLE
            + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + TRACKER_DB_ID + " TEXT," + TRACKER_DB_LATITUDE + " DOUBLE," + TRACKER_DB_LONGITUDE + " DOUBLE," + TRACKER_DB_ACCURACY + " FLOAT," + TRACKER_DB_TITLE + " TEXT," + TRACKER_DB_TIME + " LONG);";
    

	public DbHelper(Context context) {
		super(context, DbHelper.DATABASE_NAME, null, DbHelper.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DB_TRACKER_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_DB_TRACKER_TABLE);
	}

}
