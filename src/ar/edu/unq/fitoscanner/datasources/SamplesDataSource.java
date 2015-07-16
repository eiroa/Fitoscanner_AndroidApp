package ar.edu.unq.fitoscanner.datasources;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import ar.edu.unq.fitoscanner.data.FitoscannerSqLiteHelper;
import ar.edu.unq.fitoscanner.data.ImageSQLiteTable;
import ar.edu.unq.fitoscanner.data.SampleSQLiteTable;
import ar.edu.unq.fitoscanner.model.Image;
import ar.edu.unq.fitoscanner.model.Sample;
import ar.edu.unq.fitoscanner.model.TreatmentResolution;

public class SamplesDataSource extends AbstractDataSource{
	public final static String TAG = "SamplesDataSource";
	private ImageDataSource imageDataSource;
	private TreatmentResolutionDataSource trds;

	public SamplesDataSource(Context context) {
		setDbHelper(new FitoscannerSqLiteHelper(context));
		this.imageDataSource = new ImageDataSource(context);
		this.trds = new TreatmentResolutionDataSource(context);
	}

	private Sample cursorToSample(Cursor cursor) {
		Long id = cursor.getLong(0);
		String date = cursor.getString(1);
		String field = cursor.getString(2);
		String sampleName = cursor.getString(3);
		String latitude = cursor.getString(4);
		String longitude = cursor.getString(5);
		String city = cursor.getString(6);
		String state = cursor.getString(7);
		String country = cursor.getString(8);
		String hash = cursor.getString(9);
		Boolean sent = ( 1 == cursor.getInt(10)?true:false);
		Long treatmentResolutionId = cursor.getLong(11);
		Integer reqs = cursor.getInt(12);
		Integer minutesPassed = cursor.getInt(13);
		Boolean resolved = ( 1 == cursor.getInt(14)?true:false);
		Boolean valid = ( 1 == cursor.getInt(15)?true:false);
		
		
		Sample Sample = new Sample(id, date, null, field, 
				sampleName,latitude,longitude,city,state,
				country, hash,sent,null,reqs,
				minutesPassed,resolved,valid);
		
		if(Sample.getResolved()){
			//seteamos solo el id, simulando un cargado lazy, no obtenemos toda la resolucion y tratamientos en el momento
			Sample.setTreatmentResolutionId(treatmentResolutionId);
		}
		return Sample;
	}

	private List<Sample> cursorToListOfSamples(Cursor cursor) {

		List<Sample> samples = new ArrayList<Sample>();

		if (cursor != null) {
			try {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Sample sample = cursorToSample(cursor);
					samples.add(sample);
					cursor.moveToNext();
				}
			} finally {
				cursor.close();
			}
		}

