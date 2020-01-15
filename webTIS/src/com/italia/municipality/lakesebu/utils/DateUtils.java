package com.italia.municipality.lakesebu.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.italia.municipality.lakesebu.enm.FormType;
/**
 * 
 * @author mark italia
 * @since 11/12/2016
 * @version 1.0
 *
 */
public class DateUtils {

	/**
	 * 	
	 * @return current date
	 * @format MMMM dd, yyyy (eg January 01, 2016)
	 */
	public static String getCurrentDateMMMMDDYYYY(){
		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * 	
	 * @return current date
	 * @format MM-dd-yyyy
	 */
	public static String getCurrentDateMMDDYYYY(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");//new SimpleDateFormat("MM/dd/yyyy");//new SimpleDateFormat("yyyy/MM/dd hh:mm: a");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * 	
	 * @return current date
	 * @format yyyy-MM-dd
	 */
	public static String getCurrentDateYYYYMMDD(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * 	
	 * @return current date
	 * @format MMddyyyy
	 */
	public static String getCurrentDateMMDDYYYYPlain(){
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * 	
	 * @return current date with time
	 * @format MM-dd-yyyy hh:mm:ss a
	 */
	public static String getCurrentDateMMDDYYYYTIME(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String getCurrentDateMMDDYYYYTIMEPlain(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyyhhmmss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static int getCurrentDay(){
		java.util.Date date= new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DATE);
		return day;
	}
	
	public static int getCurrentYear(){
		Calendar now = Calendar.getInstance();   // Gets the current date and time
		int year = now.get(Calendar.YEAR);      // The current year as an int
		return year;
	}
	
	public static int getCurrentMonth(){
		java.util.Date date= new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;//i dont know the reason but you have to add 1 in order to get the accurate month
		return month;
	}
	
	public static String getFirstDayOfTheMonth(String dateFormat,String dateInputed, Locale locale){
		String date="";
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, locale);
		LocalDate now = LocalDate.parse(dateInputed, dateTimeFormatter);
		LocalDate initial = now.of(getCurrentYear(), getCurrentMonth(), 13);
		LocalDate start = initial.withDayOfMonth(1);
		//LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
		return start.toString();
	}
	
	public static String getLastDayOfTheMonth(String dateFormat,String dateInputed, Locale locale){
		String date="";
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, locale);
		LocalDate now = LocalDate.parse(dateInputed, dateTimeFormatter);
		LocalDate initial = now.of(getCurrentYear(), getCurrentMonth(), 13);
		//LocalDate start = initial.withDayOfMonth(1);
		LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
		return end.toString();
	}
	
	public static String getLastDayOfTheMonth(String dateFormat,String dateInputed, Locale locale, int year, int month){
		String date="";
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, locale);
		LocalDate now = LocalDate.parse(dateInputed, dateTimeFormatter);
		LocalDate initial = now.of(year, month, 13);
		//LocalDate start = initial.withDayOfMonth(1);
		LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
		return end.toString();
	}
	
	/**Not in use
	 * 
	 * @param dateFormat (eg. mm-dd-yyyy)
	 * @param locale (eg. Locale.TAIWAN/Locale.US)
	 * @return last date of the month(eg. 31)
	 */
	public static String getEndOfMonthDate(String dateFormat, Locale locale){
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, locale);
		LocalDate now = LocalDate.parse(DateUtils.getCurrentDateMMDDYYYY(), dateTimeFormatter);
		LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth()); //2015-11-30
		
		String mm="",dd="",yyyy="",endOfMonth=lastDay.atStartOfDay().toString().split("T")[0];
		yyyy=endOfMonth.split("-")[0];
		mm=endOfMonth.split("-")[1];
		dd=endOfMonth.split("-")[2];
		
		return mm + "-" + dd + "-" + yyyy;
	}
	
	/**
	 * Convert 24 format hour to 12 format
	 * @param hour
	 * @return
	 */
	public static String timeTo12Format(String time, boolean isIncludePM){
		int hh=Integer.valueOf(time.split(":")[0]);
		String mm = time.split(":")[1];
		String ss = time.split(":")[2];
		String result = isIncludePM? "00:"+ mm +":"+ ss +" PM" : "00:"+ mm +":"+ ss;
		
		if(hh<=12){
			if(hh<=9){
				result = isIncludePM?  "0"+hh+":"+ mm +":"+ ss +" AM" : "00:"+ mm +":"+ ss;
			}else{
				result = isIncludePM?  hh+":"+ mm +":"+ ss +" AM" : "00:"+ mm +":"+ ss;
			}
		}
		switch(hh){
			case 13 : result = isIncludePM? "01:"+ mm +":"+ ss +" PM" : "01:"+ mm +":"+ ss;
			case 14 : result = isIncludePM? "02:"+ mm +":"+ ss +" PM" : "02:"+ mm +":"+ ss;
			case 15 : result = isIncludePM? "03:"+ mm +":"+ ss +" PM" : "03:"+ mm +":"+ ss;
			case 16 : result = isIncludePM? "04:"+ mm +":"+ ss +" PM" : "04:"+ mm +":"+ ss;
			case 17 : result = isIncludePM? "05:"+ mm +":"+ ss +" PM" : "05:"+ mm +":"+ ss;
			case 18 : result = isIncludePM? "06:"+ mm +":"+ ss +" PM" : "06:"+ mm +":"+ ss;
			case 19 : result = isIncludePM? "07:"+ mm +":"+ ss +" PM" : "07:"+ mm +":"+ ss;
			case 20 : result = isIncludePM? "08:"+ mm +":"+ ss +" PM" : "08:"+ mm +":"+ ss;
			case 21 : result = isIncludePM? "09:"+ mm +":"+ ss +" PM" : "09:"+ mm +":"+ ss;
			case 22 : result = isIncludePM? "10:"+ mm +":"+ ss +" PM" : "10:"+ mm +":"+ ss;
			case 23 : result = isIncludePM? "11:"+ mm +":"+ ss +" PM" : "11:"+ mm +":"+ ss;
			case 00 : result = isIncludePM? "12:"+ mm +":"+ ss +" PM" : "12:"+ mm +":"+ ss;
		}
		return result;
	}
	
