package com.example.baishihuitong;

import com.arrive.ArrivescanActivity;
import com.packages.PackscanActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class BusinessActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.business);
	}
	public void onclick(View v){
	     switch (v.getId()) {
		case R.id.yetongbutton:
			startActivity(new Intent(BusinessActivity.this,BusinessselectActivity.class));
			
			break;
		case R.id.fascanbutton:
			startActivity(new Intent(BusinessActivity.this,SendscanActivity.class));
			break;
		case R.id.daoscanbutton:
			startActivity(new Intent(BusinessActivity.this,ArrivescanActivity.class));
			break;
		case R.id.jiscanbutton:
			Intent pIntent=new Intent(BusinessActivity.this,PackscanActivity.class);
			startActivity(pIntent);
			break;
		case R.id.businessback_button:
			finish();
			break;

		default:
			break;
		}	
	}
	
    
}
