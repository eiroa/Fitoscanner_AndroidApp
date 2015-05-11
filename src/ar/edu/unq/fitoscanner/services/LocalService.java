package ar.edu.unq.fitoscanner.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.activities.LoginActivity;
import ar.edu.unq.fitoscanner.activities.RecordsMenuActivity;

public class LocalService extends Service{
	public final static String TAG = "GetTreatmentService";
    private static Timer timer = new Timer(); 
    private Context ctx;
    private SamplesDataSource samplesDataSource;
    public Integer timeRepetition = 300000 //ms

    public IBinder onBind(Intent arg0) 
    {
          return null;
    }

    public void onCreate() 
    {
          super.onCreate();
          ctx = this; 
          startService();
    }

    private void startService()
    {   
    // cada 5 min, verificar muestras a consultar resolucion
        timer.scheduleAtFixedRate(new requestTreatmentTask(), 0, timeRepetition);
    }

    private class requestTreatmentTask extends TimerTask
    { 
        private List<Sample> samplesToCheck;
    	private int x = 0;
        public void run() 
        {   
            //Obtener muestras a consultar
            samplesToCheck = getSamples();
            for (Sample sample : samplesToCheck) {
			    switch (sample.getRequestTreatmentIntents()) {
                case 0:
                break;
                case 1:
                break;
                case 2:
                break;
                case 3:
                break;
                case 4:
                break;
                case 5:
                break;
                case 6:
                break;
                case 7:
                break;
                case 8:
                break;
                case 9:
                break;
                case 10:
                break;
                case 11:
                break;
                case 12:
                break;
                }
		    }
            Log.d("Local Service", "var increased = "+x);
            x++;
            
            String serverResponse = getJson();
            Log.d(TAG, "Json obtained = "+serverResponse);
//          toastHandler.sendEmptyMessage(0);
            JSONObject jsonResponse = null;
            try {
				jsonResponse = new JSONObject(serverResponse);
				Boolean valid = jsonResponse.getBoolean("valid");
				Boolean resolved = jsonResponse.getBoolean("resolved");
				String serverMessage = jsonResponse.getString("message");
				
				Log.d(TAG, "Response is valid= "+valid +  " , Sample is resolved= "+resolved + 
						" , Server message is: "+serverMessage);
				
				if(!resolved){
					final NotificationManager mgr=
				            (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
				        Notification note=new Notification(R.drawable.flechitader,
				                                                        "Resolucion de muestra",
				                                                        System.currentTimeMillis());
				         
				        // This pending intent will open after notification click
				        PendingIntent i=PendingIntent.getActivity(ctx, 0,
				                                                new Intent(ctx, RecordsMenuActivity.class),
				                                                0);
				         
				        note.setLatestEventInfo(ctx, "La muestra aun no ha sido resuelta",
				                                "El servidor aun no ha resuelto la muestra", i);
				         
				        //After uncomment this line you will see number of notification arrived
				        //note.number=2;
				        mgr.notify(100, note);
				}
//              To get a specific string
//
//              String aJsonString = jObject.getString("STRINGNAME");
//              To get a specific boolean
//
//              boolean aJsonBoolean = jObject.getBoolean("BOOLEANNAME");
//              To get a specific integer
//
//              int aJsonInteger = jObject.getInt("INTEGERNAME");
//              To get a specific long
//
//              long aJsonLong = jObject.getBoolean("LONGNAME");
//              To get a specific double
//
//              double aJsonDouble = jObject.getDouble("DOUBLENAME");
//              To get a specific JSONArray:
//
//              JSONArray jArray = jObject.getJSONArray("ARRAYNAME");
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
        }
        
        private String getJson(){
        	DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httppost = new HttpPost("http://192.168.137.213:8081/tip_eiroa_mauro_server_backend/rest/treatment/requestTreatment");
            // Depends on your web service
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            
            nameValuePairs.add(new BasicNameValuePair("imei", "356792041012974"));
			nameValuePairs.add(new BasicNameValuePair("images_hash", "3"));
			
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			httppost.setHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            
            InputStream inputStream = null;
            String result = "";
            try {
                HttpResponse response = httpclient.execute(httppost);           
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            } catch (Exception e) { 
                // Oops
            }
            finally {
                try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
            }
            return result;
        }
    }    

    public void onDestroy() 
    {
          super.onDestroy();
          Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }

    public List<Sample> getSamples(){
		samplesDataSource.open();
    	try
    	{
    		return samplesDataSource.getSamples(getSent);
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
}