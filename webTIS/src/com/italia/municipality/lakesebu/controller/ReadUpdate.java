package com.italia.municipality.lakesebu.controller;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Deprecated
public class ReadUpdate {

	private String fileName="";
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void readXML(){
		try{
			setFileName("D:\\CheckSystem\\update.xml");
		String fileName = 	getFileName();
		File xmlFile = new File(fileName);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
		Document doc = documentBuilder.parse(xmlFile);
		
		/////////////normalize
		doc.getDocumentElement().normalize();
		System.out.println("Get field name: "+ doc.getDocumentElement().getNodeName());
		
		NodeList ls = doc.getElementsByTagName("update");
		for(int i=0; i<ls.getLength(); i++){
			Node n = ls.item(i);
			System.out.println("Current Node: "+ n.getNodeName());
			
			if(n.getNodeType() == Node.ELEMENT_NODE){
				Element e = (Element)n;
				System.out.println("Update field id : " + e.getAttribute("id"));
				System.out.println("Pages: " + e.getElementsByTagName("pages").item(0).getTextContent() );
				System.out.println("Database: " + e.getElementsByTagName("database").item(0).getTextContent() );
				
			}
			
			
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ReadUpdate update = new ReadUpdate();
		update.readXML();
	}
}
