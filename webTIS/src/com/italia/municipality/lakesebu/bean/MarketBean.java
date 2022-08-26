package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;

import com.italia.municipality.lakesebu.controller.MarketTenant;
import com.italia.municipality.lakesebu.controller.MarketTenantProperty;
import com.italia.municipality.lakesebu.enm.BuildingType;
import com.italia.municipality.lakesebu.enm.Buildings;
import com.italia.municipality.lakesebu.enm.StallType;
import com.italia.municipality.lakesebu.utils.Application;
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
public class MarketBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4365643343431L;
	
	////////////////////////////////////////////TENANT//////////////////////////////////////
	@Setter @Getter private MarketTenant tenant;
	@Setter @Getter private List<MarketTenant> tenants;
	
	
	///////////////////////////////////////TENANT PROPERTY INFO/////////////////////////////
	@Setter @Getter private MarketTenantProperty property;
	@Setter @Getter private List<MarketTenantProperty> properties;
	@Setter @Getter private List buildings;
	@Setter @Getter private List buildingTypes;
	@Setter @Getter private List stallTypes;
	@Setter @Getter private List tenantOwners;
	
	@PostConstruct
	public void init() {
		loadDefault();
		loadDefaultProperty();
		loadTenant();
	}
	
	public void loadTenant() {
		String sql = " ORDER BY tid DESC";
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
			loadTenant();
		}else if("Tenant Property Info".equalsIgnoreCase(event.getTab().getTitle())) {
			loadProperty();
		}else if("Tenant Payment Info".equalsIgnoreCase(event.getTab().getTitle())) {
			
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
			
			if(tenant.getContactNo()==null || tenant.getContactNo().isBlank()) {
				isOk = false;
				Application.addMessage(3, "Error", "Please provide contact no");
			}
			
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
		String sql = " ORDER BY prp.prid DESC";
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
				loadProperty();
				Application.addMessage(1, "Success", "Successfully saved.");
			}
			
		}
	}
	
	public void clickItemProperty(MarketTenantProperty prop) {
		property.setAquiredDateTmp(DateUtils.convertDateString(prop.getDateAquired(), "yyyy-MM-dd"));
		if(prop.getDateReturned()!=null) {
			property.setRetiredDateTmp(DateUtils.convertDateString(prop.getDateReturned(), "yyyy-MM-dd"));
		}
		property = prop;
	}
	
	public void deleteProperty(MarketTenantProperty prop) {
		prop.delete();
		properties.remove(prop);
		Application.addMessage(1, "Success", "Successfully deleted");
	}
}
