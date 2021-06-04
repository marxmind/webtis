package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.controller.Customer2;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.licensing.controller.Customer;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author mark italia
 * @since 06/15/2018
 * @version 1.0
 */

@Named
@ViewScoped
@Setter
@Getter
public class CustomerBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 13244769657536L;
	
	private Date regDate;
	private String fullName;
	private String address;
	private String contactNo;
	private Customer customerSelected;
	private String searchName;
	private List<Customer> customers = Collections.synchronizedList(new ArrayList<Customer>());
	
	@PostConstruct
	public void init() {
		if(regDate==null) {
			regDate = DateUtils.getDateToday();
		}
		
	}
	
	public void loadCustomer() {
		customers = Collections.synchronizedList(new ArrayList<Customer>());
		String sql = "";
		String[] params = new String[0];
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
			sql += " AND cus.fullname like '%"+ getSearchName().replace("--", "") +"%'";
		}
		customers = Customer.retrieve(sql, params);
		
		if(customers.size()==1) {
			clickItem(customers.get(0));
		}
	}
	
	public void save() {
		boolean isOk = true;
		Customer customer = new Customer();
		if(getCustomerSelected()!=null) {
			customer = getCustomerSelected();
		}else {
			customer.setIsactive(1);
		}
		
		if(getFullName()==null || getFullName().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Customer fullname");
		}else {
			if(Customer.validateNameEntry(getFullName().trim())) {
				isOk = false;
				Application.addMessage(3, "Error", "Name is already recorded.");
			}
		}
		if(getAddress()==null || getAddress().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide address");
		}
		
		if(isOk) {
			UserDtls user = Login.getUserLogin().getUserDtls();
			customer.setDateregistered(DateUtils.convertDate(getRegDate(),"yyyy-MM-dd"));
			customer.setFullname(getFullName().trim().toUpperCase());
			//customer.setAddress(getAddress().trim().toUpperCase());
			customer.setContactno(getContactNo());
			customer.setUserDtls(user);
			customer.save();
			loadCustomer();
			clearFlds();
			Application.addMessage(1, "Success", "Successfully saved.");
		}
	}
	
	public void clearFlds() {
		setCustomerSelected(null);
		setRegDate(DateUtils.getDateToday());
		setFullName(null);
		setAddress(null);
		setContactNo(null);
		loadCustomer();
	}
	
	public void deleteRow(Customer cus) {
		cus.delete();
		loadCustomer();
		clearFlds();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void clickItem(Customer cus) {
		setCustomerSelected(cus);
		setRegDate(DateUtils.convertDateString(cus.getDateregistered(), "yyyy-MM-dd"));
		setFullName(cus.getFullname());
		setAddress(cus.getCompleteAddress());
		setContactNo(cus.getContactno());
	}
	
}
