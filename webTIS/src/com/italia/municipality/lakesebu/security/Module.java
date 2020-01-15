package com.italia.municipality.lakesebu.security;
/**
 * 
 * @author mark italia
 * @since 01/24/2017
 * @version 1.0
 *
 */
public enum Module {

	CHECK_WRITING(1,"CHECK-WRITING"),
	LAND_TAX(2,"LAND-TAX"),
	CASH_BOOK(3,"CASH-BOOK"),
	VOUCHER_TRACKER(4,"VOUCHER-TRACKER"),
	MOOE(5,"MOOE"),
	CASH_IN_BANK(6,"CASH IN BANK"),
	CASH_IN_TREASURY(7,"CASH IN TREASURY"),
	DTR_GENERATOR(8,"DTR GENERATOR"),
	MARKET_RENTALS(9,"MARKET RENTALS"),
	STOCK_RECORDING(10,"STOCK RECORDING"),
	ISSUED_FORM_RECORDING(11,"ISSUED FORM RECORDING"),
	COLLECTORS_RECORDING(12,"COLLECTOR'S RECORDING"),
	GRAPH_REPORTS(13,"GRAPH REPORTS"),
	GENERAL_COLLECTIONS(14,"GENERAL COLLECTIONS"),
	USER_SETTINGS(15,"USER SETTINGS"),
	UPLOAD_FORM(16,"UPLOAD FORM"),
	VOUCHER_RECORDING(17,"VOUCHER RECORDING"),
	WATERBILL(18,"WATER BILLING INFO");
	
	
	
	private int id;
	private String name;
	
	public static String moduleName(int id){
		
		for(Module m : Module.values()){
			if(id==m.getId()){
				return m.getName();
			}
		}
		return Module.CHECK_WRITING.getName();
	}
	
	public static int moduleId(String name){
		
		for(Module m : Module.values()){
			if(name.equalsIgnoreCase(m.getName())){
				return m.getId();
			}
		}
		return Module.CHECK_WRITING.getId();
	}
	
	public static Module selected(int id){
		for(Module m : Module.values()){
			if(id==m.getId()){
				return m;
			}
		}
		return Module.CHECK_WRITING;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private Module(int id, String name){
		this.id = id;
		this.name = name;
	}
	
}
