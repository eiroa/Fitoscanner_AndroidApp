package ar.edu.unq.fitoscanner.data;

public class ConfigurationSQLiteTable {
	
	// Table name
	public static final String TABLE = "configuration";
	
	// Column names
	public static final String COLUMN_CONFIGURATION_ID = "_id";
	public static final String COLUMN_SERVER_IP = "serverIp";
	public static final String COLUMN_USER_NICK = "nick";
	public static final String COLUMN_USER_PASS = "pass";
	public static final String COLUMN_USER_NAME = "name";
	public static final String COLUMN_USER_SURNAME = "surname";
	

	
	public static String[] ALL_COLUMNS = {
			ConfigurationSQLiteTable.COLUMN_CONFIGURATION_ID,
			ConfigurationSQLiteTable.COLUMN_SERVER_IP,
			ConfigurationSQLiteTable.COLUMN_USER_NICK,
			ConfigurationSQLiteTable.COLUMN_USER_PASS,
			ConfigurationSQLiteTable.COLUMN_USER_NAME,
			ConfigurationSQLiteTable.COLUMN_USER_SURNAME};
	

}
