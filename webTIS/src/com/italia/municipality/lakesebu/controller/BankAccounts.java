package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.municipality.lakesebu.database.BankChequeDatabaseConnect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccounts {

	private int bankId;
	private String bankAccntNo;
	private String bankAccntName;
	private String bankAccntBranch;
	private String timestamp;
	
	public static List<BankAccounts> retrieve(String sql, String[] params){
		List<BankAccounts> cList =  new ArrayList<BankAccounts>();//Collections.synchronizedList(new ArrayList<BankAccounts>());
		
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
			
			BankAccounts ac = new BankAccounts();
			
			try{ac.setBankId(rs.getInt("bank_id"));}catch(NullPointerException e){}
			try{ac.setBankAccntName(rs.getString("bank_account_name"));}catch(NullPointerException e){}
			try{ac.setBankAccntNo(rs.getString("bank_account_no"));}catch(NullPointerException e){}
			try{ac.setBankAccntBranch(rs.getString("bank_branch"));}catch(NullPointerException e){}
			
			cList.add(ac);
		}
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException sl){}
		
		return cList;
	}
	
	/*
	public int getBankId() {
		return bankId;
	}
	public void setBankId(int bankId) {
		this.bankId = bankId;
	}
	public String getBankAccntNo() {
		return bankAccntNo;
	}
	public void setBankAccntNo(String bankAccntNo) {
		this.bankAccntNo = bankAccntNo;
	}
	public String getBankAccntName() {
		return bankAccntName;
	}
	public void setBankAccntName(String bankAccntName) {
		this.bankAccntName = bankAccntName;
	}
	public String getBankAccntBranch() {
		return bankAccntBranch;
	}
	public void setBankAccntBranch(String bankAccntBranch) {
		this.bankAccntBranch = bankAccntBranch;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	*/
	
	
	
}
