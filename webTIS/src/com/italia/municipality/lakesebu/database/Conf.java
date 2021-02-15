package com.italia.municipality.lakesebu.database;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.security.SecureChar;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @since 10-15-2020
 * @version 1.0
 *
 */
@Setter
@Getter
public class Conf {

	private String CONFIG_FILE_NAME = "dbconf.max";
	private String APP_FOLDER = AppConf.PRIMARY_DRIVE.getValue() + File.separator + AppConf.APP_CONFIG_FOLDER_NAME.getValue() + File.separator + AppConf.APP_CONFIG_SETTING_FOLDER.getValue() + File.separator;
	private static volatile Conf conf;
	private String databaseBank;
	private String databaseLand;
	private String databaseMain;
	private String databaseLicensing;
	private String databaseAgriculture;
	private String databaseCashBook;
	private String databasePort;
	private String databaseUrl;
	private String databaseDriver;
	private String databaseSSL;
	private String databaseTimeZone;
	private String databaseUserName;
	private String databasePassword;
	
	private Conf() {
		System.out.println("initializing database information...");
	}
	
	public static Conf getInstance() {
		
		if(conf == null) {
			synchronized(Conf.class) {
				if(conf ==  null) {
					conf = new Conf();
					conf.readConf();//reading configuration on dbconf
					System.out.println("reading database information");
				}
			}
		}
		
		return conf;
	}
	
	private void readConf() {
		System.out.println("File: " + APP_FOLDER + CONFIG_FILE_NAME);
		try {
			//URL path = ConnectDB.class.getResource(CONFIG_FILE_NAME);
			
			File file = new File(APP_FOLDER + CONFIG_FILE_NAME);
			Properties prop = new Properties();
			prop.load(new FileInputStream(file));
			
			String u_name = SecureChar.decode(prop.getProperty("DATABASE_UNAME"));
			   u_name = u_name.replaceAll("mark", "");
			   u_name = u_name.replaceAll("rivera", "");
			   u_name = u_name.replaceAll("italia", "");
			String pword =  SecureChar.decode(prop.getProperty("DATABASE_PASSWORD"));
			   pword = pword.replaceAll("mark", "");
			   pword = pword.replaceAll("rivera", "");
			   pword = pword.replaceAll("italia", "");   
			conf.setDatabaseBank(prop.getProperty("DATABASE_NAME_BANK"));
			conf.setDatabaseLand(prop.getProperty("DATABASE_NAME_LAND"));
			conf.setDatabaseMain(prop.getProperty("DATABASE_NAME_MAIN"));
			conf.setDatabaseLicensing(prop.getProperty("DATABASE_NAME_LICENSING"));
			conf.setDatabaseAgriculture(prop.getProperty("DATABASE_NAME_AGRICULTURE"));
			conf.setDatabaseCashBook(prop.getProperty("DATABASE_NAME_CASHBOOK"));
			conf.setDatabaseDriver(prop.getProperty("DATABASE_DRIVER"));
			conf.setDatabaseUrl(prop.getProperty("DATABASE_URL"));
			conf.setDatabasePort(prop.getProperty("DATABASE_PORT"));
			conf.setDatabaseSSL(prop.getProperty("DATABASE_SSL"));
			conf.setDatabaseTimeZone(prop.getProperty("DATABASE_SERVER_TIME_ZONE"));
			conf.setDatabaseUserName(u_name);
			conf.setDatabasePassword(pword);
			
		}catch(Exception e) {
			System.out.println("Configuration file was not set. See error: " + e.getMessage());
		}
	}

	
	
}
