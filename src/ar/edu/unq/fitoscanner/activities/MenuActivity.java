package ar.edu.unq.fitoscanner.activities;



import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.data.FitoscannerSqLiteHelper;
import ar.edu.unq.fitoscanner.datasources.ConfigurationDataSource;
import ar.edu.unq.fitoscanner.helpers.ArrayHelper;
import ar.edu.unq.fitoscanner.helpers.GPSHelper;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;
import ar.edu.unq.fitoscanner.model.Configuration;
import ar.edu.unq.fitoscanner.model.Image;

@SuppressLint("NewApi")
public class MenuActivity extends Activity {
	//
	GPSHelper gps;
	Button btnCreateSample;
	MenuActivity activity;
	ConfigurationDataSource configurationDataSource;
	private String result;
	final Context context = this;
	private boolean usesLocation = true;
	private boolean usesLowDefinition = true;
	public boolean debug = true;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        this.activity = this;
        configurationDataSource = new ConfigurationDataSource(this);
        Button btLogout = (Button) findViewById(R.id.buttonMenuLogout);
        Typeface font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
        btnCreateSample = (Button) findViewById(R.id.buttonCreateSample);
        btnCreateSample.setTypeface(font);
        btnCreateSample.setOnClickListener(new View.OnClickListener(){
			
			@Override
			public void onClick(View v) {
				ArrayHelper ar = new ArrayHelper(context);
				ar.saveArray("paths", new ArrayList<String>());
				ar.saveArray("base64s", new ArrayList<String>());
				Intent intent = new Intent(activity, CreateSampleActivity.class);
        		activity.startActivity(intent);
			}
		});
        btLogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {       		
        		Configuration conf = getConfiguration();
        		conf.setLogged(false);
        		saveConfiguration(conf);
            	Toast.makeText(activity.getApplicationContext(),"Se ha desconectado",Toast.LENGTH_LONG).show();
            	activity.finish();
            }
        });
        
        Button goTakePic = (Button)findViewById(R.id.buttonOpenTakePic);
        goTakePic.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {
        		if(debug)Log.d("Menu Activity", "Starting Make Photo Activity from Menu");
            	Intent intent = new Intent(activity, MakePhotoActivity.class);
            	intent.putExtra("usesLocation", usesLocation);
            	intent.putExtra("usesLowDefinition",usesLowDefinition);
        		activity.startActivity(intent);
            }
            
        });
        Button goRecords = (Button)findViewById(R.id.buttonOpenRecordsMenu);
        goRecords.setOnClickListener(new View.OnClickListener() {
        	
        	public void onClick(View arg0) {
            	Intent intent = new Intent(activity, RecordsMenuActivity.class);
        		activity.startActivity(intent);      		
            }
        	
            
        });
        
        btLogout.setTypeface(font);
        goTakePic.setTypeface(font);
        goRecords.setTypeface(font);
    }
	
	
	@Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu_options, menu); 
		menu.findItem(R.id.activateSampleLocation).setVisible(false);
		menu.findItem(R.id.deactivateSampleLocation).setVisible(false);
		menu.findItem(R.id.useLowDefinition).setVisible(false);
		menu.findItem(R.id.useHighDefinition).setVisible(false);
		
        return true;  
    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (this.usesLocation){
			menu.findItem(R.id.deactivateSampleLocation).setVisible(true);
			
		}else{
			menu.findItem(R.id.activateSampleLocation).setVisible(true);
			
		}
		
		if (this.usesLowDefinition){
			menu.findItem(R.id.useHighDefinition).setVisible(true);
			
		}else{
			menu.findItem(R.id.useLowDefinition).setVisible(true);
			
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
            case R.id.useLowDefinition:  
                Toast.makeText(getApplicationContext(),"Se utilizará baja definición para las imágenes de  nuevas muestras",Toast.LENGTH_SHORT).show();  
                this.usesLowDefinition = true;
                break;
            case R.id.useHighDefinition:  
                Toast.makeText(getApplicationContext(),"Se utilizará alta definición para las imágenes de  nuevas muestras \n Esto aumentará el consumo de datos utilizados",Toast.LENGTH_SHORT).show();  
                this.usesLowDefinition = false;
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
    
    /**
     * Permite un cambio temporal de la ip a la que mandar las muestras. 
     */
    private void showIpDialog(){
    	LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.ip_prompt, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Update IP",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
				result =userInput.getText().toString();
				configurationDataSource.open();
				try {
					Configuration conf = configurationDataSource.getConfigurationById(1);
					if(conf != null){
						conf.setIp(result);
					}else{
						conf = new Configuration(1, result,null,null,null,null,FitoscannerSqLiteHelper.DATABASE_VERSION,false);
					}
					configurationDataSource.doSaveConfiguration(conf);
				}finally{
		    		configurationDataSource.close();
		    	}
				Toast.makeText(getApplicationContext(), "New ip saved: "+result, Toast.LENGTH_SHORT).show();
				dialog.cancel();
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
    }
    
    private void saveConfiguration(Configuration conf){
		configurationDataSource.open();
    	try
    	{	
			configurationDataSource.doSaveConfiguration(conf);
    	}
    	finally{
    		configurationDataSource.close();
    	}
	}
	
	private Configuration getConfiguration(){
		Configuration conf  = null;
		configurationDataSource.open();
    	try
    	{	
			conf = configurationDataSource.getConfigurationById(1);
    	}
    	finally{
    		configurationDataSource.close();
    	}
    	return conf;
	}
    

}
