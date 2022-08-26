package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class MarketTenantPayment {

	private long id;
	private String datePaid;
	private int paidForMonth;
	private int paidForYear;
	private String remarks;
	private String officialNumber;
	private double amountPaid;
	private int paymentType;
	private int isActive;
	private int buildingId;
	private int stallTypeId;
	private int buildingTypeId;
	private MarketTenant marketTenant;
	private MarketTenantProperty marketTenantProperty;
	
	public static List<MarketTenantPayment> retrieve(String sql, String[] params){
		List<MarketTenantPayment> trans = new ArrayList<MarketTenantPayment>();
		
		String tablePay = "py";
		String tableTenant = "tnt";
		String tableProp = "prp";
		String sqlAdd = "SELECT * FROM market_tenant_payment "+ tablePay +", market_tenant "+ tableTenant +", market_tenant_property "+ tableProp +"  WHERE  "+ tablePay +".isactivetp=1 AND " +
				tablePay + ".tid=" + tableTenant + ".tid AND " +
				tablePay + ".prid=" + tableProp + ".prid ";
		
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
					.isActive(rs.getInt("isactivepr"))
					.specificName(rs.getString("specificname"))
					.marketTenant(mk)
					.build();
			
			MarketTenantPayment pay = MarketTenantPayment.builder()
					.id(rs.getLong("tpid"))
					.datePaid(rs.getString("datepaid"))
					.paidForMonth(rs.getInt("paidformonth"))
					.paidForYear(rs.getInt("paidforyear"))
					.remarks(rs.getString("remarks"))
					.officialNumber(rs.getString("ornumber"))
					.amountPaid(rs.getDouble("amount"))
					.paymentType(rs.getInt("paymenttype"))
					.isActive(rs.getInt("isactivetp"))
					.marketTenant(mk)
					.stallTypeId(rs.getInt("stalltypeid"))
					.buildingId(rs.getInt("buildingid"))
					.buildingTypeId(rs.getInt("buildingtypeid"))
					.marketTenantProperty(prp)
					.build();
			
			
			trans.add(pay);
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	public static MarketTenantPayment save(MarketTenantPayment st){
		if(st!=null){
			
			long id = MarketTenantPayment.getInfo(st.getId() ==0? MarketTenantPayment.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = MarketTenantPayment.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = MarketTenantPayment.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = MarketTenantPayment.insertData(st, "3");
			}
			
		}
		return st;
	}
	
	public void save(){
		
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		LogU.add("checking for new added data");
		if(id==1){
			LogU.add("insert new Data ");
			MarketTenantPayment.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			MarketTenantPayment.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			MarketTenantPayment.insertData(this, "3");
		}
	}
	
	public static MarketTenantPayment insertData(MarketTenantPayment st, String type){
		String sql = "INSERT INTO market_tenant_payment ("
				+ "tpid,"
				+ "datepaid,"
				+ "paidformonth,"
				+ "paidforyear,"
				+ "remarks,"
				+ "ornumber,"
				+ "amount,"
				+ "paymenttype,"
				+ "isactivetp,"
				+ "tid,"
				+ "stalltypeid,"
				+ "buildingid,"
				+ "buildingtypeid,"
				+ "prid)" 
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table market_tenant_payment");
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
		
		ps.setString(cnt++, st.getDatePaid());
		ps.setInt(cnt++, st.getPaidForMonth());
		ps.setInt(cnt++, st.getPaidForYear());
		ps.setString(cnt++, st.getRemarks());
		ps.setString(cnt++, st.getOfficialNumber());
		ps.setDouble(cnt++, st.getAmountPaid());
		ps.setInt(cnt++, st.getPaymentType());
		ps.setInt(cnt++, st.getIsActive());
		ps.setLong(cnt++, st.getMarketTenant().getId());
		ps.setInt(cnt++, st.getStallTypeId());
		ps.setInt(cnt++, st.getBuildingId());
		ps.setInt(cnt++, st.getBuildingTypeId());
		ps.setLong(cnt++, st.getMarketTenantProperty().getId());
		
		LogU.add(st.getDatePaid());
		LogU.add(st.getPaidForMonth());
		LogU.add(st.getPaidForYear());
		LogU.add(st.getRemarks());
		LogU.add(st.getOfficialNumber());
		LogU.add(st.getAmountPaid());
		LogU.add(st.getPaymentType());
		LogU.add(st.getIsActive());
		LogU.add(st.getMarketTenant().getId());
		LogU.add(st.getStallTypeId());
		LogU.add(st.getBuildingId());
		LogU.add(st.getBuildingTypeId());
		LogU.add(st.getMarketTenantProperty().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to market_tenant_payment : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static MarketTenantPayment updateData(MarketTenantPayment st){
		String sql = "UPDATE market_tenant_payment SET "
				+ "datepaid=?,"
				+ "paidformonth=?,"
				+ "paidforyear=?,"
				+ "remarks=?,"
				+ "ornumber=?,"
				+ "amount=?,"
				+ "paymenttype=?,"
				+ "tid=?,"
				+ "stalltypeid=?,"
				+ "buildingid=?,"
				+ "buildingtypeid=?,"
				+ "prid=?" 
				+ " WHERE tpid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table market_tenant_payment");
		
		
		ps.setString(cnt++, st.getDatePaid());
		ps.setInt(cnt++, st.getPaidForMonth());
		ps.setInt(cnt++, st.getPaidForYear());
		ps.setString(cnt++, st.getRemarks());
		ps.setString(cnt++, st.getOfficialNumber());
		ps.setDouble(cnt++, st.getAmountPaid());
		ps.setInt(cnt++, st.getPaymentType());
		ps.setInt(cnt++, st.getIsActive());
		ps.setLong(cnt++, st.getMarketTenant().getId());
		ps.setInt(cnt++, st.getStallTypeId());
		ps.setInt(cnt++, st.getBuildingId());
		ps.setInt(cnt++, st.getBuildingTypeId());
		ps.setLong(cnt++, st.getMarketTenantProperty().getId());
		
		LogU.add(st.getDatePaid());
		LogU.add(st.getPaidForMonth());
		LogU.add(st.getPaidForYear());
		LogU.add(st.getRemarks());
		LogU.add(st.getOfficialNumber());
		LogU.add(st.getAmountPaid());
		LogU.add(st.getPaymentType());
		LogU.add(st.getIsActive());
		LogU.add(st.getMarketTenant().getId());
		LogU.add(st.getStallTypeId());
		LogU.add(st.getBuildingId());
		LogU.add(st.getBuildingTypeId());
		LogU.add(st.getMarketTenantProperty().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to market_tenant_payment : " + s.getMessage());
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
		sql="SELECT tpid FROM market_tenant_payment  ORDER BY tpid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("tpid");
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
		ps = conn.prepareStatement("SELECT tpid FROM market_tenant_payment WHERE tpid=?");
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
		String sql = "UPDATE market_tenant_payment set isactivetp=0 WHERE tpid=?";
		
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
