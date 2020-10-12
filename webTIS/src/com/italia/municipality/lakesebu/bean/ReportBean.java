package com.italia.municipality.lakesebu.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import com.italia.municipality.lakesebu.controller.BankAccounts;
import com.italia.municipality.lakesebu.controller.ReportFields;
import com.italia.municipality.lakesebu.database.BankChequeDatabaseConnect;
import com.italia.municipality.lakesebu.reports.ReportCompiler;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class ReportBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 187878867767L;
	private List<ReportFields> reports = new ArrayList<>();
	private String grandTotal;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private final Path PATH_APP = Paths.get("");
	private final String sep = File.separator;
	private final String REPORT_PATH = "C:" + sep + "CheckSystem" + sep + "reports" + sep;
	private  String REPORT_NAME = "Dispense";//"DispenseChequeReport";
	
/*	public ReportBean() {
		this.reports.add(new ReportFields("1234","56789"));
		this.reports.add(new ReportFields("8907","34521"));
	}*/
	
	
	@PostConstruct
	public void init(){
		String dateToday = getDateTime();
		getAccounts();
		retrieveData(dateToday, "init");
		if(reports!=null && reports.size()>0){
			
		}else{
			System.out.println("Process me");
			ReportFields rpt = new ReportFields();
			rpt.setId(1);
			reports.add(rpt);
		}
		
	}
	
	private String search;
	public void setSearch(String search){
		this.search = search;
	}
	public String getSearch(){
		return search;
	}
	
	public String find(){
		System.out.println("find called");
		
		try{
			int val = search.indexOf(":");
			if(3==val || val==4 || val==5){
				retrieveDate(search);
			}else{
				retrieveData(search,"search");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return "search";
	}
	private void retrieveDate(String val){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		double grandAmount = 0.0;
		String[] data = val.split(":");
		String st = "";
		try{

			if("month".equalsIgnoreCase(data[0])){
				st= "'"+data[1]+"%'";
			}else if("day".equalsIgnoreCase(data[0])){
				st= "'%"+data[1]+"%'";
			}else if("year".equalsIgnoreCase(data[0])){
				st= "'%"+data[1]+"'";
			}
			sql = " SELECT * from tbl_chequedtls where date_disbursement like " + st;
			conn = BankChequeDatabaseConnect.getConnection();
			ps = conn.prepareStatement(sql);
			//ps.setString(1, data[1]);
			System.out.println("Search SQL: "+ ps.toString());
			rs = ps.executeQuery();
			
			
			reports = new ArrayList<>();
			
			while(rs.next()){
				ReportFields rpt = new ReportFields();
				rpt.setId(rs.getInt("cheque_id"));
				System.out.println("Cheque Id: " + rpt.getId());
				String amount = rs.getString("cheque_amount");
				
				if(accounts!=null){
					int ac = Integer.valueOf(rs.getString("accnt_no"));
					rpt.setAccntNumber(accounts.get(ac).getBankAccntNo());
				}else{
					rpt.setAccntNumber("");
				}
				
				rpt.setCheckNo(rs.getString("cheque_no"));
				rpt.setAccntName(rs.getString("accnt_name"));
				rpt.setBankName(rs.getString("bank_name"));
				rpt.setDate_disbursement(rs.getString("date_disbursement"));
				rpt.setAmount(formatAmount(amount));
				rpt.setPayToTheOrderOf(rs.getString("pay_to_the_order_of"));
				rpt.setAmountInWOrds(rs.getString("amount_in_words"));
				rpt.setProcessBy(rs.getString("proc_by"));
				rpt.setDate_created(rs.getTimestamp("date_created")+"");
				rpt.setDate_edited(rs.getTimestamp("date_edited")+"");
				
				grandAmount += Double.valueOf(amount);
				
				reports.add(rpt);
			}
			
			rs.close();
			ps.close();
			BankChequeDatabaseConnect.close(conn);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	private void retrieveData(String val, String mode){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = ""; 
		double grandAmount = 0.0;
		if("init".equalsIgnoreCase(mode)){
			sql = "SELECT * FROM tbl_chequedtls WHERE date_created>=? ORDER BY date_created DESC";
		}else if("search".equalsIgnoreCase(mode)){
			if("*".equalsIgnoreCase(val)){
				sql = "SELECT * FROM tbl_chequedtls ORDER BY date_created DESC";
			}else{
				sql = "SELECT * FROM tbl_chequedtls WHERE "
						+ "accnt_no=? || "
						+ "cheque_no=? || "
						+ "accnt_name=? || "
						+ "bank_name=? || "
						+ "date_disbursement=? || "
						+ "cheque_amount=? || "
						+ "pay_to_the_order_of=? || "
						+ "amount_in_words=? || "
						+ "proc_by=? || "
						+ "date_created=? || "
						+ "date_edited=?"
						+ "ORDER BY date_created DESC";
			}
		}
		try{
			conn = BankChequeDatabaseConnect.getConnection();
			ps = conn.prepareStatement(sql);
			if("search".equalsIgnoreCase(mode)){
				if(!"*".equalsIgnoreCase(val)){
					for(int i=1;i<=11; i++){
						ps.setString(i, val);
					}
					
				}
			}else if("init".equalsIgnoreCase(mode)){
				ps.setString(1, val);
			}
			System.out.println("Search SQL: "+ ps.toString());
			rs = ps.executeQuery();
			reports = new ArrayList<>();
			
			while(rs.next()){
				ReportFields rpt = new ReportFields();
				rpt.setId(rs.getInt("cheque_id"));
				System.out.println("Cheque Id: " + rpt.getId());
				String amount = rs.getString("cheque_amount");
				
				if(accounts!=null){
					int ac = Integer.valueOf(rs.getString("accnt_no"));
					rpt.setAccntNumber(accounts.get(ac).getBankAccntNo());
				}else{
					rpt.setAccntNumber("");
				}
				
				rpt.setCheckNo(rs.getString("cheque_no"));
				rpt.setAccntName(rs.getString("accnt_name"));
				rpt.setBankName(rs.getString("bank_name"));
				rpt.setDate_disbursement(rs.getString("date_disbursement"));
				rpt.setAmount(formatAmount(amount));
				rpt.setPayToTheOrderOf(rs.getString("pay_to_the_order_of"));
				rpt.setAmountInWOrds(rs.getString("amount_in_words"));
				rpt.setProcessBy(rs.getString("proc_by"));
				rpt.setDate_created(rs.getTimestamp("date_created")+"");
				rpt.setDate_edited(rs.getTimestamp("date_edited")+"");
				
				grandAmount += Double.valueOf(amount);
				
				reports.add(rpt);
			}
			rs.close();
			ps.close();
			BankChequeDatabaseConnect.close(conn);
			setGrandTotal(formatAmount(grandAmount+""));
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	Map<Integer, BankAccounts> accounts = java.util.Collections.synchronizedMap(new HashMap<Integer,BankAccounts>());
	public void getAccounts(){
		accounts = java.util.Collections.synchronizedMap(new HashMap<Integer,BankAccounts>());
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM tbl_bankaccounts";
		try{
		
			conn=BankChequeDatabaseConnect.getConnection();
			ps = conn.prepareStatement(sql);		
			rs = ps.executeQuery();
			while(rs.next()){
				
				System.out.println("Id: " + rs.getString("bank_id") + 
						" Name: "+ rs.getString("bank_account_name")
						+" Bank Account No: "+rs.getString("bank_account_no") + 
						" Bank Branch: " + rs.getString("bank_branch"));
				BankAccounts account = new BankAccounts();
				account.setBankId(rs.getInt("bank_id"));
				account.setBankAccntNo(rs.getString("bank_account_no"));
				account.setBankAccntName(rs.getString("bank_account_name"));
				account.setBankAccntBranch(rs.getString("bank_branch"));
				accounts.put(account.getBankId(), account);
				
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	private String formatAmount(String amount){
		double money = Double.valueOf(amount);
		NumberFormat format = NumberFormat.getCurrencyInstance();
		amount = format.format(money).replace("$", "");
		amount = amount.replace("Php", "");
		return amount;
	}
	
	public void setGrandTotal(String grandTotal){
		this.grandTotal = grandTotal;
	}
	public String getGrandTotal(){
		return grandTotal;
	}
	
	public void setReports(List<ReportFields> reports){
		this.reports = reports;
	}
	public List<ReportFields> getReports(){
		return reports;
	}
	
	public String writing(){
		return "welcome";
	}
	
	
	public void printReport(){
		try{
			
			compileReport(reports);	
			
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

private HtmlDataTable cheques;
public void setCheques(HtmlDataTable cheques){
	this.cheques = cheques;
}
public HtmlDataTable getCheques(){
	return cheques;
}

public String selectedCheque(){
	System.out.println("Selected click");
	return "welcome";
//	/return "welcome";
}
	
public String getDateTime(){
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	Date date = new Date();
	String _date = dateFormat.format(date);
	return _date + " 00:00:00";
}
private void compileReport(List<ReportFields> reportFields){
		
		/*System.out.println("CheckReport path: " + REPORT_PATH);
		ReportCompiler compiler = new ReportCompiler();
		System.out.println("REPORT_NAME: " +REPORT_NAME + " REPORT_PATH: " + REPORT_PATH);
		String reportLocation = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		System.out.println("Check report path: " + reportLocation);
		HashMap params = new HashMap();
		for(ReportFields rpt : reportFields){
		
		
		params.put("PARAM_ACCOUNT_NO", rpt.getAccntNumber());
		params.put("PARAM_CHECK_NO", rpt.getCheckNo());
		params.put("PARAM_DATE_DISBURSEMENT", rpt.getDate_disbursement());
		params.put("PARAM_BANK_NAME", rpt.getBankName());
		params.put("PARAM_ACCOUNT_NAME", rpt.getAccntName());
		params.put("PARAM_AMOUNT", rpt.getAmount());
		params.put("PARAM_ORDEROF", rpt.getPayToTheOrderOf());
		
		HttpSession session = SessionBean.getSession();
		
		params.put("PARAM_PROCESSED_BY", session.getAttribute("username").toString());
		//params.put("PARAM_AMOUNT_INWORDS", rpt.getAmountInWOrds());
		//params.put("PARAM_SIGNATORY1", rpt.getSignatory1());
		//params.put("PARAM_SIGNATORY2", rpt.getSignatory2());
		
		}
		
		JasperPrint print = compiler.report(reportLocation, params);
		File pdf = null;
		try{
		pdf = new File(REPORT_PATH+REPORT_NAME+".pdf");
		pdf.createNewFile();
		JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(pdf));*/
	try{
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
  		ArrayList<ReportFields> rpts = new ArrayList<ReportFields>();
  		for(ReportFields r : reportFields){
  			rpts.add(r);
  		}
  		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(rpts);
  		HashMap param = new HashMap();
  		
  		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");//new SimpleDateFormat("MM/dd/yyyy");//new SimpleDateFormat("yyyy/MM/dd hh:mm: a");
		Date date = new Date();
		String _date = dateFormat.format(date);
  		param.put("PARAM_DATE", _date);
  		param.put("PARAM_GRAND_TOTAL","Grand Total: Php "+getGrandTotal());
  		
  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
	
	
		System.out.println("pdf successfully created...");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

public void printList(List<ReportFields> reportFields){
	try{
		ReportCompiler compiler = new ReportCompiler();
	
		
		
	      
	      try {
	    	 /* String sourceFileName = compiler.compileReport("jasper_report_template", "jasper_report_template", REPORT_PATH);
	  		DataBeanList dataBeanList = new DataBeanList();
	  		ArrayList<DataBean> dataList = dataBeanList.getDataBeanList();
	  		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(dataList);
	  		HashMap parameters = new HashMap();
	  	      parameters.put("ReportTitle", "List of Contacts");
	  	      parameters.put("Author", "Prepared By Manisha");
	    	  String filejasper = JasperFillManager.fillReportToFile(
	          sourceFileName, parameters, beanCollectionDataSource);
	    	  System.out.println("jaspereportfile: " + filejasper);
	  		  JasperExportManager.exportReportToPdfFile(filejasper,REPORT_PATH+"jasper_report_template_temp.pdf");*/
	  		
	  		
	  		System.out.println("Dispense.......");
	  		String jrxmlFile = compiler.compileReport("Dispense", "Dispense", REPORT_PATH);
	  		ArrayList<ReportFields> rpts = new ArrayList<ReportFields>();
	  		for(ReportFields r : reportFields){
	  			rpts.add(r);
	  		}
	  		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(rpts);
	  		HashMap param = new HashMap();
	  		param.put("PARAM_TITLE", "DISPENSE");
	  		
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+"Dispense.pdf");
	  		System.out.println("Completed...");
	       } catch (JRException e) {
	          e.printStackTrace();
	       }
		
	}catch(Exception e){
		e.printStackTrace();
	}
}

public static void main(String[] args) {
	
	//String val = "date 2106 my name is mark";
			//val = "month:january";
			//val = "year:2016";
			//val = "day:5";
	//System.out.println(val.indexOf(":"));
	
	ReportBean bean = new ReportBean();
	List<ReportFields> r = new ArrayList<>();
	
	ReportFields f = new ReportFields();
	f.setCheckNo("111");
	f.setAccntName("hello");
	r.add(f);
	
	f = new ReportFields();
	f.setCheckNo("222");
	f.setAccntName("world");
	r.add(f);
	
	bean.printList(r);
	
}
}













