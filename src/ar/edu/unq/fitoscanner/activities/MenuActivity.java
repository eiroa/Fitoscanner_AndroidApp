package ar.edu.unq.fitoscanner.activities;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.helpers.GPSHelper;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;

public class MenuActivity extends Activity {
	//
	GPSHelper gps;
	Button btnShowLocation;
	MenuActivity activity;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        this.activity = this;
        
        Button bt = (Button) findViewById(R.id.buttonMenuLogout);
        Typeface font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
        btnShowLocation = (Button) findViewById(R.id.buttonShowLocation);
        
        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View arg0) {        
                // create class object
                gps = new GPSHelper(MenuActivity.this);
 
                // check if GPS enabled     
                if(gps.canGetLocation()){
                     
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                     
                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + 
                    longitude + "\n  Closest city: "+gps.getAddress(), Toast.LENGTH_SHORT).show();    
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
                 
            }
        });
        bt.setTypeface(font);
        bt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {       		
        		activity.finish();
        		LoginActivity.logged = false;
            	Toast.makeText(activity.getApplicationContext(),"Se ha desconectado",Toast.LENGTH_LONG).show();       		
            }
        });
        
        Button goTakePic = (Button)findViewById(R.id.buttonOpenTakePic);
        goTakePic.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {
            	Intent intent = new Intent(activity, MakePhotoActivity.class);
        		activity.startActivity(intent);      		
            }
            
        });
        goTakePic.setTypeface(font);
        Button goRecords = (Button)findViewById(R.id.buttonOpenRecords);
        goRecords.setOnClickListener(new View.OnClickListener() {
        	
        	public void onClick(View arg0) {
            	Intent intent = new Intent(activity, RecordsActivity.class);
        		activity.startActivity(intent);      		
            }
        	
            
        });
        goRecords.setTypeface(font);
    }
}
