package ar.edu.unq.fitoscanner.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.datasources.SamplesDataSource;
import ar.edu.unq.fitoscanner.helpers.ArrayHelper;
import ar.edu.unq.fitoscanner.helpers.Base64Helper;
import ar.edu.unq.fitoscanner.helpers.CustomImageListViewAdapter;
import ar.edu.unq.fitoscanner.helpers.SecurityHelper;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;
import ar.edu.unq.fitoscanner.model.Image;
import ar.edu.unq.fitoscanner.model.LocationData;
import ar.edu.unq.fitoscanner.model.Sample;

public class CreateSampleActivity extends Activity {
	public final static String TAG = "CreateSampleActivity";
	private static final int REQUEST_CHOOSER = 1234;
	private static final int IMAGE_MAX_SIZE  = 2500;
	private  Button btnChooseImage;
	private static ArrayList<Image> previews;
	private static ArrayList<String> paths = new ArrayList<String>();
	private static ArrayList<String> base64s = new ArrayList<String>();
	private SamplesDataSource samplesDataSource;
	public Activity context = this;
	private Bitmap imageSelected = null;
	private String pathSelected;
	private EditText sampleNameField;
	private boolean saving;
	private boolean creatingSample = false;
	private boolean imageAlreadyProcessed = false;
	private String newLocalPath;
	private String newBase64;
	private static CustomImageListViewAdapter customAdapter;
	private static ListView listview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.previews_layout);
		setSamplesDataSource(new SamplesDataSource(this));
		previews = new ArrayList<Image>();
		customAdapter = new CustomImageListViewAdapter(
				getApplicationContext(),
				R.layout.samplepreview_fragment, previews);
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
				Log.d(TAG, "Starting Chooser for an image, previews has "+previews.size()+ " images");
				if(previews.size()==7){
					Toast.makeText(
							getApplicationContext(),
							"Atencion, la muestra no puede superar las 7 imagenes",
							Toast.LENGTH_SHORT).show();
				}else{
					Log.d(TAG, "Starting file chooser, previews size is "+previews.size());
					Toast.makeText(getApplicationContext(),
							"Seleccione una imagen", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		             intent.setType("image/*");
		             intent.putExtra("return-data", true);
					startActivityForResult(intent, REQUEST_CHOOSER);
				}
			}
		});
		
	}

	private void setImagesList() {
		 listview = (ListView) findViewById(R.id.previewSamplesList);
		 customAdapter = new CustomImageListViewAdapter(
				getApplicationContext(),
				R.layout.samplepreview_fragment, previews);

		listview.setAdapter(customAdapter);
		customAdapter.notifyDataSetChanged();
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,
					final View view, final int position, long id) {
				final Image i = (Image)listview.getAdapter().getItem(position);
	        	   
	        	   imageSelected =Base64Helper.decodeScaledBase64(i.getBase64(),
	       	    		getWindowManager().getDefaultDisplay().getWidth(),
	       	    		getWindowManager().getDefaultDisplay().getWidth());
	        	    final Dialog builder = new Dialog(context);
	        	    builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        	    builder.setContentView(R.layout.image_popup);
	         	    ImageView imgPopup = (ImageView) builder.findViewById(R.id.image_popup);
	         	   Button delete = (Button)builder.findViewById(R.id.buttonDeleteImageSample);
	         	    Display display = getWindowManager().getDefaultDisplay();
	         	    Point size = new Point();
					if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2){
						imgPopup.getLayoutParams().height = (int) (display.getHeight()* 0.75);
						imgPopup.getLayoutParams().width = (int) (display.getWidth() * 0.90);
					}else{
						display.getSize(size);
						imgPopup.getLayoutParams().height = (int) (size.y * 0.75);
						imgPopup.getLayoutParams().width = (int) (size.x * 0.90);
					}
					
					
					imgPopup.setImageBitmap(imageSelected);
					builder.show();
					delete.setOnClickListener(new View.OnClickListener() {
			        	
			        	public void onClick(View arg0) {
			        		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									context);

							// set title
							String title = "¿Eliminar "+i.getTitle()+ " ?";


							prepareDialog(alertDialogBuilder, title, "Elija una opción ", true,
									"Aceptar", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											previews.remove(i);
											ArrayHelper ar = new ArrayHelper(context);
											paths = ar.getArray("paths");
											base64s = ar.getArray("base64s");
											paths.remove(position);
											base64s.remove(position);
											ar.saveArray("paths", paths);
											ar.saveArray("base64s", base64s);
											refreshImageList();
										}
									}, "Cancelar", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});

							// create alert dialog
							AlertDialog alertDialog = alertDialogBuilder.create();

							// show it
							alertDialog.show();
			            	builder.cancel();	
			            }
			        	
			            
			        });
					

				}

			});
		
	}
	
	private void refreshImageList(){
		customAdapter.notifyDataSetChanged();
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
	      						previews.clear();
	      						ArrayHelper ar = new ArrayHelper(context);
	      						ar.saveArray("paths", new ArrayList<String>());
	      						ar.saveArray("base64s", new ArrayList<String>());
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
		Log.d(TAG, "Previews Local size before image processing: "+previews.size() + " images, attempting to pass list");
		super.onActivityResult(requestCode, resultCode, data);
		
		boolean doConvert = false;
		switch (requestCode) {
		case REQUEST_CHOOSER:
			if (resultCode == RESULT_OK) {
				
				final Uri uri = data.getData();
				pathSelected = getPath(context, uri);
				Log.d(TAG, "Path selected: "+pathSelected);
				if(pathSelected!= null){
					creatingSample = true;
					doConvert = true;
				}
				
			}else{
				//Nothing
			}
			break;
		}
		Log.d(TAG, "doConvert: "+doConvert );
		if(doConvert){
			if(imageAlreadyProcessed(pathSelected)){
				imageAlreadyProcessed = true;
				Toast.makeText(getApplicationContext(),
						"La imagen ya fue agregada...", Toast.LENGTH_SHORT).show();
				return;
			}
			imageSelected = decodeFile(pathSelected);
			if(imageSelected == null){
				Toast.makeText(getApplicationContext(),
						"Archivo no válido como imagen...", Toast.LENGTH_SHORT).show();
			}else{
				newBase64 = new String(Base64Helper.encodeTobase64(imageSelected));
				newLocalPath = new String(pathSelected);
				Log.d(TAG,"new image size before added: "+newBase64.getBytes().length);
				imageSelected.recycle();
				imageSelected = null;
				System.gc();
			}
		}

	}
	
	private void addImage(){
		Log.d(TAG, " Previews size before addition "+previews.size() + " images");
		
		
		String copyBase64 = new String(newBase64); 
		String copyPath = new String(newLocalPath);

		Image img =new Image(null, 
				null, 
				null, 
				null, 
				"Imagen "+(previews.size() +1), 
				new Date().toLocaleString(),
				copyBase64, 
				copyPath);
		//tenemos la nueva imagen
		//creamos el helper
		 ArrayHelper ar = new ArrayHelper(context);
		 
		 //obtenemos los paths guardados, vacio si es la primera imagen
		 paths= ar.getArray("paths");
		 //obtenemos los base64
		 base64s = ar.getArray("base64s");
		 previews.clear();
		 // Indicamos que es conveniente correr el gc para evitar posibles leaks de memoria tras vaciar la lista
		 System.gc();
		 // regenramos las imagenes de los paths
		 String saved64;
		 String newLocPath;
		 int x = 1;
		 for (String i : paths) {
			 
			 //el base64 ya fue previamente guardado, solo resta obtenerlo
				saved64  = base64s.get(x-1);
				newLocPath = new String(i);
				Log.d(TAG,"new image size before added: "+saved64.getBytes().length);
				//Reconstruimos la imagen y la agregamos a previews
				previews.add(new Image(null, 
						null, 
						null, 
						null, 
						"Imagen "+x, 
						new Date().toLocaleString(),
						saved64, 
						newLocPath));
				x++;
			
		}
		 //agregamos la nueva
		 previews.add(img);
		 // agregamos el path actual y base64
		 Log.d(TAG, "adding base64 for path: "+ copyPath  );
		 Log.d(TAG, "Before addition in sharedPref => paths size: "+paths.size() +  "  /  base64s size: "+base64s.size());
		 paths.add(copyPath);
		 base64s.add(copyBase64);
		 
		 //Guardamos los paths y base64, las posiciones en ambas listas se corresponden
		 ar.saveArray("paths", paths);
		 ar.saveArray("base64s", base64s);
		 
		 Log.d(TAG, "After addition in sharedPref=> paths size: "+paths.size() +  "  /  base64s size: "+base64s.size());
		
		Log.d(TAG, "newImage added, Previews have now "+previews.size() + " images");
	}
	
	
	private boolean imageAlreadyProcessed(String path) {
		for (Image img : previews) {
			if(img.getLocalPath().equals(path)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(creatingSample && !imageAlreadyProcessed){
			Log.d(TAG, "ONResume started, attempting to execute AddImage for newImageToAdd ");
			addImage();
			creatingSample = false;
		}
		imageAlreadyProcessed = false;
		refreshImageList();
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
								//Ayudamos a que el usuario rapidamente coloque un nombre reubicando cursor y abriendo teclado
								sampleNameField.setSelection(0);
								sampleNameField.requestFocus();
								InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
							    inputMethodManager.toggleSoftInputFromWindow(sampleNameField.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
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
		int c = 1;
		//Reconstruimos las imágenes utilizando la informacion en SharedPreferences 
		// Solucion a bug de lista de images eliminada de memoria
		// detectado en galaxy S3 y Lg Spirit. Se utiliza SharedPreferences como intermediario para pasar Base64s y paths de cada imagen elegida
		previews.clear();
		ArrayList<Image> preImages = new ArrayList<Image>();
		ArrayHelper ar = new ArrayHelper(context);
		paths = ar.getArray("paths");
		base64s = ar.getArray("base64s");
		for (String path : paths) {
			preImages.add(new Image(null, 
					null, 
					null, 
					null, 
					"Imagen "+c, 
					new Date().toLocaleString(),
					base64s.get(c-1), 
					path));
			c++;
		}
		newSample.setImages(preImages);
		newSample.setSent(false);
		newSample.setResolved(false);
		newSample.setValid(true);
		newSample.setRequestTreatmentIntents(0);
		newSample.setMinutesFromLastRequest(0);
		TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		Double randomValue = Math.random();
		String hashRaw = mngr.getDeviceId() + newSample.getOriginDate().toString()+ randomValue.toString();
		Log.d(TAG,"PreHash is: "+ hashRaw);
		newSample.setHash(SecurityHelper.toSHA256(hashRaw));
		
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
				   previews.clear();
			   }
			}); 
	    	
	    	
	    }
	}
	
	
	

	public ArrayList<Image> getPreviews() {
		return previews;
	}

	public SamplesDataSource getSamplesDataSource() {
		return samplesDataSource;
	}

	public void setSamplesDataSource(SamplesDataSource samplesDataSource) {
		this.samplesDataSource = samplesDataSource;
	}
	
	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @author paulburke
	 */
	public static String getPath(final Context context, final Uri uri) {

	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

	    // DocumentProvider
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            if ("primary".equalsIgnoreCase(type)) {
	                return Environment.getExternalStorageDirectory() + "/" + split[1];
	            }

	            // TODO handle non-primary volumes
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) {

	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

	            return getDataColumn(context, contentUri, null, null);
	        }
	        // MediaProvider
	        else if (isMediaDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            Uri contentUri = null;
	            if ("image".equals(type)) {
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            } else if ("video".equals(type)) {
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	            } else if ("audio".equals(type)) {
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	            }

	            final String selection = "_id=?";
	            final String[] selectionArgs = new String[] {
	                    split[1]
	            };

	            return getDataColumn(context, contentUri, selection, selectionArgs);
	        }
	    }
	    // MediaStore (and general)
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {
	        return getDataColumn(context, uri, null, null);
	    }
	    // File
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
	        String[] selectionArgs) {

	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = {
	            column
	    };

	    try {
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
	                null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int column_index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(column_index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
	
	
}
