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
public class WaterBill {

	private long id;
	private String dateTrans;
	private String billNo;
	private double amountDue;
	private String dueDate;
	private String disconnectionDate;
	private int status;
	private int isActive;
	
	private WaterMeter waterMeter;
	private Customer customer;
	private WaterReadingMeter readingMeter;
	private WaterAccnt accnt;
	
	public static void main(String[] args) {
		
		WaterBill b = new WaterBill();
		b.setId(1);
		b.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		b.setBillNo("0000");
		b.setAmountDue(1200);
		b.setDueDate(DateUtils.getCurrentDateYYYYMMDD());
		b.setDisconnectionDate(DateUtils.getCurrentDateYYYYMMDD());
		b.setStatus(1);
		b.setIsActive(1);
		
		WaterReadingMeter rd = new WaterReadingMeter();
		rd.setId(1);
		b.setReadingMeter(rd);
		WaterMeter m = new WaterMeter();
		m.setId(1);
		b.setWaterMeter(m);
		Customer customer = new Customer();
		customer.setId(1);
		b.setCustomer(customer);
		WaterAccnt a = new WaterAccnt();
		a.setId(1);
		b.setAccnt(a);
		
		b.save();
		
	}
	
	public static String waterBillNo(Customer customer) {
		String billNo="1";
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		String sql = " AND bl.isactiveb=1 AND cuz.customerid=" + customer.getId();
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
			billNo = rs.getString("billno");
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		
		if(!billNo.equalsIgnoreCase("1")) {
			int num = Integer.valueOf(billNo);
			num +=1;
			billNo = num+"";
		}
		
		}catch(Exception e){e.getMessage();}
		
