package com.italia.municipality.lakesebu.utils;

import java.math.BigDecimal;

/**
 * 
 * @author mark italia
 * @since 09/28/2016
 *
 */

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Currency {
	
	public static void main(String[] args) {
		//String money = "$1234000.01";
		//money = formatAmount(money);
		//System.out.println(money);
		//System.out.println(formatAmount(money));
		double amount = 14866910.99;
		System.out.println("Php: " + formatAmount(amount));
	}
	
	public static String removeCurrencySymbol(String value, String replaceChr){
		
		/*String[] symbols = {"Php","php","PHP","$",","};
		if(value==null) return "";
		for(String symbol : symbols){
			value = value.replace(symbol, replaceChr);
		}*/
		String[] symbols = {"Php","php","PHP","$"};
		if(value==null) return "";
		for(String symbol : symbols){
			value = value.replace(symbol, replaceChr);
		}
		
		return value;
	}
	
	public static String removeComma(String value){
		//value = value.replace(",", "");
		
		if(value==null) return "0";
		if(value.isEmpty()) return "0";
		try{
			value = value.replace(",", "");
			value = value.replace("$", "");
			value = value.replace("Php", "");
			value = value.replace("\\u20B1", "");	
		NumberFormat format = NumberFormat.getCurrencyInstance();
		Number number = format.parse(value);
		return number.toString();
		}catch(ParseException e) {}
		
		return "";
	}
	
	public static String formatAmount(BigDecimal amount){
		return formatAmount(amount+"");
	}
	
	public static String formatAmount(double amount){
		amount = roundOf(amount, 2);
		return formatAmount(amount+"");
	}
	
	public static String formatAmount(int amount){
		return formatAmount(amount+"");
	}
	
	private static double roundOf(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	/*
	public static String formatAmount(String amount){
		
		if(amount==null) return "0";
		if(amount.isEmpty()) return "0";
		try{
		amount = amount.replaceAll("[^\\d.]", "");	
		double money = Double.valueOf(amount.replace(",", ""));
		NumberFormat format = NumberFormat.getCurrencyInstance();
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(4);
		amount = format.format(money);
		
		}catch(Exception e){
			
		}
		return amount;
	}*/
	
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
			int len  = dec.length();
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
		amount = formatAmount(amount);	
		amnt = Double.valueOf(amount);
		return amnt;
		/*
		if(amount == null) return 0d;
		if(amount.isEmpty()) return 0d;
		double amnt = 0d;
		amount = amount.replace(",", "");
		amnt = Double.valueOf(amount);
		return amnt;*/
		
		
	}
	
}

