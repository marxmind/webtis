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
 * @since 9/27/2019
 * @version 1.0
 */
public class WaterOtherBill {

	private long id;
	private String dateTrans;
	private String description;
	private double amount;
	private int isActive;
	
	private WaterBill waterBill;
	private Customer customer;
	private WaterAccnt accnt;
	
	public static void main(String[] args) {
		
		WaterOtherBill o = new WaterOtherBill();
		o.setId(1);
		o.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		o.setAmount(1111);
		o.setDescription("Testing kuno");
		o.setIsActive(1);
		
		WaterBill b = new WaterBill();
		b.setId(1);
		o.setWaterBill(b);
		Customer customer = new Customer();
		customer.setId(1);
		o.setCustomer(customer);
		WaterAccnt a = new WaterAccnt();
		a.setId(1);
		o.setAccnt(a);
		o.save();
		
	}
	
	public static List<WaterOtherBill> retrieve(String sqlAdd, String[] params){
		List<WaterOtherBill> was = new ArrayList<WaterOtherBill>();
		
		String tableOt = "ot";
		String tableBl = "bl";
		String tableAcc = "acc";
		String tableCus = "cuz";
		String sql = "SELECT * FROM otherwaterbill "+ tableOt +" ,waterbill "+ tableBl  +" ,accwater "+tableAcc+", customer "+ tableCus +"  WHERE  " + 
		tableOt +".wid=" + tableBl + ".wid AND " +		
		tableOt +".customerid=" + tableCus + ".customerid AND " +
		tableOt +".accid="+ tableAcc + ".accid "; 
		
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
			
			WaterOtherBill ot = new WaterOtherBill();
			try{ot.setId(rs.getLong("owid"));}catch(NullPointerException e){}
			try{ot.setDateTrans(rs.getString("obilldate"));}catch(NullPointerException e){}
			try{ot.setDescription(rs.getString("oname"));}catch(NullPointerException e){}
			try{ot.setAmount(rs.getDouble("oamount"));}catch(NullPointerException e){}
			try{ot.setIsActive(rs.getInt("isactiveo"));}catch(NullPointerException e){}
			
			WaterBill bill = new WaterBill();
			try{bill.setId(rs.getLong("wid"));}catch(NullPointerException e){}
			try{bill.setDateTrans(rs.getString("billdate"));}catch(NullPointerException e){}
			try{bill.setBillNo(rs.getString("billno"));}catch(NullPointerException e){}
			try{bill.setAmountDue(rs.getDouble("amountdue"));}catch(NullPointerException e){}
			try{bill.setDueDate(rs.getString("duedate"));}catch(NullPointerException e){}
			try{bill.setDisconnectionDate(rs.getString("disconnectiondate"));}catch(NullPointerException e){}
			try{bill.setStatus(rs.getInt("statusbill"));}catch(NullPointerException e){}
			try{bill.setIsActive(rs.getInt("isactiveb"));}catch(NullPointerException e){}
			ot.setWaterBill(bill);
			
			WaterAccnt acc = new WaterAccnt();
			try{acc.setId(rs.getLong("accid"));}catch(NullPointerException e){}
			try{acc.setDateTrans(rs.getString("accdatetrans"));}catch(NullPointerException e){}
			try{acc.setLocation(rs.getString("location"));}catch(NullPointerException e){}
			try{acc.setStatus(rs.getInt("accstatus"));}catch(NullPointerException e){}
			try{acc.setIsActive(rs.getInt("isactiveacc"));}catch(NullPointerException e){}
			ot.setAccnt(acc);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			ot.setCustomer(cus);
			
			was.add(ot);
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return was;
	}
	
