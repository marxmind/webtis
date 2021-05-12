package com.italia.municipality.lakesebu.bean;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.TabChangeEvent;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.italia.municipality.lakesebu.controller.Cedula;
import com.italia.municipality.lakesebu.controller.Collector;
import com.italia.municipality.lakesebu.controller.Customer;
import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.NumberToWords;
import com.italia.municipality.lakesebu.controller.OR51;
import com.italia.municipality.lakesebu.controller.ORListing;
import com.italia.municipality.lakesebu.controller.ORNameList;
import com.italia.municipality.lakesebu.controller.PaymentName;
import com.italia.municipality.lakesebu.controller.ReportFields;
import com.italia.municipality.lakesebu.controller.Reports;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.enm.CivilStatus;
import com.italia.municipality.lakesebu.enm.FormStatus;
import com.italia.municipality.lakesebu.enm.FormType;
import com.italia.municipality.lakesebu.enm.Months;
import com.italia.municipality.lakesebu.global.GlobalVar;
import com.italia.municipality.lakesebu.licensing.controller.Barangay;
import com.italia.municipality.lakesebu.licensing.controller.DocumentFormatter;
import com.italia.municipality.lakesebu.licensing.controller.Municipality;
import com.italia.municipality.lakesebu.licensing.controller.Province;
import com.italia.municipality.lakesebu.licensing.controller.Purok;
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
 * @since 04/23/2019
 * @version 1.0
 *
 *
 */
