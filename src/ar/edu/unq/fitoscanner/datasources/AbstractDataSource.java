package ar.edu.unq.fitoscanner.datasources;

import java.util.ArrayList;
import java.util.List;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ar.edu.unq.fitoscanner.data.FitoscannerSqLiteHelper;
import ar.edu.unq.fitoscanner.model.Image;

public abstract class AbstractDataSource<T> {
	private SQLiteDatabase database;
	private FitoscannerSqLiteHelper dbHelper;
	
	
	
	public SQLiteDatabase getDatabase() {
		return database;
	}

	public void setDatabase(SQLiteDatabase database) {
		this.database = database;
	}

	public FitoscannerSqLiteHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(FitoscannerSqLiteHelper dbHelper) {
		this.dbHelper = dbHelper;
	}
	
	/**
	 * Abre una conexion a la base
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		setDatabase(getDbHelper().getWritableDatabase());
	}
	/**
	 * Cierra la conexion a la base
	 */
	public void close() {
		getDbHelper().close();
		
	}
	
	
	public List<T> getEntitiesForIds(String ids) {
		
		// x-y-z
		// =>  [(x),(y),(z) ]
		List<T> result = new ArrayList<T>();
		T target;
		if (ids.contains("-")) {
			for (String retval: ids.split("-")){
				try {
					target = getById(Long.parseLong(retval));
					result.add(target);
				} catch (Exception e) {
					Log.e("Abstract Data Source", "Error trying to get entity for id "+retval);
				}
		      }
		} else {
			if(!ids.equals("")){
				try {
					target = getById(Long.parseLong(ids));
					result.add(target);
				} catch (Exception e) {
					Log.e("Abstract Data Source", "Error trying to get entity for id "+ids);
				}
			}
		}
		return result;
	}
	
	public abstract T getById(Long id);
	

}
