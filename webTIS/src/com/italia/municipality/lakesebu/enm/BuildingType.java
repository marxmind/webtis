package com.italia.municipality.lakesebu.enm;

/**
 * 
 * @author mark italia
 * @since 06/15/2018
 * @version 1.0
 */
public enum BuildingType {

	NEW_BUILD(1, "NEW BUILDING"),
	OLD_BUILD(2, "OLD BUILDING"),
	GROUND(3, "GROUND RENTALS");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private BuildingType(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String nameId(int id){
		
		for(BuildingType type : BuildingType.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		
		return BuildingType.NEW_BUILD.getName();
	}
	
	public static int idName(String name){
		
		for(BuildingType type : BuildingType.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		
		return BuildingType.NEW_BUILD.getId();
	}
}	
