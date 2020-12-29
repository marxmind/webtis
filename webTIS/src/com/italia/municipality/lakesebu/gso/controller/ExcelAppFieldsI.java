package com.italia.municipality.lakesebu.gso.controller;

import com.italia.municipality.lakesebu.controller.Department;

public interface ExcelAppFieldsI {
	
	 int getCount();
	 long getId();
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
	
	 double getTotalUnitCost();
	 double getTotalQty();
	 double getGrandTotalUnitCost();
	
	 double getFirstQtrTotalQty();
	 double getFirstQtrTotalAmnt();
	 double getSecondQtrTotalQty();
	 double getSecondQtrTotalAmnt();
	 double getThirdQtrTotalQty();
	 double getThirdQtrTotalAmnt();
	 double getFourthQtrTotalQty();
	 double getFourthQtrTotalAmnt();
	
	 Department getDepartment();
	 String getUploadedDate();
}
