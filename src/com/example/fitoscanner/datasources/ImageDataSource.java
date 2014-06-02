package com.example.fitoscanner.datasources;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



import com.example.fitoscanner.data.FitoscannerSqLiteHelper;
import com.example.fitoscanner.data.ImageSQLiteTable;
import com.example.fitoscanner.model.Image;

public class ImageDataSource {
	public final String TAG = "ImageDateSource";
	private SQLiteDatabase database;
	private FitoscannerSqLiteHelper dbHelper;

	private Image cursorToImage(Cursor cursor) {
		Long id = cursor.getLong(0);
		String name = cursor.getString(1);
		String description = cursor.getString(2);
		String base64 = cursor.getString(3);
		Long idSample = cursor.getLong(4);
		
		Image image = new Image(id,base64,name,description,idSample);
		return image;
	}
	
	private ArrayList<Image> cursorToListOfImages(Cursor cursor) {		
		ArrayList<Image> contents = new ArrayList<Image>();
		if (cursor != null) {
			try{
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Image content = cursorToImage(cursor);
					contents.add(content);
					cursor.moveToNext();
				}				
			}
			finally{
				cursor.close();
			}
		}
		return contents;
	}	

	public ImageDataSource(Context context) {
		dbHelper = new FitoscannerSqLiteHelper(context);
	}
	

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

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
			String title = image.getTitle();
			String description = image.getDescription();
			String base64 = image.getBase64();
			
			ContentValues values = new ContentValues();
			values.put(ImageSQLiteTable.COLUMN_IMAGE_ID, id);
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



}
