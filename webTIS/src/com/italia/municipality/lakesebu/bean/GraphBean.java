package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.italia.municipality.lakesebu.controller.Chequedtls;
/**
 * 
 * @author mark italia
 * @since 11/15/2016
 * @version 1.0
 *
 */

@Named
@ViewScoped
public class GraphBean implements Serializable {

	/*
	private static final long serialVersionUID = 7587574416262141247L;
	private StreamedContent pieChartMonths;
	private StreamedContent pieChartAccouns;
	
	@ManagedProperty("#{checkBean}")
	private BankCheckBean bankCheckBean;
	public void setBankCheckBean(BankCheckBean bankCheckBean){
		this.bankCheckBean = bankCheckBean;
	}
	
	private List<Chequedtls> cheques = Collections.synchronizedList(new ArrayList<Chequedtls>());
	
	private Map<String, Double> mapChks = Collections.synchronizedMap(new HashMap<String,Double>());
	private Map<String, Double> mapAccs = Collections.synchronizedMap(new HashMap<String,Double>());
	
	private StreamedContent grandTotal;
	
	@PostConstruct
    public void init() {
        try {
        	int size = bankCheckBean.getCheques().size();
        	System.out.println("Cheques size : " + size);
        	mapChks = Collections.synchronizedMap(new HashMap<String,Double>());
        	mapAccs = Collections.synchronizedMap(new HashMap<String,Double>());
        	if(size>0){
	        	for(Chequedtls chk : bankCheckBean.getCheques()){
	        		//String tmp = chk.getDate_disbursement().split(",")[0];
	        		String month = chk.getDate_disbursement().split("-")[1];
	        		month = getMonthName(Integer.valueOf(month));
	        		double amnt = 0d;
	        		
	        		//Months
	        		if(mapChks!=null && mapChks.size()>0){
	        			if(mapChks.containsKey(month)){
	        				double tmpamnt = mapChks.get(month).doubleValue();
	        				System.out.println("no else = " + chk.getAmount());
	        				amnt = Double.valueOf(chk.getAmount().replace(",", ""));
	        				amnt = tmpamnt + amnt; 
	        				mapChks.remove(month);
	        				mapChks.put(month, amnt);
	        			}else{
	        				System.out.println("second else = " + chk.getAmount());
	        				amnt = Double.valueOf(chk.getAmount().replace(",", ""));
	        				mapChks.put(month, amnt);
	        			}
	        		}else{
	        			System.out.println("first else = " + chk.getAmount()); 
	        			amnt = Double.valueOf(chk.getAmount().replace(",", ""));
	        			mapChks.put(month, amnt);
	        		}
	        		
	        		//Accounts
	        		amnt = 0d;
	        		String acc = chk.getAccntName();
	        		if(mapAccs!=null && mapAccs.size()>0){
	        			if(mapAccs.containsKey(acc)){
	        				double tmpamnt = mapAccs.get(acc).doubleValue();
	        				System.out.println("no else = " + chk.getAmount());
	        				amnt = Double.valueOf(chk.getAmount().replace(",", ""));
	        				amnt = tmpamnt + amnt; 
	        				mapAccs.remove(acc);
	        				mapAccs.put(acc, amnt);
	        			}else{
	        				System.out.println("second else = " + chk.getAmount());
	        				amnt = Double.valueOf(chk.getAmount().replace(",", ""));
	        				mapAccs.put(acc, amnt);
	        			}
	        		}else{
	        			System.out.println("first else = " + chk.getAmount()); 
	        			amnt = Double.valueOf(chk.getAmount().replace(",", ""));
	        			mapAccs.put(acc, amnt);
	        		}
	        		
	        		
	        	}
        	
        	}
        	
        	
        	String gTotal = bankCheckBean.getGrandTotal();
        	System.out.println("Grand Total " + gTotal);
        	
            //Graphic Text
           BufferedImage bufferedImg = new BufferedImage(100, 25, BufferedImage.TYPE_INT_RGB);
           Graphics2D g2 = bufferedImg.createGraphics();
           g2.drawString("Php "+gTotal, 0, 10);
           ByteArrayOutputStream os = new ByteArrayOutputStream();
           ImageIO.write(bufferedImg, "png", os);
           grandTotal = new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/png"); 
 
            //Chart
            JFreeChart jfreechart = ChartFactory.createPieChart("Months", createMonthsDataset(), true, true, false);
            File monthschartFile = new File("dynamiMonthschart");
            ChartUtilities.saveChartAsPNG(monthschartFile, jfreechart, 600, 600);
            pieChartMonths = new DefaultStreamedContent(new FileInputStream(monthschartFile), "image/png");
            
            jfreechart = ChartFactory.createPieChart("Accounts", createAccountsDataset(), true, true, false);
            File accountchartFile = new File("dynamiAccountchart");
            ChartUtilities.saveChartAsPNG(accountchartFile, jfreechart, 600, 600);
            pieChartAccouns = new DefaultStreamedContent(new FileInputStream(accountchartFile), "image/png");
            
            
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

	public StreamedContent getPieChartAccouns() {
		return pieChartAccouns;
	}

	public void setPieChartAccouns(StreamedContent pieChartAccouns) {
		this.pieChartAccouns = pieChartAccouns;
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
