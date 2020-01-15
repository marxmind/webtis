package com.italia.municipality.lakesebu.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * 
 * @author Mark Italia
 * @since 02/21/2019
 * @version 1.0
 *
 */

public class ReadDashboardInfo {
	
	public static final String CONFIG_FILE_NAME="dashboard-data.tis";
	
	public static void main(String[] args) {
		
	}
	
	public static Map<String, Double> getInfo(String key){
		Map<String, Double> info = Collections.synchronizedMap(new HashMap<String, Double>());
		
		String fileName = System.getenv("SystemDrive");
		fileName += File.separator + "webtis" + File.separator + "conf" + File.separator;
		fileName += CONFIG_FILE_NAME;
		
		System.out.println("File=" + fileName);
		
		Properties prop = new Properties();
		if("collection-last-year".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("collection-last-year").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		
		}else if("collection-this-year".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("collection-this-year").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		
		}else if("forms-last-year".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("forms-last-year").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		
		}else if("forms-this-year".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("forms-this-year").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		
		}else if("forms-last-year".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("forms-last-year").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		
		}else if("forms-this-year".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("forms-this-year").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		
		}else if("collector-data".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("collector-data").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		
		}
		
		Map<String, Double> sorted = new TreeMap<String, Double>(info);
		
		return sorted;
		
	}
	
}
