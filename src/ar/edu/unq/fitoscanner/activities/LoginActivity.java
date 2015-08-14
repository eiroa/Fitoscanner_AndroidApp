package ar.edu.unq.fitoscanner.activities;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.data.ConfigurationSQLiteTable;
import ar.edu.unq.fitoscanner.data.FitoscannerSqLiteHelper;
import ar.edu.unq.fitoscanner.data.ImageSQLiteTable;
import ar.edu.unq.fitoscanner.datasources.ConfigurationDataSource;
import ar.edu.unq.fitoscanner.datasources.ImageDataSource;
import ar.edu.unq.fitoscanner.helpers.SecurityHelper;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;
import ar.edu.unq.fitoscanner.helpers.URLHelper;
import ar.edu.unq.fitoscanner.model.Configuration;
import ar.edu.unq.fitoscanner.model.Sample;
import ar.edu.unq.fitoscanner.services.GetTreatmentService;

@SuppressLint("NewApi")
public class LoginActivity extends Activity implements OnClickListener {
	LoginActivity activity;
	ConfigurationDataSource configurationDataSource;
	Configuration conf;
	public static final String TAG = "LoginActivity";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.activity = this;
		Intent test = new Intent(activity, GetTreatmentService.class);
		// Flag de publicidad se activará al instalarse la app, valor de lastSearch seteado en string vacio.
		activity.startService(test);
		configurationDataSource = new ConfigurationDataSource(this);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setConfiguration();
        
        checkDatabaseVersion();
        
//		describeView();
//		setDefaultIP();
	}
	
	/**
	 Metodo para actualizar cualquier cambio en la base. Deberia realizarse en un thread aparte
	 */
	public void checkDatabaseVersion(){
			FitoscannerSqLiteHelper fitoBase = new FitoscannerSqLiteHelper(activity);
			Integer currentVersion = fitoBase.getWritableDatabase().getVersion();
			if(currentVersion < FitoscannerSqLiteHelper.DATABASE_VERSION){
				//Hay que actualizar
				Log.d(TAG, "Current version of DB is "+currentVersion+" starting upgrade to "+FitoscannerSqLiteHelper.DATABASE_VERSION);
				fitoBase.onUpgrade(fitoBase.getWritableDatabase(), currentVersion, FitoscannerSqLiteHelper.DATABASE_VERSION);
			}else{
				Log.d(TAG, "Current version of DB is "+currentVersion+" . Database is up to date");
			}
	}
	
	
	private void describeView(){
		setContentView(R.layout.activity_login);
		TextView loginUserTxt = (TextView) findViewById(R.id.loginUserText);
		TextView loginPassTxt = (TextView) findViewById(R.id.loginPassText);
		
		final EditText loginUserField = (EditText) findViewById(R.id.loginUserField);
		final EditText loginPassField = (EditText) findViewById(R.id.loginPassField);
		loginUserField.setText("");
		Typeface font = TypefacesHelper.getTypeface(this, "fonts/optien.ttf");
		loginUserTxt.setTypeface(font);
		loginPassTxt.setTypeface(font);
		Button btLogin = (Button) findViewById(R.id.loginButton);
		Button btNoAuth = (Button) findViewById(R.id.loginNoAuthButton);
		Button btCreateUser = (Button)findViewById(R.id.createUserButton);
		btLogin.setTypeface(font);
		btNoAuth.setTypeface(font);
		btCreateUser.setTypeface(font);
		btLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				String user = (loginUserField.getText()).toString();
				String pass = (loginPassField.getText()).toString();
				if (user.equals(conf.getNick()) &&  SecurityHelper.toSHA256(pass).equals(conf.getPass())) {
					conf.setLogged(true);
					saveConfiguration();
					startMenu();
					Toast.makeText(activity.getApplicationContext(),
							"Se ha logueado correctamente", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(activity.getApplicationContext(),
							"Usuario y/o clave incorrecta", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		
		btNoAuth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				startMenu();
				Toast.makeText(activity.getApplicationContext(),
						"Ha ingresado sin registrarse", Toast.LENGTH_SHORT)
						.show();

			}
		});
		
		btCreateUser.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				startUserCreation();
			}
		});
		
		if(checkIfUserRegistered()){
			btNoAuth.setVisibility(View.GONE);
			btCreateUser.setVisibility(View.GONE);
		}else{
			btLogin.setVisibility(View.GONE);
			loginPassTxt.setVisibility(View.GONE);
			loginUserTxt.setVisibility(View.GONE);
			loginPassField.setVisibility(View.GONE);
			loginUserField.setVisibility(View.GONE);
		}
	}
	
	public void setDefaultIP(){
		configurationDataSource.open();
    	try
    	{	
			if(conf != null){
				conf.setIp(URLHelper.SERVER_ADDRESS);
			}else{
				conf = new Configuration(1, URLHelper.SERVER_ADDRESS,null,null,null,null, FitoscannerSqLiteHelper.DATABASE_VERSION,false);
			}
			configurationDataSource.doSaveConfiguration(conf);
    	}
    	finally{
    		configurationDataSource.close();
    	}
	}
	
	private void saveConfiguration(){
		configurationDataSource.open();
    	try
    	{	
			configurationDataSource.doSaveConfiguration(conf);
    	}
    	finally{
    		configurationDataSource.close();
    	}
	}
	
	private boolean isLogged(){
		Configuration confx = null;;
		configurationDataSource.open();
    	try
    	{	
			confx = configurationDataSource.getConfigurationById(1);
    	}
    	finally{
    		configurationDataSource.close();
    	}
    	if(confx == null){
    		return false;
    	}else{
    		return confx.isLogged();
    	}
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
	
	private Configuration getConfiguration(){
		Configuration conf  = null;
		configurationDataSource.open();
    	try
    	{	
			conf = configurationDataSource.getConfigurationById(1);
    	}
    	finally{
    		configurationDataSource.close();
    	}
    	return conf;
	}
	
	public boolean checkIfUserRegistered(){
		Configuration confx = getConfiguration();
    	return (conf.getNick() != null && !conf.getNick().equals(""));
	}
	
	public void showIP(){
		configurationDataSource.open();
    	try
    	{
    		Toast.makeText(getApplicationContext(), "Ip is " + conf.getIp(), 
    				Toast.LENGTH_SHORT).show();
    	}
    	finally{
    		configurationDataSource.close();
    	}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (isLogged()) {
			startMenu();
		}else{
			describeView();
		}
		
	}

	private void startMenu() {

		Intent intent = new Intent(activity, MenuActivity.class);
		activity.startActivity(intent);
	}
	
	private void startUserCreation(){
		Intent intent = new Intent(activity, NewUserActivity.class);
		activity.startActivity(intent);
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
