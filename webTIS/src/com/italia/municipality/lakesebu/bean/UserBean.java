package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.municipality.lakesebu.database.BankChequeDatabaseConnect;

@Named
@ViewScoped
public class UserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 134434343678L;
	private String userCompleteName;
	private String userName;
	private String userPassword;
	private String userPosition;
	private String userAccessLevel;
	private List userPositionList = new ArrayList<>();
	private List userAccessLevelList = new ArrayList<>();
	private String userPositionLabel;
	private String userAccessLevelLabel;
	private String UserNotification="Please input user information.";
	public String getUserNotification() {
		return UserNotification;
	}

	public void setUserNotification(String userNotification) {
		UserNotification = userNotification;
	}	
	@PostConstruct
	private void init(){
		
		if(getUserPositionLabel()==null) setUserPositionLabel("Please Select...");
		if(getUserAccessLevelLabel()==null) setUserAccessLevelLabel("Please Select...");
		
	}
	
	public String save(){
		
		if(!checkEmptyFields()){
			
			if(checkDuplicateUserNamePassword()){
				setUserNotification("User and Password are not available for this account. Please change.");
			}else{
				insertData();
				setUserNotification("New user information has been added.");
			}
		
		}
		return "Save";
	}
	public String update(){
		
		String val = findExistingData();
		if(!"0".equalsIgnoreCase(val)){
			
			String sql = "UPDATE tbl_login set user_name=?, user_password=?, is_admin=? WHERE user_details_id=?";
			PreparedStatement ps = null;
			Connection conn = null;
			
			try{
				conn = BankChequeDatabaseConnect.getConnection();
				ps = conn.prepareStatement(sql);
				
				ps.setString(1, getUserName());
				ps.setString(2, getUserPassword());
				ps.setString(3, getUserPosition());
				ps.setInt(4, Integer.valueOf(val));
				ps.executeQuery();
				
				sql = "UPDATE tbl_userdtls set user_position=? WHERE user_details_id=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, getUserPosition());
				ps.setInt(2, Integer.valueOf(val));
				ps.executeQuery();
				
				
				ps.close();
				BankChequeDatabaseConnect.close(conn);
			}catch(SQLException e){
				e.printStackTrace();
			}
			setUserNotification("User Information has been updated.");
		}else{
				setUserNotification("This information is new. Please provide information or click save button.");
		}
		
		return "update";
	}
	
	private String findExistingData(){
		String result="0";
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = ""; 
		
		sql = "SELECT * FROM tbl_userdtls WHERE user_complete_name=?";
		
		try{
			conn = BankChequeDatabaseConnect.getConnection();
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, getUserCompleteName());
			
			
			System.out.println("Search SQL: "+ ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				result = rs.getString("user_details_id");
			}
			rs.close();
			ps.close();
			BankChequeDatabaseConnect.close(conn);
		}catch(SQLException e){
			e.printStackTrace();
		}
		System.out.println("findExistingData : " + result);
		return result;
	}
	
	private void insertData(){
		try{
			System.out.println("Preparing to save user log in details...");
			String sql = "INSERT INTO tbl_login (" 
					+"user_id,"
					+ "user_name,"
					+ "user_password,"
					+ "is_admin,"
					+ "is_online,"
					+ "user_details_id)" 
					+ " values(?,?,?,?,?,?)";
			PreparedStatement ps = null;
			Connection conn = null;
			try{
			int id = getLastId()+1;	
			conn = BankChequeDatabaseConnect.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.setString(2, getUserName());
			ps.setString(3, getUserPassword());
			int pos = Integer.valueOf(getUserPosition());
			ps.setInt(4, pos);
			ps.setInt(5, 0);
			ps.setInt(6, id);
			
			System.out.println("SQL ADD : " + ps.toString());
			ps.execute();
			System.out.println("Saving user log-in details....");
			System.out.println("Preparing to save user details...");
			
			sql = "INSERT INTO tbl_userdtls ( "
					+ "user_details_id, "
					+ "user_complete_name,"
					+ "user_position) "
					+ "values(?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.setString(2, getUserCompleteName().toUpperCase());
			ps.setString(3, getUserPosition());
			
			System.out.println("SQL ADD : " + ps.toString());
			ps.execute();
			
			}catch(SQLException e){
				e.printStackTrace();
			}
			
			ps.close();
			BankChequeDatabaseConnect.close(conn);
			}catch(SQLException sql){
				sql.printStackTrace();
			}
	}
	private boolean isMatched(){
		boolean isMatched=false;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = ""; 
		
		sql = "SELECT * FROM tbl_userdtls t1 INNER JOIN tbl_login t2 ON t1.user_details_id=t2.user_id "
				+ "WHERE t1.user_complete_name=? "
				+ "&& t2.user_name=? "
				+ "&& t2.user_password=? "
				+ "&& t1.user_position=?";
		
		try{
			conn = BankChequeDatabaseConnect.getConnection();
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, getUserCompleteName());
			ps.setString(2, getUserName());
			ps.setString(3, getUserPassword());
			ps.setString(4, getUserPosition());
			
			
			System.out.println("Search SQL: "+ ps.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				isMatched=true;
			}
			rs.close();
			ps.close();
			BankChequeDatabaseConnect.close(conn);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return isMatched;
	}
	private boolean checkEmptyFields(){
		boolean isEmpty=false;
		String msg0="",msg1="",msg2="",msg3="",msg4="",msg="";
		
		msg0 = !getUserCompleteName().isEmpty()? "" : "User Complete Name";
		msg1= !getUserName().isEmpty()? "" : "User Name";
		msg2= !getUserPassword().isEmpty()? "" : "User Password";
		msg3= !getUserPosition().isEmpty()? "" : "User Position";
		msg4= !getUserAccessLevel().isEmpty()? "" : "User Access Level";
		msg = (!msg0.isEmpty()? msg0+",":"") + (!msg1.isEmpty()? msg1+",":"") + (!msg2.isEmpty()? msg2+",":"") + (!msg3.isEmpty()? msg3+",":"") + (!msg4.isEmpty()? msg4+",":"");
		System.out.println("User messages: "+msg);
		if(!"".equalsIgnoreCase(msg)){
			setUserNotification("Please provide information for " + msg +".");
			isEmpty=true;
			System.out.println("Empty fields");
		}
		return isEmpty;
	}
	
	public boolean checkDuplicateUserNamePassword(){
		boolean isExisting = false;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = ""; 
		sql = "SELECT * FROM tbl_login WHERE user_name=? && user_password=?";
		
		try{
			conn = BankChequeDatabaseConnect.getConnection();
			ps = conn.prepareStatement(sql);
				ps.setString(1, getUserName());
				ps.setString(2, getUserPassword());
			
			
			System.out.println("Search SQL: "+ ps.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				isExisting=true;
			}
			rs.close();
			ps.close();
			BankChequeDatabaseConnect.close(conn);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return isExisting;
	}
	public String find(){
		System.out.println("find called");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = ""; 
		double grandAmount = 0.0;
		
		sql = "SELECT * FROM tbl_userdtls t1 INNER JOIN tbl_login t2 ON t1.user_details_id=t2.user_id "
				+ "WHERE t1.user_complete_name=? "
				+ "|| t2.user_name=? "
				+ "|| t2.user_password=? "
				+ "|| t1.user_position=?";
		
		try{
			conn = BankChequeDatabaseConnect.getConnection();
			ps = conn.prepareStatement(sql);
			for(int i=1;i<=4;i++){
				ps.setString(i, getUserCompleteName());
			}
			
			System.out.println("Search SQL: "+ ps.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				setUserCompleteName(rs.getString("user_complete_name"));
				setUserName(rs.getString("user_name"));
				setUserPassword(rs.getString("user_password"));
				setUserPosition(rs.getString("user_position"));
				setUserAccessLevel(rs.getString("user_position"));
			}
			rs.close();
			ps.close();
			BankChequeDatabaseConnect.close(conn);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return "search";
	}
	private int getLastId(){
		String sql = "SELECT user_id FROM tbl_login ORDER BY user_id DESC LIMIT 1";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		int result = 0;
		try{
		conn = BankChequeDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=rs.getInt("user_id");
		}
		
		rs.close();
		ps.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		BankChequeDatabaseConnect.close(conn);
		return result;
	}
	
	public String getUserPositionLabel() {
		return userPositionLabel;
	}

	public void setUserPositionLabel(String userPositionLabel) {
		this.userPositionLabel = userPositionLabel;
	}

	public String getUserAccessLevelLabel() {
		return userAccessLevelLabel;
	}

	public void setUserAccessLevelLabel(String userAccessLevelLabel) {
		this.userAccessLevelLabel = userAccessLevelLabel;
	}

	
	
	public List getUserAccessLevelList() {
		
		userAccessLevelList = new ArrayList<>();
		userAccessLevelList.add(new SelectItem("1","1 - Admin"));
		userAccessLevelList.add(new SelectItem("2","2 - Second Admin"));
		userAccessLevelList.add(new SelectItem("3","3 - Normal User"));
		
		return userAccessLevelList;
	}

	public void setUserAccessLevelList(List userAccessLevelList) {
		this.userAccessLevelList = userAccessLevelList;
	}

	public List getUserPositionList() {
		userPositionList = new ArrayList<>();
		userPositionList.add(new SelectItem("1","Admin"));
		userPositionList.add(new SelectItem("2","Clerk"));
		userPositionList.add(new SelectItem("3","Secretary"));
		
		return userPositionList;
	}

	public void setUserPositionList(List userPositionList) {
		this.userPositionList = userPositionList;
	}

	public String getUserCompleteName() {
		return userCompleteName;
	}

	public void setUserCompleteName(String userCompleteName) {
		this.userCompleteName = userCompleteName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserPosition() {
		return userPosition;
	}

	public void setUserPosition(String userPosition) {
		this.userPosition = userPosition;
	}

	public String getUserAccessLevel() {
		return userAccessLevel;
	}

	public void setUserAccessLevel(String userAccessLevel) {
		this.userAccessLevel = userAccessLevel;
	}
	
	public String reportPage(){
		return "reports";
	}
	
	
	
	
}
