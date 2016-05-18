package com.query;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import sendupSet.TimeQueryData;
import com.baishi.db.cn.DBHelper;
import com.baishihuitong.untils.ListViewCompat;
import com.example.baishihuitong.R;
import com.packages.JiBaoSQLite;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchJiBao_Every_dayActivity extends Activity {
	public Button search;
	public Spinner spinner;
	public TextView station_name_jibao,number_jibao;
	public ListViewCompat listView;
	public String str;
	public String[] m_date = { "今天", "昨天", "前天", "任意一天"};
	
	public String times="";
	public String query_time = "";
	
	public jibao_query_bytoday_adapter adapter;
	public TimeQueryData timeQuery;
	public DBHelper dbHelper;
	private SQLiteDatabase database;
	int position_key = 0;
	ArrayList<JiBaoSQLite> daoJianSQLite_today = new ArrayList<JiBaoSQLite>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchrecrod);
		init();
		spinner.setAdapter(mdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				search.setTag(position);// 传给search所点击的那个数据
				if(position == 3){
					DatePickerDialog d = null;
					Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
					if (d == null) {
						d = new DatePickerDialog(SearchJiBao_Every_dayActivity.this, new OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								Calendar calendar = Calendar.getInstance();
								calendar.set(year, monthOfYear, dayOfMonth);
								long time = calendar.getTimeInMillis() ;
								Date date=new Date(time);
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
								query_time = format.format(date);
									times = format.format(date);
									m_date[3] = times;
									spinner.setSelection(3);
									mdapter.notifyDataSetChanged();
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
					}
					d.setButton(DatePickerDialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					d.show();
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				position_key = (Integer) v.getTag();
				switch (position_key) {
				case 0:
					refresh(0, 0, 10, false);
					adapter.setList_data(daoJianSQLite_today);
					adapter.notifyDataSetChanged();
					break;
				case 1:
					refresh(1, 0, 10, false);
					adapter.setList_data(daoJianSQLite_today);
					adapter.notifyDataSetChanged();
					break;
				case 2:
					refresh(2, 0, 10, false);
					adapter.setList_data(daoJianSQLite_today);
					adapter.notifyDataSetChanged();
					break;
				case 3:
					
					refresh(3, 0, 10, false);
					adapter.setList_data(daoJianSQLite_today);
					adapter.notifyDataSetChanged();
					break;

				default:
					break;
				}
			}
		});
		
	}
	public int number_index = 0;
	public void mListViewRefresh() {
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(true);
		listView.setXListViewListener(new ListViewCompat.IXListViewListener() {
			public void onRefresh() {
				number_index = 0;
				listView.setRefreshTime(getDates());
				refresh(position_key,0, 10,false);
				adapter.setList_data(daoJianSQLite_today);
				adapter.notifyDataSetChanged();
			}

			public void onLoadMore() {
				number_index += 10;
				refresh(position_key,number_index, 10, true);
				adapter.setList_data(daoJianSQLite_today);
				adapter.notifyDataSetChanged();
				listView.stopRefresh();
				listView.stopLoadMore();
			}
		});
	}
	public void init() {
		spinner = (Spinner) findViewById(R.id.spinner);
		search = (Button) findViewById(R.id.search);
		listView = (ListViewCompat) findViewById(R.id.dlistview);
		dbHelper = new DBHelper(this);
		database = dbHelper.getWritableDatabase();
		timeQuery = new TimeQueryData(database);
		refresh(position_key,0, 10, false);
		adapter = new jibao_query_bytoday_adapter(SearchJiBao_Every_dayActivity.this, daoJianSQLite_today);
		listView.setAdapter(adapter);
		mListViewRefresh();
	}
	public void refresh(int key_value,int start,int end,boolean flag){
		ArrayList<JiBaoSQLite> faJianSQLite = new ArrayList<JiBaoSQLite>();
		if(!flag)
			daoJianSQLite_today.clear();
		if(key_value == 0){
			Log.e("tag_查询时间", getDates()+"");
			faJianSQLite = timeQuery.getJiBaoSQLite(getDates(),start,end);
		}else if(key_value == 1){
			Log.e("tag_查询时间", yesTerDay()+"");
			faJianSQLite = timeQuery.getJiBaoSQLite(yesTerDay(),start,end);
		}else  if(key_value == 2){
			Log.e("tag_查询时间", beforeYesterDay()+"");
			faJianSQLite = timeQuery.getJiBaoSQLite(beforeYesterDay(),start,end);
		}else  if(key_value == 3){
			Log.e("tag_查询时间", times+"");
			faJianSQLite = timeQuery.getJiBaoSQLite(query_time,start,end);
		}
		daoJianSQLite_today.addAll(faJianSQLite);
	}
	
	// 获取当前日期
	public String getDates() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(date);
		return time;
	}

	// 获取昨天日期
	public String yesTerDay() {
		Format f = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		System.out.println("昨天:" + f.format(c.getTime()));
		c.add(Calendar.DAY_OF_MONTH, -1);
		String time = f.format(c.getTime());
		return time;
	}

	// 获取前天日期
	public String beforeYesterDay() {
		Format f = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		System.out.println("前天:" + f.format(c.getTime()));
		c.add(Calendar.DAY_OF_MONTH, -2);
		String time = f.format(c.getTime());
		return time;
	}
	
	// 获取当前时间
	public String getDate() {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = format.format(date);
		return time;
	}
	
	BaseAdapter mdapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(SearchJiBao_Every_dayActivity.this).inflate(R.layout.date, null);
			((TextView) convertView.findViewById(R.id.text)).setText(m_date[position]);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return m_date[position];
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return m_date.length;
		}
	};
}
