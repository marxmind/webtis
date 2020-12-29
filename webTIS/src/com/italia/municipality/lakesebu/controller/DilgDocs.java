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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @since 11-18-2020
 * @version 1.0
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class DilgDocs {

	private int id;
	private String fileName;
	private String path;
	private int isActive;
	
	public static List<DilgDocs> retrieve(String sql, String[] params){
		List<DilgDocs> docs = Collections.synchronizedList(new ArrayList<DilgDocs>());
		
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
			DilgDocs doc = DilgDocs.builder()
					.id(rs.getInt("docid"))
					.fileName(rs.getString("docname"))
					.path(rs.getString("docpath"))
					.isActive(rs.getInt("isactivedoc"))
					.build();
			docs.add(doc);
		}
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){}
		
		return docs;
	}
	
	public static void save(DilgDocs in){
		if(in!=null){
			
			int id = DilgDocs.getInfo(in.getId()==0? DilgDocs.getLatestId()+1 : in.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				DilgDocs.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				DilgDocs.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				DilgDocs.insertData(in, "3");
			}
			
		}
	}
	
	public void save(){
			
			long id = getInfo(getId()==0? getLatestId()+1 : getId());
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
	
	public static DilgDocs insertData(DilgDocs in, String type){
		String sql = "INSERT INTO dilgdocs ("
				+ "docid,"
				+ "docname,"
				+ "docpath,"
				+ "isactivedoc)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table dilgdocs");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			in.setId(id);
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			in.setId(id);
			LogU.add("logid: " + id);
		}
		
		ps.setString(cnt++, in.getFileName());
		ps.setString(cnt++, in.getPath());
		ps.setInt(cnt++, in.getIsActive());
		
		LogU.add(in.getFileName());
		LogU.add(in.getPath());
		LogU.add(in.getIsActive());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to dilgdocs : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO dilgdocs ("
				+ "docid,"
				+ "docname,"
				+ "docpath,"
				+ "isactivedoc)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table dilgdocs");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			setId(id);
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			setId(id);
			LogU.add("logid: " + id);
		}
		
		ps.setString(cnt++, getFileName());
		ps.setString(cnt++, getPath());
		ps.setInt(cnt++, getIsActive());
		
		LogU.add(getFileName());
		LogU.add(getPath());
		LogU.add(getIsActive());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to dilgdocs : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static DilgDocs updateData(DilgDocs in){
		String sql = "UPDATE dilgdocs SET "
				+ "docname=?,"
				+ "docpath=?" 
				+ " WHERE docid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table dilgdocs");
		
		ps.setString(cnt++, in.getFileName());
		ps.setString(cnt++, in.getPath());
		ps.setInt(cnt++, in.getId());
		
		LogU.add(in.getFileName());
		LogU.add(in.getPath());
		LogU.add(in.getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to dilgdocs : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return in;
	}
	
	public void updateData(){
		String sql = "UPDATE dilgdocs SET "
				+ "docname=?,"
				+ "docpath=?" 
				+ " WHERE docid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table dilgdocs");
		
		ps.setString(cnt++, getFileName());
		ps.setString(cnt++, getPath());
		ps.setInt(cnt++, getId());
		
		LogU.add(getFileName());
		LogU.add(getPath());
		LogU.add(getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to dilgdocs : " + s.getMessage());
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
		sql="SELECT docid FROM dilgdocs  ORDER BY docid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("docid");
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
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement("SELECT docid FROM dilgdocs WHERE docid=?");
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
	
	public void delete(boolean retain){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE dilgdocs set isactivedoc=0 WHERE docid=?";
		
		if(!retain){
			sql = "DELETE FROM dilgdocs WHERE docid=?";
		}
		
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
}
