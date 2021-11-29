package com.italia.municipality.lakesebu.licensing.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.utils.LogU;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 02/08/2019
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class BusinessPermit {

	private long id;
	private String dateTrans;
	private String businessName;
	private String businessEngage;
	private String businessAddress;
	private String barangay;
	private String memoType;
	private String plateNo;
	private String issuedOn;
	private String validUntil;
	private String year;
	private String oic;
	private String mayor;
	private String ors;
	private int isActive;
	private String controlNo;
	private String type;
	private String empdtls;
	private double grossAmount;
	
	private BusinessCustomer customer;
	
	//temp
	private String capital;
	private String gross;
	
	public static String getNewPlateNo() {
		String sql = "SELECT businessplateno FROM businesspermit WHERE isactivebusiness=1 ORDER BY bid DESC limit 1";
		String plateNo = "0";
		String result = "0";
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			plateNo = rs.getString("businessplateno");
		}
		
		
		int no = Integer.valueOf(plateNo) +1;
		String newNo = no+"";
		int size = newNo.length();
		if(size==4) {
			result = no+"";
		}else if(size==3) {
			result = "0"+no;
		}else if(size==2) {
			result = "00"+no;
		}else if(size==1) {
			result = "000"+no;
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return result;
	}
	
	public static String getNewControlNo() {
		String sql = "SELECT controlno FROM businesspermit WHERE isactivebusiness=1 and year="+ DateUtils.getCurrentYear() +" ORDER BY bid DESC limit 1";
		String controlNo = "0";
		String year = null;
		String yearToday = DateUtils.getCurrentYear()+"";
		String result = yearToday + "-" + "001";
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			controlNo = rs.getString("controlno").split("-")[1];
			year = rs.getString("controlno").split("-")[0];
		}
		
		if(year==null) {
			year = yearToday;
			controlNo = "0";
		}
		
		
		int no = Integer.valueOf(controlNo)+1;
		String newNo = no+"";
		int size = newNo.length();
		if(size==3) {
			result = year +"-"+ no;
		}else if(size==2) {
			result = year +"-0"+no;
		}else if(size==1) {
			result = year +"-00"+no;
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return result;
	}
	
	public static List<BusinessPermit> retrieve(String sqlAdd, String[] params){
		List<BusinessPermit> coms = new ArrayList<BusinessPermit>();
		
		String tableBus="bz";
		String tableCus="cuz";
		
		String sql = "SELECT * FROM businesspermit "+ tableBus + ", businesscustomer " + tableCus +" WHERE "
				+ tableBus + ".customerid=" + tableCus + ".customerid ";
				
		
		sql = sql + sqlAdd;
				
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		System.out.println("Business SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			BusinessPermit bus = new BusinessPermit();
			try{bus.setId(rs.getLong("bid"));}catch(NullPointerException e){}
			try{bus.setDateTrans(rs.getString("datetrans"));}catch(NullPointerException e){}
			try{bus.setBusinessName(rs.getString("businessname"));}catch(NullPointerException e){}
			try{bus.setBusinessEngage(rs.getString("businessengage"));}catch(NullPointerException e){}
			try{bus.setBusinessAddress(rs.getString("businessaddress"));}catch(NullPointerException e){}
			try{bus.setBarangay(rs.getString("barangay"));}catch(NullPointerException e){}
			try{bus.setPlateNo(rs.getString("businessplateno"));}catch(NullPointerException e){}
			try{bus.setValidUntil(rs.getString("validuntil"));}catch(NullPointerException e){}
			try{bus.setIssuedOn(rs.getString("issuedon"));}catch(NullPointerException e){}
			try{bus.setYear(rs.getString("year"));}catch(NullPointerException e){}
			try{bus.setMemoType(rs.getString("memotype"));}catch(NullPointerException e){}
			try{bus.setOic(rs.getString("oic"));}catch(NullPointerException e){}
			try{bus.setMayor(rs.getString("mayor"));}catch(NullPointerException e){}
			try{bus.setControlNo(rs.getString("controlno"));}catch(NullPointerException e){}
			try{bus.setIsActive(rs.getInt("isactivebusiness"));}catch(NullPointerException e){}
			try{bus.setOrs(rs.getString("ors"));}catch(NullPointerException e){}
			try{bus.setType(rs.getString("typeof"));}catch(NullPointerException e){}
			try{bus.setEmpdtls(rs.getString("empdtls"));}catch(NullPointerException e){}
			try{bus.setGrossAmount(rs.getDouble("grossamnt"));}catch(NullPointerException e){}
			
			try {
				if("NEW".equalsIgnoreCase(rs.getString("typeof")) || "ADDITIONAL".equalsIgnoreCase(rs.getString("typeof"))) {
					bus.setCapital(Currency.formatAmount(rs.getDouble("grossamnt")));
				}else {
					bus.setGross(Currency.formatAmount(rs.getDouble("grossamnt")));
				}
			}catch(Exception e) {}
			
			
			BusinessCustomer cus = new BusinessCustomer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
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
			try{BusinessCustomer emergency = new BusinessCustomer();
			emergency.setId(rs.getLong("emeperson"));
			cus.setEmergencyContactPerson(emergency);}catch(NullPointerException e){}
			try{cus.setRelationship(rs.getInt("relid"));}catch(NullPointerException e){}
			
			Purok pur = new Purok();
			try{pur.setId(rs.getLong("purid"));}catch(NullPointerException e){}
			cus.setPurok(pur);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
			bus.setCustomer(cus);
			
			coms.add(bus);
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return coms;
	}
	
	public static BusinessPermit save(BusinessPermit na){
		if(na!=null){
			
			long id = BusinessPermit.getInfo(na.getId() ==0? BusinessPermit.getLatestId()+1 : na.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				na = BusinessPermit.insertData(na, "1");
			}else if(id==2){
				LogU.add("update Data ");
				na = BusinessPermit.updateData(na);
			}else if(id==3){
				LogU.add("added new Data ");
				na = BusinessPermit.insertData(na, "3");
			}
			
		}
		return na;
	}
	
	public void save(){
			
			long id = getInfo(getId() ==0? getLatestId()+1 : getId());
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
	
	public static BusinessPermit insertData(BusinessPermit na, String type){
		String sql = "INSERT INTO businesspermit ("
				+ "bid,"
				+ "customerid,"
				+ "datetrans,"
				+ "businessname,"
				+ "businessengage,"
				+ "businessaddress,"
				+ "barangay,"
				+ "businessplateno,"
				+ "validuntil,"
				+ "issuedon,"
				+ "year,"
				+ "memotype,"
				+ "oic,"
				+ "mayor,"
				+ "ors,"
				+ "controlno,"
				+ "isactivebusiness,"
				+ "typeof,"
				+ "empdtls,"
				+ "grossamnt)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table businesspermit");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			na.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			na.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setLong(cnt++, na.getCustomer()==null? 0 : na.getCustomer().getId());
		ps.setString(cnt++, na.getDateTrans());
		ps.setString(cnt++, na.getBusinessName());
		ps.setString(cnt++, na.getBusinessEngage());
		ps.setString(cnt++, na.getBusinessAddress());
		ps.setString(cnt++, na.getBarangay());
		ps.setString(cnt++, na.getPlateNo());
		ps.setString(cnt++, na.getValidUntil());
		ps.setString(cnt++, na.getIssuedOn());
		ps.setString(cnt++, na.getYear());
		ps.setString(cnt++, na.getMemoType());
		ps.setString(cnt++, na.getOic());
		ps.setString(cnt++, na.getMayor());
		ps.setString(cnt++, na.getOrs());
		ps.setString(cnt++, na.getControlNo());
		ps.setInt(cnt++, na.getIsActive());
		ps.setString(cnt++, na.getType());
		ps.setString(cnt++, na.getEmpdtls());
		ps.setDouble(cnt++, na.getGrossAmount());
		
		LogU.add(na.getCustomer()==null? 0 : na.getCustomer().getId());
		LogU.add(na.getDateTrans());
		LogU.add(na.getBusinessName());
		LogU.add(na.getBusinessEngage());
		LogU.add(na.getBusinessAddress());
		LogU.add(na.getBarangay());
		LogU.add(na.getPlateNo());
		LogU.add(na.getValidUntil());
		LogU.add(na.getIssuedOn());
		LogU.add(na.getYear());
		LogU.add(na.getMemoType());
		LogU.add(na.getOic());
		LogU.add(na.getMayor());
		LogU.add(na.getOrs());
		LogU.add(na.getControlNo());
		LogU.add(na.getIsActive());
		LogU.add(na.getType());
		LogU.add(na.getEmpdtls());
		LogU.add(na.getGrossAmount());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to businesspermit : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return na;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO businesspermit ("
				+ "bid,"
				+ "customerid,"
				+ "datetrans,"
				+ "businessname,"
				+ "businessengage,"
				+ "businessaddress,"
				+ "barangay,"
				+ "businessplateno,"
				+ "validuntil,"
				+ "issuedon,"
				+ "year,"
				+ "memotype,"
				+ "oic,"
				+ "mayor,"
				+ "ors,"
				+ "controlno,"
				+ "isactivebusiness,"
				+ "typeof,"
				+ "empdtls,"
				+ "grossamnt)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table businesspermit");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getBusinessName());
		ps.setString(cnt++, getBusinessEngage());
		ps.setString(cnt++, getBusinessAddress());
		ps.setString(cnt++, getBarangay());
		ps.setString(cnt++, getPlateNo());
		ps.setString(cnt++, getValidUntil());
		ps.setString(cnt++, getIssuedOn());
		ps.setString(cnt++, getYear());
		ps.setString(cnt++, getMemoType());
		ps.setString(cnt++, getOic());
		ps.setString(cnt++, getMayor());
		ps.setString(cnt++, getOrs());
		ps.setString(cnt++, getControlNo());
		ps.setInt(cnt++, getIsActive());
		ps.setString(cnt++, getType());
		ps.setString(cnt++, getEmpdtls());
		ps.setDouble(cnt++, getGrossAmount());
		
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getDateTrans());
		LogU.add(getBusinessName());
		LogU.add(getBusinessEngage());
		LogU.add(getBusinessAddress());
		LogU.add(getBarangay());
		LogU.add(getPlateNo());
		LogU.add(getValidUntil());
		LogU.add(getIssuedOn());
		LogU.add(getYear());
		LogU.add(getMemoType());
		LogU.add(getOic());
		LogU.add(getMayor());
		LogU.add(getOrs());
		LogU.add(getControlNo());
		LogU.add(getIsActive());
		LogU.add(getType());
		LogU.add(getEmpdtls());
		LogU.add(getGrossAmount());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to businesspermit : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static BusinessPermit updateData(BusinessPermit na){
		String sql = "UPDATE businesspermit SET "
				+ "customerid=?,"
				+ "datetrans=?,"
				+ "businessname=?,"
				+ "businessengage=?,"
				+ "businessaddress=?,"
				+ "barangay=?,"
				+ "businessplateno=?,"
				+ "validuntil=?,"
				+ "issuedon=?,"
				+ "year=?,"
				+ "memotype=?,"
				+ "oic=?,"
				+ "mayor=?,"
				+ "ors=?,"
				+ "controlno=?,"
				+ "typeof=?,"
				+ "empdtls=?,"
				+ "grossamnt=?" 
				+ " WHERE bid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table businesspermit");
		
		ps.setLong(cnt++, na.getCustomer()==null? 0 : na.getCustomer().getId());
		ps.setString(cnt++, na.getDateTrans());
		ps.setString(cnt++, na.getBusinessName());
		ps.setString(cnt++, na.getBusinessEngage());
		ps.setString(cnt++, na.getBusinessAddress());
		ps.setString(cnt++, na.getBarangay());
		ps.setString(cnt++, na.getPlateNo());
		ps.setString(cnt++, na.getValidUntil());
		ps.setString(cnt++, na.getIssuedOn());
		ps.setString(cnt++, na.getYear());
		ps.setString(cnt++, na.getMemoType());
		ps.setString(cnt++, na.getOic());
		ps.setString(cnt++, na.getMayor());
		ps.setString(cnt++, na.getOrs());
		ps.setString(cnt++, na.getControlNo());
		ps.setString(cnt++, na.getType());
		ps.setString(cnt++, na.getEmpdtls());
		ps.setDouble(cnt++, na.getGrossAmount());
		ps.setLong(cnt++, na.getId());
		
		
		LogU.add(na.getCustomer()==null? 0 : na.getCustomer().getId());
		LogU.add(na.getDateTrans());
		LogU.add(na.getBusinessName());
		LogU.add(na.getBusinessEngage());
		LogU.add(na.getBusinessAddress());
		LogU.add(na.getBarangay());
		LogU.add(na.getPlateNo());
		LogU.add(na.getValidUntil());
		LogU.add(na.getIssuedOn());
		LogU.add(na.getYear());
		LogU.add(na.getMemoType());
		LogU.add(na.getOic());
		LogU.add(na.getMayor());
		LogU.add(na.getOrs());
		LogU.add(na.getControlNo());
		LogU.add(na.getType());
		LogU.add(na.getEmpdtls());
		LogU.add(na.getGrossAmount());
		LogU.add(na.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to businesspermit : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return na;
	}
	
	public void updateData(){
		String sql = "UPDATE businesspermit SET "
				+ "customerid=?,"
				+ "datetrans=?,"
				+ "businessname=?,"
				+ "businessengage=?,"
				+ "businessaddress=?,"
				+ "barangay=?,"
				+ "businessplateno=?,"
				+ "validuntil=?,"
				+ "issuedon=?,"
				+ "year=?,"
				+ "memotype=?,"
				+ "oic=?,"
				+ "mayor=?,"
				+ "ors=?,"
				+ "controlno=?,"
				+ "typeof=?,"
				+ "empdtls=?,"
				+ "grossamnt=?" 
				+ " WHERE bid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table businesspermit");
		
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getBusinessName());
		ps.setString(cnt++, getBusinessEngage());
		ps.setString(cnt++, getBusinessAddress());
		ps.setString(cnt++, getBarangay());
		ps.setString(cnt++, getPlateNo());
		ps.setString(cnt++, getValidUntil());
		ps.setString(cnt++, getIssuedOn());
		ps.setString(cnt++, getYear());
		ps.setString(cnt++, getMemoType());
		ps.setString(cnt++, getOic());
		ps.setString(cnt++, getMayor());
		ps.setString(cnt++, getOrs());
		ps.setString(cnt++, getControlNo());
		ps.setString(cnt++, getType());
		ps.setString(cnt++, getEmpdtls());
		ps.setDouble(cnt++, getGrossAmount());
		ps.setLong(cnt++, getId());
		
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getDateTrans());
		LogU.add(getBusinessName());
		LogU.add(getBusinessEngage());
		LogU.add(getBusinessAddress());
		LogU.add(getBarangay());
		LogU.add(getPlateNo());
		LogU.add(getValidUntil());
		LogU.add(getIssuedOn());
		LogU.add(getYear());
		LogU.add(getMemoType());
		LogU.add(getOic());
		LogU.add(getMayor());
		LogU.add(getOrs());
		LogU.add(getControlNo());
		LogU.add(getType());
		LogU.add(getEmpdtls());
		LogU.add(getGrossAmount());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to businesspermit : " + s.getMessage());
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
		sql="SELECT bid FROM businesspermit  ORDER BY bid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("bid");
		}
		
		rs.close();
		prep.close();
		WebTISDatabaseConnect.close(conn);
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
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement("SELECT bid FROM businesspermit WHERE bid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "DELETE FROM businesspermit WHERE bid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
}
