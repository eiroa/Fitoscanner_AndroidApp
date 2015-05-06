package ar.edu.unq.fitoscanner.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.datasources.ImageDataSource;
import ar.edu.unq.fitoscanner.datasources.SamplesDataSource;
import ar.edu.unq.fitoscanner.helpers.Base64Helper;
import ar.edu.unq.fitoscanner.helpers.CameraPreview;
import ar.edu.unq.fitoscanner.helpers.CustomImageListViewAdapter;
import ar.edu.unq.fitoscanner.helpers.GPSHelper;
import ar.edu.unq.fitoscanner.helpers.SecurityHelper;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;
import ar.edu.unq.fitoscanner.model.Image;
import ar.edu.unq.fitoscanner.model.Sample;

@SuppressLint("NewApi")
public class MakePhotoActivity extends Activity {
	public final static String TAG = "MakePhotoActivity";
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private Camera camera;
	private int cameraId = 0;
	private boolean hasCamera;
	private Bitmap recentPhoto;
	private Bitmap compressed;
	private ArrayList<Image> previews = new ArrayList<Image>();
	private CameraPreview mPreview;
	private ImageDataSource imageDatasource;
	private SamplesDataSource samplesDataSource;
	private Sample newSample;
	private Typeface font;
	private boolean usesLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.usesLocation =getIntent().getBooleanExtra("usesLocation", false);
		 font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");

