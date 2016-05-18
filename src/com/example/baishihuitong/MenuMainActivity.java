package com.example.baishihuitong;

import com.delet.data.DeleScanDataActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MenuMainActivity extends Activity implements OnClickListener{
	public Button yewubutton, delectscanbutton,setingbutton,Shuttle_bus_back_button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mainmenu_activity);
		find_view();
	}
	public void find_view(){
		yewubutton = (Button) findViewById(R.id.yewubutton);
		delectscanbutton = (Button) findViewById(R.id.delectscanbutton);
		setingbutton = (Button) findViewById(R.id.setingbutton);
		Shuttle_bus_back_button = (Button) findViewById(R.id.Shuttle_bus_back_button);
		yewubutton.setOnClickListener(this);
		delectscanbutton.setOnClickListener(this);
		setingbutton.setOnClickListener(this);
		Shuttle_bus_back_button.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.yewubutton:
			Intent yIntent = new Intent(MenuMainActivity.this,BusinessActivity.class);
			startActivity(yIntent);
			break;
		case R.id.delectscanbutton:
			Intent delectscan = new Intent(MenuMainActivity.this,DeleScanDataActivity.class);
			startActivity(delectscan);
			break;
		case R.id.setingbutton:
			startActivity(new Intent(MenuMainActivity.this,SettingActivity.class));
			break;
		case R.id.Shuttle_bus_back_button:
			//finish();
			AlertDialog.Builder builder=new Builder(MenuMainActivity.this);
			builder.setMessage("确认返回登录界面？");
			builder.setTitle("询问");
			builder.setPositiveButton("确认",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					MenuMainActivity.this.finish();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			break;
		}
	}
}
