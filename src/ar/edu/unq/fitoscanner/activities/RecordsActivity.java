package ar.edu.unq.fitoscanner.activities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.datasources.ConfigurationDataSource;
import ar.edu.unq.fitoscanner.datasources.ImageDataSource;
import ar.edu.unq.fitoscanner.datasources.SamplesDataSource;
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
	int samplePositionSelected = -1;
	private List<Sample> samples = new ArrayList<Sample>();
	private final Context context = this;
	private Typeface font;
	private Boolean getSent;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
        imageDataSource = new ImageDataSource(this);
        samplesDataSource = new SamplesDataSource(this);
        configurationDataSource = new ConfigurationDataSource(this);
        this.getSent =getIntent().getBooleanExtra("getSent", true);
        
        
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
         setSendSampleButton();
         listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, final View view,
               int position, long id) {
        	   //El tostadoooorrrrrrrrrr
        	   Sample s = (Sample)listview.getAdapter().getItem(position);
        	   Toast.makeText(getApplicationContext(),"Muestra "+s.getSampleName()+ " de "+ s.getOriginDate() + 
        			   " seleccionada",Toast.LENGTH_SHORT).show();
        	   samplePositionSelected = position;           	        
             
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
	
	private void setSendSampleButton(){
		Button sendButton = (Button) findViewById(R.id.button_send);
		sendButton.setTypeface(font);
		if(!getSent){
			sendButton.setOnClickListener(
			         new View.OnClickListener() {
			             @Override
			             public void onClick(View v) {
			            	 if(samplePositionSelected<0 || samplePositionSelected > samples.size()){
			            		 Toast.makeText(getApplicationContext(),"Seleccione una muestra primero",Toast.LENGTH_SHORT).show();
			            	 }else{
			            		 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
			 	         				context);
			 	          
			 	         			alertDialogBuilder.setTitle("¿Enviar muestra "+
			 	         			"'"+samples.get(samplePositionSelected).getSampleName()+ "'  de fecha " +
			 	         			samples.get(samplePositionSelected).getOriginDate()+ " al servidor para determinar tratamiento?");
			 	          
			 	         			alertDialogBuilder
			 	         				.setMessage("Elija una opción ")
			 	         				.setCancelable(false)
			 	         				.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
			 	         					public void onClick(DialogInterface dialog,int id) {
			 	         						sendSample(samples.get(samplePositionSelected));
			 	         						generateSamplesView();
			 	         						
			 	         					}
			 	         				  })
			 	         				.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
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
    			return samplesDataSource.getSamplesSentUnresolved();
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
		Sample sample = samples.get(samplePositionSelected);
	    @Override
	    protected HttpResponse doInBackground(String... params) {
	        final HttpParams httpParameters = new BasicHttpParams();
			HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
			HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
			
			AndroidHttpClient httpclient = AndroidHttpClient.newInstance("Android");
    		HttpPost httppost = null;
    		
    		String detailUrl ="";
        	try {
        		Sample sample = samples.get(samplePositionSelected);
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
  			     saveSample(sample);
	        }else{
	        	Toast.makeText(context,"Error al enviar muestra, "
						+ "intente reenviarla",Toast.LENGTH_LONG).show();
	        }
	    }
	}
}
