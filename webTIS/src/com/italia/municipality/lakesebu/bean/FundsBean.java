package com.italia.municipality.lakesebu.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.CellEditEvent;

import com.italia.municipality.lakesebu.controller.BankAccounts;
import com.italia.municipality.lakesebu.controller.CashTransactions;
import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.ReadConfig;
import com.italia.municipality.lakesebu.controller.Reports;
import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.enm.TransactionType;
import com.italia.municipality.lakesebu.reports.ReportCompiler;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.xml.BookCashBook;
import com.italia.municipality.lakesebu.xml.BookCheck;
import com.italia.municipality.lakesebu.xml.CashBookXML;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author mark italia
 * @since 02/01/2017
 * @version 1.0
 *
 */
@ManagedBean(name="fundBean", eager=true)
@ViewScoped
public class FundsBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 142478653448784L;

	private List accountNameList = new ArrayList<>();
	private int accountNameId;
	
	private String searchParticulars;
	private Date dateFrom;
	private Date dateTo;
	
	private List<CashTransactions> trans = Collections.synchronizedList(new ArrayList<CashTransactions>());
	private List<CashTransactions> selectedData;
	
	private int transId;
	private List transType;
	
	private String debitTotal;
	private String creditTotal;
	private String balanceTotal;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	
	private int departmentId;
	private List departments;
	
	@PostConstruct
	public void init(){
		
		//load xml if there are xml to be load from checkno
		try{BookCheck.loadXML();}catch(Exception e){}
		//load backup
		try{BookCashBook.loadXML(new CashTransactions());}catch(Exception e){}
		
		
		String sql = "SELECT * FROM cashtransactions WHERE cashisactive=1 AND cashDate>=? AND cashDate<=? AND bank_id=? ";
		String[] params = new String[3]; 
		if(getSearchParticulars()!=null && !getSearchParticulars().isEmpty()){
			sql += " AND cashParticulars like '%" + getSearchParticulars().replace("--", "") + "%'";
		}
		params[0] = DateUtils.convertDate(getDateFrom(),"yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getDateTo(),"yyyy-MM-dd");
		params[2] = getAccountNameId()+"";
		sql +=" ORDER BY cashDate";
		
		trans = new ArrayList<CashTransactions>();
		selectedData = new ArrayList<CashTransactions>();
		//trans = CashTransactions.retrieve(sql, params);
		
		int cnt = 1;
		double debitAmount=0d, creditAmount=0d, balancesAmount=0d;
		for(CashTransactions tran : CashTransactions.retrieve(sql, params)){
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
			CashTransactions tran = cashField();
			tran.setCnt(cnt);
			trans.add(tran);
		}
		
		//Collections.reverse(trans);
		setDebitTotal(Currency.formatAmount(debitAmount));
		setCreditTotal(Currency.formatAmount(creditAmount));
		balancesAmount = debitAmount - creditAmount;
		setBalanceTotal(Currency.formatAmount(balancesAmount));
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
	        
	        //reset transaction type
	        setTransId(0);
	 } 
	 
	 
	 
	
	 private void saveChanges(int index){
		 if(trans.size()>0){
			 int bankId = trans.get(index).getAccounts().getBankId();
			 CashTransactions tran = new CashTransactions();
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
	 
	 private void saveXML(CashTransactions cash){
		 
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
		 
		 BookCashBook.createXML(cash, xml, "BANK-"+cash.getAccounts().getBankId()+"-"+cash.getVoucherNo()+".xml");
	 }
	 
	 public void saveData(){
		 for(CashTransactions tran : trans){
			 BankAccounts accounts = new BankAccounts();
			 accounts.setBankId(getAccountNameId());
			 tran.setAccounts(accounts);
			 tran.setUserDtls(Login.getUserLogin().getUserDtls());
			 tran.save();
		 }
	 }
	 
	public void addNew(){
		int cnt = 0;
		CashTransactions tran = cashField();
		if(trans!=null && trans.size()>0){
			cnt = trans.size() + 1;
			try{tran.setDateTrans(trans.get(trans.size()-1).getDateTrans());}catch(IndexOutOfBoundsException e){}
		}
		
		tran.setCnt(cnt);
		setTransId(0);
		trans.add(tran);
	}
	 
	private static final String PATH = AppConf.PRIMARY_DRIVE.getValue() + AppConf.SEPERATOR.getValue();
	 private static final String APP_FOLDER = AppConf.APP_CONFIG_FOLDER_NAME.getValue();
	 private static final String BACKUPBANK = PATH +  APP_FOLDER + AppConf.SEPERATOR.getValue() + AppConf.BACKUPCASHBOOKBANKXML.getValue() + AppConf.SEPERATOR.getValue(); 	
	 public void deleteData() {
		 if(getSelectedData()!=null && getSelectedData().size()>0) {
			 System.out.println("deleting files...");
			 for(CashTransactions cash : getSelectedData()) {
				 File xml = new File(BACKUPBANK+"BANK-"+cash.getAccounts().getBankId()+"-"+cash.getVoucherNo()+".xml");
				 System.out.println("deleting >> " + cash.getVoucherNo());
				 try{xml.delete(); System.out.println("deleted...");}catch(Exception e) {}
				 cash.delete();
				 trans.remove(cash);
			 }
			 init();
		 }
	 }
	
	private CashTransactions cashField(){
		
		CashTransactions cash = new CashTransactions();
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
			String date = DateUtils.getFirstDayOfTheMonth("yyyy-MM-dd",DateUtils.getCurrentDateYYYYMMDD(), Locale.TAIWAN); 
			dateFrom = DateUtils.convertDateString(date, "yyyy-MM-dd");
		}
		
		return dateFrom;
	}


	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}


	public Date getDateTo() {
		
		if(dateTo==null){
			String date = DateUtils.getLastDayOfTheMonth("yyyy-MM-dd",DateUtils.getCurrentDateYYYYMMDD(), Locale.TAIWAN); 
			dateTo = DateUtils.convertDateString(date, "yyyy-MM-dd");
		}
		
		return dateTo;
	}


	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public List<CashTransactions> getTrans() {
		return trans;
	}

	public void setTrans(List<CashTransactions> trans) {
		this.trans = trans;
	}

	public List<CashTransactions> getSelectedData() {
		return selectedData;
	}

	public void setSelectedData(List<CashTransactions> selectedData) {
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
		
		return transType;
	}
	
	public void print(){ //check issued only
		if(getSelectedData().size()>0){
		//if(trans.size()>0){
			List<Reports> reports = Collections.synchronizedList(new ArrayList<Reports>());
			double total = 0d;
			for(CashTransactions tran : getSelectedData()){
			//for(CashTransactions tran : trans){
				if(tran.getTransType()==TransactionType.CHECK_ISSUED.getId() ||
						tran.getTransType()==TransactionType.COLLECTION.getId() ||
						tran.getTransType()==TransactionType.DEPOSIT.getId() ||
						tran.getTransType()==TransactionType.JEV.getId()){
					Reports rpt = new Reports();
					rpt.setF1(DateUtils.convertDateCustom(tran.getDateTrans()) );
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
					AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.CHEQUE_REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			String REPORT_NAME =ReadConfig.value(AppConf.CHECK_ISSUED);
			System.out.println("Report path " + REPORT_PATH + " name " + REPORT_NAME);
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
			
			JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
	  		HashMap param = new HashMap();
			
	  		param.put("PARAM_REPORT_TITLE","REPORT OF CHECK ISSUED");
	  		
	  		param.put("PARAM_PRINTED_DATE",DateUtils.getCurrentDateMonthDayYear());
	  		
	  		String fromMonth = DateUtils.convertDate(getDateFrom(),"yyyy-MM-dd").split("-")[1];
	  		String fromDay = DateUtils.convertDate(getDateFrom(),"yyyy-MM-dd").split("-")[2];
	  		String toMonth = DateUtils.convertDate(getDateTo(),"yyyy-MM-dd").split("-")[1];
	  		String toDay = DateUtils.convertDate(getDateTo(),"yyyy-MM-dd").split("-")[2];
	  		
	  		String dateCovered = "";
	  		if((fromMonth.equalsIgnoreCase(toMonth)) && (fromDay.equalsIgnoreCase(toDay))) {
	  			dateCovered = DateUtils.convertDateToMonthDayYear(DateUtils.convertDate(getDateTo(),"yyyy-MM-dd"));
	  		}else {
	  			if(fromMonth.equalsIgnoreCase(toMonth)){
	  				dateCovered = DateUtils.getMonthName(Integer.valueOf(fromMonth)) + " " + fromDay + " to " + toDay + ", " + DateUtils.convertDate(getDateTo(),"yyyy-MM-dd").split("-")[0];
	  			}else {
	  				dateCovered = DateUtils.getMonthName(Integer.valueOf(fromMonth)) + " " + fromDay + " to " + DateUtils.getMonthName(Integer.valueOf(toMonth)) + " " + toDay + ", " + DateUtils.convertDate(getDateTo(),"yyyy-MM-dd").split("-")[0];
		  			
	  			}
	  		}
	  		
	  		param.put("PARAM_RANGE_DATE",dateCovered);
	  		
	  		BankAccounts accnt = BankAccounts.retrieve("SELECT * FROM tbl_bankaccounts WHERE bank_id=" + getAccountNameId(), new String[0]).get(0);
	  		param.put("PARAM_ACCOUNT_NAME",accnt.getBankAccntBranch() + " " + accnt.getBankAccntNo());
			
	  		param.put("PARAM_SUB_TOTAL",Currency.formatAmount(total));
	  		
	  		if(getAccountNameId()==1){
	  			param.put("PARAM_RECEIVEDBY","ANITA PASTOR");
	  		}else if(getAccountNameId()==3 || getAccountNameId()==5){
	  			param.put("PARAM_RECEIVEDBY","EMMANUEL S. FACTORA");
	  		}else if(getAccountNameId()==2 || getAccountNameId()==4){
	  			param.put("PARAM_RECEIVEDBY","SHIRLEY D. SOLIS");
	  		}
	  		
	  		//logo
			String officialLogo = REPORT_PATH + "logo.png";
			try{File file = new File(officialLogo);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_LOGO", off);
			}catch(Exception e){e.printStackTrace();}
			
			//logo
			String officialLogotrans = REPORT_PATH + "logotrans.png";
			try{File file = new File(officialLogotrans);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_LOGO_TRANS", off);
			}catch(Exception e){e.printStackTrace();}
	  		
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
		if(getSelectedData().size()>0){
		//if(trans.size()>0){
			List<Reports> reports = Collections.synchronizedList(new ArrayList<Reports>());
			double total = 0d;
			for(CashTransactions tran : getSelectedData()){
				if(tran.getTransType()==TransactionType.COLLECTION.getId() || 
						tran.getTransType()==TransactionType.DEPOSIT.getId() ||
						tran.getTransType()==TransactionType.JEV.getId()){
					Reports rpt = new Reports();
					rpt.setF1(DateUtils.convertDateCustom(tran.getDateTrans()));
					rpt.setF2(tran.getCheckNo().equalsIgnoreCase("0")? "" : tran.getCheckNo());
					rpt.setF3(tran.getVoucherNo());
					rpt.setF4(tran.getDepartmentCode());
					rpt.setF5(tran.getParticulars());
					rpt.setF6(tran.getNaturePayment());
					rpt.setF7(tran.getdAmount());
					reports.add(rpt);
					total += tran.getDebitAmount();
				}
			}
			
			//compiling report
			String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
					AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.CHEQUE_REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			String REPORT_NAME =ReadConfig.value(AppConf.CHECK_ISSUED) +"_rcdgen";
			System.out.println("Report path " + REPORT_PATH + " name " + REPORT_NAME);
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
			
			JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
	  		HashMap param = new HashMap();
			
	  		param.put("PARAM_REPORT_TITLE","REPORT OF COLLECTION AND DEPOSIT");
	  		
	  		param.put("PARAM_PRINTED_DATE",DateUtils.getCurrentDateMonthDayYear());
	  		
	  		String fromMonth = DateUtils.convertDate(getDateFrom(),"yyyy-MM-dd").split("-")[1];
	  		String fromDay = DateUtils.convertDate(getDateFrom(),"yyyy-MM-dd").split("-")[2];
	  		String toMonth = DateUtils.convertDate(getDateTo(),"yyyy-MM-dd").split("-")[1];
	  		String toDay = DateUtils.convertDate(getDateTo(),"yyyy-MM-dd").split("-")[2];
	  		String dateCovered = "";
	  		if((fromMonth.equalsIgnoreCase(toMonth)) && (fromDay.equalsIgnoreCase(toDay))) {
	  			dateCovered = DateUtils.convertDateToMonthDayYear(DateUtils.convertDate(getDateTo(),"yyyy-MM-dd"));
	  		}else {
	  			if(fromMonth.equalsIgnoreCase(toMonth)){
	  				dateCovered = DateUtils.getMonthName(Integer.valueOf(fromMonth)) + " " + fromDay + " to " + toDay + ", " + DateUtils.convertDate(getDateTo(),"yyyy-MM-dd").split("-")[0];
	  			}else {
	  				dateCovered = DateUtils.getMonthName(Integer.valueOf(fromMonth)) + " " + fromDay + " to " + DateUtils.getMonthName(Integer.valueOf(toMonth)) + " " + toDay + ", " + DateUtils.convertDate(getDateTo(),"yyyy-MM-dd").split("-")[0];
		  			
	  			}
	  		}
	  		
	  		param.put("PARAM_RANGE_DATE",dateCovered);
	  		
	  		BankAccounts accnt = BankAccounts.retrieve("SELECT * FROM tbl_bankaccounts WHERE bank_id=" + getAccountNameId(), new String[0]).get(0);
	  		param.put("PARAM_ACCOUNT_NAME", accnt.getBankAccntBranch() + " - " + accnt.getBankAccntNo());
			
	  		param.put("PARAM_SUB_TOTAL",Currency.formatAmount(total));
	  		
	  		if(getAccountNameId()==1){
	  			param.put("PARAM_RECEIVEDBY","");
	  		}else if(getAccountNameId()==3 || getAccountNameId()==5){
	  			param.put("PARAM_RECEIVEDBY","EMMANUEL S. FACTORA");
	  		}else if(getAccountNameId()==2 || getAccountNameId()==4){
	  			param.put("PARAM_RECEIVEDBY","SHIRLEY D. SOLIS");
	  		}
	  		
	  	//logo
			String officialLogo = REPORT_PATH + "logo.png";
			try{File file = new File(officialLogo);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_LOGO", off);
			}catch(Exception e){e.printStackTrace();}
			
			//logo
			String officialLogotrans = REPORT_PATH + "logotrans.png";
			try{File file = new File(officialLogotrans);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_LOGO_TRANS", off);
			}catch(Exception e){e.printStackTrace();}
	  		
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
			for(CashTransactions tran : trans){
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
					AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.CHEQUE_REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			String REPORT_NAME =ReadConfig.value(AppConf.CASH_BOOK);
			System.out.println("Report path " + REPORT_PATH + " name " + REPORT_NAME);
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
			
			JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
	  		HashMap param = new HashMap();
			
	  		param.put("PARAM_TITLE","CASH BOOK IN BANK");
	  		
	  		param.put("PARAM_PRINTED_DATE","Printed: "+DateUtils.getCurrentDateMMDDYYYYTIME());
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
		
		for(Department dep : Department.retrieve("SELECT * FROM department order by departmentname", new String[0])){
			departments.add(new SelectItem(dep.getDepid(), dep.getDepartmentName()));
		}
		
		return departments;
	}

	public void setDepartments(List departments) {
		this.departments = departments;
	}
	
}
