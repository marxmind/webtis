package com.italia.municipality.lakesebu.gso.reader;
/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 04/09/2020
 *
 */
public enum FileExtension {
	
	XLS("xls"),
	XLSX("xlsx"),
	DOC("doc"),
	DOCX("docx");
	
	private FileExtension(String name) { 
		this.name = name;
	}
	
	private String name;
	
	public String getName() {
		return name;
	}
}
