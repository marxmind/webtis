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

import com.italia.municipality.lakesebu.controller.BuildingOwnerHistory;
import com.italia.municipality.lakesebu.controller.BuildingStall;
import com.italia.municipality.lakesebu.controller.Customer;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.MarketBuilding;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 06/15/2018
 * @version 1.0
 */
@Named
@ViewScoped
public class AssBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4546588536361L;
	
	private Date dateStart;
	private Date dateEnd;
	
	private List customers;
	private long customerId;
	
	private List buildings;
	private int buildingId;
	
	private List stalls;
	private int stallId;
	
	private BuildingOwnerHistory selectedOwner;
	private String searchName;
	private List<BuildingOwnerHistory> owners = Collections.synchronizedList(new ArrayList<BuildingOwnerHistory>());
	
	@PostConstruct
	public void init() {
		
	}
	
	public void loadHistoryBuilding() {
		owners = Collections.synchronizedList(new ArrayList<BuildingOwnerHistory>());
		String sql = "";
		String[] params = new String[0];
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
			sql += " AND cuz.fullname like '%"+ getSearchName().replace("--", "") +"%'";
		}
		owners = BuildingOwnerHistory.retrieve(sql, params);
	}
	
	public void save() {
		boolean isOk = true;
		BuildingOwnerHistory own = new BuildingOwnerHistory();
		if(getSelectedOwner()!=null) {
			own= getSelectedOwner();
		}else {
			own.setIsActive(1);
		}
		
		if(getCustomerId()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please add Customer");
		}
		if(getBuildingId()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please add Building");
		}
		
		if(isOk) {
			own.setDateStart(DateUtils.convertDate(getDateStart(), "yyyy-MM-dd"));
			if(getDateEnd()!=null) {
				own.setDateStart(DateUtils.convertDate(getDateEnd(), "yyyy-MM-dd"));
			}
			own.setIsCurrentOwner(1);
			
			Customer customer = new Customer();
			customer.setId(getCustomerId());
			own.setCustomer(customer);
			
			MarketBuilding building = new MarketBuilding();
			building.setId(getBuildingId());
			own.setMarketBuilding(building);
			
			BuildingStall stall = new BuildingStall();
			stall.setId(getStallId());
			own.setStall(stall);
			
			UserDtls user = Login.getUserLogin().getUserDtls();
			own.setUserDtls(user);
			
			own.save();
			loadHistoryBuilding();
			clearFlds();
			Application.addMessage(1, "Success", "Successfully saved.");
		}
	}
	
	public void clickItem(BuildingOwnerHistory own) {
		setDateStart(DateUtils.convertDateString(own.getDateStart(),"yyyy-MM-dd"));
		if(own.getDateEnd()!=null) {
			setDateEnd(DateUtils.convertDateString(own.getDateEnd(),"yyyy-MM-dd"));
		}else {
			setDateEnd(null);
		}
		setSelectedOwner(own);
		setCustomerId(own.getCustomer().getId());
		setBuildingId(own.getMarketBuilding().getId());
		setStallId(own.getStall().getId());
	}
	
	public void deleteRow(BuildingOwnerHistory own) {
		own.delete();
		loadHistoryBuilding();
		clearFlds();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void clearFlds() {
		setDateStart(null);
		setDateEnd(null);
		setSelectedOwner(null);
		setCustomerId(0);
		setBuildingId(0);
		loadHistoryBuilding();
	}
	
	public Date getDateStart() {
		if(dateStart==null) {
			dateStart = DateUtils.getDateToday();
		}
		return dateStart;
	}
	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}
	public Date getDateEnd() {
		return dateEnd;
	}
	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}
	public List getCustomers() {
		customers = new ArrayList<>();
		customers.add(new SelectItem(0, "Select..."));
		for(Customer cus : Customer.retrieve("", new String[0])) {
			customers.add(new SelectItem(cus.getId(), cus.getFullName()));
		}
		
		return customers;
	}
	public void setCustomers(List customers) {
		customers = customers;
	}
	public long getCustomerId() {
		if(customerId==0) {
			customerId = 1;
		}
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public List getBuildings() {
		buildings = new ArrayList<>();
		buildings.add(new SelectItem(0, "Select..."));
		for(MarketBuilding build : MarketBuilding.retrieve("", new String[0])) {
			buildings.add(new SelectItem(build.getId(), build.getName()));
		}
		
		return buildings;
	}
	public void setBuildings(List buildings) {
		this.buildings = buildings;
	}
	public int getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(int buildingId) {
		this.buildingId = buildingId;
	}

	public BuildingOwnerHistory getSelectedOwner() {
		return selectedOwner;
	}

	public void setSelectedOwner(BuildingOwnerHistory selectedOwner) {
		this.selectedOwner = selectedOwner;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public List<BuildingOwnerHistory> getOwners() {
		return owners;
	}

	public void setOwners(List<BuildingOwnerHistory> owners) {
		this.owners = owners;
	}

	public List getStalls() {
		stalls = new ArrayList<>();
		stalls.add(new SelectItem(0, "Select..."));
		for(BuildingStall stall : BuildingStall.retrieve(" AND st.isoccupied=1 AND bl.isactivatebldg=1 AND bl.bldgid="+getBuildingId() + " AND cuz.customerid="+getCustomerId(), new String[0])) {
			stalls.add(new SelectItem(stall.getId(), stall.getName()));
		}
		return stalls;
	}

	public void setStalls(List stalls) {
		this.stalls = stalls;
	}

	public int getStallId() {
		return stallId;
	}

	public void setStallId(int stallId) {
		this.stallId = stallId;
	}

}
