package com.italia.marxmind.appUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
/**
 * 
 * @author mark italia
 * @since 09/28/2016
 *
 */
public class DateUtils {
	public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }
	
	/**
	 * 
	 * @param pass birthdate format yyyy-mm-dd
	 * @return
	 */
	public static int calculateAge(String birthdate){
		//int val = 0;
		System.out.println("Calculating age");
		if(birthdate!=null && !birthdate.isEmpty()){
			int year = Integer.valueOf(birthdate.split("-")[0]);
			int month = Integer.valueOf(birthdate.split("-")[1]);
			int day = Integer.valueOf(birthdate.split("-")[2]);
			int age = DateUtils.getCurrentYear() - year;
			
			if(month==DateUtils.getCurrentMonth()){
				if(day>DateUtils.getCurrentDay()){
					age -=1;
				}
			}else if(month>DateUtils.getCurrentMonth()){
				age -=1;
			}
			
			
			return age;
			
		}else{
			return 0;
		}
	}
	
	
	/**
	 * 
	 * @param pass birthdate format yyyy-mm-dd
	 * @return
	 */
	public static double calculateAgeNow(String birthdate){
		//int val = 0;
		
		if(birthdate!=null && !birthdate.isEmpty()){
			int year = Integer.valueOf(birthdate.split("-")[0]);
			int month = Integer.valueOf(birthdate.split("-")[1]);
			int day = Integer.valueOf(birthdate.split("-")[2]);
			double age = 0;
			
			LocalDate birtdate = LocalDate.of(year, month, day);
			age = calculateAge(birtdate, LocalDate.of(DateUtils.getCurrentYear(), DateUtils.getCurrentMonth(), DateUtils.getCurrentDay()));
			
			/*double age = DateUtils.getCurrentYear() - year;
			
			if(age>0){
			
			if(month==DateUtils.getCurrentMonth()){
				if(day>DateUtils.getCurrentDay()){
					
					day = day - DateUtils.getCurrentDay();
					double dayAge = 0.0;
					
					switch(month){
					case 1: dayAge=0.01; break;
					case 2: dayAge=0.02; break;
					case 3: dayAge=0.03; break;
					case 4: dayAge=0.04; break;
					case 5: dayAge=0.05; break;
					case 6: dayAge=0.06; break;
					case 7: dayAge=0.07; break;
					case 8: dayAge=0.08; break;
					case 9: dayAge=0.09; break;
					case 10: dayAge=0.10; break;
					case 11: dayAge=0.11; break;
					case 12: dayAge=1.0; break;
					}
					
					age -=dayAge;
					System.out.println("if 1 >> " + age);
				}
			}else if(month>DateUtils.getCurrentMonth()){
				
				month = month - DateUtils.getCurrentMonth();
				double monthAge = 0.0;
				switch(month){
				case 1: monthAge=0.01; break;
				case 2: monthAge=0.02; break;
				case 3: monthAge=0.03; break;
				case 4: monthAge=0.04; break;
				case 5: monthAge=0.05; break;
				case 6: monthAge=0.06; break;
				case 7: monthAge=0.07; break;
				case 8: monthAge=0.08; break;
				case 9: monthAge=0.09; break;
				case 10: monthAge=0.10; break;
				case 11: monthAge=0.11; break;
				case 12: monthAge=1.0; break;
				}
				
				age -=monthAge;
				System.out.println("if 2 >> " + age);
			}else if(month<DateUtils.getCurrentMonth()){
				
				double monthAge = 0.0;
				double currentMonth = 0.0;
				
				switch(month){
				case 1: monthAge=0.01; break;
				case 2: monthAge=0.02; break;
				case 3: monthAge=0.03; break;
				case 4: monthAge=0.04; break;
				case 5: monthAge=0.05; break;
				case 6: monthAge=0.06; break;
				case 7: monthAge=0.07; break;
				case 8: monthAge=0.08; break;
				case 9: monthAge=0.09; break;
				case 10: monthAge=0.10; break;
				case 11: monthAge=0.11; break;
				case 12: monthAge=1.0; break;
				}
				
				switch(DateUtils.getCurrentMonth()){
				case 1: currentMonth = 0.01; break;
				case 2: currentMonth=0.02; break;
				case 3: currentMonth=0.03; break;
				case 4: currentMonth=0.04; break;
				case 5: currentMonth=0.05; break;
				case 6: currentMonth=0.06; break;
				case 7: currentMonth=0.07; break;
				case 8: currentMonth=0.08; break;
				case 9: currentMonth=0.09; break;
				case 10: currentMonth=0.10; break;
				case 11: currentMonth=0.11; break;
				case 12: currentMonth=1.0; break;
				}
				
				age += (currentMonth - monthAge) ;
				System.out.println("if 3 >> " + age);
			}
			
			}else{
				if(month>getCurrentMonth()){
					age = 0;
				}else if(month==getCurrentMonth()){
					//age = 0.1;
					switch(month){
					case 1: age=0.01; break;
					case 2: age=0.02; break;
					case 3: age=0.03; break;
					case 4: age=0.04; break;
					case 5: age=0.05; break;
					case 6: age=0.06; break;
					case 7: age=0.07; break;
					case 8: age=0.08; break;
					case 9: age=0.09; break;
					case 10: age=0.10; break;
					case 11: age=0.11; break;
					case 12: age=1.0; break;
					}
				}else if(month<getCurrentMonth()){
					age = getCurrentMonth() - month;
					String edad = String.valueOf(age).replace(".0", "");
					int a = Integer.valueOf(edad);
					switch(a){
						case 1: age=0.01; break;
						case 2: age=0.02; break;
						case 3: age=0.03; break;
						case 4: age=0.04; break;
						case 5: age=0.05; break;
						case 6: age=0.06; break;
						case 7: age=0.07; break;
						case 8: age=0.08; break;
						case 9: age=0.09; break;
						case 10: age=0.10; break;
						case 11: age=0.11; break;
						case 12: age=1.0; break;
					}
					
				}
			}*/
			//System.out.println("if 4 >> " + age);
			
			return age;
			
		}else{
			return 0;
		}
	}
	
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
	
	/**
	 * 	
	 * @return current date with time
	 * @format hh:mm:ss a
	 */
	public static String getCurrentTIME(){
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String getCurrentDateMMDDYYYYTIMEPlain(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyyhhmmss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * 
	 * @param dateFormat (eg. mm-dd-yyyy)
	 * @param locale (eg. Locale.TAIWAN/Locale.US)
	 * @return last date of the month(eg. 31)
	 * date
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
	 * 
	 * @param dateFormat 1=MM-dd-yyyy 2=yyyy-MM-dd 3=dd-MM-yyyy
	 * @param locale
	 * @return dateFormat
	 */
	public static String getEndOfMonthDate(int dateFormat, Locale locale){
		String datePatern = "yyyy-MM-dd";
		if(dateFormat==1){
			datePatern = "MM-dd-yyyy";
		}else if(dateFormat==2){
			datePatern = "yyyy-MM-dd";
		}else if(dateFormat==3){
			datePatern = "dd-MM-yyyy";
		}	
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePatern, locale);
		LocalDate now = LocalDate.parse(DateUtils.getCurrentDateMMDDYYYY(), dateTimeFormatter);
		LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth()); //2015-11-30
		
		String mm="",dd="",yyyy="",endOfMonth=lastDay.atStartOfDay().toString().split("T")[0];
		yyyy=endOfMonth.split("-")[0];
		mm=endOfMonth.split("-")[1];
		dd=endOfMonth.split("-")[2];
		
		if(dateFormat==1){
			return mm + "-" + dd + "-" + yyyy;
		}else if(dateFormat==2){
			return yyyy + "-" + mm + "-" + dd;
		}else if(dateFormat==3){
			datePatern = "dd-MM-yyyy";
			return dd + "-" + mm + "-" + yyyy;
		}
		
		return yyyy + "-" + mm + "-" + dd;
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
	
	public static int getLastDayOfTheMonth(String inputedDate,int year, int month, Locale locale){
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale);
		LocalDate now = LocalDate.parse(inputedDate, dateTimeFormatter);
		LocalDate initial = now.of(year, month, 13);
		//LocalDate start = initial.withDayOfMonth(1);
		LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
		return end.getDayOfMonth();
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
			case 13 : result = isIncludePM? "01:"+ mm +":"+ ss +" PM" : "01:"+ mm +":"+ ss; break;
			case 14 : result = isIncludePM? "02:"+ mm +":"+ ss +" PM" : "02:"+ mm +":"+ ss; break;
			case 15 : result = isIncludePM? "03:"+ mm +":"+ ss +" PM" : "03:"+ mm +":"+ ss; break;
			case 16 : result = isIncludePM? "04:"+ mm +":"+ ss +" PM" : "04:"+ mm +":"+ ss; break;
			case 17 : result = isIncludePM? "05:"+ mm +":"+ ss +" PM" : "05:"+ mm +":"+ ss; break;
			case 18 : result = isIncludePM? "06:"+ mm +":"+ ss +" PM" : "06:"+ mm +":"+ ss; break;
			case 19 : result = isIncludePM? "07:"+ mm +":"+ ss +" PM" : "07:"+ mm +":"+ ss; break;
			case 20 : result = isIncludePM? "08:"+ mm +":"+ ss +" PM" : "08:"+ mm +":"+ ss; break;
			case 21 : result = isIncludePM? "09:"+ mm +":"+ ss +" PM" : "09:"+ mm +":"+ ss; break;
			case 22 : result = isIncludePM? "10:"+ mm +":"+ ss +" PM" : "10:"+ mm +":"+ ss; break;
			case 23 : result = isIncludePM? "11:"+ mm +":"+ ss +" PM" : "11:"+ mm +":"+ ss; break;
			case 00 : result = isIncludePM? "12:"+ mm +":"+ ss +" PM" : "12:"+ mm +":"+ ss; break;
		}
		return result;
	}
	
	public static String dayNaming(String date){
		String result = "";
		int len = date.length();
		int day = 0;
		if(len==1)
			day = Integer.valueOf(date);
		else
			day = Integer.valueOf(date.substring(1,2));
		
		int first = Integer.valueOf(date.substring(0, 1));
//		/int second = Integer.valueOf(date.substring(1, 2));
		
		if(first==0 || first==2 || first==3){
			switch(day){
				case 0 : result = "th"; break;
				case 1 : result = "st"; break;
				case 2 : result = "nd"; break;
				case 3 : result = "rd"; break;
				case 4 : result = "th"; break;
				case 5 : result = "th"; break;
				case 6 : result = "th"; break;
				case 7 : result = "th"; break;
				case 8 : result = "th"; break;
				case 9 : result = "th"; break;
			}
		}
		
		if(first==1){
			switch(day){
				case 0 : result = "th"; break;
				case 1 : result = "th"; break;
				case 2 : result = "th"; break;
				case 3 : result = "th"; break;
				case 4 : result = "th"; break;
				case 5 : result = "th"; break;
				case 6 : result = "th"; break;
				case 7 : result = "th"; break;
				case 8 : result = "th"; break;
				case 9 : result = "th"; break;
			}
		}
		
		date = removeFirstZero(date);
		
		
	return date + result;
	}
	
	private static String removeFirstZero(String number) {
		switch(number) {
		case "01": number="1"; break;
		case "02": number="2"; break;
		case "03": number="3"; break;
		case "04": number="4"; break;
		case "05": number="5"; break;
		case "06": number="6"; break;
		case "07": number="7"; break;
		case "08": number="8"; break;
		case "09": number="9"; break;
		}
	 return number;
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
	 * Format MM-DD-YYYY
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
	
	public static String getMonthName(String month) {
		return getMonthName(Integer.valueOf(month));
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
	
	public static int getCurrentMonth(){
		java.util.Date date= new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;//i dont know the reason but you have to add 1 in order to get the accurate month
		return month;
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
	
	/**
	 * 
	 * @param formatStye
	 * @param 1 [yyyy-MM-dd]
	 * @param 2 [MM-dd-yyyy]
	 * @Note applicable only for current month dateValue inputed
	 */
	public static String getDateBaseOnCount(int count, String dateValue, int formatStye){
		int dd=0,mm=0,yy=0;
		int ddd=0,mmm=0,yyy=0;
		String lastDay = "", timeValue;
		System.out.println("getDateBaseOnCount " + dateValue + " format " + formatStye);
		//timeValue = dateValue.split(" ")[1];
		dateValue = dateValue.split(" ")[0];
		
		if(formatStye==1){
			dd = Integer.valueOf(dateValue.split("-")[2]);
			mm = Integer.valueOf(dateValue.split("-")[1]);
			yy = Integer.valueOf(dateValue.split("-")[0]);
			
			lastDay = getLastDayOfTheMonth("yyyy-MM-dd", dateValue, Locale.TAIWAN);
			ddd = Integer.valueOf(lastDay.split("-")[2]);
			mmm = Integer.valueOf(lastDay.split("-")[1]);
			yyy = Integer.valueOf(lastDay.split("-")[0]);
			
			count -=1; //to get the actual date count including today
			
			int additionalDate =0;
			if(mm==getCurrentMonth()){
				
				additionalDate = dd + count;
				
				if(additionalDate<=ddd){ // determine if the additional date is not greater than end date of the month
					dateValue = yy + "-" + (mm<=9? "0"+mm : mm) + "-" + (additionalDate<=9? "0"+ additionalDate : additionalDate);
				}else{
					
					dd = additionalDate - ddd;
					mm +=1; // add plus 1 to get the next month
					dateValue = yy + "-" + (mm<=9? "0"+mm : mm) + "-" + (dd<=9? "0"+ dd : dd);
				}
				
			}
			
		}else if(formatStye==2){
			mm = Integer.valueOf(dateValue.split("-")[0]);
			dd = Integer.valueOf(dateValue.split("-")[1]);
			yy = Integer.valueOf(dateValue.split("-")[2]);
			
			lastDay = getLastDayOfTheMonth("MM-dd-yyyy", dateValue, Locale.TAIWAN);
			
			mmm = Integer.valueOf(lastDay.split("-")[0]);
			ddd = Integer.valueOf(lastDay.split("-")[1]);
			yyy = Integer.valueOf(lastDay.split("-")[2]);
			
			int additionalDate =0;
			if(mm==getCurrentMonth()){
				
				additionalDate = dd + count;
				
				if(additionalDate<=ddd){ // determine if the additional date is not greater than end date of the month
					dateValue = yy + "-" + (mm<=9? "0"+mm : mm) + "-" + (additionalDate<=9? "0"+ additionalDate : additionalDate);
				}else{
					
					dd = additionalDate - ddd;
					mm +=1; // add pluse 1 to get the next month
					dateValue = yy + "-" + (mm<=9? "0"+mm : mm) + "-" + (dd<=9? "0"+ dd : dd);
				}
				
			}
			
		}
		
		
		return dateValue;
	}
	
	/**
	 * 	
	 * @return current date with time
	 * @format yyyy-MM-dd hh:mm:ss a
	 */
	public static String getCurrentDateYYYYMMDDTIME(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static Date getDateFromString(String datevalue, String format){
		Date date = new Date();
		
		try{
		DateFormat dateFormat = new SimpleDateFormat(format);
		date = dateFormat.parse(datevalue);
		}catch(ParseException e){}
		
		return date;
	}
	
	/**
	 * Get a diff between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}

	public static long getRemainingDays(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
	    Collections.reverse(units);
	    Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
	    long milliesRest = diffInMillies;
	    for ( TimeUnit unit : units ) {
	        long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
	        long diffInMilliesForUnit = unit.toMillis(diff);
	        milliesRest = milliesRest - diffInMilliesForUnit;
	        result.put(unit,diff);
	    }
	    return result;
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
	
	public static String getLastDayOfTheMonth(String dateFormat,String dateInputed, Locale locale, int year, int month){
		String date="";
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, locale);
		LocalDate now = LocalDate.parse(dateInputed, dateTimeFormatter);
		LocalDate initial = now.of(year, month, 13);
		//LocalDate start = initial.withDayOfMonth(1);
		LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
		return end.toString();
	}
}
