package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.utils.Application;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 *@since 08/16/2019
 *
 */
@Named
@ViewScoped
public class DepartmentBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 546789876756431L;
	
	private String name;
	private String code;
	private String searchName;
	private String departmentHead;
	private Department depData;
	private List<Department> deps = Collections.synchronizedList(new ArrayList<Department>());
	
	@PostConstruct
	public void init() {
		deps = Collections.synchronizedList(new ArrayList<Department>());
		String[] params = new String[0];
		String sql = "SELECT * FROM department WHERE isactivedep=1 ";
		
		if(getSearchName()!=null) {
			sql += " AND (departmentname LIKE '%"+ getSearchName() +"%' OR ";
			sql += " code LIKE '%"+ getSearchName() +"%')";
		}
		
		sql += " ORDER BY departmentid DESC LIMIT 10";
		
		deps = Department.retrieve(sql, params);
		if(deps!=null && deps.size()==1) {
			clickItem(deps.get(0));
		}
		
	}
	
	public void saveDep() {
		Department dep = new Department();
		boolean isOk = true;
		if(getDepData()!=null) {
			dep = getDepData();
		}
		
		if(getName()==null) {
			isOk=false;
			Application.addMessage(3, "Error", "Please provide name");
		}
		if(getCode()==null) {
			isOk=false;
			Application.addMessage(3, "Error", "Please provide code");
		}
		if(getDepartmentHead()==null) {
			isOk=false;
			Application.addMessage(3, "Error", "Please provide department head");
		}
		
		if(isOk) {
			dep.setIsActive(1);
			dep.setDepartmentName(getName());
			dep.setCode(getCode());
			dep.setDepartmentHead(getDepartmentHead());
			dep.save();
			init();
			clear();
			Application.addMessage(1, "Success", "Successfully saved");
		}
		
		
	}
	
	public void clickItem(Department dep) {
		setDepData(dep);
		setName(dep.getDepartmentName());
		setCode(dep.getCode());
		setDepartmentHead(dep.getDepartmentHead());
	}
	
	public void deleteRow(Department dep) {
		dep.delete();
		clear();
		init();
	}
	
	public void clear() {
		setDepData(null);
		setName(null);
		setCode(null);
		setDepartmentHead(null);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public Department getDepData() {
		return depData;
	}
	public void setDepData(Department depData) {
		this.depData = depData;
	}
	public List<Department> getDeps() {
		return deps;
	}
	public void setDeps(List<Department> deps) {
		this.deps = deps;
	}

	public String getDepartmentHead() {
		return departmentHead;
	}

	public void setDepartmentHead(String departmentHead) {
		this.departmentHead = departmentHead;
	}
	
	
	
}
