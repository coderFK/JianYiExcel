package com.app.jianyiexcel.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.app.jianyiexcel.R;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileNameAdapter extends BaseAdapter {

	File[] array_fileNames;
	int resource;
	Context context;
	
	public FileNameAdapter(File[] array_fileNames,
			int resource, Context context) {
		super();
		this.array_fileNames = array_fileNames;
		this.resource = resource;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return array_fileNames.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return array_fileNames[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		File file=array_fileNames[position];
		String fileName=file.getName();
		ViewHolder viewHolder=null;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(resource, null);
			viewHolder.tv_item_file_name=(TextView) convertView.findViewById(R.id.tv_item_file_name);
			viewHolder.iv_item_file_name=(ImageView) convertView.findViewById(R.id.iv_item_file_name);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		viewHolder.tv_item_file_name.setText(fileName);
		if(file.isFile()){
			if(fileName.endsWith(".xls")||fileName.endsWith(".xlsx")){
				viewHolder.iv_item_file_name.setImageResource(R.drawable.excelfile);
			}
			else{
				viewHolder.iv_item_file_name.setImageResource(R.drawable.file);
			}
		}
		else{
			viewHolder.iv_item_file_name.setImageResource(R.drawable.folder);
		}
		return convertView;
	}
	public class ViewHolder{
		TextView tv_item_file_name;
		ImageView iv_item_file_name;
	}

}
