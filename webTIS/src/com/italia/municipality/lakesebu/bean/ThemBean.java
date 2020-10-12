package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.controller.ReadConfig;
import com.italia.municipality.lakesebu.enm.AppConf;

/**
 * 
 * @author mark italia
 * @since 04/09/2017
 * @version 1.0
 *
 */

@Named
@SessionScoped
public class ThemBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 658674386854751L;

	public String getApplicationTheme(){
		System.out.println("Appliying theme....");
		String value = "none";
		try{value = ReadConfig.value(AppConf.THEME_STYLE);
		System.out.println("Theme "+ value + " has been applied....");
		}catch(Exception e){}
		return value;
	}
	
}
