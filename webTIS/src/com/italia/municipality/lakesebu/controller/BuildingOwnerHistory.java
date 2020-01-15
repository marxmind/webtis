package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 06/15/2018
 * @version 1.0
 */
public class BuildingOwnerHistory {

	private long id;
	private String dateStart;
	private String dateEnd;
	private int isCurrentOwner;
	private int isActive;
	
	private BuildingStall stall;
	private Customer customer;
	private MarketBuilding marketBuilding;
	private UserDtls userDtls;
	
	public static List<BuildingOwnerHistory> retrieve(String sqlAdd, String[] params){
		List<BuildingOwnerHistory> mks = Collections.synchronizedList(new ArrayList<BuildingOwnerHistory>());
		String ownTable = "own";
		String bldgTable = "bl";
		String stallTable = "st";
		String cusTable = "cuz";
		String usrTable = "usr";
		String sql = "SELECT * FROM bldgownertranshis "+ ownTable + ", marketbuilding " + bldgTable
				+", customer " + cusTable + ", userdtls " + usrTable + ", stall " + stallTable +
				" WHERE "
				+ ownTable + ".customerid=" + cusTable + ".customerid AND "
				+ ownTable + ".bldgid=" + bldgTable + ".bldgid AND "
				+ ownTable + ".userdtlsid=" + usrTable + ".userdtlsid AND "
				+ ownTable + ".stallid=" + stallTable + ".stallid AND "
				+ ownTable +".isactivateownertrans = 1 ";
		
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
		//System.out.println("BuildingOwnerHistory SQL "+ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			BuildingOwnerHistory own = new BuildingOwnerHistory();
			try{own.setId(rs.getLong("ownertransid"));}catch(NullPointerException e){}
			try{own.setDateStart(rs.getString("ownerdatestart"));}catch(NullPointerException e){}
			try{own.setDateEnd(rs.getString("ownerdateend"));}catch(NullPointerException e){}
			try{own.setIsCurrentOwner(rs.getInt("iscurrentowner"));}catch(NullPointerException e){}
			try{own.setIsActive(rs.getInt("isactivateownertrans"));}catch(NullPointerException e){}
			
			MarketBuilding mk = new MarketBuilding();
			try{mk.setId(rs.getInt("bldgid"));}catch(NullPointerException e) {}
			try{mk.setName(rs.getString("bldgname"));}catch(NullPointerException e) {}
			try{mk.setStallNumber(rs.getString("stallno"));}catch(NullPointerException e) {}
			try{mk.setType(rs.getInt("bldgtype"));}catch(NullPointerException e) {}
			try{mk.setMonthlyRental(rs.getDouble("monthylyrental"));}catch(NullPointerException e) {}
			try{mk.setIsActive(rs.getInt("isactivatebldg"));}catch(NullPointerException e) {}
			own.setMarketBuilding(mk);
			
			BuildingStall  stall = new BuildingStall();
			try{stall.setId(rs.getInt("stallid"));}catch(NullPointerException e) {}
			try{stall.setName(rs.getString("stallname"));}catch(NullPointerException e) {}
			try{stall.setType(rs.getInt("stalltype"));}catch(NullPointerException e) {}
			try{stall.setMonthlyRental(rs.getDouble("stallmonthlyrental"));}catch(NullPointerException e) {}
			try{stall.setIsOccupied(rs.getInt("isoccupied"));}catch(NullPointerException e) {}
			try{stall.setIsActive(rs.getInt("isactivatestall"));}catch(NullPointerException e) {}
			own.setStall(stall);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			own.setCustomer(cus);
			
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
			own.setUserDtls(user);
			
			mks.add(own);
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return mks;
	}
	
