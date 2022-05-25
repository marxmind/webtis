package com.italia.municipality.lakesebu.acc.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.acc.controller.EmployeePayroll;
import com.italia.municipality.lakesebu.acc.controller.PayrollGroupSeries;
import com.italia.municipality.lakesebu.enm.EmployeeType;
import com.italia.municipality.lakesebu.enm.Months;
import com.italia.municipality.lakesebu.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
@Setter
@Getter
public class PayrollPrepBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4567534568761L;
	private EmployeePayroll payData;
	
	private List months;
	private List years;
	private List stats;
	private String series;
	
	
	@PostConstruct
	public void init() {
		loadDefault();
	}
	
	public String getSeries() {
		return PayrollGroupSeries.getLatestSeriesId(EmployeeType.REGULAR);
	}
	
	private void loadDefault() {
		months = new ArrayList<>();
		years = new ArrayList<>();
		stats = new ArrayList<>();
		stats.add(new SelectItem(0, "Draft"));
		stats.add(new SelectItem(1, "Posted"));
		
		payData = new EmployeePayroll();
		payData.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		payData.setMonth(DateUtils.getCurrentMonth());
		payData.setYear(DateUtils.getCurrentYear());
		for(Months m : Months.values()) {
			months.add(new SelectItem(m.getId(), m.getName()));
		}
		for(int y=2022; y<=DateUtils.getCurrentYear(); y++) {
			years.add(new SelectItem(y, y+""));
		}
		
	}
	

}
