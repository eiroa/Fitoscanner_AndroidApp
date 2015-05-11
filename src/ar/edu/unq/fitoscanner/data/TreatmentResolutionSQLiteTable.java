package ar.edu.unq.fitoscanner.data;

public class TreatmentResolutionSQLiteTable {

	// Table name
	public static final String TABLE = "treatmentResolution";
	
	// Column names
	public static final String COLUMN_TREATMENTR_ID = "_id";
	public static final String COLUMN_TREATMENTR_SPECIE_NAME = "specieName";
	public static final String COLUMN_TREATMENTR_SPECIE_SCIENTIFIC_NAME= "specieScientificName";
	public static final String COLUMN_TREATMENTR_SPECIE_DESCRIPTION= "specieDescription";
	public static final String COLUMN_TREATMENTR_ID_SPECIE_IMAGES = "idSpecieImages";
	public static final String COLUMN_TREATMENTR_VALID= "isValid";
	public static final String COLUMN_TREATMENTR_RESOLVED= "isResolved";
	public static final String COLUMN_TREATMENTR_MESSAGE= "message";
	public static final String COLUMN_TREATMENTR_TREATMENT_IDS= "treatmentIds";
	
	public static String[] ALL_COLUMNS = {
			TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID,
			TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_NAME,
			TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_SCIENTIFIC_NAME,
			TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_DESCRIPTION,
			TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID_SPECIE_IMAGES,
			TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_VALID,
			TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_RESOLVED,
			TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_MESSAGE,
			TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_TREATMENT_IDS
			};
		
}
