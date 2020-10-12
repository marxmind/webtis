package com.italia.municipality.lakesebu.licensing.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.database.LicensingDatabaseConnect;
import com.italia.municipality.lakesebu.utils.LogU;
/**
 * 
 * @author mark italia
 * @since 09/14/2017
 * @version 1.0
 *
 */
public class Regional {
	private int id;
	private String name;
	private int isActive;
	
	public Regional(){}
	
	public Regional(
			int id,
			String name,
			int isActive
			){
		this.id = id;
		this.name = name;
		this.isActive = isActive;
	}
	
	public static String regionalSQL(String tablename,Municipality bg){
		String sql= " AND "+ tablename +".isactivereg=" + bg.getIsActive();
		if(bg!=null){
			
			sql += " AND "+ tablename +".regid=" + bg.getId();
			
			if(bg.getName()!=null){
				
				sql += " AND "+ tablename +".regname like '%"+ bg.getName() +"%'";
				
			}
			
		}
		
		return sql;
	}	
	
	public static List<Regional> retrieve(String sql, String[] params){
		List<Regional> regs = new ArrayList<Regional>();
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			Regional reg = new Regional();
			try{reg.setId(rs.getInt("regid"));}catch(NullPointerException e){}
			try{reg.setName(rs.getString("regname"));}catch(NullPointerException e){}
			try{reg.setIsActive(rs.getInt("isactivereg"));}catch(NullPointerException e){}
			regs.add(reg);
		}
		
		rs.close();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return regs;
	}
	
	public static Regional save(Regional reg){
		if(reg!=null){
			
			int id = Regional.getInfo(reg.getId() ==0? Regional.getLatestId()+1 : reg.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				reg = Regional.insertData(reg, "1");
			}else if(id==2){
				LogU.add("update Data ");
				reg = Regional.updateData(reg);
			}else if(id==3){
				LogU.add("added new Data ");
				reg = Regional.insertData(reg, "3");
			}
			
		}
		return reg;
	}
	
	public void save(){
			
			int id = getInfo(getId() ==0? getLatestId()+1 : getId());
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
	
	public static Regional insertData(Regional reg, String type){
		String sql = "INSERT INTO regional ("
				+ "regid,"
				+ "regname,"
				+ "isactivereg)" 
				+ "values(?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table regional");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			reg.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			reg.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, reg.getName());
		ps.setInt(cnt++, reg.getIsActive());
		
		
		LogU.add(reg.getName());
		LogU.add(reg.getIsActive());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		LicensingDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to regional : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return reg;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO regional ("
				+ "regid,"
				+ "regname,"
				+ "isactivereg)" 
				+ "values(?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table regional");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, getName());
		ps.setInt(cnt++, getIsActive());
		
		
		LogU.add(getName());
		LogU.add(getIsActive());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		LicensingDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to regional : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Regional updateData(Regional reg){
		String sql = "UPDATE regional SET "
				+ "regname=?" 
				+ " WHERE regid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table regional");
		
		ps.setString(cnt++, reg.getName());
		ps.setInt(cnt++, reg.getId());
		
		
		LogU.add(reg.getName());
		LogU.add(reg.getId());
		
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		LicensingDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to regional : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return reg;
	}
	
	public void updateData(){
		String sql = "UPDATE regional SET "
				+ "regname=?" 
				+ " WHERE regid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table regional");
		
		ps.setString(cnt++, getName());
		ps.setInt(cnt++, getId());
		
		
		LogU.add(getName());
		LogU.add(getId());
		
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		LicensingDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to regional : " + s.getMessage());
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
		sql="SELECT regid FROM regional  ORDER BY regid DESC LIMIT 1";	
		conn = LicensingDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("regid");
		}
		
		rs.close();
		prep.close();
		LicensingDatabaseConnect.close(conn);
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
	public static boolean isIdNoExist(int id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement("SELECT regid FROM regional WHERE regid=?");
		ps.setInt(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		LicensingDatabaseConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE regional set isactivereg=0 WHERE regid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
		try{
		conn = LicensingDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		LicensingDatabaseConnect.close(conn);
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
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	
}



