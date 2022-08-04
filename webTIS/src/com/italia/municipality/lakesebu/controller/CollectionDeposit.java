package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.database.BankChequeDatabaseConnect;
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
public class CollectionDeposit {
	private long id;
	private int serialNo;
	private String dateTrans;
	private String dvPayroll;
	private String cafoaNo;
	private String centerOffice;
	private String payee;
	private String naturePay;
	private double amount;
	private int fundId;
	private int isActive;
	private CollectionDepositReport collectionDepositReport;
	
	private int number;
	
	public static List<CollectionDeposit> retrieveReportGroupCollectionDeposit(long reportId){
		List<CollectionDeposit> caz = new ArrayList<CollectionDeposit>();
		
		String sql = "SELECT * FROM collectiondeposit  WHERE  isactivecdd=1 AND cdid="+ reportId + " ORDER BY dvpayroll";
			
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("Cash collectiondeposit SQL " + ps.toString());
		rs = ps.executeQuery();
		int x = 1;
		while(rs.next()){
			

			CollectionDeposit cz = CollectionDeposit.builder()
					.number(x)
					.id(rs.getLong("cddid"))
					.dateTrans(rs.getString("datetrans"))
					.serialNo(rs.getInt("serialno"))
					.dvPayroll(rs.getString("dvpayroll"))
					.cafoaNo(rs.getString("cafoano"))
					.centerOffice(rs.getString("center"))
					.payee(rs.getString("payee"))
					.naturePay(rs.getString("naturepay"))
					.amount(rs.getDouble("amount"))
					.isActive(rs.getInt("isactivecdd"))
					.fundId(rs.getInt("fundid"))
					.collectionDepositReport(CollectionDepositReport.builder().id(rs.getLong("cdid")).build())
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
	
	public static List<CollectionDeposit> retrieveOne(long reportId){
		List<CollectionDeposit> caz = new ArrayList<CollectionDeposit>();
		
		String sql = "SELECT * FROM collectiondeposit  WHERE  isactivecdd=1 AND cddid="+ reportId;
			
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("Cash collectiondeposit SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			CollectionDeposit cz = CollectionDeposit.builder()
					.id(rs.getLong("cddid"))
					.dateTrans(rs.getString("datetrans"))
					.serialNo(rs.getInt("serialno"))
					.dvPayroll(rs.getString("dvpayroll"))
					.cafoaNo(rs.getString("cafoano"))
					.centerOffice(rs.getString("center"))
					.payee(rs.getString("payee"))
					.naturePay(rs.getString("naturepay"))
					.amount(rs.getDouble("amount"))
					.isActive(rs.getInt("isactivecdd"))
					.fundId(rs.getInt("fundid"))
					.collectionDepositReport(CollectionDepositReport.builder().id(rs.getLong("cdid")).build())
					.build();
			caz.add(cz);
		}
		
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return caz;
	}
	
	public static List<CollectionDeposit> retrieve(String sql, String[] params){
		List<CollectionDeposit> caz = new ArrayList<CollectionDeposit>();
		
		
		String tableCaz = "caz";
		String tableRpt = "rpt";
		String sqlAdd = "SELECT * FROM collectiondeposit "+tableCaz+", collectiondepositreport "+ tableRpt +"  WHERE  "+tableCaz+".isactivecdd=1 AND "
				+ tableCaz + ".cdid=" + tableRpt + ".cdid ";
				
		
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
		System.out.println("Cash collectiondeposit SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			CollectionDepositReport rpt = CollectionDepositReport.builder()
					.id(rs.getLong("cdid"))
					.reportNo(rs.getString("reportno"))
					.bankAccountNo(rs.getString("bankaccountno"))
					.periodCovered(rs.getString("periodcovered"))
					.dateReport(rs.getString("datereport"))
					.totalAmount(rs.getDouble("totalamount"))
					.disbursingOfficer(rs.getString("disbursingofficer"))
					.designation(rs.getString("designation"))
					.receivingOfficer(rs.getString("receivingofficer"))
					.receivePosition(rs.getString("receivedposition"))
					.isActive(rs.getInt("isactivecd"))
					.fundId(rs.getInt("fundid"))
					.build();
			
			CollectionDeposit cz = CollectionDeposit.builder()
					.id(rs.getLong("cddid"))
					.dateTrans(rs.getString("datetrans"))
					.serialNo(rs.getInt("serialno"))
					.dvPayroll(rs.getString("dvpayroll"))
					.cafoaNo(rs.getString("cafoano"))
					.centerOffice(rs.getString("center"))
					.payee(rs.getString("payee"))
					.naturePay(rs.getString("naturepay"))
					.amount(rs.getDouble("amount"))
					.isActive(rs.getInt("isactivecdd"))
					.fundId(rs.getInt("fundid"))
					.collectionDepositReport(rpt)
					.build();
			caz.add(cz);
		}
		
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return caz;
	}
	
	public static CollectionDeposit save(CollectionDeposit st){
		if(st!=null){
			
			long id = CollectionDeposit.getInfo(st.getId() ==0? CollectionDeposit.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = CollectionDeposit.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = CollectionDeposit.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = CollectionDeposit.insertData(st, "3");
			}
			
		}
		return st;
	}
	
	public void save(){
		
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		LogU.add("checking for new added data");
		if(id==1){
			LogU.add("insert new Data ");
			CollectionDeposit.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			CollectionDeposit.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			CollectionDeposit.insertData(this, "3");
		}
		
 }
	
	public static CollectionDeposit insertData(CollectionDeposit st, String type){
		String sql = "INSERT INTO collectiondeposit ("
				+ "cddid,"
				+ "datetrans,"
				+ "serialno,"
				+ "dvpayroll,"
				+ "cafoano,"
				+ "center,"
				+ "payee,"
				+ "naturepay,"
				+ "amount,"
				+ "isactivecdd,"
				+ "fundid,"
				+ "cdid)" 
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table collectiondeposit");
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
		ps.setInt(cnt++, st.getSerialNo());
		ps.setString(cnt++, st.getDvPayroll());
		ps.setString(cnt++, st.getCafoaNo());
		ps.setString(cnt++, st.getCenterOffice());
		ps.setString(cnt++, st.getPayee());
		ps.setString(cnt++, st.getNaturePay());
		ps.setDouble(cnt++, st.getAmount());
		ps.setInt(cnt++, st.getIsActive());
		ps.setInt(cnt++, st.getFundId());
		ps.setLong(cnt++, st.getCollectionDepositReport().getId());
		
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getSerialNo());
		LogU.add(st.getDvPayroll());
		LogU.add(st.getCafoaNo());
		LogU.add(st.getCenterOffice());
		LogU.add(st.getPayee());
		LogU.add(st.getNaturePay());
		LogU.add(st.getAmount());
		LogU.add(st.getIsActive());
		LogU.add(st.getFundId());
		LogU.add(st.getCollectionDepositReport().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to collectiondeposit : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static CollectionDeposit updateData(CollectionDeposit st){
		String sql = "UPDATE collectiondeposit SET "
				+ "datetrans=?,"
				+ "serialno=?,"
				+ "dvpayroll=?,"
				+ "cafoano=?,"
				+ "center=?,"
				+ "payee=?,"
				+ "naturepay=?,"
				+ "amount=?,"
				+ "fundid=?,"
				+ "cdid=?" 
				+ " WHERE cddid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table collectiondeposit");
		
		ps.setString(cnt++, st.getDateTrans());
		ps.setInt(cnt++, st.getSerialNo());
		ps.setString(cnt++, st.getDvPayroll());
		ps.setString(cnt++, st.getCafoaNo());
		ps.setString(cnt++, st.getCenterOffice());
		ps.setString(cnt++, st.getPayee());
		ps.setString(cnt++, st.getNaturePay());
		ps.setDouble(cnt++, st.getAmount());
		ps.setInt(cnt++, st.getFundId());
		ps.setLong(cnt++, st.getCollectionDepositReport().getId());
		ps.setLong(cnt++, st.getId());
		
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getSerialNo());
		LogU.add(st.getDvPayroll());
		LogU.add(st.getCafoaNo());
		LogU.add(st.getCenterOffice());
		LogU.add(st.getPayee());
		LogU.add(st.getNaturePay());
		LogU.add(st.getAmount());
		LogU.add(st.getFundId());
		LogU.add(st.getCollectionDepositReport().getId());
		LogU.add(st.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to collectiondeposit : " + s.getMessage());
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
		sql="SELECT cddid FROM collectiondeposit  ORDER BY cddid DESC LIMIT 1";	
		conn = BankChequeDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("cddid");
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
		ps = conn.prepareStatement("SELECT cddid FROM collectiondeposit WHERE cddid=?");
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
		String sql = "UPDATE collectiondeposit set isactivecdd=0 WHERE cddid=?";
		
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