	public static WaterOtherBill save(WaterOtherBill is){
		if(is!=null){
			
			long id = WaterOtherBill.getInfo(is.getId() ==0? WaterOtherBill.getLatestId()+1 : is.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				is = WaterOtherBill.insertData(is, "1");
			}else if(id==2){
				LogU.add("update Data ");
				is = WaterOtherBill.updateData(is);
			}else if(id==3){
				LogU.add("added new Data ");
				is = WaterOtherBill.insertData(is, "3");
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
	
	public static WaterOtherBill insertData(WaterOtherBill wa, String type){
		String sql = "INSERT INTO otherwaterbill ("
				+ "owid,"
				+ "obilldate,"
				+ "oname,"
				+ "oamount,"
				+ "isactiveo,"
				+ "wid,"
				+ "customerid,"
				+ "accid)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table otherwaterbill");
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
		ps.setString(cnt++, wa.getDescription());
		ps.setDouble(cnt++, wa.getAmount());
		ps.setInt(cnt++, wa.getIsActive());
		ps.setLong(cnt++, wa.getWaterBill()==null? 0 : wa.getWaterBill().getId());
		ps.setLong(cnt++, wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		ps.setLong(cnt++, wa.getAccnt()==null? 0 : wa.getAccnt().getId());
		
		LogU.add(wa.getDateTrans());
		LogU.add(wa.getDescription());
		LogU.add(wa.getAmount());
		LogU.add(wa.getIsActive());
		LogU.add(wa.getWaterBill()==null? 0 : wa.getWaterBill().getId());
		LogU.add(wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		LogU.add(wa.getAccnt()==null? 0 : wa.getAccnt().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to otherwaterbill : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return wa;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO otherwaterbill ("
				+ "owid,"
				+ "obilldate,"
				+ "oname,"
				+ "oamount,"
				+ "isactiveo,"
				+ "wid,"
				+ "customerid,"
				+ "accid)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table otherwaterbill");
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
		ps.setString(cnt++, getDescription());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getWaterBill()==null? 0 : getWaterBill().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getAccnt()==null? 0 : getAccnt().getId());
		
		LogU.add(getDateTrans());
		LogU.add(getDescription());
		LogU.add(getAmount());
		LogU.add(getIsActive());
		LogU.add(getWaterBill()==null? 0 : getWaterBill().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getAccnt()==null? 0 : getAccnt().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to otherwaterbill : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static WaterOtherBill updateData(WaterOtherBill wa){
		String sql = "UPDATE otherwaterbill SET "
				+ "obilldate=?,"
				+ "oname=?,"
				+ "oamount=?,"
				+ "wid=?,"
				+ "customerid=?,"
				+ "accid=?" 
				+ " WHERE owid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table otherwaterbill");
		
		ps.setString(cnt++, wa.getDateTrans());
		ps.setString(cnt++, wa.getDescription());
		ps.setDouble(cnt++, wa.getAmount());
		ps.setLong(cnt++, wa.getWaterBill()==null? 0 : wa.getWaterBill().getId());
		ps.setLong(cnt++, wa.getCustomer()==null? 0 : wa.getCustomer().getId());
		ps.setLong(cnt++, wa.getAccnt()==null? 0 : wa.getAccnt().getId());
		ps.setLong(cnt++, wa.getId());
		
		LogU.add(wa.getDateTrans());
		LogU.add(wa.getDescription());
		LogU.add(wa.getAmount());
		LogU.add(wa.getWaterBill()==null? 0 : wa.getWaterBill().getId());
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
			LogU.add("error updating data to otherwaterbill : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return wa;
	}
	
	public void updateData(){
		String sql = "UPDATE otherwaterbill SET "
				+ "obilldate=?,"
				+ "oname=?,"
				+ "oamount=?,"
				+ "wid=?,"
				+ "customerid=?,"
				+ "accid=?" 
				+ " WHERE owid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table otherwaterbill");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getDescription());
		ps.setDouble(cnt++, getAmount());
		ps.setLong(cnt++, getWaterBill()==null? 0 : getWaterBill().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setLong(cnt++, getAccnt()==null? 0 : getAccnt().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getDescription());
		LogU.add(getAmount());
		LogU.add(getWaterBill()==null? 0 : getWaterBill().getId());
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
			LogU.add("error updating data to otherwaterbill : " + s.getMessage());
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
		sql="SELECT owid FROM otherwaterbill  ORDER BY owid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("owid");
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
		ps = conn.prepareStatement("SELECT owid FROM otherwaterbill WHERE owid=?");
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
		String sql = "UPDATE otherwaterbill set isactiveo=0 WHERE owid=?";
		
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
	public String getDescription() {
		return description;
	}
	public double getAmount() {
		return amount;
	}
	public int getIsActive() {
		return isActive;
	}
	public WaterBill getWaterBill() {
		return waterBill;
	}
	public Customer getCustomer() {
		return customer;
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
	public void setDescription(String description) {
		this.description = description;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public void setWaterBill(WaterBill waterBill) {
		this.waterBill = waterBill;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public void setAccnt(WaterAccnt accnt) {
		this.accnt = accnt;
	}
	
}
