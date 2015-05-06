package ar.edu.unq.fitoscanner.datasources;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import ar.edu.unq.fitoscanner.data.FitoscannerSqLiteHelper;

public abstract class AbstractDataSource {
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

}
