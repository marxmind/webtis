package com.italia.municipality.lakesebu.controller;

import java.sql.Timestamp;

/**
 * 
 * @author mark italia
 * @since 09/27/2016
 *
 */
public class Company {

	private Long compid;
	private String companyName;
	private String address;
	private String contactNo;
	private String ownername;
	private Timestamp timestamp;
	
	public Company(){}
	
	public Company(
			Long compid,
			String companyName,
			String address,
			String contactNo,
			String ownername
			){
		this.compid = compid;
		this.companyName = companyName;
		this.address = address;
		this.contactNo = contactNo;
		this.ownername = ownername;
	}
	
	public Long getCompid() {
		return compid;
	}
	public void setCompid(Long compid) {
		this.compid = compid;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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
	public String getOwnername() {
		return ownername;
	}
	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}

