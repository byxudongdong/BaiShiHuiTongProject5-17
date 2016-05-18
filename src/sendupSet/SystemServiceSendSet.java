package sendupSet;

import java.util.ArrayList;

import com.baishi.db.cn.SaveShardMessage;
import com.bluetooth.Util;
import com.example.baishihuitong.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SystemServiceSendSet extends Activity implements OnClickListener {
	public EditText system_service_send_edit;
	public TextView service_return_button, service_sendupset_btn;
	public SaveShardMessage saveShard;
	private ListView popUp_window;
	// 退出时间
	private long currentBackPressedTime = 0;
	// 退出间隔
	private static final int BACK_PRESSED_INTERVAL = 2000;
	private InputMethodManager mIM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.system_service_send_set);
		mIM = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		findView();
		saveShard = new SaveShardMessage(SystemServiceSendSet.this);
		String http_url = saveShard
				.getsystem_service_flag(SystemServiceSendSet.this);
		setListUser(http_url);
		system_service_send_edit.setInputType(InputType.TYPE_NULL);
		system_service_send_edit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
					currentBackPressedTime = System.currentTimeMillis();
					showPopupWindow(system_service_send_edit);
				} else {
					system_service_send_edit.setSelection(system_service_send_edit.getText().toString().length());
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
					mIM.showSoftInput(system_service_send_edit,InputMethodManager.SHOW_FORCED);
					
				}
			}
		});
	}

	public void findView() {
		system_service_send_edit = (EditText) findViewById(R.id.system_service_send_edit);
		service_return_button = (TextView) findViewById(R.id.service_return_button);
		service_sendupset_btn = (TextView) findViewById(R.id.service_sendupset_btn);
		service_return_button.setOnClickListener(this);
		service_sendupset_btn.setOnClickListener(this);
	}

	private ArrayList<String> user_name = new ArrayList<String>();
	private ArrayList<String> userInfoList;

	public void setListUser(String text) {
		user_name.clear();
		userInfoList = Util.getLocalUserInfoList(this);
		system_service_send_edit.setText(text);
		int n = userInfoList.size();

		for (int i = n - 1; i >= 0; i--) {
			user_name.add(userInfoList.get(i));
		}
	}

	public boolean checkUserMessage() {
		String name = system_service_send_edit.getText().toString();
		userInfoList = Util.getLocalUserInfoList(this);
		if (userInfoList.size() > 0) {
			for (int i = userInfoList.size() - 1; i >= 0; i--) {
				if (userInfoList.get(i).equals(name)) {
					Util.USER_NAME = userInfoList.get(i);
					return false;
				}
			}
		}
		return true;
	}

	public void saveUserMessage() {
		SharedPreferences settings = SystemServiceSendSet.this
				.getSharedPreferences("setting", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("name_" + Util.userNum, system_service_send_edit
				.getText().toString());
		Util.userNum++;
		editor.putInt("userNum", Util.userNum); // 更新用户数
		editor.commit();
	}

	private void showPopupWindow(View view) {
		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(SystemServiceSendSet.this)
				.inflate(R.layout.pop_window, null);
		// 设置按钮的点击事件
		popUp_window = (ListView) contentView.findViewById(R.id.popUp_window);
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

		popUp_window.setAdapter(new ArrayAdapter<String>(SystemServiceSendSet.this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				user_name));
		popUp_window.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				system_service_send_edit
						.setText(user_name.get(arg2).split("—")[0]);
				popupWindow.dismiss();
			}
		});

		popupWindow.setTouchable(true);
		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.b));
		// 设置好参数之后再show
		popupWindow.showAsDropDown(view);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.service_return_button:
			finish();
			break;
		case R.id.service_sendupset_btn:
			ShowDialog("保存设置信息");
			break;
		}
	}

	public void ShowDialog(String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				SystemServiceSendSet.this);
		builder.setCancelable(false);
		builder.setMessage("确认" + message + "?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				saveShard.Sharedsystem_service_flag(system_service_send_edit
						.getText().toString());
				if(checkUserMessage()){
					saveUserMessage();
				}
				
				finish();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}
}
