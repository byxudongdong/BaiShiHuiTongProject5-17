package com.query;

import sendupSet.TimeQueryData;

import com.baishi.db.cn.DBHelper;
import com.example.baishihuitong.R;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class JiBaoQueryByTime extends Activity implements OnClickListener{
	public Button fajian_query_today,fajian_query_yesterday,
	fajian_query_befor_yesterday,fajian_query_every_day,fajian_selectback_button;
	public TimeQueryData timeQuery;
	public DBHelper dbHelper;
	private SQLiteDatabase database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fajian_query_time);
		init();
		dbHelper = new DBHelper(this);
		database = dbHelper.getWritableDatabase();
		timeQuery = new TimeQueryData(database);
	}
	public void init(){
		fajian_query_today = (Button) findViewById(R.id.fajian_query_today);
		fajian_query_yesterday = (Button) findViewById(R.id.fajian_query_yesterday);
		fajian_query_befor_yesterday = (Button) findViewById(R.id.fajian_query_befor_yesterday);
		fajian_query_every_day = (Button) findViewById(R.id.fajian_query_every_day);
		fajian_selectback_button = (Button) findViewById(R.id.fajian_selectback_button);
		
		fajian_query_today.setOnClickListener(this);
		fajian_query_yesterday.setOnClickListener(this);
		fajian_query_befor_yesterday.setOnClickListener(this);
		fajian_query_every_day.setOnClickListener(this);
		fajian_selectback_button.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.fajian_selectback_button:
			finish();
			break;
		case R.id.fajian_query_today:
			Intent today_intent = new Intent(JiBaoQueryByTime.this, JiBaoQueryBytoday.class);
			today_intent.putExtra("key_intent", "today_intent");
			startActivity(today_intent);
			break;
		case R.id.fajian_query_yesterday:
			Intent yesterday_intent = new Intent(JiBaoQueryByTime.this, JiBaoQueryBytoday.class);
			yesterday_intent.putExtra("key_intent", "yesterday_intent");
			startActivity(yesterday_intent);
			break;
		case R.id.fajian_query_befor_yesterday:
			Intent befor_yesterday_intent = new Intent(JiBaoQueryByTime.this, JiBaoQueryBytoday.class);
			befor_yesterday_intent.putExtra("key_intent", "befor_yesterday_intent");
			startActivity(befor_yesterday_intent);
			break;
		case R.id.fajian_query_every_day:
			Intent every_day_intent = new Intent(JiBaoQueryByTime.this, SearchJiBao_Every_dayActivity.class);
			every_day_intent.putExtra("key_intent", "befor_yesterday_intent");
			startActivity(every_day_intent);
			break;
		}
	}

}
