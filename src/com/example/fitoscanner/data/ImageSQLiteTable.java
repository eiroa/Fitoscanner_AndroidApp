package com.example.fitoscanner.data;

public class ImageSQLiteTable {

	// Table name
	public static final String TABLE = "image";
	
	// Column names
	public static final String COLUMN_IMAGE_ID = "_id";
	public static final String COLUMN_IMAGE_NAME = "name";
	public static final String COLUMN_IMAGE_BASE64= "base64";
	
	public static String[] ALL_COLUMNS = {
			ImageSQLiteTable.COLUMN_IMAGE_ID,
			ImageSQLiteTable.COLUMN_IMAGE_NAME,
			ImageSQLiteTable.COLUMN_IMAGE_BASE64};
		
}
