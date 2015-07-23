package ar.edu.unq.fitoscanner.data;

public class TreatmentSQLiteTable {

	// Table name
	public static final String TABLE = "treatment";
	
	// Column names
	public static final String COLUMN_TREATMENT_ID = "_id";
	public static final String COLUMN_TREATMENT_NAME = "idSample";
	public static final String COLUMN_TREATMENT_DESCRIPTION = "description";
	public static final String COLUMN_TREATMENT_ID_IMAGES = "idImages";
	public static final String COLUMN_TREATMENT_UNIT = "unit";
	public static final String COLUMN_TREATMENT_UNIT_TYPE= "unitType";
	public static final String COLUMN_TREATMENT_FREQUENCY= "frequency";
	public static final String COLUMN_TREATMENT_FREQUENCY_TYPE= "frequencyType";
	public static final String COLUMN_TREATMENT_EXTRA_LINK_1= "extraLink1";
	public static final String COLUMN_TREATMENT_EXTRA_LINK_2= "extraLink2";
	public static final String COLUMN_TREATMENT_EXTRA_LINK_3= "extraLink3";
	public static final String COLUMN_TREATMENT_USE_EXPLANATION= "useExplanation";
	
	public static String[] ALL_COLUMNS = {
			TreatmentSQLiteTable.COLUMN_TREATMENT_ID,
			TreatmentSQLiteTable.COLUMN_TREATMENT_NAME,
			TreatmentSQLiteTable.COLUMN_TREATMENT_DESCRIPTION,
			TreatmentSQLiteTable.COLUMN_TREATMENT_ID_IMAGES,
			TreatmentSQLiteTable.COLUMN_TREATMENT_UNIT,
			TreatmentSQLiteTable.COLUMN_TREATMENT_UNIT_TYPE,
			TreatmentSQLiteTable.COLUMN_TREATMENT_FREQUENCY,
			TreatmentSQLiteTable.COLUMN_TREATMENT_FREQUENCY_TYPE,
			TreatmentSQLiteTable.COLUMN_TREATMENT_EXTRA_LINK_1,
			TreatmentSQLiteTable.COLUMN_TREATMENT_EXTRA_LINK_2,
			TreatmentSQLiteTable.COLUMN_TREATMENT_EXTRA_LINK_3,
			TreatmentSQLiteTable.COLUMN_TREATMENT_USE_EXPLANATION
			};
		
}
