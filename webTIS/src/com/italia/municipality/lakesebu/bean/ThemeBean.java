package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author mark italia
 * @since 04/09/2017
 * @version 1.0
 *
 */

@Named
@ViewScoped
public class ThemeBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 658674386854751L;

	public String getApplicationTheme(){
		System.out.println("Appliying theme....");
		String value = "saga";
		try{
			//value = ReadConfig.value(AppConf.THEME_STYLE);
			HttpSession session = SessionBean.getSession();
			value = session.getAttribute("theme").toString();
		System.out.println("Theme "+ value + " has been applied....");
		}catch(Exception e){}
		return value;
	}
	
}
