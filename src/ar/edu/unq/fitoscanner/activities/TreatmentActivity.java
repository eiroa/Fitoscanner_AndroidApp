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

public class TreatmentActivity extends Activity{
	public final static String TAG = "RecordsActivity";
	ImageDataSource imageDataSource;
	TreatmentDataSource tds;
	private Treatment treatment;
	private Long idTreatment;
	private final Context context = this;
	private Typeface font;
	private ListView listviewTreatmentTreatment;
	private Bitmap imageSelected;
	private TextView txtTreatmentNameLabel;
	private TextView txtTreatmentDescriptionLabel;
	private TextView txtTreatmentName;
	private TextView txtTreatmentDescription;
	private CustomImageListViewAdapter customAdapterTreatmentImages;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.gc();
        font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
        imageDataSource = new ImageDataSource(this);
        tds = new TreatmentDataSource(this);
        idTreatment = (Long) getIntent().getLongExtra("idTreatment", 0L);
        getTreatment();
        
    }
	
	private void generateTreatmentsView(){
		setContentView(R.layout.treatment_layout);
        
        
        initiateTextFields();
        configureTextFields();
        listviewTreatmentTreatment = (ListView) findViewById(R.id.treatmentImagesList);
        
        
        
         
        listviewTreatmentTreatment.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, final View view,
               int position, long id) {
        	   Image i = (Image)listviewTreatmentTreatment.getAdapter().getItem(position);
        	   
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
		txtTreatmentNameLabel = (TextView) findViewById(R.id.treatmentName_label);
        txtTreatmentDescriptionLabel= (TextView) findViewById(R.id.treatmentDescription_label);
        
        txtTreatmentName = (TextView) findViewById(R.id.treatmentName_text);
        txtTreatmentDescription = (TextView) findViewById(R.id.treatmentDescription_text);
	}
	
	
	private void setTextFieldsVisibility(Integer v){
		txtTreatmentNameLabel.setVisibility(v);
        txtTreatmentDescriptionLabel.setVisibility(v);
        
        txtTreatmentName.setVisibility(v);
        txtTreatmentDescription.setVisibility(v);
	}
	
	private void anulateAllTextViews(){
		setTextFieldsVisibility(View.GONE);
	}
	
	private void configureTextFields(){
        setTextFieldsVisibility(View.VISIBLE);
        
        txtTreatmentName.setText(treatment.getName());
        txtTreatmentDescription.setText(treatment.getDescription());
        
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
	
	public void getTreatment(){
		tds.open();
    	try
    	{
    		treatment = tds.getById(idTreatment);
    	}
    	finally{
    		tds.close();
    	}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		generateTreatmentsView();
		customAdapterTreatmentImages = new CustomImageListViewAdapter(
				getApplicationContext(),
				R.layout.samplepreview_fragment, treatment.getImages());
        listviewTreatmentTreatment.setAdapter(customAdapterTreatmentImages); 
	}
	
	
}
