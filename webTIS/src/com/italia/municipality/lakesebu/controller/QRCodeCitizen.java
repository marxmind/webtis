package com.italia.municipality.lakesebu.controller;

import java.io.FileInputStream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class QRCodeCitizen {

	private FileInputStream f1;
	private FileInputStream f2;
	private FileInputStream f3;
	
}
