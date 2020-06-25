package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.database.BankChequeDatabaseConnect;
import com.italia.municipality.lakesebu.database.TaxDatabaseConnect;
import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 5/28/2020
 *
 */
public class ModePayment {
	
	private long id;
	private int type;
	private double penaltyAmount;
	private double amount;
	private int isActive;
	private Timestamp timestamp;
	private PaymentHistory history;
	
	public ModePayment() {}
	
	public ModePayment(long id) {
		this.id = id;
	}
	
	public static List<ModePayment> retrieve(String sql, String[] params){
		List<ModePayment> lands = new ArrayList<ModePayment>();
		
		String dbTax = ReadConfig.value(AppConf.DB_NAME_TAX);
		
		String tblMode = "md";
		String tblHis = "hs";
		
		String stateSql = "SELECT * FROM "+ dbTax + ".paymenthistory " + tblMode + ", " + dbTax +".paymenthistory " + tblHis + " WHERE " + tblMode + ".isactivemode=1 AND " +
		tblMode + ".pid="+ tblHis + ".pid ";
		
		sql = stateSql + sql;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = TaxDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		System.out.println("SQL land: " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			ModePayment m = new ModePayment();
			try{m.setId(rs.getLong("moid"));}catch(NullPointerException e){}
			try{m.setType(rs.getInt("typemode"));}catch(NullPointerException e){}
			try{m.setPenaltyAmount(rs.getDouble("penaltyamnt"));}catch(NullPointerException e){}
			try{m.setAmount(rs.getDouble("amount"));}catch(NullPointerException e){}
			try{m.setIsActive(rs.getInt("isactivemode"));}catch(NullPointerException e){}
			
			PaymentHistory p = new PaymentHistory();
			try{p.setId(rs.getLong("pid"));}catch(NullPointerException e){}
			try{p.setDatePaid(rs.getString("pdate"));}catch(NullPointerException e){}
			try{p.setLandPayor(new LandPayor(rs.getLong("payorlandid")));}catch(NullPointerException e){}
			try{p.setYearPaid(rs.getString("yearpaid"));}catch(NullPointerException e){}
			try{p.setOrNumber(rs.getString("ornumber"));}catch(NullPointerException e){}
			try{p.setIsActive(rs.getInt("isactivepayment"));}catch(NullPointerException e){}
			try{p.setTaxDue(rs.getDouble("taxdue"));}catch(NullPointerException e){}
			p.setTimestamp(rs.getTimestamp("timestampy"));
			m.setHistory(p);
			
			lands.add(m);
			
		}
		
		rs.close();
		ps.close();
		TaxDatabaseConnect.close(conn);
		}catch(SQLException sl){}
		
		return lands;
	}
	
	public static ModePayment save(ModePayment pay){
		if(pay!=null){
			long id = getInfo(pay.getId()==0? getLatestId()+1 : pay.getId());
			if(id==1){
				pay = ModePayment.insertData(pay, "1");
			}else if(id==2){
				pay = ModePayment.updateData(pay);
			}else if(id==3){
				pay = ModePayment.insertData(pay, "3");
			}
			
		}
		return pay;
	}
	
	
	public void save(){
		
			long id = getInfo(getId()==0? getLatestId()+1 : getId());
			if(id==1){
				insertData("1");
			}else if(id==2){
				updateData();
			}else if(id==3){
				insertData("3");
			}
			
		
	}
	
public static ModePayment insertData(ModePayment pay, String type){
		
		String sql = "INSERT INTO modepayment ("
				+ "moid,"
				+ "pid,"
				+ "typemode,"
				+ "penaltyamnt,"
				+ "amount,"
				+ "isactivemode) " 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		LogU.add("Insert into table modepayment");	
		conn = TaxDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			pay.setId(id);
			LogU.add(id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			pay.setId(id);
			LogU.add(id);
		}
		ps.setLong(cnt++, pay.getHistory().getId());
		ps.setInt(cnt++, pay.getType());
		ps.setDouble(cnt++, pay.getPenaltyAmount());
		ps.setDouble(cnt++, pay.getAmount());
		ps.setInt(cnt++, pay.getIsActive());
		
		LogU.add(pay.getHistory().getId());
		LogU.add(pay.getType());
		LogU.add(pay.getPenaltyAmount());
		LogU.add(pay.getAmount());
		LogU.add(pay.getIsActive());
		
		ps.execute();
		ps.close();
		TaxDatabaseConnect.close(conn);
		LogU.add("Insert into table modepayment successfully.");
		}catch(SQLException s){
			s.getMessage();
			LogU.add("Error Insert into table modepayment.");
		}
		
		return pay;
	}
	
