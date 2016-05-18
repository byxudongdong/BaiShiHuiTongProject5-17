package com.example.baishihuitong;

import com.query.DaoJianQueryByTime;
import com.query.FajianQueryByTime;
import com.query.JiBaoQueryByTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class BusinessselectActivity extends Activity {

	public Button yetongselectbutton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.businessselect);
	    yetongselectbutton = (Button) findViewById(R.id.yetongselectbutton);
	    yetongselectbutton.setVisibility(View.GONE);
	}
	public void onclick(View v){
		switch (v.getId()) {
		case R.id.yetongselectbutton:
			startActivity(new Intent(BusinessselectActivity.this,TimeselectActivity.class));
			break;
		case R.id.faselectbutton:
			startActivity(new Intent(BusinessselectActivity.this,FajianQueryByTime.class));
			break;
		case R.id.daoselectbutton:
			startActivity(new Intent(BusinessselectActivity.this,DaoJianQueryByTime.class));
			break;
		case R.id.jiselectbutton:
			Intent mIntent=new Intent(BusinessselectActivity.this,JiBaoQueryByTime.class);
			startActivity(mIntent);
			break;
		case R.id.selectback_button:
			finish();
			break; 

		default:
			break;
		}
	}
	
       
}
