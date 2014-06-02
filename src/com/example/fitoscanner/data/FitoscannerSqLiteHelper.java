package com.example.fitoscanner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FitoscannerSqLiteHelper extends SQLiteOpenHelper {

	// DatabaseName
	private static final String DATABASE_NAME = "fitoscanner.db";
	private static final int DATABASE_VERSION = 1;
	
	// define the script to create table

	
	private static final String DATABASE_CREATE_REGISTER = " create table "
			+ SamplesSQLiteTable.TABLE + "( " 
			+ SamplesSQLiteTable.COLUMN_CATEGORY_ID + " integer primary key, " 
			+ SamplesSQLiteTable.COLUMN_CATEGORY_DESCRIPTION + " text not null, "
			+ SamplesSQLiteTable.COLUMN_CATEGORY_ORDER + " integer, "
			+ SamplesSQLiteTable.COLUMN_CATEGORY_IMAGE_ID + " integer, " 
			+ SamplesSQLiteTable.COLUMN_CATEGORY_LOCATION_PORTAL_ID + " integer"
			+ " );";
		
	private static final String DATABASE_CREATE_IMAGE = " create table "
			+ ImageSQLiteTable.TABLE + "( " 
			+ ImageSQLiteTable.COLUMN_IMAGE_ID + " integer primary key, " 
			+ ImageSQLiteTable.COLUMN_IMAGE_TITLE+ " text , "
			+ ImageSQLiteTable.COLUMN_IMAGE_DESCRIPTION+ " text , "
			+ ImageSQLiteTable.COLUMN_IMAGE_BASE64 + " text not null"
			+ " );";


	
//	private static final String DATABASE_CREATE_CONFIGURATION = " create table "
//			+ ConfigurationSQLiteTable.TABLE + "( " 
//			+ ConfigurationSQLiteTable.COLUMN_CONFIGURATION_ID + " integer primary key autoincrement, " 
//			+ ConfigurationSQLiteTable.COLUMN_CONFIGURATION_LAST_SEARCH + " text not null, "
//			+ ConfigurationSQLiteTable.COLUMN_CONFIGURATION_PUBLICITY_ENABLED + " integer not null "
//			+ " );";
	
	
	public FitoscannerSqLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_REGISTER);
//		database.execSQL(DATABASE_CREATE_CONTENT);
//		database.execSQL(DATABASE_CREATE_CONTENT_INFO);
		database.execSQL(DATABASE_CREATE_IMAGE);
//		database.execSQL(DATABASE_CREATE_CONFIGURATION);		
		Log.d("Database Fitscanners:", "OnCreate executed");		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
//		database.execSQL("DROP TABLE IF EXISTS " + ContentSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + SamplesSQLiteTable.TABLE);
//		database.execSQL("DROP TABLE IF EXISTS " + ContentInfoSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + ImageSQLiteTable.TABLE);
//		database.execSQL("DROP TABLE IF EXISTS " + ConfigurationSQLiteTable.TABLE);
		onCreate(database);
		Log.d("Database Fitoscanner:", "OnUpgrade executed");		
	}
}

