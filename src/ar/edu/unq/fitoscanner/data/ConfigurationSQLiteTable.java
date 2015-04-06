package ar.edu.unq.fitoscanner.data;

public class ConfigurationSQLiteTable {
	
	// Table name
	public static final String TABLE = "configuration";
	
	// Column names
	public static final String COLUMN_CONFIGURATION_ID = "_id";
	public static final String COLUMN_SERVER_IP = "serverIp";
	

	
	public static String[] ALL_COLUMNS = {
			ConfigurationSQLiteTable.COLUMN_CONFIGURATION_ID,
			ConfigurationSQLiteTable.COLUMN_SERVER_IP};
	

}
