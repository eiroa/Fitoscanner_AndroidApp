package ar.edu.unq.fitoscanner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FitoscannerSqLiteHelper extends SQLiteOpenHelper {

	// DatabaseName
	public static final String DATABASE_NAME = "fitoscanner.db";
	public static final int DATABASE_VERSION = 3;
	public static final String TAG = "Fitoscanner Database";
	private String sqlScriptToExecute;
	private boolean doFullUpgrade;
	private Context context;
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
			+ SampleSQLiteTable.COLUMN_TREATMENT_RESOLUTION_ID + " integer, "
			+ SampleSQLiteTable.COLUMN_SAMPLE_REQUEST_TREATMENT_INTENTS + " integer, "
			+ SampleSQLiteTable.COLUMN_SAMPLE_MINUTES_FROM_LAST_REQUEST + " integer, "
			+ SampleSQLiteTable.COLUMN_RESOLVED + " integer, "
			+ SampleSQLiteTable.COLUMN_VALID + " integer"
			+ " );";
		
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}


	private static final String DATABASE_CREATE_IMAGE = " create table "
			+ ImageSQLiteTable.TABLE + "( " 
			+ ImageSQLiteTable.COLUMN_IMAGE_ID + " integer primary key , "
			+ ImageSQLiteTable.COLUMN_IMAGE_SAMPLE_ID+ " integer , "
			+ ImageSQLiteTable.COLUMN_IMAGE_TREATMENT_ID+ " integer , "
			+ ImageSQLiteTable.COLUMN_IMAGE_TREATMENT_RESOLUTION_ID+ " integer , "
			+ ImageSQLiteTable.COLUMN_IMAGE_TITLE+ " text , "
			+ ImageSQLiteTable.COLUMN_IMAGE_DESCRIPTION+ " text , "
			+ ImageSQLiteTable.COLUMN_IMAGE_BASE64 + " text not null , "
			+ ImageSQLiteTable.COLUMN_IMAGE_SENT+ " integer"
			+ " );";


	
	private static final String DATABASE_CREATE_CONFIGURATION = " create table "
			+ ConfigurationSQLiteTable.TABLE + "( " 
			+ ConfigurationSQLiteTable.COLUMN_CONFIGURATION_ID + " integer primary key , " 
			+ ConfigurationSQLiteTable.COLUMN_SERVER_IP + " text , "
			+ ConfigurationSQLiteTable.COLUMN_USER_NICK + " text, "
			+ ConfigurationSQLiteTable.COLUMN_USER_PASS + " text, " 
			+ ConfigurationSQLiteTable.COLUMN_USER_NAME+ " text, " 
			+ ConfigurationSQLiteTable.COLUMN_USER_SURNAME + " text, "
			+ ConfigurationSQLiteTable.COLUMN_USER_DATABASE_VERSION+ " integer,"
			+ ConfigurationSQLiteTable.COLUMN_USER_LOGGED+ " integer"
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
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_EXTRA_LINK_3+ " text, "
			+ TreatmentSQLiteTable.COLUMN_TREATMENT_USE_EXPLANATION+ " text"
			+ " );";
	
	private static final String DATABASE_CREATE_TREATMENT_RESOLUTION= " create table "
			+ TreatmentResolutionSQLiteTable.TABLE + "( " 
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID + " integer primary key , " 
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_NAME + " text, " 
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_SCIENTIFIC_NAME + " text, "
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_DESCRIPTION+ " text, " 
			+ TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID_SPECIE_IMAGES + " text, "
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
		database.setVersion(DATABASE_VERSION);
		Log.d(TAG, "OnCreate executed");		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		
		//full upgrade
		if(database.getVersion() == 1){
			
			Log.d(TAG, "Db is in version 1. Full upgrading database");	
			resetTables(database);
		}else{
			initiateDatabaseUpgrade(database, oldVersion, newVersion);
//			database.setVersion(version);
		}
	}
	
	public void resetTables(SQLiteDatabase database){
		database.execSQL("DROP TABLE IF EXISTS " + TreatmentSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + TreatmentResolutionSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + SampleSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + ImageSQLiteTable.TABLE);
		database.execSQL("DROP TABLE IF EXISTS " + ConfigurationSQLiteTable.TABLE);
		onCreate(database);
	}
	
	public void updateDatabase(SQLiteDatabase database,int oldVersion, int newVersion,boolean dropTablesFirst, Context cxt){
		this.context = cxt;
		this.onUpgrade(database, oldVersion, newVersion);
	}
	
	
	public void initiateDatabaseUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
		int currentVersion = oldVersion;
		while (currentVersion < newVersion) {
			Log.d(TAG, "Upgrading to version => "+ (currentVersion+1));		
			sqlScriptToExecute = FitoScannerIncrementalScripts.INCREMENTAL_SCRIPTS.get(currentVersion);
			Log.d(TAG, "Executing script for version "+ currentVersion +": "+sqlScriptToExecute);	
			database.execSQL(sqlScriptToExecute);
			currentVersion = currentVersion + 1;
			database.setVersion(currentVersion);
			Log.d(TAG, "DB upgraded to version "+currentVersion);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

