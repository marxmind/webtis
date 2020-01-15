package com.italia.municipality.lakesebu.enm;
/**
 * 
 * @author Mark Italia
 * @since 08-11-2018
 * @version 1.0
 *
 */
public enum FormType {

	CTC_INDIVIDUAL(1, "CTC I."),
	CTC_CORPORATION(2, "CTC C."),
	AF_51(3, "AF-51"),
	AF_52(4, "AF-52"),
	AF_53(5, "AF-53"),
	AF_54(6, "AF-54"),
	AF_55(7, "AF-56"),
	AF_47(8, "AF-47"),
	CT_2(9,"CT 2.00"),
	CT_5(10,"CT 5.00");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private FormType(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String nameId(int id){
		
		for(FormType type : FormType.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		
		return FormType.CTC_INDIVIDUAL.getName();
	}
	
	public static int idName(String name){
		
		for(FormType type : FormType.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		
		return FormType.CTC_INDIVIDUAL.getId();
	}
	
}
