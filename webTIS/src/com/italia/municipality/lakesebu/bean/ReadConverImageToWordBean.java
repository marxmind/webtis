package com.italia.municipality.lakesebu.bean;

import java.io.File;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ReadConverImageToWordBean {

	public static void main(String[] args) {
		
		Tesseract tesseract = new Tesseract();
		try {
		 
			tesseract.setDatapath("C:\\AA-Files\\tesseract");
			
			String text = tesseract.doOCR(new File("word.png"));
		 
			System.out.println("read: " + text);
		}catch(TesseractException e) { 
			e.printStackTrace();
		}
	}

}
