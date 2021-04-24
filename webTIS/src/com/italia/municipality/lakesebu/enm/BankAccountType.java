package com.italia.municipality.lakesebu.enm;

/**
 * 
 * @author mark italia
 * @since 04/12/2021
 * @version 1.0
 *
 *refer to bank_cheque.tbl_bankaccounts
 */
public enum BankAccountType {
	SPECIAL_EDUCATION_FUND(1,"SPECIAL EDUCATION FUND"),
	GENERAL_FUND(2, "GENERAL FUND"),
	TRUST_FUND(3,"TRUST FUND"),
	MOTORPOOL(4,"MOTORPOOL"),
	TRUST_FUND_SURALLAH(5,"TRUST FUND SURALLAH"),
	PROFESSIONAL_FEE(6, "PROFESSIONAL FEE"),
	PHILHEALTH(7, "PHILHEALTH")
	;
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private BankAccountType(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(BankAccountType type : BankAccountType.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return BankAccountType.GENERAL_FUND.getName();
	}
	public static int typeId(String name){
		for(BankAccountType type : BankAccountType.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return BankAccountType.GENERAL_FUND.getId();
	}
}
