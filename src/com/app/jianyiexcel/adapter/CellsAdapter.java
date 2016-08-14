

package com.app.jianyiexcel.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.app.jianyiexcel.R;
import com.app.jianyiexcel.activity.OpenWorkBook;
import com.app.jianyiexcel.model.OneRow;
import com.app.jianyiexcel.view.CellHorizontalScrollView;
import com.app.jianyiexcel.view.CellHorizontalScrollView.CellOnScrollChangedListener;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CellsAdapter extends BaseAdapter {
	List<OneRow> map_rows;	
	public List<String> list_keys;
	public List<String> list_CellValues;
	public int cellValuesNumber;
	String[] array_EditTextValues;
	Context context;
	int resource;
	int positionRow;
	//记录选中的position
	int index=-1;
	
	public CellsAdapter(Context context, int resource,int positionRow) {
		this.context=context;
		this.resource=resource;
		this.positionRow=positionRow;
		
		map_rows=OpenWorkBook.map_rows;
		list_keys=OpenWorkBook.list_keys;
		list_CellValues=map_rows.get(positionRow).getList_oneCell();
		cellValuesNumber = list_CellValues.size();
		array_EditTextValues=new String[cellValuesNumber];
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cellValuesNumber;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list_CellValues.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@SuppressWarnings("null")
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v;
		ViewHolder viewHolder;
		
		if(convertView==null){
			viewHolder=new ViewHolder();
			v=LayoutInflater.from(context).inflate(resource, null);
			viewHolder.tv_item_cell=(TextView) v.findViewById(R.id.tv_item_cell);
			viewHolder.bt_item=(Button) v.findViewById(R.id.bt_item_cell);
			viewHolder.et_item=(EditText) v.findViewById(R.id.et_item_cell);
			//设置可以获取焦点为true，以及点击获取焦点，给EditText设置标记position
			viewHolder.et_item.setFocusable(true);
			viewHolder.et_item.setFocusableInTouchMode(true);
			viewHolder.et_item.setTag(position);
			//给EditText设置点击事件
			viewHolder.et_item.setOnTouchListener(new OnTouchListener() {
				
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if(arg1.getAction()==MotionEvent.ACTION_UP){
						//点击后指针设置为刚刚点击的那个EditText的序号，即position
						index=(Integer) arg0.getTag();
					}
					return false;
				}
			});
			//添加文本数据监听事件
			viewHolder.et_item.addTextChangedListener(new  MyEditTextWatcher(viewHolder));
			
			v.setTag(viewHolder);
			
		}
		else{
			v=convertView;
			viewHolder=(ViewHolder) v.getTag();
			viewHolder.et_item.setTag(position);
		}
		
		String key=list_keys.get(position);
		String cellValue="";
		if(position<cellValuesNumber){
			cellValue=list_CellValues.get(position);
			
		}
		viewHolder.tv_item_cell.setText((position+1)+"");
		viewHolder.bt_item.setText(key);
		viewHolder.et_item.setText(cellValue);
		//更新数据后清除焦点
		viewHolder.et_item.clearFocus();
		if(index!=-1&&index==position){
			//用户点击到某个位置的EditText时，请求焦点
			viewHolder.et_item.requestFocus();
		}
		//获取焦点
		
		
		return v;
		
	}
	//缓存收纳者
	class ViewHolder{
		
		TextView tv_item_cell;
		
		Button bt_item;
		
		EditText et_item;
		
	}
	//文本数据监听者
	class MyEditTextWatcher implements TextWatcher{
		ViewHolder mholder;
		public MyEditTextWatcher(ViewHolder holder) {
			super();
			mholder=holder;
			// TODO Auto-generated constructor stub
		}

		//输入框中的数据发生改变后在List中储存起来
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			if(!TextUtils.isEmpty(arg0)){
				int position2=(Integer) mholder.et_item.getTag();
				String editTextValue=arg0.toString();
				list_CellValues.set(position2, editTextValue);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1,
				int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1,
				int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
