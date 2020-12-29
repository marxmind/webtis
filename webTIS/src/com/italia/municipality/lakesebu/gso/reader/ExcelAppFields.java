package com.italia.municipality.lakesebu.gso.reader;

import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.gso.controller.ExcelAppFieldsI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 04/09/2020
 *
 */

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class ExcelAppFields implements ExcelAppFieldsI{
	
	private int count;
	
	private long id;
	private String type;
	private String description;
	private double quantity;
	private String unit;
	private double unitCost;
	private double totalCost;
	private double firstQtrQty;
	private double firstQtrAmnt;
	private double secondQtrQty;
	private double secondQtrAmnt;
	private double thirdQtrQty;
	private double thirdQtrAmnt;
	private double fourthQtrQty;
	private double fourthQtrAmnt;
	
	private double totalUnitCost;
	private double totalQty;
	private double grandTotalUnitCost;
	
	private double firstQtrTotalQty;
	private double firstQtrTotalAmnt;
	private double secondQtrTotalQty;
	private double secondQtrTotalAmnt;
	private double thirdQtrTotalQty;
	private double thirdQtrTotalAmnt;
	private double fourthQtrTotalQty;
	private double fourthQtrTotalAmnt;
	
	private Department department;
	private String uploadedDate;
	
	
}