@Named
@ViewScoped
public class ORListingBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 645698875451L;
	
	private Date dateTrans;
	private String orNumber;
	private String payorName;
	private String address;
	private int formTypeId;
	private List formTypes;
	
	private List<PaymentName> namesData = new ArrayList<PaymentName>();//Collections.synchronizedList(new ArrayList<PaymentName>());
	private List<PaymentName> suggestedData = new ArrayList<PaymentName>();//Collections.synchronizedList(new ArrayList<PaymentName>());
	private List<PaymentName> namesDataSelected = new ArrayList<PaymentName>();//Collections.synchronizedList(new ArrayList<PaymentName>());
	private Map<Long, PaymentName> selectedPaymentNameMap = new HashMap<Long, PaymentName>();//Collections.synchronizedMap(new HashMap<Long, PaymentName>());
	private String totalAmount;
	private String searchPayName;
	
	private ORListing orRecordData;
	private List<ORNameList> ornameListData;
	private List<ORListing> ors = new ArrayList<ORListing>();//Collections.synchronizedList(new ArrayList<ORListing>());
	private String totalCollection;
	
	private String searchName;
	private int monthId;
	private List months;
	private int yearId;
	private List years;
	private int paymentId;
	private List paymentTypes;
	private int formTypeSearchId;
	private List formTypeSearch;
	
	private List<Reports>  dtls = new ArrayList<Reports>();//Collections.synchronizedList(new ArrayList<Reports>());
	private List<Reports>  rpts = new ArrayList<Reports>();//Collections.synchronizedList(new ArrayList<Reports>());
	
	private List collectors;
	private int collectorId;
	
	private List collectorsSearch;
	private int collectorSearchId;
	
	private List sorts;
	private int sortId;
	
	private int statusId;
	private List status;
	
	private int statusSearchId;
	private List statusSearch;
	
	private boolean filtered;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	
	private String sumNames;
	private String sumAmounts;
	
	private int depId;
	private List departments;
	
	private String textContent;
	private String rptSummary;
	
	List<Reports> rptsOnly = new ArrayList<Reports>();//Collections.synchronizedList(new ArrayList<Reports>());
	private String totalAmountSummaryOnly;
	
	private String limitData;
	
	private int selectOrTypeId;
	private List selectOrTypes;
	
	private boolean collectorsMode;
	private String formInfo;
	
	//for ctc individual and corporation
	private double label2;
	private double label3;
	private double label4;
	private double amount1;
	private double amount2;
	private double amount3;
	private double amount4;
	private double amount5;
	private double amount6;
	private double amount7;
	private String tinNo;
	private String hieghtDateReg;
	private String weight;
	private String customerAddress;
	//private String civilStatusOrganization;
	private String professionBusinessNature;
	private String signatory;
	private String placeOfBirth;
	private String citizenshipOrganization;
	
	private int civilStatusId;
	private List civilStatus;
	
	private int genderId=1;
	private List genders;
	
	private Date birthdate;
	private boolean enableBirthday=true;
	
	private String qrcodeMsg="Please place the QRCOde on the camera";
	private com.italia.municipality.lakesebu.licensing.controller.Customer customerDataSelected;
	
	@PostConstruct
	public void init() {
		if(monthId==0) {
			monthId = DateUtils.getCurrentMonth();
		}
		System.out.println("Initialized....");
		
		setLimitData("10");
		load();
		
		suggestedInfo();
	}
	
	public void modeMsg() {
		Application.addMessage(1, "Collector's Mode", "You have " + (isCollectorsMode()==true? "activated the collector's mode" : "deactivated the collector's mode"));
	}
	
	public void reloadInit() {
		if(monthId==0) {
			monthId = DateUtils.getCurrentMonth();
		}
		System.out.println("Initialized....");
		
		setLimitData("10");
		load();
	}
	
	private void suggestedInfo() {
		suggestedData = new ArrayList<PaymentName>();//Collections.synchronizedList(new ArrayList<PaymentName>());
		Map<Long, PaymentName> mapData = new HashMap<Long, PaymentName>();//Collections.synchronizedMap(new HashMap<Long, PaymentName>());
		int cnt = 1;
		for(ORNameList or : ORNameList.retrieve(" ORDER BY nameL.timestampol DESC LIMIT 100", new String[0])) {
			if(cnt<=10) {
				long id = or.getPaymentName().getId();
				if(mapData!=null) {
					if(!mapData.containsKey(id)) {
						PaymentName name = or.getPaymentName();
						name.setAmount(or.getAmount());
						suggestedData.add(name);
						mapData.put(id, name);
					}
				}else {
					PaymentName name = or.getPaymentName();
					name.setAmount(or.getAmount());
					suggestedData.add(name);
					mapData.put(id, name);
				}
			}
			cnt++;
		}
	}
	
	public void load() {
		String sql = "";
		String[] params = new String[0];
		double amount = 0d;
		ors = new ArrayList<ORListing>();//Collections.synchronizedList(new ArrayList<ORListing>());
		String monthFrom = "";
		String monthTo = "";
		
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
			
			int num = 0;
			try {
				num = Integer.valueOf(getSearchName());
				sql += " AND orl.ornumber like '%"+ getSearchName() +"%'";
			}catch(Exception e) {
				sql += " AND cuz.fullname like '%"+ getSearchName().replace("--", "") +"%'";
			}
			
		}
		
		System.out.println("Check filtered status : " + isFiltered());
		
		if(!isFiltered()) {
		
		if(getDepId()>0) {
			sql += " AND col.departmentid=" + getDepId();
		}
			
		if(getYearId()==0) {
			params = new String[2];
			if(getMonthId()>0) {
				
				monthFrom = "0000-" + (getMonthId()<10? "0"+getMonthId() : ""+getMonthId()) + "-01";
				monthTo = DateUtils.getCurrentYear() +"-" + (getMonthId()<10? "0"+getMonthId() : ""+getMonthId()) + "-31";
				
			}else {
				
				monthFrom = "0000-01-01";
				monthFrom = DateUtils.getCurrentYear() +"-12-31";
				
			}
			sql += " AND (orl.ordatetrans>=? AND orl.ordatetrans<=?)";
			params[0] = monthFrom;
			params[1] = monthTo;
		}else {
			params = new String[2];
			if(getMonthId()>0) {
				
				monthFrom = getYearId() + "-" + (getMonthId()<10? "0"+getMonthId() : ""+getMonthId()) + "-01";
				monthTo = getYearId() + "-" + (getMonthId()<10? "0"+getMonthId() : ""+getMonthId()) + "-31";
				
			}else {
				
				monthFrom = getYearId() + "-01-01";
				monthTo = getYearId() + "-12-31";
				
			}
			sql += " AND (orl.ordatetrans>=? AND orl.ordatetrans<=?)";
			params[0] = monthFrom;
			params[1] = monthTo;
		}
		
		
		
		if(getFormTypeSearchId()>0) {
			sql += " AND orl.aform=" + getFormTypeSearchId();
		}
		
		if(getCollectorSearchId()>0) {
			sql += " AND col.isid=" + getCollectorSearchId();
		}
		
		if(getSortId()>0) {
			sql += " ORDER By ";
			if(getSortId()==1) {
				sql += " orl.ordatetrans";
			}else if(getSortId()==2) {
				sql += " col.collectorname";
			}else if(getSortId()==3) {
				sql += " orl.aform";
			}else if(getSortId()==4) {
				sql += " orl.ornumber";
			}else if(getSortId()==5) {
				sql += " cuz.fullname";
			}
		}
		
		if(getStatusSearchId()>0) {
			sql += " AND orl.orstatus=" + getStatusSearchId();
		}
		
		if(getLimitData()!=null && !getLimitData().isEmpty()) {
			String num = getLimitData();
			num = num.replace(".0", "");
			num = num.replace(".00", "");
			sql += " LIMIT " + num;
		}
		
		System.out.println("filetered >> " +sql);
		
		
		if(getPaymentId()>0) {
			for(ORListing o : ORListing.retrieve(sql, params)) {
				
					List<ORNameList> or = new ArrayList<ORNameList>();//Collections.synchronizedList(new ArrayList<ORNameList>());
					boolean hasFound = false;
					double totalAmount = 0d;
					
					for(ORNameList n : o.getOrNameList()) {
						if(getPaymentId()==n.getPaymentName().getId()) {
							or.add(n);
							if(FormStatus.CANCELLED.getId()!=o.getStatus()) {
								amount += n.getAmount();
							}
							totalAmount += n.getAmount();
							hasFound = true;
						}
					
						/*
						 * o.setAmount(totalAmount); o.setOrNameList(or); if(hasFound) { ors.add(o); }
						 */
				}
					
					  o.setAmount(totalAmount); o.setOrNameList(or); if(hasFound) { ors.add(o); }
					 
			}
		}else {
			for(ORListing o : ORListing.retrieve(sql, params)) {
				if(FormStatus.CANCELLED.getId()!=o.getStatus()) {
					amount += o.getAmount();
				}	
				ors.add(o);
				
			}
		}
		
		}else {
			
			sql += " ORDER BY orl.orid DESC ";
			if(getLimitData()!=null && !getLimitData().isEmpty()) {
				String num = getLimitData();
				num = num.replace(".0", "");
				num = num.replace(".00", "");
				sql += " LIMIT " + num;
			}
			
			for(ORListing o : ORListing.retrieve(sql, params)) {
				if(FormStatus.CANCELLED.getId()!=o.getStatus()) {
					amount += o.getAmount();
				}	
				ors.add(o);
				
			}
		}
		
		detailsData();
		setTotalCollection(Currency.formatAmount(amount));
	}
	
	
	private void detailsData() {
		rpts = new ArrayList<Reports>();//Collections.synchronizedList(new ArrayList<Reports>());
		dtls = new ArrayList<Reports>();//Collections.synchronizedList(new ArrayList<Reports>());
		//init();//get the data from init
		Map<Long, PaymentName> mapData = new HashMap<Long, PaymentName>();//Collections.synchronizedMap(new HashMap<Long, PaymentName>());
		double grandTotal = 0d;
		Reports rpt = new Reports();
		double grandcancelledAmnt = 0d;
		double barangayctcamount = 0d;
		double mtoctcamount = 0d;
		double ctctotal = 0d;
		for(ORListing or : ors) {
			rpt = new Reports();
			rpt.setF8(or.getStatusName());
			rpt.setF1(or.getDateTrans());
			rpt.setF2(or.getOrNumber());
			rpt.setF3(or.getCustomer().getFullName());
			rpt.setF4(or.getFormName());
			rpt.setF5("");
			rpt.setF6("");
			rpt.setF7(or.getCollector().getName());
			dtls.add(rpt);
			rpts.add(rpt);
			
			double amount = 0d;
			double canAmount = 0d;
			boolean isCTC = false;
			//if(or.getFormType()==FormType.CTC_CORPORATION.getId() || or.getFormType()==FormType.CTC_INDIVIDUAL.getId()) {
			if(or.getFormType()==FormType.CTC_INDIVIDUAL.getId()) {
				isCTC = true;
			}
			boolean isbarangayCtc = or.getCollector().getDepartment().getDepid()>=62? true : false; //62-80 = barangay
			for(ORNameList n : or.getOrNameList()) {
				rpt = new Reports();
				rpt.setF1("");
				rpt.setF2("");
				rpt.setF3("");
				rpt.setF4("");
				rpt.setF5(n.getPaymentName().getName());
				rpt.setF6(Currency.formatAmount(n.getAmount()));
				rpt.setF7("");
				dtls.add(rpt);
				rpts.add(rpt);
				if(FormStatus.CANCELLED.getId()!=or.getStatus()) {
					amount += n.getAmount();
					if(isCTC) {
						ctctotal += n.getAmount();
						if(isbarangayCtc) {
							barangayctcamount += n.getAmount();
						}else {
							mtoctcamount += n.getAmount();
						}
					}
				}else {
					grandcancelledAmnt += n.getAmount();
					canAmount += n.getAmount();
				}
				
				
				//for report summary only
				//do not remove
				
				long key = n.getPaymentName().getId();	
				if(mapData!=null && mapData.containsKey(key)) {
					double amnt = mapData.get(key).getAmount();
					amnt += n.getAmount();
					PaymentName na = n.getPaymentName();
					na.setAmount(amnt);
					mapData.put(key, na);
				}else {
					PaymentName na = n.getPaymentName();
					na.setAmount(n.getAmount());
					mapData.put(key, na);
				}
				
			}
			rpt = new Reports();
			rpt.setF1("Total");
			rpt.setF2("");
			rpt.setF3("");
			rpt.setF4("");
			rpt.setF5("");
			if(FormStatus.CANCELLED.getId()!=or.getStatus()) {
				rpt.setF6(Currency.formatAmount(amount));
			}else {
				rpt.setF6("("+Currency.formatAmount(canAmount)+")");
			}
			rpt.setF7("");
			dtls.add(rpt);
			//rpts.add(rpt);
			grandTotal += amount;
		}
		rpt = new Reports();
		rpt.setF1("Grand Total");
		rpt.setF2("");
		rpt.setF3("");
		rpt.setF4("");
		rpt.setF5("");
		rpt.setF6(Currency.formatAmount(grandTotal));
		dtls.add(rpt);
		rpts.add(rpt);
		rpt = new Reports();
		rpt.setF1("");
		rpt.setF2("");
		rpt.setF3("");
		rpt.setF4("");
		rpt.setF5("");
		rpt.setF6("");
		dtls.add(rpt);
		
		rpt = new Reports();
		rpt.setF1("");
		rpt.setF2("");
		rpt.setF3("");
		rpt.setF4("");
		rpt.setF5("Summary Details");
		rpt.setF6("");
		dtls.add(rpt);
		
		rptsOnly = new ArrayList<Reports>();//Collections.synchronizedList(new ArrayList<Reports>());
		
		double sumAmount = 0d;
		String sumN="";
		String sumA="";
		Reports rss = new Reports();
		for(PaymentName nem : mapData.values()) {
			rpt = new Reports();
			rpt.setF1("");
			rpt.setF2("");
			rpt.setF3("");
			rpt.setF4("");
			rpt.setF5(nem.getName());
			sumAmount += nem.getAmount();
			rpt.setF6(Currency.formatAmount(nem.getAmount()));
			dtls.add(rpt);
			sumN += rpt.getF5() + "\n";
			sumA += rpt.getF6() + "\n";
			
			rss = new Reports();
			rss.setF1(nem.getName());
			rss.setF2(Currency.formatAmount(nem.getAmount()));
			rptsOnly.add(rss);
		}
		
		sumAmount -= grandcancelledAmnt;
		rpt = new Reports();
		rpt.setF1("");
		rpt.setF2("");
		rpt.setF3("");
		rpt.setF4("");
		rpt.setF5("Minus Cancelled OR");
		rpt.setF6("("+Currency.formatAmount(grandcancelledAmnt)+")");
		dtls.add(rpt);
		sumN += rpt.getF5() + "\n";
		sumA += rpt.getF6() + "\n";
		
		rss = new Reports();
		rss.setF1("Minus Cancelled OR");
		rss.setF2("("+Currency.formatAmount(grandcancelledAmnt)+")");
		rptsOnly.add(rss);
		
		rpt = new Reports();
		rpt.setF1("");
		rpt.setF2("");
		rpt.setF3("");
		rpt.setF4("");
		rpt.setF5("CTC Collection" + "("+Currency.formatAmount(ctctotal)+")");
		rpt.setF6("");
		dtls.add(rpt);
		sumN += rpt.getF5() + "\n";
		sumA += rpt.getF6() + "\n";
		
		rss = new Reports();
		rss.setF1("CTC Collection" + "("+Currency.formatAmount(ctctotal)+")");
		rss.setF2("");
		rptsOnly.add(rss);
		
		rpt = new Reports();
		rpt.setF1("");
		rpt.setF2("");
		rpt.setF3("");
		rpt.setF4("");
		rpt.setF5("MTO CTC Collection" + "("+Currency.formatAmount(mtoctcamount)+")");
		rpt.setF6("");
		dtls.add(rpt);
		sumN += rpt.getF5() + "\n";
		sumA += rpt.getF6() + "\n";
		
		rss = new Reports();
		rss.setF1("MTO CTC Collection" + "("+Currency.formatAmount(mtoctcamount)+")");
		rss.setF2("");
		rptsOnly.add(rss);
		
		rpt = new Reports();
		rpt.setF1("");
		rpt.setF2("");
		rpt.setF3("");
		rpt.setF4("");
		rpt.setF5("Barangay CTC Collection" + "("+Currency.formatAmount(barangayctcamount)+")");
		rpt.setF6("");
		dtls.add(rpt);
		sumN += rpt.getF5() + "\n";
		sumA += rpt.getF6() + "\n";
		
		rss = new Reports();
		rss.setF1("Barangay CTC Collection" + "("+Currency.formatAmount(barangayctcamount)+")");
		rss.setF2("");
		rptsOnly.add(rss);
		
		rpt = new Reports();
		rpt.setF1("");
		rpt.setF2("");
		rpt.setF3("");
		rpt.setF4("");
		double sharedAmount = barangayctcamount * 0.50;
		rpt.setF5("Barangay Shared CTC Amount(50%) (" + Currency.formatAmount(sharedAmount)+")");
		rpt.setF6("");
		dtls.add(rpt);
		sumN += rpt.getF5() + "\n";
		sumA += rpt.getF6() + "\n";
		
		rss = new Reports();
		rss.setF1("Barangay Shared CTC Amount(50%) (" + Currency.formatAmount(sharedAmount)+")");
		rss.setF2("");
		rptsOnly.add(rss);
		
		rpt = new Reports();
		rpt.setF1("");
		rpt.setF2("");
		rpt.setF3("");
		rpt.setF4("");
		rpt.setF5("Grand Total");
		rpt.setF6(Currency.formatAmount(sumAmount));
		dtls.add(rpt);
		sumN += rpt.getF5() + "\n";
		sumA += rpt.getF6() + "\n";
		
		setSumNames(sumN);
		setSumAmounts(sumA);
		
		setTotalAmountSummaryOnly(Currency.formatAmount(sumAmount));
	}
	
	public void onChange(TabChangeEvent event) {
		if("Reports Viewing".equalsIgnoreCase(event.getTab().getTitle())) {
			init();
		}else if("Details/Extract".equalsIgnoreCase(event.getTab().getTitle())) {
			detailsData();
		}
			
	}
	
	public List<String> payorNameSuggested(String query){
		List<String> result = new ArrayList<>();
		if(query!=null && !query.isEmpty()) {
			int size = query.length();
			if(size>=4) {
				String sql = " AND cus.fullname like '%"+query+"%'";
				String[] params = new String[0];
				/*for(Customer cz : Customer.retrieve(sql, params)) {
					result.add(cz.getFullName());
				}*/
				
				sql = " AND (cus.cuslastname like '%"+ query +"%' OR ";
				sql += " cus.fullname like '%"+ query +"%' ";
				sql += " ) GROUP BY cus.fullname LIMIT 10";
				params = new String[0];
				
				for(com.italia.municipality.lakesebu.licensing.controller.Customer cust : com.italia.municipality.lakesebu.licensing.controller.Customer.retrieve(sql, params)) {
					result.add(cust.getLastname().toUpperCase() + ", " + cust.getFirstname() + " " + (cust.getMiddlename()!=null? cust.getMiddlename().substring(0, 1).toUpperCase()+"." : "."));
				}
				
			 //result = com.italia.municipality.lakesebu.licensing.controller.Customer.retrieveLFMName(query, "fullname", "10");
			}
		}
		return result;
	}
	
	public void popupData() {
		
		if(getPayorName()!=null) {
			
		}
		PrimeFaces pf = PrimeFaces.current();
		pf.executeScript("PF('dlgCedula').show();");
	}
	
	public void loadPaymentNames() {
		
		
		namesData = new ArrayList<PaymentName>();//Collections.synchronizedList(new ArrayList<PaymentName>());
		
		String sql = " LIMIT 10";
		
		if(getSearchPayName()!=null && !getSearchPayName().isEmpty()) {
			sql = " AND pyname like '%"+ getSearchPayName().replace("--", "") +"%'";
			
			for(PaymentName name : PaymentName.retrieve(sql, new String[0])) {
				namesData.add(name);
			}
		}else {
			
			Map<Long, PaymentName> mapData = new HashMap<Long, PaymentName>();//Collections.synchronizedMap(new HashMap<Long, PaymentName>());
			int cnt = 1;
			for(ORNameList or : ORNameList.retrieve(" ORDER BY nameL.timestampol DESC LIMIT 100", new String[0])) {
				if(cnt<=10) {
					long id = or.getPaymentName().getId();
					if(mapData!=null) {
						if(!mapData.containsKey(id)) {
							PaymentName name = or.getPaymentName();
							name.setAmount(0.00);
							namesData.add(name);
							mapData.put(id, name);
						}
					}else {
						PaymentName name = or.getPaymentName();
						name.setAmount(0.00);
						namesData.add(name);
						mapData.put(id, name);
					}
				}
				cnt++;
			}
			
			
		}
		
	}
	
	public void loadLastORToSuggestForNewReceipt() {
		setOrRecordData(null);
		namesDataSelected = new ArrayList<PaymentName>();
		selectedPaymentNameMap = new HashMap<Long, PaymentName>();
		double amount = 0d;
		String sql = "";
		String[] params = new String[1];
		params[0] = getFormTypeId()+"";
		sql = " AND orl.isactiveor=1 AND orl.aform=? ORDER BY orl.orid DESC LIMIT 1";
		
		List<ORListing> ol = ORListing.retrieve(sql, params);
		if(ol!=null && ol.size()>0) {
			ORListing o = ol.get(0);
			
			params = new String[1];
			params[0] = o.getId()+"";
			sql = " AND orl.orid=? AND nameL.isactiveol=1";
			
			for(ORNameList or : ORNameList.retrieve(sql, params)) {
				PaymentName py = or.getPaymentName();
				py.setAmount(or.getAmount());
				amount += or.getAmount();
				namesDataSelected.add(py);
				getSelectedPaymentNameMap().put(py.getId(), py);
			}
			
			setFormTypeId(o.getFormType());
			if(getCollectorId()==1) {
				setCollectorId(o.getCollector().getId());
			}
			setTotalAmount(Currency.formatAmount(amount));
		}
		
	}
	
	private Customer selectedCustomer() {
		String sql = " AND cus.fullname like '%"+getPayorName()+"%'";
		String[] params = new String[0];
		for(Customer cz : Customer.retrieve(sql, params)) {
			return cz;
		}
		return null;
	}
	
	 public void onCellEdit(CellEditEvent event) {
		 try{
	        Object oldValue = event.getOldValue();
	        Object newValue = event.getNewValue();
	        
	        System.out.println("old Value: " + oldValue);
	        System.out.println("new Value: " + newValue);
	        
	        int index = event.getRowIndex();
	        
	        PaymentName name = getNamesData().get(index);
	        
	        addNamePayment(name);
	        
		 }catch(Exception e){}  
	 }       
	
	 public void onCellEditSelected(CellEditEvent event) {
		 try{
		        Object oldValue = event.getOldValue();
		        Object newValue = event.getNewValue();
		        
		        System.out.println("old Value: " + oldValue);
		        System.out.println("new Value: " + newValue);
		        
		        int index = event.getRowIndex();
		        
		        namesDataSelected.get(index).setAmount(Double.valueOf(newValue+""));
		        long id = namesDataSelected.get(index).getId();
		        boolean reloadInit = false;
		        
		        if(getOrRecordData()!=null) {//update
		        	try {
			        	ORNameList py = ORNameList.retrieve(" AND nameL.isactiveol=1 AND orl.orid="+getOrRecordData().getId() + " AND pay.pyid=" + id, new String[0]).get(0);
			        	py.setAmount(namesDataSelected.get(index).getAmount());
			        	py.save();
			        	reloadInit = true;
		        	}catch(Exception e) {}
		        }
		        
		        double amount = 0d;
		        for(PaymentName na : namesDataSelected) {
		        	amount += na.getAmount();
		        }
		        setTotalAmount(Currency.formatAmount(amount));
		        
		        if(reloadInit) {
		        	//init();
		        	reloadInit();
		        }
		        
			 }catch(Exception e){}  
	}  
	 
	 public void addSuggested(PaymentName name) {
		 addNamePayment(name);
	 }
	 
	public void addNamePayment(PaymentName name) {
		
		if(getSelectedPaymentNameMap()!=null) {
			
			namesDataSelected = new ArrayList<PaymentName>();//Collections.synchronizedList(new ArrayList<PaymentName>());
			getSelectedPaymentNameMap().put(name.getId(), name);
			double amount = 0d;
			for(PaymentName na : getSelectedPaymentNameMap().values()) {
				namesDataSelected.add(na);
				amount += na.getAmount();
			}
			setTotalAmount(Currency.formatAmount(amount));
		}else {
			setSelectedPaymentNameMap(new HashMap<Long, PaymentName>());
			getSelectedPaymentNameMap().put(name.getId(), name);
			getNamesDataSelected().add(name);
			setTotalAmount(Currency.formatAmount(name.getAmount()));
		}
		
	}
	
	public void calculateSelected() {
		double amount = 0d;
		for(PaymentName na : namesDataSelected) {
			amount += na.getAmount();
		}
		setTotalAmount(Currency.formatAmount(amount));
	}
	
	public void selectedOR() {
		namesDataSelected = new ArrayList<PaymentName>();
		String sql = "";
		String[] params = new String[0];
		
		clearFlds();//clearing all fields
		ctcFlds(false);
		if (getSelectOrTypeId()==0) {//new
		 //plain new
		}else if (getSelectOrTypeId()==1) {//new cedula
			setSelectedPaymentNameMap(new HashMap<Long, PaymentName>());
			if(FormType.CTC_CORPORATION.getId()==getFormTypeId()) {
			/**
			 * 60-CTC Individual
			 * 65-CTC Interest 
			 */
			setFormTypeId(FormType.CTC_INDIVIDUAL.getId());
			int[] ids = {60};
			double[] amounts = {0.00};
			double amount = 0d;
			
			try {
				for(int i=0; i< ids.length; i++) {
					sql = " AND pyid="+ids[i];
					PaymentName py =  PaymentName.retrieve(sql, params).get(0);
					py.setAmount(amounts[i]);
					amount += amounts[i];
					namesDataSelected.add(py);
					getSelectedPaymentNameMap().put(py.getId(), py);
				}
			}catch(Exception e) {e.printStackTrace();}
			
			
			
			if(DateUtils.getCurrentMonth()>=3) {
				try {
						sql = " AND pyid=65";
						PaymentName py =  PaymentName.retrieve(sql, params).get(0);
						py.setAmount(00.00);
						amount += 0.00;
						namesDataSelected.add(py);
						getSelectedPaymentNameMap().put(py.getId(), py);
				}catch(Exception e) {}
			}
			setTotalAmount(Currency.formatAmount(amount));
			
			ctcFlds(true);
			
			calculateCedula();
			}
			
			if(FormType.CTC_CORPORATION.getId()==getFormTypeId()) {
				/**63-BIR CTC
				 * 65-CTC Interest 
				 */
				namesDataSelected = new ArrayList<PaymentName>();
				setSelectedPaymentNameMap(new HashMap<Long, PaymentName>());
				setFormTypeId(FormType.CTC_CORPORATION.getId());
				int[] ids = {63};
				double[] amounts = {0.00};
				double amount = 0d;
				
				try {
					for(int i=0; i< ids.length; i++) {
						sql = " AND pyid="+ids[i];
						PaymentName py =  PaymentName.retrieve(sql, params).get(0);
						py.setAmount(amounts[i]);
						amount += amounts[i];
						namesDataSelected.add(py);
						getSelectedPaymentNameMap().put(py.getId(), py);
					}
				}catch(Exception e) {e.printStackTrace();}
				
				
				
				if(DateUtils.getCurrentMonth()>=3) {
					try {
							sql = " AND pyid=65";
							PaymentName py =  PaymentName.retrieve(sql, params).get(0);
							py.setAmount(00.00);
							amount += 0.00;
							namesDataSelected.add(py);
							getSelectedPaymentNameMap().put(py.getId(), py);
					}catch(Exception e) {}
				}
				setTotalAmount(Currency.formatAmount(amount));
				calculateCedula();
				ctcFlds(true);
			}
			
			
		}else if(getSelectOrTypeId()==2) {//new OR
			setFormTypeId(FormType.AF_51.getId());
			loadLastORToSuggestForNewReceipt();//suggest last transaction
		}else if (getSelectOrTypeId()==3) { //business
			
			/**
			 * 60-CT Individual
			 * 38-Business Plate Misc.
			 * 28-Garbage fee
			 * 18-Inspection fee
			 * 4-Mayor's Permit
			 * 37-other Misc income
			 * 48-Occupation Tax
			 * 36-Police Misc
			 * 33-Sanitary Fees
			 * 20-Secretary Fees
			 * 9-Zonal
			 */
			setFormTypeId(FormType.AF_51.getId());
			int[] ids = {60,38,28,18,4,37,48,36,33,20,9};
			double[] amounts = {0.00,350.00,1100.00,40.00,770.00,40.00,100.00,40.00,40.00,50.00,100.00};
			double amount = 0d;
			try {
				for(int i=0; i< ids.length; i++) {
					sql = " AND pyid="+ids[i];
					PaymentName py =  PaymentName.retrieve(sql, params).get(0);
					py.setAmount(amounts[i]);
					amount += amounts[i];
					namesDataSelected.add(py);
					getSelectedPaymentNameMap().put(py.getId(), py);
				}
			}catch(Exception e) {e.printStackTrace();}
			
			setTotalAmount(Currency.formatAmount(amount));
			
		}else if (getSelectOrTypeId()==4) { //fish cage
			//3-fishery rental 0.00
			/**
			 * 3-Fishery Rental
			 * 4-Mayor's Permit
			 * 20-Secretary Fees
			 * 36-Police Misc
			 * 33-Sanitary Fees
			 * 18-Inspection fee
			 * 53-DST
			 * 14-Banca Registration Fee
			 * 9-Zonal
			 * 60-CT Individual
			 */
			setFormTypeId(FormType.AF_51.getId());
			int[] ids = {3,4,20,36,33,18,53,14,9,60};
			double[] amounts = {0.00,100.00,50.00,40.00,40.00,40.00,30.00,75.00,100.00,0.00};
			double amount = 0d;
			try {
				for(int i=0; i< ids.length; i++) {
					sql = " AND pyid="+ids[i];
					PaymentName py =  PaymentName.retrieve(sql, params).get(0);
					py.setAmount(amounts[i]);
					amount += amounts[i];
					namesDataSelected.add(py);
					getSelectedPaymentNameMap().put(py.getId(), py);
				}
			}catch(Exception e) {}
			
			setTotalAmount(Currency.formatAmount(amount));
		}else if (getSelectOrTypeId()==5) { //skylab permit
			
			/**
			 * 10-Skylab Permit
			 * 11-Filling Permit
			 * 20-Secretary Fees
			 * 12-Sticker
			 * 53-DST
			 * 13-Fines and Penalty
			 */
			setFormTypeId(FormType.AF_51.getId());
			int[] ids = {10,11,20,12,53};
			double[] amounts = {320.00,180.00,50.00,50.00,30.00};
			double amount = 0d;
			try {
				for(int i=0; i< ids.length; i++) {
					sql = " AND pyid="+ids[i];
					PaymentName py =  PaymentName.retrieve(sql, params).get(0);
					py.setAmount(amounts[i]);
					amount += amounts[i];
					namesDataSelected.add(py);
					getSelectedPaymentNameMap().put(py.getId(), py);
				}
			}catch(Exception e) {}
			
			if(DateUtils.getCurrentMonth()==1 && DateUtils.getCurrentDay()>20) {
				try {
						sql = " AND pyid=13";
						PaymentName py =  PaymentName.retrieve(sql, params).get(0);
						py.setAmount(80.00);
						amount += 80.00;
						namesDataSelected.add(py);
						getSelectedPaymentNameMap().put(py.getId(), py);
				}catch(Exception e) {}
			}
			
			
			setTotalAmount(Currency.formatAmount(amount));
			
			
		}
	}
	
	
	public void clearFlds() {
		
		setStatusId(4);
		setStatusSearchId(0);
		setCollectorId(0);
		setOrNumber(null);
		setDateTrans(null);
		setFormTypeId(1);
		setPayorName(null);
		setAddress(null);
		setTotalAmount("0.00");
		setOrRecordData(null);
		setOrnameListData(null);
		setSelectedPaymentNameMap(null);
		setSearchPayName(null);
		setFormInfo(null);
		
		setLabel2(0);
		setLabel3(0);
		setLabel4(0);
		setAmount1(0);
		setAmount2(0);
		setAmount3(0);
		setAmount4(0);
		setAmount5(0);
		setAmount6(0);
		setAmount7(0);
		setGenderId(1);
		setBirthdate(null);
		setTinNo(null);
		setHieghtDateReg(null);
		setWeight(null);
		setCustomerAddress(null);
		//setCivilStatusOrganization(null);
		setProfessionBusinessNature(null);
		setSignatory(null);
		setPlaceOfBirth(null);
		setCitizenshipOrganization(null);
		
		namesDataSelected = new ArrayList<PaymentName>();//Collections.synchronizedList(new ArrayList<PaymentName>());
		selectedPaymentNameMap = new HashMap<Long, PaymentName>();//Collections.synchronizedMap(new HashMap<Long, PaymentName>());
	}
	
	public void saveData() {
		Customer customer = selectedCustomer();
		ORListing or = new ORListing();
		
		if(getOrRecordData()!=null) {
			or = getOrRecordData();
		}else {
			or.setIsActive(1);
		}
		
		if(customer==null) {
			customer = new Customer();
			customer.setFullName(getPayorName().toUpperCase());
			customer.setAddress(getAddress());
			customer.setContactNumber("0");
			customer.setIsActive(1);
			customer.setRegistrationDate(DateUtils.getCurrentDateYYYYMMDD());
			UserDtls user = Login.getUserLogin().getUserDtls();
			customer.setUserDtls(user);
			customer = Customer.save(customer);
		}else {
			customer.setFullName(getPayorName().toUpperCase());
			customer.setAddress(getAddress());
			customer.setContactNumber("0");
			customer.setIsActive(1);
			customer.setRegistrationDate(DateUtils.getCurrentDateYYYYMMDD());
			UserDtls user = Login.getUserLogin().getUserDtls();
			customer.setUserDtls(user);
			customer = Customer.save(customer);
		}
		boolean isOk = true;
		if(getOrNumber()==null || getOrNumber().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide official receipt");
		}
		if(getPayorName()==null || getPayorName().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide payor name");
		}
		
		if(getSelectedPaymentNameMap()==null) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide payment name");
		}
		
		if(isOk) {
			
			if(FormType.CTC_INDIVIDUAL.getId()==getFormTypeId() || FormType.CTC_CORPORATION.getId()==getFormTypeId()) {
				String val = "";
				
				val = getLabel2() + "<->";
				val += getLabel3() + "<->";
				val += getLabel4() + "<->";
				val += getAmount1() + "<->";
				val += getAmount2() + "<->";
				val += getAmount3() + "<->";
				val += getAmount4() + "<->";
				val += getAmount5() + "<->";
				val += getAmount6() + "<->";
				val += getAmount7() + "<->";
				val += getGenderId() + "<->";
				val += getBirthdate()==null? "0<->" : DateUtils.convertDate(getBirthdate(), "yyyy-MM-dd") + "<->";
				val += getTinNo().isEmpty()? "0<->" : getTinNo() + "<->";
				val += getHieghtDateReg().isEmpty()? "0<->" : getHieghtDateReg() + "<->";
				val += getWeight()!=null? getWeight() + "<->" : "0<->";  //   getWeight().isEmpty()? "0<->" : getWeight() + "<->";
				val += getCustomerAddress().isEmpty()? "0<->" : getCustomerAddress() + "<->";
				val += getCivilStatusId()==0? "0<->" : getCivilStatusId() + "<->";
				val += getProfessionBusinessNature().isEmpty()? "0<->" : getProfessionBusinessNature() + "<->";
				val += GlobalVar.MTO_OR_CEDULA_SIGNATORY + "<->";//signatory
				val += getPlaceOfBirth().isEmpty()? "0<->" : getPlaceOfBirth() +"<->";
				val += getCitizenshipOrganization().isEmpty()? "0" : getCitizenshipOrganization();
				
				or.setForminfo(val);
			}
			
			
			
			
			
			or.setStatus(getStatusId());
			or.setDateTrans(DateUtils.convertDate(getDateTrans(), "yyyy-MM-dd"));
			or.setFormType(getFormTypeId());
			or.setCustomer(customer);
			try{or.setOrNumber(getOrNumber().trim());}catch(Exception e){}
			Collector col = new Collector();
			col.setId(getCollectorId());
			or.setCollector(col);
			or = ORListing.save(or);
			
			//delete first paynames attached in orlisting table
			//this will ensure for duplication of data
			ORNameList.delete("DELETE FROM ornamelist WHERE orid=" + or.getId(), new String[0]);
			
			List<PaymentName> tmpName = new ArrayList<PaymentName>();
			Map<Long, PaymentName> tmpMap = new HashMap<Long, PaymentName>();
			for(PaymentName name : getSelectedPaymentNameMap().values()) {
				if(name.getAmount()>0) {
					ORNameList o = new ORNameList();
					o.setAmount(name.getAmount());
					o.setOrList(or);
					o.setCustomer(customer);
					o.setIsActive(1);
					o.setPaymentName(name);
					o.save();
					
					name.setAmount(0);
					tmpName.add(name);
					tmpMap.put(name.getId(), name);
				}
			}
			Application.addMessage(1, "Success", "Successfully saved.");
			if(isCollectorsMode()) {
				setSearchName(getPayorName());
			}
			
			namesDataSelected = new ArrayList<PaymentName>();
			selectedPaymentNameMap = new HashMap<Long, PaymentName>();
			setPayorName(null);
			setTotalAmount("0.00");
			setOrRecordData(null);
			setOrnameListData(null);
			setSelectedPaymentNameMap(null);
			setOrNumber(null);
			init(); //do not load data to reduce loading
			
			setCollectorId(or.getCollector().getId());
			
			
			//if(getSelectOrTypeId()>0) {
				//namesDataSelected = tmpName;
				//selectedPaymentNameMap = tmpMap;
			//}
			//forSaveOnly();
			
			if(FormType.CTC_INDIVIDUAL.getId()==getFormTypeId() || FormType.CTC_CORPORATION.getId()==getFormTypeId()) {
				setLabel2(0);
				setLabel3(0);
				setLabel4(0);
				setAmount1(0);
				setAmount2(0);
				setAmount3(0);
				setAmount4(0);
				setAmount5(0);
				setAmount6(0);
				setAmount7(0);
				setGenderId(1);
				setBirthdate(null);
				setTinNo(null);
				setHieghtDateReg(null);
				setWeight(null);
				setCustomerAddress(null);
				setCivilStatusId(1);
				setProfessionBusinessNature(null);
				setPlaceOfBirth(null);
				setCitizenshipOrganization(null);
				selectedOR();
			}
			
			setFormTypeId(FormType.AF_51.getId());
		}
		
	}
	
	private void ctcFlds(boolean enanleCTC) {
		
		PrimeFaces pf = PrimeFaces.current();
		if(enanleCTC) {
			pf.executeScript("$('#ctid').show()");
		}else {
			pf.executeScript("$('#ctid').hide()");
		}
		
	}
	
	public void clickItem(ORListing or) {
		setSelectedPaymentNameMap(new HashMap<Long, PaymentName>());//empty the temporary data location
		setOrRecordData(or);
		setStatusId(or.getStatus());
		setOrnameListData(or.getOrNameList());
		setDateTrans(DateUtils.convertDateString(or.getDateTrans(), "yyyy-MM-dd"));
		setOrNumber(or.getOrNumber());
		setFormTypeId(or.getFormType());
		setPayorName(or.getCustomer().getFullName());
		setAddress(or.getCustomer().getAddress());
		setCollectorId(or.getCollector().getId());
		namesDataSelected = new ArrayList<PaymentName>();//Collections.synchronizedList(new ArrayList<PaymentName>());
		double amount = 0d;
		/*for(ORNameList on : or.getOrNameList()) {
			PaymentName name = on.getPaymentName();
			name.setAmount(on.getAmount());
			namesDataSelected.add(name);
			amount += on.getAmount();
		}*/
		String sql = " AND nameL.isactiveol=1 AND nameL.orid="+or.getId();
		ctcFlds(false);
		if(FormType.CTC_INDIVIDUAL.getId()==or.getFormType() || FormType.CTC_CORPORATION.getId()==or.getFormType()) {
			
			if(or.getForminfo()!=null && !or.getForminfo().isEmpty()) {
				System.out.println(or.getForminfo());
			String[] val = or.getForminfo().split("<->");
			
			setLabel2(Double.valueOf(val[0]));
			setLabel3(Double.valueOf(val[1]));
			setLabel4(Double.valueOf(val[2]));
			setAmount1(Double.valueOf(val[3]));
			setAmount2(Double.valueOf(val[4]));
			setAmount3(Double.valueOf(val[5]));
			setAmount4(Double.valueOf(val[6]));
			setAmount5(Double.valueOf(val[7]));
			setAmount6(Double.valueOf(val[8]));
			setAmount7(Double.valueOf(val[9]));
			setGenderId(Integer.valueOf(val[10]));
			setBirthdate(val[11].equalsIgnoreCase("0")? null : DateUtils.convertDateString(val[11], "yyyy-MM-dd"));
			setTinNo(val[12].equalsIgnoreCase("0")? "" : val[12]);
			setHieghtDateReg(val[13].equalsIgnoreCase("0")? "" : val[13]);
			setWeight(val[14].equalsIgnoreCase("0")? "" : val[14]);
			setCustomerAddress(val[15].equalsIgnoreCase("0")? "" : val[15]);
			setCivilStatusId(Integer.valueOf(val[16]));
			setProfessionBusinessNature(val[17].equalsIgnoreCase("0")? "" : val[17]);
			setSignatory(val[18].equalsIgnoreCase("0")? "" : val[18]);
			setPlaceOfBirth(val[19].equalsIgnoreCase("0")? "" : val[19]);
			setCitizenshipOrganization(val[20].equalsIgnoreCase("0")? "" : val[20]);
			ctcFlds(true);
			
			if(FormType.CTC_INDIVIDUAL.getId()==or.getFormType()) {
				setEnableBirthday(false);
			}else {
				setEnableBirthday(true);
			}
			
			}
			
		}else {
			ctcFlds(false);
		}
		
		for(ORNameList on : ORNameList.retrieve(sql, new String[0])) {
			PaymentName name = on.getPaymentName();
			name.setAmount(on.getAmount());
			namesDataSelected.add(name);
			amount += on.getAmount();
			getSelectedPaymentNameMap().put(on.getPaymentName().getId(), name);
		}
		setTotalAmount(Currency.formatAmount(amount));
	}
	
	public void editPay(PaymentName names) {
		setSearchPayName(names.getName());
		loadPaymentNames();
		if(namesData!=null && namesData.size()>0) {
			namesData.get(0).setAmount(names.getAmount());
		}
	}
	
	public void paynameDelete(PaymentName names) {
		if(getSelectedPaymentNameMap()!=null && getSelectedPaymentNameMap().size()>0) {
			getSelectedPaymentNameMap().remove(names.getId());
		}
		try{
			if(getOrnameListData()!=null && getOrnameListData().size()>0) {
				for(ORNameList or : getOrnameListData()) {
					if(names.getId()==or.getPaymentName().getId()) {
						or.delete();
					}
				}
			}
		}catch(Exception e) {e.printStackTrace();System.out.println("no data>>");}
		
		List<PaymentName> nms = new ArrayList<PaymentName>();//Collections.synchronizedList(new ArrayList<PaymentName>());
		for(PaymentName n : namesDataSelected) {
			if(names.getId()!=n.getId()) {
				nms.add(n);
			}
		}
		namesDataSelected = nms;
		double amount = 0d;
		for(PaymentName on : namesDataSelected) {
			amount += on.getAmount();
		}
		setTotalAmount(Currency.formatAmount(amount));
		init();
		Application.addMessage(1, "Sucess", "Successfully deleted.");
	}
	
	public void deleteRow(ORListing or) {
		or.delete();
		init();
		clearFlds();
		Application.addMessage(1, "Sucess", "Successfully deleted.");
	}
	
	public void printOR(ORListing py) {
		
		try{
			
			String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
					AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			
			String REPORT_NAME = "OR51_" + DocumentFormatter.getTagName("printer-type");//GlobalVar.OR51_FX2175_PRINT;
			
			//String printFormat = DocumentFormatter.getTagName("printer-type");
			
			
			
			ReportCompiler compiler = new ReportCompiler();
			HashMap param = new HashMap();
	  		
	  		param.put("PARAM_DATE", DateUtils.convertDateToMonthDayYear(py.getDateTrans()));
	  		param.put("PARAM_PAYOR", py.getCustomer().getFullName());
	  		com.italia.municipality.lakesebu.controller.NumberToWords numberToWords = new NumberToWords();
	  		
	  		String amnt = Currency.formatAmount(py.getAmount());
	  		amnt = amnt.replace(",", "");
	  		param.put("PARAM_WORDS", numberToWords.changeToWords(amnt).toUpperCase().replace("/", " / ") );
	  		param.put("PARAM_CASHCHECK", "CASH");
	  		param.put("PARAM_COLLECTING_OFFICER", "FERDINAND L. LOPEZ");
			
	  		List<OR51> ors = new ArrayList<OR51>();
			if(FormType.CTC_INDIVIDUAL.getId()==py.getFormType() || FormType.CTC_CORPORATION.getId()==py.getFormType()) {
				REPORT_NAME = "cedula_" + DocumentFormatter.getTagName("printer-type");//GlobalVar.CEDULA_FX2175_PRINT;
				if(py.getForminfo()!=null && !py.getForminfo().isEmpty()) {
					
					String[] dt = py.getDateTrans().split("-");
					param.put("PARAM_DATE", dt[1] + " " + dt[2] + " " + dt[0]);
					
					String[] val = py.getForminfo().split("<->");
					
					/*
					setLabel2(Double.valueOf(val[0]));
					setLabel3(Double.valueOf(val[1]));
					setLabel4(Double.valueOf(val[2]));
					setAmount1(Double.valueOf(val[3]));
					setAmount2(Double.valueOf(val[4]));
					setAmount3(Double.valueOf(val[5]));
					setAmount4(Double.valueOf(val[6]));
					setAmount5(Double.valueOf(val[7]));
					setAmount6(Double.valueOf(val[8]));
					setAmount7(Double.valueOf(val[9]));
					setGenderId(Integer.valueOf(val[10]));
					setBirthdate(val[11].equalsIgnoreCase("0")? null : DateUtils.convertDateString(val[11], "yyyy-MM-dd"));
					setTinNo(val[12].equalsIgnoreCase("0")? "" : val[12]);
					setHieghtDateReg(val[13].equalsIgnoreCase("0")? "" : val[13]);
					setWeight(val[14].equalsIgnoreCase("0")? "" : val[14]);
					setCustomerAddress(val[15].equalsIgnoreCase("0")? "" : val[15]);
					setCivilStatusId(Integer.valueOf(val[16]));
					setProfessionBusinessNature(val[17].equalsIgnoreCase("0")? "" : val[17]);
					setSignatory(val[18].equalsIgnoreCase("0")? "" : val[18]);
					setPlaceOfBirth(val[19].equalsIgnoreCase("0")? "" : val[19]);
					setCitizenshipOrganization(val[20].equalsIgnoreCase("0")? "" : val[20]);
					*/
					
					param.put("PARAM_LABEL2", Currency.formatAmount(Double.valueOf(val[0])));
					param.put("PARAM_LABEL3", Currency.formatAmount(Double.valueOf(val[1])));
					param.put("PARAM_LABEL4", Currency.formatAmount(Double.valueOf(val[2])));
					
					param.put("PARAM_YEAR", py.getDateTrans().split("-")[0]);
					param.put("PARAM_POI", "MTO-LAKE SEBU");
					param.put("PARAM_ADDRESS", py.getCustomer().getAddress());
					param.put("PARAM_TIN", val[12].equalsIgnoreCase("0")? "" : val[12]);
					param.put("PARAM_CITIZENSHIP", val[20].equalsIgnoreCase("0")? "" : val[20]);
					param.put("PARAM_CIVIL", CivilStatus.typeName(Integer.valueOf(val[16])));
					param.put("PARAM_PROF",val[17].equalsIgnoreCase("0")? "" : val[17]);
					param.put("PARAM_POB", val[19].equalsIgnoreCase("0")? "" : val[19]);
					param.put("PARAM_GENDER", Integer.valueOf(val[10])==1? "MALE":"FEMALE");
					param.put("PARAM_H", val[13].equalsIgnoreCase("0")? "" : val[13]);
					param.put("PARAM_W", val[14].equalsIgnoreCase("0")? "" : val[14]);
					param.put("PARAM_ONE", Currency.formatAmount(Double.valueOf(val[3])));
					param.put("PARAM_TWO", Currency.formatAmount(Double.valueOf(val[4])));
					param.put("PARAM_THREE", Currency.formatAmount(Double.valueOf(val[5])));
					param.put("PARAM_FOUR", Currency.formatAmount(Double.valueOf(val[6])));
					param.put("PARAM_FIVE", Currency.formatAmount(Double.valueOf(val[7])));
					param.put("PARAM_INTEREST", Currency.formatAmount(Double.valueOf(val[8])));
					param.put("PARAM_GRAND", Currency.formatAmount(Double.valueOf(val[9])));
					param.put("PARAM_BIRTH", val[11]);
					
		  			ors.add(new OR51());
				}
				
			}else { //Form51
			
				
		  		
		  		
		  		int cnt = 0;
		  		double amount = 0d;
		  		for(ORNameList na : py.getOrNameList()) {
		  			if(na.getAmount()>0) {
		  				OR51 or = new OR51();
				  		or.setDescription(na.getPaymentName().getName());
				  		or.setCode(na.getPaymentName().getAccountingCode());
				  		or.setAmount(Currency.formatAmount(na.getAmount()));
			  			ors.add(or);
			  			cnt++;
			  			amount += na.getAmount();
		  			}
		  		}
		  		
		  		if(cnt<12) {
		  			int addFldSize = 11 - cnt;
			  		for(int i=1; i<=addFldSize; i++) {
			  			OR51 or = new OR51();
				  		or.setDescription("");
				  		or.setCode("");
				  		or.setAmount("");
			  			ors.add(or);
			  		}
		  		}
		  		
		  		
		  		//total amount
		  		OR51 or = new OR51();
		  		or.setDescription("");
		  		or.setCode("");
		  		or.setAmount(Currency.formatAmount(amount));
	  			ors.add(or);
	  		
			}
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
	  		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(ors);
	  		
	  		
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
	  	    
	  	    File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
			 FacesContext faces = FacesContext.getCurrentInstance();
			 ExternalContext context = faces.getExternalContext();
			 HttpServletResponse response = (HttpServletResponse)context.getResponse();
				
		     BufferedInputStream input = null;
		     BufferedOutputStream output = null;
	  	    try {
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
	                //System.out.println("printReportAll read : " + length);
	            }

	            // Finalize task.
	            output.flush();
		    }finally {
		    	 close(output);
		         close(input);
		    }	
	            
			
			}catch(Exception e){
				e.printStackTrace();
			}
		
	}
	
	
	public void print() {
		//List<Reports> rpts = Collections.synchronizedList(new ArrayList<Reports>());
		
		try {
		compileReportList(rpts);
		
		String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
				AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
		String REPORT_NAME = "generalcollections";
		if(getCollectorSearchId()!=0) {
			REPORT_NAME = "generalcollectionscollector";
		}
		//String REPORT_PATH_FILE = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() +  AppConf.CHEQUE_REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
		
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
	                //System.out.println("printReportAll read : " + length);
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
	     
	       // backupPrintDispense();
	        
		}catch(Exception ioe){
			ioe.printStackTrace();
		}
	}
	
	public void printSummary() {
		
				List<Reports> rpts = new ArrayList<Reports>();//Collections.synchronizedList(new ArrayList<Reports>());
				Reports rpt = new Reports();
				rpts.add(rpt);
				try {
					compileReportSummary(rpts);
				
				String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
						AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
				String REPORT_NAME = "summarycollection";
				
				
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
			                //System.out.println("printReportAll read : " + length);
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
			     
			       // backupPrintDispense();
			        
				}catch(Exception ioe){
					ioe.printStackTrace();
				}
	}
	
	private  void compileReportList(List<Reports> reportFields){
		try{
			
			String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
					AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			String REPORT_NAME = "generalcollections";
			
			if(getCollectorSearchId()!=0) {
				REPORT_NAME = "generalcollectionscollector";
			}
			
			//String REPORT_PATH_FILE = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() +  AppConf.CHEQUE_REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
	  		ArrayList<ReportFields> rpts = new ArrayList<ReportFields>();
	  		/*for(ReportFields r : reportFields){
	  			rpts.add(r);
	  		}*/
	  		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reportFields);
	  		HashMap param = new HashMap();
	  		
	  		/*DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");//new SimpleDateFormat("MM/dd/yyyy");//new SimpleDateFormat("yyyy/MM/dd hh:mm: a");
			Date date = new Date();
			String _date = dateFormat.format(date);
			param.put("PARAM_DATE_RANGE", "From: "+ DateUtils.convertDate(getDateFrom(),"yyyy-MM-dd") + " to " + DateUtils.convertDate(getDateTo(),"yyyy-MM-dd"));
	  		param.put("PARAM_DATE", "Printed: "+_date);
	  		
	  		param.put("PARAM_PREPAREDBY", "Prepared By: "+ Login.getUserLogin().getUserDtls().getFirstname() + " " + Login.getUserLogin().getUserDtls().getLastname());*/
	  		
	  		param.put("PARAM_TITLE", "GENERAL REPORT COLLECTION");
	  		param.put("PARAM_SUM_NAME", getSumNames());
	  		param.put("PARAM_SUM_AMOUNT", getSumAmounts());
	  		
	  		if(getCollectorSearchId()!=0) {
	  			Collector col = Collector.retrieve(getCollectorSearchId());
	  			param.put("PARAM_COLLECTOR", "Collector Name: " +col.getName().toUpperCase());
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
			param.put("PARAM_WATERMARK", off);
			}catch(Exception e){e.printStackTrace();}
	  		
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
		
		
			System.out.println("pdf successfully created...");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
	
	public void printSummaryOnly() {
		try{
			List<Reports> rss = new ArrayList<Reports>();//Collections.synchronizedList(new ArrayList<Reports>());
			
			int size = getRptsOnly().size();
			
			if(size>35) {
				int cnt=1;
				List<Reports> r1 = new ArrayList<Reports>();//Collections.synchronizedList(new ArrayList<>());
				List<Reports> r2 = new ArrayList<Reports>();//Collections.synchronizedList(new ArrayList<>());
				int cntRem = 1;
				for(Reports r : getRptsOnly()) {
					if(cnt<36) {
						r1.add(r);
					}else {
						r2.add(r);
						cntRem++;
					}
					cnt++;
				}
				cntRem -=1;
				
				//this for r2 only data
				for(int i=cntRem; i<36; i++) {//supply the remaining fields
					r2.add(new Reports());
				}
				
				
				//combine the two list
				for(int i=0; i<35;i++) {
					Reports r = new Reports();
					r.setF1(r1.get(i).getF1());
					r.setF2(r1.get(i).getF2());
					
					r.setF3(r2.get(i).getF1());
					r.setF4(r2.get(i).getF2());
					
					rss.add(r);
				}
				
			}else {
				rss = getRptsOnly();
			}
			
			
			
			String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
					AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			String REPORT_NAME = "summaryonly";
			
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
	  		
	  		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(rss);
	  		HashMap param = new HashMap();
	  		UserDtls user = Login.getUserLogin().getUserDtls();
	  		param.put("PARAM_TITLE", "SUMMARY OF COLLECTION FOR THE MONTH OF "+DateUtils.getMonthName(getMonthId()).toUpperCase() + " " + getYearId());
	  		param.put("PARAM_TOTAL", "Php"+getTotalAmountSummaryOnly());
	  		param.put("PARAM_PRINTED_DATE", DateUtils.getCurrentDateMMMMDDYYYY());
	  		
	  		
	  		param.put("PARAM_PREPAREDBY", user.getFirstname().toUpperCase() + " " + user.getLastname().toUpperCase());
	  		
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
	  		
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
	  	    
	  	    
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
		                //System.out.println("printReportAll read : " + length);
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
		     
		       // backupPrintDispense();
		        
			
		
			System.out.println("pdf successfully created...");
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	private  void compileReportSummary(List<Reports> reportFields){
		try{
			
			String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
					AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			String REPORT_NAME = "summarycollection";
			
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
	  		
	  		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reportFields);
	  		HashMap param = new HashMap();
	  		UserDtls user = Login.getUserLogin().getUserDtls();
	  		param.put("PARAM_TITLE", "SUMMARY COLLECTION REPORT");
	  		String summary = getRptSummary();
	  		
	  		summary = summary.replace(", "+user.getFirstname(), ".");
	  		String preparedBy = user.getFirstname() + " " + user.getLastname();
	  		summary += "\n\n\n\n\n\n\n";
	  		summary += "Prepared By: " + preparedBy;
	  		
	  		param.put("PARAM_SUMMARY", summary);
	  		
	  		
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
			param.put("PARAM_WATERMARK", off);
			}catch(Exception e){e.printStackTrace();}
	  		
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
		
		
			System.out.println("pdf successfully created...");
			}catch(Exception e){
				e.printStackTrace();
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

	public Date getDateTrans() {
		
		if(dateTrans==null) {
			dateTrans = DateUtils.getDateToday();
		}
		
		return dateTrans;
	}
	public void setDateTrans(Date dateTrans) {
		this.dateTrans = dateTrans;
	}

	public List<PaymentName> getNamesData() {
		return namesData;
	}

	public void setNamesData(List<PaymentName> namesData) {
		this.namesData = namesData;
	}

	public List<PaymentName> getNamesDataSelected() {
		return namesDataSelected;
	}

	public void setNamesDataSelected(List<PaymentName> namesDataSelected) {
		this.namesDataSelected = namesDataSelected;
	}

	public Map<Long, PaymentName> getSelectedPaymentNameMap() {
		return selectedPaymentNameMap;
	}

	public void setSelectedPaymentNameMap(Map<Long, PaymentName> selectedPaymentNameMap) {
		this.selectedPaymentNameMap = selectedPaymentNameMap;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getSearchPayName() {
		return searchPayName;
	}

	public void setSearchPayName(String searchPayName) {
		this.searchPayName = searchPayName;
	}
	
	public void calculateCedula() {
		
		double total = getAmount1() + getAmount2() + getAmount3() + getAmount4();
		setAmount5(total);
		double rate = Cedula.cedulaPenaltyRate(Months.getMonth(DateUtils.getCurrentMonth()));
		double interest = 0d;
		rate = rate/100;
		
		if(getAmount1()>0) {
			interest = rate * getAmount1();
		}
		
		if(getAmount2()>0) {
			interest = rate * getAmount2();
		}
		
		if(getAmount3()>0) {
			interest = rate * getAmount3();
		}
		
		if(getAmount4()>0) {
			interest = rate * getAmount4();
		}
		
		
		
		setAmount5(total);
		if(getAmount5()>0) {
			interest = rate * getAmount5();
		}
		
		
		setAmount6(interest);
		setAmount7(total + interest);
		
		int index=getNamesDataSelected().size();
		
		if(index==2) {
			getNamesDataSelected().get(0).setDateTrans(DateUtils.getCurrentDateMonthDayYear());
			getNamesDataSelected().get(0).setAmount(getAmount5());
			
			getNamesDataSelected().get(1).setDateTrans(DateUtils.getCurrentDateMonthDayYear());
			getNamesDataSelected().get(1).setAmount(interest);
		}else {
			getNamesDataSelected().get(0).setDateTrans(DateUtils.getCurrentDateMonthDayYear());
			getNamesDataSelected().get(0).setAmount(getAmount5());
		}
	}
	
	public void updateORNumber() {
		orNumber = ORListing.getLatestORNumber(getFormTypeId(),getCollectorId());
		ctcFlds(false);
		if(FormType.CT_2.getId()==getFormTypeId() || FormType.CT_5.getId()==getFormTypeId()) {
			setPayorName("N/A");
			setOrNumber("0");
			setSearchPayName("cash");
			cedulaInterest();
		}else if(FormType.CTC_INDIVIDUAL.getId()==getFormTypeId()) {
			setAmount1(5.00);
			setSearchPayName("ctc");
			ctcFlds(true);
			enableBirthday=false;
			cedulaInterest();
		}else if(FormType.CTC_CORPORATION.getId()==getFormTypeId()) {
			setAmount1(500.00);
			setSearchPayName("ctc");
			ctcFlds(true);
			enableBirthday=true;
			cedulaCorporation();
		}else if(FormType.AF_51.getId()==getFormTypeId()) {
			setSearchPayName("tax");
		}else if(FormType.AF_52.getId()==getFormTypeId()) {
			setSearchPayName("cattle");
		}else if(FormType.AF_47.getId()==getFormTypeId()) {
			setSearchPayName("Death/Burial Income");
		}
		
		if(getSelectOrTypeId()>0) {//show suggested if not NEW is selected
			loadLastORToSuggestForNewReceipt();
		}
		
	}
	
	
	private void cedulaInterest() {
		
		double rate = Cedula.cedulaPenaltyRate(Months.getMonth(DateUtils.getCurrentMonth()));
		double interest = 0d;
		rate = rate/100;
		
		/**
		 * 60-CTC Individual
		 * 65-CTC Interest 
		 */
		namesDataSelected = new ArrayList<>();
		setSelectedPaymentNameMap(new HashMap<Long, PaymentName>());
		String sql = "";
		String[] params = new String[0];
		setFormTypeId(FormType.CTC_INDIVIDUAL.getId());
		int[] ids = {60};
		double[] amounts = {5.00};
		double amount = 0d;
		
		try {
			for(int i=0; i< ids.length; i++) {
				sql = " AND pyid="+ids[i];
				PaymentName py =  PaymentName.retrieve(sql, params).get(0);
				py.setAmount(amounts[i]);
				amount += amounts[i];
				namesDataSelected.add(py);
				getSelectedPaymentNameMap().put(py.getId(), py);
			}
		}catch(Exception e) {e.printStackTrace();}
		
		
		
		if(DateUtils.getCurrentMonth()>=3) {
			try {
					interest = rate * 5.00;
					sql = " AND pyid=65";
					PaymentName py =  PaymentName.retrieve(sql, params).get(0);
					py.setAmount(interest);
					amount += 0.00;
					namesDataSelected.add(py);
					getSelectedPaymentNameMap().put(py.getId(), py);
			}catch(Exception e) {}
		}
		setAmount5(5.00);
		setAmount6(interest);
		setAmount7(5.00 + interest);
		
		setTotalAmount(Currency.formatAmount(500 + interest));
	}
	
	private void cedulaCorporation() {
		
		double rate = Cedula.cedulaPenaltyRate(Months.getMonth(DateUtils.getCurrentMonth()));
		double interest = 0d;
		rate = rate/100;
		/**
		 * 60-CTC Individual
		 * 65-CTC Interest 
		 */
		namesDataSelected = new ArrayList<>();
		setSelectedPaymentNameMap(new HashMap<Long, PaymentName>());
		String sql = "";
		String[] params = new String[0];
		setFormTypeId(FormType.CTC_CORPORATION.getId());
		int[] ids = {63};
		double[] amounts = {500.00};
		double amount = 0d;
		
		try {
			interest = rate * 500.00;
			for(int i=0; i< ids.length; i++) {
				sql = " AND pyid="+ids[i];
				PaymentName py =  PaymentName.retrieve(sql, params).get(0);
				py.setAmount(amounts[i]);
				amount += amounts[i];
				namesDataSelected.add(py);
				getSelectedPaymentNameMap().put(py.getId(), py);
			}
		}catch(Exception e) {e.printStackTrace();}
		
		
		
		if(DateUtils.getCurrentMonth()>=3) {
			try {
					sql = " AND pyid=62";
					PaymentName py =  PaymentName.retrieve(sql, params).get(0);
					py.setAmount(interest);
					amount += 0.00;
					namesDataSelected.add(py);
					getSelectedPaymentNameMap().put(py.getId(), py);
			}catch(Exception e) {}
		}
		setAmount5(500);
		setAmount6(interest);
		setAmount7(500 + interest);
		
		setTotalAmount(Currency.formatAmount(500 + interest));
	}
	
	public String getOrNumber() {
		if(orNumber==null) {
			orNumber = ORListing.getLatestORNumber(getFormTypeId(),getCollectorId());
		}
		return orNumber;
	}

	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}
	public int getFormTypeId() {
		//if(formTypeId==0) {
			//formTypeId=1;
		//}
		return formTypeId;
	}
	public void setFormTypeId(int formTypeId) {
		this.formTypeId = formTypeId;
	}
	public List getFormTypes() {
		formTypes = new ArrayList<>();
		formTypes.add(new SelectItem(0, "Select Forms"));
		for(FormType form : FormType.values()) {
			formTypes.add(new SelectItem(form.getId(), form.getName()));
		}
		
		return formTypes;
	}
	public void setFormTypes(List formTypes) {
		this.formTypes = formTypes;
	}

	public String getPayorName() {
		return payorName;
	}

	public void setPayorName(String payorName) {
		this.payorName = payorName;
	}

	public String getAddress() {
		if(address==null) {
			address = "Lake Sebu, South Cotabato";
		}
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ORListing getOrRecordData() {
		return orRecordData;
	}

	public void setOrRecordData(ORListing orRecordData) {
		this.orRecordData = orRecordData;
	}

	public List<ORListing> getOrs() {
		return ors;
	}

	public void setOrs(List<ORListing> ors) {
		this.ors = ors;
	}

	public String getTotalCollection() {
		return totalCollection;
	}

	public void setTotalCollection(String totalCollection) {
		this.totalCollection = totalCollection;
	}

	public int getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}

	public List getPaymentTypes() {
		
		paymentTypes = new ArrayList<>();
		paymentTypes.add(new SelectItem(0, "All Payment Names"));
		for(PaymentName name : PaymentName.retrieve("", new String[0])) {
			paymentTypes.add(new SelectItem(name.getId(), name.getName()));
		}
		
		return paymentTypes;
	}

	public void setPaymentTypes(List paymentTypes) {
		this.paymentTypes = paymentTypes;
	}

	public int getMonthId() {
		return monthId;
	}

	public void setMonthId(int monthId) {
		this.monthId = monthId;
	}

	public List getMonths() {
		
		months = new ArrayList<>();
		months.add(new SelectItem(0, "All Months"));
		for(int month=1; month<=12; month++) {
			months.add(new SelectItem(month, DateUtils.getMonthName(month)));
		}
		return months;
	}

	public void setMonths(List months) {
		this.months = months;
	}

	public int getYearId() {
		if(yearId==0) {
			yearId = DateUtils.getCurrentYear();
		}
		return yearId;
	}

	public void setYearId(int yearId) {
		this.yearId = yearId;
	}

	public List getYears() {
		
		years = new ArrayList<>();
		years.add(new SelectItem(0, "All years"));
		int lastYear=0;
		for(int year=2019; year<=DateUtils.getCurrentYear(); year++) {
			years.add(new SelectItem(year, year+""));
			lastYear=year;
		}
		lastYear += 1;
		years.add(new SelectItem(lastYear, lastYear+""));
		
		return years;
	}

	public void setYears(List years) {
		this.years = years;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public int getFormTypeSearchId() {
		return formTypeSearchId;
	}

	public void setFormTypeSearchId(int formTypeSearchId) {
		this.formTypeSearchId = formTypeSearchId;
	}

	public List getFormTypeSearch() {
		formTypeSearch = new ArrayList<>();
		formTypeSearch.add(new SelectItem(0, "All Forms"));
		for(FormType form : FormType.values()) {
			formTypeSearch.add(new SelectItem(form.getId(), form.getName()));
		}
		
		return formTypeSearch;
	}

	public void setFormTypeSearch(List formTypeSearch) {
		this.formTypeSearch = formTypeSearch;
	}

	public List<Reports> getDtls() {
		return dtls;
	}

	public void setDtls(List<Reports> dtls) {
		this.dtls = dtls;
	}

	public List getCollectors() {
		
		collectors = new ArrayList<>();
		for(Collector c : Collector.retrieve(" ORDER BY cl.collectorname", new String[0])) {
			if(c.getId()>0) {
				collectors.add(new SelectItem(c.getId(), c.getName()));
			}
		}
		
		return collectors;
	}

	public void setCollectors(List collectors) {
		this.collectors = collectors;
	}

	public int getCollectorId() {
		if(collectorId==0) {
			collectorId = 1;
		}
		return collectorId;
	}

	public void setCollectorId(int collectorId) {
		this.collectorId = collectorId;
	}

	public List getCollectorsSearch() {
		
		collectorsSearch = new ArrayList<>();
		for(Collector c : Collector.retrieve(" ORDER BY cl.collectorname", new String[0])) {
			if(c.getId()==0) {
				collectorsSearch.add(new SelectItem(0, "All Collector"));
			}else {
				collectorsSearch.add(new SelectItem(c.getId(), c.getName()));
			}
		}
		
		return collectorsSearch;
	}

	public void setCollectorsSearch(List collectorsSearch) {
		this.collectorsSearch = collectorsSearch;
	}

	public int getCollectorSearchId() {
		return collectorSearchId;
	}

	public void setCollectorSearchId(int collectorSearchId) {
		this.collectorSearchId = collectorSearchId;
	}

	public List getSorts() {
		sorts = new ArrayList<>();
		sorts.add(new SelectItem(0, "Non Sort"));
		sorts.add(new SelectItem(1, "Order by Date"));
		sorts.add(new SelectItem(2, "Order by Collector"));
		sorts.add(new SelectItem(3, "Order by Form Name"));
		sorts.add(new SelectItem(4, "Order by OR Number"));
		sorts.add(new SelectItem(5, "Order by Payor"));
		return sorts;
	}

	public void setSorts(List sorts) {
		this.sorts = sorts;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public List<ORNameList> getOrnameListData() {
		return ornameListData;
	}

	public void setOrnameListData(List<ORNameList> ornameListData) {
		this.ornameListData = ornameListData;
	}

	public int getStatusId() {
		if(statusId==0) {
			statusId = 4;
		}
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public List getStatus() {
		status = new ArrayList<>();
		
		for(FormStatus s : FormStatus.values()) {
			if(s.getId()==4 || s.getId()==5) {
				status.add(new SelectItem(s.getId(), s.getName()));
			}
		}
		
		return status;
	}

	public void setStatus(List status) {
		this.status = status;
	}

	public int getStatusSearchId() {
		return statusSearchId;
	}

	public void setStatusSearchId(int statusSearchId) {
		this.statusSearchId = statusSearchId;
	}

	public List getStatusSearch() {
		
		statusSearch = new ArrayList<>();
		statusSearch.add(new SelectItem(0, "All Status"));
		for(FormStatus s : FormStatus.values()) {
			if(s.getId()==4 || s.getId()==5) {
				statusSearch.add(new SelectItem(s.getId(), s.getName()));
			}
		}
		
		return statusSearch;
	}

	public void setStatusSearch(List statusSearch) {
		this.statusSearch = statusSearch;
	}

	public boolean isFiltered() {
		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public List<Reports> getRpts() {
		return rpts;
	}

	public void setRpts(List<Reports> rpts) {
		this.rpts = rpts;
	}

	public String getSumNames() {
		return sumNames;
	}

	public void setSumNames(String sumNames) {
		this.sumNames = sumNames;
	}

	public String getSumAmounts() {
		return sumAmounts;
	}

	public void setSumAmounts(String sumAmounts) {
		this.sumAmounts = sumAmounts;
	}

	public List<PaymentName> getSuggestedData() {
		return suggestedData;
	}

	public void setSuggestedData(List<PaymentName> suggestedData) {
		this.suggestedData = suggestedData;
	}

	public int getDepId() {
		return depId;
	}

	public void setDepId(int depId) {
		this.depId = depId;
	}

	public List getDepartments() {
		departments = new ArrayList<>();
		departments.add(new SelectItem(0, "All Department"));
		for(Department dep : Department.retrieve("SELECT * FROM department ORDER BY departmentname", new String[0])) {
			departments.add(new SelectItem(dep.getDepid(), dep.getDepartmentName()));
		}
		
		return departments;
	}

	public void setDepartments(List departments) {
		this.departments = departments;
	}

	public void loadContent() {
		setTextContent("<p><h1>Analyzing the data please wait....</h1></p>");
		Map<String, Double> names = new HashMap<String, Double>();//Collections.synchronizedMap(new HashMap<String, Double>());
		Map<Double, String> amounts = new HashMap<Double, String>();//Collections.synchronizedMap(new HashMap<Double, String>());
		
		
		UserDtls user = Login.getUserLogin().getUserDtls();
		
		String[] greets = {"Good day","Hope this update finds you well","I hope you're having a wonderful day"};
		
		String text = "<p><strong>Greetings</strong></p>";
			  text += "</br>";
			  text +=  "<p><strong>"+ greets[(int) (Math.random() * greets.length)] + ", "+ user.getFirstname() +"</strong></p>";
			  text += "</br>";
			  
			  
			  double amount = 0d;
			  String str = "";
			  for(int month=1; month<=DateUtils.getCurrentMonth(); month++) {
				  String sql = " AND orl.orstatus=4 AND (orl.ordatetrans>=? AND orl.ordatetrans<=?)";//encoded
				  String[] params = new String[2];
				  params[0] = DateUtils.getCurrentYear() + "-"+ (month<10? "0" + month : month) +"-01";
				  params[1] = DateUtils.getCurrentYear() + "-"+ (month<10? "0" + month : month) +"-31";
				  str += "<li>"+ DateUtils.getMonthName(month) +" <strong>Php ";
				  double amnt = 0d;
				  for(ORListing or : ORListing.retrieve(sql, params)) {
					  amount += or.getAmount();
					  amnt += or.getAmount();
					  
					  String name = or.getCollector().getName();
					  String dep = "";
					  try {
						  dep = Department.department(or.getCollector().getDepartment().getDepid()+"").getDepartmentName();
						  name += "("+dep+")";
					  }catch(Exception e) {}
					  if(names!=null && names.containsKey(name)) {
						  double tmp = names.get(name);
						  tmp += or.getAmount();
						  names.put(name, tmp);
					  }else {
						  names.put(name, or.getAmount());
					  }
					  
				  }
				  if(amnt==0) {
					  str += Currency.formatAmount(amnt) +" (No data has been recorded)</strong></li>";
				  }else {
					  str += Currency.formatAmount(amnt) +"</strong></li>";
				  }
			  }
			  text += "<p>As of <strong>"+ DateUtils.getCurrentDateMMMMDDYYYY() +"</strong> the total collected amount is <strong>Php"+ Currency.formatAmount(amount) +"</strong></p>";
			  text += "</br><p>Below are the details collected per month.</p>";
			  //text += "</br>";
			  text += "<ul>";
			  text += str;
			  text += "</ul>";
			  text += "</br>";
			  text += "<p>Collectors Information.</p>";
			  for(String name : names.keySet()) {
				  amounts.put(names.get(name), name);
			  }
			  Map<Double, String> sortedAmount = new TreeMap<Double, String>(amounts);
			  
			  
			  int cnt = 1;
			  str = "";
			  List<String> data = new ArrayList<String>();//Collections.synchronizedList(new ArrayList<String>());
			  for(double a : sortedAmount.keySet()) {
				  //str += "<li>"+ cnt++ +") "+ sortedAmount.get(a) +" <strong>Php" + Currency.formatAmount(a) +"</strong></li>";
				  data.add(sortedAmount.get(a)+" <strong>Php" + Currency.formatAmount(a) +"</strong>");
			  }
			  Collections.reverse(data);
			  for(String s : data) {
				  str +="<li>"+ cnt++ +") "+ s +"</strong>";
			  }
			  text += "<ul>";
			  text += str;
			  text += "</ul>";
			  
			  
		setTextContent(text);
		collectorMsgSavePath(text, "collection");
		setRptSummary(removingHtmlTag(text));
	}
	
	private String removingHtmlTag(String content) {
		
		String[] tags = {"<p>","</p>","<strong>","</strong>","</br>","<ul>","</ul>","<li>","</li>"};
		
		for(int i=0; i<tags.length; i++) {
			if(tags[i].equalsIgnoreCase("<p>")) {
				content = content.replace(tags[i], "\n");
			}else if(tags[i].equalsIgnoreCase("</br>")) {
				content = content.replace(tags[i], "\n");
			}else if(tags[i].equalsIgnoreCase("<li>")) {
				content = content.replace(tags[i], "\n");	
			}else {
				content = content.replace(tags[i], "");
			}
		}
		
		return content;
	}
	
	public void loadMsg() {
		String folderPath = System.getenv("SystemDrive");
		folderPath += File.separator + "webtis" + File.separator + "conf" + File.separator;
		setTextContent(readMsgCollectorFile(folderPath, "collection.tis"));
		
		setRptSummary(removingHtmlTag(getTextContent()));
	}
	
	public String readMsgCollectorFile(String path, String fileName) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(path + fileName));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while(line!=null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			return sb.toString();
			
		}catch(Exception e) {}
		
		return "";
	}
	
	public void collectorMsgSavePath(String msg, String fileName) {
		
		String folderPath = System.getenv("SystemDrive");
		folderPath += File.separator + "webtis" + File.separator + "conf" + File.separator;
		//fileName += fileName;
		
		File file = new File(folderPath);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		File email = new File(folderPath + fileName + ".tis");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		}catch(Exception e) {}
	}
	
	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public String getRptSummary() {
		return rptSummary;
	}

	public void setRptSummary(String rptSummary) {
		this.rptSummary = rptSummary;
	}

	public List<Reports> getRptsOnly() {
		return rptsOnly;
	}

	public void setRptsOnly(List<Reports> rptsOnly) {
		this.rptsOnly = rptsOnly;
	}

	public String getTotalAmountSummaryOnly() {
		return totalAmountSummaryOnly;
	}

	public void setTotalAmountSummaryOnly(String totalAmountSummaryOnly) {
		this.totalAmountSummaryOnly = totalAmountSummaryOnly;
	}

	public String getLimitData() {
		return limitData;
	}

	public void setLimitData(String limitData) {
		this.limitData = limitData;
	}

	public int getSelectOrTypeId() {
		return selectOrTypeId;
	}

	public void setSelectOrTypeId(int selectOrTypeId) {
		this.selectOrTypeId = selectOrTypeId;
	}

	public List getSelectOrTypes() {
		
		selectOrTypes = new ArrayList<>();
		selectOrTypes.add(new SelectItem(0, "New"));
		selectOrTypes.add(new SelectItem(1, "New Cedula"));
		selectOrTypes.add(new SelectItem(2, "New OR"));
		selectOrTypes.add(new SelectItem(3, "New Business OR"));
		selectOrTypes.add(new SelectItem(4, "New Fish Cage OR"));
		selectOrTypes.add(new SelectItem(5, "New Skylab Permit OR"));
		return selectOrTypes;
	}

	public void setSelectOrTypes(List selectOrTypes) {
		this.selectOrTypes = selectOrTypes;
	}

	public boolean isCollectorsMode() {
		return collectorsMode;
	}

	public void setCollectorsMode(boolean collectorsMode) {
		this.collectorsMode = collectorsMode;
	}

	public String getFormInfo() {
		return formInfo;
	}

	public void setFormInfo(String formInfo) {
		this.formInfo = formInfo;
	}

	public double getLabel2() {
		return label2;
	}

	public void setLabel2(double label2) {
		this.label2 = label2;
	}

	public double getLabel3() {
		return label3;
	}

	public void setLabel3(double label3) {
		this.label3 = label3;
	}

	public double getLabel4() {
		return label4;
	}

	public void setLabel4(double label4) {
		this.label4 = label4;
	}

	public double getAmount1() {
		return amount1;
	}

	public void setAmount1(double amount1) {
		this.amount1 = amount1;
	}

	public double getAmount2() {
		return amount2;
	}

	public void setAmount2(double amount2) {
		this.amount2 = amount2;
	}

	public double getAmount3() {
		return amount3;
	}

	public void setAmount3(double amount3) {
		this.amount3 = amount3;
	}

	public double getAmount4() {
		return amount4;
	}

	public void setAmount4(double amount4) {
		this.amount4 = amount4;
	}

	public double getAmount5() {
		return amount5;
	}

	public void setAmount5(double amount5) {
		this.amount5 = amount5;
	}

	public double getAmount6() {
		return amount6;
	}

	public void setAmount6(double amount6) {
		this.amount6 = amount6;
	}

	public double getAmount7() {
		return amount7;
	}

	public void setAmount7(double amount7) {
		this.amount7 = amount7;
	}

	public Date getBirthdate() {
		if(birthdate==null) {
			birthdate = DateUtils.getDateToday();
		}
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getTinNo() {
		return tinNo;
	}

	public void setTinNo(String tinNo) {
		this.tinNo = tinNo;
	}

	public String getHieghtDateReg() {
		return hieghtDateReg;
	}

	public void setHieghtDateReg(String hieghtDateReg) {
		this.hieghtDateReg = hieghtDateReg;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	

	public String getProfessionBusinessNature() {
		return professionBusinessNature;
	}

	public void setProfessionBusinessNature(String professionBusinessNature) {
		this.professionBusinessNature = professionBusinessNature;
	}

	public String getSignatory() {
		return signatory;
	}

	public void setSignatory(String signatory) {
		this.signatory = signatory;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	public String getCitizenshipOrganization() {
		return citizenshipOrganization;
	}

	public void setCitizenshipOrganization(String citizenship) {
		this.citizenshipOrganization = citizenship;
	}

	public int getCivilStatusId() {
		if(civilStatusId==0) {
			civilStatusId = 1;
		}
		return civilStatusId;
	}

	public void setCivilStatusId(int civilStatusId) {
		this.civilStatusId = civilStatusId;
	}

	public List getCivilStatus() {
		
		civilStatus = new ArrayList<>();
		for(CivilStatus s : CivilStatus.values()) {
			civilStatus.add(new SelectItem(s.getId(), s.getName()));
		}
		
		return civilStatus;
	}

	public void setCivilStatus(List civilStatus) {
		this.civilStatus = civilStatus;
	}

	public int getGenderId() {
		if(genderId==0) {
			genderId=1;
		}
		return genderId;
	}

	public void setGenderId(int genderId) {
		this.genderId = genderId;
	}

	public List getGenders() {
		genders = new ArrayList<>();
		genders.add(new SelectItem(1, "Male"));
		genders.add(new SelectItem(2, "Female"));
		return genders;
	}

	public void setGenders(List genders) {
		this.genders = genders;
	}

	public boolean isEnableBirthday() {
		return enableBirthday;
	}

	public void setEnableBirthday(boolean enableBirthday) {
		this.enableBirthday = enableBirthday;
	}
	
	public void oncapture(CaptureEvent captureEvent) {
		System.out.println("=======================================================SCANNING==================================================================");
        try {
            if (captureEvent != null) {
                Result result = null;
                BufferedImage image = null;

                InputStream in = new ByteArrayInputStream(captureEvent.getData());

                image = ImageIO.read(in);

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                result = new MultiFormatReader().decode(bitmap);
                System.out.println("Scanning the qrcode");
                if (result != null) {
                    //setResultText(result.getText());
                	System.out.println(result.getText());
                	setQrcodeMsg("QRCode has been read successfully");
                	//setQrCodeData(result.getText());
                	PrimeFaces pf = PrimeFaces.current();
                	pf.executeScript("PF('dlgCam').hide();PF('dlgSelection').show();");
                	
                }
            }
        } catch (NotFoundException ex) {
            // fall thru, it means there is no QR code in image
        	//setResultText("Image is not readable...");
        	System.out.println("==============WARNING============ERROR=========");
        	setQrcodeMsg("Error reading the qrcode....");
        } catch (ReaderException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	
	public void acceptSelection(String sel) {
		
		if(getCustomerDataSelected()!=null) {
		
		com.italia.municipality.lakesebu.licensing.controller.Customer cus = getCustomerDataSelected();	
		
		PrimeFaces pf = PrimeFaces.current();
		if("individual".equalsIgnoreCase(sel)) {
			pf.executeScript("PF('dlgSelection').hide();");
			setFormTypeId(FormType.CTC_INDIVIDUAL.getId());
		}
		if("corporation".equalsIgnoreCase(sel)) {
			pf.executeScript("PF('dlgSelection').hide();");
			setFormTypeId(FormType.CTC_CORPORATION.getId());
		}
		if("51".equalsIgnoreCase(sel)) {
			pf.executeScript("PF('dlgSelection').hide();");
			setFormTypeId(FormType.AF_51.getId());
		}
		if("52".equalsIgnoreCase(sel)) {
			pf.executeScript("PF('dlgSelection').hide();");
			setFormTypeId(FormType.AF_52.getId());
		}
		if("53".equalsIgnoreCase(sel)) {
			pf.executeScript("PF('dlgSelection').hide();");
			setFormTypeId(FormType.AF_53.getId());
		}
		if("54".equalsIgnoreCase(sel)) {
			pf.executeScript("PF('dlgSelection').hide();");
			setFormTypeId(FormType.AF_54.getId());
		}
		if("56".equalsIgnoreCase(sel)) {
			pf.executeScript("PF('dlgSelection').hide();");
			setFormTypeId(FormType.AF_55.getId());
		}
		
		setPayorName(cus.getLastname().toUpperCase() + ", " + cus.getFirstname().toUpperCase() + " " + cus.getMiddlename().substring(0, 1).toUpperCase() + ".");
		setCustomerAddress(cus.getCompleteAddress());
		
		ctcFlds(false);
		if(FormType.CTC_INDIVIDUAL.getId()==getFormTypeId() || FormType.CTC_CORPORATION.getId()==getFormTypeId()) {
			
			
			setLabel2(0);
			setLabel3(0);
			setLabel4(0);
			setAmount1(5.00);
			setAmount2(0);
			setAmount3(0);
			setAmount4(0);
			
			setGenderId(Integer.valueOf(cus.getGender()));
			setBirthdate(DateUtils.convertDateString(cus.getBirthdate(), "yyyy-MM-dd"));
			setHieghtDateReg(cus.getHeight());
			setWeight(cus.getWeight());
			
			setCivilStatusId(cus.getCivilStatus());
			setProfessionBusinessNature(cus.getWork());
			setPlaceOfBirth(cus.getBornplace());
			setCitizenshipOrganization(cus.getCitizenship());
			
			ctcFlds(true);
			
			if(FormType.CTC_INDIVIDUAL.getId()==getFormTypeId()) {
				setEnableBirthday(false);
				cedulaInterest();
			}else {
				setAmount1(500.00);
				setEnableBirthday(true);
				cedulaCorporation();
			}
			
			
			
		}else {
			ctcFlds(false);
		}
		
		updateORNumber();
		
		}
	}
	
	public void findQRCode() {
		System.out.println("now looking the qrcode......");
		final String jsonData = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("qrcode");
		
		System.out.println("jsondata: " + jsonData);
		
		
		String sql = " AND (cus.qrcode like '%"+ jsonData +"%' OR ";
		sql += " cus.fullname like '%"+ jsonData +"%' ";
		sql += " )";
		String[] params = new String[0];
		
		List<com.italia.municipality.lakesebu.licensing.controller.Customer> cust = com.italia.municipality.lakesebu.licensing.controller.Customer.retrieve(sql, params);
		PrimeFaces pf = PrimeFaces.current();
		if(cust!=null && cust.size()>0) {
			setCustomerDataSelected(cust.get(0));
	    	pf.executeScript("PF('dlgCam').hide();PF('dlgSelection').show();");
		}else {
			Application.addMessage(1, "Error", "This QRCode is not yet registered... Please register it first in Citizen Registration Page");
		}
		
		
		
		
	}
	
	public String getQrcodeMsg() {
		return qrcodeMsg;
	}

	public void setQrcodeMsg(String qrcodeMsg) {
		this.qrcodeMsg = qrcodeMsg;
	}

	public com.italia.municipality.lakesebu.licensing.controller.Customer getCustomerDataSelected() {
		return customerDataSelected;
	}

	public void setCustomerDataSelected(
			com.italia.municipality.lakesebu.licensing.controller.Customer customerDataSelected) {
		this.customerDataSelected = customerDataSelected;
	}
}
