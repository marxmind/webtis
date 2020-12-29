package com.italia.municipality.lakesebu.gso.bean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;


import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.global.GlobalVar;
import com.italia.municipality.lakesebu.gso.reader.ExcelAppFields;
import com.italia.municipality.lakesebu.gso.reader.ExcelReader;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.utils.Numbers;

import lombok.Getter;
import lombok.Setter;





/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 04/09/2020
 *
 */
@Named
@ViewScoped
public class UploadBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 156344343434L;
	@Getter @Setter private int departmentId;
	@Getter @Setter private List departments;
	@Getter @Setter private List<ExcelAppFields> dataList;
	@Getter @Setter private List<ExcelAppFields> dataListOriginal;
	@Getter @Setter private String searchParameter;
	
	@Getter @Setter private double totalUnitCost;
	@Getter @Setter private double totalQty;
	@Getter @Setter private double grandTotalUnitCost;
	
	@Getter @Setter private double firstQtrTotalQty;
	@Getter @Setter private double firstQtrTotalAmnt;
	@Getter @Setter private double secondQtrTotalQty;
	@Getter @Setter private double secondQtrTotalAmnt;
	@Getter @Setter private double thirdQtrTotalQty;
	@Getter @Setter private double thirdQtrTotalAmnt;
	@Getter @Setter private double fourthQtrTotalQty;
	@Getter @Setter private double fourthQtrTotalAmnt;
	
	@Getter @Setter private List<ExcelAppFields> selectedItems = new ArrayList<ExcelAppFields>();
	@Getter @Setter private boolean checkAll;
	@Getter @Setter private boolean departmentSelected=true;
	
	@Getter @Setter private Map<Integer, Department> mapDepartment = new HashMap<Integer, Department>();
	
	@PostConstruct
	public void init() {
		clearNew();
		departments = new ArrayList<>();
		departments.add(new SelectItem(0, "Select..."));
		for(Department d : Department.retrieve("SELECT * FROM Department", new String[0])) {
			departments.add(new SelectItem(d.getDepid(), d.getDepartmentName()));
			getMapDepartment().put(d.getDepid(), d);
		}
	}
	
	public void departmentListener() {
		if(getDepartmentId()>0) {
			setDepartmentSelected(false);
		}else {
			setDepartmentSelected(true);
		}
	}
	
	public void checkListener() {
		
		if(isCheckAll()) {
			selectedItems = new ArrayList<ExcelAppFields>();
			for(ExcelAppFields f : getDataList()) {
				getSelectedItems().add(f);
			}
			Application.addMessage(1, "Checking", "Check item/s " + getSelectedItems().size());
		}else {
			selectedItems = new ArrayList<ExcelAppFields>();
			Application.addMessage(1, "Checking", "Uncheck item/s " + getDataList().size());
		}
	}
	
	public void saveSelected() {
		if(getDataList()!=null && getDataList().size()>0) {
			int count = 0;
			if(getSelectedItems().size()>0) {
				for(ExcelAppFields e : getSelectedItems()) {
					count++;
					getDataList().remove(e);
					getDataListOriginal().remove(e);
				}
				selectedItems = new ArrayList<ExcelAppFields>();
				setCheckAll(false);
				calculate(getDataList());
				
			}else {
				for(ExcelAppFields e : getDataList()) {
					count++;
				}
				clearNew();
				
			}
			Application.addMessage(1, "Success", count + " have been successfully saved");
		}else {
			Application.addMessage(2, "Failed", "No item/s have been selected");
		}
	}
	
	public void clearNew() {
		selectedItems = new ArrayList<ExcelAppFields>();
		dataList = new ArrayList<ExcelAppFields>();
		dataListOriginal = new ArrayList<ExcelAppFields>();
		setDepartmentId(0);
		setDepartmentSelected(true);
		setCheckAll(false);
		resetValue();
	}
	
	public void deleteSelected() {
		if(getSelectedItems()!=null && getSelectedItems().size()>0) {
			for(ExcelAppFields e : getSelectedItems()) {
				getDataList().remove(e);
				getDataListOriginal().remove(e);
			}
			Application.addMessage(1, "Success", "Successfully deleted");
		}else {
			Application.addMessage(2, "Failed", "No item/s have been selected");
		}
	}
	
	public void search() {
		dataList = new ArrayList<ExcelAppFields>();
		int count = 1;
		resetValue();
		for(ExcelAppFields e : getDataListOriginal()) {
			if(e.getDescription().toLowerCase().startsWith(getSearchParameter().toLowerCase())) {
				e.setCount(count);
				dataList.add(e);
				calculateFooterAmount(e);
				count++;
			}
		}
		
		
		if((dataList==null || dataList.size()==0) && (getSearchParameter()==null || getSearchParameter().isBlank())) {
			dataList = getDataListOriginal();
			for(ExcelAppFields e : getDataListOriginal()) {
				calculateFooterAmount(e);
			}
		}
		
		convertDouble();
	}
	
	public void uploadFile(FileUploadEvent event){
		System.out.println("uploading file");
		 try {
			 InputStream stream = event.getFile().getInputStream();
			 String ext = FilenameUtils.getExtension(event.getFile().getFileName());
			 String file = event.getFile().getFileName();
			 
			 if(writeDocToFile(stream,ext)){
				 Application.addMessage(1, "Success", "Data has been successfully uploaded");
			 }else{
				 Application.addMessage(3, "Error", "Error uploading the data " + file);
			 }
			 
	     } catch (Exception e) {
	     
	     }
	}
	
	private boolean writeDocToFile(InputStream stream, String ext){
		System.out.println("writing file");
		try{
			String filename = "uploaded-" + DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "." + ext;	
			System.out.println("writing... writeDocToFile : " + filename);
			File fileDoc = new File(GlobalVar.UPLOADED_EXCEL_PATH_FOLDER +  filename);
			Path file = fileDoc.toPath();
			Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
			readingExcelFile(GlobalVar.UPLOADED_EXCEL_PATH_FOLDER +  filename, ext);
			return true;
		}catch(IOException e) {}
		return false;
	}
	private void readingExcelFile(String fileName, String ext) {
		System.out.println("readinf file content");
		File file = new File(fileName);
		dataList = new ArrayList<ExcelAppFields>();
		int sheetNo = 0;
		Map<Integer, Map<Integer, ExcelAppFields>> data = ExcelReader.loadFile(file, ext,sheetNo,10);
		int count = 1;
		resetValue();
		for(int row : data.get(sheetNo).keySet()) {
			ExcelAppFields fld = data.get(sheetNo).get(row);
			fld.setCount(count);
			fld.setId(Long.valueOf(count+""));
			dataList.add(fld);
			calculateFooterAmount(fld);
			count++;
		}
		dataListOriginal = dataList;
		convertDouble();
	}
	
	private void resetValue() {
		totalUnitCost = 0d;
		totalQty = 0d;
		grandTotalUnitCost = 0d;
		firstQtrTotalQty = 0d;
		firstQtrTotalAmnt = 0d;
		secondQtrTotalQty = 0d;
		secondQtrTotalAmnt = 0d;
		thirdQtrTotalQty = 0d;
		thirdQtrTotalAmnt = 0d;
		fourthQtrTotalQty = 0d;
		fourthQtrTotalAmnt = 0d;
	}
	
	private void convertDouble() {
		totalUnitCost = Numbers.formatDouble(getTotalUnitCost());
		totalQty = Numbers.formatDouble(getTotalQty());
		grandTotalUnitCost = Numbers.formatDouble(getGrandTotalUnitCost());
		firstQtrTotalQty = Numbers.formatDouble(getFirstQtrTotalQty());
		firstQtrTotalAmnt = Numbers.formatDouble(getFirstQtrTotalAmnt());
		secondQtrTotalQty = Numbers.formatDouble(getSecondQtrTotalQty());
		secondQtrTotalAmnt = Numbers.formatDouble(getSecondQtrTotalAmnt());
		thirdQtrTotalQty = Numbers.formatDouble(getThirdQtrTotalQty());
		thirdQtrTotalAmnt = Numbers.formatDouble(getThirdQtrTotalAmnt());
		fourthQtrTotalQty = Numbers.formatDouble(getFourthQtrTotalQty());
		fourthQtrTotalAmnt = Numbers.formatDouble(getFourthQtrTotalAmnt());
	}
	private void calculateFooterAmount(ExcelAppFields fld) {
		
		totalUnitCost += fld.getUnitCost();
		totalQty += fld.getQuantity();
		grandTotalUnitCost += fld.getTotalCost();
		
		firstQtrTotalQty += fld.getFirstQtrQty();
		firstQtrTotalAmnt += fld.getFirstQtrAmnt();
		secondQtrTotalQty += fld.getSecondQtrQty();
		secondQtrTotalAmnt += fld.getSecondQtrAmnt();
		thirdQtrTotalQty += fld.getThirdQtrQty();
		thirdQtrTotalAmnt += fld.getThirdQtrAmnt();
		fourthQtrTotalQty += fld.getFourthQtrQty();
		fourthQtrTotalAmnt += fld.getFourthQtrAmnt();
		
	}
	
	private void calculate(List<ExcelAppFields> flds) {
		resetValue();
		for(ExcelAppFields e : flds) {
			calculateFooterAmount(e);
		}
		convertDouble();
	}
	
	public void onRowEdit(RowEditEvent<ExcelAppFields> event) {
		
		ExcelAppFields e = (ExcelAppFields)event.getObject();
		
        Application.addMessage(1, "Sucess", "Successfully saved.");
        
        //if(getDataList().size()!=getDataListOriginal().size()) {
        	//updating data on original list
        	int index = Integer.valueOf(e.getId()+"") - 1;
        	getDataListOriginal().set(index, e);
        //}
        
    }
     
    public void onRowCancel(RowEditEvent<ExcelAppFields> event) {
        //FacesMessage msg = new FacesMessage("Edit Cancelled", event.getObject().getId());
        //FacesContext.getCurrentInstance().addMessage(null, msg);
    }
     
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
         
        if(newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
	
    /*
	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public List getDepartments() {
		return departments;
	}

	public void setDepartments(List departments) {
		this.departments = departments;
	}

	public List<ExcelAppFields> getDataList() {
		return dataList;
	}

	public void setDataList(List<ExcelAppFields> dataList) {
		this.dataList = dataList;
	}

	public List<ExcelAppFields> getDataListOriginal() {
		return dataListOriginal;
	}

	public void setDataListOriginal(List<ExcelAppFields> dataListOriginal) {
		this.dataListOriginal = dataListOriginal;
	}

	public String getSearchParameter() {
		return searchParameter;
	}

	public void setSearchParameter(String searchParameter) {
		this.searchParameter = searchParameter;
	}

	public double getTotalUnitCost() {
		return totalUnitCost;
	}

	public void setTotalUnitCost(double totalUnitCost) {
		this.totalUnitCost = totalUnitCost;
	}

	public double getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(double totalQty) {
		this.totalQty = totalQty;
	}

	public double getGrandTotalUnitCost() {
		return grandTotalUnitCost;
	}

	public void setGrandTotalUnitCost(double grandTotalUnitCost) {
		this.grandTotalUnitCost = grandTotalUnitCost;
	}

	public double getFirstQtrTotalQty() {
		return firstQtrTotalQty;
	}

	public void setFirstQtrTotalQty(double firstQtrTotalQty) {
		this.firstQtrTotalQty = firstQtrTotalQty;
	}

	public double getFirstQtrTotalAmnt() {
		return firstQtrTotalAmnt;
	}

	public void setFirstQtrTotalAmnt(double firstQtrTotalAmnt) {
		this.firstQtrTotalAmnt = firstQtrTotalAmnt;
	}

	public double getSecondQtrTotalQty() {
		return secondQtrTotalQty;
	}

	public void setSecondQtrTotalQty(double secondQtrTotalQty) {
		this.secondQtrTotalQty = secondQtrTotalQty;
	}

	public double getSecondQtrTotalAmnt() {
		return secondQtrTotalAmnt;
	}

	public void setSecondQtrTotalAmnt(double secondQtrTotalAmnt) {
		this.secondQtrTotalAmnt = secondQtrTotalAmnt;
	}

	public double getThirdQtrTotalQty() {
		return thirdQtrTotalQty;
	}

	public void setThirdQtrTotalQty(double thirdQtrTotalQty) {
		this.thirdQtrTotalQty = thirdQtrTotalQty;
	}

	public double getThirdQtrTotalAmnt() {
		return thirdQtrTotalAmnt;
	}

	public void setThirdQtrTotalAmnt(double thirdQtrTotalAmnt) {
		this.thirdQtrTotalAmnt = thirdQtrTotalAmnt;
	}

	public double getFourthQtrTotalQty() {
		return fourthQtrTotalQty;
	}

	public void setFourthQtrTotalQty(double fourthQtrTotalQty) {
		this.fourthQtrTotalQty = fourthQtrTotalQty;
	}

	public double getFourthQtrTotalAmnt() {
		return fourthQtrTotalAmnt;
	}

	public void setFourthQtrTotalAmnt(double fourthQtrTotalAmnt) {
		this.fourthQtrTotalAmnt = fourthQtrTotalAmnt;
	}

	public List<ExcelAppFields> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<ExcelAppFields> selectedItems) {
		this.selectedItems = selectedItems;
	}


	public boolean isCheckAll() {
		return checkAll;
	}


	public void setCheckAll(boolean checkAll) {
		this.checkAll = checkAll;
	}

	public boolean isDepartmentSelected() {
		return departmentSelected;
	}

	public void setDepartmentSelected(boolean departmentSelected) {
		this.departmentSelected = departmentSelected;
	}
	*/
	

	

}
