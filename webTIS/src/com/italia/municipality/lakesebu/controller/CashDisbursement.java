package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.database.BankChequeDatabaseConnect;
import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.utils.LogU;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class CashDisbursement {
	
	private long id;
	private String dateTrans;
	private String dvPayroll;
	private String cafoaNo;
	private String payee;
	private String naturePay;
	private double amount;
	private int fundId;
	private int isActive;
	private CashDisbursementReport report;
	
	private int number;
	
	public static List<CashDisbursement> retrieveReportGroupDisbursement(long reportId){
		List<CashDisbursement> caz = new ArrayList<CashDisbursement>();
		
		String sql = "SELECT * FROM cashdisbursement  WHERE  isactived=1 AND zid="+ reportId + " ORDER BY dvpayroll";
			
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("Cash Disbursement SQL " + ps.toString());
		rs = ps.executeQuery();
		int x = 1;
		while(rs.next()){
			
			CashDisbursement cz = CashDisbursement.builder()
					.id(rs.getLong("did"))
					.dateTrans(rs.getString("datetrans"))
					.dvPayroll(rs.getString("dvpayroll"))
					.cafoaNo(rs.getString("cafoano"))
					.payee(rs.getString("payee"))
					.naturePay(rs.getString("naturepay"))
					.amount(rs.getDouble("amount"))
					.isActive(rs.getInt("isactived"))
					.fundId(rs.getInt("fundid"))
					.report(CashDisbursementReport.builder().id(rs.getLong("zid")).build())
					.number(x)
					.build();
			caz.add(cz);
			x++;
		}
		
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return caz;
	}
	
	public static List<CashDisbursement> retrieveOne(long reportId){
		List<CashDisbursement> caz = new ArrayList<CashDisbursement>();
		
		String sql = "SELECT * FROM cashdisbursement  WHERE  isactived=1 AND did="+ reportId;
			
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("Cash Disbursement SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			CashDisbursement cz = CashDisbursement.builder()
					.id(rs.getLong("did"))
					.dateTrans(rs.getString("datetrans"))
					.dvPayroll(rs.getString("dvpayroll"))
					.cafoaNo(rs.getString("cafoano"))
					.payee(rs.getString("payee"))
					.naturePay(rs.getString("naturepay"))
					.amount(rs.getDouble("amount"))
					.isActive(rs.getInt("isactived"))
					.fundId(rs.getInt("fundid"))
					.report(CashDisbursementReport.builder().id(rs.getLong("zid")).build())
					.build();
			caz.add(cz);
		}
		
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return caz;
	}
	
	public static List<CashDisbursement> retrieve(String sql, String[] params){
		List<CashDisbursement> caz = new ArrayList<CashDisbursement>();
		
		
		String tableCaz = "caz";
		String tableRpt = "rpt";
		String sqlAdd = "SELECT * FROM cashdisbursement "+tableCaz+", cashdisbursementreport "+ tableRpt +"  WHERE  "+tableCaz+".isactived=1 AND "
				+ tableRpt + ".zid=" + tableRpt + ".zid ";
				
		
		sql = sqlAdd + sql;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		System.out.println("Cash Disbursement SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			CashDisbursementReport rpt = CashDisbursementReport.builder()
					.id(rs.getLong("zid"))
					.periodCovered(rs.getString("periodcovered"))
					.dateReport(rs.getString("datereport"))
					.totalAmount(rs.getDouble("totalamount"))
					.disbursingOfficer(rs.getString("disbursingofficer"))
					.designation(rs.getString("designation"))
					.isActive(rs.getInt("isactivez"))
					.fundId(rs.getInt("fundid"))
					.build();
			
			CashDisbursement cz = CashDisbursement.builder()
					.id(rs.getLong("did"))
					.dateTrans(rs.getString("datetrans"))
					.dvPayroll(rs.getString("dvpayroll"))
					.cafoaNo(rs.getString("cafoano"))
					.payee(rs.getString("payee"))
					.naturePay(rs.getString("naturepay"))
					.amount(rs.getDouble("amount"))
					.isActive(rs.getInt("isactived"))
					.fundId(rs.getInt("fundid"))
					.report(rpt)
					.build();
			caz.add(cz);
		}
		
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return caz;
	}
	
	public static CashDisbursement save(CashDisbursement st){
		if(st!=null){
			
			long id = CashDisbursement.getInfo(st.getId() ==0? CashDisbursement.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = CashDisbursement.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = CashDisbursement.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = CashDisbursement.insertData(st, "3");
			}
			
		}
		return st;
	}
	
	public void save(){
		
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		LogU.add("checking for new added data");
		if(id==1){
			LogU.add("insert new Data ");
			CashDisbursement.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			CashDisbursement.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			CashDisbursement.insertData(this, "3");
		}
		
 }
	
	public static CashDisbursement insertData(CashDisbursement st, String type){
		String sql = "INSERT INTO cashdisbursement ("
				+ "did,"
				+ "datetrans,"
				+ "dvpayroll,"
				+ "cafoano,"
				+ "payee,"
				+ "naturepay,"
				+ "amount,"
				+ "isactived,"
				+ "fundid,"
				+ "zid)" 
				+ " VALUES(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table cashdisbursement");
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
		ps.setString(cnt++, st.getDvPayroll());
		ps.setString(cnt++, st.getCafoaNo());
		ps.setString(cnt++, st.getPayee());
		ps.setString(cnt++, st.getNaturePay());
		ps.setDouble(cnt++, st.getAmount());
		ps.setInt(cnt++, st.getIsActive());
		ps.setInt(cnt++, st.getFundId());
		ps.setLong(cnt++, st.getReport().getId());
		
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getDvPayroll());
		LogU.add(st.getCafoaNo());
		LogU.add(st.getPayee());
		LogU.add(st.getNaturePay());
		LogU.add(st.getAmount());
		LogU.add(st.getIsActive());
		LogU.add(st.getFundId());
		LogU.add(st.getReport().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to cashdisbursement : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static CashDisbursement updateData(CashDisbursement st){
		String sql = "UPDATE cashdisbursement SET "
				+ "datetrans=?,"
				+ "dvpayroll=?,"
				+ "cafoano=?,"
				+ "payee=?,"
				+ "naturepay=?,"
				+ "amount=?,"
				+ "fundid=?,"
				+ "zid=?" 
				+ " WHERE did=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table cashdisbursement");
		
		ps.setString(cnt++, st.getDateTrans());
		ps.setString(cnt++, st.getDvPayroll());
		ps.setString(cnt++, st.getCafoaNo());
		ps.setString(cnt++, st.getPayee());
		ps.setString(cnt++, st.getNaturePay());
		ps.setDouble(cnt++, st.getAmount());
		ps.setInt(cnt++, st.getFundId());
		ps.setLong(cnt++, st.getReport().getId());
		ps.setLong(cnt++, st.getId());
		
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getDvPayroll());
		LogU.add(st.getCafoaNo());
		LogU.add(st.getPayee());
		LogU.add(st.getNaturePay());
		LogU.add(st.getAmount());
		LogU.add(st.getFundId());
		LogU.add(st.getReport().getId());
		LogU.add(st.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to cashdisbursement : " + s.getMessage());
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
		sql="SELECT did FROM cashdisbursement  ORDER BY did DESC LIMIT 1";	
		conn = BankChequeDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("did");
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
		ps = conn.prepareStatement("SELECT did FROM cashdisbursement WHERE did=?");
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
		String sql = "UPDATE cashdisbursement set isactived=0 WHERE did=?";
		
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
