package ar.edu.unq.fitoscanner.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.activities.LoginActivity;
import ar.edu.unq.fitoscanner.activities.RecordsMenuActivity;
import ar.edu.unq.fitoscanner.datasources.ConfigurationDataSource;
import ar.edu.unq.fitoscanner.datasources.ImageDataSource;
import ar.edu.unq.fitoscanner.datasources.SamplesDataSource;
import ar.edu.unq.fitoscanner.datasources.TreatmentDataSource;
import ar.edu.unq.fitoscanner.datasources.TreatmentResolutionDataSource;
import ar.edu.unq.fitoscanner.helpers.Base64Helper;
import ar.edu.unq.fitoscanner.helpers.URLHelper;
import ar.edu.unq.fitoscanner.model.Image;
import ar.edu.unq.fitoscanner.model.Sample;
import ar.edu.unq.fitoscanner.model.Treatment;
import ar.edu.unq.fitoscanner.model.TreatmentResolution;

public class GetTreatmentService extends Service {
	public final static String TAG = "GetTreatmentService";
	private static Timer timer = new Timer();
	private Context ctx;
	private SamplesDataSource samplesDataSource;
	private ConfigurationDataSource configurationDataSource;
	private ImageDataSource imageDataSource;
	private TreatmentResolutionDataSource trds;
	private TreatmentDataSource tds;
	public Integer timeRepetition = 300000; // ms
	public String imei;
	public Integer notifyId;

	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		ctx = this;
		Log.d(TAG, "Get Tratment service started...");
		initiateVars(ctx);
		startService();
		
	}
	
	private void initiateVars(Context ctx){
		configurationDataSource = new ConfigurationDataSource(ctx);
		samplesDataSource = new SamplesDataSource(ctx);
		imageDataSource = new ImageDataSource(ctx);
		tds = new TreatmentDataSource(ctx);
		trds = new TreatmentResolutionDataSource(ctx);
		notifyId = 100;
		setImei();
	}
	
	private void setImei(){
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		imei= mngr.getDeviceId();
	}

	private void startService() {
		// cada 5 min, verificar muestras a consultar resolucion
		timer.scheduleAtFixedRate(new requestTreatmentTask(), 0, timeRepetition);
	}

	private class requestTreatmentTask extends TimerTask {
		private List<Sample> samplesToCheck;
		private int x = 0;

		public void run() {
			// Obtener muestras a consultar
			samplesToCheck = getSamplesToSolve();
			Log.d("Local Service", "samples to check = "+samplesToCheck.size());
			for (Sample sample : samplesToCheck) {
				if(sample.getMinutesFromLastRequest() > Math.pow(2, sample.getRequestTreatmentIntents())
				&& sample.getRequestTreatmentIntents() <= 12){
					resolveServerResponse(sample);
					//Estamos haciendo request, minutos pasados es cero
					sample.setMinutesFromLastRequest(0);
					//Incrementar numero de intentos
					sample.setRequestTreatmentIntents(sample.getRequestTreatmentIntents()+1);
					
					if(sample.getTreatmentResolution() == null){
					//	fail;
					}
				}else{
					//Incrementar tiempo desde ultimo en minutos   ms -> segundos -> minutos
					sample.setMinutesFromLastRequest(sample.getMinutesFromLastRequest() +((timeRepetition/1000)/60));
				}
				//actualizar sample
				saveSample(sample);
			}
			
		}
		
		private void resolveServerResponse(Sample sample){
			try {
				String serverResponse = getJsonResponse(sample.getHash());
				Log.d(TAG, "Json obtained = " + serverResponse);
				final NotificationManager mgr= (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
				JSONObject jsonResponse = new JSONObject(serverResponse);
				Boolean valid = jsonResponse.getBoolean("valid");
				String serverMessage = jsonResponse.getString("message");
				Notification userNotification =null;;
				
				if(!valid){
					//Muestra invalida para el servidor, o bien el server elimino el usuario o la muestra, reenviar
					invalidateSample(sample,userNotification,mgr);
					
				}else{
					Boolean resolved = jsonResponse.getBoolean("resolved");
					Log.d(TAG, "Response is valid= " + valid
							+ " , Sample is resolved= " + resolved
							+ " , Server message is: " + serverMessage);
					if (!resolved) {
						//Muestra sin resolver aun, continuar.
//						notifyToUser(mgr, R.drawable.flechitader, "Resolucion de muestra", 
//								"Muestra "+sample.getSampleName(), 
//								"Muestra "+sample.getSampleName()+ " sin respuesta aún");
						sample.setSent(true);
						sample.setResolved(false);
						
					}else{
												//tratamiento obtenido, resolver
						JSONObject specie =jsonResponse.getJSONObject("specie");
						JSONArray specieImages = specie.getJSONArray("images");
						
						//Descargar imagenes y construir string con ids de base local.
						
						String serverIdSpecieImages = parseToStringPropertyInArray(specieImages, "id", "-");
						
						String idSpecieImages = downloadAndMakeImageIds("Imagen_Especie",serverIdSpecieImages);
												
						String specieName = specie.getString("name");
						String specieScientificName = specie.getString("scientific_name");
						String specieDescription = specie.getString("description");
						
						
						JSONArray treatments = jsonResponse.getJSONArray("treatments");
						
						//procesamos la lista de tratamientos
						
						List<Treatment> treatmentsParsed = processTreatments(treatments);
						
						
//						construimos la resolucion
						TreatmentResolution tr = new TreatmentResolution();
						tr.setSpecieName(specieName);
						tr.setSpecieDescription(specieDescription);
						tr.setSpecieScientificName(specieScientificName);
						tr.setMessage(serverMessage);
						tr.setValid(valid);
						tr.setResolved(resolved);
						tr.setIdSpecieImages(idSpecieImages);
						//guardamos los tratamientos y generamos el string con ids de base
						tr.setIdTreatments(saveAndGetIdTreatments(treatmentsParsed));
						//guardamos la resolucion y se lo seteamos al mismo objeto. el id ser usado al actualizar el sample
						tr.setId(saveTreatmentResolution(tr));
						notifyToUser(mgr, R.drawable.flechitader, "Resolucion de muestra", 
								"Muestra resuelta", 
								"Tratamiento obtenido para "+sample.getSampleName());
																	
						
						Log.d(TAG, "Tratamiento obtenido para la muestra "+sample.getSampleName()+"!! , To string de T"
								+ "reatmentResolution ==> " +tr.toString());
						sample.setSent(true);
						sample.setResolved(true);
						
						//Guardar el tratamiento
						sample.setTreatmentResolution(tr);
						
					}
				}
				
				notifyId = notifyId + 1;
				
			} catch (JSONException e) {
				e.printStackTrace();
				//Ante un error obteniendo la muestra, reseteamos los flags y una posible resolucion fallida
				Log.e(TAG, "Error resolving sample "+sample.getSampleName());
				sample.setResolved(false);
				sample.setSent(true);
				sample.setTreatmentResolution(null);
			}
		}
		
		private void invalidateSample(Sample sample, Notification userNotification,NotificationManager mgr){
					
			notifyToUser(mgr, R.drawable.flechitader, "Resolucion de muestra",
					"Error resolviendo muestra", 
					"Por favor reenviar muestra "+sample.getSampleName());
			sample.setSent(true);
			sample.setValid(false);
		}

		private String getJsonResponse(String sampleHash) {
			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());
			HttpPost httppost = new HttpPost(URLHelper.SERVER_ADDRESS+"/treatment/requestTreatment");
			// Depends on your web service
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("imei", imei));
			nameValuePairs.add(new BasicNameValuePair("images_hash", sampleHash));

			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");

			InputStream inputStream = null;
			String result = "";
			try {
				
				Log.d(TAG,"Attempting to get treatment for sample: "+sampleHash);
				
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();
				// json is UTF-8 by default
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
			} catch (Exception e) {
				// Oops
			} finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (Exception squish) {
				}
			}
			return result;
		}
	}


	public String parseToStringPropertyInArray(JSONArray JArray,String property,String separator) throws JSONException {
		JSONObject element;
		String parsedResult ="";
		for (int i = 0; i < JArray.length(); i++) {
			element = JArray.getJSONObject(i);
	    	if(i==0){
	    		parsedResult =  element.get(property).toString();
	    	}else{
	    		parsedResult = parsedResult + separator + element.get(property).toString();
	    	}
	    }
		return parsedResult;
	}
	
	public String saveAndGetIdTreatments(List<Treatment> treatmentsParsed) {
		String result = "";
		Long currentId;
		for (Treatment treatment : treatmentsParsed) {
			currentId = saveTreatment(treatment);
			if(result == null || result == ""){
				result =  result + currentId;
			}else{
				result = result + "-" + currentId;
			}
		}
		return result;
	}

	public synchronized String downloadAndMakeImageIds(String prefix,String serverIdSpecieImages) {
		String result = "";
		byte[] bytes = null;
		Bitmap currentPic = null;
		if (serverIdSpecieImages.contains("-")) {
			//Existen al menos 2
			
			for (String currentId: serverIdSpecieImages.split("-")){
				//Obtener imagen actual
				Log.d(TAG, "Getting image with id = "+currentId+ " from serve");
				
				try {
					if(result == null || result == ""){
						result =  result + getAndSaveImage(prefix,currentId, bytes, currentPic);
					}else{
						result = result + "-" + getAndSaveImage(prefix,currentId, bytes, currentPic);
					}
	                
	                
	            } catch (Exception e) {
	               // log error
	            	e.printStackTrace();
	            	Log.e(TAG, "Error getting image "+ currentId + " from server");
	            }
		    }
		} else {
			// o solo es una imagen o es nulo
			if(serverIdSpecieImages != null ||  serverIdSpecieImages != ""){
				try {
					Log.d(TAG, "Getting image with id = "+serverIdSpecieImages+ " from serve");
					result = getAndSaveImage(prefix,serverIdSpecieImages, bytes, currentPic).toString();
				} catch (IOException e) {
					e.printStackTrace();
	            	Log.e(TAG, "Error getting image "+ serverIdSpecieImages+" from server");
				}
			}
		}
		
		return result;
	}

	public void notifyToUser(NotificationManager mgr, int icon, String tickerText,  
			String title, String description) {
		Notification note = new Notification(icon, tickerText,System.currentTimeMillis());
		PendingIntent i = PendingIntent.getActivity(ctx, 0,new Intent(ctx, RecordsMenuActivity.class), 0);

		note.setLatestEventInfo(ctx,title,description, i);
		mgr.notify(notifyId, note);
	}


	public List<Treatment> processTreatments(JSONArray treatments) throws JSONException {
		List<Treatment>treatmentsParsed= new ArrayList<Treatment>();
		for (int i = 0; i < treatments.length(); i++) {
		    JSONObject element = treatments.getJSONObject(i);
		    Treatment newTreatment = new Treatment();
		    JSONArray treatmentImages = element.getJSONArray("images");
		    String quant = element.getString("quantity");
			String typeQuan = element.getString("typeQuantity");
			String freq =element.getString("frequency");
			String typeFreq =element.getString("typeFrequency");
			String useEx =element.getString("useExplanation");
			String extra1 =element.getString("extraLink1");
			String extra2 =element.getString("extraLink2");
			String extra3 =element.getString("extraLink3");
		    String serverTreatmentImagesIds = parseToStringPropertyInArray(treatmentImages, "id", "-");
		    
		    String treatmentImagesIds = downloadAndMakeImageIds("Imagen_tratamiento_",serverTreatmentImagesIds);
		    newTreatment.setName(element.getString("name"));
		    newTreatment.setDescription(element.getString("description"));
		    newTreatment.setUnit(quant);
		    newTreatment.setUnitType(typeQuan);
		    newTreatment.setFrequency(freq);
		    newTreatment.setFrequencyType(typeFreq);
		    newTreatment.setUseExplanation(useEx);
		    newTreatment.setExtraLink1(extra1);
		    newTreatment.setExtraLink2(extra1);
		    newTreatment.setExtraLink3(extra1);
		    newTreatment.setIdImages(treatmentImagesIds);
		    treatmentsParsed.add(newTreatment);
		}
		return treatmentsParsed;
	}

	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
	}
	
	private Long getAndSaveImage(String prefix,String currentId, byte[]bytes, Bitmap currentPic) throws IOException{
		Log.d(TAG, "Attempting to get and save image from server with id "+currentId);
        InputStream in = new URL(URLHelper.SERVER_ADDRESS+"/image/getImageThumbnail/" + currentId).openStream();
        bytes = IOUtils.toByteArray(in);
        currentPic = BitmapFactory.decodeByteArray(bytes, 0,
				bytes.length);
        Image newImage = new Image(null, null,null,null, 
        		prefix+currentId, new Date().toLocaleString(),
				Base64Helper.encodeTobase64(currentPic));
        bytes = null;
        currentPic = null;
        return saveImage(newImage);
	}

	public List<Sample> getSamplesToSolve() {
		samplesDataSource.open();
		try {
			//preguntar por muestras enviadas
			 List <Sample> result = samplesDataSource.getSamplesSentUnresolved();
			 return result;
		} finally {
			samplesDataSource.close();
		}
	}

	public void deleteSample(Sample sample) {
		samplesDataSource.open();
		try {
			samplesDataSource.deleteSample(sample);
		} finally {
			samplesDataSource.close();
		}
	}

	public void saveSample(Sample sample) {
		samplesDataSource.open();
		try {

			samplesDataSource.fullSaveSample(sample);

		} finally {
			samplesDataSource.close();
		}
	}
	
	public Long saveImage(Image img){
		imageDataSource.open();
		try {

			return imageDataSource.doSaveImage(img);

		} finally {
			imageDataSource.close();
		}
	}
	
	public Long saveTreatmentResolution(TreatmentResolution tr){
		trds.open();
		try {
			return trds.doSaveTreatmentResolution(tr);
		} finally {
			trds.close();
		}
	}
	
	public Long saveTreatment(Treatment t){
		tds.open();
		try {
			return tds.doSaveTreatment(t);
		} finally {
			tds.close();
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
}