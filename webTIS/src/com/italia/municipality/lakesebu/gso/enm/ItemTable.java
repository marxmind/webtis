package com.italia.municipality.lakesebu.gso.enm;

import java.util.ArrayList;
import java.util.List;

public enum ItemTable {

	ID(0,"itmid"),
	DATE(1,"itmtransDate"),
	TYPE(2,"itmtype"),
	DESCRIPTION(3,"itmdescription"),
	QUANTITY(4,"itmquantity"),
	UNIT(5,"itmunit"),
	UNIT_COST(6,"itmunitcost"),
	TOTAL_COST(7,"itmtotalcost "),
	FIRST_QTY(8,"itmfirstQtrQty"),
	FIRST_AMNT(9,"itmfirstQtrAmnt"),
	SECOND_QTY(10,"itmsecondQtrQty"),
	SECOND_AMNT(11,"itmsecondQtrAmnt"),
	THIRD_QTY(12,"itmthirdQtrQty"),
	THIRD_AMNT(13,"itmthirdQtrAmnt"),
	FOURTH_QTY(14,"itmfourthQtrQty"),
	FOURTH_AMNT(15,"itmfourthQtrAmnt"),
	DEPARTMENT(16,"departmentid"),
	ACTIVE(17,"isactiveitm");
	//TIMESTAMP(18,"timestampitm");
	
	private int id;
	private String name;
	
	private ItemTable(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public static String fields() {
		StringBuilder fld = new StringBuilder();
		int cnt = 1;
		for(ItemTable i : ItemTable.values()) {
			if(cnt>1) {
				fld.append(","+i);
			}else {
				fld.append(i);
			}
			cnt++;
		}
		return fld.toString();
	}
	
	public static String tableInsertStatement() {
		StringBuilder fld = new StringBuilder();
		fld.append("INSERT INTO "+ Table.ITEMS.getName() + " (");
		int cnt = 1;
		StringBuilder n = new StringBuilder();
		for(ItemTable i : ItemTable.values()) {
			if(cnt>1) {
				fld.append(","+i);
				n.append(",?");
			}else {
				fld.append(i);
				n.append("?");
			}
			cnt++;
		}
		fld.append(") VALUES(");
		fld.append(n.toString()).append(")");
		return fld.toString();
	}	
	
	public static String typeName(int id){
		for(ItemTable type : ItemTable.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return ItemTable.ID.getName();
	}
	public static int typeId(String name){
		for(ItemTable type : ItemTable.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return ItemTable.ID.getId();
	}
	
}
