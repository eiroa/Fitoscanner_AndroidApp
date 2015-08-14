package ar.edu.unq.fitoscanner.datasources;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ar.edu.unq.fitoscanner.data.ConfigurationSQLiteTable;
import ar.edu.unq.fitoscanner.data.FitoscannerSqLiteHelper;
import ar.edu.unq.fitoscanner.model.Configuration;

public class ConfigurationDataSource extends AbstractDataSource{
	public final static String TAG = "ConfigurationDataSource";

	public ConfigurationDataSource(Context context) {
		setDbHelper(new FitoscannerSqLiteHelper(context));
	}
	
	/**
	 * Construye un objeto Configuration 
	 * @param cursor
	 * @return
	 */
		private Configuration cursorToConfiguration(Cursor cursor) {
			Integer id = cursor.getInt(0);
			String ip= cursor.getString(1);
			String nick = cursor.getString(2);
			String passHash= cursor.getString(3);
			String name = cursor.getString(4);
			String surname = cursor.getString(5);
			Integer db  = cursor.getInt(6);
			Boolean logged = ( 1 == cursor.getInt(7)?true:false);
			Log.d("Configuration Datasource", "Getting configuration of user: "+nick+" with logged state: "+logged);
			Configuration conf = new Configuration(id,ip,nick,passHash,name,surname,db,logged);
			return conf;
		}

	public Long doSaveIp(String ip) {
		Long idResult = 0L;
		ContentValues values = new ContentValues();
		values.put(ConfigurationSQLiteTable.COLUMN_CONFIGURATION_ID, 1);
		values.put(ConfigurationSQLiteTable.COLUMN_SERVER_IP, ip);
		getDatabase().update(ConfigurationSQLiteTable.TABLE, values,
				ConfigurationSQLiteTable.COLUMN_CONFIGURATION_ID + " = " + 1, null);
		
		return idResult;
	}
	
	public void doSaveConfiguration(Configuration conf) {		
		if (conf != null)
		{
			
			String ip = conf.getIp();
			String nick = conf.getNick();
			String passHash= conf.getPass();
			String name = conf.getName();
			String surname = conf.getSurname();
			Integer dbVersion = conf.getDbVersion();
			Integer logged = conf.isLogged()?1:0;
			Log.d("Configuration datasource", "Updating configuration with nick: "+nick+ " and logged state: " +logged);
			
			ContentValues values = new ContentValues();
			values.put(ConfigurationSQLiteTable.COLUMN_CONFIGURATION_ID, 1);
			values.put(ConfigurationSQLiteTable.COLUMN_SERVER_IP, ip);
			values.put(ConfigurationSQLiteTable.COLUMN_USER_NICK, nick);
			values.put(ConfigurationSQLiteTable.COLUMN_USER_PASS, passHash);
			values.put(ConfigurationSQLiteTable.COLUMN_USER_NAME, name);
			values.put(ConfigurationSQLiteTable.COLUMN_USER_SURNAME, surname);
			values.put(ConfigurationSQLiteTable.COLUMN_USER_DATABASE_VERSION, dbVersion);
			values.put(ConfigurationSQLiteTable.COLUMN_USER_LOGGED, logged);
			
			if(this.configurationExists(conf))
			{
				getDatabase().update(ConfigurationSQLiteTable.TABLE, values, ConfigurationSQLiteTable.COLUMN_CONFIGURATION_ID + " = " + 1, null);			
			} else {
				getDatabase().insert(ConfigurationSQLiteTable.TABLE, null, values);	
			}	
		}		
	}
	
	public Configuration getById(Long id){
		return getConfigurationById(id.intValue());
	}
	
	public Configuration getConfigurationById(Integer id) {
		Configuration conf = null;		
		try {
			String table = ConfigurationSQLiteTable.TABLE;
			String where = ConfigurationSQLiteTable.COLUMN_CONFIGURATION_ID + " = " + id;
			Cursor cursor = getDatabase().query(table, ConfigurationSQLiteTable.ALL_COLUMNS, where, null, null, null, null);
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						conf = cursorToConfiguration(cursor);
					}
				} finally {
					cursor.close();
				}
			}				
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		return conf;
	}
	
	public boolean configurationExists(Configuration conf) {

		boolean exists = false;
		try {
			String table = ConfigurationSQLiteTable.TABLE;
			String where = ConfigurationSQLiteTable.COLUMN_CONFIGURATION_ID + " = " + conf.getId();
			Cursor cursor = getDatabase().query(table, ConfigurationSQLiteTable.ALL_COLUMNS, where, null, null, null, null);						
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
			Log.e(TAG, ("configuration not found" + conf.getId().toString()));
		}		

		return exists;
	}



}
