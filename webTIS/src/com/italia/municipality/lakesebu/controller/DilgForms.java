package com.italia.municipality.lakesebu.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @since 11-16-2020
 * @version 1.0
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class DilgForms {

	private int id;
	private int year;
	private int quarter;
	private String dateUploaded;
	private String fileName;
	private int isActive;
	
	
}
