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
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_layout);
        imageDataSource = new ImageDataSource(this);
        samplesDataSource = new SamplesDataSource(this);
        final ArrayList<Sample> samples = this.getSamples();
        final ListView listview = (ListView) findViewById(R.id.savedSamplesList);
        final CustomSampleListViewAdapter customAdapter = new CustomSampleListViewAdapter(
        		 getApplicationContext(), R.layout.savedsample_fragment, samples);
        
         
         listview.setAdapter(customAdapter);
         Log.i(TAG, "Adapter for samples set...");
         listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, final View view,
               int position, long id) {
        	   Toast toast = Toast.makeText(getApplicationContext(),
        	            "Item " + (position + 1) + ": " + samples.get(position),
        	            Toast.LENGTH_SHORT);
        	        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        	        toast.show();
             
           }

         });
        
    }
	
	@Override
	protected void onStart() {
		super.onStart();
//		ArrayList<Sample>samples = this.getSamples();
//		Log.i(TAG, "Se han obtenido las muestras = "+samples.toString() + " son un total de "+samples.size()+ " muestras");
//		
//		for (Sample sample : samples) {
//			
//			for (Image image : sample.getImages()) {
//				Log.i(TAG, "Muestra con Id:"+sample.getId()+ " de fecha "+sample.getOriginDate()+ 
//						" tiene imagen "+image.getId()+ " con nombre "+image.getTitle());
//			}
//			
//		}
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
}
