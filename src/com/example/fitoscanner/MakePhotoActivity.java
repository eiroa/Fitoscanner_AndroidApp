package com.example.fitoscanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;

import com.example.fitoscanner.helpers.Base64Helper;
import com.example.fitoscanner.helpers.CameraPreview;
import com.example.fitoscanner.helpers.CustomListViewAdapter;
import com.example.fitoscanner.helpers.PhotoHandler;
import com.example.fitoscanner.model.Image;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MakePhotoActivity extends Activity {
	  public final static String DEBUG_TAG = "MakePhotoActivity";
	  public final static String TAG = "MakePhotoActivity";
	  public static final int MEDIA_TYPE_IMAGE = 1;
	  public static final int MEDIA_TYPE_VIDEO = 2;
	  private Camera camera;
	  private int cameraId = 0;
	  public boolean hasCamera;
	  Bitmap recentPhoto;
	  private ArrayList<Image> previews = new ArrayList<Image>();
	  CameraPreview mPreview;
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	   this.hasCamera = checkCameraHardware(this);
	    if(this.hasCamera){
	    	this.camera = Camera.open();
	    	this.setShotView();
	    }
    
	  }
	  
	@Override
	protected void onStart() {
		super.onStart();
		
	}
	
	private void setShotView(){
		setContentView(R.layout.takepic_layout);
        Log.i(TAG, "Layout changed to previews layout");
        Log.i(TAG, "previews are... "+previews.toString());
        startPreview();
        setShotButton();
	}
	  
	private void setTakeOneMoreButton(){
		Button takeOneMoreButton = (Button) findViewById(R.id.button_takePicAgain);
	     takeOneMoreButton.setOnClickListener(
	         new View.OnClickListener() {
	             @Override
	             public void onClick(View v) {
	                 setShotView();
	               }              
	         }
	     );
	}
	
	private void setShotButton(){
		Button captureButton = (Button) findViewById(R.id.button_capture);
	    captureButton.setOnClickListener(
	         new View.OnClickListener() {
	             @Override
	             public void onClick(View v) {
	                 // get an image from the camera
	                 camera.takePicture(myShutterCallback, myPictureCallback_RAW, mPicture);
	                 setContentView(R.layout.previews_layout);
	                 Log.i(TAG, "Layout changed to previews layout");
	                 Log.i(TAG, "previews are... "+previews.toString());
	                 setTakeOneMoreButton();
	               }              
	         }
	     ); 
	}
	  
	  private void startPreview(){
		  if(hasCamera){
		    	try {
		    		
		    		camera.setDisplayOrientation(90);
		    		mPreview = new CameraPreview(this, camera);
	    	        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
	    	        preview.addView(mPreview);
	    	        
	    	        Camera.Parameters p = camera.getParameters();
	    	        p.setRotation(90);
	    	        camera.setParameters(p);
		    	} catch (Exception e) {
		 			Toast.makeText(getApplicationContext(), "Error opening camera", Toast.LENGTH_LONG).show();
		 		} 
		  }
	  }
	  
	  private boolean checkCameraHardware(Context context) {
		    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
		    	Toast.makeText(this, "Camera detected", Toast.LENGTH_SHORT).show();
		        return true;
		    } else {
		    	Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
		        return false;
		    }
		}
	  
	  
	  ShutterCallback myShutterCallback = new ShutterCallback(){

		  @Override
		  public void onShutter() {
		  
		  }};

		 PictureCallback myPictureCallback_RAW = new PictureCallback(){

		  @Override
		  public void onPictureTaken(byte[] arg0, Camera arg1) {
		  
		  }};
	  
	  @Override
	  protected void onPause() {
	    if (camera != null) {
	      camera.release();
	      camera = null;
	    }
	    super.onPause();
	  }
	
	  private PictureCallback mPicture = new PictureCallback() {

		    @Override
		    public void onPictureTaken(byte[] data, Camera camera) {
		    	
		        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
		        if (pictureFile == null){
		        	Toast.makeText(getApplicationContext(), "Failed to create File " +pictureFile.getPath(), Toast.LENGTH_LONG).show();
		            return;
		        }else if(pictureFile.exists()){
		        	pictureFile.delete();
		        }
		       String absPath = pictureFile.getAbsolutePath();
		        
		        Log.i(TAG, "Picture saved "+absPath);
		        
		        setTakeOneMoreButton();
		        try {
				     
		        	
		            FileOutputStream fos = new FileOutputStream(pictureFile);
		            fos.write(data);
		            fos.close();
		            Toast.makeText(getApplicationContext(), "Image saved! as "+pictureFile.getName() + " in "+absPath, Toast.LENGTH_LONG).show();
		            recentPhoto = BitmapFactory.decodeFile(absPath);
	                Image newImage = new Image(0L,Base64Helper.encodeTobase64(recentPhoto), pictureFile.getName(), absPath);
	                previews.add(newImage);
	                setTakeOneMoreButton();
	                final ListView listview = (ListView) findViewById(R.id.previewSamplesList);
	                final CustomListViewAdapter customAdapter = new CustomListViewAdapter(
	                		 getApplicationContext(), R.layout.samplepreview_fragment, previews);
	                
	                 
	                 listview.setAdapter(customAdapter);
	                 Log.i(TAG, "Adapter set...");
	                 listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	                   @Override
	                   public void onItemClick(AdapterView<?> parent, final View view,
	                       int position, long id) {
	                	   Toast toast = Toast.makeText(getApplicationContext(),
	                	            "Item " + (position + 1) + ": " + previews.get(position),
	                	            Toast.LENGTH_SHORT);
	                	        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	                	        toast.show();
	                     
	                   }

	                 });
	                Log.i(TAG, "Recent photo loaded");
	                Log.i(TAG,previews.toString());
		        } catch (FileNotFoundException e) {
		            Log.d(TAG, "File not found: " + e.getMessage());
		        } catch (IOException e) {
		            Log.d(TAG, "Error accessing file: " + e.getMessage());
		        }
		    }

			
		};
		
		public  Uri getOutputMediaFileUri(int type){
		      return Uri.fromFile(getOutputMediaFile(type));
		}

		/** Create a File for saving an image or video */
		public File getOutputMediaFile(int type){


		    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
		              Environment.DIRECTORY_PICTURES), "FitoScanner");

		    if (! mediaStorageDir.exists()){
		        if (! mediaStorageDir.mkdirs()){
		            Log.d("MyCameraApp", "failed to create directory");
		            return null;
		        }
		    }

		    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		    File mediaFile;
		    if (type == MEDIA_TYPE_IMAGE){
		        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
		        "FitoScanner_shot_"+ timeStamp + ".jpg");
		    } else if(type == MEDIA_TYPE_VIDEO) {
		        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
		        "VID_"+ timeStamp + ".mp4");
		    } else {
		        return null;
		    }

		    return mediaFile;
		}

	
}
