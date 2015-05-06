package ar.edu.unq.fitoscanner.datasources;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import ar.edu.unq.fitoscanner.data.FitoscannerSqLiteHelper;
import ar.edu.unq.fitoscanner.data.TreatmentResolutionSQLiteTable;
import ar.edu.unq.fitoscanner.model.TreatmentResolution;

public class TreatmentResolutionDataSource extends AbstractDataSource{
	public final String TAG = "treatmentResolutionDateSource";
	private TreatmentDataSource treatmentDataSource;
	
	public TreatmentDataSource getTreatmentDataSource() {
		return treatmentDataSource;
	}

	public void setTreatmentDataSource(TreatmentDataSource treatmentDataSource) {
		this.treatmentDataSource = treatmentDataSource;
	}

/**
 * Construye un objeto treatMentResolution utilizando la informacion de la base
 * @param cursor
 * @return
 */
	private TreatmentResolution cursorToTreatmentResolution(Cursor cursor) {
		Long id = cursor.getLong(0);
		String specieName = cursor.getString(1);
		String specieScientificName = cursor.getString(2);
		String specieDescription = cursor.getString(3);
		String idSpecieImages = cursor.getString(4);
		Boolean valid = ( 1 == cursor.getInt(5)?true:false);
		Boolean resolved = ( 1 == cursor.getInt(6)?true:false); 
		String message = cursor.getString(7);
		String idTreatments = cursor.getString(8);
				
		TreatmentResolution tr= new TreatmentResolution(id, specieName, specieScientificName, 
				specieDescription, null, valid, resolved, message, null);
		
		tr.setIdSpecieImages(idSpecieImages);
		tr.setIdTreatments(idTreatments);
		
		return tr;
	}
	
	/**
	 * Construye una lista de treatment resolution obtenidas de la base
	 * @param cursor
	 * @return
	 */
	private ArrayList<TreatmentResolution> cursorToListOfTreatmentResolution(Cursor cursor) {		
		ArrayList<TreatmentResolution> trs = new ArrayList<TreatmentResolution>();
		if (cursor != null) {
			try{
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					TreatmentResolution tr = cursorToTreatmentResolution(cursor);
					trs.add(tr);
					cursor.moveToNext();
				}				
			}
			finally{
				cursor.close();
			}
		}
		return trs;
	}	

	public TreatmentResolutionDataSource(Context context) {
		setDbHelper(new FitoscannerSqLiteHelper(context));
	}
	
	public boolean treatmentResolutionExists(TreatmentResolution tr) {

		boolean exists = false;
		try {
			String table = TreatmentResolutionSQLiteTable.TABLE;
			String where = TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID + " = " + tr.getId();
			Cursor cursor = getDatabase().query(table, TreatmentResolutionSQLiteTable.ALL_COLUMNS, where, null, null, null, null);						
			if (cursor != null) {
				try {
					cursor.moveToFirst();
					int count = cursor.getCount();
					exists = count != 0;
				} finally {
					cursor.close();
				}				
			}
		} catch (Exception e) {
			Log.e(TAG, ("TreatmentResolution not found" + tr.getId().toString()));
		}		

		return exists;
	}
	
	public void doSaveTreatmentResolution(TreatmentResolution tr) {		
		if (tr != null)
		{

			Long id = tr.getId();
			String specieName = tr.getSpecieName();
			String specieScientificName = tr.getSpecieScientificName();
			String specieDescription = tr.getSpecieDescription();
			String idSpecieImages = tr.getIdSpecieImages();
			Integer valid = tr.getValid()?1:0;
			Integer resolved = tr.getResolved()?1:0; 
			String message = tr.getMessage();
			String idTreatments = tr.getIdTreatments();
			
			ContentValues values = new ContentValues();
			values.put(TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID, id);
			values.put(TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_NAME, specieName);
			values.put(TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_SCIENTIFIC_NAME, specieScientificName);
			values.put(TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_SPECIE_DESCRIPTION, specieDescription);
			values.put(TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID_SPECIE_IMAGES, idSpecieImages);
			values.put(TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_VALID, valid);
			values.put(TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_RESOLVED, resolved);
			values.put(TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_MESSAGE, message);
			values.put(TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_TREATMENT_IDS, idTreatments);
			if(id!=null&&treatmentResolutionExists(tr))
			{
				getDatabase().update(TreatmentResolutionSQLiteTable.TABLE, values, TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID + " = " + id, null);
			} else 
			{
				getDatabase().insert(TreatmentResolutionSQLiteTable.TABLE, null, values);	
			}														
		}		
	}

	public TreatmentResolution getTreatmentResolutionById(Long id) {
		TreatmentResolution TR = null;		
		try {
			String table = TreatmentResolutionSQLiteTable.TABLE;
			String where = TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID + " = " + id;
			Cursor cursor = getDatabase().query(table, TreatmentResolutionSQLiteTable.ALL_COLUMNS, where, null, null, null, null);
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						TR = cursorToTreatmentResolution(cursor);
					}
				} finally {
					cursor.close();
				}
			}				
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return TR;
	}


	public void deleteAllRows() {
		try {			
			getDatabase().delete(TreatmentResolutionSQLiteTable.TABLE, null, null);										
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}		
	}
	
	public void deleteById(Long id) {
		try {
			String where =TreatmentResolutionSQLiteTable.COLUMN_TREATMENTR_ID + " = " + id;
			getDatabase().delete(TreatmentResolutionSQLiteTable.TABLE, where, null);			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}		
	}
	
}
