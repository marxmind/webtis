package com.italia.municipality.lakesebu.enm;

/**
 * 
 * @author mark italia
 * @since 06/16/2018
 * @version 1.0
 */
public enum StallType {

	GROCERRY(1, "GROCERRY"),
	RESTAURANT(2, "RESTAURANT"),
	SALON(3, "SALON"),
	BODEGA(4, "BODEGA"),
	MERCHANDISE(5, "MERCHANDISE"),
	BAKERY(6, "BAKERY"),
	TAILORING(7, "TAILORING"),
	MARKET_OFFICE(8, "MARKET OFFICE"),
	COOP(9, "COOPERATIVE"),
	AMENITIES(10, "AMENITIES"),
	RESERVED(11, "RESERVED");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private StallType(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String nameId(int id){
		
		for(StallType type : StallType.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		
		return StallType.GROCERRY.getName();
	}
	
	public static int idName(String name){
		
		for(StallType type : StallType.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		
		return StallType.GROCERRY.getId();
	}
	
}
