package com.italia.municipality.lakesebu.gso.bean;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 04/09/2020
 *
 */
@Named
@ViewScoped
public class MenuGsoBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1887874342424L;
	
	public String home() {
		return "main";
	}
	
	public String upload() {
		return "upload";
	}

}
