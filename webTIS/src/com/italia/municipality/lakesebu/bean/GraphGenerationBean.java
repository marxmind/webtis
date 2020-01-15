package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;

import com.italia.municipality.lakesebu.controller.BankAccounts;
import com.italia.municipality.lakesebu.controller.Budget;
import com.italia.municipality.lakesebu.controller.Chequedtls;
import com.italia.municipality.lakesebu.controller.IBudget;
import com.italia.municipality.lakesebu.enm.Months;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 02/22/2017
 * @version 1.0
 *
 */
@ManagedBean(name="genBean", eager=true)
@ViewScoped
public class GraphGenerationBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 165475586L;

	
	private int rangeId;
	private List range;
	private int monthId;
	private List months;
	private int yearFromId;
	private List yearFrom;
	private int yearToId;
	private List yearTo;
	
	private String dateFrom;
	private String dateTo;
	
	private Map<String, Map<String, Chequedtls>> accounts = Collections.synchronizedMap(new HashMap<String, Map<String, Chequedtls>>());
	private Map<Integer, BankAccounts> banks = Collections.synchronizedMap(new HashMap<Integer, BankAccounts>());
	
	private BarChartModel barModel;
	
	private Map<Integer, IBudget> budgets = Collections.synchronizedMap(new HashMap<Integer, IBudget>());
	
	@PostConstruct
	public void init(){
		
		bankAccounts();//load accounts
		
		if(getRangeId()==0){
			loadTemporary(); // load temporary data
		}else{
			loadSearch();
		}
		
		
		
	}
	
	private void loadSearch(){
		
		if(getRangeId()==1){//day
			if(getMonthId()<=9){
				setDateFrom(getYearFromId() + "-0"+getMonthId()+ "-01");
				setDateTo(getYearToId() + "-0"+getMonthId()+ "-31");
			}else{
				setDateFrom(getYearFromId() + "-"+getMonthId()+ "-01");
				setDateTo(getYearToId() + "-"+getMonthId()+ "-31");
			}
		}else if(getRangeId()==2){//month
				setDateFrom(getYearFromId() + "-01-01");
				int year = getYearToId() + 1;
				setDateTo(year + "-01-31");
		}else if(getRangeId()==3){//year
				setDateFrom(getYearFromId() + "-01-01");
				setDateTo(getYearToId() + "-12-31");
		}
		
		String sql = "SELECT * FROM tbl_chequedtls WHERE date_disbursement>=? AND date_disbursement<=?";
		String params[] = new String[2];
		params[0] = getDateFrom();
		params[1] = getDateTo();
		
		accounts = Collections.synchronizedMap(new HashMap<String, Map<String, Chequedtls>>());
		Map<String, Chequedtls> dtls = Collections.synchronizedMap(new HashMap<String, Chequedtls>());
		for(Chequedtls chk : Chequedtls.retrieve(sql, params)){
			
			String date = "";
			
			if(getRangeId()==1){//day
				date = chk.getDate_disbursement();
			}else if(getRangeId()==2){//month
				int bankId = Integer.valueOf(chk.getAccntNumber());
				IBudget bud = getBudgets().get(bankId);
				date = chk.getDate_disbursement().split("-")[1];
				int day = Integer.valueOf(chk.getDate_disbursement().split("-")[2]);
				int tmpMonth = Integer.valueOf(date);
				int year = Integer.valueOf(chk.getDate_disbursement().split("-")[0]);
				int currentYear =  Integer.valueOf(DateUtils.getCurrentDateYYYYMMDD().split("-")[0]);
				if(day<bud.getCycleDate()){
					System.out.println(" before Date " + chk.getDate_disbursement() + " month  " + date + " day " + day);
					boolean isJan = false;
					
					if(year == currentYear){
						if(tmpMonth==1){isJan = true;}
					}
					
					tmpMonth -= 1;
					if(tmpMonth==0) {tmpMonth=12;}
					date = tmpMonth<10? "0"+tmpMonth : tmpMonth+"";
					if(isJan){date="0";}
					System.out.println(" after Date " + chk.getDate_disbursement() + " month  " + date + " day " + day);
				}
				
				
			}else if(getRangeId()==3){//year
				date = chk.getDate_disbursement().split("-")[0];
			}
			
			if(accounts!=null && accounts.size()>0){
				
				if(accounts.containsKey(date)){
					String accountNo = chk.getAccntNumber();
					if(accounts.get(date).containsKey(accountNo)){
						
						double amount = Double.valueOf(chk.getAmount().replace(",", ""));
						amount += Double.valueOf(accounts.get(date).get(accountNo).getAmount().replace(",", ""));
						accounts.get(date).get(accountNo).setAmount(amount+"");
					}else{
						accounts.get(date).put(accountNo, chk);
					}
					
				}else{
					dtls = Collections.synchronizedMap(new HashMap<String, Chequedtls>());
					dtls.put(chk.getAccntNumber(), chk);
					accounts.put(date, dtls);
				}
				
			}else{
				dtls = Collections.synchronizedMap(new HashMap<String, Chequedtls>());
				dtls.put(chk.getAccntNumber(), chk);
				accounts.put(date, dtls);
			}
			
		}
		
		Map<String, Map<String, Chequedtls>> treesort = new TreeMap<String, Map<String, Chequedtls>>(accounts);
		setAccounts(treesort);
		
		createModel();
	}
	
	private void createModel(){
		
		barModel = initBarModel();
		String title = "";
		
		if(getRangeId()==1){//day
			title = " for " + Months.getMonthName(getMonthId()) + " " + getYearFromId();
			barModel.setShowPointLabels(false);
		}else if(getRangeId()==2){//month
			title = " for " + getYearFromId();
			barModel.setShowPointLabels(true);
		}else if(getRangeId()==3){//year
			if(getYearFromId()==getYearToId()){
				title = " for " + getYearFromId();
			}else{
				title = " for " + getYearFromId() + " to " + getYearToId();
			}
			barModel.setShowPointLabels(true);
		}
		
		 
		barModel.setTitle("Check Disbursement Chart Report" + title);
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        //barModel.setLegendCols(0);
        //barModel.setMouseoverHighlight(false);
        barModel.setLegendPlacement(LegendPlacement.OUTSIDE);
        barModel.setZoom(true);
        //barModel.setShadow(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
        if(getRangeId()==1){
        	xAxis.setLabel("Day");
        	yAxis.setMin(1);
            yAxis.setMax(18000000);
        }else if(getRangeId()==2){
        	xAxis.setLabel("Month");
        	yAxis.setMin(1);
            yAxis.setMax(30000000);
        }else if(getRangeId()==3){
        	xAxis.setLabel("Year");
        	yAxis.setMin(1);
            yAxis.setMax(300000000);
        }
        
        xAxis.setTickFormat("Php%'d");
        
        
        yAxis.setTickFormat("Php%'d");
        yAxis.setLabel("Amount");
        
		
	}
	
	private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
 
        /*ChartSeries boys = new ChartSeries();
        boys.setLabel("Boys");
        boys.set("2004", 120);
        boys.set("2005", 100);
        boys.set("2006", 44);
        boys.set("2007", 150);
        boys.set("2008", 25);
 
        ChartSeries girls = new ChartSeries();
        girls.setLabel("Girls");
        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 110);
        girls.set("2007", 135);
        girls.set("2008", 120);
 
        model.addSeries(boys);
        model.addSeries(girls);*/
         
        for(BankAccounts bank : getBanks().values()){
        	
        ChartSeries account = new ChartSeries();
        
        account.setLabel(bank.getBankAccntName() + " " + bank.getBankAccntBranch());
        
        for(String date : getAccounts().keySet()){
        	
        	if("0".equalsIgnoreCase(date) || "13".equalsIgnoreCase(date)){ // removing previous year
        		//do nothing
        	}else{
        	Chequedtls chk = null;
        	
        	try{chk = getAccounts().get(date).get(bank.getBankId()+"");}catch(Exception e){}
        	if(chk!=null){	
        		if(getRangeId()==1){//Day
        			date = date.split("-")[2];
        			account.set(date, Double.valueOf(chk.getAmount().replace(",", "")));
        		}else if(getRangeId()==3){//year
        			account.set(date, Double.valueOf(chk.getAmount().replace(",", "")));
        		}else if(getRangeId()==2){//month
        			int dte = Integer.valueOf(date);
        			date = Months.getMonthName(dte);
        			account.set(date, Double.valueOf(chk.getAmount().replace(",", "")));
        		}
        	}else{
        		if(getRangeId()==1){//Day
        			date = date.split("-")[2];
        			account.set(date, 0);
        		}else if(getRangeId()==2){//month
        			int dte = Integer.valueOf(date);
        			date = Months.getMonthName(dte);
        			account.set(date, 0);
        		}else{	
        			account.set(date, 0);
        		}
        	}
        	
        	}
        }
        
        model.addSeries(account);
        
        
        }
        return model;
    }
	
	private void bankAccounts(){
		banks = Collections.synchronizedMap(new HashMap<Integer, BankAccounts>());
		for(BankAccounts acc : BankAccounts.retrieve("SELECT * FROM tbl_bankaccounts", new String[0])){
			banks.put(acc.getBankId(), acc);
		}
		
		budgets = Collections.synchronizedMap(new HashMap<Integer, IBudget>());
		for(IBudget bud : Budget.retrieveBudget("SELECT * FROM budget", new String[0])){
			budgets.put(bud.getAccounts().getBankId(), bud);
		}
	}
	
	private Chequedtls[] checks(){
		
		Chequedtls[] chks = new Chequedtls[5];
		
		Chequedtls chk = new Chequedtls();
		chk.setAccntNumber("1");
		chk.setDate_disbursement(DateUtils.getCurrentDateYYYYMMDD());
		chk.setAmount(Currency.formatAmount("10000000"));
		chks[0] = chk;
		
		chk = new Chequedtls();
		chk.setAccntNumber("2");
		chk.setDate_disbursement(DateUtils.getCurrentDateYYYYMMDD());
		chk.setAmount(Currency.formatAmount("27000000"));
		chks[1] = chk;
		
		chk = new Chequedtls();
		chk.setAccntNumber("3");
		chk.setDate_disbursement(DateUtils.getCurrentDateYYYYMMDD());
		chk.setAmount(Currency.formatAmount("15000000"));
		chks[2] = chk;
		
		chk = new Chequedtls();
		chk.setAccntNumber("4");
		chk.setDate_disbursement(DateUtils.getCurrentDateYYYYMMDD());
		chk.setAmount(Currency.formatAmount("5000000"));
		chks[3] = chk;
		
		chk = new Chequedtls();
		chk.setAccntNumber("5");
		chk.setDate_disbursement(DateUtils.getCurrentDateYYYYMMDD());
		chk.setAmount(Currency.formatAmount("20000000"));
		chks[4] = chk;
		
		return chks;
	}
	
	
	private void loadTemporary(){
		accounts = Collections.synchronizedMap(new HashMap<String, Map<String, Chequedtls>>());
		Map<String, Chequedtls> dtls = Collections.synchronizedMap(new HashMap<String, Chequedtls>());
		
		for(int month=1; month<=12; month++){
			for(BankAccounts acc : getBanks().values()){
				
				
				if(accounts!=null && accounts.size()>0){
					
					if(accounts.containsKey(month+"")){
						
						if(accounts.get(month+"").containsKey(acc.getBankId()+"")){
							
						}else{
							Chequedtls chk = new Chequedtls();
							int i = acc.getBankId()-1;
							chk = checks()[i];
							accounts.get(month+"").put(acc.getBankId()+"", chk);
						}
						
					}else{
						Chequedtls chk = new Chequedtls();
						int i = acc.getBankId()-1;
						chk = checks()[i];
						dtls = Collections.synchronizedMap(new HashMap<String, Chequedtls>());
						dtls.put(acc.getBankId()+"", chk);
						accounts.put(month+"", dtls);
					}
					
				}else{
					Chequedtls chk = new Chequedtls();
					int i = acc.getBankId()-1;
					chk = checks()[i];
					dtls = Collections.synchronizedMap(new HashMap<String, Chequedtls>());
					dtls.put(acc.getBankId()+"", chk);
					accounts.put(month+"", dtls);
				}
				
				
			}
		}
		Map<String, Map<String, Chequedtls>> treesort = new TreeMap<String, Map<String, Chequedtls>>(accounts);
		setAccounts(treesort);
		setRangeId(2);
		createTempModel();
		
	}
	
	private void createTempModel(){
		barModel = initBarModel();
		
		barModel.setShowPointLabels(true);
		barModel.setTitle("Example Check Disbursement Chart Report");
        barModel.setLegendPosition("ne");
        barModel.setAnimate(true);
        barModel.setLegendPlacement(LegendPlacement.OUTSIDE);
        barModel.setZoom(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X); 
        Axis yAxis = barModel.getAxis(AxisType.Y);
        
       
       xAxis.setLabel("Month");
       yAxis.setMin(0);
       yAxis.setMax(30000000);
       
        
        xAxis.setTickFormat("Php%'d");
        
        
        yAxis.setTickFormat("Php%'d");
        yAxis.setLabel("Amount");
	}
	
	public int getRangeId() {
		/*if(rangeId==0){
			rangeId = 2;
		}*/
		return rangeId;
	}
	public void setRangeId(int rangeId) {
		this.rangeId = rangeId;
	}
	public List getRange() {
		
		range = new ArrayList<>();
		range.add(new SelectItem(1, "DAY"));
		range.add(new SelectItem(2, "MONTH"));
		range.add(new SelectItem(3, "YEAR"));
		
		return range;
	}
	public void setRange(List range) {
		this.range = range;
	}
	public int getMonthId() {
		if(monthId==0){
			monthId = DateUtils.getCurrentMonth();
		}
		return monthId;
	}
	public void setMonthId(int monthId) {
		this.monthId = monthId;
	}
	public List getMonths() {
		
		months = new ArrayList<>();
		for(Months m : Months.values()){
			months.add(new SelectItem(m.getId(), m.getName()));
		}
		
		return months;
	}
	public void setMonths(List months) {
		this.months = months;
	}
	public int getYearFromId() {
		if(yearFromId==0){
			yearFromId = DateUtils.getCurrentYear();
		}
		return yearFromId;
	}
	public void setYearFromId(int yearFromId) {
		this.yearFromId = yearFromId;
	}
	public List getYearFrom() {
		yearFrom = new ArrayList<>();
		for(int year = 2016; year<=DateUtils.getCurrentYear(); year++){
			yearFrom.add(new SelectItem(year, year+""));
		}
		return yearFrom;
	}
	public void setYearFrom(List yearFrom) {
		this.yearFrom = yearFrom;
	}
	public int getYearToId() {
		if(yearToId==0){
			yearToId = DateUtils.getCurrentYear();
		}
		return yearToId;
	}
	public void setYearToId(int yearToId) {
		this.yearToId = yearToId;
	}
	public List getYearTo() {
		
		yearTo = new ArrayList<>();
		for(int year = 2016; year<=DateUtils.getCurrentYear(); year++){
			yearTo.add(new SelectItem(year, year+""));
		}
		
		return yearTo;
	}
	public void setYearTo(List yearTo) {
		this.yearTo = yearTo;
	}
	public Map<String, Map<String, Chequedtls>> getAccounts() {
		return accounts;
	}
	public void setAccounts(Map<String, Map<String, Chequedtls>> accounts) {
		this.accounts = accounts;
	}
	public Map<Integer, BankAccounts> getBanks() {
		return banks;
	}
	public void setBanks(Map<Integer, BankAccounts> banks) {
		this.banks = banks;
	}

	public String getDateFrom() {
		if(dateFrom==null){
			dateFrom = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		if(dateTo==null){
			dateTo = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public BarChartModel getBarModel() {
		return barModel;
	}

	public void setBarModel(BarChartModel barModel) {
		this.barModel = barModel;
	}

	public Map<Integer, IBudget> getBudgets() {
		return budgets;
	}

	public void setBudgets(Map<Integer, IBudget> budgets) {
		this.budgets = budgets;
	}
	
}
