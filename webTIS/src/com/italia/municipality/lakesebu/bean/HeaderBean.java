package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.UserDtls;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 10/15/1986
 *
 */

@ManagedBean(name="headerBean", eager=true)
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
