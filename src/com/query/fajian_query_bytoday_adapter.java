package com.query;

import java.util.ArrayList;
import java.util.List;

import com.baishi.db.cn.FaJianSQLite;
import com.example.baishihuitong.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class fajian_query_bytoday_adapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater mInflater;
	private List<FaJianSQLite> list;
	
	public fajian_query_bytoday_adapter(Context context,List<FaJianSQLite> list) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.list = list;
		mInflater = LayoutInflater.from(mContext);
	}
	public void setList_data(List<FaJianSQLite> list){
		this.list = list;
	}
	public int position_background = 0;
	public void setpostion(int position){
		position_background = position;
	}
	@Override
	public int getCount() {
		if (list == null) {
			list = new ArrayList<FaJianSQLite>();
		}
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position).getIndex();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		View itemView = mInflater.inflate(R.layout.fajian_query_listview_item, null);

		holder = new ViewHolder(itemView);

		FaJianSQLite item = list.get(position);
		if(position_background == position){
			holder.fajian_query_today_VehicleNo.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
			holder.fajian_query_today_NextSite.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
			holder.fajian_query_today_BillCode.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
		}
		holder.fajian_query_today_VehicleNo.setText(item.getVehicleNo());
		holder.fajian_query_today_NextSite.setText((position + 1) + ": " + item.getNextSite());
		holder.fajian_query_today_BillCode.setText(item.getBillCode());
		notifyDataSetChanged();
		return itemView;
	}

	private static class ViewHolder {
		public TextView fajian_query_today_VehicleNo,fajian_query_today_NextSite,fajian_query_today_BillCode;
		public LinearLayout linear_item;

		ViewHolder(View view) {
			fajian_query_today_VehicleNo = (TextView) view.findViewById(R.id.fajian_query_today_VehicleNo);
			fajian_query_today_NextSite = (TextView) view.findViewById(R.id.fajian_query_today_NextSite);
			fajian_query_today_BillCode = (TextView) view.findViewById(R.id.fajian_query_today_BillCode);
		}
	}
}
