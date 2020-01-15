package com.italia.municipality.lakesebu.xml;

import java.sql.Timestamp;

import com.italia.municipality.lakesebu.controller.BankAccounts;
import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.UserDtls;
/**
 * 
 * @author mark italia
 * @since 02/01/2017
 * @version 1.0
 *
 */
public enum Book {
	
	DATE_TRANS("cashDate"),
	PAYEE("cashParticulars"),
	NATURE_PAYMENT("naturepayment"),
	VOUCHER("voucherno"),
	OR_NUMBER("orno"),
	CHECK_NO("checkno"),
	ACTIVE("cashisactive"),
	TRANS_TYPE("cashtranstype"),
	DEBIT("cashDebit"),
	CREDIT("cashCredit"),
	BALANCE("balances"),
	USER("userdtlsid"),
	BANK("bankid"),
	DEPARTMENT("departmentid");
	
	private String tagName;
	
	public String getTagName(){
		return tagName;
	}
	
	private Book(String tagName){
		this.tagName=tagName;
	}
	
}
