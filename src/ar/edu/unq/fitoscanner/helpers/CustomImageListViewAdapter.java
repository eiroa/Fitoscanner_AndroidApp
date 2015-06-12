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
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.model.Image;
 
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
        
         
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.samplepreview_fragment, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.secondLine);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.firstLine);
            holder.imageView = (ImageView) convertView.findViewById(R.id.photo);
            convertView.setTag(holder);
        }else{
        	holder = (ViewHolder) convertView.getTag();
        }
        
        Image image = getItem(position);
        if(image != null){
        	holder.txtDesc.setText(image.getDescription());
            holder.txtTitle.setText(image.getTitle());
            //Optimizacion critica de memoria. Se muestra en el listView una imagen muy reducida respecto a la original
            // de esta manera se ahorra mucha memoria.
            // Dispositivos con poca memoria beneficiados y dispositivos de alta gama con camaras muy poderosas tambien
            //se benefician puesto que de esta manera no tienen que mostrar una imagen de muchos mpx.
            Bitmap bmp =Base64Helper.decodeScaledBase64(image.getBase64(),150,150);
            holder.imageView.setImageBitmap(bmp);
            
        }
        System.gc();
        return convertView;
     }
}
