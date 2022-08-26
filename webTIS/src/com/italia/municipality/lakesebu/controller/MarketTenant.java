package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
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
 * @since 08/09/2022
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class MarketTenant {
	private long id;
	private String dateRegistered;
	private String fullName;
	private String address;
	private String contactNo;
	private int isActive;
	
	private Date dateRegisteredTmp;
	
	public static List<MarketTenant> retrieve(String sql, String[] params){
		List<MarketTenant> trans = new ArrayList<MarketTenant>();
		
		String sqlAdd = "SELECT * FROM market_tenant  WHERE  isactivet=1 ";
		
		sql = sqlAdd + sql;
		
		
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
		System.out.println("market_tenant SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			MarketTenant mk = MarketTenant.builder()
					.id(rs.getLong("tid"))
					.dateRegistered(rs.getString("datetrans"))
					.fullName(rs.getString("fullname"))
					.address(rs.getString("address"))
					.contactNo(rs.getString("contactnumber"))
					.isActive(rs.getInt("isactivet"))
					.build();
		
			
			trans.add(mk);
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	public static MarketTenant save(MarketTenant st){
		if(st!=null){
			
			long id = MarketTenant.getInfo(st.getId() ==0? MarketTenant.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = MarketTenant.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = MarketTenant.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = MarketTenant.insertData(st, "3");
			}
			
		}
		return st;
	}
	
	public void save(){
		
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		LogU.add("checking for new added data");
		if(id==1){
			LogU.add("insert new Data ");
			MarketTenant.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			MarketTenant.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			MarketTenant.insertData(this, "3");
		}
	}
	
	public static MarketTenant insertData(MarketTenant st, String type){
		String sql = "INSERT INTO market_tenant ("
				+ "tid,"
				+ "datetrans,"
				+ "fullname,"
				+ "address,"
				+ "contactnumber,"
				+ "isactivet)" 
				+ " VALUES(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table market_tenant");
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
		
		ps.setString(cnt++, st.getDateRegistered());
		ps.setString(cnt++, st.getFullName().toUpperCase());
		ps.setString(cnt++, st.getAddress().toUpperCase());
		ps.setString(cnt++, st.getContactNo());
		ps.setInt(cnt++, st.getIsActive());
		
		LogU.add(st.getDateRegistered());
		LogU.add(st.getFullName().toUpperCase());
		LogU.add(st.getAddress().toUpperCase());
		LogU.add(st.getContactNo());
		LogU.add(st.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to market_tenant : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static MarketTenant updateData(MarketTenant st){
		String sql = "UPDATE market_tenant SET "
				+ "datetrans=?,"
				+ "fullname=?,"
				+ "address=?,"
				+ "contactnumber=?"
				+ " WHERE tid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table market_tenant");
		
		ps.setString(cnt++, st.getDateRegistered());
		ps.setString(cnt++, st.getFullName().toUpperCase());
		ps.setString(cnt++, st.getAddress().toUpperCase());
		ps.setString(cnt++, st.getContactNo());
		ps.setLong(cnt++, st.getId());
		
		LogU.add(st.getDateRegistered());
		LogU.add(st.getFullName().toUpperCase());
		LogU.add(st.getAddress().toUpperCase());
		LogU.add(st.getContactNo());
		LogU.add(st.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to market_tenant : " + s.getMessage());
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
		sql="SELECT tid FROM market_tenant  ORDER BY tid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("tid");
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
		ps = conn.prepareStatement("SELECT tid FROM market_tenant WHERE tid=?");
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
		String sql = "UPDATE market_tenant set isactivet=0 WHERE tid=?";
		
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
