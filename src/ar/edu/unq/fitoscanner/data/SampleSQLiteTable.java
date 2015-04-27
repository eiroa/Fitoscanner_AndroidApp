package ar.edu.unq.fitoscanner.data;

public class SampleSQLiteTable {
	
	// Table name
	public static final String TABLE = "samples";
	
	// Column names
	public static final String COLUMN_SAMPLE_ID = "_id";
	public static final String COLUMN_SAMPLE_ORIGIN_DATE = "originDate";
	public static final String COLUMN_SAMPLE_FIELD_NAME = "fieldName";
	public static final String COLUMN_SAMPLE_NAME = "sampleName";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_CITY = "city";
	public static final String COLUMN_STATE = "state";
	public static final String COLUMN_COUNTRY = "country";
	public static final String COLUMN_HASH = "hash";
	public static final String COLUMN_SENT = "sent";
	public static final String COLUMN_TREATMENT_RESOLUTION_ID= "treatmentResolutionId";
	

	
	public static String[] ALL_COLUMNS = {
			SampleSQLiteTable.COLUMN_SAMPLE_ID,
			SampleSQLiteTable.COLUMN_SAMPLE_ORIGIN_DATE,
			SampleSQLiteTable.COLUMN_SAMPLE_FIELD_NAME,
			SampleSQLiteTable.COLUMN_SAMPLE_NAME,
			SampleSQLiteTable.COLUMN_LATITUDE,
			SampleSQLiteTable.COLUMN_LONGITUDE,
			SampleSQLiteTable.COLUMN_CITY,
			SampleSQLiteTable.COLUMN_STATE,
			SampleSQLiteTable.COLUMN_COUNTRY,
			SampleSQLiteTable.COLUMN_HASH,
			SampleSQLiteTable.COLUMN_SENT,
			SampleSQLiteTable.COLUMN_TREATMENT_RESOLUTION_ID};
	

}
