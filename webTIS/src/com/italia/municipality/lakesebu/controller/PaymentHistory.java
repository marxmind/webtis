package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
 *this class is no longer use please refer to Payorpayment class
 */
@Deprecated
public class PaymentHistory {

	private long id;
	private String datePaid;
	private String yearPaid;
	private String orNumber;
	private double taxDue;
	private int isActive;
	private Timestamp timestamp;
	
	private UserDtls userDtls;
	private LandPayor landPayor;
	
	public PaymentHistory() {}
	
	public PaymentHistory(long id) {
		this.id = id;
	}
	
	public static List<PaymentHistory> retrieve(String sql, String[] params){
		List<PaymentHistory> lands = new ArrayList<PaymentHistory>();
		String dbTax = ReadConfig.value(AppConf.DB_NAME_TAX);
		String dbWeb = ReadConfig.value(AppConf.DB_NAME_WEBTIS);
		String tblHis = "hs";
		String tblLand = "ld";
		String tblUsr = "ur";
		String stateSql = "SELECT * FROM "+ dbTax +".paymenthistory " + tblHis + ", " + dbTax + ".payorland " + tblLand + ", " + dbWeb + ".userdtls " + tblUsr + " WHERE " + tblHis + ".isactivepayment=1 AND " +
		tblHis + ".payorlandid="+ tblLand + ".payorlandid AND " +
		tblHis + ".userdtlsid=" + tblUsr + ".userdtlsid "		;
		
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
			
			PaymentHistory p = new PaymentHistory();
			try{p.setId(rs.getLong("pid"));}catch(NullPointerException e){}
			try{p.setDatePaid(rs.getString("pdate"));}catch(NullPointerException e){}
			try{p.setLandPayor(new LandPayor(rs.getLong("payorlandid")));}catch(NullPointerException e){}
			try{p.setYearPaid(rs.getString("yearpaid"));}catch(NullPointerException e){}
			try{p.setOrNumber(rs.getString("ornumber"));}catch(NullPointerException e){}
			try{p.setIsActive(rs.getInt("isactivepayment"));}catch(NullPointerException e){}
			try{p.setTaxDue(rs.getDouble("taxdue"));}catch(NullPointerException e){}
			p.setTimestamp(rs.getTimestamp("timestampy"));
			
			LandPayor pay = new LandPayor();
			try{pay.setId(rs.getLong("payorlandid"));}catch(NullPointerException e){}
			try{pay.setTaxDeclarionNo(rs.getString("landtd"));}catch(NullPointerException e){}
			try{pay.setLandValue(rs.getDouble("landvalue"));}catch(NullPointerException e){}
			try{pay.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{pay.setLotNo(rs.getString("lotno"));}catch(NullPointerException e){}
			try{pay.setRemarks(rs.getString("landremarks"));}catch(NullPointerException e){}
			try{pay.setIsActive(rs.getInt("isactiveland"));}catch(NullPointerException e){}
			try{pay.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{pay.setStatus(rs.getInt("landstatus"));}catch(NullPointerException e){}
			p.setLandPayor(pay);
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			p.setUserDtls(user);
			
			lands.add(p);
			
		}
		
		rs.close();
		ps.close();
		TaxDatabaseConnect.close(conn);
		}catch(SQLException sl){}
		
		return lands;
	}
	
