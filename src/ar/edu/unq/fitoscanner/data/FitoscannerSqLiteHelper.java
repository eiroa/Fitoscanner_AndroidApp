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
			+ SampleSQLiteTable.COLUMN_SENT + " integer, "
			+ SampleSQLiteTable.COLUMN_TREATMENT_RESOLUTION_ID + " integer"
			+ " );";
		
	private static final String DATABASE_CREATE_IMAGE = " create table "
			+ ImageSQLiteTable.TABLE + "( " 
			+ ImageSQLiteTable.COLUMN_IMAGE_ID + " integer primary key autoincrement, "
			+ ImageSQLiteTable.COLUMN_IMAGE_SAMPLE_ID+ " integer , "
			+ ImageSQLiteTable.COLUMN_IMAGE_TREATMENT_ID+ " integer , "
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
	
	
	private static final String DATABASE_CREATE_TREATMENT= " create table "
			+ TreatmentSQLiteTable.TABLE + "( " 
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_ID + " integer primary key, " 
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_NAME + " text , "
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_DESCRIPTION + " text, "
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_ID_IMAGES + " text, "
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_UNIT + " text, " 
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_UNIT_TYPE+ " text, "
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_FREQUENCY+ " text, " 
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_FREQUENCY_TYPE+ " text, " 
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_EXTRA_LINK_1+ " text, " 
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_EXTRA_LINK_2+ " text, " 
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_EXTRA_LINK_3+ " text" 
			+ " );";
	
	private static final String DATABASE_CREATE_TREATMENT_RESOLUTION= " create table "
			+ TreatmentResolutionSQLiteTable.TABLE + "( " 
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID + " integer primary key, " 
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_NAME + " text , "
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_DESCRIPTION + " text, "
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID_SPECIE_IMAGES + " text, "
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_NAME + " text, " 
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_SCIENTIFIC_NAME + " text, "
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_DESCRIPTION+ " text, " 
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_VALID+ " integer, " 
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_RESOLVED+ " integer, " 
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_MESSAGE+ " text, " 
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_TREATMENT_IDS+ " text" 
			+ " );";
	
	
	public FitoscannerSqLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_TREATMENT);
		database.execSQL(DATABASE_CREATE_TREATMENT_RESOLUTION);
		database.execSQL(DATABASE_CREATE_SAMPLE);
		database.execSQL(DATABASE_CREATE_IMAGE);
		database.execSQL(DATABASE_CREATE_CONFIGURATION);	
		Log.d("Database Fitoscanner:", "OnCreate executed");		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TreatmentSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + TreatmentResolutionSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + SampleSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + ImageSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + ConfigurationSQLiteTable.TABLE);
		onCreate(database);
		Log.d("Database Fitoscanner:", "OnUpgrade executed");		
	}
}

