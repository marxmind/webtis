package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.licensing.controller.Customer;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 06/15/2018
 * @version 1.0
 */
public class BuildingPaymentTrans {

	private long id;
	private String dateTrans;
	private String orNumber;
	private double amountPaid;
	private int paymentType;
	private int isActive;
	
	private BuildingStall stall;
	private Customer customer;
	private UserDtls userDtls;
	
	private List<MonthTrans> months = Collections.synchronizedList(new ArrayList<MonthTrans>());
	
	public static List<BuildingPaymentTrans> retrieve(String sqlAdd, String[] params){
		List<BuildingPaymentTrans> mks = Collections.synchronizedList(new ArrayList<BuildingPaymentTrans>());
		String payTable = "py";
		String bldgTable = "bl";
		String cusTable = "cuz";
		String usrTable = "usr";
		String sql = "SELECT * FROM bldgypaymenttrans "+ payTable + ", stall " + bldgTable
				+", customer " + cusTable + ", userdtls " + usrTable +
				" WHERE "
				+ payTable + ".stallid=" + bldgTable + ".stallid AND "
				+ payTable + ".customerid=" + cusTable + ".customerid AND "
				+ payTable + ".userdtlsid=" + usrTable + ".userdtlsid AND "
				+ payTable +".isactivatepayment = 1 ";
		
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
		System.out.println("bldgypaymenttrans "+ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			BuildingPaymentTrans trans = new BuildingPaymentTrans();
			try{trans.setId(rs.getLong("bldgpyid"));}catch(NullPointerException e) {}
			try{trans.setDateTrans(rs.getString("paymentdatetrans"));}catch(NullPointerException e) {}
			try{trans.setOrNumber(rs.getString("ornumber"));}catch(NullPointerException e) {}
			try{trans.setAmountPaid(rs.getDouble("amountpaid"));}catch(NullPointerException e) {}
			try{trans.setPaymentType(rs.getInt("paymenttype"));}catch(NullPointerException e) {}
			try{trans.setIsActive(rs.getInt("isactivatepayment"));}catch(NullPointerException e) {}
			
			BuildingStall  stall = new BuildingStall();
			try{stall.setId(rs.getInt("stallid"));}catch(NullPointerException e) {}
			try{stall.setName(rs.getString("stallname"));}catch(NullPointerException e) {}
			try{stall.setType(rs.getInt("stalltype"));}catch(NullPointerException e) {}
			try{stall.setMonthlyRental(rs.getDouble("stallmonthlyrental"));}catch(NullPointerException e) {}
			try{stall.setIsOccupied(rs.getInt("isoccupied"));}catch(NullPointerException e) {}
			try{stall.setIsActive(rs.getInt("isactivatestall"));}catch(NullPointerException e) {}
			trans.setStall(stall);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			trans.setCustomer(cus);
			
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
			trans.setUserDtls(user);
			
			mks.add(trans);
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return mks;
	}
	
	public static BuildingPaymentTrans retrieve(long id){
		BuildingPaymentTrans trans = new BuildingPaymentTrans();
		String payTable = "py";
		String bldgTable = "bl";
		String cusTable = "cuz";
		String usrTable = "usr";
		String sql = "SELECT * FROM bldgypaymenttrans "+ payTable + ", stall " + bldgTable
				+", customer " + cusTable + ", userdtls " + usrTable +
				" WHERE "
				+ payTable + ".stallid=" + bldgTable + ".stallid AND "
				+ payTable + ".customerid=" + cusTable + ".customerid AND "
				+ payTable + ".userdtlsid=" + usrTable + ".userdtlsid AND "
				+ payTable +".isactivatepayment = 1 AND " + payTable + ".bldgpyid="+ id;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{trans.setId(rs.getLong("bldgpyid"));}catch(NullPointerException e) {}
			try{trans.setDateTrans(rs.getString("paymentdatetrans"));}catch(NullPointerException e) {}
			try{trans.setOrNumber(rs.getString("ornumber"));}catch(NullPointerException e) {}
			try{trans.setAmountPaid(rs.getDouble("amountpaid"));}catch(NullPointerException e) {}
			try{trans.setPaymentType(rs.getInt("paymenttype"));}catch(NullPointerException e) {}
			try{trans.setIsActive(rs.getInt("isactivatepayment"));}catch(NullPointerException e) {}
			
			BuildingStall  stall = new BuildingStall();
			try{stall.setId(rs.getInt("stallid"));}catch(NullPointerException e) {}
			try{stall.setName(rs.getString("stallname"));}catch(NullPointerException e) {}
			try{stall.setType(rs.getInt("stalltype"));}catch(NullPointerException e) {}
			try{stall.setMonthlyRental(rs.getDouble("stallmonthlyrental"));}catch(NullPointerException e) {}
			try{stall.setIsOccupied(rs.getInt("isoccupied"));}catch(NullPointerException e) {}
			try{stall.setIsActive(rs.getInt("isactivatestall"));}catch(NullPointerException e) {}
			trans.setStall(stall);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			trans.setCustomer(cus);
			
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
			trans.setUserDtls(user);
			
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	public static BuildingPaymentTrans save(BuildingPaymentTrans mk){
		if(mk!=null){
			
			long id = BuildingPaymentTrans.getInfo(mk.getId() ==0? BuildingPaymentTrans.getLatestId()+1 : mk.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				mk = BuildingPaymentTrans.insertData(mk, "1");
			}else if(id==2){
				LogU.add("update Data ");
				mk = BuildingPaymentTrans.updateData(mk);
			}else if(id==3){
				LogU.add("added new Data ");
				mk = BuildingPaymentTrans.insertData(mk, "3");
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
	
	public static BuildingPaymentTrans insertData(BuildingPaymentTrans mk, String type){
		String sql = "INSERT INTO bldgypaymenttrans ("
				+ "bldgpyid,"
				+ "paymentdatetrans,"
				+ "ornumber,"
				+ "amountpaid,"
				+ "paymenttype,"
				+ "isactivatepayment,"
				+ "stallid,"
				+ "customerid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table bldgypaymenttrans");
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
		ps.setInt(cnt++, mk.getPaymentType());
		ps.setInt(cnt++, mk.getIsActive());
		ps.setInt(cnt++, mk.getStall()==null? 0 : mk.getStall().getId());
		ps.setLong(cnt++, mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		ps.setLong(cnt++, mk.getUserDtls()==null? 0 : mk.getUserDtls().getUserdtlsid());
		
		LogU.add(mk.getDateTrans());
		LogU.add(mk.getOrNumber());
		LogU.add(mk.getAmountPaid());
		LogU.add(mk.getPaymentType());
		LogU.add(mk.getIsActive());
		LogU.add(mk.getStall()==null? 0 : mk.getStall().getId());
		LogU.add(mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		LogU.add(mk.getUserDtls()==null? 0 : mk.getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to bldgypaymenttrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mk;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO bldgypaymenttrans ("
				+ "bldgpyid,"
				+ "paymentdatetrans,"
				+ "ornumber,"
				+ "amountpaid,"
				+ "paymenttype,"
				+ "isactivatepayment,"
				+ "stallid,"
				+ "customerid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table bldgypaymenttrans");
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
		ps.setInt(cnt++, getPaymentType());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getStall()==null? 0 : getStall().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add(getDateTrans());
		LogU.add(getOrNumber());
		LogU.add(getAmountPaid());
		LogU.add(getPaymentType());
		LogU.add(getIsActive());
		LogU.add(getStall()==null? 0 : getStall().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to bldgypaymenttrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static BuildingPaymentTrans updateData(BuildingPaymentTrans mk){
		String sql = "UPDATE bldgypaymenttrans SET "
				+ "paymentdatetrans=?,"
				+ "ornumber=?,"
				+ "amountpaid=?,"
				+ "paymenttype=?,"
				+ "stallid=?,"
				+ "customerid=?,"
				+ "userdtlsid=?" 
				+ " WHERE bldgpyid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table bldgypaymenttrans");
		
		ps.setString(cnt++, mk.getDateTrans());
		ps.setString(cnt++, mk.getOrNumber());
		ps.setDouble(cnt++, mk.getAmountPaid());
		ps.setInt(cnt++, mk.getPaymentType());
		ps.setInt(cnt++, mk.getStall()==null? 0 : mk.getStall().getId());
		ps.setLong(cnt++, mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		ps.setLong(cnt++, mk.getUserDtls()==null? 0 : mk.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, mk.getId());
		
		LogU.add(mk.getDateTrans());
		LogU.add(mk.getOrNumber());
		LogU.add(mk.getAmountPaid());
		LogU.add(mk.getPaymentType());
		LogU.add(mk.getIsActive());
		LogU.add(mk.getStall()==null? 0 : mk.getStall().getId());
		LogU.add(mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		LogU.add(mk.getUserDtls()==null? 0 : mk.getUserDtls().getUserdtlsid());
		LogU.add(mk.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to bldgypaymenttrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mk;
	}
	
	public void updateData(){
		String sql = "UPDATE bldgypaymenttrans SET "
				+ "paymentdatetrans=?,"
				+ "ornumber=?,"
				+ "amountpaid=?,"
				+ "paymenttype=?,"
				+ "stallid=?,"
				+ "customerid=?,"
				+ "userdtlsid=?" 
				+ " WHERE bldgpyid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table bldgypaymenttrans");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getOrNumber());
		ps.setDouble(cnt++, getAmountPaid());
		ps.setInt(cnt++, getPaymentType());
		ps.setInt(cnt++, getStall()==null? 0 : getStall().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getOrNumber());
		LogU.add(getAmountPaid());
		LogU.add(getPaymentType());
		LogU.add(getIsActive());
		LogU.add(getStall()==null? 0 : getStall().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to bldgypaymenttrans : " + s.getMessage());
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
		sql="SELECT bldgpyid FROM bldgypaymenttrans  ORDER BY bldgpyid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("bldgpyid");
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
		ps = conn.prepareStatement("SELECT bldgpyid FROM bldgypaymenttrans WHERE bldgpyid=?");
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
		String sql = "UPDATE bldgypaymenttrans set isactivatepayment=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE bldgpyid=?";
		
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

	public List<MonthTrans> getMonths() {
		return months;
	}

	public void setMonths(List<MonthTrans> months) {
		this.months = months;
	}
	
}
