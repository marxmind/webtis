package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.controller.BuildingOwnerHistory;
import com.italia.municipality.lakesebu.controller.BuildingPaymentTrans;
import com.italia.municipality.lakesebu.controller.BuildingStall;
import com.italia.municipality.lakesebu.controller.Customer;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.MarketReports;
import com.italia.municipality.lakesebu.controller.MonthTrans;
import com.italia.municipality.lakesebu.controller.StallPayments;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.enm.BuildingType;
import com.italia.municipality.lakesebu.enm.Months;
import com.italia.municipality.lakesebu.enm.PaymentType;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;

/**
 * 
 * @author Mark Italia
 * @since 06/16/2018
 * @version 1.0
 *
 */

@Named
@ViewScoped
public class MarketBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4365687343431L;
	
	private static final int YEAR_START = 2017;
	
	private List<MarketReports> reports = Collections.synchronizedList(new ArrayList<MarketReports>());
	private String searchName;
	private Object selectedObject;
	
	private int yearsFromId;
	private List yearsFrom;
	
	private int yearsToId;
	private List yearsTo;
	
	private List customers;
	private long customerId;
	
	private Date paidDate;
	private String orNumber;
	private double paidAmount;
	private boolean splitPayment;
	private int stallId;
	private List stalls;
	private StallPayments selectedPayment;
	private String paymentBillingNotes;
	
	private List<MonthTrans> monthPayments = Collections.synchronizedList(new ArrayList<MonthTrans>());
	private Map<Integer, List<MonthTrans>> mapPayments = Collections.synchronizedMap(new HashMap<Integer, List<MonthTrans>>());
	private boolean paymentTermPartial;
	
	private List<StallPayments> stallPayments = Collections.synchronizedList(new ArrayList<StallPayments>());
	
	
	private List<BuildingPaymentTrans> ors = Collections.synchronizedList(new ArrayList<BuildingPaymentTrans>());
	
	
	public void clickBuildingPayment(BuildingPaymentTrans pay) {
		StallPayments pays = new StallPayments();
		
		pays.setAmountPaid(pay.getAmountPaid());
		pays.setOrNumber(pay.getOrNumber());
		pays.setDateTrans(pay.getDateTrans());
		pays.setPaymentType(pay.getPaymentType());
		pays.setCustomer(pay.getCustomer());
		
		List<BuildingPaymentTrans> trans= Collections.synchronizedList(new ArrayList<BuildingPaymentTrans>());
		trans.add(pay);
		pays.setPaymentTrans(trans);
		pays.setMonthTransactions(pay.getMonths());
		
		setSelectedPayment(pays);
		setCustomerId(pay.getCustomer().getId());
		setOrNumber(pay.getOrNumber());
		setPaidAmount(pay.getAmountPaid());
		setStallId(pay.getStall().getId());
		
		setSplitPayment(false);
	}
	
	private UserDtls getUser() {
		return Login.getUserLogin().getUserDtls();
	}
	
	public void savePayment() {
		boolean isOk = true;
		StallPayments stall = new StallPayments();
		if(getSelectedPayment()!=null) {
			stall = getSelectedPayment();
		}
		
		if(getCustomerId()==0) {
			isOk= false;
			Application.addMessage(3, "Error", "Please provide Payor name");
		}
		
		if(getOrNumber()==null || getOrNumber().isEmpty()) {
			isOk= false;
			Application.addMessage(3, "Error", "Please provide Official Receipt No");
		}
		
		if(getPaidAmount()==0) {
			isOk= false;
			Application.addMessage(3, "Error", "Please provide paid amount");
		}
		
		if(getSelectedPayment()==null) {
			List<BuildingPaymentTrans> trans = Collections.synchronizedList(new ArrayList<BuildingPaymentTrans>());
			for(int stallId : getMapPayments().keySet()) {
				
				BuildingPaymentTrans tran = new BuildingPaymentTrans();
				Customer customer = getMapPayments().get(stallId).get(0).getCustomer();
				tran.setCustomer(customer);
				tran.setPaymentType(isPaymentTermPartial()==true? PaymentType.PARTIAL.getId() : PaymentType.FULL.getId());
				
				BuildingStall st = new BuildingStall();
				st.setId(stallId);
				tran.setStall(st);
				
				trans.add(tran);
				
			}
		}else {
			//monthPayments = Collections.synchronizedList(new ArrayList<MonthTrans>());
			/*for(BuildingPaymentTrans tran : getSelectedPayment().getPaymentTrans()) {
				for(MonthTrans month : tran.getMonths()) {
					MonthTrans mo = monthDB(month.getYear(), month.getMonth(), month.getAmountPaid(), month.getPaymentType(), month.getCustomer());
					monthPayments.add(mo);
				}
			}*/
			//editing OR and date only Only
			
			BuildingPaymentTrans tran = getSelectedPayment().getPaymentTrans().get(0);
			tran.setOrNumber(getOrNumber());
			tran.setDateTrans(DateUtils.convertDate(getPaidDate(), "yyyy-MM-dd"));
			tran.save();
			isOk = false;
			clearFldsPayment();
			Application.addMessage(1, "Success", "Payment has been successfully processed.");
		}
		
		if(isOk) {
			double amount = getPaidAmount();
			if(isSplitPayment() && getStallId()==0) {
				amount = amount / getMapPayments().size();
			}
			for(int stallId : getMapPayments().keySet()) {
				
				BuildingPaymentTrans tran = new BuildingPaymentTrans();
				
				tran.setIsActive(1);
				tran.setDateTrans(DateUtils.convertDate(getPaidDate(), "yyyy-MM-dd"));
				tran.setOrNumber(getOrNumber());
				tran.setAmountPaid(amount);
				
				Customer customer = getMapPayments().get(stallId).get(0).getCustomer();
				tran.setCustomer(customer);
				tran.setPaymentType(isPaymentTermPartial()==true? PaymentType.PARTIAL.getId() : PaymentType.FULL.getId());
				
				BuildingStall st = new BuildingStall();
				st.setId(stallId);
				tran.setStall(st);
				
				tran.setUserDtls(getUser());
				tran = BuildingPaymentTrans.save(tran);
				
				for(MonthTrans month : getMapPayments().get(stallId)) {
					month.setDateTrans(tran.getDateTrans());
					month.setOrNumber(tran.getOrNumber());
					month.setIsActive(tran.getIsActive());
					
					month.setStall(tran.getStall());
					month.setBuildingPayment(tran);
					month.setCustomer(tran.getCustomer());
					month.setUserDtls(tran.getUserDtls());
					month.save();
				}
				
			}
			
			clearFldsPayment();
			Application.addMessage(1, "Success", "Payment has been successfully processed.");
			
		}
		
	}
	 
	
	public void calculateAmountPaid() {
		UserDtls user = Login.getUserLogin().getUserDtls();
		boolean isOk = true;
		String notes="<br/><p><strong>Hi " + user.getFirstname() + ",</strong> Please see below calculated based on the amount you have provided which is <a style=\"color:blue\">Php"+Currency.formatAmount(getPaidAmount())+"</a></p>";
		if(getPaidAmount()<=0) {
			notes="<p><strong>Hi " + user.getFirstname() + ",</strong></p><br/>";
		}
		
		if(getCustomerId()==0) {
			notes += "<p><a style=\"color:red;\">Please provide owner name.</a></p>";
			isOk = false;
		}
		if(getOrNumber()==null || getOrNumber().isEmpty()) {
			notes += "<p><a style=\"color:red;\">Please provide Official Receipt.</a></p>";
			isOk = false;
		}
		
		if(getPaidAmount()<=0) {
			notes += "<p><a style=\"color:red;\">Please provide amount.</a></p>";
			isOk = false;
		}
		
		if(isOk) {
			notes +=calculate();
			String html="<html><head><title>Billing Statement</title></head><body>"+ notes + "</body></html>";
			setPaymentBillingNotes(html);
		}else {
			notes +="<p><strong>In order for you to proceed.</strong></p>";
			String html="<html><head><title>Billing Statement</title></head><body>"+ notes + "</body></html>";
			setPaymentBillingNotes(html);
			ors = Collections.synchronizedList(new ArrayList<BuildingPaymentTrans>());
		}
	}
	
	
	
	private String calculate() {
		ors = Collections.synchronizedList(new ArrayList<BuildingPaymentTrans>());
		
		String notes="";
		Customer customer = Customer.customer(getCustomerId());
			
			String sql = " AND cuz.customerid=? AND own.ownerdatestart is not null AND own.iscurrentowner=1";
			String[] params = new String[1];
			params[0] = getCustomerId()+"";
			
			if(getStallId()>0) {
				sql = " AND cuz.customerid=? AND own.ownerdatestart is not null AND own.iscurrentowner=1 AND st.stallid=?";
				params = new String[2];
				params[0] = getCustomerId()+"";
				params[1] = getStallId()+"";
			}
			
			int yearToday = DateUtils.getCurrentYear();
			int monthToday=DateUtils.getCurrentMonth();
			double paidAmount = getPaidAmount();
			double unpaidAmount = 0d;
			double paymentPerStall = 0d;
			int splitSize = 0;
			double remainingAmountSplit = 0d;
			if(isSplitPayment() && getStallId()==0) {
				List<BuildingOwnerHistory> his = BuildingOwnerHistory.retrieve(sql, params);
				if(his.size()>0) {
					splitSize = his.size();
					paymentPerStall = paidAmount / his.size();
					
				}
			}
			
			for(BuildingOwnerHistory his : BuildingOwnerHistory.retrieve(sql, params)) {
				sql = " AND cuz.customerid=? AND st.isoccupied=1 AND st.stallid=?";
				params = new String[2];
				params[0] = his.getCustomer().getId()+"";
				params[1] = his.getStall().getId()+"";
				
				
				if(isSplitPayment() && getStallId()==0) {//this code use only if user decided to split payment per stall
					paidAmount = paymentPerStall + remainingAmountSplit;
				}
				
				for(BuildingStall stall : BuildingStall.retrieve(sql, params)) {
					
					monthPayments = Collections.synchronizedList(new ArrayList<MonthTrans>());
					
					
					params = new String[2];
					params[0] = stall.getCustomer().getId()+"";
					params[1] = stall.getId()+"";
					sql = " AND cuz.customerid=? AND st.stallid=? ORDER BY mnt.monthtransid DESC LIMIT 1";
					List<MonthTrans> lastPaidMonth = MonthTrans.retrieve(sql, params);
					String stallName = stall.getBuilding().getName() +"("+BuildingType.nameId(stall.getBuilding().getType())+")" + " || " + stall.getName() +" || Monthly rate Php" + Currency.formatAmount(stall.getMonthlyRental());
					
					
					if(lastPaidMonth.size()>0) {
						
						int monthLastPaid = lastPaidMonth.get(0).getMonth();
						int yearPaid = lastPaidMonth.get(0).getYear();
						double lastPaidAmount = lastPaidMonth.get(0).getAmountPaid();
						
						params = new String[4];
						params[0] = stall.getCustomer().getId()+"";
						params[1] = stall.getId()+"";
						params[2] = yearPaid+"";
						params[3] = monthLastPaid+"";
						sql = " AND cuz.customerid=? AND st.stallid=? AND mnt.yearpaid=? AND mnt.monthpaid=?";
						List<MonthTrans> collectMonth = MonthTrans.retrieve(sql, params);
						if(collectMonth.size()>1) {
							lastPaidAmount = 0d;
							for(MonthTrans m : collectMonth) {
								lastPaidAmount += m.getAmountPaid(); 
							}
						}
						
						
						if(yearPaid==yearToday) {//current year
							notes +="<p><strong>"+stallName+"</strong></p>";
							if(monthLastPaid>monthToday) {
								notes += "<p><strong><h2>Advance payment for year " + yearPaid +"</h2><strong></p>";
								notes +="<p>Last payment was paid on " + DateUtils.convertDateToMonthDayYear(lastPaidMonth.get(0).getDateTrans()) + " for the month of " + DateUtils.getMonthName(monthLastPaid) + " year " + yearPaid + " amounting to Php" + Currency.formatAmount(lastPaidAmount)+ " pesos only.";
								unpaidAmount=0;
								paidAmount=0;
							}else {
								
								if(paidAmount>0) {	
										if(lastPaidAmount<stall.getMonthlyRental()) {
											double payable = (stall.getMonthlyRental() - lastPaidAmount);
											if(payable<=paidAmount) {
												notes += "<p>" + yearPaid +"</p>";
												notes += "<ul>";											
												paidAmount -= (stall.getMonthlyRental() - lastPaidAmount);
												notes +="<li> Payable amount from partial payment for the month of "+DateUtils.getMonthName(monthLastPaid) + " which is <a style=\"color: blue\">Php" + Currency.formatAmount(payable) + " pesos only.</a></li>";
												
												MonthTrans tran = monthDB(yearPaid, monthLastPaid, payable, PaymentType.FULL.getId(), customer);
												monthPayments.add(tran);
												
											}else if(payable>paidAmount) {
												//payable = paidAmount;
												double newPayableAmount = payable - paidAmount;
												notes +="<li> Partial payment for the month of "+DateUtils.getMonthName(monthLastPaid) + " which is <a style=\"color: blue\">Php" + Currency.formatAmount(paidAmount) + "</a> and remaining payable amount is <strong>Php"+ Currency.formatAmount(newPayableAmount) +"</strong></li>";
												
												MonthTrans tran = monthDB(yearPaid, monthLastPaid, paidAmount, PaymentType.PARTIAL.getId(), customer);
												monthPayments.add(tran);
												
												unpaidAmount +=newPayableAmount;
												
												paidAmount = 0;
											}
										}else {
											notes += "<p>" + yearPaid +"</p>";
											notes += "<ul>";
										}
									
										int startMonth = monthLastPaid + 1;// new month;
										for(int month = startMonth; month<=monthToday; month++) {
											if(paidAmount>0) {
												//if(month == monthToday) {
													if(paidAmount>= stall.getMonthlyRental()) {
														paidAmount -= stall.getMonthlyRental();
														notes +="<li>"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(stall.getMonthlyRental()) + "/FULL </li>";
														
														MonthTrans tran = monthDB(yearPaid, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), customer);
														monthPayments.add(tran);
														
													}else if(paidAmount< stall.getMonthlyRental()) {
														double payable = stall.getMonthlyRental() - paidAmount;
														notes +="<li><a style=\"color:blue\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(paidAmount) + "/PARTIAL</a> </li>";
														
														MonthTrans tran = monthDB(yearPaid, month, paidAmount, PaymentType.PARTIAL.getId(), customer);
														monthPayments.add(tran);
														
														unpaidAmount +=payable;
														
														paidAmount =0;
													}
												//}
											}else {
												notes +="<li><a style=\"color:red\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(stall.getMonthlyRental()) + "/UNPAID </a></li>";
												unpaidAmount +=stall.getMonthlyRental();
											}
										}
								
										notes += "</ul>";
									
								}
									
							}
						}else if(yearPaid<yearToday){//last year payable
							notes +="<p><strong>"+stallName+"</strong></p>";
							if(lastPaidAmount<stall.getMonthlyRental()) {
								if(paidAmount>0) {
									notes += "<p>" + yearPaid +"</p>";
									notes += "<ul>";	
									double payable = (stall.getMonthlyRental() - lastPaidAmount);
									if(payable<=paidAmount) {					
										paidAmount -= (stall.getMonthlyRental() - lastPaidAmount);
										notes +="<li> Payable amount from partial payment for the month of "+DateUtils.getMonthName(monthLastPaid) + " which is <a style=\"color: blue\">Php" + Currency.formatAmount(payable) + " pesos only.</a></li>";
										
										MonthTrans tran = monthDB(yearPaid, monthLastPaid, payable, PaymentType.FULL.getId(), customer);
										monthPayments.add(tran);
										
									}else if(payable>paidAmount) {
										
										double newPayableAmount = payable - paidAmount;
										notes +="<li> Partial payment for the month of "+DateUtils.getMonthName(monthLastPaid) + " which is <a style=\"color: blue\">Php" + Currency.formatAmount(paidAmount) + "</a> and remaining payable amount is <strong>Php"+ Currency.formatAmount(newPayableAmount) +"</strong></li>";
										
										MonthTrans tran = monthDB(yearPaid, monthLastPaid, paidAmount, PaymentType.PARTIAL.getId(), customer);
										monthPayments.add(tran);
										
										unpaidAmount +=newPayableAmount;
										
										paidAmount = 0;
									}
									
								}else {
									double payable = (stall.getMonthlyRental() - lastPaidAmount);
									notes +="<li>Unpaid amount for the month of "+DateUtils.getMonthName(monthLastPaid) + " which is <a style=\"color: red\">Php" + Currency.formatAmount(payable) + " pesos only.</a></li>";
									unpaidAmount +=payable;
								}
								
							}
						
							if(paidAmount>0) {
								//notes += "<p>" + yearPaid +"</p>";
								//notes += "<ul>";
							int startMonth = monthLastPaid + 1;// new month;
							for(int month = startMonth; month<=12; month++) {
								if(paidAmount>0) {
									if(paidAmount>= stall.getMonthlyRental()) {
										paidAmount -= stall.getMonthlyRental();
										notes +="<li>"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(stall.getMonthlyRental()) + "/FULL </li>";
										
										MonthTrans tran = monthDB(yearPaid, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), customer);
										monthPayments.add(tran);
										
									}else if(paidAmount< stall.getMonthlyRental()) {
										double payable = stall.getMonthlyRental() - paidAmount;
										notes +="<li><a style=\"colore:blue\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(paidAmount) + "/PARTIAL</a> </li>";
										
										MonthTrans tran = monthDB(yearPaid, month, paidAmount, PaymentType.PARTIAL.getId(), customer);
										monthPayments.add(tran);
										
										unpaidAmount +=payable;
										
										paidAmount = 0;
									}
								}else {
									notes +="<li><a style=\"colore:red\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(stall.getMonthlyRental()) + "/UNPAID</a> </li>";
									unpaidAmount +=stall.getMonthlyRental();
								}
								
							}
					
							notes += "</ul>";
							}else {
								notes += "</ul>";
							}
							int yearStart = yearPaid + 1;
							//previous year
							for(int year=yearStart; year<yearToday; year++) {
								if(paidAmount>0) {
									notes += "<p>" + year +"</p>";
									notes += "<ul>";
									for(int month = 1; month<=12; month++) {
										if(paidAmount>0) {
											if(paidAmount>= stall.getMonthlyRental()) {
												paidAmount -= stall.getMonthlyRental();
												notes +="<li>"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(stall.getMonthlyRental()) + "/FULL </li>";
												
												MonthTrans tran = monthDB(year, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), customer);
												monthPayments.add(tran);
												
											}else if(paidAmount< stall.getMonthlyRental()) {
												double payable = stall.getMonthlyRental() - paidAmount;
												notes +="<li><a style=\"color:red\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(paidAmount) + "/PARTIAL</a> </li>";
												
												MonthTrans tran = monthDB(year, month, paidAmount, PaymentType.PARTIAL.getId(), customer);
												monthPayments.add(tran);
												
												unpaidAmount +=payable;
												
												paidAmount = 0;
											}
										}else {
											notes +="<li><a style=\"color:red\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(stall.getMonthlyRental()) + "/UNPAID</a> </li>";
											unpaidAmount +=stall.getMonthlyRental();
										}
									}
									notes += "</ul>";
								}else {
									notes += "<p>" + year +"</p>";
									notes += "<ul>";
									
									for(int month = 1; month<=12; month++) {
										notes +="<li><a style=\"color:red\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(stall.getMonthlyRental()) + "/UNPAID</a> </li>";
										unpaidAmount +=stall.getMonthlyRental();
									}
									
									notes += "</ul>";
								}
							}
							//current year
							if(yearStart == yearToday) {
								if(paidAmount>0) {
									notes += "<p>" + yearStart +"</p>";
									notes += "<ul>";
									for(int month = 1; month<=monthToday; month++) {
										if(paidAmount>0) {
											if(paidAmount>= stall.getMonthlyRental()) {
												paidAmount -= stall.getMonthlyRental();
												notes +="<li>"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(stall.getMonthlyRental()) + "/FULL </li>";
												
												MonthTrans tran = monthDB(yearStart, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), customer);
												monthPayments.add(tran);
												
											}else if(paidAmount< stall.getMonthlyRental()) {
												double payable = stall.getMonthlyRental() - paidAmount;
												notes +="<li><a style=\"color:blue\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(paidAmount) + "/PARTIAL</a> </li>";
												
												MonthTrans tran = monthDB(yearStart, month, paidAmount, PaymentType.PARTIAL.getId(), customer);
												monthPayments.add(tran);
												
												unpaidAmount +=payable;
												
												paidAmount = 0;
											}
										}else {
											notes +="<li><a style=\"color:red\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(stall.getMonthlyRental()) + "/UNPAID</a> </li>";
											unpaidAmount +=stall.getMonthlyRental();
										}
									}
									notes += "</ul>";
								}else {
									notes += "<p>" + yearStart +"</p>";
									notes += "<ul>";
									
									for(int month = 1; month<=monthToday; month++) {
										notes +="<li><a style=\"color:red\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(stall.getMonthlyRental()) + "/UNPAID</a> </li>";
										unpaidAmount +=stall.getMonthlyRental();
									}
									
									notes += "</ul>";
								}
							}
							
						}
						
					}else{//end of with payment
					//start of no payment;
						int yearReg = Integer.valueOf(his.getDateStart().split("-")[0]);
						int monthReg = Integer.valueOf(his.getDateStart().split("-")[1]);
						
						int startMonth = monthReg + 1;
						
						if(yearReg==yearToday) {
							if(startMonth<=monthToday) {
							notes +="<p><strong>"+stallName+"</strong></p>";
							notes +="<p>"+yearReg+"</p>";
							notes +="<ul>";
							for(int month=startMonth; month<=monthToday; month++) {
								
								if(paidAmount>0) {
									if(paidAmount>=stall.getMonthlyRental()) {
										paidAmount -= stall.getMonthlyRental();
										notes +="<li>"+DateUtils.getMonthName(month) + " = "+ Currency.formatAmount(stall.getMonthlyRental()) +"/FULL </li>";
										
										MonthTrans tran = monthDB(yearReg, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), customer);
										monthPayments.add(tran);
										
									}else if(paidAmount<stall.getMonthlyRental()) {
										double payable = stall.getMonthlyRental() - paidAmount;
										notes +="<li>"+DateUtils.getMonthName(month) + "= <a style=\"color:blue;\">PARTIAL("+Currency.formatAmount(paidAmount)+")</a> remaining payable is Php <a style=\"color:red;\">"+Currency.formatAmount(payable)+"</a></li>";
										unpaidAmount +=payable;
										
										MonthTrans tran = monthDB(yearReg, month, paidAmount, PaymentType.PARTIAL.getId(), customer);//do not move this code
										monthPayments.add(tran);
										
										paidAmount = 0;
									}
								}else {
									notes +="<li>"+DateUtils.getMonthName(month) + "= <a style=\"color:red;\">UNPAID("+Currency.formatAmount(stall.getMonthlyRental())+") </a></li>";
									unpaidAmount += stall.getMonthlyRental();
								}
							}
							notes +="</ul>";
							}
						}else {
							
							notes +="<p><strong>"+stallName+"</strong></p>";
							notes +="<p>"+yearReg+"</p>";
							notes +="<ul>";
							
							for(int month=startMonth; month<=12; month++) {
								if(paidAmount>0) {
									if(paidAmount>=stall.getMonthlyRental()) {
										paidAmount -= stall.getMonthlyRental();
										notes +="<li>"+DateUtils.getMonthName(month) + "= "+Currency.formatAmount(stall.getMonthlyRental())+"/FULL </li>";
										
										MonthTrans tran = monthDB(yearReg, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), customer);
										monthPayments.add(tran);
										
									}else if(paidAmount<stall.getMonthlyRental()) {
										double payable = stall.getMonthlyRental() - paidAmount;
										notes +="<li>"+DateUtils.getMonthName(month) + "= <a style=\"color:blue;\">PARTIAL("+Currency.formatAmount(paidAmount)+")</a> remaining payable is Php <a style=\"color:red;\">"+Currency.formatAmount(payable)+"</a></li>";
										unpaidAmount +=payable;
										
										MonthTrans tran = monthDB(yearReg, month, paidAmount, PaymentType.PARTIAL.getId(), customer);//do not move this code
										monthPayments.add(tran);
										
										paidAmount = 0;
									}
								}else {
									notes +="<li>"+DateUtils.getMonthName(month) + "= <a style=\"color:red;\">UNPAID("+Currency.formatAmount(stall.getMonthlyRental())+") </a></li>";
									unpaidAmount += stall.getMonthlyRental();
								}
							}
							notes +="</ul>";
							
							int newYear = yearReg + 1;
							for(int year=newYear; year<=yearToday; year++) {
								notes +="<p>"+year+"</p>";
								notes +="<ul>";
								if(year==yearToday) {
									
									for(int month=1; month<=monthToday; month++) {
										if(paidAmount>0) {
											if(paidAmount>=stall.getMonthlyRental()) {
												paidAmount -= stall.getMonthlyRental();
												notes +="<li>"+DateUtils.getMonthName(month) + "= "+Currency.formatAmount(stall.getMonthlyRental())+"/FULL </li>";
												
												MonthTrans tran = monthDB(year, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), customer);
												monthPayments.add(tran);
											
											}else if(paidAmount<stall.getMonthlyRental()) {
												double payable = stall.getMonthlyRental() - paidAmount;
												notes +="<li>"+DateUtils.getMonthName(month) + "= <a style=\"color:blue;\">PARTIAL("+Currency.formatAmount(paidAmount)+")</a> remaining payable is Php <a style=\"color:red;\">"+Currency.formatAmount(payable)+"</a></li>";
												unpaidAmount +=payable;
												
												MonthTrans tran = monthDB(year, month, paidAmount, PaymentType.PARTIAL.getId(), customer);//do not move this code
												monthPayments.add(tran);
												
												paidAmount = 0;
											}
										}else {
											notes +="<li>"+DateUtils.getMonthName(month) + "= <a style=\"color:red;\">UNPAID("+Currency.formatAmount(stall.getMonthlyRental())+") </a></li>";
											unpaidAmount += stall.getMonthlyRental();
										}
									}
									
								}else {
									
									for(int month=1; month<=12; month++) {
										if(paidAmount>0) {
											if(paidAmount>=stall.getMonthlyRental()) {
												paidAmount -= stall.getMonthlyRental();
												notes +="<li>"+DateUtils.getMonthName(month) + "= "+Currency.formatAmount(stall.getMonthlyRental())+"/FULL </li>";
												
												MonthTrans tran = monthDB(year, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), customer);
												monthPayments.add(tran);
											
											}else if(paidAmount<stall.getMonthlyRental()) {
												double payable = stall.getMonthlyRental() - paidAmount;
												notes +="<li>"+DateUtils.getMonthName(month) + "= <a style=\"color:blue;\">PARTIAL("+Currency.formatAmount(paidAmount)+")</a> remaining payable is Php <a style=\"color:red;\">"+Currency.formatAmount(payable)+"</a></li>";
												unpaidAmount +=payable;
												
												MonthTrans tran = monthDB(year, month, paidAmount, PaymentType.PARTIAL.getId(), customer);//do not move this code
												monthPayments.add(tran);
												
												paidAmount = 0;
											}
										}else {
											notes +="<li>"+DateUtils.getMonthName(month) + "= <a style=\"color:red;\">UNPAID("+Currency.formatAmount(stall.getMonthlyRental())+") </a></li>";
											unpaidAmount += stall.getMonthlyRental();
										}
									}
									
								}
								
								notes +="</ul>";
							}
						}
						
					}
					
					mapPayments.put(stall.getId(), monthPayments);
					
				}
				remainingAmountSplit = paidAmount;
			}
			if(paidAmount>0) {
				notes +=carriedOver(paidAmount, isSplitPayment(), splitSize);
			}
			
			String str = "";
			if(paidAmount==0 && unpaidAmount>0) {
				str="<p>Unfortunately, <strong>"+customer.getFullName()+"</strong> has a remaining payable amount of <a style=\"color:red\">Php"+Currency.formatAmount(unpaidAmount)+"</a></p>";
				//notes += "<p><strong>Remaining Payable Amount is <a style=\"color:red\">Php"+Currency.formatAmount(unpaidAmount)+"</a></strong></p>";
				str +="<br/><p><strong>TIP:</strong><a style=\"color:orange;\">If you are ok with below information. Please proceed to click </a><a style=\"color:green;\">SAVE BUTTON.</a></p>";
				str += "<br/><p><strong>Please see below detailed information</strong></p>";
				notes = str + notes;
				setPaymentTermPartial(true);
			}
			
			if(paidAmount>0 && unpaidAmount==0) {
				str = "<p>Awesome! The excess for the paid amount by <strong>"+customer.getFullName()+"</strong> has been carried over for the future payment which is <a style=\"color:blue\">Php"+Currency.formatAmount(paidAmount)+"</a></p>";
				str +="<br/><p><strong>TIP:</strong><a style=\"color:orange;\">If you are ok with below information. Please proceed to click </a><a style=\"color:green;\">SAVE BUTTON.</a></p>";
				str += "<br/><p><strong>Please see below detailed information</strong></p>";
				notes = str + notes;
			}
		
			if(paidAmount==0 && unpaidAmount==0) {
				str = "<p>This is good, "+ getUser().getFirstname() +"! <strong>"+customer.getFullName()+"</strong> has making a good record to us. Keep this owner watch out.</p>";
				str +="<br/><p><strong>TIP:</strong><a style=\"color:orange;\">If you are ok with below information. Please proceed to click </a><a style=\"color:green;\">SAVE BUTTON.</a></p>";
				str += "<br/><p><strong>Please see below detailed information</strong></p>";
				notes = str + notes;
			}
			
		return notes;
	}
	
	private MonthTrans monthDB(int year, int month, double paidAmount, int paymentType, Customer customer) {
		MonthTrans trans = new MonthTrans();
		trans.setYear(year);
		trans.setIsActive(1);
		trans.setMonth(month);
		trans.setAmountPaid(paidAmount);
		trans.setPaymentType(paymentType);
		trans.setRemarks(PaymentType.nameId(paymentType));
		trans.setCustomer(customer);
		return trans;
	}
	
	private String carriedOver(double paidAmount, boolean isSplit, int splitSize) {
		String notes ="<p><strong><a style=\"color:blue;\">Carried Over Amount</a></strong></p>";
		
		String sql = " AND cuz.customerid=? AND own.ownerdatestart is not null AND own.iscurrentowner=1";
		String[] params = new String[1];
		params[0] = getCustomerId()+"";
		
		if(getStallId()>0) {
			sql = " AND cuz.customerid=? AND own.ownerdatestart is not null AND own.iscurrentowner=1 AND st.stallid=?";
			params = new String[2];
			params[0] = getCustomerId()+"";
			params[1] = getStallId()+"";
		}
		
		double splitAmount = 0d;
		if(isSplit && getStallId()==0) {
			splitAmount = paidAmount / splitSize;
		}
		double remainingAmountSplit = 0d;
		for(BuildingOwnerHistory his : BuildingOwnerHistory.retrieve(sql, params)) {
			sql = " AND cuz.customerid=? AND st.isoccupied=1 AND st.stallid=?";
			params = new String[2];
			params[0] = his.getCustomer().getId()+"";
			params[1] = his.getStall().getId()+"";
			
			if(isSplit && getStallId()==0) {
				paidAmount = splitAmount + remainingAmountSplit;
			}
			int yearToday = DateUtils.getCurrentYear();
			int monthToday=DateUtils.getCurrentMonth();
			for(BuildingStall stall : BuildingStall.retrieve(sql, params)) {
				
				
				if(getMapPayments()!=null) {
					if(getMapPayments().containsKey(stall.getId())) {
						monthPayments = getMapPayments().get(stall.getId());
					}
				}else {
					monthPayments = Collections.synchronizedList(new ArrayList<MonthTrans>());
				}
				
				if(paidAmount>0) {
				notes +="<p>"+stall.getBuilding().getName()+"("+BuildingType.nameId(stall.getBuilding().getType())+")" + " || " +  stall.getName()+ " || Monthly rate Php" + Currency.formatAmount(stall.getMonthlyRental()) +"</p>";
				notes +="<p>"+yearToday+"</p>";
				notes +="<ul>";
				monthToday +=1;
				for(int month=monthToday; month<=12; month++) {
					if(paidAmount>0) {
						if(paidAmount==stall.getMonthlyRental()) {
							paidAmount -= stall.getMonthlyRental();
							notes +="<li>"+DateUtils.getMonthName(month) + "= FULL </li>";
							
							MonthTrans tran = monthDB(yearToday, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), his.getCustomer());
							monthPayments.add(tran);
							
						}else if(paidAmount>stall.getMonthlyRental()) {
							paidAmount -= stall.getMonthlyRental();
							notes +="<li>"+DateUtils.getMonthName(month) + "= FULL </li>";
							
							MonthTrans tran = monthDB(yearToday, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), his.getCustomer());
							monthPayments.add(tran);
							
						}else if(paidAmount<stall.getMonthlyRental()) {
							notes +="<li>"+DateUtils.getMonthName(month) + "= <a style=\"color:blue;\">PARTIAL("+Currency.formatAmount(paidAmount)+")</a></li>";
							
							MonthTrans tran = monthDB(yearToday, month, paidAmount, PaymentType.PARTIAL.getId(), his.getCustomer());//do not move this code
							monthPayments.add(tran);
							
							paidAmount = 0;
						}
					}else {
						break;
					}
				}
				notes +="</ul>";
				yearToday +=1;
				if(paidAmount>0) {
					int futureYear = yearToday + 5;
					for(int year=yearToday;year<=futureYear;year++) {
						if(paidAmount>0) {
							notes +="<p>"+year+"</p>";
							notes +="<ul>";
							for(int month=1; month<=12; month++) {
								if(paidAmount>0) {
									if(paidAmount==stall.getMonthlyRental()) {
										paidAmount -= stall.getMonthlyRental();
										notes +="<li>"+DateUtils.getMonthName(month) + "= FULL </li>";
										
										MonthTrans tran = monthDB(year, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), his.getCustomer());
										monthPayments.add(tran);
										
									}else if(paidAmount>stall.getMonthlyRental()) {
										paidAmount -= stall.getMonthlyRental();
										notes +="<li>"+DateUtils.getMonthName(month) + "= FULL </li>";
										
										MonthTrans tran = monthDB(year, month, stall.getMonthlyRental(), PaymentType.FULL.getId(), his.getCustomer());
										monthPayments.add(tran);
										
									}else if(paidAmount<stall.getMonthlyRental()) {
										notes +="<li>"+DateUtils.getMonthName(month) + "= <a style=\"color:blue;\">PARTIAL("+Currency.formatAmount(paidAmount)+")</a></li>";
										
										MonthTrans tran = monthDB(year, month, paidAmount, PaymentType.PARTIAL.getId(), his.getCustomer());
										monthPayments.add(tran);
										
										
										paidAmount = 0;
									}
								}else {
									break;
								}
							}
							notes +="</ul>";
					}else {
						break;
					}
					}
				}
			}
				mapPayments.put(stall.getId(), monthPayments);
			}
			remainingAmountSplit = paidAmount;
			
		}
		
		return notes;
	}
	
	public void clearFldsPayment() {
		setSelectedPayment(null);
		setPaidDate(null);
		setOrNumber(null);
		setCustomerId(0);
		setPaidAmount(0);
		setPaymentBillingNotes(null);
		setMonthPayments(Collections.synchronizedList(new ArrayList<MonthTrans>()));
		setMapPayments(Collections.synchronizedMap(new HashMap<Integer, List<MonthTrans>>()));
		setPaymentTermPartial(false);
		setOrs(Collections.synchronizedList(new ArrayList<BuildingPaymentTrans>()));
	}
	
	
	public void calculatePayments() {
		ors = Collections.synchronizedList(new ArrayList<BuildingPaymentTrans>());
		
		if(getCustomerId()>0) {
		Customer customer = Customer.customer(getCustomerId());
		String user = Login.getUserLogin().getUserDtls().getFirstname();
		String startSentence = "<p><strong>Hi " + user + ",</strong></p>";
		startSentence += "<br/><p>Please see below gathered information for owner " + customer.getFullName() + ".</p>";
		//String infoStall = "";
		String infoPayments = "";
			
			
			String sql = " AND cuz.customerid=? AND own.ownerdatestart is not null AND own.iscurrentowner=1";
			String[] params = new String[1];
			params[0] = getCustomerId()+"";
			
			boolean isAdvancePayment = false;
			double payableAmount = 0d;
			
			for(BuildingOwnerHistory his : BuildingOwnerHistory.retrieve(sql, params)) {
				sql = " AND cuz.customerid=? AND st.isoccupied=1 AND st.stallid=?";
				params = new String[2];
				params[0] = his.getCustomer().getId()+"";
				params[1] = his.getStall().getId()+"";
				int yearToday = DateUtils.getCurrentYear();
				int monthToday=DateUtils.getCurrentMonth();
				for(BuildingStall stall : BuildingStall.retrieve(sql, params)) {
					params = new String[2];
					params[0] = stall.getCustomer().getId()+"";
					params[1] = stall.getId()+"";
					sql = " AND cuz.customerid=? AND st.stallid=? ORDER BY mnt.monthtransid DESC LIMIT 1";
					
					List<MonthTrans> lastPaidMonth = MonthTrans.retrieve(sql, params);
					String stallName = stall.getName();
					//infoPayments += "<p>" +  +" = Php"; 
					double stallPment = 0d;
					String monthNotes = "";
					double amountPerMonth = 0d;
					if(lastPaidMonth.size()>0) {
						int monthLastPaid = lastPaidMonth.get(0).getMonth();
						int yearPaid = lastPaidMonth.get(0).getYear();
						double lastPaidAmount = lastPaidMonth.get(0).getAmountPaid();
						
						params = new String[4];
						params[0] = stall.getCustomer().getId()+"";
						params[1] = stall.getId()+"";
						params[2] = yearPaid+"";
						params[3] = monthLastPaid+"";
						sql = " AND cuz.customerid=? AND st.stallid=? AND mnt.yearpaid=? AND mnt.monthpaid=?";
						List<MonthTrans> collectMonth = MonthTrans.retrieve(sql, params);
						if(collectMonth.size()>1) {
							lastPaidAmount = 0d;
							for(MonthTrans m : collectMonth) {
								lastPaidAmount += m.getAmountPaid(); 
							}
						}
						
						if(yearPaid==yearToday) {//current year
							if(monthLastPaid>monthToday) {
								monthNotes += "<p><strong><h2>Advance payment for year " + yearPaid +"</h2><strong></p>";
								monthNotes +="<p>Last payment was paid on " + DateUtils.convertDateToMonthDayYear(lastPaidMonth.get(0).getDateTrans()) + " for the month of " + DateUtils.getMonthName(monthLastPaid) + " year " + yearPaid + " amounting to Php" + Currency.formatAmount(lastPaidAmount)+ " pesos only.";
								isAdvancePayment = true;
							}else {
								
										
										if(lastPaidAmount<stall.getMonthlyRental()) {
											monthNotes += "<p>" + yearPaid +"</p>";
											monthNotes = "<ul>";
											payableAmount += (stall.getMonthlyRental() - lastPaidAmount);
											stallPment += (stall.getMonthlyRental() - lastPaidAmount);
											amountPerMonth  = (stall.getMonthlyRental() - lastPaidAmount);
											monthNotes +="<li> Collectible amount from partial payment for the month of "+DateUtils.getMonthName(monthLastPaid) + " which is <a style=\"color: blue\">Php" + Currency.formatAmount(amountPerMonth) + " pesos only.</a></li>";
										}else {
											monthNotes += "<p>" + yearPaid +"</p>";
											monthNotes += "<ul>";
										}
									
										int startMonth = monthLastPaid + 1;// new month;
										for(int month = startMonth; month<=monthToday; month++) {
											//if(month == monthToday) {
												payableAmount += stall.getMonthlyRental();
												stallPment += stall.getMonthlyRental();
												amountPerMonth  = stall.getMonthlyRental();
												monthNotes +="<li><a style=\"color: red\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(amountPerMonth) + "/UNPAID</a></li>";
											//}else {
												//monthNotes +="<li><a style=\"color: read\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(stall.getMonthlyRental()) + "UNPAID</a></li>";
												//payableAmount += stall.getMonthlyRental();
											//}
										}
								
									monthNotes += "</ul>";
									
								
									
							}
						}else if(yearPaid<yearToday){//last year payable
							
							if(lastPaidAmount<stall.getMonthlyRental()) {
								monthNotes += "<p>" + yearPaid +"</p>";
								monthNotes += "<ul>";
								payableAmount += (stall.getMonthlyRental() - lastPaidAmount);
								stallPment += (stall.getMonthlyRental() - lastPaidAmount);
								amountPerMonth  = (stall.getMonthlyRental() - lastPaidAmount);
								monthNotes +="<li> Collectible amount from partial payment for the month of "+DateUtils.getMonthName(monthLastPaid) + " which is <a style=\"color: blue\">Php" + Currency.formatAmount(amountPerMonth) + " pesos only.</a></li>";
							}else {
								monthNotes += "<p>" + yearPaid +"</p>";
								monthNotes += "<ul>";
							}
						
							int startMonth = monthLastPaid + 1;// new month;
							for(int month = startMonth; month<=12; month++) {
									payableAmount += stall.getMonthlyRental();
									stallPment += stall.getMonthlyRental();
									amountPerMonth  = stall.getMonthlyRental();
									
									monthNotes +="<li><a style=\"color: red\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(amountPerMonth) + "/UNPAID</a></li>";
									
							}
					
							monthNotes += "</ul>";
							
							int yearStart = yearPaid + 1;
							//previous year
							for(int year=yearStart; year<yearToday; year++) {
								monthNotes += "<p>" + year +"</p>";
								monthNotes += "<ul>";
								for(int month = 1; month<=12; month++) {
									payableAmount += stall.getMonthlyRental();
									stallPment += stall.getMonthlyRental();
									amountPerMonth  = stall.getMonthlyRental();
									
									monthNotes +="<li><a style=\"color: red\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(amountPerMonth) + "/UNPAID</a></li>";
								}
								monthNotes += "</ul>";
							}
							//current year
							if(yearStart == yearToday) {
								monthNotes += "<p>" + yearStart +"</p>";
								monthNotes += "<ul>";
								for(int month = 1; month<=monthToday; month++) {
									payableAmount += stall.getMonthlyRental();
									stallPment += stall.getMonthlyRental();
									amountPerMonth  = stall.getMonthlyRental();
									monthNotes +="<li><a style=\"color: red\">"+DateUtils.getMonthName(month) + " = " + Currency.formatAmount(amountPerMonth) + "/UNPAID</a></li>";
								}
								monthNotes += "</ul>";
							}
							
						}
						
					}else{//end of with payment
					//start of no payment;
						int yearReg = Integer.valueOf(his.getDateStart().split("-")[0]);
						int monthReg = Integer.valueOf(his.getDateStart().split("-")[1]);
						
						int startMonth = monthReg + 1;
						
						if(yearReg==yearToday) {
							monthNotes += "<p>" + yearReg +"</p>";
							monthNotes += "<ul>";
							for(int month=startMonth; month<=monthToday; month++) {
								payableAmount += stall.getMonthlyRental();
								stallPment += stall.getMonthlyRental();
								amountPerMonth  = stall.getMonthlyRental();
								monthNotes +="<li>"+DateUtils.getMonthName(month) + "=" + Currency.formatAmount(amountPerMonth) + "</li>";
							}
							monthNotes += "</ul>";
						}else {
							monthNotes += "<p>" + yearReg +"</p>";
							monthNotes += "<ul>";
							for(int month=startMonth; month<=12; month++) {
								payableAmount += stall.getMonthlyRental();
								stallPment += stall.getMonthlyRental();
								amountPerMonth  = stall.getMonthlyRental();
								monthNotes +="<li>"+DateUtils.getMonthName(month) + "=" + Currency.formatAmount(amountPerMonth) + "</li>";
							}
							monthNotes += "</ul>";
							int newYear = yearReg + 1;
							for(int year=newYear; year<=yearToday; year++) {//collect all year not yet paid
								
								if(year==yearToday) {
									monthNotes += "<p>" + year +"</p>";
									monthNotes += "<ul>";
									for(int month=1; month<=monthToday; month++) {
										payableAmount += stall.getMonthlyRental();
										stallPment += stall.getMonthlyRental();
										amountPerMonth  = stall.getMonthlyRental();
										monthNotes +="<li>"+DateUtils.getMonthName(month) + "=" + Currency.formatAmount(amountPerMonth) + "</li>";
									}
									monthNotes += "</ul>";
								}else {
									monthNotes += "<p>" + year +"</p>";
									monthNotes += "<ul>";
									for(int month=1; month<=12; month++) {
										payableAmount += stall.getMonthlyRental();
										stallPment += stall.getMonthlyRental();
										amountPerMonth  = stall.getMonthlyRental();
										monthNotes +="<li>"+DateUtils.getMonthName(month) + "=" + Currency.formatAmount(amountPerMonth) + "</li>";
									}
									monthNotes += "</ul>";
								}
							}
						}
						
					}
					
						infoPayments += "<br/><p><strong>Details:</strong></p>";
						infoPayments +=monthNotes;
					
					if(!isAdvancePayment && payableAmount>0) {
						infoPayments += "<p><strong>"+ stallName + "</strong><a style=\"color:red\"> Php" +Currency.formatAmount(stallPment) + "</a></p>";
						infoPayments += "<p>-----------------------------------------------</p>";
					}
				}
				
			}
			if(!isAdvancePayment && payableAmount>0) {
				infoPayments += "<p><strong>Total Payable is Php " + Currency.formatAmount(payableAmount) + "<strong></p>";
			}
			String html="<html><head><title>Billing Statement</title></head><body>"+ startSentence + infoPayments + "</body></html>";
			setPaymentBillingNotes(html);
			
			if(payableAmount==0 && !isAdvancePayment) {
				infoPayments = "<p><strong><h2>"+ getUser().getFirstname() +", this owner has no payable amount.</h2><strong></p>";
				setPaymentBillingNotes(infoPayments);
			}
			
		}
		
	}
	
	public void loadORHistory() {
		ors = Collections.synchronizedList(new ArrayList<BuildingPaymentTrans>());
		
		String sql = " AND cuz.customerid=? ORDER BY py.paymentdatetrans DESC";
		String[] params = new String[1];
		params[0] = getCustomerId()+"";
		
		for(BuildingPaymentTrans pay : BuildingPaymentTrans.retrieve(sql, params)) {
			sql = " AND cuz.customerid=? AND py.bldgpyid=? AND st.stallid=? ";
			params = new String[3];
			params[0] = pay.getCustomer().getId()+"";
			params[1] = pay.getId()+"";
			params[2] = pay.getStall().getId()+"";
			List<MonthTrans> months = MonthTrans.retrieve(sql, params);
			pay.setMonths(months);
			ors.add(pay);
		}
		setPaymentBillingNotes("");
		
	}
	
	@PostConstruct
	public void init() {
		
		
		  
		  reports = Collections.synchronizedList(new ArrayList<MarketReports>());
		  
		  String sql = ""; String[] params = new String[0]; if(getSearchName()!=null &&
		  !getSearchName().isEmpty()) { sql += " AND cus.fullname like '%"+
		  getSearchName().replace("--", "") +"%'"; } int count = 1; for(Customer cz :
		  Customer.retrieve(sql, params)) {
		  
		  sql =
		  " AND cuz.customerid=? AND own.ownerdatestart is not null AND own.iscurrentowner=1"
		  ; params = new String[1]; params[0] = cz.getId()+"";
		  
		  System.out.println("customer >> " + cz.getFullName()); int cntStall = 1;
		  for(BuildingOwnerHistory his : BuildingOwnerHistory.retrieve(sql, params)) {
		  
		  System.out.println("history customer >> " + his.getCustomer().getFullName());
		  sql = " AND cuz.customerid=? AND st.isoccupied=1 AND st.stallid=?"; params =
		  new String[2]; params[0] = his.getCustomer().getId()+""; params[1] =
		  his.getStall().getId()+"";
		  
		  MarketReports rpt = new MarketReports(); rpt.setObj(his); if(cntStall==1) {
		  rpt.setCount(count++); rpt.setF1(cz.getFullName()); }else {
		  rpt.setCount(count); rpt.setF1(""); } cntStall++;
		  
		  for(BuildingStall stall : BuildingStall.retrieve(sql, params)) {
		  System.out.println("stall customer >> " + stall.getCustomer().getFullName());
		  
		  
		  rpt.setF14(stall.getBuilding().getName()); rpt.setF15(stall.getName());
		  
		  for(int month=1; month<=12; month++) {
		  
		  sql =
		  " AND cuz.customerid=? AND st.stallid=? AND (mnt.yearpaid>=? AND mnt.yearpaid<=?) AND mnt.monthpaid=?"
		  ; params = new String[5]; params[0] = cz.getId()+""; params[1] =
		  stall.getId()+""; params[2] = getYearsFromId()+""; params[3] =
		  getYearsToId()+""; params[4] = month+"";
		  
		  List<MonthTrans> months = MonthTrans.retrieve(sql, params);
		  if(months.size()>0) { double monthlyRate = stall.getMonthlyRental(); double
		  paidAmount = 0d; for(MonthTrans m : months) { paidAmount +=
		  m.getAmountPaid(); }
		  
		  if(monthlyRate==paidAmount) { rpt = paymentNote(rpt, month, "PAID"); }else
		  if(monthlyRate>paidAmount) { rpt = paymentNote(rpt, month, "PARTIAL"); }else
		  { rpt = paymentNote(rpt, month, "UNPAID"); } }else { rpt = paymentNote(rpt,
		  month, "UNPAID"); }
		  
		  }
		  
		  } reports.add(rpt); } }
		  
		 }
	
	private MarketReports paymentNote(MarketReports rpt, int monthId, String status) {
		switch(monthId) {
			case 1: rpt.setF2(status); break;
			case 2: rpt.setF3(status); break;
			case 3: rpt.setF4(status); break;
			case 4: rpt.setF5(status); break;
			case 5: rpt.setF6(status); break;
			case 6: rpt.setF7(status); break;
			case 7: rpt.setF8(status); break;
			case 8: rpt.setF9(status); break;
			case 9: rpt.setF10(status); break;
			case 10: rpt.setF11(status); break;
			case 11: rpt.setF12(status); break;
			case 12: rpt.setF13(status); break;
		}
		return rpt;
	}
	
	
	public List<String> autoPayToName(String query){
		return Customer.names(query);
	}
	
	public List<MarketReports> getReports() {
		return reports;
	}
	public void setReports(List<MarketReports> reports) {
		this.reports = reports;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public Object getSelectedObject() {
		return selectedObject;
	}
	public void setSelectedObject(Object selectedObject) {
		this.selectedObject = selectedObject;
	}


	public int getYearsFromId() {
		if(yearsFromId==0) {
			yearsFromId = DateUtils.getCurrentYear();
		}
		return yearsFromId;
	}


	public void setYearsFromId(int yearsFromId) {
		this.yearsFromId = yearsFromId;
	}


	public List getYearsFrom() {
		yearsFrom = new ArrayList<>();
		for(int yr=YEAR_START; yr<=DateUtils.getCurrentYear(); yr++ ) {
			yearsFrom.add(new SelectItem(yr, yr+""));
		}
		return yearsFrom;
	}


	public void setYearsFrom(List yearsFrom) {
		this.yearsFrom = yearsFrom;
	}


	public int getYearsToId() {
		if(yearsToId==0) {
			yearsToId = DateUtils.getCurrentYear();
		}
		return yearsToId;
	}


	public void setYearsToId(int yearsToId) {
		this.yearsToId = yearsToId;
	}


	public List getYearsTo() {
		yearsTo = new ArrayList<>();
		for(int yr=YEAR_START; yr<=DateUtils.getCurrentYear(); yr++ ) {
			yearsTo.add(new SelectItem(yr, yr+""));
		}
		return yearsTo;
	}


	public void setYearsTo(List yearsTo) {
		this.yearsTo = yearsTo;
	}
	
	public List getCustomers() {
		customers = new ArrayList<>();
		customers.add(new SelectItem(0, "Select..."));
		for(Customer cus : Customer.retrieve("", new String[0])) {
			customers.add(new SelectItem(cus.getId(), cus.getFullName()));
		}
		
		return customers;
	}
	public void setCustomers(List customers) {
		customers = customers;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public Date getPaidDate() {
		if(paidDate==null) {
			paidDate = DateUtils.getDateToday();
		}
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public String getOrNumber() {
		return orNumber;
	}

	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}

	public List<StallPayments> getStallPayments() {
		return stallPayments;
	}

	public void setStallPayments(List<StallPayments> stallPayments) {
		this.stallPayments = stallPayments;
	}

	public String getPaymentBillingNotes() {
		return paymentBillingNotes;
	}

	public void setPaymentBillingNotes(String paymentBillingNotes) {
		this.paymentBillingNotes = paymentBillingNotes;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}



	public boolean isSplitPayment() {
		return splitPayment;
	}



	public void setSplitPayment(boolean splitPayment) {
		this.splitPayment = splitPayment;
	}
	
	public List getStalls() {
		stalls = new ArrayList<>();
		stalls.add(new SelectItem(0, "Select..."));
		for(BuildingStall stall : BuildingStall.retrieve(" AND st.isoccupied=1 AND bl.isactivatebldg=1 AND cuz.customerid="+getCustomerId(), new String[0])) {
			stalls.add(new SelectItem(stall.getId(), stall.getName()));
		}
		return stalls;
	}

	public void setStalls(List stalls) {
		this.stalls = stalls;
	}

	public int getStallId() {
		return stallId;
	}

	public void setStallId(int stallId) {
		this.stallId = stallId;
	}

	public StallPayments getSelectedPayment() {
		return selectedPayment;
	}

	public void setSelectedPayment(StallPayments selectedPayment) {
		this.selectedPayment = selectedPayment;
	}

	public List<MonthTrans> getMonthPayments() {
		return monthPayments;
	}

	public void setMonthPayments(List<MonthTrans> monthPayments) {
		this.monthPayments = monthPayments;
	}

	public Map<Integer, List<MonthTrans>> getMapPayments() {
		return mapPayments;
	}

	public void setMapPayments(Map<Integer, List<MonthTrans>> mapPayments) {
		this.mapPayments = mapPayments;
	}

	public boolean isPaymentTermPartial() {
		return paymentTermPartial;
	}

	public void setPaymentTermPartial(boolean paymentTermPartial) {
		this.paymentTermPartial = paymentTermPartial;
	}

	public List<BuildingPaymentTrans> getOrs() {
		return ors;
	}

	public void setOrs(List<BuildingPaymentTrans> ors) {
		this.ors = ors;
	}
	
}
