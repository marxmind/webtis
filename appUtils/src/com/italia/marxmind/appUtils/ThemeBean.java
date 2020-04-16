package com.italia.marxmind.appUtils;

import javax.servlet.http.HttpSession;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
/**
 * 
 * @author mark italia
 * @since 04/09/2017
 * @version 1.0
 *
 */
@Named
@SessionScoped
public class ThemeBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 147868854437557L;

	public String getApplicationTheme(){
	 
		String theme = "nova-colored";
		System.out.println("Applying theme...");
		try{
			HttpSession session = SessionBean.getSession();
			theme = session.getAttribute("theme").toString();
			System.out.println("Theme " + theme + " has been applied...");
		}catch(Exception e){}
		return theme;
	}
	
}
