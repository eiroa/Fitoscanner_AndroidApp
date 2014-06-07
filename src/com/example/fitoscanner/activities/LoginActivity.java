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
import android.os.Looper;
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
		bt.setTypeface(font);

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
							"Se ha logueado correctamente", Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(activity.getApplicationContext(),
							"Usuario y/o clave incorrecta", Toast.LENGTH_LONG)
							.show();
				}

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
