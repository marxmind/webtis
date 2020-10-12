package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
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

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LegendPlacement;

import com.italia.municipality.lakesebu.controller.Chequedtls;
import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.Voucher;
import com.italia.municipality.lakesebu.enm.Months;
import com.italia.municipality.lakesebu.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 02/25/2017
 * @version 1.0
 *
 */
@ManagedBean(name="depExpBean", eager=true)
@ViewScoped
public class DepartmentExpensesBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6885646776871L;
	
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
	
	private BarChartModel barModel;
	
	private Map<String, Map<Integer, Voucher>> departments = Collections.synchronizedMap(new HashMap<String, Map<Integer, Voucher>>());
	private int departmentId;
	private List department;
	private Map<Integer, Department> departmentData = Collections.synchronizedMap(new HashMap<Integer, Department>());
	
	@PostConstruct
	public void init(){
		loadDepartment();
		if(getDepartmentId()==0){
			loadTemporary();
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
				setDateTo(getYearToId() + "-12-31");
		}else if(getRangeId()==3){//year
				setDateFrom(getYearFromId() + "-01-01");
				setDateTo(getYearToId() + "-12-31");
		}
		
		String sql = "SELECT * FROM voucher WHERE vDate>=? AND vDate<=? AND departmentid=?";
		String params[] = new String[3];
		params[0] = getDateFrom();
		params[1] = getDateTo();
		params[2] = getDepartmentId()+"";
		
		departments = Collections.synchronizedMap(new HashMap<String, Map<Integer, Voucher>>());
		Map<Integer, Voucher> vo = Collections.synchronizedMap(new HashMap<Integer, Voucher>());
		for(Voucher vc : Voucher.retrieve(sql, params)){
			
			String date = "";
			
			if(getRangeId()==1){//day
				date = vc.getDateTrans();
			}else if(getRangeId()==2){//month
				date = vc.getDateTrans().split("-")[1];
			}else if(getRangeId()==3){//year
				date = vc.getDateTrans().split("-")[0];
			}
			
			if(departments!=null && departments.size()>0){
				
				if(departments.containsKey(date)){
					int depId = vc.getDepartment().getDepid();
					if(departments.get(date).containsKey(depId)){
						
						double amount = vc.getAmount();
						amount += departments.get(date).get(depId).getAmount();
						departments.get(date).get(depId).setAmount(amount);
					}else{
						departments.get(date).put(depId, vc);
					}
					
				}else{
					vo = Collections.synchronizedMap(new HashMap<Integer, Voucher>());
					vo.put(vc.getDepartment().getDepid(), vc);
					departments.put(date, vo);
				}
				
			}else{
				vo = Collections.synchronizedMap(new HashMap<Integer, Voucher>());
				vo.put(vc.getDepartment().getDepid(), vc);
				departments.put(date, vo);
			}
			
		}
		
		Map<String, Map<Integer, Voucher>> treesort = new TreeMap<String, Map<Integer, Voucher>>(departments);
		setDepartments(treesort);
		
		createModel();
	}
	
	private void createModel(){
		
		barModel = initBarModel();
		String title = "";
		
		if(getRangeId()==1){//day
			title = " for " + Months.getMonthName(getMonthId()) + " " + getYearFromId();
			barModel.setShowPointLabels(true);
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
		
		 
		barModel.setTitle(getDepartmentData().get(getDepartmentId()).getDepartmentName() + " Check Request Chart Report" + title);
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
        	yAxis.setMin(0);
            yAxis.setMax(500000);
        }else if(getRangeId()==2){
        	xAxis.setLabel("Month");
        	yAxis.setMin(0);
            yAxis.setMax(2000000);
        }else if(getRangeId()==3){
        	xAxis.setLabel("Year");
        	yAxis.setMin(0);
            yAxis.setMax(10000000);
        }
        
        xAxis.setTickFormat("Php%'d");
        
        
        yAxis.setTickFormat("Php%'d");
        yAxis.setLabel("Amount");
        
		
	}
	
	private void loadTemporary(){
		departments = Collections.synchronizedMap(new HashMap<String, Map<Integer, Voucher>>());
		Map<Integer, Voucher> vo = Collections.synchronizedMap(new HashMap<Integer, Voucher>());
		for(int month=1; month<=12; month++){
			
			for(int dep=0; dep<=2; dep++){
				
			if(departments!=null && departments.size()>0){
				
				if(departments.containsKey(month+"")){
							
					if(departments.get(month+"").containsKey(dep)){
						
					}else{
						Voucher v = new Voucher();
						v = addVoucher()[dep];
						departments.get(month+"").put(v.getDepartment().getDepid(), v);
					}
					
				}else{
					
					vo = Collections.synchronizedMap(new HashMap<Integer, Voucher>());
					Voucher v = new Voucher();
					v = addVoucher()[dep];
					vo.put(v.getDepartment().getDepid(), v);
					departments.put(month+"", vo);
					
				}
				
			}else{
				
				vo = Collections.synchronizedMap(new HashMap<Integer, Voucher>());
				Voucher v = new Voucher();
				v = addVoucher()[dep];
				vo.put(v.getDepartment().getDepid(), v);
				departments.put(month+"", vo);
			}
			
			}
			
		}
		setDepartmentId(1);
		setRangeId(2);
		createTempModel();
	}
	
	private Voucher[] addVoucher(){
		Voucher[] vo = new Voucher[3];
		
		Voucher v = new Voucher();
		v.setId(1);
		Department dep = new Department();
		dep.setDepid(1);
		dep.setDepartmentName("MTO");
		v.setDepartment(dep);
		v.setAmount(10000000);
		vo[0] = v;
		
		v = new Voucher();
		v.setId(2);
		dep = new Department();
		dep.setDepid(2);
		dep.setDepartmentName("Accounting");
		v.setDepartment(dep);
		v.setAmount(20000000);
		vo[1] = v;
		
		v = new Voucher();
		v.setId(3);
		dep = new Department();
		dep.setDepid(3);
		dep.setDepartmentName("Budget");
		v.setDepartment(dep);
		v.setAmount(30000000);
		vo[2] = v;
		
		return vo;
	}
	
	private void createTempModel(){
		barModel = initBarModel();
		
		barModel.setShowPointLabels(true);
		barModel.setTitle("Example Department Check Request Chart Report");
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
	
	private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
        
        
        ChartSeries account = new ChartSeries();
        account.setLabel(getDepartmentData().get(getDepartmentId()).getDepartmentName());
        
        for(String date : getDepartments().keySet()){
        	Voucher vc = null;
        	
        	try{vc = getDepartments().get(date).get(getDepartmentId());}catch(Exception e){}
        	if(vc!=null){	
        		if(getRangeId()==1){//Day
        			date = date.split("-")[2];
        			account.set(date, vc.getAmount());
        		}else if(getRangeId()==3){//year
        			account.set(date, vc.getAmount());
        		}else if(getRangeId()==2){//month
        			int dte = Integer.valueOf(date);
        			date = Months.getMonthName(dte);
        			account.set(date, vc.getAmount());
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
        model.addSeries(account);
        
        return model;
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

	public Map<String, Map<Integer, Voucher>> getDepartments() {
		return departments;
	}

	public void setDepartments(Map<String, Map<Integer, Voucher>> departments) {
		this.departments = departments;
	}

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}
	
	private void loadDepartment(){
		departmentData = Collections.synchronizedMap(new HashMap<Integer, Department>());
		for(Department dep : Department.retrieve("SELECT * FROM department", new String[0])){
			departmentData.put(dep.getDepid(), dep);
		}
	}
	
	public List getDepartment() {
		
		department = new ArrayList<>();
		for(Department dep : getDepartmentData().values()){
			department.add(new SelectItem(dep.getDepid(), dep.getDepartmentName()));
		}
		return department;
	}

	public void setDepartment(List department) {
		this.department = department;
	}

	public Map<Integer, Department> getDepartmentData() {
		return departmentData;
	}

	public void setDepartmentData(Map<Integer, Department> departmentData) {
		this.departmentData = departmentData;
	}

}
