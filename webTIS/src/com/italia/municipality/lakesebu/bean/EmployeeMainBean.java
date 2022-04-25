package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.EmployeeMain;
import com.italia.municipality.lakesebu.enm.CivilStatus;
import com.italia.municipality.lakesebu.enm.EmployeeType;
import com.italia.municipality.lakesebu.enm.Gender;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 03/29/2022
 *
 */
@Named
@ViewScoped
public class EmployeeMainBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 14564678754534L;

	@Getter @Setter private EmployeeMain employee;
	
	@Getter @Setter private List types;
	@Getter @Setter private int typeId;
	@Getter @Setter private List genders;
	@Getter @Setter private int genderId;
	@Getter @Setter private List civils;
	@Getter @Setter private int civilId;
	@Getter @Setter private List deps;
	@Getter @Setter private int depId;
	@Getter @Setter private String searchParam;
	@Getter @Setter private List<EmployeeMain> employees;
	
	@PostConstruct
	public void init() {
		System.out.println("init...");
		defaultValue();
		loadData();
	}
	
	public void defaultValue() {
		
		types = new ArrayList<>();
		typeId = 1;
		for(EmployeeType t : EmployeeType.values()) {
			types.add(new SelectItem(t.getId(), t.getName()));
		}
		
		genders = new ArrayList<>();
		genderId = 1;
		for(Gender g : Gender.values()) {
			genders.add(new SelectItem(g.getId(), g.getName()));
		}
		
		civils = new ArrayList<>();
		civilId = 1;
		for(CivilStatus c : CivilStatus.values()) {
			civils.add(new SelectItem(c.getId(), c.getName()));
		}
		
		deps = new ArrayList<>();
		depId = 1;
		for(Department d : Department.retrieve("SELECT * FROM department WHERE isactivedep=1 ORDER BY departmentname ASC", new String[0])) {
			deps.add(new SelectItem(d.getDepid(), d.getDepartmentName()));
		}
		
		addDefaultEmployee();
	}
	
	private void addDefaultEmployee() {
		
		setDepId(1);
		setCivilId(1);
		
		EmployeeMain emp = EmployeeMain.builder()
				.employeeId(EmployeeMain.getLatestEmloyeeId(EmployeeType.REGULAR))
				.regDate(DateUtils.getCurrentDateYYYYMMDD())
				.tempRegDate(DateUtils.getDateToday())
				.firstName(null)
				.middleName(null)
				.lastName(null)
				.fullName("")
				.birthDate(DateUtils.getCurrentDateYYYYMMDD())
				.tempBirthDate(DateUtils.getDateToday())
				.civilStatus(1)
				.position("")
				.department(Department.department("1"))
				.cctsId("")
				.employeeType(getTypeId())
				.address("")
				.gender(1)
				.contactNo("")
				.signatureid("")
				.isResigned(0)
				.isActiveEmployee(1)
				.emergecnyContactDtls("")
				.photoid("")
				.build();
		
		setEmployee(emp);
	}
	
	public void loadData() {
		employees = new ArrayList<EmployeeMain>();
		String sql = "";
		String[] params = new String[0];
		
		if(getSearchParam()!=null && !getSearchParam().isBlank() && getSearchParam().length()>3) {
			sql = " AND emp.fullname like '%"+ getSearchParam() +"%'";
		}
		
		sql += " ORDER BY emp.eid DESC";
		
		employees = EmployeeMain.retrieve(sql, params);
	}
	
	
	public void updateEmployeeIDSeries() {
		getEmployee().setEmployeeId(EmployeeMain.getLatestEmloyeeId(EmployeeType.value(getTypeId())));
	}
	
	public void clear() {
		setEmployee(null);
		addDefaultEmployee();
	}
	
	public void clickItem(EmployeeMain em) {
		setCivilId(em.getCivilStatus());
		setDepId(em.getDepartment().getDepid());
		em.setTempRegDate(DateUtils.convertDateString(em.getRegDate(), "yyyy-MM-dd"));
		em.setTempBirthDate(DateUtils.convertDateString(em.getBirthDate(), "yyyy-MM-dd"));
		setEmployee(em);
	}
	
	public void delete(EmployeeMain em) {
		em.delete();
		loadData();
		clear();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void save() {
		boolean isOk = true;
		EmployeeMain em = getEmployee();
		
		if(em.getFirstName()==null | em.getFirstName().isBlank()) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in first name");
		}
		if(em.getMiddleName()==null || em.getMiddleName().isBlank()) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in middle name");
		}
		if(em.getLastName()==null || em.getLastName().isBlank()) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in last name");
		}
		if(em.getAddress()==null || em.getAddress().isBlank()) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in address");
		}
		if(em.getPosition()==null || em.getPosition().isBlank()) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in position");
		}
		if(em.getEmergecnyContactDtls()==null) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in emergency contact details");
		}
		if(em.getContactNo()==null) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in contact details");
		}
			
		if(isOk) {	
			em.setRegDate(DateUtils.convertDate(em.getTempRegDate(), "yyyy-MM-dd"));
			em.setEmployeeType(getTypeId());
			em.setBirthDate(DateUtils.convertDate(em.getTempBirthDate(), "yyyy-MM-dd"));
			em.setCivilStatus(getCivilId());
			em.setGender(getGenderId());
			em.setFullName(em.getLastName() + ", " + em.getFirstName() + " " + em.getMiddleName());
			
			Department dep = new Department();
			dep.setDepid(getDepId());
			em.setDepartment(dep);
			
			em.save();
			addDefaultEmployee();
			loadData();
			Application.addMessage(1, "Success", "Successfully saved.");
		}
	}

}








