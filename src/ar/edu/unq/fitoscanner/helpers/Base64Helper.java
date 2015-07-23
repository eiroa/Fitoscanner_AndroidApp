package ar.edu.unq.fitoscanner.helpers;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class Base64Helper {

	public static String encodeTobase64(Bitmap image)
	{
		Log.d("Base64Helper","attempting to encode image");
		if (android.os.Build.VERSION.SDK_INT>10)Log.d("Base64Helper", " image size attempting to encode: " +image.getByteCount());
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    image.compress(Bitmap.CompressFormat.JPEG, 95, baos);
	    byte[] b = baos.toByteArray();
	    String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

	    Log.d("Base64Helper", "Base64 result of new image is: "+ imageEncoded);
	    return imageEncoded;
	}
	public static Bitmap decodeBase64(String input) 
	{
	    byte[] decodedByte = Base64.decode(input, 0);
	    
	    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
	}
	
	public static Bitmap decodeScaledBase64(String input, int width, int height) 
	{
	    byte[] decodedByte = Base64.decode(input, 0);
	    
	    Bitmap bm = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
	    return Bitmap.createScaledBitmap(bm,width,height,true);
	}

}
