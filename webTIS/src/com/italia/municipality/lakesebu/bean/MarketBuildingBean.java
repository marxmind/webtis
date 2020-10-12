package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.controller.MarketBuilding;
import com.italia.municipality.lakesebu.enm.BuildingType;
import com.italia.municipality.lakesebu.utils.Application;

/**
 * 
 * @author mark italia
 * @since 06/15/2018
 * @version 1.0
 */
@Named
@ViewScoped
public class MarketBuildingBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6546585795691L;
	private String name;
	private String stallNo;
	private double amount;
	
	private int typeId;
	private List types;
	
	private MarketBuilding selectedBuilding;
	private List<MarketBuilding> buildings = Collections.synchronizedList(new ArrayList<MarketBuilding>());
	
	@PostConstruct
	public void init() {
		
	}
	
	public void loadBuilding() {
		buildings = Collections.synchronizedList(new ArrayList<MarketBuilding>());
		buildings = MarketBuilding.retrieve("", new String[0]);
	}
	
	public void save() {
		boolean isOk = true;
		MarketBuilding build = new MarketBuilding();
		if(getSelectedBuilding()!=null) {
			build = getSelectedBuilding();
		}else {
			build.setIsActive(1);
		}
		
		if(getName()==null || getName().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Name");
		}
		
		if(isOk) {
			build.setName(getName());
			build.setStallNumber(getStallNo());
			build.setMonthlyRental(getAmount());
			build.setType(getTypeId());
			build.save();
			loadBuilding();
			clearFlds();
			Application.addMessage(1, "Success", "Successfully saved.");
		}
	}
	
	public void clickItem(MarketBuilding build) {
		setSelectedBuilding(build);
		setName(build.getName());
		setStallNo(build.getStallNumber());
		setAmount(build.getMonthlyRental());
		setTypeId(build.getType());
	}
	
	public void deleteRow(MarketBuilding build) {
		build.delete();
		loadBuilding();
		clearFlds();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void clearFlds() {
		setSelectedBuilding(null);
		setName(null);
		setStallNo(null);
		setAmount(0);
		setTypeId(1);
		loadBuilding();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStallNo() {
		return stallNo;
	}
	public void setStallNo(String stallNo) {
		this.stallNo = stallNo;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getTypeId() {
		if(typeId==0) {
			typeId = BuildingType.NEW_BUILD.getId();
		}
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public List getTypes() {
		types = new ArrayList<>();
		for(BuildingType type : BuildingType.values()) {
			types.add(new SelectItem(type.getId(), type.getName()));
		}
		return types;
	}
	public void setTypes(List types) {
		this.types = types;
	}
	public MarketBuilding getSelectedBuilding() {
		return selectedBuilding;
	}
	public void setSelectedBuilding(MarketBuilding selectedBuilding) {
		this.selectedBuilding = selectedBuilding;
	}
	public List<MarketBuilding> getBuildings() {
		return buildings;
	}
	public void setBuildings(List<MarketBuilding> buildings) {
		this.buildings = buildings;
	}
	
}
