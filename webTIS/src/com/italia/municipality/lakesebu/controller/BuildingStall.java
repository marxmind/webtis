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
 * @since 06/16/2018
 * @version 1.0
 */
public class BuildingStall {

	private int id;
	private String name;
	private int type;
	private double monthlyRental;
	private int isOccupied;
	private int isActive;
	
	private MarketBuilding building;
	private Customer customer;
	
	public static List<BuildingStall> retrieve(String sqlAdd, String[] params){
		List<BuildingStall> mks = Collections.synchronizedList(new ArrayList<BuildingStall>());
		String stallTable = "st";
		String bldgTable = "bl";
		String cusTable = "cuz";
		String sql = "SELECT * FROM stall "+ stallTable + ", marketbuilding " + bldgTable
				+", customer " + cusTable +
				" WHERE "
				+ stallTable + ".customerid=" + cusTable + ".customerid AND "
				+ stallTable + ".bldgid=" + bldgTable + ".bldgid AND "
				+ stallTable +".isactivatestall = 1 ";
		
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
		//System.out.println("STALL SQL "+ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			BuildingStall  own = new BuildingStall();
			try{own.setId(rs.getInt("stallid"));}catch(NullPointerException e) {}
			try{own.setName(rs.getString("stallname"));}catch(NullPointerException e) {}
			try{own.setType(rs.getInt("stalltype"));}catch(NullPointerException e) {}
			try{own.setMonthlyRental(rs.getDouble("stallmonthlyrental"));}catch(NullPointerException e) {}
			try{own.setIsOccupied(rs.getInt("isoccupied"));}catch(NullPointerException e) {}
			try{own.setIsActive(rs.getInt("isactivatestall"));}catch(NullPointerException e) {}
			
			MarketBuilding mk = new MarketBuilding();
			try{mk.setId(rs.getInt("bldgid"));}catch(NullPointerException e) {}
			try{mk.setName(rs.getString("bldgname"));}catch(NullPointerException e) {}
			try{mk.setStallNumber(rs.getString("stallno"));}catch(NullPointerException e) {}
			try{mk.setType(rs.getInt("bldgtype"));}catch(NullPointerException e) {}
			try{mk.setMonthlyRental(rs.getDouble("monthylyrental"));}catch(NullPointerException e) {}
			try{mk.setIsActive(rs.getInt("isactivatebldg"));}catch(NullPointerException e) {}
			own.setBuilding(mk);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			own.setCustomer(cus);
			
			
			
			mks.add(own);
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return mks;
	}
	
	public static BuildingStall retrieve(int id){
		BuildingStall  own = new BuildingStall();
		String stallTable = "st";
		String bldgTable = "bl";
		String cusTable = "cuz";
		String sql = "SELECT * FROM stall "+ stallTable + ", marketbuilding " + bldgTable
				+", customer " + cusTable +
				" WHERE "
				+ stallTable + ".customerid=" + cusTable + ".customerid AND "
				+ stallTable + ".bldgid=" + bldgTable + ".bldgid AND "
				+ stallTable +".isactivatestall = 1 AND " + stallTable + ".stallid=" + id;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{own.setId(rs.getInt("stallid"));}catch(NullPointerException e) {}
			try{own.setName(rs.getString("stallname"));}catch(NullPointerException e) {}
			try{own.setType(rs.getInt("stalltype"));}catch(NullPointerException e) {}
			try{own.setMonthlyRental(rs.getDouble("stallmonthlyrental"));}catch(NullPointerException e) {}
			try{own.setIsOccupied(rs.getInt("isoccupied"));}catch(NullPointerException e) {}
			try{own.setIsActive(rs.getInt("isactivatestall"));}catch(NullPointerException e) {}
			
			MarketBuilding mk = new MarketBuilding();
			try{mk.setId(rs.getInt("bldgid"));}catch(NullPointerException e) {}
			try{mk.setName(rs.getString("bldgname"));}catch(NullPointerException e) {}
			try{mk.setStallNumber(rs.getString("stallno"));}catch(NullPointerException e) {}
			try{mk.setType(rs.getInt("bldgtype"));}catch(NullPointerException e) {}
			try{mk.setMonthlyRental(rs.getDouble("monthylyrental"));}catch(NullPointerException e) {}
			try{mk.setIsActive(rs.getInt("isactivatebldg"));}catch(NullPointerException e) {}
			own.setBuilding(mk);
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setRegistrationDate(rs.getString("regdate"));}catch(NullPointerException e){}
			try{cus.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{cus.setContactNumber(rs.getString("contactno"));}catch(NullPointerException e){}
			try{cus.setIsActive(rs.getInt("isactivatecus"));}catch(NullPointerException e){}
			own.setCustomer(cus);
			
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return own;
	}
	
