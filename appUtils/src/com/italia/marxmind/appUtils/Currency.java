package com.italia.marxmind.appUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
/**
 * 
 * @author mark italia
 * @since 09/28/2016
 *
 */

public class Currency {

	public static String removeCurrencySymbol(String value, String replaceChr){
		String[] symbols = {"Php","php","PHP","$"};
		if(value==null) return "";
		for(String symbol : symbols){
			value = value.replace(symbol, replaceChr);
		}
		
		return value;
	}
	
	public static String removeComma(String value){
		value = value.replace(",", "");
		return value;
	}
	
	public static String formatAmount(BigDecimal amount){
		return formatAmount(amount+"");
	}
	
	public static String formatAmount(double amount){
		return formatAmount(amount+"");
	}
	
	public static String formatAmount(String amount){
		if(amount==null) return "0";
		if(amount.isEmpty()) return "0";
		try{
		amount = amount.replace(",", "");
		amount = amount.replace("$", "");
		amount = amount.replace("Php", "");
		amount = amount.replace("\\u20B1", "");
		double money = Double.valueOf(amount.replace(",", ""));
		NumberFormat format = NumberFormat.getNumberInstance();
		amount = format.format(money).replace("$", "");
		amount = amount.replace("Php", "");
		
		if(amount.contains(".")) {
			String dec = amount.split("\\.")[1];
			int len = dec.length();
			System.out.println("len " + len);
			if(len==1) {
				amount +="0"; 
			}
			
		}else {
			amount += ".00";
		}
		}catch(Exception e){
			
		}
		return amount;
	}
	
	public static double amountDouble(String amount){
		if(amount == null) return 0d;
		if(amount.isEmpty()) return 0d;
		double amnt = 0d;
		amount = amount.replace(",", "");
		amnt = Double.valueOf(amount);
		return amnt;
	}
	
}
