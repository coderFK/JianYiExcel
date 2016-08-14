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
	//�����ļ����飬�Լ���ǰ״̬���ļ���
	private File[] array_fileNames;
	private File currentFile;
	private String absolutePath=Environment.getExternalStorageDirectory()+"/";
	private File rootFile=Environment.getExternalStorageDirectory();
	private FileStack<File> fileStack;
	
	//��ʼ���ļ����Լ����йؼ��ֵ�λ��
	public String excelFileDir=Environment.getExternalStorageDirectory()+"/";
	public static String excelFileName=null;
	public String excelFilePath=null;
	public int CREATE_NEW_EXCEL_FILE=300;
	private TextView tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//�жϴ洢���Ƿ����
		if(!(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))){
			Toast.makeText(SelectFile.this, "��ǰSd�������ã�����Sd��", Toast.LENGTH_LONG).show();
		}
		else{
			//��ʾ����
			setContentView(R.layout.activity_select_file);
			//��ʼ������
			initial();
			//����ļ��б���
			getListFileName(rootFile);
			//����ListView
			setListView();
			//��ʼ������
			setLayout();
		}
		
	}
	
	//����Fileջ
	private void initial() {
		//����һ���������ݽṹ��ջ����
		fileStack = new FileStack<File>();
		//��ջ�����Ԫ��
		fileStack.add(rootFile);
	}

	//���file�ļ����µ��ļ��б�
	public void getListFileName(File file){
		array_fileNames = file.listFiles();
	}
	
	//����ListView
	public void setListView(){
		ListView lv_test=(ListView) findViewById(R.id.lv_file_name);
		FileNameAdapter fileNameAdapter=new FileNameAdapter(array_fileNames, R.layout.item_file_name, SelectFile.this);
		lv_test.setAdapter(fileNameAdapter);
		fileNameAdapter.notifyDataSetChanged();
		lv_test.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				//��õ�ǰ�ļ���·��
				currentFile = array_fileNames[position];
				excelFileName= currentFile.getName();
				excelFilePath=currentFile.getAbsolutePath();
				
				//�ж����ļ������ļ������ļ���
				if(currentFile.isDirectory()){
					excelFileDir=currentFile.getAbsolutePath();
					//�ļ�������ջ������Ҹ���ҳ��
					fileStack.add(currentFile);
					getListFileName(currentFile);
					setListView();
					//��þ���·��������ʾ
					absolutePath=currentFile.getAbsolutePath();
					tv.setText("�ļ�·��: "+absolutePath);
				}
				else{
					excelFileDir=currentFile.getParent();
					//���ļ�����׺Ϊ.xls����.xlsxʱ���ļ�
					if(excelFileName.endsWith(".xls")||excelFileName.endsWith(".xlsx")){
						//�ж��ļ����ں���ת
						checkFileExist();
						Toast.makeText(SelectFile.this, "��"+excelFilePath, 0).show();
					}
					else{
						Toast.makeText(SelectFile.this, "���ļ�����Excel�����ļ�", 0).show();
					}
				}
			}
		});
	}
		
	//�ж��ļ��Ƿ���ڣ��ٽ�����ת
	public void checkFileExist(){
		File excelFile=new File(excelFilePath);
		if(excelFile.exists()){
			//����Ϣ���浽�����ļ��У���ֹ���ڴ��б����
			SharedPreferences spf=getSharedPreferences(excelFileName+"_xml_data", MODE_PRIVATE);
			Editor editor=spf.edit();
			editor.putString("excelFileDir", excelFileDir);
			editor.putString("excelFileName", excelFileName);
			editor.putString("excelFilePath", excelFilePath);
			editor.putInt("sheetIndex", 0);
			editor.putInt("sheetIndex", 0);
			editor.commit();
			//��ת��OpenWorkBook
			Intent intentOpenWWorkBook=new Intent(SelectFile.this, OpenWorkBook.class);
			startActivity(intentOpenWWorkBook);
			finish();
		}
		else{
			Toast.makeText(this, excelFilePath+"�ļ������ڣ�", Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	//��ʼ������
	private void setLayout() {
		tv = (TextView) findViewById(R.id.tv_select_file);
		tv.setText("�ļ�·��: "+absolutePath);
		//�����½�Excel�ļ���ť
		Button bt_create_excel_file=(Button) findViewById(R.id.bt_create_excel_file);
		bt_create_excel_file.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final EditText et_input_file_name=new EditText(SelectFile.this);
				et_input_file_name.setText("�½��ļ�.xls");
				//�����Ի������û�����ָ��λ��
				AlertDialog.Builder dialog3=new Builder(SelectFile.this);
				dialog3.setTitle("�½�Excel�ļ�");
				dialog3.setMessage("�½��ļ�·����"+"\r\n"+excelFileDir+"\r\n"+"�������ļ���");
				dialog3.setView(et_input_file_name);
				dialog3.setCancelable(true);
				dialog3.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface arg0, int arg1) {
						String input_fileName=et_input_file_name.getText().toString();
						if(!TextUtils.isEmpty(input_fileName)){
							excelFileName=input_fileName;
							excelFilePath=excelFileDir+"/"+excelFileName;
							//����Ϣ���浽�����ļ��У���ֹ���ڴ��б����
							SharedPreferences spf=getSharedPreferences(excelFileName+"_xml_data", MODE_PRIVATE);
							Editor editor=spf.edit();
							editor.putString("excelFileDir", excelFileDir);
							editor.putString("excelFileName", excelFileName);
							editor.putString("excelFilePath", excelFilePath);
							editor.commit();
							//��ת��OpenWorkBook
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
	//���ݵ�ǰ�ļ��е�״̬�����˳�ѡ��
	public void onBackPressed() {
		if(currentFile==rootFile||currentFile==null){
			finish();
		}
		else{
			//��Fileջ��ɾ����ǰ��һ��File�����ص�ǰ��File
			fileStack.remove();
			currentFile=fileStack.getCurrentItem();
			getListFileName(currentFile);
			setListView();
		}
		
	}
	
	
	

}
