package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.enm.FormStatus;
import com.italia.municipality.lakesebu.enm.FormType;
import com.italia.municipality.lakesebu.utils.LogU;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 1/17/2022
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class GenCollectionForms {

	private long id;
	private int formType;
	private int isActive;
	private ORListing orListing;
	private GenCollection genCollection;
	
	public static List<GenCollectionForms> retrieve(String sqlAdd, String[] params){
		List<GenCollectionForms> taxes = new ArrayList<GenCollectionForms>();
		
		String tableform ="st";
		String tableGen = "gen";
		String tableOr = "orl";
		String sql = "SELECT * FROM gencollectionforms "+ tableform +" ,gencollection "+ tableGen + " ,orlisting "+ tableOr +"  WHERE "+tableform+".subisactive=1 " +
				tableform + ".genid=" + tableGen + ".genid AND " +
				tableform + ".orid=" + tableOr + ".orid ";  
		
				
		sql = sql + sqlAdd;		
				
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
			
			ORListing or = new ORListing();
			try{or.setId(rs.getLong("orid"));}catch(NullPointerException e){}
			try{or.setDateTrans(rs.getString("ordatetrans"));}catch(NullPointerException e){}
			try{or.setOrNumber(rs.getString("ornumber"));}catch(NullPointerException e){}
			try{or.setFormType(rs.getInt("aform"));}catch(NullPointerException e){}
			try{or.setIsActive(rs.getInt("isactiveor"));}catch(NullPointerException e){}
			try{or.setFormName(FormType.nameId(or.getFormType()));}catch(NullPointerException e){}
			try{or.setStatus(rs.getInt("orstatus"));}catch(NullPointerException e){}
			try{or.setStatusName(FormStatus.nameId(or.getStatus()));}catch(NullPointerException e){}
			try{or.setForminfo(rs.getString("forminfo"));}catch(NullPointerException e){}
			try{or.setNotes(rs.getString("notes"));}catch(NullPointerException e){}
			
			GenCollection gen = GenCollection.builder()
					.id(rs.getLong("genid"))
					.dateTrans(rs.getString("datetrans"))
					.monthYear(rs.getString("monthyear"))
					.isActive(rs.getInt("genisactive"))
					.build();
			
			GenCollectionForms form = GenCollectionForms.builder()
					.id(rs.getLong("genid"))
					.formType(rs.getInt("formtype"))
					.isActive(rs.getInt("subisactive"))
					.genCollection(gen)
					.orListing(or)
					.build();
			
			taxes.add(form);
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return taxes;
	}
	
	public static GenCollectionForms save(GenCollectionForms st){
		if(st!=null){
			
			long id = GenCollectionForms.getInfo(st.getId() ==0? GenCollectionForms.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = GenCollectionForms.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = GenCollectionForms.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = GenCollectionForms.insertData(st, "3");
			}
			
		}
		return st;
	}
	
	public void save(){
		
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		LogU.add("checking for new added data");
		if(id==1){
			LogU.add("insert new Data ");
			GenCollectionForms.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			GenCollectionForms.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			GenCollectionForms.insertData(this, "3");
		}
		
	}
	
	public static GenCollectionForms insertData(GenCollectionForms st, String type){
		String sql = "INSERT INTO gencollectionforms ("
				+ "subid,"
				+ "formtype,"
				+ "subisactive,"
				+ "genid."
				+ "orid)" 
				+ " VALUES(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table gencollectionforms");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			st.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			st.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setInt(cnt++, st.getFormType());
		ps.setInt(cnt++, st.getIsActive());
		ps.setLong(cnt++, st.getGenCollection().getId());
		ps.setLong(cnt++, st.getOrListing().getId());
		
		LogU.add(st.getFormType());
		LogU.add(st.getIsActive());
		LogU.add(st.getGenCollection().getId());
		LogU.add(st.getOrListing().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to gencollectionforms : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static GenCollectionForms updateData(GenCollectionForms st){
		String sql = "UPDATE gencollectionforms SET "
				+ "formtype=?,"
				+ "genid=?,"
				+ "orid=?"
				+ " WHERE subid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table gencollectionforms");
		
		ps.setInt(cnt++, st.getFormType());
		ps.setLong(cnt++, st.getGenCollection().getId());
		ps.setLong(cnt++, st.getOrListing().getId());
		
		LogU.add(st.getFormType());
		LogU.add(st.getGenCollection().getId());
		LogU.add(st.getOrListing().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to gencollectionforms : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT subid FROM gencollectionforms  ORDER BY subid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("subid");
		}
		
		rs.close();
		prep.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static long getInfo(long id){
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
		ps = conn.prepareStatement("SELECT subid FROM gencollectionforms WHERE subid=?");
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
		String sql = "UPDATE gencollectionforms set subisactive=0 WHERE subid=?";
		
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
