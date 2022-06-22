package com.italia.municipality.lakesebu.controller;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.italia.municipality.lakesebu.database.BankChequeDatabaseConnect;
import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.reports.ReportCompiler;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
/**
 * 
 * @author mark italia
 * @since 11/12/2013
 * @version 1.0
 */

public class Chequedtls {

	private long cheque_id;
	private String accntNumber;
	private String checkNo;
	private String date_disbursement;
	private String bankName;
	private String accntName;
	private double amount;
	private String payToTheOrderOf;
	private String amountInWOrds;
	private int signatory1;
	private int signatory2;
	private String processBy;
	private String date_edited;
	private String date_created;
	private int isActive;
	private int status;
	private String remarks;
	private int hasAdvice;
	private String signatoryName1;
	private String signatoryName2;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static String grandTotal;
	
	private String statusName;
	private int fundTypeId;
	
	public Chequedtls(){}
	
	public Chequedtls(
			long cheque_id,
			String accntNumber,
			String checkNo,
			String date_disbursement,
			String bankName,
			String accntName,
			double amount,
			String payToTheOrderOf,
			String amountInWOrds,
			int signatory1,
			int signatory2,
			String processBy,
			String date_edited,
			int isActive,
			int status,
			String remarks
			){
		this.cheque_id = cheque_id;
		this.accntNumber = accntNumber; 
		this.checkNo = checkNo; 
		this.date_disbursement = date_disbursement;
		this.bankName = bankName;
		this.accntName = accntName;
		this.amount = amount;
		this.payToTheOrderOf = payToTheOrderOf;
		this.amountInWOrds = amountInWOrds;
		this.signatory1 = signatory1;
		this.signatory2 = signatory2;
		this.processBy = processBy;
		this.date_edited = date_edited;
		this.isActive = isActive;
		this.status = status;
		this.remarks = remarks;
	}
	
	public static String formatAmount(String amount){
		double money = Double.valueOf(amount);
		NumberFormat format = NumberFormat.getCurrencyInstance();
		amount = format.format(money).replace("$", "");
		amount = amount.replace("Php", "");
		return amount;
	}
	
