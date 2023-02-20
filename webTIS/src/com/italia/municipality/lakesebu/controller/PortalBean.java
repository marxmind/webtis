package com.italia.municipality.lakesebu.controller;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.italia.municipality.lakesebu.bean.SessionBean;
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
	
	@PostConstruct
	public void init() {
		String val = ReadConfig.value(AppConf.SERVER_LOCAL);
        HttpSession session = SessionBean.getSession();
		session.setAttribute("server-local", val);
		session.setAttribute("theme", "saga");
		System.out.println("assigning local: " + val);
	}
	
	public static String getPortal(Department department) {
		
		Properties prop = new Properties();
		
		try{
			prop.load(new FileInputStream(PROPERTY_FILE));
			return prop.getProperty(department.getName());
		}catch(Exception e) {}	
		
		return "login.xhtml";
	}
	
	public String mayor() {
		return getPortal(Department.MAYOR);
	}
	
	public String vice() {
		return getPortal(Department.VICE_MAYOR);
	}
	
	public String treas() {
		return "login"; //getPortal(Department.TREASURER);
	}
	
	public String da() {
		return getPortal(Department.MAO);
	}
	
	public String licensing() {
		return getPortal(Department.LICENSING);
	}
	public String gso() {
		return getPortal(Department.GSO);
	}
	
	public String accounting() {
		return getPortal(Department.ACCOUNTING);
	}
	
	public String dilg() {
		return "dilg";//getPortal(Department.DILG);
	}
	
	public String personnel() {
		return "loginper";
	}
}
