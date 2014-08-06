package ar.edu.unq.fitoscanner.datasources;

import java.util.ArrayList;

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

public class ImageDataSource {
	public final String TAG = "ImageDateSource";
	private SQLiteDatabase database;
	private FitoscannerSqLiteHelper dbHelper;
	
/**
 * Construye un objeto Image utilizando la informacion de la base
 * @param cursor
 * @return
 */
	private Image cursorToImage(Cursor cursor) {
		Long id = cursor.getLong(0);
		Long idSample = cursor.getLong(1);
		String title = cursor.getString(2);
		String description = cursor.getString(3);
		String base64 = cursor.getString(4);
				
		Image image = new Image(id,idSample,title,description,base64);
		return image;
	}
	
	/**
	 * Construye una lista de images obtenidas de la base
	 * @param cursor
	 * @return
	 */
	private ArrayList<Image> cursorToListOfImages(Cursor cursor) {		
		ArrayList<Image> images = new ArrayList<Image>();
		if (cursor != null) {
			try{
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Image image = cursorToImage(cursor);
					images.add(image);
					cursor.moveToNext();
				}				
			}
			finally{
				cursor.close();
			}
		}
		return images;
	}	

	public ImageDataSource(Context context) {
		dbHelper = new FitoscannerSqLiteHelper(context);
	}
	
/**
 * Abre una conexion a la base
 * @throws SQLException
 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
/**
 * Cierra la conexion a la base
 */
	public void close() {
		dbHelper.close();
	}	
	
	public boolean imageExists(Image image) {

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
			Log.e(TAG, ("Image not found" + image.getId().toString()));
		}		

		return exists;
	}
	
	public void doSaveImage(Image image) {		
		if (image != null)
		{
			Long id = image.getId();
			Long idSample = image.getIdSample();
			String title = image.getTitle();
			String description = image.getDescription();
			String base64 = image.getBase64();
			
			ContentValues values = new ContentValues();
			values.put(ImageSQLiteTable.COLUMN_IMAGE_SAMPLE_ID, idSample);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_TITLE, title);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_DESCRIPTION, description);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_BASE64, base64);
			if(id!=null&&imageExists(image))
			{
				database.update(ImageSQLiteTable.TABLE, values, ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + id, null);
			} else 
			{
				database.insert(ImageSQLiteTable.TABLE, null, values);	
			}														
		}		
	}

	private String parseForSqlIn(ArrayList<Long> params) {
		String in = "(";
		
		if (params.size() == 0){
			in += "-1";
		}		
		for (int i = 0; i < params.size(); i++) {
			in += params.get(i);
			if(i != params.size() - 1){
				in += ", ";
			}
		}
		
		in += ")";
		return in;
	}
	


	public Image getImageById(Long id) {
		Image image = null;		
		try {
			String table = ImageSQLiteTable.TABLE;
			String where = ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + id;
			Cursor cursor = database.query(table, ImageSQLiteTable.ALL_COLUMNS, where, null, null, null, null);
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						image = cursorToImage(cursor);
					}
				} finally {
					cursor.close();
				}
			}				
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		return image;
	}
	
	public Image getImageByTitle(String title) {
		Image image = null;		
		try {
			String table = ImageSQLiteTable.TABLE;
			String where = ImageSQLiteTable.COLUMN_IMAGE_TITLE + " = " + title;
			Cursor cursor = database.query(table, ImageSQLiteTable.ALL_COLUMNS, where, null, null, null, null);
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						image = cursorToImage(cursor);
					}
				} finally {
					cursor.close();
				}
			}				
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		return image;
	}
	

	public ArrayList<Long> getImageIds() {		
		ArrayList<Long> ids = new ArrayList<Long>();								
		ArrayList<Image> images = this.getImages();
			
		for (Image image : images){ 
			ids.add(image.getId());
		}
		return ids;			
	}

	
	public ArrayList<Image> getImages() {		
		ArrayList<Image> images = null;
		try {
			Cursor cursor = database.query(ImageSQLiteTable.TABLE, ImageSQLiteTable.ALL_COLUMNS, null,null,null,null,null);				
			images = cursorToListOfImages(cursor);
		}
		catch (Exception e){
			Log.e(TAG, e.getMessage());
		}				
		return images;		
	}


	public void deleteAllRows() {
		try {			
			database.delete(ImageSQLiteTable.TABLE, null, null);										
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}		
	}
	
	public void saveImagesSample(Sample sample){
		
	}
	
	public ArrayList<Image> getImagesBySampleId(Long id) {
		
		ArrayList<Image> images = new ArrayList<Image>();		
		try {
			String where = ImageSQLiteTable.COLUMN_IMAGE_SAMPLE_ID + " = " + id;
			Cursor cursor = database.query(ImageSQLiteTable.TABLE, ImageSQLiteTable.ALL_COLUMNS, where,null,null,null,null);				
			images = cursorToListOfImages(cursor);
	
		}
		catch (Exception e){
			Log.e(TAG, e.getMessage());
		}
				
		return images;			
	}
	
	public void deleteById(Long id) {
		try {
			String where = ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + id;
			database.delete(ImageSQLiteTable.TABLE, where, null);			
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
