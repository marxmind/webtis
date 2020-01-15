package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 06/15/2018
 * @version 1.0
 */
public class MonthTrans {

	private long id;
	private String dateTrans;
	private String orNumber;
	private double amountPaid;
	private int month;
	private int year;
	private int paymentType;
	private int isActive;
	private String remarks;
	
	private BuildingPaymentTrans buildingPayment;
	private BuildingStall stall;
	private Customer customer;
	private UserDtls userDtls;
	
	private String monthName;
	
	public static List<MonthTrans> retrieve(String sqlAdd, String[] params){
		List<MonthTrans> mks = Collections.synchronizedList(new ArrayList<MonthTrans>());
		String monthTable = "mnt";
		String payTable = "py";
		String cusTable = "cuz";
		String usrTable = "usr";
		String stallTable = "st";
		String sql = "SELECT * FROM monthtrans "+ monthTable +", bldgypaymenttrans "+ payTable 
				+", customer " + cusTable + ", userdtls " + usrTable + ", stall " + stallTable +
				" WHERE "
				+ monthTable + ".bldgpyid=" + payTable + ".bldgpyid AND "
				+ monthTable + ".customerid=" + cusTable + ".customerid AND "
				+ monthTable + ".userdtlsid=" + usrTable + ".userdtlsid AND "
				+ monthTable + ".stallid=" + stallTable + ".stallid AND "
				+ monthTable +".isactivatemonthtrans = 1 ";
		
		sql = sql + sqlAdd;
		
        //System.out.println("SQL "+sql);
		
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
		System.out.println("month trans SQL "+ ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			MonthTrans month = new MonthTrans();
			try{month.setId(rs.getLong("monthtransid"));}catch(NullPointerException e) {}
			try{month.setDateTrans(rs.getString("monthdatetrans"));}catch(NullPointerException e) {}
			try{month.setOrNumber(rs.getString("monthornumber"));}catch(NullPointerException e) {}
			try{month.setAmountPaid(rs.getDouble("monthamountpaid"));}catch(NullPointerException e) {}
			try{month.setMonth(rs.getInt("monthpaid"));}catch(NullPointerException e) {}
			try{month.setYear(rs.getInt("yearpaid"));}catch(NullPointerException e) {}
			try{month.setPaymentType(rs.getInt("monthpaymenttype"));}catch(NullPointerException e) {}
			try{month.setIsActive(rs.getInt("isactivatemonthtrans"));}catch(NullPointerException e) {}
			try{month.setRemarks(rs.getString("remarks"));}catch(NullPointerException e) {}
			try{month.setMonthName(DateUtils.getMonthName(rs.getInt("monthpaid")));}catch(NullPointerException e) {}
			
			BuildingPaymentTrans trans = new BuildingPaymentTrans();
			try{trans.setId(rs.getLong("bldgpyid"));}catch(NullPointerException e) {}
			try{trans.setDateTrans(rs.getString("paymentdatetrans"));}catch(NullPointerException e) {}
			try{trans.setOrNumber(rs.getString("ornumber"));}catch(NullPointerException e) {}
			try{trans.setAmountPaid(rs.getDouble("amountpaid"));}catch(NullPointerException e) {}
			try{trans.setPaymentType(rs.getInt("paymenttype"));}catch(NullPointerException e) {}
			try{trans.setIsActive(rs.getInt("isactivatepayment"));}catch(NullPointerException e) {}
			month.setBuildingPayment(trans);
			
			BuildingStall  stall = new BuildingStall();
			try{stall.setId(rs.getInt("stallid"));}catch(NullPointerException e) {}
			try{stall.setName(rs.getString("stallname"));}catch(NullPointerException e) {}
			try{stall.setType(rs.getInt("stalltype"));}catch(NullPointerException e) {}
			try{stall.setMonthlyRental(rs.getDouble("stallmonthlyrental"));}catch(NullPointerException e) {}
			try{stall.setIsOccupied(rs.getInt("isoccupied"));}catch(NullPointerException e) {}
			try{stall.setIsActive(rs.getInt("isactivatestall"));}catch(NullPointerException e) {}
			month.setStall(stall);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			month.setCustomer(cus);
			
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
			month.setUserDtls(user);
			
			mks.add(month);
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return mks;
	}
	
	public static MonthTrans retrieve(long id){
		MonthTrans month = new MonthTrans();
		String monthTable = "mnt";
		String payTable = "py";
		String cusTable = "cuz";
		String usrTable = "usr";
		String stallTable = "st";
		String sql = "SELECT * FROM monthtrans "+ monthTable +", bldgypaymenttrans "+ payTable 
				+", customer " + cusTable + ", userdtls " + usrTable + ", stall " + stallTable +
				" WHERE "
				+ monthTable + ".bldgpyid=" + payTable + ".bldgpyid AND "
				+ monthTable + ".customerid=" + cusTable + ".customerid AND "
				+ monthTable + ".userdtlsid=" + usrTable + ".userdtlsid AND "
				+ monthTable + ".stallid=" + stallTable + ".stallid AND "
				+ monthTable +".isactivatemonthtrans = 1 AND " + monthTable + ".monthtransid="+id;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{month.setId(rs.getLong("monthtransid"));}catch(NullPointerException e) {}
			try{month.setDateTrans(rs.getString("monthdatetrans"));}catch(NullPointerException e) {}
			try{month.setOrNumber(rs.getString("monthornumber"));}catch(NullPointerException e) {}
			try{month.setAmountPaid(rs.getDouble("monthamountpaid"));}catch(NullPointerException e) {}
			try{month.setMonth(rs.getInt("monthpaid"));}catch(NullPointerException e) {}
			try{month.setYear(rs.getInt("yearpaid"));}catch(NullPointerException e) {}
			try{month.setPaymentType(rs.getInt("monthpaymenttype"));}catch(NullPointerException e) {}
			try{month.setIsActive(rs.getInt("isactivatemonthtrans"));}catch(NullPointerException e) {}
			try{month.setRemarks(rs.getString("remarks"));}catch(NullPointerException e) {}
			try{month.setMonthName(DateUtils.getMonthName(rs.getInt("monthpaid")));}catch(NullPointerException e) {}
			
			BuildingPaymentTrans trans = new BuildingPaymentTrans();
			try{trans.setId(rs.getLong("bldgpyid"));}catch(NullPointerException e) {}
			try{trans.setDateTrans(rs.getString("paymentdatetrans"));}catch(NullPointerException e) {}
			try{trans.setOrNumber(rs.getString("ornumber"));}catch(NullPointerException e) {}
			try{trans.setAmountPaid(rs.getDouble("amountpaid"));}catch(NullPointerException e) {}
			try{trans.setPaymentType(rs.getInt("paymenttype"));}catch(NullPointerException e) {}
			try{trans.setIsActive(rs.getInt("isactivatepayment"));}catch(NullPointerException e) {}
			month.setBuildingPayment(trans);
			
			BuildingStall  stall = new BuildingStall();
			try{stall.setId(rs.getInt("stallid"));}catch(NullPointerException e) {}
			try{stall.setName(rs.getString("stallname"));}catch(NullPointerException e) {}
			try{stall.setType(rs.getInt("stalltype"));}catch(NullPointerException e) {}
			try{stall.setMonthlyRental(rs.getDouble("stallmonthlyrental"));}catch(NullPointerException e) {}
			try{stall.setIsOccupied(rs.getInt("isoccupied"));}catch(NullPointerException e) {}
			try{stall.setIsActive(rs.getInt("isactivatestall"));}catch(NullPointerException e) {}
			month.setStall(stall);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			month.setCustomer(cus);
			
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
			month.setUserDtls(user);
			
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return month;
	}
	
	public static MonthTrans save(MonthTrans mk){
		if(mk!=null){
			
			long id = MonthTrans.getInfo(mk.getId() ==0? MonthTrans.getLatestId()+1 : mk.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				mk = MonthTrans.insertData(mk, "1");
			}else if(id==2){
				LogU.add("update Data ");
				mk = MonthTrans.updateData(mk);
			}else if(id==3){
				LogU.add("added new Data ");
				mk = MonthTrans.insertData(mk, "3");
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
	
	public static MonthTrans insertData(MonthTrans mk, String type){
		String sql = "INSERT INTO monthtrans ("
				+ "monthtransid,"
				+ "monthdatetrans,"
				+ "monthornumber,"
				+ "monthamountpaid,"
				+ "monthpaid,"
				+ "yearpaid,"
				+ "monthpaymenttype,"
				+ "isactivatemonthtrans,"
				+ "remarks,"
				+ "bldgpyid,"
				+ "customerid,"
				+ "userdtlsid,"
				+ "stallid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table monthtrans");
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
		ps.setString(cnt++, mk.getDateTrans());
		ps.setString(cnt++, mk.getOrNumber());
		ps.setDouble(cnt++, mk.getAmountPaid());
		ps.setInt(cnt++, mk.getMonth());
		ps.setInt(cnt++, mk.getYear());
		ps.setInt(cnt++, mk.getPaymentType());
		ps.setInt(cnt++, mk.getIsActive());
		ps.setString(cnt++, mk.getRemarks());
		ps.setLong(cnt++, mk.getBuildingPayment()==null? 0 : mk.getBuildingPayment().getId());
		ps.setLong(cnt++, mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		ps.setLong(cnt++, mk.getUserDtls()==null? 0 : mk.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, mk.getStall()==null? 0 : mk.getStall().getId());
		
		LogU.add(mk.getDateTrans());
		LogU.add(mk.getOrNumber());
		LogU.add(mk.getAmountPaid());
		LogU.add(mk.getMonth());
		LogU.add(mk.getYear());
		LogU.add(mk.getPaymentType());
		LogU.add(mk.getIsActive());
		LogU.add(mk.getRemarks());
		LogU.add(mk.getBuildingPayment()==null? 0 : mk.getBuildingPayment().getId());
		LogU.add(mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		LogU.add(mk.getUserDtls()==null? 0 : mk.getUserDtls().getUserdtlsid());
		LogU.add(mk.getStall()==null? 0 : mk.getStall().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to monthtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mk;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO monthtrans ("
				+ "monthtransid,"
				+ "monthdatetrans,"
				+ "monthornumber,"
				+ "monthamountpaid,"
				+ "monthpaid,"
				+ "yearpaid,"
				+ "monthpaymenttype,"
				+ "isactivatemonthtrans,"
				+ "remarks,"
				+ "bldgpyid,"
				+ "customerid,"
				+ "userdtlsid,"
				+ "stallid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table monthtrans");
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
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getOrNumber());
		ps.setDouble(cnt++, getAmountPaid());
		ps.setInt(cnt++, getMonth());
		ps.setInt(cnt++, getYear());
		ps.setInt(cnt++, getPaymentType());
		ps.setInt(cnt++, getIsActive());
		ps.setString(cnt++, getRemarks());
		ps.setLong(cnt++, getBuildingPayment()==null? 0 : getBuildingPayment().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getStall()==null? 0 : getStall().getId());
		
		LogU.add(getDateTrans());
		LogU.add(getOrNumber());
		LogU.add(getAmountPaid());
		LogU.add(getMonth());
		LogU.add(getYear());
		LogU.add(getPaymentType());
		LogU.add(getIsActive());
		LogU.add(getRemarks());
		LogU.add(getBuildingPayment()==null? 0 : getBuildingPayment().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getStall()==null? 0 : getStall().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to monthtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static MonthTrans updateData(MonthTrans mk){
		String sql = "UPDATE monthtrans SET "
				+ "monthdatetrans=?,"
				+ "monthornumber=?,"
				+ "monthamountpaid=?,"
				+ "monthpaid=?,"
				+ "yearpaid=?,"
				+ "monthpaymenttype=?,"
				+ "remarks=?,"
				+ "bldgpyid=?,"
				+ "customerid=?,"
				+ "userdtlsid=?,"
				+ "stallid=?" 
				+ " WHERE monthtransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table monthtrans");
		
		ps.setString(cnt++, mk.getDateTrans());
		ps.setString(cnt++, mk.getOrNumber());
		ps.setDouble(cnt++, mk.getAmountPaid());
		ps.setInt(cnt++, mk.getMonth());
		ps.setInt(cnt++, mk.getYear());
		ps.setInt(cnt++, mk.getPaymentType());
		ps.setString(cnt++, mk.getRemarks());
		ps.setLong(cnt++, mk.getBuildingPayment()==null? 0 : mk.getBuildingPayment().getId());
		ps.setLong(cnt++, mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		ps.setLong(cnt++, mk.getUserDtls()==null? 0 : mk.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, mk.getStall()==null? 0 : mk.getStall().getId());
		ps.setLong(cnt++, mk.getId());
		
		LogU.add(mk.getDateTrans());
		LogU.add(mk.getOrNumber());
		LogU.add(mk.getAmountPaid());
		LogU.add(mk.getMonth());
		LogU.add(mk.getYear());
		LogU.add(mk.getPaymentType());
		LogU.add(mk.getRemarks());
		LogU.add(mk.getBuildingPayment()==null? 0 : mk.getBuildingPayment().getId());
		LogU.add(mk.getCustomer()==null? 0 : mk.getCustomer().getId());
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
			LogU.add("error updating data to monthtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mk;
	}
	
	public void updateData(){
		String sql = "UPDATE monthtrans SET "
				+ "monthdatetrans=?,"
				+ "monthornumber=?,"
				+ "monthamountpaid=?,"
				+ "monthpaid=?,"
				+ "yearpaid=?,"
				+ "monthpaymenttype=?,"
				+ "remarks=?,"
				+ "bldgpyid=?,"
				+ "customerid=?,"
				+ "userdtlsid=?,"
				+ "stallid=?" 
				+ " WHERE monthtransid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table monthtrans");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getOrNumber());
		ps.setDouble(cnt++, getAmountPaid());
		ps.setInt(cnt++, getMonth());
		ps.setInt(cnt++, getYear());
		ps.setInt(cnt++, getPaymentType());
		ps.setString(cnt++, getRemarks());
		ps.setLong(cnt++, getBuildingPayment()==null? 0 : getBuildingPayment().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getStall()==null? 0 : getStall().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getOrNumber());
		LogU.add(getAmountPaid());
		LogU.add(getMonth());
		LogU.add(getYear());
		LogU.add(getPaymentType());
		LogU.add(getRemarks());
		LogU.add(getBuildingPayment()==null? 0 : getBuildingPayment().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
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
			LogU.add("error updating data to monthtrans : " + s.getMessage());
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
		sql="SELECT monthtransid FROM monthtrans  ORDER BY monthtransid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("monthtransid");
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
		ps = conn.prepareStatement("SELECT monthtransid FROM monthtrans WHERE monthtransid=?");
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
		String sql = "UPDATE monthtrans set isactivatemonthtrans=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE monthtransid=?";
		
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
	public String getDateTrans() {
		return dateTrans;
	}
	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}
	public String getOrNumber() {
		return orNumber;
	}
	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}
	public double getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public BuildingPaymentTrans getBuildingPayment() {
		return buildingPayment;
	}
	public void setBuildingPayment(BuildingPaymentTrans buildingPayment) {
		this.buildingPayment = buildingPayment;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
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

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	
}
