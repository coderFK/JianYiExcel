package com.app.jianyiexcel.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyFileUtil {
	//��apk��assets�е��ļ�������ָ��·��
	public static void copyFile(String fileName,String newFileName ,String fileTarget,Context context){
		String dataPath=fileTarget+"/"+newFileName;
		
		File newFilePath=new File(dataPath);
		if(newFilePath.exists()){
			return ;
		}
		else{
			InputStream is=null;
			FileOutputStream fos=null;
			
			try {
				File newFileDir=new File(fileTarget);
				//�ȴ���·��
				if(newFileDir.mkdir()){
					
				}
				else{
				}
				//�õ���Դ
				AssetManager assetManager=context.getAssets();
				//�õ����ݿ�������
				is = assetManager.open(fileName);
				
				//�������д��Sd����
				fos = new FileOutputStream(dataPath);
				byte[]by=new byte[1024*64];
				int count=0;
				while((count=is.read(by))>0){
					fos.write(by, 0, count);
					fos.flush();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				try {
					if(is!=null)
						is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if(fos!=null)
						fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
