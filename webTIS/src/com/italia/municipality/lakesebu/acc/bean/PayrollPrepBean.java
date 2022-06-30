package com.italia.municipality.lakesebu.acc.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.HashedMap;
import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;

import com.italia.municipality.lakesebu.acc.controller.EmployeePayroll;
import com.italia.municipality.lakesebu.acc.controller.PayrollGroupSeries;
import com.italia.municipality.lakesebu.controller.CalendarDate;
import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.EmployeeLoan;
import com.italia.municipality.lakesebu.controller.EmployeeMain;
import com.italia.municipality.lakesebu.controller.Payroll;
import com.italia.municipality.lakesebu.controller.PayrollApprover;
import com.italia.municipality.lakesebu.controller.PayrollFund;
import com.italia.municipality.lakesebu.enm.AppConf;
import com.italia.municipality.lakesebu.enm.EmployeeType;
import com.italia.municipality.lakesebu.enm.Months;
import com.italia.municipality.lakesebu.enm.RateType;
import com.italia.municipality.lakesebu.global.GlobalVar;
import com.italia.municipality.lakesebu.reports.CalendarDays;
import com.italia.municipality.lakesebu.reports.PayrollRpt;
import com.italia.municipality.lakesebu.reports.ReportCompiler;
import com.italia.municipality.lakesebu.reports.Week;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
@Setter
@Getter
public class PayrollPrepBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4567534568761L;
	private EmployeePayroll payData;
	
	private List months;
	private List years;
	private List stats;
	//private String series;
	private Date dateTrans;
	private String searchEmployee;
	private List<EmployeeMain> employeeData;
	private EmployeeMain employeeSelected;
	private List<EmployeeLoan> loans;
	private List<EmployeePayroll> rolls;
	private String searchParam;
	//private PayrollGroupSeries groupData;
	
	private int type;
	private List types;
	
	/*private int group;
	private List groups;*/
	
	private String grossTotal;
	private String taxTotal;
	private String netTotal;
	private String eeTotal;
	private String erTotal;
	private String coopTotal;
	
	private String eeTotalLoan;
	private String erTotalLoan;
	private String coopTotalLoan;
	
	private List<EmployeePayroll> paySelected;
	private List<EmployeePayroll> paySelectedLoan;
	
	private long fundId;
	private List funds;
	
	private long fundId2;
	private List funds2;
	
	private int monthSeachId;
	private List monthSearch;
	private int yearSearchId;
	private List yearSearch;
	
	private long supervisorId;
	private List supervisors;
	private long approverId;
	private List approvers;
	private long treasurerId;
	private List treasurers;
	private long disbursingId;
	private List disbursings;
	private long accountantId;
	private List accountants;
	private String seriesPayroll;
	
	private Map<Long, PayrollApprover> officers;
	
	private List<Payroll> payrolls;
	
	@PostConstruct
	public void init() {
		loadDefault();
		loadRoll();
		
		
		types = new ArrayList<>();
		types.add(new SelectItem(0, "All"));
		for(EmployeeType t : EmployeeType.values()) {
			types.add(new SelectItem(t.getId(), t.getName()));
		}
		
		/*groups = new ArrayList<>();
		groups.add(new SelectItem(0, "All"));
		for(PayrollGroupSeries gp : PayrollGroupSeries.retrieve(" ORDER BY gp.gid DESC", new String[0])) {
			groups.add(new SelectItem(gp.getId(), gp.getSeries()));
		}*/
		
		funds = new ArrayList<>();
		funds2 = new ArrayList<>();
		
		fundId = 1;
		fundId2 = 1;
		for(PayrollFund f : PayrollFund.retrieveAll().values()) {
			funds.add(new SelectItem(f.getId(), f.getName()));
			funds2.add(new SelectItem(f.getId(), f.getName()));
		}
		
	}
	
	public void selectedFund() {
		setFundId2(getFundId());
	}
	public void selectedFund2() {
		setFundId(getFundId2());
	}
	
	/*public void typeFind() {
		
		
		String sql = "";
		
		groups = new ArrayList<>();
		if(getType()>0) {
			sql = " AND gp.emtype=" + getType();
		}else {
			groups.add(new SelectItem(0, "All"));
			
		}
		
		for(PayrollGroupSeries gp : PayrollGroupSeries.retrieve(sql + " ORDER BY gp.gid DESC", new String[0])) {
			groups.add(new SelectItem(gp.getId(), gp.getSeries()));
		}
		
		
		loadRoll();
	}*/
	
	public void loadRoll() {
		rolls = new ArrayList<EmployeePayroll>();
		String[] params = new String[0];
		String sql = " AND py.pystatus=0";
		
		if(getType()>0) {
			sql += " AND emp.employeetype=" + getType();
		}
		
		if(getYearSearchId()>0) {
			sql += " AND py.yearperiod="+getYearSearchId();
		}
		
		if(getMonthSeachId()>0) {
			sql += " AND py.monthperiod="+getMonthSeachId();
		}
		
		if(getFundId2()>0) {
			sql += " AND fn.fid="+ getFundId2();
		}
		
		if(getSearchParam()!=null && !getSearchParam().isEmpty() && getSearchParam().length()>=3) {
			sql += " AND emp.fullname like '%"+ getSearchParam() +"%'";
		}else {
			if(getType()==0 && getFundId2()==0) {
				sql += " ORDER BY py.pyid DESC LIMIT 10";
			}
		}
		
		Object[] obj = EmployeePayroll.retrieveData(sql, params);
		rolls = (List<EmployeePayroll>)obj[6];
		setTaxTotal(Currency.formatAmount(Double.valueOf(obj[0].toString())));
		setGrossTotal(Currency.formatAmount(Double.valueOf(obj[1].toString())));
		setNetTotal(Currency.formatAmount(Double.valueOf(obj[2].toString())));
		setEeTotal(Currency.formatAmount(Double.valueOf(obj[3].toString())));
		setErTotal(Currency.formatAmount(Double.valueOf(obj[4].toString())));
		setCoopTotal(Currency.formatAmount(Double.valueOf(obj[5].toString())));
		
		if(rolls!=null && rolls.size()==1) {
			clickPayroll(rolls.get(0));
		}
		
	}
	
	//public String getSeries() {
	//	return PayrollGroupSeries.getLatestSeriesId(EmployeeType.REGULAR);
	//}
	
	private void loadDefault() {
		setDateTrans(new Date());
		months = new ArrayList<>();
		years = new ArrayList<>();
		stats = new ArrayList<>();
		stats.add(new SelectItem(0, "Draft"));
		stats.add(new SelectItem(1, "Posted"));
		
		payData = new EmployeePayroll();
		payData.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		payData.setMonth(DateUtils.getCurrentMonth());
		payData.setYear(DateUtils.getCurrentYear());
		monthSeachId = DateUtils.getCurrentMonth();
		yearSearch = new ArrayList<>();
		monthSearch = new ArrayList<>();
		
		yearSearchId = DateUtils.getCurrentYear();
		for(Months m : Months.values()) {
			
			if(m.getId()==0) {
				monthSearch.add(new SelectItem(0, "All Months"));
			}else {
				months.add(new SelectItem(m.getId(), m.getName()));
				monthSearch.add(new SelectItem(m.getId(), m.getName()));
			}
		}
		for(int y=2022; y<=DateUtils.getCurrentYear(); y++) {
			years.add(new SelectItem(y, y+""));
			yearSearch.add(new SelectItem(y, y+""));
		}
		
		//setSeries(PayrollGroupSeries.getLatestSeriesId(EmployeeType.REGULAR));
		setPaySelected(new ArrayList<EmployeePayroll>());
	}
	
	public void loadEmployee() {
		employeeData = new ArrayList<EmployeeMain>();
		String sql = "";
		String[] params = new String[0];
		
		if(getSearchEmployee()!=null && !getSearchEmployee().isBlank() && getSearchEmployee().length()>3) {
			sql = " AND emp.fullname like '%"+ getSearchEmployee() +"%'";
		}
		
		sql += " ORDER BY emp.eid DESC";
		
		employeeData = EmployeeMain.retrieve(sql, params);
	}
	public void clickEmployee(EmployeeMain em) {
		
		loans = EmployeeLoan.retrieve(" AND loan.iscompleted=1 AND emp.eid="+ em.getId(), new String[0]);
		
		EmployeePayroll pay = EmployeePayroll.builder()
				.rate(em.getRate())
				.coop(0)
				.tax(0)
				.dateTrans(DateUtils.convertDate(getDateTrans(), "yyyy-MM-dd"))
				.designation(em.getPosition())
				.ee(0)
				.er(0)
				.employeeMain(em)
				.gross(0)
				.net(0)
				.numberOfWork(0)
				.month(Months.getMonth(DateUtils.getCurrentMonth()).getId())
				.year(DateUtils.getCurrentYear())
				.status(0)
				.build();
		
		
		
		setPayData(pay);
		setEmployeeSelected(em);
		//calculatePayslip();
		
		//setSeries(PayrollGroupSeries.getLatestSeriesId(EmployeeType.value(em.getEmployeeType())));
		retrieveWorkingDays();
		calculatePayslip();
	}
	
	public void calculatePayslip() {
		double workingDays = getPayData().getNumberOfWork();
		double rate = getEmployeeSelected().getRate();
		boolean wholeYearPaid = getEmployeeSelected().getWithHoldingTax()==1? true : false;
		double totalAmountRendered = 0d;
		double taxableAmount = 0d;
		double gross = 0d;
		double net = 0d;
		if(RateType.DAILYRATE.getId()==getEmployeeSelected().getRateType()) {
			totalAmountRendered = workingDays * rate;
		}else if(RateType.FIXEDRATE.getId()==getEmployeeSelected().getRateType()){
			totalAmountRendered = rate;
		}else if(RateType.REGRATE.getId()==getEmployeeSelected().getRateType()) {
			totalAmountRendered = workingDays * (rate/8);
		}
		
		gross = totalAmountRendered;
		
		if(!wholeYearPaid) {
			taxableAmount = totalAmountRendered * 0.01;
			totalAmountRendered -= taxableAmount;
		}
		
		double loanAmount = 0d;
		for(EmployeeLoan l : loans) {
			loanAmount += l.getMonthlyDeduction();
		}
		
		totalAmountRendered -= loanAmount;
		
		net = totalAmountRendered;
		
		getPayData().setGross(gross);
		getPayData().setTax(taxableAmount);
		getPayData().setNet(net);
		
	}
	
	public void savePayroll() {
		boolean isOk = true;
		EmployeePayroll pay = new EmployeePayroll();
		//PayrollGroupSeries gp = new PayrollGroupSeries();
		if(getPayData()!=null) {
			pay = getPayData();
		}
		
		if(getEmployeeSelected()==null) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide employee");
		}
		
		if(getEmployeeSelected()!=null && 
				getPayData().getId()==0 && 
				EmployeePayroll.isExistPayroll(getPayData().getMonth(), getPayData().getYear(), getEmployeeSelected().getId())) {
			isOk = false;
			Application.addMessage(3, "Error", "You have already created a payroll for " + getEmployeeSelected().getFullName() + " for the month of " + DateUtils.getMonthName(getPayData().getMonth()) + " " + getPayData().getYear());
		}
		
		if(isOk) {
			
			if(loans!=null && loans.size()>0) {
				double ee = 0d, loan=0d,coop=0d;
				for(EmployeeLoan l : loans) {
					switch(l.getName()) {
						case "EE":ee+=l.getMonthlyDeduction(); break;
						case "LOAN": loan+=l.getMonthlyDeduction(); break;
						case "COOP": coop+=l.getMonthlyDeduction(); break;
					}
				}
				if(ee>0) {
					pay.setEe(ee);
				}
				if(loan>0) {
					pay.setEr(loan);
				}
				if(coop>0) {
					pay.setCoop(coop);
				}
			}
			
			/*if(getGroupData()!=null) {
				gp = getGroupData();
			}else {
				gp = PayrollGroupSeries.retrieveSeries(getSeries());
				if(gp==null) {
					gp.setDateTrans(DateUtils.getCurrentDateMonthDayYear());
					gp.setIsActive(1);
					gp.setSeries(getSeries());
					gp.setType(getEmployeeSelected().getEmployeeType());
					gp = PayrollGroupSeries.save(gp);
				}
			}
			
			
			pay.setGroup(gp);*/
			pay.setFund(PayrollFund.builder().id(getFundId()).build());
			pay.setIsActive(1);
			pay.setEmployeeMain(getEmployeeSelected());
			pay.setPayroll(Payroll.builder().id(0).build());
			pay.save();
			setFundId2(pay.getFund().getId());
			loadRoll();
			Application.addMessage(1, "Success", "Successfully saved.");
			clearAll();
			
		}
		
		
	}
	
	public void delete(EmployeePayroll pay) {
		pay.delete();
		loadRoll();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void clickPayroll(EmployeePayroll pay) {
		EmployeeMain em = pay.getEmployeeMain();
		setPayData(pay);
		setEmployeeSelected(em);
		loans = EmployeeLoan.retrieve(" AND loan.iscompleted=1 AND emp.eid="+ em.getId(), new String[0]);
		
		//PayrollGroupSeries gp = PayrollGroupSeries.retrieveGroup(pay.getGroup().getId(), em.getEmployeeType());
		//setGroupData(gp);
		setFundId(pay.getFund().getId());
		//setSeries(gp.getSeries());
	}
	
	public void clearAll() {
		loans = new ArrayList<EmployeeLoan>();
		setPayData(null);
		setEmployeeSelected(null);
		//setGroupData(null);
		loadDefault();
		setPaySelected(null);
	}
	
	public void retrieveWorkingDays() {
		int monthId = getPayData().getMonth();
		int yearId = getPayData().getYear();
		
		System.out.println("Month:"+monthId + " Year:"+yearId);
		
			String month = monthId<10? "0"+monthId: monthId+"";
			String year = yearId+"";
			String sql = " AND datetype>=2 AND (caldate>=? AND caldate<=?) AND datetype>=2 ORDER BY caldate";
			String[] params = new String[2];
			params[0] = year + "-" + month + "-01";
			params[1] = year + "-" + month + "-31";
			List<CalendarDate> days = CalendarDate.retrieve(sql,params);
			int size = days.size();
			int[] nums=new int[size];
			int i=0;
			for(CalendarDate d : days) {
				int num = Integer.valueOf(d.getDateVal().split("-")[2]);
				nums[i++] = num;
			}
			
			
			List<Week> calendars = CalendarDays.displayMonthSelected(monthId, yearId,nums);
		int dayWork = Week.workingDay(calendars, size, EmployeeType.value(getEmployeeSelected().getEmployeeType()));
		getPayData().setNumberOfWork(dayWork);
		
	}
	
	public void batchPayrollList() {
		if(getPaySelected()!=null && getPaySelected().size()>0) {
			double ee=0d, er=0d, coop=0d;
			//List<EmployeePayroll> tempLoan = getPaySelected();
			setPaySelectedLoan(new ArrayList<EmployeePayroll>());
			for(EmployeePayroll l : getPaySelected()) {
				if(l.getEe()>0 || l.getEr()>0 || l.getCoop()>0) {
					
					ee += l.getEe();
					er += l.getEr();
					coop += l.getCoop();
					getPaySelectedLoan().add(l);
				}
			}
			setEeTotalLoan(Currency.formatAmount(ee));
			setErTotalLoan(Currency.formatAmount(er));
			setCoopTotalLoan(Currency.formatAmount(coop));
			PrimeFaces pf = PrimeFaces.current();
			pf.executeScript("PF('dlgLoan').show(1000)");
		}
	}
	
	
	public void showCreatePayroll() {
		if(getPaySelected()!=null && getPaySelected().size()>0) {
			PrimeFaces pf = PrimeFaces.current();
			
			setSeriesPayroll(Payroll.getLatestSeriesId(getPaySelected().get(0).getFund().getId()));
			
			supervisorId = 1;
			approverId = 1;
			treasurerId = 4;
			disbursingId = 7;
			accountantId = 5;
			supervisors = new ArrayList<>();
			approvers = new ArrayList<>();
			treasurers = new ArrayList<>();
			disbursings = new ArrayList<>();
			accountants = new ArrayList<>();
			
			officers = PayrollApprover.retrieveAll();//to be use also in saving
			
			for(PayrollApprover a : officers.values()) {
				supervisors.add(new SelectItem(a.getId(), a.getName()));
				approvers.add(new SelectItem(a.getId(), a.getName()));
				treasurers.add(new SelectItem(a.getId(), a.getName()));
				disbursings.add(new SelectItem(a.getId(), a.getName()));
				accountants.add(new SelectItem(a.getId(), a.getName()));
			}
			
			pf.executeScript("PF('dlgPayroll').show(1000)");
		}else {
			Application.addMessage(2, "No Action", "Please select employee first.");
		}
	}
	
	public void deleteSelected(EmployeePayroll py) {
		getPaySelected().remove(py);
		py.setStatus(0);
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void clearPayroll() {
		//setSeriesPayroll(null);
		supervisorId = 1;
		approverId = 1;
		treasurerId = 4;
		disbursingId = 7;
		accountantId = 5;
	}
	
	public void createPayrollList() {
		if(getPaySelected()!=null && getPaySelected().size()>0) {
			String ids = "";
			double gross=0d,ee=0d, loan=0d, coop=0d, tax=0d,net=0d;
			int count = 1;
			PayrollFund fund = getPaySelected().get(0).getFund();
			for(EmployeePayroll p : getPaySelected()) {
				if(count==1) {
					ids = p.getId()+"";
				}else {
					ids += ":" + p.getId();
				}
				count++;
				
				gross += p.getGross();
				ee += p.getEe();
				loan += p.getEr();
				coop += p.getCoop();
				tax += p.getTax();
				net += p.getNet();
				
			}
			
			if(getOfficers()!=null) {
			
			Payroll pay = Payroll.builder()
			.dateTrans(DateUtils.getCurrentDateYYYYMMDD())
			.series(getSeriesPayroll())
			
			.supervisor(getOfficers().get(getSupervisorId()))
			.approver(getOfficers().get(getApproverId()))
			.treasurer(getOfficers().get(getTreasurerId()))
			.disbursing(getOfficers().get(getDisbursingId()))
			.accountant(getOfficers().get(getAccountantId()))
			
			.ids(ids)
			
			.grossTotal(gross)
			.eeTotal(ee)
			.loanTotal(loan)
			.coopTotal(coop)
			.taxTotal(tax)
			.netTotal(net)
			
			.isActive(1)
			.fund(fund)
			
			.build();
				
			pay = Payroll.save(pay);	
			
			for(EmployeePayroll p : getPaySelected()) {
				p.setPayroll(pay);
				p.setStatus(1);
				p.save();
			}
				
			Application.addMessage(1, "Success", "Successfully saved.");
			}
			
		}
	}
	
	public void closePayroll() {
		loadRoll();
		setPaySelected(new ArrayList<EmployeePayroll>());
	}
	
	public void clickPayrollEdit(Payroll pay) {
		setPaySelected(pay.retrieveAll(pay.getIds()));
		setSupervisorId(pay.getSupervisor().getId());
		setApproverId(pay.getApprover().getId());
		setTreasurerId(pay.getTreasurer().getId());
		setDisbursingId(pay.getDisbursing().getId());
		setAccountantId(pay.getAccountant().getId());
		setSeriesPayroll(pay.getSeries());
	}
	
	public void onTabChange(TabChangeEvent event) {
        //FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab: " + event.getTab().getTitle());
        //FacesContext.getCurrentInstance().addMessage(null, msg);
		
		if("Creation".equalsIgnoreCase(event.getTab().getTitle())) {
			
		}else if("Viewing".equalsIgnoreCase(event.getTab().getTitle())) {
				payrolls =  Payroll.retrieve(" ORDER BY pseries DESC", new String[0]);
		}
		
    }

    public void onTabClose(TabCloseEvent event) {
        //FacesMessage msg = new FacesMessage("Tab Closed", "Closed tab: " + event.getTab().getTitle());
        //FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
    public void showPayroll() {
    	setSeriesPayroll(Payroll.getLatestSeriesId(1));
		
		supervisorId = 1;
		approverId = 1;
		treasurerId = 4;
		disbursingId = 7;
		accountantId = 5;
		supervisors = new ArrayList<>();
		approvers = new ArrayList<>();
		treasurers = new ArrayList<>();
		disbursings = new ArrayList<>();
		accountants = new ArrayList<>();
		
		officers = PayrollApprover.retrieveAll();//to be use also in saving
		
		for(PayrollApprover a : officers.values()) {
			supervisors.add(new SelectItem(a.getId(), a.getName()));
			approvers.add(new SelectItem(a.getId(), a.getName()));
			treasurers.add(new SelectItem(a.getId(), a.getName()));
			disbursings.add(new SelectItem(a.getId(), a.getName()));
			accountants.add(new SelectItem(a.getId(), a.getName()));
		}
    }
    
    public void deletePayroll(Payroll pay) {
    	boolean isSuccess = Payroll.rollbackPayroll(pay);
    	if(isSuccess) {
    		pay.delete();
    		getPayrolls().remove(pay);
        	Application.addMessage(1, "Success", "Successfully deleted.");
    	}else {
    		Application.addMessage(3, "Error", "Error deleting payroll list");
    	}
    }
    
	public void print(Payroll pay) {
		//if(getPaySelected()!=null && getPaySelected().size()>0) {
		if(pay.getIds()!=null && !pay.getIds().isEmpty()) {
			
			String REPORT_PATH = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
					AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
			String REPORT_NAME = GlobalVar.EMPLOYEE_PAYROLL;
			
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
			
			List<PayrollRpt> rpts = new ArrayList<PayrollRpt>();
			int count = 1;
			double gross=0d, ee=0d, er=0d, tax=0d, coop=0d, net=0d;
			int month = 0, year=0;
			//for(EmployeePayroll p : getPaySelected()) {
			for(EmployeePayroll p : Payroll.retrieveAll(pay.getIds())) {
				
				String department = null;
				
				try {department = Department.department(p.getEmployeeMain().getDepartment().getDepid()+"").getDepartmentName(); }catch(Exception e) {}
				
				rpts.add(
						PayrollRpt.builder()
						.f1(count+"")
						.f2(p.getEmployeeMain().getFullName())
						.f3(p.getDesignation())
						.f4(p.getEmployeeMain().getRateType()==RateType.FIXEDRATE.getId()? "" :  p.getNumberOfWork()+"")
						.f5(Currency.formatAmount(p.getRate()))
						.f6(Currency.formatAmount(p.getGross()))
						.f7(Currency.formatAmount(p.getEe()))
						.f8(Currency.formatAmount(p.getEr()))
						.f9(Currency.formatAmount(p.getTax()))
						.f10(Currency.formatAmount(p.getCoop()))
						.f11(Currency.formatAmount(p.getNet()))
						.f16(department!=null? department : "")
						.build()
						);
				count++;
				gross += p.getGross();
				ee += p.getEe();
				er += p.getEr();
				tax += p.getTax();
				coop += p.getCoop();
				net += p.getNet();
				month = p.getMonth();
				year = p.getYear();
			}
			
			String datePeriod = DateUtils.getMonthName(month).toUpperCase() + " 01-31, " + year;
			
			JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(rpts);
	  		HashMap param = new HashMap();
	  		
	  		param.put("PARAM_TITLE", "DAILY WAGE PAYROLL");
	  		param.put("PARAM_SUB_TITLE",   pay.getFund().getName());//"MAYOR'S OFFICE - GENERAL SERVICE FUND");
	  		param.put("PARAM_SUB_SUB_TITLE", "PROJECT");
	  		
	  		param.put("PARAM_LGU_NAME", "LGU - LAKE SEBU");
	  		param.put("PARAM_DATE_PERIOD", datePeriod);
	  		
			param.put("PARAM_SUPERVISOR", pay.getSupervisor().getName().toUpperCase());
			param.put("PARAM_APPROVING_OFFICER", pay.getApprover().getName().toUpperCase());
			param.put("PARAM_ACCOUNTANT", pay.getAccountant().getName().toUpperCase());
			param.put("PARAM_TREASURER", pay.getTreasurer().getName().toUpperCase());
			param.put("PARAM_DISBURSING", pay.getDisbursing().getName().toUpperCase());
			
			param.put("PARAM_GROSS_TOTAL", Currency.formatAmount(gross));
			param.put("PARAM_EE_TOTAL", Currency.formatAmount(ee));
			param.put("PARAM_LOAN_TOTAL", Currency.formatAmount(er));
			param.put("PARAM_TAX_TOTAL", Currency.formatAmount(tax));
			param.put("PARAM_COOP_TOTAL", Currency.formatAmount(coop));
			param.put("PARAM_NET_TOTAL", Currency.formatAmount(net));
			
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
			            input = new BufferedInputStream(new FileInputStream(file), GlobalVar.DEFAULT_BUFFER_SIZE);

			            // Init servlet response.
			            response.reset();
			            response.setHeader("Content-Type", "application/pdf");
			            response.setHeader("Content-Length", String.valueOf(file.length()));
			            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
			            output = new BufferedOutputStream(response.getOutputStream(), GlobalVar.DEFAULT_BUFFER_SIZE);

			            // Write file contents to response.
			            byte[] buffer = new byte[GlobalVar.DEFAULT_BUFFER_SIZE];
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
	
}
