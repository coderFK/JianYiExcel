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
	//ͨ��·������һ��HSSFWorkbook
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
	//�����µ�Excel�ļ�
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
//		//��������������뱣��
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
	//��Excel�ļ����Workbook
	public static HSSFWorkbook readExcelWorkbook(
			String excelFilePath2,String password,
			boolean havePassword,boolean isPasswordError,
			Context context,Class passwordClass) {
		// TODO Auto-generated method stub
		//����IO��
		FileInputStream input = null;
		BufferedInputStream binput = null;
		POIFSFileSystem poifs = null;
		HSSFWorkbook wb=null;

		try {
			
			//��·��ΪEXCEL_FILEPATH��Excel�ļ�
			input = new FileInputStream(excelFilePath2);
			binput = new BufferedInputStream(input);
			//��ȡExcel�ļ������������õ�Excel�ļ�����������
			poifs = new POIFSFileSystem(input);
			//���������ж�
			if(havePassword){
				//�ж�����������Ƿ���ȷ
				Biff8EncryptionKey.setCurrentUserPassword(password);
			}
			//������ȷ���¼�������Ա���淽�㱣����
			Editor editor=context.getSharedPreferences(
					"excelFileDate", context.MODE_PRIVATE).edit();
			editor.putString("password", password);
			editor.commit();
			
			//���Workbook
			wb = new HSSFWorkbook(poifs);
			
		} catch (IOException e) {
			System.out.println(e.toString());
		} 
		
		//������Ļ�����ת����Ҫ�����Activity
		catch (EncryptedDocumentException e) {
			//��1�������������ȷ�������������
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
	
	//��ȡ����
	public static void getPassword(Context context,
			Class passwordClass,boolean isPasswordError){
		Intent intentEnterPassword=new Intent(context,passwordClass);
		//����flag�ж������Ƿ��д���
		intentEnterPassword.putExtra("isPasswordError", isPasswordError);
		context.startActivity(intentEnterPassword);
		((Activity) context).finish();
	}
	
	//������ʽΪ���ֵ�Cell�������˶����ڵĽ���
	public static String getNumberic(HSSFCell cell) {
		double number=cell.getNumericCellValue();
		Date date=DateUtil.getJavaDate(number);
		String cellValue=null;
		//��õ�ǰcell�����ڸ�ʽ
		short dataFormat=cell.getCellStyle().getDataFormat();
		//�Զ�������ڸ�ʽ
		SimpleDateFormat simpleDateFormat=null;
		
//			�жϵ�Ԫ���Ƿ�Ϊ����
//			�������ڸ�ʽ������ͨ��getDataFormat()ֵ���ж�
//			m��d��  ----------	58
//			yyyy-MM-dd-----	14
//			yyyy��m��d��---	31
//			yyyy��m��-------	57
//			HH:mm-----------20
//			hʱmm��  -------	32
		
		switch (dataFormat) {
		case 58:
			simpleDateFormat=new SimpleDateFormat("M��d��");
			cellValue=simpleDateFormat.format(date);
			break;
		case 14:
			simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
			cellValue=simpleDateFormat.format(date);
			break;
		case 31:
			simpleDateFormat=new SimpleDateFormat("yyyy��M��d��");
			cellValue=simpleDateFormat.format(date);
			break;
		case 57:
			simpleDateFormat=new SimpleDateFormat("yyyy��M��");
			cellValue=simpleDateFormat.format(date);
			break;
		case 20:
			simpleDateFormat=new SimpleDateFormat("HH:mm");
			cellValue=simpleDateFormat.format(date);
			break;
		case 32:
			simpleDateFormat=new SimpleDateFormat("hʱmm��");
			cellValue=simpleDateFormat.format(date);
			break;
			//��ͨ�ĵ����ָ�ʽ
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
	
	//�жϵ�Ԫ��cell�ĸ�ʽ������ȡֵ
	public static String getCellValue(HSSFCell cell) {
		// TODO Auto-generated method stub
		String cellValue="";
		//�жϸõ�Ԫ���Ƿ�Ϊnull
		if(cell!=null){
			//�Ե�Ԫ�������ݵ������ж�
			switch (cell.getCellType()) {
			//����
			case HSSFCell.CELL_TYPE_NUMERIC:
				cellValue=getNumberic(cell);
				break;
			//�ַ���
			case HSSFCell.CELL_TYPE_STRING:
				cellValue=""+cell.getStringCellValue();
				break;
			//�������
			case HSSFCell.CELL_TYPE_BOOLEAN:
				cellValue=""+ cell.getBooleanCellValue();
				break;
			//��ʽ
			case HSSFCell.CELL_TYPE_FORMULA:
				cellValue=""+ cell.getCellFormula();
				break;
			//�հ�
			case HSSFCell.CELL_TYPE_BLANK:
				cellValue=""+"";
				break;
			//����
			case HSSFCell.CELL_TYPE_ERROR:
				cellValue=""+"";
				break;
			default:
				break;
			}
		}
		return cellValue;
	}
	//��ñ����ΪsheetIndex�ı���
	public static String getSheetName(int sheetIndex){
		
		String sheetName=wb.getSheetName(sheetIndex);
		
		return sheetName;
	}
	//������������ֵ
	public static int getMaxColumnNumber(){
		
		return maxColumnNumber;
		
	}
	//���һ�ű���ɵ�HashMap����
	public static List<OneRow> getSheet(
		String excelFilePath,String password,
		boolean havePassword,boolean isPasswordError,
		Context context,Class passwordClass,
		int sheetIndex,int keyRowIndex) {
		//�򿪱�
		wb = ExcelUtil.readExcelWorkbook(
				excelFilePath,password,
				havePassword,isPasswordError,
				context,passwordClass);
		System.out.println("sheetIndex"+"====="+wb+"========="+sheetIndex);
		//��Excel�ļ��еĵ�SHEET_NUMBER(1)�ű�
		HSSFSheet hs=(HSSFSheet) wb.getSheetAt(sheetIndex);
		//�ܵ���������סnumberOfRowsҪ��һ
		int numberOfRows=hs.getLastRowNum()+1;
		//�ȸ�������ʼ��Ϊ1
		int colunmNumber=1;
		
		//����װOneRow�ļ���
		map_rows = new ArrayList<OneRow>();
		//ÿһ����һ��ѭ��
		for (int i = 0; i < numberOfRows; i++) {
			//ÿ��һ��ѭ��֮ǰ������һ��Rows������еļ���
			OneRow oneRow=new OneRow();
			oneRow.creatMap();
			oneRow.creatList_oneCell();
			//��õ�ǰ�ĵ�i��
			HSSFRow row=hs.getRow(i);
			//�ж�row�Ƿ�Ϊnull
			if(row!=null){
				//�õ����е�����
				colunmNumber=row.getLastCellNum();
				if(colunmNumber>maxColumnNumber){
					maxColumnNumber=colunmNumber;
				}
				//ÿһ����Ԫ����һ��ѭ��
				for (int j = 0; j <colunmNumber ; j++) {
					//��õ�Ԫ�����
					HSSFCell cell=hs.getRow(i).getCell(j);
					//������Ԫ���ֵ
					String cellValue=ExcelUtil.getCellValue(cell);
					oneRow.addLastCellValue(cellValue);
				}
			}
			//�������Ϊnull����ʲô������
			else{
				for (int j = 0; j < colunmNumber; j++) {
					oneRow.addLastCellValue("");
				}
			}
			//ÿ��ѭ����ĩβ��Rows���������oneRow
			map_rows.add(oneRow);
			//���oneRow��ֵ
			oneRow=null;
		}
		return map_rows;
	}
	//��ȡ�����Ŀ
	public static int getSheetsNumber(){
		int sheetNumber=wb.getNumberOfSheets();
		return sheetNumber;
	}
	
	//��һxls��β��ת����xlsx
	public static String changeToXSSF(String oldExcelFilePath){
		StringBuilder sb=new StringBuilder(oldExcelFilePath);
		sb.append("x");
		String newExcelFilePath=sb.toString();
		return newExcelFilePath;
	}
	//���һ�ű�����������
	public static List<String> getList_RowsIndex(){
		List<String> list_rowsIndex=new ArrayList<String>();
		
		for (int i = 0; i < map_rows.size(); i++) {
			list_rowsIndex.add("��"+(i+1)+"��");
		}
		return list_rowsIndex;
	}
}
