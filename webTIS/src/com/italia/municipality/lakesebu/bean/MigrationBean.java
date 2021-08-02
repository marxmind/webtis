package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.controller.Customer2;
import com.italia.municipality.lakesebu.licensing.controller.Customer;

import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
@Setter
@Getter
public class MigrationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2324556571L;
	
	private String searchName;
	private List<Customer> customer1;
	private List<Customer2> customer2;
	
	@PostConstruct
	public void init() {
		
		customer1 = new ArrayList<Customer>();
		customer2 = new ArrayList<Customer2>();
		String sql = "";
		String[] params = new String[0];
		if(getSearchName()!=null && getSearchName().length()>0) {
			
			sql = " AND (cus.fullname like '%"+ getSearchName() +"%' OR ";
			sql += " cus.cusfirstname like '%"+ getSearchName() +"%' OR ";
			sql += " cus.cusmiddlename like '%"+ getSearchName() +"%' OR ";
			sql += " cus.cuslastname like '%"+ getSearchName() +"%'";
			sql +=")";
			customer1 = Customer.retrieve(sql, params);
			
			sql = " AND cus.fullname like '%"+ getSearchName() +"%'";
			customer2 = Customer2.retrieve(sql, params);
			
		}
		
	}
	
}