		return samples;
	}
	
	public void updateSentStatusSample(Sample sample){
		try {
			Integer sent = sample.getSent()?1:0;
			getDatabase().rawQuery("UPDATE "+ SampleSQLiteTable.TABLE +
					" SET sent = "+sent+" where "
					+SampleSQLiteTable.COLUMN_SAMPLE_ID+"="+sample.getId(), null);
		} catch (Exception e) {
			Log.e(TAG, "Error updating sample!");
			e.printStackTrace();
		}
	}
	

	public Long fullSaveSample(Sample sample) {
		Long idResult = 0L;
		Log.d(TAG, "Saving sample "+sample.getSampleName());
		try {
			// Guardamos la muestra y al guardarse se devuelve el id con el que
			// se guardo,
			// dicho id se insertara en cada imagen que contenga la muestra
			if(this.sampleExists(sample)){
				idResult = sample.getId();
				simpleSaveSample(sample);
			}else{
				
				idResult = simpleSaveSample(sample);
			}
			

			// Guardamos cada imagen correspondiente de la muestra
			Log.d(TAG, "Sample details of "+sample.getSampleName() +" saved, attempting to save images");
			for (Image image : sample.getImages()) {
				image.setIdSample(idResult);
				this.imageDataSource.setDatabase(getDatabase());
				this.imageDataSource.doSaveImage(image);
			}
		} catch (Exception e) {
			Log.e(TAG, "Error saving sample!");
			e.printStackTrace();
		}
		return idResult;
	}

	private void saveImage(Image image) {
		if (image != null) {
			Long id = image.getId();
			Long idSample = image.getIdSample();
			String title = image.getTitle();
			String description = image.getDescription();
			String base64 = image.getBase64();

			ContentValues values = new ContentValues();
			values.put(ImageSQLiteTable.COLUMN_IMAGE_ID, id);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_SAMPLE_ID, idSample);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_TITLE, title);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_DESCRIPTION, description);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_BASE64, base64);

			if (imageExists(image)) {
				getDatabase().update(ImageSQLiteTable.TABLE, values,
						ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + id, null);
			} else {
				getDatabase().insert(ImageSQLiteTable.TABLE, null, values);
			}
		}
	}

	public Long simpleSaveSample(Sample sample) {
		Long idResult = 0L;
		Long id = sample.getId();
		String date = sample.getOriginDate();
		String fieldName = sample.getFieldName();
		String sampleName = sample.getSampleName();
		String latitude = sample.getLocationData().getLatitude();
		String longitude = sample.getLocationData().getLongitude();
		String city = sample.getLocationData().getCity();
		String state = sample.getLocationData().getState();
		String country = sample.getLocationData().getCountry();
		String hash = sample.getHash();
		Integer sent = sample.getSent()?1:0;
		// El tratamiento debe haber sido guardado antes para que exista el id
		Long idTreatmentResolution = sample.getTreatmentResolution()!=null?sample.getTreatmentResolution().getId():null;
		Integer reqs = sample.getRequestTreatmentIntents();
		Integer minutesPassed = sample.getMinutesFromLastRequest();
		Integer resolved = sample.getResolved()?1:0;
		Integer valid = sample.getValid()?1:0;
		ContentValues values = new ContentValues();

		values.put(SampleSQLiteTable.COLUMN_SAMPLE_ORIGIN_DATE, date);
		values.put(SampleSQLiteTable.COLUMN_SAMPLE_FIELD_NAME, fieldName);
		values.put(SampleSQLiteTable.COLUMN_SAMPLE_NAME, sampleName);
		values.put(SampleSQLiteTable.COLUMN_LATITUDE, latitude);
		values.put(SampleSQLiteTable.COLUMN_LONGITUDE, longitude);
		values.put(SampleSQLiteTable.COLUMN_CITY, city);
		values.put(SampleSQLiteTable.COLUMN_STATE, state);
		values.put(SampleSQLiteTable.COLUMN_COUNTRY, country);
		values.put(SampleSQLiteTable.COLUMN_HASH, hash);
		values.put(SampleSQLiteTable.COLUMN_SENT, sent);
		values.put(SampleSQLiteTable.COLUMN_TREATMENT_RESOLUTION_ID, idTreatmentResolution);
		values.put(SampleSQLiteTable.COLUMN_SAMPLE_REQUEST_TREATMENT_INTENTS, reqs);
		values.put(SampleSQLiteTable.COLUMN_SAMPLE_MINUTES_FROM_LAST_REQUEST, minutesPassed);
		values.put(SampleSQLiteTable.COLUMN_RESOLVED, resolved);
		values.put(SampleSQLiteTable.COLUMN_VALID, valid);

		if (id != null && sampleExists(sample)) {
			getDatabase().update(SampleSQLiteTable.TABLE, values,
					SampleSQLiteTable.COLUMN_SAMPLE_ID + " = " + id, null);
		} else {
			idResult = getDatabase().insert(SampleSQLiteTable.TABLE, null, values);
		}
		return idResult;
	}

	private boolean sampleExists(Sample sample) {

		boolean exists = false;
		try {
			String table = SampleSQLiteTable.TABLE;
			String where = SampleSQLiteTable.COLUMN_SAMPLE_ID + " = "
					+ sample.getId();
			Cursor cursor = getDatabase().query(table,
					SampleSQLiteTable.ALL_COLUMNS, where, null, null, null,
					null);
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
			Log.e(TAG, ("Sample not found" + sample.getId().toString()));
		}

		return exists;
	}

	private boolean imageExists(Image image) {

		boolean exists = false;
		try {
			String table = ImageSQLiteTable.TABLE;
			String where = ImageSQLiteTable.COLUMN_IMAGE_ID + " = "
					+ image.getId();
			Cursor cursor = getDatabase().query(table, ImageSQLiteTable.ALL_COLUMNS,
					where, null, null, null, null);
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
			Log.e(TAG, ("iMAGE NOT FOUND..." + image.getId().toString()));
		}

		return exists;
	}

	public Sample getById(Long id) {
		Sample Sample = null;
		try {
			String table = SampleSQLiteTable.TABLE;
			String where = SampleSQLiteTable.COLUMN_SAMPLE_ID + " = " + id;
			
			// join
			Cursor cursor = getDatabase().query(table,
					SampleSQLiteTable.ALL_COLUMNS, where, null, null, null,
					null);
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						Sample = cursorToSample(cursor);
						Sample.setImages(this.imageDataSource
								.getImagesBySampleId(id));
					}
				} finally {
					cursor.close();
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return Sample;
	}
	
	/**
	 * Muestras enviadas resueltas
	 * @return
	 */
	public List<Sample> getSamplesSentResolved(){
		return this.getSamples(true,true,true);
	}
	
	/**
	 * Muestras enviadas sin resolucion
	 * @return
	 */
	public List<Sample> getSamplesSentUnresolved(){
		return this.getSamples(true, false,true);
	}
	
	/**
	 * Muestras nuevas que nunca se enviaron
	 * @return
	 */
	public List<Sample> getSamplesUnsent(){
		return this.getSamples(false, false,true);
	}
	
	/**
	 * Muestras enviadas que fallaron
	 * @return
	 */
	public List<Sample>getSamplesSentInvalid(){
		return this.getSamples(true, false,false);
	}
	

	/**
	 * Devuelve en una lista todas las muestras con sus respectivas imágenes
	 * 
	 * @return
	 */
	private List<Sample> getSamples(Boolean getSent,Boolean getResolved,Boolean getValid) {
		List<Sample> samples = new ArrayList<Sample>();
		try {
			
			Integer sent = (getSent?1:0);
			Integer resolved = (getResolved?1:0);
			Integer valid = (getValid?1:0);
			
			String whereClause = SampleSQLiteTable.COLUMN_SENT+" = ?"+" AND "+
			SampleSQLiteTable.COLUMN_RESOLVED+" = ?"+" AND "+
					SampleSQLiteTable.COLUMN_VALID+" = ?";
			
			String[] whereArgs = new String[] {
			    sent.toString(),
			    resolved.toString(),
			    valid.toString()
			};
			

			Cursor cursor = getDatabase()
					.query(SampleSQLiteTable.TABLE,
							SampleSQLiteTable.ALL_COLUMNS, whereClause, whereArgs, null,
							null, null);
			samples = cursorToListOfSamples(cursor);
			for (Sample sample : samples) {
				// Se le pasa al imageDatasource la conexion a la base de datos
				this.imageDataSource.setDatabase(getDatabase());
				List<Image> images = this.imageDataSource.getImagesBySampleId(sample.getId());
				sample.setImages(images);
			}
		} catch (Exception e) {
			Log.e(TAG, " Error al obtener muestras");
			e.printStackTrace();
		}
		return samples;
	}
	
	public void deleteById(Long id) {
		try {
			String where = SampleSQLiteTable.COLUMN_SAMPLE_ID + " = " + id;
			getDatabase().delete(SampleSQLiteTable.TABLE, where, null);				
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}		
	}
	
	public void deleteSample(Sample sample) {
		this.imageDataSource.setDatabase(this.getDatabase());
		try {

			for (Image image: sample.getImages()) {
				this.imageDataSource.deleteById(image.getId());
			}
			String where = SampleSQLiteTable.COLUMN_SAMPLE_ID + " = " + sample.getId();
			getDatabase().delete(SampleSQLiteTable.TABLE, where, null);
			Log.i(TAG,"Se ha eliminado la muestra "+sample.getId()+ " de nombre "+
			sample.getSampleName() + " de fecha "+sample.getOriginDate() + " junto con sus "+
					sample.getImages().size() + " imagenes ");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}		
	}

}
