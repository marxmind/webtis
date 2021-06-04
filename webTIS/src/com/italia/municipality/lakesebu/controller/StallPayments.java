package com.italia.municipality.lakesebu.controller;

import java.util.List;

import com.italia.municipality.lakesebu.licensing.controller.Customer;

/**
 * 
 * @author mark italia
 * @since 06/16/2018
 * @version 1.0
 */
public class StallPayments {
	
	private String dateTrans;
	private String orNumber;
	private double payableAmount;
	private double amountPaid;
	private int paymentType;
	private Customer customer;
	private List<BuildingPaymentTrans> paymentTrans;
	private List<MonthTrans> monthTransactions;
	public String getDateTrans() {
		return dateTrans;
	}
	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}
	public String getOrNumber() {
		return orNumber;
	}
	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}
	public double getPayableAmount() {
		return payableAmount;
	}
	public void setPayableAmount(double payableAmount) {
		this.payableAmount = payableAmount;
	}
	public double getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public List<MonthTrans> getMonthTransactions() {
		return monthTransactions;
	}
	public void setMonthTransactions(List<MonthTrans> monthTransactions) {
		this.monthTransactions = monthTransactions;
	}
	public int getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}
	public List<BuildingPaymentTrans> getPaymentTrans() {
		return paymentTrans;
	}
	public void setPaymentTrans(List<BuildingPaymentTrans> paymentTrans) {
		this.paymentTrans = paymentTrans;
	}
	
}
