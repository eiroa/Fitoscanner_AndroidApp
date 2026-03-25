package ar.edu.unq.fitoscanner.helpers;

import android.R.string;

public class URLHelper { 
	// Remote production server
//	public static final String SERVER_ADDRESS = "http://104.236.252.51:8080/tip_eiroa_mauro_server_backend/rest";

	// Local development server via ADB reverse port forwarding (adb reverse tcp:8081 tcp:8081)
	public static final String SERVER_ADDRESS = "http://127.0.0.1:8081/tip_eiroa_mauro_server_backend/rest";
}
