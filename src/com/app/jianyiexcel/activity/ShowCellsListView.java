package com.app.jianyiexcel.activity;

import java.util.HashMap;
import java.util.List;

import com.app.jianyiexcel.R;
import com.app.jianyiexcel.adapter.CellsAdapter;
import com.app.jianyiexcel.adapter.PopWindowAdapter;
import com.app.jianyiexcel.model.OneRow;
import com.app.jianyiexcel.util.ExcelUtil;
import com.app.jianyiexcel.view.CellHorizontalScrollView;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCellsListView extends Activity {
	public static LinearLayout ll_show_cells;
	private CellsAdapter cellAdapter;
	private ListView lv;
	private int positionRow;
	private Button bt_activity_item_select;
	private PopupWindow pop_select_rows;
	private static int SHOW_POP_SELECT_ROWS=1;
	private static int SHOW_POP_SELECT_KEY_ROWS=2;
	private static int TO_OPENWORKBOOK_JUST_SAVE=10;
	private static int TO_OPENWORKBOOK_SELECT_KEY_ROWS=20;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_cells);
		//获得其他Activity的信息
		getIntentData();
		//初始化数据和布局
		initial(positionRow);
		//设置按钮监听
		setButton();
	}
	//获得其他Activity的信息
	private void getIntentData() {
		// TODO Auto-generated method stub
		//获得目标行序号
		positionRow = getIntent().getIntExtra("positionRow", 0);
	}
	//初始化布局
	private void initial(int positionRow) {
		// TODO Auto-generated method stub
		//设置顶行中的滚动CellHorizontalScrollView布局
		ll_show_cells = (LinearLayout) findViewById(R.id.ll_show_cells);
		ll_show_cells.setFocusable(true);
		ll_show_cells.setClickable(true);
		ll_show_cells.setOnTouchListener(new MyOnTouchListener());
		
		//设置ListView
		lv = (ListView) findViewById(R.id.lv_show_cells);
		cellAdapter = new CellsAdapter(ShowCellsListView.this,
				R.layout.item_show_cells, positionRow);
		lv.setAdapter(cellAdapter);
		//监听数据变化
		cellAdapter.notifyDataSetChanged();
		
	}
	
	//设置按钮监听
	public void setButton(){

		//设置保存按钮
		Button bt_activity_item_save=(Button) findViewById(R.id.bt_activity_item_save);
		bt_activity_item_save.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				saveOneRow();
				Toast.makeText(ShowCellsListView.this, "第"+(positionRow+1)+"行数据保存成功", 0).show();
			}
		});
		//设置选择Row的按钮
		bt_activity_item_select = (Button) findViewById(R.id.bt_activity_item_select);
		bt_activity_item_select.setText("第"+(positionRow+1)+"行");
		bt_activity_item_select.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				
				//如果弹出的窗口已经展现了则关闭它
				if(pop_select_rows!=null&&pop_select_rows.isShowing()){
					pop_select_rows.dismiss();
				}
				else{
					showPopupWindows(SHOW_POP_SELECT_ROWS);
				}
			}
		});
		
		//设置改变首行中的各项的按钮
		Button bt_activity_item_cell=(Button) 
				findViewById(R.id.bt_activity_item_cell);
		bt_activity_item_cell.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder dialog=new AlertDialog.Builder(ShowCellsListView.this);
				dialog.setTitle("是否要改变该列");
				dialog.setMessage("您可以选择上一个页面中的一行作为该列的值，是否重新选择？");
				dialog.setCancelable(true);
				dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface arg0, int arg1) {
						showPopupWindows(SHOW_POP_SELECT_KEY_ROWS);
						
					}

				});
				dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
				});
				
				dialog.create();
				dialog.show();
			}
		});
	}
	private void showPopupWindows(final int type) {
		//设置弹出窗口
		LinearLayout ll_show_cells_menu=(LinearLayout) findViewById(R.id.ll_show_cells_menu);
		LinearLayout popup_window=(LinearLayout) LayoutInflater.
				from(ShowCellsListView.this).inflate(R.layout.popup_window, null);
		pop_select_rows = new PopupWindow(popup_window, 
				ll_show_cells_menu.getWidth()/2, LayoutParams.WRAP_CONTENT, true);
		pop_select_rows.setBackgroundDrawable(new BitmapDrawable());
		pop_select_rows.showAsDropDown(ll_show_cells_menu);
		pop_select_rows.setOutsideTouchable(true);
		pop_select_rows.update();
		//设置ListView
		ListView lv_pop=(ListView)popup_window. findViewById(R.id.lv_pop);
		PopWindowAdapter popWindowAdapter=new PopWindowAdapter(ShowCellsListView.this,
				R.layout.item_pop, ExcelUtil.getList_RowsIndex());
		lv_pop.setAdapter(popWindowAdapter);
		popWindowAdapter.notifyDataSetChanged();
		lv_pop.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int positionRow2, long arg3) {

				switch (type) {
				case 1:
					initial(positionRow2);
					positionRow=positionRow2;
					bt_activity_item_select.setText("第"+(positionRow+1)+"行");
					pop_select_rows.dismiss();
					saveOneRow();
					break;
				case 2:
					//将用户选择的关键字序号保存在本地
					Editor editor=getSharedPreferences(SelectFile.excelFileName+"_xml_data", MODE_PRIVATE).edit();
					editor.putInt("keyRowIndex",positionRow2);
					editor.commit();
					Toast.makeText(ShowCellsListView.this,
							"已经选择"+(positionRow2+1)+"行的数据作为该列的各值", 0).show();
					//保存并跳转到OpenWorkBook
					saveOneRow();
					Intent intentShowRows=new Intent(ShowCellsListView.this,OpenWorkBook.class);
					intentShowRows.putExtra("fromShowCellsListViewType", TO_OPENWORKBOOK_SELECT_KEY_ROWS);
					startActivity(intentShowRows);
					finish();
					break;

				default:
					break;
				}
				
			}
		});
		
	}
	//保存当前行
	public void saveOneRow(){
		OneRow oneRow=new OneRow();
		oneRow.setList_oneCell(cellAdapter.list_CellValues);
		//改变OpenWorkBook中map_rows的值
		OpenWorkBook.map_rows.remove(positionRow);
		OpenWorkBook.map_rows.add(positionRow, oneRow);
	}
	//触摸监听
	class MyOnTouchListener implements View.OnTouchListener{

		//当在列头 和 listView控件上touch时，将这个touch的事件分发给 ScrollView
		public boolean onTouch(View v, MotionEvent event) {
			CellHorizontalScrollView cHSV=(CellHorizontalScrollView) 
					ll_show_cells.findViewById(R.id.cHSV_horizontal);
			cHSV.onTouchEvent(event);
			return false;
		}
		
	}
	//后退返回至ShowRowsListView
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		//退出时保存数据
		saveOneRow();
		Intent intentShowRows=new Intent(ShowCellsListView.this,OpenWorkBook.class);
		intentShowRows.putExtra("fromShowCellsListViewType", 10);
		startActivity(intentShowRows);
		finish();
	}
	
	

}
