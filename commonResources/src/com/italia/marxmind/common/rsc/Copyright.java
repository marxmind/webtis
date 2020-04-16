package com.italia.marxmind.common.rsc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.italia.marxmind.ConnectDB;
import com.italia.marxmind.appUtils.DateUtils;
/**
 * 
 * @author mark italia
 * @since 09/28/2016
 *
 */
public class Copyright {
	
	private Long id;
	private String copyrightname;
	private String expdate;
	private String appname;
	private String currentversion;
	private String oldversion;
	private String author;
	private String contactno;
	private String email;
	private Timestamp timestamp;
	
	public Copyright (){}
	
	public Copyright(
			Long id,
			String copyrightname,
			String expdate,
			String appname,
			String currentversion,
			String oldversion,
			String author,
			String contactno,
			String email
			){
		this.id = id;
		this.copyrightname = copyrightname;
		this.expdate = expdate;
		this.appname = appname;
		this.currentversion = currentversion;
		this.oldversion = oldversion;
		this.author = author;
		this.contactno = contactno;
		this.email = email;
	}
	
	public static List<Copyright> retrieve(String sql, String[] params){
		List<Copyright> data = new ArrayList<>();//Collections.synchronizedList(new ArrayList<>());
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			Copyright app = new Copyright();
			try{app.setId(rs.getLong("id"));}catch(NullPointerException e){}
			try{app.setCopyrightname(rs.getString("copyrightname"));}catch(NullPointerException e){}
			try{app.setExpdate(rs.getString("expdate"));}catch(NullPointerException e){}
			try{app.setAppname(rs.getString("appname"));}catch(NullPointerException e){}
			try{app.setCurrentversion(rs.getString("currentversion"));}catch(NullPointerException e){}
			try{app.setOldversion(rs.getString("oldversion"));}catch(NullPointerException e){}
			try{app.setAuthor(rs.getString("author"));}catch(NullPointerException e){}
			try{app.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{app.setEmail(rs.getString("email"));}catch(NullPointerException e){}
			try{app.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			data.add(app);
		}
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		return data;
	}
	
	private static String xmlLicense(){
		try {
		return RSCActivate.getInstance().getAppExpiration();
		}catch(Exception e) {
			System.out.println("You have not initialize the value in RSCActivation. To initialize call the Class RSCActivate.getInstance().setAppExpiration('expiration parameter')");
			return null;
		}
	}
	private static String dbLicense(){
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		String sql = "SELECT expdate FROM copyright ORDER BY id desc limit 1";	
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		
		while(rs.next()){
			return rs.getString("expdate");
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return null;
	}
	
	public static boolean checkLicenseExpiration(){
		
		String dblicense = dbLicense();
		if(dblicense==null) return false;
		char[] month = dblicense.split("-")[0].toCharArray();
		int m1 = Integer.valueOf(month[0]+"");
		int m2 = Integer.valueOf(month[1]+"");
		
		char[] day = dblicense.split("-")[1].toCharArray();
		int d1 = Integer.valueOf(day[0]+"");
		int d2 = Integer.valueOf(day[1]+"");
		
		
		char[] year = dblicense.split("-")[2].toCharArray();
		int y1 = Integer.valueOf(year[0]+"");
		int y2 = Integer.valueOf(year[1]+"");
		int y3 = Integer.valueOf(year[2]+"");
		int y4 = Integer.valueOf(year[3]+"");
		
		String chkVal = months()[m1]+months()[m2] +"-"+ days()[d1]+days()[d2] +"-"+ years()[y1]+years()[y2]+years()[y3]+years()[y4]; 
		
		//System.out.println("xml : " + xmlLicense() );
		System.out.println("dblicense : " + dblicense + " converted " + chkVal);
		
		if(xmlLicense().equalsIgnoreCase(chkVal)){
			
			return checkdate(dblicense);
			
		}
		
		return true;
		
	}
	
	/**
	 * 
	 * @return true if expired
	 */
	private static boolean checkdate(String dbLicense){
		
		String systemDate = DateUtils.getCurrentDateMMDDYYYY();
		
		boolean isYear = false;
		boolean isMonth = false;
		boolean isDay = false;
		int sxMonth = Integer.valueOf(systemDate.split("-")[0]);
		int sxDay = Integer.valueOf(systemDate.split("-")[1]);
		int sxYear = Integer.valueOf(systemDate.split("-")[2]);
		
		int dbMonth = Integer.valueOf(dbLicense.split("-")[0]);
		int dbDay = Integer.valueOf(dbLicense.split("-")[1]);
		int dbYear = Integer.valueOf(dbLicense.split("-")[2]);
		
		
		//first check year
		if(dbYear<=sxYear){
			isYear = true;
		}
		//second check month
		if(dbMonth<=sxMonth){
			isMonth = true;
		}
		//third check day
		if(dbDay<=sxDay){
			isDay = true;
		}
		
		if(isYear && isMonth && isDay){
			return true;
		}
		
		/*if(db<=sx){
			//System.out.println("true");
			return true;
		}*/
		
		return false;
	}
	
	private static String[] days(){
		char[] addChar = "markitalia".toCharArray();
		String[] days = new String[10];
		for(int i=0; i<=9;i++){
			days[i] = "0"+i+addChar[i];
		}
		return days;
	}
	
	private static String[] months(){
		char[] addChar = "mritaliamark".toCharArray();
		String[] months = new String[12];
		for(int i=0; i<12;i++){
			months[i] = "0" + (i+1) + addChar[i];
		}
		return months;
	}
	
	private static String[] years(){
		char[] addChar = "markitalia".toCharArray();
		String[] years = new String[12];
		for(int i=0; i<=9;i++){
			years[i] = "0" + i + addChar[i];
		}
		return years;
	}
	
	private void test(){
		System.out.println("month: " + months()[1]+ months()[0]);
		System.out.println("day: " + days()[1]+ days()[8]);
		System.out.println("year: " +years()[2]+ years()[0]+years()[1]+ years()[6]);
		
		System.out.println("match ? : " + checkLicenseExpiration());
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCopyrightname() {
		return copyrightname;
	}

	public void setCopyrightname(String copyrightname) {
		this.copyrightname = copyrightname;
	}

	public String getExpdate() {
		return expdate;
	}

	public void setExpdate(String expdate) {
		this.expdate = expdate;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getCurrentversion() {
		return currentversion;
	}

	public void setCurrentversion(String currentversion) {
		this.currentversion = currentversion;
	}

	public String getOldversion() {
		return oldversion;
	}

	public void setOldversion(String oldversion) {
		this.oldversion = oldversion;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getContactno() {
		return contactno;
	}

	public void setContactno(String contactno) {
		this.contactno = contactno;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public static void main(String[] args) {
		Copyright c  = new Copyright();
		c.checkLicenseExpiration();
	}
	
}













