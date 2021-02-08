package com.italia.municipality.lakesebu.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;

import com.italia.municipality.lakesebu.controller.BankAccounts;
import com.italia.municipality.lakesebu.controller.CashTransactionTreasury;
import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.ReadConfig;
import com.italia.municipality.lakesebu.controller.Reports;
import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.enm.TransactionType;
import com.italia.municipality.lakesebu.reports.ReportCompiler;
import com.italia.municipality.lakesebu.utils.AppGlobalVar;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.xml.BookCashBook;
import com.italia.municipality.lakesebu.xml.CashBookXML;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author mark italia
 * @since 02/09/2017
 * @version 1.0
 *
 */

@Named
@ViewScoped
public class FundTresBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 142478611448784L;

	private List accountNameList = new ArrayList<>();
	private int accountNameId;
	
	private String searchParticulars;
	private Date dateFrom;
	private Date dateTo;
	
	private List<CashTransactionTreasury> trans = Collections.synchronizedList(new ArrayList<CashTransactionTreasury>());
	private CashTransactionTreasury selectedData;
	
	private int transId;
	private List transType;
	
	private int searchTransId;
	private List searchTransType;
	
	private String debitTotal;
	private String creditTotal;
	private String balanceTotal;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	
	private int departmentId;
	private List departments;
	
	private Date dateTrans;
	private String particulars;
	private String naturePayment;
	private String voucherNo;
	private String orNumber;
	private String checkNo;
	private double amount;
	private List<CashTransactionTreasury> selections;
	
	@PostConstruct
	public void init(){
		
		//load backup xml
		try{BookCashBook.loadXML(new CashTransactionTreasury());}catch(Exception e){}
		
		String sql = "SELECT * FROM cashtransactionstreasury WHERE cashisactive=1 AND cashDate>=? AND cashDate<=? AND bank_id=? ";
		String[] params = new String[3]; 
		if(getSearchParticulars()!=null && !getSearchParticulars().isEmpty()){
			sql += " AND cashParticulars like '%" + getSearchParticulars().replace("--", "") + "%'";
		}
		
		if(getSearchTransId()>0) {
			sql += " AND cashtranstype=" + getSearchTransId();
		}
		
		params[0] = DateUtils.convertDate(getDateFrom(),"yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(),"yyyy-MM-dd");
		params[2] = getAccountNameId()+"";
		sql +=" ORDER BY cashDate";
		
		trans = Collections.synchronizedList(new ArrayList<CashTransactionTreasury>());
		//trans = CashTransactions.retrieve(sql, params);
		
		int cnt = 1;
		double debitAmount=0d, creditAmount=0d, balancesAmount=0d;
		for(CashTransactionTreasury tran : CashTransactionTreasury.retrieve(sql, params)){
			tran.setCnt(cnt++);
			tran.setdAmount(Currency.formatAmount(tran.getDebitAmount()));
			tran.setcAmount(Currency.formatAmount(tran.getCreditAmount()));
			tran.setbAmount(Currency.formatAmount(tran.getBalances()));
			tran.setTransactionName(TransactionType.nameId(tran.getTransType()));
			Department dep = null;
			try{dep = Department.retrieve("SELECT * FROM department WHERE departmentid=" + tran.getDepartment().getDepid(), new String[0]).get(0);
			tran.setDepartmentCode(dep.getCode() + "-" + dep.getDepartmentName());
			}catch(IndexOutOfBoundsException e){tran.setDepartmentCode("");}
			
			trans.add(tran);
			debitAmount += tran.getDebitAmount();
			creditAmount += tran.getCreditAmount();
		}
		
		if(trans.size()==0){ //load default data if there is no data retrieve
			CashTransactionTreasury tran = cashField();
			tran.setCnt(cnt);
			trans.add(tran);
		}
		
		//Collections.reverse(trans);
		setDebitTotal(Currency.formatAmount(debitAmount));
		setCreditTotal(Currency.formatAmount(creditAmount));
		balancesAmount = debitAmount - creditAmount;
		setBalanceTotal(Currency.formatAmount(balancesAmount));
	}
	
	public void deleteRow(CashTransactionTreasury tran) {
		tran.delete();
		init();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	 public void onCellEdit(CellEditEvent event) {
	        System.out.println("Old Value   "+ event.getOldValue()); 
	        System.out.println("New Value   "+ event.getNewValue());
	        System.out.println("Index   "+ event.getRowIndex());
	        int index = event.getRowIndex();
	        if(getTransId()!=0){
	        	trans.get(index).setTransactionName(TransactionType.nameId(getTransId()));
	        	trans.get(index).setTransType(getTransId());
	        }
	        
	        if(getDepartmentId()!=0){
	        	Department department = Department.retrieve("SELECT * FROM department WHERE departmentid="+ getDepartmentId(), new String[0]).get(0);
	        	trans.get(index).setDepartment(department);
	        	trans.get(index).setDepartmentCode(department.getCode());
	        }
	        saveChanges(index);
	        //copy xml
	        saveXML(trans.get(index));
	 } 
	
	 private void saveChanges(int index){
		 if(trans.size()>0){
			 int bankId = trans.get(index).getAccounts().getBankId();
			 CashTransactionTreasury tran = new CashTransactionTreasury();
			 System.out.println("check accounts: tmp " + getAccountNameId() + " bank_id  " + bankId);
			 if(getAccountNameId()==bankId){
				 tran = trans.get(index);
			 }else{
				 BankAccounts accounts = new BankAccounts();
				 accounts.setBankId(getAccountNameId());
				 tran.setAccounts(accounts);
			 }
			 
			 
			 tran.setDebitAmount(Double.valueOf(tran.getdAmount().replace(",", "")));
			 tran.setCreditAmount(Double.valueOf(tran.getcAmount().replace(",", "")));
			 tran.setBalances(Double.valueOf(tran.getbAmount().replace(",", "")));
			 
			 tran.setUserDtls(Login.getUserLogin().getUserDtls());
			 tran.save();
			 init();
		 }
	 }
	 
	public void clear() {
		setSelectedData(null);
		setDateTrans(DateUtils.getDateToday());
		setParticulars(null);
		setNaturePayment(null);
		setVoucherNo(null);
		setOrNumber(null);
		setCheckNo(null);
		setAmount(0);
		setDepartmentId(0);
		setTransId(0);
	}
	 
	public void showSelected(CashTransactionTreasury tran) {
		
		if(tran!=null) {
			setSelectedData(tran);
			setDateTrans(DateUtils.convertDateString(tran.getDateTrans(), "yyyy-MM-dd"));
			setParticulars(tran.getParticulars());
			setNaturePayment(tran.getNaturePayment());
			setVoucherNo(tran.getVoucherNo());
			setOrNumber(tran.getOrNumber());
			setCheckNo(tran.getCheckNo());
			setTransId(tran.getTransType());
			setDepartmentId(tran.getDepartment().getDepid());
			if(TransactionType.COLLECTION.getId()==getTransId() ||
					//TransactionType.DEPOSIT.getId()==getTransId() ||
					TransactionType.OTHER_INCOME.getId()==getTransId()) {
			
				setAmount(tran.getDebitAmount());
			}else {
				setAmount(tran.getCreditAmount());
			}
		}
		
	}
	 
	public void saveVoucherData() {
		boolean isOk = true;
		CashTransactionTreasury tran = new CashTransactionTreasury();
		if(getSelectedData()!=null) {
			tran = getSelectedData();
		}else {
			tran.setIsActive(1);
		}
		
		if(getCheckNo()==null) {
			isOk = false;
			Application.addMessage(2, "Warning", "Please provide check no.");
		}
		
		if(getVoucherNo()==null) {
			isOk = false;
			Application.addMessage(2, "Warning", "Please provide voucher no");
		}
		
		if(getAmount()<=0) {
			isOk = false;
			Application.addMessage(2, "Warning", "Please provide amount");
		}
		
		//if(getDepartmentId()==0) {
		//	isOk = false;
		//	Application.addMessage(2, "Warning", "Please provide department");
		//}
		
		if(getTransId()==0) {
			isOk = false;
			Application.addMessage(2, "Warning", "Please provide transaction type");
		}
		
		if(isOk) {
			tran.setDateTrans(DateUtils.convertDate(getDateTrans(), "yyyy-MM-dd"));
			tran.setTransType(getTransId());
			tran.setParticulars(getParticulars());
			tran.setNaturePayment(getNaturePayment());
			tran.setVoucherNo(getVoucherNo());
			tran.setOrNumber(getOrNumber());
			tran.setCheckNo(getCheckNo());
			if(TransactionType.COLLECTION.getId()==getTransId() ||
					//TransactionType.DEPOSIT.getId()==getTransId() ||
					TransactionType.OTHER_INCOME.getId()==getTransId()) {
				tran.setDebitAmount(getAmount());
			}else {
				tran.setCreditAmount(getAmount());
			}
			
			Department department = new Department();
			department.setDepid(getDepartmentId());
			tran.setDepartment(department);
			
			 BankAccounts accounts = new BankAccounts();
			 accounts.setBankId(getAccountNameId());
			 tran.setAccounts(accounts);
			 tran.setUserDtls(Login.getUserLogin().getUserDtls());
			 tran.save();
			 init();
			 Application.addMessage(1, "Success", "Successfully saved");
			 PrimeFaces pf = PrimeFaces.current();
			 pf.executeScript("PF('voucherDlg').hide()");
			 clear();
		}
	}
	 
	 private void saveXML(CashTransactionTreasury cash){
		 
		 CashBookXML xml = new CashBookXML();
		 xml.setDateTrans(cash.getDateTrans());
		 xml.setPayee(cash.getParticulars());
		 xml.setVoucherNo(cash.getVoucherNo());
		 xml.setIsActive(cash.getIsActive());
		 xml.setTransType(cash.getTransType());
		 xml.setDebitAmount(cash.getDebitAmount());
		 xml.setCreditAmount(cash.getCreditAmount());
		 xml.setBalances(cash.getBalances());
		 xml.setAccounts(cash.getAccounts());
		 xml.setUserDtls(cash.getUserDtls());
		 xml.setOrNumber(cash.getOrNumber());
		 xml.setCheckNo(cash.getCheckNo());
		 xml.setNaturePayment(cash.getNaturePayment());
		 xml.setDepartment(cash.getDepartment());
		 
		 BookCashBook.createXML(cash, xml, "TRES-"+ cash.getAccounts().getBankId()+"-"+cash.getVoucherNo()+".xml");
	 }
	 
	 public void saveData(){
		 for(CashTransactionTreasury tran : trans){
			 BankAccounts accounts = new BankAccounts();
			 accounts.setBankId(getAccountNameId());
			 tran.setAccounts(accounts);
			 tran.setUserDtls(Login.getUserLogin().getUserDtls());
			 tran.save();
		 }
	 }
	 
	public void addNew(){
		int cnt = 0;
		CashTransactionTreasury tran = cashField();
		if(trans!=null && trans.size()>0){
			cnt = trans.size() + 1;
			try{tran.setDateTrans(trans.get(trans.size()-1).getDateTrans());}catch(IndexOutOfBoundsException e){}
		}
		
		tran.setCnt(cnt);
		setTransId(0);
		trans.add(tran);
	}
	 
	private CashTransactionTreasury cashField(){
		
		CashTransactionTreasury cash = new CashTransactionTreasury();
		cash.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		cash.setParticulars("particulars");
		cash.setNaturePayment("nature of payment");
		cash.setVoucherNo(cash.voucherNumber(getAccountNameId()));
		cash.setOrNumber("");
		cash.setCheckNo(cash.checkNumber(getAccountNameId()));
		cash.setIsActive(1);
		cash.setTransType(0);
		cash.setDebitAmount(0);
		cash.setCreditAmount(0);
		cash.setBalances(0);
		cash.setdAmount("0");
		cash.setcAmount("0");
		cash.setbAmount("0");
		BankAccounts accounts = new BankAccounts();
		accounts.setBankId(getAccountNameId());
		cash.setAccounts(accounts);
		return cash;
	}
	
	
	public List getAccountNameList() {
		Login user = Login.getUserLogin();
		accountNameList = new ArrayList<>();
		for(BankAccounts a : BankAccounts.retrieve("SELECT * FROM tbl_bankaccounts", new String[0])){
			
			switch(user.getAccessLevel().getLevel()){
			case 1 :{
				accountNameList.add(new SelectItem(a.getBankId(),a.getBankAccntName() + " " + a.getBankAccntBranch()));
				break;
			}
			case 2 :{
				accountNameList.add(new SelectItem(a.getBankId(),a.getBankAccntName() + " " + a.getBankAccntBranch()));
				break;
			}
			case 3 : {
					if(a.getBankId()==3 || a.getBankId()==5){
						accountNameList.add(new SelectItem(a.getBankId(),a.getBankAccntName() + " " + a.getBankAccntBranch()));
					}
					break;
				}
			case 4 :{
				if(a.getBankId()==2 || a.getBankId()==4){
					accountNameList.add(new SelectItem(a.getBankId(),a.getBankAccntName() + " " + a.getBankAccntBranch()));
				}
				break;
			}
			case 8:
				if(a.getBankId()==1){
					accountNameList.add(new SelectItem(a.getBankId(),a.getBankAccntName() + " " + a.getBankAccntBranch()));
				}
				break;
			}
			
		}
		return accountNameList;
	}
	public void setAccountNameList(List accountNameList) {
		this.accountNameList = accountNameList;
	}
	public int getAccountNameId() {
		
		Login user = Login.getUserLogin();
			
			switch(user.getAccessLevel().getLevel()){
			case 1 :{
				if(accountNameId==0){
					accountNameId = 1;
				}
				break;
			}
			case 3 : {
				if(accountNameId==0){
					accountNameId = 3;
				}
					break;
				}
			case 4 :{
				if(accountNameId==0){
					accountNameId = 2;
				}
				break;
			}
			case 8:{
				if(accountNameId==0){
					accountNameId = 1;
				}
				break;
			}
			
		}
		
		return accountNameId;
	}
	public void setAccountNameId(int accountNameId) {
		this.accountNameId = accountNameId;
	}


	public String getSearchParticulars() {
		return searchParticulars;
	}


	public void setSearchParticulars(String searchParticulars) {
		this.searchParticulars = searchParticulars;
	}


	public Date getDateFrom() {
		
		if(dateFrom==null){
			//String date = DateUtils.getFirstDayOfTheMonth("yyyy-MM-dd",DateUtils.getCurrentDateYYYYMMDD(), Locale.TAIWAN); 
			//dateFrom = DateUtils.convertDateString(date, "yyyy-MM-dd");
			dateFrom = DateUtils.getDateToday();
		}
		
		return dateFrom;
	}


	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}


	public Date getDateTo() {
		
		if(dateTo==null){
			//String date = DateUtils.getLastDayOfTheMonth("yyyy-MM-dd",DateUtils.getCurrentDateYYYYMMDD(), Locale.TAIWAN); 
			//dateTo = DateUtils.convertDateString(date, "yyyy-MM-dd");
			dateTo = DateUtils.getDateToday();
		}
		
		return dateTo;
	}


	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public List<CashTransactionTreasury> getTrans() {
		return trans;
	}

	public void setTrans(List<CashTransactionTreasury> trans) {
		this.trans = trans;
	}

	public CashTransactionTreasury getSelectedData() {
		return selectedData;
	}

	public void setSelectedData(CashTransactionTreasury selectedData) {
		this.selectedData = selectedData;
	}

	public int getTransId() {
		return transId;
	}

	public void setTransId(int transId) {
		this.transId = transId;
	}

	public List getTransType() {
		
		transType = new ArrayList<>();
		for(TransactionType type : TransactionType.values()) {
			transType.add(new SelectItem(type.getId(), type.getName()));
		}
		/*
		transType.add(new SelectItem(TransactionType.IRA.getId(), TransactionType.IRA.getName()));
		transType.add(new SelectItem(TransactionType.DEPOSIT.getId(), TransactionType.DEPOSIT.getName()));
		transType.add(new SelectItem(TransactionType.LOAN.getId(), TransactionType.LOAN.getName()));
		transType.add(new SelectItem(TransactionType.SINKING.getId(), TransactionType.SINKING.getName()));
		transType.add(new SelectItem(TransactionType.OTHER_INCOME.getId(), TransactionType.OTHER_INCOME.getName()));
		transType.add(new SelectItem(TransactionType.OTHER_EXPENSES.getId(), TransactionType.OTHER_EXPENSES.getName()));
		transType.add(new SelectItem(TransactionType.BOTH.getId(), TransactionType.BOTH.getName()));
		transType.add(new SelectItem(TransactionType.SHARE.getId(), TransactionType.SHARE.getName()));
		transType.add(new SelectItem(TransactionType.COLLECTION.getId(), TransactionType.COLLECTION.getName()));
		transType.add(new SelectItem(TransactionType.PAYMENT.getId(), TransactionType.PAYMENT.getName()));
		transType.add(new SelectItem(TransactionType.CHECK_ISSUED.getId(), TransactionType.CHECK_ISSUED.getName()));
		transType.add(new SelectItem(TransactionType.CASH_ADVANCE.getId(), TransactionType.CASH_ADVANCE.getName()));
		*/
		return transType;
	}
	
	public void print(){ //check issued only
		if(getSelections().size()>0){
			List<Reports> reports = Collections.synchronizedList(new ArrayList<Reports>());
			double total = 0d;
			for(CashTransactionTreasury tran : getSelections()){
				if(tran.getTransType()==TransactionType.CHECK_ISSUED.getId()){
					Reports rpt = new Reports();
					rpt.setF1(tran.getDateTrans());
					rpt.setF2(tran.getCheckNo().equalsIgnoreCase("0")? "" : tran.getCheckNo());
					rpt.setF3(tran.getVoucherNo());
					try{rpt.setF4(tran.getDepartmentCode().split("-")[0]);}catch(Exception e){rpt.setF4(tran.getDepartmentCode());}
					rpt.setF5(tran.getParticulars());
					rpt.setF6(tran.getNaturePayment());
					rpt.setF7(tran.getcAmount());
					reports.add(rpt);
					total += tran.getCreditAmount();
				}
			}
			
			//compiling report
			String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
					AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			String REPORT_NAME =ReadConfig.value(AppConf.CHECK_ISSUED);
			System.out.println("Report path " + REPORT_PATH + " name " + REPORT_NAME);
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
			
			JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
	  		HashMap param = new HashMap();
			
	  		param.put("PARAM_REPORT_TITLE","REPORT OF CHECK ISSUED");
	  		
	  		param.put("PARAM_PRINTED_DATE","Printed: "+DateUtils.getCurrentDateMMMMDDYYYY());
	  		param.put("PARAM_RANGE_DATE",DateUtils.convertDate(getDateFrom(),"yyyy-MM-dd") + " to " + DateUtils.convertDate(getDateTo(),"yyyy-MM-dd"));
	  		
	  		BankAccounts accnt = BankAccounts.retrieve("SELECT * FROM tbl_bankaccounts WHERE bank_id=" + getAccountNameId(), new String[0]).get(0);
	  		param.put("PARAM_ACCOUNT_NAME","Bank Name/Account No. "+accnt.getBankAccntNo() + "-" + accnt.getBankAccntBranch());
			
	  		param.put("PARAM_SUB_TOTAL",Currency.formatAmount(total));
	  		
	  		if(getAccountNameId()==1){
	  			param.put("PARAM_RECEIVEDBY","");
	  		}else if(getAccountNameId()==3 || getAccountNameId()==5){
	  			param.put("PARAM_RECEIVEDBY","EMMANUEL S. FACTORA");
	  		}else if(getAccountNameId()==2 || getAccountNameId()==4){
	  			param.put("PARAM_RECEIVEDBY","SHIRLEY D. SOLIS");
	  		}
	  		try{
		  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
		  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
		  		}catch(Exception e){e.printStackTrace();}
				
		  		try{
		  		File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
				 FacesContext faces = FacesContext.getCurrentInstance();
				 ExternalContext context = faces.getExternalContext();
				 HttpServletResponse response = (HttpServletResponse)context.getResponse();
					
			     BufferedInputStream input = null;
			     BufferedOutputStream output = null;
			     
			     try{
			    	 
			    	 // Open file.
			            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

			            // Init servlet response.
			            response.reset();
			            response.setHeader("Content-Type", "application/pdf");
			            response.setHeader("Content-Length", String.valueOf(file.length()));
			            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
			            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			            // Write file contents to response.
			            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			            int length;
			            while ((length = input.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			            }

			            // Finalize task.
			            output.flush();
			    	 
			     }finally{
			    	// Gently close streams.
			            close(output);
			            close(input);
			     }
			     
			     // Inform JSF that it doesn't need to handle response.
			        // This is very important, otherwise you will get the following exception in the logs:
			        // java.lang.IllegalStateException: Cannot forward after response has been committed.
			        faces.responseComplete();
			        
				}catch(Exception ioe){
					ioe.printStackTrace();
				}
	  		
		}
	}
	
	public void printCollection(){ //Deposit and collection only
		if(getSelections().size()>0){
			List<Reports> reports = Collections.synchronizedList(new ArrayList<Reports>());
			//double total = 0d;
			String dateReport = DateUtils.getCurrentDateMMMMDDYYYY();
			int cnt=0;
			
			Map<String, CashTransactionTreasury> unsorted = new LinkedHashMap<String, CashTransactionTreasury>();
			for(CashTransactionTreasury tran : getSelections()){
				unsorted.put(tran.getVoucherNo(), tran);
			}
			Map<String, CashTransactionTreasury> sorted = new TreeMap<String, CashTransactionTreasury>(unsorted);
			for(CashTransactionTreasury tran : sorted.values()){
				if(tran.getTransType()==TransactionType.COLLECTION.getId() 
						|| tran.getTransType()==TransactionType.DEPOSIT.getId() 
						|| tran.getTransType()==TransactionType.IRA.getId()
						|| tran.getTransType()==TransactionType.LOAN.getId()){
					Reports rpt = new Reports();
					if(cnt==0) {
						dateReport = DateUtils.convertDateToMonthDayYear(tran.getDateTrans());
						rpt.setF1(tran.getDateTrans());
					}
					
					rpt.setF2(tran.getCheckNo().equalsIgnoreCase("0")? "" : tran.getCheckNo());
					rpt.setF3(tran.getVoucherNo());
					//rpt.setF4(tran.getDepartmentCode());
					rpt.setF5(tran.getParticulars());
					rpt.setF6(tran.getNaturePayment());
					if(tran.getTransType()==TransactionType.COLLECTION.getId()){
						rpt.setF7(tran.getdAmount());
					}
					if(tran.getTransType()==TransactionType.DEPOSIT.getId() 
							|| tran.getTransType()==TransactionType.IRA.getId()
							|| tran.getTransType()==TransactionType.LOAN.getId()){
						rpt.setF7(tran.getcAmount());
					}
					reports.add(rpt);
					//total += tran.getDebitAmount();
					
					cnt++;
				}
			}
			int size = reports.size();
			//compiling report
			String REPORT_PATH = AppGlobalVar.REPORT_FOLDER;//AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
					//AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			String REPORT_NAME = AppGlobalVar.TREASURER_COLLECTION_DEPOSIT_REPORT;//ReadConfig.value(AppConf.CHECK_ISSUED);
			
			
			/*
			if(size>30 && size<=32) {
				REPORT_NAME = AppGlobalVar.TREASURER_COLLECTION_DEPOSIT_OVER_32_REPORT;
			}
			if(size>32 && size<=54) {
				REPORT_NAME = AppGlobalVar.TREASURER_COLLECTION_DEPOSIT_OVER_35_REPORT;
			}*/
			
			
			
			System.out.println("Report path " + REPORT_PATH + " name " + REPORT_NAME);
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
			
			
			
			JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
	  		HashMap param = new HashMap();
			
	  		param.put("PARAM_REPORT_TITLE","REPORT OF COLLECTION AND DEPOSIT");
	  		
	  		param.put("PARAM_PRINTED_DATE", dateReport);
	  		//param.put("PARAM_RANGE_DATE",DateUtils.convertDate(getDateFrom(),"yyyy-MM-dd") + " to " + DateUtils.convertDate(getDateTo(),"yyyy-MM-dd"));
	  		param.put("PARAM_RANGE_DATE",dateReport);
	  		BankAccounts accnt = BankAccounts.retrieve("SELECT * FROM tbl_bankaccounts WHERE bank_id=" + getAccountNameId(), new String[0]).get(0);
	  		param.put("PARAM_ACCOUNT_NAME","Bank Name/Account No. "+accnt.getBankAccntNo() + "-" + accnt.getBankAccntBranch());
			
	  		//param.put("PARAM_SUB_TOTAL",Currency.formatAmount(total));
	  		
	  		if(getAccountNameId()==1){
	  			param.put("PARAM_RECEIVEDBY","");
	  		}else if(getAccountNameId()==3 || getAccountNameId()==5){
	  			param.put("PARAM_RECEIVEDBY","EMMANUEL S. FACTORA");
	  		}else if(getAccountNameId()==2 || getAccountNameId()==4){
	  			param.put("PARAM_RECEIVEDBY","SHIRLEY D. SOLIS");
	  		}
	  		try{
		  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
		  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
		  		}catch(Exception e){e.printStackTrace();}
				
		  		try{
		  		File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
				 FacesContext faces = FacesContext.getCurrentInstance();
				 ExternalContext context = faces.getExternalContext();
				 HttpServletResponse response = (HttpServletResponse)context.getResponse();
					
			     BufferedInputStream input = null;
			     BufferedOutputStream output = null;
			     
			     try{
			    	 
			    	 // Open file.
			            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

			            // Init servlet response.
			            response.reset();
			            response.setHeader("Content-Type", "application/pdf");
			            response.setHeader("Content-Length", String.valueOf(file.length()));
			            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
			            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			            // Write file contents to response.
			            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			            int length;
			            while ((length = input.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			            }

			            // Finalize task.
			            output.flush();
			    	 
			     }finally{
			    	// Gently close streams.
			            close(output);
			            close(input);
			     }
			     
			     // Inform JSF that it doesn't need to handle response.
			        // This is very important, otherwise you will get the following exception in the logs:
			        // java.lang.IllegalStateException: Cannot forward after response has been committed.
			        faces.responseComplete();
			        
				}catch(Exception ioe){
					ioe.printStackTrace();
				}
	  		
		}
	}
	
	private void close(Closeable resource) {
	    if (resource != null) {
	        try {
	            resource.close();
	        } catch (IOException e) {
	            // Do your thing with the exception. Print it, log it or mail it. It may be useful to 
	            // know that this will generally only be thrown when the client aborted the download.
	            e.printStackTrace();
	        }
	    }
	}
	
	public void printAll(){
		if(trans.size()>0){
			List<Reports> reports = Collections.synchronizedList(new ArrayList<Reports>());
			double baltotal = 0d,debitTotal=0d, creditTotal=0d;
			for(CashTransactionTreasury tran : trans){
				Reports rpt = new Reports();
				rpt.setF1(tran.getDateTrans());
				rpt.setF2(tran.getCheckNo().equalsIgnoreCase("0")? "" : tran.getCheckNo());
				rpt.setF3(tran.getVoucherNo());
				rpt.setF4(tran.getOrNumber());
				rpt.setF5(tran.getDepartmentCode());
				rpt.setF6(tran.getParticulars());
				rpt.setF7(tran.getNaturePayment());
				rpt.setF8(tran.getdAmount());
				rpt.setF9(tran.getcAmount());
				reports.add(rpt);
				debitTotal += tran.getDebitAmount();
				creditTotal += tran.getCreditAmount();
			}
			baltotal = debitTotal - creditTotal;
			//compiling report
			String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
					AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			String REPORT_NAME =ReadConfig.value(AppConf.CASH_BOOK);
			System.out.println("Report path " + REPORT_PATH + " name " + REPORT_NAME);
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
			
			JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
	  		HashMap param = new HashMap();
			
	  		param.put("PARAM_TITLE","CASH BOOK IN TREASURY");
	  		
	  		param.put("PARAM_PRINTED_DATE","Printed: "+DateUtils.getCurrentDateMMMMDDYYYY());
	  		param.put("PARAM_RANGE_DATE",DateUtils.convertDate(getDateFrom(),"yyyy-MM-dd") + " to " + DateUtils.convertDate(getDateTo(),"yyyy-MM-dd"));
	  		
	  		BankAccounts accnt = BankAccounts.retrieve("SELECT * FROM tbl_bankaccounts WHERE bank_id=" + getAccountNameId(), new String[0]).get(0);
	  		param.put("PARAM_ACCOUNT_NAME","Bank Name/Account No. "+accnt.getBankAccntNo() + "-" + accnt.getBankAccntBranch());
			
	  		param.put("PARAM_DEBIT_TOTAL",Currency.formatAmount(debitTotal));
	  		param.put("PARAM_CREDIT_TOTAL",Currency.formatAmount(creditTotal));
	  		param.put("PARAM_BALANCE_TOTAL",Currency.formatAmount(baltotal));
	  		
	  		try{
		  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
		  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
		  		}catch(Exception e){e.printStackTrace();}
				
		  		try{
		  		File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
				 FacesContext faces = FacesContext.getCurrentInstance();
				 ExternalContext context = faces.getExternalContext();
				 HttpServletResponse response = (HttpServletResponse)context.getResponse();
					
			     BufferedInputStream input = null;
			     BufferedOutputStream output = null;
			     
			     try{
			    	 
			    	 // Open file.
			            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

			            // Init servlet response.
			            response.reset();
			            response.setHeader("Content-Type", "application/pdf");
			            response.setHeader("Content-Length", String.valueOf(file.length()));
			            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
			            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			            // Write file contents to response.
			            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			            int length;
			            while ((length = input.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			            }

			            // Finalize task.
			            output.flush();
			    	 
			     }finally{
			    	// Gently close streams.
			            close(output);
			            close(input);
			     }
			     
			     // Inform JSF that it doesn't need to handle response.
			        // This is very important, otherwise you will get the following exception in the logs:
			        // java.lang.IllegalStateException: Cannot forward after response has been committed.
			        faces.responseComplete();
			        
				}catch(Exception ioe){
					ioe.printStackTrace();
				}
	  		
		}
	}
	
	public void setTransType(List transType) {
		this.transType = transType;
	}

	public String getDebitTotal() {
		return debitTotal;
	}

	public void setDebitTotal(String debitTotal) {
		this.debitTotal = debitTotal;
	}

	public String getCreditTotal() {
		return creditTotal;
	}

	public void setCreditTotal(String creditTotal) {
		this.creditTotal = creditTotal;
	}

	public String getBalanceTotal() {
		return balanceTotal;
	}

	public void setBalanceTotal(String balanceTotal) {
		this.balanceTotal = balanceTotal;
	}

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public List getDepartments() {
		
		departments = new ArrayList<>();
		
		for(Department dep : Department.retrieve("SELECT * FROM department order by code", new String[0])){
			//departments.add(new SelectItem(dep.getDepid(), dep.getDepartmentName()));
			departments.add(new SelectItem(dep.getDepid(), dep.getCode()));
		}
		
		return departments;
	}

	public void setDepartments(List departments) {
		this.departments = departments;
	}

	public Date getDateTrans() {
		if(dateTrans==null) {
			dateTrans = DateUtils.getDateToday();
		}
		return dateTrans;
	}

	public void setDateTrans(Date dateTrans) {
		this.dateTrans = dateTrans;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public List<CashTransactionTreasury> getSelections() {
		return selections;
	}

	public void setSelections(List<CashTransactionTreasury> selections) {
		this.selections = selections;
	}

	public int getSearchTransId() {
		return searchTransId;
	}

	public void setSearchTransId(int searchTransId) {
		this.searchTransId = searchTransId;
	}

	public List getSearchTransType() {
		searchTransType = new ArrayList<>();
		searchTransType.add(new SelectItem(0, "All Types"));
		for(TransactionType type : TransactionType.values()) {
			searchTransType.add(new SelectItem(type.getId(), type.getName()));
		}
		return searchTransType;
	}

	public void setSearchTransType(List searchTransType) {
		this.searchTransType = searchTransType;
	}
	
}

