package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;

import com.italia.municipality.lakesebu.controller.CollectionInfo;
import com.italia.municipality.lakesebu.controller.Collector;
import com.italia.municipality.lakesebu.enm.FormType;
import com.italia.municipality.lakesebu.enm.GraphColor;
import com.italia.municipality.lakesebu.enm.GraphColorWithBorder;
import com.italia.municipality.lakesebu.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 11/08/2021
 *
 */
@Named
@RequestScoped
public class GraphIssuanceBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1435756756423L;
	
	@Setter @Getter private LineChartModel lineModel;
	@Setter @Getter private List years;
	@Setter @Getter private List selectedYear;
	@Setter @Getter private List collectors;
	@Setter @Getter private List selectedCollector;
	@Setter @Getter private Map<Integer, Collector> collectorMap;
	@Setter @Getter private List forms;
	@Setter @Getter private int selectedForm;
	@Setter @Getter private BarChartModel barModel2;
	
	@PostConstruct
	public void init() {
		collectorMap = new LinkedHashMap<Integer, Collector>();
		
		loadDummyData();
		selectedYear = new ArrayList<>();
		
		years = new ArrayList<>();
		for(int year=2019; year<=DateUtils.getCurrentYear(); year++) {
			years.add(new SelectItem(year, year+""));
		}
		selectedCollector = new ArrayList<>();
		collectors = new ArrayList<>();
		for(Collector col : Collector.retrieve(" AND cl.isid!=0 AND cl.isid!=25 ORDER BY cl.departmentid, cl.collectorname", new String[0])) {
			collectors.add(new SelectItem(col.getId(), col.getName()));
			collectorMap.put(col.getId(), col);
		}
		
		selectedForm = 0;
		forms = new ArrayList<>();
		forms.add(new SelectItem(0, "All Forms"));
		for(FormType f : FormType.values()) {
			forms.add(new SelectItem(f.getId(), f.getDescription()));
		}
		
		dummyBarModel2();
		
	}
	
	public void dummyBarModel2() {
        barModel2 = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("My First Dataset");
        barDataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        barDataSet.setBorderColor("rgb(255, 99, 132)");
        barDataSet.setBorderWidth(1);
        List<Number> values = new ArrayList<>();
        values.add(65);
        values.add(59);
        values.add(80);
        values.add(81);
        values.add(56);
        values.add(55);
        values.add(40);
        barDataSet.setData(values);

        BarChartDataSet barDataSet2 = new BarChartDataSet();
        barDataSet2.setLabel("My Second Dataset");
        barDataSet2.setBackgroundColor("rgba(255, 159, 64, 0.2)");
        barDataSet2.setBorderColor("rgb(255, 159, 64)");
        barDataSet2.setBorderWidth(1);
        List<Number> values2 = new ArrayList<>();
        values2.add(85);
        values2.add(69);
        values2.add(20);
        values2.add(51);
        values2.add(76);
        values2.add(75);
        values2.add(10);
        barDataSet2.setData(values2);
        

        data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet2);

        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        data.setLabels(labels);
        barModel2.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart");
        options.setTitle(title);
        
        
        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("bottom");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(12);
        legend.setLabels(legendLabels);
        options.setLegend(legend);
        
		/*
		 * Tooltip tooltip = new Tooltip(); tooltip.setEnabled(true);
		 * tooltip.setMode("index"); tooltip.setIntersect(false);
		 * options.setTooltip(tooltip);
		 */
        
        barModel2.setOptions(options);
        //barModel2.setExtender("addValueOnGraph");
    }
	
	public void loadCurrentBar() {
        barModel2 = new BarChartModel();
        ChartData data = new ChartData();
        
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel(DateUtils.getCurrentYear()+" Collections");
        barDataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        barDataSet.setBorderColor("rgb(255, 99, 132)");
        barDataSet.setBorderWidth(1);
        List<Number> values = new ArrayList<>();
        Map<Integer, Double> mapData = CollectionInfo.retrieveYear(DateUtils.getCurrentYear());
        List<String> labels = new ArrayList<>();
        
        for(int key : mapData.keySet()) {
        	labels.add(DateUtils.getMonthName(key));
        	values.add(mapData.get(key));
        }
        
       
        
        barDataSet.setData(values);
        data.addChartDataSet(barDataSet);

        
        data.setLabels(labels);
        barModel2.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Year Collection Data");
        options.setTitle(title);
        
        
        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("bottom");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(12);
        legend.setLabels(legendLabels);
        options.setLegend(legend);
        
		/*
		 * Tooltip tooltip = new Tooltip(); tooltip.setEnabled(true);
		 * tooltip.setMode("index"); tooltip.setIntersect(false);
		 * options.setTooltip(tooltip);
		 */
        
        barModel2.setOptions(options);
        //barModel2.setExtender("addValueOnGraph");
    }
	
	public void loadDynamicBar() {
		
		 barModel2 = new BarChartModel();
	     ChartData data = new ChartData();
	        
	    int color = 1;
	    Map<Integer, Map<Integer, Double>> mapData = CollectionInfo.graph(getSelectedYear());
	    boolean isMorethanOneYearSelected=false;
	    if(getSelectedYear()!=null && getSelectedYear().size()>1) {
	    	isMorethanOneYearSelected=true;
	    }
	    for(int year : mapData.keySet()) {
	    	
	    	
	    	BarChartDataSet barDataSet = new BarChartDataSet();
		    barDataSet.setLabel(year+"");
		    barDataSet.setBackgroundColor(GraphColorWithBorder.valueId(color));
		    barDataSet.setBorderColor(GraphColor.valueId(color));
		    barDataSet.setBorderWidth(1);
	    	
		    color++;
		    
		     List<Number> values = new ArrayList<>();
			
	        for(int month=1; month<=12; month++) {
	        	if(mapData!=null && mapData.get(year).containsKey(month)) {
	        		values.add(mapData.get(year).get(month));
	        	}else {
	        		values.add(0);
	        	}
		    }
	        
	        barDataSet.setData(values);
	        
	        
	        
	        data.addChartDataSet(barDataSet);
	        
	        
	    }
	    List<String> labels = new ArrayList<>();
	    for(int month=1; month<=12; month++) {
	    	labels.add(DateUtils.getMonthName(month));
	    }
	    data.setLabels(labels);
	    barModel2.setData(data);
	    
	    
	  //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Year Collection Data"+ (isMorethanOneYearSelected==true? "" : " " +  getSelectedYear().toString()));
        options.setTitle(title);
        
        
        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("bottom");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(12);
        legend.setLabels(legendLabels);
        options.setLegend(legend);
        
		/*
		 * Tooltip tooltip = new Tooltip(); tooltip.setEnabled(true);
		 * tooltip.setMode("index"); tooltip.setIntersect(false);
		 * options.setTooltip(tooltip);
		 */
        
        barModel2.setOptions(options);
	}
	
	public void loadDynamicBar2() {
		barModel2 = new BarChartModel();
	    ChartData data = new ChartData();
	    int color = 1;
	    Map<Integer, Map<Integer, Map<Integer, Double>>> mapData = CollectionInfo.graph(getSelectedYear(),getSelectedCollector(),getSelectedForm());
	    
	    boolean isMorethanOneYearSelected=false;
	    if(getSelectedYear()!=null && getSelectedYear().size()>1) {
	    	isMorethanOneYearSelected=true;
	    }
	    
	    for(int year : mapData.keySet()) {
	    	
	        
	        for(int col : mapData.get(year).keySet()) {
	        	
	        	BarChartDataSet barDataSet = new BarChartDataSet();
	        	List<Number> values = new ArrayList<>();
	        	
	 	        for(int month=1; month<=12; month++) {
		        	if(mapData!=null && mapData.get(year).containsKey(col)) {
		        		if(mapData.get(year).get(col).containsKey(month)) {
		        			values.add(mapData.get(year).get(col).get(month));
		        		}else {
		        			values.add(0);
		        		}
		        	}else {
		        		values.add(0);
		        	}
			    }
	 	        
	 	        barDataSet.setData(values);
	 	       barDataSet.setLabel(getCollectorMap().get(col).getName() +  (isMorethanOneYearSelected==true?  "("+ year +")" : ""));
			    barDataSet.setBackgroundColor(GraphColorWithBorder.valueId(color));
			    barDataSet.setBorderColor(GraphColor.valueId(color));
			    barDataSet.setBorderWidth(1);
		        data.addChartDataSet(barDataSet);
	        	color++;
	        }
	        
	    }
	    List<String> labels = new ArrayList<>();
	    for(int month=1; month<=12; month++) {
	    	labels.add(DateUtils.getMonthName(month));
	    }
	    data.setLabels(labels);
	    barModel2.setData(data);
	    
        
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Year Collection Data" + (isMorethanOneYearSelected==true? "" : " " +  getSelectedYear().toString()) + " " + (getSelectedForm()==0? "" : FormType.nameId(getSelectedForm())));
        options.setTitle(title);
        
        
        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("bottom");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(12);
        legend.setLabels(legendLabels);
        options.setLegend(legend);
        
		/*
		 * Tooltip tooltip = new Tooltip(); tooltip.setEnabled(true);
		 * tooltip.setMode("index"); tooltip.setIntersect(false);
		 * options.setTooltip(tooltip);
		 */
        
        barModel2.setOptions(options);
        
	}
	
	public void loadGraph() {
		
		loadCurrentData();
		loadCurrentBar();
		PrimeFaces pf = PrimeFaces.current();
		pf.executeScript("PF('dlgGraph').show(1000);");
	}
	
	
	public void loadCurrentData() {
		
		
		lineModel = new LineChartModel();
       ChartData data = new ChartData();
       List<String> labels = new ArrayList<>();
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        
        Map<Integer, Double> mapData = CollectionInfo.retrieveYear(DateUtils.getCurrentYear());
        
        for(int key : mapData.keySet()) {
        	labels.add(DateUtils.getMonthName(key));
        	values.add(mapData.get(key));
        }
        
        
        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel(DateUtils.getCurrentYear()+" Collections");
        dataSet.setBorderColor("rgb(75, 192, 192)");
        //dataSet.setTension(0.1);
        data.addChartDataSet(dataSet);

        
        
        data.setLabels(labels);

        //Options
        LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Year Collection Data");
        options.setTitle(title);

        lineModel.setOptions(options);
        lineModel.setData(data);
	
	
}
	
	
	
	public void selectedGraph() {
		
		if(getSelectedYear()!=null && getSelectedYear().size()>0) {
			if(getSelectedCollector().size()>0) {
				loadDynamic2();
				loadDynamicBar2();
			}else {
				loadDynamic();
				loadDynamicBar();
			}
			
		}
	}
	
	public void loadDynamic() {
		lineModel = new LineChartModel();
		ChartData data = new ChartData();
	    int color = 1;
	    Map<Integer, Map<Integer, Double>> mapData = CollectionInfo.graph(getSelectedYear());
	    boolean isMorethanOneYearSelected=false;
	    if(getSelectedYear()!=null && getSelectedYear().size()>1) {
	    	isMorethanOneYearSelected=true;
	    }
	    for(int year : mapData.keySet()) {
	    	
	    	
	        LineChartDataSet dataSet = new LineChartDataSet();
	        List<Object> values = new ArrayList<>();
			
	        for(int month=1; month<=12; month++) {
	        	if(mapData!=null && mapData.get(year).containsKey(month)) {
	        		values.add(mapData.get(year).get(month));
	        	}else {
	        		values.add(0);
	        	}
		    }
	        
	        dataSet.setData(values);
	        dataSet.setFill(false);
	        dataSet.setLabel(year+"");
	        dataSet.setBorderColor(GraphColor.valueId(color++));
	        
	        data.addChartDataSet(dataSet);
	        
	        
	    }
	    List<String> labels = new ArrayList<>();
	    for(int month=1; month<=12; month++) {
	    	labels.add(DateUtils.getMonthName(month));
	    }
	    data.setLabels(labels);
	    lineModel.setData(data);
	    
	    
	    LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Year Collection Data"+ (isMorethanOneYearSelected==true? "" : " " +  getSelectedYear().toString()));
        options.setTitle(title);

        lineModel.setOptions(options);
        //lineModel.setExtender("addValueOnGraph");
        
	}
	
	public void loadDynamic2() {
		lineModel = new LineChartModel();
		ChartData data = new ChartData();
	    int color = 1;
	    Map<Integer, Map<Integer, Map<Integer, Double>>> mapData = CollectionInfo.graph(getSelectedYear(),getSelectedCollector(),getSelectedForm());
	    
	    boolean isMorethanOneYearSelected=false;
	    if(getSelectedYear()!=null && getSelectedYear().size()>1) {
	    	isMorethanOneYearSelected=true;
	    }
	    
	    for(int year : mapData.keySet()) {
	    	
	        
	        for(int col : mapData.get(year).keySet()) {
	        	
	        	 LineChartDataSet dataSet = new LineChartDataSet();
	 	         List<Object> values = new ArrayList<>();
	        	
	 	        for(int month=1; month<=12; month++) {
		        	if(mapData!=null && mapData.get(year).containsKey(col)) {
		        		if(mapData.get(year).get(col).containsKey(month)) {
		        			values.add(mapData.get(year).get(col).get(month));
		        		}else {
		        			values.add(0);
		        		}
		        	}else {
		        		values.add(0);
		        	}
			    }
	 	        
	 	        dataSet.setData(values);
		        dataSet.setFill(false);
		        dataSet.setLabel(getCollectorMap().get(col).getName() +  (isMorethanOneYearSelected==true?  "("+ year +")" : ""));
		        dataSet.setBorderColor(GraphColor.valueId(color++));
		        data.addChartDataSet(dataSet);
	        	
	        }
	        
	    }
	    List<String> labels = new ArrayList<>();
	    for(int month=1; month<=12; month++) {
	    	labels.add(DateUtils.getMonthName(month));
	    }
	    data.setLabels(labels);
	    lineModel.setData(data);
	    
	    LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Year Collection Data" + (isMorethanOneYearSelected==true? "" : " " +  getSelectedYear().toString()) + " " + (getSelectedForm()==0? "" : FormType.nameId(getSelectedForm())));
        options.setTitle(title);

        lineModel.setOptions(options);
        
	}
	
	
	
	public void loadDummyData() {
		
        lineModel = new LineChartModel();
       ChartData data = new ChartData();

        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        values.add(65);
        values.add(59);
        values.add(80);
        values.add(81);
        values.add(56);
        values.add(55);
        values.add(40);
        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel("Please select Data");
        dataSet.setBorderColor("rgb(75, 192, 192)");
        //dataSet.setTension(0.1);
        data.addChartDataSet(dataSet);

        List<String> labels = new ArrayList<>();
        
        for(int i=1; i<=12; i++) {
        	labels.add(DateUtils.getMonthName(i));
        }
        
        data.setLabels(labels);

        //Options
        LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Year Collection Data");
        options.setTitle(title);

        lineModel.setOptions(options);
        lineModel.setData(data);
    
}
	
	public void onChange(TabChangeEvent event) {
		if("Line Graph".equalsIgnoreCase(event.getTab().getTitle())) {
			
			
			if(getSelectedYear()!=null && getSelectedYear().size()>0) {
				if(getSelectedCollector().size()>0) {
					loadDynamic2();
				}else {
					loadDynamic();
				}
				
			}
			
		}else if("Bar Graph".equalsIgnoreCase(event.getTab().getTitle())){
			
			if(getSelectedYear()!=null && getSelectedYear().size()>0) {
				if(getSelectedCollector().size()>0) {
					loadDynamicBar2();
				}else {
					loadDynamicBar();
				}
				
			}
			
		}	
		
		
		
		
	}
	
}
