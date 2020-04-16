package com.italia.marxmind.appUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 04/06/2020
 *
 */
public class TimeUtils {
	/**
	 * 	
	 * @return current date with time
	 * @format MM-dd-yyyy 12:00 AM
	 */
	public static String getCurrentDateMMDDYYYYTIME(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm: a");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * 	
	 * @return current date with time
	 * @format hh:mm am/pm 12:00 AM
	 */
	public static String getTime12Format(){
		DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * 	
	 * @return current date with time
	 * @format hh:mm am/pm 24:00 AM
	 */
	public static String getTime24Format(){
		DateFormat dateFormat = new SimpleDateFormat("kk:mm aa");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	
	public static void main(String[] args) {
		System.out.println(getTime12Format());
		System.out.println(getTime24Format());
	}
	
	public static String checkTime(String time) {
		
		String ampm = time.split(" ")[1];
		String mm = time.split(":")[1];
		if("PM".equalsIgnoreCase(ampm)) {
			int hh = time24Format(Integer.valueOf(time.split(":")[0]));
			return hh+":"+mm;
		}
		
		return time;
	}
	
	private static int time24Format(int hour) {
		
		switch(hour) {
		case 1 : return 13;
		case 2 : return 14;
		case 3 : return 15;
		case 4 : return 16;
		case 5 : return 17;
		case 6 : return 18;
		case 7 : return 19;
		case 8 : return 20;
		case 9 : return 21;
		case 10 : return 22;
		case 11 : return 23;
		}
		
		return 0;
	}
}
