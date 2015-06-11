package ar.edu.unq.fitoscanner.activities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.datasources.ConfigurationDataSource;
import ar.edu.unq.fitoscanner.helpers.SecurityHelper;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;
import ar.edu.unq.fitoscanner.helpers.URLHelper;
import ar.edu.unq.fitoscanner.model.Configuration;
import ar.edu.unq.fitoscanner.model.Image;

public class NewUserActivity extends Activity {
	private final Context context = this;
	ConfigurationDataSource configurationDataSource;
	Configuration conf;
	EditText userNickField;
	EditText userPassField;
	EditText userRepeatPassField;
	EditText userNameField;
	EditText userSurnameField;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user_layout);
		configurationDataSource = new ConfigurationDataSource(this);
		setConfiguration();
		describeView();
		
	}
	
	private void describeView(){
		TextView userNickTxt = (TextView) findViewById(R.id.newUserNickText);
		TextView userPassTxt = (TextView) findViewById(R.id.newUserPassText);
		TextView userRepeatPassTxt = (TextView) findViewById(R.id.newUserRepeatPassText);
		TextView userNameTxt = (TextView) findViewById(R.id.newUserNameText);
		TextView userSurnameTxt = (TextView) findViewById(R.id.newUserSurnameText);
		
		userNickField= (EditText) findViewById(R.id.newUserNickField);
		userPassField = (EditText) findViewById(R.id.newUserPassField);
		userRepeatPassField= (EditText) findViewById(R.id.newUserRepeatPassField);
		userNameField = (EditText) findViewById(R.id.newUserNameField);
		userSurnameField = (EditText) findViewById(R.id.newUserSurnameField);

		Typeface font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
		userNickTxt.setTypeface(font);
		userPassTxt.setTypeface(font);
		userRepeatPassTxt.setTypeface(font);
		userNameTxt.setTypeface(font);
		userSurnameTxt.setTypeface(font);
		
		Button btCreateUser = (Button) findViewById(R.id.buttonNewUserRegister);
		btCreateUser.setTypeface(font);
		btCreateUser.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if( ! (userPassField.getText().toString().equals(userRepeatPassField.getText().toString()) )){
					Toast.makeText(context,"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();
					return;
				}
				if(!validatefields()){
					Toast.makeText(context,"Debe completar todos los campos",Toast.LENGTH_SHORT).show();
					return;
				}
				
				final HttpParams httpParameters = new BasicHttpParams();
				HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
				HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
	        	HttpClient httpclient = new DefaultHttpClient(httpParameters);
	    		HttpPost httppost = null;
	    		
	    		String detailUrl ="";
	        	try {
	        		// matchear contra el metodo en el servidor para guardar usuario
	        		// falta campo anonimo y codigo
	        		// donde codigo es simplemente para datos del telefono
	        		// IMEI-VERSION_ANDROID-NOMBRE_DISPOSITIVO
	    			   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
	    		       TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
	    		       
	    		       nameValuePairs.add(new BasicNameValuePair("imei", mngr.getDeviceId()));
	    		       nameValuePairs.add(new BasicNameValuePair("anon", "false"));
	    		       nameValuePairs.add(new BasicNameValuePair("nick", userNickField.getText().toString()));
	    		       nameValuePairs.add(new BasicNameValuePair("ps",  SecurityHelper.toSHA256(userPassField.getText().toString())));
	    		       nameValuePairs.add(new BasicNameValuePair("name",  userNameField.getText().toString()));
	    		       nameValuePairs.add(new BasicNameValuePair("surname",  userSurnameField.getText().toString()));
	    		       nameValuePairs.add(new BasicNameValuePair("code",  mngr.getDeviceId()+'-'+Build.VERSION.RELEASE.toString()+'-'+android.os.Build.MODEL));
	    		       
	    		       String url; 
	    		       try{
	    		    	   url = conf.getIp();
	    		       }catch(NullPointerException ne){
	    		    	   Log.d("New user Activity", "WARNING: No configuration present, using default server address");
	    		    	   url = URLHelper.SERVER_ADDRESS;
	    		       }
	    		       detailUrl="/user/save";
	    		       httppost = new HttpPost(url+detailUrl);
	    				httppost.setHeader("Content-Type",
	    		                "application/x-www-form-urlencoded;charset=UTF-8");
	    		       
	    		       httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    		   	   httpclient.execute(httppost);
	    		   	Toast.makeText(context,"Usuario registrado, a partir de ahora deberá ingresar usuario y clave",Toast.LENGTH_LONG).show();
					if(conf==null){
						conf = new Configuration();
					}
					conf.setIp(URLHelper.SERVER_ADDRESS);
					conf.setId(1);
	    		   	conf.setName(userNameField.getText().toString());
					conf.setNick(userNickField.getText().toString());
					conf.setPass(SecurityHelper.toSHA256(userPassField.getText().toString()));
					conf.setSurname(userSurnameField.getText().toString());
					saveConfiguration(conf);
					LoginActivity.logged = true;
	    		}catch (HttpHostConnectException e) {
	    			Log.d("New User Activity", "WARNING: Server address from database unreacheable, using default address");
	    			try {
	    				httppost = new HttpPost(URLHelper.SERVER_ADDRESS+detailUrl);
	    				httpclient.execute(httppost);
	    				Toast.makeText(context,"Usuario registrado, a partir de ahora deberá ingresar usuario y clave",Toast.LENGTH_LONG).show();
	    				conf.setName(userNameField.getText().toString());
	    				conf.setNick(userNickField.getText().toString());
	    				conf.setPass(SecurityHelper.toSHA256(userPassField.getText().toString()));
	    				conf.setSurname(userSurnameField.getText().toString());
	    				saveConfiguration(conf);
	    				LoginActivity.logged = true;
	    				
//	    				Toast.makeText(context,"Usuario registrado, para ingresar deberá utilizar nick y clave",Toast.LENGTH_LONG).show();
	    			} catch (Exception ex) {
	    				Toast.makeText(context,"Error al intentar registrarse en el servidor, verifique su conexión",Toast.LENGTH_SHORT).show();
	    				ex.printStackTrace();
	    			}
	    		} catch (ClientProtocolException e) {
	    			e.printStackTrace();
	    			Toast.makeText(context,"Error al enviar la muestra, "
	    					+ "verifique su conexión a internet",Toast.LENGTH_SHORT).show();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}catch (Exception e) {
	    			e.printStackTrace();
	    			Toast.makeText(context,"Error grave y desconocido al intentar registrarse... "
	    					+ " se recomiendo reinstalar aplicación con última versión",Toast.LENGTH_SHORT).show();
	    		}
	        	
			}
		});
	}
	
	private Boolean validatefields(){
		return fieldNotNull(userNickField) &&
				fieldNotNull(userPassField) &&
				fieldNotNull(userRepeatPassField) &&
				fieldNotNull(userNameField) &&
				fieldNotNull(userSurnameField);
				
	}
	
	private Boolean fieldNotNull(EditText et){
		return et.getText().toString() != null && !et.getText().toString().equals("") ;
	}
	
	private void registerUser(){
		
	}
	
	private void setConfiguration(){
		configurationDataSource.open();
    	try
    	{	
			conf = configurationDataSource.getConfigurationById(1);
    	}
    	finally{
    		configurationDataSource.close();
    	}
	}
	
	private void saveConfiguration(Configuration conf){
		configurationDataSource.open();
    	try
    	{	
			configurationDataSource.doSaveConfiguration(conf);
    	}
    	finally{
    		configurationDataSource.close();
    	}
    	this.finish();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		View v =findViewById(R.layout.activity_login);
		v.invalidate();
		v.requestLayout();
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	
	
	

}
