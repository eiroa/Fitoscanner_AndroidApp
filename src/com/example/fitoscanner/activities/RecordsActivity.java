package com.example.fitoscanner.activities;

import com.example.fitoscanner.R;
import com.example.fitoscanner.R.layout;
import com.example.fitoscanner.datasources.ImageDataSource;
import com.example.fitoscanner.datasources.SamplesDataSource;
import com.example.fitoscanner.helpers.TypefacesHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RecordsActivity extends Activity{
	ImageDataSource imageDataSource;
	SamplesDataSource samplesDatasource;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_layout);
       imageDataSource = new ImageDataSource(this);
       samplesDatasource = new SamplesDataSource(this);
        
    }
}
