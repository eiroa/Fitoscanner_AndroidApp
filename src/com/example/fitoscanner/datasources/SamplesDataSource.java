package com.example.fitoscanner.datasources;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;




import com.example.fitoscanner.data.FitoscannerSqLiteHelper;
import com.example.fitoscanner.data.ImageSQLiteTable;
import com.example.fitoscanner.data.SampleSQLiteTable;
import com.example.fitoscanner.model.Image;
import com.example.fitoscanner.model.Sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
		
		Sample Sample = new Sample(id,date,null,field);
		return Sample;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void saveSample(Sample sample) {
		this.open();
		try {
			try {
				// Guardamos la muestra
				 doSaveSample(sample);

				// Guardamos cada imagen correspondiente de la muestra
				for (Image image : sample.getImages()) {
					this.saveImage(image);
				}
			} catch (Exception e) {
				Log.e(TAG, "Error saving sample!" );
				e.printStackTrace();
			}
		} finally {
			this.close();
		}
	}

	private void saveImage(Image image) {
		if (image != null)
		{
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
			
			if(imageExists(image))
			{
				database.update(ImageSQLiteTable.TABLE, values, ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + id, null);
			} else 
			{
				database.insert(ImageSQLiteTable.TABLE, null, values);	
			}														
		}
	}

	private void doSaveSample(Sample sample) {

		Long id = sample.getId();
		String date = sample.getOriginDate();
		String fieldName = sample.getFieldName();
		ContentValues values = new ContentValues();
		values.put(SampleSQLiteTable.COLUMN_SAMPLE_ID, id);
		values.put(SampleSQLiteTable.COLUMN_SAMPLE_ORIGIN_DATE, date);
		values.put(SampleSQLiteTable.COLUMN_SAMPLE_FIELD_NAME, fieldName);

		if (sampleExists(sample)) {
			database.update(SampleSQLiteTable.TABLE, values,
					SampleSQLiteTable.COLUMN_SAMPLE_ID + " = " + id, null);
		} else {
			database.insert(SampleSQLiteTable.TABLE, null, values);
		}
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
			String where = ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + image.getId();
			Cursor cursor = database.query(table, ImageSQLiteTable.ALL_COLUMNS, where, null, null, null, null);						
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
        this.open();
		Sample Sample = null;		
		try {
			String table = SampleSQLiteTable.TABLE;
			String where = SampleSQLiteTable.COLUMN_SAMPLE_ID + " = " + id;
			Cursor cursor = database.query(table, SampleSQLiteTable.ALL_COLUMNS, where, null, null, null, null);
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						Sample = cursorToSample(cursor);
						Sample.setImages(this.imageDataSource.getImagesBySampleId(id));
					}
				} finally {
					cursor.close();
				}
			}				
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		this.close();
		return Sample;
	}
}
