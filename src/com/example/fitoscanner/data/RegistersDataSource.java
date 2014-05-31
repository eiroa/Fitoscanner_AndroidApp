package com.example.fitoscanner.data;

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


public class RegistersDataSource {

	private SQLiteDatabase database;
	private FitoscannerSqLiteHelper dbHelper;
	private Context mContext;

//	private Category cursorToCategory(Cursor cursor) {
//
//		Long id = cursor.getLong(0);
//		String description = cursor.getString(1);
//		int order = cursor.getInt(2);
//		Long imageId = cursor.getLong(3);
//		int locationPortalId = cursor.getInt(4);
//
//		Image image = imageId == null ? null : new Image(imageId, "", "");
//		
//		Category category = new Category(id, description, order, image, locationPortalId, null, false);
//		return category;
//	}
//	
//
//	private ArrayList<Category> cursorToListOfCategory(Cursor cursor) {
//		
//		ArrayList<Category> categories = new ArrayList<Category>();
//
//		if (cursor != null) {
//			try{
//				cursor.moveToFirst();
//				while (!cursor.isAfterLast()) {
//					Category category = cursorToCategory(cursor);
//					categories.add(category);
//					cursor.moveToNext();
//				}						
//			} finally {
//				cursor.close();						
//			}
//		}
//		
//		return categories;
//	}
//
//	public RegistersDataSource(Context context) {
//		this.mContext = context;
//		dbHelper = new FitoscannerSqLiteHelper(context);
//		contentDataSource = new ContentsDataSource(context);		
//	}
//
//	private boolean categoryExists(Category category) {
//
//		boolean exists = false;
//		try {
//			String table = RegistersSQLiteTable.TABLE;
//			String where = RegistersSQLiteTable.COLUMN_CATEGORY_ID + " = " + category.getId();
//			Cursor cursor = database.query(table, RegistersSQLiteTable.ALL_COLUMNS, where, null, null, null, null);						
//			if (cursor != null) {
//				try {
//					cursor.moveToFirst();
//					int count = cursor.getCount();
//					exists = count != 0;
//				} finally {
//					cursor.close();
//				}				
//			}
//		} catch (Exception e) {
//			Log.e(LogTags.RegistersDataSource, (ExceptionMessages.CATEGORY_NOT_FOUND + category.getId().toString()));
//		}		
//
//		return exists;
//	}
//
//	private void deleteNonExistentContents(Category category){
//		
//		ArrayList<Long> contentIds = new ArrayList<Long>();
//		for (Content content : category.getContent()) {
//			contentIds.add(content.getId());
//		}
//		
//		String contentIdsStr = parseForSqlIn(contentIds);
//		
//		contentDataSource.deleteInverseContentsByCategoryId(contentIdsStr, category.getId());
//	}
//	
//	private void doSaveCategory(Category category) {
//		
//		deleteNonExistentContents(category);
//		
//		String description = category.getDescription();
//		int order = category.getOrder();
//		int locationPortalId = category.getUbicacionPortalId();
//		Long id = category.getId();
//		Long imageId = category.getImage() != null ? category.getImage().getId() : null;
//		
//		ContentValues values = new ContentValues();
//		values.put(RegistersSQLiteTable.COLUMN_CATEGORY_ID, id);
//		values.put(RegistersSQLiteTable.COLUMN_CATEGORY_DESCRIPTION, description);
//		values.put(RegistersSQLiteTable.COLUMN_CATEGORY_ORDER, order);
//		values.put(RegistersSQLiteTable.COLUMN_CATEGORY_IMAGE_ID, imageId);
//		values.put(RegistersSQLiteTable.COLUMN_CATEGORY_LOCATION_PORTAL_ID, locationPortalId);
//		if(categoryExists(category))
//		{
//			database.update(RegistersSQLiteTable.TABLE, values, RegistersSQLiteTable.COLUMN_CATEGORY_ID + " = " + id, null);
//		} else 
//		{
//			database.insert(RegistersSQLiteTable.TABLE, null, values);	
//		}											
//	}
//
//	private boolean imageExists(Image image) {
//
//		boolean exists = false;
//		try {
//			String table = ImageSQLiteTable.TABLE;
//			String where = ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + image.getId();
//			Cursor cursor = database.query(table, ImageSQLiteTable.ALL_COLUMNS, where, null, null, null, null);						
//			if (cursor != null) {
//				try {
//					cursor.moveToFirst();
//					int count = cursor.getCount();
//					exists = count != 0;
//				} finally {
//					cursor.close();
//				}				
//			}
//		} catch (Exception e) {
//			Log.e(LogTags.RegistersDataSource, (ExceptionMessages.CATEGORY_NOT_FOUND + image.getId().toString()));
//		}		
//
//		return exists;
//	}
//	
//	private void doSaveImage(Image image) {
//		
//		if (image != null)
//		{
//			Long id = image.getId();
//			String name = image.getName();
//			String base64 = image.getBase64();
//			
//			ContentValues values = new ContentValues();
//			values.put(ImageSQLiteTable.COLUMN_IMAGE_ID, id);
//			values.put(ImageSQLiteTable.COLUMN_IMAGE_NAME, name);
//			values.put(ImageSQLiteTable.COLUMN_IMAGE_BASE64, base64);
//			if(imageExists(image))
//			{
//				database.update(ImageSQLiteTable.TABLE, values, ImageSQLiteTable.COLUMN_IMAGE_ID + " = " + id, null);
//			} else 
//			{
//				database.insert(ImageSQLiteTable.TABLE, null, values);	
//			}														
//		}		
//	}
//	
//	private String parseForSqlIn(ArrayList<Long> params) {
//		String in = "(";
//		
//		if (params.size() == 0){
//			in += "-1";
//		}
//		for (int i = 0; i < params.size(); i++) {
//			in += params.get(i);
//			if(i != params.size() - 1){
//				in += ", ";
//			}
//		}
//		
//		in += ")";
//		return in;
//	}	
//	
//	private ArrayList<Long> getCategoriesIds(ArrayList<Category> categories){
//		ArrayList<Long> ids = new ArrayList<Long>();
//		for (Category category : categories) {
//			ids.add(category.getId());
//		}
//		return ids;
//	}
//	
//	private String getInverseCategoryIds(String ids){
//		ArrayList<Long> inverseids = new ArrayList<Long>();		
//		try {
//			String where  = RegistersSQLiteTable.COLUMN_CATEGORY_ID + " NOT IN " + ids;
//			Cursor cursor = database.query(RegistersSQLiteTable.TABLE, RegistersSQLiteTable.ALL_COLUMNS, where,null,null,null,null);				
//			ArrayList<Category> categories = cursorToListOfCategory(cursor);
//			
//			for (Category category : categories) {
//				inverseids.add(category.getId());
//			}
//		}
//		catch (Exception e){
//			Log.e(LogTags.ContentDataSource, e.getMessage());
//		}
//				
//		return parseForSqlIn(inverseids);					
//	}
//	
//	private void deleteCategoriesByIds(String ids){
//		
//		contentDataSource.open();
//		try
//		{
//			contentDataSource.deleteContentsByCategoryIds(ids);			
//		}
//		finally{
//			contentDataSource.close();
//		}
//		try {
//			String where = RegistersSQLiteTable.COLUMN_CATEGORY_ID + " IN " + ids;
//			database.delete(RegistersSQLiteTable.TABLE, where, null);				
//		} catch (Exception e) {
//			Log.e(LogTags.RegistersDataSource, e.getMessage());
//		}				
//	}
//	
//	private void deleteNonExistentCategories(ArrayList<Category> categories){
//		
//		ArrayList<Long> categoryIds = getCategoriesIds(categories);
//		String ids = parseForSqlIn(categoryIds);
//		String existentIds = getInverseCategoryIds(ids);
//		deleteCategoriesByIds(existentIds);		
//	}
//
//	public void open() throws SQLException {
//		database = dbHelper.getWritableDatabase();
//	}
//
//	public void close() {
//		dbHelper.close();
//	}
//	
//	public void saveCategory(Category category) {
//		contentDataSource.open();
//		try{
//			try {
//				doSaveImage(category.getImage());
//				doSaveCategory(category);
//				
//				for (Content content : category.getContent()) {
//					contentDataSource.saveContent(content);
//				}
//			} catch (Exception e) {
//				Log.e(LogTags.RegistersDataSource, e.getMessage());
//			}			
//		}
//		finally{
//			contentDataSource.close();
//		}
//	}
//	
//	public void saveCategories(ArrayList<Category> categories){
//		try {
//			deleteNonExistentCategories(categories);
//					
//			contentDataSource.open();
//			try
//			{
//				for (Category category : categories) {
//					doSaveImage(category.getImage());
//					doSaveCategory(category);
//					
//					for (Content content : category.getContent()) {
//						contentDataSource.saveContent(content);
//					}
//				}
//			}
//			finally{
//				contentDataSource.close();
//			}		
//			
//		} catch (Exception e) {
//			Log.e(LogTags.RegistersDataSource, e.getMessage());
//		}					
//	}
//
//	public void deleteAllRows() {
//		try {
//			database.delete(RegistersSQLiteTable.TABLE, null, null);				
//		} catch (Exception e) {
//			Log.e(LogTags.RegistersDataSource, e.getMessage());
//		}		
//	}
//
//	public void deleteRowBy(String column, int field) {
//		try {
//			String where = column + " = " + field;
//			database.delete(RegistersSQLiteTable.TABLE, where, null);				
//		} catch (Exception e) {
//			Log.e(LogTags.RegistersDataSource, e.getMessage());
//		}		
//	}
//
//	public boolean isCategoriesTableEmpty() {
//
//		boolean empty = false;		
//		try {
//			Cursor cursor = database.query(RegistersSQLiteTable.TABLE, RegistersSQLiteTable.ALL_COLUMNS, null, null, null, null, null);
//			try {
//				cursor.moveToFirst();
//				int count = cursor.getCount();
//				empty = count == 0;
//			} finally {
//				cursor.close();
//			}				
//		} catch (Exception e) {
//			Log.e("ERROR- CONSULTA SIZE DE PUBLICIDADES", e.getMessage());
//		}
//
//		return empty;
//	}
//
//	public Category getCategoryById(Long id) {
//
//		Category category = null;		
//		try {
//			String table = RegistersSQLiteTable.TABLE;
//			String where = RegistersSQLiteTable.COLUMN_CATEGORY_ID + " = " + id;
//			Cursor cursor = database.query(table, RegistersSQLiteTable.ALL_COLUMNS, where, null, null, null, null);
//			if (cursor != null) {
//				try {
//					if (cursor.moveToFirst()) {
//						category = cursorToCategory(cursor);
//					}
//				} finally {
//					cursor.close();
//				}
//			}				
//		} catch (Exception e) {
//			Log.e(LogTags.RegistersDataSource, e.getMessage());
//		}
//		
//		return category;
//	}
//
//	public ArrayList<Category> getHomeCategories() {
//		
//		ArrayList<Category> categories = new ArrayList<Category>();		
//		try {
//			String where = RegistersSQLiteTable.COLUMN_CATEGORY_LOCATION_PORTAL_ID + " = " + UbicacionPortalEnum.HOME.getValue();
//			Cursor cursor = database.query(RegistersSQLiteTable.TABLE, RegistersSQLiteTable.ALL_COLUMNS, where,null,null,null,null);				
//			categories = cursorToListOfCategory(cursor);
//			
//			for (Category category : categories) {
//				Image image = category.getImage() == null? null : getImageById(category.getImage().getId());
//				category.setImage(image);
//			}			
//		}
//		catch (Exception e){
//			Log.e(LogTags.RegistersDataSource, e.getMessage());
//		}
//				
//		return categories;			
//	}
//	
//	public ArrayList<Category> getCaroucelCategories() {
//		
//		ArrayList<Category> categories = new ArrayList<Category>();		
//		try {
//			String where = RegistersSQLiteTable.COLUMN_CATEGORY_LOCATION_PORTAL_ID + " = " + UbicacionPortalEnum.CARRUCEL.getValue();			
//			Cursor cursor = database.query(RegistersSQLiteTable.TABLE, RegistersSQLiteTable.ALL_COLUMNS, where,null,null,null,null);				
//			categories = cursorToListOfCategory(cursor);
//			for (Category category : categories) {
//				Image image = category.getImage() == null? null : getImageById(category.getImage().getId());
//				category.setImage(image);
//			}						
//		}
//		catch (Exception e){
//			Log.e(LogTags.RegistersDataSource, e.getMessage());
//		}
//				
//		return categories;			
//	}
//
//	private Image getImageById(Long id) {
//		contentDataSource.open();
//		try{
//			return contentDataSource.getImageById(id);
//		}
//		finally{
//			contentDataSource.close();
//		}
//	}
//
//	public void setLastSearch() {
//		boolean flag = this.isPublicityEnabled();
//		try {
//			this.open();
//			database.delete(ConfigurationSQLiteTable.TABLE, null, null);
//			
//			int value= (flag)?1:0;
//			String today = getTodayToString();
//			ContentValues values = new ContentValues();
//			values.put(ConfigurationSQLiteTable.COLUMN_CONFIGURATION_LAST_SEARCH, today);
//			values.put(ConfigurationSQLiteTable.COLUMN_CONFIGURATION_PUBLICITY_ENABLED, value);
//
//			database.insert(ConfigurationSQLiteTable.TABLE, null, values);
//		}finally{
//			this.close();
//		}
//				
//	}
//
//	public boolean shouldLook() {
//		
//		boolean should = true;
//		Cursor cursor = database.query(ConfigurationSQLiteTable.TABLE, ConfigurationSQLiteTable.ALL_COLUMNS, null,null,null,null,null);
//		if (cursor != null) {
//			try{
//				if (cursor.moveToFirst()){
//					String lastDate = cursor.getString(1);					
//					String today = getTodayToString();
//					//Compara equivalencia de Strings
//					if (today.equals(lastDate)){
//						should = false;
//					}
//				}
//			} finally {
//				cursor.close();						
//			}
//		}
//		
//		return should;
//		
//	}
//	
//	public void disablePublicity(){
//		this.setPublicityEnabled(false);
//		Log.d("Disable Publicity:", "Publicity correctly disabled ");
//		
//	}
//	public void enablePublicity(){
//		this.setPublicityEnabled(true);
//	}
//	public void setPublicityEnabled(boolean bool){
//		String lastSearch = this.getLastSearch();
//		try {
//			this.open();
//			try{
//				int value = (bool)?1:0;
//				database.delete(ConfigurationSQLiteTable.TABLE, null, null);
//				
//				ContentValues values = new ContentValues();
//				values.put(ConfigurationSQLiteTable.COLUMN_CONFIGURATION_PUBLICITY_ENABLED, value);			
//				values.put(ConfigurationSQLiteTable.COLUMN_CONFIGURATION_LAST_SEARCH, lastSearch);		
//				database.insert(ConfigurationSQLiteTable.TABLE, null, values);
//				Log.d("Modify Publicity Flag", "Publicity Flag set to "+value);
//
//			}
//			finally{
//				
//				this.close();				
//			}			
//		} catch (Exception e) {
//			Log.e("ERROR -  SETTING Publicity FLAG", e.getMessage());
//		}		
//	}
//	
//				
//	/**
//	 * Consulta el valor del flag publicity
//	 * @return
//	 */
//	public boolean isPublicityEnabled()
//	{
//		try {
//			this.open();
//			try {			
//				Cursor cursor = database.query(ConfigurationSQLiteTable.TABLE, 
//						ConfigurationSQLiteTable.ALL_COLUMNS, null, null, null, null, null);
//				
//				Log.d("IsPublicityhEnaled:", "Correctly created cursor");
//				if (cursor != null) {
//					try {
//						cursor.moveToFirst();
//						Log.d("IsPublicityEnabled: ", "Correctly moved to first row");
//						Log.d("IsPublicityEnabled: ", "columns are: "+cursor.getColumnNames() + " and quantity: "+ cursor.getColumnCount()+ " current cursor points this rows: "+cursor.getCount() );
//						int searchFlag=0;
//						while (!cursor.isAfterLast()) {
//							searchFlag = cursor.getInt(2);
//							Log.d("IsPublicityEnabled: ", "Correctly obtained search flag value, which is "+searchFlag);
//							return searchFlag == 1;
//						}						
//					} finally {
//						cursor.close();
//					}
//				}
//			} 
//			finally{
//				this.close();
//			}
//		}
//		catch (Exception e){
//			Log.e("ERROR AL DETERMINAR FLAG:", e.getMessage());
//		}	
//		return false;		
//	}
//	
//	public String getLastSearch()
//	{
//		try {
//			this.open();
//			try {			
//				Cursor cursor = database.query(ConfigurationSQLiteTable.TABLE, 
//						ConfigurationSQLiteTable.ALL_COLUMNS, null, null, null, null, null);
//				
//				if (cursor != null) {
//					try {
//						cursor.moveToFirst();
//						while (!cursor.isAfterLast()) {
//							String lastSearch = cursor.getString(1);
//							return lastSearch;
//						}
//					} finally {
//						cursor.close();
//					}
//				}
//			} 
//			finally{
//				this.close();
//			}
//		}
//		catch (Exception e){
//			Log.e("ERROR- OBTENER REDES DB:", e.getMessage());
//			return "";
//		}
//		
//		return "";		
//	}
//	private String getTodayToString()
//	{		
//		Date date = Calendar.getInstance().getTime();	
//		return (new SimpleDateFormat("yyyy-MM-dd", Locale.US)).format(date);		
//	}
	
	
}
