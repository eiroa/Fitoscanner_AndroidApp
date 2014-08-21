package ar.edu.unq.fitoscanner.activities;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.helpers.GPSHelper;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;

@SuppressLint("NewApi")
public class MenuActivity extends Activity {
	//
	GPSHelper gps;
	Button btnShowLocation;
	MenuActivity activity;
	private boolean usesLocation = false;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        this.activity = this;
        
        Button bt = (Button) findViewById(R.id.buttonMenuLogout);
        Typeface font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
        btnShowLocation = (Button) findViewById(R.id.buttonShowLocation);
        btnShowLocation.setTypeface(font);
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
                    Toast.makeText(getApplicationContext(), "Tu ubicación es - \nLat: " + latitude + "\nLong: " + 
                    longitude + "\n  Lugar geográfico: "+gps.getAddress(), Toast.LENGTH_SHORT).show();    
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
            	intent.putExtra("usesLocation", usesLocation);
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
	
	
	@Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu_gps, menu); 
		menu.findItem(R.id.activateSampleLocation).setVisible(false);
		menu.findItem(R.id.deactivateSampleLocation).setVisible(false);
		
        return true;  
    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (this.usesLocation){
			menu.findItem(R.id.deactivateSampleLocation).setVisible(true);
			
		}else{
			menu.findItem(R.id.activateSampleLocation).setVisible(true);
			
		}
		return super.onPrepareOptionsMenu(menu);
	}
      
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) { 
    	item.setVisible(false);
        switch (item.getItemId()) {  
            case R.id.activateSampleLocation:  
              Toast.makeText(getApplicationContext(),"Se intentará obtener la ubicación geográfica para las muestras a obtener",Toast.LENGTH_SHORT).show();  
              this.usesLocation = true;
              break;
            case R.id.deactivateSampleLocation:  
                Toast.makeText(getApplicationContext(),"No se adjuntará la ubicación geográfica para las muestras",Toast.LENGTH_SHORT).show();  
                this.usesLocation = false;
                break;
              default:  
                return super.onOptionsItemSelected(item);  
        }
        invalidateOptionsMenu();
        return true;
        
    }
    
    @Override
    protected void onStop() {
    	try {
    		gps.stopSelf();
		} catch (Exception e) {
		}
    	
    	super.onStop();
    }
    
    @Override
    protected void onPause() {
    	try {		
    		gps.stopSelf();
		} catch (Exception e) {
		}
    	
    	super.onPause();
    }
    

}
