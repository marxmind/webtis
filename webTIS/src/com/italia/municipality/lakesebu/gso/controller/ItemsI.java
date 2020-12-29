package com.italia.municipality.lakesebu.gso.controller;

import com.italia.municipality.lakesebu.controller.Department;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 04/22/2020
 *
 */
public interface ItemsI {
	 long getId();
	 String getTransDate();
	 String getType();
	 String getDescription();
	 double getQuantity();
	 String getUnit();
	 double getUnitCost();
	 double getTotalCost();
	 double getFirstQtrQty();
	 double getFirstQtrAmnt();
	 double getSecondQtrQty();
	 double getSecondQtrAmnt();
	 double getThirdQtrQty();
	 double getThirdQtrAmnt();
	 double getFourthQtrQty();
	 double getFourthQtrAmnt();
	 Department getDepartment();
	 int getIsActive();
}
