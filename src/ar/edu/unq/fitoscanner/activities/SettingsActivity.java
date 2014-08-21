package ar.edu.unq.fitoscanner.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import ar.edu.unq.fitoscanner.R;

public class SettingsActivity extends PreferenceActivity{
 
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences);
	    }

}
