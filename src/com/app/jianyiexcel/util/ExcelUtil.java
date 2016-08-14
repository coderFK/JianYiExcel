package com.app.jianyiexcel.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

import com.app.jianyiexcel.activity.EnterPassword;
import com.app.jianyiexcel.activity.OpenWorkBook;
import com.app.jianyiexcel.model.OneRow;

public class ExcelUtil {

	private static Workbook wb;
	private static int maxColumnNumber;
	private static List<OneRow> map_rows;
	//通过路径创建一个HSSFWorkbook
	public static HSSFWorkbook creatWorkbook(String excelFilePath4){
		FileInputStream fis=null;
		HSSFWorkbook sw=null;
		try {
			fis=new FileInputStream(excelFilePath4);
			POIFSFileSystem poif=new POIFSFileSystem(fis);
			sw=new HSSFWorkbook(poif);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if(fis!=null){
					fis.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sw;
	}
	//创建新的Excel文件
	public static void creatExcelFile(
		Workbook wb, String excelFilePath3) {
		// TODO Auto-generated method stub
		FileOutputStream fos=null;
		try {
			
			fos=new FileOutputStream(excelFilePath3);
			POIFSFileSystem pfs=new POIFSFileSystem();
			wb.write(fos);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
//		//有密码则进行密码保护
//		if(password!=null){
//			
//			POIFSFileSystem pfs=new POIFSFileSystem();
//			try {
//				@SuppressWarnings("deprecation")
//				EncryptionInfo info=new EncryptionInfo(pfs,EncryptionMode.agile);
//				
//				Encryptor encryptor=info.getEncryptor();
//				encryptor.confirmPassword(password);
//				File excelFile=new File(excelFilePath3);
//				OPCPackage opc=OPCPackage.open(excelFile,PackageAccess.READ_WRITE);
//				OutputStream os=encryptor.getDataStream(pfs);
//				opc.save(os);
//				opc.close();
//				
//				FileOutputStream fos2=new FileOutputStream(excelFile);
//				pfs.writeFilesystem(fos2);
//				fos2.close();
//				System.out.println(password+"======================password");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			
//		}
	}
	//打开Excel文件获得Workbook
	public static HSSFWorkbook readExcelWorkbook(
			String excelFilePath2,String password,
			boolean havePassword,boolean isPasswordError,
			Context context,Class passwordClass) {
		// TODO Auto-generated method stub
		//声明IO流
		FileInputStream input = null;
		BufferedInputStream binput = null;
		POIFSFileSystem poifs = null;
		HSSFWorkbook wb=null;

		try {
			
			//打开路径为EXCEL_FILEPATH的Excel文件
			input = new FileInputStream(excelFilePath2);
			binput = new BufferedInputStream(input);
			//读取Excel文件的输入流，得到Excel文件输入流对象
			poifs = new POIFSFileSystem(input);
			//有密码则判断
			if(havePassword){
				//判断输入的密码是否正确
				Biff8EncryptionKey.setCurrentUserPassword(password);
			}
			//密码正确则记录下来，以便后面方便保存用
			Editor editor=context.getSharedPreferences(
					"excelFileDate", context.MODE_PRIVATE).edit();
			editor.putString("password", password);
			editor.commit();
			
			//获得Workbook
			wb = new HSSFWorkbook(poifs);
			
		} catch (IOException e) {
			System.out.println(e.toString());
		} 
		
		//有密码的话则跳转至需要密码的Activity
		catch (EncryptedDocumentException e) {
			//第1次输入密码后不正确会提醒密码错误
			getPassword(context,passwordClass,isPasswordError);
		}
		
		finally {
			try {
				if(binput!=null){
					binput.close();
				}
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
		return wb;
	}
	
	//获取密码
	public static void getPassword(Context context,
			Class passwordClass,boolean isPasswordError){
		Intent intentEnterPassword=new Intent(context,passwordClass);
		//设置flag判断密码是否有错误
		intentEnterPassword.putExtra("isPasswordError", isPasswordError);
		context.startActivity(intentEnterPassword);
		((Activity) context).finish();
	}
	
	//解析格式为数字的Cell，包含了对日期的解析
	public static String getNumberic(HSSFCell cell) {
		double number=cell.getNumericCellValue();
		Date date=DateUtil.getJavaDate(number);
		String cellValue=null;
		//获得当前cell的日期格式
		short dataFormat=cell.getCellStyle().getDataFormat();
		//自定义的日期格式
		SimpleDateFormat simpleDateFormat=null;
		
//			判断单元格是否为日期
//			所有日期格式都可以通过getDataFormat()值来判断
//			m月d日  ----------	58
//			yyyy-MM-dd-----	14
//			yyyy年m月d日---	31
//			yyyy年m月-------	57
//			HH:mm-----------20
//			h时mm分  -------	32
		
		switch (dataFormat) {
		case 58:
			simpleDateFormat=new SimpleDateFormat("M月d日");
			cellValue=simpleDateFormat.format(date);
			break;
		case 14:
			simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
			cellValue=simpleDateFormat.format(date);
			break;
		case 31:
			simpleDateFormat=new SimpleDateFormat("yyyy年M月d日");
			cellValue=simpleDateFormat.format(date);
			break;
		case 57:
			simpleDateFormat=new SimpleDateFormat("yyyy年M月");
			cellValue=simpleDateFormat.format(date);
			break;
		case 20:
			simpleDateFormat=new SimpleDateFormat("HH:mm");
			cellValue=simpleDateFormat.format(date);
			break;
		case 32:
			simpleDateFormat=new SimpleDateFormat("h时mm分");
			cellValue=simpleDateFormat.format(date);
			break;
			//普通的的数字格式
		default:
			cellValue=""+cell.getNumericCellValue();
			CellStyle cellStyle=cell.getCellStyle();
			DecimalFormat decimalFormat=new DecimalFormat();
			String cellStyleValue=cellStyle.getDataFormatString();
			if("General".equals(cellStyleValue)){
				decimalFormat.applyPattern("#");
			}
			decimalFormat.format(number);
			break;
		}
		
		return cellValue;
	}
	
	//判断单元格cell的格式，区别取值
	public static String getCellValue(HSSFCell cell) {
		// TODO Auto-generated method stub
		String cellValue="";
		//判断该单元格是否为null
		if(cell!=null){
			//对单元格中内容的类型判断
			switch (cell.getCellType()) {
			//数字
			case HSSFCell.CELL_TYPE_NUMERIC:
				cellValue=getNumberic(cell);
				break;
			//字符串
			case HSSFCell.CELL_TYPE_STRING:
				cellValue=""+cell.getStringCellValue();
				break;
			//真假类型
			case HSSFCell.CELL_TYPE_BOOLEAN:
				cellValue=""+ cell.getBooleanCellValue();
				break;
			//公式
			case HSSFCell.CELL_TYPE_FORMULA:
				cellValue=""+ cell.getCellFormula();
				break;
			//空白
			case HSSFCell.CELL_TYPE_BLANK:
				cellValue=""+"";
				break;
			//错误
			case HSSFCell.CELL_TYPE_ERROR:
				cellValue=""+"";
				break;
			default:
				break;
			}
		}
		return cellValue;
	}
	//获得表序号为sheetIndex的表名
	public static String getSheetName(int sheetIndex){
		
		String sheetName=wb.getSheetName(sheetIndex);
		
		return sheetName;
	}
	//获得列数的最大值
	public static int getMaxColumnNumber(){
		
		return maxColumnNumber;
		
	}
	//获得一张表组成的HashMap集合
	public static List<OneRow> getSheet(
		String excelFilePath,String password,
		boolean havePassword,boolean isPasswordError,
		Context context,Class passwordClass,
		int sheetIndex,int keyRowIndex) {
		//打开表
		wb = ExcelUtil.readExcelWorkbook(
				excelFilePath,password,
				havePassword,isPasswordError,
				context,passwordClass);
		System.out.println("sheetIndex"+"====="+wb+"========="+sheetIndex);
		//打开Excel文件中的第SHEET_NUMBER(1)张表
		HSSFSheet hs=(HSSFSheet) wb.getSheetAt(sheetIndex);
		//总的行数，记住numberOfRows要加一
		int numberOfRows=hs.getLastRowNum()+1;
		//先给列数初始化为1
		int colunmNumber=1;
		
		//用来装OneRow的集合
		map_rows = new ArrayList<OneRow>();
		//每一行是一个循环
		for (int i = 0; i < numberOfRows; i++) {
			//每当一个循环之前，创建一个Rows类和其中的集合
			OneRow oneRow=new OneRow();
			oneRow.creatMap();
			oneRow.creatList_oneCell();
			//获得当前的第i行
			HSSFRow row=hs.getRow(i);
			//判断row是否为null
			if(row!=null){
				//得到该行的列数
				colunmNumber=row.getLastCellNum();
				if(colunmNumber>maxColumnNumber){
					maxColumnNumber=colunmNumber;
				}
				//每一个单元格是一个循环
				for (int j = 0; j <colunmNumber ; j++) {
					//获得单元格对象
					HSSFCell cell=hs.getRow(i).getCell(j);
					//声明单元格的值
					String cellValue=ExcelUtil.getCellValue(cell);
					oneRow.addLastCellValue(cellValue);
				}
			}
			//如果该行为null，则什么都不做
			else{
				for (int j = 0; j < colunmNumber; j++) {
					oneRow.addLastCellValue("");
				}
			}
			//每次循环到末尾向Rows集合中添加oneRow
			map_rows.add(oneRow);
			//清空oneRow的值
			oneRow=null;
		}
		return map_rows;
	}
	//获取表的数目
	public static int getSheetsNumber(){
		int sheetNumber=wb.getNumberOfSheets();
		return sheetNumber;
	}
	
	//将一xls结尾的转化成xlsx
	public static String changeToXSSF(String oldExcelFilePath){
		StringBuilder sb=new StringBuilder(oldExcelFilePath);
		sb.append("x");
		String newExcelFilePath=sb.toString();
		return newExcelFilePath;
	}
	//获得一张表中行数个数
	public static List<String> getList_RowsIndex(){
		List<String> list_rowsIndex=new ArrayList<String>();
		
		for (int i = 0; i < map_rows.size(); i++) {
			list_rowsIndex.add("第"+(i+1)+"行");
		}
		return list_rowsIndex;
	}
}
