package com.app.jianyiexcel.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.app.jianyiexcel.R;
import com.app.jianyiexcel.adapter.PopWindowAdapter;
import com.app.jianyiexcel.adapter.RowsAdapter;
import com.app.jianyiexcel.model.ExcelFileData;
import com.app.jianyiexcel.model.OneRow;
import com.app.jianyiexcel.util.ExcelUtil;
import com.app.jianyiexcel.util.MyFileUtil;
import com.app.jianyiexcel.util.MyStringUtil;
import com.app.jianyiexcel.view.CellHorizontalScrollView;



import android.R.anim;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OpenWorkBook extends Activity {

	private static String excelFileDir ;
	private static String excelFilePath ;
	private static String excelFileName ;
	private static String password ;
	private static int sheetIndex ;
	private static int sheetNumber;
	private static int rowsNumber;
	private static List<String > list_sheetName;
	private static boolean isPasswordError;
	private static int fromShowCellsListViewType;
	private int fromSelectFileType;
	private static boolean havePassword;
	private static List<String> list_pop_value;
	private ProgressBar pb;
	private ListView lv;
	private RowsAdapter rowsAdapter;
	private Button bt_selectSheet;
	
	private static int TO_OPENWORKBOOK_JUST_SAVE=10;
	private static int TO_OPENWORKBOOK_SELECT_KEY_ROWS=20;
	private static int POP_SELECT_SHEET=100;
	private static int POP_ADD_DELETE_ONEROW=110;
	private static int POP_ADD_DELETE_ONECOLUMN=120;
	private static int ADD_ONEROW_INDEX=200;
	private static int DELETE_ONEROW_INDEX=210;
	private static int ADD_ONECOLUMN_INDEX=220;
	private static int DELETE_ONECOLUMN_INDEX=230;
	private static int CREATE_NEW_EXCEL_FILE=300;
	
	public static List<OneRow> map_rows;
	public static HashMap<Integer,List<OneRow>> map_sheets;
	public static HashMap<Integer,List<String>> map_list_keys;
	public static LinearLayout ll_show_row;
	public static String sheetName=null;
	public static List<Integer> list_sheetIndex;
	public static List<String> list_keys;
	public static int keyRowIndex;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_open_workbook);
		
		//获得其他Activity的信息
		getIntentData();
		
		//如果是从ShowCellsListView直接过来的Activity，则不用进行解析excel操作，直接打开ShowRowsListView
		//如果是从ShowCellsListView要求选择关键字行序号为目的，要进行重新解析
		if(fromShowCellsListViewType==TO_OPENWORKBOOK_SELECT_KEY_ROWS){
			//初始化
			getSharedData();
			//重新给list_keys赋值
			setList_keys(keyRowIndex);
		}
		//不是从ShowCellsListView过来的要重新赋值
		else if(fromShowCellsListViewType==0){
			if(fromSelectFileType==CREATE_NEW_EXCEL_FILE){
				//初始化
				initial();
				getSharedData();
				String fileName="default.xls";
				//复制本地文件
				MyFileUtil.copyFile(fileName,excelFileName, excelFileDir, OpenWorkBook.this);
				System.out.println(fileName+" "+excelFileName+" "+ excelFileDir+" "+excelFilePath+" "+OpenWorkBook.this);
				openWorkbook(sheetIndex);
				
			}
			else{
				//初始化
				initial();
				getSharedData();
				//解析Excel文件
				openWorkbook(sheetIndex);
			}
			
		}
		
		//不管怎样都要重新加载布局
		//显示页面
		showRows();
		//设置按钮
		setButton();
		
		for (int i = 0; i < list_keys.size(); i++) {
			System.out.print(i+"="+list_keys.get(i)+" ");
		}
		
	}
	
	//获得其他Activity的信息
	public void getIntentData(){
		Intent intentOpenWorkBook=getIntent();
		password=intentOpenWorkBook.getStringExtra("password");
		havePassword=intentOpenWorkBook.getBooleanExtra("havePassword", false);
		isPasswordError=intentOpenWorkBook.getBooleanExtra("isPasswordError", false);
		fromShowCellsListViewType = intentOpenWorkBook.getIntExtra("fromShowCellsListViewType", 0);
		fromSelectFileType = intentOpenWorkBook.getIntExtra("fromSelectFileType", 0);
	}
	
	//初始化
	public void initial(){
		//创建 sheet 记录表序号容器 行头List
		map_sheets=new HashMap<Integer, List<OneRow>>();
		list_sheetIndex=new ArrayList<Integer>();
		map_list_keys=new HashMap<Integer, List<String>>();
		
	}
	//从本地中获得文件路径等信息
	public void getSharedData(){
		SharedPreferences spf=getSharedPreferences(SelectFile.excelFileName+"_xml_data", MODE_PRIVATE);
		excelFileDir=spf.getString("excelFileDir","");
		excelFilePath=spf.getString("excelFilePath","");
		excelFileName=spf.getString("excelFileName","");
		sheetIndex=spf.getInt("sheetIndex",0);
		keyRowIndex=spf.getInt("keyRowIndex",0);
	}
	
	//打开excel表
	public void openWorkbook(int sheetIndex2) {
		//当表序号中有了该表时，便不需要再重新加载
		if(!list_sheetIndex.contains(sheetIndex2)){
			//获得表中一系列信息
			map_rows=ExcelUtil.getSheet(
					excelFilePath,password,
					havePassword,isPasswordError,
					OpenWorkBook.this,EnterPassword.class,
					sheetIndex2,keyRowIndex);
			list_sheetName = new ArrayList<String>();
			setList_keys(keyRowIndex);
			sheetName=ExcelUtil.getSheetName(sheetIndex2);
			sheetNumber = ExcelUtil.getSheetsNumber();
			rowsNumber=map_rows.size();
			for (int i = 0; i < sheetNumber; i++) {
				String sheetName2=ExcelUtil.getSheetName(i);
				list_sheetName.add(sheetName2);
			}
			//添加一个sheet的信息,记录表序号
			map_sheets.put(sheetIndex2, map_rows);
			list_sheetIndex.add(sheetIndex2);
		}
		else{
			map_rows=map_sheets.get(sheetIndex2);
			setList_keys(keyRowIndex);
		}
		
	}
	//设置关键字那一行
	public void setList_keys(int keyRowIndex3){
		list_keys=map_rows.get(keyRowIndex3).getList_oneCell();
		int maxColumnNumber=ExcelUtil.getMaxColumnNumber();
		int keyNumber=list_keys.size();
		//如果关键字一行不是后面有null,则要将它的长度设置列的最大值
		if(keyNumber<maxColumnNumber){
			for (int i = 0; i < maxColumnNumber-keyNumber; i++) {
				list_keys.add("");
			}
		}
		
	}
	//跳转至ShowRowsListView
	public void showRows(){
		//设置顶行
		setHead();
		//设置ListView
		setListView();
	}
	
	//设置行头
	private void setHead() {
		ll_show_row = (LinearLayout) findViewById(R.id.ll_show_row);
		ll_show_row.setFocusable(true);
		ll_show_row.setClickable(true);
		ll_show_row.setBackgroundColor(Color.parseColor("#66CCFF"));
		ll_show_row.setOnTouchListener(new MyOnTouchListener());
		
//		TextView tv_show_rows_menu=(TextView) findViewById(R.id.tv_show_rows_menu);
//		tv_show_rows_menu.setText("");
	}
	
	//设置ListView
	private void setListView() {
		lv = (ListView) findViewById(R.id.lv_show_rows);
		rowsAdapter = new RowsAdapter(OpenWorkBook.this, R.layout.item_show_rows);
		lv.setAdapter(rowsAdapter);
		//添加ListView的滚动监听
		lv.setOnTouchListener(new MyOnTouchListener());
		//设置ListView监听事件
		//点击跳转到ShowCellsListView
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intentShowCell=new Intent(OpenWorkBook.this,ShowCellsListView.class);
				intentShowCell.putExtra("positionRow", position);
				startActivity(intentShowCell);
				finish();
			}
		});
	}
	
	//设置按钮功能
	private void setButton() {
		
		//设置保存的按钮
		Button bt_saveSheet = (Button) findViewById(R.id.bt_saveSheet);
		bt_saveSheet.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//保存所有表
				saveAllSheets();
				
			}
		});
		//设置选择表名的按钮
		bt_selectSheet = (Button) findViewById(R.id.bt_selectSheet);
		bt_selectSheet.setText(sheetName);
		//设置按钮监听事件
		bt_selectSheet.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//显示选择表的弹出窗口布局
				showPopup(POP_SELECT_SHEET);
			}
		});
		//设置行操作的按钮
		Button bt_add_delete_oneRow = (Button) findViewById(R.id.bt_add_delete_oneRow);
		bt_add_delete_oneRow.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//显示行操作的弹出窗口布局
				showPopup(POP_ADD_DELETE_ONEROW);
				
			}
		});
		//设置列操作的按钮
		Button bt_add_delete_oneColumn = (Button) findViewById(R.id.bt_add_delete_oneColumn);
		bt_add_delete_oneColumn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//显示列操作的弹出窗口布局
				showPopup(POP_ADD_DELETE_ONECOLUMN);
				
			}
		});
		
	}
	private void showPopup(final int clickType) {
		// TODO Auto-generated method stub
		//获取顶栏布局
		LinearLayout ll_show_rows_menu=(LinearLayout) 
				findViewById(R.id.ll_show_rows_menu);
		//获得可弹出的布局
		LinearLayout popup_window=(LinearLayout) LayoutInflater.from
				(OpenWorkBook.this).inflate(R.layout.popup_window, null);
		//获得弹出布局的对象，为顶栏布局1/2宽，合适长度，可获取焦点
		final PopupWindow pop=new PopupWindow(popup_window, 
				ll_show_rows_menu.getWidth()/2, LayoutParams.WRAP_CONTENT, true);
		//设置背景
		pop.setBackgroundDrawable(new BitmapDrawable() );
		//设置可在外部点击
		pop.setOutsideTouchable(true);
		//设置弹出布局在顶栏布局的下面
		pop.showAsDropDown(ll_show_rows_menu);
		//设置弹出布局的状态
//		pop.update();
		
		//初始化list_pop_value
		list_pop_value=new ArrayList<String>();
		//根据clickType类型选择用哪一个集合
		switch (clickType) {
		//选择表
		case 100:
			list_pop_value.clear();
			list_pop_value = list_sheetName;
			break;
		//对行操作
		case 110:
			list_pop_value.clear();
			list_pop_value.add("在末尾添加一行");
			list_pop_value.add("在指定位置添加一行");
			list_pop_value.add("删除末尾一行");
			list_pop_value.add("删除指定位置的一行");
			break;
		//对列操作
		case 120:
			list_pop_value.clear();
			list_pop_value.add("在末尾添加一列");
			list_pop_value.add("在指定位置添加一列");
			list_pop_value.add("删除末尾一列");
			list_pop_value.add("删除指定位置的一列");
			break;

		default:
			break;
		}
		//获取表名的ListView，配置适配器
		ListView lv_pop=(ListView) popup_window.
				findViewById(R.id.lv_pop);
		PopWindowAdapter popWindowAdapter=new PopWindowAdapter
				(OpenWorkBook.this, R.layout.item_pop, list_pop_value);
		lv_pop.setAdapter(popWindowAdapter);
		//监听适配器资源变化
		popWindowAdapter.notifyDataSetChanged();
		//配置ListView点击事件监听器
		lv_pop.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> arg0, View arg1, int position3,
					long arg3) {
				// TODO Auto-generated method stub
				if(position3!=-1){
					switch (clickType) {
					//选择表
					case 100:
						//开启指定表序号的一张表
						openWorkbook(position3);
						showRows();
						bt_selectSheet.setText(sheetName);
						pop.dismiss();
						break;
					//对行操作	
					case 110:
						//操作一行
						add_delete_oneRow(position3);
						pop.dismiss();
						break;
					//对列操作	
					case 120:
						//操作一列
						add_delete_oneColumn(position3);
						break;
					default:
						break;
					}
					
				}
			}
		});
	}
	//添加，删除一行
	private void add_delete_oneRow(int position3) {
		
		switch (position3) {
		//在末尾添加一行
		case 0:
			//获得上一行的单元格数
			int lastRowColumnNumber=0;
			//如果当前没有单元格，则创建一行20列的单元格
			if(rowsNumber==0||map_rows==null){
				lastRowColumnNumber=20;
			}
			else{
				lastRowColumnNumber=map_rows.get((rowsNumber-1)).getList_oneCell().size();
			}
			OneRow oneRow=new OneRow();
			oneRow.creatMap();
			oneRow.creatList_oneCell();
			for (int j = 0; j <lastRowColumnNumber; j++) {
				oneRow.addLastCellValue("");
			}
			map_rows.add(oneRow);
			setList_keys(keyRowIndex);
			Toast.makeText(OpenWorkBook.this, "添加第"+(rowsNumber+1)+"行！", 0).show();
			//定位到添加的那一行
			showRows();
			lv.setSelection(rowsNumber);
			rowsNumber=map_rows.size();
			break;
			
		//在指定位置添加一行
		case 1:
			showEditTextDialog(rowsNumber,ADD_ONEROW_INDEX);
			
			break;
			
		//删除末尾一行
		case 2:
			map_rows.remove(rowsNumber-1);
			setList_keys(keyRowIndex);
			Toast.makeText(OpenWorkBook.this, "删除第"+rowsNumber+"行！", 0).show();
			showRows();
			lv.setSelection(rowsNumber-1);
			rowsNumber=map_rows.size();
			break;
			
		//删除指定位置的一行
		case 3:
			showEditTextDialog(rowsNumber,DELETE_ONEROW_INDEX);
			break;

		default:
			break;
		}
		//刷新页面
		
//		showRows();
	}
	//添加，删除一列
	private void add_delete_oneColumn(int position3) {
		//得到Excel文件excelFilePath中的表
		HSSFWorkbook wb=ExcelUtil.creatWorkbook(excelFilePath);
		int do_columnIndex;
		switch (position3) {
		//在末尾添加一列
		case 0:
			for (int i = 0; i < rowsNumber; i++) {
				map_rows.get(i).addLastCellValue("");
			}
			showRows();
			do_columnIndex=map_rows.get(0).getList_oneCell().size();
			Toast.makeText(OpenWorkBook.this,"添加了第"+do_columnIndex+"列", 0).show();
			break;
		//在指定位置添加一列	
		case 1:
			do_columnIndex=map_rows.get(0).getList_oneCell().size();
			showEditTextDialog(do_columnIndex,ADD_ONECOLUMN_INDEX);
			
			break;
		//删除末尾一列
		case 2:
			for (int i = 0; i < rowsNumber; i++) {
				map_rows.get(i).deleteLastCellValue();
				
			}
			do_columnIndex=map_rows.get(0).getList_oneCell().size();
			Toast.makeText(OpenWorkBook.this,"删除第"+(do_columnIndex+1)+"列", 0).show();
			showRows();
			break;
		//删除指定位置的一列
		case 3:
			do_columnIndex=map_rows.get(0).getList_oneCell().size();
			showEditTextDialog(do_columnIndex,DELETE_ONECOLUMN_INDEX);
			break;

		default:
			break;
		}
	}
	public void showEditTextDialog(final int hintNumber,final int dialogType){
		final EditText et_add_oneRow3=new EditText(OpenWorkBook.this);
		et_add_oneRow3.setText((hintNumber-1)+"");
		//弹出对话框让用户输入指定位置
		AlertDialog.Builder dialog3=new Builder(OpenWorkBook.this);
		dialog3.setTitle("输入需要操作的位置");
		dialog3.setView(et_add_oneRow3);
		dialog3.setCancelable(true);
		dialog3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				String input_rowsIndex=et_add_oneRow3.getText().toString();
				if(MyStringUtil.isInteger(input_rowsIndex)){
					//注意要将用户输入的数字减一，计算机数字开头是0
					int select_rowsIndex=Integer.parseInt(input_rowsIndex)-1;
					if(select_rowsIndex<=hintNumber){
						switch (dialogType) {
						//添加指定位置行
						case 200:
							int lastRowColumnNumber=0;
							//	判断要添加的行数是否是第一行
							if(rowsNumber==0||map_rows==null){
								lastRowColumnNumber=20;
							}
							//如果当前没有单元格，则创建一行20列的单元格
							else if(select_rowsIndex==0){
								//获得下一行的单元格数
								lastRowColumnNumber=map_rows.get((select_rowsIndex+1)).getList_oneCell().size();
							}
							else{
								//获得上一行的单元格数
								lastRowColumnNumber=map_rows.get((select_rowsIndex-1)).getList_oneCell().size();
							}
							OneRow oneRow=new OneRow();
							oneRow.creatMap();
							oneRow.creatList_oneCell();
							for (int j = 0; j <lastRowColumnNumber; j++) {
								oneRow.addLastCellValue("");
							}
							map_rows.add(select_rowsIndex, oneRow);
							//要重新给list_keys赋值
							setList_keys(keyRowIndex);
							Toast.makeText(OpenWorkBook.this, "添加第"+input_rowsIndex+"行！", 0).show();
							rowsNumber=map_rows.size();
							showRows();
							lv.setSelection(select_rowsIndex);
							break;
						//删除指定位置行
						case 210:
								map_rows.remove(select_rowsIndex);
								setList_keys(keyRowIndex);
								rowsNumber=map_rows.size();
								showRows();
								lv.setSelection(select_rowsIndex);
								Toast.makeText(OpenWorkBook.this, "删除了第"+input_rowsIndex+"行", 0).show();
						//添加指定位置列
						case 220:
								for (int i = 0; i < rowsNumber; i++) {
									map_rows.get(i).addCellValue(select_rowsIndex, "");
									showRows();
								}							
								Toast.makeText(OpenWorkBook.this, "添加了第"+input_rowsIndex+"列", 0).show();
							break;
						//删除指定位置列
						case 230:
							for (int i = 0; i < rowsNumber; i++) {
								map_rows.get(i).deleteCellValue(select_rowsIndex);
								showRows();
							}							
							Toast.makeText(OpenWorkBook.this, "删除了第"+input_rowsIndex+"列", 0).show();
							break;
	
						default:
							break;
						}
					}
					else{
						Toast.makeText(OpenWorkBook.this, "请输入小于等于"+hintNumber+"数字！", 0).show();
					}
				}
				else{
					Toast.makeText(OpenWorkBook.this, "请输入数字！", 0).show();
				}
			}
		});
		dialog3.create().show();
	}
	//保存一张表
	private HSSFWorkbook saveSheet(HSSFWorkbook wb,int sheetIndex2,List<OneRow>map_rows2) {
		// TODO Auto-generated method stub
		Sheet sh=wb.getSheetAt(sheetIndex2);
		//得到行数
		int rowsNumber=map_rows2.size();
		//一个循环是一行
		for (int i = 0; i <rowsNumber; i++) {
			//创建一行
			Row row=sh.createRow(i);
			//获得当行的列数
			OneRow oneRow=map_rows2.get(i);
			List<String> list_oneCell=oneRow.getList_oneCell();
			int columnNumber=list_oneCell.size();
			//一个循环式一个单元格
			for (int j = 0; j < columnNumber; j++) {
				if(row!=null){
					String cellValue=list_oneCell.get(j);
					Cell cell=row.createCell(j);
					
					if(cell!=null){
						HSSFRichTextString cellString=new HSSFRichTextString(cellValue);
						//设置单元格的值
						cell.setCellValue(cellString);
					}
				}
			}
		}
		return wb;
	}
	//保存所有的表
	private void saveAllSheets() {
		// TODO Auto-generated method stub
		//得到Excel文件excelFilePath中的表sheetIndex
		HSSFWorkbook wb=ExcelUtil.creatWorkbook(excelFilePath);
		//保存所有已经被打开过的sheet
		for (int i = 0; i <list_sheetIndex.size(); i++) {
			//获取表的序号
			int sheetIndex2=list_sheetIndex.get(i);
			List<OneRow>map_rows2=map_sheets.get(sheetIndex2);
			//根据表序号保存表
			wb=saveSheet(wb,sheetIndex2,map_rows2);
		}
		//创建Excel文件
		ExcelUtil.creatExcelFile(wb,excelFilePath);
		Toast.makeText(OpenWorkBook.this, "保存成功", 0).show();
	}

	//触摸监听对象转换
	class MyOnTouchListener implements View.OnTouchListener{

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			
			CellHorizontalScrollView cHSV=(CellHorizontalScrollView) 
					ll_show_row.findViewById(R.id.head_cHSV_row_scrooll_container);
			cHSV.onTouchEvent(arg1);
			return false;
		}
		
	}

	//设置进度条
	public ProgressBar setProgressBar(Activity  activity){
		//找到activity的根部ViewGroup，类型都为FramLayout
		FrameLayout rootContainer=(FrameLayout) activity.findViewById(android.R.id.content);
		//初始化控件的位置
		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams
				(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.gravity=Gravity.CENTER;
		//设置pb的显示位置
		pb = new ProgressBar(activity);
		pb.setLayoutParams(params);
		pb.setVisibility(View.GONE);
		//添加到根节点下
		rootContainer.addView(pb);
		return pb;
	}
	//后退返回至MainActivity
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder dialog=new AlertDialog.Builder(OpenWorkBook.this); 
		dialog.setTitle("您还未保存");
		dialog.setMessage("您还未保存该Excel文件，请问是否保存？");
		dialog.setCancelable(true);
		//设置保存后退出按钮
		dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				//保存后退出
				saveAllSheets();
				//退出Activity
				finishActivity();
			}
		});
		//设置直接退出按钮
		dialog.setNegativeButton("不保存直接退出", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				//退出Activity
				finishActivity();
			}
		});
		dialog.create();
		dialog.show();
	}

	public void finishActivity(){
		Intent intentMain=new Intent(OpenWorkBook.this,SelectFile.class);
		startActivity(intentMain);
		finish();
	}
}
