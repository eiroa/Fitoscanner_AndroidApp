package com.example.fitoscanner.activities;



import com.example.fitoscanner.R;
import com.example.fitoscanner.R.id;
import com.example.fitoscanner.R.layout;
import com.example.fitoscanner.R.menu;
import com.example.fitoscanner.helpers.TypefacesHelper;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class LoginActivity extends Activity implements OnClickListener{
	LoginActivity activity;
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.activity = this;
        TextView txtView1 = (TextView) findViewById(R.id.loginUserText);
        TextView txtView2 = (TextView) findViewById(R.id.loginPassText);
        
        Typeface font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
        txtView1.setTypeface(font);
        txtView2.setTypeface(font);
        Button bt = (Button) findViewById(R.id.loginButton);
        bt.setTypeface(font);
        
        bt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
            	//Toast.makeText(activity,"heyyy",Toast.LENGTH_SHORT).show();
        		
        		EditText txtView1 = (EditText) findViewById(R.id.userField);
        		EditText txtView2 = (EditText) findViewById(R.id.passField);
        		String user = (txtView1.getText()).toString();
                String pass = (txtView2.getText()).toString();
        		if(user.equals("admin")&&pass.equals("1234")){
        			Toast.makeText(activity.getApplicationContext(),"Se ha logueado correctamente",Toast.LENGTH_LONG).show();
        			Intent intent = new Intent(activity, MenuActivity.class);
        	        //intent.putExtra(GenericNames.CATEGORY_ID, categoryId);
        	        activity.startActivity(intent);
        		}else{
        			Toast.makeText(activity.getApplicationContext(),"Usuario y/o clave incorrecta",Toast.LENGTH_LONG).show();
        		}

            }
        });
        //
      
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        TextView txtView2 = (TextView) findViewById(R.id.loginPassText);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


	public void onClick(View v) {
		
		
		
	}


	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

   
}
