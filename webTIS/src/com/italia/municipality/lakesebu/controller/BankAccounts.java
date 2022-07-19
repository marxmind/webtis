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
	
	public static List<BankAccounts> retrieveAll(){
		List<BankAccounts> cList =  new ArrayList<BankAccounts>();
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement("SELECT * FROM tbl_bankaccounts ORDER BY bank_account_name");
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			BankAccounts ac = BankAccounts.builder()
					.bankId(rs.getInt("bank_id"))
					.bankAccntName(rs.getString("bank_account_name"))
					.bankAccntNo(rs.getString("bank_account_no"))
					.bankAccntBranch(rs.getString("bank_branch"))
					.build();
			
			cList.add(ac);
		}
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException sl){}
		
		return cList;
	}
	
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
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			BankAccounts ac = BankAccounts.builder()
					.bankId(rs.getInt("bank_id"))
					.bankAccntName(rs.getString("bank_account_name"))
					.bankAccntNo(rs.getString("bank_account_no"))
					.bankAccntBranch(rs.getString("bank_branch"))
					.build();
			
			cList.add(ac);
		}
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException sl){}
		
		return cList;
	}
	
}
