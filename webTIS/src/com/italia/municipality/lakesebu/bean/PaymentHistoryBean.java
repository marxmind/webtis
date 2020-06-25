package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.italia.municipality.lakesebu.controller.LandPayor;
import com.italia.municipality.lakesebu.controller.PenaltyCalculation;
import com.italia.municipality.lakesebu.enm.PenalyMonth;
import com.italia.municipality.lakesebu.utils.DateUtils;

import jdk.jfr.Name;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 5/29/2020
 *
 */
@ManagedBean(name="paymentHistoryBean", eager=true)
@ViewScoped
public class PaymentHistoryBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 32456757687756341L;

	private Date datePaid;
	private double penaltyAmount1;
	private double penaltyAmount2;
	private double totalAmount1;
	private double totalAmount2;
	private String ORNumber;
	private String yearPaid;
	private LandPayor landPayor;
	private String payorName="Select Owner";
	private String searchPayor;
	private double assessedValue;
	private double annualTax;
	private double taxDue;
	private String tdNumber;
	
	
	private List yearFrom; 
	private int yearFromId; 
	private List yearTo; 
	private int yearToId;
	 
	
	private List<LandPayor> lands = new ArrayList<LandPayor>();
	
	@PostConstruct
	public void init() {
		loadYears();
	}
	
	public void createNew() {
		clearAll();
	}
	
	public void clearAll() {
		setDatePaid(DateUtils.getDateToday());
		setPenaltyAmount1(0);
		setTotalAmount1(0);
		setPenaltyAmount2(0);
		setTotalAmount2(0);
		setORNumber("");
		setYearPaid("");
		setLandPayor(null);
		setAssessedValue(0);
		setAnnualTax(0);
		setTaxDue(0);
		setPayorName("Select Owner");
		lands = new ArrayList<LandPayor>();
	}
	
	public void loadPayor() {
		lands = new ArrayList<LandPayor>();
		String[] params = new String[0];
		String sql = " AND p.payorname like '%"+ getSearchPayor() +"%'";
		if(getSearchPayor()!=null && !getSearchPayor().isEmpty()) {
			lands = LandPayor.load(sql, params);
		}
		
	}
	
	public void selectedLand(LandPayor land) {
		setLandPayor(land);
		setPayorName(land.getPayor().getFullName());
		setAssessedValue(land.getLandValue());
		double annual = land.getLandValue() * 0.10;
		setAnnualTax(annual * 0.01);
		setTaxDue(annual * 0.01);
		setTdNumber(land.getTaxDeclarionNo());
		calculatePenalty();
	}
	
	private void loadYears() {
		int currentYear = DateUtils.getCurrentYear() + 1;// current year plus the future year
		
		yearFrom = new ArrayList<>();
		yearTo = new ArrayList<>();
		for(int y=1985; y<=currentYear; y++) {
			yearFrom.add(new SelectItem(y, y+""));
			yearTo.add(new SelectItem(y, y+""));
		}
	}
	
	public void calculateTaxDue() {
		int totalYear = getYearToId() - getYearFromId();
		double tax = getAnnualTax() * (totalYear + 1);
		System.out.println("calculate tax due >> " + tax);
		setTaxDue(tax * 0.01);
		calculatePenalty();
	}
	
	public void calculateTotal(String type) {
	
		double total = 0;
		if("BASIC".equalsIgnoreCase(type)) {
			total = getTaxDue() + getPenaltyAmount1();
			setTotalAmount1(total);
		}else {
			total = getTaxDue() + getPenaltyAmount2();
			setTotalAmount2(total);
		}
	}
	
	private void calculatePenalty() {
		double yearPenalty = 0d;
		double taxDue = getTaxDue();
		double totalPenalty = 0d;
		if(getYearFromId()==DateUtils.getCurrentYear() && getYearToId()==DateUtils.getCurrentYear()) {
			yearPenalty = PenaltyCalculation.monthPenalty(getYearFromId(), PenalyMonth.month(DateUtils.getCurrentMonth()));
			if(DateUtils.getCurrentMonth()<=3) {//no penalty
				totalPenalty = yearPenalty - taxDue;
			}else {
				totalPenalty = yearPenalty + taxDue;
			}
			
		}else {
			for(int year= getYearFromId(); year<=getYearToId(); year++) {
				
				if(DateUtils.getCurrentYear()==year) {
					yearPenalty = PenaltyCalculation.monthPenalty(year, PenalyMonth.month(DateUtils.getCurrentMonth()));
					if(DateUtils.getCurrentMonth()<=3) {//no penalty
						totalPenalty += yearPenalty - taxDue;
					}else {
						totalPenalty += yearPenalty + taxDue;
					}
				}else {
					yearPenalty = PenaltyCalculation.monthPenalty(year, PenalyMonth.month(12));
					totalPenalty += yearPenalty + taxDue;
				}
				
			}
		}
		setPenaltyAmount1(totalPenalty);
		calculateTotal("BASIC");
		setPenaltyAmount2(totalPenalty);
		calculateTotal("SEF");
		
	}
	
	public Date getDatePaid() {
		if(datePaid==null) {
			datePaid = DateUtils.getDateToday();
		}
		return datePaid;
	}

	public void setDatePaid(Date datePaid) {
		this.datePaid = datePaid;
	}

	public String getORNumber() {
		return ORNumber;
	}

	public void setORNumber(String oRNumber) {
		ORNumber = oRNumber;
	}

	public String getYearPaid() {
		return yearPaid;
	}

	public void setYearPaid(String yearPaid) {
		this.yearPaid = yearPaid;
	}

	public LandPayor getLandPayor() {
		return landPayor;
	}

	public void setLandPayor(LandPayor landPayor) {
		this.landPayor = landPayor;
	}

	public String getPayorName() {
		return payorName;
	}

	public void setPayorName(String payorName) {
		this.payorName = payorName;
	}

	public String getSearchPayor() {
		return searchPayor;
	}

	public void setSearchPayor(String searchPayor) {
		this.searchPayor = searchPayor;
	}

	public double getPenaltyAmount1() {
		return penaltyAmount1;
	}

	public void setPenaltyAmount1(double penaltyAmount1) {
		this.penaltyAmount1 = penaltyAmount1;
	}

	public double getPenaltyAmount2() {
		return penaltyAmount2;
	}

	public void setPenaltyAmount2(double penaltyAmount2) {
		this.penaltyAmount2 = penaltyAmount2;
	}

	public double getTotalAmount1() {
		return totalAmount1;
	}

	public void setTotalAmount1(double totalAmount1) {
		this.totalAmount1 = totalAmount1;
	}

	public double getTotalAmount2() {
		return totalAmount2;
	}

	public void setTotalAmount2(double totalAmount2) {
		this.totalAmount2 = totalAmount2;
	}

	public List<LandPayor> getLands() {
		return lands;
	}

	public void setLands(List<LandPayor> lands) {
		this.lands = lands;
	}

	public double getAssessedValue() {
		return assessedValue;
	}

	public void setAssessedValue(double assessedValue) {
		this.assessedValue = assessedValue;
	}

	public double getTaxDue() {
		return taxDue;
	}

	public void setTaxDue(double taxDue) {
		this.taxDue = taxDue;
	}

	public double getAnnualTax() {
		return annualTax;
	}

	public void setAnnualTax(double annualTax) {
		this.annualTax = annualTax;
	}

	public List getYearFrom() {
		return yearFrom;
	}

	public void setYearFrom(List yearFrom) {
		this.yearFrom = yearFrom;
	}

	public int getYearFromId() {
		if(yearFromId==0){
			yearFromId = DateUtils.getCurrentYear();
		}
		return yearFromId;
	}

	public void setYearFromId(int yearFromId) {
		this.yearFromId = yearFromId;
	}

	public List getYearTo() {
		return yearTo;
	}

	public void setYearTo(List yearTo) {
		this.yearTo = yearTo;
	}

	public int getYearToId() {
		if(yearToId==0){
			yearToId = DateUtils.getCurrentYear();
		}
		return yearToId;
	}

	public void setYearToId(int yearToId) {
		this.yearToId = yearToId;
	}

	public String getTdNumber() {
		return tdNumber;
	}

	public void setTdNumber(String tdNumber) {
		this.tdNumber = tdNumber;
	}
	
}
