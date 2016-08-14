package com.app.jianyiexcel.model;

import android.os.Environment;

public class ExcelFileData {
	public static String excelFileDir;
	public static String excelFileName;
	public static String excelFilePath;
	public static int sheetIndex;
	public static int keyRowIndex;
	
	public int getKeyRowIndex() {
		return keyRowIndex;
	}
	public void setKeyRowIndex(int keyRowIndex) {
		this.keyRowIndex = keyRowIndex;
	}
	public String getExcelFileDir() {
		return excelFileDir;
	}
	public void setExcelFileDir(String excelFileDir) {
		this.excelFileDir = excelFileDir;
	}
	public String getExcelFileName() {
		return excelFileName;
	}
	public void setExcelFileName(String excelFileName) {
		this.excelFileName = excelFileName;
	}
	public String getExcelFilePath() {
		return excelFilePath;
	}
	public void setExcelFilePath(String excelFilePath) {
		this.excelFilePath = excelFilePath;
	}
	public int getSheetIndex() {
		return sheetIndex;
	}
	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}
	
	
}
