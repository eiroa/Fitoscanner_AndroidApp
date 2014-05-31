package com.example.fitoscanner;

import com.example.fitoscanner.helpers.TypefacesHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity {
	MenuActivity activity;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        this.activity = this;
        TextView txtView1 = (TextView) findViewById(R.id.textView1);
        TextView txtView2 = (TextView) findViewById(R.id.textView2);
        Button bt = (Button) findViewById(R.id.buttonMenuLogout);
        Typeface font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
        txtView1.setTypeface(font);
        txtView2.setTypeface(font);
        bt.setTypeface(font);
        bt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {       		
        		activity.finish();
            	Toast.makeText(activity.getApplicationContext(),"Se ha desconectado",Toast.LENGTH_LONG).show();       		
            }
        });
        
        ImageView arrowRegisters = (ImageView)findViewById(R.id.imMenuArrowRegisters);
        arrowRegisters.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
            	Intent intent = new Intent(activity, RecordsActivity.class);
        		activity.startActivity(intent);      		
            }
        });
        ImageView arrowTakePic = (ImageView)findViewById(R.id.imMenuArrowTakePic);
        arrowTakePic.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
            	Intent intent = new Intent(activity, MakePhotoActivity.class);
        		activity.startActivity(intent);      		
            }
        });
    }
}
