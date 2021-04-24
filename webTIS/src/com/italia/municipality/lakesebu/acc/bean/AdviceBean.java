package com.italia.municipality.lakesebu.acc.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.italia.municipality.lakesebu.acc.controller.Advice;
import com.italia.municipality.lakesebu.controller.Chequedtls;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 04/06/2021
 *
 */
@Named
@ViewScoped
@Setter
@Getter
public class AdviceBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 978657897981L;
	
	private List<Advice> advices;
	private List<Chequedtls> checks;
	private String controlNumber;
	
	private List<Chequedtls> selectedCheck;

	
	private Date dateFrom;
	private Date dateTo;
	private String searchName;
	
	private Date dateCreated;
	
	@PostConstruct
	public void init() {
		setDateCreated(DateUtils.getDateToday());
		setDateFrom(DateUtils.getDateToday());
		setDateTo(DateUtils.getDateToday());
		loadChecks();
		loadAdvices();
		
	}
	
	public void loadSelectedChk(){
		PrimeFaces pf = PrimeFaces.current();
		
		if(getSelectedCheck()!=null && getSelectedCheck().size()>0) {
			pf.executeScript("PF('dlgCreate').show()");
		}else {
			pf.executeScript("PF('dlgCreate').hide()");
		}
	}
	
	private void loadAdvices() {
		advices = new ArrayList<Advice>();
		advices = Advice.retrieve("", new String[0]);
	}
	
	public UserDtls getUser() {
		return Login.getUserLogin().getUserDtls();
	}
	
	
	public void deleteAdvice(Advice advice) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		
		if(advice.getChecks().contains("-")) {
			String[] ids = advice.getChecks().split("-");
			for(String id : ids) {
				List<Chequedtls> chk = Chequedtls.retrieve("SELECT * FROM tbl_chequedtls WHERE cheque_id="+id,new String[0]);
				if(chk!=null && chk.size()>0) {
					Chequedtls c = chk.get(0);
					c.setDate_edited(dateFormat.format(date));
					c.setHasAdvice(0);
					c.save();
				}
			}
		}else {
			List<Chequedtls> chk = Chequedtls.retrieve("SELECT * FROM tbl_chequedtls WHERE cheque_id="+advice.getChecks(),new String[0]);
			if(chk!=null && chk.size()>0) {
				Chequedtls c = chk.get(0);
				c.setDate_edited(dateFormat.format(date));
				c.setHasAdvice(0);
				c.save();
			}
		}
		advice.delete();
		loadAdvices();
		loadChecks();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void saveAdvice() {
		if(getSelectedCheck()!=null && getSelectedCheck().size()>0) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			
			Advice advice = Advice.builder()
					.dateTrans(DateUtils.getCurrentDateMonthDayYear())
					.controlNumber(getControlNumber())
					.isActive(1)
					.userDtls(getUser())
					.build();
			String check = "";
			int cnt = 1;
			for(Chequedtls chk : getSelectedCheck()) {
				if(cnt==1) {
					check = chk.getCheque_id()+"";
				}else {
					check += "-" + chk.getCheque_id()+"";
				}
				cnt++;
			}
			advice.setFundType(getSelectedCheck().get(0).getFundTypeId());
			advice.setChecks(check);
			advice.save();
			loadAdvices();
			
			
			
			for(Chequedtls chk : getSelectedCheck()) {
				chk.setDate_edited(dateFormat.format(date));
				chk.setHasAdvice(1);
				chk.save();
			}
			loadChecks();
			Application.addMessage(1, "Success", "Successfully saved.");
			
			
			PrimeFaces pf = PrimeFaces.current();
			pf.executeScript("PF('dlgCreate').hide()");
			
			
		}
	}
	
	private String  detectSearchParam(String val) {
		String sql = " AND d.hasadvice=0 ";
		if(val.contains(":")) {
			System.out.println("contain : " + val);
			String[] sp = val.split(":");
			
			int count=0;
			boolean isMonth = false;
			boolean isYear = false;
			for(String s : sp) {
				if(count==0) {
					
						
						if("month".equalsIgnoreCase(s)) {
							isMonth = true;
						}
					
						if("year".equalsIgnoreCase(s)) {
							isYear=true;
						}
					
					
				}
				if(count==1) {
					
					if(isMonth) {
						sql += " AND (d.date_disbursement>='" + DateUtils.getCurrentYear() + "-" + convertMonth(s) + "-01' AND d.date_disbursement<= '"+ DateUtils.getCurrentYear() + "-" + convertMonth(s) +"-31')";
					}
					
					if(isYear) {
						int year = DateUtils.getCurrentYear();
						
						try {
							
							year = Integer.valueOf(s);
							
							sql += " AND (d.date_disbursement>='" + year + "-01-01' AND d.date_disbursement<= '"+ year + "-12-31')";
							
						}catch(NumberFormatException num) {}
						
					}
					
				}
				count++;
			}
			
		
		}else {
			
			if(isMonth(val)) {
				sql += " AND (d.date_disbursement>='" + DateUtils.getCurrentYear() + "-" + convertMonth(val) + "-01' AND d.date_disbursement<= '"+ DateUtils.getCurrentYear() + "-" + convertMonth(val) +"-31')";
			} else {
				sql += " AND (d.cheque_no like '%"+ val +"%' OR d.pay_to_the_order_of like '%"+ val +"%' OR b.bank_account_no like '%"+ val +"%' OR b.bank_account_name like '%"+ val +"%' OR d.date_disbursement like '%" + val + "%')";
			}
		}
		return sql;
	}
	
	public static String convertMonth(String month){
		switch(month.toLowerCase()){
			case "january": return "01";
			case "february" : return "02";
			case "march" : return "03";
			case "april" : return "04";
			case "may" : return "05";
			case "june" : return "06";
			case "july" : return "07";
			case "august" : return "08";
			case "september" :  return "09";
			case "october" : return "10";
			case "november" : return "11";
			case "december" : return "12";
		}
		return DateUtils.getCurrentMonth()<10? "0" + DateUtils.getCurrentMonth() : DateUtils.getCurrentMonth()+""; 
	}
	
	private boolean isMonth(String month) {
		switch(month.toLowerCase()){
		case "january": return true; 
		case "february" : return true;
		case "march" : return true;
		case "april" : return true;
		case "may" : return true;
		case "june" : return true;
		case "july" : return true;
		case "august" : return true;
		case "september" :  return true;
		case "october" : return true;
		case "november" : return true;
		case "december" : return true;
		}
		
		return false;
	}
	
	
	public void loadChecks() {
		String sql = " AND (d.date_disbursement>=? AND d.date_disbursement<=?) AND d.hasadvice=0 ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(), "yyyy-MM-dd");
		
		if(getSearchName()!=null && !getSearchName().isEmpty() && getSearchName().length()>=4) {
			//sql += " AND ( d.cheque_no like '%"+ getSearchName() +"%' OR d.pay_to_the_order_of like '%"+ getSearchName() +"%' )";
			
			sql = detectSearchParam(getSearchName());
			params = new String[0];
		}
		
		checks = new ArrayList<Chequedtls>();
		checks = Chequedtls.retrieveChecks(sql, params);
	}
	
	
	
	
	
	
}
