package com.italia.municipality.lakesebu.controller;

import java.util.List;

import com.italia.municipality.lakesebu.licensing.controller.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author mark italia
 * @since 06/16/2018
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class StallPayments {
	
	private String dateTrans;
	private String orNumber;
	private double payableAmount;
	private double amountPaid;
	private int paymentType;
	private Customer customer;
	private List<BuildingPaymentTrans> paymentTrans;
	private List<MonthTrans> monthTransactions;
}
