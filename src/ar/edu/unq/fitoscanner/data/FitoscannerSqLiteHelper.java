package ar.edu.unq.fitoscanner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FitoscannerSqLiteHelper extends SQLiteOpenHelper {

	// DatabaseName
	private static final String DATABASE_NAME = "fitoscanner.db";
	private static final int DATABASE_VERSION = 1;
	
	// define the script to create table

	
	private static final String DATABASE_CREATE_SAMPLE = " create table "
			+ SampleSQLiteTable.TABLE + "( " 
			+ SampleSQLiteTable.COLUMN_SAMPLE_ID + " integer primary key autoincrement, " 
			+ SampleSQLiteTable.COLUMN_SAMPLE_ORIGIN_DATE + " text, "
			+ SampleSQLiteTable.COLUMN_SAMPLE_FIELD_NAME + " text, "
			+ SampleSQLiteTable.COLUMN_SAMPLE_NAME + " text, "
			+ SampleSQLiteTable.COLUMN_LATITUDE + " text, "
			+ SampleSQLiteTable.COLUMN_LONGITUDE + " text, "
			+ SampleSQLiteTable.COLUMN_CITY + " text, "
			+ SampleSQLiteTable.COLUMN_STATE + " text, "
			+ SampleSQLiteTable.COLUMN_COUNTRY + " text, "
			+ SampleSQLiteTable.COLUMN_HASH + " text, "
			+ SampleSQLiteTable.COLUMN_SENT + " integer"
			+ " );";
		
	private static final String DATABASE_CREATE_IMAGE = " create table "
			+ ImageSQLiteTable.TABLE + "( " 
			+ ImageSQLiteTable.COLUMN_IMAGE_ID + " integer primary key autoincrement, "
			+ ImageSQLiteTable.COLUMN_IMAGE_SAMPLE_ID+ " integer , "
			+ ImageSQLiteTable.COLUMN_IMAGE_TITLE+ " text , "
			+ ImageSQLiteTable.COLUMN_IMAGE_DESCRIPTION+ " text , "
			+ ImageSQLiteTable.COLUMN_IMAGE_BASE64 + " text not null"
			+ " );";


	
	private static final String DATABASE_CREATE_CONFIGURATION = " create table "
			+ ConfigurationSQLiteTable.TABLE + "( " 
			+ ConfigurationSQLiteTable.COLUMN_CONFIGURATION_ID + " integer primary key, " 
			+ ConfigurationSQLiteTable.COLUMN_SERVER_IP + " text , "
			+ ConfigurationSQLiteTable.COLUMN_USER_NICK + " text, "
			+ ConfigurationSQLiteTable.COLUMN_USER_PASS + " text, " 
			+ ConfigurationSQLiteTable.COLUMN_USER_NAME+ " text, " 
			+ ConfigurationSQLiteTable.COLUMN_USER_SURNAME + " text" 
			+ " );";
	
	
	public FitoscannerSqLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_SAMPLE);
		database.execSQL(DATABASE_CREATE_IMAGE);
		database.execSQL(DATABASE_CREATE_CONFIGURATION);		
		Log.d("Database Fitoscanner:", "OnCreate executed");		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + SampleSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + ImageSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + ConfigurationSQLiteTable.TABLE);
		onCreate(database);
		Log.d("Database Fitoscanner:", "OnUpgrade executed");		
	}
}

