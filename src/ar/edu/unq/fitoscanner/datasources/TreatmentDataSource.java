package ar.edu.unq.fitoscanner.datasources;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import ar.edu.unq.fitoscanner.data.FitoscannerSqLiteHelper;
import ar.edu.unq.fitoscanner.data.TreatmentSQLiteTable;
import ar.edu.unq.fitoscanner.model.Treatment;

public class TreatmentDataSource extends AbstractDataSource {
	public final String TAG = "ImageDateSource";
	private ImageDataSource imageDataSource;

	
	
	/**
	 * Construye un objeto treatMent utilizando la informacion de la
	 * base
	 * 
	 * @param cursor
	 * @return
	 */
	private Treatment cursorToTreatment(Cursor cursor) {
		
		Long id = cursor.getLong(0);
		String name = cursor.getString(1);
		String description = cursor.getString(2);
		String idImages = cursor.getString(3);
		String unit = cursor.getString(4);
		String unitType = cursor.getString(5);
		String frequency = cursor.getString(6);
		String frequencyType = cursor.getString(7);
		String extraLink1 = cursor.getString(8);
		String extraLink2 = cursor.getString(9);
		String extraLink3 = cursor.getString(10);
		String useExplanation = cursor.getString(11);

		Treatment tr = new Treatment(id, name, description, null, 
				unit, unitType, frequency, frequencyType, extraLink1, extraLink2, extraLink3,useExplanation);

		tr.setIdImages(idImages);
		
		imageDataSource.setDatabase(getDatabase());
		tr.setImages(imageDataSource.getEntitiesForIds(idImages));

		return tr;
	}

	/**
	 * Construye una lista de tratamientos obtenidas de la base
	 * 
	 * @param cursor
	 * @return
	 */
	private List<Treatment> cursorToListOfTreatments(
			Cursor cursor) {
		List<Treatment> trs = new ArrayList<Treatment>();
		if (cursor != null) {
			try {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Treatment tr = cursorToTreatment(cursor);
					trs.add(tr);
					cursor.moveToNext();
				}
			} finally {
				cursor.close();
			}
		}
		return trs;
	}

	public TreatmentDataSource(Context context) {
		setDbHelper(new FitoscannerSqLiteHelper(context));
		this.imageDataSource = new ImageDataSource(context);
	}

	public boolean treatmentExists(Treatment tr) {

		boolean exists = false;
		try {
			String table = TreatmentSQLiteTable.TABLE;
			String where = TreatmentSQLiteTable.COLUMN_TREATMENT_ID
					+ " = " + tr.getId();
			Cursor cursor = getDatabase().query(table,
					TreatmentSQLiteTable.ALL_COLUMNS, where, null,
					null, null, null);
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
			Log.e(TAG,
					("Treatment not found" + tr.getId().toString()));
		}

		return exists;
	}

	public Long doSaveTreatment(Treatment tr) {
		Long result = 0L;
		if (tr != null) {
			Long id = tr.getId();
			String name = tr.getName();
			String description = tr.getDescription();
			String idImages = tr.getIdImages();
			String unit = tr.getUnit();
			String unitType = tr.getUnitType();
			String frequency = tr.getFrequency();
			String frequencyType = tr.getFrequencyType();
			String extraLink1 = tr.getExtraLink1();
			String extraLink2 = tr.getExtraLink2();
			String extraLink3 = tr.getExtraLink3();
			String useExplanation = tr.getUseExplanation();

			ContentValues values = new ContentValues();
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_ID, id);
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_NAME,name);
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_DESCRIPTION,description);
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_ID_IMAGES,idImages);
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_UNIT,unit);
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_UNIT_TYPE,unitType);
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_FREQUENCY,frequency);
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_FREQUENCY_TYPE,frequencyType);
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_EXTRA_LINK_1,extraLink1);
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_EXTRA_LINK_2,extraLink2);
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_EXTRA_LINK_3,extraLink3);
			values.put(TreatmentSQLiteTable.COLUMN_TREATMENT_USE_EXPLANATION, useExplanation);
			if (id != null && treatmentExists(tr)) {
				getDatabase().update(
						TreatmentSQLiteTable.TABLE,values,TreatmentSQLiteTable.COLUMN_TREATMENT_ID+ " = " + id, null);
				result = id;
			} else {
				result = getDatabase().insert(TreatmentSQLiteTable.TABLE,null, values);
			}
		}
		return result;
	}

	public Treatment getById(Long id) {
		Treatment TR = null;
		try {
			String table = TreatmentSQLiteTable.TABLE;
			String where = TreatmentSQLiteTable.COLUMN_TREATMENT_ID
					+ " = " + id;
			Cursor cursor = getDatabase().query(table,
					TreatmentSQLiteTable.ALL_COLUMNS, where, null,
					null, null, null);
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						TR = cursorToTreatment(cursor);
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
			getDatabase().delete(TreatmentSQLiteTable.TABLE, null, null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void deleteById(Long id) {
		try {
			String where = TreatmentSQLiteTable.COLUMN_TREATMENT_ID
					+ " = " + id;
			getDatabase().delete(TreatmentSQLiteTable.TABLE, where,
					null);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public List<Treatment> getTreatmentsForIds(String idTreatments) {
		// TODO Auto-generated method stub
		return null;
	}

}
