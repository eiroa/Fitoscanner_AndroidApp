package com.example.fitoscanner.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class SamplesDataSource {

	private SQLiteDatabase database;
	private FitoscannerSqLiteHelper dbHelper;
	private Context mContext;


	public SamplesDataSource(Context context) {
		this.mContext = context;
		dbHelper = new FitoscannerSqLiteHelper(context);		
	}

	
	
}