	public static PaymentHistory save(PaymentHistory pay){
		if(pay!=null){
			long id = getInfo(pay.getId()==0? getLatestId()+1 : pay.getId());
			if(id==1){
				pay = PaymentHistory.insertData(pay, "1");
			}else if(id==2){
				pay = PaymentHistory.updateData(pay);
			}else if(id==3){
				pay = PaymentHistory.insertData(pay, "3");
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
	
public static PaymentHistory insertData(PaymentHistory pay, String type){
		
		String sql = "INSERT INTO paymenthistory ("
				+ "pid,"
				+ "pdate,"
				+ "payorlandid,"
				+ "userdtlsid,"
				+ "yearpaid,"
				+ "ornumber,"
				+ "taxdue,"
				+ "isactivepayment) " 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		LogU.add("Insert into table paymenthistory");	
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
		ps.setString(cnt++, pay.getDatePaid());
		ps.setLong(cnt++, pay.getLandPayor().getId());
		ps.setLong(cnt++, pay.getUserDtls().getUserdtlsid());
		ps.setString(cnt++, pay.getYearPaid());
		ps.setString(cnt++, pay.getOrNumber());
		ps.setDouble(cnt++, pay.getTaxDue());
		ps.setInt(cnt++, pay.getIsActive());
		
		LogU.add(pay.getDatePaid());
		LogU.add(pay.getLandPayor().getId());
		LogU.add(pay.getUserDtls().getUserdtlsid());
		LogU.add(pay.getYearPaid());
		LogU.add(pay.getOrNumber());
		LogU.add(pay.getTaxDue());
		LogU.add(pay.getIsActive());
		
		ps.execute();
		ps.close();
		TaxDatabaseConnect.close(conn);
		LogU.add("Insert into table paymenthistory successfully.");
		}catch(SQLException s){
			s.getMessage();
			LogU.add("Error Insert into table paymenthistory.");
		}
		
		return pay;
	}

public void insertData(String type){
	
	String sql = "INSERT INTO paymenthistory ("
			+ "pid,"
			+ "pdate,"
			+ "payorlandid,"
			+ "userdtlsid,"
			+ "yearpaid,"
			+ "ornumber,"
			+ "taxdue,"
			+ "isactivepayment) " 
			+ "values(?,?,?,?,?,?,?,?)";
	
	PreparedStatement ps = null;
	Connection conn = null;
	try{
	LogU.add("Insert into table paymenthistory");	
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
	ps.setString(cnt++, getDatePaid());
	ps.setLong(cnt++, getLandPayor().getId());
	ps.setLong(cnt++, getUserDtls().getUserdtlsid());
	ps.setString(cnt++, getYearPaid());
	ps.setString(cnt++, getOrNumber());
	ps.setDouble(cnt++, getTaxDue());
	ps.setInt(cnt++, getIsActive());
	
	LogU.add(getDatePaid());
	LogU.add(getLandPayor().getId());
	LogU.add(getUserDtls().getUserdtlsid());
	LogU.add(getYearPaid());
	LogU.add(getOrNumber());
	LogU.add(getIsActive());
	
	ps.execute();
	ps.close();
	TaxDatabaseConnect.close(conn);
	LogU.add("Insert into table paymenthistory successfully.");
	}catch(SQLException s){
		s.getMessage();
		LogU.add("Error Insert into table paymenthistory.");
	}
	
}

public static PaymentHistory updateData(PaymentHistory pay){
	
	String sql = "UPDATE paymenthistory SET "
			+ "pdate=?,"
			+ "payorlandid=?,"
			+ "userdtlsid=?,"
			+ "yearpaid=?,"
			+ "ornumber=?,"
			+ "taxdue=? "
			+ " WHERE pid=?";
	
	PreparedStatement ps = null;
	Connection conn = null;
	try{
	LogU.add("Insert into table paymenthistory");	
	conn = TaxDatabaseConnect.getConnection();
	ps = conn.prepareStatement(sql);
	
	int cnt = 1;
	
	ps.setString(cnt++, pay.getDatePaid());
	ps.setLong(cnt++, pay.getLandPayor().getId());
	ps.setLong(cnt++, pay.getUserDtls().getUserdtlsid());
	ps.setString(cnt++, pay.getYearPaid());
	ps.setString(cnt++, pay.getOrNumber());
	ps.setDouble(cnt++, pay.getTaxDue());
	ps.setLong(cnt++, pay.getId());
	
	LogU.add(pay.getDatePaid());
	LogU.add(pay.getLandPayor().getId());
	LogU.add(pay.getUserDtls().getUserdtlsid());
	LogU.add(pay.getYearPaid());
	LogU.add(pay.getOrNumber());
	LogU.add(pay.getTaxDue());
	LogU.add(pay.getId());
	
	ps.execute();
	ps.close();
	TaxDatabaseConnect.close(conn);
	LogU.add("update  paymenthistory successfully.");
	}catch(SQLException s){
		s.getMessage();
		LogU.add("Error update into table paymenthistory.");
	}
	
	return pay;
}

public void updateData(){
	
	String sql = "UPDATE paymenthistory SET "
			+ "pdate=?,"
			+ "payorlandid=?,"
			+ "userdtlsid=?,"
			+ "yearpaid=?,"
			+ "ornumber=?,"
			+ "taxdue=? "
			+ " WHERE pid=?";
	
	PreparedStatement ps = null;
	Connection conn = null;
	try{
	LogU.add("Insert into table paymenthistory");	
	conn = TaxDatabaseConnect.getConnection();
	ps = conn.prepareStatement(sql);
	
	int cnt = 1;
	
	ps.setString(cnt++, getDatePaid());
	ps.setLong(cnt++, getLandPayor().getId());
	ps.setLong(cnt++, getUserDtls().getUserdtlsid());
	ps.setString(cnt++, getYearPaid());
	ps.setString(cnt++, getOrNumber());
	ps.setDouble(cnt++, getTaxDue());
	ps.setLong(cnt++, getId());
	
	LogU.add(getDatePaid());
	LogU.add(getLandPayor().getId());
	LogU.add(getUserDtls().getUserdtlsid());
	LogU.add(getYearPaid());
	LogU.add(getOrNumber());
	LogU.add(getTaxDue());
	LogU.add(getId());
	
	ps.execute();
	ps.close();
	TaxDatabaseConnect.close(conn);
	LogU.add("update  paymenthistory successfully.");
	}catch(SQLException s){
		s.getMessage();
		LogU.add("Error update into table paymenthistory.");
	}
}

	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT pid FROM paymenthistory  ORDER BY pid DESC LIMIT 1";	
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
		ps = conn.prepareStatement("SELECT pid FROM paymenthistory WHERE pid=?");
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
		String sql = "update paymenthistory set isactivepayment=0, userdtlsid=? WHERE pid=?";
		String params[] = new String[2];
		params[0] = Login.getUserLogin().getUserDtls().getUserdtlsid()+"";
		params[1] = getId()+"";
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		LogU.add("Deleting data in payorland where pid="+getId());	
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
	public String getYearPaid() {
		return yearPaid;
	}
	public void setYearPaid(String yearPaid) {
		this.yearPaid = yearPaid;
	}
	public String getOrNumber() {
		return orNumber;
	}
	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
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
	public LandPayor getLandPayor() {
		return landPayor;
	}
	public void setLandPayor(LandPayor landPayor) {
		this.landPayor = landPayor;
	}

	public String getDatePaid() {
		return datePaid;
	}

	public void setDatePaid(String datePaid) {
		this.datePaid = datePaid;
	}

	public double getTaxDue() {
		return taxDue;
	}

	public void setTaxDue(double taxDue) {
		this.taxDue = taxDue;
	}
}
