package com.delet.data;

import com.baishi.db.cn.DBHelper;
import com.baishi.db.cn.SaveShardMessage;
import com.example.baishihuitong.R;
import com.example.baishihuitong.SendscanActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DeleScanDataActivity extends Activity implements OnClickListener{
	public Button delet_fascanbutton,delet_daoscanbutton,delet_jiscanbutton,delet_back_button;
	public DBHelper dbHelper;
	private SQLiteDatabase database;
	public SaveShardMessage saveShard;
	private SharedPreferences mPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.deletdata_layout);
		findView();
		// 本地数据库
		mPreferences = getSharedPreferences("mode", MODE_PRIVATE);
		dbHelper = new DBHelper(this);
		database = dbHelper.getWritableDatabase();
		saveShard = new SaveShardMessage(database);
		
	}
	
	public void findView(){
		delet_fascanbutton = (Button) findViewById(R.id.delet_fascanbutton);
		delet_daoscanbutton = (Button) findViewById(R.id.delet_daoscanbutton);
		delet_jiscanbutton = (Button) findViewById(R.id.delet_jiscanbutton);
		delet_back_button = (Button) findViewById(R.id.delet_back_button);
		delet_fascanbutton.setOnClickListener(this);
		delet_daoscanbutton.setOnClickListener(this);
		delet_jiscanbutton.setOnClickListener(this);
		delet_back_button.setOnClickListener(this);
		
	}
	
	public void showDialog(Context context, final String msg) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(msg);
		builder.setTitle("询问");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (msg.equals("确认删除发件数据？")) {
					mPreferences.edit().putInt("unSend_count_msg_fajian", 0).commit();
					saveShard.deleTableFaJian();
				}else if(msg.equals("确认删除到件数据？")){
					mPreferences.edit().putInt("unSend_count_msg_dao", 0).commit();
					saveShard.deleTableDaoJian();
				}else if(msg.equals("确认删除集包数据？")){
					mPreferences.edit().putInt("unSend_count_msg_jibao", 0).commit();
					saveShard.deleTableJiBao();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.delet_fascanbutton:
			showDialog(DeleScanDataActivity.this, "确认删除发件数据？");
			break;
		case R.id.delet_daoscanbutton:
			showDialog(DeleScanDataActivity.this, "确认删除到件数据？");
			break;
		case R.id.delet_jiscanbutton:
			showDialog(DeleScanDataActivity.this, "确认删除集包数据？");
			break;
		case R.id.delet_back_button:
			DeleScanDataActivity.this.finish();
			break;
		}
	}
}
