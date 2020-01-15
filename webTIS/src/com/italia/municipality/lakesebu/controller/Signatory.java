package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.italia.municipality.lakesebu.database.BankChequeDatabaseConnect;

/**
 * 
 * Please use the new Class
 * {@link}Signatories.class
 *
 */
@Deprecated
public class Signatory {

	private int sigId;
	private String sigName;
	private String sigPosition;
	private String sigCreated;
	
	public Signatory(){}
	
	public Signatory(
			int sigId,
			String sigName,
			String sigPosition,
			String sigCreated
			){
		this.sigId = sigId;
		this.sigName = sigName;
		this.sigPosition = sigPosition;
		this.sigCreated = sigCreated;
	}
	
	public static List<Signatory> retrieve(String sql, String[] params){
		List<Signatory> sigs =  Collections.synchronizedList(new ArrayList<Signatory>());
		
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
			
			Signatory sig = new Signatory();
			try{sig.setSigId(rs.getInt("sig_id"));}catch(NullPointerException e){}
			try{sig.setSigName(rs.getString("sig_name"));}catch(NullPointerException e){}
			try{sig.setSigPosition(rs.getString("sig_position"));}catch(NullPointerException e){}
			try{sig.setSigCreated(rs.getString("sig_created"));}catch(NullPointerException e){}
			sigs.add(sig);
			
		}
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException sl){}
		
		return sigs;
	}
	
	public static Map<String, Signatory> retrieveSig(String sql, String[] params){
		Map<String,Signatory> sigs = java.util.Collections.synchronizedMap(new HashMap<String,Signatory>());
		
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
			
			Signatory sig = new Signatory();
			try{sig.setSigId(rs.getInt("sig_id"));}catch(NullPointerException e){}
			try{sig.setSigName(rs.getString("sig_name"));}catch(NullPointerException e){}
			try{sig.setSigPosition(rs.getString("sig_position"));}catch(NullPointerException e){}
			try{sig.setSigCreated(rs.getString("sig_created"));}catch(NullPointerException e){}
			sigs.put(sig.getSigId()+"", sig);
			
		}
		rs.close();
		ps.close();
		BankChequeDatabaseConnect.close(conn);
		}catch(SQLException sl){}
		
		return sigs;
	}
	
	public int getSigId() {
		return sigId;
	}
	public void setSigId(int sigId) {
		this.sigId = sigId;
	}
	public String getSigName() {
		return sigName;
	}
	public void setSigName(String sigName) {
		this.sigName = sigName;
	}
	
	public String getSigCreated() {
		return sigCreated;
	}
	public void setSigCreated(String sigCreated) {
		this.sigCreated = sigCreated;
	}

	public String getSigPosition() {
		return sigPosition;
	}

	public void setSigPosition(String sigPosition) {
		this.sigPosition = sigPosition;
	}
	
	
	
	
	
}
