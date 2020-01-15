package com.italia.municipality.lakesebu.enm;


/**
 * 
 * @author mark italia
 * @since 09/2/2019
 * @version 1.0
 *
 */
public enum WaterBillStatus {

	UNPAID(1, "UNPAID"),
	FULLPAID(2, "FULLPAID"),
	PARTIALPAID(3, "PARTIAL PAYMENT"),
	CANCELLED(4, "CANCELLED");
	
	
	private int id;
	private String name;
	
	public static String moduleName(int id){
		
		for(Pages m : Pages.values()){
			if(id==m.getId()){
				return m.getName();
			}
		}
		return WaterBillStatus.UNPAID.getName();
	}
	
	public static int moduleId(String name){
		
		for(WaterBillStatus m : WaterBillStatus.values()){
			if(name.equalsIgnoreCase(m.getName())){
				return m.getId();
			}
		}
		return WaterBillStatus.UNPAID.getId();
	}
	
	public static WaterBillStatus selected(int id){
		for(WaterBillStatus m : WaterBillStatus.values()){
			if(id==m.getId()){
				return m;
			}
		}
		return WaterBillStatus.UNPAID;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private WaterBillStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
}

