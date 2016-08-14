package com.app.jianyiexcel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneRow {

	int _id;
	
	Map<String, String> map_oneRow;

	List<String> list_oneCell;
	
	//������_id
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getCellValue(int position) {
		return list_oneCell.get(position);
	}

	public void addCellValue(int index,String cellValue) {
		list_oneCell.add(index,cellValue);
	}
	public void addLastCellValue(String cellValue) {
		list_oneCell.add(cellValue);
	}
	public void deleteCellValue(int index) {
		list_oneCell.remove(index);
	}
	public void deleteLastCellValue() {
		list_oneCell.remove(list_oneCell.size()-1);
	}

	public List<String> getList_oneCell() {
		return list_oneCell;
	}

	public void creatList_oneCell() {
		list_oneCell=new ArrayList<String>();
	}
	public void setList_oneCell(List<String> list_CellValues) {
		list_oneCell=list_CellValues;
	}
	
	//��Map����ȷ����ͻ�ȡ����
	public Map<String, String> getMap_oneRow() {
		return map_oneRow;
	}

	public void setMap_oneRow(Map<String, String> map_oneRow) {
		this.map_oneRow = map_oneRow;
	}
	//����Map����
	public void creatMap(){
		map_oneRow=new HashMap<String, String>();
	}
	
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < list_oneCell.size(); i++) {
			sb.append(list_oneCell.get(i)+" ");
		}
		return sb.toString();
	}
}
