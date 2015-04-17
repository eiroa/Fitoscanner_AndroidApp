package ar.edu.unq.fitoscanner.datasources;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;




import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ar.edu.unq.fitoscanner.data.FitoscannerSqLiteHelper;
import ar.edu.unq.fitoscanner.data.ImageSQLiteTable;
import ar.edu.unq.fitoscanner.data.SampleSQLiteTable;
import ar.edu.unq.fitoscanner.model.Image;
import ar.edu.unq.fitoscanner.model.Sample;

public class SamplesDataSource {
	public final static String TAG = "SamplesDataSource";
	private SQLiteDatabase database;
	private FitoscannerSqLiteHelper dbHelper;
	private Context mContext;
	private ImageDataSource imageDataSource;

	public SamplesDataSource(Context context) {
		this.mContext = context;
		dbHelper = new FitoscannerSqLiteHelper(context);
		this.imageDataSource = new ImageDataSource(context);
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
		Sample Sample = new Sample(id, date, null, field, sampleName,
				latitude,longitude,city,state,country, hash,sent);
		return Sample;
	}

	private ArrayList<Sample> cursorToListOfSamples(Cursor cursor, Boolean getSent) {

		ArrayList<Sample> samples = new ArrayList<Sample>();

		if (cursor != null) {
			try {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Sample sample = cursorToSample(cursor);
					if(getSent){
						if(sample.getSent())samples.add(sample);
					}else{
						if(!sample.getSent())samples.add(sample);
					}
					
					cursor.moveToNext();
				}
			} finally {
				cursor.close();
			}
		}

		return samples;
	}
	
	
	private ArrayList<Sample> cursorToListOfSamples(Cursor cursor) {

		ArrayList<Sample> samples = new ArrayList<Sample>();

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

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Long saveSample(Sample sample) {
		Long idResult = 0L;

		try {
			// Guardamos la muestra y al guardarse se devuelve el id con el que
			// se guardo,
			// dicho id se insertara en cada imagen que contenga la muestra
			idResult = doSaveSample(sample);

			// Guardamos cada imagen correspondiente de la muestra
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
				database.update(ImageSQLiteTable.TABLE, values,
						ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + id, null);
			} else {
				database.insert(ImageSQLiteTable.TABLE, null, values);
			}
		}
	}

	private Long doSaveSample(Sample sample) {
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
		

		if (id != null && sampleExists(sample)) {
			database.update(SampleSQLiteTable.TABLE, values,
					SampleSQLiteTable.COLUMN_SAMPLE_ID + " = " + id, null);
		} else {
			idResult = database.insert(SampleSQLiteTable.TABLE, null, values);
		}
		return idResult;
	}

	private boolean sampleExists(Sample sample) {

		boolean exists = false;
		try {
			String table = SampleSQLiteTable.TABLE;
			String where = SampleSQLiteTable.COLUMN_SAMPLE_ID + " = "
					+ sample.getId();
			Cursor cursor = database.query(table,
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
			Cursor cursor = database.query(table, ImageSQLiteTable.ALL_COLUMNS,
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

	public Sample getSampleById(Long id) {
		Sample Sample = null;
		try {
			String table = SampleSQLiteTable.TABLE;
			String where = SampleSQLiteTable.COLUMN_SAMPLE_ID + " = " + id;
			Cursor cursor = database.query(table,
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
	 * Devuelve en una lista todas las muestras con sus respectivas imágenes
	 * 
	 * @return
	 */
	public ArrayList<Sample> getSamples(Boolean getSent) {
		ArrayList<Sample> samples = new ArrayList<Sample>();
		try {

			Cursor cursor = database
					.query(SampleSQLiteTable.TABLE,
							SampleSQLiteTable.ALL_COLUMNS, null, null, null,
							null, null);
			samples = cursorToListOfSamples(cursor,getSent);

			for (Sample sample : samples) {
				// Se le pasa al imageDatasource la conexion a la base de datos
				this.imageDataSource.setDatabase(getDatabase());
				ArrayList<Image> images = this.imageDataSource
						.getImagesBySampleId(sample.getId());
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
			database.delete(SampleSQLiteTable.TABLE, where, null);				
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
			database.delete(SampleSQLiteTable.TABLE, where, null);
			Log.i(TAG,"Se ha eliminado la muestra "+sample.getId()+ " de nombre "+
			sample.getSampleName() + " de fecha "+sample.getOriginDate() + " junto con sus "+
					sample.getImages().size() + " imagenes ");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}		
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

	public void setDatabase(SQLiteDatabase database) {
		this.database = database;
	}

}
