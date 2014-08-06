package ar.edu.unq.fitoscanner.activities;



import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ar.edu.unq.fitoscanner.R;
import ar.edu.unq.fitoscanner.helpers.TypefacesHelper;

public class LoginActivity extends Activity implements OnClickListener {
	LoginActivity activity;
	public static boolean logged = false;

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
		Button btNoAuth = (Button) findViewById(R.id.loginNoAuthButton);
		bt.setTypeface(font);
		btNoAuth.setTypeface(font);

		bt.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				EditText userField = (EditText) findViewById(R.id.userField);
				EditText passField = (EditText) findViewById(R.id.passField);
				String user = (userField.getText()).toString();
				String pass = (passField.getText()).toString();
				if (user.equals("admin") && pass.equals("1234")) {
					logged = true;
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
						"Ha ingresado en modo prueba", Toast.LENGTH_SHORT)
						.show();

			}
		});
		//

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		logged = false;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (logged) {
			startMenu();
		}

	}

	private void startMenu() {

		Intent intent = new Intent(activity, MenuActivity.class);
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
