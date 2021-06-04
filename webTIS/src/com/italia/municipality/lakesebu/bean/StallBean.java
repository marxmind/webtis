package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.controller.BuildingStall;
import com.italia.municipality.lakesebu.controller.Customer2;
import com.italia.municipality.lakesebu.controller.MarketBuilding;
import com.italia.municipality.lakesebu.enm.StallType;
import com.italia.municipality.lakesebu.licensing.controller.Customer;
import com.italia.municipality.lakesebu.utils.Application;

/**
 * 
 * @author mark italia
 * @since 06/16/2018
 * @version 1.0
 */
@Named
@ViewScoped
public class StallBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 68976542441L;
	
	private String stallName;
	
	private int typeId;
	private List types;
	
	private List customers;
	private long customerId;
	
	private List buildings;
	private int buildingId;
	
	private boolean occupied;
	private double rental;
	
	private List<BuildingStall> stalls = Collections.synchronizedList(new ArrayList<BuildingStall>());
	private BuildingStall selectedStall;
	private String searchName;
	
	@PostConstruct
	public void init() {
		
	}
	
	public void loadStall() {
		stalls = Collections.synchronizedList(new ArrayList<BuildingStall>());
		String sql = "";
		String[] params = new String[0];
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
			sql += " AND cuz.isactivatecus=1 AND cuz.fullname like '%"+ getSearchName().replace("--", "") +"%'";
		}
		stalls = BuildingStall.retrieve(sql, params);
	}
	
	public void save() {
		boolean isOk = true;
		BuildingStall stall = new BuildingStall();
		if(getSelectedStall()!=null) {
			stall = getSelectedStall();
		}else {
			stall.setIsActive(1);
		}
		
		if(getStallName()==null || getStallName().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide stall name");
		}
		
		if(getCustomerId()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide owner name");
		}
		
		if(getBuildingId()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide building name");
		}
		
		if(getRental()<=0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide rental amount");
		}
		
		if(isOk) {
			
			stall.setName(getStallName().trim().toUpperCase());
			stall.setIsOccupied(isOccupied()==true? 1 : 0);
			stall.setMonthlyRental(getRental());
			stall.setType(getTypeId());
			
			Customer customer = new Customer();
			customer.setId(getCustomerId());
			stall.setCustomer(customer);
			
			MarketBuilding building = new MarketBuilding();
			building.setId(getBuildingId());
			stall.setBuilding(building);
			
			stall.save();
			loadStall();
			clearFlds();
			Application.addMessage(1, "Success", "Successfully saved.");
		}
		
	}
	
	public void clickItem(BuildingStall stall) {
		setSelectedStall(stall);
		setCustomerId(stall.getCustomer().getId());
		setBuildingId(stall.getBuilding().getId());
		setStallName(stall.getName());
		setRental(stall.getMonthlyRental());
		setOccupied(stall.getIsOccupied()==1? true : false);
		setTypeId(stall.getType());
	}
	
	public void deleteRow(BuildingStall stall) {
		stall.delete();
		loadStall();
		clearFlds();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void clearFlds() {
		setSelectedStall(null);
		setCustomerId(0);
		setBuildingId(0);
		setStallName(null);
		setRental(0);
		setOccupied(false);
		setTypeId(0);
		loadStall();
	}
	
	public List getCustomers() {
		customers = new ArrayList<>();
		customers.add(new SelectItem(0, "Select..."));
		for(Customer cus : Customer.retrieve("", new String[0])) {
			customers.add(new SelectItem(cus.getId(), cus.getFullname()));
		}
		
		return customers;
	}
	public void setCustomers(List customers) {
		customers = customers;
	}
	public long getCustomerId() {
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
	public String getStallName() {
		return stallName;
	}
	public void setStallName(String stallName) {
		this.stallName = stallName;
	}
	public int getTypeId() {
		if(typeId==0) {
			typeId = 1;
		}
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public List getTypes() {
		types = new ArrayList<>();
		for(StallType type : StallType.values()) {
			types.add(new SelectItem(type.getId(), type.getName()));
		}
		return types;
	}
	public void setTypes(List types) {
		this.types = types;
	}
	public boolean isOccupied() {
		return occupied;
	}
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public List<BuildingStall> getStalls() {
		return stalls;
	}

	public void setStalls(List<BuildingStall> stalls) {
		this.stalls = stalls;
	}

	public BuildingStall getSelectedStall() {
		return selectedStall;
	}

	public void setSelectedStall(BuildingStall selectedStall) {
		this.selectedStall = selectedStall;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public double getRental() {
		return rental;
	}

	public void setRental(double rental) {
		this.rental = rental;
	}

}
