package com.example.baishihuitong;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import sendupSet.JsonToString;
import sendupSet.PrivateListingAdapter;
import sendupSet.TimeQueryData;
import sendupSet.WeakUPScreen;
import userMessage.SiteCodeMessage;
import userMessage.XDeviceInfo;

import com.baishi.db.cn.DBHelper;
import com.baishi.db.cn.FaJianSQLite;
import com.baishi.db.cn.GetSaveDabaseSQ;
import com.baishi.db.cn.MyNative;
import com.baishi.db.cn.SaveShardMessage;
import com.baishihuitong.untils.HttpUrl;
import com.baishihuitong.untils.ListViewCompat;
import com.bluetooth.BluetoothLeService;
import com.google.gson.Gson;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SendscanActivity extends Activity {
	public EditText nextname;// 下一站
	public static String NEXT_SITECODE = "";
	public EditText workorder;// 任务单
	public EditText waybillnumber;// 运单号
	TextView verify, send_number_count;// 验证
	TextView nolocal,upload_text_flag;// 未锁
	Button fanhui;// 返回
	Button delect;// 删除
	Button queding;// 确定
	public String numberString;
	private SharedPreferences mPreferences;

	private ListViewCompat listView;
	public MyNative myNative;
	public boolean is_sending_up = false;
	public int is_sending_up_mub_count = 0;
	public int unSend_count_msg = 0;
	public int current_all_number = 0;
	public static Toast toast = null;
	public String WORK_PRE = "";
	/**
	 * 广播接收器
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				String EXTRA_DATA = intent
						.getStringExtra(BluetoothLeService.EXTRA_DATA);
				if (EXTRA_DATA.length() > 0) {
					numberString = EXTRA_DATA;
					if (nextname.isFocused()) {
						check_site_code(numberString);
					} else if (workorder.isFocused()) {
						if (check_sitecode_flag && show_dialog_ing) {
							if (myNative.OpCom_CheckBarcode(numberString, 1) == 1 ) {
								// 任务单
								WeakUPScreen
										.onScan(SendscanActivity.this);
								play_beep_sound.playBeepSoundAndVibrate(0);
								if (workorder.getText().toString().length() == 8
										&& !numberString.equals(workorder
												.getText().toString())) {
									show_dialog_ing = false;
									WORK_PRE = workorder.getText().toString();
									showDialog(SendscanActivity.this,
											"确认更改任务单？");
								} else {
									dialog_work_click_button();
								}
							} else {
								play_beep_sound.playBeepSoundAndVibrate(1);
								showTextToast("任务单不符合规则");
							}
						}
					} else if (myNative.OpCom_CheckBarcode(numberString, 3) == 1) {
						// 运单号
						if (check_sitecode_flag) {
							waybillnumber.setText(numberString);
							if (myNative.OpCom_CheckBarcode(workorder.getText()
									.toString(), 1) == 1) {
								waybillnumber.requestFocus();
								saveDabaseFaJian();
							} else {
								play_beep_sound.playBeepSoundAndVibrate(1);
								showTextToast("任务单错误");
							}
						}
					} else {
						play_beep_sound.playBeepSoundAndVibrate(1);
						showTextToast("条码不符合规则");
					}
				}
			}
		}
	};

	public void check_site_code(String station_name) {
		if (show_dialog_ing) {
			check_sitecode_flag = false;
			check_sitecode_success = false;
			if (myNative.OpCom_CheckBarcode(nextname.getText().toString(), 2) != 1
					&& NEXT_SITECODE.length() == 6
					&& !station_name.equals(NEXT_SITECODE)
					&& nextname.getText().toString().length() > 0) {
				nextname.setText(station_name);
				NEXT_SITECODE = station_name;
				show_dialog_ing = false;
				play_beep_sound.playBeepSoundAndVibrate(0);
				showDialog(SendscanActivity.this, "确认更改站点？");
			} else {
				nextname.setText(station_name);
				NEXT_SITECODE = station_name;
				dialog_next_click_button(station_name);
			}
		}
	}

	private void showTextToast(String msg) {
		TextView text = new TextView(this);
		text.setTextSize(24);
		text.setPadding(10, 5, 10, 5);
		text.setTextColor(Color.rgb(255, 255, 255));
		text.setBackgroundColor(Color.rgb(50, 50, 50));
		text.setText(msg);
		toast.setView(text);
		toast.setGravity(Gravity.CENTER, 0, 40);
		toast.show();
	}

	public SaveShardMessage saveShard;
	public DBHelper dbHelper;
	private SQLiteDatabase database;
	int num_count_limit, num_count_time;
	public LinearLayout dialog_progress_line;
	public PlayBeepSound play_beep_sound;
	private InputMethodManager mIM;
	public final int Get_SQLite_count = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sendscan);
		WeakUPScreen.oncreate(SendscanActivity.this);
		mIM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		init();
		if (toast == null) {
			toast = new Toast(getApplicationContext());
		}
		play_beep_sound = new PlayBeepSound(SendscanActivity.this);
		myNative = new MyNative();
		// 本地数据库
		dbHelper = new DBHelper(this);
		database = dbHelper.getWritableDatabase();
		saveShard = new SaveShardMessage(database);
		timeQuery = new TimeQueryData(database);
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		num_count_limit = SaveShardMessage
				.getchange_count_flag(SendscanActivity.this);
		num_count_time = SaveShardMessage
				.getchange_time_flag(SendscanActivity.this);
		mPreferences = getSharedPreferences("mode", MODE_PRIVATE);
		unSend_count_msg = mPreferences.getInt("unSend_count_msg_fajian", 0);
		getSQLite = new GetSaveDabaseSQ(database);
		current_all_number = (int) saveShard.getCount("发件");
		ArrayList<FaJianSQLite> fajianlist = getFajianlist(current_all_number,
				Get_SQLite_count);
		adapter = new PrivateListingAdapter(SendscanActivity.this, fajianlist);
		listView.setAdapter(adapter);
		sending_count_n = unSend_count_msg;
		adapter.setNowSending(sending_count_n);
		send_number_count.setText(unSend_count_msg + "票" + "/"
				+ current_all_number + "票");
		check_sleep_time();
		setViewOnClick();
		mListViewRefresh();
	}

	boolean LOAD_MORE_FLAGE = true, LOAD_MORE_END = true;
	int number_index;

	// 列表刷新
	public void mListViewRefresh() {
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(true);
		listView.setXListViewListener(new ListViewCompat.IXListViewListener() {
			public void onRefresh() {
				listView.setRefreshTime(getCurrentTime("yyyy-MM-dd HH:mm:ss"));
				LOAD_MORE_FLAGE = true;
				LOAD_MORE_END = true;
				mGetSaveDabaseSQ(false, (int) saveShard.getCount("发件"));
			}

			public void onLoadMore() {
				if (LOAD_MORE_FLAGE && current_all_number > 3) {
					number_index = current_all_number - 3;
					LOAD_MORE_FLAGE = false;
				} else if (LOAD_MORE_FLAGE && current_all_number < 3) {
					number_index = 0;
					LOAD_MORE_FLAGE = false;
				}
				if (LOAD_MORE_END) {
					if (number_index <= 0) {
						LOAD_MORE_END = false;
					} else {
						mGetSaveDabaseSQ(true, number_index);
					}
				}
				if (number_index < 0) {
					Toast.makeText(SendscanActivity.this, "已加载全部数据", 0).show();
				}
				number_index -= 3;
				listView.stopRefresh();
				listView.stopLoadMore();
			}
		});
	}

	// 退出时间
	private long currentBackPressedTime = 0;
	// 退出间隔
	private static final int BACK_PRESSED_INTERVAL = 2000;

	public void setViewOnClick() {
		nextname.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if (s.length() > 6
						&& myNative.OpCom_CheckBarcode(s.toString(), 2) == 1) {
					play_beep_sound.playBeepSoundAndVibrate(1);
					showTextToast("只能输入6位数字");
				}
			}
		});

		nextname.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					workorder.requestFocus();
				}
				return false;
			}
		});
		nextname.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					nextname.setBackgroundColor(Color.rgb(00, 119, 153));
					workorder.setBackgroundColor(Color.rgb(255, 255, 255));
					waybillnumber.setBackgroundColor(Color.rgb(255, 255, 255));
				}
			}
		});
		nextname.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
					currentBackPressedTime = System.currentTimeMillis();
				} else {
					nextname.setSelection(nextname.getText().toString()
							.length());
					getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
					mIM.showSoftInput(nextname, InputMethodManager.SHOW_FORCED);
				}
			}
		});
		workorder.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if(s.length() > 6 && s.length() <= 8){
					play_beep_sound.playBeepSoundAndVibrate(1);
					showTextToast("是否输入6位纯数字");
				}else if (s.length() > 8) {
					play_beep_sound.playBeepSoundAndVibrate(1);
					showTextToast("任务单长度限制8位");
				}
			}
		});
		workorder.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
					currentBackPressedTime = System.currentTimeMillis();
				} else {
					getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
					workorder.setSelection(workorder.getText().toString()
							.length());
					mIM.showSoftInput(workorder, InputMethodManager.SHOW_FORCED);
				}
			}
		});
		workorder.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					workorder.setBackgroundColor(Color.rgb(00, 119, 153));
					nextname.setBackgroundColor(Color.rgb(255, 255, 255));
					waybillnumber.setBackgroundColor(Color.rgb(255, 255, 255));
				}
			}
		});
		workorder.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					if (workorder.isFocused()
							&& workorder.getText().toString().length() > 0) {
						waybillnumber.requestFocus();
					}
				}
				return false;
			}
		});

		waybillnumber.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if (s.length() > 14) {
					play_beep_sound.playBeepSoundAndVibrate(1);
					showTextToast("只能输入少于14位数字");
				}
			}
		});

		waybillnumber.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					workorder.setBackgroundColor(Color.rgb(255, 255, 255));
					nextname.setBackgroundColor(Color.rgb(255, 255, 255));
					waybillnumber.setBackgroundColor(Color.rgb(00, 119, 153));
				}
			}
		});
		waybillnumber.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
					currentBackPressedTime = System.currentTimeMillis();
				} else {
					getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
					waybillnumber.setSelection(waybillnumber.getText()
							.toString().length());
					mIM.showSoftInput(waybillnumber,
							InputMethodManager.SHOW_FORCED);
				}
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int index = (Integer) parent.getItemAtPosition(position);
				Message msg = new Message();
				msg.what = 0x003;
				msg.arg1 = index;
				msg.arg2 = position;
				handler.sendMessage(msg);
			}
		});

		fanhui.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(SendscanActivity.this, "确认退出当前界面？");
			}
		});

		verify.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (myNative.OpCom_CheckBarcode(nextname.getText().toString(),
						2) == 1) {
					NEXT_SITECODE = nextname.getText().toString();
					check_sitecode();
				} else {
					play_beep_sound.playBeepSoundAndVibrate(1);
					showTextToast("站点为6位数字");
				}
			}
		});

		delect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (index_dele > 0 && position > 0) {
					if (position <= unSend_count_msg) {
						delet_dbHelp(index_dele);
						index_dele = -1;
						position = -1;
					} else {
						play_beep_sound.playBeepSoundAndVibrate(1);
						index_dele = -1;
						position = -1;
						showTextToast("已上传数据不能删除");
					}
				} else if (unSend_count_msg > 0) {
					int postiont = (Integer) adapter.getItem(0);
					delet_dbHelp(postiont);
				} else if (unSend_count_msg == 0 && index_dele < 0) {
					play_beep_sound.playBeepSoundAndVibrate(1);
					showTextToast("已上传数据不能删除");
				}
			}
		});
		queding.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (check_sitecode_flag && check_sitecode_success) {
					if (myNative.OpCom_CheckBarcode(nextname.getText()
							.toString(), 2) != 1) {
						if (myNative.OpCom_CheckBarcode(workorder.getText()
								.toString(), 1) == 1) {
							if (myNative.OpCom_CheckBarcode(waybillnumber
									.getText().toString(), 3) == 1) {
								saveDabaseFaJian();
								mGetSaveDabaseSQ(false,
										(int) saveShard.getCount("发件"));
								if (!is_sending_up
										&& unSend_count_msg >= num_count_limit) {
									send_FaJian_parames(1);
								}
							} else {
								play_beep_sound.playBeepSoundAndVibrate(1);
								showTextToast("运单号错误");
							}
						} else {
							play_beep_sound.playBeepSoundAndVibrate(1);
							showTextToast("任务单错误");
						}
					} else {
						play_beep_sound.playBeepSoundAndVibrate(1);
						showTextToast("站点未验证");
					}

				} else {
					play_beep_sound.playBeepSoundAndVibrate(1);
					showTextToast("站点未验证");
				}
			}
		});

		nolocal.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (nolocal.getText().equals("未锁")) {
					nolocal.setText("已锁");
					nextname.setFocusable(false);
					workorder.setFocusable(false);
					KEYCODE_BACK_flag = true;
					nextname.setBackgroundColor(Color.parseColor("#808080"));
					workorder.setBackgroundColor(Color.parseColor("#808080"));
				} else {
					nolocal.setText("未锁");
					KEYCODE_BACK_flag = false;
					nextname.setFocusable(true);
					nextname.requestFocus();
					nextname.setFocusableInTouchMode(true);
					workorder.setFocusable(true);
					workorder.requestFocus();
					workorder.setFocusableInTouchMode(true);
					nextname.setBackgroundColor(Color.parseColor("#F7F7F7"));
					workorder.setBackgroundColor(Color.parseColor("#F7F7F7"));
				}
			}
		});
	}

	public void delet_dbHelp(int position) {
		deleDatabaseSQ(position);
		unSend_count_msg--;
		if (unSend_count_msg < 1)
			unSend_count_msg = 0;
		current_all_number = (int) saveShard.getCount("发件");
		mGetSaveDabaseSQ(false, current_all_number);
	}

	public boolean KEYCODE_BACK_flag = false;
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!KEYCODE_BACK_flag) {
				showDialog(SendscanActivity.this, "确认退出当前界面？");
			}
			return KEYCODE_BACK_flag ? true : super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	};

	public boolean check_sitecode_flag = false;
	public String PRE_STATION = "";
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x001) {
				if (check_sitecode_success) {
					check_sitecode_flag = true;
					dialog_progress_line.setVisibility(View.GONE);
					PRE_STATION = siteCodeMsg.getSitename();
					nextname.setText(siteCodeMsg.getSitename());
					workorder.requestFocus();
					m_thread.interrupt();
				} else {
					dialog_progress_line.setVisibility(View.GONE);
					m_thread.interrupt();
				}
			} else if (msg.what == 0x002) {
				unSend_count_msg = unSend_count_msg - sending_count_n;
				adapter.setNowSending(unSend_count_msg);
				adapter.notifyDataSetChanged();
				send_number_count.setText(unSend_count_msg + "票" + " / "
						+ (int) saveShard.getCount("发件") + "票");
				upload_text_flag.setVisibility(View.GONE);
				showTextToast("上传成功");
			} else if (msg.what == 0x003) {
				index_dele = msg.arg1;
				position = msg.arg2;
				if (position == 0)
					adapter.setpostion(0);
				else
					adapter.setpostion(position - 1);
				adapter.notifyDataSetChanged();
			}else if(msg.what == 0x004){
				send_FaJian_parames(1);
			}
		};
	};

	public boolean send_up_destroy = false;
	public static int index_dele = -1;
	public static int position = -1;

	public String getXDeviceInfo() {
		XDeviceInfo xDevice = new XDeviceInfo();
		xDevice.setKey(SaveShardMessage.TOKEN + "-"
				+ getCurrentTime("yyyyMMddHHmm"));
		xDevice.setPhoneNumber(null);
		xDevice.setIMEI(null);
		xDevice.setIMSI(null);
		xDevice.setDeviceID(LoginActivity.DEVICE_ID);
		xDevice.setDeviceModel(null);
		xDevice.setSystemType(null);
		xDevice.setSystemVersion(null);
		String xDeviceInfo = new Gson().toJson(xDevice);
		Log.d("标记符号", xDeviceInfo);
		return xDeviceInfo;
	}

	public int sending_count_n = 0;

	/**
	 * 发件请求
	 */
	public void send_FaJian_parames(int send_flag) {
		is_sending_up = true;
		upload_text_flag.setVisibility(View.VISIBLE);
		if (saveShard.getchange_sendup_flag(SendscanActivity.this) == 1
				&& send_flag == 1) {
			send_flag_parames();
		} else if (send_flag == 0) {
			send_flag_parames();
		}
	}

	public void send_flag_parames() {
		sending_count_n = unSend_count_msg;
		JsonToString jsont = new JsonToString();
		Map<String, String> paramsmap = new HashMap<String, String>();
		paramsmap.put(
				"scanDataList",
				jsont.getScanDataList(getFajianlist(
						(int) saveShard.getCount("发件"), sending_count_n)));
		paramsmap.put("xDeviceInfo", getXDeviceInfo());
		paramsmap.put("userName",
				"\"" + mPreferences.getString("X-Auth-User", "") + "\"");
		paramsmap.put("siteCode",
				"\"" + mPreferences.getString("X-Auth-Site", "") + "\"");
		paramsmap.put("clientTime", "\"" + getCurrentTime("yyyy-MM-dd") + "T"
				+ getCurrentTime("HH:mm:ss") + "+08:00" + "\"");
		Map<String, String> headParams = getHeadParams();
		// http://handset2.appl.800best.com/hyco/v1/HTScan/HycoSend
		// http://opendev.appl.800best.com/hscedev/v1/HTScan/HycoSend
		String url = LoginActivity.http_util + "/v1/HTScan/HycoSend";
		new getSiteCode(paramsmap, headParams, url, 1).execute();
	}

	public static long old_tiem = 0;
	public TimeQueryData timeQuery;

	public String getNewString(){
		int count = (int) saveShard.getCount("发件");
		String s = count + "";
		String newStr = "";
		for(int i = 0; i < (6 - s.length()); i ++){
			newStr += "0";
		}
		return newStr + s;
	}
	/**
	 * 数据库保存
	 */
	public void saveDabaseFaJian() {
		WeakUPScreen.onScan(SendscanActivity.this);
		String time = getCurrentTime("HH:mm:ss");
		String date = getCurrentTime("yyyy-MM-dd");
		old_tiem = getOldTime(time);
		String number_value = "@" + waybillnumber.getText().toString();
		if (timeQuery.setFajianCursor(number_value)) {
			unSend_count_msg++;
			saveShard.saveShardFaJianMessage(new FaJianSQLite(number_value,
					NEXT_SITECODE, workorder.getText().toString(), time, date,
					mPreferences.getString("X-Auth-User", ""), mPreferences
							.getString("X-Auth-Site", "")));
			waybillnumber.setText("");
			play_beep_sound.playBeepSoundAndVibrate(0);
			current_all_number = (int) saveShard.getCount("发件");
			mGetSaveDabaseSQ(false, current_all_number);
			if (!is_sending_up && unSend_count_msg >= num_count_limit) {
				if (myNative.OpCom_CheckBarcode(workorder.getText().toString(),
						1) == 1) {
					send_FaJian_parames(1);
				}
			}
		} else {
			adapter.setmMessageItems(timeQuery.faJianSQLite);
			adapter.notifyDataSetChanged();
			play_beep_sound.playBeepSoundAndVibrate(1);
			showTextToast("重复运单号是否删除");
		}
	}

	public Thread timer_thread;
	public boolean timer_thread_flag = true;

	public void check_sleep_time() {
		timer_thread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000 * 60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					String time = getCurrentTime("HH:mm:ss");
					long current_time = getOldTime(time);
					if (old_tiem > 0
							&& (current_time - old_tiem) > num_count_time * 60 * 1000
							&& unSend_count_msg > 0) {
						handler.sendEmptyMessage(0x004);
						
					}
				}
			}
		});
		timer_thread.start();
	}

	public void close_timer() {
		if (timer_thread != null) {
			timer_thread_flag = false;
			timer_thread.interrupt();
			timer_thread = null;
		}
	}

	public long getOldTime(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		long timeStart = 0;
		try {
			timeStart = sdf.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeStart;
	}

	/**
	 * 删除
	 * 
	 * @param position
	 */
	public void deleDatabaseSQ(int position) {
		saveShard.deleShardFaJian(position);
	}

	/**
	 * 获取数据库数据
	 */
	GetSaveDabaseSQ getSQLite;
	PrivateListingAdapter adapter;
	ArrayList<FaJianSQLite> fajianlist = new ArrayList<FaJianSQLite>();

	public void mGetSaveDabaseSQ(boolean falg, int current_all_number) {
		ArrayList<FaJianSQLite> list = getFajianlist(current_all_number, Get_SQLite_count);
		if (!falg) {
			fajianlist.clear();
		}
		fajianlist.addAll(list);
		adapter.setNowSending(unSend_count_msg);
		adapter.setmMessageItems(fajianlist);
		adapter.notifyDataSetChanged();
		send_number_count.setText(unSend_count_msg + "票" + "/"
				+ (int) saveShard.getCount("发件") + "票");
	}

	public ArrayList<FaJianSQLite> getFajianlist(int current_num, int count) {
		ArrayList<FaJianSQLite> fajianlist = getSQLite.getFajianData(database,
				current_num, count, false);
		ArrayList<FaJianSQLite> fajian_print = new ArrayList<FaJianSQLite>();
		for (int i = fajianlist.size() - 1; i >= 0; i--) {
			fajian_print.add(fajianlist.get(i));
		}
		return fajian_print;
	}

	/**
	 * 获得当前时间
	 */
	public String getCurrentTime(String current_time) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(current_time);
		return formatter.format(date);
	}

	public Thread m_thread;
	public boolean check_sitecode_success = false;

	public void check_sitecode() {
		m_thread = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0x001);
			}
		});
		m_thread.start();
		dialog_progress_line.setVisibility(View.VISIBLE);
		Map<String, String> paramsmap = new HashMap<String, String>();
		paramsmap.put("siteCode", "\"" + NEXT_SITECODE + "\"");
		Map<String, String> headParams = getHeadParams();
		// url = http://handset2.appl.800best.com/hyco/v1/HTSite/HycoSearchSite
		// http://opendev.appl.800best.com/hscedev/v1/HTSite/HycoSearchSite
		String url = LoginActivity.http_util + "/v1/HTSite/HycoSearchSite";
		new getSiteCode(paramsmap, headParams, url, 0).execute();
	}

	public Map<String, String> getHeadParams() {
		Map<String, String> headParams = new HashMap<String, String>();
		headParams.put("Content-Type", "application/x-www-form-urlencoded");
		headParams.put("X-Auth-Token", SaveShardMessage.TOKEN);
		headParams
				.put("X-Auth-Site", mPreferences.getString("X-Auth-Site", ""));
		headParams
				.put("X-Auth-User", mPreferences.getString("X-Auth-User", ""));
		return headParams;
	}

	SiteCodeMessage siteCodeMsg = new SiteCodeMessage();

	class getSiteCode extends AsyncTask<Void, Void, String> {
		public Map<String, String> paramsmap;
		public Map<String, String> headParams;
		public String http_url;
		public int flag;

		public getSiteCode(Map<String, String> paramsmap,
				Map<String, String> headParams, String http_url, int flag) {
			super();
			this.paramsmap = paramsmap;
			this.headParams = headParams;
			this.http_url = http_url;
			this.flag = flag;
		}

		protected String doInBackground(Void... params) {
			HttpUrl httpUrl = new HttpUrl();
			String result = httpUrl.post(http_url, paramsmap, headParams);
			return result;
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			is_sending_up = false;
			send_up_destroy = false;
			if (null != result && result.length() > 0) {
				Log.d("KKKKKKKK", result);
				if (flag == 0) {
					if (!result.equals("500")) {
						setSiteCode(result);
						if (result.equals("403")) {
							dialog_progress_line.setVisibility(View.GONE);
							check_sitecode_success = false;
							play_beep_sound.playBeepSoundAndVibrate(1);
							showTextToast("Token失效，请重新登录");
						} else if (!"null".equals(siteCodeMsg.getSitecode())
								&& siteCodeMsg.getSitecode().length() > 4) {
							check_sitecode_success = true;
							handler.sendEmptyMessage(0x001);
						} else {
							dialog_progress_line.setVisibility(View.GONE);
							check_sitecode_success = false;
							play_beep_sound.playBeepSoundAndVibrate(1);
							showTextToast("验证失败");
						}
					} else {
						check_sitecode_success = false;
						play_beep_sound.playBeepSoundAndVibrate(1);
						dialog_progress_line.setVisibility(View.GONE);
						showTextToast("验证失败");
					}
				} else if (flag == 1)
					checkSendScan_parames(result);
			}
		}

		public void setSiteCode(String result) {
			JSONObject mJsonObject;
			try {
				mJsonObject = new JSONObject(result);
				siteCodeMsg.setSitename(mJsonObject.getString("sitename"));
				siteCodeMsg.setDispatchrange(mJsonObject
						.getString("dispatchrange"));
				siteCodeMsg.setNotdispatchrange(mJsonObject
						.getString("notdispatchrange"));
				siteCodeMsg.setSitecode(mJsonObject.getString("sitecode"));
				siteCodeMsg.setPhone(mJsonObject.getString("phone"));
				siteCodeMsg.setPrincipal(mJsonObject.getString("principal"));
				siteCodeMsg.setCity(mJsonObject.getString("city"));
				siteCodeMsg.setProvince(mJsonObject.getString("province"));
				siteCodeMsg.setModifydate(mJsonObject.getString("modifydate"));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public void checkSendScan_parames(String result) {
			JSONObject mJsonObject;
			try {
				mJsonObject = new JSONObject(result);
				if (result.equals("403")) {
					play_beep_sound.playBeepSoundAndVibrate(2);
					showTextToast("Token失效，请重新登录");
				}else if (0 <= Integer.parseInt(mJsonObject.getString("acceptcount"))){
					handler.sendEmptyMessage(0x002);
				}else{
					play_beep_sound.playBeepSoundAndVibrate(2);
					showTextToast("上传失败,请检查网络");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void init() {
		nextname = (EditText) findViewById(R.id.nextname);
		workorder = (EditText) findViewById(R.id.workorder);
		waybillnumber = (EditText) findViewById(R.id.waybillnumber);
		verify = (TextView) findViewById(R.id.verify);
		nolocal = (TextView) findViewById(R.id.nolock);
		upload_text_flag = (TextView) findViewById(R.id.upload_fajian_flag);
		send_number_count = (TextView) findViewById(R.id.send_number_count);
		listView = (ListViewCompat) findViewById(R.id.send_scan_listview);
		nextname.setInputType(InputType.TYPE_NULL);
		workorder.setInputType(InputType.TYPE_NULL);
		waybillnumber.setInputType(InputType.TYPE_NULL);
		
		fanhui = (Button) findViewById(R.id.fanhui);
		delect = (Button) findViewById(R.id.delect);
		queding = (Button) findViewById(R.id.queding);
		upload_text_flag.setVisibility(View.GONE);
		dialog_progress_line = (LinearLayout) findViewById(R.id.dialog_progress_line);
		dialog_progress_line.setVisibility(View.GONE);
	}

	public void dialog_next_click_button(String station_name) {
		if (station_name.length() == 6) {
			play_beep_sound.playBeepSoundAndVibrate(0);
			check_sitecode();
		} else {
			play_beep_sound.playBeepSoundAndVibrate(1);
			showTextToast("站点错误");
		}
	}

	public void dialog_work_click_button() {
		if (nextname.getText().toString() != null)
			workorder.setText(numberString);
		waybillnumber.requestFocus();
	}

	public boolean show_dialog_ing = true;

	public void showDialog(Context context, final String msg) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(msg);
		builder.setTitle("询问");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (msg.equals("确认退出当前界面？")) {
					if (unSend_count_msg != 0)
						showDialog(SendscanActivity.this, "还有未上传数据，请上传");
					else {
						dialog.dismiss();
						SendscanActivity.this.finish();
					}
				} else if (msg.equals("确认更改站点？")) {
					show_dialog_ing = true;
					dialog_next_click_button(NEXT_SITECODE);
				} else if (msg.equals("确认更改任务单？")) {
					show_dialog_ing = true;
					dialog_work_click_button();
				} else if (msg.equals("还有未上传数据，请上传")) {
					befor_destroy();
					send_up_destroy = true;
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (msg.equals("还有未上传数据，请上传")) {
					SendscanActivity.this.finish();
				} else if (msg.equals("确认更改站点？")) {
					nextname.setText(PRE_STATION);
					show_dialog_ing = true;
					check_sitecode_flag = true;
				} else if (msg.equals("确认更改任务单？")) {
					workorder.setText(WORK_PRE);
					check_sitecode_flag = true;
					show_dialog_ing = true;
				} else {
					show_dialog_ing = true;
				}
			}
		});
		builder.create().show();
	}

	public void befor_destroy() {
		if (!is_sending_up) {
			send_FaJian_parames(0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		WeakUPScreen.onResume();
		check_sitecode_flag = true;
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		close_timer();
		WeakUPScreen.onDestroy();
		NEXT_SITECODE = "";
		play_beep_sound.player_release();
		unregisterReceiver(mGattUpdateReceiver);
		mPreferences.edit().putInt("unSend_count_msg_fajian", unSend_count_msg)
				.commit();
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
}
