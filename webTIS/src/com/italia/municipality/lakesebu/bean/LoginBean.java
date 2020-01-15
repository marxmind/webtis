package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.LoginDAO;
import com.italia.municipality.lakesebu.enm.Pages;
import com.italia.municipality.lakesebu.reports.DailyReport;
import com.italia.municipality.lakesebu.security.ClientInfo;
import com.italia.municipality.lakesebu.security.Copyright;
import com.italia.municipality.lakesebu.security.License;
import com.italia.municipality.lakesebu.security.Module;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.utils.LogU;
import com.italia.municipality.lakesebu.utils.Whitelist;

@ManagedBean(name= "loginBean", eager=true)
@SessionScoped
public class LoginBean implements Serializable{

	private static final long serialVersionUID = 1094801825228386363L;
	
	private String name;
	private String password;
	private String errorMessage;
	private String keyPress;
	
	private int moduleId;
	private List modules;
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@PostConstruct
	public void init() {
		DailyReport.runReport();
	}
	
	//validate login
public String validateUserNamePassword(){
		
		String sql = "SELECT * FROM login WHERE username=? and password=?";
		String[] params = new String[2];
		         params[0] = Whitelist.remove(name);
		         params[1] = Whitelist.remove(password);
		Login in = null;
		try{in = Login.retrieve(sql, params).get(0);}catch(Exception e){return "login";}
		
		/*boolean valid = Login.validate(sql, params);
		System.out.println("Valid: " + valid);*/
		
		String result="login";
		LogU.add("Guest with username : " + name + " and password " + password + " is trying to log in the system.");
		if(in!=null){
			
	        HttpSession session = SessionBean.getSession();
	        session.setAttribute("username", name);
			session.setAttribute("userid", in.getLogid());
			
			/*
			 * switch(in.getAccessLevel().getLevel()){ case 1: {result="main.xhtml"; break;}
			 * case 2: { result="main.xhtml"; break;} case 3: {
			 * 
			 * boolean isExpired = License.checkLicenseExpiration(Module.CASH_BOOK);
			 * if(isExpired){ result = "expired.xhtml"; }else{ result = "funds.xhtml"; }
			 * break; } case 4: {
			 * 
			 * boolean isExpired = License.checkLicenseExpiration(Module.CASH_BOOK);
			 * if(isExpired){ result = "expired.xhtml"; }else{ result = "funds.xhtml"; }
			 * break; } case 6: {
			 * 
			 * boolean isExpired = License.checkLicenseExpiration(Module.LAND_TAX);
			 * if(isExpired){ result = "expired.xhtml"; }else{ result = "form56.xhtml"; }
			 * break; } case 8: {
			 * 
			 * boolean isExpired = License.checkLicenseExpiration(Module.CHECK_WRITING);
			 * if(isExpired){ result= "expired.xhtml"; }else{ result= "logform.xhtml"; }
			 * break; } case 9: {
			 * 
			 * boolean isExpired = License.checkLicenseExpiration(Module.VOUCHER_TRACKER);
			 * if(isExpired){ result= "expired.xhtml"; }else{ result= "voucher.xhtml"; }
			 * break; } }
			 */
			
			if(getModuleId()>0) {
				result = assignModule();
			}else {
				result="main.xhtml";
				//result="dashboard.xhtml";//temporary
			}
			
			LogU.add("The user has been successfully login to the application with the username : " + name + " and password " + password);
			
			//Check application Expiration
			/*if(Copyright.checkLicenseExpiration()){	
				LogU.add("The application is expired. Please contact application owner.");
				result = "expired";
			}else{*/
				logUserIn(in);
			//}
		}else{
			FacesContext.getCurrentInstance().addMessage(
					null,new FacesMessage(
							FacesMessage.SEVERITY_WARN, 
							"Incorrect username and password", 
							"Please enter correct username and password"
							)
					);
//			/setErrorMessage("Incorrect username and password.");
			LogU.add("The user was not successfully login to the application with the username : " + name + " and password " + password);
			setName("");
			setPassword("");
			result= "login";
		}
		System.out.println(getErrorMessage());
		return result;
	}
	
private String assignModule() {
	
	switch(getModuleId()) {
		case 1 : return Pages.CheckWriting.getName();
		case 2 : return Pages.LandTax.getName();
		case 4 : return Pages.VoucherLoging.getName();
		case 5 : return Pages.Mooe.getName();
		case 6 : return Pages.CashInBank.getName();
		case 7 : return Pages.CashInTreasury.getName();
		case 8 : return Pages.Dtr.getName();
		case 9 : return Pages.Market.getName();
		case 10 : return Pages.StockRecording.getName();
		case 11 : return Pages.IssuedForm.getName();
		case 12 : return Pages.CollectorRecording.getName();
		case 13 : return Pages.ReportGraph.getName();
		case 14 : return Pages.Orlisting.getName();
		case 15 : return "main";
		case 16 : return Pages.UploadRcd.getName();
		case 17 : return Pages.VoucherExpense.getName();
		case 18 : return Pages.WaterBilling.getName();
	}
	
	
	return "main";
}

private void logUserIn(Login in){
	if(in==null) in = new Login();
	ClientInfo cinfo = new ClientInfo();
	in.setLogintime(DateUtils.getCurrentDateMMDDYYYYTIME());
	in.setIsOnline(1);
	in.setClientip(cinfo.getClientIP());
	in.setClientbrowser(cinfo.getBrowserName());
	in.save();
}

private void logUserOut(){
	String sql = "SELECT * FROM login WHERE username=? and logid=?";
	HttpSession session = SessionBean.getSession();
	String userid = session.getAttribute("userid").toString();
	String username = session.getAttribute("username").toString();
	String[] params = new String[2];
	params[0] = username;
	params[1] = userid;
	Login in = null;
	try{in = Login.retrieve(sql, params).get(0);}catch(Exception e){}
	ClientInfo cinfo = new ClientInfo();
	if(in!=null){
		in.setLastlogin(DateUtils.getCurrentDateMMDDYYYYTIME());
		in.setIsOnline(0);
		in.setClientip(cinfo.getClientIP());
		in.setClientbrowser(cinfo.getBrowserName());
		in.save();
	}
	LogU.add("The user " + username + " was logging out to the application.");
	
	//Remove registered bean in session
	IBean.removeBean();
	
}

