package com.example.fitoscanner.helpers;

import java.util.List;

import com.example.fitoscanner.R;
import com.example.fitoscanner.model.Image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class CustomImageListViewAdapter extends ArrayAdapter<Image> {
 
    Context context;
 
    public CustomImageListViewAdapter(Context context, int resourceId,
            List<Image> items) {
        super(context, resourceId, items);
        this.context = context;
    }
     
    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
    }
     
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Image image = getItem(position);
         
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.samplepreview_fragment, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.secondLine);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.firstLine);
            holder.imageView = (ImageView) convertView.findViewById(R.id.photo);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
                 
        holder.txtDesc.setText(image.getDescription());
        holder.txtTitle.setText(image.getTitle());
        Bitmap bm =Base64Helper.decodeBase64(image.getBase64());

        holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bm, 120, 120, false));
        System.gc();
        return convertView;
    }
}