public void insertData(String type){
	
	String sql = "INSERT INTO modepayment ("
			+ "moid,"
			+ "pid,"
			+ "typemode,"
			+ "penaltyamnt,"
			+ "amount,"
			+ "isactivemode) " 
			+ "values(?,?,?,?,?,?)";
	
	PreparedStatement ps = null;
	Connection conn = null;
	try{
	LogU.add("Insert into table modepayment");	
	conn = TaxDatabaseConnect.getConnection();
	ps = conn.prepareStatement(sql);
	long id =1;
	int cnt = 1;
	if("1".equalsIgnoreCase(type)){
		ps.setLong(cnt++, id);
		setId(id);
		LogU.add(id);
	}else if("3".equalsIgnoreCase(type)){
		id=getLatestId()+1;
		ps.setLong(cnt++, id);
		setId(id);
		LogU.add(id);
	}
	ps.setLong(cnt++, getHistory().getId());
	ps.setInt(cnt++, getType());
	ps.setDouble(cnt++, getPenaltyAmount());
	ps.setDouble(cnt++, getAmount());
	ps.setInt(cnt++, getIsActive());
	
	LogU.add(getHistory().getId());
	LogU.add(getType());
	LogU.add(getPenaltyAmount());
	LogU.add(getAmount());
	LogU.add(getIsActive());
	
	ps.execute();
	ps.close();
	TaxDatabaseConnect.close(conn);
	LogU.add("Insert into table modepayment successfully.");
	}catch(SQLException s){
		s.getMessage();
		LogU.add("Error Insert into table modepayment.");
	}
	
}

public static ModePayment updateData(ModePayment pay){
	
	String sql = "UPDATE modepayment SET "
			+ "pid=?,"
			+ "typemode=?,"
			+ "penaltyamnt=?,"
			+ "amount=? " 
			+ " WHERE moid=?";
	
	PreparedStatement ps = null;
	Connection conn = null;
	try{
	LogU.add("update into table modepayment");	
	conn = TaxDatabaseConnect.getConnection();
	ps = conn.prepareStatement(sql);
	
	int cnt = 1;
	
	ps.setLong(cnt++, pay.getHistory().getId());
	ps.setInt(cnt++, pay.getType());
	ps.setDouble(cnt++, pay.getPenaltyAmount());
	ps.setDouble(cnt++, pay.getAmount());
	ps.setLong(cnt++, pay.getId());
	
	LogU.add(pay.getHistory().getId());
	LogU.add(pay.getType());
	LogU.add(pay.getPenaltyAmount());
	LogU.add(pay.getAmount());
	LogU.add(pay.getId());
	
	ps.execute();
	ps.close();
	TaxDatabaseConnect.close(conn);
	LogU.add("update into table modepayment successfully.");
	}catch(SQLException s){
		s.getMessage();
		LogU.add("update Insert into table modepayment.");
	}
	
	return pay;
}

public void updateData(){
	
	String sql = "UPDATE modepayment SET "
			+ "pid=?,"
			+ "typemode=?,"
			+ "penaltyamnt=?,"
			+ "amount=? " 
			+ " WHERE moid=?";
	
	PreparedStatement ps = null;
	Connection conn = null;
	try{
	LogU.add("update into table modepayment");	
	conn = TaxDatabaseConnect.getConnection();
	ps = conn.prepareStatement(sql);
	
	int cnt = 1;
	
	ps.setLong(cnt++, getHistory().getId());
	ps.setInt(cnt++, getType());
	ps.setDouble(cnt++, getPenaltyAmount());
	ps.setDouble(cnt++, getAmount());
	ps.setLong(cnt++, getId());
	
	LogU.add(getHistory().getId());
	LogU.add(getType());
	LogU.add(getPenaltyAmount());
	LogU.add(getAmount());
	LogU.add(getId());
	
	ps.execute();
	ps.close();
	TaxDatabaseConnect.close(conn);
	LogU.add("update into table modepayment successfully.");
	}catch(SQLException s){
		s.getMessage();
		LogU.add("update Insert into table modepayment.");
	}
	
}

	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT moid FROM modepayment  ORDER BY moid DESC LIMIT 1";	
		conn = TaxDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("pid");
		}
		
		rs.close();
		prep.close();
		TaxDatabaseConnect.close(conn);
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
		conn = TaxDatabaseConnect.getConnection();
		ps = conn.prepareStatement("SELECT moid FROM modepayment WHERE moid=?");
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
		conn = TaxDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		ps.executeUpdate();
		ps.close();
		TaxDatabaseConnect.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
		
	}
	
	
	public void delete(){
		String sql = "update modepayment set isactivepayment=0 WHERE moid=?";
		String params[] = new String[2];
		params[0] = Login.getUserLogin().getUserDtls().getUserdtlsid()+"";
		params[1] = getId()+"";
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		LogU.add("Deleting data in modepayment where moid="+getId());	
		conn = TaxDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		ps.executeUpdate();
		ps.close();
		TaxDatabaseConnect.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public double getPenaltyAmount() {
		return penaltyAmount;
	}
	public void setPenaltyAmount(double penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
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
	public PaymentHistory getHistory() {
		return history;
	}
	public void setHistory(PaymentHistory history) {
		this.history = history;
	}
	
}