	public static BuildingOwnerHistory retrieve(long id){
		BuildingOwnerHistory own = new BuildingOwnerHistory();
		String ownTable = "own";
		String bldgTable = "bl";
		String stallTable = "st";
		String cusTable = "cuz";
		String usrTable = "usr";
		String sql = "SELECT * FROM bldgownertranshis "+ ownTable + ", marketbuilding " + bldgTable
				+", customer " + cusTable + ", userdtls " + usrTable + ", stall " + stallTable +
				" WHERE "
				+ ownTable + ".customerid=" + cusTable + ".customerid AND "
				+ ownTable + ".bldgid=" + bldgTable + ".bldgid AND "
				+ ownTable + ".userdtlsid=" + usrTable + ".userdtlsid AND "
				+ ownTable + ".stallid=" + stallTable + ".stallid AND "
				+ ownTable +".isactivateownertrans = 1 AND " + ownTable +".ownertransid=" +id;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{own.setId(rs.getLong("ownertransid"));}catch(NullPointerException e){}
			try{own.setDateStart(rs.getString("ownerdatestart"));}catch(NullPointerException e){}
			try{own.setDateEnd(rs.getString("ownerdateend"));}catch(NullPointerException e){}
			try{own.setIsCurrentOwner(rs.getInt("iscurrentowner"));}catch(NullPointerException e){}
			try{own.setIsActive(rs.getInt("isactivateownertrans"));}catch(NullPointerException e){}
			
			MarketBuilding mk = new MarketBuilding();
			try{mk.setId(rs.getInt("bldgid"));}catch(NullPointerException e) {}
			try{mk.setName(rs.getString("bldgname"));}catch(NullPointerException e) {}
			try{mk.setStallNumber(rs.getString("stallno"));}catch(NullPointerException e) {}
			try{mk.setType(rs.getInt("bldgtype"));}catch(NullPointerException e) {}
			try{mk.setMonthlyRental(rs.getDouble("monthylyrental"));}catch(NullPointerException e) {}
			try{mk.setIsActive(rs.getInt("isactivatebldg"));}catch(NullPointerException e) {}
			own.setMarketBuilding(mk);
			
			BuildingStall  stall = new BuildingStall();
			try{stall.setId(rs.getInt("stallid"));}catch(NullPointerException e) {}
			try{stall.setName(rs.getString("stallname"));}catch(NullPointerException e) {}
			try{stall.setType(rs.getInt("stalltype"));}catch(NullPointerException e) {}
			try{stall.setMonthlyRental(rs.getDouble("stallmonthlyrental"));}catch(NullPointerException e) {}
			try{stall.setIsOccupied(rs.getInt("isoccupied"));}catch(NullPointerException e) {}
			try{stall.setIsActive(rs.getInt("isactivatestall"));}catch(NullPointerException e) {}
			own.setStall(stall);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			own.setCustomer(cus);
			
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
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		return own;
	}
	
