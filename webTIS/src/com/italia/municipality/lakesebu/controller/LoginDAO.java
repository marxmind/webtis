package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.italia.municipality.lakesebu.database.BankChequeDatabaseConnect;
import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;

public class LoginDAO {

	public static boolean validate(String unme, String pazzword){
		Connection conn = null;
		PreparedStatement ps = null;
		
		try{
			conn = WebTISDatabaseConnect.getConnection();
			ps = conn.prepareStatement("SELECT user_name, user_password FROM tbl_login WHERE user_name= ? AND user_password=?");
			ps.setString(1, unme);
			ps.setString(2, pazzword);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				//System.out.println(rs.getString("user_name"));
				//System.out.println(rs.getString("user_password"));
				return true;
			}
			
		}catch(SQLException sql){
			sql.printStackTrace();
			System.out.println("Login error>>>> ");
			return false;
		}finally{
			WebTISDatabaseConnect.close(conn);
		}
		return false;
	}
	public static void main(String[] args) {
		
		LoginDAO.validate("mark", "10181986");
		
	}
}
