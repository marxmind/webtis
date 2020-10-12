package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.Responsibility;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;

/**
 * 
 * @author Mark Italia
 * @since 08/15/2019
 * @version 1.0
 *
 */
@ManagedBean(name="rssBean", eager=true)
@ViewScoped
public class ResponsibilityBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6454765445421L;
	private String responsibilityName;
	private int depResId;
	private List depRes;
	private Responsibility resData;
	private String searchName;
	private List<Responsibility> rss = Collections.synchronizedList(new ArrayList<>());
	
	
	@PostConstruct
	public void init() {
		String sql = " ";
		rss = Collections.synchronizedList(new ArrayList<>());
		if(getSearchName()!=null) {
			sql += " AND rss.rname like '%"+ getSearchName() +"%'";
		}else {
			sql += " ORDER BY rss.rname ASC LIMIT 10";
		}
		rss = Responsibility.retrieve(sql, new String[0]);
		System.out.println("Loading...... responsibility....");
	}
	
	
	public void saveRes() {
		Responsibility rs = new Responsibility();
		if(getResData()!=null) {
			rs = getResData();
		}
		boolean isOk = true;
		if(getResponsibilityName()==null) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Responsibility Person");
		}
		if(getDepResId()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Department");
		}
		
		if(isOk) {
			rs.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			rs.setIsActive(1);
			rs.setIsResign(0);
			rs.setName(getResponsibilityName());
			Department department = new Department();
			department.setDepid(getDepResId());
			rs.setDepartment(department);
			rs.save();
			setSearchName(rs.getName());
			init();
			clearRes();
		}
		
	}
	
	public void clickItem(Responsibility rs) {
		setResData(rs);
		setResponsibilityName(rs.getName());
		setDepResId(rs.getDepartment().getDepid());
	}
	
	public void deleteRow(Responsibility rs) {
		rs.delete();
		init();
		clearRes();
	}
	
	public void clearRes() {
		setResData(null);
		setDepResId(0);
		setResponsibilityName(null);
	}
	
	public String getResponsibilityName() {
		return responsibilityName;
	}

	public void setResponsibilityName(String responsibilityName) {
		this.responsibilityName = responsibilityName;
	}

	public int getDepResId() {
		return depResId;
	}

	public void setDepResId(int depResId) {
		this.depResId = depResId;
	}

	public List getDepRes() {
		String sql = "SELECT * FROM department ORDER BY code";
		depRes = Collections.synchronizedList(new ArrayList<>());
		for(Department dep : Department.retrieve(sql, new String[0])) {
			depRes.add(new SelectItem(dep.getDepid(), dep.getCode() + " " + dep.getDepartmentName()));
		}
		return depRes;
	}

	public void setDepRes(List depRes) {
		this.depRes = depRes;
	}

	public Responsibility getResData() {
		return resData;
	}

	public void setResData(Responsibility resData) {
		this.resData = resData;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public List<Responsibility> getRss() {
		return rss;
	}

	public void setRss(List<Responsibility> rss) {
		this.rss = rss;
	}
}
