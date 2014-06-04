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

	public SamplesDataSource(Context context) {
		this.mContext = context;
		dbHelper = new FitoscannerSqLiteHelper(context);
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
				Log.e(TAG, e.getMessage());
			}
		} finally {
			this.close();
		}
	}

	private void saveImage(Image image) {
		if (image != null)
		{
			Long id = image.getId();
			String title = image.getTitle();
			String base64 = image.getBase64();
			String description = image.getDescription();
			
			ContentValues values = new ContentValues();
			values.put(ImageSQLiteTable.COLUMN_IMAGE_ID, id);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_TITLE, title);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_BASE64, base64);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_DESCRIPTION, description);
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

		ContentValues values = new ContentValues();
		values.put(SampleSQLiteTable.COLUMN_SAMPLE_ID, id);

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
}
