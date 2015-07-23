package ar.edu.unq.fitoscanner.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.datasources.ImageDataSource;
import ar.edu.unq.fitoscanner.datasources.SamplesDataSource;
import ar.edu.unq.fitoscanner.helpers.Base64Helper;
import ar.edu.unq.fitoscanner.helpers.CameraPreview;
import ar.edu.unq.fitoscanner.helpers.CustomImageListViewAdapter;
import ar.edu.unq.fitoscanner.helpers.SecurityHelper;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;
import ar.edu.unq.fitoscanner.model.Image;
import ar.edu.unq.fitoscanner.model.LocationData;
import ar.edu.unq.fitoscanner.model.Sample;

import com.ipaulpro.afilechooser.utils.FileUtils;

public class CreateSampleActivity extends Activity {
	public final static String TAG = "CreateSampleActivity";
	private static final int REQUEST_CHOOSER = 1234;
	private static final int IMAGE_MAX_SIZE  = 2500;
	private File fileSelected;
	public Activity context = this;
	private  Button btnChooseImage;
	private ArrayList<Image> previews = new ArrayList<Image>();
	private SamplesDataSource samplesDataSource;
	private Bitmap imageSelected;
	private String pathSelected;
	private EditText sampleNameField;
	private boolean saving;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.previews_layout);
		setSamplesDataSource(new SamplesDataSource(this));
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		btnChooseImage = (Button) findViewById(R.id.button_takePicAgain);
		sampleNameField = (EditText) findViewById(R.id.preview_sampleNameField);
		setImagesList();
		setChooseImageButton();
		setSaveButton();
	}
	
	private void setChooseImageButton() {
		btnChooseImage.setTypeface(TypefacesHelper.getTypeface(this, "fonts/optien.ttf"));
		btnChooseImage.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				
				if(previews.size()==7){
					Toast.makeText(
							getApplicationContext(),
							"Atencion, la muestra no puede superar las 7 imagenes",
							Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(),
							"Seleccione una imagen", Toast.LENGTH_SHORT).show();
					Intent getContentIntent = FileUtils.createGetContentIntent();
					Intent intent = Intent.createChooser(getContentIntent,
							"Select a file");
					startActivityForResult(intent, REQUEST_CHOOSER);
				}
			}
		});
		
	}

	private void setImagesList() {
		final ListView listview = (ListView) findViewById(R.id.previewSamplesList);
		CustomImageListViewAdapter customAdapter = new CustomImageListViewAdapter(
				getApplicationContext(),
				R.layout.samplepreview_fragment, previews);

		listview.setAdapter(customAdapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,
					final View view, int position, long id) {
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
	
	@Override
	public void onBackPressed() {
		if(saving){
			super.onBackPressed();
		}else{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
	  				context);
	   
	  			String title = "Atención, está a punto de eliminar la muestra actual ";
	   
	  			prepareDialog(alertDialogBuilder,
	  					title,
	  					"Elija una opción ", 
	  					true, 
	  					"Aceptar", new DialogInterface.OnClickListener() {
	      					public void onClick(DialogInterface dialog,int id) {
	      						context.finish();
	      					}
	      				  }, 
	      				"Cancelar", new DialogInterface.OnClickListener() {
	      					public void onClick(DialogInterface dialog,int id) {
	      						dialog.cancel();
	      					}
	      				});
	  			
	  				AlertDialog alertDialog = alertDialogBuilder.create();
	  				alertDialog.show();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean doConvert = false;
		switch (requestCode) {
		case REQUEST_CHOOSER:
			if (resultCode == RESULT_OK) {

				final Uri uri = data.getData();
				pathSelected = FileUtils.getPath(this, uri);
				Log.d(TAG, "Path selected: "+pathSelected);
				if(pathSelected!= null){
					doConvert = true;
				}
				
			}else{
				if(resultCode == RESULT_CANCELED){
					//Nothing
				}
			}
			break;
		}
		Log.d(TAG, "doConvert: "+doConvert );
		if(doConvert){
			if(imageAlreadyProcessed(pathSelected)){
				Toast.makeText(getApplicationContext(),
						"La imagen ya fue agregada...", Toast.LENGTH_SHORT).show();
				return;
			}
			imageSelected = decodeFile(pathSelected);
			if(imageSelected == null){
				Toast.makeText(getApplicationContext(),
						"Archivo no válido como imagen...", Toast.LENGTH_SHORT).show();
			}else{
				Image newImage = new Image(null, null,null,null, "Picture "
						+ (previews.size() + 1), new Date().toLocaleString(),
						Base64Helper.encodeTobase64(imageSelected));
				newImage.setLocalPath(pathSelected);
				Log.d(TAG, "Image object created =>> "+newImage.toString());
				imageSelected.recycle();
				System.gc();
				previews.add(newImage);
				newImage = null;
				
				Log.i(TAG, "Recent photo loaded");
				
				Log.i(TAG, previews.toString());
			}
		}

	}
	
	private boolean imageAlreadyProcessed(String path) {
		for (Image img : previews) {
			if(img.getLocalPath().equals(path)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Reducimos la imagen en caso que sea demasiado grande, util en dispositivos con camaras de alta gama
	 * @param path
	 * @return
	 */
	private Bitmap decodeFile(String path){
	    Bitmap b = null;

	    //Decode image size
	    BitmapFactory.Options o = new BitmapFactory.Options();
	    o.inJustDecodeBounds = true;
	    b = BitmapFactory.decodeFile(path);
	    if (b==null){
	    	//Decode invalido, archivo no valido como imagen
	    	return b;
	    }
	    Log.d(TAG, "Width of image  selected: "+b.getWidth());
		Log.d(TAG, "Height of image selected: "+b.getHeight());
		
		
		if (android.os.Build.VERSION.SDK_INT>10)Log.d(TAG, "Bytes of image selected: "+b.getByteCount());
	    int scale = 1;
	    if (b.getHeight() > IMAGE_MAX_SIZE || b.getWidth() > IMAGE_MAX_SIZE) {
	    	Log.d(TAG, "Image size too big, reducing...");
	        scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE / 
	           (double) Math.max(b.getHeight(), b.getWidth())) / Math.log(0.5)));
	        
	      //Decode with inSampleSize
		    BitmapFactory.Options o2 = new BitmapFactory.Options();
		    o2.inSampleSize = scale;
		    FileInputStream fis = null;
			try {
				fis = new FileInputStream(new File(path));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		    b = BitmapFactory.decodeStream(fis, null, o2);
		    try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		    
		    Log.d(TAG, "Width of image processed : "+b.getWidth());
			Log.d(TAG, "Height of image processed: "+b.getHeight());
			if (android.os.Build.VERSION.SDK_INT>10)Log.d(TAG, "Bytes of image processed: "+b.getByteCount());
	    }
	    
	    

	    return b;
	}
	
	private void setSaveButton() {
		Button saveButton = (Button) findViewById(R.id.button_saveSample);
		saveButton.setTypeface(TypefacesHelper.getTypeface(this, "fonts/optien.ttf"));
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String sampleName = (sampleNameField.getText()).toString();
					if (previews.size() < 3) {
						Toast.makeText(
								getApplicationContext(),
								"Atencion, la muestra debe contener al menos 3 imagenes",
								Toast.LENGTH_SHORT).show();
					} else {
							if(sampleName.equals("")|| sampleName==null){
								Toast.makeText(
										getApplicationContext(),
										"Aplique un nombre a la muestra por favor",
										Toast.LENGTH_LONG).show();
							}else{
								processNewSample();
							}
					}
				}
			}
		);
	}
	/**
	 * Construye una nueva muestra
	 */
	private Sample makeNewSample() {
		EditText sampleNameField = (EditText) findViewById(R.id.preview_sampleNameField);
		String sampleName = (sampleNameField.getText()).toString();
		final Sample newSample = new Sample();
		newSample.setSampleName(sampleName);
		newSample.setOriginDate(new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss")
				.format(new Date()));
		newSample.setFieldName("testing field");
		newSample.setImages(previews);
		newSample.setSent(false);
		newSample.setResolved(false);
		newSample.setValid(true);
		newSample.setRequestTreatmentIntents(0);
		newSample.setMinutesFromLastRequest(0);
		String base64full = "";
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String hash = mngr.getDeviceId();
		
		// calcular hash de cada imagen, concatenerlos y luego obtener hash de los hashes concatenados
		// tener en cuenta que cada base 64 tiene un salto de linea al final
		for (Image img : newSample.getImages()) {
			base64full = img.getBase64();
			hash = hash+SecurityHelper.toSHA256(base64full);
		}
		Double randomValue = Math.random();
		Log.i(TAG, "setting final hash for sample => "+hash+randomValue.toString());
		newSample.setHash(SecurityHelper.toSHA256(hash+randomValue.toString()));
		
		newSample.setLocationData(new LocationData("", "", "-", "-", "-"));
		
		return newSample;
		
	
	}


	
	private void processNewSample(){
		new NewSampleTask().execute("");
		saving = true;
		onBackPressed();
	}
	
	private class NewSampleTask extends AsyncTask<String, Void,Void> {
		Sample sample = null;
		int size = 3;
		String name = "";
		SamplesDataSource sds = new SamplesDataSource(context);
		LocationData ldt;
	    @Override
	    protected Void doInBackground(String... params) {
	    	sample =makeNewSample();
	    	size = sample.getImages().size();
	    			name = sample.getSampleName();
			return null;
	    }
	    
	    @Override
	    protected void onPostExecute(Void result) {
	    	runOnUiThread(new Runnable() 
			{
			   public void run() 
			   {
				   sds.open();
				   sds.fullSaveSample(sample);
				   sds.close();
				   Toast.makeText(
							context,"Se ha guardado la muestra "+ name + 
							" con " + ""+ size + " imágenes",
							Toast.LENGTH_SHORT).show();
			   }
			}); 
	    	
	    	
	    }
	}

	public SamplesDataSource getSamplesDataSource() {
		return samplesDataSource;
	}

	public void setSamplesDataSource(SamplesDataSource samplesDataSource) {
		this.samplesDataSource = samplesDataSource;
	}
	
	
}
