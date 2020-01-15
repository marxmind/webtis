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
public class WaterReadingMeter {
	
	private long id;
	private String dateTrans;
	private int month;
	private int year;
	private int isActive;
	private int readingNo;
	
	private WaterMeter waterMeter;
	private WaterAccnt accnt;
	private Customer customer;
	
	public static void main(String[] args) {
		
		WaterReadingMeter rd = new WaterReadingMeter();
		rd.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		rd.setIsActive(1);
		rd.setMonth(1);
		rd.setYear(2019);
		
		
		WaterMeter m = new WaterMeter();
		m.setId(1);
		rd.setWaterMeter(m);
		Customer customer = new Customer();
		customer.setId(1);
		rd.setCustomer(customer);
		WaterAccnt a = new WaterAccnt();
		a.setId(1);
		rd.setAccnt(a);
		rd.save();
		
	}
	
	public static int prevReading(Customer customer) {
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		String sql = " AND rd.isactiver=1 AND cuz.customerid=" + customer.getId() +" ORDER BY rd.rid DESC LIMTI 1";
		String[] params = new String[0];
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
			return rs.getInt("readingNo");
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		
		
		
		}catch(Exception e){e.getMessage();}
		
		return 0;
	}
	
	public static List<WaterReadingMeter> retrieve(String sqlAdd, String[] params){
		List<WaterReadingMeter> was = new ArrayList<WaterReadingMeter>();
		
		String tableRd = "rd";
		String tableMt = "mt";
		String tableAcc = "acc";
		String tableCus = "cuz";
		String sql = "SELECT * FROM readingmeter "+ tableRd +" ,watermeter "+ tableMt +" ,accwater "+tableAcc+", customer "+ tableCus +"  WHERE  " + 
				tableRd +".mid=" + tableMt + ".mid AND " +
				tableRd +".customerid=" + tableCus + ".customerid AND " +
				tableRd +".accid="+ tableAcc + ".accid "; 
		
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
			
			WaterReadingMeter rd = new WaterReadingMeter();
			try{rd.setId(rs.getLong("rid"));}catch(NullPointerException e){}
			try{rd.setDateTrans(rs.getString("readingdate"));}catch(NullPointerException e){}
			try{rd.setMonth(rs.getInt("rmonth"));}catch(NullPointerException e){}
			try{rd.setYear(rs.getInt("ryear"));}catch(NullPointerException e){}
			try{rd.setIsActive(rs.getInt("isactiver"));}catch(NullPointerException e){}
			try{rd.setReadingNo(rs.getInt("readingNo"));}catch(NullPointerException e){}
			
			WaterMeter meter = new WaterMeter();
			try{meter.setId(rs.getLong("mid"));}catch(NullPointerException e){}
			try{meter.setDateTrans(rs.getString("midatetrans"));}catch(NullPointerException e){}
			try{meter.setMeterNo(rs.getString("meterno"));}catch(NullPointerException e){}
			try{meter.setStatus(rs.getInt("mstatus"));}catch(NullPointerException e){}
			try{meter.setIsActive(rs.getInt("isactivem"));}catch(NullPointerException e){}
			rd.setWaterMeter(meter);
			
			WaterAccnt acc = new WaterAccnt();
			try{acc.setId(rs.getLong("accid"));}catch(NullPointerException e){}
			try{acc.setDateTrans(rs.getString("accdatetrans"));}catch(NullPointerException e){}
			try{acc.setLocation(rs.getString("location"));}catch(NullPointerException e){}
			try{acc.setStatus(rs.getInt("accstatus"));}catch(NullPointerException e){}
			try{acc.setIsActive(rs.getInt("isactiveacc"));}catch(NullPointerException e){}
			rd.setAccnt(acc);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			rd.setCustomer(cus);
			
			was.add(rd);
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return was;
	}
	
	public static WaterReadingMeter save(WaterReadingMeter is){
		if(is!=null){
			
			long id = WaterReadingMeter.getInfo(is.getId() ==0? WaterReadingMeter.getLatestId()+1 : is.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				is = WaterReadingMeter.insertData(is, "1");
			}else if(id==2){
				LogU.add("update Data ");
				is = WaterReadingMeter.updateData(is);
			}else if(id==3){
				LogU.add("added new Data ");
				is = WaterReadingMeter.insertData(is, "3");
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
	
	public static WaterReadingMeter insertData(WaterReadingMeter wa, String type){
		String sql = "INSERT INTO readingmeter ("
				+ "rid,"
				+ "readingdate,"
				+ "rmonth,"
				+ "ryear,"
				+ "isactiver,"
				+ "mid,"
				+ "customerid,"
				+ "accid,"
				+ "readingNo)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table readingmeter");
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
		ps.setInt(cnt++, wa.getMonth());
		ps.setInt(cnt++, wa.getYear());
		ps.setInt(cnt++, wa.getIsActive());
		ps.setLong(cnt++, wa.getWaterMeter()==null? 0 : wa.getWaterMeter().getId());
		ps.setLong(cnt++, wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		ps.setLong(cnt++, wa.getAccnt()==null? 0 : wa.getAccnt().getId());
		ps.setInt(cnt++, wa.getReadingNo());
		
		LogU.add(wa.getDateTrans());
		LogU.add(wa.getMonth());
		LogU.add(wa.getYear());
		LogU.add(wa.getIsActive());
		LogU.add(wa.getWaterMeter()==null? 0 : wa.getWaterMeter().getId());
		LogU.add(wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		LogU.add(wa.getAccnt()==null? 0 : wa.getAccnt().getId());
		LogU.add(wa.getReadingNo());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to readingmeter : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return wa;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO readingmeter ("
				+ "rid,"
				+ "readingdate,"
				+ "rmonth,"
				+ "ryear,"
				+ "isactiver,"
				+ "mid,"
				+ "customerid,"
				+ "accid,"
				+ "readingNo)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table readingmeter");
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
		ps.setInt(cnt++, getMonth());
		ps.setInt(cnt++, getYear());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getWaterMeter()==null? 0 : getWaterMeter().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getAccnt()==null? 0 : getAccnt().getId());
		ps.setInt(cnt++, getReadingNo());
		
		LogU.add(getDateTrans());
		LogU.add(getMonth());
		LogU.add(getYear());
		LogU.add(getIsActive());
		LogU.add(getWaterMeter()==null? 0 : getWaterMeter().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getAccnt()==null? 0 : getAccnt().getId());
		LogU.add(getReadingNo());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to readingmeter : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static WaterReadingMeter updateData(WaterReadingMeter wa){
		String sql = "UPDATE readingmeter SET "
				+ "readingdate=?,"
				+ "rmonth=?,"
				+ "ryear=?,"
				+ "mid=?,"
				+ "customerid=?,"
				+ "accid=?,"
				+ "readingNo=?" 
				+ " WHERE rid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table readingmeter");
		
		
		ps.setString(cnt++, wa.getDateTrans());
		ps.setInt(cnt++, wa.getMonth());
		ps.setInt(cnt++, wa.getYear());
		ps.setLong(cnt++, wa.getWaterMeter()==null? 0 : wa.getWaterMeter().getId());
		ps.setLong(cnt++, wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		ps.setLong(cnt++, wa.getAccnt()==null? 0 : wa.getAccnt().getId());
		ps.setInt(cnt++, wa.getReadingNo());
		ps.setLong(cnt++, wa.getId());
		
		LogU.add(wa.getDateTrans());
		LogU.add(wa.getMonth());
		LogU.add(wa.getYear());
		LogU.add(wa.getWaterMeter()==null? 0 : wa.getWaterMeter().getId());
		LogU.add(wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		LogU.add(wa.getAccnt()==null? 0 : wa.getAccnt().getId());
		LogU.add(wa.getReadingNo());
		LogU.add(wa.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to readingmeter : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return wa;
	}
	
	public void updateData(){
		String sql = "UPDATE readingmeter SET "
				+ "readingdate=?,"
				+ "rmonth=?,"
				+ "ryear=?,"
				+ "mid=?,"
				+ "customerid=?,"
				+ "accid=?,"
				+ "readingNo=?" 
				+ " WHERE rid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table readingmeter");
		
		
		ps.setString(cnt++, getDateTrans());
		ps.setInt(cnt++, getMonth());
		ps.setInt(cnt++, getYear());
		ps.setLong(cnt++, getWaterMeter()==null? 0 : getWaterMeter().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getAccnt()==null? 0 : getAccnt().getId());
		ps.setInt(cnt++, getReadingNo());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getMonth());
		LogU.add(getYear());
		LogU.add(getWaterMeter()==null? 0 : getWaterMeter().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getAccnt()==null? 0 : getAccnt().getId());
		LogU.add(getReadingNo());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to readingmeter : " + s.getMessage());
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
		sql="SELECT rid FROM readingmeter  ORDER BY rid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("rid");
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
		ps = conn.prepareStatement("SELECT rid FROM readingmeter WHERE rid=?");
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
		String sql = "UPDATE readingmeter set isactiver=0 WHERE rid=?";
		
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
	public int getMonth() {
		return month;
	}
	public int getYear() {
		return year;
	}
	public int getIsActive() {
		return isActive;
	}
	public WaterMeter getWaterMeter() {
		return waterMeter;
	}
	public WaterAccnt getAccnt() {
		return accnt;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public void setWaterMeter(WaterMeter waterMeter) {
		this.waterMeter = waterMeter;
	}
	public void setAccnt(WaterAccnt accnt) {
		this.accnt = accnt;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public int getReadingNo() {
		return readingNo;
	}

	public void setReadingNo(int readingNo) {
		this.readingNo = readingNo;
	}
	
}
