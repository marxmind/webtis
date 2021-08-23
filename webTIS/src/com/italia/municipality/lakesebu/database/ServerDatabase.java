package com.italia.municipality.lakesebu.database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 
 * @author Mark Italia
 * This is for server connection
 *
 */
public class ServerDatabase {

	
	public static Connection getConnection(){
		Conf conf = Conf.getInstance();
		Connection conn = null;
		try{
			
			String driver = conf.getDatabaseDriver();
			Class.forName(driver);
			String url =  conf.getServerDatabase();
			System.out.println("URL DATA: " + url);
			String u_name = conf.getDatabaseUserName();
			String pword = conf.getDatabasePassword();
			conn = DriverManager.getConnection(url, u_name, pword);
			return conn;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static void close(Connection conn){
		try{
			if(conn!=null){
				conn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		
		BankChequeDatabaseConnect dt = new BankChequeDatabaseConnect();
		Connection s = dt.getConnection();
		
	}

}
