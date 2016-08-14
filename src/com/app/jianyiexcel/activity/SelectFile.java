package com.app.jianyiexcel.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.app.jianyiexcel.R;
import com.app.jianyiexcel.adapter.FileNameAdapter;
import com.app.jianyiexcel.model.ExcelFileData;
import com.app.jianyiexcel.model.FileStack;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectFile extends Activity {
	//包含文件数组，以及当前状态的文件夹
	private File[] array_fileNames;
	private File currentFile;
	private String absolutePath=Environment.getExternalStorageDirectory()+"/";
	private File rootFile=Environment.getExternalStorageDirectory();
	private FileStack<File> fileStack;
	
	//初始化文件名以及表中关键字的位置
	public String excelFileDir=Environment.getExternalStorageDirectory()+"/";
	public static String excelFileName=null;
	public String excelFilePath=null;
	public int CREATE_NEW_EXCEL_FILE=300;
	private TextView tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//判断存储卡是否挂载
		if(!(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))){
			Toast.makeText(SelectFile.this, "当前Sd卡不可用，请检查Sd卡", Toast.LENGTH_LONG).show();
		}
		else{
			//显示布局
			setContentView(R.layout.activity_select_file);
			//初始化数据
			initial();
			//获得文件列表名
			getListFileName(rootFile);
			//配置ListView
			setListView();
			//初始化布局
			setLayout();
		}
		
	}
	
	//创建File栈
	private void initial() {
		//创建一个链表数据结构的栈容器
		fileStack = new FileStack<File>();
		//向栈中添加元素
		fileStack.add(rootFile);
	}

	//获得file文件夹下的文件列表
	public void getListFileName(File file){
		array_fileNames = file.listFiles();
	}
	
	//设置ListView
	public void setListView(){
		ListView lv_test=(ListView) findViewById(R.id.lv_file_name);
		FileNameAdapter fileNameAdapter=new FileNameAdapter(array_fileNames, R.layout.item_file_name, SelectFile.this);
		lv_test.setAdapter(fileNameAdapter);
		fileNameAdapter.notifyDataSetChanged();
		lv_test.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				//获得当前文件夹路径
				currentFile = array_fileNames[position];
				excelFileName= currentFile.getName();
				excelFilePath=currentFile.getAbsolutePath();
				
				//判断所文件是是文件还是文件夹
				if(currentFile.isDirectory()){
					excelFileDir=currentFile.getAbsolutePath();
					//文件夹则在栈中添加且更新页面
					fileStack.add(currentFile);
					getListFileName(currentFile);
					setListView();
					//获得绝对路径并且显示
					absolutePath=currentFile.getAbsolutePath();
					tv.setText("文件路径: "+absolutePath);
				}
				else{
					excelFileDir=currentFile.getParent();
					//当文件名后缀为.xls或者.xlsx时打开文件
					if(excelFileName.endsWith(".xls")||excelFileName.endsWith(".xlsx")){
						//判断文件存在后跳转
						checkFileExist();
						Toast.makeText(SelectFile.this, "打开"+excelFilePath, 0).show();
					}
					else{
						Toast.makeText(SelectFile.this, "该文件不是Excel类型文件", 0).show();
					}
				}
			}
		});
	}
		
	//判断文件是否存在，再进行跳转
	public void checkFileExist(){
		File excelFile=new File(excelFilePath);
		if(excelFile.exists()){
			//将信息储存到本地文件中，防止在内存中北清除
			SharedPreferences spf=getSharedPreferences(excelFileName+"_xml_data", MODE_PRIVATE);
			Editor editor=spf.edit();
			editor.putString("excelFileDir", excelFileDir);
			editor.putString("excelFileName", excelFileName);
			editor.putString("excelFilePath", excelFilePath);
			editor.putInt("sheetIndex", 0);
			editor.putInt("sheetIndex", 0);
			editor.commit();
			//跳转到OpenWorkBook
			Intent intentOpenWWorkBook=new Intent(SelectFile.this, OpenWorkBook.class);
			startActivity(intentOpenWWorkBook);
			finish();
		}
		else{
			Toast.makeText(this, excelFilePath+"文件不存在！", Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	//初始化布局
	private void setLayout() {
		tv = (TextView) findViewById(R.id.tv_select_file);
		tv.setText("文件路径: "+absolutePath);
		//设置新建Excel文件按钮
		Button bt_create_excel_file=(Button) findViewById(R.id.bt_create_excel_file);
		bt_create_excel_file.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final EditText et_input_file_name=new EditText(SelectFile.this);
				et_input_file_name.setText("新建文件.xls");
				//弹出对话框让用户输入指定位置
				AlertDialog.Builder dialog3=new Builder(SelectFile.this);
				dialog3.setTitle("新建Excel文件");
				dialog3.setMessage("新建文件路径："+"\r\n"+excelFileDir+"\r\n"+"请输入文件名");
				dialog3.setView(et_input_file_name);
				dialog3.setCancelable(true);
				dialog3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface arg0, int arg1) {
						String input_fileName=et_input_file_name.getText().toString();
						if(!TextUtils.isEmpty(input_fileName)){
							excelFileName=input_fileName;
							excelFilePath=excelFileDir+"/"+excelFileName;
							//将信息储存到本地文件中，防止在内存中北清除
							SharedPreferences spf=getSharedPreferences(excelFileName+"_xml_data", MODE_PRIVATE);
							Editor editor=spf.edit();
							editor.putString("excelFileDir", excelFileDir);
							editor.putString("excelFileName", excelFileName);
							editor.putString("excelFilePath", excelFilePath);
							editor.commit();
							//跳转到OpenWorkBook
							Intent intentOpenWorkBook=new Intent(SelectFile.this, OpenWorkBook.class);
							intentOpenWorkBook.putExtra("fromSelectFileType", CREATE_NEW_EXCEL_FILE);
							startActivity(intentOpenWorkBook);
							finish();
						}
					}
				});
				dialog3.create().show();
			}
		});
		
	}
	//根据当前文件夹的状态进行退出选择
	public void onBackPressed() {
		if(currentFile==rootFile||currentFile==null){
			finish();
		}
		else{
			//在File栈中删除当前的一个File，返回当前的File
			fileStack.remove();
			currentFile=fileStack.getCurrentItem();
			getListFileName(currentFile);
			setListView();
		}
		
	}
	
	
	

}
