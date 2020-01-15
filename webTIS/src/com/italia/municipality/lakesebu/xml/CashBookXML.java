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
public class CashBookXML {

	private long id;
	private String dateTrans;
	private String payee;
	private String naturePayment;
	private String voucherNo;
	private String orNumber;
	private String checkNo;
	private int isActive;
	private int transType;
	private double debitAmount;
	private double creditAmount;
	private double balances;
	private Timestamp timestamp;
	private UserDtls userDtls;
	private BankAccounts accounts;
	private Department department;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDateTrans() {
		return dateTrans;
	}
	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}
	
	public String getNaturePayment() {
		return naturePayment;
	}
	public void setNaturePayment(String naturePayment) {
		this.naturePayment = naturePayment;
	}
	public String getVoucherNo() {
		return voucherNo;
	}
	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}
	public String getOrNumber() {
		return orNumber;
	}
	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}
	public String getCheckNo() {
		return checkNo;
	}
	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public int getTransType() {
		return transType;
	}
	public void setTransType(int transType) {
		this.transType = transType;
	}
	public double getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(double debitAmount) {
		this.debitAmount = debitAmount;
	}
	public double getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(double creditAmount) {
		this.creditAmount = creditAmount;
	}
	public double getBalances() {
		return balances;
	}
	public void setBalances(double balances) {
		this.balances = balances;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public BankAccounts getAccounts() {
		return accounts;
	}
	public void setAccounts(BankAccounts accounts) {
		this.accounts = accounts;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public String getPayee() {
		return payee;
	}
	public void setPayee(String payee) {
		this.payee = payee;
	}
	
}