	/**
	 * 
	 * @param dateVal YYYY-MM-DD
	 * @return Month day, Year
	 */
	public static String convertDateToMonthDayYear(String dateVal){
		//System.out.println("Date : " + dateVal);
		if(dateVal==null || dateVal.isEmpty()){
			dateVal = DateUtils.getCurrentDateYYYYMMDD();
		}
		int month = Integer.valueOf(dateVal.split("-")[1]); 
		String year = dateVal.split("-")[0];
		int day = Integer.valueOf(dateVal.split("-")[2]);
		
		if(day<10){
			dateVal = getMonthName(month) + " 0"+day + ", " + year;
		}else{
			dateVal = getMonthName(month) + " "+day + ", " + year;
		}
		//System.out.println("return date: "+ dateVal);
		return dateVal;
	}
	
	/**
	 * 
	 * @param dateVal Month day, Year 
	 * @return YYYY-MM-DD
	 */
	public static String convertDateToYearMontyDay(String dateVal){
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
	
	public static String getMonthNumber(String month){
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
	
	public static String getMonthName(int month){
		switch(month){
			case 1: return "January";
			case 2 : return "February";
			case 3 : return "March";
			case 4 : return "April";
			case 5 : return "May";
			case 6 : return "June";
			case 7 : return "July";
			case 8 : return "August";
			case 9 : return "September";
			case 10 : return "October";
			case 11 : return "November";
			case 12 : return "December";
		}
		return "January";
	}
	
	/**
	 * 
	 * @param date
	 * @param format default(yyyy-MM-dd)
	 * @return string date format
	 */
	public static String convertDate(Date date, String format){
		try{
		if(format==null || format.isEmpty()) format ="yyyy-MM-dd";
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 
	 * @param datevalue
	 * @param format
	 * @return date
	 */
	public static Date convertDateString(String datevalue, String format){
		
		try{
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat(format);
		date = dateFormat.parse(datevalue);
		return date;
		}catch(Exception e){}
		return null;
	}
	
	public static Date getDateToday(){
		return new Date();
	}
	
	
	public static void main(String[] args) {
		System.out.println(DateUtils.getFirstDayOfTheMonth("yyyy-MM-dd",DateUtils.getCurrentDateYYYYMMDD(), Locale.TAIWAN));
		System.out.println(DateUtils.getLastDayOfTheMonth("yyyy-MM-dd",DateUtils.getCurrentDateYYYYMMDD(), Locale.TAIWAN));
		
		
		System.out.println("year: " + (DateUtils.getCurrentYear()+"").substring(2, 4));
		System.out.println("month: " + DateUtils.getCurrentMonth());
		/*
		LocalDate initial = LocalDate.of(2016, 2, 13);
		LocalDate start = initial.withDayOfMonth(1);
		LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
		
		System.out.println("First : " + start.toString() + " last " + end);
		*/
		
	}
	
	public static String numberResult(int formId, long series) {
		String result = "";
		
		String start = series+"";
		int startSize = start.length();
		
		
		if(FormType.CTC_INDIVIDUAL.getId()==formId || FormType.CTC_CORPORATION.getId()==formId) {//for cedula
			
			switch(startSize) {
			case 1 : result = "0000000"+start; break;
			case 2 : result = "000000"+start; break;
			case 3 : result = "00000"+start; break;
			case 4 : result = "0000"+start; break;
			case 5 : result = "000"+start; break;
			case 6 : result = "00"+start; break;
			case 7 : result = "0"+start; break;
			case 8 : result = start; break;
			}
			
			
		}else {
			
			switch(startSize) {
			case 1 : result = "000000"+start; break;
			case 2 : result = "00000"+start; break;
			case 3 : result = "0000"+start; break;
			case 4 : result = "000"+start; break;
			case 5 : result = "00"+start; break;
			case 6 : result = "0"+start; break;
			case 7 : result = start; break;
			
			}
			
		}
		
		return result;
	}
	
	public static String numberResult2(String formId, String series) {
		String result = "";
		
		if(series==null || series.isEmpty()) {
			result = series;
		}else {
		
		long startLong = Long.valueOf(series);
		String start = startLong + "";
		int formId2 = Integer.valueOf(formId);
		int startSize = start.length();
		
		
		if(FormType.CTC_INDIVIDUAL.getId()==formId2 || FormType.CTC_CORPORATION.getId()==formId2) {//for cedula
			
			switch(startSize) {
			case 1 : result = "0000000"+start; break;
			case 2 : result = "000000"+start; break;
			case 3 : result = "00000"+start; break;
			case 4 : result = "0000"+start; break;
			case 5 : result = "000"+start; break;
			case 6 : result = "00"+start; break;
			case 7 : result = "0"+start; break;
			case 8 : result = start; break;
			}
			
			
		}else {
			
			switch(startSize) {
			case 1 : result = "000000"+start; break;
			case 2 : result = "00000"+start; break;
			case 3 : result = "0000"+start; break;
			case 4 : result = "000"+start; break;
			case 5 : result = "00"+start; break;
			case 6 : result = "0"+start; break;
			case 7 : result = start; break;
			
			}
			
		}
		
		}
		
		return result;
	}
	
}


