package com.italia.marxmind.common.rsc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;

import com.italia.marxmind.ConnectDB;
import com.italia.marxmind.SecureChar;
/**
 * 
 * @author mark italia
 * @since 01/24/2017
 * @version 1.0
 *
 */
public class License {
	
	private long id;
	private String moduleName;
	private String codeName;
	private String monthExpiration;
	private String activationCode;
	private int isActive;
	private Timestamp timestamp;
	
	public static List<License> retrieve(String sql, String params[]){
		List<License> lis = new ArrayList<License>();//Collections.synchronizedList(new ArrayList<License>());
		
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
		
		//System.out.println("SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			License li = new License();
			li.setId(rs.getLong("lid"));
			li.setModuleName(rs.getString("modulename"));
			li.setCodeName(rs.getString("codename"));
			li.setMonthExpiration(rs.getString("monthexp"));
			li.setActivationCode(rs.getString("activationcode"));
			li.setIsActive(rs.getInt("isactivated"));
			li.setTimestamp(rs.getTimestamp("timestamp"));
			lis.add(li);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return lis;
	}
	
	public void update(){
		
		String sql = "UPDATE license SET " +
				"codename=?," +
				"monthexp=?,"
				+ "activationcode=?,"
				+ "isactivated=?"
				+ " WHERE lid=?";
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, getCodeName());
		ps.setString(2, getMonthExpiration());
		ps.setString(3, getActivationCode());
		ps.setInt(4, getIsActive());
		ps.setLong(5, getId());
		
