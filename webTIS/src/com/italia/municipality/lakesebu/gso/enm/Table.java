package com.italia.municipality.lakesebu.gso.enm;

public enum Table {
	
	ACCESSLEVEL("useraccesslevel"),
	LOGIN("login"),
	USERDTLS("userdtls"),
	JOB("jobtitle"),
	DEPARTMENT("department"),
	ITEMS("items");
	
	private String name;
	
	private Table(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
