package com.app.jianyiexcel.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.app.jianyiexcel.activity.OpenWorkBook;
import com.app.jianyiexcel.model.OneRow;
import com.app.jianyiexcel.view.CellHorizontalScrollView;
import com.app.jianyiexcel.view.CellHorizontalScrollView.CellOnScrollChangedListener;
import com.app.jianyiexcel.R;

import android.content.Context;
import android.content.Loader.ForceLoadContentObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class RowsAdapter extends BaseAdapter {
	List<OneRow> map_rows;	
	Context context;
	int resource;
	int keyRowIndex;
	LayoutInflater rowsInflater;
	List<ViewHolder>list_ViewHolder=new ArrayList<ViewHolder>();
	
	public RowsAdapter(Context context, int resource) {
		this.context=context;
		this.resource=resource;
		//获得一张表的数据
		map_rows=OpenWorkBook.map_rows;
		keyRowIndex=OpenWorkBook.keyRowIndex;
		rowsInflater = LayoutInflater.from(context);
		
	}

	@SuppressWarnings("null")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder=null;
		//获得一行的数据
		OneRow oneRow=map_rows.get(position);
		List<String> list_oneCell=oneRow.getList_oneCell();
		
		if(convertView==null){
			//加同步锁，同时进行
			synchronized (context) {
				viewHolder=new ViewHolder();
				convertView=rowsInflater.inflate(resource, null);
				
				viewHolder.list_TextView=new ArrayList<TextView>();
				viewHolder.ll_item_row=(LinearLayout) convertView.findViewById(R.id.ll_item_row);
				viewHolder.tv_item_row_id=(TextView) convertView.findViewById(R.id.tv_item_row_id);
				
				//加载滚动布局，顶行
				viewHolder.cHSV_head=(CellHorizontalScrollView) 
						OpenWorkBook.ll_show_row.findViewById(R.id.head_cHSV_row_scrooll_container);
				//加载滚动布局，其他行
				viewHolder.cHSV=(CellHorizontalScrollView) 
						convertView.findViewById(R.id.cHSV_row_scrooll_container);
				//添加顶行布局的滚动监听和其他行关联
				viewHolder.cHSV_head.addOnScrollChangedListener(
						new MyCellOnScrollChangedListener(viewHolder.cHSV));
				//初始化TextView
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView1));
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView2));
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView3));
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView4));
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView5));
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView6));
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView7));
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView8));
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView9));
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView10));
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView11));
				viewHolder.list_TextView.add((TextView) convertView.findViewById(R.id.textView12));		
				list_ViewHolder.add(viewHolder);
				convertView.setTag(viewHolder);
			}
			
		}
		else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tv_item_row_id.setText(position+1+"");
		//关键字那一行字的颜色为红色
		if(position==keyRowIndex){
			viewHolder.tv_item_row_id.setTextColor(Color.parseColor("#FF6666"));
		}
		else{
			viewHolder.tv_item_row_id.setTextColor(Color.parseColor("#000000"));
		}
		System.out.println(position+"----"+keyRowIndex);
		if(list_oneCell.size()>=12){
			for (int i=0;i<12;i++) {
				final String cellValue=list_oneCell.get(i);
				TextView textView0=viewHolder.list_TextView.get(i);
				if(position==keyRowIndex){
					textView0.setTextColor(Color.parseColor("#FF6666"));
					
				}
				else{
					textView0.setTextColor(Color.parseColor("#000000"));
				}
				textView0.setText(cellValue);
				textView0.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(context, cellValue, 0).show();
					}
				});
			
			}
		}
		else{
			for (int i=0;i<list_oneCell.size();i++) {
				final String cellValue=list_oneCell.get(i);
				TextView textView0=viewHolder.list_TextView.get(i);
				if(position==keyRowIndex){
					textView0.setTextColor(Color.parseColor("#FF6666"));
					
				}
				else{
					textView0.setTextColor(Color.parseColor("#000000"));
				}
				textView0.setText(cellValue);
				textView0.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(context, cellValue, 0).show();
					}
				});
			}
		}
			
			
		return convertView;
		
	}
	class ViewHolder{
		
		CellHorizontalScrollView cHSV;
		CellHorizontalScrollView cHSV_head;
		
		LinearLayout ll_item_row;
		
		TextView tv_item_row_id;
		
		TextView textView1;
		TextView textView2;
		TextView textView3;
		TextView textView4;
		TextView textView5;
		TextView textView6;
		TextView textView7;
		TextView textView8;
		TextView textView9;
		TextView textView10;
		TextView textView11;
		TextView textView12;
		
		List <TextView> list_TextView;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return map_rows.size();
	}

	@Override
	public OneRow getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	//滑动监听
	class MyCellOnScrollChangedListener implements CellOnScrollChangedListener{
		CellHorizontalScrollView cHSV;
		
		public MyCellOnScrollChangedListener(CellHorizontalScrollView cellHorizontalScrollView) {
			cHSV=cellHorizontalScrollView;
		}

		@Override
		public void onScrollChanged(int l, int t, int oldl, int oldt) {
			cHSV.smoothScrollTo(l, t);
		}
		
	}
}