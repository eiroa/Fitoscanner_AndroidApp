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

public class ImageDataSource extends AbstractDataSource{
	public final String TAG = "ImageDateSource";
	
/**
 * Construye un objeto Image utilizando la informacion de la base
 * @param cursor
 * @return
 */
	private Image cursorToImage(Cursor cursor) {
		Long id = cursor.getLong(0);
		Long idSample = cursor.getLong(1);
		Long idTreatment = cursor.getLong(2);
		Long idTreatmentResolution = cursor.getLong(3);
		String title = cursor.getString(4);
		String description = cursor.getString(5);
		String base64 = cursor.getString(6);
		Boolean sent = ( 1 == cursor.getInt(7)?true:false);
				
		Image image = new Image(id,idSample,idTreatment,idTreatmentResolution,title,description,base64);
		image.setSent(sent);
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
		setDbHelper(new FitoscannerSqLiteHelper(context));
	}
	
	
	public boolean imageExists(Image image) {

		boolean exists = false;
		try {
			String table = ImageSQLiteTable.TABLE;
			String where = ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + image.getId();
			Cursor cursor = getDatabase().query(table, ImageSQLiteTable.ALL_COLUMNS, where, null, null, null, null);						
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
	
	public Long doSaveImage(Image image) {
		Long result =0L;
		if (image != null)
		{
			Long id = image.getId();
			Long idSample = image.getIdSample();
			Long idTreatment = image.getIdTreatment();
			Long idTreatmentResolution = image.getIdTreatmentResolution();
			String title = image.getTitle();
			String description = image.getDescription();
			String base64 = image.getBase64();
			Integer sent = image.isSent()?1:0;

			ContentValues values = new ContentValues();
			values.put(ImageSQLiteTable.COLUMN_IMAGE_SAMPLE_ID, idSample);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_TREATMENT_ID, idTreatment);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_TREATMENT_RESOLUTION_ID, idTreatmentResolution);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_TITLE, title);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_DESCRIPTION, description);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_BASE64, base64);
			values.put(ImageSQLiteTable.COLUMN_IMAGE_SENT, sent);
			if(id!=null&&imageExists(image)){
				getDatabase().update(ImageSQLiteTable.TABLE, values, ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + id, null);
				result =id;
			} else {
				result = getDatabase().insert(ImageSQLiteTable.TABLE, null, values);
				Log.d(TAG, "image saved");
			}														
		}
		return result;
	}

	


	public Image getById(Long id) {
		Image image = null;		
		try {
			String table = ImageSQLiteTable.TABLE;
			String where = ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + id;
			Cursor cursor = getDatabase().query(table, ImageSQLiteTable.ALL_COLUMNS, where, null, null, null, null);
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
			e.printStackTrace();
			Log.e(TAG, "Error getting image by id");
		}
		
		return image;
	}
	
	public Image getImageByTitle(String title) {
		Image image = null;		
		try {
			String table = ImageSQLiteTable.TABLE;
			String where = ImageSQLiteTable.COLUMN_IMAGE_TITLE + " = " + title;
			Cursor cursor = getDatabase().query(table, ImageSQLiteTable.ALL_COLUMNS, where, null, null, null, null);
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
			Cursor cursor = getDatabase().query(ImageSQLiteTable.TABLE, ImageSQLiteTable.ALL_COLUMNS, null,null,null,null,null);				
			images = cursorToListOfImages(cursor);
		}
		catch (Exception e){
			Log.e(TAG, e.getMessage());
		}				
		return images;		
	}


	public void deleteAllRows() {
		try {			
			getDatabase().delete(ImageSQLiteTable.TABLE, null, null);										
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
			Cursor cursor = getDatabase().query(ImageSQLiteTable.TABLE, ImageSQLiteTable.ALL_COLUMNS, where,null,null,null,null);				
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
			getDatabase().delete(ImageSQLiteTable.TABLE, where, null);			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}		
	}

	public void saveImage(Image img) {
		// TODO Auto-generated method stub
		
	}



	
}
