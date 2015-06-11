package ar.edu.unq.fitoscanner.activities;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;

@SuppressLint("NewApi")
public class RecordsMenuActivity extends Activity {
	RecordsMenuActivity activity;
	final Context context = this;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_menu_layout);
        this.activity = this;
        Typeface font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
        
        Button goRecords = (Button)findViewById(R.id.buttonOpenNewRecords);
        goRecords.setOnClickListener(new View.OnClickListener() {
        	
        	public void onClick(View arg0) {
            	Intent intent = new Intent(activity, RecordsActivity.class);
            	intent.putExtra("getSent", false);
            	intent.putExtra("getResolved", false);
            	intent.putExtra("getValid", true);
        		activity.startActivity(intent);      		
            }
        	
            
        });
        Button goSentRecords = (Button)findViewById(R.id.buttonOpenSentRecords);
        goSentRecords.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {
            	Intent intent = new Intent(activity, RecordsActivity.class);
            	intent.putExtra("getSent", true);
            	intent.putExtra("getResolved", false);
            	intent.putExtra("getValid", true);
        		activity.startActivity(intent);      		
            }
        });
        
        Button goResolvedRecords = (Button)findViewById(R.id.buttonOpenResolvedRecords);
        goResolvedRecords.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {
        		
        		//Abrir resolucion de tratamiento.Muchachos, este es el ultimo paso. Lets go.
        		
            	Intent intent = new Intent(activity, ResolvedSamples.class);
//            	intent.putExtra("getSent", true);
//            	intent.putExtra("getResolved", true);
//            	intent.putExtra("getValid", true);
        		activity.startActivity(intent);      		
            }
        });
        
        Button goFailedRecords = (Button)findViewById(R.id.buttonOpenUnresolvedRecords);
        goFailedRecords.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {
            	Intent intent = new Intent(activity, RecordsActivity.class);
            	intent.putExtra("getSent", true);
            	intent.putExtra("getResolved", false);
            	intent.putExtra("getValid", false);
        		activity.startActivity(intent);      		
            }
        });
        
        goRecords.setTypeface(font);
        goSentRecords.setTypeface(font);
        goResolvedRecords.setTypeface(font);
        goFailedRecords.setTypeface(font);
	}
}
