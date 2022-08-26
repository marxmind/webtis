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
	UKAY_UKAY(11, "UKAY-UKAY"),
	RESERVED(12, "RESERVED"),
	AGRIVET(13, "AGRIVET"),
	WHOLESALER(14, "WHOLESALER"),
	RETAILER(15, "RETAILER"),
	TRANSPORT_COOP(16, "TRANSPORT COOP"),
	SOUVENIR_SHOP(17, "SOUVENIR SHOP"),
	AMUSEMENT(18, "AMUSEMENT"),
	OTHERS(19, "OTHERS");
	
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
