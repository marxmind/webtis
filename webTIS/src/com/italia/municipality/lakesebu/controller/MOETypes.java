package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 02/16/2017
 * @version 1.0
 *
 */
public class MOETypes {

	private int id;
	private String nameType;
	private int isActive;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	public MOETypes(){}
	
	public MOETypes(
			int id,
			String nameType,
			int isActive,
			UserDtls userDtls
			){
		this.id = id;
		this.nameType = nameType;
		this.isActive = isActive;
		this.userDtls = userDtls;
	}
	
	public static List<MOETypes> retrieve(String sql, String[] params){
		
		List<MOETypes> types = Collections.synchronizedList(new ArrayList<MOETypes>());
		
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
			
			MOETypes type = new MOETypes();
			try{type.setId(rs.getInt("mid"));}catch(NullPointerException e){}
			try{type.setNameType(rs.getString("moename"));}catch(NullPointerException e){}
			try{type.setIsActive(rs.getInt("misactive"));}catch(NullPointerException e){}
			try{type.setTimestamp(rs.getTimestamp("mtimestamp"));}catch(NullPointerException e){}
			try{UserDtls userDtls = new UserDtls();
			userDtls.setUserdtlsid(rs.getLong("userdtlsid"));
			type.setUserDtls(userDtls);}catch(NullPointerException e){}
			types.add(type);
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		return types;
	}

	public static void save(MOETypes type){
		if(type!=null){
			
			int id = MOETypes.getInfo(type.getId() ==0? MOETypes.getLatestId()+1 : type.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				MOETypes.insertData(type, "1");
			}else if(id==2){
				LogU.add("update Data ");
				MOETypes.updateData(type);
			}else if(id==3){
				LogU.add("added new Data ");
				MOETypes.insertData(type, "3");
			}
			
		}
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
	
	public static MOETypes insertData(MOETypes moe, String type){
		String sql = "INSERT INTO moetypes ("
				+ "mid,"
				+ "moename,"
				+ "misactive,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table moetypes");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			moe.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			moe.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, moe.getNameType());
		ps.setInt(cnt++, moe.getIsActive());
		ps.setLong(cnt++, moe.getUserDtls()==null? 0l : (moe.getUserDtls().getUserdtlsid()==0l? 0l : moe.getUserDtls().getUserdtlsid()));
		
		LogU.add(moe.getNameType());
		LogU.add(moe.getIsActive());
		LogU.add(moe.getUserDtls()==null? 0l : (moe.getUserDtls().getUserdtlsid()==0l? 0l : moe.getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to moetypes : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return moe;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO moetypes ("
				+ "mid,"
				+ "moename,"
				+ "misactive,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table moetypes");
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
		
		ps.setString(cnt++, getNameType());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==0l? 0l : getUserDtls().getUserdtlsid()));
		
		LogU.add(getNameType());
		LogU.add(getIsActive());
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==0l? 0l : getUserDtls().getUserdtlsid()));
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to moetypes : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static MOETypes updateData(MOETypes moe){
		String sql = "UPDATE moetypes SET "
				+ "moename=?,"
				+ "userdtlsid=? " 
				+ " WHERE mid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table moetypes");
				
		ps.setString(cnt++, moe.getNameType());
		ps.setLong(cnt++, moe.getUserDtls()==null? 0l : (moe.getUserDtls().getUserdtlsid()==0l? 0l : moe.getUserDtls().getUserdtlsid()));
		ps.setInt(cnt++, moe.getId());
		
		LogU.add(moe.getNameType());
		LogU.add(moe.getUserDtls()==null? 0l : (moe.getUserDtls().getUserdtlsid()==0l? 0l : moe.getUserDtls().getUserdtlsid()));
		LogU.add(moe.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to moetypes : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return moe;
	}
	
	public void updateData(){
		String sql = "UPDATE moetypes SET "
				+ "moename=?,"
				+ "userdtlsid=? " 
				+ " WHERE mid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table moetypes");
				
		ps.setString(cnt++, getNameType());
		ps.setLong(cnt++, getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==0l? 0l : getUserDtls().getUserdtlsid()));
		ps.setInt(cnt++, getId());
		
		LogU.add(getNameType());
		LogU.add(getUserDtls()==null? 0l : (getUserDtls().getUserdtlsid()==0l? 0l : getUserDtls().getUserdtlsid()));
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to moetypes : " + s.getMessage());
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
		sql="SELECT mid FROM moetypes  ORDER BY mid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("mid");
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
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement("SELECT mid FROM moetypes WHERE mid=?");
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
		String sql = "UPDATE moetypes set misactive=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE userdtlsid=?";
		
		String[] params = new String[1];
		params[0] = getUserDtls().getUserdtlsid()+"";
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

	public String getNameType() {
		return nameType;
	}

	public void setNameType(String nameType) {
		this.nameType = nameType;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public UserDtls getUserDtls() {
		return userDtls;
	}

	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	
}
