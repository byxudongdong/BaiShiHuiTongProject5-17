package com.example.baishihuitong;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class TimeselectActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.timeselect);
	}
	public void onclick(View v){
		switch (v.getId()) {
		case R.id.currentdaybutton:
			Toast.makeText(this, "此功能暂未实现！", Toast.LENGTH_SHORT).show();
			break;
		case R.id.yestertdaybutton:
			Toast.makeText(this, "此功能暂未实现！", Toast.LENGTH_SHORT).show();
			break;
		case R.id.beforedaybutton:
			Toast.makeText(this, "此功能暂未实现！", Toast.LENGTH_SHORT).show();
			break;
		case R.id.anydaybutton:
			Toast.makeText(this, "此功能暂未实现！", Toast.LENGTH_SHORT).show();
			break;
		case R.id.selecttimeback_button:
			finish();
			break;

		default:
			break;
		}
	}
	

}
