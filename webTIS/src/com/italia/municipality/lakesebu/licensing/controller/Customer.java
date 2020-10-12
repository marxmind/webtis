package com.italia.municipality.lakesebu.licensing.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.database.LicensingDatabaseConnect;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 11/01/2016
 * @version 1.0
 */


public class Customer {

	private long customerid;
	private String firstname;
	private String middlename;
	private String lastname;
	private String fullname;
	private String gender;
	private int age;
	//private String address; 
	private String contactno;
	private String dateregistered;
	private String cardno; 
	private int isactive;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private int civilStatus;
	private String photoid;
	
	private int relationship;
	private String birthdate;
	private Customer emergencyContactPerson;
	
	private Purok purok;
	private Barangay barangay;
	private Municipality municipality;
	private Province province;
	
	private String genderName;
	
	private String completeAddress;
	
	public Customer(){}
	
	public Customer(
			long customerid,
			String firstname,
			String middlename,
			String lastname,
			String fullname,
			String gender,
			int age,
			String address, 
			String contactno,
			String dateregistered,
			String cardno, 
			int isactive,
			UserDtls userDtls,
			Barangay barangay,
			Municipality municipality,
			Province province,
			int civilStatus,
			int relationship,
			String birthdate,
			Customer emergencyContactPerson,
			String photoid
			){
		
		this.customerid = customerid;
		this.firstname = firstname;
		this.middlename = middlename;
		this.lastname = lastname;
		this.fullname = fullname;
		this.gender = gender;
		this.age = age;
		//this.address = address;
		this.contactno = contactno;
		this.dateregistered = dateregistered;
		this.cardno = cardno;
		this.isactive = isactive;
		this.userDtls = userDtls;
		this.barangay = barangay;
		this.municipality = municipality;
		this.province = province;
		this.civilStatus = civilStatus;
		this.relationship = relationship;
		this.birthdate = birthdate;
		this.emergencyContactPerson = emergencyContactPerson;
		this.photoid = photoid;
		
	}
	
	public static String cardNumber(){
		String cardNum = "000000000000000";
		int id = 0;
		try{id = Integer.valueOf(getLatestCardNo().split(" ")[1]);}catch(Exception e){}
		String newCardNumber = (id+1) + "";
		//cardNum = (getLatestId()+1) + DateUtils.getCurrentDateMMDDYYYYPlain() + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
		String brgyCode = "PLS ";
		int num = newCardNumber.length();
		
		switch(num){
			case 1: cardNum = brgyCode+"00000"+newCardNumber;  break;
			case 2: cardNum = brgyCode+"0000"+newCardNumber;  break;
			case 3: cardNum = brgyCode+"000"+newCardNumber;  break;
			case 4: cardNum = brgyCode+"00"+newCardNumber;  break;
			case 5: cardNum = brgyCode+"0"+newCardNumber;  break;
			case 6: cardNum = brgyCode+newCardNumber;  break;
		}
		
		return cardNum;
	}
	
	public static int firstYearData(){
		String sql = "SELECT cusdateregistered FROM customer limit 1";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return Integer.valueOf(rs.getString("cusdateregistered").split("-")[0]);
		}
		
