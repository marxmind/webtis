package com.italia.municipality.lakesebu.enm;

public enum Buildings {
	
	BUILDING_1(1, "BUILDING NO 1"),
	BUILDING_2(2, "BUILDING NO 2"),
	BUILDING_3(3, "BUILDING NO 3"),
	BUILDING_4(4, "BUILDING NO 4"),
	BUILDING_5(5, "BUILDING NO 5"),
	BUILDING_6(6, "BUILDING NO 6"),
	BUILDING_7(7, "BUILDING NO 7"),
	BUILDING_8(8, "BUILDING NO 8"),
	BUILDING_9(9, "BUILDING NO 9"),
	BUILDING_10(10, "BUILDING NO 10"),
	BUILDING_11(11, "BUILDING NO 11"),
	BUILDING_12(12, "BUILDING NO 12"),
	GROUND_TERMINAL(13, "GROUND TERMINAL"),
	GROUND(14, "GROUND");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private Buildings(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String nameId(int id){
		
		for(Buildings type : Buildings.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		
		return Buildings.BUILDING_1.getName();
	}
	
	public static int idName(String name){
		
		for(Buildings type : Buildings.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		
		return Buildings.BUILDING_1.getId();
	}
	
}
