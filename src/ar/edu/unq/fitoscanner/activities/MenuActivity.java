package ar.edu.unq.fitoscanner.activities;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;

public class MenuActivity extends Activity {
	//
	MenuActivity activity;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        this.activity = this;
        
        Button bt = (Button) findViewById(R.id.buttonMenuLogout);
        Typeface font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");

        bt.setTypeface(font);
        bt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {       		
        		activity.finish();
        		LoginActivity.logged = false;
            	Toast.makeText(activity.getApplicationContext(),"Se ha desconectado",Toast.LENGTH_LONG).show();       		
            }
        });
        
        Button goTakePic = (Button)findViewById(R.id.buttonOpenTakePic);
        goTakePic.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {
            	Intent intent = new Intent(activity, MakePhotoActivity.class);
        		activity.startActivity(intent);      		
            }
            
        });
        goTakePic.setTypeface(font);
        Button goRecords = (Button)findViewById(R.id.buttonOpenRecords);
        goRecords.setOnClickListener(new View.OnClickListener() {
        	
        	public void onClick(View arg0) {
            	Intent intent = new Intent(activity, RecordsActivity.class);
        		activity.startActivity(intent);      		
            }
        	
            
        });
        goRecords.setTypeface(font);
    }
}
