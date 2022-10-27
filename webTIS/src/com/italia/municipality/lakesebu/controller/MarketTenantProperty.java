package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.enm.BuildingType;
import com.italia.municipality.lakesebu.enm.Buildings;
import com.italia.municipality.lakesebu.enm.StallType;
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
public class MarketTenantProperty {

	private long id;
	private int buildingId;
	private int buildingTypeId;
	private int stallTypeId;
	private String dateAquired;
	private String dateReturned;
	private double amountRented;
	private String specificName;
	private int isActive;
	private MarketTenant marketTenant;
	
	private Date aquiredDateTmp;
	private Date retiredDateTmp;
	
	private String buildingName;
	private String buildingType;
	private String stallName;
	
	public static List<MarketTenantProperty> retrieve(String sql, String[] params){
		List<MarketTenantProperty> trans = new ArrayList<MarketTenantProperty>();
		
		String tableProp = "prp";
		String tableTnt="tnt";
		String sqlAdd = "SELECT * FROM market_tenant_property "+ tableProp +", market_tenant "+ tableTnt +"  WHERE  "+ tableProp +".isactivepr=1 AND " +
		tableProp + ".tid=" + tableTnt + ".tid ";
		
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
		System.out.println("market_tenant_property SQL " + ps.toString());
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
		
			MarketTenantProperty prp = MarketTenantProperty.builder()
					.id(rs.getLong("prid"))
					.buildingId(rs.getInt("buildingid"))
					.stallTypeId(rs.getInt("stalltypeid"))
					.buildingTypeId(rs.getInt("buildingtypeid"))
					.dateAquired(rs.getString("dateaquired"))
					.dateReturned(rs.getString("datereturned"))
					.amountRented(rs.getDouble("rentalamount"))
					.specificName(rs.getString("specificname"))
					.isActive(rs.getInt("isactivepr"))
					.marketTenant(mk)
					.buildingName(Buildings.nameId(rs.getInt("buildingid")))
					.buildingType(BuildingType.nameId(rs.getInt("buildingid")))
					.stallName(StallType.nameId(rs.getInt("stalltypeid")))
					.build();
			
			
			trans.add(prp);
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	public static MarketTenantProperty save(MarketTenantProperty st){
		if(st!=null){
			
			long id = MarketTenantProperty.getInfo(st.getId() ==0? MarketTenantProperty.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = MarketTenantProperty.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = MarketTenantProperty.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = MarketTenantProperty.insertData(st, "3");
			}
			
		}
		return st;
	}
	
	public void save(){
		
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		LogU.add("checking for new added data");
		if(id==1){
			LogU.add("insert new Data ");
			MarketTenantProperty.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			MarketTenantProperty.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			MarketTenantProperty.insertData(this, "3");
		}
	}
	
	public static MarketTenantProperty insertData(MarketTenantProperty st, String type){
		String sql = "INSERT INTO market_tenant_property ("
				+ "prid,"
				+ "buildingid,"
				+ "stalltypeid,"
				+ "buildingtypeid,"
				+ "dateaquired,"
				+ "datereturned,"
				+ "rentalamount,"
				+ "isactivepr,"
				+ "tid,"
				+ "specificname)" 
				+ " VALUES(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table market_tenant_property");
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
		
		ps.setInt(cnt++, st.getBuildingId());
		ps.setInt(cnt++, st.getStallTypeId());
		ps.setInt(cnt++, st.getBuildingTypeId());
		ps.setString(cnt++, st.getDateAquired());
		ps.setString(cnt++, st.getDateReturned());
		ps.setDouble(cnt++, st.getAmountRented());
		ps.setInt(cnt++, st.getIsActive());
		ps.setLong(cnt++, st.getMarketTenant().getId());
		ps.setString(cnt++, st.getSpecificName());
		
		LogU.add(st.getBuildingId());
		LogU.add(st.getStallTypeId());
		LogU.add(st.getBuildingTypeId());
		LogU.add(st.getDateAquired());
		LogU.add(st.getDateReturned());
		LogU.add(st.getAmountRented());
		LogU.add(st.getIsActive());
		LogU.add(st.getMarketTenant().getId());
		LogU.add(st.getSpecificName());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to market_tenant_property : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static MarketTenantProperty updateData(MarketTenantProperty st){
		String sql = "UPDATE market_tenant_property SET "
				+ "buildingid=?,"
				+ "stalltypeid=?,"
				+ "buildingtypeid=?,"
				+ "dateaquired=?,"
				+ "datereturned=?,"
				+ "rentalamount=?,"
				+ "tid=?,"
				+ "specificname=?" 
				+ " WHERE prid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table market_tenant_property");
		
		ps.setInt(cnt++, st.getBuildingId());
		ps.setInt(cnt++, st.getStallTypeId());
		ps.setInt(cnt++, st.getBuildingTypeId());
		ps.setString(cnt++, st.getDateAquired());
		ps.setString(cnt++, st.getDateReturned());
		ps.setDouble(cnt++, st.getAmountRented());
		ps.setLong(cnt++, st.getMarketTenant().getId());
		ps.setString(cnt++, st.getSpecificName());
		ps.setLong(cnt++, st.getId());
		
		LogU.add(st.getBuildingId());
		LogU.add(st.getStallTypeId());
		LogU.add(st.getBuildingTypeId());
		LogU.add(st.getDateAquired());
		LogU.add(st.getDateReturned());
		LogU.add(st.getAmountRented());
		LogU.add(st.getMarketTenant().getId());
		LogU.add(st.getSpecificName());
		LogU.add(st.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to market_tenant_property : " + s.getMessage());
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
		sql="SELECT prid FROM market_tenant_property  ORDER BY prid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("prid");
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
		ps = conn.prepareStatement("SELECT prid FROM market_tenant_property WHERE prid=?");
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
		String sql = "UPDATE market_tenant_property set isactivepr=0 WHERE prid=?";
		
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
