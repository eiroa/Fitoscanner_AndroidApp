package com.example.fitoscanner.data;

public class RegistersSQLiteTable {
	
	// Table name
	public static final String TABLE = "category";
	
	// Column names
	public static final String COLUMN_CATEGORY_ID = "_id";
	public static final String COLUMN_CATEGORY_DESCRIPTION = "description";
	public static final String COLUMN_CATEGORY_ORDER = "orden";
	public static final String COLUMN_CATEGORY_IMAGE_ID = "imageId";
	public static final String COLUMN_CATEGORY_LOCATION_PORTAL_ID = "locationPortalId";
	
	public static String[] ALL_COLUMNS = {
			RegistersSQLiteTable.COLUMN_CATEGORY_ID,
			RegistersSQLiteTable.COLUMN_CATEGORY_DESCRIPTION,
			RegistersSQLiteTable.COLUMN_CATEGORY_ORDER,
			RegistersSQLiteTable.COLUMN_CATEGORY_IMAGE_ID,
			RegistersSQLiteTable.COLUMN_CATEGORY_LOCATION_PORTAL_ID};
	

}
