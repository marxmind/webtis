package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;

import com.italia.municipality.lakesebu.controller.MarketMonthReport;
import com.italia.municipality.lakesebu.controller.MarketTenant;
import com.italia.municipality.lakesebu.controller.MarketTenantPayment;
import com.italia.municipality.lakesebu.controller.MarketTenantProperty;
import com.italia.municipality.lakesebu.enm.BuildingType;
import com.italia.municipality.lakesebu.enm.Buildings;
import com.italia.municipality.lakesebu.enm.PaymentType;
import com.italia.municipality.lakesebu.enm.StallType;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.Currency;
import com.italia.municipality.lakesebu.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @since 06/16/2018
 * @version 1.0
 *
 */

@Named
@ViewScoped
@Setter @Getter 
public class MarketBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4365643343431L;
	
	////////////////////////////////////////////TENANT//////////////////////////////////////
	private MarketTenant tenant;
	private List<MarketTenant> tenants;
	private String searchTenant;
	
	
	///////////////////////////////////////TENANT PROPERTY INFO/////////////////////////////
	private MarketTenantProperty property;
	private List<MarketTenantProperty> properties;
	private List buildings;
	private List buildingTypes;
	private List stallTypes;
	private List tenantOwners;
	private String searchProperty;
	
	///////////////////////////////////////Payments////////////////////////////////////////
	private MarketTenantPayment payment;
	private List<MarketTenantPayment> payments;
	private List ownerPayments;
	private List tenantProperty;
	private List paymentTypes;
	private List years;
	private List months;
	private Map<Long, MarketTenantProperty> propData;
	private double grandTotalCollected;
	private String searchCollection;
	
	////////////////////////////Inquiry/////////////////////////////////////////
	private String inquiryData;
	private String searchParam;
	private List<MarketMonthReport> marketRpts;
	
	
	@PostConstruct
	public void init() {
		loadDefault();
		loadDefaultProperty();
		loadTenant();
		loadPaymentDefault();
	}
	
	public void loadTenant() {
		String sql = "";
		
		if(getSearchTenant()!=null && !getSearchTenant().isBlank()) {
			sql += " AND (fullname like '%"+ getSearchTenant() +"%'";
			sql += " OR address like '%"+ getSearchTenant() +"%'";
			sql += " OR contactnumber like '%"+ getSearchTenant() +"%')";
		}
		sql += " ORDER BY tid DESC";
		
		tenants = new ArrayList<MarketTenant>();
		tenants = MarketTenant.retrieve(sql, new String[0]);
	}
	
	private void loadDefault() {
		tenant = MarketTenant.builder()
				.dateRegisteredTmp(new Date())
				.isActive(1)
				.build();
	}
	
	public void onTabChange(TabChangeEvent event) {
        //FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab: " + event.getTab().getTitle());
        //FacesContext.getCurrentInstance().addMessage(null, msg);
		if("Tenant Info".equalsIgnoreCase(event.getTab().getTitle())) {
			loadDefault();
			loadTenant();
		}else if("Tenant Property Info".equalsIgnoreCase(event.getTab().getTitle())) {
			loadDefaultProperty();
			loadProperty();
		}else if("Tenant Payment Info".equalsIgnoreCase(event.getTab().getTitle())) {
			loadPaymentDefault();
			loadPayment();
		}else if("Inquiry".equalsIgnoreCase(event.getTab().getTitle())) {
			loadInquiry();
		}
    }

    public void onTabClose(TabCloseEvent event) {
        //FacesMessage msg = new FacesMessage("Tab Closed", "Closed tab: " + event.getTab().getTitle());
        //FacesContext.getCurrentInstance().addMessage(null, msg);
    }

	
	public void clear() {
		loadDefault();
	}
	
	public void clickItem(MarketTenant ten) {
		ten.setDateRegisteredTmp(DateUtils.convertDateString(ten.getDateRegistered(), "yyyy-MM-dd"));
		tenant = ten;
	}
	
	public void delete(MarketTenant ten) {
		ten.delete();
		tenants.remove(ten);
		Application.addMessage(1, "Success", "Successfully deleted");
	}
	
	public void save() {
		boolean isOk = true;
		boolean isOld = false;
		if(tenant!=null ) {
			
			if(tenant!=null && tenant.getId()>0) {isOld=true;}//check if data is old or new if old assigned true boolean value
			
			if(tenant.getFullName()==null || tenant.getFullName().isBlank()) {
				isOk = false;
				Application.addMessage(3, "Error", "Please provide tenant name");
			}
			
			if(tenant.getAddress()==null || tenant.getAddress().isBlank()) {
				isOk = false;
				Application.addMessage(3, "Error", "Please provide address");
			}
			
			/*
			 * if(tenant.getContactNo()==null || tenant.getContactNo().isBlank()) { isOk =
			 * false; Application.addMessage(3, "Error", "Please provide contact no"); }
			 */
			
			if(isOk) {
				tenant.setIsActive(1);
				tenant.setDateRegistered(DateUtils.convertDate(tenant.getDateRegisteredTmp(), "yyyy-MM-dd"));
				tenant = MarketTenant.save(tenant);
				loadTenant();
				clear();
				Application.addMessage(1, "Success", "Successfully saved");
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////TENANT PROPERTY INFO//////////////////////////////////////////////////////////////////////
	private void loadDefaultProperty() {
		
		tenantOwners = new ArrayList<>();
		for(MarketTenant t : MarketTenant.retrieve("", new String[0])) {
			tenantOwners.add(new SelectItem(t.getId(), t.getFullName()));
		}
		
		buildings = new ArrayList<>();
		for(Buildings b : Buildings.values()) {
			buildings.add(new SelectItem(b.getId(), b.getName()));
		}
		buildingTypes = new ArrayList<>();
		for(BuildingType t : BuildingType.values()) {
			buildingTypes.add(new SelectItem(t.getId(), t.getName()));
		}
		stallTypes = new ArrayList<>();
		for(StallType s : StallType.values()) {
			stallTypes.add(new SelectItem(s.getId(), s.getName()));
		}
		MarketTenant ten = MarketTenant.builder().id(1).build();
		
		property = MarketTenantProperty.builder()
				.aquiredDateTmp(new Date())
				.retiredDateTmp(null)
				.isActive(1)
				.marketTenant(ten)
				.build();
	}
	
	
	public void loadProperty() {
		String sql = "";
		if(getSearchProperty()!=null && !getSearchProperty().isBlank()) {
			sql = " AND (prp.specificname like '%"+ getSearchProperty() +"%'";
			sql += " OR tnt.fullname like '%"+ getSearchProperty() +"%')";
		}
		sql += " ORDER BY prp.prid DESC";
		properties = new ArrayList<MarketTenantProperty>();
		properties = MarketTenantProperty.retrieve(sql, new String[0]);
	}
	
	public void clearProperty() {
		MarketTenant ten = MarketTenant.builder().id(1).build();
		
		property = MarketTenantProperty.builder()
				.aquiredDateTmp(new Date())
				.isActive(1)
				.marketTenant(ten)
				.build();
	}
	
	public void saveProperty() {
		boolean isOk = true;
		if(getProperty()!=null) {
			if(property.getAmountRented()==0) {
				isOk = false;
				Application.addMessage(3, "Error", "Please provide amount");
			}
			if(property.getSpecificName()==null) {
				isOk = false;
				Application.addMessage(3, "Error", "Please provide specific name");
			}
			
			if(isOk) {
				property.setDateAquired(DateUtils.convertDate(property.getAquiredDateTmp(), "yyyy-MM-dd"));
				if(property.getRetiredDateTmp()!=null) {
					property.setDateReturned(DateUtils.convertDate(property.getRetiredDateTmp(), "yyyy-MM-dd"));
				}
				property.save();
				clearProperty();
				loadProperty();
				Application.addMessage(1, "Success", "Successfully saved.");
			}
			
		}
	}
	
	public void clickItemProperty(MarketTenantProperty prop) {
		prop.setAquiredDateTmp(DateUtils.convertDateString(prop.getDateAquired(), "yyyy-MM-dd"));
		if(prop.getDateReturned()!=null) {
			prop.setRetiredDateTmp(DateUtils.convertDateString(prop.getDateReturned(), "yyyy-MM-dd"));
		}
		property = prop;
	}
	
	public void deleteProperty(MarketTenantProperty prop) {
		prop.delete();
		properties.remove(prop);
		Application.addMessage(1, "Success", "Successfully deleted");
	}
	///////////////////////////////////tenant payment/////////////////////////////////////
	public void loadPaymentDefault() {
		ownerPayments = new ArrayList<>();
		payment = MarketTenantPayment.builder()
				.datePaidTmp(new Date())
				.isActive(1)
				.marketTenant(MarketTenant.builder().id(0).build())
				.marketTenantProperty(MarketTenantProperty.builder().id(0).build())
				.paymentType(2)
				.build();
		ownerPayments.add(new SelectItem(0,"Select"));
		for(MarketTenant t : MarketTenant.retrieve("", new String[0])) {
			ownerPayments.add(new SelectItem(t.getId(), t.getFullName()));
		}
		tenantProperty = new ArrayList<>();
		selectedTenant();
		
		paymentTypes = new ArrayList<>();
		for(PaymentType t : PaymentType.values()) {
			paymentTypes.add(new SelectItem(t.getId(), t.getName()));
		}
		
		months = new ArrayList<>();
		years = new ArrayList<>();
		for(int year=2015; year<=DateUtils.getCurrentYear(); year++) {
			years.add(new SelectItem(year, year+""));
		}
		for(int month=1; month<=12; month++) {
			months.add(new SelectItem(month, DateUtils.getMonthName(month)));
		}
	}
	public void selectedTenant() {
		tenantProperty = new ArrayList<>();
		tenantProperty.add(new SelectItem(0,"Select"));
		propData = new LinkedHashMap<Long, MarketTenantProperty>();
		for(MarketTenantProperty p : MarketTenantProperty.retrieve(" AND tnt.tid="+ payment.getMarketTenant().getId(), new String[0])) {
			tenantProperty.add(new SelectItem(p.getId(), p.getBuildingName() + "-" + p.getSpecificName()));
			propData.put(p.getId(), p);
		}
		
	}
	
	public void selectedTenantProperty() {
		payment.setAmountPaid(getPropData().get(payment.getMarketTenantProperty().getId()).getAmountRented());
	}
	
	public void loadPayment() {
		String sql = "";
		if(getSearchCollection()!=null && !getSearchCollection().isBlank()) {
			sql = " AND (py.ornumber like '%"+ getSearchCollection()+"%'";
			sql += " OR prp.specificname like '%"+ getSearchCollection() +"%'";
			sql += " OR tnt.fullname like '%"+ getSearchCollection() +"%' )";
		}
		sql += " ORDER BY py.tpid DESC";
		
		double amount = 0d;
		payments = new ArrayList<MarketTenantPayment>();
		for(MarketTenantPayment p : MarketTenantPayment.retrieve(sql, new String[0])) {
			amount += p.getAmountPaid();
			payments.add(p);
		}
		setGrandTotalCollected(amount);
	}
	
	public void clickItemPayment(MarketTenantPayment pay) {
		pay.setDatePaidTmp(DateUtils.convertDateString(pay.getDatePaid(), "yyyy-MM-dd"));
		payment = pay;
		selectedTenant();
	}
	
	public void deletePayment(MarketTenantPayment p) {
		p.delete();
		payments.remove(p);
		Application.addMessage(1, "Success", "Successfully deleted");
	}
	
	public void clearPayment() {
		loadPaymentDefault();
	}
	
	public void savePayment() {
		boolean isOk = true;
		
		if(payment.getAmountPaid()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide amount");
		}
		
		if(payment.getOfficialNumber().isBlank()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Official receipt number");
		}
		
		if(payment.getMarketTenant().getId()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide owner");
		}
		
		if(payment.getMarketTenantProperty().getId()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide property");
		}
		
		if(isOk) {
			payment.setBuildingId(getPropData().get(payment.getMarketTenantProperty().getId()).getBuildingId());
			payment.setBuildingTypeId(getPropData().get(payment.getMarketTenantProperty().getId()).getBuildingTypeId());
			payment.setStallTypeId(getPropData().get(payment.getMarketTenantProperty().getId()).getStallTypeId());
			payment.setMarketTenantProperty(getPropData().get(payment.getMarketTenantProperty().getId()));
			payment.setDatePaid(DateUtils.convertDate(payment.getDatePaidTmp(), "yyyy-MM-dd"));
			payment = MarketTenantPayment.save(payment);
			clearPayment();
			loadPayment();
		}
	}
	
	public void loadInquiry() {
		String text = "<h3>Welcome to market inquiry</h3>";
		setInquiryData(text);
	}
	
	public void inquirySearch() {
		String text = getSearchParam();
		
		String sql = " AND fullname like '%"+ text +"%'";
		String vs = "";
		
		
		
		
		System.out.println("Inquiry: " + vs);
		//vs = inquiry(sql, vs);
		vs = check(sql, vs);
		setInquiryData(vs);
	}
	
	private String inquiry(String sql, String vs) {
		List<MarketTenant> tenants = MarketTenant.retrieve(sql, new String[0]);
		if(tenants!=null && tenants.size()>0) {
			vs = "<ul>";
			for(MarketTenant mk : tenants) {
				List<MarketTenantPayment> pays = MarketTenantPayment.retrieve(" AND tnt.tid="+ mk.getId(), new String[0]);
				if(pays!=null && pays.size()>0) {
					
					vs += "<li>"+ mk.getFullName() +"<ul>";
					Map<Long, List<MarketTenantPayment>> mapPay = new LinkedHashMap<Long, List<MarketTenantPayment>>();
					List<MarketTenantPayment> payList = new ArrayList<MarketTenantPayment>();
					for(MarketTenantPayment py : pays) {
						long idProp = py.getMarketTenantProperty().getId();
						if(mapPay!=null) {
							if(mapPay.containsKey(idProp)) {
								mapPay.get(idProp).add(py);
							}else {
								payList = new ArrayList<MarketTenantPayment>();
								payList.add(py);
								mapPay.put(idProp, payList);
							}
						}else {
							payList.add(py);
							mapPay.put(idProp, payList);
						}
					}
					Map<Long, List<MarketTenantPayment>> sortedData = new TreeMap<Long, List<MarketTenantPayment>>(mapPay);
					for(Long id : sortedData.keySet()) {
						vs += "<li>" + sortedData.get(id).get(0).getMarketTenantProperty().getSpecificName().toUpperCase() + " Aquired: " + DateUtils.convertDateToMonthDayYear(sortedData.get(id).get(0).getMarketTenantProperty().getDateAquired()) + "<br/>Payment History";
						vs +="<ul>";
						for(MarketTenantPayment p : sortedData.get(id)) {
							vs += "<li>Date: " + DateUtils.convertDateToMonthDayYear(p.getDatePaid()) + "=" + Currency.formatAmount(p.getAmountPaid())+"</li>";
						}
						vs += "</ul></li>";
					}
					
					
					vs += "</ul></li>";
					vs +="<li>------------------------------------------------------------------------------------------------</li>";
					
					//check if there
					
				}else {
					vs += "<li>" + mk.getFullName(); //if no paid yet property
					List<MarketTenantProperty> props = MarketTenantProperty.retrieve(" AND tnt.tid="+mk.getId(), new String[0]);
					if(props!=null && props.size()>0) {
						vs += "<ul>";
						for(MarketTenantProperty p : props) {
							vs += "<li>"+p.getSpecificName().toUpperCase() + " Aquired: " + DateUtils.convertDateToMonthDayYear(p.getDateAquired())+"</li>";
						}
						vs += "</ul>";
					}
					vs +="</li>";
				}
			}
			vs += "</ul>";
		}else {
			vs = "<p>No data found</p>";
		}
		System.out.println("VS=" + vs);
		return vs;
	}
	
	public String check(String sql, String vs) {
		
		List<MarketTenant> tenants = MarketTenant.retrieve(sql, new String[0]);
		if(tenants!=null && tenants.size()>0) {
			for(MarketTenant mk : tenants) {
				List<MarketTenantProperty> props = MarketTenantProperty.retrieve(" AND tnt.tid="+mk.getId(), new String[0]);
				if(props!=null && props.size()>0) {
					vs += "<p>" + mk.getFullName()+"</p>";
					vs += "<ul>";
					for(MarketTenantProperty p : props) {
						vs += "<li>" + p.getSpecificName().toUpperCase() + " <br>Acquired: " + DateUtils.convertDateToMonthDayYear(p.getDateAquired());
						List<MarketTenantPayment> pays = MarketTenantPayment.retrieve(" AND prp.prid=" + p.getId(), new String[0]);
						if(pays!=null && pays.size()>0) {
							vs += "<br>Payment History";
							vs += "<ul>";
							for(MarketTenantPayment py : pays) {
								vs += "<li>Date:"+ DateUtils.convertDateToMonthDayYear(py.getDatePaid()) + "  |  OR No: " + (py.getOfficialNumber()==null? " " : py.getOfficialNumber()) + "  |  Amount: " + Currency.formatAmount(py.getAmountPaid()) +"</li>";
							}
							vs += "</ul>";
						}else {
							vs += "(No Payment transaction yet)";
						}
						
						vs += "</li>";
					}
					vs += "</ul>";
				}else {//tenant no property acquired
					vs = "<p>"+ mk.getFullName() +" has no property acquired yet</p>";
				}
			}
		}else {
			vs = "<p>No data found</p>";
		}
		
		
		
		return vs;
	}
	
	public void cal() {
		marketRpts = new ArrayList<MarketMonthReport>();
		Map<Integer, Map<Long, Map<Long, List<MarketTenantPayment>>>> mapYear = new LinkedHashMap<Integer, Map<Long, Map<Long, List<MarketTenantPayment>>>>();
		Map<Long, Map<Long, List<MarketTenantPayment>>> mapTenant = new LinkedHashMap<Long, Map<Long, List<MarketTenantPayment>>>();
		Map<Long, List<MarketTenantPayment>> mapProp = new LinkedHashMap<Long, List<MarketTenantPayment>>();
		List<MarketTenantPayment> pys = new ArrayList<MarketTenantPayment>();
		for(MarketTenantPayment p : MarketTenantPayment.retrieve(" AND tnt.fullname like '%"+ getSearchParam() +"%' ORDER BY py.paidforyear", new String[0])) {
			int year = p.getPaidForYear();
			long tenantId = p.getMarketTenant().getId();
			long propId = p.getMarketTenantProperty().getId();
			if(mapYear!=null && mapYear.size()>0) {
				if(mapYear.containsKey(year)) {
					
					if(mapYear.get(year).containsKey(tenantId)) {
						
						if(mapYear.get(year).get(tenantId).containsKey(propId)) {
							mapYear.get(year).get(tenantId).get(propId).add(p);
						}else {
							pys = new ArrayList<MarketTenantPayment>();
							pys.add(p);
							mapYear.get(year).get(tenantId).put(propId, pys);
						}
						
					}else {
						pys = new ArrayList<MarketTenantPayment>();
						mapProp = new LinkedHashMap<Long, List<MarketTenantPayment>>();
						
						pys.add(p);
						mapProp.put(propId, pys);
						mapYear.get(year).put(tenantId, mapProp);
					}
					
				}else {
					pys = new ArrayList<MarketTenantPayment>();
					mapProp = new LinkedHashMap<Long, List<MarketTenantPayment>>();
					mapTenant = new LinkedHashMap<Long, Map<Long, List<MarketTenantPayment>>>();
					
					pys.add(p);
					mapProp.put(propId, pys);
					mapTenant.put(tenantId, mapProp);
					mapYear.put(year, mapTenant);
				}
			}else {
				pys.add(p);
				mapProp.put(propId, pys);
				mapTenant.put(tenantId, mapProp);
				mapYear.put(year, mapTenant);
			}
		}
		
		Map<Integer, Map<Long, Map<Long, List<MarketTenantPayment>>>> mapSorted = new TreeMap<Integer, Map<Long, Map<Long, List<MarketTenantPayment>>>>(mapYear);
		int count = 1;
		int cnt = 1;
		for(int year : mapYear.keySet()) {
			
			
				for(long tenantId : mapYear.get(year).keySet()) {
					for(long propId : mapYear.get(year).get(tenantId).keySet()) {
						MarketMonthReport rpt = new MarketMonthReport();
						//if(count==1) {rpt.setYear(year);} count++;
						rpt.setYear(year);
						for(MarketTenantPayment py : mapYear.get(year).get(tenantId).get(propId)) {
							
							if(cnt==1) {
								rpt.setOwner(py.getMarketTenant().getFullName());
								rpt.setEstablishment(py.getMarketTenantProperty().getSpecificName());
							}
							cnt++;
							
							String text = "PAID";
							switch(py.getPaidForMonth()) {
								case 1 : rpt.setJanuary(text); break;
								case 2 : rpt.setFebruary(text); break;
								case 3 : rpt.setMarch(text); break;
								case 4 : rpt.setApril(text); break;
								case 5 : rpt.setMay(text); break;
								case 6 : rpt.setJune(text); break;
								case 7 : rpt.setJuly(text); break;
								case 8 : rpt.setAugust(text); break;
								case 9 : rpt.setSeptember(text); break;
								case 10 : rpt.setOctober(text); break;
								case 11 : rpt.setNovember(text); break;
								case 12 : rpt.setDecember(text); break;
							}
						}
						marketRpts.add(rpt);	
					}
				}
				
				
			
		}
		
	}
	
	
}
