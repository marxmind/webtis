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
public class Customer {
	
	private long id;
	private String registrationDate;
	private String fullName;
	private String address;
	private String contactNumber;
	private int isActive;
	private UserDtls userDtls;
	
	
	
	public static List<String> names(String name){
		List<String> str = new ArrayList<String>();
		String sql = "SELECT fullname FROM customer WHERE isactivatecus=1 AND fullname like '%"+ name.replace("--", "") +"%'";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			str.add(rs.getString("fullname"));
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return str;
	}
	
	public static boolean validateNameEntry(String name){
		String sql = "SELECT fullname FROM customer WHERE isactivatecus=1 AND fullname=?";
		String[] params = new String[1];
		params[0] = name;
		
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
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return true;
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
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
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			result.add(rs.getString(fieldName));
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return result;
	}
	
	public static List<Customer> retrieve(String sqlAdd, String[] params){
		List<Customer> cuss = new ArrayList<Customer>();//Collections.synchronizedList(new ArrayList<Customer>());
		String supTable = "cus";
		String userTable = "usr";
		String sql = "SELECT * FROM customer "+ supTable +
				                 ", userdtls "+ userTable + 
				                 
				" WHERE "
				+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND " + supTable + ".isactivatecus=1 ";
		
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
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			
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
			
			
			
			cuss.add(cus);
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cuss;
	}
	
	public static Customer retrieveName(String name){
		Customer cus = new Customer();
		String supTable = "cus";
		String userTable = "usr";
		String sql = "SELECT * FROM customer "+ supTable +
				                 ", userdtls "+ userTable + 
				                 
				" WHERE "
				+ supTable +".userdtlsid = "+ userTable +".userdtlsid AND " + supTable + ".isactivatecus=1 " + supTable + ".fullname=" + name;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			
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
		
		return cus;
	}
	
	public static Customer customer(long customerId){
		Customer cus = new Customer();
		String supTable = "cus";
		String userTable = "usr";
		
		String sql = "SELECT * FROM customer "+ supTable +
                ", userdtls "+ userTable + " WHERE " + supTable +".userdtlsid = "+ userTable +".userdtlsid AND " + supTable +".isactivatecus=1 AND " + supTable + ".customerid="+customerId;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);


		rs = ps.executeQuery();
		
		while(rs.next()){

			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			
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
		
		return cus;
		
	}
	
	public static Customer save(Customer cus){
		if(cus!=null){
			
			long id = Customer.getInfo(cus.getId() ==0? Customer.getLatestId()+1 : cus.getId());
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
	
	public static Customer insertData(Customer cus, String type){
		String sql = "INSERT INTO customer ("
				+ "customerid,"
				+ "regdate,"
				+ "fullname,"
				+ "address,"
				+ "contactno,"
				+ "isactivatecus,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customer");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			cus.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			cus.setId(id);
			LogU.add("id: " + id);
		}
		ps.setString(cnt++, cus.getRegistrationDate());
		ps.setString(cnt++, cus.getFullName());
		ps.setString(cnt++, cus.getAddress());
		ps.setString(cnt++, cus.getContactNumber());
		ps.setInt(cnt++, cus.getIsActive());
		ps.setLong(cnt++, cus.getUserDtls()==null? 0 : cus.getUserDtls().getUserdtlsid());
		
		LogU.add(cus.getRegistrationDate());
		LogU.add(cus.getFullName());
		LogU.add(cus.getAddress());
		LogU.add(cus.getContactNumber());
		LogU.add(cus.getIsActive());
		LogU.add(cus.getUserDtls()==null? 0 : cus.getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
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
				+ "regdate,"
				+ "fullname,"
				+ "address,"
				+ "contactno,"
				+ "isactivatecus,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table customer");
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
		ps.setString(cnt++, getRegistrationDate());
		ps.setString(cnt++, getFullName());
		ps.setString(cnt++, getAddress());
		ps.setString(cnt++, getContactNumber());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add(getRegistrationDate());
		LogU.add(getFullName());
		LogU.add(getAddress());
		LogU.add(getContactNumber());
		LogU.add(getIsActive());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to customer : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Customer updateData(Customer cus){
		String sql = "UPDATE customer SET "
				+ "regdate=?,"
				+ "fullname=?,"
				+ "address=?,"
				+ "contactno=?,"
				+ "userdtlsid=?" 
				+ " WHERE customerid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table customer");
		
		ps.setString(cnt++, cus.getRegistrationDate());
		ps.setString(cnt++, cus.getFullName());
		ps.setString(cnt++, cus.getAddress());
		ps.setString(cnt++, cus.getContactNumber());
		ps.setLong(cnt++, cus.getUserDtls()==null? 0 : cus.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, cus.getId());
		
		LogU.add(cus.getRegistrationDate());
		LogU.add(cus.getFullName());
		LogU.add(cus.getAddress());
		LogU.add(cus.getContactNumber());
		LogU.add(cus.getUserDtls()==null? 0 : cus.getUserDtls().getUserdtlsid());
		LogU.add(cus.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to customer : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cus;
	}
	
	public void updateData(){
		String sql = "UPDATE customer SET "
				+ "regdate=?,"
				+ "fullname=?,"
				+ "address=?,"
				+ "contactno=?,"
				+ "userdtlsid=?" 
				+ " WHERE customerid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table customer");
		
		ps.setString(cnt++, getRegistrationDate());
		ps.setString(cnt++, getFullName());
		ps.setString(cnt++, getAddress());
		ps.setString(cnt++, getContactNumber());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getRegistrationDate());
		LogU.add(getFullName());
		LogU.add(getAddress());
		LogU.add(getContactNumber());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
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
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("customerid");
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
		ps = conn.prepareStatement("SELECT customerid FROM customer WHERE customerid=?");
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
		String sql = "UPDATE customer set isactivatecus=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE customerid=?";
		
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
	public String getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	
}
