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
		
		//�������Activity����Ϣ
		getIntentData();
		
		//����Ǵ�ShowCellsListViewֱ�ӹ�����Activity�����ý��н���excel������ֱ�Ӵ�ShowRowsListView
		//����Ǵ�ShowCellsListViewҪ��ѡ��ؼ��������ΪĿ�ģ�Ҫ�������½���
		if(fromShowCellsListViewType==TO_OPENWORKBOOK_SELECT_KEY_ROWS){
			//��ʼ��
			getSharedData();
			//���¸�list_keys��ֵ
			setList_keys(keyRowIndex);
		}
		//���Ǵ�ShowCellsListView������Ҫ���¸�ֵ
		else if(fromShowCellsListViewType==0){
			if(fromSelectFileType==CREATE_NEW_EXCEL_FILE){
				//��ʼ��
				initial();
				getSharedData();
				String fileName="default.xls";
				//���Ʊ����ļ�
				MyFileUtil.copyFile(fileName,excelFileName, excelFileDir, OpenWorkBook.this);
				System.out.println(fileName+" "+excelFileName+" "+ excelFileDir+" "+excelFilePath+" "+OpenWorkBook.this);
				openWorkbook(sheetIndex);
				
			}
			else{
				//��ʼ��
				initial();
				getSharedData();
				//����Excel�ļ�
				openWorkbook(sheetIndex);
			}
			
		}
		
		//����������Ҫ���¼��ز���
		//��ʾҳ��
		showRows();
		//���ð�ť
		setButton();
		
		for (int i = 0; i < list_keys.size(); i++) {
			System.out.print(i+"="+list_keys.get(i)+" ");
		}
		
	}
	
	//�������Activity����Ϣ
	public void getIntentData(){
		Intent intentOpenWorkBook=getIntent();
		password=intentOpenWorkBook.getStringExtra("password");
		havePassword=intentOpenWorkBook.getBooleanExtra("havePassword", false);
		isPasswordError=intentOpenWorkBook.getBooleanExtra("isPasswordError", false);
		fromShowCellsListViewType = intentOpenWorkBook.getIntExtra("fromShowCellsListViewType", 0);
		fromSelectFileType = intentOpenWorkBook.getIntExtra("fromSelectFileType", 0);
	}
	
	//��ʼ��
	public void initial(){
		//���� sheet ��¼��������� ��ͷList
		map_sheets=new HashMap<Integer, List<OneRow>>();
		list_sheetIndex=new ArrayList<Integer>();
		map_list_keys=new HashMap<Integer, List<String>>();
		
	}
	//�ӱ����л���ļ�·������Ϣ
	public void getSharedData(){
		SharedPreferences spf=getSharedPreferences(SelectFile.excelFileName+"_xml_data", MODE_PRIVATE);
		excelFileDir=spf.getString("excelFileDir","");
		excelFilePath=spf.getString("excelFilePath","");
		excelFileName=spf.getString("excelFileName","");
		sheetIndex=spf.getInt("sheetIndex",0);
		keyRowIndex=spf.getInt("keyRowIndex",0);
	}
	
	//��excel��
	public void openWorkbook(int sheetIndex2) {
		//������������˸ñ�ʱ���㲻��Ҫ�����¼���
		if(!list_sheetIndex.contains(sheetIndex2)){
			//��ñ���һϵ����Ϣ
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
			//���һ��sheet����Ϣ,��¼�����
			map_sheets.put(sheetIndex2, map_rows);
			list_sheetIndex.add(sheetIndex2);
		}
		else{
			map_rows=map_sheets.get(sheetIndex2);
			setList_keys(keyRowIndex);
		}
		
	}
	//���ùؼ�����һ��
	public void setList_keys(int keyRowIndex3){
		list_keys=map_rows.get(keyRowIndex3).getList_oneCell();
		int maxColumnNumber=ExcelUtil.getMaxColumnNumber();
		int keyNumber=list_keys.size();
		//����ؼ���һ�в��Ǻ�����null,��Ҫ�����ĳ��������е����ֵ
		if(keyNumber<maxColumnNumber){
			for (int i = 0; i < maxColumnNumber-keyNumber; i++) {
				list_keys.add("");
			}
		}
		
	}
	//��ת��ShowRowsListView
	public void showRows(){
		//���ö���
		setHead();
		//����ListView
		setListView();
	}
	
	//������ͷ
	private void setHead() {
		ll_show_row = (LinearLayout) findViewById(R.id.ll_show_row);
		ll_show_row.setFocusable(true);
		ll_show_row.setClickable(true);
		ll_show_row.setBackgroundColor(Color.parseColor("#66CCFF"));
		ll_show_row.setOnTouchListener(new MyOnTouchListener());
		
//		TextView tv_show_rows_menu=(TextView) findViewById(R.id.tv_show_rows_menu);
//		tv_show_rows_menu.setText("");
	}
	
	//����ListView
	private void setListView() {
		lv = (ListView) findViewById(R.id.lv_show_rows);
		rowsAdapter = new RowsAdapter(OpenWorkBook.this, R.layout.item_show_rows);
		lv.setAdapter(rowsAdapter);
		//���ListView�Ĺ�������
		lv.setOnTouchListener(new MyOnTouchListener());
		//����ListView�����¼�
		//�����ת��ShowCellsListView
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
	
	//���ð�ť����
	private void setButton() {
		
		//���ñ���İ�ť
		Button bt_saveSheet = (Button) findViewById(R.id.bt_saveSheet);
		bt_saveSheet.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//�������б�
				saveAllSheets();
				
			}
		});
		//����ѡ������İ�ť
		bt_selectSheet = (Button) findViewById(R.id.bt_selectSheet);
		bt_selectSheet.setText(sheetName);
		//���ð�ť�����¼�
		bt_selectSheet.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//��ʾѡ���ĵ������ڲ���
				showPopup(POP_SELECT_SHEET);
			}
		});
		//�����в����İ�ť
		Button bt_add_delete_oneRow = (Button) findViewById(R.id.bt_add_delete_oneRow);
		bt_add_delete_oneRow.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//��ʾ�в����ĵ������ڲ���
				showPopup(POP_ADD_DELETE_ONEROW);
				
			}
		});
		//�����в����İ�ť
		Button bt_add_delete_oneColumn = (Button) findViewById(R.id.bt_add_delete_oneColumn);
		bt_add_delete_oneColumn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//��ʾ�в����ĵ������ڲ���
				showPopup(POP_ADD_DELETE_ONECOLUMN);
				
			}
		});
		
	}
	private void showPopup(final int clickType) {
		// TODO Auto-generated method stub
		//��ȡ��������
		LinearLayout ll_show_rows_menu=(LinearLayout) 
				findViewById(R.id.ll_show_rows_menu);
		//��ÿɵ����Ĳ���
		LinearLayout popup_window=(LinearLayout) LayoutInflater.from
				(OpenWorkBook.this).inflate(R.layout.popup_window, null);
		//��õ������ֵĶ���Ϊ��������1/2�����ʳ��ȣ��ɻ�ȡ����
		final PopupWindow pop=new PopupWindow(popup_window, 
				ll_show_rows_menu.getWidth()/2, LayoutParams.WRAP_CONTENT, true);
		//���ñ���
		pop.setBackgroundDrawable(new BitmapDrawable() );
		//���ÿ����ⲿ���
		pop.setOutsideTouchable(true);
		//���õ��������ڶ������ֵ�����
		pop.showAsDropDown(ll_show_rows_menu);
		//���õ������ֵ�״̬
//		pop.update();
		
		//��ʼ��list_pop_value
		list_pop_value=new ArrayList<String>();
		//����clickType����ѡ������һ������
		switch (clickType) {
		//ѡ���
		case 100:
			list_pop_value.clear();
			list_pop_value = list_sheetName;
			break;
		//���в���
		case 110:
			list_pop_value.clear();
			list_pop_value.add("��ĩβ���һ��");
			list_pop_value.add("��ָ��λ�����һ��");
			list_pop_value.add("ɾ��ĩβһ��");
			list_pop_value.add("ɾ��ָ��λ�õ�һ��");
			break;
		//���в���
		case 120:
			list_pop_value.clear();
			list_pop_value.add("��ĩβ���һ��");
			list_pop_value.add("��ָ��λ�����һ��");
			list_pop_value.add("ɾ��ĩβһ��");
			list_pop_value.add("ɾ��ָ��λ�õ�һ��");
			break;

		default:
			break;
		}
		//��ȡ������ListView������������
		ListView lv_pop=(ListView) popup_window.
				findViewById(R.id.lv_pop);
		PopWindowAdapter popWindowAdapter=new PopWindowAdapter
				(OpenWorkBook.this, R.layout.item_pop, list_pop_value);
		lv_pop.setAdapter(popWindowAdapter);
		//������������Դ�仯
		popWindowAdapter.notifyDataSetChanged();
		//����ListView����¼�������
		lv_pop.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> arg0, View arg1, int position3,
					long arg3) {
				// TODO Auto-generated method stub
				if(position3!=-1){
					switch (clickType) {
					//ѡ���
					case 100:
						//����ָ������ŵ�һ�ű�
						openWorkbook(position3);
						showRows();
						bt_selectSheet.setText(sheetName);
						pop.dismiss();
						break;
					//���в���	
					case 110:
						//����һ��
						add_delete_oneRow(position3);
						pop.dismiss();
						break;
					//���в���	
					case 120:
						//����һ��
						add_delete_oneColumn(position3);
						break;
					default:
						break;
					}
					
				}
			}
		});
	}
	//��ӣ�ɾ��һ��
	private void add_delete_oneRow(int position3) {
		
		switch (position3) {
		//��ĩβ���һ��
		case 0:
			//�����һ�еĵ�Ԫ����
			int lastRowColumnNumber=0;
			//�����ǰû�е�Ԫ���򴴽�һ��20�еĵ�Ԫ��
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
			Toast.makeText(OpenWorkBook.this, "��ӵ�"+(rowsNumber+1)+"�У�", 0).show();
			//��λ����ӵ���һ��
			showRows();
			lv.setSelection(rowsNumber);
			rowsNumber=map_rows.size();
			break;
			
		//��ָ��λ�����һ��
		case 1:
			showEditTextDialog(rowsNumber,ADD_ONEROW_INDEX);
			
			break;
			
		//ɾ��ĩβһ��
		case 2:
			map_rows.remove(rowsNumber-1);
			setList_keys(keyRowIndex);
			Toast.makeText(OpenWorkBook.this, "ɾ����"+rowsNumber+"�У�", 0).show();
			showRows();
			lv.setSelection(rowsNumber-1);
			rowsNumber=map_rows.size();
			break;
			
		//ɾ��ָ��λ�õ�һ��
		case 3:
			showEditTextDialog(rowsNumber,DELETE_ONEROW_INDEX);
			break;

		default:
			break;
		}
		//ˢ��ҳ��
		
//		showRows();
	}
	//��ӣ�ɾ��һ��
	private void add_delete_oneColumn(int position3) {
		//�õ�Excel�ļ�excelFilePath�еı�
		HSSFWorkbook wb=ExcelUtil.creatWorkbook(excelFilePath);
		int do_columnIndex;
		switch (position3) {
		//��ĩβ���һ��
		case 0:
			for (int i = 0; i < rowsNumber; i++) {
				map_rows.get(i).addLastCellValue("");
			}
			showRows();
			do_columnIndex=map_rows.get(0).getList_oneCell().size();
			Toast.makeText(OpenWorkBook.this,"����˵�"+do_columnIndex+"��", 0).show();
			break;
		//��ָ��λ�����һ��	
		case 1:
			do_columnIndex=map_rows.get(0).getList_oneCell().size();
			showEditTextDialog(do_columnIndex,ADD_ONECOLUMN_INDEX);
			
			break;
		//ɾ��ĩβһ��
		case 2:
			for (int i = 0; i < rowsNumber; i++) {
				map_rows.get(i).deleteLastCellValue();
				
			}
			do_columnIndex=map_rows.get(0).getList_oneCell().size();
			Toast.makeText(OpenWorkBook.this,"ɾ����"+(do_columnIndex+1)+"��", 0).show();
			showRows();
			break;
		//ɾ��ָ��λ�õ�һ��
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
		//�����Ի������û�����ָ��λ��
		AlertDialog.Builder dialog3=new Builder(OpenWorkBook.this);
		dialog3.setTitle("������Ҫ������λ��");
		dialog3.setView(et_add_oneRow3);
		dialog3.setCancelable(true);
		dialog3.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				String input_rowsIndex=et_add_oneRow3.getText().toString();
				if(MyStringUtil.isInteger(input_rowsIndex)){
					//ע��Ҫ���û���������ּ�һ����������ֿ�ͷ��0
					int select_rowsIndex=Integer.parseInt(input_rowsIndex)-1;
					if(select_rowsIndex<=hintNumber){
						switch (dialogType) {
						//���ָ��λ����
						case 200:
							int lastRowColumnNumber=0;
							//	�ж�Ҫ��ӵ������Ƿ��ǵ�һ��
							if(rowsNumber==0||map_rows==null){
								lastRowColumnNumber=20;
							}
							//�����ǰû�е�Ԫ���򴴽�һ��20�еĵ�Ԫ��
							else if(select_rowsIndex==0){
								//�����һ�еĵ�Ԫ����
								lastRowColumnNumber=map_rows.get((select_rowsIndex+1)).getList_oneCell().size();
							}
							else{
								//�����һ�еĵ�Ԫ����
								lastRowColumnNumber=map_rows.get((select_rowsIndex-1)).getList_oneCell().size();
							}
							OneRow oneRow=new OneRow();
							oneRow.creatMap();
							oneRow.creatList_oneCell();
							for (int j = 0; j <lastRowColumnNumber; j++) {
								oneRow.addLastCellValue("");
							}
							map_rows.add(select_rowsIndex, oneRow);
							//Ҫ���¸�list_keys��ֵ
							setList_keys(keyRowIndex);
							Toast.makeText(OpenWorkBook.this, "��ӵ�"+input_rowsIndex+"�У�", 0).show();
							rowsNumber=map_rows.size();
							showRows();
							lv.setSelection(select_rowsIndex);
							break;
						//ɾ��ָ��λ����
						case 210:
								map_rows.remove(select_rowsIndex);
								setList_keys(keyRowIndex);
								rowsNumber=map_rows.size();
								showRows();
								lv.setSelection(select_rowsIndex);
								Toast.makeText(OpenWorkBook.this, "ɾ���˵�"+input_rowsIndex+"��", 0).show();
						//���ָ��λ����
						case 220:
								for (int i = 0; i < rowsNumber; i++) {
									map_rows.get(i).addCellValue(select_rowsIndex, "");
									showRows();
								}							
								Toast.makeText(OpenWorkBook.this, "����˵�"+input_rowsIndex+"��", 0).show();
							break;
						//ɾ��ָ��λ����
						case 230:
							for (int i = 0; i < rowsNumber; i++) {
								map_rows.get(i).deleteCellValue(select_rowsIndex);
								showRows();
							}							
							Toast.makeText(OpenWorkBook.this, "ɾ���˵�"+input_rowsIndex+"��", 0).show();
							break;
	
						default:
							break;
						}
					}
					else{
						Toast.makeText(OpenWorkBook.this, "������С�ڵ���"+hintNumber+"���֣�", 0).show();
					}
				}
				else{
					Toast.makeText(OpenWorkBook.this, "���������֣�", 0).show();
				}
			}
		});
		dialog3.create().show();
	}
	//����һ�ű�
	private HSSFWorkbook saveSheet(HSSFWorkbook wb,int sheetIndex2,List<OneRow>map_rows2) {
		// TODO Auto-generated method stub
		Sheet sh=wb.getSheetAt(sheetIndex2);
		//�õ�����
		int rowsNumber=map_rows2.size();
		//һ��ѭ����һ��
		for (int i = 0; i <rowsNumber; i++) {
			//����һ��
			Row row=sh.createRow(i);
			//��õ��е�����
			OneRow oneRow=map_rows2.get(i);
			List<String> list_oneCell=oneRow.getList_oneCell();
			int columnNumber=list_oneCell.size();
			//һ��ѭ��ʽһ����Ԫ��
			for (int j = 0; j < columnNumber; j++) {
				if(row!=null){
					String cellValue=list_oneCell.get(j);
					Cell cell=row.createCell(j);
					
					if(cell!=null){
						HSSFRichTextString cellString=new HSSFRichTextString(cellValue);
						//���õ�Ԫ���ֵ
						cell.setCellValue(cellString);
					}
				}
			}
		}
		return wb;
	}
	//�������еı�
	private void saveAllSheets() {
		// TODO Auto-generated method stub
		//�õ�Excel�ļ�excelFilePath�еı�sheetIndex
		HSSFWorkbook wb=ExcelUtil.creatWorkbook(excelFilePath);
		//���������Ѿ����򿪹���sheet
		for (int i = 0; i <list_sheetIndex.size(); i++) {
			//��ȡ������
			int sheetIndex2=list_sheetIndex.get(i);
			List<OneRow>map_rows2=map_sheets.get(sheetIndex2);
			//���ݱ���ű����
			wb=saveSheet(wb,sheetIndex2,map_rows2);
		}
		//����Excel�ļ�
		ExcelUtil.creatExcelFile(wb,excelFilePath);
		Toast.makeText(OpenWorkBook.this, "����ɹ�", 0).show();
	}

	//������������ת��
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

	//���ý�����
	public ProgressBar setProgressBar(Activity  activity){
		//�ҵ�activity�ĸ���ViewGroup�����Ͷ�ΪFramLayout
		FrameLayout rootContainer=(FrameLayout) activity.findViewById(android.R.id.content);
		//��ʼ���ؼ���λ��
		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams
				(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.gravity=Gravity.CENTER;
		//����pb����ʾλ��
		pb = new ProgressBar(activity);
		pb.setLayoutParams(params);
		pb.setVisibility(View.GONE);
		//��ӵ����ڵ���
		rootContainer.addView(pb);
		return pb;
	}
	//���˷�����MainActivity
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder dialog=new AlertDialog.Builder(OpenWorkBook.this); 
		dialog.setTitle("����δ����");
		dialog.setMessage("����δ�����Excel�ļ��������Ƿ񱣴棿");
		dialog.setCancelable(true);
		//���ñ�����˳���ť
		dialog.setPositiveButton("����", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				//������˳�
				saveAllSheets();
				//�˳�Activity
				finishActivity();
			}
		});
		//����ֱ���˳���ť
		dialog.setNegativeButton("������ֱ���˳�", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				//�˳�Activity
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