	public static BuildingOwnerHistory save(BuildingOwnerHistory mk){
		if(mk!=null){
			
			long id = BuildingOwnerHistory.getInfo(mk.getId() ==0? BuildingOwnerHistory.getLatestId()+1 : mk.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				mk = BuildingOwnerHistory.insertData(mk, "1");
			}else if(id==2){
				LogU.add("update Data ");
				mk = BuildingOwnerHistory.updateData(mk);
			}else if(id==3){
				LogU.add("added new Data ");
				mk = BuildingOwnerHistory.insertData(mk, "3");
			}
			
		}
		return mk;
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
	
	public static BuildingOwnerHistory insertData(BuildingOwnerHistory mk, String type){
		String sql = "INSERT INTO bldgownertranshis ("
				+ "ownertransid,"
				+ "ownerdatestart,"
				+ "ownerdateend,"
				+ "iscurrentowner,"
				+ "isactivateownertrans,"
				+ "customerid,"
				+ "bldgid,"
				+ "userdtlsid,"
				+ "stallid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table bldgownertranshis");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			mk.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			mk.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, mk.getDateStart());
		ps.setString(cnt++, mk.getDateEnd());
		ps.setInt(cnt++, mk.getIsCurrentOwner());
		ps.setInt(cnt++, mk.getIsActive());
		ps.setLong(cnt++, mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		ps.setLong(cnt++, mk.getMarketBuilding()==null? 0 : mk.getMarketBuilding().getId());
		ps.setLong(cnt++, mk.getUserDtls()==null? 0 : mk.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, mk.getStall()==null? 0 : mk.getStall().getId());
		
		LogU.add(mk.getDateStart());
		LogU.add(mk.getDateEnd());
		LogU.add(mk.getIsCurrentOwner());
		LogU.add(mk.getIsActive());
		LogU.add(mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		LogU.add(mk.getMarketBuilding()==null? 0 : mk.getMarketBuilding().getId());
		LogU.add(mk.getUserDtls()==null? 0 : mk.getUserDtls().getUserdtlsid());
		LogU.add(mk.getStall()==null? 0 : mk.getStall().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to bldgownertranshis : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mk;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO bldgownertranshis ("
				+ "ownertransid,"
				+ "ownerdatestart,"
				+ "ownerdateend,"
				+ "iscurrentowner,"
				+ "isactivateownertrans,"
				+ "customerid,"
				+ "bldgid,"
				+ "userdtlsid,"
				+ "stallid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table bldgownertranshis");
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
		ps.setString(cnt++, getDateStart());
		ps.setString(cnt++, getDateEnd());
		ps.setInt(cnt++, getIsCurrentOwner());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getMarketBuilding()==null? 0 : getMarketBuilding().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getStall()==null? 0 : getStall().getId());
		
		LogU.add(getDateStart());
		LogU.add(getDateEnd());
		LogU.add(getIsCurrentOwner());
		LogU.add(getIsActive());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getMarketBuilding()==null? 0 : getMarketBuilding().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getStall()==null? 0 : getStall().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to bldgownertranshis : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static BuildingOwnerHistory updateData(BuildingOwnerHistory mk){
		String sql = "UPDATE bldgownertranshis SET "
				+ "ownerdatestart=?,"
				+ "ownerdateend=?,"
				+ "iscurrentowner=?,"
				+ "customerid=?,"
				+ "bldgid=?,"
				+ "userdtlsid=?,"
				+ "stallid=?" 
				+ " WHERE ownertransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table bldgownertranshis");
		
		ps.setString(cnt++, mk.getDateStart());
		ps.setString(cnt++, mk.getDateEnd());
		ps.setInt(cnt++, mk.getIsCurrentOwner());
		ps.setLong(cnt++, mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		ps.setLong(cnt++, mk.getMarketBuilding()==null? 0 : mk.getMarketBuilding().getId());
		ps.setLong(cnt++, mk.getUserDtls()==null? 0 : mk.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, mk.getStall()==null? 0 : mk.getStall().getId());
		ps.setLong(cnt++, mk.getId());
		
		LogU.add(mk.getDateStart());
		LogU.add(mk.getDateEnd());
		LogU.add(mk.getIsCurrentOwner());
		LogU.add(mk.getIsActive());
		LogU.add(mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		LogU.add(mk.getMarketBuilding()==null? 0 : mk.getMarketBuilding().getId());
		LogU.add(mk.getUserDtls()==null? 0 : mk.getUserDtls().getUserdtlsid());
		LogU.add(mk.getStall()==null? 0 : mk.getStall().getId());
		LogU.add(mk.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to bldgownertranshis : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mk;
	}
	
	public void updateData(){
		String sql = "UPDATE bldgownertranshis SET "
				+ "ownerdatestart=?,"
				+ "ownerdateend=?,"
				+ "iscurrentowner=?,"
				+ "customerid=?,"
				+ "bldgid=?,"
				+ "userdtlsid=?,"
				+ "stallid=?" 
				+ " WHERE ownertransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table bldgownertranshis");
		
		ps.setString(cnt++, getDateStart());
		ps.setString(cnt++, getDateEnd());
		ps.setInt(cnt++, getIsCurrentOwner());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getMarketBuilding()==null? 0 : getMarketBuilding().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getStall()==null? 0 : getStall().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateStart());
		LogU.add(getDateEnd());
		LogU.add(getIsCurrentOwner());
		LogU.add(getIsActive());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getMarketBuilding()==null? 0 : getMarketBuilding().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getStall()==null? 0 : getStall().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to bldgownertranshis : " + s.getMessage());
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
		sql="SELECT ownertransid FROM bldgownertranshis  ORDER BY ownertransid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("ownertransid");
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
		ps = conn.prepareStatement("SELECT ownertransid FROM bldgownertranshis WHERE ownertransid=?");
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
		String sql = "UPDATE bldgownertranshis set isactivateownertrans=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE ownertransid=?";
		
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
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDateStart() {
		return dateStart;
	}
	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}
	public String getDateEnd() {
		return dateEnd;
	}
	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}
	public int getIsCurrentOwner() {
		return isCurrentOwner;
	}
	public void setIsCurrentOwner(int isCurrentOwner) {
		this.isCurrentOwner = isCurrentOwner;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public MarketBuilding getMarketBuilding() {
		return marketBuilding;
	}
	public void setMarketBuilding(MarketBuilding marketBuilding) {
		this.marketBuilding = marketBuilding;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}

	public BuildingStall getStall() {
		return stall;
	}

	public void setStall(BuildingStall stall) {
		this.stall = stall;
	}
	
}