		return billNo;
	}
	
	public static double collectAllUnpaidBill(Customer cus) {
		double amount=0d;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		String sql = " AND bl.isactiveb=1 AND cuz.customerid=" + cus.getId() + " AND bl.statusbill=1"; //Unpaid
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
			amount += rs.getDouble("amountdue");
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		
		}catch(Exception e){e.getMessage();}
		
		return amount;
	}
	
	public static List<WaterBill> retrieve(String sqlAdd, String[] params){
		List<WaterBill> was = new ArrayList<WaterBill>();
		
		String tableBl = "bl";
		String tableRd = "rd";
		String tableMt = "mt";
		String tableAcc = "acc";
		String tableCus = "cuz";
		String sql = "SELECT * FROM waterbill "+ tableBl +" ,readingmeter "+ tableRd +" ,watermeter "+ tableMt +" ,accwater "+tableAcc+", customer "+ tableCus +"  WHERE  " + 
				tableBl +".rid=" + tableRd + ".rid  AND " +
				tableBl +".mid=" + tableMt + ".mid AND " +
				tableBl +".customerid=" + tableCus + ".customerid AND " +
				tableBl +".accid="+ tableAcc + ".accid "; 
		
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
			
			WaterBill bill = new WaterBill();
			try{bill.setId(rs.getLong("wid"));}catch(NullPointerException e){}
			try{bill.setDateTrans(rs.getString("billdate"));}catch(NullPointerException e){}
			try{bill.setBillNo(rs.getString("billno"));}catch(NullPointerException e){}
			try{bill.setAmountDue(rs.getDouble("amountdue"));}catch(NullPointerException e){}
			try{bill.setDueDate(rs.getString("duedate"));}catch(NullPointerException e){}
			try{bill.setDisconnectionDate(rs.getString("disconnectiondate"));}catch(NullPointerException e){}
			try{bill.setStatus(rs.getInt("statusbill"));}catch(NullPointerException e){}
			try{bill.setIsActive(rs.getInt("isactiveb"));}catch(NullPointerException e){}
			
			WaterReadingMeter rd = new WaterReadingMeter();
			try{rd.setId(rs.getLong("rid"));}catch(NullPointerException e){}
			try{rd.setDateTrans(rs.getString("readingdate"));}catch(NullPointerException e){}
			try{rd.setMonth(rs.getInt("rmonth"));}catch(NullPointerException e){}
			try{rd.setYear(rs.getInt("ryear"));}catch(NullPointerException e){}
			try{rd.setIsActive(rs.getInt("isactiver"));}catch(NullPointerException e){}
			try{rd.setReadingNo(rs.getInt("readingNo"));}catch(NullPointerException e){}
			bill.setReadingMeter(rd);
			
			WaterMeter meter = new WaterMeter();
			try{meter.setId(rs.getLong("mid"));}catch(NullPointerException e){}
			try{meter.setDateTrans(rs.getString("midatetrans"));}catch(NullPointerException e){}
			try{meter.setMeterNo(rs.getString("meterno"));}catch(NullPointerException e){}
			try{meter.setStatus(rs.getInt("mstatus"));}catch(NullPointerException e){}
			try{meter.setIsActive(rs.getInt("isactivem"));}catch(NullPointerException e){}
			bill.setWaterMeter(meter);
			
			WaterAccnt acc = new WaterAccnt();
			try{acc.setId(rs.getLong("accid"));}catch(NullPointerException e){}
			try{acc.setDateTrans(rs.getString("accdatetrans"));}catch(NullPointerException e){}
			try{acc.setLocation(rs.getString("location"));}catch(NullPointerException e){}
			try{acc.setStatus(rs.getInt("accstatus"));}catch(NullPointerException e){}
			try{acc.setIsActive(rs.getInt("isactiveacc"));}catch(NullPointerException e){}
			bill.setAccnt(acc);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			bill.setCustomer(cus);
			
			was.add(bill);
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return was;
	}
	
	public static WaterBill save(WaterBill is){
		if(is!=null){
			
			long id = WaterBill.getInfo(is.getId() ==0? WaterBill.getLatestId()+1 : is.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				is = WaterBill.insertData(is, "1");
			}else if(id==2){
				LogU.add("update Data ");
				is = WaterBill.updateData(is);
			}else if(id==3){
				LogU.add("added new Data ");
				is = WaterBill.insertData(is, "3");
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
	
	public static WaterBill insertData(WaterBill wa, String type){
		String sql = "INSERT INTO waterbill ("
				+ "wid,"
				+ "billdate,"
				+ "billno,"
				+ "amountdue,"
				+ "duedate,"
				+ "disconnectiondate,"
				+ "statusbill,"
				+ "isactiveb,"
				+ "rid,"
				+ "mid,"
				+ "customerid,"
				+ "accid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table waterbill");
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
		ps.setString(cnt++, wa.getBillNo());
		ps.setDouble(cnt++, wa.getAmountDue());
		ps.setString(cnt++, wa.getDueDate());
		ps.setString(cnt++, wa.getDisconnectionDate());
		ps.setInt(cnt++, wa.getStatus());
		ps.setInt(cnt++, wa.getIsActive());
		ps.setLong(cnt++, wa.getReadingMeter()==null? 0 : wa.getReadingMeter().getId());
		ps.setLong(cnt++, wa.getWaterMeter()==null? 0 : wa.getWaterMeter().getId());
		ps.setLong(cnt++, wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		ps.setLong(cnt++, wa.getAccnt()==null? 0 : wa.getAccnt().getId());
		
		LogU.add(wa.getDateTrans());
		LogU.add(wa.getBillNo());
		LogU.add(wa.getAmountDue());
		LogU.add(wa.getDueDate());
		LogU.add(wa.getDisconnectionDate());
		LogU.add(wa.getStatus());
		LogU.add(wa.getIsActive());
		LogU.add(wa.getReadingMeter()==null? 0 : wa.getReadingMeter().getId());
		LogU.add(wa.getWaterMeter()==null? 0 : wa.getWaterMeter().getId());
		LogU.add(wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		LogU.add(wa.getAccnt()==null? 0 : wa.getAccnt().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to waterbill : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return wa;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO waterbill ("
				+ "wid,"
				+ "billdate,"
				+ "billno,"
				+ "amountdue,"
				+ "duedate,"
				+ "disconnectiondate,"
				+ "statusbill,"
				+ "isactiveb,"
				+ "rid,"
				+ "mid,"
				+ "customerid,"
				+ "accid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table waterbill");
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
		ps.setString(cnt++, getBillNo());
		ps.setDouble(cnt++, getAmountDue());
		ps.setString(cnt++, getDueDate());
		ps.setString(cnt++, getDisconnectionDate());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getReadingMeter()==null? 0 : getReadingMeter().getId());
		ps.setLong(cnt++, getWaterMeter()==null? 0 : getWaterMeter().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getAccnt()==null? 0 : getAccnt().getId());
		
		LogU.add(getDateTrans());
		LogU.add(getBillNo());
		LogU.add(getAmountDue());
		LogU.add(getDueDate());
		LogU.add(getDisconnectionDate());
		LogU.add(getStatus());
		LogU.add(getIsActive());
		LogU.add(getReadingMeter()==null? 0 : getReadingMeter().getId());
		LogU.add(getWaterMeter()==null? 0 : getWaterMeter().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getAccnt()==null? 0 : getAccnt().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to waterbill : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static WaterBill updateData(WaterBill wa){
		String sql = "UPDATE waterbill SET "
				+ "billdate=?,"
				+ "billno=?,"
				+ "amountdue=?,"
				+ "duedate=?,"
				+ "disconnectiondate=?,"
				+ "statusbill=?,"
				+ "rid=?,"
				+ "mid=?,"
				+ "customerid=?,"
				+ "accid=?" 
				+ " WHERE wid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table waterbill");
		
		ps.setString(cnt++, wa.getDateTrans());
		ps.setString(cnt++, wa.getBillNo());
		ps.setDouble(cnt++, wa.getAmountDue());
		ps.setString(cnt++, wa.getDueDate());
		ps.setString(cnt++, wa.getDisconnectionDate());
		ps.setInt(cnt++, wa.getStatus());
		ps.setLong(cnt++, wa.getReadingMeter()==null? 0 : wa.getReadingMeter().getId());
		ps.setLong(cnt++, wa.getWaterMeter()==null? 0 : wa.getWaterMeter().getId());
		ps.setLong(cnt++, wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		ps.setLong(cnt++, wa.getAccnt()==null? 0 : wa.getAccnt().getId());
		ps.setLong(cnt++, wa.getId());
		
		LogU.add(wa.getDateTrans());
		LogU.add(wa.getBillNo());
		LogU.add(wa.getAmountDue());
		LogU.add(wa.getDueDate());
		LogU.add(wa.getDisconnectionDate());
		LogU.add(wa.getStatus());
		LogU.add(wa.getReadingMeter()==null? 0 : wa.getReadingMeter().getId());
		LogU.add(wa.getWaterMeter()==null? 0 : wa.getWaterMeter().getId());
		LogU.add(wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		LogU.add(wa.getAccnt()==null? 0 : wa.getAccnt().getId());
		LogU.add(wa.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to waterbill : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return wa;
	}
	
	public void updateData(){
		String sql = "UPDATE waterbill SET "
				+ "billdate=?,"
				+ "billno=?,"
				+ "amountdue=?,"
				+ "duedate=?,"
				+ "disconnectiondate=?,"
				+ "statusbill=?,"
				+ "rid=?,"
				+ "mid=?,"
				+ "customerid=?,"
				+ "accid=?" 
				+ " WHERE wid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table waterbill");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getBillNo());
		ps.setDouble(cnt++, getAmountDue());
		ps.setString(cnt++, getDueDate());
		ps.setString(cnt++, getDisconnectionDate());
		ps.setInt(cnt++, getStatus());
		ps.setLong(cnt++, getReadingMeter()==null? 0 : getReadingMeter().getId());
		ps.setLong(cnt++, getWaterMeter()==null? 0 : getWaterMeter().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getAccnt()==null? 0 : getAccnt().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getBillNo());
		LogU.add(getAmountDue());
		LogU.add(getDueDate());
		LogU.add(getDisconnectionDate());
		LogU.add(getStatus());
		LogU.add(getReadingMeter()==null? 0 : getReadingMeter().getId());
		LogU.add(getWaterMeter()==null? 0 : getWaterMeter().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getAccnt()==null? 0 : getAccnt().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to waterbill : " + s.getMessage());
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
		sql="SELECT wid FROM waterbill  ORDER BY wid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("wid");
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
		ps = conn.prepareStatement("SELECT wid FROM waterbill WHERE wid=?");
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
		String sql = "UPDATE waterbill set isactiveb=0 WHERE wid=?";
		
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
	public String getBillNo() {
		return billNo;
	}
	public double getAmountDue() {
		return amountDue;
	}
	public String getDueDate() {
		return dueDate;
	}
	public String getDisconnectionDate() {
		return disconnectionDate;
	}
	public int getStatus() {
		return status;
	}
	public int getIsActive() {
		return isActive;
	}
	public WaterMeter getWaterMeter() {
		return waterMeter;
	}
	public Customer getCustomer() {
		return customer;
	}
	public WaterReadingMeter getReadingMeter() {
		return readingMeter;
	}
	public WaterAccnt getAccnt() {
		return accnt;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public void setAmountDue(double amountDue) {
		this.amountDue = amountDue;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public void setDisconnectionDate(String disconnectionDate) {
		this.disconnectionDate = disconnectionDate;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public void setWaterMeter(WaterMeter waterMeter) {
		this.waterMeter = waterMeter;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public void setReadingMeter(WaterReadingMeter readingMeter) {
		this.readingMeter = readingMeter;
	}
	public void setAccnt(WaterAccnt accnt) {
		this.accnt = accnt;
	}
	
}
