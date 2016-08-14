package com.app.jianyiexcel.util;

public class MyStringUtil {
	public static boolean isInteger(String value){
		try{
			Integer.parseInt(value);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
}
