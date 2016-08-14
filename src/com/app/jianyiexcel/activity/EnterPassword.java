package com.app.jianyiexcel.activity;


import com.app.jianyiexcel.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class EnterPassword extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_enter_password);
		
		//在第2次输入密码时及以后不正确会提醒密码错误
		boolean isPasswordError=getIntent().getBooleanExtra("isPasswordError", false);
		if(isPasswordError){
			Toast.makeText(EnterPassword.this, "密码错误", 0).show();
		}
	}
	
	//获取输入框中的密码
	public void writePassword(View v){
		EditText et_writePassword=(EditText) findViewById(R.id.et_writePassword);
		String password = et_writePassword.getText().toString();
		
		Intent intentOpenWordBook=new Intent(EnterPassword.this,OpenWorkBook.class);
		intentOpenWordBook.putExtra("password", password);
		intentOpenWordBook.putExtra("havePassword", true);
		intentOpenWordBook.putExtra("isPasswordError", true);
		intentOpenWordBook.putExtra("isFromEnterPassword", true);
		startActivity(intentOpenWordBook);
		finish();
	}
	
	//后退返回至MainActivity
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		Intent intentMain=new Intent(EnterPassword.this,SelectFile.class);
		startActivity(intentMain);
		finish();
	}	
}
