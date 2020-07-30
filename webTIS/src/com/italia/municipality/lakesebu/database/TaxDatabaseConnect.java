package com.italia.municipality.lakesebu.database;

import java.sql.Connection;
import java.sql.DriverManager;

import com.italia.municipality.lakesebu.controller.ReadConfig;
import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.security.SecureChar;

/**
 * 
 * @author mark italia
 * @since 11/18/2016
 * @version 1.0
 *
 */
public class TaxDatabaseConnect {

	public static Connection getConnection(){
		Connection conn = null;
		try{
			
			String driver = ReadConfig.value(AppConf.DB_DRIVER);
			       //driver = SecureChar.decode(driver);
			Class.forName(driver);
			String db_url = ReadConfig.value(AppConf.DB_URL);
				   //db_url = SecureChar.decode(db_url);
			String port = ReadConfig.value(AppConf.DB_PORT);
			       //port = SecureChar.decode(port);
			String dbName = ReadConfig.value(AppConf.DB_NAME_TAX);
				  // dbName = SecureChar.decode(dbName);
			String url = db_url + ":" + port + "/" + dbName + "?serverTimezone=UTC&" + ReadConfig.value(AppConf.DB_SSL);
			String u_name = ReadConfig.value(AppConf.USER_NAME);
				   u_name = SecureChar.decode(u_name);
			String pword = ReadConfig.value(AppConf.USER_PASS);
				   pword = SecureChar.decode(pword);
				   pword = pword.replace("mark", "");
				   pword = pword.replace("italia", "");
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

