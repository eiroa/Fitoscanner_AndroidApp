package com.example.fitoscanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.fitoscanner.helpers.CameraPreview;
import com.example.fitoscanner.helpers.PhotoHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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
	  private ArrayList<File> previews = new ArrayList<File>();
	  CameraPreview mPreview;
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.takepic_layout);
	     
	     
	     

	     
	     
	     Button captureButton = (Button) findViewById(R.id.button_capture);
	     captureButton.setOnClickListener(
	         new View.OnClickListener() {
	             @Override
	             public void onClick(View v) {
	                 // get an image from the camera
	                 camera.takePicture(myShutterCallback, myPictureCallback_RAW, mPicture);
	                 setContentView(R.layout.previews_layout);
	             }
	         }
	     ); 

	    
	  }
	  
	  @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		hasCamera = checkCameraHardware(this);
		if(hasCamera){
	    	try {
	    		camera = Camera.open();
	    		mPreview = new CameraPreview(this, camera);
    	        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
    	        preview.addView(mPreview);
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
	  
//	@SuppressLint("NewApi")
//	private int findFrontFacingCamera() {
//	    int cameraId = -1;
//	    // Search for the front facing camera
//	    int numberOfCameras = Camera.getNumberOfCameras();
//	    for (int i = 0; i < numberOfCameras; i++) {
//	      CameraInfo info = new CameraInfo();
//	      Camera.getCameraInfo(i, info);
//	      if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
//	        Log.d(DEBUG_TAG, "Camera found");
//	        cameraId = i;
//	        break;
//	      }
//	    }
//	    return cameraId;
//	  }
	  
	  ShutterCallback myShutterCallback = new ShutterCallback(){

		  @Override
		  public void onShutter() {
		   // TODO Auto-generated method stub
		  
		  }};

		 PictureCallback myPictureCallback_RAW = new PictureCallback(){

		  @Override
		  public void onPictureTaken(byte[] arg0, Camera arg1) {
		   // TODO Auto-generated method stub
		  
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
		        }

		        try {
		            FileOutputStream fos = new FileOutputStream(pictureFile);
		            fos.write(data);
		            fos.close();
		            Toast.makeText(getApplicationContext(), "Image saved! as "+pictureFile.getName() + " in "+pictureFile.getPath(), Toast.LENGTH_LONG).show();
		            previews.add(pictureFile);
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
