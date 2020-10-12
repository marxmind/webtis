package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;


/**
 * 
 * @author mark italia
 * @since 11/30/2016
 * @version 1.0
 *
 */
@Named
@ViewScoped
public class Graph56Bean implements Serializable  {
	
	/*
	
	
	private static final long serialVersionUID = 7587574416262141247L;
	private StreamedContent pieChartMonths;
	private StreamedContent pieChartLandTye;
	
	@ManagedProperty("#{form56Bean}")
	private RealTaxForm56Bean form56Bean;
	public void setForm56Bean(RealTaxForm56Bean form56Bean){
		this.form56Bean = form56Bean;
	}
	
	private List<Chequedtls> cheques = Collections.synchronizedList(new ArrayList<Chequedtls>());
	
	private Map<String, Double> mapChks = Collections.synchronizedMap(new HashMap<String,Double>());
	private Map<String, Double> mapAccs = Collections.synchronizedMap(new HashMap<String,Double>());
	
	private StreamedContent grandTotal;
	
	@PostConstruct
    public void init() {
        
        	try{
            
 
            //Chart
            jfreechart jfreechart = ChartFactory.createPieChart("Months", createMonthsDataset(), true, true, false);
            File monthschartFile = new File("dynamiMonthschart");
            ChartUtilities.saveChartAsPNG(monthschartFile, jfreechart, 600, 600);
            pieChartMonths = new DefaultStreamedContent(new FileInputStream(monthschartFile), "image/png");
            
            jfreechart = ChartFactory.createPieChart("Land Type", createAccountsDataset(), true, true, false);
            File accountchartFile = new File("dynamiLandchart");
            ChartUtilities.saveChartAsPNG(accountchartFile, jfreechart, 600, 600);
            pieChartLandTye = new DefaultStreamedContent(new FileInputStream(accountchartFile), "image/png");
            
            
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
 
	private String getMonthName(int month){
		switch(month){
			case 1: return "January";
			case 2 : return "February";
			case 3 : return "March";
			case 4 : return "April";
			case 5 : return "May";
			case 6 : return "June";
			case 7 : return "July";
			case 8 : return "August";
			case 9 : return "September";
			case 10 : return "October";
			case 11 : return "November";
			case 12 : return "December";
		}
		return "January";
	}
	
	private PieDataset createMonthsDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        for(String month : mapChks.keySet()){
        	double amnt = 0d;
        	amnt = mapChks.get(month).longValue();
        	dataset.setValue(month +" = Php"+ formatAmount(amnt+""), amnt);
        }
 
        return dataset;
    }
	
	private PieDataset createAccountsDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for(String acc : mapAccs.keySet()){
        	double amnt = 0d;
        	amnt = mapAccs.get(acc).longValue();
        	dataset.setValue(acc +" = Php"+ formatAmount(amnt+""), amnt);
        }
        return dataset;
    }

	public StreamedContent getPieChartMonths() {
		return pieChartMonths;
	}

	public void setPieChartMonths(StreamedContent pieChartMonths) {
		this.pieChartMonths = pieChartMonths;
	}

	public StreamedContent getPieChartLandTye() {
		return pieChartLandTye;
	}

	public void setPieChartLandTye(StreamedContent pieChartLandTye) {
		this.pieChartLandTye = pieChartLandTye;
	}

	public List<Chequedtls> getCheques() {
		return cheques;
	}

	public void setCheques(List<Chequedtls> cheques) {
		this.cheques = cheques;
	}

	public Map<String, Double> getMapChks() {
		return mapChks;
	}

	public void setMapChks(Map<String, Double> mapChks) {
		this.mapChks = mapChks;
	}
	
	public String formatAmount(String amount){
		double money = Double.valueOf(amount);
		NumberFormat format = NumberFormat.getCurrencyInstance();
		amount = format.format(money).replace("$", "");
		amount = amount.replace("Php", "");
		return amount;
	}
	
	public static void main(String[] args) {
		double a = 00.00;
		for(int i=1;i<10;i++){
			String str = "1,234,234.00";
			str = str.replace(",","");
			System.out.println(str);
			//str = str.split("\\.")[0];
			//System.out.println(str);
			a += Double.valueOf(str);
		}
		//System.out.println(GraphicImageBean.formatAmount(a+""));
	}

	public Map<String, Double> getMapAccs() {
		return mapAccs;
	}

	public void setMapAccs(Map<String, Double> mapAccs) {
		this.mapAccs = mapAccs;
	}

	public StreamedContent getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(StreamedContent grandTotal) {
		this.grandTotal = grandTotal;
	}
	*/
}