		this.hasCamera = checkCameraHardware(this);
		if (this.hasCamera) {
			// Abrimos la camara trasera que este disponible
			this.camera = Camera.open();
			// Se inicia el layout para tomar una foto
			this.setShotView();

			// Iniciamos los data source para poder guardar la nueva muestra con
			// sus
			// respectivas imagenes en la base de datos SQLite
			setSamplesDataSource(new SamplesDataSource(this));
			setImageDatasource(new ImageDataSource(this));

		}

	}

	private void setShotView() {
		this.setContentView(R.layout.takepic_layout);
		Log.i(TAG, "Layout changed to previews layout");
		Log.i(TAG, "previews are... " + previews.toString());
		this.startPreview();
		System.gc();
	}

	private void setTakeOneMoreButton() {
		Button takeOneMoreButton = (Button) findViewById(R.id.button_takePicAgain);
		takeOneMoreButton.setTypeface(font);
		takeOneMoreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setShotView();
			}
		});
	}

	private void setShotButton(FrameLayout previewFrame) {
		previewFrame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				camera.takePicture(myShutterCallback, myPictureCallback_RAW,
						mPicture);
				setContentView(R.layout.previews_layout);
				Log.i(TAG, "Layout changed to previews layout");
				Log.i(TAG, "previews are... " + previews.toString());
				setTakeOneMoreButton();
				setSaveButton();
			}
		});
	}

	private void setSaveButton() {
		Button saveButton = (Button) findViewById(R.id.button_saveSample);
		saveButton.setTypeface(font);
		final EditText sampleNameField = (EditText) findViewById(R.id.preview_sampleNameField);
        
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String sampleName = (sampleNameField.getText()).toString();
				if (previews.size() < 3) {
					Toast.makeText(
							getApplicationContext(),
							"Atencion, de momento, la muestra debe contener al menos 3 imagenes",
							Toast.LENGTH_LONG).show();
					
				} else {
					if(sampleName.equals("")|| sampleName==null){
						Toast.makeText(
								getApplicationContext(),
								"Aplique un nombre a la muestra por favor",
								Toast.LENGTH_LONG).show();
					}else{
						makeNewSample();					
						saveSample();
						Toast.makeText(
								getApplicationContext(),
								"Se " + "ha guardado la muestra "
										+ newSample.getSampleName() + " con " + ""
										+ previews.size() + " imágenes",
								Toast.LENGTH_LONG).show();
						finish();
					}
					
				}
			}
		});
	}

	private void startPreview() {
		if (hasCamera) {
			Toast.makeText(getApplicationContext(),
					"Toque la pantalla para tomar una foto", Toast.LENGTH_LONG)
					.show();
			try {

				camera.setDisplayOrientation(90);
				mPreview = new CameraPreview(this, camera);
				FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
				preview.addView(mPreview);
				this.setShotButton(preview);
				// Directamente asignamos la funcion de tomar la foto al tocar
				// la pantalla

				Camera.Parameters p = camera.getParameters();
				p.setRotation(90);
				camera.setParameters(p);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Error opening camera",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * Construye una nueva muestra
	 */
	private void makeNewSample() {
		EditText sampleNameField = (EditText) findViewById(R.id.preview_sampleNameField);
		String sampleName = (sampleNameField.getText()).toString();
		newSample = new Sample();
		newSample.setSampleName(sampleName);
		newSample.setOriginDate(new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss")
				.format(new Date()));
		newSample.setFieldName("testing field");
		newSample.setImages(previews);
		newSample.setSent(false);
		String base64full = "";
		String hash = "";
		
		// calcular hash de cada imagen, concatenerlos y luego obtener hash de los hashes concatenados
		// tener en cuenta que cada base 64 tiene un salto de linea al final
		for (Image img : newSample.getImages()) {
			base64full = img.getBase64();
			hash = hash+SecurityHelper.toSHA256(base64full);
		}
		newSample.setHash(SecurityHelper.toSHA256(hash));
		if(this.usesLocation){
			Log.d(TAG, "Attempting to obtain location for sample " + sampleName); 
			addLocation();
		}
		

	}
	
	private void addLocation(){
		GPSHelper gps = new GPSHelper(MakePhotoActivity.this);
		Geocoder gcd = new Geocoder(this);
		List<Address> addresses = null;
        // check if GPS enabled     
        if(gps.canGetLocation()){
             
            String latitude = Double.toString(gps.getLatitude());
            String longitude = Double.toString(gps.getLongitude());
             
            try {
    			addresses = gcd.getFromLocation(gps.getLocation().getLatitude(), gps.getLocation().getLongitude(), 1);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		String city = "-";
    		String state = "-";
    		String country = "-";
    		try {
    			city = addresses.get(0).getLocality();
    			state = addresses.get(0).getAdminArea();
    			country = addresses.get(0).getCountryName();
    		} catch (NullPointerException e) {
    			Log.e(TAG, "Error! No data obtained from geocoder");
    		}
        	newSample.setLocationData(latitude, longitude, city, state, country);
            Log.d(TAG, "Your Location is - \nLat: " + latitude + "\nLong: " + 
            longitude + "\n  Closest city: "+gps.getAddress());
            gps.stopSelf();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
	}

	/**
	 * Verifica que el dispositivo tenga una camara disponible
	 * 
	 * @param context
	 * @return
	 */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(this,
					"Se ha podido detectar una cámara en el dispositivo",
					Toast.LENGTH_SHORT).show();
			return true;
		} else {
			Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
					.show();
			return false;
		}
	}

	@Override
	protected void onPause() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (camera != null) {
			camera.release();
			camera = null;
		}
		super.onBackPressed();
	}

	/**
	 * Por seguridad, para evitar leaks de memoria, seteamos en null algunos
	 * valores El Garbage Collector, se encargará de liberar la memoria
	 * consiguientemente
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		previews = null;
		newSample = null;
		imageDatasource = null;
		samplesDataSource = null;
	};

	ShutterCallback myShutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {

		}
	};

	PictureCallback myPictureCallback_RAW = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {

		}
	};

	/**
	 * Devuelve un PictureCallback que define el comportamiento que tendrá
	 * ejecutar la camara
	 */
	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			setTakeOneMoreButton();
			try {				
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 8;
				options.inPurgeable = true;
				recentPhoto = BitmapFactory.decodeByteArray(data, 0,
						data.length,options);
				
				//System.out.println("Photo encoded from camera with size "+ recentPhoto.getByteCount()+", attempting resize");
//				byte[] compressedBytes =  BitmapResizer.resizeImage(
//						data, 
//						recentPhoto.getWidth(), 
//						recentPhoto.getHeight(), 1);
//				recentPhoto.recycle();
//				recentPhoto=null;
//				System.out.println("bytes obtained with size:"+compressedBytes.length);
//				compressed = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.length);
//				System.out.println("Bitmap compressed with size: " + compressed.getByteCount());
				Image newImage = new Image(null, null,null,null, "Picture "
						+ (previews.size() + 1), new Date().toLocaleString(),
						Base64Helper.encodeTobase64(recentPhoto));
//				
//				compressed=null;
//				
				recentPhoto.recycle();
//				compressedBytes = null;
				System.gc();
				previews.add(newImage);
				newImage = null;
				final ListView listview = (ListView) findViewById(R.id.previewSamplesList);
				final CustomImageListViewAdapter customAdapter = new CustomImageListViewAdapter(
						getApplicationContext(),
						R.layout.samplepreview_fragment, previews);

				listview.setAdapter(customAdapter);
				Log.i(TAG, "Adapter set...");
				listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent,
							final View view, int position, long id) {
						Toast toast = Toast.makeText(
								getApplicationContext(),
								"Item " + (position + 1) + ": "
										+ previews.get(position),
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.BOTTOM
								| Gravity.CENTER_HORIZONTAL, 0, 0);
						toast.show();

					}

				});
				Log.i(TAG, "Recent photo loaded");
				Log.i(TAG, previews.toString());
			} catch (Exception e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			}
		}

	};

	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	public File getOutputMediaFile(int type) {

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"FitoScanner");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyy-MM-dd__HH_mm_ss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "FitoScanner_shot_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	/**
	 * Guarda la muestra que fue generada al tomar la primera foto
	 */
	public void saveSample() {
		samplesDataSource.open();
		try {

			samplesDataSource.saveSample(this.newSample);

		} finally {
			samplesDataSource.close();
		}
	}

	public ImageDataSource getImageDatasource() {
		return imageDatasource;
	}

	public void setImageDatasource(ImageDataSource imageDatasource) {
		this.imageDatasource = imageDatasource;
	}

	public SamplesDataSource getSamplesDataSource() {
		return samplesDataSource;
	}

	public void setSamplesDataSource(SamplesDataSource samplesDataSource) {
		this.samplesDataSource = samplesDataSource;
	}

}
