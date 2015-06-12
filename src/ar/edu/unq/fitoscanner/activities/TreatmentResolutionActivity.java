package ar.edu.unq.fitoscanner.activities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract.Instances;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.datasources.ConfigurationDataSource;
import ar.edu.unq.fitoscanner.datasources.ImageDataSource;
import ar.edu.unq.fitoscanner.datasources.SamplesDataSource;
import ar.edu.unq.fitoscanner.datasources.TreatmentDataSource;
import ar.edu.unq.fitoscanner.datasources.TreatmentResolutionDataSource;
import ar.edu.unq.fitoscanner.helpers.Base64Helper;
import ar.edu.unq.fitoscanner.helpers.CustomImageListViewAdapter;
import ar.edu.unq.fitoscanner.helpers.CustomSampleListViewAdapter;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;
import ar.edu.unq.fitoscanner.helpers.URLHelper;
import ar.edu.unq.fitoscanner.model.Image;
import ar.edu.unq.fitoscanner.model.Sample;
import ar.edu.unq.fitoscanner.model.Treatment;
import ar.edu.unq.fitoscanner.model.TreatmentResolution;

public class TreatmentResolutionActivity extends Activity{
	public final static String TAG = "RecordsActivity";
	ImageDataSource imageDataSource;
	TreatmentResolutionDataSource trds;
	TreatmentDataSource tds;
	private TreatmentResolution treatmentResolution;
	private List<Treatment> treatments = new ArrayList<Treatment>();
	private Long idTreatmentResolution;
	private final Context context = this;
	private Typeface font;
	private Spinner spinner1;
	private TextView txtSample;
	private TextView txtCountry;
	private List<Image> currentTreatmentImages;
	private Treatment currentTreatment;
	private HashMap<String, Treatment> treatmentMap;
	private ListView listviewTreatmentSpecie;
	private CustomImageListViewAdapter customAdapterSpecieImages;
	private Bitmap imageSelected;
	private TextView txtSpecieNameLabel;
	private TextView txtSpecieScientificNameLabel;
	private TextView txtSpecieDescriptionLabel;
	private TextView txtSpecieName;
	private TextView txtSpecieScientificName;
	private TextView txtSpecieDescription;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
        imageDataSource = new ImageDataSource(this);
        trds = new TreatmentResolutionDataSource(this);
        tds = new TreatmentDataSource(this);
        
    }
	
	private void generateTreatmentsView(){
		setContentView(R.layout.treatment_resolution_layout);
        treatments = treatmentResolution.getTreatments();
        
        //check that we really have them.
        
        Log.d(TAG, " showing treatments obtained");
        for (Treatment r : treatments) {
//			Log.d(TAG, "treatment => "+r.toString());
		}
        
        //Spinner seleccionador de muestras
        setSpinnerSelector();
        initiateTextFields();
        configureTextFields();
        listviewTreatmentSpecie = (ListView) findViewById(R.id.specieImagesList);
        
        
        //Anulamos elementos de muestra al inicio del activity
        customAdapterSpecieImages = new CustomImageListViewAdapter(
				getApplicationContext(),
				R.layout.samplepreview_fragment, treatmentResolution.getSpecieImages());
        listviewTreatmentSpecie.setAdapter(customAdapterSpecieImages); 
         
        listviewTreatmentSpecie.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, final View view,
               int position, long id) {
        	   Image i = (Image)listviewTreatmentSpecie.getAdapter().getItem(position);
        	   
        	   //Optimizacion de ram
        	   // Queremos ver bien la imagen, por lo tanto, escalamos la imagen al tamaño del dispositivo
        	   imageSelected =Base64Helper.decodeScaledBase64(i.getBase64(),
       	    		getWindowManager().getDefaultDisplay().getWidth(),
       	    		getWindowManager().getDefaultDisplay().getWidth());
        	    final Dialog builder = new Dialog(context);
        	    builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        	    builder.setContentView(R.layout.image_popup);
         	    ImageView imgPopup = (ImageView) builder.findViewById(R.id.image_popup);
         	    Display display = getWindowManager().getDefaultDisplay();
         	    Point size = new Point();
         	    display.getSize(size);	
         	    
        	    imgPopup.setImageBitmap(imageSelected);
        	    imgPopup.getLayoutParams().height = (int) (size.y*0.75);
        	    imgPopup.getLayoutParams().width = (int) (size.x*0.90);
        	    builder.show();
             
           }
         });
	}
	
	private void initiateTextFields(){
		txtSpecieNameLabel = (TextView) findViewById(R.id.specieName_label);
        txtSpecieScientificNameLabel = (TextView) findViewById(R.id.specieScientificName_label);
        txtSpecieDescriptionLabel= (TextView) findViewById(R.id.specieDescription_label);
        
        txtSpecieName = (TextView) findViewById(R.id.specieName_text);
        txtSpecieScientificName = (TextView) findViewById(R.id.specieScientificName_text);
        txtSpecieDescription = (TextView) findViewById(R.id.specieDescription_text);
	}
	
	
	private void setTextFieldsVisibility(Integer v){
		txtSpecieNameLabel.setVisibility(v);
		txtSpecieScientificNameLabel.setVisibility(v);
        txtSpecieDescriptionLabel.setVisibility(v);
        
        txtSpecieName.setVisibility(v);
        txtSpecieScientificName.setVisibility(v);
        txtSpecieDescription.setVisibility(v);
	}
	
	private void anulateAllTextViews(){
		setTextFieldsVisibility(View.GONE);
	}
	
	private void configureTextFields(){
        setTextFieldsVisibility(View.VISIBLE);
        
        txtSpecieName.setText(treatmentResolution.getSpecieName());
        txtSpecieScientificName.setText(treatmentResolution.getSpecieScientificName());
        txtSpecieDescription.setText(treatmentResolution.getSpecieDescription());
        
	}
	
	public void setSpinnerSelector(){
		spinner1 = (Spinner) findViewById(R.id.spinner1);
        List<String> list = new ArrayList<String>();
        
        list.add("Seleccione un tratamiento");
        int i = 1;
        for (Treatment r : treatments) {
			list.add(i+". "+r.getName());
			treatmentMap.put(i+". "+r.getName(), r);
			i++;
		}
        
         
        if(treatments.isEmpty()){
        	
        	View buttons = (View) findViewById(R.id.sampleActionButtons_layout);
        	View spinner = (View) findViewById(R.id.spinner1);
        	spinner.setVisibility(View.GONE);
        	buttons.setVisibility(View.GONE);
        }else{
        	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
            (this, R.layout.custom_spinner_item,list);
        	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        	spinner1.setAdapter(dataAdapter);
        	//seter comportamiento al seleccionar elemento de spinner
        	addListenerOnSpinnerItemSelection();
        }
	}
	
	public void addListenerOnSpinnerItemSelection(){
        
        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(final AdapterView<?> parent, View view,
					final int position, long id) {
				if(!parent.getItemAtPosition(position).toString().equals("Seleccione un tratamiento")){
					currentTreatment = treatmentMap.get(parent.getItemAtPosition(position).toString());
					try {
						Intent intent = new Intent(context, TreatmentActivity.class);
					    intent.putExtra("idTreatment", currentTreatment.getId());
					    intent.putExtra("idTreatmentResolution", idTreatmentResolution);
						onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
						customAdapterSpecieImages.clear();
						listviewTreatmentSpecie.destroyDrawingCache();
						currentTreatment = null;
						System.gc();
					    
						context.startActivity(intent); 
					} catch (OutOfMemoryError e) {
						finish();
						Intent intent = new Intent(context, TreatmentActivity.class);
					    intent.putExtra("idTreatment", currentTreatment.getId());
					    intent.putExtra("idTreatmentResolution", idTreatmentResolution);
						context.startActivity(intent);
					}
					 
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}

	private void setDeleteSampleButton(){
		Button deleteButton = (Button) findViewById(R.id.button_deleteSample);
		deleteButton.setTypeface(font);
	    deleteButton.setOnClickListener(
	         new View.OnClickListener() {
	             @Override
	             public void onClick(View v) {
	            	 	            		         			            
	               }              
	         }
	    );
	}
	
	private void prepareDialog(AlertDialog.Builder builder,String title,String message,
			Boolean isCancelable,String OkButtonTxt, android.content.DialogInterface.OnClickListener okListener, 
			String cancelButtonTxt, android.content.DialogInterface.OnClickListener cancelListener){
		    builder
			.setTitle(title)
			.setMessage(message)
			.setCancelable(isCancelable)
			.setPositiveButton(OkButtonTxt,okListener)
			.setNegativeButton(cancelButtonTxt,cancelListener);
	}
	
	private void setOpenResolutionButton(){
		Button sendButton = (Button) findViewById(R.id.button_open_resolution);
		sendButton.setTypeface(font);
		sendButton.setOnClickListener(
	         new View.OnClickListener() {
	             @Override
	             public void onClick(View v) {
	               }              
	         }
		);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		currentTreatmentImages = new ArrayList<Image>();
        treatmentMap = new HashMap<String, Treatment>();
        idTreatmentResolution = (Long) getIntent().getLongExtra("idTreatmentResolution", 0L);
        getTreatmentResolution();
		generateTreatmentsView();
	}
	
	/**
	 * Obtiene la resolucion por id
	 */
	public void getTreatmentResolution(){
		trds.open();
    	try
    	{
    		treatmentResolution = trds.getById(idTreatmentResolution);
    	}
    	finally{
    		trds.close();
    	}
	}
	
	
	
}
