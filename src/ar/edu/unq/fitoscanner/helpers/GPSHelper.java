package ar.edu.unq.fitoscanner.helpers;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class GPSHelper extends Service implements LocationListener {
	 
    private final Context mContext;
    Geocoder gcd; 
    List<Address> addresses = null;
 
    // flag for GPS status
    boolean isGPSEnabled = false;
 
    // flag for network status
    boolean isNetworkEnabled = false;
 
    // flag for GPS status
    boolean canGetLocation = false;
 
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
 
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
 
    // Declaring a Location Manager
    protected LocationManager locationManager;
 
    public GPSHelper(Context context) {
        this.mContext = context;
        gcd = new Geocoder(mContext, Locale.getDefault());
        getLocation();
    }
 
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
 
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
 
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network is enabled");
                    if (locationManager != null) {
                    	
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                        	try {
                    			addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    		} catch (IOException e) {
                    			// TODO Auto-generated catch block
                    			e.printStackTrace();
                    		}
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
//                	Toast.makeText(mContext, "Gps is enabled", Toast.LENGTH_SHORT).show();;
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                            	try {
                        			addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        		} catch (IOException e) {
                        			// TODO Auto-generated catch block
                        			e.printStackTrace();
                        		}
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }
     
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSHelper.this);
        }       
    }
     
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
         
        // return latitude
        return latitude;
    }
     
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
         
        // return longitude
        return longitude;
    }
    
    public String getAddress(){
    	String city = "Desconocido";
		String state = " ";
		String country = "";
		String result = "";
		try {
			city = addresses.get(0).getLocality();
			state = addresses.get(0).getAdminArea();
			country = addresses.get(0).getCountryName();
		} catch (NullPointerException e) {
			Log.e("GPSHelper", "Error, addresses var is null,  internet deisabled?");
		}
    	return city + ","+state+","+country;
    	
    }
     
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
     
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }
 
    @Override
    public void onLocationChanged(Location location) {
    	
    	
		try {
			addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String city = "Desconocido";
		String state = "";
		String country = "";
		try {
			city = addresses.get(0).getLocality();
			state = addresses.get(0).getAdminArea();
			country = addresses.get(0).getCountryName();
		} catch (NullPointerException e) {
			// TODO: handle exception
		}
    	if (addresses != null && addresses.size() > 0) {
    		Log.d("Location updated", "Latitude:" +location.getLatitude() + "--- Longitude: \n"+location.getLongitude());
        	Toast.makeText(mContext, "Location updated..   \n"+"Latitude:" +location.getLatitude() + 
        			"--- Longitude: "+location.getLongitude() + "\n Closest City: "+
        			city + "\n "+"state: "+state+  "\n country:"+country , Toast.LENGTH_SHORT).show();
    	}
    	Toast.makeText(mContext, "Location updated..   \n"+"Latitude:" +location.getLatitude() + 
    			"\n Longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
    	
    }
 
    @Override
    public void onProviderDisabled(String provider) {
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
 
}
