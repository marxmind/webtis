package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.italia.municipality.lakesebu.controller.Customer;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 06/15/2018
 * @version 1.0
 */
@ManagedBean(name="customerBean", eager=true)
@ViewScoped
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
			customer.setIsActive(1);
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
			customer.setRegistrationDate(DateUtils.convertDate(getRegDate(),"yyyy-MM-dd"));
			customer.setFullName(getFullName().trim().toUpperCase());
			customer.setAddress(getAddress().trim().toUpperCase());
			customer.setContactNumber(getContactNo());
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
		setRegDate(DateUtils.convertDateString(cus.getRegistrationDate(), "yyyy-MM-dd"));
		setFullName(cus.getFullName());
		setAddress(cus.getAddress());
		setContactNo(cus.getContactNumber());
	}
	
	public Date getRegDate() {
		if(regDate==null) {
			regDate = DateUtils.getDateToday();
		}
		return regDate;
	}
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public Customer getCustomerSelected() {
		return customerSelected;
	}
	public void setCustomerSelected(Customer customerSelected) {
		this.customerSelected = customerSelected;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

}
