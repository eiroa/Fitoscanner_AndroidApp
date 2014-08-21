package ar.edu.unq.fitoscanner.helpers;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.model.Image;
import ar.edu.unq.fitoscanner.model.Sample;

public class CustomSampleListViewAdapter extends ArrayAdapter<Sample> {

	
	Context context;

	public CustomSampleListViewAdapter(Context context, int resourceId,
			List<Sample> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	/* private view holder class */
	private class ViewHolder {
		TextView txtDate;
		TextView txtSample;
		TextView txtLatAndLond;
		TextView txtCity;
		TextView txtState;
		TextView txtCountry;
		ImageView imagep1;
		ImageView imagep2;
		ImageView imagep3;
		TextView txtTitlep1;
		TextView txtDescp1;
		TextView txtTitlep2;
		TextView txtDescp2;
		TextView txtTitlep3;
		TextView txtDescp3;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Sample sample = getItem(position);
        Image p1 = sample.getImages().get(0);
        Image p2 = sample.getImages().get(1);
        Image p3 = sample.getImages().get(2);
         
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.savedsample_fragment, null);
            holder = new ViewHolder();

            holder.txtDate = (TextView) convertView.findViewById(R.id.savedSample_originDate);
            holder.txtSample = (TextView) convertView.findViewById(R.id.savedSample_sampleNameField);
            holder.txtLatAndLond = (TextView) convertView.findViewById(R.id.savedSample_latAndLon);
            holder.txtCity = (TextView) convertView.findViewById(R.id.savedSample_city);
            holder.txtState = (TextView) convertView.findViewById(R.id.savedSample_state);
            holder.txtCountry = (TextView) convertView.findViewById(R.id.savedSample_country);
            
            holder.txtDescp1 = (TextView) convertView.findViewById(R.id.secondLinep1);
            holder.txtTitlep1 = (TextView) convertView.findViewById(R.id.firstLinep1);
            holder.imagep1 = (ImageView) convertView.findViewById(R.id.photo1);
            
            holder.txtDescp2 = (TextView) convertView.findViewById(R.id.secondLinep2);
            holder.txtTitlep2 = (TextView) convertView.findViewById(R.id.firstLinep2);
            holder.imagep2 = (ImageView) convertView.findViewById(R.id.photo2);
            
            holder.txtDescp3 = (TextView) convertView.findViewById(R.id.secondLinep3);
            holder.txtTitlep3 = (TextView) convertView.findViewById(R.id.firstLinep3);
            holder.imagep3 = (ImageView) convertView.findViewById(R.id.photo3);
            convertView.setTag(holder);
        } else{
        	
        	Bitmap bm1 =Base64Helper.decodeBase64(p1.getBase64());
            Bitmap bm2 =Base64Helper.decodeBase64(p2.getBase64());
            Bitmap bm3 =Base64Helper.decodeBase64(p3.getBase64());
            holder = (ViewHolder) convertView.getTag();
        
        holder.txtDate.setText(sample.getOriginDate());
        holder.txtSample.setText(sample.getSampleName());
        holder.txtLatAndLond.setText(sample.getLocationData().getLatitude() + " / " 
        + sample.getLocationData().getLongitude());
        holder.txtCity.setText(sample.getLocationData().getCity());
        holder.txtState.setText(sample.getLocationData().getState());
        holder.txtCountry.setText(sample.getLocationData().getCountry());
        
        holder.txtDescp1.setText(p1.getDescription());
        holder.txtTitlep1.setText(p1.getTitle());
        holder.imagep1.setImageBitmap(Bitmap.createScaledBitmap(bm1, 120, 120, false));
//        bm1.recycle(); 
        
        holder.txtDescp2.setText(p2.getDescription());
        holder.txtTitlep2.setText(p2.getTitle());
        holder.imagep2.setImageBitmap(Bitmap.createScaledBitmap(bm2, 120, 120, false));
//        bm2.recycle();
        holder.txtDescp3.setText(p3.getDescription());
        holder.txtTitlep3.setText(p3.getTitle());
        holder.imagep3.setImageBitmap(Bitmap.createScaledBitmap(bm3, 120, 120, false));
//        bm3.recycle();
       
     
        System.gc();
        
       }
        return convertView;
	}
	public void onClick(View arg0) {
		Toast.makeText(context, "Item clicked", Toast.LENGTH_SHORT).show();

	}
}
