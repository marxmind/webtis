package com.italia.municipality.lakesebu.property;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BankChequePropertyValues {

	private String result="";
	InputStream inputStream;
	
	public String getPropertyValues() throws IOException{
		
		try{
			Properties prop = new Properties();
			String propFileName = "messages.properties";
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			
			if(inputStream!=null){
				prop.load(inputStream);
			}else{
				throw new FileNotFoundException("Property file "+ propFileName + " not found.");
			}
			
			String month = prop.getProperty("month");
			String day = prop.getProperty("day");
			String year = prop.getProperty("year");
			
			result = "month:"+month+":day:"+day+":year:"+year; 
			
		}catch(Exception e){
			System.out.println("Exception: " + e);
		}finally{
			inputStream.close();
		}
		
		
		return result;
	}
	public static void main(String[] args) {
		BankChequePropertyValues values = new BankChequePropertyValues();
		try{
		String val = values.getPropertyValues();
		System.out.println(val);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