	/*public String validateUserNamePassword(){
		
		
		//System.out.println("UserName: " + name + " Password: " + password);
		boolean valid = LoginDAO.validate(name, password);
		//System.out.println("Valid: " + valid);
		String result="login";
		if(valid){
			
			if(Copyright.checkLicenseExpiration()){
			
				result = "expired";
			}else{	
				HttpSession session = SessionBean.getSession();
				session.setAttribute("username", name);
				refreshBean();
				result = "form56";
				//result = "welcome";
			}
			
		}else{
			FacesContext.getCurrentInstance().addMessage(
					null,new FacesMessage(
							FacesMessage.SEVERITY_WARN, 
							"Incorrect username and password", 
							"Please enter correct username and password"
							)
					);
//			/setErrorMessage("Incorrect username and password.");
			setName("");
			setPassword("");
			result= "login";
		}
		System.out.println(getErrorMessage());
		return result;
	}*/
	//logout event, invalidate session
	public String logout(){
		logUserOut();
		//IBean.removeBean();
		//removeBean();
		/*HttpSession session = SessionBean.getSession();
		FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("loginBean");*/
		setName("");
		setPassword("");
		//session.invalidate();
		return "login.xhtml?faces-redirect=true";
	}
	
	//logout event, invalidate session
	@Deprecated
	private void refreshBean(){
		String[] beans = {
				"checkBean",
				"dBPCheckBean",
				"reportBean",
				"userBean"
				};
		for(String bean : beans){
			FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove(bean);
		}
		}
	
	/**
	 * Remove and invalidate user session
	 */
	@Deprecated
	public static void removeBean(){
		HttpSession session = SessionBean.getSession();
		String[] beans = {
				"loginBean",
				"checkBean",
				"dBPCheckBean",
				"reportBean",
				"userBean"
				};
		for(String bean : beans){
			FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove(bean);
		}
		session.invalidate();
	}
	
	public String getKeyPress() {
		keyPress = "logId";
		return keyPress;
	}


	public void setKeyPress(String keyPress) {
		this.keyPress = keyPress;
	}
	public int getModuleId() {
		return moduleId;
	}
	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}
	public List getModules() {
		modules = new ArrayList<>();
		modules.add(new SelectItem(0, "Select Module"));
		for(Module m : Module.values()) {
			modules.add(new SelectItem(m.getId(), m.getName()));
		}
		
		return modules;
	}
	public void setModules(List modules) {
		this.modules = modules;
	}
}
