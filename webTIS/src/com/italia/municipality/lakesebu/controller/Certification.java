package com.italia.municipality.lakesebu.controller;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 6/4/2020
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Certification {

	private String id;
	private String mode;
	private String tdNo;
	private String assessedValue;
	private String annualTax;
	private String taxDue;
	private String penalty;
	private String total;
	private String datePaid;
	private String orNumber;
	private String yearPaid;
	
	private TaxPayerReceipt receipt;
	private String yearFrom;
	private String yearTo;
	
}
