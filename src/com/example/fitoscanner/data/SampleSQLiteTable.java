package com.example.fitoscanner.data;

public class SampleSQLiteTable {
	
	// Table name
	public static final String TABLE = "samples";
	
	// Column names
	public static final String COLUMN_SAMPLE_ID = "_id";
	public static final String COLUMN_SAMPLE_ORIGIN_DATE = "originDate";
	public static final String COLUMN_SAMPLE_FIELD_NAME = "fieldName";
	public static final String COLUMN_SAMPLE_NAME = "sampleName";

	
	public static String[] ALL_COLUMNS = {
			SampleSQLiteTable.COLUMN_SAMPLE_ID,
			SampleSQLiteTable.COLUMN_SAMPLE_ORIGIN_DATE,
			SampleSQLiteTable.COLUMN_SAMPLE_FIELD_NAME,
			SampleSQLiteTable.COLUMN_SAMPLE_NAME};
	

}