		ps.execute();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){
		}
		
	}
	
	
	public static boolean isActivated(Module module){
		
		String sql = "SELECT * FROM license where modulename=? AND isactivated=1";
		String[] params = new String[1];
		params[0] = module.getName();
		License lic = null;
		
		try{lic = License.retrieve(sql, params).get(0);}catch(Exception e){}
		
		if(lic==null){
			return false; //expired
		}
		
		return true;
	}
	
	private static String licenseKey(String date){
		String value="";
		Properties prop = new Properties();
		try{
			prop.load(new FileInputStream(RSCActivate.getInstance().getLicenseDataFile()));
			value = prop.getProperty(date);
		}catch(Exception e){
			System.out.println("RSCActivate is not initialize for license file name. to Initialize RSCActivate.getInstance().setLicenseDataFile('file data')");
		}	
		
		return value;
	}
	
	public static boolean activateLicenseCode(Module module, String activationCode){
		
		String sql = "SELECT * FROM activationcode where modulename=? AND codename=?";
		String[] params = new String[2]; 
		params[0] = module.getName();
		params[1] = activationCode;
		
		ActivationCode code = null;
		try{code = ActivationCode.retrieve(sql, params).get(0);}catch(Exception e){}
		
		if(code!=null){
			
			String codename = SecureChar.decode(code.getActivationCode());
			codename = SecureChar.decodeLicense(codename);
			String productkey = SecureChar.decodeLicense(activationCode);
			
			String key = licenseKey(code.getMonthExpiration());
			if(codename.equalsIgnoreCase(key) && productkey.equalsIgnoreCase(key)){
			
			System.out.println("Code accepted....");
			System.out.println("updating license..");
			
			sql = "SELECT * FROM license WHERE modulename=?";
			params = new String[1];
			params[0] = module.getName();
			License lic = null; 
			try{
			
			lic = License.retrieve(sql, params).get(0);
			lic.setIsActive(1);
			lic.setMonthExpiration(code.getMonthExpiration());
			lic.setCodeName(code.getCodeName());
			lic.setActivationCode(code.getActivationCode());
			lic.update();
			
			char[] month = lic.getMonthExpiration().split("-")[0].toCharArray();
			int m1 = Integer.valueOf(month[0]+"");
			int m2 = Integer.valueOf(month[1]+"");
			
			char[] day = lic.getMonthExpiration().split("-")[1].toCharArray();
			int d1 = Integer.valueOf(day[0]+"");
			int d2 = Integer.valueOf(day[1]+"");
			
			
			char[] year = lic.getMonthExpiration().split("-")[2].toCharArray();
			int y1 = Integer.valueOf(year[0]+"");
			int y2 = Integer.valueOf(year[1]+"");
			int y3 = Integer.valueOf(year[2]+"");
			int y4 = Integer.valueOf(year[3]+"");
			
			String decodedDate = months()[m1]+months()[m2] +"-"+ days()[d1]+days()[d2] +"-"+ years()[y1]+years()[y2]+years()[y3]+years()[y4]; 
			
			//update xml license file
			updateLicense(module, decodedDate);
			
			}catch(Exception e){}
			
			return true;
			
			}else{
				return false;
			}
			
		}
		
		return false;
	}
	
	private static void updateLicense(Module moduleName, String licenseKey){
		try {
			
			Parameters params = new Parameters();
			FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
	                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
	                        .configure(params.properties().setFile(new File(RSCActivate.getInstance().getLicenseFileName())));
			Configuration cfg = builder.getConfiguration();
			
			cfg.setProperty(moduleName.getName(), licenseKey);
			
			builder.save();
			
		}catch(Exception e) {
			System.out.println("RSCActivate is not initialize for license file name. to Initialize RSCActivate.getInstance().setLicenseFileName('file name')");
			e.printStackTrace();}
	}
	
	public static String licenseFile(Module module){
		
		try {
    		Properties prop = new Properties();
        	prop.load(new FileInputStream(RSCActivate.getInstance().getLicenseFileName()));
        	return prop.getProperty(module.getName());
        	
           } catch (Exception e) {
        	   System.out.println("RSCActivate is not initialize for license file name. to Initialize RSCActivate.getInstance().setLicenseFileName('file name')");
        	   e.printStackTrace();
           }
		return "";
	}
	
	private static String xmlLicense(Module module){
		return licenseFile(module);
	}
	private static String dbLicense(Module module){
		System.out.println("dbLicense...");
		String sql = "SELECT * FROM license WHERE modulename=? AND isactivated=1";
		String[] params = new String[1];
		params[0] = module.getName();
		License lic = null;
		
		try{
			lic = License.retrieve(sql, params).get(0);
			return lic.getMonthExpiration();
		}catch(Exception e){}
		
		return null;
	}
	
	/**
	 * 
	 * @return true if expired
	 */
	public static boolean checkLicenseExpiration(Module module){
		
		String dblicense = dbLicense(module);
		
		if(dblicense==null) return true;
		
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
		
		//System.out.println("xml : " + xmlLicense(module) );
		//System.out.println("dblicense : " + dblicense + " converted " + chkVal);
		
		if(xmlLicense(module).equalsIgnoreCase(chkVal)){
			//System.out.println("xml and database equal...");
			//System.out.println("checking current date...");
			return checkdate(dblicense);
			
		}else{
			System.out.println("Application expired");
		}
		
		return true;
		
	}
	
	/**
	 * 
	 * @return true if expired
	 */
	private static boolean checkdate(String dbLicense){
		
		
		String systemDate = DateUtils.getCurrentDateMMDDYYYY();
		
		SimpleDateFormat dFormat = new SimpleDateFormat("MM-dd-yyyy");
		
		try{
		Date dbDate = dFormat.parse(dbLicense);	
		Date sysDate = dFormat.parse(systemDate);
		
		if(dbDate.compareTo(sysDate)>0){
			System.out.println("Not expired");
		}else if(dbDate.compareTo(sysDate)<0){
			System.out.println("Expired...");
			return true;
		}else if(dbDate.compareTo(sysDate)==0){
			System.out.println("Expired...");
			return true;
		}else{
			System.out.println("Expired...");
			return true;
		}
		
		}catch(ParseException pre){}
		
		
		
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
	
	public static void main(String[] args) {
		/*License lic = new License();
		boolean isActivated = lic.activateLicenseCode(Module.BRIS, "markitalia");
		boolean isModule = License.checkLicenseExpiration(Module.BRIS);
		
		if(isModule){
			System.out.println("module expired");
		}*/
		
		
		String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		String[] number = {"0","1","2","3","4","5","6","7","8","9"};
		
		String val="";
		for(int i=1; i<=120;i++ ){
			
			for(int x=1; x<=9;x++){
				val+=alphabet[(int)(Math.random() * alphabet.length)];
				int len = val.length();
				if(len==3 || len==7){
					val+="-";
				}
			}
			val+="-";
			for(int x=1; x<=6;x++){
				val+=number[(int)(Math.random() * number.length)];
				int len = val.length();
				if(len==11 || len==15){
					val+="-";
				}
			}
			
			System.out.println(val);
			val="";
		}
		
	} 

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getMonthExpiration() {
		return monthExpiration;
	}

	public void setMonthExpiration(String monthExpiration) {
		this.monthExpiration = monthExpiration;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}

