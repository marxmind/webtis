package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.enm.BuildingType;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 06/15/2018
 * @version 1.0
 */
/**
 * 
 * @deprecated
 *this is no longer use
 */
public class MarketBuilding {

	private int id;
	private String name;
	private String stallNumber;
	private int type;
	private double monthlyRental;
	private int isActive;
	
	private String typeName;
	
	public static List<MarketBuilding> retrieve(String sqlAdd, String[] params){
		List<MarketBuilding> mks = Collections.synchronizedList(new ArrayList<MarketBuilding>());
		String supTable = "mkt";
		String sql = "SELECT * FROM marketbuilding "+ supTable +
				" WHERE "
				+ supTable +".isactivatebldg = 1 ";
		
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
			
			MarketBuilding mk = new MarketBuilding();
			try{mk.setId(rs.getInt("bldgid"));}catch(NullPointerException e) {}
			try{mk.setName(rs.getString("bldgname"));}catch(NullPointerException e) {}
			try{mk.setStallNumber(rs.getString("stallno"));}catch(NullPointerException e) {}
			try{mk.setType(rs.getInt("bldgtype"));}catch(NullPointerException e) {}
			try{mk.setMonthlyRental(rs.getDouble("monthylyrental"));}catch(NullPointerException e) {}
			try{mk.setIsActive(rs.getInt("isactivatebldg"));}catch(NullPointerException e) {}
			try{mk.setTypeName(BuildingType.nameId(mk.getType()));}catch(NullPointerException e) {}
			
			mks.add(mk);
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return mks;
	}
	
	public static MarketBuilding retrieve(int id){
		MarketBuilding mk = new MarketBuilding();
		String supTable = "mkt";
		String sql = "SELECT * FROM marketbuilding "+ supTable +
				" WHERE "
				+ supTable +".isactivatebldg = 1 AND bldgid=" + id;
		
        System.out.println("SQL "+sql);
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{mk.setId(rs.getInt("bldgid"));}catch(NullPointerException e) {}
			try{mk.setName(rs.getString("bldgname"));}catch(NullPointerException e) {}
			try{mk.setStallNumber(rs.getString("stallno"));}catch(NullPointerException e) {}
			try{mk.setType(rs.getInt("bldgtype"));}catch(NullPointerException e) {}
			try{mk.setMonthlyRental(rs.getDouble("monthylyrental"));}catch(NullPointerException e) {}
			try{mk.setIsActive(rs.getInt("isactivatebldg"));}catch(NullPointerException e) {}
			try{mk.setTypeName(BuildingType.nameId(mk.getType()));}catch(NullPointerException e) {}
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return mk;
	}
	
	public static MarketBuilding save(MarketBuilding mk){
		if(mk!=null){
			
			int id = MarketBuilding.getInfo(mk.getId() ==0? MarketBuilding.getLatestId()+1 : mk.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				mk = MarketBuilding.insertData(mk, "1");
			}else if(id==2){
				LogU.add("update Data ");
				mk = MarketBuilding.updateData(mk);
			}else if(id==3){
				LogU.add("added new Data ");
				mk = MarketBuilding.insertData(mk, "3");
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
	
	public static MarketBuilding insertData(MarketBuilding mk, String type){
		String sql = "INSERT INTO marketbuilding ("
				+ "bldgid,"
				+ "bldgname,"
				+ "stallno,"
				+ "bldgtype,"
				+ "monthylyrental,"
				+ "isactivatebldg)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table marketbuilding");
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
		ps.setString(cnt++, mk.getStallNumber());
		ps.setInt(cnt++, mk.getType());
		ps.setDouble(cnt++, mk.getMonthlyRental());
		ps.setInt(cnt++, mk.getIsActive());
		
		LogU.add(mk.getName());
		LogU.add(mk.getStallNumber());
		LogU.add(mk.getType());
		LogU.add(mk.getMonthlyRental());
		LogU.add(mk.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to marketbuilding : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mk;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO marketbuilding ("
				+ "bldgid,"
				+ "bldgname,"
				+ "stallno,"
				+ "bldgtype,"
				+ "monthylyrental,"
				+ "isactivatebldg)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table marketbuilding");
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
		ps.setString(cnt++, getStallNumber());
		ps.setInt(cnt++, getType());
		ps.setDouble(cnt++, getMonthlyRental());
		ps.setInt(cnt++, getIsActive());
		
		LogU.add(getName());
		LogU.add(getStallNumber());
		LogU.add(getType());
		LogU.add(getMonthlyRental());
		LogU.add(getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to marketbuilding : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static MarketBuilding updateData(MarketBuilding mk){
		String sql = "UPDATE marketbuilding SET "
				+ "bldgname=?,"
				+ "stallno=?,"
				+ "bldgtype=?,"
				+ "monthylyrental=? " 
				+ " WHERE bldgid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table marketbuilding");
		
		ps.setString(cnt++, mk.getName());
		ps.setString(cnt++, mk.getStallNumber());
		ps.setInt(cnt++, mk.getType());
		ps.setDouble(cnt++, mk.getMonthlyRental());
		ps.setInt(cnt++, mk.getId());
		
		LogU.add(mk.getName());
		LogU.add(mk.getStallNumber());
		LogU.add(mk.getType());
		LogU.add(mk.getMonthlyRental());
		LogU.add(mk.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to marketbuilding : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return mk;
	}
	
	public void updateData(){
		String sql = "UPDATE marketbuilding SET "
				+ "bldgname=?,"
				+ "stallno=?,"
				+ "bldgtype=?,"
				+ "monthylyrental=? " 
				+ " WHERE bldgid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table marketbuilding");
		
		ps.setString(cnt++, getName());
		ps.setString(cnt++, getStallNumber());
		ps.setInt(cnt++, getType());
		ps.setDouble(cnt++, getMonthlyRental());
		ps.setInt(cnt++, getId());
		
		LogU.add(getName());
		LogU.add(getStallNumber());
		LogU.add(getType());
		LogU.add(getMonthlyRental());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to marketbuilding : " + s.getMessage());
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
		sql="SELECT bldgid FROM marketbuilding  ORDER BY bldgid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("bldgid");
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
		ps = conn.prepareStatement("SELECT bldgid FROM marketbuilding WHERE bldgid=?");
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
		String sql = "UPDATE marketbuilding set isactivatebldg=0 WHERE bldgid=?";
		
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
	public String getStallNumber() {
		return stallNumber;
	}
	public void setStallNumber(String stallNumber) {
		this.stallNumber = stallNumber;
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
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	 
	
}
