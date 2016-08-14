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
	//将apk中assets中的文件拷贝到指定路径
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
				//先创建路径
				if(newFileDir.mkdir()){
					
				}
				else{
				}
				//得到资源
				AssetManager assetManager=context.getAssets();
				//得到数据库输入流
				is = assetManager.open(fileName);
				
				//用输出流写到Sd卡里
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
