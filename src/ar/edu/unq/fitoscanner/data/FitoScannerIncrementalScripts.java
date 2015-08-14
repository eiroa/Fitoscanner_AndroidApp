package ar.edu.unq.fitoscanner.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FitoScannerIncrementalScripts {
	
	
	
	private static final String incremental_script_to_db_2 = 
			" ALTER TABLE "+ImageSQLiteTable.TABLE +" ADD " // SI SE agrega columna al final, sentencia column no es ncesaria
			+ ImageSQLiteTable.COLUMN_IMAGE_SENT + " integer; "+
			" ALTER TABLE "+ConfigurationSQLiteTable.TABLE +" ADD "
			+ ConfigurationSQLiteTable.COLUMN_USER_DATABASE_VERSION + " integer; ";
	
	private static final String incremental_script_to_db_3 = 
			" ALTER TABLE "+ConfigurationSQLiteTable.TABLE +" ADD "
			+ ConfigurationSQLiteTable.COLUMN_USER_LOGGED + " integer; ";
	
	
	public static final Map<Integer, String> INCREMENTAL_SCRIPTS;
    static {
        Map<Integer, String> aMap = new HashMap<Integer,String>();
        aMap.put(1, incremental_script_to_db_2); // al ejecutarse ->  db version = 2
        aMap.put(2, incremental_script_to_db_3);
        INCREMENTAL_SCRIPTS = Collections.unmodifiableMap(aMap);
    }
}
