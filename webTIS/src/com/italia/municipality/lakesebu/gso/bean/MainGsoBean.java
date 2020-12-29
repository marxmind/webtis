package com.italia.municipality.lakesebu.gso.bean;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class MainGsoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 17867565786970870L;
	
	private String searchParam;

	public String getSearchParam() {
		return searchParam;
	}

	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
	}
}
