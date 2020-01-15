package com.italia.municipality.lakesebu.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.italia.municipality.lakesebu.controller.BankAccounts;
import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.Email;
import com.italia.municipality.lakesebu.controller.ReadConfig;
import com.italia.municipality.lakesebu.controller.Reports;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.controller.Voucher;
import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.enm.EmailType;
import com.italia.municipality.lakesebu.enm.TransactionType;
import com.italia.municipality.lakesebu.reports.ReportCompiler;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class DailyReport {
	
	private static final String EMAIL_FOLDER = AppConf.PRIMARY_DRIVE.getValue() + File.separator + 
			AppConf.APP_CONFIG_FOLDER_NAME.getValue() + File.separator + "email" + File.separator;
	
	private static final String LOG_FOLDER = AppConf.PRIMARY_DRIVE.getValue() + File.separator + 
			AppConf.APP_CONFIG_FOLDER_NAME.getValue() + File.separator + "log" + File.separator;
	
	private static final String REPORT_FOLDER = AppConf.PRIMARY_DRIVE.getValue() + File.separator + 
			AppConf.APP_CONFIG_FOLDER_NAME.getValue() + File.separator + "dailyreport" + File.separator;
	
	public static void main(String[] args) {
		String log = "";
		if(!isUserFileExist()) {
			log += "File User is not existing....\n";
			log +="Start saving new report user....\n";
			saveUsers();
			log +="End saving new report user....\n";
		}
		
		if(isDateFileExist()) {
			
			if(readDate().equalsIgnoreCase(DateUtils.getCurrentDateYYYYMMDD())) {
				System.out.println("No report to be generate....");
			}else {
			
				log +="Start fetching data for report generation....\n";
				collectReport();
				log +="Done fetching data for report generation....\n";
				log +="Start saving new report date generation....\n";
				saveDate();
				log +="End saving new report date generation....\n";
				saveLog(log);
			}
		}else {
			log +="File date is not existing....\n";
			log +="Start saving new report date generation....\n";
			saveDate();
			log +="End saving new report date generation....\n";
			
			if(readDate().equalsIgnoreCase(DateUtils.getCurrentDateYYYYMMDD())) {
				System.out.println("No report to be generate....");;
			}else {
			
				log +="Start fetching data for report generation....\n";
				collectReport();
				log +="Done fetching data for report generation....\n";
				log +="Start saving new report date generation....\n";
				saveDate();
				log +="End saving new report date generation....\n";
				saveLog(log);
				
			}
		}
		
	}
	
	public static boolean isUserFileExist() {
		File file = new File(REPORT_FOLDER + "reportUsers.tis");
		if(file.exists()) {
			System.out.println("File exist... " + REPORT_FOLDER);
			return true;
		}
		
		return false;
	}
	
	public static void saveUsers() {
		
		File file = new File(REPORT_FOLDER);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		String msg = "Do not delete this file\n";
		msg += "Date Created: " + DateUtils.getCurrentDateMMMMDDYYYY();
		msg += "\nAuthor: MARXMIND\n";
		msg += "-------------------\n";
		msg += "\n";
		String val = "";
		for(int i=1; i<=5; i++) {
			if(i>1) {
				val += ","+i;
			}else {
				val += i;
			}
		}
		msg += "Users=" + val;
		File email = new File(REPORT_FOLDER + "reportUsers.tis");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static String readUsers() {
		
		String emailPath = REPORT_FOLDER + "reportUsers.tis";
		
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream(emailPath));
			return prop.getProperty("Users");
		}catch(Exception e) {}
		
		return "5";
	}
	
	public static boolean isDateFileExist() {
		String pathFile = REPORT_FOLDER + "reportDate.tis";
		File file = new File(pathFile);
		if(file.exists()) {
			System.out.println("isDateFileExist>>> File exist... " + pathFile);
			return true;
		}
		
		return false;
	}
	
	public static String readDate() {
		
		String emailPath = REPORT_FOLDER + "reportDate.tis";		
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream(emailPath));
			return prop.getProperty("Date");
		}catch(Exception e) {}
		
		return DateUtils.getCurrentDateYYYYMMDD();
	}
	
	public static void saveDate() {
		
		File file = new File(REPORT_FOLDER);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		String msg = "Do not delete this file\n";
		msg += "Date Created: " + DateUtils.getCurrentDateMMMMDDYYYY();
		msg += "\nAuthor: MARXMIND\n";
		msg += "-------------------\n";
		msg += "\n";
		msg += "Date="+DateUtils.getCurrentDateYYYYMMDD();
		File email = new File(REPORT_FOLDER + "reportDate.tis");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
public static void saveLog(String log) {
		
		File file = new File(LOG_FOLDER);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		String msg = "Log File\n";
		msg += "Date Created: " + DateUtils.getCurrentDateMMMMDDYYYY();
		msg += "\nAuthor: MARXMIND\n";
		msg += "-------------------\n";
		msg += log;
		File email = new File(LOG_FOLDER + "webtis-runner-log-"+DateUtils.getCurrentDateMMDDYYYYTIMEPlain()+".log");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void createReport(List<Reports> reports, String accountName, double total) {
		//compiling report
		String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
				AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.CHEQUE_REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
		String REPORT_NAME ="dailyreport";
		System.out.println("Report path " + REPORT_PATH + " name " + REPORT_NAME);
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
  		HashMap param = new HashMap();
		
  		param.put("PARAM_REPORT_TITLE","CHECK ISSUED REPORT");
  		
  		param.put("PARAM_PRINTED_DATE","Printed: "+DateUtils.getCurrentDateMMDDYYYYTIME());
  		param.put("PARAM_RANGE_DATE",DateUtils.convertDateToMonthDayYear(readDate()));
  		
  		param.put("PARAM_ACCOUNT_NAME",accountName);
  		param.put("PARAM_SUB_TOTAL",Currency.formatAmount(total));
  		
  		
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
		
		String fileName = accountName.replace(" ", "") + readDate();
			try{
		  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
		  	    JasperExportManager.exportReportToPdfFile(jrprint,EMAIL_FOLDER+ fileName +".pdf");
	  		}catch(Exception e){e.printStackTrace();}
		
		//create temporary file - this file will be transfer in attachment in webapps	
		tempEmailFolder(fileName);	
	}
	
	public static void tempEmailFolder(String fileName) {
		String tmpFolder = EMAIL_FOLDER + "tmpemail" + File.separator;
		File fileTmp = new File(tmpFolder);
		if(!fileTmp.isDirectory()) {
			fileTmp.mkdir();
		}
		String oldFile = EMAIL_FOLDER + fileName + ".pdf";
		String newFile = tmpFolder + fileName + ".pdf";
		
		File file = new File(oldFile);
        try{
			Files.copy(file.toPath(), (new File(newFile)).toPath(),
			        StandardCopyOption.REPLACE_EXISTING);
			}catch(IOException e){
				e.printStackTrace();
			}
		
	}
	
	/*private static void copyFileToWebPath(String fileName){
		
		Properties prop = new Properties();
		try{
			prop.load(new FileInputStream(REPORT_FOLDER+ "webapp.tis"));
		}catch(Exception e) {}	
		
		String driveFile =  EMAIL_FOLDER +  fileName + ".pdf";
		System.out.println("copyFileToWebPath = " + driveFile);
		File emailFile = new File(driveFile);
		if(emailFile.exists()) {
			
			String pathToSave = prop.getProperty("webapp-path");
            System.out.println("path save = " + pathToSave);
            File file = new File(driveFile);
            try{
    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){
    				e.printStackTrace();
    			}
		}
	}*/
	
	private static void collectReport() {
		
		String htmlHead = "";
		
		htmlHead += "<html><head><title>System Generated Reports</title></head><body><form>";
		
		htmlHead += "<p><center><h2><strong>Generated Report for "+ DateUtils.convertDateToMonthDayYear(readDate()) +"</strong></h2></center></p>";
		htmlHead += "<br/>";
		//msg += "<p><h3><strong>Please see below details for the daily report</strong></h3></p>";
		htmlHead += "<br/>";
		
		String htmlDoc ="";
		
		String sqlAccounts = "select * from tbl_bankaccounts";
		List<String> lstAccounts = Collections.synchronizedList(new ArrayList<>());
		for(BankAccounts ac : BankAccounts.retrieve(sqlAccounts, new String[0])) {
			
			List<Reports> reports = Collections.synchronizedList(new ArrayList<Reports>());
			htmlDoc += "<p><h2>"+ ac.getBankAccntName().toUpperCase() +"</h2></p>";
			
			htmlDoc += "<table border=\"1\">";
			htmlDoc += "<tr style=\"background-color: black;color: white;font-weight: bold\">";
			htmlDoc += "<td width=\"100\">Date</td><td width=\"100\">Check No</td><td width=\"300\">Payee</td><td width=\"300\">Nature of Payment</td><td width=\"300\">Department</td><td width=\"100\">Amount</td>";
			htmlDoc += "</tr>";
			
			sqlAccounts = "SELECT * FROM voucher WHERE vDate=? AND bank_id=? AND vtranstype=? ORDER BY checkno";
			String[] params = new String[3];
			params[0] = readDate();
			params[1] = ac.getBankId()+"";
			params[2] = TransactionType.CHECK_ISSUED.getId()+"";
			
			double amount=0d;
			boolean hasIssuedCheck = false;
			for(Voucher tran : Voucher.retrieve(sqlAccounts, params)){
				
					tran.setdAmount(Currency.formatAmount(tran.getAmount()));
					tran.setTransactionName(TransactionType.nameId(tran.getTransType()));
					Department dep = null;
					try{dep = Department.retrieve("SELECT * FROM department WHERE departmentid=" + tran.getDepartment().getDepid(), new String[0]).get(0);
					tran.setDepartmentCode(dep.getCode());
					tran.setDepartmentName(dep.getDepartmentName());
					tran.setDepartment(dep);
					}catch(IndexOutOfBoundsException e){tran.setDepartmentCode("");}
					
					htmlDoc += "<tr style=\"background-color: white;color: black;font-weight: bold\">";
					htmlDoc += "<td>"+ tran.getDateTrans() +"</td><td>"+ tran.getCheckNo() +"</td><td>"+tran.getPayee()+"</td><td>"+ tran.getNaturePayment() +"</td><td>"+ tran.getDepartmentName() +"</td><td>"+ tran.getdAmount()+"</td>";
					htmlDoc += "</tr>";
					
					
					amount += tran.getAmount();
					
					Reports rpt = new Reports();
					rpt.setF1(tran.getDateTrans());
					rpt.setF2(tran.getCheckNo().equalsIgnoreCase("0")? "" : tran.getCheckNo());
					rpt.setF4(tran.getDepartmentName());
					rpt.setF5(tran.getPayee());
					rpt.setF6(tran.getNaturePayment());
					rpt.setF7(tran.getdAmount());
					reports.add(rpt);
					
					hasIssuedCheck = true;
			}
			
			
			htmlDoc += "<tr style=\"background-color: black;color: white;font-weight: bold\">";
			htmlDoc += "<td>Total</td><td></td><td></td><td></td><td></td><td>"+ Currency.formatAmount(amount)+"</td>";
			htmlDoc += "</tr>";
			htmlDoc += "</table>";
			
			if(hasIssuedCheck) {
				createReport(reports, ac.getBankAccntName().toUpperCase(), amount);
				lstAccounts.add(ac.getBankAccntName().toUpperCase());
			}
			
		}
		
		String htmlFooter ="</form></body></html>";
		
		String html = htmlHead;
		html += htmlDoc;
		html += htmlFooter;
		
		
		
		int cnt=1;
		String toMailUser = "";
		boolean isCheckNote=false;
		for(String id : readUsers().split(",")) {
			
					String sql = "SELECT * FROM userdtls WHERE isactive=1 AND jobtitleid=?";
					String[] params = new String[1];
					params[0] = id;
					List<UserDtls> toUsers = UserDtls.retrieve(sql, params);
					
					if(toUsers.size()>0) {
						isCheckNote=true;
						if(toUsers.size()>1) {
							String toM = "";
							int cntM=1;
							for(UserDtls user : toUsers) {
								if(cntM>1) {
									toM += ":"+user.getUserdtlsid()+"";
								}else {
									toM = user.getUserdtlsid()+"";
								}
								cntM++;	
							}
							if(cnt>1) {
								toMailUser += ":"+toM;
							}else {
								toMailUser = toM;
							}
							
						}else {
							if(cnt>1) {
								toMailUser += ":"+toUsers.get(0).getUserdtlsid()+"";
							}else {
								toMailUser = toUsers.get(0).getUserdtlsid()+"";
							}
						}
					}	
				
			
			cnt++;
		}
		
		System.out.println("send to " + toMailUser);
		
		isCheckNote=true;
		if(isCheckNote && !toMailUser.isEmpty()) {
			
			boolean isMore = false;
			try {
				String[] em = toMailUser.split(":");
				isMore=true;
		    }catch(Exception ex) {}
			if(isMore) {
				String dateofissue = readDate();
				for(String sendTo : toMailUser.split(":")) {
					Email e = new Email();
					e.setSendDate(DateUtils.getCurrentDateYYYYMMDD());
					e.setTitle("Report for ["+ DateUtils.convertDateToMonthDayYear(dateofissue) +"] Transactions");
					
					e.setType(EmailType.INBOX.getId());
					e.setIsOpen(0);
					e.setIsDeleted(0);
					e.setIsActive(1);
					
					e.setToEmail(toMailUser);
					e.setPersonCopy(Long.valueOf(sendTo));
					e.setFromEmail("0");
					
					
					String fileName = DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "-" + sendTo;
					Email.saveAttachment(fileName, html, "html");
					
					String msg = "<p><strong>Hi</strong></p>";
					msg += "<br/>";
					msg += "<p>Please see attached file for the report transactions</p>";
					msg += "<br/>";
					msg += "<p>Summary : <a href=\"/webTIS/faces/attachment/"+ fileName +".html\" target=\"_blank\">Click here to see the attachment</a></p>";
					msg += "<br/>";
					for(String acc : lstAccounts) {
						msg += "<p>Report for "+acc+" : <a href=\"/webTIS/faces/attachment/"+acc.replace(" ", "")+ dateofissue +".pdf\" target=\"_blank\">Click here to see the attachment</a></p>";
						msg += "<br/>";
					}
					
					Email.emailSavePath(msg, fileName);
					//Email.transferAttachment(fileName + ".html");
					e.setContendId(fileName);
					e.save();
					
				}
			}else {
				String dateofissue = readDate();
				Email e = new Email();
				e.setSendDate(DateUtils.getCurrentDateYYYYMMDD());
				e.setTitle("Report for ["+ DateUtils.convertDateToMonthDayYear(dateofissue) +"] Transactions");
				
				e.setType(EmailType.INBOX.getId());
				e.setIsOpen(0);
				e.setIsDeleted(0);
				e.setIsActive(1);
				
				e.setToEmail(toMailUser);
				e.setPersonCopy(Long.valueOf(toMailUser));
				e.setFromEmail("0");
				
				
				String fileName = DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "-" + toMailUser;
				Email.saveAttachment(fileName, html, "html");
				
				String msg = "<p><strong>Hi</strong></p>";
				msg += "<br/>";
				msg += "<p>Please see attached file for the report transactions</p>";
				msg += "<br/>";
				msg += "<p>Summary : <a href=\"/webTIS/faces/attachment/"+ fileName +".html\" target=\"_blank\">Click here to see the attachment</a></p>";
				msg += "<br/>";
				for(String acc : lstAccounts) {
					msg += "<p>Report for "+acc+" : <a href=\"/webTIS/faces/attachment/"+acc.replace(" ", "")+ dateofissue +".pdf\" target=\"_blank\">Click here to see the attachment</a></p>";
					msg += "<br/>";
				}
				
				Email.emailSavePath(msg, fileName);
				//Email.transferAttachment(fileName + ".html");
				e.setContendId(fileName);
				e.save();
				
			}
		}
		
		
	}

}