		rs.close();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return 0;
	}
	
	public static boolean isImageInUsed(String photoId){
		
		String sql = "SELECT photoid FROM customer WHERE photoid='" + photoId + "' ";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return true;
		}
		
		rs.close();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return false;
	}
	
	public static boolean validateNameEntry(String first, String middle, String last){
		String sql = "SELECT cusfirstname,cusmiddlename,cuslastname FROM customer WHERE cusisactive=1 AND cusfirstname=? AND cusmiddlename=? AND cuslastname=?";
		String[] params = new String[3];
		params[0] = first;
		params[1] = middle;
		params[2] = last;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return true;
		}
		
		rs.close();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return false;
	}
	
	public static List<String> retrieve(String param, String fieldName, String limit){
		
		String sql = "SELECT DISTINCT " + fieldName + " FROM customer WHERE " + fieldName +" like '" + param + "%' " + limit;
		List<String> result = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			result.add(rs.getString(fieldName));
		}
		
		rs.close();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return result;
	}
	
	public static String customerSQL(String tablename,Customer cus){
		String sql= " AND "+ tablename +".cusisactive=" + cus.getIsactive();
		if(cus!=null){
			
			if(cus.getCustomerid()!=0){
				sql += " AND "+ tablename +".customerid=" + cus.getCustomerid();
			}/*else{
				sql += " AND "+ tablename +".customerid=" + cus.getCustomerid();
			}*/
			if(cus.getDateregistered()!=null){
				sql += " AND "+ tablename +".cusdateregistered like '%" + cus.getDateregistered()+"%'";
			}
			if(cus.getFirstname()!=null){
				sql += " AND "+ tablename +".cusfirstname like '%" + cus.getFirstname()+"%'";
			}
			if(cus.getMiddlename()!=null){
				sql += " AND "+ tablename +".cusmiddlename like '%" + cus.getMiddlename()+"%'";
			}
			if(cus.getLastname()!=null){
				sql += " AND "+ tablename +".cuslastname like '%" + cus.getLastname()+"%'";
			}
			if(cus.getFullname()!=null){
				sql += " AND "+ tablename +".fullname like '%" + cus.getFullname()+"%'";
			}
			/*if(cus.getAddress()!=null){
				sql += " AND "+ tablename +".cusaddress like '%" + cus.getAddress()+"%'";
			}*/
			if(cus.getContactno()!=null){
				sql += " AND "+ tablename +".cuscontactno like'%" + cus.getContactno()+"%'";
			}
			if(cus.getAge()!=0){
				sql += " AND "+ tablename +".cusage=" + cus.getAge();
			}
			if(cus.getGender()!=null){
				sql += " AND "+ tablename +".cusgender like'%" + cus.getGender()+"%'";
			}
			
			if(cus.getCivilStatus()!=0){
				sql += " AND "+ tablename +".civilstatus=" + cus.getCivilStatus();
			}
			
			if(cus.getUserDtls()!=null){
				if(cus.getUserDtls().getUserdtlsid()!=0){
					sql += " AND "+ tablename +".userdtlsid=" + cus.getUserDtls().getUserdtlsid() ;
				}
			}
			
		}
		
		return sql;
	}
	
	public static List<Customer> retrieve(Object... obj){
		List<Customer> cuss = Collections.synchronizedList(new ArrayList<Customer>());
		String supTable = "cus";
		String userTable = "usr";
		String purokTable = "pur";
		String barTable = "bar";
		String munTable = "mun";
		String provTable = "prov";
		String sql = "SELECT * FROM customer "+ supTable +
				                 ", userdtls "+ userTable +
				                 ", purok "+ purokTable +
				                 ", barangay "+ barTable +
				                 ", municipality "+ munTable +
				                 ", province "+ provTable + 
				                 
				" WHERE "
				+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+ supTable +".purid = "+ purokTable +".purid AND "
				+ supTable +".bgid = "+ barTable +".bgid AND "
				+ supTable +".munid = "+ munTable +".munid AND "
				+ supTable +".provid = "+ provTable +".provid AND " 
				+ supTable +".purid = " + purokTable +".purid ";
		
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof Customer){
				sql += customerSQL(supTable,(Customer)obj[i]);
			}
			if(obj[i] instanceof UserDtls){
				sql += UserDtls.userSQL(userTable,(UserDtls)obj[i]);
			}
			if(obj[i] instanceof Purok){
				sql += Purok.purokSQL(purokTable,(Purok)obj[i]);
			}
			if(obj[i] instanceof Barangay){
				sql += Barangay.barangaySQL(barTable,(Barangay)obj[i]);
			}
			if(obj[i] instanceof Municipality){
				sql += Municipality.municipalitySQL(munTable,(Municipality)obj[i]);
			}
			if(obj[i] instanceof Province){
				sql += Province.provinceSQL(provTable,(Province)obj[i]);
			}
		}
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			//try{cus.setAddress(rs.getString("cusaddress"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{cus.setCivilStatus(rs.getInt("civilstatus"));}catch(NullPointerException e){}
			try{cus.setPhotoid(rs.getString("photoid"));}catch(NullPointerException e){}
			
			if("1".equalsIgnoreCase(cus.getGender())){
				cus.setGenderName("Male");
			}else{
				cus.setGenderName("Female");
			}
			
			try{cus.setBirthdate(rs.getString("borndate"));}catch(NullPointerException e){}
			try{Customer emergency = new Customer();
			emergency.setCustomerid(rs.getLong("emeperson"));
			cus.setEmergencyContactPerson(emergency);}catch(NullPointerException e){}
			try{cus.setRelationship(rs.getInt("relid"));}catch(NullPointerException e){}
			
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			cus.setUserDtls(user);
			
			Purok pur = new Purok();
			try{pur.setId(rs.getLong("purid"));}catch(NullPointerException e){}
			try{pur.setDateTrans(rs.getString("purdate"));}catch(NullPointerException e){}
			try{pur.setPurokName(rs.getString("purokname"));}catch(NullPointerException e){}
			try{pur.setIsActive(rs.getInt("isactivepurok"));}catch(NullPointerException e){}
			cus.setPurok(pur);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			try{bar.setName(rs.getString("bgname"));}catch(NullPointerException e){}
			try{bar.setIsActive(rs.getInt("bgisactive"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			try{mun.setName(rs.getString("munname"));}catch(NullPointerException e){}
			try{mun.setIsActive(rs.getInt("munisactive"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
			String address="";
			try{
				address = pur.getId()==0?"" : pur.getPurokName()+", ";
				address += bar.getId()==0?"" : bar.getName()+", ";
				address += mun.getId()==0?"" : mun.getName()+", ";
				address += prov.getName();
			}catch(Exception e){}
			cus.setCompleteAddress(address);
			
			cuss.add(cus);
		}
		
		rs.close();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cuss;
	}
	
	public static List<Customer> retrieve(String sqlAdd, String[] params){
		List<Customer> cuss = Collections.synchronizedList(new ArrayList<Customer>());
		String supTable = "cus";
		String userTable = "usr";
		String purTable = "pur";
		String barTable = "bar";
		String munTable = "mun";
		String provTable = "prov";
		String sql = "SELECT * FROM customer "+ supTable +
				                 ", userdtls "+ userTable +
				                 ", purok "+ purTable +
				                 ", barangay "+ barTable +
				                 ", municipality "+ munTable +
				                 ", province "+ provTable + 
				                 
				" WHERE "
				+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+ supTable +".purid = "+ purTable +".purid AND "
				+ supTable +".bgid = "+ barTable +".bgid AND "
				+ supTable +".munid = "+ munTable +".munid AND "
				+ supTable +".provid = "+ provTable +".provid ";
		
		sql = sql + sqlAdd;
		
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			//try{cus.setAddress(rs.getString("cusaddress"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{cus.setCivilStatus(rs.getInt("civilstatus"));}catch(NullPointerException e){}
			try{cus.setPhotoid(rs.getString("photoid"));}catch(NullPointerException e){}
			
			if("1".equalsIgnoreCase(cus.getGender())){
				cus.setGenderName("Male");
			}else{
				cus.setGenderName("Female");
			}
			
			try{cus.setBirthdate(rs.getString("borndate"));}catch(NullPointerException e){}
			try{Customer emergency = new Customer();
			emergency.setCustomerid(rs.getLong("emeperson"));
			cus.setEmergencyContactPerson(emergency);}catch(NullPointerException e){}
			try{cus.setRelationship(rs.getInt("relid"));}catch(NullPointerException e){}
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			cus.setUserDtls(user);
			
			Purok pur = new Purok();
			try{pur.setId(rs.getLong("purid"));}catch(NullPointerException e){}
			try{pur.setDateTrans(rs.getString("purdate"));}catch(NullPointerException e){}
			try{pur.setPurokName(rs.getString("purokname"));}catch(NullPointerException e){}
			try{pur.setIsActive(rs.getInt("isactivepurok"));}catch(NullPointerException e){}
			cus.setPurok(pur);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			try{bar.setName(rs.getString("bgname"));}catch(NullPointerException e){}
			try{bar.setIsActive(rs.getInt("bgisactive"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			try{mun.setName(rs.getString("munname"));}catch(NullPointerException e){}
			try{mun.setIsActive(rs.getInt("munisactive"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
			String address="";
			try{
				address = pur.getId()==0?"" : pur.getPurokName()+", ";
				address += bar.getId()==0?"" : bar.getName()+", ";
				address += mun.getId()==0?"" : mun.getName()+", ";
				address += prov.getName();
			}catch(Exception e){}
			cus.setCompleteAddress(address);
			
			cuss.add(cus);
		}
		
		rs.close();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cuss;
	}
	
	public static Customer retrieve(long customerid){
		Customer cus = new Customer();
		String supTable = "cus";
		String userTable = "usr";
		/*String sql = "SELECT * FROM customer "+ supTable +", userdtls "+ userTable +" WHERE "+ supTable +".userdtlsid = "
		+ userTable +".userdtlsid AND " + supTable + ".customerid="+customerid;
		*/
		String purTable = "pur";
		String barTable = "bar";
		String munTable = "mun";
		String provTable = "prov";
		String sql = "SELECT * FROM customer "+ supTable +
				                 ", userdtls "+ userTable +
				                 ", purok "+ purTable +
				                 ", barangay "+ barTable +
				                 ", municipality "+ munTable +
				                 ", province "+ provTable + 
				                 
				" WHERE "
				+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+ supTable +".purid = "+ purTable +".purid AND "
				+ supTable +".bgid = "+ barTable +".bgid AND "
				+ supTable +".munid = "+ munTable +".munid AND "
				+ supTable +".provid = "+ provTable +".provid AND " +
				supTable + ".customerid="+customerid;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			//try{cus.setAddress(rs.getString("cusaddress"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{cus.setCivilStatus(rs.getInt("civilstatus"));}catch(NullPointerException e){}
			try{cus.setPhotoid(rs.getString("photoid"));}catch(NullPointerException e){}
			
			if("1".equalsIgnoreCase(cus.getGender())){
				cus.setGenderName("Male");
			}else{
				cus.setGenderName("Female");
			}
			
			try{cus.setBirthdate(rs.getString("borndate"));}catch(NullPointerException e){}
			try{Customer emergency = new Customer();
			emergency.setCustomerid(rs.getLong("emeperson"));
			cus.setEmergencyContactPerson(emergency);}catch(NullPointerException e){}
			try{cus.setRelationship(rs.getInt("relid"));}catch(NullPointerException e){}
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			cus.setUserDtls(user);
			
			Purok pur = new Purok();
			try{pur.setId(rs.getLong("purid"));}catch(NullPointerException e){}
			try{pur.setDateTrans(rs.getString("purdate"));}catch(NullPointerException e){}
			try{pur.setPurokName(rs.getString("purokname"));}catch(NullPointerException e){}
			try{pur.setIsActive(rs.getInt("isactivepurok"));}catch(NullPointerException e){}
			cus.setPurok(pur);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			try{bar.setName(rs.getString("bgname"));}catch(NullPointerException e){}
			try{bar.setIsActive(rs.getInt("bgisactive"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			try{mun.setName(rs.getString("munname"));}catch(NullPointerException e){}
			try{mun.setIsActive(rs.getInt("munisactive"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
			String address="";
			try{
				address = pur.getId()==0?"" : pur.getPurokName()+", ";
				address += bar.getId()==0?"" : bar.getName()+", ";
				address += mun.getId()==0?"" : mun.getName()+", ";
				address += prov.getName();
			}catch(Exception e){}
			cus.setCompleteAddress(address);
			
		}
		
		rs.close();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cus;
	}
	
	public static Customer addUnknownCustomer(){
		Customer customer = new Customer();
		String unknowname = "unknown buyer" + DateUtils.getCurrentDateMMDDYYYYTIME();
		customer.setFirstname(unknowname);
		customer.setMiddlename(unknowname);
		customer.setLastname(unknowname);
		customer.setFullname(unknowname);
		customer.setGender("1");
		customer.setAge(0);
		//customer.setAddress("unknown address");
		customer.setContactno("00000000000");
		customer.setDateregistered(DateUtils.getCurrentDateYYYYMMDD());
		customer.setIsactive(1);
		customer.setUserDtls(Login.getUserLogin().getUserDtls());
		return customer;
	}
	
	public static Customer customer(long customerId){
		Customer cus = new Customer();
		String supTable = "cus";
		String userTable = "usr";
		/*String sql = "SELECT * FROM customer "+ supTable +", userdtls "+ userTable +" WHERE "+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+ supTable +".customerid="+customerId;
		
        System.out.println("SQL "+sql);*/
		String purTable = "pur";
		String barTable = "bar";
		String munTable = "mun";
		String provTable = "prov";
		String sql = "SELECT * FROM customer "+ supTable +
				                 ", userdtls "+ userTable +
				                 ", purok "+ purTable +
				                 ", barangay "+ barTable +
				                 ", municipality "+ munTable +
				                 ", province "+ provTable + 
				                 
				" WHERE "
				+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND "
				+ supTable +".purid = "+ purTable +".purid AND "
				+ supTable +".bgid = "+ barTable +".bgid AND "
				+ supTable +".munid = "+ munTable +".munid AND "
				+ supTable +".provid = "+ provTable +".provid AND " +
				supTable + ".customerid="+customerId;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			//try{cus.setAddress(rs.getString("cusaddress"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{cus.setCivilStatus(rs.getInt("civilstatus"));}catch(NullPointerException e){}
			try{cus.setPhotoid(rs.getString("photoid"));}catch(NullPointerException e){}
			
			if("1".equalsIgnoreCase(cus.getGender())){
				cus.setGenderName("Male");
			}else{
				cus.setGenderName("Female");
			}
			
			try{cus.setBirthdate(rs.getString("borndate"));}catch(NullPointerException e){}
			try{Customer emergency = new Customer();
			emergency.setCustomerid(rs.getLong("emeperson"));
			cus.setEmergencyContactPerson(emergency);}catch(NullPointerException e){}
			try{cus.setRelationship(rs.getInt("relid"));}catch(NullPointerException e){}
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			cus.setUserDtls(user);
			
			Purok pur = new Purok();
			try{pur.setId(rs.getLong("purid"));}catch(NullPointerException e){}
			try{pur.setDateTrans(rs.getString("purdate"));}catch(NullPointerException e){}
			try{pur.setPurokName(rs.getString("purokname"));}catch(NullPointerException e){}
			try{pur.setIsActive(rs.getInt("isactivepurok"));}catch(NullPointerException e){}
			cus.setPurok(pur);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			try{bar.setName(rs.getString("bgname"));}catch(NullPointerException e){}
			try{bar.setIsActive(rs.getInt("bgisactive"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			try{mun.setName(rs.getString("munname"));}catch(NullPointerException e){}
			try{mun.setIsActive(rs.getInt("munisactive"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			try{prov.setName(rs.getString("provname"));}catch(NullPointerException e){}
			try{prov.setIsActive(rs.getInt("provisactive"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
			String address="";
			try{
				address = pur.getId()==0?"" : pur.getPurokName()+", ";
				address += bar.getId()==0?"" : bar.getName()+", ";
				address += mun.getId()==0?"" : mun.getName()+", ";
				address += prov.getName();
			}catch(Exception e){}
			cus.setCompleteAddress(address);
			
		}
		
		rs.close();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cus;
	}
	
	public static void updatePhoto(String sql, String[] params){
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		System.out.println("SQL: " + ps.toString());
		
		ps.execute();
		
		ps.close();
		LicensingDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){}
		
	}
	
	public static Customer save(Customer cus){
		if(cus!=null){
			
			long id = Customer.getInfo(cus.getCustomerid() ==0? Customer.getLatestId()+1 : cus.getCustomerid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				cus = Customer.insertData(cus, "1");
			}else if(id==2){
				LogU.add("update Data ");
				cus = Customer.updateData(cus);
			}else if(id==3){
				LogU.add("added new Data ");
				cus = Customer.insertData(cus, "3");
			}
			
		}
		return cus;
	}
	
	public void save(){
			
			long id = getInfo(getCustomerid() ==0? getLatestId()+1 : getCustomerid());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				insertData("1");
			}else if(id==2){
				LogU.add("update Data ");
				updateData();
			}else if(id==3){
				LogU.add("added new Data ");
				insertData("3");
			}
			
	}
	
	public static Customer insertData(Customer cus, String type){
		String sql = "INSERT INTO customer ("
				+ "customerid,"
				+ "cusfirstname,"
				+ "cusmiddlename,"
				+ "cuslastname,"
				+ "cusgender,"
				+ "cusage,"
				+ "purid,"
				+ "cuscontactno,"
				+ "cusdateregistered,"
				+ "cuscardno,"
				+ "cusisactive,"
				+ "userdtlsid,"
				+ "fullname,"
				+ "bgid,"
				+ "munid,"
				+ "provid,"
				+ "civilstatus,"
				+ "borndate,"
				+ "emeperson,"
				+ "relid,"
				+ "photoid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customer");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			cus.setCustomerid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			cus.setCustomerid(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, cus.getFirstname());
		ps.setString(cnt++, cus.getMiddlename());
		ps.setString(cnt++, cus.getLastname());
		ps.setString(cnt++, cus.getGender());
		ps.setInt(cnt++, cus.getAge());
		ps.setLong(cnt++, cus.getPurok().getId());
		ps.setString(cnt++, cus.getContactno());
		ps.setString(cnt++, cus.getDateregistered());
		ps.setString(cnt++, cardNumber());
		ps.setInt(cnt++, cus.getIsactive());
		ps.setLong(cnt++, cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==0? 0 : cus.getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, cus.getFullname());
		ps.setInt(cnt++, cus.getBarangay()==null? 0 : cus.getBarangay().getId());
		ps.setInt(cnt++, cus.getMunicipality()==null? 0 : cus.getMunicipality().getId());
		ps.setInt(cnt++, cus.getProvince()==null? 0 : cus.getProvince().getId());
		ps.setInt(cnt++, cus.getCivilStatus());
		ps.setString(cnt++, cus.getBirthdate());
		ps.setLong(cnt++, cus.getEmergencyContactPerson()==null? 0l : cus.getEmergencyContactPerson().getCustomerid());
		ps.setInt(cnt++, cus.getRelationship());
		ps.setString(cnt++, cus.getPhotoid());
		
		LogU.add(cus.getFirstname());
		LogU.add(cus.getMiddlename());
		LogU.add(cus.getLastname());
		LogU.add(cus.getGender());
		LogU.add(cus.getAge());
		LogU.add(cus.getPurok().getId());
		LogU.add(cus.getContactno());
		LogU.add(cus.getDateregistered());
		LogU.add(cus.getCardno());
		LogU.add(cus.getIsactive());
		LogU.add(cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==0? 0 : cus.getUserDtls().getUserdtlsid()));
		LogU.add(cus.getFullname());
		LogU.add(cus.getBarangay()==null? 0 : cus.getBarangay().getId());
		LogU.add(cus.getMunicipality()==null? 0 : cus.getMunicipality().getId());
		LogU.add(cus.getProvince()==null? 0 : cus.getProvince().getId());
		LogU.add(cus.getCivilStatus());
		LogU.add(cus.getBirthdate());
		LogU.add(cus.getEmergencyContactPerson()==null? 0l : cus.getEmergencyContactPerson().getCustomerid());
		LogU.add(cus.getRelationship());
		LogU.add(cus.getPhotoid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		LicensingDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customer : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cus;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO customer ("
				+ "customerid,"
				+ "cusfirstname,"
				+ "cusmiddlename,"
				+ "cuslastname,"
				+ "cusgender,"
				+ "cusage,"
				+ "purid,"
				+ "cuscontactno,"
				+ "cusdateregistered,"
				+ "cuscardno,"
				+ "cusisactive,"
				+ "userdtlsid,"
				+ "fullname,"
				+ "bgid,"
				+ "munid,"
				+ "provid,"
				+ "civilstatus,"
				+ "borndate,"
				+ "emeperson,"
				+ "relid,"
				+ "photoid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customer");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setCustomerid(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setCustomerid(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, getFirstname());
		ps.setString(cnt++, getMiddlename());
		ps.setString(cnt++, getLastname());
		ps.setString(cnt++, getGender());
		ps.setInt(cnt++, getAge());
		ps.setLong(cnt++, getPurok().getId());
		ps.setString(cnt++, getContactno());
		ps.setString(cnt++, getDateregistered());
		ps.setString(cnt++, cardNumber());
		ps.setInt(cnt++, getIsactive());
		ps.setLong(cnt++, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==0? 0 : getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, getFullname());
		ps.setInt(cnt++, getBarangay()==null? 0 : getBarangay().getId());
		ps.setInt(cnt++, getMunicipality()==null? 0 : getMunicipality().getId());
		ps.setInt(cnt++, getProvince()==null? 0 : getProvince().getId());
		ps.setInt(cnt++, getCivilStatus());
		ps.setString(cnt++, getBirthdate());
		ps.setLong(cnt++, getEmergencyContactPerson()==null? 0l : getEmergencyContactPerson().getCustomerid());
		ps.setInt(cnt++, getRelationship());
		ps.setString(cnt++, getPhotoid());
		
		LogU.add(getFirstname());
		LogU.add(getMiddlename());
		LogU.add(getLastname());
		LogU.add(getGender());
		LogU.add(getAge());
		LogU.add(getPurok().getId());
		LogU.add(getContactno());
		LogU.add(getDateregistered());
		LogU.add(getCardno());
		LogU.add(getIsactive());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==0? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getFullname());
		LogU.add(getBarangay()==null? 0 : getBarangay().getId());
		LogU.add(getMunicipality()==null? 0 : getMunicipality().getId());
		LogU.add(getProvince()==null? 0 : getProvince().getId());
		LogU.add(getCivilStatus());
		LogU.add(getBirthdate());
		LogU.add(getEmergencyContactPerson()==null? 0l : getEmergencyContactPerson().getCustomerid());
		LogU.add(getRelationship());
		LogU.add(getPhotoid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		LicensingDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customer : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Customer updateData(Customer cus){
		String sql = "UPDATE customer SET "
				+ "cusfirstname=?,"
				+ "cusmiddlename=?,"
				+ "cuslastname=?,"
				+ "cusgender=?,"
				+ "cusage=?,"
				+ "purid=?,"
				+ "cuscontactno=?,"
				+ "cusdateregistered=?,"
				+ "cusisactive=?,"
				+ "userdtlsid=?,"
				+ "fullname=?,"
				+ "bgid=?,"
				+ "munid=?,"
				+ "provid=?,"
				+ "civilstatus=?,"
				+ "borndate=?,"
				+ "emeperson=?,"
				+ "relid=?,"
				+ "photoid=? " 
				+ " WHERE customerid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table customer");
		int cnt=1;
		ps.setString(cnt++, cus.getFirstname());
		ps.setString(cnt++, cus.getMiddlename());
		ps.setString(cnt++, cus.getLastname());
		ps.setString(cnt++, cus.getGender());
		ps.setInt(cnt++, cus.getAge());
		ps.setLong(cnt++, cus.getPurok().getId());
		ps.setString(cnt++, cus.getContactno());
		ps.setString(cnt++, cus.getDateregistered());
		ps.setInt(cnt++, cus.getIsactive());
		ps.setLong(cnt++, cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==0? 0 : cus.getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, cus.getFullname());
		ps.setInt(cnt++, cus.getBarangay()==null? 0 : cus.getBarangay().getId());
		ps.setInt(cnt++, cus.getMunicipality()==null? 0 : cus.getMunicipality().getId());
		ps.setInt(cnt++, cus.getProvince()==null? 0 : cus.getProvince().getId());
		ps.setInt(cnt++, cus.getCivilStatus());
		ps.setString(cnt++, cus.getBirthdate());
		ps.setLong(cnt++, cus.getEmergencyContactPerson()==null? 0l : cus.getEmergencyContactPerson().getCustomerid());
		ps.setInt(cnt++, cus.getRelationship());
		ps.setString(cnt++, cus.getPhotoid());
		ps.setLong(cnt++, cus.getCustomerid());
		
		LogU.add(cus.getFirstname());
		LogU.add(cus.getMiddlename());
		LogU.add(cus.getLastname());
		LogU.add(cus.getGender());
		LogU.add(cus.getAge());
		LogU.add(cus.getPurok().getId());
		LogU.add(cus.getContactno());
		LogU.add(cus.getDateregistered());
		LogU.add(cus.getIsactive());
		LogU.add(cus.getUserDtls()==null? 0 : (cus.getUserDtls().getUserdtlsid()==0? 0 : cus.getUserDtls().getUserdtlsid()));
		LogU.add(cus.getFullname());
		LogU.add(cus.getBarangay()==null? 0 : cus.getBarangay().getId());
		LogU.add(cus.getMunicipality()==null? 0 : cus.getMunicipality().getId());
		LogU.add(cus.getProvince()==null? 0 : cus.getProvince().getId());
		LogU.add(cus.getCivilStatus());
		LogU.add(cus.getBirthdate());
		LogU.add(cus.getEmergencyContactPerson()==null? 0l : cus.getEmergencyContactPerson().getCustomerid());
		LogU.add(cus.getRelationship());
		LogU.add(cus.getPhotoid());
		LogU.add(cus.getCustomerid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		LicensingDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to customer : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cus;
	}
	
	public void updateData(){
		String sql = "UPDATE customer SET "
				+ "cusfirstname=?,"
				+ "cusmiddlename=?,"
				+ "cuslastname=?,"
				+ "cusgender=?,"
				+ "cusage=?,"
				+ "purid=?,"
				+ "cuscontactno=?,"
				+ "cusdateregistered=?,"
				+ "cusisactive=?,"
				+ "userdtlsid=?,"
				+ "fullname=?,"
				+ "bgid=?,"
				+ "munid=?,"
				+ "provid=?,"
				+ "civilstatus=?,"
				+ "borndate=?,"
				+ "emeperson=?,"
				+ "relid=?,"
				+ "photoid=? " 
				+ " WHERE customerid=?";
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("updating data into table customer");
		int cnt = 1;
		ps.setString(cnt++, getFirstname());
		ps.setString(cnt++, getMiddlename());
		ps.setString(cnt++, getLastname());
		ps.setString(cnt++, getGender());
		ps.setInt(cnt++, getAge());
		ps.setLong(cnt++, getPurok().getId());
		ps.setString(cnt++, getContactno());
		ps.setString(cnt++, getDateregistered());
		ps.setInt(cnt++, getIsactive());
		ps.setLong(cnt++, getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==0? 0 : getUserDtls().getUserdtlsid()));
		ps.setString(cnt++, getFullname());
		ps.setInt(cnt++, getBarangay()==null? 0 : getBarangay().getId());
		ps.setInt(cnt++, getMunicipality()==null? 0 : getMunicipality().getId());
		ps.setInt(cnt++, getProvince()==null? 0 : getProvince().getId());
		ps.setInt(cnt++, getCivilStatus());
		ps.setString(cnt++, getBirthdate());
		ps.setLong(cnt++, getEmergencyContactPerson()==null? 0l : getEmergencyContactPerson().getCustomerid());
		ps.setInt(cnt++, getRelationship());
		ps.setString(cnt++, getPhotoid());
		ps.setLong(cnt++, getCustomerid());
		
		
		LogU.add(getFirstname());
		LogU.add(getMiddlename());
		LogU.add(getLastname());
		LogU.add(getGender());
		LogU.add(getAge());
		LogU.add(getPurok().getId());
		LogU.add(getContactno());
		LogU.add(getDateregistered());
		LogU.add(getIsactive());
		LogU.add(getUserDtls()==null? 0 : (getUserDtls().getUserdtlsid()==0? 0 : getUserDtls().getUserdtlsid()));
		LogU.add(getFullname());
		LogU.add(getBarangay()==null? 0 : getBarangay().getId());
		LogU.add(getMunicipality()==null? 0 : getMunicipality().getId());
		LogU.add(getProvince()==null? 0 : getProvince().getId());
		LogU.add(getCivilStatus());
		LogU.add(getBirthdate());
		LogU.add(getEmergencyContactPerson()==null? 0l : getEmergencyContactPerson().getCustomerid());
		LogU.add(getRelationship());
		LogU.add(getPhotoid());
		LogU.add(getCustomerid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		LicensingDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to customer : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT customerid FROM customer  ORDER BY customerid DESC LIMIT 1";	
		conn = LicensingDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("customerid");
		}
		
		rs.close();
		prep.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static String getLatestCardNo(){
		String id ="0";
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT cuscardno FROM customer WHERE cusisactive=1 ORDER BY customerid DESC LIMIT 1";	
		conn = LicensingDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getString("cuscardno");
		}
		
		rs.close();
		prep.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestId();	
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement("SELECT customerid FROM customer WHERE customerid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE customer set cusisactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE customerid=?";
		
		String[] params = new String[1];
		params[0] = getCustomerid()+"";
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
	public long getCustomerid() {
		return customerid;
	}
	public void setCustomerid(long customerid) {
		this.customerid = customerid;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	/*public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}*/
	public String getContactno() {
		if(contactno==null) {
			contactno = "";
		}
		return contactno;
	}
	public void setContactno(String contactno) {
		this.contactno = contactno;
	}
	public String getDateregistered() {
		if(dateregistered==null) {
			dateregistered = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateregistered;
	}
	public void setDateregistered(String dateregistered) {
		this.dateregistered = dateregistered;
	}
	public String getCardno() {
		if(cardno==null) {
			cardno = Customer.cardNumber();
		}
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public int getIsactive() {
		return isactive;
	}
	public void setIsactive(int isactive) {
		this.isactive = isactive;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public String getGender() {
		if(gender==null) {
			gender = "1";
		}
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Barangay getBarangay() {
		return barangay;
	}

	public void setBarangay(Barangay barangay) {
		this.barangay = barangay;
	}

	public Municipality getMunicipality() {
		return municipality;
	}

	public void setMunicipality(Municipality municipality) {
		this.municipality = municipality;
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public int getCivilStatus() {
		if(civilStatus==0) {
			civilStatus = 1;
		}
		return civilStatus;
	}

	public void setCivilStatus(int civilStatus) {
		this.civilStatus = civilStatus;
	}

	public int getRelationship() {
		return relationship;
	}

	public void setRelationship(int relationship) {
		this.relationship = relationship;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public Customer getEmergencyContactPerson() {
		return emergencyContactPerson;
	}

	public void setEmergencyContactPerson(Customer emergencyContactPerson) {
		this.emergencyContactPerson = emergencyContactPerson;
	}

	public String getPhotoid() {
		return photoid;
	}

	public void setPhotoid(String photoid) {
		this.photoid = photoid;
	}

	public String getGenderName() {
		return genderName;
	}

	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}

	public Purok getPurok() {
		return purok;
	}

	public void setPurok(Purok purok) {
		this.purok = purok;
	}

	public String getCompleteAddress() {
		return completeAddress;
	}

	public void setCompleteAddress(String completeAddress) {
		this.completeAddress = completeAddress;
	}

	public static void main(String[] args) {
		
		System.out.println(Customer.cardNumber());
		
		/*Customer c = new Customer();
		//c.setCustomerid(1);
		c.setFirstname("Markos");
		c.setMiddlename("M");
		c.setLastname("L");
		c.setGender("Male");
		c.setAge(1);
		c.setAddress("Add");
		c.setContactno("1211");
		c.setDateregistered(DateUtils.getCurrentDateMMDDYYYY());
		c.setCardno("45247");
		c.setIsactive(1);
		UserDtls u = new UserDtls();
		u.setUserdtlsid(1l);
		c.setUserDtls(u);
		c.save();
		c.setIsactive(1);
		for(Customer cx :  Customer.retrieve(c)){
			System.out.println("name : " + cx.getFirstname());
		}*/
	}

	public Long getIdentifier() {
		return this.customerid;
	}
	
}











