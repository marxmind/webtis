package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.UserDtls;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 10/15/1986
 *
 */
@Named
@ViewScoped
public class HeaderBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 452333333322121L;
	
	
	public UserDtls getUser() {
		System.out.println("Get user.....");
		return Login.getUserLogin().getUserDtls();
	}

}
