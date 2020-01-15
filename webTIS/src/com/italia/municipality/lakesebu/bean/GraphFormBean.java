package com.italia.municipality.lakesebu.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.optionconfig.tooltip.Tooltip;

import com.italia.municipality.lakesebu.controller.CollectionInfo;
import com.italia.municipality.lakesebu.controller.Collector;
import com.italia.municipality.lakesebu.controller.CollectorCollections;
import com.italia.municipality.lakesebu.controller.RcdInfo;
import com.italia.municipality.lakesebu.controller.ReadDashboardInfo;
import com.italia.municipality.lakesebu.controller.SummaryCollections;
import com.italia.municipality.lakesebu.controller.SummaryCollectionsCollector;
import com.italia.municipality.lakesebu.enm.FormType;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.utils.Numbers;

/**
 * 
 * @author Mark Italia
 * @since 03/20/2019
 * @version 1.0
 *
 *
 */

@ManagedBean(name="grapFormBean", eager=true)
@ViewScoped
public class GraphFormBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1445670876346L;
	
	private LineChartModel lineModel;
	private HorizontalBarChartModel hbarModel;
	private BarChartModel stackedGroupBarModel;
	
	private Map<String, Double> lastData;
	private Map<String, Double> currentData;
	
	private  String DOC_PATH = System.getenv("SystemDrive") + File.separator + "webtis" + File.separator + "upload" + File.separator;
			
	private HorizontalBarChartModel hbarModelCollector;
	
	private List collectors;
	private int collectorId;
	private List years;
	private int yearId;
	
	private List collectorsPerYear;
	private int collectorPerYearId;
	
	//temp
	private Date fromDate;
	private Date toDate;

	private List<SummaryCollections> summData = Collections.synchronizedList(new ArrayList<SummaryCollections>());
	private List<SummaryCollectionsCollector> summColletorData = Collections.synchronizedList(new ArrayList<SummaryCollectionsCollector>());
	private List<CollectorCollections> perColletorForm = Collections.synchronizedList(new ArrayList<CollectorCollections>());
	
	private List<CollectorCollections> collectorData = Collections.synchronizedList(new ArrayList<CollectorCollections>());
	
	private int monthId;
	private List months;
	
	private SummaryCollections editDataSummaryCollections;
	private SummaryCollectionsCollector editDataSummaryCollectionsCollector;
	private CollectorCollections editDataSummaryCollectorCollections;
	
	private double year;
	private double January;
	private double February;
	private double March;
	private double April;
	private double May;
	private double June;
	private double July;
	private double August;
	private double September;
	private double October;
	private double November;
	private double December;
	
	private double year1;
	private double January1;
	private double February1;
	private double March1;
	private double April1;
	private double May1;
	private double June1;
	private double July1;
	private double August1;
	private double September1;
	private double October1;
	private double November1;
	private double December1;
	private String totalCollectionPerYer;
	
	private String totalCollection;
	
	private int formTypeId;
	private List forms;
	private Date dateTrans;
	private double amountReceived;
	
	private int collectorAddId;
	private List collectorAdd;
	
	private List collectorsPer;
	private int collectorIdPer;
	private List yearsPer;
	private int yearIdPer;
	private List monthsPer;
	private int monthIdPer;
	
	private boolean withCompareCollectorOtherCollection;
	
	@PostConstruct
	public void init() {
		loadData();
		createLineModel();
		createHorizontalBarModel(ReadDashboardInfo.getInfo("forms-this-year"));
		createStackedGroupBarModel();
		createHorizontalBarModelCollector(ReadDashboardInfo.getInfo("collector-data"));
	}
	
	public void selectedCollectorPer() {
		
		System.out.println("year: " + getYearIdPer() + " collector " + getCollectorIdPer());
		
		Map<String, Double> data = Collections.synchronizedMap(new HashMap<String, Double>());
		
		String[] params = new String[1];
		String sql = "";
		
		if(getCollectorIdPer()!=0){
			
			params = new String[2];
			sql = " AND col.colyear=?";
			params[0] = getYearIdPer()+"";
			sql += " AND iss.isid=?";
			params[1] = getCollectorIdPer()+"";
			
		}else {
			
			params = new String[1];
			sql = " AND col.colyear=?";
			params[0] = getYearIdPer()+"";
			
		}
		
		summColletorData = Collections.synchronizedList(new ArrayList<SummaryCollectionsCollector>());
		for(SummaryCollectionsCollector i : SummaryCollectionsCollector.retrive(sql, params)) {
			
			try{double amount = 0d;
			SummaryCollectionsCollector filter = new SummaryCollectionsCollector();
			if(getMonthIdPer()==0) {
				amount = i.getJanuary() + i.getFebruary() + i.getMarch() + i.getApril() + i.getMay() + i.getJune() + i.getJuly() + i.getAugust() + i.getSeptember() + i.getOctober() + i.getNovember() + i.getDecember();
				filter = i;
			}else {
				
				filter.setId(i.getId());
				filter.setCollector(i.getCollector());
				filter.setIsActive(1);
				filter.setYear(i.getYear());
				
				switch(getMonthIdPer()) {
					case 1 : amount = i.getJanuary(); filter.setJanuary(i.getJanuary()); break;
					case 2 : amount = i.getFebruary(); filter.setFebruary(i.getFebruary()); break;
					case 3 : amount = i.getMarch(); filter.setMarch(i.getMarch()); break;
					case 4 : amount = i.getApril(); filter.setApril(i.getApril()); break;
					case 5 : amount = i.getMay(); filter.setMay(i.getMay()); break;
					case 6 : amount = i.getJune(); filter.setJune(i.getJune()); break;
					case 7 : amount = i.getJuly(); filter.setJuly(i.getJuly()); break;
					case 8 : amount = i.getAugust(); filter.setAugust(i.getAugust()); break;
					case 9 : amount = i.getSeptember(); filter.setSeptember(i.getSeptember()); break;
					case 10 : amount = i.getOctober(); filter.setOctober(i.getOctober()); break;
					case 11 : amount = i.getNovember(); filter.setNovember(i.getNovember()); break;
					case 12 : amount = i.getDecember(); filter.setDecember(i.getDecember()); break;
				}
			}
			
			
			data.put(i.getCollector().getName(), amount);
			
			if(amount>0) {
				summColletorData.add(filter);
			}
			
			}catch(Exception e) {e.printStackTrace();}
			
			
		}
		
		createHorizontalBarModelCollector(data);
		
	}
	
	public void deleteRow(Object obj) {
		
		if(obj instanceof SummaryCollections) {//first yearly summary
			
			SummaryCollections cc = (SummaryCollections)obj;
			
		}else if(obj instanceof SummaryCollectionsCollector) {//collector summary
			
			SummaryCollectionsCollector sc = (SummaryCollectionsCollector)obj;
			
		}else if(obj instanceof CollectorCollections) {//collector per report
			
			CollectorCollections cc = (CollectorCollections)obj;
			
		}
		
	}
	
	public void clickItem(Object obj) {
		try {
		System.out.println("object " + obj.toString());
		if(obj instanceof SummaryCollections) {//first yearly summary
			
			SummaryCollections cc = (SummaryCollections)obj;
			System.out.println("year:" + cc.getYear());
			setEditDataSummaryCollections(cc);
			setYear1(cc.getYear());
			setJanuary1(cc.getJanuary());
			setFebruary1(cc.getFebruary());
			setMarch1(cc.getMarch());
			setApril1(cc.getApril());
			setMay1(cc.getMay());
			setJune1(cc.getJune());
			setJuly1(cc.getJuly());
			setAugust1(cc.getAugust());
			setSeptember1(cc.getSeptember());
			setOctober1(cc.getOctober());
			setNovember1(cc.getNovember());
			setDecember1(cc.getDecember());
		}else if(obj instanceof SummaryCollectionsCollector) {//collector summary
			
			SummaryCollectionsCollector cc = (SummaryCollectionsCollector)obj;
			setEditDataSummaryCollectionsCollector(cc);
			setCollectorPerYearId(cc.getCollector().getId());
			setYear(cc.getYear());
			setJanuary(cc.getJanuary());
			setFebruary(cc.getFebruary());
			setMarch(cc.getMarch());
			setApril(cc.getApril());
			setMay(cc.getMay());
			setJune(cc.getJune());
			setJuly(cc.getJuly());
			setAugust(cc.getAugust());
			setSeptember(cc.getSeptember());
			setOctober(cc.getOctober());
			setNovember(cc.getNovember());
			setDecember(cc.getDecember());
		}else if(obj instanceof CollectorCollections) {//collector per report
			
			CollectorCollections cc = (CollectorCollections)obj;
			setEditDataSummaryCollectorCollections(cc);
			
			setCollectorAddId(cc.getCollector().getId());
			setFormTypeId(cc.getFormType());
			setDateTrans(DateUtils.convertDateString(cc.getDateTrans(), "yyyy-MM-dd"));
			setAmountReceived(cc.getAmount());
			
		}
		}catch(Exception e) {e.printStackTrace();}
	}
	
	public void createNew() {
		setEditDataSummaryCollections(null);
		setEditDataSummaryCollectionsCollector(null);
		setEditDataSummaryCollectorCollections(null);
		setYear(0);
		setJanuary(0);
		setFebruary(0);
		setMarch(0);
		setApril(0);
		setMay(0);
		setJune(0);
		setJuly(0);
		setAugust(0);
		setSeptember(0);
		setOctober(0);
		setNovember(0);
		setDecember(0);
		
		setYear1(0);
		setJanuary1(0);
		setFebruary1(0);
		setMarch1(0);
		setApril1(0);
		setMay1(0);
		setJune1(0);
		setJuly1(0);
		setAugust1(0);
		setSeptember1(0);
		setOctober1(0);
		setNovember1(0);
		setDecember1(0);
		
		setDateTrans(null);
		setAmountReceived(0.00);
	}
	
	public void saveDataSumYear() {
		
		SummaryCollections sum = new SummaryCollections();
		
		if(getEditDataSummaryCollections()!=null) {
			sum = getEditDataSummaryCollections();
		}else {
			sum.setIsActive(1);
		}
		
		System.out.println("May:" + getMay1());
		
		sum.setYear(getYear1());
		
		if(getJanuary1()>0) {
			sum.setJanuary(getJanuary1());
		}
		if(getFebruary1()>0) {
			sum.setFebruary(getFebruary1());
		}
		if(getMarch1()>0) {
			sum.setMarch(getMarch1());
		}
		if(getApril1()>0) {
			sum.setApril(getApril1());
		}
		if(getMay1()>0) {
			sum.setMay(getMay1());
		}
		if(getJune1()>0) {
			sum.setJune(getJune1());
		}
		if(getJuly1()>0) {
			sum.setJuly(getJuly1());
		}
		if(getAugust1()>0) {
			sum.setAugust(getAugust1());
		}
		if(getSeptember1()>0) {
			sum.setSeptember(getSeptember1());
		}
		if(getOctober1()>0) {
			sum.setOctober(getOctober1());
		}
		if(getNovember1()>0) {
			sum.setNovember(getNovember1());
		}
		if(getDecember1()>0) {
			sum.setDecember(getDecember1());
		}
		sum = SummaryCollections.save(sum);
		
		Application.addMessage(1, "Success", "Successfully saved.");
		
		loadData();
		createLineModel();
		createStackedGroupBarModel();
		createNew();
	}
	
	public void saveDataSumCollectorPerYear() {
		
		SummaryCollectionsCollector sum = new SummaryCollectionsCollector();
		
		if(getEditDataSummaryCollectionsCollector()!=null) {
			sum = getEditDataSummaryCollectionsCollector();
		}else {
			sum.setIsActive(1);
		}
		
		Collector coll = new Collector();
		coll.setId(getCollectorPerYearId());
		sum.setCollector(coll);
		
		sum.setYear(getYear());
		if(getJanuary()>0) {
			sum.setJanuary(getJanuary());
		}
		if(getFebruary()>0) {
			sum.setFebruary(getFebruary());
		}
		if(getMarch()>0) {
			sum.setMarch(getMarch());
		}
		if(getApril()>0) {
			sum.setApril(getApril());
		}
		if(getMay()>0) {
			sum.setMay(getMay());
		}
		if(getJune()>0) {
			sum.setJune(getJune());
		}
		if(getJuly()>0) {
			sum.setJuly(getJuly());
		}
		if(getAugust()>0) {
			sum.setAugust(getAugust());
		}
		if(getSeptember()>0) {
			sum.setSeptember(getSeptember());
		}
		if(getOctober()>0) {
			sum.setOctober(getOctober());
		}
		if(getNovember()>0) {
			sum.setNovember(getNovember());
		}
		if(getDecember()>0) {
			sum.setDecember(getDecember());
		}
		sum = SummaryCollectionsCollector.save(sum);
		
		Application.addMessage(1, "Success", "Successfully saved.");

		
		loadPerCollectorInfo();
		createNew();
	}
	
	private void loadPerCollectorInfo() {
		Map<String, Double> data = Collections.synchronizedMap(new HashMap<String, Double>());
		String[] params = new String[1];
		String sql = " AND col.colyear=?";
		params[0] = DateUtils.getCurrentYear()+"";
		summColletorData = Collections.synchronizedList(new ArrayList<SummaryCollectionsCollector>());
		double amountTotal = 0d;
		for(SummaryCollectionsCollector i : SummaryCollectionsCollector.retrive(sql, params)) {
			double amount = 0d;
			try{amount = i.getJanuary() + i.getFebruary() + i.getMarch() + i.getApril() + i.getMay() + i.getJune() + i.getJuly() + i.getAugust() + i.getSeptember() + i.getOctober() + i.getNovember() + i.getDecember();
			System.out.println("Amount collector>>> " + amount);
			data.put(i.getCollector().getName(), amount);}catch(Exception e) {e.printStackTrace();}
			
			amountTotal += amount;
			summColletorData.add(i);
		}
		setTotalCollectionPerYer(Currency.formatAmount(amountTotal));
		createHorizontalBarModelCollector(data);
	}
	
	public void saveDataPerCollector() {
		
		CollectorCollections cc = new CollectorCollections();
		
		if(getEditDataSummaryCollectorCollections()!=null) {
			cc = getEditDataSummaryCollectorCollections();
		}else {
			cc.setIsActive(1);
		}
		
		Collector coll = new Collector();
		coll.setId(getCollectorAddId());
		cc.setCollector(coll);
		cc.setFormType(getFormTypeId());
		cc.setDateTrans(DateUtils.convertDate(getDateTrans(), "yyyy-MM-dd"));
		cc.setAmount(getAmountReceived());
		
		cc = CollectorCollections.save(cc);
		
		String[] date = cc.getDateTrans().split("-");
		int yer = Integer.valueOf(date[0]);
		int mont = Integer.valueOf(date[1]);
		
		String sql = " AND col.isid=? AND col.colyear=?";
		String[] params = new String[2];
		params[0] = cc.getCollector().getId()+"";
		params[1] = yer+"";
		List<SummaryCollectionsCollector> cols = SummaryCollectionsCollector.retrive(sql, params);
		SummaryCollectionsCollector col = new  SummaryCollectionsCollector();
		if(cols!=null && cols.size()>0) {
			col = cols.get(0);
		}else {
			col.setIsActive(1);
		}
		
		double amount = getAmountReceived();
		
		switch(mont) {
		case 1 : amount += col.getJanuary();  col.setJanuary(amount); col.save(); break;
		case 2 : amount += col.getFebruary();  col.setFebruary(amount); col.save(); break;
		case 3 : amount += col.getMarch(); col.setMarch(amount); col.save(); break;
		case 4 : amount += col.getApril(); col.setApril(amount); col.save(); break;
		case 5 : amount += col.getMay(); col.setMay(amount);  col.save(); break;
		case 6 : amount += col.getJune(); col.setJune(amount); col.save(); break;
		case 7 : amount += col.getJuly(); col.setJuly(amount); col.save(); break;
		case 8 : amount += col.getAugust(); col.setAugust(amount); col.save(); break;
		case 9 : amount += col.getSeptember(); col.setSeptember(amount);  col.save(); break;
		case 10 : amount += col.getOctober(); col.setOctober(amount); col.save(); break;
		case 11 : amount += col.getNovember(); col.setNovember(amount);  col.save(); break;
		case 12 : amount += col.getDecember(); col.setDecember(amount); col.save(); break;
		}
		
		sql = " AND sumyear=?";
		params = new String[1];
		params[0] = yer+"";
		List<SummaryCollections> sums = SummaryCollections.retrive(sql, params);
		SummaryCollections sum = new  SummaryCollections();
		if(sums!=null && sums.size()>0) {
			sum = sums.get(0);
		}else {
			sum.setIsActive(1);
		}
		
		amount = getAmountReceived();
		
		switch(mont) {
		case 1 : amount += sum.getJanuary();  sum.setJanuary(amount); sum.save(); break;
		case 2 : amount += sum.getFebruary();  sum.setFebruary(amount); sum.save(); break;
		case 3 : amount += sum.getMarch(); sum.setMarch(amount); sum.save(); break;
		case 4 : amount += sum.getApril(); sum.setApril(amount); sum.save(); break;
		case 5 : amount += sum.getMay(); sum.setMay(amount);  sum.save(); break;
		case 6 : amount += sum.getJune(); sum.setJune(amount); sum.save(); break;
		case 7 : amount += sum.getJuly(); sum.setJuly(amount); sum.save(); break;
		case 8 : amount += sum.getAugust(); sum.setAugust(amount); sum.save(); break;
		case 9 : amount += sum.getSeptember(); sum.setSeptember(amount);  sum.save(); break;
		case 10 : amount += sum.getOctober(); sum.setOctober(amount); sum.save(); break;
		case 11 : amount += sum.getNovember(); sum.setNovember(amount);  sum.save(); break;
		case 12 : amount += sum.getDecember(); sum.setDecember(amount); sum.save(); break;
		}
		
		setCollectorId(getCollectorAddId());
		setYearId(yer);
		setMonthId(mont);
		selectedCollector();
	}
	
	public void graphButton(String type, String closeOpen) {
		
		PrimeFaces current = PrimeFaces.current();
		String script = "";
		
		if("line-summary".equalsIgnoreCase(type)) {
			
			if("open".equalsIgnoreCase(closeOpen)) {
				loadData();
				createLineModel();
				createStackedGroupBarModel();
				script = "$(\"#idCollector\").hide(1000);";
				script += "$(\"#idSummaryFormCollector\").hide(1000);";
				
				script += "$(\"#idSummaryLine\").show(1000);";
			}else {
				script = "$(\"#idSummaryLine\").hide(1000);";
			}
			
		}else if("per-collector-summary".equalsIgnoreCase(type)) {
			
			if("open".equalsIgnoreCase(closeOpen)) {
				
				script = "$(\"#idCollector\").hide(1000);";
				script += "$(\"#idSummaryLine\").hide(1000);";
				script += "$(\"#idSummaryFormCollector\").show(1000);";
				
				loadPerCollectorInfo();
				
			}else {
				script = "$(\"#idSummaryFormCollector\").hide(1000);";
			}
			
		}else if("collector-collection".equalsIgnoreCase(type)) {
			
			if("open".equalsIgnoreCase(closeOpen)) {
				
				script = "$(\"#idSummaryLine\").hide(1000);";
				script += "$(\"#idSummaryFormCollector\").hide(1000);";
				
				script += "$(\"#idCollector\").show(1000);";
				selectedCollector();
			}else {
				script = "$(\"#idCollector\").hide(1000);";
			}
			
		}
		System.out.println("script: " + script);
		current.executeScript(script);
	}
	
	public void uploadFile(FileUploadEvent event) {
		String fileName = event.getFile().getFileName();
		System.out.println("Checking uploaded file : " + fileName);
		boolean isValid = false;
			switch(fileName) {
				case "RCD-UPLOAD-FILE-SUMMARY.xls" : isValid = true; break;
				case "CHARLOTTE_LUCERO.xls" : isValid = true; break;
				case "ELLENA_FRONDOSO.xls" : isValid = true; break;
				case "EVELYN_BOLIVAR.xls" : isValid = true; break;
				case "GARCI_ALAM.xls" : isValid = true; break;
				case "HENRY_MAGBANUA.xls" : isValid = true; break;
				case "JESSIE_HANDATU" : isValid = true; break;
				case "JOCELYN_DAMOLE.xls" : isValid = true; break;
				case "JOEL_LUGAN.xls" : isValid = true; break;
				case "JOSE_LEVY_WEAVER.xls" : isValid = true; break;
				case "JUANITO_UMEK.xls" : isValid = true; break;
				case "MAILYN_BINUEZA.xls" : isValid = true; break;
				case "MAMERTA_BAUTISTA.xls" : isValid = true; break;
				case "MELVER_VERGARA.xls" : isValid = true; break;
				case "RANNY_FIKAN.xls" : isValid = true; break;
				case "RESELLETA_VALERA.xls" : isValid = true; break;
				case "RICKY_LAHAW.xls" : isValid = true; break;
			}
			
		if(isValid) {
			if("RCD-UPLOAD-FILE-SUMMARY.xls".equalsIgnoreCase(fileName)) {
				uploadData(event);
			}else {
				uploadDataCollector(event);
			}
		}else {
			Application.addMessage(3, "Invalid", "The uploaded file is invalid");
		}
		
	}
	
	public void loadData() {
		String sql = " AND (sumyear>=? AND sumyear<=?)";
		String[] params = new String[2];
		
		int yearNow = DateUtils.getCurrentYear();
		int yearLast = yearNow - 1; 
		lastData = Collections.synchronizedMap(new HashMap<String, Double>());
		currentData = Collections.synchronizedMap(new HashMap<String, Double>());
		
		//supply temp data
		for(int month=1; month<=12; month++) {
			lastData.put(month+"", 0.0);
			currentData.put(month+"", 0.0);
		}
		
		params[0] = yearLast+"";
		params[1] = yearNow+"";
		
		//for(CollectionInfo info : CollectionInfo.retrieve(sql, params)) {
			//String day = info.getReceivedDate().split("-")[1];
		/**
		 * int key = Integer.valueOf(day);
			if(currentData!=null && currentData.containsKey(key+"")) {
				double amount = currentData.get(key+"");
				amount += info.getAmount();
				currentData.put(key+"", amount);
			}else {
				currentData.put(key+"", info.getAmount());
			}
		 */
		summData = Collections.synchronizedList(new ArrayList<SummaryCollections>());
		for(SummaryCollections i : SummaryCollections.retrive(sql, params)) {
			
			summData.add(i);
			
			if(yearLast==i.getYear()) {
				lastData.put("1", i.getJanuary());
				lastData.put("2", i.getFebruary());
				lastData.put("3", i.getMarch());
				lastData.put("4", i.getApril());
				lastData.put("5", i.getMay());
				lastData.put("6", i.getJune());
				lastData.put("7", i.getJuly());
				lastData.put("8", i.getAugust());
				lastData.put("9", i.getSeptember());
				lastData.put("10", i.getOctober());
				lastData.put("11", i.getNovember());
				lastData.put("12", i.getDecember());
			
		}else if(yearNow==i.getYear()) {
			
				currentData.put("1", i.getJanuary());
				currentData.put("2", i.getFebruary());
				currentData.put("3", i.getMarch());
				currentData.put("4", i.getApril());
				currentData.put("5", i.getMay());
				currentData.put("6", i.getJune());
				currentData.put("7", i.getJuly());
				currentData.put("8", i.getAugust());
				currentData.put("9", i.getSeptember());
				currentData.put("10", i.getOctober());
				currentData.put("11", i.getNovember());
				currentData.put("12", i.getDecember());
		}	
		}
		/*year -=1;
		
		params[0] = year + "-01-01";
		params[1] = year + "-12-31";
		for(CollectionInfo info : CollectionInfo.retrieve(sql, params)) {
			String day = info.getReceivedDate().split("-")[1];
			int key = Integer.valueOf(day);
			
			if(lastData!=null && lastData.containsKey(key+"")) {
				double amount = currentData.get(key+"");
				amount += info.getAmount();
				lastData.put(key+"", amount);
			}else {
				lastData.put(key+"", info.getAmount());
			}
			
		}*/
		
		
		
	}
	
	
	private String[] rgbColors() {
    	String[] rgb = {"rgb(255, 99, 132)","rgb(250, 53, 95)","rgb(183, 83, 104)","rgb(123, 70, 82)","rgb(99, 3, 23)",
				"rgb(54, 162, 235)","rgb(14, 122, 195)","rgb(8, 73, 116)","rgb(88, 109, 124)","rgb(195, 227, 249)",
				"rgb(255, 205, 86)","rgb(228, 165, 14)","rgb(169, 144, 84)","rgb(244, 227, 187)","rgb(180, 177, 170)",
				"rgb(116, 243, 143)","rgb(117, 165, 127)","rgb(9, 48, 17)","rgb(123, 118, 240)","rgb(14, 2, 249)",
				"rgb(138, 132, 246)","rgb(67, 62, 171)","rgb(46, 238, 200)","rgb(21, 190, 156)","rgb(6, 147, 119)",
				"rgb(4, 70, 57)","rgb(26, 75, 65)","rgb(121, 172, 162)","rgb(204, 248, 239)","rgb(234, 204, 248)",
				"rgb(219, 158, 247)","rgb(203, 111, 246)","rgb(188, 58, 248)","rgb(167, 6, 242)","rgb(128, 20, 178)",
				"rgb(178, 20, 143)","rgb(190, 55, 160)","rgb(217, 106, 192)","rgb(239, 156, 220)","rgb(247, 218, 241)",
				"rgb(253, 247, 233)","rgb(248, 223, 169)","rgb(234, 191, 100)","rgb(207, 152, 32)","rgb(144, 120, 69)",
				"rgb(156, 142, 113)","rgb(141, 137, 130)","rgb(175, 213, 205)","rgb(110, 181, 167)","rgb(57, 143, 126)"
				};
    	
    	return rgb;
    }
	
	public void createLineModel() {
		System.out.println("create line model");
		lineModel = new LineChartModel();
        ChartData data = new ChartData();
        
        
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Number> values = new ArrayList<>();
        
        LineChartDataSet dataSet2 = new LineChartDataSet();
        List<Number> values2 = new ArrayList<>();
         
        List<String> labels = new ArrayList<>();
        
        /*Map<String, Double> last = ReadDashboardInfo.getInfo("collection-last-year");
        Map<String, Double> current = ReadDashboardInfo.getInfo("collection-this-year");*/
        
        Map<String, Double> last = getLastData();
        Map<String, Double> current = getCurrentData();
        
        int sizeLast = last.size();
        int sizeCurrent = current.size();
        int loopHisghest = 12;
        
        if(sizeLast > sizeCurrent) {
        	loopHisghest = sizeLast;
        }else if(sizeCurrent > sizeLast) {
        	loopHisghest = sizeCurrent;
        }	
        
        for(int key=1;key<=loopHisghest;key++) {
        	labels.add(DateUtils.getMonthName(key));
        	
        	if(last.containsKey(key+"")) {
        		values.add(last.get(key+""));
        	}else {
        		values.add(0);
        	}
        	
        	if(current.containsKey(key+"")) {
        		values2.add(current.get(key+""));
        	}else {
        		values2.add(0);
        	}
        	
        }
        
        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel("Last Year");
        dataSet.setBorderColor("rgb(255, 99, 132)");
        dataSet.setLineTension(0.1);
        data.addChartDataSet(dataSet);
        
        dataSet2.setData(values2);
        dataSet2.setFill(false);
        dataSet2.setLabel("Current Year");
        dataSet2.setBorderColor("rgb(75, 192, 192)");
        dataSet2.setLineTension(0.1);
        data.addChartDataSet(dataSet2);
        
        data.setLabels(labels);
         
        //Options
        LineChartOptions options = new LineChartOptions();        
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Collection for the Current Year");
        options.setTitle(title);
         
        lineModel.setOptions(options);
        lineModel.setData(data);
	}
	
	public void selectedCollector() {
		
		String sql = " AND (col.ccdate>=? AND col.ccdate<=?) ";
		String[] params = new String[2];
		
		if(getCollectorId()!=0) {
			params = new String[3];
		}
		
		if(getMonthId()==0) {
			params[0] = getYearId() +"-01-01";
			params[1] = getYearId() +"-12-31";
		}else {
			String month = getMonthId()<10? "0"+getMonthId() : ""+getMonthId();
			params[0] = getYearId() +"-"+month+"-01";
			params[1] = getYearId() +"-"+month+"-31";
		}
		
		if(getCollectorId()!=0) {
			sql +=" AND iss.isid=?";
			params[2] = getCollectorId()+"";
		}
		
		
		Map<String, Double> current = Collections.synchronizedMap(new HashMap<String, Double>());
		Map<Integer, CollectorCollections> colMap = Collections.synchronizedMap(new HashMap<Integer,CollectorCollections>());
		collectorData = Collections.synchronizedList(new ArrayList<CollectorCollections>());
		
		for(CollectorCollections col : CollectorCollections.retrive(sql, params)) {
			col.setFormName(FormType.nameId(col.getFormType()));
			collectorData.add(col);
		}
		
		for(CollectorCollections col : CollectorCollections.retrive(sql, params)) {
			
			int key = col.getFormType();
			if(colMap!=null && colMap.containsKey(key)) {
				double amount = col.getAmount();
				amount += colMap.get(key).getAmount();
				col.setAmount(amount);
				colMap.put(key, col);
			}else {
				colMap.put(key, col);
			}
			
		}
		
		
		double amount = 0d;
		for(CollectorCollections col : colMap.values()) {
			current.put(FormType.nameId(col.getFormType()), col.getAmount());
			col.setFormName(FormType.nameId(col.getFormType()));
			amount += col.getAmount();
		}
		setTotalCollection(Currency.formatAmount(amount));
		createHorizontalBarModel(current);
	}
	
	public void createHorizontalBarModel(Map<String, Double> current) {
        hbarModel = new HorizontalBarChartModel();
        ChartData data = new ChartData();
         
        HorizontalBarChartDataSet hbarDataSet = new HorizontalBarChartDataSet();
        HorizontalBarChartDataSet hbarDataSet2 = new HorizontalBarChartDataSet();
        
        if(getCollectorId()==0) {
        	hbarDataSet.setLabel("Example Only("+ DateUtils.getCurrentYear() +")");
        	int prevYear = DateUtils.getCurrentYear() - 1;
        	hbarDataSet2.setLabel("Example Only("+ prevYear +")");
        }else {
        	//Collector collector = Collector.retrieve(getCollectorId());
        	String monthName = getMonthId()>0? " ("+DateUtils.getMonthName(getMonthId())+") " + getYearId() : "";
        	if(getMonthId()==0) {
        		monthName = "All months";
        	}
        	
        	try{hbarDataSet.setLabel(monthName);}catch(Exception e) {hbarDataSet.setLabel("Error on loading. Something went wrong Or the selected Collector has not yet recorded");}
        	
        }
         
        List<Number> values = new ArrayList<>();
        List<String> bgColor = new ArrayList<>(); 
        List<String> borderColor = new ArrayList<>();
         
        List<String> labels = new ArrayList<>();
        
        /*List<Number> values2 = new ArrayList<>();
        List<String> bgColor2 = new ArrayList<>(); 
        List<String> borderColor2 = new ArrayList<>();
         
        List<String> labels2 = new ArrayList<>();*/
        
        int rgbColor = 0;
        String[] rgbs = rgbColors();
        for(String key : current.keySet()) {
        	double number = current.get(key); 
        	if(number>0) {
	        	values.add(number);
	        	bgColor.add(rgbs[rgbColor++]);
	        	borderColor.add(rgbs[rgbColor]);
	        	labels.add(key + "("+ Currency.formatAmount(number) +")");
	        	
	        	/*values2.add(number);
	        	bgColor2.add(rgbs[rgbColor++]);
	        	borderColor2.add(rgbs[rgbColor]);
	        	labels2.add(key + "("+ Currency.formatAmount(number) +")");*/
        	}
        }
        hbarDataSet.setData(values);
        hbarDataSet.setBackgroundColor(bgColor);
        data.addChartDataSet(hbarDataSet);
        hbarDataSet.setBorderColor(borderColor);
        hbarDataSet.setBorderWidth(1);
        data.setLabels(labels);
        hbarModel.setData(data);
        
        /*hbarDataSet2.setData(values2);
        hbarDataSet2.setBackgroundColor(bgColor2);
        data.addChartDataSet(hbarDataSet2);
        hbarDataSet2.setBorderColor(borderColor2);
        hbarDataSet2.setBorderWidth(1);
        data.setLabels(labels2);*/
        
        hbarModel.setData(data);
         
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addXAxesData(linearAxes);
        options.setScales(cScales);
         
        Title title = new Title();
        title.setDisplay(true);
        if(getCollectorId()==0) {
        	title.setText("Example Only - Please select collector");
        }else {
        	
        	Collector collector = Collector.retrieve(getCollectorId());
        	String monthName = getMonthId()>0? " ("+DateUtils.getMonthName(getMonthId())+") " + getYearId() : "";
        	String name = "";
        	try{name = collector.getName().toUpperCase() + monthName;}catch(Exception e) {}
        	
        	title.setText("Form Collection Recorded ("+ name + ")");
        }
        options.setTitle(title);
         
        hbarModel.setOptions(options);
    }
	
	public void createHorizontalBarModelCollector(Map<String, Double> current) {
		hbarModelCollector = new HorizontalBarChartModel();
        ChartData data = new ChartData();
         
        HorizontalBarChartDataSet hbarDataSet = new HorizontalBarChartDataSet();
        int year = getYearIdPer(); 
        String titleCap = "";
        
        if(getMonthIdPer()>0) {
        	titleCap = DateUtils.getMonthName(getMonthIdPer());
        }else if(getMonthIdPer()==0) {
        		if(DateUtils.getCurrentYear()==getYearIdPer()) {
        			if(DateUtils.getCurrentMonth()>1) {
        				titleCap = "January to " + DateUtils.getMonthName(DateUtils.getCurrentMonth());
        			}else {
        				titleCap = "January";
        			}
        		}else {
        			titleCap = "January to December";
        		}
        }
        
        hbarDataSet.setLabel(titleCap);
         
        List<Number> values = new ArrayList<>();
        List<String> bgColor = new ArrayList<>(); 
        List<String> borderColor = new ArrayList<>();
         
        List<String> labels = new ArrayList<>();
        
        
        
        int rgbColor = 0;
        String[] rgbs = rgbColors();
        for(String key : current.keySet()) {
        	double number = current.get(key); 
        	if(number>0) {
	        	values.add(number);
	        	bgColor.add(rgbs[rgbColor++]);
	        	borderColor.add(rgbs[rgbColor]);
	        	labels.add(key + "("+ Currency.formatAmount(number) +")");
        	}
        }
        hbarDataSet.setData(values);
        hbarDataSet.setBackgroundColor(bgColor);
        data.addChartDataSet(hbarDataSet);
        hbarDataSet.setBorderColor(borderColor);
        hbarDataSet.setBorderWidth(1);
        data.setLabels(labels);
        hbarModelCollector.setData(data);
         
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addXAxesData(linearAxes);
        options.setScales(cScales);
         
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Collectors Collection Information for the year " + year);
        options.setTitle(title);
         
        hbarModelCollector.setOptions(options);
    }
	
	public void createStackedGroupBarModel() {
		System.out.println("create stack group model>>>");
        stackedGroupBarModel = new BarChartModel();
        ChartData data = new ChartData();
        
        /*Map<String, Double> last = ReadDashboardInfo.getInfo("collection-last-year");
        Map<String, Double> current = ReadDashboardInfo.getInfo("collection-this-year");*/
        Map<String, Double> last = getLastData();
        Map<String, Double> current = getCurrentData();
        
        int sizeLast = last.size();
        int sizeCurrent = current.size();
        int loopHisghest = 12;
        
        if(sizeLast > sizeCurrent) {
        	loopHisghest = sizeLast;
        }else if(sizeCurrent > sizeLast) {
        	loopHisghest = sizeCurrent;
        }	
        
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Percentage");
        barDataSet.setBackgroundColor("rgb(255, 99, 132)");
        barDataSet.setStack("Stack 0");
        List<Number> dataVal = new ArrayList<>();
       /* dataVal.add(-22.22);
        dataVal.add(-70);
        dataVal.add(-33);
        dataVal.add(30);
        dataVal.add(-49);
        dataVal.add(23);
        dataVal.add(-92);
        barDataSet.setData(dataVal);*/
         
        BarChartDataSet barDataSet2 = new BarChartDataSet();
        barDataSet2.setLabel("2018 Income");
        barDataSet2.setBackgroundColor("rgb(54, 162, 235)");
        barDataSet2.setStack("Stack 0");
        List<Number> dataVal2 = new ArrayList<>();
        /*dataVal2.add(90);
        dataVal2.add(18);
        dataVal2.add(86);
        dataVal2.add(8);
        dataVal2.add(34);
        dataVal2.add(46);
        dataVal2.add(11);
        barDataSet2.setData(dataVal2);*/
         
        BarChartDataSet barDataSet3 = new BarChartDataSet();
        barDataSet3.setLabel("2019 Income");
        barDataSet3.setBackgroundColor("rgb(75, 192, 192)");
        barDataSet3.setStack("Stack 0");
        List<Number> dataVal3 = new ArrayList<>();
        /*dataVal3.add(70);
        dataVal3.add(73);
        dataVal3.add(-25);
        dataVal3.add(65);
        dataVal3.add(49);
        dataVal3.add(-18);
        dataVal3.add(46);
        barDataSet3.setData(dataVal3);*/
         
        /*data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet2);
        data.addChartDataSet(barDataSet3);*/
         
        List<String> labels = new ArrayList<>();
        
        
        for(int key=1;key<=loopHisghest;key++) {
        	
        	double lastAmount = 0d;
        	double currentAmount = 0d;
        	double percentage = 0d;
        	
        	if(last!=null && last.containsKey(key+"")) {
        		lastAmount = last.get(key+"");
        	}
        	
        	if(current!=null && current.containsKey(key+"")) {
        		currentAmount = current.get(key+"");
        	}
        	
        	if(lastAmount>0 && currentAmount>0) {
        		
        		percentage = lastAmount - currentAmount;
        		
        	}else if(lastAmount>0 && currentAmount<=0) {
        		
        		percentage = lastAmount;
        		
        	}else if(lastAmount<=0 && currentAmount>0) {
        		
        		percentage = 0;
        		
        	}
        	
        	if(lastAmount>currentAmount) {
    			percentage /=lastAmount;
    			percentage *=100;
    			percentage = -percentage;
    			percentage = Numbers.roundOf(percentage, 2);
    		}else {
    			percentage /=lastAmount;
    			percentage *=100;
    			percentage *= -1;
    			percentage = Numbers.roundOf(percentage, 2);
    		}
        	
        	
        	dataVal.add(percentage);
        	dataVal2.add(lastAmount);
        	dataVal3.add(currentAmount);
        	
        	
        	labels.add(DateUtils.getMonthName(key));
        }
        barDataSet.setData(dataVal);
        barDataSet2.setData(dataVal2);
        barDataSet3.setData(dataVal3);
        
        data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet2);
        data.addChartDataSet(barDataSet3);
        
        data.setLabels(labels);
        stackedGroupBarModel.setData(data);
         
        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setStacked(true);    
        cScales.addXAxesData(linearAxes);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);
         
        Title title = new Title();
        title.setDisplay(true);
        title.setText("MTO Collections Information");
        options.setTitle(title);
         
        Tooltip tooltip = new Tooltip();
        tooltip.setMode("index");
        tooltip.setIntersect(false);
        options.setTooltip(tooltip);  
         
        stackedGroupBarModel.setOptions(options);
    }
	
	public void uploadData(FileUploadEvent event){
		
		 try {
			 InputStream stream = event.getFile().getInputstream();
			 //String ext = FilenameUtils.getExtension(event.getFile().getFileName());
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
		String filename = "RCD-Data-" + DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "."+fileExt.toLowerCase();
		
		//create folder if not exist
		File dirPath = new File(DOC_PATH);
		dirPath.mkdir();
		
		System.out.println("writing... writeDocToFile : " + filename);
		File fileDoc = new File(DOC_PATH +  filename);
		Path file = fileDoc.toPath();
		Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
		readingExcelFile(DOC_PATH + filename);
		createLineModel();
		createStackedGroupBarModel();
		
		Map<String, Double> current = Collections.synchronizedMap(new HashMap<String, Double>());
		
		for(RcdInfo i : readXLSFileCollector(new File(DOC_PATH + filename), 1)) {
			String sql = " AND col.colyear=" + i.getYear() + " AND iss.isid="+i.getId();
			List<SummaryCollectionsCollector> cols = SummaryCollectionsCollector.retrive(sql, new String[0]);
			SummaryCollectionsCollector col = new SummaryCollectionsCollector();
			if(cols!=null && cols.size()>0) {
				col = cols.get(0);
			}else {
				col.setIsActive(1);
				col.setYear(i.getYear());
				Collector collector = new Collector();
				collector.setId(i.getId());
				col.setCollector(collector);
			}
			if(i.getJanuary()>0) {
				col.setJanuary(i.getJanuary());
			}
			if(i.getFebruary()>0) {
				col.setFebruary(i.getFebruary());
			}
			if(i.getMarch()>0) {
				col.setMarch(i.getMarch());
			}
			if(i.getApril()>0) {
				col.setApril(i.getApril());
			}
			if(i.getMay()>0) {
				col.setMay(i.getMay());
			}
			if(i.getJune()>0) {
				col.setJune(i.getJune());
			}
			if(i.getJuly()>0) {
				col.setJuly(i.getJuly());
			}
			if(i.getAugust()>0) {
				col.setAugust(i.getAugust());
			}
			if(i.getSeptember()>0) {
				col.setSeptember(i.getSeptember());
			}
			if(i.getOctober()>0) {
				col.setOctober(i.getOctober());
			}
			if(i.getNovember()>0) {
				col.setNovember(i.getNovember());
			}
			if(i.getDecember()>0) {
				col.setDecember(i.getDecember());
			}
			col.save();
			
			double amount = i.getJanuary() + i.getFebruary() + i.getMarch() + i.getApril() + i.getMay() + i.getJune() + i.getJuly() + i.getAugust() + i.getSeptember() + i.getOctober() + i.getNovember() + i.getDecember();
			current.put(i.getName(), amount);
		}
		
		createHorizontalBarModelCollector(current);
		
		return true;
		}catch(IOException e){return false;}
		
	}
	
	public static void main(String[] args) {
		String fileName = System.getenv("SystemDrive") + File.separator + "webtis" + File.separator + "upload" + File.separator + "RCD-Data-03252019105909.xls";
		GraphFormBean b = new GraphFormBean();
		b.readingExcelFile(fileName);
	}
	
	public void readingExcelFile(String fileName) {
		//File file = new File(DOC_PATH + fileName);
		System.out.println("check file path >> " + fileName);
		File file = new File(fileName);
		//loadFile(file,0);
		lastData = Collections.synchronizedMap(new HashMap<String, Double>());
		currentData = Collections.synchronizedMap(new HashMap<String, Double>());
		
		int count = 1;
		summData = Collections.synchronizedList(new ArrayList<SummaryCollections>());
		for(RcdInfo i : readXLSFileYEAR(file, 0)) {
			System.out.println("reading data>> " + i.getYear());
			
			String sql = " AND sumyear=" + i.getYear();
			
			SummaryCollections sum =  new SummaryCollections();
			List<SummaryCollections> sums =	SummaryCollections.retrive(sql, new String[0]);
			
			if(sums!=null && sums.size()>0) {
				sum = sums.get(0);
			}else {
				sum.setIsActive(1);
				sum.setYear(i.getYear());
			}
			
			sum.setJanuary(i.getJanuary());
			sum.setFebruary(i.getFebruary());
			sum.setMarch(i.getMarch());
			sum.setApril(i.getApril());
			sum.setMay(i.getMay());
			sum.setJune(i.getJune());
			sum.setJuly(i.getJuly());
			sum.setAugust(i.getAugust());
			sum.setSeptember(i.getSeptember());
			sum.setOctober(i.getOctober());
			sum.setNovember(i.getNovember());
			sum.setDecember(i.getDecember());
			sum = SummaryCollections.save(sum);
			
			if(count==1) {
				
					lastData.put("1", i.getJanuary());
					lastData.put("2", i.getFebruary());
					lastData.put("3", i.getMarch());
					lastData.put("4", i.getApril());
					lastData.put("5", i.getMay());
					lastData.put("6", i.getJune());
					lastData.put("7", i.getJuly());
					lastData.put("8", i.getAugust());
					lastData.put("9", i.getSeptember());
					lastData.put("10", i.getOctober());
					lastData.put("11", i.getNovember());
					lastData.put("12", i.getDecember());
				
			}else {
				
					currentData.put("1", i.getJanuary());
					currentData.put("2", i.getFebruary());
					currentData.put("3", i.getMarch());
					currentData.put("4", i.getApril());
					currentData.put("5", i.getMay());
					currentData.put("6", i.getJune());
					currentData.put("7", i.getJuly());
					currentData.put("8", i.getAugust());
					currentData.put("9", i.getSeptember());
					currentData.put("10", i.getOctober());
					currentData.put("11", i.getNovember());
					currentData.put("12", i.getDecember());
				
			}
			summData.add(sum);
			count++;
		}
		
		System.out.println("Last Year");
		for(String key : lastData.keySet()) {
			int month = Integer.valueOf(key);
			System.out.println(DateUtils.getMonthName(month) + " Amount: " + lastData.get(key));
		}
		System.out.println("Current Year");
		for(String key : currentData.keySet()) {
			int month = Integer.valueOf(key);
			System.out.println(DateUtils.getMonthName(month) + " Amount: " + currentData.get(key));
		}
		
	}
	
	/*private void loadFile(File file,int sheetNo) {
		System.out.println(file.getName());
		String ext = file.getName().split("\\.")[1];
		
	}*/
	
	private static List<RcdInfo> readXLSFileYEAR(File file,int sheetNo) {
		System.out.println("reading excel");
		try {
			FileInputStream fin = new FileInputStream(file);
			POIFSFileSystem fs = new POIFSFileSystem(fin); 
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(sheetNo);
			HSSFRow row;
			HSSFCell cell;
				
				List<RcdInfo> names = Collections.synchronizedList(new ArrayList<RcdInfo>());
				Iterator rows = sheet.rowIterator();
				int startRow=1;
			    while (rows.hasNext()){
		            row=(HSSFRow) rows.next();
		            Iterator cells = row.cellIterator();
		            int countRow = 1;
		            RcdInfo att = new RcdInfo();
		            System.out.println("iterate>> ");
		            if(startRow>1) {
		            	
			            while (cells.hasNext()){
			            	
				                cell=(HSSFCell) cells.next();
				                double value=0d;
				                if(cell.getCellTypeEnum()==CellType.STRING) {
				                	//value = Double.valueOf(cell.getStringCellValue());
				                }else if(cell.getCellTypeEnum()==CellType.NUMERIC) {
				                	value = cell.getNumericCellValue();
				                }else {
				                	//U Can Handel Boolean, Formula, Errors
				                	//System.out.println("\t");
				                }
				                System.out.println("vlue>> " + value);
				                switch(countRow) {
				                case 1: att.setYear(value); break;
				                case 2: att.setJanuary(value); break;
				                case 3: att.setFebruary(value); break;
				                case 4: att.setMarch(value); break;
				                case 5: att.setApril(value); break;
				                case 6: att.setMay(value); break;
				                case 7: att.setJune(value); break;
				                case 8: att.setJuly(value); break;
				                case 9: att.setAugust(value); break;
				                case 10: att.setSeptember(value); break;
				                case 11: att.setOctober(value); break;
				                case 12: att.setNovember(value); break;
				                case 13: att.setDecember(value); break;
				                }
				                
				               countRow++;
			            }
			            names.add(att);
		            }
		            startRow++;
			    }   
	    
			   
			    fin.close();
				
			    return names;
			    } catch(Exception e) {
			    	e.printStackTrace();
			    }	
		return null;
			
	}
	
	private static List<RcdInfo> readXLSFileCollector(File file,int sheetNo) {
		System.out.println("reading excel");
		try {
			FileInputStream fin = new FileInputStream(file);
			POIFSFileSystem fs = new POIFSFileSystem(fin); 
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(sheetNo);
			HSSFRow row;
			HSSFCell cell;
				
				List<RcdInfo> names = Collections.synchronizedList(new ArrayList<RcdInfo>());
				Iterator rows = sheet.rowIterator();
				int startRow=1;
			    while (rows.hasNext()){
		            row=(HSSFRow) rows.next();
		            Iterator cells = row.cellIterator();
		            int countRow = 1;
		            RcdInfo att = new RcdInfo();
		            System.out.println("iterate>> ");
		            if(startRow>1) {
		            	
			            while (cells.hasNext()){
			            	
				                cell=(HSSFCell) cells.next();
				                String value="";
				                double amount = 0d; 
				                if(cell.getCellTypeEnum()==CellType.STRING) {
				                	value = cell.getStringCellValue();
				                }else if(cell.getCellTypeEnum()==CellType.NUMERIC) {
				                	amount = cell.getNumericCellValue();
				                }else {
				                	//U Can Handel Boolean, Formula, Errors
				                	//System.out.println("\t");
				                }
				                System.out.println("vlue>> " + value);
				                switch(countRow) {
				                case 1: String a = amount+""; a = a.replace(".0", ""); att.setId(Integer.valueOf(a));break;
				                case 2: att.setName(value);
				                case 3: att.setYear(amount); break;
				                case 4: att.setJanuary(amount); break;
				                case 5: att.setFebruary(amount); break;
				                case 6: att.setMarch(amount); break;
				                case 7: att.setApril(amount); break;
				                case 8: att.setMay(amount); break;
				                case 9: att.setJune(amount); break;
				                case 10: att.setJuly(amount); break;
				                case 11: att.setAugust(amount); break;
				                case 12: att.setSeptember(amount); break;
				                case 13: att.setOctober(amount); break;
				                case 14: att.setNovember(amount); break;
				                case 15: att.setDecember(amount); break;
				                }
				                
				               countRow++;
			            }
			            names.add(att);
		            }
		            startRow++;
			    }   
	    
			   
			    fin.close();
				
			    return names;
			    } catch(Exception e) {
			    	e.printStackTrace();
			    }	
		return null;
			
	}
	
	public void uploadDataCollector(FileUploadEvent event){
		
		 try {
			 InputStream stream = event.getFile().getInputstream();
			 //String ext = FilenameUtils.getExtension(event.getFile().getFileName());
			 String file = event.getFile().getFileName();
			 
			 if(writeDocToFileCollector(event)){
				 Application.addMessage(1, "Success", "Data has been successfully uploaded");
				 
			 }else{
				 Application.addMessage(3, "Error", "Error uploading the data " + file);
			 }
			 
	     } catch (Exception e) {
	     
	     }
		
	}
	
	private boolean writeDocToFileCollector(FileUploadEvent event){
		try{
		InputStream stream = event.getFile().getInputstream();
		String fileExt = event.getFile().getFileName().split("\\.")[1];
		String filename = "RCD-Data-Collector-" + DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "."+fileExt.toLowerCase();
		
		//create folder if not exist
		File dirPath = new File(DOC_PATH);
		dirPath.mkdir();
		
		System.out.println("writing... writeDocToFile : " + filename);
		File fileDoc = new File(DOC_PATH +  filename);
		Path file = fileDoc.toPath();
		Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
		/*readingExcelFile(DOC_PATH + filename);
		createLineModel();
		createStackedGroupBarModel();*/
		
		Map<String, Double> current = Collections.synchronizedMap(new HashMap<String, Double>());
		int collecotId = 0;
		double amount = 0d;
		int year = DateUtils.getCurrentYear();
		int month = DateUtils.getCurrentMonth();
		for(CollectorCollections i : readXLSFileCollectorCollections(new File(DOC_PATH + filename), 0)) {
			System.out.println(" collecting forms>> " + i.getFormType());
			if(i.getCollector()!=null) {
			collecotId = i.getCollector().getId();
			String sql = " AND col.isid=? AND col.ccdate=? AND col.formtype=?";
			String[] params = new String[3];
			params[0] = i.getCollector().getId()+"";
			params[1] = i.getDateTrans();
			params[2] = i.getFormType() +"";
			List<CollectorCollections> cols = CollectorCollections.retrive(sql, params);
			CollectorCollections col = new  CollectorCollections();
			if(cols!=null && cols.size()>0) {
				col = cols.get(0);
			}else {
				col.setIsActive(1);
			}
			if(i.getAmount()>0) { // zero amount will not be saved
				col.setDateTrans(i.getDateTrans());
				col.setAmount(i.getAmount());
				col.setFormType(i.getFormType());
				col.setCollector(i.getCollector());
				col.save();
				
				amount += i.getAmount();
				
				String[] nums = col.getDateTrans().split("-");
				year = Integer.valueOf(nums[0]);
				month = Integer.valueOf(nums[1]);
			}
			
			current.put(FormType.nameId(i.getFormType()), i.getAmount());
			}
		}
		
		Collector collector = Collector.retrieve(collecotId);
		setCollectorId(collector.getId());
		
		createHorizontalBarModel(current);
		
		
		
		String sql = " AND col.colyear=" + year + " AND iss.isid="+collector.getId();
		List<SummaryCollectionsCollector> cols = SummaryCollectionsCollector.retrive(sql, new String[0]);
		SummaryCollectionsCollector col = new SummaryCollectionsCollector();
		if(cols!=null && cols.size()>0) {
			col = cols.get(0);
			
			switch(month) {
			case 1 : amount += col.getJanuary(); col.setJanuary(amount); col.save(); break;
			case 2 : amount += col.getFebruary(); col.setFebruary(amount); col.save(); break;
			case 3 : amount += col.getMarch(); col.setMarch(amount); col.save(); break;
			case 4 : amount += col.getApril(); col.setApril(amount); col.save(); break;
			case 5 : amount += col.getMay(); col.setMay(amount); col.save(); break;
			case 6 : amount += col.getJune(); col.setJune(amount); col.save(); break;
			case 7 : amount += col.getJuly(); col.setJuly(amount); col.save(); break;
			case 8 : amount += col.getAugust(); col.setAugust(amount); col.save(); break;
			case 9 : amount += col.getSeptember(); col.setSeptember(amount); col.save(); break;
			case 10 : amount += col.getOctober(); col.setOctober(amount); col.save(); break;
			case 11 : amount += col.getNovember(); col.setNovember(amount); col.save(); break;
			case 12 : amount += col.getDecember(); col.setDecember(amount); col.save(); break;
			}
			
		}else {
			col.setCollector(collector);
			col.setYear(year);
			col.setIsActive(1);
			switch(month) {
			case 1 : col.setJanuary(amount); col.save(); break;
			case 2 : col.setFebruary(amount); col.save(); break;
			case 3 : col.setMarch(amount); col.save(); break;
			case 4 : col.setApril(amount); col.save(); break;
			case 5 : col.setMay(amount); col.save(); break;
			case 6 : col.setJune(amount); col.save(); break;
			case 7 : col.setJuly(amount); col.save(); break;
			case 8 : col.setAugust(amount); col.save(); break;
			case 9 : col.setSeptember(amount); col.save(); break;
			case 10 : col.setOctober(amount); col.save(); break;
			case 11 : col.setNovember(amount); col.save(); break;
			case 12 : col.setDecember(amount); col.save(); break;
			}
		}
		
		sql = " AND sumyear=" + year;
		List<SummaryCollections> sumcols = SummaryCollections.retrive(sql, new String[0]);
		SummaryCollections sumcol = new SummaryCollections();
		if(sumcols!=null && sumcols.size()>0) {
			sumcol = sumcols.get(0);
			
			switch(month) {
			case 1 : amount += sumcol.getJanuary(); sumcol.setJanuary(amount); sumcol.save(); break;
			case 2 : amount += sumcol.getFebruary(); sumcol.setFebruary(amount); sumcol.save(); break;
			case 3 : amount += sumcol.getMarch(); sumcol.setMarch(amount); sumcol.save(); break;
			case 4 : amount += sumcol.getApril(); sumcol.setApril(amount); sumcol.save(); break;
			case 5 : amount += sumcol.getMay(); sumcol.setMay(amount); sumcol.save(); break;
			case 6 : amount += sumcol.getJune(); sumcol.setJune(amount); sumcol.save(); break;
			case 7 : amount += sumcol.getJuly(); sumcol.setJuly(amount); sumcol.save(); break;
			case 8 : amount += sumcol.getAugust(); sumcol.setAugust(amount); sumcol.save(); break;
			case 9 : amount += sumcol.getSeptember(); sumcol.setSeptember(amount); sumcol.save(); break;
			case 10 : amount += sumcol.getOctober(); sumcol.setOctober(amount); sumcol.save(); break;
			case 11 : amount += sumcol.getNovember(); sumcol.setNovember(amount); sumcol.save(); break;
			case 12 : amount += sumcol.getDecember(); sumcol.setDecember(amount); sumcol.save(); break;
			}
			
		}else {
			sumcol.setYear(year);
			sumcol.setIsActive(1);
			switch(month) {
			case 1 : sumcol.setJanuary(amount); sumcol.save(); break;
			case 2 : sumcol.setFebruary(amount); sumcol.save(); break;
			case 3 : sumcol.setMarch(amount); sumcol.save(); break;
			case 4 : sumcol.setApril(amount); sumcol.save(); break;
			case 5 : sumcol.setMay(amount); sumcol.save(); break;
			case 6 : sumcol.setJune(amount); sumcol.save(); break;
			case 7 : sumcol.setJuly(amount); sumcol.save(); break;
			case 8 : sumcol.setAugust(amount); sumcol.save(); break;
			case 9 : sumcol.setSeptember(amount); sumcol.save(); break;
			case 10 : sumcol.setOctober(amount); sumcol.save(); break;
			case 11 : sumcol.setNovember(amount); sumcol.save(); break;
			case 12 : sumcol.setDecember(amount); sumcol.save(); break;
			}
		}
		
		return true;
		}catch(IOException e){return false;}
		
	}
	
	private static List<CollectorCollections> readXLSFileCollectorCollections(File file,int sheetNo) {
		System.out.println("reading excel");
		List<CollectorCollections> names = Collections.synchronizedList(new ArrayList<CollectorCollections>());
		try {
			FileInputStream fin = new FileInputStream(file);
			POIFSFileSystem fs = new POIFSFileSystem(fin); 
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(sheetNo);
			HSSFRow row;
			HSSFCell cell;
				
				
				Iterator rows = sheet.rowIterator();
				int startRow=1;
			    while (rows.hasNext()){
		            row=(HSSFRow) rows.next();
		            Iterator cells = row.cellIterator();
		            int countRow = 1;
		            
		            //System.out.println("iterate>> ");
		            if(startRow>1 && startRow<12) {
		            	CollectorCollections att = new CollectorCollections();
			            while (cells.hasNext()){
			            	
				                cell=(HSSFCell) cells.next();
				                String value="";
				                double amount = 0d; 
				                if(cell.getCellTypeEnum()==CellType.STRING) {
				                	value = cell.getStringCellValue();
				                }else if(cell.getCellTypeEnum()==CellType.NUMERIC) {
				                	amount = cell.getNumericCellValue();
				                }else {
				                	//System.out.println("Others " + cell.getStringCellValue());
				                	//System.out.println("\t");
				                }
				                //System.out.println("row "+ startRow +"value>> " + value + " amount>> " + amount);
				                
				                switch(countRow) {
				                case 1: try{String a = amount+""; a = a.replace(".0", ""); Collector collector = new Collector(); collector.setId(Integer.valueOf(a)); att.setCollector(collector);}catch(Exception e) {e.printStackTrace();}break;
				                case 2: break;
				                case 3: try{int type=startRow-1; att.setFormType(type);}catch(Exception e) {e.printStackTrace();} break;
				                case 4: try{att.setDateTrans(value);}catch(Exception e) {e.printStackTrace();} break;
				                case 5: try{att.setAmount(amount);}catch(Exception e) {e.printStackTrace();} break;
				                
				                }
				                
				               countRow++;
			            }
			            
			          
			           	names.add(att);
			           	
			           
		            }
		            startRow++;
			    }   
	    
			   
			    fin.close();
				
			    /*for(CollectorCollections c : names) {
			    	System.out.println("ID:" + c.getCollector().getId() + " form " + c.getFormType() + "  Date " + c.getDateTrans() + " amount " + c.getAmount());
			    }*/
			    
			    return names;
			    } catch(Exception e) {
			    	e.printStackTrace();
			    }	
		return names;
			
	}
	
	public LineChartModel getLineModel() {
		return lineModel;
	}

	public void setLineModel(LineChartModel lineModel) {
		this.lineModel = lineModel;
	}

	public HorizontalBarChartModel getHbarModel() {
		return hbarModel;
	}

	public void setHbarModel(HorizontalBarChartModel hbarModel) {
		this.hbarModel = hbarModel;
	}

	public BarChartModel getStackedGroupBarModel() {
		return stackedGroupBarModel;
	}

	public void setStackedGroupBarModel(BarChartModel stackedGroupBarModel) {
		this.stackedGroupBarModel = stackedGroupBarModel;
	}

	public Map<String, Double> getLastData() {
		return lastData;
	}

	public void setLastData(Map<String, Double> lastData) {
		this.lastData = lastData;
	}

	public Map<String, Double> getCurrentData() {
		return currentData;
	}

	public void setCurrentData(Map<String, Double> currentData) {
		this.currentData = currentData;
	}

	public HorizontalBarChartModel getHbarModelCollector() {
		return hbarModelCollector;
	}

	public void setHbarModelCollector(HorizontalBarChartModel hbarModelCollector) {
		this.hbarModelCollector = hbarModelCollector;
	}

	public List getCollectors() {
		collectors = new ArrayList<>();
		
		for(Collector c : Collector.retrieve("", new String[0])) {
			if(c.getId()==0) {
				collectors.add(new SelectItem(0, "All Collector"));
			}else {
				collectors.add(new SelectItem(c.getId(), c.getName()));
			}
		}
		
		return collectors;
	}

	public void setCollectors(List collectors) {
		this.collectors = collectors;
	}

	public int getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(int collectorId) {
		this.collectorId = collectorId;
	}

	public Date getFromDate() {
		if(fromDate==null) {
			fromDate = DateUtils.getDateToday();
		}
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		if(toDate==null) {
			toDate = DateUtils.getDateToday();
		}
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List getYears() {
		years = new ArrayList<>();
		for(int year=2018; year<=DateUtils.getCurrentYear(); year++) {
			years.add(new SelectItem(year, year+""));
		}
		return years;
	}

	public void setYears(List years) {
		this.years = years;
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

	public List<SummaryCollections> getSummData() {
		return summData;
	}

	public void setSummData(List<SummaryCollections> summData) {
		this.summData = summData;
	}

	public List<SummaryCollectionsCollector> getSummColletorData() {
		return summColletorData;
	}

	public void setSummColletorData(List<SummaryCollectionsCollector> summColletorData) {
		this.summColletorData = summColletorData;
	}

	public List<CollectorCollections> getPerColletorForm() {
		return perColletorForm;
	}

	public void setPerColletorForm(List<CollectorCollections> perColletorForm) {
		this.perColletorForm = perColletorForm;
	}

	public List<CollectorCollections> getCollectorData() {
		return collectorData;
	}

	public void setCollectorData(List<CollectorCollections> collectorData) {
		this.collectorData = collectorData;
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

	public SummaryCollections getEditDataSummaryCollections() {
		return editDataSummaryCollections;
	}

	public void setEditDataSummaryCollections(SummaryCollections editDataSummaryCollections) {
		this.editDataSummaryCollections = editDataSummaryCollections;
	}

	public SummaryCollectionsCollector getEditDataSummaryCollectionsCollector() {
		return editDataSummaryCollectionsCollector;
	}

	public void setEditDataSummaryCollectionsCollector(SummaryCollectionsCollector editDataSummaryCollectionsCollector) {
		this.editDataSummaryCollectionsCollector = editDataSummaryCollectionsCollector;
	}

	public CollectorCollections getEditDataSummaryCollectorCollections() {
		return editDataSummaryCollectorCollections;
	}

	public void setEditDataSummaryCollectorCollections(CollectorCollections editDataSummaryCollectorCollections) {
		this.editDataSummaryCollectorCollections = editDataSummaryCollectorCollections;
	}

	public double getYear() {
		if(year==0) {
			year = DateUtils.getCurrentYear();
		}
		return year;
	}

	public void setYear(double year) {
		this.year = year;
	}

	public double getJanuary() {
		return January;
	}

	public void setJanuary(double january) {
		January = january;
	}

	public double getFebruary() {
		return February;
	}

	public void setFebruary(double february) {
		February = february;
	}

	public double getMarch() {
		return March;
	}

	public void setMarch(double march) {
		March = march;
	}

	public double getApril() {
		return April;
	}

	public void setApril(double april) {
		April = april;
	}

	public double getMay() {
		return May;
	}

	public void setMay(double may) {
		May = may;
	}

	public double getJune() {
		return June;
	}

	public void setJune(double june) {
		June = june;
	}

	public double getJuly() {
		return July;
	}

	public void setJuly(double july) {
		July = july;
	}

	public double getAugust() {
		return August;
	}

	public void setAugust(double august) {
		August = august;
	}

	public double getSeptember() {
		return September;
	}

	public void setSeptember(double september) {
		September = september;
	}

	public double getOctober() {
		return October;
	}

	public void setOctober(double october) {
		October = october;
	}

	public double getNovember() {
		return November;
	}

	public void setNovember(double november) {
		November = november;
	}

	public double getDecember() {
		return December;
	}

	public void setDecember(double december) {
		December = december;
	}

	public List getCollectorsPerYear() {
		
		collectorsPerYear = new ArrayList<>();
		
		for(Collector c : Collector.retrieve("", new String[0])) {
			if(c.getId()!=0) {
				collectorsPerYear.add(new SelectItem(c.getId(), c.getName()));
			}
		}
		
		return collectorsPerYear;
	}

	public void setCollectorsPerYear(List collectorsPerYear) {
		this.collectorsPerYear = collectorsPerYear;
	}

	public int getCollectorPerYearId() {
		return collectorPerYearId;
	}

	public void setCollectorPerYearId(int collectorPerYearId) {
		this.collectorPerYearId = collectorPerYearId;
	}

	public String getTotalCollection() {
		return totalCollection;
	}

	public void setTotalCollection(String totalCollection) {
		this.totalCollection = totalCollection;
	}

	public int getFormTypeId() {
		return formTypeId;
	}

	public void setFormTypeId(int formTypeId) {
		this.formTypeId = formTypeId;
	}

	public List getForms() {
		forms = new ArrayList<>();
		
		for(FormType t : FormType.values()) {
			forms.add(new SelectItem(t.getId(), t.getName()));
		}
		
		return forms;
	}

	public void setForms(List forms) {
		this.forms = forms;
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

	public double getAmountReceived() {
		return amountReceived;
	}

	public void setAmountReceived(double amountReceived) {
		this.amountReceived = amountReceived;
	}

	public int getCollectorAddId() {
		return collectorAddId;
	}

	public void setCollectorAddId(int collectorAddId) {
		this.collectorAddId = collectorAddId;
	}

	public List getCollectorAdd() {
		
		collectorAdd = new ArrayList<>();
		
		for(Collector c : Collector.retrieve("", new String[0])) {
			if(c.getId()!=0) {
				collectorAdd.add(new SelectItem(c.getId(), c.getName()));
			}
		}
		
		return collectorAdd;
	}

	public void setCollectorAdd(List collectorAdd) {
		this.collectorAdd = collectorAdd;
	}

	public double getYear1() {
		if(year1==0) {
			year1 = DateUtils.getCurrentYear();
		}
		return year1;
	}

	public void setYear1(double year1) {
		this.year1 = year1;
	}

	public double getJanuary1() {
		return January1;
	}

	public void setJanuary1(double january1) {
		January1 = january1;
	}

	public double getFebruary1() {
		return February1;
	}

	public void setFebruary1(double february1) {
		February1 = february1;
	}

	public double getMarch1() {
		return March1;
	}

	public void setMarch1(double march1) {
		March1 = march1;
	}

	public double getApril1() {
		return April1;
	}

	public void setApril1(double april1) {
		April1 = april1;
	}

	public double getMay1() {
		return May1;
	}

	public void setMay1(double may1) {
		May1 = may1;
	}

	public double getJune1() {
		return June1;
	}

	public void setJune1(double june1) {
		June1 = june1;
	}

	public double getJuly1() {
		return July1;
	}

	public void setJuly1(double july1) {
		July1 = july1;
	}

	public double getAugust1() {
		return August1;
	}

	public void setAugust1(double august1) {
		August1 = august1;
	}

	public double getSeptember1() {
		return September1;
	}

	public void setSeptember1(double september1) {
		September1 = september1;
	}

	public double getOctober1() {
		return October1;
	}

	public void setOctober1(double october1) {
		October1 = october1;
	}

	public double getNovember1() {
		return November1;
	}

	public void setNovember1(double november1) {
		November1 = november1;
	}

	public double getDecember1() {
		return December1;
	}

	public void setDecember1(double december1) {
		December1 = december1;
	}

	public List getCollectorsPer() {
		
		collectorsPer = new ArrayList<>();
		for(Collector c : Collector.retrieve("", new String[0])) {
			if(c.getId()==0) {
				collectorsPer.add(new SelectItem(0, "All Collector"));
			}else {
				collectorsPer.add(new SelectItem(c.getId(), c.getName()));
			}
		}
		
		return collectorsPer;
	}

	public void setCollectorsPer(List collectorsPer) {
		this.collectorsPer = collectorsPer;
	}

	public int getCollectorIdPer() {
		return collectorIdPer;
	}

	public void setCollectorIdPer(int collectorIdPer) {
		this.collectorIdPer = collectorIdPer;
	}

	public List getYearsPer() {
		
		yearsPer = new ArrayList<>();
		for(int yer=2018; yer<=DateUtils.getCurrentYear(); yer++) {
			yearsPer.add(new SelectItem(yer, yer+ ""));
		}
		
		return yearsPer;
	}

	public void setYearsPer(List yearsPer) {
		this.yearsPer = yearsPer;
	}

	public int getYearIdPer() {
		
		if(yearIdPer==0) {
			yearIdPer = DateUtils.getCurrentYear();
		}
		
		return yearIdPer;
	}

	public void setYearIdPer(int yearIdPer) {
		this.yearIdPer = yearIdPer;
	}

	public List getMonthsPer() {
		
		monthsPer = new ArrayList<>();
		monthsPer.add(new SelectItem(0, "All Months"));
		for(int month=1; month<=12; month++) {
			monthsPer.add(new SelectItem(month, DateUtils.getMonthName(month)));
		}
		
		return monthsPer;
	}

	public void setMonthsPer(List monthsPer) {
		this.monthsPer = monthsPer;
	}

	public int getMonthIdPer() {
		
		return monthIdPer;
	}

	public void setMonthIdPer(int monthIdPer) {
		this.monthIdPer = monthIdPer;
	}

	public String getTotalCollectionPerYer() {
		return totalCollectionPerYer;
	}

	public void setTotalCollectionPerYer(String totalCollectionPerYer) {
		this.totalCollectionPerYer = totalCollectionPerYer;
	}

	public boolean isWithCompareCollectorOtherCollection() {
		return withCompareCollectorOtherCollection;
	}

	public void setWithCompareCollectorOtherCollection(boolean withCompareCollectorOtherCollection) {
		this.withCompareCollectorOtherCollection = withCompareCollectorOtherCollection;
	}

	 
	 
}
