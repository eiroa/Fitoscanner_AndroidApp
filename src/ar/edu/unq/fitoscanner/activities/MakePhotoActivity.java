package ar.edu.unq.fitoscanner.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Semaphore;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import ar.edu.unq.fitoscanner.helpers.URLHelper;
import ar.edu.unq.fitoscanner.model.Image;
import ar.edu.unq.fitoscanner.model.LocationData;
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
	private ArrayList<Image> previews = new ArrayList<Image>();
	private CameraPreview mPreview;
	private ImageDataSource imageDatasource;
	private SamplesDataSource samplesDataSource;
	private Sample newSample;
	private Typeface font;
	private boolean usesLocation;
	private boolean  usesLowDefinition;
	private boolean saving;
	public boolean debug = true;
	public boolean takingPhoto;
	public boolean onPause = false;
	final private Activity activity = this;
	private String sampleName = "";
	private EditText sampleNameField;
	private Bitmap imageSelected;
	private boolean clicked = false;
	private int lowHeight = 600;
	private int lowWidth = 800;
	private int highHeight = 950;
	private int highWidth = 1200;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.usesLocation = getIntent().getBooleanExtra("usesLocation", false);
		this.usesLowDefinition = getIntent().getBooleanExtra("usesLowDefinition", true);
		Log.d(TAG, "using Low definition: " + usesLowDefinition);
		font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");

		this.hasCamera = checkCameraHardware(this);
		if (this.hasCamera) {
			// Abrimos la camara trasera que este disponible
			Log.d(TAG, "Camera detected, attempting opening for first time");
			this.camera = Camera.open();
			try {
				// Se ha detectado que algunos dispositivos, al bindear la vista
				// de la camara a la
				// pantalla de forma inmediata, generaba un error, probableme
				// por una concurrencia fallida
				// en la cual la camara no habia logrado ser abierta en tiempo
				// correcto
				// Se ha visto que forzando una mínima espera de unos
				// milisegundos, luego
				// de ejecutar Camera-open() resuelve este problema

				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Se inicia el layout para tomar una foto
			Log.d(TAG, "Attempting to set Photo screen ");
			this.setShotView();

			// Iniciamos los data source para poder guardar la nueva muestra con
			// sus
			// respectivas imagenes en la base de datos SQLite
			setSamplesDataSource(new SamplesDataSource(this));
			setImageDatasource(new ImageDataSource(this));

		}
		saving = false;

	}

	private void setShotView() {
		if (previews.size() > 0) {
			sampleName = sampleNameField.getText().toString();
		}
		Log.d(TAG, "Setting Photo screen layout");
		takingPhoto = true;
		this.setContentView(R.layout.takepic_layout);
		this.startPreview();
		System.gc();
	}

	private void prepareDialog(AlertDialog.Builder builder, String title,
			String message, Boolean isCancelable, String OkButtonTxt,
			android.content.DialogInterface.OnClickListener okListener,
			String cancelButtonTxt,
			android.content.DialogInterface.OnClickListener cancelListener) {
		builder.setTitle(title).setMessage(message).setCancelable(isCancelable)
				.setPositiveButton(OkButtonTxt, okListener)
				.setNegativeButton(cancelButtonTxt, cancelListener);
	}

	private void releaseCamera() {
		if (camera != null) {
			Log.d(TAG, "Releasing camera resources");
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.lock();
			camera.release();
			camera = null;
		}
	}

	private void setTakeOneMoreButton() {
		Button takeOneMoreButton = (Button) findViewById(R.id.button_takePicAgain);
		takeOneMoreButton.setTypeface(font);
		takeOneMoreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// Al volver a utilizar la camara,
				// asegurarse de toda forma posible, que se destruye la
				// instancia de la camara, y volver a pedir otra

				releaseCamera();
				clicked = false;
				if (previews.size() >= 7) {
					Toast.makeText(
							getApplicationContext(),
							"Atencion, la muestra no puede superar las 7 imagenes",
							Toast.LENGTH_SHORT).show();
				} else {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.d(TAG, "Re opening camera");
					camera = Camera.open();
					if (!saving)
						setShotView();
				}

			}
		});
	}

	/**
	 * Su funcion es solamente hacer un wrap del metodo take picture.
	 * 
	 * @param previewFrame
	 */
	private void setShotButton(FrameLayout previewFrame) {
		previewFrame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!clicked) {
					Log.d(TAG, "Attempting to take photo");

					// El siguiente codigo antes de takePicture si no existe se
					// ha presenciado un cuelgue en la camara
					// en un dispositivo android, particularmente Samsung galaxy
					// prime, no está claro el porque.
					// aparenta ser un error de Android directamente, la versión
					// afectada fue la 4.4.4 kitkat.
					// +++++++++
					System.gc();
					camera.setPreviewCallback(null);
					camera.setOneShotPreviewCallback(null);
					try {
						camera.reconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}// +++++++++
					
					//tocamos para sacar foto 
					//****
					clicked = true;
					takingPhoto = false;
					//******
					
					
					camera.takePicture(myShutterCallback,
							myPictureCallback_RAW, mPicture);
				} else {
					Log.d(TAG, "Attempted multiple click when taking photo");
				}

			}
		});
	}

	/*
	 * Setea el preview de layouts
	 */
	private void setPreviewsLayout() {

		// Seteo layout
		Log.i(TAG, "Setting previews Layout");
		setContentView(R.layout.previews_layout);
		Log.i(TAG, "previews are... " + previews.toString());

		// Seteo listView
		final ListView listview = (ListView) findViewById(R.id.previewSamplesList);
		CustomImageListViewAdapter customAdapter = new CustomImageListViewAdapter(
				getApplicationContext(), R.layout.samplepreview_fragment,
				previews);

		listview.setAdapter(customAdapter);
		Log.i(TAG, "Adapter set...");
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final Image i = (Image) listview.getAdapter().getItem(position);

				imageSelected = Base64Helper.decodeScaledBase64(i.getBase64(),
						getWindowManager().getDefaultDisplay().getWidth(),
						getWindowManager().getDefaultDisplay().getWidth());
				final Dialog builder = new Dialog(activity);
				builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
				builder.setContentView(R.layout.image_popup);
				ImageView imgPopup = (ImageView) builder
						.findViewById(R.id.image_popup);
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
								activity);

						// set title
						String title = "¿Eliminar "+i.getTitle()+ " ?";


						prepareDialog(alertDialogBuilder, title, "Elija una opción ", true,
								"Aceptar", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										previews.remove(i);
										setPreviewsLayout();
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
		
		

		// Seteo nombre
		sampleNameField = (EditText) findViewById(R.id.preview_sampleNameField);
		sampleNameField.setText(sampleName);

		// Seteo comportamiento tomar nueva imagen
		setTakeOneMoreButton();

		// Seteo comportamiento guardar imagebn
		setSaveButton();

		// liberamos camara
		releaseCamera();
	}

	private void setSaveButton() {
		Button saveButton = (Button) findViewById(R.id.button_saveSample);
		saveButton.setTypeface(font);
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String sampleName = (sampleNameField.getText()).toString();
				if (!saving) {
					if (previews.size() < 3) {
						Toast.makeText(
								getApplicationContext(),
								"Atencion, la muestra debe contener al menos 3 imagenes",
								Toast.LENGTH_SHORT).show();
					} else {
						if (sampleName.equals("") || sampleName == null) {
							Toast.makeText(getApplicationContext(),
									"Aplique un nombre a la muestra por favor",
									Toast.LENGTH_LONG).show();
							// Ayudamos a que el usuario rapidamente coloque un
							// nombre reubicando cursor y abriendo teclado
							sampleNameField.setSelection(0);
							sampleNameField.requestFocus();
							InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							inputMethodManager.toggleSoftInputFromWindow(
									sampleNameField.getApplicationWindowToken(),
									InputMethodManager.SHOW_FORCED, 0);
						} else {
							processNewSample();
						}
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

//				camera.setDisplayOrientation(90);
				Log.d(TAG, "Initiating photo screen ");
				
				FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
				Log.d(TAG, "Setting camera previen into layout");
				
				setCameraParameters();
				mPreview = new CameraPreview(this, camera);
				preview.addView(mPreview);
				Log.d(TAG, "Camera parameters ready");
				Log.d(TAG, "Setting  camera shot action");
				this.setShotButton(preview);
				// Directamente asignamos la funcion de tomar la foto al tocar
				// la pantalla

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"Error al abrir cámara !", Toast.LENGTH_SHORT).show();
				Log.e(TAG,
						"Error opening camera -> "
								+ e.getStackTrace().toString() + " message: "
								+ e.getMessage() + e.toString() + e.getCause());
			}
		}
	}
	
	/**
	 * El objetivo es setear la camara con el tamaño minimo, utilizar flash, autofoco y rotar la imagen
	 */
	private void setCameraParameters() {
		Camera.Parameters p = null;
		try {
			p = camera.getParameters();
			Log.d(TAG, "Setting camera parameters");
			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			
//			Camera.Size size = getBestPreviewSize(displaymetrics.widthPixels, displaymetrics.heightPixels, p);
//			Camera.Size pictureSize = getSmallestPictureSize(p);
			Camera.Size bestSize = chooseOptimalSize(p);
			Log.d(TAG, " Configuring camera for optimal size. \n Optimal height is: "+bestSize.height + 
					" and optimal width is: "+bestSize.width);
			
			// Valoeres H y W invertidos, queremos una foto vertical
			p.setPictureSize(bestSize.width,bestSize.height);
			p.setPictureFormat(ImageFormat.JPEG);
			// Por defecto, obtenemos la foto con flash y con foco automatico, siempre y cuando el hardware lo soporte
			if (p.getSupportedFlashModes().contains(
					Camera.Parameters.FLASH_MODE_AUTO)) {
				Log.d(TAG, "using Auto flash...");
				p.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
			} else {
				Log.d(TAG, "Auto flash not supported...");
			}
			if (p.getSupportedFocusModes().contains(
					Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
				Log.d(TAG, "Standard focus enabled");
				p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			} else {
				Log.d(TAG,
						"Standard focus mode not supported, are you a samsung?");
				if (p.getSupportedFocusModes().contains(
						Camera.Parameters.FLASH_MODE_AUTO)) {
					p.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
				} else {
					Log.d(TAG,
							"No possible auto focus on camera... pitiful device");
				}
			}
			p.setRotation(90);
			camera.setParameters(p);
		} catch (Exception e) {
			if (p == null) {
				Log.d(TAG,
						"Error setting camera parameters. Attempting to continue. Camera Parameters are null!");
			} else {
				Log.d(TAG,
						"Error setting camera parameters. Attempting to continue. Camera Parameters:"
								+ p.toString());
			}
		}

	}

	private void processNewSample() {
		saving = true;
		new NewSampleTask().execute("");
		onBackPressed();
	}

	/**
	 * Construye una nueva muestra
	 * El hash unico se construye con imei + fechaMuestra + randomValue, y eso se pasa a SHA256
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
		for (Image i : previews) {
			i.setTitle("Imagen "+ c);
			c++;
		}
		newSample.setImages(previews);
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
		return newSample;

	}

	@Override
	protected void onPause() {
		releaseCamera();
		onPause = true;
		clicked = false;
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (takingPhoto && onPause) {
			Log.d(TAG, "Restoring camera from screen lock");
			camera = camera.open();
			setShotView();
			onPause = false;
		}
	}

	@Override
	public void onBackPressed() {
		if (saving) {
			super.onBackPressed();
		} else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					activity);

			// set title
			String title = "Atención, está a punto de eliminar la muestra actual ";

			// prepare dialog

			prepareDialog(alertDialogBuilder, title, "Elija una opción ", true,
					"Aceptar", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (camera != null) {
								camera.release();
								camera = null;
							}
							activity.finish();
						}
					}, "Cancelar", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
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
			Log.d(TAG, "OnShutter executed");
		}
	};

	PictureCallback myPictureCallback_RAW = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {

		}
	};

	/**
	 * Devuelve un PictureCallback que define el comportamiento que tendrá
	 * ejecutar la camara y obtener los datos de la foto
	 * 
	 * Al finalizar el procesamiento de la nueva imagen, se regenera el layout
	 * de las previews
	 */
	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			Log.d(TAG, "Picture Taken, saving new image");
			try {
				Log.d(TAG, "Size of new picture: "+ (data.length / 1024) +" Kb");
				final BitmapFactory.Options options = new BitmapFactory.Options();


				options.inPurgeable = true;
				recentPhoto = BitmapFactory.decodeByteArray(data, 0,
						data.length, options);

				Image newImage = new Image(null, null, null, null, "Imagen "
						+ (previews.size() + 1), new Date().toLocaleString(),
						Base64Helper.encodeTobase64(recentPhoto));

				Log.d(TAG, "Image object created =>> " + newImage.toString());
				recentPhoto.recycle();
				System.gc();
				previews.add(newImage);
				newImage = null;

				Log.i(TAG, "Recent photo loaded");

				Log.i(TAG, previews.toString());

				// Generamos la vista de previews
				setPreviewsLayout();
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
	public void saveSample(Sample sample) {
		samplesDataSource.open();
		try {

			samplesDataSource.fullSaveSample(sample);

		} finally {
			samplesDataSource.close();
		}
	}

	private LocationData getLocation() {
		Log.d(TAG, "Attempting to get GPS data");
		GPSHelper gps = new GPSHelper(activity);
		Geocoder gcd = new Geocoder(activity);
		Log.d(TAG, "GPSHelper is:" + gps + "  and Geocoder is: " + gcd);
		LocationData ldt = new LocationData("", "", "-", "-", "-");
		List<Address> addresses = null;
		// check if GPS enabled
		Log.d(TAG, "Checking if location can be obtained...sleeping for 500 ms");
	try {
		Thread.sleep(500);
	} catch (InterruptedException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
		if (gps.canGetLocation()) {
			Log.d(TAG, "GPS data available, accesing data...");
			String latitude = "";
			String longitude = "";
			String city = "-";
			String state = "-";
			String country = "-";
			try {

				latitude = Double.toString(gps.getLatitude());
				Log.d(TAG, "Getting latitude: " + latitude);
				longitude = Double.toString(gps.getLongitude());
				Log.d(TAG, "Getting longitude: " + longitude);
				addresses = gcd.getFromLocation(
						gps.getLocation().getLatitude(), gps.getLocation()
								.getLongitude(), 1);

				city = addresses.get(0).getLocality();
				Log.d(TAG, "Getting city: " + city);
				state = addresses.get(0).getAdminArea();
				Log.d(TAG, "Getting state: " + state);
				country = addresses.get(0).getCountryName();
				Log.d(TAG, "Getting country: " + country);

				ldt.setCity(city);
				ldt.setCountry(country);
				ldt.setLatitude(latitude);
				ldt.setLongitude(longitude);
				ldt.setState(state);
				// Log.d(TAG, "Your Location is - \nLat: " + latitude +
				// "\nLong: " +
				// longitude + "\n  Closest city: "+gps.getAddress());
			} catch (NullPointerException e) {
				Log.e(TAG, "Error! No data obtained from geocoder");
				Log.w(TAG,
						"Null pointer obtained, ... returning empty location details");

				return ldt;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.e(TAG, "Error obtaining data from GPS");
				e.printStackTrace();
			} finally {
				gps.stopSelf();
			}

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			// gps.showSettingsAlert();
			Log.d(TAG,
					"GPS not available, ... returning empty location details");
			Toast.makeText(
					activity,
					"Atencion, tanto internet como gps esta deshabilitado, "
							+ "no podra adjuntar ubicacion", Toast.LENGTH_SHORT)
					.show();
		}
		return ldt;
	}

	private class NewSampleTask extends AsyncTask<String, Void, Void> {
		Sample sample = null;
		int size = 3;
		String name = "";
		SamplesDataSource sds = new SamplesDataSource(activity);
		LocationData ldt;

		@Override
		protected Void doInBackground(String... params) {
			sample = makeNewSample();
			size = sample.getImages().size();
			name = sample.getSampleName();
			if (usesLocation) {
				Log.d(TAG,
						"Attempting to obtain location for sample "
								+ sample.getSampleName());
				runOnUiThread(new Runnable() {
					public void run() {
						ldt = getLocation();
					}
				});
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			runOnUiThread(new Runnable() {
				public void run() {
					sample.setLocationData(ldt);
					sds.open();
					sds.fullSaveSample(sample);
					sds.close();
					Toast.makeText(
							activity,
							"Se ha guardado la muestra " + name + " con " + ""
									+ size + " imágenes", Toast.LENGTH_SHORT)
							.show();
				}
			});

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
	
	/**
	 * Obtiene el mejor tamaño minimo posible de imagen preview , 
	 * @param width
	 * @param height
	 * @param parameters
	 * @return
	 */
	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return (result);
	}

	/**
	 * Devuelve el tamaño mas chico posible
	 * @param parameters
	 * @return
	 */
	private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		Camera.Size result = null;
		Log.d(TAG, "Displaying Available image sizes..." );
		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			Log.d(TAG, "Available Size => Width: "+size.width+ " / Height: "+size.height +
					"  =>"+ (size.width * size.height)/1024000 +" Mp");
			if (result == null) {
				result = size;
			} else {
				int resultArea = result.width * result.height;
				int newArea = size.width * size.height;
				if (newArea < resultArea) {
					result = size;
				}
			}
		}

		return (result);
	}
	
	private Camera.Size chooseOptimalSize(Camera.Parameters parameters) {
		Camera.Size result = null;
		Log.d(TAG, "Displaying Available image sizes..." );
		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			Log.d(TAG, "Available Size => Width: "+size.width+ " / Height: "+size.height +
					"  =>"+  (( new Double(size.width) * new Double(size.height) )/1024000) +" Mp");
			if (result == null) {
				result = size;
				//Si solo soporta un tamaño, usamos ese
			} else {
				if(usesLowDefinition){
					Log.d(TAG, "Attempting to find optimal low res size");
					if( ((lowHeight +125) > size.height &&  size.height > (lowHeight - 125))
							&&
							((lowWidth +125)  >size.width &&  size.width > (lowWidth - 125) )	){
						result = size;
						Log.d(TAG, "Low Optimal size found");
					}else{
						if( ((lowHeight +500) > size.height &&  size.height > (lowHeight - 300))
								&&
								((lowWidth +500)  >size.width &&  size.width > (lowWidth - 300) )	){
							result = size;
							Log.d(TAG, "Low res Size found -- Warning: High dispersion from optimal");
					    }
					}
				}else{
					Log.d(TAG, "Attempting to find optimal high res size");
					if( ((highHeight +200) > size.height &&  size.height > (highHeight - 200) )
							&&
							((highWidth +200) > size.width &&  size.width > (highWidth - 200))	){
						result = size;
						Log.d(TAG, "High Optimal size found");
					}else{
						if( ((highHeight +600) > size.height &&  size.height > (highHeight - 300) )
								&&
								((highWidth +600) > size.width &&  size.width > (highWidth - 300))	){
							result = size;
							Log.d(TAG, "High res Size found -- Warning: High dispersion from optimal");
						}
					}
				}
			}
		}

		return (result);
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

	public ArrayList<Image> getPreviews() {
		return previews;
	}

	public void setPreviews(ArrayList<Image> previews) {
		this.previews = previews;
	}
	
	

}
