package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;

import com.italia.municipality.lakesebu.controller.BankAccounts;
import com.italia.municipality.lakesebu.controller.CashDisbursement;
import com.italia.municipality.lakesebu.controller.CashDisbursementReport;
import com.italia.municipality.lakesebu.licensing.controller.Words;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

@Named("cashDv")
@ViewScoped
@Setter @Getter
public class CashDVReportBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4364576864451L;
	
	private List funds;
	private CashDisbursementReport cashDvData;
	private List<CashDisbursementReport> rpts;
	private String searchParam;
	private boolean enabledTable;
	
	@PostConstruct
	public void init() {
		
		funds = new ArrayList<>();
		for(BankAccounts a : BankAccounts.retrieveAll()) {
			funds.add(new SelectItem(a.getBankId(), a.getBankAccntName()));
		}
		
		defaultValue();
		load();
	}
	
	private void defaultValue() {
		CashDisbursementReport r = CashDisbursementReport.builder()
				.dateReportTmp(new Date())
				.disbursingOfficer(Words.getTagName("disbursing-officer"))
				.designation(Words.getTagName("disbursing-officer-designation"))
				.build();
		
		List<CashDisbursement> dvs = new ArrayList<CashDisbursement>();
		int x=1;
		while(x<=90) {
			dvs.add(CashDisbursement.builder().number(x).build());
			x++;
		}
		r.setRpts(dvs);
		
		setCashDvData(r);
	}
	
	public void clickItemRpt(CashDisbursementReport rpt) {
		
	}
	
	public void print(CashDisbursementReport rpt) {
		
	}
	
	public void deleteRpt(CashDisbursementReport rpt) {
		rpt.delete();
		rpts.remove(rpt);
	}
	
	public void clear() {
		defaultValue();
	}
	
	/*
	 * public void deleteItem(CashDisbursement item) { item.delete();
	 * getCashDvData().getRpts().remove(item); }
	 */
	
	public void addItem() {
		int count = getCashDvData().getRpts().size() + 1;
		getCashDvData().getRpts().add(CashDisbursement.builder().number(count).build());
	}
	
	public void autoSave() {
		
	}
	
	public void save() {
		boolean isOk = true;
		CashDisbursementReport cashDv = getCashDvData();
		
		if(cashDv.getPeriodCovered()==null) {
			Application.addMessage(2, "Warning", "Please provide period covered details");
			isOk = false;
		}
		
		if(cashDv!=null && cashDv.getRpts().size()==0) {
			Application.addMessage(2, "Warning", "Please provide voucher details");
			isOk = false;
		}
		
		if(isOk) {
			cashDv.setDateReport(DateUtils.convertDate(cashDv.getDateReportTmp(), "yyyy-MM-dd"));
			cashDv.setIsActive(1);
			cashDv = CashDisbursementReport.save(cashDv);
			
			List<CashDisbursement> items = new ArrayList<CashDisbursement>();
			for(CashDisbursement r : cashDv.getRpts()) {
				r.setIsActive(1);
				r.setReport(cashDv);
				r = CashDisbursement.save(r);
				items.add(r);
			}
			cashDv.setRpts(items);
			Application.addMessage(1, "Success", "Successfully saved.");
		}
	}
	
	
	
	public void load() {
		rpts = new ArrayList<CashDisbursementReport>();
	}
	
	public void createItem() {
		
		boolean isOk = true;
		CashDisbursementReport cashDv = getCashDvData();
		
		if(cashDv.getPeriodCovered()==null || cashDv.getPeriodCovered().isEmpty()) {
			Application.addMessage(2, "Warning", "Please provide period covered details");
			isOk = false;
		}
		
		if(isOk) {
			cashDv.setDateReport(DateUtils.convertDate(cashDv.getDateReportTmp(), "yyyy-MM-dd"));
			cashDv.setIsActive(1);
			cashDv = CashDisbursementReport.save(cashDv);
			List<CashDisbursement> dvs = new ArrayList<CashDisbursement>();
			int x=1;
			while(x<=90) {
				dvs.add(CashDisbursement.builder().number(x).report(cashDv).build());
				x++;
			}
			getCashDvData().setRpts(dvs);
			setEnabledTable(true);
		}
	}
	
	public void onCellEdit(CellEditEvent event) {
		 try{
	        Object oldValue = event.getOldValue();
	        Object newValue = event.getNewValue();
	        
	        System.out.println("old Value: " + oldValue);
	        System.out.println("new Value: " + newValue);
	        
	        int index = event.getRowIndex();
	        String column =  event.getColumn().getHeaderText();
	        
	        if(getCashDvData().getRpts()!=null && getCashDvData().getRpts().size()>0) {
	        	
	        	CashDisbursement cz = getCashDvData().getRpts().get(index);
	        	cz.setFundId(getCashDvData().getFundId());
	        	cz.setIsActive(1);
	        	cz.setReport(getCashDvData());
	        	cz = CashDisbursement.save(cz);
	        	
	        	getCashDvData().getRpts().get(index).setId(cz.getId());//update item id
	        	
	        }
	        
		 }catch(Exception e){}  
	 }
	
}
