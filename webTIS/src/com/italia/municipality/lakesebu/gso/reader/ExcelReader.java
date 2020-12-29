package com.italia.municipality.lakesebu.gso.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.italia.municipality.lakesebu.utils.Numbers;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 04/09/2020
 *
 */
public class ExcelReader {
	
	public static Map<Integer, Map<Integer, ExcelAppFields>> loadFile(File file, String ext,int sheetNo, int startRow) {
		System.out.println("loading file content file");
		if(FileExtension.XLS.getName().equalsIgnoreCase(ext)) {
			return readXLSFile(file, sheetNo, startRow);
		}else if(FileExtension.XLSX.getName().equalsIgnoreCase(ext)) {
			return readXLSXFile(file, sheetNo, startRow);
		}
		
		return null;
	}
	
	private static  Map<Integer, Map<Integer, ExcelAppFields>> readXLSFile(File file, int sheetNo, int startRow) {
		try {
			Map<Integer, Map<Integer, ExcelAppFields>> dataMap = new HashMap<Integer, Map<Integer, ExcelAppFields>>();
			Map<Integer, ExcelAppFields> data = new HashMap<Integer, ExcelAppFields>();
			FileInputStream fin = new FileInputStream(file);
			HSSFWorkbook wb = new HSSFWorkbook(fin);
			HSSFSheet sheet = wb.getSheetAt(sheetNo);
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
			int countRow = 0;
			for(Row row : sheet) {
				if(countRow>=startRow) {
					int countCell = 0;
					ExcelAppFields fld = new ExcelAppFields();
				for(Cell cell : row) {
					switch(evaluator.evaluateInCell(cell).getCellType()) {
					case NUMERIC :{ double value=cell.getNumericCellValue();
									fld = dataCollected(countCell, value, fld);
									break;
									}			  
					case STRING : {String value=cell.getStringCellValue(); 
									fld = dataCollected(countCell, value, fld);
									break;
									}
					case BLANK : {String value=cell.getStringCellValue(); 
									fld = dataCollected(countCell, value, fld);
									break;
									}
				}
					countCell++;
				}
				data.put(countRow, fld);
				}
				countRow++;
			}
			dataMap.put(sheetNo, data);
			return dataMap;
			
		}catch(FileNotFoundException fne) {
			
		}catch(IOException io) {
			
		}
		
		return null;
	}
	
	private static  Map<Integer, Map<Integer, ExcelAppFields>> readXLSXFile(File file, int sheetNo, int startRow) {
		try {
			Map<Integer, Map<Integer, ExcelAppFields>> dataMap = new HashMap<Integer, Map<Integer, ExcelAppFields>>();
			Map<Integer, ExcelAppFields> data = new HashMap<Integer, ExcelAppFields>();
			FileInputStream fin = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(fin);
			XSSFSheet sheet = wb.getSheetAt(sheetNo);
			
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
			int countRow = 0;
			for(Row row : sheet) {
				
				if(countRow>=startRow) {
					int countCell = 0;
					//String value = "";
					ExcelAppFields fld = new ExcelAppFields();
				for(Cell cell : row) {
					
					switch(evaluator.evaluateInCell(cell).getCellType()) {
						case NUMERIC :{ double value=cell.getNumericCellValue();
										fld = dataCollected(countCell, value, fld);
										break;
										}			  
						case STRING : {String value=cell.getStringCellValue(); 
										fld = dataCollected(countCell, value, fld);
										break;
										}
						case BLANK : {String value=cell.getStringCellValue(); 
										fld = dataCollected(countCell, value, fld);
										break;
										}
					}
					countCell++;
				}
				data.put(countRow, fld);
				
				}
				countRow++;
			}
			dataMap.put(sheetNo, data);
			return dataMap;
			
			/**
			Iterator<Row> itr = sheet.iterator(); 
			while(itr.hasNext()) {
				Row row = itr.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				
				while(cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch(cell.getCellType()) {
					case NUMERIC : System.out.print(cell.getNumericCellValue()+"\t\t"); break;
					case STRING : System.out.print(cell.getStringCellValue()+"\t\t"); break;
					case BLANK : System.out.print(cell.getStringCellValue()+"\t\t"); break;
					}
				}
				System.out.println();
				
			}**/
			
		}catch(FileNotFoundException fne) {
			
		}catch(IOException io) {
			
		}
		
		return null;
	}
	
	private static ExcelAppFields dataCollected(int column, String value, ExcelAppFields fld) {
		switch(column) {
		case 0 : {
			fld.setId(Long.valueOf(value));
			break;
		}
		case 1 : {
				fld.setDescription(value);
				break;
			}
		
		case 4 : {
				fld.setUnit(value);
				break;
			}
		}
		return fld;
	}
	
	private static ExcelAppFields dataCollected(int column, double value, ExcelAppFields fld) {
		value = Numbers.formatDouble(value);
		switch(column) {
		
		case 2 : {
				fld.setUnitCost(value);
				break;
			}
		case 3 : {
				fld.setQuantity(value);
				break;
			}	
		
		case 5 : {
				fld.setTotalCost(value);
				break;
			}
		case 6 : {
				fld.setFirstQtrQty(value);
				break;
			}
		case 7 : {
				fld.setFirstQtrAmnt(value);
				break;
			}
		case 8 : {
				fld.setSecondQtrQty(value);
				break;
			}
		case 9 : {
				fld.setSecondQtrAmnt(value);
				break;
			}
		case 10 : {
				fld.setThirdQtrQty(value);
				break;
			}
		case 11 : {
				fld.setThirdQtrAmnt(value);
				break;
			}
		case 12 : {
				fld.setFourthQtrQty(value);
				break;
			}
		case 13 : {
				fld.setFourthQtrAmnt(value);
				break;
			}
		}
		
		
		return fld;
	}
	
}