	public static BuildingStall save(BuildingStall mk){
		if(mk!=null){
			
			int id = BuildingStall.getInfo(mk.getId() ==0? BuildingStall.getLatestId()+1 : mk.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				mk = BuildingStall.insertData(mk, "1");
			}else if(id==2){
				LogU.add("update Data ");
				mk = BuildingStall.updateData(mk);
			}else if(id==3){
				LogU.add("added new Data ");
				mk = BuildingStall.insertData(mk, "3");
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
	
	public static BuildingStall insertData(BuildingStall mk, String type){
		String sql = "INSERT INTO stall ("
				+ "stallid,"
				+ "stallname,"
				+ "stalltype,"
				+ "stallmonthlyrental,"
				+ "isoccupied,"
				+ "isactivatestall,"
				+ "bldgid,"
				+ "customerid)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table stall");
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
		ps.setString(cnt++, mk.getName());
		ps.setInt(cnt++, mk.getType());
		ps.setDouble(cnt++, mk.getMonthlyRental());
		ps.setInt(cnt++, mk.getIsOccupied());
		ps.setInt(cnt++, mk.getIsActive());
		ps.setInt(cnt++, mk.getBuilding()==null? 0 : mk.getBuilding().getId());
		ps.setLong(cnt++, mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		
		LogU.add(mk.getName());
		LogU.add(mk.getType());
		LogU.add(mk.getMonthlyRental());
		LogU.add(mk.getIsOccupied());
		LogU.add(mk.getIsActive());
		LogU.add(mk.getBuilding()==null? 0 : mk.getBuilding().getId());
		LogU.add(mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to stall : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mk;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO stall ("
				+ "stallid,"
				+ "stallname,"
				+ "stalltype,"
				+ "stallmonthlyrental,"
				+ "isoccupied,"
				+ "isactivatestall,"
				+ "bldgid,"
				+ "customerid)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table stall");
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
		ps.setString(cnt++, getName());
		ps.setInt(cnt++, getType());
		ps.setDouble(cnt++, getMonthlyRental());
		ps.setInt(cnt++, getIsOccupied());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getBuilding()==null? 0 : getBuilding().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		
		LogU.add(getName());
		LogU.add(getType());
		LogU.add(getMonthlyRental());
		LogU.add(getIsOccupied());
		LogU.add(getIsActive());
		LogU.add(getBuilding()==null? 0 : getBuilding().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to stall : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static BuildingStall updateData(BuildingStall mk){
		String sql = "UPDATE stall SET "
				+ "stallname=?,"
				+ "stalltype=?,"
				+ "stallmonthlyrental=?,"
				+ "isoccupied=?,"
				+ "bldgid=?,"
				+ "customerid=?" 
				+ " WHERE stallid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table stall");
		
		ps.setString(cnt++, mk.getName());
		ps.setInt(cnt++, mk.getType());
		ps.setDouble(cnt++, mk.getMonthlyRental());
		ps.setInt(cnt++, mk.getIsOccupied());
		ps.setInt(cnt++, mk.getBuilding()==null? 0 : mk.getBuilding().getId());
		ps.setLong(cnt++, mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		ps.setInt(cnt++, mk.getId());
		
		LogU.add(mk.getName());
		LogU.add(mk.getType());
		LogU.add(mk.getMonthlyRental());
		LogU.add(mk.getIsOccupied());
		LogU.add(mk.getBuilding()==null? 0 : mk.getBuilding().getId());
		LogU.add(mk.getCustomer()==null? 0 : mk.getCustomer().getId());
		LogU.add(mk.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to stall : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mk;
	}
	
	public void updateData(){
		String sql = "UPDATE stall SET "
				+ "stallname=?,"
				+ "stalltype=?,"
				+ "stallmonthlyrental=?,"
				+ "isoccupied=?,"
				+ "bldgid=?,"
				+ "customerid=?" 
				+ " WHERE stallid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table stall");
		
		ps.setString(cnt++, getName());
		ps.setInt(cnt++, getType());
		ps.setDouble(cnt++, getMonthlyRental());
		ps.setInt(cnt++, getIsOccupied());
		ps.setInt(cnt++, getBuilding()==null? 0 : getBuilding().getId());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setInt(cnt++, getId());
		
		LogU.add(getName());
		LogU.add(getType());
		LogU.add(getMonthlyRental());
		LogU.add(getIsOccupied());
		LogU.add(getBuilding()==null? 0 : getBuilding().getId());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to stall : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT stallid FROM stall  ORDER BY stallid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("stallid");
		}
		
		rs.close();
		prep.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static int getInfo(int id){
		boolean isNotNull=false;
		int result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		int val = getLatestId();	
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
	public static boolean isIdNoExist(int id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement("SELECT stallid FROM stall WHERE stallid=?");
		ps.setInt(1, id);
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
		String sql = "UPDATE stall set isactivatestall=0 WHERE stallid=?";
		
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
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public double getMonthlyRental() {
		return monthlyRental;
	}
	public void setMonthlyRental(double monthlyRental) {
		this.monthlyRental = monthlyRental;
	}
	public int getIsOccupied() {
		return isOccupied;
	}
	public void setIsOccupied(int isOccupied) {
		this.isOccupied = isOccupied;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public MarketBuilding getBuilding() {
		return building;
	}
	public void setBuilding(MarketBuilding building) {
		this.building = building;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