	public static List<Chequedtls> retrieveCheckOnly(String sql, String[] params){
		List<Chequedtls> cList =  new ArrayList<Chequedtls>();
		
		
		String stm = "SELECT "
				+ "	d.cheque_id,d.date_disbursement,d.bank_name,d.cheque_no,d.cheque_amount,d.hasadvice,d.chkstatus,d.chkremarks,d.pay_to_the_order_of,"
				+ "	b.bank_account_name,b.bank_account_no,b.bank_id "
				+ "FROM tbl_chequedtls d,tbl_bankaccounts b "
				+ "	WHERE d.accnt_no=b.bank_account_no AND d.isactive=1 ";
		
		sql = stm + sql;
		
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
		
		System.out.println("CHECK SQL " + ps.toString());
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			Chequedtls chk = new Chequedtls();
			try{chk.setDate_disbursement(rs.getString("date_disbursement"));}catch(NullPointerException e){}
			try{chk.setCheque_id(rs.getLong("cheque_id"));}catch(NullPointerException e){}
			try{chk.setCheckNo(rs.getString("cheque_no"));}catch(NullPointerException e){}
			try{chk.setBankName(rs.getString("bank_name"));}catch(NullPointerException e){}
			try{chk.setAmount(rs.getDouble("cheque_amount"));}catch(NullPointerException e){}
			try{chk.setPayToTheOrderOf(rs.getString("pay_to_the_order_of"));}catch(NullPointerException e){}
			try{chk.setHasAdvice(rs.getInt("hasadvice"));}catch(NullPointerException e){}
			try{chk.setStatus(rs.getInt("chkstatus"));}catch(NullPointerException e){}
			try{chk.setRemarks(rs.getString("chkremarks"));}catch(NullPointerException e){}
			try{chk.setAccntNumber(rs.getString("bank_account_no"));}catch(NullPointerException e){}
			try{chk.setAccntName(rs.getString("bank_account_name"));}catch(NullPointerException e){}
			try{chk.setStatusName(rs.getInt("chkstatus")==1? "FOR ADVICE" : "CANCELLED");}catch(NullPointerException e){}
			try{chk.setFundTypeId(rs.getInt("bank_id"));}catch(NullPointerException e){}
			
			cList.add(chk);
		}
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException sl){sl.getMessage();}
		
		return cList;
	}
	
	/*
	public static Chequedtls retrieveCheck(long id){
		Chequedtls chk = new Chequedtls();
		String sql = "SELECT "
				+ "	d.cheque_id,d.date_disbursement,d.bank_name,d.cheque_no,d.cheque_amount,d.hasadvice,d.chkstatus,d.chkremarks,d.pay_to_the_order_of,"
				+ "	b.bank_account_name,b.bank_account_no,b.bank_id,"
				+ "	(SELECT s.sig_name FROM tbl_signatory s WHERE s.sig_id=d.sig1_id) as sig1_id,"
				+ "	(SELECT s.sig_name FROM tbl_signatory s WHERE s.sig_id=d.sig2_id) as sig2_id "
				+ "FROM tbl_chequedtls d,tbl_bankaccounts b "
				+ "	WHERE d.accnt_no=b.bank_id AND d.isactive=1 AND d.cheque_id=" + id;
		
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		
		System.out.println("CHECK SQL " + ps.toString());
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			try{chk.setDate_disbursement(rs.getString("date_disbursement"));}catch(NullPointerException e){}
			try{chk.setCheque_id(rs.getLong("cheque_id"));}catch(NullPointerException e){}
			try{chk.setCheckNo(rs.getString("cheque_no"));}catch(NullPointerException e){}
			try{chk.setBankName(rs.getString("bank_name"));}catch(NullPointerException e){}
			try{chk.setAmount(rs.getDouble("cheque_amount"));}catch(NullPointerException e){}
			try{chk.setPayToTheOrderOf(rs.getString("pay_to_the_order_of"));}catch(NullPointerException e){}
			try{chk.setHasAdvice(rs.getInt("hasadvice"));}catch(NullPointerException e){}
			try{chk.setStatus(rs.getInt("chkstatus"));}catch(NullPointerException e){}
			try{chk.setRemarks(rs.getString("chkremarks"));}catch(NullPointerException e){}
			try{chk.setAccntNumber(rs.getString("bank_account_no"));}catch(NullPointerException e){}
			try{chk.setAccntName(rs.getString("bank_account_name"));}catch(NullPointerException e){}
			try{chk.setSignatoryName1(rs.getString("sig1_id"));}catch(NullPointerException e){}
			try{chk.setSignatoryName2(rs.getString("sig2_id"));}catch(NullPointerException e){}
			try{chk.setStatusName(rs.getInt("chkstatus")==1? "FOR ADVICE" : "CANCELLED");}catch(NullPointerException e){}
			try{chk.setFundTypeId(rs.getInt("bank_id"));}catch(NullPointerException e){}
			
			
		}
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException sl){sl.getMessage();}
		
		return chk;
	}*/
	
	public static List<Chequedtls> retrieveChecks(String sql, String[] params){
		List<Chequedtls> cList =  new ArrayList<Chequedtls>();
		
		
		String stm = "SELECT "
				+ "	d.cheque_id,d.date_disbursement,d.bank_name,d.cheque_no,d.cheque_amount,d.hasadvice,d.chkstatus,d.chkremarks,d.pay_to_the_order_of,"
				+ "	b.bank_account_name,b.bank_account_no,b.bank_id,"
				+ "	(SELECT s.sig_name FROM tbl_signatory s WHERE s.sig_id=d.sig1_id) as sig1_id,"
				+ "	(SELECT s.sig_name FROM tbl_signatory s WHERE s.sig_id=d.sig2_id) as sig2_id "
				+ "FROM tbl_chequedtls d,tbl_bankaccounts b "
				+ "	WHERE d.accnt_no=b.bank_account_no AND d.isactive=1 ";
		
		sql = stm + sql;
		
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
		
		System.out.println("CHECK SQL " + ps.toString());
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			Chequedtls chk = new Chequedtls();
			try{chk.setDate_disbursement(rs.getString("date_disbursement"));}catch(NullPointerException e){}
			try{chk.setCheque_id(rs.getLong("cheque_id"));}catch(NullPointerException e){}
			try{chk.setCheckNo(rs.getString("cheque_no"));}catch(NullPointerException e){}
			try{chk.setBankName(rs.getString("bank_name"));}catch(NullPointerException e){}
			try{chk.setAmount(rs.getDouble("cheque_amount"));}catch(NullPointerException e){}
			try{chk.setPayToTheOrderOf(rs.getString("pay_to_the_order_of"));}catch(NullPointerException e){}
			try{chk.setHasAdvice(rs.getInt("hasadvice"));}catch(NullPointerException e){}
			try{chk.setStatus(rs.getInt("chkstatus"));}catch(NullPointerException e){}
			try{chk.setRemarks(rs.getString("chkremarks"));}catch(NullPointerException e){}
			try{chk.setAccntNumber(rs.getString("bank_account_no"));}catch(NullPointerException e){}
			try{chk.setAccntName(rs.getString("bank_account_name"));}catch(NullPointerException e){}
			try{chk.setSignatoryName1(rs.getString("sig1_id"));}catch(NullPointerException e){}
			try{chk.setSignatoryName2(rs.getString("sig2_id"));}catch(NullPointerException e){}
			try{chk.setStatusName(rs.getInt("chkstatus")==1? "FOR ADVICE" : "CANCELLED");}catch(NullPointerException e){}
			try{chk.setFundTypeId(rs.getInt("bank_id"));}catch(NullPointerException e){}
			
			cList.add(chk);
		}
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException sl){sl.getMessage();}
		
		return cList;
	}
	
	public static List<Chequedtls> retrieve(String sql, String[] params){
		List<Chequedtls> cList =  new ArrayList<Chequedtls>();
		
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
		
		System.out.println("CHECK SQL " + ps.toString());
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			Chequedtls chk = new Chequedtls();
			try{chk.setCheque_id(rs.getLong("cheque_id"));}catch(NullPointerException e){}
			try{chk.setCheckNo(rs.getString("cheque_no"));}catch(NullPointerException e){}
			try{chk.setAccntNumber(rs.getString("accnt_no"));}catch(NullPointerException e){}
			try{chk.setAccntName(rs.getString("accnt_name"));}catch(NullPointerException e){}
			try{chk.setBankName(rs.getString("bank_name"));}catch(NullPointerException e){}
			try{chk.setDate_disbursement(rs.getString("date_disbursement"));}catch(NullPointerException e){}
			try{chk.setAmount(rs.getDouble("cheque_amount"));}catch(NullPointerException e){}
			try{chk.setPayToTheOrderOf(rs.getString("pay_to_the_order_of"));}catch(NullPointerException e){}
			try{chk.setAmountInWOrds(rs.getString("amount_in_words"));}catch(NullPointerException e){}
			try{chk.setProcessBy(rs.getString("proc_by"));}catch(NullPointerException e){}
			try{chk.setDate_created(timeStamp(rs.getTimestamp("date_created")+""));}catch(NullPointerException e){}
			try{chk.setDate_edited(timeStamp(rs.getTimestamp("date_edited")+""));}catch(NullPointerException e){}
			try{chk.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			try{chk.setStatus(rs.getInt("chkstatus"));}catch(NullPointerException e){}
			try{chk.setRemarks(rs.getString("chkremarks"));}catch(NullPointerException e){}
			try{chk.setHasAdvice(rs.getInt("hasadvice"));}catch(NullPointerException e){}
			int sig1=0,sig2=0;
			
			
			try{
				sig1=rs.getInt("sig1_id");
				chk.setSignatory1(sig1);}catch(NullPointerException e){}
			try{
				sig2=rs.getInt("sig2_id");
				chk.setSignatory2(sig2);}catch(NullPointerException e){}
			
			sql = "SELECT * FROM tbl_signatory WHERE sig_id=?";
			params = new String[1];
			params[0] = sig1+"";
			try{chk.setSignatoryName1(Signatory.retrieve(sql, params).get(0).getSigName());}catch(Exception e){}
			params[0] = sig2+"";
			try{chk.setSignatoryName2(Signatory.retrieve(sql, params).get(0).getSigName());}catch(Exception e){}
			
			cList.add(chk);
		}
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException sl){sl.getMessage();}
		
		return cList;
	}
	
	/**
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static BigDecimal sum(String sql, String[] params,String fieldName){
		Connection conn = null;
		ResultSet rs = null;
		BigDecimal amnt = new BigDecimal("0.00"); 
		PreparedStatement ps = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		System.out.println("Budget SQL: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			amnt = rs.getBigDecimal(fieldName);
		}
		
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException e){}
		
		return amnt;
	}
	
	private static String timeStamp(String timestamp){
		try{
			timestamp = timestamp.split("\\.")[0];
			//System.out.println("new timeStamp " + timestamp);
			String time = "", date="";
			date = timestamp.split(" ")[0];
			time = timestamp.split(" ")[1];
			//System.out.println("new time " + time);
			return date + " " + DateUtils.timeTo12Format(time,true);
		}catch(Exception e){}
		
		return "error";
	}
	
	
	
	
	public static List<String> retrievePayOrderOf(String sql, String[] params){
		List<Chequedtls> cList =  Collections.synchronizedList(new ArrayList<Chequedtls>());
		List<String> results = new ArrayList<String>();
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
		
		//System.out.println("SQL " + ps.toString());
		
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			//Chequedtls chk = new Chequedtls();
			//try{chk.setPayToTheOrderOf(rs.getString("pay_to_the_order_of"));}catch(NullPointerException e){}
			results.add(rs.getString("pay_to_the_order_of"));
			//cList.add(chk);
		}
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException sl){}
		
		return results;
	}
	
	public static Chequedtls save(Chequedtls chk){
		if(chk!=null){
			long id = getInfo(chk.getCheque_id()==0? getLatestId()+1 : chk.getCheque_id());
			if(id==1){
				chk = Chequedtls.insertData(chk, "1");
			}else if(id==2){
				chk = Chequedtls.updateData(chk);
			}else if(id==3){
				chk = Chequedtls.insertData(chk, "3");
			}
			
		}
		return chk;
	}
	
	public void save(){
			long id = getInfo(getCheque_id()==0? getLatestId()+1 : getCheque_id());
			if(id==1){
				insertData("1");
			}else if(id==2){
				updateData();
			}else if(id==3){
				insertData("3");
			}
	}
	
	public static Chequedtls insertData(Chequedtls chk, String type){
		String sql = "INSERT INTO tbl_chequedtls ("
				+ "cheque_id,"
				+ "accnt_no,"
				+ "cheque_no,"
				+ "accnt_name,"
				+ "bank_name,"
				+ "date_disbursement,"
				+ "cheque_amount,"
				+ "pay_to_the_order_of,"
				+ "amount_in_words,"
				+ "proc_by,"
				+ "sig1_id,"
				+ "sig2_id,"
				+ "date_edited,"
				+ "isactive,"
				+ "chkstatus,"
				+ "chkremarks,"
				+ "hasadvice) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			chk.setCheque_id(id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			chk.setCheque_id(id);
		}
		ps.setString(2, chk.getAccntNumber());
		ps.setString(3, chk.getCheckNo());
		ps.setString(4, chk.getAccntName());
		ps.setString(5, chk.getBankName());
		ps.setString(6, chk.getDate_disbursement());
		ps.setDouble(7, chk.getAmount());
		ps.setString(8, chk.getPayToTheOrderOf());
		ps.setString(9, chk.getAmountInWOrds());
		ps.setString(10, chk.getProcessBy());
		ps.setInt(11, chk.getSignatory1());
		ps.setInt(12, chk.getSignatory2());
		ps.setTimestamp(13, Timestamp.valueOf(chk.getDate_edited()));
		ps.setInt(14, chk.getIsActive());
		ps.setInt(15, chk.getStatus());
		ps.setString(16, chk.getRemarks());
		ps.setInt(17, chk.getHasAdvice());
		ps.execute();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
		return chk;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO tbl_chequedtls ("
				+ "cheque_id,"
				+ "accnt_no,"
				+ "cheque_no,"
				+ "accnt_name,"
				+ "bank_name,"
				+ "date_disbursement,"
				+ "cheque_amount,"
				+ "pay_to_the_order_of,"
				+ "amount_in_words,"
				+ "proc_by,"
				+ "sig1_id,"
				+ "sig2_id,"
				+ "date_edited,"
				+ "isactive,"
				+ "chkstatus,"
				+ "chkremarks,"
				+ "hasadvice) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setCheque_id(id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setCheque_id(id);
		}
		ps.setString(2, getAccntNumber());
		ps.setString(3, getCheckNo());
		ps.setString(4, getAccntName());
		ps.setString(5, getBankName());
		ps.setString(6, getDate_disbursement());
		ps.setDouble(7, getAmount());
		ps.setString(8, getPayToTheOrderOf());
		ps.setString(9, getAmountInWOrds());
		ps.setString(10, getProcessBy());
		ps.setInt(11, getSignatory1());
		ps.setInt(12, getSignatory2());
		ps.setTimestamp(13, Timestamp.valueOf(getDate_edited()));
		ps.setInt(14, getIsActive());
		ps.setInt(15, getStatus());
		ps.setString(16, getRemarks());
		ps.setInt(17, hasAdvice);
		ps.execute();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
	}
	
	public static Chequedtls updateData(Chequedtls chk){
		String sql = "UPDATE tbl_chequedtls SET "
				+ "accnt_no=?,"
				+ "cheque_no=?,"
				+ "accnt_name=?,"
				+ "bank_name=?,"
				+ "date_disbursement=?,"
				+ "cheque_amount=?,"
				+ "pay_to_the_order_of=?,"
				+ "amount_in_words=?,"
				+ "proc_by=?,"
				+ "sig1_id=?,"
				+ "sig2_id=?,"
				+ "chkstatus=?,"
				+ "chkremarks=?,"
				+ "hasadvice=? " 
				+ " WHERE cheque_id=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, chk.getAccntNumber());
		ps.setString(2, chk.getCheckNo());
		ps.setString(3, chk.getAccntName());
		ps.setString(4, chk.getBankName());
		ps.setString(5, chk.getDate_disbursement());
		ps.setDouble(6, chk.getAmount());
		ps.setString(7, chk.getPayToTheOrderOf());
		ps.setString(8, chk.getAmountInWOrds());
		ps.setString(9, chk.getProcessBy());
		ps.setInt(10, chk.getSignatory1());
		ps.setInt(11, chk.getSignatory2());
		ps.setInt(12, chk.getStatus());
		ps.setString(13, chk.getRemarks());
		ps.setInt(14, chk.getHasAdvice());
		ps.setLong(15, chk.getCheque_id());
		
		ps.executeUpdate();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
		return chk;
	}
	
	public void updateData(){
		String sql = "UPDATE tbl_chequedtls SET "
				+ "accnt_no=?,"
				+ "cheque_no=?,"
				+ "accnt_name=?,"
				+ "bank_name=?,"
				+ "date_disbursement=?,"
				+ "cheque_amount=?,"
				+ "pay_to_the_order_of=?,"
				+ "amount_in_words=?,"
				+ "proc_by=?,"
				+ "sig1_id=?,"
				+ "sig2_id=?,"
				+ "chkstatus=?,"
				+ "chkremarks=?,"
				+ "hasadvice=? " 
				+ " WHERE cheque_id=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, getAccntNumber());
		ps.setString(2, getCheckNo());
		ps.setString(3, getAccntName());
		ps.setString(4, getBankName());
		ps.setString(5, getDate_disbursement());
		ps.setDouble(6, getAmount());
		ps.setString(7, getPayToTheOrderOf());
		ps.setString(8, getAmountInWOrds());
		ps.setString(9, getProcessBy());
		ps.setInt(10, getSignatory1());
		ps.setInt(11, getSignatory2());
		ps.setInt(12, getStatus());
		ps.setString(13, getRemarks());
		ps.setInt(14, getHasAdvice());
		ps.setLong(15, getCheque_id());
		
		ps.executeUpdate();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT cheque_id FROM tbl_chequedtls  ORDER BY cheque_id DESC LIMIT 1";	
		conn = BankChequeDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("cheque_id");
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
		ps = conn.prepareStatement("SELECT cheque_id FROM tbl_chequedtls WHERE cheque_id=?");
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
		}catch(SQLException s){
			s.getMessage();
		}
		
	}
	
	public void delete(){
		String sql = "update tbl_chequedtls set isactive=0, chkstatus=0,chkremarks='VOID' WHERE cheque_id=?";
		String params[] = new String[1];
		params[0] = getCheque_id()+"";
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
		}catch(SQLException s){
			s.getMessage();
		}
		
	}
	
	public long getCheque_id() {
		return cheque_id;
	}
	public void setCheque_id(long cheque_id) {
		this.cheque_id = cheque_id;
	}
	public String getAccntNumber() {
		return accntNumber;
	}
	public void setAccntNumber(String accntNumber) {
		this.accntNumber = accntNumber;
	}
	public String getCheckNo() {
		return checkNo;
	}
	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}
	public String getDate_disbursement() {
		return date_disbursement;
	}
	public void setDate_disbursement(String date_disbursement) {
		this.date_disbursement = date_disbursement;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAccntName() {
		return accntName;
	}
	public void setAccntName(String accntName) {
		this.accntName = accntName;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getPayToTheOrderOf() {
		return payToTheOrderOf;
	}
	public void setPayToTheOrderOf(String payToTheOrderOf) {
		this.payToTheOrderOf = payToTheOrderOf;
	}
	public String getAmountInWOrds() {
		return amountInWOrds;
	}
	public void setAmountInWOrds(String amountInWOrds) {
		this.amountInWOrds = amountInWOrds;
	}
	public int getSignatory1() {
		return signatory1;
	}
	public void setSignatory1(int signatory1) {
		this.signatory1 = signatory1;
	}
	public int getSignatory2() {
		return signatory2;
	}
	public void setSignatory2(int signatory2) {
		this.signatory2 = signatory2;
	}
	public String getProcessBy() {
		return processBy;
	}
	public void setProcessBy(String processBy) {
		this.processBy = processBy;
	}
	public String getDate_edited() {
		return date_edited;
	}
	/*
	 * set this if saving data in first time
	 * this setter is date_created just ignore the name
	 */
	
	public void setDate_edited(String date_edited) {
		this.date_edited = date_edited;
	}
	public String getDate_created() {
		return date_created;
	}
	
	/*
	 * set this if updating data
	 * this setter is date_edited just ignore the name
	 */
	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}
	
	public String getSignatoryName1() {
		return signatoryName1;
	}

	public void setSignatoryName1(String signatoryName1) {
		this.signatoryName1 = signatoryName1;
	}

	public String getSignatoryName2() {
		return signatoryName2;
	}

	public void setSignatoryName2(String signatoryName2) {
		this.signatoryName2 = signatoryName2;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	public static void compileReport(Chequedtls reportFields){
		try{
		String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
				AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
		String REPORT_NAME = ReadConfig.value(AppConf.CHEQUE_REPORT_NAME);
		String JRXMLFILE= ReadConfig.value(AppConf.CHEQUE_JRXML_FILE);
		
		System.out.println("CheckReport path: " + REPORT_PATH);
		HashMap paramMap = new HashMap();
		Chequedtls rpt = reportFields;
		ReportCompiler compiler = new ReportCompiler();
		System.out.println("REPORT_NAME: " +REPORT_NAME + " REPORT_PATH: " + REPORT_PATH);
		String jasperreportLocation = compiler.compileReport(JRXMLFILE, REPORT_NAME, REPORT_PATH);
		System.out.println("Check report path: " + jasperreportLocation);
		HashMap params = new HashMap();
		
		params.put("PARAM_ACCOUNT_NUMBER", rpt.getAccntNumber());
		params.put("PARAM_CHECK_NUMBER", rpt.getCheckNo());
		params.put("PARAM_DATE_DISBURSEMENT", rpt.getDate_disbursement());
		params.put("PARAM_BANK_NAME", rpt.getBankName().toUpperCase());
		params.put("PARAM_ACCOUNT_NAME", rpt.getAccntName().toUpperCase());
		params.put("PARAM_AMOUNT", Currency.formatAmount(rpt.getAmount()));
		params.put("PARAM_PAYTOORDEROF", rpt.getPayToTheOrderOf().toUpperCase());
		
		params.put("PARAM_AMOUNT_INWORDS", rpt.getAmountInWOrds().toUpperCase());
		
		String sql = "select * from tbl_signatory";
		Map<String, Signatory> sigs = Signatory.retrieveSig(sql, new String[0]);
		
		params.put("PARAM_SIGNATORY1", sigs.get(rpt.getSignatory1()+"").getSigName());
		params.put("PARAM_SIGNATORY2", sigs.get(rpt.getSignatory2()+"").getSigName());
		
		
		
		JasperPrint print = compiler.report(jasperreportLocation, params);
		File pdf = null;
		
		pdf = new File(REPORT_PATH+REPORT_NAME+".pdf");
		pdf.createNewFile();
		JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(pdf));
		System.out.println("pdf successfully created...");
		System.out.println("Creating a backup copy....");
		pdf = new File(REPORT_PATH+REPORT_NAME+"_copy"+".pdf");
		pdf.createNewFile();
		JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(pdf));
		
		
		/*String jrxmlFile = compiler.compileReport(JRXMLFILE, REPORT_NAME, REPORT_PATH);
		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, params);
		JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");*/
		
		System.out.println("Done creating a backup copy....");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		
	}
	
	public static void backupPrint(){
		
		String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
				AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
		String REPORT_NAME = AppConf.CHEQUE_REPORT_NAME.getValue();
		String REPORT_PATH_FILE = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() +  AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
		String dll = "rundll32 url.dll,FileProtocolHandler";
		String fileName = REPORT_PATH + REPORT_NAME + ".pdf"; 
		File file = new File(fileName);
		try{
			if(file.exists()){
				//Process process = Runtime.getRuntime().exec(dll + " " + fileName);
				Process process = Runtime.getRuntime().exec("cmd /c start /wait " + REPORT_PATH + "copyReport.bat");
				process = Runtime.getRuntime().exec(dll + " " + REPORT_PATH_FILE + REPORT_NAME + ".pdf");
				process.waitFor();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void close(Closeable resource) {
	    if (resource != null) {
	        try {
	            resource.close();
	        } catch (IOException e) {
	            // Do your thing with the exception. Print it, log it or mail it. It may be useful to 
	            // know that this will generally only be thrown when the client aborted the download.
	            e.printStackTrace();
	        }
	    }
}


	

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public static void main(String[] args) {
		
		Chequedtls chk = new Chequedtls();
		//chk.setCheque_id(4);
		chk.setAccntNumber("12345678");
		chk.setCheckNo("255476586");
		chk.setAccntName("TRUST FUND");
		chk.setBankName("BP Marbel");
		chk.setDate_disbursement(DateUtils.getCurrentDateMMMMDDYYYY());
		chk.setAmount(100.00);
		chk.setPayToTheOrderOf("Sodo");
		chk.setAmountInWOrds("One Hundred Pesos Only");
		chk.setProcessBy("Mark");
		chk.setSignatory1(1);
		chk.setSignatory2(2);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		chk.setDate_edited(dateFormat.format(date));
		chk.save();
		
		String sql = "SELECT * FROM tbl_chequedtls WHERE cheque_id=?";
		String[] params = new String[1];
		params[0] = "1";
		for(Chequedtls c : Chequedtls.retrieve(sql, params)){
			System.out.println(c.getBankName());
		}
		
	}

	public int getHasAdvice() {
		return hasAdvice;
	}

	public void setHasAdvice(int hasAdvice) {
		this.hasAdvice = hasAdvice;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public int getFundTypeId() {
		return fundTypeId;
	}

	public void setFundTypeId(int fundTypeId) {
		this.fundTypeId = fundTypeId;
	}

	
	
}
