package com.example.fitoscanner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageActivity extends Activity{
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int REQUEST_CODE = 1;
	
	private Uri fileUri;
	private Bitmap bitmap;
	private ImageView imageView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.takepic_layout);
//	    imageView = (ImageView) findViewById(R.id.imageSourceTaken);
	    // create Intent to take a picture and return control to the calling application
//	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//	    fileUri = getOutputMediaFileUri("sourceImage"); // create a file to save the image
//	    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
//
//	    // start the image capture Intent
//	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	    
//	    Button bt = (Button) findViewById(R.id.takeShotButton);
//        bt.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View arg0) {     		
//            	Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent, REQUEST_CODE);
//            }
//        });
	}
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    InputStream stream = null;
	    if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
	      try {
	        // recyle unused bitmaps
	        if (bitmap != null) {
	          bitmap.recycle();
	        }
	        stream = getContentResolver().openInputStream(data.getData());
	        bitmap = BitmapFactory.decodeStream(stream);

	        imageView.setImageBitmap(bitmap);
	      } catch (Exception e) {
	        e.printStackTrace();
	      } finally {
	        {
	        if (stream != null)
	          try {
	            stream.close();
	          } catch (IOException e) {
	            e.printStackTrace();
	          }
	      }
	  }
	 }
	
}
