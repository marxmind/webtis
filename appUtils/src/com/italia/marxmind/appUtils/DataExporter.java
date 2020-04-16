package com.italia.marxmind.appUtils;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;

/**
 * 
 * @author Mark Italia
 * @Version 1.0
 * @since 02/13/2020
 *
 */
@Named
@ViewScoped
public class DataExporter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5467685644547571L;
	
	private ExcelOptions excelOpt;
	private PDFOptions pdfOpt;
	
	public ExcelOptions getExcelOpt() {
		
		excelOpt = new ExcelOptions();
		excelOpt.setFacetBgColor("#050505");
		excelOpt.setFacetFontSize("12");
		excelOpt.setFacetFontColor("#f9fafa");
		excelOpt.setFacetFontStyle("BOLD");
		excelOpt.setCellFontColor("#050505");
		excelOpt.setCellFontSize("10");
		
		return excelOpt;
	}
	
	public void postProcessXLS(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
			header.getCell(i).setCellStyle(cellStyle);
		}
	}
	
	public PDFOptions getPdfOpt(){
		pdfOpt = new PDFOptions();
		pdfOpt.setFacetBgColor("#050505");
		pdfOpt.setFacetFontSize("10");
		pdfOpt.setFacetFontColor("#f9fafa");
		pdfOpt.setFacetFontStyle("BOLD");
		pdfOpt.setCellFontColor("#050505");
		pdfOpt.setCellFontSize("8");
		
		return pdfOpt;
	}
	
	public void preProcessPDF(Object document) throws IOException,
		BadElementException, DocumentException {
		Document pdf = (Document) document;
		pdf.open();
		pdf.setPageSize(PageSize.A4);
		pdf.addTitle("Record Details");
		
	}
}
