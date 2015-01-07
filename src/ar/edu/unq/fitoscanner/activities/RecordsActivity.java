package ar.edu.unq.fitoscanner.activities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
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
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
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
	int samplePositionSelected = -1;
	private ArrayList<Sample> samples = new ArrayList<Sample>();
	private final Context context = this;
	private Typeface font;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
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
	
	
	public void sendSample(Sample sample){
		HttpParams httpParameters = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
		HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		
		HttpPost httppost = new HttpPost(URLHelper.SERVER_ADDRESS+"/sample/save/3");
		httppost.setHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=UTF-8");
		
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(sample.getImages().size()*2+6 );
			int i = 1;
			for (Image img : sample.getImages()) {
				nameValuePairs.add(new BasicNameValuePair("sample_image_"+i+"_title", img.getTitle()));
				nameValuePairs.add(new BasicNameValuePair("sample_image_"+i+"_base64", img.getBase64()));
				i++;
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
		       
		       httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		   	   httpclient.execute(httppost);
				Toast.makeText(getBaseContext(),"Muestra enviada al servidor, "
						+ "el servidor intentará responder lo más pronto posible",Toast.LENGTH_LONG).show();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(),"Error al enviar la muestra, "
					+ "verifique su conexión a internet",Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			Toast.makeText(getBaseContext(),"Error al enviar la muestra, "
					+ "verifique su conexión a internet",Toast.LENGTH_SHORT).show();
		}
	}
}
