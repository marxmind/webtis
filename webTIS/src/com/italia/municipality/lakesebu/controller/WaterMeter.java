package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @since 9/26/2019
 * @version 1.0
 */
public class WaterMeter {

	private long id;
	private String dateTrans;
	private String meterNo;
	private int status;
	private int isActive;
	
	private Customer customer;
	private WaterAccnt account;
	
	public static void main(String[] args) {
		WaterMeter m = new WaterMeter();
		m.setId(1);
		m.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		m.setMeterNo("0001234569");
		m.setStatus(1);
		m.setIsActive(1);
		Customer customer = new Customer();
		customer.setId(1);
		m.setCustomer(customer);
		WaterAccnt a = new WaterAccnt();
		a.setId(1);
		m.setAccount(a);
		m.save();
	}
	
	public static List<WaterMeter> retrieve(String sqlAdd, String[] params){
		List<WaterMeter> was = new ArrayList<WaterMeter>();
		
		String tableMt = "mt";
		String tableAcc = "acc";
		String tableCus = "cuz";
		String sql = "SELECT * FROM watermeter "+ tableMt +" ,accwater "+tableAcc+", customer "+ tableCus +"  WHERE  " + 
				tableMt +".customerid=" + tableCus + ".customerid AND " +
				tableMt +".accid="+ tableAcc + ".accid"; 
		
		sql += sqlAdd;
		
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
		System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			WaterMeter meter = new WaterMeter();
			try{meter.setId(rs.getLong("mid"));}catch(NullPointerException e){}
			try{meter.setDateTrans(rs.getString("midatetrans"));}catch(NullPointerException e){}
			try{meter.setMeterNo(rs.getString("meterno"));}catch(NullPointerException e){}
			try{meter.setStatus(rs.getInt("mstatus"));}catch(NullPointerException e){}
			try{meter.setIsActive(rs.getInt("isactivem"));}catch(NullPointerException e){}
			
			WaterAccnt acc = new WaterAccnt();
			try{acc.setId(rs.getLong("accid"));}catch(NullPointerException e){}
			try{acc.setDateTrans(rs.getString("accdatetrans"));}catch(NullPointerException e){}
			try{acc.setLocation(rs.getString("location"));}catch(NullPointerException e){}
			try{acc.setStatus(rs.getInt("accstatus"));}catch(NullPointerException e){}
			try{acc.setIsActive(rs.getInt("isactiveacc"));}catch(NullPointerException e){}
			meter.setAccount(acc);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			meter.setCustomer(cus);
			
			was.add(meter);
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return was;
	}
	
	public static WaterMeter save(WaterMeter is){
		if(is!=null){
			
			long id = WaterMeter.getInfo(is.getId() ==0? WaterMeter.getLatestId()+1 : is.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				is = WaterMeter.insertData(is, "1");
			}else if(id==2){
				LogU.add("update Data ");
				is = WaterMeter.updateData(is);
			}else if(id==3){
				LogU.add("added new Data ");
				is = WaterMeter.insertData(is, "3");
			}
			
		}
		return is;
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
	
	public static WaterMeter insertData(WaterMeter wa, String type){
		String sql = "INSERT INTO watermeter ("
				+ "mid,"
				+ "midatetrans,"
				+ "meterno,"
				+ "mstatus,"
				+ "isactivem,"
				+ "customerid,"
				+ "accid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table watermeter");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			wa.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			wa.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, wa.getDateTrans());
		ps.setString(cnt++, wa.getMeterNo());
		ps.setInt(cnt++, wa.getStatus());
		ps.setInt(cnt++, wa.getIsActive());
		ps.setLong(cnt++, wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		ps.setLong(cnt++, wa.getAccount()==null? 0 : wa.getAccount().getId());
		
		LogU.add(wa.getDateTrans());
		LogU.add(wa.getMeterNo());
		LogU.add(wa.getStatus());
		LogU.add(wa.getIsActive());
		LogU.add(wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		LogU.add(wa.getAccount()==null? 0 : wa.getAccount().getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to watermeter : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return wa;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO watermeter ("
				+ "mid,"
				+ "midatetrans,"
				+ "meterno,"
				+ "mstatus,"
				+ "isactivem,"
				+ "customerid,"
				+ "accid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table watermeter");
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
		ps.setString(cnt++, getMeterNo());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getAccount()==null? 0 : getAccount().getId());
		
		LogU.add(getDateTrans());
		LogU.add(getMeterNo());
		LogU.add(getStatus());
		LogU.add(getIsActive());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getAccount()==null? 0 : getAccount().getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to watermeter : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static WaterMeter updateData(WaterMeter wa){
		String sql = "UPDATE watermeter SET "
				+ "midatetrans=?,"
				+ "meterno=?,"
				+ "mstatus=?,"
				+ "customerid=?,"
				+ "accid=?" 
				+ " WHERE mid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table watermeter");
		
		ps.setString(cnt++, wa.getDateTrans());
		ps.setString(cnt++, wa.getMeterNo());
		ps.setInt(cnt++, wa.getStatus());
		ps.setLong(cnt++, wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		ps.setLong(cnt++, wa.getAccount()==null? 0 : wa.getAccount().getId());
		ps.setLong(cnt++, wa.getId());
		
		LogU.add(wa.getDateTrans());
		LogU.add(wa.getMeterNo());
		LogU.add(wa.getStatus());
		LogU.add(wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		LogU.add(wa.getAccount()==null? 0 : wa.getAccount().getId());
		LogU.add(wa.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to watermeter : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return wa;
	}
	
	public void updateData(){
		String sql = "UPDATE watermeter SET "
				+ "midatetrans=?,"
				+ "meterno=?,"
				+ "mstatus=?,"
				+ "customerid=?,"
				+ "accid=?" 
				+ " WHERE mid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table watermeter");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getMeterNo());
		ps.setInt(cnt++, getStatus());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getAccount()==null? 0 : getAccount().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getMeterNo());
		LogU.add(getStatus());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getAccount()==null? 0 : getAccount().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to watermeter : " + s.getMessage());
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
		sql="SELECT mid FROM watermeter  ORDER BY mid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("mid");
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
		ps = conn.prepareStatement("SELECT mid FROM watermeter WHERE mid=?");
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
		String sql = "UPDATE watermeter set isactivem=0 WHERE mid=?";
		
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
	public String getDateTrans() {
		return dateTrans;
	}
	public String getMeterNo() {
		return meterNo;
	}
	public int getStatus() {
		return status;
	}
	public int getIsActive() {
		return isActive;
	}
	public Customer getCustomer() {
		return customer;
	}
	public WaterAccnt getAccount() {
		return account;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}
	public void setMeterNo(String meterNo) {
		this.meterNo = meterNo;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public void setAccount(WaterAccnt account) {
		this.account = account;
	}
}
