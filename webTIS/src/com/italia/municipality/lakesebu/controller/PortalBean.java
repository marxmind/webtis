package com.italia.municipality.lakesebu.controller;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.enm.Department;

@Named
@ViewScoped
public class PortalBean implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 456465876441L;
	private static final String PROPERTY_FILE = AppConf.PRIMARY_DRIVE.getValue() + AppConf.SEPERATOR.getValue() +
			AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() +	
			AppConf.APP_CONFIG_SETTING_FOLDER.getValue() + AppConf.SEPERATOR.getValue() + "office.tis";
	
	public static String getPortal(Department department) {
		
		Properties prop = new Properties();
		
		try{
			prop.load(new FileInputStream(PROPERTY_FILE));
			return prop.getProperty(department.getName());
		}catch(Exception e) {}	
		
		return "login.xhtml";
	}
	
	public String treas() {
		return getPortal(Department.TREASURER);
	}
	
}
