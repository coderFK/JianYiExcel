package com.app.jianyiexcel.adapter;

import java.util.List;

import com.app.jianyiexcel.util.ExcelUtil;
import com.app.jianyiexcel.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
//ÎªPopupWindowÖÐµÄListVIewÊÊÅäÆ÷
public class PopWindowAdapter extends BaseAdapter {

	Context context;
	int resource;
	List<String> list_pop_value;

	public PopWindowAdapter(Context context, int resource,
			List<String> list_pop_value) {
		super();
		this.context = context;
		this.resource = resource;
		this.list_pop_value = list_pop_value;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_pop_value.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list_pop_value.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if(arg1==null){
			arg1=LayoutInflater.from(context).inflate(resource, null);
			
		}
		String pop_value=list_pop_value.get(arg0);
		TextView tv_item_pop_show_rows=(TextView) arg1.
				findViewById(R.id.tv_item_pop);
		if(pop_value!=null){
			tv_item_pop_show_rows.setText(pop_value);
		}
		return arg1;
	}

	
}
