package com.italia.municipality.lakesebu.controller;

import com.italia.municipality.lakesebu.utils.DateUtils;

public class RevertDate {

	public static void main(String[] args) {
		RevertDate.replaceDateFormat();
	}
	
	public static void replaceDateFormat(){
		String sql = "SELECT * FROM tbl_chequedtls WHERE cheque_id>=1 AND cheque_id<=1007"; //order by date_edited desc limit 1
		
		for(Chequedtls chk : Chequedtls.retrieve(sql, new String[0])){
			String newDateFormat = "ERROR"; 
					
			try{newDateFormat = convertDateToYearMontyDay(chk.getDate_disbursement());}catch(Exception e){newDateFormat=chk.getDate_disbursement();}
			//System.out.println(newDateFormat);
			//chk.setDate_disbursement(newDateFormat);
			//chk.save();
			 
			System.out.println("UPDATE  tbl_chequedtls set date_disbursement='"+ newDateFormat +"' WHERE cheque_id="+chk.getCheque_id() + ";");
			
		}
		
	}
	
	
	/**
	 * 
	 * @param dateVal Month day, Year 
	 * @return YYYY-MM-DD
	 */
	private static String convertDateToYearMontyDay(String dateVal){
		//System.out.println("Date : " + dateVal);
		if(dateVal==null || dateVal.isEmpty()){
			dateVal = DateUtils.getCurrentDateMMMMDDYYYY();
		}
		String tmp = dateVal.split(",")[0];
		String month = tmp.split(" ")[0];
		String day = tmp.split(" ")[1];
		String year = dateVal.split(",")[1].trim();
		dateVal = year + "-" + getMonthNumber(month) + "-" + day;
		return dateVal;
	}
	
	private static String getMonthNumber(String month){
		switch(month){
			case "January": return "01";
			case "February" : return "02";
			case "March" : return "03";
			case "April" : return "04";
			case "May" : return "05";
			case "June" : return "06";
			case "July" : return "07";
			case "August" : return "08";
			case "September" :  return "09";
			case "October" : return "10";
			case "November" : return "11";
			case "December" : return "12";
		}
		return "January";
	}
	
}
