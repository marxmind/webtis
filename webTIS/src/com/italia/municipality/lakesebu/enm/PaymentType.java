package com.italia.municipality.lakesebu.enm;

public enum PaymentType {
	
	PARTIAL(1, "PARTIAL"),
	FULL(2, "FULL");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private PaymentType(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String nameId(int id){
		
		for(PaymentType type : PaymentType.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		
		return PaymentType.FULL.getName();
	}
	
	public static int idName(String name){
		
		for(PaymentType type : PaymentType.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		
		return PaymentType.FULL.getId();
	}
}
