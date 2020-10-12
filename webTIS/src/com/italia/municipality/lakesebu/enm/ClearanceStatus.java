package com.italia.municipality.lakesebu.enm;

/**
 * 
 * @author mark italia
 * @since 07/7/2017
 * @version 1.0
 *
 */

public enum ClearanceStatus {

	DRAFT(1, "DRAFT"),
	COMPLETED(2,"COMPLETED");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private ClearanceStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(ClearanceStatus type : ClearanceStatus.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return ClearanceStatus.DRAFT.getName();
	}
	public static int typeId(String name){
		for(ClearanceStatus type : ClearanceStatus.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return ClearanceStatus.DRAFT.getId();
	}

	
}
