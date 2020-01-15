package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @since 04/23/2019
 * @version 1.0
 *
 *
 */
public class ORNameList {

	private long id;
	private double amount;
	private int isActive;
	
	private ORListing orList;
	private Customer customer;
	private PaymentName paymentName;
	
	public static List<ORNameList> retrieve(String sqlAdd, String[] params){
		List<ORNameList> orns = new ArrayList<ORNameList>();
		
		String tableNameList = "nameL";
		String tableOr = "orl";
		String tableCus = "cuz";
		String tableName = "pay";
		String sql = "SELECT * FROM ornamelist "+ tableNameList +", orlisting "+tableOr+", customer "+ tableCus +",paymentname "+ tableName +"  WHERE  "+tableOr+".isactiveor=1 AND " + 
				tableNameList +".customerid=" + tableCus + ".customerid AND " + 
				tableNameList +".orid=" + tableOr + ".orid AND " +
				tableNameList +".pyid=" + tableName + ".pyid"; 
		
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
			
			ORNameList orn = new ORNameList();
			try{orn.setId(rs.getLong("olid"));}catch(NullPointerException e){}
			try{orn.setAmount(rs.getDouble("olamount"));}catch(NullPointerException e){}
			try{orn.setIsActive(rs.getInt("isactiveol"));}catch(NullPointerException e){}
			
			ORListing or = new ORListing();
			try{or.setId(rs.getLong("orid"));}catch(NullPointerException e){}
			try{or.setDateTrans(rs.getString("ordatetrans"));}catch(NullPointerException e){}
			try{or.setOrNumber(rs.getString("ornumber"));}catch(NullPointerException e){}
			try{or.setFormType(rs.getInt("aform"));}catch(NullPointerException e){}
			try{or.setIsActive(rs.getInt("isactiveor"));}catch(NullPointerException e){}
			orn.setOrList(or);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			cus.setUserDtls(user);
			
			orn.setCustomer(cus);
			
			PaymentName name = new PaymentName();
			try{name.setId(rs.getLong("pyid"));}catch(NullPointerException e){}
			try{name.setDateTrans(rs.getString("pydatetrans"));}catch(NullPointerException e){}
			try{name.setAccountingCode(rs.getString("pyaccntcode"));}catch(NullPointerException e){}
			try{name.setName(rs.getString("pyname"));}catch(NullPointerException e){}
			try{name.setAmount(rs.getDouble("pyamount"));}catch(NullPointerException e){}
			try{name.setIsActive(rs.getInt("isactivepy"));}catch(NullPointerException e){}
			orn.setPaymentName(name);
			
			orns.add(orn);
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return orns;
	}
	
	public static ORNameList save(ORNameList is){
		if(is!=null){
			
			long id = ORNameList.getInfo(is.getId() ==0? ORNameList.getLatestId()+1 : is.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				is = ORNameList.insertData(is, "1");
			}else if(id==2){
				LogU.add("update Data ");
				is = ORNameList.updateData(is);
			}else if(id==3){
				LogU.add("added new Data ");
				is = ORNameList.insertData(is, "3");
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
	
	public static ORNameList insertData(ORNameList name, String type){
		String sql = "INSERT INTO ornamelist ("
				+ "olid,"
				+ "orid,"
				+ "pyid,"
				+ "customerid,"
				+ "olamount,"
				+ "isactiveol)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table ornamelist");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			name.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			name.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setLong(cnt++, name.getOrList().getId());
		ps.setLong(cnt++, name.getPaymentName().getId());
		ps.setLong(cnt++, name.getCustomer().getId());
		ps.setDouble(cnt++, name.getAmount());
		ps.setInt(cnt++, name.getIsActive());
		
		LogU.add(name.getOrList().getId());
		LogU.add(name.getPaymentName().getId());
		LogU.add(name.getCustomer().getId());
		LogU.add(name.getAmount());
		LogU.add(name.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to ornamelist : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return name;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO ornamelist ("
				+ "olid,"
				+ "orid,"
				+ "pyid,"
				+ "customerid,"
				+ "olamount,"
				+ "isactiveol)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table ornamelist");
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
		
		ps.setLong(cnt++, getOrList().getId());
		ps.setLong(cnt++, getPaymentName().getId());
		ps.setLong(cnt++, getCustomer().getId());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getIsActive());
		
		LogU.add(getOrList().getId());
		LogU.add(getPaymentName().getId());
		LogU.add(getCustomer().getId());
		LogU.add(getAmount());
		LogU.add(getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to ornamelist : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static ORNameList updateData(ORNameList name){
		String sql = "UPDATE ornamelist SET "
				+ "orid=?,"
				+ "pyid=?,"
				+ "customerid=?,"
				+ "olamount=?" 
				+ " WHERE olid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table ornamelist");
		
		ps.setLong(cnt++, name.getOrList().getId());
		ps.setLong(cnt++, name.getPaymentName().getId());
		ps.setLong(cnt++, name.getCustomer().getId());
		ps.setDouble(cnt++, name.getAmount());
		ps.setLong(cnt++, name.getId());
		
		LogU.add(name.getOrList().getId());
		LogU.add(name.getPaymentName().getId());
		LogU.add(name.getCustomer().getId());
		LogU.add(name.getAmount());
		LogU.add(name.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to ornamelist : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return name;
	}
	
	public void updateData(){
		String sql = "UPDATE ornamelist SET "
				+ "orid=?,"
				+ "pyid=?,"
				+ "customerid=?,"
				+ "olamount=?" 
				+ " WHERE olid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table ornamelist");
		
		ps.setLong(cnt++, getOrList().getId());
		ps.setLong(cnt++, getPaymentName().getId());
		ps.setLong(cnt++, getCustomer().getId());
		ps.setDouble(cnt++, getAmount());
		ps.setLong(cnt++, getId());
		
		LogU.add(getOrList().getId());
		LogU.add(getPaymentName().getId());
		LogU.add(getCustomer().getId());
		LogU.add(getAmount());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to ornamelist : " + s.getMessage());
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
		sql="SELECT olid FROM ornamelist  ORDER BY olid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("olid");
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
		ps = conn.prepareStatement("SELECT olid FROM ornamelist WHERE olid=?");
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
		String sql = "UPDATE ornamelist set isactiveol=0 WHERE olid=?";
		
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
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public ORListing getOrList() {
		return orList;
	}
	public void setOrList(ORListing orList) {
		this.orList = orList;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public PaymentName getPaymentName() {
		return paymentName;
	}

	public void setPaymentName(PaymentName paymentName) {
		this.paymentName = paymentName;
	}
}
