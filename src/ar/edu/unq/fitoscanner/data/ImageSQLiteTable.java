package ar.edu.unq.fitoscanner.data;

public class ImageSQLiteTable {

	// Table name
	public static final String TABLE = "image";
	
	// Column names
	public static final String COLUMN_IMAGE_ID = "_id";
	public static final String COLUMN_IMAGE_SAMPLE_ID = "idSample";
	public static final String COLUMN_IMAGE_TREATMENT_ID = "idTreatment";
	public static final String COLUMN_IMAGE_TREATMENT_RESOLUTION_ID = "idTreatmentResolution";
	public static final String COLUMN_IMAGE_TITLE = "title";
	public static final String COLUMN_IMAGE_DESCRIPTION = "description";
	public static final String COLUMN_IMAGE_BASE64= "base64";
	public static final String COLUMN_IMAGE_SENT= "sent";
	
	public static String[] ALL_COLUMNS = {
			ImageSQLiteTable.COLUMN_IMAGE_ID,
			ImageSQLiteTable.COLUMN_IMAGE_SAMPLE_ID,
			ImageSQLiteTable.COLUMN_IMAGE_TREATMENT_ID,
			ImageSQLiteTable.COLUMN_IMAGE_TREATMENT_RESOLUTION_ID,
			ImageSQLiteTable.COLUMN_IMAGE_TITLE,
			ImageSQLiteTable.COLUMN_IMAGE_DESCRIPTION,
			ImageSQLiteTable.COLUMN_IMAGE_BASE64,
			ImageSQLiteTable.COLUMN_IMAGE_SENT};
		
}
