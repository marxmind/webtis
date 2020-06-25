package com.italia.municipality.lakesebu.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.codehaus.groovy.tools.shell.commands.ClearCommand;
import org.primefaces.component.api.UIColumn;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;

import com.italia.municipality.lakesebu.controller.Barangay;
import com.italia.municipality.lakesebu.controller.Certification;
import com.italia.municipality.lakesebu.controller.CollectionInfo;
import com.italia.municipality.lakesebu.controller.ITaxPayorTrans;
import com.italia.municipality.lakesebu.controller.LandPayor;
import com.italia.municipality.lakesebu.controller.PaymentHistory;
import com.italia.municipality.lakesebu.controller.PayorPayment;
import com.italia.municipality.lakesebu.controller.ReadConfig;
import com.italia.municipality.lakesebu.controller.ReportFields;
import com.italia.municipality.lakesebu.controller.Signatory;
import com.italia.municipality.lakesebu.controller.TaxPayerReceipt;
import com.italia.municipality.lakesebu.controller.TaxPayor;
import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.reports.Rcd;
import com.italia.municipality.lakesebu.reports.ReportCompiler;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.utils.Numbers;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ManagedBean(name="historyBean", eager=true)
@ViewScoped
public class HistoryBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3546546757876885751L;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private String searchPayor;
	private List<LandPayor> lands = new ArrayList<LandPayor>();
	private List<TaxPayerReceipt> receipts = new ArrayList<TaxPayerReceipt>();
	
	private List<Certification> basicTax = new ArrayList<Certification>();
	private List<Certification> sefTax = new ArrayList<Certification>();
	
	private String contents;
	private List<Certification> selectedBasic = new ArrayList<Certification>();
	private List<Certification> selectedSef = new ArrayList<Certification>();
	
	private String lotNo;
	private String requestor;
	private String landAddress;
	private String purpose;
	private String preparedBy="JOY ESTARIS";
	private LandPayor selectedLand;
	private static Map<String, Barangay> bars = Collections.synchronizedMap(new HashMap<String, Barangay>());
	private static final String DOC_PATH = AppConf.PRIMARY_DRIVE.getValue() + File.separator + AppConf.APP_CONFIG_FOLDER_NAME.getValue() + File.separator +"rptsExtractFiles" + File.separator;
	private static final String DOC_FILTER = AppConf.PRIMARY_DRIVE.getValue() + File.separator + AppConf.APP_CONFIG_FOLDER_NAME.getValue() + File.separator +AppConf.APP_CONFIG_SETTING_FOLDER.getValue() + File.separator + "extractFilter.max";
	private List<PayorPayment> pays = Collections.synchronizedList(new ArrayList<PayorPayment>());
	private List<PayorPayment> holdpays = Collections.synchronizedList(new ArrayList<PayorPayment>());
	private String searchParama;
	
	private List<PayorPayment> payments = new ArrayList<PayorPayment>();
	private String searchHistory;
	
	@PostConstruct
	public void init() {
		
	}
	
	public void loadHistory() {
		payments = new ArrayList<PayorPayment>();
		String sql ="";
		if(getSearchHistory()!=null && !getSearchHistory().isEmpty()) {
			sql = " AND (tdNo like '%"+ getSearchHistory() +"%'";
			sql += " OR lotNo like '%"+ getSearchHistory() +"%'";
			sql += " OR owner like '%"+ getSearchHistory() +"%'";
			sql += " OR orNumber like '%"+ getSearchHistory() +"%')";
			payments = PayorPayment.retrieve(sql, new String[0]);
		} 
		
	}
	
	public void addRow(PayorPayment py) {
		setLotNo(py.getLotNo());
		setRequestor(py.getOwner());
		setLandAddress(py.getBarangay());
		String type = py.getType();
		payments.remove(py);
		String year = py.getFromYear()+"";
		if(py.getFromYear()!=py.getToYear()) {
			year = py.getFromYear() + "-" + py.getToYear();
		}
		System.out.println("year form>> " + year);
		
		String revision = py.getTdNo().split("-")[0];
		String revisionNumber = py.getTdNo().split("-")[1].substring(3,7);
		String newTDNO = revision + "-" + revisionNumber;
		
		Certification b = Certification.builder()
				.id(py.getId()+"")
				.tdNo(newTDNO)
				.assessedValue(Currency.formatAmount(py.getAssessedValue()))
				.annualTax(Currency.formatAmount(py.getPrincipal()))
				.taxDue(Currency.formatAmount(py.getPrincipal()))
				.penalty(Currency.formatAmount(py.getPenalty()))
				.total(Currency.formatAmount(py.getTotal()))
				.datePaid(py.getPaidDate())
				.orNumber(py.getOrNumber())
				.yearPaid(py.getPaidDate())
				.yearFrom(py.getFromYear()+"")
				.yearTo(py.getToYear()+"")
				.yearPaid(year)
				.build();
		if("BASIC".equalsIgnoreCase(type)) {
			basicTax.add(b);
			selectedBasic.add(b);
		}else {
			sefTax.add(b);
			selectedSef.add(b);
		}
	}
	
	private void collectLotInfoForPrinting() {
		int lastIndex = 0;
		if(getSelectedBasic()!=null && getSelectedSef()!=null) {
			lastIndex = getSelectedBasic().size()>=getSelectedSef().size()? getSelectedBasic().size() - 1 : getSelectedSef().size() - 1; 
		
			
			contents="To whom it may concern:\n\n";
			contents +="\t\tThis is to certify that the name of " + getRequestor() + " a resident of Brgy. " + getLandAddress() + " ";
			contents +="appeared in the Tax Register of this Municipality as the owner of Lot. No. " + getLotNo() + "\n";
			contents +="situated in Brgy. " + getLandAddress() + " bearing TD/FAAS no. " + getSelectedBasic().get(lastIndex).getTdNo() + "\n";
			contents +="with and Assessed value of Php" + Currency.formatAmount(getSelectedBasic().get(lastIndex).getAssessedValue()) + " and annual Tax of Php" + Currency.formatAmount(Double.parseDouble(getSelectedBasic().get(lastIndex).getAssessedValue().replace(",", "")) * 0.01) + ".\n";
		}
	}
	
	public void saveExtract() {
		if(getPays()!=null && getPays().size()>0) {
			for(PayorPayment pay : getPays()) {
				pay.setIsActive(1);
				pay.save();
				getHoldpays().remove(pay);
			}
			setPays(getHoldpays());
			Application.addMessage(1, "Success", "Successfully saved the extracted data.");
		}else {
			Application.addMessage(3, "Error", "No data extracted");
		}
	}
	
	public void clearUpload() {
		setSearchParama(null);
		pays = Collections.synchronizedList(new ArrayList<PayorPayment>());
		holdpays = Collections.synchronizedList(new ArrayList<PayorPayment>());
	}
	
	public void searchExtract() {
		if(getHoldpays()!=null && getHoldpays().size()>0) {
			if(getSearchParama()!=null && !getSearchParama().isEmpty()) {
				pays = Collections.synchronizedList(new ArrayList<PayorPayment>());
				for(PayorPayment p : getHoldpays()) {
					//boolean lotno = (p.getLotNo()==null? false : (p.getLotNo().isBlank()? false : true));
					if(p.getOwner().contains(getSearchParama().toUpperCase()) || 
							p.getTdNo().contains(getSearchParama()) || 
							(p.getLotNo()!=null && p.getLotNo().contains(getSearchParama())) || 
							p.getOrNumber().contains(getSearchParama())) {
						pays.add(p);
					}
				}
			}
		}
		if(getSearchParama()==null || getSearchParama().isEmpty()) {
			pays = getHoldpays();
		}
	}
	
	public void uploadData(FileUploadEvent event){
		
		 try {
			 InputStream stream = event.getFile().getInputstream();
			 String file = event.getFile().getFileName();
			 
			 if(writeDocToFile(event)){
				 Application.addMessage(1, "Success", "Data has been successfully uploaded");
			 }else{
				 Application.addMessage(3, "Error", "Error uploading the data " + file);
			 }
			 
	     } catch (Exception e) {
	     
	     }
		
	}
	
	private boolean writeDocToFile(FileUploadEvent event){
		try{
		InputStream stream = event.getFile().getInputstream();
		String fileExt = event.getFile().getFileName().split("\\.")[1];
		String filename = "rpts-extract-" + DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "."+fileExt.toLowerCase();
		
		System.out.println("writing... writeDocToFile : " + filename);
		
		File dir = new File(DOC_PATH);
		dir.mkdirs();//create dir if not present
		
		File fileDoc = new File(DOC_PATH +  filename);
		Path file = fileDoc.toPath();
		Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
		//return loadToDatabase(fileDoc);
		readExcel(fileDoc);
		return true;
		}catch(IOException e){return false;}
		
	}
	
	public List<PayorPayment> readExcel(File file) {
		loadBarangay();//initialize value of Barangay
		pays = Collections.synchronizedList(new ArrayList<PayorPayment>());
		holdpays = Collections.synchronizedList(new ArrayList<PayorPayment>());
		try {
			String sep = File.separator;
		    POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
		    HSSFWorkbook wb = new HSSFWorkbook(fs);
		    HSSFSheet sheet = wb.getSheetAt(0);
		    HSSFRow row;
		    HSSFCell cell;

		    int rows; // No of rows
		    rows = sheet.getPhysicalNumberOfRows();

		    int cols = 0; // No of columns
		    int tmp = 0;

		    // This trick ensures that we get the data properly even if it doesn't start from first few rows
		    for(int i = 0; i < 10 || i < rows; i++) {
		        row = sheet.getRow(i);
		        if(row != null) {
		            tmp = sheet.getRow(i).getPhysicalNumberOfCells();
		            if(tmp > cols) cols = tmp;
		        }
		    }
		    //System.out.println("rows>>> " + rows + " cols>> " + cols);
		    //Map<String, PayorPayment> payorMap = Collections.synchronizedMap(new LinkedHashMap<String, PayorPayment>());
		    Map<String, PayorPayment> trackDuplicate = new LinkedHashMap<String, PayorPayment>();
		    String[] filters = loadFilter();
		    for(int r = 1; r < rows; r++) {//start from row 1 instead of row 0 (row zero is the header information on excel)
		        row = sheet.getRow(r);
		        PayorPayment ro = new PayorPayment();
		        if(row != null) {
		            for(int c = 0; c < cols; c++) {
		                cell = row.getCell((short)c);
		                if(cell != null) {
		                    int fromQrt=1;
		                    int toQrt=1;
		                    int fromYear=0;
		                    int toYear=0;
		                    double principal=0d;
		                    double penalty=0d;
		                    double total=0d;
		                	switch(c) {
		                	case 0 : try{ro.setOrNumber(cell.getStringCellValue());}catch(Exception e) {ro.setOrNumber(cell.getNumericCellValue()+"");} break;
		                	case 1 : try{ro.setPaidDate(cell.getStringCellValue());}catch(Exception e) {ro.setPaidDate(cell.getNumericCellValue()+"");} break;
		                	case 2 : ro.setTdNo(cell.getStringCellValue());break;
		                	case 3 : try{fromQrt= Integer.parseInt(cell.getStringCellValue().replace(".0", ""));}catch(Exception e) {fromQrt= Integer.parseInt(cell.getNumericCellValue()+"");} ro.setFromQtr(fromQrt); break;
		                	case 4 : try{fromYear=Integer.valueOf(cell.getStringCellValue());}catch(Exception e) {fromYear=Integer.valueOf(cell.getNumericCellValue()+"");} ro.setFromYear(fromYear); break;
		                	case 5 : try{toQrt= Integer.parseInt(cell.getStringCellValue());}catch(Exception e) {toQrt= Integer.parseInt(cell.getNumericCellValue()+"");} ro.setToQtr(toQrt); break;
		                	case 6 : try{toYear=Integer.valueOf(cell.getStringCellValue());}catch(Exception e) {toYear=Integer.valueOf(cell.getNumericCellValue()+"");} ro.setToYear(toYear); break;
		                	case 7 : ro.setOwner(cell.getStringCellValue());break;
		                	case 8 : ro.setBarangay(cell.getStringCellValue());break;
		                	case 9 : ro.setKind(cell.getStringCellValue());break;
		                	case 10 : ro.setActualUse(cell.getStringCellValue());break;
		                	case 11 : try{ ro.setAssessedValue(cell.getNumericCellValue());}catch(Exception e) {}break;
		                	case 12 : try{principal=Numbers.formatDouble(cell.getStringCellValue());}catch(Exception e) {ro.setPrincipal(cell.getNumericCellValue());}  break;
		                	case 13 : try{penalty = Numbers.formatDouble(cell.getStringCellValue());}catch(Exception e) {ro.setPenalty(cell.getNumericCellValue());} break;
		                	case 14 : try{total = Numbers.formatDouble(cell.getStringCellValue());}catch(Exception e) {ro.setTotal(cell.getNumericCellValue());} break;
		                	case 15 : try{ro.setCollector(cell.getStringCellValue());}catch(Exception e) {ro.setCollector(cell.getNumericCellValue()+"");} break;
		                	}
		                	
		                }
		            }
		            String key = ro.getTdNo().strip()+ro.getFromYear()+ro.getToYear();
		            if(trackDuplicate!=null && trackDuplicate.containsKey(key)) {
		            	ro.setType("SEF");
		            }else {
		            	ro.setType("BASIC");
		            	trackDuplicate.put(key, ro);
		            }
		            
		            //pays.add(ro);
		            //String total = Currency.formatAmount(ro.getTotal());
		            //String asval = Currency.formatAmount(ro.getAssessedValue());
		            //ro.setAssessedValue(asval);
		            //ro.setTotal(total);
		            ro = splitOwnerLot(ro,filters); // add lot number if available
		            //payorMap.put(ro.getTdNo(), ro);
		            //System.out.println("row>> " + ro.getOwner());
		            pays.add(ro);
			    	holdpays.add(ro);
		        }
		        
		    }
		    
			/*
			 * for(PayorPayment pa : payorMap.values()) { pays.add(pa); holdpays.add(pa); }
			 */
		    
		    return pays;
		} catch(Exception ioe) {
		    ioe.printStackTrace();
		}
		return Collections.synchronizedList(new ArrayList<>());
	}
	
	public String[] loadFilter() {
		
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(DOC_FILTER));
			String[] fls = prop.getProperty("filter").split(",");
			
			String[] filters = new String[fls.length];
			int i=0;
			for(String f : fls) {
				filters[i++] = f;
			}
			return filters;
		}catch(Exception e) {}
		return null;
	}
	
	private PayorPayment splitOwnerLot(PayorPayment pay, String[] filters) {
		
		try {
			String[] val = new String[2];
			
			for(String fil : filters) {
				if(pay.getOwner().contains(fil)){
					val = pay.getOwner().split(fil);
					pay.setOwner(val[0].trim());
					try{
						pay.setLotNo(val[1]);}catch(IndexOutOfBoundsException ie) {
						pay.setLotNo(fil);
					}
				}	
			}
			
			
			String key = pay.getBarangay().toUpperCase();
			if(getBars().containsKey(key)) {
				pay.setAddress(key + ", LAKE SEBU, SO. COT.");
				pay.setBarangay(getBars().get(key).getId()+"");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return pay;
	}
	
	private Map<String, Barangay> loadBarangay() {
		if(bars!=null && bars.size()>0) {
			for(Barangay b : Barangay.retrieve("SELECT * FROM barangay", new String[0])) {
				bars.put(b.getName().toUpperCase(), b);
			}
		}
		return bars;
	}
	
	
	
	
	public void loadPayor() {
		lands = new ArrayList<LandPayor>();
		String[] params = new String[0];
		String sql = " AND p.payorname like '%"+ getSearchPayor() +"%' GROUP BY l.lotno";
		if(getSearchPayor()!=null && !getSearchPayor().isEmpty()) {
			lands = LandPayor.load(sql, params);
		}
		
	}
	
	public void selectedLand(LandPayor land) {
		
		selectedBasic = new ArrayList<Certification>();
		selectedSef = new ArrayList<Certification>();
		basicTax = new ArrayList<Certification>();
		sefTax = new ArrayList<Certification>();
		setSelectedLand(land);
		receipts = new ArrayList<TaxPayerReceipt>();
		setRequestor(land.getPayor().getFullName().toUpperCase());
		String sql = " AND rc.lotblockno='"+ land.getLotNo().trim()+"' AND ow.payorid=" + land.getPayor().getId() ;
		String[] params = new String[0];
		
		receipts = TaxPayerReceipt.retrieveReceipts(sql, params);
		TaxPayor payor = (TaxPayor)receipts.get(0).getPayor();
		int id=1;
		
		for(TaxPayerReceipt r : receipts) {
			
			String[] range = r.getInstallmentRangeAndType().split(":");
			String type = range[2];
			String yrFrom = range[0];
			String yrTo = range[1];
			String year = "";
			
			
			if(yrFrom.equalsIgnoreCase(yrTo)) {
				r.setInstallmentRangeAndType(yrFrom);
				year = yrFrom;
			}else{
				r.setInstallmentRangeAndType(yrFrom + "-" + yrTo);
				year = yrFrom + "-" + yrTo;
			}
			
			
			
			Certification cert = Certification.builder()
					.id(id+"")
					.tdNo(r.getTaxDecNo())
					.assessedValue(Currency.formatAmount(r.getAssValueLand()))
					.annualTax(Currency.formatAmount(r.getAssValueLand() * 0.01))
					.taxDue(Currency.formatAmount(r.getFullPayment()))
					.penalty(Currency.formatAmount(r.getPenaltyPercent()))
					.total(Currency.formatAmount(r.getOverallTotal()))
					.datePaid(r.getPayorTrans().getTransDate())
					.orNumber(r.getPayorTrans().getScNo())
					.yearPaid(year)
					.receipt(r)
					.yearFrom(yrFrom)
					.yearTo(yrTo)
					.build();
			
			
			if("BASIC".equalsIgnoreCase(type)) {
				cert.setMode("BASIC");
				basicTax.add(cert);
				getSelectedBasic().add(cert);
			}else {
				cert.setMode("SEF");
				sefTax.add(cert);
				getSelectedSef().add(cert);
			}
			
			id++;
		}
		
		contents="To whom it may concern:\n\n";
		contents +="\t\tThis is to certify that the name of " + payor.getFullName() + " a resident of Brgy. " + payor.getAddress() + " ";
		contents +="appeared in the Tax Register of this Municipality as the owner of Lot. No. " + land.getLotNo() + "\n";
		contents +="situated in Brgy. " + land.getAddress() + " bearing TD/FAAS no. " + land.getTaxDeclarionNo() + "\n";
		contents +="with and Assessed value of Php" + Currency.formatAmount(land.getLandValue()) + " and annual Tax of Php" + Currency.formatAmount((land.getLandValue() * 0.01)) + ".\n";
		
	}
	
	private List years() {
		List years = new ArrayList<>();
		for(int yr=1985; yr<=DateUtils.getCurrentYear(); yr++) {
			years.add(new SelectItem(yr, yr+""));
		}
		return years;
	}
	
	public void onCellEditBasic(CellEditEvent event) {
		try{
	        //Object oldValue = event.getOldValue();
	        //Object newValue = event.getNewValue();
	        
	        //System.out.println("old Value: " + oldValue);
	        //System.out.println("new Value: " + newValue);
	        
	        int index = event.getRowIndex();
	        Certification cert = getBasicTax().get(index);
	        
	        LandPayor land = getSelectedLand();
	        land.setLandValue(Numbers.formatDouble(cert.getAssessedValue()));
	        land = LandPayor.save(land);
	        
	        TaxPayerReceipt pay = cert.getReceipt();
	        	
	        pay.setInstallmentRangeAndType(cert.getYearFrom()+":"+cert.getYearTo()+":BASIC");
	        pay.setAssValueLand(land.getLandValue());
	        pay.setAssValueTotal(land.getLandValue());
	        pay.setTaxDue(Numbers.formatDouble(cert.getTaxDue()));
	        pay.setPenaltyPercent(Numbers.formatDouble(cert.getPenalty()));
	        //pay.setFullPayment(Numbers.formatDouble(cert.getTotal()));
	        pay.setOverallTotal(Numbers.formatDouble(cert.getTotal()));
	        pay.save();
	        
	        ITaxPayorTrans trans = cert.getReceipt().getPayorTrans();
	        trans.setScNo(cert.getOrNumber());
	        trans.setTransDate(cert.getDatePaid());
	        trans.save();
	        
	        selectedLand(land);
	        
		 }catch(Exception e){}  
	}
	
	public void onCellEditSEF(CellEditEvent event) {
		try{
	        //Object oldValue = event.getOldValue();
	        //Object newValue = event.getNewValue();
	        
	        //System.out.println("old Value: " + oldValue);
	        //System.out.println("new Value: " + newValue);
	        
	        int index = event.getRowIndex();
	        Certification cert = getSefTax().get(index);
	        
	        LandPayor land = getSelectedLand();
	        land.setLandValue(Numbers.formatDouble(cert.getAssessedValue()));
	        land = LandPayor.save(land);
	        
	        TaxPayerReceipt pay = cert.getReceipt();
	        	
	        pay.setInstallmentRangeAndType(cert.getYearFrom()+":"+cert.getYearTo()+":SEF");
	        pay.setAssValueLand(land.getLandValue());
	        pay.setAssValueTotal(land.getLandValue());
	        pay.setTaxDue(Numbers.formatDouble(cert.getTaxDue()));
	        pay.setPenaltyPercent(Numbers.formatDouble(cert.getPenalty()));
	        //pay.setFullPayment(Numbers.formatDouble(cert.getTotal()));
	        pay.setOverallTotal(Numbers.formatDouble(cert.getTotal()));
	        pay.save();
	        
	        ITaxPayorTrans trans = cert.getReceipt().getPayorTrans();
	        trans.setScNo(cert.getOrNumber());
	        trans.setTransDate(cert.getDatePaid());
	        trans.save();
	        
	        selectedLand(land);
	        
		 }catch(Exception e){}  
	}
	
	public void printReport(){
			
			collectLotInfoForPrinting();
		
			String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
					AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.CHEQUE_REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			String REPORT_NAME ="history";
			
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
			
			List<Certification> reports = new ArrayList<Certification>();
			Certification crt = Certification.builder()
					.id("")
					.tdNo("")
					.assessedValue("")
					.annualTax("")
					.taxDue("")
					.penalty("BASIC")
					.total("")
					.datePaid("")
					.orNumber("")
					.yearPaid("")
					.build();
			
			reports.add(crt);
			reports.addAll(getSelectedBasic());
			//reports.addAll(getBasicTax());
			crt = Certification.builder()
					.id("")
					.tdNo("")
					.assessedValue("")
					.annualTax("")
					.taxDue("")
					.penalty("")
					.total("")
					.datePaid("")
					.orNumber("")
					.yearPaid("")
					.build();
			reports.add(crt);
			
			crt = Certification.builder()
					.id("")
					.tdNo("")
					.assessedValue("")
					.annualTax("")
					.taxDue("")
					.penalty("SEF")
					.total("")
					.datePaid("")
					.orNumber("")
					.yearPaid("")
					.build();
			reports.add(crt);
			reports.addAll(getSelectedSef());
			
			//reports.addAll(getSefTax());
			
			
			JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
	  		HashMap param = new HashMap();
	  		
	  		param.put("PARAM_DATE", DateUtils.getCurrentDateMMMMDDYYYY());
	  		param.put("PARAM_CONTENT", getContents());
	  		
	  		String content2 = "\t\tThis certification is subject for verification at the Land Tax Division Provincial Treasurer's Office for the previous years payments.\n\n";
	  		content2 += "\t\tThis certification is being issued upon the request of "+ getRequestor().toUpperCase() +" for "+ getPurpose().toUpperCase() +" purposes only.";
	  		
	  		param.put("PARAM_CONTENT2", content2);
	  		
			param.put("PARAM_TREASURER", "FERDINAND L. LOPEZ");
	  		param.put("PARAM_CLERK", getPreparedBy().toUpperCase());
	  		
	  		
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
	
	public String getSearchPayor() {
		return searchPayor;
	}

	public void setSearchPayor(String searchPayor) {
		this.searchPayor = searchPayor;
	}

	public List<LandPayor> getLands() {
		return lands;
	}

	public void setLands(List<LandPayor> lands) {
		this.lands = lands;
	}

	public List<TaxPayerReceipt> getReceipts() {
		return receipts;
	}

	public void setReceipts(List<TaxPayerReceipt> receipts) {
		this.receipts = receipts;
	}

	public List<Certification> getBasicTax() {
		return basicTax;
	}

	public void setBasicTax(List<Certification> basicTax) {
		this.basicTax = basicTax;
	}

	public List<Certification> getSefTax() {
		return sefTax;
	}

	public void setSefTax(List<Certification> sefTax) {
		this.sefTax = sefTax;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public List<Certification> getSelectedBasic() {
		return selectedBasic;
	}

	public void setSelectedBasic(List<Certification> selectedBasic) {
		this.selectedBasic = selectedBasic;
	}

	public List<Certification> getSelectedSef() {
		return selectedSef;
	}

	public void setSelectedSef(List<Certification> selectedSef) {
		this.selectedSef = selectedSef;
	}

	public String getPurpose() {
		if(purpose==null || purpose.isEmpty()) {
			purpose = "TRANSFER OF ASSESSMENT";
		}
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getRequestor() {
		if(requestor==null || requestor.isEmpty()) {
			requestor = "LGU OF LAKE SEBU";
		}
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public LandPayor getSelectedLand() {
		return selectedLand;
	}

	public void setSelectedLand(LandPayor selectedLand) {
		this.selectedLand = selectedLand;
	}

	public List<PayorPayment> getPays() {
		return pays;
	}

	public void setPays(List<PayorPayment> pays) {
		this.pays = pays;
	}

	public List<PayorPayment> getHoldpays() {
		return holdpays;
	}

	public void setHoldpays(List<PayorPayment> holdpays) {
		this.holdpays = holdpays;
	}

	public String getSearchParama() {
		return searchParama;
	}

	public void setSearchParama(String searchParama) {
		this.searchParama = searchParama;
	}

	public Map<String, Barangay> getBars() {
		return bars;
	}

	public void setBars(Map<String, Barangay> bars) {
		this.bars = bars;
	}

	public List<PayorPayment> getPayments() {
		return payments;
	}

	public void setPayments(List<PayorPayment> payments) {
		this.payments = payments;
	}

	public String getSearchHistory() {
		return searchHistory;
	}

	public void setSearchHistory(String searchHistory) {
		this.searchHistory = searchHistory;
	}

	public String getLandAddress() {
		return landAddress;
	}

	public void setLandAddress(String landAddress) {
		this.landAddress = landAddress;
	}

	public String getLotNo() {
		return lotNo;
	}

	public void setLotNo(String lotNo) {
		this.lotNo = lotNo;
	}

	public String getPreparedBy() {
		return preparedBy;
	}

	public void setPreparedBy(String preparedBy) {
		this.preparedBy = preparedBy;
	}
	
}
