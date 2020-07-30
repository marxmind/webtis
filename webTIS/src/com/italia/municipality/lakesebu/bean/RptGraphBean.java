package com.italia.municipality.lakesebu.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
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

import com.italia.municipality.lakesebu.controller.Collector;
import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.ORListing;
import com.italia.municipality.lakesebu.controller.ReadDashboardInfo;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.enm.FormType;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.utils.Numbers;

/**
 * 
 * @author Mark Italia
 * @since 05/09/2019
 * @version 1.0
 *
 */
//former name ReportsGraphBean
@Named
@ViewScoped
public class RptGraphBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6576983435431L;
	
	private Map<String, Double> lastData;
	private Map<String, Double> currentData;
	
	private  String DOC_PATH = System.getenv("SystemDrive") + File.separator + "webtis" + File.separator + "upload" + File.separator;
	
	private LineChartModel lineModel;
	private LineChartModel lineModelPerCollector;
	private BarChartModel stackedGroupBarModel;
	private HorizontalBarChartModel hbarModelCollector;
	private HorizontalBarChartModel hbarModel;
	
	private List collectorsPer;
	private int collectorIdPer;
	private List yearsPer;
	private int yearIdPer;
	private List monthsPer;
	private int monthIdPer;
	
	private int monthId;
	private List months;
	private List years;
	private int yearId;
	private List collectors;
	private int collectorId;
	
	private String textContent;
	
	@PostConstruct
	public void init() {
		System.out.println(">>>> ReportGraphBean init");
		loadData();
		createLineModel();
		createStackedGroupBarModel();
		createHorizontalBarModelCollector(ReadDashboardInfo.getInfo("collector-data"));
		createHorizontalBarModel(ReadDashboardInfo.getInfo("forms-this-year"));
		onloadLineModelCollector();
		
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
	
	public void selectedCollectorPer() {
		Map<String, Double> data = Collections.synchronizedMap(new HashMap<String, Double>());
		String sql = "";
		String[] params = new String[0];
		
		int month = DateUtils.getCurrentMonth();
		String monthFrom = "";
		String monthTo = "";
		if(getYearIdPer()!=DateUtils.getCurrentYear()) {//year selected
			if(getCollectorIdPer()>0){//with collector
				if(getMonthIdPer()>0) {
					System.out.println("selected year with collector and specific month");
					month = getMonthIdPer();
					monthFrom = getYearIdPer() + "-" + (month<10? "0"+month : month) + "-01";
					monthTo = getYearIdPer() + "-" + (month<10? "0"+month : month) + "-31";
					sql = " AND (orl.ordatetrans>='"+monthFrom+"' AND orl.ordatetrans<='"+ monthTo +"') AND col.isid=" + getCollectorIdPer();
					String[] pm = orListingCollector(sql, params);
					double amnt = Double.valueOf(pm[0]);
					if(amnt>0) {
						data.put(pm[1], amnt);
					}
				}else {
					System.out.println("selected year with collector and all months");
					//for(int mnth=1; mnth<=12; mnth++) {
						params = new String[3];
						sql = " AND (orl.ordatetrans>=? AND orl.ordatetrans<=?) AND col.isid=?";
						params[0] = getYearIdPer() + "-01-01";
						params[1] = getYearIdPer() + "-12-31";
						params[2] = getCollectorIdPer()+"";
						String[] pm = orListingCollector(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
					//}
				}
			}else {
				System.out.println("selected year without collector");
				if(getMonthIdPer()>0) {
					System.out.println("selected year without collector and specific month");
					month = getMonthIdPer();
					monthFrom = getYearIdPer() + "-" + (month<10? "0"+month : month) + "-01";
					monthTo = getYearIdPer() + "-" + (month<10? "0"+month : month) + "-31";
					for(Collector c : Collector.retrieve("", new String[0])) {
						sql = " AND (orl.ordatetrans>='"+monthFrom+"' AND orl.ordatetrans<='"+ monthTo +"') AND col.isid=" + c.getId();
						String[] pm = orListingCollector(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
					}
				}else {
					System.out.println("selected year withouth collector and all months");
					for(Collector c : Collector.retrieve("", new String[0])) {
						params = new String[2];
						sql = " AND (orl.ordatetrans>=? AND orl.ordatetrans<=?) AND col.isid=" + c.getId();
						params[0] = getYearIdPer() + "-01-01";
						params[1] = getYearIdPer() + "-12-31";
						String[] pm = orListingCollector(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
					}
				}
			}
		}else {//current year
			if(getCollectorIdPer()>0){//with collector
				if(getMonthIdPer()>0) {
					System.out.println("current year with collector and specific month");
					month = getMonthIdPer();
					monthFrom = getYearIdPer() + "-" + (month<10? "0"+month : month) + "-01";
					monthTo = getYearIdPer() + "-" + (month<10? "0"+month : month) + "-31";
					sql = " AND (orl.ordatetrans>='"+monthFrom+"' AND orl.ordatetrans<='"+ monthTo +"') AND col.isid=" + getCollectorIdPer();
					String[] pm = orListingCollector(sql, params);
					double amnt = Double.valueOf(pm[0]);
					if(amnt>0) {
						data.put(pm[1], amnt);
					}
				}else {
					System.out.println("current year with collector and all months");
					int mnth=DateUtils.getCurrentMonth();
						params = new String[3];
						sql = " AND (orl.ordatetrans>=? AND orl.ordatetrans<=?) AND col.isid=?";
						params[0] = getYearIdPer() + "-01-01";
						params[1] = getYearIdPer() + "-" + (mnth<10? "0"+mnth : mnth) + "-31";
						params[2] = getCollectorIdPer()+"";
						String[] pm = orListingCollector(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
				}
			}else {
				System.out.println("current year without collector");
				if(getMonthIdPer()>0) {
					System.out.println("current year without collector and specific month");
					
					
					for(Collector c : Collector.retrieve("", new String[0])) {
						month = getMonthIdPer();
						monthFrom = getYearIdPer() + "-" + (month<10? "0"+month : month) + "-01";
						monthTo = getYearIdPer() + "-" + (month<10? "0"+month : month) + "-31";
						sql = " AND (orl.ordatetrans>='"+monthFrom+"' AND orl.ordatetrans<='"+ monthTo +"') AND col.isid=" + c.getId();
						String[] pm = orListingCollector(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
					}
					
					
				}else {
					System.out.println("current year without collector and all months");
					int mnth=DateUtils.getCurrentMonth();
					for(Collector c : Collector.retrieve("", new String[0])) {
						params = new String[2];
						sql = " AND (orl.ordatetrans>=? AND orl.ordatetrans<=?) AND col.isid=" + c.getId();
						params[0] = getYearIdPer() + "-01-01";
						params[1] = getYearIdPer() + "-" + (mnth<10? "0"+mnth : mnth) + "-31";
						String[] pm = orListingCollector(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
					}	
					
				}
			}
		}
		
		createHorizontalBarModelCollector(data);
		
	}
	
	public void selectedCollector() {
		Map<String, Double> data = Collections.synchronizedMap(new HashMap<String, Double>());
		String sql = "";
		String[] params = new String[0];
		
		int month = DateUtils.getCurrentMonth();
		String monthFrom = "";
		String monthTo = "";
		if(getYearId()!=DateUtils.getCurrentYear()) {//year selected
			if(getCollectorId()>0){//with collector
				if(getMonthId()>0) {
					for(FormType ftype : FormType.values()) {
					System.out.println("selected year with collector and specific month");
					month = getMonthIdPer();
					monthFrom = getYearId() + "-" + (month<10? "0"+month : month) + "-01";
					monthTo = getYearId() + "-" + (month<10? "0"+month : month) + "-31";
					sql = " AND (orl.ordatetrans>='"+monthFrom+"' AND orl.ordatetrans<='"+ monthTo +"') AND col.isid=" + getCollectorId() + " AND orl.aform=" + ftype.getId();
					String[] pm = orListingForms(sql, params);
					double amnt = Double.valueOf(pm[0]);
					if(amnt>0) {
						data.put(pm[1], amnt);
					}
					}
				}else {
					System.out.println("selected year with collector and all months");
					//for(int mnth=1; mnth<=12; mnth++) {
					for(FormType ftype : FormType.values()) {
						params = new String[3];
						sql = " AND (orl.ordatetrans>='"+monthFrom+"' AND orl.ordatetrans<='"+ monthTo +"') AND col.isid=" + getCollectorId() + " AND orl.aform=" + ftype.getId();
						params[0] = getYearId() + "-01-01";
						params[1] = getYearId() + "-12-31";
						params[2] = getCollectorId()+"";
						String[] pm = orListingForms(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
					}
				}
			}else {
				System.out.println("selected year without collector");
				if(getMonthId()>0) {
					System.out.println("selected year without collector and specific month");
					month = getMonthId();
					monthFrom = getYearId() + "-" + (month<10? "0"+month : month) + "-01";
					monthTo = getYearId() + "-" + (month<10? "0"+month : month) + "-31";
					for(FormType ftype : FormType.values()) {
						sql = " AND (orl.ordatetrans>='"+monthFrom+"' AND orl.ordatetrans<='"+ monthTo +"') AND col.isid=" + getCollectorId() + " AND orl.aform=" + ftype.getId();
						String[] pm = orListingForms(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
					}
				}else {
					System.out.println("selected year withouth collector and all months");
					for(FormType ftype : FormType.values()) {
						params = new String[2];
						sql = " AND (orl.ordatetrans>='"+monthFrom+"' AND orl.ordatetrans<='"+ monthTo +"') AND col.isid=" + getCollectorId() + " AND orl.aform=" + ftype.getId();
						params[0] = getYearId() + "-01-01";
						params[1] = getYearId() + "-12-31";
						String[] pm = orListingForms(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
					}
				}
			}
		}else {//current year
			if(getCollectorId()>0){//with collector
				if(getMonthId()>0) {
					System.out.println("current year with collector and specific month");
					for(FormType ftype : FormType.values()) {
					month = getMonthId();
					monthFrom = getYearId() + "-" + (month<10? "0"+month : month) + "-01";
					monthTo = getYearId() + "-" + (month<10? "0"+month : month) + "-31";
					sql = " AND (orl.ordatetrans>='"+monthFrom+"' AND orl.ordatetrans<='"+ monthTo +"') AND col.isid=" + getCollectorId() + " AND orl.aform=" + ftype.getId();
					String[] pm = orListingForms(sql, params);
					double amnt = Double.valueOf(pm[0]);
					if(amnt>0) {
						data.put(pm[1], amnt);
					}
					}
				}else {
					System.out.println("current year with collector and all months");
					int mnth=DateUtils.getCurrentMonth();
					for(FormType ftype : FormType.values()) {
						params = new String[2];
						sql = " AND (orl.ordatetrans>=? AND orl.ordatetrans<=?) AND col.isid=" + getCollectorId() + " AND orl.aform=" + ftype.getId();
						params[0] = getYearId() + "-01-01";
						params[1] = getYearId() + "-" + (mnth<10? "0"+mnth : mnth) + "-31";
						String[] pm = orListingForms(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
					}	
				}
			}else {
				System.out.println("current year without collector");
				if(getMonthId()>0) {
					System.out.println("current year without collector and specific month");
					
					
					for(FormType ftype : FormType.values()) {
						month = getMonthId();
						monthFrom = getYearId() + "-" + (month<10? "0"+month : month) + "-01";
						monthTo = getYearId() + "-" + (month<10? "0"+month : month) + "-31";
						sql = " AND (orl.ordatetrans>='"+monthFrom+"' AND orl.ordatetrans<='"+ monthTo +"') AND col.isid=" + getCollectorId() + " AND orl.aform=" + ftype.getId();
						String[] pm = orListingForms(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
					}
					
					
				}else {
					System.out.println("current year without collector and all months");
					int mnth=DateUtils.getCurrentMonth();
					for(FormType ftype : FormType.values()) {
						params = new String[2];
						sql = " AND (orl.ordatetrans>='"+monthFrom+"' AND orl.ordatetrans<='"+ monthTo +"') AND col.isid=" + getCollectorId() + " AND orl.aform=" + ftype.getId();
						params[0] = getYearId() + "-01-01";
						params[1] = getYearId() + "-" + (mnth<10? "0"+mnth : mnth) + "-31";
						String[] pm = orListingForms(sql, params);
						double amnt = Double.valueOf(pm[0]);
						if(amnt>0) {
							data.put(pm[1], amnt);
						}
					}	
					
				}
			}
		}
		createHorizontalBarModel(data);
		loadPerCollectorData();
	}
	
	public void loadData() {
		
		String sql = "";
		String[] params = new String[2];
		
		int yearCurrent = DateUtils.getCurrentYear();
		int yearLast = yearCurrent - 1;
		
		lastData = Collections.synchronizedMap(new HashMap<String, Double>());
		currentData = Collections.synchronizedMap(new HashMap<String, Double>());
		
		int cnt = 1;
		for(int year=yearLast; year<=DateUtils.getCurrentYear(); year++) {
			for(int month=1; month<=12; month++) {
				sql = " AND (orl.ordatetrans>=? AND orl.ordatetrans<=?)";
				params[0] = year + "-" + (month<10? "0"+month : month) + "-01";
				params[1] = year + "-" + (month<10? "0"+month : month) + "-31";
				
				double amount = amountORListing(sql, params);
				if(cnt==1) {
					lastData.put(month+"", amount);
				}else {
					currentData.put(month+"", amount);
				}
				cnt++;
				
			}
		}
		
	}
	
	public void loadPerCollectorData() {
		
		String sql = "";
		String[] params = new String[3];
		
		int yearCurrent = getYearId();
		int yearLast = yearCurrent - 1;
		
		lastData = Collections.synchronizedMap(new HashMap<String, Double>());
		currentData = Collections.synchronizedMap(new HashMap<String, Double>());
		
		int cnt = 1;
		for(int year=yearLast; year<=getYearId(); year++) {
			
			int monthEndSet = (getMonthId()==0? 12 : getMonthId());
			
				for(int month=1; month<=monthEndSet; month++) {
					sql = " AND (orl.ordatetrans>=? AND orl.ordatetrans<=?) AND col.isid=?";
					params[0] = year + "-" + (month<10? "0"+month : month) + "-01";
					params[1] = year + "-" + (month<10? "0"+month : month) + "-31";
					params[2] = getCollectorId()+"";
					
					double amount = amountORListing(sql, params);
					if(cnt==1) {
						lastData.put(month+"", amount);
					}else {
						currentData.put(month+"", amount);
					}
					cnt++;
					
				}
		}
		createLineModelPerCollector();
	}
	
	public double amountORListing(String sql, String[] params) {
		double amount = 0d;
		
		for(ORListing or : ORListing.retrieve(sql, params)) {
			amount += or.getAmount();
		}
		
		return amount;
	}
	public String[] orListingCollector(String sql, String[] params) {
		String[] pm = new String[2];
		double amount = 0d;
		String name = "";
		for(ORListing or : ORListing.retrieve(sql, params)) {
			amount += or.getAmount();
			name = or.getCollector().getName();
		}
		pm[0] = amount+"";
		pm[1] = name;
		return pm;
	}
	public String[] orListingForms(String sql, String[] params) {
		String[] pm = new String[2];
		double amount = 0d;
		String name = "";
		for(ORListing or : ORListing.retrieve(sql, params)) {
			amount += or.getAmount();
			name = or.getFormName();
		}
		pm[0] = amount+"";
		pm[1] = name;
		return pm;
	}
	
	public void createLineModel() {
		System.out.println("create line lineModel");
		lineModel = new LineChartModel();
        ChartData data = new ChartData();
        
        
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        
        LineChartDataSet dataSet2 = new LineChartDataSet();
        List<Object> values2 = new ArrayList<>();
         
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
        dataSet.setLabel(""+(DateUtils.getCurrentYear()-1));
        dataSet.setBorderColor("rgb(255, 99, 132)");
        dataSet.setLineTension(0.1);
        data.addChartDataSet(dataSet);
        
        dataSet2.setData(values2);
        dataSet2.setFill(false);
        dataSet2.setLabel(""+DateUtils.getCurrentYear());
        dataSet2.setBorderColor("rgb(75, 192, 192)");
        dataSet2.setLineTension(0.1);
        data.addChartDataSet(dataSet2);
        
        data.setLabels(labels);
         
        //Options
        LineChartOptions options = new LineChartOptions();        
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Collection Information");
        options.setTitle(title);
         
        lineModel.setOptions(options);
        lineModel.setData(data);
	}
	
	public void onloadLineModelCollector() {
		System.out.println("create line lineModelPerCollector");
		lineModelPerCollector = new LineChartModel();
        ChartData data = new ChartData();
        
        
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        values.add(0);
        LineChartDataSet dataSet2 = new LineChartDataSet();
        List<Object> values2 = new ArrayList<>();
        values2.add(0); 
        List<String> labels = new ArrayList<>();
        labels.add("None");
        
        
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
         
        lineModelPerCollector.setOptions(options);
        lineModelPerCollector.setData(data);
	}
	
	public void createLineModelPerCollector() {
		System.out.println("create line lineModelPerCollector");
		lineModelPerCollector = new LineChartModel();
        ChartData data = new ChartData();
        
        
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        
        LineChartDataSet dataSet2 = new LineChartDataSet();
        List<Object> values2 = new ArrayList<>();
         
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
        dataSet.setLabel(""+(getYearId()-1));
        dataSet.setBorderColor("rgb(255, 99, 132)");
        dataSet.setLineTension(0.1);
        data.addChartDataSet(dataSet);
        
        dataSet2.setData(values2);
        dataSet2.setFill(false);
        dataSet2.setLabel(""+getYearId());
        dataSet2.setBorderColor("rgb(75, 192, 192)");
        dataSet2.setLineTension(0.1);
        data.addChartDataSet(dataSet2);
        
        data.setLabels(labels);
         
        //Options
        LineChartOptions options = new LineChartOptions();        
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Collection Information");
        options.setTitle(title);
         
        lineModelPerCollector.setOptions(options);
        lineModelPerCollector.setData(data);
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
				script += "$(\"#idInfoAnlys\").hide(1000);";
				script += "$(\"#idSummaryLine\").show(1000);";
				loadData();
				createLineModel();
				createStackedGroupBarModel();
			}else {
				script = "$(\"#idSummaryLine\").hide(1000);";
			}
			
		}else if("per-collector-summary".equalsIgnoreCase(type)) {
			
			if("open".equalsIgnoreCase(closeOpen)) {
				
				script = "$(\"#idCollector\").hide(1000);";
				script += "$(\"#idSummaryLine\").hide(1000);";
				script += "$(\"#idInfoAnlys\").hide(1000);";
				script += "$(\"#idSummaryFormCollector\").show(1000);";
				
				createHorizontalBarModelCollector(ReadDashboardInfo.getInfo("collector-data"));
				
			}else {
				script = "$(\"#idSummaryFormCollector\").hide(1000);";
			}
			
		}else if("collector-collection".equalsIgnoreCase(type)) {
			
			if("open".equalsIgnoreCase(closeOpen)) {
				
				script = "$(\"#idSummaryLine\").hide(1000);";
				script += "$(\"#idSummaryFormCollector\").hide(1000);";
				script += "$(\"#idInfoAnlys\").hide(1000);";
				
				script += "$(\"#idCollector\").show(1000);";
				//selectedCollector();
				createHorizontalBarModel(ReadDashboardInfo.getInfo("forms-this-year"));
			}else {
				script = "$(\"#idCollector\").hide(1000);";
			}
			
		}else if("content".equalsIgnoreCase(type)) {
			if("open".equalsIgnoreCase(closeOpen)) {
				script = "$(\"#idSummaryLine\").hide(1000);";
				script += "$(\"#idSummaryFormCollector\").hide(1000);";
				script += "$(\"#idCollector\").hide(1000);";
				script += "$(\"#idInfoAnlys\").show(1000);";
				loadMsg();
			}
		}
		System.out.println("script: " + script);
		current.executeScript(script);
	}
	
	public void loadMsg() {
		String folderPath = System.getenv("SystemDrive");
		folderPath += File.separator + "webtis" + File.separator + "conf" + File.separator;
		setTextContent(readMsgCollectorFile(folderPath, "collection.tis"));
	}

	public void loadContent() {
		setTextContent("<p><h1>Analyzing the data please wait....</h1></p>");
		Map<String, Double> names = Collections.synchronizedMap(new HashMap<String, Double>());
		Map<Double, String> amounts = Collections.synchronizedMap(new HashMap<Double, String>());
		
		
		UserDtls user = Login.getUserLogin().getUserDtls();
		
		String[] greets = {"Hi","Hello","Good day"};
		
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
				  str += Currency.formatAmount(amnt) +"</strong></li>";
			  }
			  text += "<p>As of <strong>"+ DateUtils.getCurrentDateMMMMDDYYYY() +"</strong> the total collected amount is <strong>Php"+ Currency.formatAmount(amount) +"</strong></p>";
			  text += "<p>Below are the details collected per month.</p>";
			  text += "</br>";
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
			  List<String> data = Collections.synchronizedList(new ArrayList<String>());
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

	public LineChartModel getLineModel() {
		return lineModel;
	}

	public void setLineModel(LineChartModel lineModel) {
		this.lineModel = lineModel;
	}

	public BarChartModel getStackedGroupBarModel() {
		return stackedGroupBarModel;
	}

	public void setStackedGroupBarModel(BarChartModel stackedGroupBarModel) {
		this.stackedGroupBarModel = stackedGroupBarModel;
	}

	public HorizontalBarChartModel getHbarModelCollector() {
		return hbarModelCollector;
	}

	public void setHbarModelCollector(HorizontalBarChartModel hbarModelCollector) {
		this.hbarModelCollector = hbarModelCollector;
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

	public HorizontalBarChartModel getHbarModel() {
		return hbarModel;
	}

	public void setHbarModel(HorizontalBarChartModel hbarModel) {
		this.hbarModel = hbarModel;
	}
	public List getCollectors() {
		collectors = new ArrayList<>();
		
		for(Collector c : Collector.retrieve("", new String[0])) {
			if(c.getId()==0) {
				collectors.add(new SelectItem(0, "Select Collector"));
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

	public LineChartModel getLineModelPerCollector() {
		return lineModelPerCollector;
	}

	public void setLineModelPerCollector(LineChartModel lineModelPerCollector) {
		this.lineModelPerCollector = lineModelPerCollector;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
}
