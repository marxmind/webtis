package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import javax.annotation.PostConstruct;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

import com.italia.municipality.lakesebu.controller.MOETypes;

/**
 * 
 * @author mark italia
 * @since 02/16/2017
 * @version 1.0
 *
 */
@ManagedBean(name="moeBean", eager=true)
@ViewScoped
public class MOEBean  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 423535353264621L;

	
	private LineChartModel lineModel1;
    private LineChartModel lineModel2;
   
    
    @PostConstruct
    public void init() {
        createLineModels();
    }
 
    public LineChartModel getLineModel1() {
        return lineModel1;
    }
 
    public LineChartModel getLineModel2() {
        return lineModel2;
    }
     
    private void createLineModels() {
    	Axis yAxis = null;
    	
        /*lineModel1 = initLinearModel();
        lineModel1.setTitle("Linear Chart");
        lineModel1.setLegendPosition("e");
        yAxis = lineModel1.getAxis(AxisType.Y);
        yAxis.setMin(1);
        yAxis.setMax(250);*/
         
    	/*String title = "Product Statistic Report";
    	String rangeTitle = "Day";
    	if(1==getMonitoring().getRangeId()){//per day
    		title = "Per Day " + title;
    		rangeTitle = "Day";
    	}else if(2==getMonitoring().getRangeId()){//per month
    		title = "Per Month " + title;
    		rangeTitle = "Month";
    	}else if(3==getMonitoring().getRangeId()){//per year
    		title = "Per Year " + title;
    		rangeTitle = "Year";
    	}*/
    	
        lineModel2 = initCategoryModel();
        lineModel2.setTitle("Municipal Hospital MOE Report for the Month of January");
        lineModel2.setLegendPosition("e");
        lineModel2.setShowPointLabels(true);
        lineModel2.getAxes().put(AxisType.X, new CategoryAxis("Day"));
        yAxis = lineModel2.getAxis(AxisType.Y);
        yAxis.setLabel("Amount in Millions");
        yAxis.setMin(0);
        yAxis.setMax(20000000);
    }
     
    private LineChartModel initLinearModel() {
        LineChartModel model = new LineChartModel();
        
        //String dates[] = {"01-01-2017","01-02-2017","01-03-2017","01-04-2017","01-05-2017","01-06-2017"};
        for(MOETypes type : MOETypes.retrieve("SELECT * FROM moetypes", new String[0])){
        
        LineChartSeries series = new LineChartSeries();
    	series.setLabel(type.getNameType());
        
    	for(int i=1; i<=12;i++){
    		
    		if(i<10){
    			series.set("01-0"+i+"-2017", i * 1032320.00);
    		}else{
    			series.set("01-"+i+"-2017", i * 1032320.00);
    		}
    	}
    	model.addSeries(series);
    	
        }
    	
        /*System.out.println("monitoring transaction data " + monitoring.getTransactionData().size());
        System.out.println("monitoring product data " + monitoring.getProductData().size());
        if(monitoring.getTransactionData()!=null && monitoring.getTransactionData().size()>0){
        	
        	for(Product prod : getMonitoring().getProductData().values()){
        		
        		LineChartSeries series = new LineChartSeries();
            	series.setLabel(prod.getProductProperties().getProductname());
        		
            	for(String date : monitoring.getTransactionData().keySet()){
            		DeliveryItemTrans tran = monitoring.getTransactionData().get(date).get(prod.getProdid());
            		//System.out.println("date: " + date + "  " + tran.getQuantity());
            		try{
            			int day = Integer.valueOf(date.split("-")[2]);
            			series.set(day, tran.getQuantity());
            			
            		}catch(Exception  e){}
            	}
        		model.addSeries(series);
        	}
        
        }*/
        
         
        return model;
    }
     
    private LineChartModel initCategoryModel() {
        
    	LineChartModel model = new LineChartModel();
        
    	
    	for(MOETypes type : MOETypes.retrieve("SELECT * FROM moetypes limit 5", new String[0])){
            
    		ChartSeries series = new ChartSeries();
        	series.setLabel(type.getNameType());
            
        	for(int i=1; i<=12;i++){
        		
        		if(i<10){
        			series.set("01-0"+i+"-2017", i * Math.random() * 1032320.00);
        		}else{
        			series.set("01-"+i+"-2017", i * Math.random() * 1032320.00);
        		}
        	}
        	series.set("Legend", 0);
        	model.addSeries(series);
        	
            }
    	
       /* if(monitoring.getTransactionData()!=null && monitoring.getTransactionData().size()>0){
        	
        	for(Product prod : getMonitoring().getProductData().values()){
        		
        		ChartSeries series = new ChartSeries();
        		series.setLabel(prod.getProductProperties().getProductname());
        		
            	for(String date : monitoring.getTransactionData().keySet()){
            		DeliveryItemTrans tran = monitoring.getTransactionData().get(date).get(prod.getProdid());
            		try{
            			series.set(date, tran.getQuantity());
            			
            		}catch(Exception  e){}
            	}
        		model.addSeries(series);
        	}
        
        }*/
        
         
        return model;
    }
 
}
