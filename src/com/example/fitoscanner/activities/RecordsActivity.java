package com.example.fitoscanner.activities;

import java.util.ArrayList;

import com.example.fitoscanner.R;
import com.example.fitoscanner.R.layout;
import com.example.fitoscanner.datasources.ImageDataSource;
import com.example.fitoscanner.datasources.SamplesDataSource;
import com.example.fitoscanner.helpers.CustomSampleListViewAdapter;
import com.example.fitoscanner.helpers.TypefacesHelper;
import com.example.fitoscanner.model.Image;
import com.example.fitoscanner.model.Sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecordsActivity extends Activity{
	public final static String TAG = "RecordsActivity";
	ImageDataSource imageDataSource;
	SamplesDataSource samplesDataSource;
	int samplePositionSelected = -1;
	private ArrayList<Sample> samples = new ArrayList<Sample>();
	private final Context context = this;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageDataSource = new ImageDataSource(this);
        samplesDataSource = new SamplesDataSource(this);
        
        
    }
	
	private void generateSamplesView(){
		setContentView(R.layout.records_layout);

        samples = this.getSamples();
        final ListView listview = (ListView) findViewById(R.id.savedSamplesList);
        final CustomSampleListViewAdapter customAdapter = new CustomSampleListViewAdapter(
        		 getApplicationContext(), R.layout.savedsample_fragment, samples);
        
         
         listview.setAdapter(customAdapter);
         Log.i(TAG, "Adapter for samples set...");
         setDeleteSampleButton();
         listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, final View view,
               int position, long id) {
        	   //El tostadoooorrrrrrrrrr
        	  
        	   Toast.makeText(getApplicationContext(),"Item "+ (position + 1),Toast.LENGTH_SHORT).show();
        	   samplePositionSelected = position;           	        
             
           }

         });
	}
	
	private void setDeleteSampleButton(){
		Button deleteButton = (Button) findViewById(R.id.button_deleteSample);
	     deleteButton.setOnClickListener(
	         new View.OnClickListener() {
	             @Override
	             public void onClick(View v) {
	            	 if(samplePositionSelected<0 || samplePositionSelected > samples.size()){
	            		 Toast.makeText(getApplicationContext(),"Seleccione una muestra primero",Toast.LENGTH_SHORT).show();
	            	 }else{
	            		 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
	 	         				context);
	 	          
	 	         			// set title
	 	         			alertDialogBuilder.setTitle("¿Eliminar muestra "+
	 	         			"'"+samples.get(samplePositionSelected).getSampleName()+ "'  de fecha " +
	 	         			samples.get(samplePositionSelected).getOriginDate()+ " ?");
	 	          
	 	         			// set dialog message
	 	         			alertDialogBuilder
	 	         				.setMessage("Elija una opción ")
	 	         				.setCancelable(false)
	 	         				.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
	 	         					public void onClick(DialogInterface dialog,int id) {
	 	         						deleteSample(samples.get(samplePositionSelected));
	 	         						generateSamplesView();
	 	         						
	 	         					}
	 	         				  })
	 	         				.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
	 	         					public void onClick(DialogInterface dialog,int id) {
	 	         						// if this button is clicked, just close
	 	         						// the dialog box and do nothing
	 	         						dialog.cancel();
	 	         					}
	 	         				});
	 	          
	 	         				// create alert dialog
	 	         				AlertDialog alertDialog = alertDialogBuilder.create();
	 	          
	 	         				// show it
	 	         				alertDialog.show();
	            	 }	            		         			            
	               }              
	         }
	     );
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		generateSamplesView();
	}
	
	/**
	 * Guarda la muestra que fue generada al tomar la primera foto
	 */
	public ArrayList<Sample> getSamples(){
		samplesDataSource.open();
    	try
    	{
    		
    		return samplesDataSource.getSamples();
    		
    	}
    	finally{
    		samplesDataSource.close();
    	}
	}
	
	public void deleteSample(Sample sample){
		samplesDataSource.open();
    	try
    	{
    		
    		samplesDataSource.deleteSample(sample);
    		
    	}
    	finally{
    		samplesDataSource.close();
    	}
	}
}
