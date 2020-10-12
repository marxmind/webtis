package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.controller.Customer;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.controller.WaterAccnt;
import com.italia.municipality.lakesebu.controller.WaterBill;
import com.italia.municipality.lakesebu.controller.WaterMeter;
import com.italia.municipality.lakesebu.controller.WaterReadingMeter;
import com.italia.municipality.lakesebu.enm.WaterBillStatus;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;

/**
 * 
 * @author Mark Italia
 * @since 9/27/2019
 * @version 1.0
 */

@Named
@ViewScoped
public class WaterBillBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 546879346361L;
	
	private String accountNumber;
	private String ownerName;
	private Date dateRegistered;
	private String meterNo;
	
	
	private double amountDue;
	private Date dueDate;
	private Date disconnectiondate;
	private String location;
	
	private List<WaterAccnt> accounts = Collections.synchronizedList(new ArrayList<WaterAccnt>());
	private WaterAccnt accountSelected;
	
	private Date dateBill;
	private String billNo;
	private String memberName;
	private int currentReading;
	private int previousReading;
	private int consumption;
	private double amountConsumed;
	private int statusId;
	private List status;
	
	private double previousUnpaidAmount;
	
	private WaterBill selectedBill;
	
	@PostConstruct
	public void init() {
		
	}
	
	public void loadDetails() {
		String sql = " AND acc.isactiveacc=1 AND cuz.fullname like '%"+getMemberName().trim()+"%'";
		String[] params = new String[0];
		WaterAccnt acc = null;
		try{acc = WaterAccnt.retrieve(sql, params).get(0);}catch(IndexOutOfBoundsException e) {}
		if(acc!=null) {
			setBillNo(WaterBill.waterBillNo(acc.getCustomer()));
			setPreviousReading(WaterReadingMeter.prevReading(acc.getCustomer()));
			setPreviousUnpaidAmount(WaterBill.collectAllUnpaidBill(acc.getCustomer()));
		}
		
	}
	
	
	public List<String> payorNameOwnerSuggested(String query){
		List<String> result = new ArrayList<>();
		if(query!=null && !query.isEmpty()) {
			int size = query.length();
			if(size>=4) {
				String sql = " AND acc.isactiveacc=1 AND cuz.fullname like '%"+query+"%'";
				String[] params = new String[0];
				for(WaterAccnt cz : WaterAccnt.retrieve(sql, params)) {
					result.add(cz.getCustomer().getFullName());
				}
			}
		}
		return result;
	}
	
	
	public void calculationReading() {
		int current = getCurrentReading();
		int prev = getPreviousReading();
		double amountChargePerReading = 22.85;
		double basicPayment = 206.75;
		int consumption = 0;
		double amountConsumption = 0d;
		System.out.println("previous " + prev);
		System.out.println("current " + current);
		if(current>prev) {
			consumption = current - prev;
			System.out.println("current>prev = " + consumption);
		}
		
		if(consumption<=10) {
			amountConsumption = basicPayment;
			System.out.println("consumption<=10 = " + consumption);
		}else {
			
			consumption -= 10;
			amountConsumption = (amountChargePerReading * consumption) + basicPayment; 
			
			System.out.println("else consumption -=10 = " + consumption);
			System.out.println("else amountConsumption " + amountConsumption);
		}
		
		setConsumption(consumption);
		setAmountConsumed(amountConsumption);
		
		amountConsumption += getPreviousUnpaidAmount(); //add the previous unpaid amount
		
		setAmountDue(amountConsumption);
		
	}
	
	public void saveBill() {
		boolean isOk = true;
		WaterBill bill = new WaterBill();
		
		if(getMemberName()==null || getMemberName().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide payor name");
		}
		
		if(getCurrentReading()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide reading");
		}
		
		if(getDueDate().equals(getDisconnectiondate())) {
			isOk = false;
			Application.addMessage(3, "Error", "Disconnection Date should not be the same with due date");
		}
		
		if(isOk) {
			
			if(getSelectedBill()!=null) {
				bill = getSelectedBill();
			}else {
				bill.setIsActive(1);
			}
			
			bill.setDateTrans(DateUtils.convertDate(getDateBill(), "yyyy-MM-dd"));
			bill.setBillNo(getBillNo());
			bill.setAmountDue(getAmountDue());
			bill.setDueDate(DateUtils.convertDate(getDueDate(), "yyyy-MM-dd"));
			bill.setDisconnectionDate(DateUtils.convertDate(getDisconnectiondate(), "yyyy-MM-dd"));
			bill.setStatus(WaterBillStatus.UNPAID.getId());
			clearFlds();
			
		}
		
	}
	
	public void saveDataOwner() {
		Customer customer = null;
		WaterAccnt acc = new WaterAccnt();
		boolean isOk = true;
		
		if(getOwnerName()==null || getOwnerName().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide member name");
		}else {
			customer = selectedCustomer();
		}
		
		if(getLocation()==null || getLocation().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide location");
		}
		
		if(getMeterNo()==null || getMeterNo().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide meter no");
		}
		
		if(isOk) {
			
			//adding user if not exist
			if(customer==null && getOwnerName()!=null) {
				customer = new Customer();
				customer.setFullName(getOwnerName().toUpperCase());
				customer.setAddress(getLocation());
				customer.setContactNumber("0");
				customer.setIsActive(1);
				customer.setRegistrationDate(DateUtils.getCurrentDateYYYYMMDD());
				UserDtls user = Login.getUserLogin().getUserDtls();
				customer.setUserDtls(user);
				customer = Customer.save(customer);
			}
			
			acc.setDateTrans(DateUtils.convertDate(getDateRegistered(), "yyyy-MM-dd"));
			acc.setStatus(1);
			acc.setIsActive(1);
			acc.setLocation(getLocation());
			acc.setCustomer(customer);
			acc = WaterAccnt.save(acc);
			
			if(acc!=null) {
				WaterMeter wm = new WaterMeter();
				if(selectedMeter()==null) {
					wm.setDateTrans(DateUtils.convertDate(getDateRegistered(), "yyyy-MM-dd"));
					wm.setStatus(1); //active=1 , retired 2
					wm.setIsActive(1);
					wm.setMeterNo(getMeterNo());
					wm.setCustomer(customer);
					wm.setAccount(acc);
				}else {
					wm = selectedMeter();
					wm.setStatus(1); //active=1 , retired 2
					wm.setIsActive(1);
					wm.setMeterNo(getMeterNo());
					wm.setCustomer(customer);
					wm.setAccount(acc);
				}
				wm.save();
			}
			clearFlds();
			loadAccounts();
			Application.addMessage(1, "Success", "Successfully saved");
		}
		
	}
	
	public void clearFlds() {
		setAccountNumber(null);
		setLocation(null);
		setOwnerName(null);
		setMeterNo(null);
		setAccountSelected(null);
		
		setSelectedBill(null);
		setMemberName(null);
		setBillNo(null);
		setPreviousReading(0);
		setCurrentReading(0);
		setConsumption(0);
		setAmountConsumed(0.00);
		setAmountDue(0.00);
		setDueDate(null);
		setDisconnectiondate(null);
		setPreviousUnpaidAmount(0.00);
	}
	
	public void loadAccounts() {
		accounts = Collections.synchronizedList(new ArrayList<WaterAccnt>());
		String sql = "";
		String[] params = new String[0];
		for(WaterAccnt acc : WaterAccnt.retrieve(sql, params)) {
			WaterMeter meter = WaterMeter.retrieve(" AND mt.isactivem=1 AND mt.accid=" + acc.getId(), new String[0]).get(0);
			acc.setMeter(meter);
			accounts.add(acc);
		}
		 
	}
	
	public void clickAccount(WaterAccnt acc) {
		setAccountSelected(acc);
		setAccountNumber(WaterAccnt.accountNumber(acc.getId()));
		setLocation(acc.getLocation());
		setOwnerName(acc.getCustomer().getFullName());
		setMeterNo(acc.getMeter().getMeterNo());
	}
	
	public void deleteAccount(WaterAccnt acc) {
		WaterMeter m = acc.getMeter();
		m.setIsActive(0);
		m.save();
		acc.delete();
		Application.addMessage(1, "Success", "Successfully deleted");
	}
	
	public Customer selectedCustomer() {
		String sql = " AND cus.fullname like '%"+getOwnerName().trim()+"%'";
		String[] params = new String[0];
		Customer cz = null;
		try{cz = Customer.retrieve(sql, params).get(0);}catch(IndexOutOfBoundsException e) {}
		if(cz!=null) {
			setLocation(cz.getAddress());
			return cz;
		}
		
		return null;
	}
	
	private WaterMeter selectedMeter() {
		String sql = " AND mt.meterno='"+getMeterNo()+"' AND mt.isactivem=1";
		String[] params = new String[0];
		for(WaterMeter cz : WaterMeter.retrieve(sql, params)) {
			return cz;
		}
		return null;
	}
	
	public List<String> payorNameSuggested(String query){
		List<String> result = new ArrayList<>();
		if(query!=null && !query.isEmpty()) {
			int size = query.length();
			if(size>=4) {
				String sql = " AND cus.fullname like '%"+query+"%'";
				String[] params = new String[0];
				for(Customer cz : Customer.retrieve(sql, params)) {
					result.add(cz.getFullName());
				}
			}
		}
		return result;
	}
	
	public String getAccountNumber() {
		if(accountNumber==null) {
			accountNumber = WaterAccnt.accountNumber(0);
		}
		return accountNumber;
	}


	public String getOwnerName() {
		return ownerName;
	}


	public Date getDateRegistered() {
		if(dateRegistered==null) {
			dateRegistered = DateUtils.getDateToday();
		}
		return dateRegistered;
	}


	public String getMeterNo() {
		return meterNo;
	}


	public int getConsumption() {
		return consumption;
	}


	public double getAmountConsumed() {
		return amountConsumed;
	}


	public double getAmountDue() {
		return amountDue;
	}


	public Date getDueDate() {
		if(dueDate==null) {
			dueDate = DateUtils.getDateToday();
		}
		return dueDate;
	}


	public Date getDisconnectiondate() {
		if(disconnectiondate==null) {
			disconnectiondate = DateUtils.getDateToday();
		}
		return disconnectiondate;
	}


	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}


	public void setDateRegistered(Date dateRegistered) {
		this.dateRegistered = dateRegistered;
	}


	public void setMeterNo(String meterNo) {
		this.meterNo = meterNo;
	}



	public void setConsumption(int consumption) {
		this.consumption = consumption;
	}


	public void setAmountConsumed(double amountConsumed) {
		this.amountConsumed = amountConsumed;
	}


	public void setAmountDue(double amountDue) {
		this.amountDue = amountDue;
	}


	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}


	public void setDisconnectiondate(Date disconnectiondate) {
		this.disconnectiondate = disconnectiondate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<WaterAccnt> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<WaterAccnt> accounts) {
		this.accounts = accounts;
	}

	public WaterAccnt getAccountSelected() {
		return accountSelected;
	}

	public void setAccountSelected(WaterAccnt accountSelected) {
		this.accountSelected = accountSelected;
	}

	public Date getDateBill() {
		if(dateBill==null) {
			dateBill = DateUtils.getDateToday();
		}
		return dateBill;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setDateBill(Date dateBill) {
		this.dateBill = dateBill;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public int getCurrentReading() {
		return currentReading;
	}

	public int getPreviousReading() {
		return previousReading;
	}

	public void setCurrentReading(int currentReading) {
		this.currentReading = currentReading;
	}

	public void setPreviousReading(int previousReading) {
		this.previousReading = previousReading;
	}

	public WaterBill getSelectedBill() {
		return selectedBill;
	}

	public void setSelectedBill(WaterBill selectedBill) {
		this.selectedBill = selectedBill;
	}

	public int getStatusId() {
		if(statusId==0) {
			statusId = 1;//unpaid
		}
		return statusId;
	}

	public List getStatus() {
		
		status = new ArrayList<>();
		
		for(WaterBillStatus s : WaterBillStatus.values()) {
			status.add(new SelectItem(s.getId(), s.getName()));
		}
		
		return status;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public void setStatus(List status) {
		this.status = status;
	}

	public double getPreviousUnpaidAmount() {
		return previousUnpaidAmount;
	}

	public void setPreviousUnpaidAmount(double previousUnpaidAmount) {
		this.previousUnpaidAmount = previousUnpaidAmount;
	}
	
}
