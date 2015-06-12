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
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract.Instances;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.R.layout;
import ar.edu.unq.fitoscanner.datasources.ConfigurationDataSource;
import ar.edu.unq.fitoscanner.datasources.ImageDataSource;
import ar.edu.unq.fitoscanner.datasources.SamplesDataSource;
import ar.edu.unq.fitoscanner.helpers.Base64Helper;
import ar.edu.unq.fitoscanner.helpers.CustomImageListViewAdapter;
import ar.edu.unq.fitoscanner.helpers.CustomSampleListViewAdapter;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;
import ar.edu.unq.fitoscanner.helpers.URLHelper;
import ar.edu.unq.fitoscanner.model.Image;
import ar.edu.unq.fitoscanner.model.Sample;

public class RecordsActivity extends Activity{
	public final static String TAG = "RecordsActivity";
	ImageDataSource imageDataSource;
	SamplesDataSource samplesDataSource;
	ConfigurationDataSource configurationDataSource;
//	int samplePositionSelected = -1;
	private List<Sample> samples = new ArrayList<Sample>();
	private final Context context = this;
	private Typeface font;
	private Boolean getSent;
	private Boolean getResolved;
	private Boolean getValid;
	private Spinner spinner1;
	private TextView txtDateLabel;
	private TextView txtSampleLabel;
	private TextView txtLatAndLondLabel;
	private TextView txtCityLabel;
	private TextView txtStateLabel;
	private TextView txtCountryLabel;
	private TextView txtDate;
	private TextView txtSample;
	private TextView txtLatAndLond;
	private TextView txtCity;
	private TextView txtState;
	private TextView txtCountry;
	private List<Image> currentSampleImages;
	private Sample currentSample;
	private HashMap<String, Sample> samplesMap;
	private ListView listview;
	private CustomImageListViewAdapter customAdapter;
	private Bitmap imageSelected;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
        imageDataSource = new ImageDataSource(this);
        samplesDataSource = new SamplesDataSource(this);
        configurationDataSource = new ConfigurationDataSource(this);
        this.getSent =getIntent().getBooleanExtra("getSent", false);
        this.getResolved = getIntent().getBooleanExtra("getResolved", false);
        this.getValid = getIntent().getBooleanExtra("getValid", true);
        currentSampleImages = new ArrayList<Image>();
        samplesMap = new HashMap<String, Sample>();
        
    }
	
	private void generateSamplesView(){
		setContentView(R.layout.records_layout);
        samples = this.getSamples();
        
        //check that we really have them.
        
        Log.d(TAG, " showing samples obtained");
        for (Sample s : samples) {
			Log.d(TAG, "sample => "+s.toString());
		}
        
        //Spinner seleccionador de muestras
        setSpinnerSelector();
        initiateTextFields();
        
        listview = (ListView) findViewById(R.id.savedSampleImagesList);
        
        //Anulamos elementos de muestra al inicio del activity
        anulateAllTextViews();
         
          
         
        Log.i(TAG, "Adapter for samples set...");
        setDeleteSampleButton();
        setSendSampleButton();
         
         
         
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, final View view,
               int position, long id) {
        	   Image i = (Image)listview.getAdapter().getItem(position);
        	   
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
		txtDateLabel = (TextView) findViewById(R.id.savedSample_originDateLabel);
        txtSampleLabel = (TextView) findViewById(R.id.savedSample_sampleNameLabel);
        txtLatAndLondLabel = (TextView) findViewById(R.id.savedSample_latAndLonLabel);
        txtCityLabel = (TextView) findViewById(R.id.savedSample_cityLabel);
        txtStateLabel = (TextView) findViewById(R.id.savedSample_stateLabel);
        txtCountryLabel = (TextView) findViewById(R.id.savedSample_countryLabel);
        
        txtDate = (TextView) findViewById(R.id.savedSample_originDate);
        txtSample = (TextView) findViewById(R.id.savedSample_sampleName);
        txtLatAndLond = (TextView) findViewById(R.id.savedSample_latAndLon);
        txtCity = (TextView) findViewById(R.id.savedSample_city);
        txtState = (TextView) findViewById(R.id.savedSample_state);
        txtCountry = (TextView) findViewById(R.id.savedSample_country);
	}
	
	
	private void setTextFieldsVisibility(Integer v){
		txtDateLabel.setVisibility(v);
        txtSampleLabel.setVisibility(v);
        txtLatAndLondLabel.setVisibility(v);
        txtCityLabel.setVisibility(v);
        txtStateLabel.setVisibility(v);
        txtCountryLabel.setVisibility(v);
        
        txtDate.setVisibility(v);
        txtSample.setVisibility(v);
        txtLatAndLond.setVisibility(v);
        txtCity.setVisibility(v);
        txtState.setVisibility(v);
        txtCountry.setVisibility(v);
	}
	
	private void anulateAllTextViews(){
		setTextFieldsVisibility(View.GONE);
	}
	
	private void configureTextFields(){
        setTextFieldsVisibility(View.VISIBLE);
        
        txtDate.setText(currentSample.getOriginDate());
        txtSample.setText(currentSample.getSampleName());
        txtLatAndLond.setText(currentSample.getLocationData().getLatitude() + " / " 
        + currentSample.getLocationData().getLongitude());
        txtCity.setText(currentSample.getLocationData().getCity());
        txtState.setText(currentSample.getLocationData().getState());
        txtCountry.setText(currentSample.getLocationData().getCountry());
	}
	
	public void setSpinnerSelector(){
		spinner1 = (Spinner) findViewById(R.id.spinner1);
        List<String> list = new ArrayList<String>();
        
        //El spinner muestra titulo + fecha de muestra, donde dicho mismo string sera usado como key en el mapa
        //de string /muestra
        for (Sample s : samples) {
			list.add(s.getSampleName() + "  "+s.getOriginDate());
			samplesMap.put(s.getSampleName() + "  "+s.getOriginDate(), s);
		}
        
        if(samples.isEmpty()){
        	View buttons = (View) findViewById(R.id.sampleActionButtons_layout);
        	View spinner = (View) findViewById(R.id.spinner1);
        	spinner.setVisibility(View.GONE);
        	buttons.setVisibility(View.GONE);
        	TextView spinnerText = (TextView) findViewById(R.id.selectSampleText);
        	spinnerText.setText(" - No hay resultados - ");
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
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				//Seleccionar muestra del mapa 
				currentSample = samplesMap.get(parent.getItemAtPosition(position).toString());
				//Seleccionar imagenes
				currentSampleImages = currentSample.getImages();
				
				//Actualizar datos de muestra seleccionada
				configureTextFields();
				//Generar listView para imagenes de muestra
				customAdapter = new CustomImageListViewAdapter(
						getApplicationContext(),
						R.layout.samplepreview_fragment, currentSampleImages);
		        listview.setAdapter(customAdapter);
				
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
	            	 if(currentSample == null){
	            		 Toast.makeText(getApplicationContext(),"Seleccione una muestra primero",Toast.LENGTH_SHORT).show();
	            	 }else{
	            		 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
	 	         				context);
	 	          
	 	         			// set title
	 	         			String title = "¿Eliminar muestra "+"'"+currentSample.getSampleName()+ " ?";
	 	          
	 	         			//prepare dialog
	 	         			
	 	         			prepareDialog(alertDialogBuilder,
	 	         					title,
	 	         					"Elija una opción ", 
	 	         					true, 
	 	         					"Aceptar", new DialogInterface.OnClickListener() {
		 	         					public void onClick(DialogInterface dialog,int id) {
		 	         						deleteSample(currentSample);
		 	         						generateSamplesView();
		 	         						
		 	         					}
		 	         				  }, 
		 	         				"Cancelar", new DialogInterface.OnClickListener() {
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
	
	private void setSendSampleButton(){
		Button sendButton = (Button) findViewById(R.id.button_send);
		sendButton.setTypeface(font);
		if(!getSent || !getValid){
			sendButton.setOnClickListener(
			         new View.OnClickListener() {
			             @Override
			             public void onClick(View v) {
			            	 if(currentSample==null){
			            		 Toast.makeText(getApplicationContext(),"Seleccione una muestra primero",Toast.LENGTH_SHORT).show();
			            	 }else{
			            		 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
			 	         				context);
			            		 
			            		 String title = "¿Enviar muestra "+
					 	         			"'"+currentSample.getSampleName()+ " al servidor para determinar tratamiento?";
			            		 
			            		   prepareDialog(alertDialogBuilder, title, 
			 	         			"Elija una opción ", 
			 	         			false,
			 	         			"Aceptar", new DialogInterface.OnClickListener() {
		 	         					public void onClick(DialogInterface dialog,int id) {
		 	         						sendSample(currentSample);
		 	         						generateSamplesView();
		 	         						
		 	         					}
		 	         				  }, 
		 	         				"Cancelar",new DialogInterface.OnClickListener() {
			 	         					public void onClick(DialogInterface dialog,int id) {
			 	         						dialog.cancel();
			 	         					}
			 	         				});
			 	          
			 	         				AlertDialog alertDialog = alertDialogBuilder.create();
			 	         				alertDialog.show();
			            	 }	            		         			            
			               }              
			         }
			     );
		}
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		generateSamplesView();
	}
	
	/**
	 * Guarda la muestra que fue generada al tomar la primera foto
	 */
	public List<Sample> getSamples(){
		samplesDataSource.open();
    	try
    	{
    		if(getSent){
    			if(getValid){
    				if(getResolved){
    					return samplesDataSource.getSamplesSentResolved();
    				}else{
    					return samplesDataSource.getSamplesSentUnresolved();
    				}
    			}else{
    				return samplesDataSource.getSamplesSentInvalid();
    			}
    		}else{
    			return samplesDataSource.getSamplesUnsent();
    		}
    		
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
	
	
	public String getUrl(){
		configurationDataSource.open();
		try{
			return configurationDataSource.getConfigurationById(1).getIp();
		}
		finally{
        	configurationDataSource.close();
        }
	}
	
	public void saveSample(Sample sample) {
		samplesDataSource.open();
		try {

			samplesDataSource.saveSample(sample);

		} finally {
			samplesDataSource.close();
		}
	}
	
	
	public void sendSample(final Sample sample){
		new SendSampleTask().execute("");
		onBackPressed();
	}
	
	private class SendSampleTask extends AsyncTask<String, Void, HttpResponse> {
		Sample sample = currentSample;
	    @Override
	    protected HttpResponse doInBackground(String... params) {
	        final HttpParams httpParameters = new BasicHttpParams();
			HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
			HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
			
			AndroidHttpClient httpclient = AndroidHttpClient.newInstance("Android");
    		HttpPost httppost = null;
    		
    		String detailUrl ="";
        	try {
        		Sample sample = currentSample;
        		//La cantidad de imagenes por dos ( imagen =base 64 + titulo) + 6 campos fijos
    			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(sample.getImages().size()*2+6 );
    			int i = 0;
    			for (Image img : sample.getImages()) {
    				i++;
    				nameValuePairs.add(new BasicNameValuePair("sample_image_"+i+"_title", img.getTitle()));
    				nameValuePairs.add(new BasicNameValuePair("sample_image_"+i+"_base64", img.getBase64()));
    			}
    			//date, name, lat, lon, isAnon, imei
    		       nameValuePairs.add(new BasicNameValuePair("sample_name", sample.getSampleName()));
    		       
    		       SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
    		       timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    		       String time = timeFormat.format(new Date());
    		       TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
    		       String lon  = "No data"; 
    		       String lat = "No data";
    		       if(sample.getLocationData() != null){
    		    	   lon = sample.getLocationData().getLongitude();
    		       }
    		       if(sample.getLocationData() != null){
    		    	   lat = sample.getLocationData().getLatitude();
    		       }
    		       
    		       nameValuePairs.add(new BasicNameValuePair("date", time));
    		       nameValuePairs.add(new BasicNameValuePair("imei", mngr.getDeviceId()));
    		       nameValuePairs.add(new BasicNameValuePair("isAnon", Boolean.toString(!LoginActivity.logged)));
    		       nameValuePairs.add(new BasicNameValuePair("lon", lon));
    		       nameValuePairs.add(new BasicNameValuePair("lat",lat));
    		       nameValuePairs.add(new BasicNameValuePair("images_hash", sample.getHash()));
    		       String url; 
    		       try{
    		    	   url = getUrl();
    		       }catch(NullPointerException ne){
    		    	   Log.d("Records Activity", "WARNING: No configuration present, using default server address");
    		    	   url = URLHelper.SERVER_ADDRESS;
    		       }
    		       detailUrl="/sample/save/"+i;
    		       httppost = new HttpPost(url+detailUrl);
    				httppost.setHeader("Content-Type",
    		                "application/x-www-form-urlencoded;charset=UTF-8");
    		       
    		       httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    		       
    		   	   return httpclient.execute(httppost);
    			  
    		}catch (HttpHostConnectException e) {
    			Log.d("Records Activity", "WARNING: Server address from databse unreacheable, using default address");
    			try {
    				httppost = new HttpPost(URLHelper.SERVER_ADDRESS+detailUrl);
    				return httpclient.execute(httppost);
    			} catch (Exception ex) {
    				Toast.makeText(context,"Error al enviar la muestra, "
	    					+ "verifique su conexión a internet",Toast.LENGTH_SHORT).show();
    				ex.printStackTrace();
    			}
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    			Toast.makeText(context,"Error al enviar la muestra, "
    					+ "verifique su conexión a internet",Toast.LENGTH_SHORT).show();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}catch (Exception e) {
    			e.printStackTrace();
    			Toast.makeText(context,"Error desconocido al enviar la muestra... "
    					+ "",Toast.LENGTH_SHORT).show();
    		}finally{
    			httpclient.close();
    		}
			return null;
	    }

	    @Override
	    protected void onPostExecute(HttpResponse result) {
	        //Do something with result
	        if (result != null){
	        	Toast.makeText(context,"Muestra enviada al servidor, "
						+ "el servidor intentará responder lo más pronto posible",Toast.LENGTH_LONG).show();
	        	 sample.setSent(true);
	        	 
	        	 //La muestra pudo no haber sido resulta o invalida, permitir reenvio
	        	 sample.setValid(true);
	        	 sample.setMinutesFromLastRequest(0);
	        	 sample.setRequestTreatmentIntents(0);
  			     saveSample(sample);
	        }else{
	        	Toast.makeText(context,"Error al enviar muestra, "
						+ "intente reenviarla",Toast.LENGTH_LONG).show();
	        }
	    }
	}
}
