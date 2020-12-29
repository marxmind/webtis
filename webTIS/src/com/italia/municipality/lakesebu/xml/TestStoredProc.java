package com.italia.municipality.lakesebu.xml;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import com.italia.municipality.lakesebu.controller.Barangay;
import com.italia.municipality.lakesebu.database.TaxDatabaseConnect;
import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;

public class TestStoredProc {
	
	public static void main(String[] args) {
		System.out.println("start...");
		//createProcedure();
		//retrieveLogin();
		validateLogin();		System.out.println("end..");
	}
	
	private static void createProcedure() {
		Connection conn = null;
		ResultSet rs = null;
		Statement st = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		st = conn.createStatement();
		String sql = "DROP PROCEDURE IF EXISTS retrieveData" + 
		"CREATE PROCEDURE 'retrieveData'(IN id bigint(20)) " +
		" BEGIN " +
				" SELECT * FROM login WHERE isactive=1 AND logid=id" +
		" END ";
		st.execute(sql);
		System.out.println("done creating procedure.....");
		st.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
	}
	
	
	private static void retrieveLogin() {
		Connection conn = null;
		ResultSet rs = null;
		CallableStatement ct = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ct = conn.prepareCall("{call retrieveLoginData(?)}");
		ct.setLong(1, 1);
		ct.execute();
		rs = ct.getResultSet();
		while(rs.next()) {
			System.out.println(rs.getString("username"));
		}
		rs.close();
		ct.close();
		System.out.println("end calling procedure....");
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
	}
	
	private static void validateLogin() {
		Connection conn = null;
		CallableStatement ct = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ct = conn.prepareCall("{call retrieveLoginValidateUser(?,?,?)}");
		ct.setString(1, "mark");
		ct.setString(2, "198618");
		ct.registerOutParameter(3, Types.INTEGER);
		ct.execute();
		System.out.println(ct.getInt(3));
		ct.close();
		System.out.println("end calling procedure....");
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
	}
	
}
