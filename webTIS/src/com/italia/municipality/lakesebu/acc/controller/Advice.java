package com.italia.municipality.lakesebu.acc.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.controller.BankAccounts;
import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.Responsibility;
import com.italia.municipality.lakesebu.controller.Signatories;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.controller.VRData;
import com.italia.municipality.lakesebu.database.BankChequeDatabaseConnect;
import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.enm.BankAccountType;
import com.italia.municipality.lakesebu.enm.FundType;
import com.italia.municipality.lakesebu.utils.LogU;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @version 1.o
 * @since 04/06/2021
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Advice {

	
	private long id;
	private String dateTrans;
	private int fundType;
	private String controlNumber;
	private String checks;
	private int isActive;
	private Timestamp timestamp;
	private UserDtls userDtls;
	
	
	private String fundTypeName;
	
	public static String getLatestContolNumber(int fundType) {
		
		String sql = "SELECT controlnum FROM advice WHERE isactivead=1 AND fundtype=" + fundType + " ORDER BY aid DESC limit 1";
		String controlNumber = "";
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = BankChequeDatabaseConnect.getConnection();
			ps = conn.prepareStatement(sql);
			
			
			System.out.println("SQL retrieve >> " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				controlNumber = rs.getString("controlnum");
			}
			rs.close();
			ps.close();
			BankChequeDatabaseConnect.close(conn);
		}catch(Exception e) {e.printStackTrace();}
		
	
		
		switch(fundType) {
		
		case 1 : break;
		case 2 : break;
		case 3 : break;
		case 4 : break;
			
		
		}
		
		return "";
		
	}
	
	public static List<Advice> retrieve(String sql, String[] params){
		List<Advice> ads = new ArrayList<Advice>();
		
		String tableAd = "ad";
		String tableUsr = "usr";
		
		String sqlQ = "SELECT * FROM advice "+ tableAd +", webtis.userdtls "+ tableUsr +" WHERE "+ 
				tableAd +".isactivead=1 AND " +
				tableAd +".userdtlsid=" + tableUsr+".userdtlsid ";
		
		sql = sqlQ + sql;
		
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = BankChequeDatabaseConnect.getConnection();
			ps = conn.prepareStatement(sql);
			
			if(params!=null && params.length>0){
				
				for(int i=0; i<params.length; i++){
					ps.setString(i+1, params[i]);
				}
				
			}
			System.out.println("SQL retrieve >> " + ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				Advice ad = Advice.builder()
						.id(rs.getLong("aid"))
						.dateTrans(rs.getString("dateTrans"))
						.fundType(rs.getInt("fundtype"))
						.controlNumber(rs.getString("controlnum"))
						.checks(rs.getString("checksid"))
						.isActive(rs.getInt("isactivead"))
						.fundTypeName(BankAccountType.typeName(rs.getInt("fundtype")))
						.build();
				
				UserDtls user = new UserDtls();
				try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
				try{user.setRegdate(rs.getString("regdate"));;}catch(NullPointerException e){}
				try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
				try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
				try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
				ad.setUserDtls(user);
				
				ads.add(ad);
				
			}
			rs.close();
			ps.close();
			BankChequeDatabaseConnect.close(conn);
		}catch(Exception e) {e.printStackTrace();}
		return ads;
	}
	
	public static Advice save(Advice st){
		if(st!=null){
			
			long id = Advice.getInfo(st.getId() ==0? Advice.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = Advice.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = Advice.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = Advice.insertData(st, "3");
			}
			
		}
		return st;
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
	
	public static Advice insertData(Advice st, String type){
		String sql = "INSERT INTO advice ("
				+ "aid,"
				+ "dateTrans,"
				+ "fundtype,"
				+ "controlnum,"
				+ "checksid,"
				+ "isactivead,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table advice");
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
		
		ps.setString(cnt++, st.getDateTrans());
		ps.setInt(cnt++, st.getFundType());
		ps.setString(cnt++, st.getControlNumber());
		ps.setString(cnt++, st.getChecks());
		ps.setInt(cnt++, st.getIsActive());
		ps.setLong(cnt++, st.getUserDtls().getUserdtlsid());
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getFundType());
		LogU.add(st.getControlNumber());
		LogU.add(st.getChecks());
		LogU.add(st.getIsActive());
		LogU.add(st.getUserDtls().getUserdtlsid());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to advice : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO advice ("
				+ "aid,"
				+ "dateTrans,"
				+ "fundtype,"
				+ "controlnum,"
				+ "checksid,"
				+ "isactivead,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table advice");
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
		
		ps.setString(cnt++, getDateTrans());
		ps.setInt(cnt++, getFundType());
		ps.setString(cnt++, getControlNumber());
		ps.setString(cnt++, getChecks());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getUserDtls().getUserdtlsid());
		
		LogU.add(getDateTrans());
		LogU.add(getFundType());
		LogU.add(getControlNumber());
		LogU.add(getChecks());
		LogU.add(getIsActive());
		LogU.add(getUserDtls().getUserdtlsid());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to advice : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Advice updateData(Advice st){
		String sql = "UPDATE advice SET "
				+ "dateTrans=?,"
				+ "fundtype=?,"
				+ "controlnum=?,"
				+ "checksid=?,"
				+ "userdtlsid=? " 
				+ " WHERE aid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table advice");
		
		ps.setString(cnt++, st.getDateTrans());
		ps.setInt(cnt++, st.getFundType());
		ps.setString(cnt++, st.getControlNumber());
		ps.setString(cnt++, st.getChecks());
		ps.setLong(cnt++, st.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, st.getId());
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getFundType());
		LogU.add(st.getControlNumber());
		LogU.add(st.getChecks());
		LogU.add(st.getUserDtls().getUserdtlsid());
		LogU.add(st.getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to advice : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public void updateData(){
		String sql = "UPDATE advice SET "
				+ "dateTrans=?,"
				+ "fundtype=?,"
				+ "controlnum=?,"
				+ "checksid=?,"
				+ "userdtlsid=? " 
				+ " WHERE aid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table advice");
		
		ps.setString(cnt++, getDateTrans());
		ps.setInt(cnt++, getFundType());
		ps.setString(cnt++, getControlNumber());
		ps.setString(cnt++, getChecks());
		ps.setLong(cnt++, getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getFundType());
		LogU.add(getControlNumber());
		LogU.add(getChecks());
		LogU.add(getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to advice : " + s.getMessage());
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
		sql="SELECT aid FROM advice  ORDER BY aid DESC LIMIT 1";	
		conn = BankChequeDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("aid");
		}
		
		rs.close();
		prep.close();
		BankChequeDatabaseConnect.close(conn);
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
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement("SELECT aid FROM advice WHERE aid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE advice set isactivead=0 WHERE aid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
	
}
