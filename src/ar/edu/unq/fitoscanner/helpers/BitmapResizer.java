package ar.edu.unq.fitoscanner.helpers;

import java.io.ByteArrayOutputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class BitmapResizer {

	public BitmapResizer() {
		// TODO Auto-generated constructor stub
	}
	
	public static byte[] resizeImage(byte[] imageRawBytes, int width, int height, int factor){
		Bitmap original = BitmapFactory.decodeByteArray(imageRawBytes , 0, imageRawBytes.length);
	    Bitmap resized = Bitmap.createScaledBitmap(original, width/factor, height/factor, true);
	         
	    ByteArrayOutputStream blob = new ByteArrayOutputStream();
	    resized.compress(Bitmap.CompressFormat.JPEG, 100, blob);
	 
	    return blob.toByteArray();
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	/*
	 * Uso: getResources() R.id.nombreRecurso (La mitad del ancho de la
	 * superficie a usar) (Mitad de altura de superficie a usar)
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/*
	 * Uso: getResources() R.id.nombreRecurso (La mitad del ancho de la
	 * superficie a usar) (Mitad de altura de superficie a usar)
	 */
	public static Bitmap decodeSampledBitmapFromResource(Bitmap bmp,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// BitmapFactory.decodeResource(res, resId, options);
		byte[] b = convertBitmapToByteArray(bmp);
		BitmapFactory.decodeByteArray(b, 0, b.length, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(b, 0, b.length,
				options);
	}

	public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		} else {
			byte[] b = null;
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.PNG, 0, byteArrayOutputStream);
				b = byteArrayOutputStream.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return b;
		}
	}

}
