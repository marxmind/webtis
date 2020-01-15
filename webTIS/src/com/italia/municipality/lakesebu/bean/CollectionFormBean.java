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

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;

import com.italia.municipality.lakesebu.controller.BankAccounts;
import com.italia.municipality.lakesebu.controller.CollectionInfo;
import com.italia.municipality.lakesebu.controller.Collector;
import com.italia.municipality.lakesebu.controller.Form11Report;
import com.italia.municipality.lakesebu.controller.IssuedForm;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.ReadConfig;
import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.enm.FormStatus;
import com.italia.municipality.lakesebu.enm.FormType;
import com.italia.municipality.lakesebu.reports.ReportCompiler;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author Mark Italia
 * @since 08-11-2018
 * @version 1.0
 *
 */
//Not in used
@ManagedBean(name="colBean", eager=true)
@ViewScoped
public class CollectionFormBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 77776454541L;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	
	private Date receivedDate;
	
	private int formTypeId;
	private List formTypes;
	
	private long beginningNo;
	private long endingNo;
	private int quantity;
	
	private int collectorId;
	private List collectors;
	
	private long issuedId;
	private List issueds;
	
	private CollectionInfo selectedForm;
	private IssuedForm issuedData;
	private List<CollectionInfo> forms = Collections.synchronizedList(new ArrayList<CollectionInfo>());
	
	private int monthId;
	private List months;
	
	private List<Form11Report> reports = Collections.synchronizedList(new ArrayList<Form11Report>());
	
	private double amount;
	private double totalAmount;
	
	@PostConstruct
	public void init() {
		
		forms = Collections.synchronizedList(new ArrayList<CollectionInfo>());
		forms =  CollectionInfo.retrieve(" ORDER BY cl.collectorname", new String[0]);
		//Collections.reverse(forms);
	}
	
	public void calculateEndingNo() {
		long ending = (getBeginningNo()) + (getQuantity()==0? 0 : getQuantity()-1);
		System.out.println("begin: " + getBeginningNo() + " pcs: " + getQuantity());
		System.out.println("ending: " + ending);
		setEndingNo(ending);
	}
	
	public void createNew() {
		setFormTypeId(0);
		setReceivedDate(null);
		setBeginningNo(0);
		setEndingNo(0);
		setQuantity(0);
		setCollectorId(0);
		setSelectedForm(null);
		issueds = new ArrayList<>();
	}
	
	public void saveData() {
		
		CollectionInfo form = new CollectionInfo();
		boolean isOk = true;
		
		if(getSelectedForm()!=null) {
			form = getSelectedForm();
		}else {
			form.setIsActive(1);
		}
		
		if(getBeginningNo()<=0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Serial From");
		}
		
		if(getEndingNo()<=0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Serial To");
		}
		
		
		if(getQuantity()<=0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Quantity");
		}
		
		if(getCollectorId()<=0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Collector");
		}
		
		if(getAmount()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide amount");
		}
		
		if(isOk) {
			
			form.setReceivedDate(DateUtils.convertDate(getReceivedDate(), "yyyy-MM-dd"));
			form.setFormType(getFormTypeId());
			form.setPcs(getQuantity());
			form.setBeginningNo(getBeginningNo());
			form.setEndingNo(getEndingNo());
			form.setAmount(getAmount());
			long pcs = getIssuedData().getEndingNo() - getBeginningNo();
			form.setPrevPcs(Integer.valueOf(pcs+""));
			
			Collector collector = new Collector();
			collector.setId(getCollectorId());
			form.setCollector(collector);
			
			IssuedForm issued = new IssuedForm();
			issued.setId(getIssuedId());
			form.setIssuedForm(issued);
			
			//tag as all issued if the ending balance is match with the current collection ending series
			if(getIssuedData()!=null) {
				if(getEndingNo()==getIssuedData().getEndingNo()) {
					
					getIssuedData().setCollector(collector);
					
					getIssuedData().setStatus(FormStatus.ALL_ISSUED.getId());
					getIssuedData().save();
					
					form.setStatus(FormStatus.ALL_ISSUED.getId());
					
				}else {
					form.setStatus(FormStatus.NOT_ALL_ISSUED.getId());
				}
			}else {
				form.setStatus(FormStatus.NOT_ALL_ISSUED.getId());
			}
			
			form.save();
			
			
			
			createNew();
			init();
			Application.addMessage(1, "Success", "Successfully saved.");
		}
		
	}
	
	public void loadIssuedForm() {
		issueds = new ArrayList<>();
		
		String sql = " AND frm.formstatus=1 AND cl.isid=?";
		String[] params = new String[1];
		params[0] = getCollectorId()+"";
		
		List<IssuedForm> forms = IssuedForm.retrieve(sql, params);
		if(forms!=null && forms.size()>1) {
			int x=1;
			for(IssuedForm form : forms) {
				issueds.add(new SelectItem(form.getId(), form.getBeginningNo() +"-" + form.getEndingNo()));
				if(x==1) {
					setBeginningNo(form.getEndingNo());
					setFormTypeId(form.getFormType());
					setIssuedId(form.getId());
					loadLatestSeries();
				}
				x++;
			}
		}else if(forms!=null && forms.size()==1) {
			issueds.add(new SelectItem(forms.get(0).getId(), forms.get(0).getBeginningNo() +"-" + forms.get(0).getEndingNo()));
			setBeginningNo(forms.get(0).getEndingNo());
			setFormTypeId(forms.get(0).getFormType());
			setIssuedId(forms.get(0).getId());
			loadLatestSeries();
		}else {
			issueds.add(new SelectItem(0, "No Issued Form"));
		}
		
	}
	
	public void loadLatestSeries() {
		String sql = " AND cl.isid=? AND sud.logid=? ORDER BY frm.colid DESC limit 1";
		String[] params = new String[2];
		params[0] = getCollectorId()+"";
		params[1] = getIssuedId()+"";
		
		List<CollectionInfo> infos = CollectionInfo.retrieve(sql, params);
		if(infos!=null && infos.size()>0) {
			CollectionInfo info = infos.get(0);
			setBeginningNo(info.getEndingNo()+1);
			setFormTypeId(info.getFormType());
			long qty = info.getIssuedForm().getEndingNo() - getBeginningNo();
			setQuantity(Integer.valueOf(qty+"")+1);
			setEndingNo(info.getIssuedForm().getEndingNo());
			setIssuedData(info.getIssuedForm());
		}else {
			sql = " AND frm.formstatus=1 AND cl.isid=? AND frm.logid=?";
			params = new String[2];
			params[0] = getCollectorId()+"";
			params[1] = getIssuedId()+"";
			List<IssuedForm> forms = IssuedForm.retrieve(sql, params);
			setBeginningNo(forms.get(0).getBeginningNo());
			setEndingNo(forms.get(0).getEndingNo());
			setQuantity(forms.get(0).getPcs());
			setFormTypeId(forms.get(0).getFormType());
			setIssuedId(forms.get(0).getId());
			setIssuedData(forms.get(0));
		}
	}
	
	public void deleteRow(IssuedForm form) {
		form.delete();
		init();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void clickItem(CollectionInfo form) {
		setFormTypeId(form.getFormType());
		setReceivedDate(DateUtils.convertDateString(form.getReceivedDate(),"yyyy-MM-dd"));
		setBeginningNo(form.getBeginningNo());
		setEndingNo(form.getEndingNo());
		setQuantity(form.getPcs());
		setCollectorId(form.getCollector().getId());
		setSelectedForm(form);
	}
	
	/*public Form11Report reportIssued(IssuedForm isform) {
		
		Form11Report rpt = new Form11Report();
		
		if("Stock".equalsIgnoreCase(isform.getCollector().getName())) {
			rpt.setF1(FormType.nameId(isform.getFormType()));
			
			int logmonth = Integer.valueOf(isform.getIssuedDate().split("-")[1]);
			
			if(logmonth==getMonthId()) {
				
				rpt.setF2("");
				rpt.setF3("");
				rpt.setF4("");
				
				String be1= isform.getBeginningNo()+"";
				int be2 = be1.length();
				rpt.setF5(isform.getPcs()+"");
				rpt.setF6(be2==7? "0"+be1 : be1);
				
				String en1= isform.getEndingNo()+"";
				int en2 = en1.length();
				rpt.setF7(en2==7? "0"+en1 : en1);
				
			}else {
			
				String be1= isform.getBeginningNo()+"";
				int be2 = be1.length();
				rpt.setF2(isform.getPcs()+"");
				rpt.setF3(be2==7? "0"+be1 : be1);
				
				String en1= isform.getEndingNo()+"";
				int en2 = en1.length();
				rpt.setF4(en2==7? "0"+en1 : en1);
				
				rpt.setF5("");
				rpt.setF6("");
				rpt.setF7("");
			
			}
			
			//issued
			rpt.setF8("");
			rpt.setF9("");
			rpt.setF10("");
			
			
			//ending balance
			
			
			rpt.setF11("");
			rpt.setF12("");
			rpt.setF13("");
			
			//remarks
			rpt.setF14("");
			
			Collector col = Collector.retrieve(isform.getCollector().getId());
			if(col.getDepartment().getCode().equalsIgnoreCase("1091")) {
				rpt.setF15(col.getName());
			}else {
				rpt.setF15(col.getDepartment().getDepartmentName());
			}
			
		}else {
		
		rpt.setF1(FormType.nameId(isform.getFormType()));
		
		int logmonth = Integer.valueOf(isform.getIssuedDate().split("-")[1]);
		
		if(logmonth==getMonthId()) {
			//write in receipt
			rpt.setF2("");
			rpt.setF3("");
			rpt.setF4("");
			
			String be1= isform.getBeginningNo()+"";
			int be2 = be1.length();
			rpt.setF5(isform.getPcs()+"");
			rpt.setF6(be2==7? "0"+be1 : be1);
				
			String en1= isform.getEndingNo()+"";
			int en2 = en1.length();
			rpt.setF7(en2==7? "0"+en1 : en1);
		
			
			
		}else {
		//Write in beginning balance
			String be1= isform.getBeginningNo()+"";
			int be2 = be1.length();
			rpt.setF2(isform.getPcs()+"");
			rpt.setF3(be2==7? "0"+be1 : be1);
			
			String en1= isform.getEndingNo()+"";
			int en2 = en1.length();
			rpt.setF4(en2==7? "0"+en1 : en1);
			
			rpt.setF5("");
			rpt.setF6("");
			rpt.setF7("");
		}
		
		//issued
		rpt.setF8("");
		rpt.setF9("No Issuance");
		rpt.setF10("");
		
		
		//ending balance
		
		
		rpt.setF11(isform.getPcs()+"");
		
		String enbeg1= isform.getBeginningNo()+"";
		int enbeg2 = enbeg1.length();
		rpt.setF12(enbeg2==7? "0"+enbeg1 : enbeg1);
		
		String enending1= isform.getEndingNo()+"";
		int enending2 = enending1.length();
		rpt.setF13(enending2==7? "0"+enending1 : enending1);
		
		//remarks
		rpt.setF14("");
		
		Collector col = Collector.retrieve(isform.getCollector().getId());
		if(col.getDepartment().getCode().equalsIgnoreCase("1091")) {
			rpt.setF15(col.getName().replace("F.L Lopez-", ""));
		}else {
			rpt.setF15(col.getDepartment().getDepartmentName());
		}
		
		}
		
		return rpt;
	
	}*/
	
	/*public Form11Report reportCollectionInfo(CollectionInfo info){

		Form11Report rpt = new Form11Report();
		
		rpt.setF1(FormType.nameId(info.getFormType()));
		
		int logmonth = Integer.valueOf(info.getIssuedForm().getIssuedDate().split("-")[1]);
		int logDay = Integer.valueOf(info.getIssuedForm().getIssuedDate().split("-")[2]);
		
		if(logmonth==getMonthId() && logDay == DateUtils.getCurrentDay()) {
			//write in receipt
			rpt.setF2("");
			rpt.setF3("");
			rpt.setF4("");
			
			String be1= info.getBeginningNo()+"";
			int be2 = be1.length();
			
			if(info.getPrevPcs()==49) {
				rpt.setF5("50");
				rpt.setF6(be2==7? "0"+be1 : be1);
			}else {
				int qty = info.getPrevPcs()+1;
				rpt.setF5(qty+"");
				
				long begNo = info.getBeginningNo();
				be1= begNo+"";
				be2 = be1.length();
				rpt.setF6(be2==7? "0"+be1 : be1);
			}
			
			
			String en1= info.getIssuedForm().getEndingNo()+"";
			int en2 = en1.length();
			rpt.setF7(en2==7? "0"+en1 : en1);
		
			
			
		}else {
		//Write in beginning balance
			String be1= info.getBeginningNo()+"";
			int be2 = be1.length();
			
			if(info.getPrevPcs()==49) {
				rpt.setF2("50");
				rpt.setF3(be2==7? "0"+be1 : be1);
			}else {
				
				String sql = " AND sud.logid=?";
				String params[]= new String[1];
				params[0]= info.getIssuedForm().getId()+"";
				List<CollectionInfo> infos = CollectionInfo.retrieve(sql, params);
				
				if(infos!=null && infos.size()>1) {
					int qty = info.getPrevPcs()+1;
					rpt.setF2(qty+"");
					
					long begNo = info.getBeginningNo() - info.getPrevPcs();
					be1= begNo+"";
					be2 = be1.length();
					rpt.setF3(be2==7? "0"+be1 : be1);
				}else {//correction for those who has a beginning quantity not equal to 49
					int qty = info.getPrevPcs()+1;
					rpt.setF2(qty+"");
					
					long begNo = info.getBeginningNo();
					be1= begNo+"";
					be2 = be1.length();
					rpt.setF3(be2==7? "0"+be1 : be1);
				}
			}
			
			
			String en1= info.getIssuedForm().getEndingNo()+"";
			int en2 = en1.length();
			rpt.setF4(en2==7? "0"+en1 : en1);
		
			rpt.setF5("");
			rpt.setF6("");
			rpt.setF7("");
		}
		//issued
		rpt.setF8(info.getPcs()+"");
		
		String beg1= info.getBeginningNo()+"";
		int beg2 = beg1.length();
		rpt.setF9(beg2==7? "0"+beg1 : beg1);
		
		//String en1= info.getIssuedForm().getEndingNo()+"";
		String en1= info.getEndingNo()+"";
		int en2 = en1.length();
		rpt.setF10(en2==7? "0"+en1 : en1);
		
		
		//ending balance
		
		long endingQty = info.getIssuedForm().getEndingNo() - info.getEndingNo();
		
		if(endingQty==0) {
			rpt.setF11("");
			rpt.setF12("All Issued");
			rpt.setF13("");
		}else {
			rpt.setF11(endingQty+"");
			long enNumber = info.getEndingNo() + 1;
			String enbeg1= enNumber+"";
			int enbeg2 = enbeg1.length();
			rpt.setF12(enbeg2==7? "0"+enbeg1 : enbeg1);
			
			String enen1= info.getIssuedForm().getEndingNo()+"";
			int enen2 = enen1.length();
			rpt.setF13(enen2==7? "0"+enen1 : enen1);
		}
		//remarks
		rpt.setF14("");
		
		Collector col = Collector.retrieve(info.getCollector().getId());
		if(col.getDepartment().getCode().equalsIgnoreCase("1091")) {
			rpt.setF15(col.getName().replace("F.L Lopez-", ""));
		}else {
			rpt.setF15(col.getDepartment().getDepartmentName());
		}
		
		
		return rpt;
	}
	
	public Form11Report reportLastCollectionInfo(CollectionInfo info){

		Form11Report rpt = null;
		
		long endingQty = info.getIssuedForm().getEndingNo() - info.getEndingNo();
		
		if(endingQty>0) {
		rpt = new Form11Report();	
		rpt.setF1(FormType.nameId(info.getFormType()));
		rpt.setF2(endingQty+"");
		
		String enbeg1= info.getEndingNo()+"";
		int enbeg2 = enbeg1.length();
		rpt.setF3(enbeg2==7? "0"+enbeg1 : enbeg1);
		
		String enen1= info.getIssuedForm().getEndingNo()+"";
		int enen2 = enen1.length();
		rpt.setF4(enen2==7? "0"+enen1 : enen1);

		//Receipt
		rpt.setF5("");
		rpt.setF6("");
		rpt.setF7("");
		
		//issued
		rpt.setF8("");
		rpt.setF9("No Issuance");
		rpt.setF10("");
		
		
		//ending balance
		rpt.setF11(endingQty+"");
		rpt.setF12(enbeg2==7? "0"+enbeg1 : enbeg1);
		rpt.setF13(enen2==7? "0"+enen1 : enen1);
		
		//remarks
		rpt.setF14("");
		
		Collector col = Collector.retrieve(info.getCollector().getId());
		if(col.getDepartment().getCode().equalsIgnoreCase("1091")) {
			rpt.setF15(col.getName().replace("F.L Lopez-", ""));
		}else {
			rpt.setF15(col.getDepartment().getDepartmentName());
		}
		}
		
		return rpt;
	}*/
	
	public void printMonth() {
		String sql = "";
		String[] params = new String[0];
		
		reports = Collections.synchronizedList(new ArrayList<Form11Report>());
		for(FormType form : FormType.values()) {
			
			//sql = " AND frm.formstatus="+ FormStatus.HANDED.getId() +" AND frm.formtypelog="+form.getId();
			sql = " AND frm.formtypelog=?";
			params = new String[1];
			params[0] = form.getId()+"";
			System.out.println(">>>>>>>>>> " + sql);
			for(IssuedForm is : IssuedForm.retrieve(sql, params)) {
				
				sql = " AND (frm.receiveddate>=? AND frm.receiveddate<=?) AND sud.logid=?";
				params = new String[3];
				params[0] = DateUtils.getCurrentYear() + "-" + (getMonthId()>=10? getMonthId() : "0"+ getMonthId()) + "-01";
				params[1] = DateUtils.getCurrentYear() + "-" + (getMonthId()>=10? getMonthId() : "0"+ getMonthId()) + "-31";
				params[2] = is.getId()+"";
				boolean noIssuance = true;
				for(CollectionInfo info : CollectionInfo.retrieve(sql, params)) {
					reports.add(reportCollectionInfo(info));
					System.out.println("has collection >> " + info.getCollector().getName());
					noIssuance = false;
				}
				
				if(noIssuance) {
					
					//if no issuance for current month
					//check the last issuance if available
					sql = " AND frm.receiveddate<=? AND sud.logid=? ORDER BY frm.colid DESC limit 1";
					params = new String[2];
					int month = getMonthId() - 1;
					params[0] = DateUtils.getCurrentYear() + "-" + (month>=10? month : "0"+ month) + "-31";
					params[1] = is.getId()+"";
					System.out.println("checking previous ");
					List<CollectionInfo> infos = CollectionInfo.retrieve(sql, params);
					System.out.println("end checking previous ");
					if(infos!=null && infos.size()>0) {
						Form11Report rpt = reportLastCollectionInfo(infos.get(0));
						if(rpt!=null) {
							reports.add(rpt);
						}
					}else {
						reports.add(reportIssued(is));
					}
					
					
				}
				
			}
			
		}
		
		
		//compiling report
		String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
				AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.CHEQUE_REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
		String REPORT_NAME =ReadConfig.value(AppConf.FORM11_REPORT);
		System.out.println("Report path " + REPORT_PATH + " name " + REPORT_NAME);
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
  		HashMap param = new HashMap();
		
  		param.put("PARAM_REPORT_TITLE","CONSOLIDATED REPORT OF ACCOUNTABILITY FOR ACCOUNTABLE FORMS");
  		param.put("PARAM_MUNICIPALITY","Municipality of Lake Sebu");
  		String dateMonth = DateUtils.getCurrentYear() + "-" + (getMonthId()>=10? getMonthId() : "0"+ getMonthId()) + "-01";
  		param.put("PARAM_DATE_RANGE","For the month of "+DateUtils.getMonthName(getMonthId()) + " 1-" + DateUtils.getLastDayOfTheMonth("yyyy-MM-dd", dateMonth, Locale.TAIWAN, DateUtils.getCurrentYear(),getMonthId()).split("-")[2] +", " + DateUtils.getCurrentYear());
  		param.put("PARAM_REPORT_NO","Report No." + DateUtils.getCurrentYear() +"-" + (getMonthId()>9? getMonthId() : "0"+ getMonthId()));
  		
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
	
	public int getFormTypeId() {
		if(formTypeId==0) {
			formTypeId=1;
		}
		return formTypeId;
	}
	public void setFormTypeId(int formTypeId) {
		this.formTypeId = formTypeId;
	}
	public List getFormTypes() {
		formTypes = new ArrayList<>();
		
		for(FormType form : FormType.values()) {
			formTypes.add(new SelectItem(form.getId(), form.getName()));
		}
		
		return formTypes;
	}
	public void setFormTypes(List formTypes) {
		this.formTypes = formTypes;
	}
	public long getBeginningNo() {
		return beginningNo;
	}
	public void setBeginningNo(long beginningNo) {
		this.beginningNo = beginningNo;
	}
	public long getEndingNo() {
		return endingNo;
	}
	public void setEndingNo(long endingNo) {
		this.endingNo = endingNo;
	}

	public int getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(int collectorId) {
		this.collectorId = collectorId;
	}

	public List getCollectors() {
		collectors = new ArrayList<>();
		collectors.add(new SelectItem(0, "Select Collector"));
		for(Collector col : Collector.retrieve("", new String[0])) {
			collectors.add(new SelectItem(col.getId(), col.getDepartment().getDepartmentName()+"/"+col.getName()));
		}
		
		return collectors;
	}

	public void setCollectors(List collectors) {
		this.collectors = collectors;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public CollectionInfo getSelectedForm() {
		return selectedForm;
	}

	public void setSelectedForm(CollectionInfo selectedForm) {
		this.selectedForm = selectedForm;
	}

	public List<CollectionInfo> getForms() {
		return forms;
	}

	public void setForms(List<CollectionInfo> forms) {
		this.forms = forms;
	}

	public Date getReceivedDate() {
		if(receivedDate==null) {
			receivedDate = DateUtils.getDateToday();
		}
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public long getIssuedId() {
		return issuedId;
	}

	public void setIssuedId(long issuedId) {
		this.issuedId = issuedId;
	}

	public List getIssueds() {
		return issueds;
	}

	public void setIssueds(List issueds) {
		this.issueds = issueds;
	}

	public IssuedForm getIssuedData() {
		return issuedData;
	}

	public void setIssuedData(IssuedForm issuedData) {
		this.issuedData = issuedData;
	}

	public int getMonthId() {
		if(monthId==0) {
			monthId = DateUtils.getCurrentMonth();
		}
		return monthId;
	}

	public void setMonthId(int monthId) {
		this.monthId = monthId;
	}

	public List getMonths() {
		months = new ArrayList<>();
		months.add(new SelectItem(0, "All Months"));
		for(int m=1; m<=12;m++) {
			months.add(new SelectItem(m, DateUtils.getMonthName(m)));
		}
		return months;
	}

	public void setMonths(List months) {
		this.months = months;
	}

	public List<Form11Report> getReports() {
		return reports;
	}

	public void setReports(List<Form11Report> reports) {
		this.reports = reports;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public Form11Report reportCollectionInfo(CollectionInfo info){
		System.out.println("reportCollectionInfo>>>");
		Form11Report rpt = new Form11Report();
		
		rpt.setF1(FormType.nameId(info.getFormType()));
		
		int logmonth = Integer.valueOf(info.getIssuedForm().getIssuedDate().split("-")[1]);
		int logDay = Integer.valueOf(info.getIssuedForm().getIssuedDate().split("-")[2]);
		
		String f3 = "";
		String f4 = "";
		String f6 = "";
		String f7 = "";
		String f9 = "";
		String f10 = "";
		String f12 = "";
		String f13 = "";
		
		if(logmonth==getMonthId() && logDay == DateUtils.getCurrentDay()) {
			//write in receipt
			rpt.setF2("");
			rpt.setF3("");
			rpt.setF4("");
			
			String be1= info.getBeginningNo()+"";
			int be2 = be1.length();
			
			
			
			if(info.getPrevPcs()==49) {
				rpt.setF5("50");
				f6 = be2==7? "0"+be1 : be1;
				rpt.setF6(DateUtils.numberResult(info.getFormType(), Long.valueOf(f6)));
				
				String en1= info.getIssuedForm().getEndingNo()+"";
				int en2 = en1.length();
				//rpt.setF7(en2==7? "0"+en1 : en1);
			
				f7 = en2==7? "0"+en1 : en1;
				rpt.setF7(DateUtils.numberResult(info.getFormType(), Long.valueOf(f7)));
			}else {
				/*int qty = info.getPrevPcs()+1;
				rpt.setF5(qty+"");
				
				long begNo = info.getBeginningNo();
				be1= begNo+"";
				be2 = be1.length();
				
				f6 = be2==7? "0"+be1 : be1;
				rpt.setF6(DateUtils.numberResult(info.getFormType(), Long.valueOf(f6)));
				
				String en1= info.getIssuedForm().getEndingNo()+"";
				int en2 = en1.length();
			
				f7 = en2==7? "0"+en1 : en1;
				rpt.setF7(DateUtils.numberResult(info.getFormType(), Long.valueOf(f7)));*/
				
				int qty = info.getPrevPcs()+1;
				//temporary removed if qty not equal to 50 place it to beginning
				//rpt.setF5(qty+"");
				rpt.setF2(qty+"");
				
				long begNo = info.getBeginningNo();
				be1= begNo+"";
				be2 = be1.length();
				f6 = be2==7? "0"+be1 : be1;
				
				//temporary removed if qty not equal to 50 place it to beginning
				//rpt.setF6(DateUtils.numberResult(info.getFormType(), Long.valueOf(f6)));
				rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f6)));
				rpt.setF6("");
				
				String en1= info.getIssuedForm().getEndingNo()+"";
				int en2 = en1.length();
				f7 = en2==7? "0"+en1 : en1;
				//temporary removed if qty not equal to 50 place it to beginning
				//rpt.setF7(DateUtils.numberResult(info.getFormType(), Long.valueOf(f7)));
				rpt.setF4(DateUtils.numberResult(info.getFormType(), Long.valueOf(f7)));
				rpt.setF7("");
			}
			
			
			
			
		}else {
		//Write in beginning balance
			String be1= info.getBeginningNo()+"";
			int be2 = be1.length();
			
			if(info.getPrevPcs()==49) {
				rpt.setF2("50");
				//rpt.setF3(be2==7? "0"+be1 : be1);
				
				f3 = be2==7? "0"+be1 : be1; 
				rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f3)));
			}else {
				
				/*String sql = " AND sud.logid=?";
				String params[]= new String[1];
				params[0]= info.getIssuedForm().getId()+"";
				List<CollectionInfo> infos = CollectionInfo.retrieve(sql, params);
				
				if(infos!=null && infos.size()>1) {
					int qty = info.getPrevPcs()+1;
					rpt.setF2(qty+"");
					
					long begNo = info.getBeginningNo() - info.getPrevPcs();
					be1= begNo+"";
					be2 = be1.length();
					
					f3 = be2==7? "0"+be1 : be1; 
					rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f3)));
				}else {//correction for those who has a beginning quantity not equal to 49
					int qty = info.getPrevPcs()+1;
					rpt.setF2(qty+"");
					
					long begNo = info.getBeginningNo();
					be1= begNo+"";
					be2 = be1.length();
					
					f3 = be2==7? "0"+be1 : be1; 
					rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f3)));
				}*/
				System.out.println("Process on the new changes>>>>>");
				int qty = info.getPrevPcs()+1;
				rpt.setF2(qty+"");
				
				long begNo = info.getBeginningNo();
				be1= begNo+"";
				be2 = be1.length();
				
				f3 = be2==7? "0"+be1 : be1; 
				rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f3)));
				
				
				
				
			}
			
			
			String en1= info.getIssuedForm().getEndingNo()+"";
			int en2 = en1.length();
			//rpt.setF4(en2==7? "0"+en1 : en1);
			
			f4 = en2==7? "0"+en1 : en1;
			rpt.setF4(DateUtils.numberResult(info.getFormType(), Long.valueOf(f4)));
			
			rpt.setF5("");
			rpt.setF6("");
			rpt.setF7("");
		}
		//issued
		rpt.setF8(info.getPcs()+"");
		
		String beg1= info.getBeginningNo()+"";
		int beg2 = beg1.length();
		//rpt.setF9(beg2==7? "0"+beg1 : beg1);
		
		f9 = beg2==7? "0"+beg1 : beg1;
		rpt.setF9(DateUtils.numberResult(info.getFormType(), Long.valueOf(f9)));
		
		//String en1= info.getIssuedForm().getEndingNo()+"";
		String en1= info.getEndingNo()+"";
		int en2 = en1.length();
		//rpt.setF10(en2==7? "0"+en1 : en1);
		
		f10 = en2==7? "0"+en1 : en1;
		rpt.setF10(DateUtils.numberResult(info.getFormType(), Long.valueOf(f10)));
		
		//ending balance
		
		long endingQty = info.getIssuedForm().getEndingNo() - info.getEndingNo();
		
		if(endingQty==0) {
			rpt.setF11("");
			rpt.setF12("All Issued");
			rpt.setF13("");
		}else {
			rpt.setF11(endingQty+"");
			long enNumber = info.getEndingNo() + 1;
			String enbeg1= enNumber+"";
			int enbeg2 = enbeg1.length();
			//rpt.setF12(enbeg2==7? "0"+enbeg1 : enbeg1);
			
			f12 = enbeg2==7? "0"+enbeg1 : enbeg1;
			rpt.setF12(DateUtils.numberResult(info.getFormType(), Long.valueOf(f12)));
			
			String enen1= info.getIssuedForm().getEndingNo()+"";
			int enen2 = enen1.length();
			//rpt.setF13(enen2==7? "0"+enen1 : enen1);
			
			f13 = enen2==7? "0"+enen1 : enen1;
			rpt.setF13(DateUtils.numberResult(info.getFormType(), Long.valueOf(f13)));
		}
		//remarks
		rpt.setF14("");
		
		/*Collector col = Collector.retrieve(info.getCollector().getId());
		if(col.getDepartment().getCode().equalsIgnoreCase("1091")) {
			rpt.setF15(col.getName());
		}else {
			rpt.setF15(col.getDepartment().getDepartmentName());
		}*/
		
		//change the value if the form is Cash ticket
				if(FormType.CT_2.getId()==info.getFormType() || FormType.CT_5.getId()==info.getFormType()) {
					
					rpt.setF1(FormType.nameId(info.getFormType()));
					String allIssued = info.getBeginningNo()==0? "All Issued" : "";
					double amount = 0d;
					
					if(logmonth==getMonthId() && logDay == DateUtils.getCurrentDay()) {
						
						amount = info.getBeginningNo() + info.getAmount();
						
						if(amount==info.getEndingNo()) {
							//beginning
							rpt.setF2("");
							rpt.setF3("");
							rpt.setF4("");
							 
							//Receipt
							rpt.setF5("");
							rpt.setF6(Currency.formatAmount(amount));
							rpt.setF7("");
						}else {
							//beginning
							rpt.setF2("");
							rpt.setF3(Currency.formatAmount(amount));
							rpt.setF4("");
							 
							//Receipt
							rpt.setF5("");
							rpt.setF6("");
							rpt.setF7("");
						}
						
						//issued
						rpt.setF8("");
						rpt.setF9(Currency.formatAmount(info.getAmount()));
						rpt.setF10("");
						
						//ending balance
						rpt.setF11("");
						if(info.getBeginningNo()>0) {
							rpt.setF12(Currency.formatAmount(info.getBeginningNo()));
						}else {
							rpt.setF12(allIssued);
						}
						rpt.setF13("");
					}else {
						
						amount = info.getBeginningNo() + info.getAmount();
						
						//beginning
						rpt.setF2("");
						rpt.setF3(Currency.formatAmount(amount));
						rpt.setF4("");
						
						//Receipt
						rpt.setF5("");
						rpt.setF6("");
						rpt.setF7("");
						
						//issued
						rpt.setF8("");
						rpt.setF9(Currency.formatAmount(info.getAmount()));
						rpt.setF10("");
						
						//ending balance
						rpt.setF11("");
						if(info.getBeginningNo()>0) {
							rpt.setF12(Currency.formatAmount(info.getBeginningNo()));
						}else {
							rpt.setF12(allIssued);
						}
						rpt.setF13("");
					}
						
						
						//remarks
						rpt.setF14("");
					//}
				}
				
				Collector col = Collector.retrieve(info.getCollector().getId());
				if(col.getDepartment().getCode().equalsIgnoreCase("1091")) {
					rpt.setF15(col.getName().replace("F.L Lopez-", ""));
				}else {
					rpt.setF15(col.getDepartment().getDepartmentName());
				}
		
		return rpt;
	}
	
	public Form11Report reportLastCollectionInfo(CollectionInfo info){
		System.out.println("reportLastCollectionInfo>>>");
		Form11Report rpt = null;
		System.out.println("info.getIssuedForm().getEndingNo()=" + info.getIssuedForm().getEndingNo() + " - info.getEndingNo() " + info.getEndingNo());
		long endingQty = info.getIssuedForm().getEndingNo() - info.getEndingNo();
		System.out.println("endingQty>> " + endingQty);
		String f3 = "";
		String f4 = "";
		String f6 = "";
		String f7 = "";
		String f9 = "";
		String f10 = "";
		String f12 = "";
		String f13 = "";
		
		if(endingQty>0) {
		rpt = new Form11Report();	
		rpt.setF1(FormType.nameId(info.getFormType()));
		System.out.println("form type>> " + rpt.getF1());
		rpt.setF2(endingQty+"");
		
		long endTmp = info.getEndingNo() + 1;//added 1 if has previous issuance and no issuance on the next report 07/04/2019
		
		String enbeg1= endTmp+"";//info.getEndingNo()+"";
		int enbeg2 = enbeg1.length();
		
		f3 = enbeg2==7? "0"+enbeg1 : enbeg1;
		rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f3)));
		
		String enen1= info.getIssuedForm().getEndingNo()+"";
		int enen2 = enen1.length();
		
		f4 = enen2==7? "0"+enen1 : enen1;
		rpt.setF4(DateUtils.numberResult(info.getFormType(), Long.valueOf(f4)));
		//rpt.setF4(enen2==7? "0"+enen1 : enen1);
		
		System.out.println("Beginning from >> " + rpt.getF3());
		System.out.println("Beginning to >> " + rpt.getF4());
		
		//Receipt
		rpt.setF5("");
		rpt.setF6("");
		rpt.setF7("");
		
		//issued
		rpt.setF8("");
		rpt.setF9("No Iss.");
		rpt.setF10("");
		
		
		//ending balance
		rpt.setF11(endingQty+"");
		
		f12 = enbeg2==7? "0"+enbeg1 : enbeg1;
		f13 = enen2==7? "0"+enen1 : enen1;
		rpt.setF12(DateUtils.numberResult(info.getFormType(), Long.valueOf(f12)));
		rpt.setF13(DateUtils.numberResult(info.getFormType(), Long.valueOf(f13)));
		//rpt.setF12(enbeg2==7? "0"+enbeg1 : enbeg1);
		//rpt.setF13(enen2==7? "0"+enen1 : enen1);
		
		//remarks
		rpt.setF14("");
		
		//change the value if the form is Cash ticket
		if(FormType.CT_2.getId()==info.getFormType() || FormType.CT_5.getId()==info.getFormType()) {
			if(info.getAmount()>0) {
				int logmonth = Integer.valueOf(info.getIssuedForm().getIssuedDate().split("-")[1]);
				int logDay = Integer.valueOf(info.getIssuedForm().getIssuedDate().split("-")[2]);
				
				rpt.setF1(FormType.nameId(info.getFormType()));
				if(logmonth==getMonthId() && logDay == DateUtils.getCurrentDay()) {
					//beginning
					rpt.setF2("");
					rpt.setF3("");
					rpt.setF4("");
					
					//Receipt
					rpt.setF5("");
					rpt.setF6(Currency.formatAmount(info.getAmount()));
					rpt.setF7("");
				}else {
					//beginning
					rpt.setF2("");
					rpt.setF3(Currency.formatAmount(info.getAmount()));
					rpt.setF4("");
					
					//Receipt
					rpt.setF5("");
					rpt.setF6("");
					rpt.setF7("");
				}
				
				
				//issued
				rpt.setF8("");
				rpt.setF9(Currency.formatAmount(info.getAmount()));
				rpt.setF10("");
				
				String allIssued = info.getBeginningNo()==0? "All Issued" : "";
				//ending balance
				rpt.setF11("");
				rpt.setF12(allIssued);
				rpt.setF13("");
				
				//remarks
				rpt.setF14("");
			}
		}
		
		Collector col = Collector.retrieve(info.getCollector().getId());
		if(col.getDepartment().getCode().equalsIgnoreCase("1091")) {
			rpt.setF15(col.getName().replace("F.L Lopez-", ""));
		}else {
			rpt.setF15(col.getDepartment().getDepartmentName());
		}
		
		}
		
		return rpt;
	}
	
	public Form11Report reportIssued(IssuedForm isform) {
		
		System.out.println("reportIssued>>>");
		
		Form11Report rpt = new Form11Report();
		
		String f3 = "";
		String f4 = "";
		String f6 = "";
		String f7 = "";
		String f9 = "";
		String f10 = "";
		String f12 = "";
		String f13 = "";
		
		if("Stock".equalsIgnoreCase(isform.getCollector().getName())) {
			rpt.setF1(FormType.nameId(isform.getFormType()));
			
			int logmonth = Integer.valueOf(isform.getIssuedDate().split("-")[1]);
			
			if(logmonth==getMonthId()) {
				
				rpt.setF2("");
				rpt.setF3("");
				rpt.setF4("");
				
				String be1= isform.getBeginningNo()+"";
				int be2 = be1.length();
				rpt.setF5(isform.getPcs()+"");
				f6 = be2==7? "0"+be1 : be1;
				rpt.setF6(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f6)));
				//rpt.setF6(be2==7? "0"+be1 : be1);
				
				String en1= isform.getEndingNo()+"";
				int en2 = en1.length();
				f7 = en2==7? "0"+en1 : en1;
				rpt.setF7(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f7)));
				//rpt.setF7(en2==7? "0"+en1 : en1);
				
			}else {
			
				String be1= isform.getBeginningNo()+"";
				int be2 = be1.length();
				rpt.setF2(isform.getPcs()+"");
				f3 = be2==7? "0"+be1 : be1;
				rpt.setF3(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f3)));
				//rpt.setF3(be2==7? "0"+be1 : be1);
				
				String en1= isform.getEndingNo()+"";
				int en2 = en1.length();
				f4 = en2==7? "0"+en1 : en1;
				rpt.setF4(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f4)));
				//rpt.setF4(en2==7? "0"+en1 : en1);
				
				rpt.setF5("");
				rpt.setF6("");
				rpt.setF7("");
			
			}
			
			//issued
			rpt.setF8("");
			rpt.setF9("");
			rpt.setF10("");
			
			
			//ending balance
			
			
			rpt.setF11("");
			rpt.setF12("");
			rpt.setF13("");
			
			//remarks
			rpt.setF14("");
			
			Collector col = Collector.retrieve(isform.getCollector().getId());
			if(col.getDepartment().getCode().equalsIgnoreCase("1091")) {
				rpt.setF15(col.getName());
			}else {
				rpt.setF15(col.getDepartment().getDepartmentName());
			}
			
		}else {
		
		rpt.setF1(FormType.nameId(isform.getFormType()));
		
		int logmonth = Integer.valueOf(isform.getIssuedDate().split("-")[1]);
		int logDay = Integer.valueOf(isform.getIssuedDate().split("-")[2]);
		
		if(logmonth==getMonthId() && logDay == DateUtils.getCurrentDay()) {
			//write in receipt
			rpt.setF2("");
			rpt.setF3("");
			rpt.setF4("");
			
			String be1= isform.getBeginningNo()+"";
			int be2 = be1.length();
			rpt.setF5(isform.getPcs()+"");
			f6 = be2==7? "0"+be1 : be1;
			rpt.setF6(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f6)));
			//rpt.setF6(be2==7? "0"+be1 : be1);
				
			String en1= isform.getEndingNo()+"";
			int en2 = en1.length();
			f7 = en2==7? "0"+en1 : en1;
			rpt.setF7(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f7)));
			//rpt.setF7(en2==7? "0"+en1 : en1);
		
			
			
		}else {
		//Write in beginning balance
			String be1= isform.getBeginningNo()+"";
			int be2 = be1.length();
			rpt.setF2(isform.getPcs()+"");
			f3 = be2==7? "0"+be1 : be1;
			rpt.setF3(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f3)));
			//rpt.setF3(be2==7? "0"+be1 : be1);
			
			String en1= isform.getEndingNo()+"";
			int en2 = en1.length();
			f4 = en2==7? "0"+en1 : en1;
			rpt.setF4(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f4)));
			//rpt.setF4(en2==7? "0"+en1 : en1);
			
			rpt.setF5("");
			rpt.setF6("");
			rpt.setF7("");
		}
		
		//issued
		rpt.setF8("");
		rpt.setF9("No Iss.");
		rpt.setF10("");
		
		
		//ending balance
		
		
		rpt.setF11(isform.getPcs()+"");
		
		String enbeg1= isform.getBeginningNo()+"";
		int enbeg2 = enbeg1.length();
		f12 = enbeg2==7? "0"+enbeg1 : enbeg1;
		rpt.setF12(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f12)));
		//rpt.setF12(enbeg2==7? "0"+enbeg1 : enbeg1);
		
		String enending1= isform.getEndingNo()+"";
		int enending2 = enending1.length();
		f13 = enending2==7? "0"+enending1 : enending1;
		rpt.setF13(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f13)));
		//rpt.setF13(enending2==7? "0"+enending1 : enending1);
		
		//remarks
		rpt.setF14("");
		
		Collector col = Collector.retrieve(isform.getCollector().getId());
		if(col.getDepartment().getCode().equalsIgnoreCase("1091")) {
			rpt.setF15(col.getName().replace("F.L Lopez-", ""));
		}else {
			rpt.setF15(col.getDepartment().getDepartmentName());
		}
		
		}
		
		//change the value if the form is Cash ticket
		if(FormType.CT_2.getId()==isform.getFormType() || FormType.CT_5.getId()==isform.getFormType()) {
			
			int logmonth = Integer.valueOf(isform.getIssuedDate().split("-")[1]);
			int logDay = Integer.valueOf(isform.getIssuedDate().split("-")[2]);
			
						rpt.setF1(FormType.nameId(isform.getFormType()));
						
						
						double amount = 0d;
						if(FormType.CT_2.getId()==isform.getFormType()) {
							amount = isform.getPcs() * 2;
						}else if(FormType.CT_5.getId()==isform.getFormType()) {
							amount = isform.getPcs() * 5;
						}
						
						if(logmonth==getMonthId() && logDay == DateUtils.getCurrentDay()) {
							//beginning
							rpt.setF2("");
							rpt.setF3("");
							rpt.setF4("");
							
							//Receipt
							rpt.setF5("");
							rpt.setF6(Currency.formatAmount(amount));
							rpt.setF7("");
						}else {
							//beginning
							rpt.setF2("");
							rpt.setF3(Currency.formatAmount(amount));
							rpt.setF4("");
							
							//Receipt
							rpt.setF5("");
							rpt.setF6("");
							rpt.setF7("");
						}
						
						
						//issued
						rpt.setF8("");
						rpt.setF9("No Iss.");
						rpt.setF10("");
						
						
						//ending balance
						rpt.setF11("");
						rpt.setF12(Currency.formatAmount(amount));
						rpt.setF13("");
						
						//remarks
						rpt.setF14("");
		}
		
		return rpt;
	
	}

}

