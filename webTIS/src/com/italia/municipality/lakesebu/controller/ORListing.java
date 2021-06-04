package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.enm.FormStatus;
import com.italia.municipality.lakesebu.enm.FormType;
import com.italia.municipality.lakesebu.licensing.controller.Customer;
import com.italia.municipality.lakesebu.utils.LogU;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @since 04/23/2019
 * @version 1.0
 *
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ORListing {

	private long id;
	private String dateTrans;
	private String orNumber;
	private int formType;
	private int isActive;
	private int status;
	private String forminfo;
	
	private Collector collector;
	private Customer customer;
	
	private List<ORNameList> orNameList = Collections.synchronizedList(new ArrayList<ORNameList>());
	private double amount;
	
	private String formName;
	private String statusName;
	
	public static String getLatestORNumber(int formType, int collector) {
		String orNumber = "0000000";
		
		String sql = "SELECT ornumber FROM orlisting WHERE aform="+formType+" AND isid="+collector+"  AND isactiveor=1 ORDER BY orid DESC LIMIT 1";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("SQL latest OR " + ps.toString());
		
		rs = ps.executeQuery();
		
			while(rs.next()){
				orNumber = rs.getString("ornumber");
			}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		
		}catch(Exception e){e.getMessage();}
		
		long orno = 0l;
		
		try{
			orno = Long.valueOf(orNumber.trim());
			
			orno += 1;
			
		String len = orno+"";
		int size = len.length();
		
		if(FormType.CT_2.getId()==formType || FormType.CT_5.getId()==formType) {
			orNumber = "0";
		}else if(FormType.CTC_INDIVIDUAL.getId()==formType || FormType.CTC_CORPORATION.getId()==formType) {
			
			switch(size) {
			case 1 : orNumber = "0000000"+orno; break;
			case 2 : orNumber = "000000"+orno; break;
			case 3 : orNumber = "00000"+orno; break;
			case 4 : orNumber = "0000"+orno; break;
			case 5 : orNumber = "000"+orno; break;
			case 6 : orNumber = "00"+orno; break;
			case 7 : orNumber = "0"+orno; break;
			case 8 : orNumber = ""+orno; break;
			}
			
		}else {//Official Receipt
		
			switch(size) {
			case 1 : orNumber = "000000"+orno; break;
			case 2 : orNumber = "00000"+orno; break;
			case 3 : orNumber = "0000"+orno; break;
			case 4 : orNumber = "000"+orno; break;
			case 5 : orNumber = "00"+orno; break;
			case 6 : orNumber = "0"+orno; break;
			case 7 : orNumber = ""+orno; break;
			}
		
		}
		
		}catch(Exception e) {}
		
		return orNumber;
	}
	
	/**
	 * 
	 * @return current date data or based on the date provided
	 * 
	 * dateFrom and dateTo date format yyyy-MM-dd
	 */
	public static boolean isDataForCurrentDateExist(String dateFrom, String dateTo) {
		System.out.println("Checking isDataForCurrentDateExist");
		String[] params = new String[2];
		String sql = "SELECT * FROM orlisting WHERE isactiveor=1 AND (ordatetrans>=? AND ordatetrans<=?)";
		params[0] = dateFrom;
		params[1] = dateTo;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			System.out.println("has data");
			return true;
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return false;
	}
	
	public static List<ORListing> retrieve(String sqlAdd, String[] params){
		List<ORListing> ors = new ArrayList<ORListing>();
		
		String tableOr = "orl";
		String tableCus = "cuz";
		String tableColl = "col";
		String sql = "SELECT * FROM orlisting "+tableOr+", customer "+ tableCus +", issuedcollector "+ tableColl +"  WHERE  "+tableOr+".isactiveor=1 AND " + 
				tableOr +".customerid=" + tableCus + ".customerid AND " +
				tableOr +".isid=" + tableColl + ".isid"; 
		
		sql += sqlAdd;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		System.out.println("ORLISTING SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			ORListing or = new ORListing();
			try{or.setId(rs.getLong("orid"));}catch(NullPointerException e){}
			try{or.setDateTrans(rs.getString("ordatetrans"));}catch(NullPointerException e){}
			try{or.setOrNumber(rs.getString("ornumber"));}catch(NullPointerException e){}
			try{or.setFormType(rs.getInt("aform"));}catch(NullPointerException e){}
			try{or.setIsActive(rs.getInt("isactiveor"));}catch(NullPointerException e){}
			try{or.setFormName(FormType.nameId(or.getFormType()));}catch(NullPointerException e){}
			try{or.setStatus(rs.getInt("orstatus"));}catch(NullPointerException e){}
			try{or.setStatusName(FormStatus.nameId(or.getStatus()));}catch(NullPointerException e){}
			try{or.setForminfo(rs.getString("forminfo"));}catch(NullPointerException e){}
			
			Customer cus = new Customer();
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			cus.setUserDtls(user);
			
			or.setCustomer(cus);
			
			sql = " AND nameL.isactiveol=1 AND nameL.orid="+or.getId();
			params = new String[0];
			List<ORNameList> orn = Collections.synchronizedList(new ArrayList<ORNameList>());
			double amount = 0d;
			for(ORNameList o : ORNameList.retrieve(sql, params)) {
				amount += o.getAmount();
				orn.add(o);
			}
			or.setAmount(amount);
			or.setOrNameList(orn);
			
			Collector col = new Collector();
			try{col.setId(rs.getInt("isid"));}catch(NullPointerException e){}
			try{col.setName(rs.getString("collectorname"));}catch(NullPointerException e){}
			try{col.setIsActive(rs.getInt("isactivecollector"));}catch(NullPointerException e){}
			try{col.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			
			Department dep = new Department();
			try{dep.setDepid(rs.getInt("departmentid"));}catch(NullPointerException e){}
			col.setDepartment(dep);
			or.setCollector(col);
			
			ors.add(or);
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return ors;
	}
	
	public static double retrieveTotal(int year){
		
		String tableOr = "orl";
		String tableCus = "cuz";
		String tableColl = "col";
		String sql = "SELECT * FROM orlisting "+tableOr+", customer "+ tableCus +", issuedcollector "+ tableColl +"  WHERE  "+tableOr+".isactiveor=1 AND " + 
				tableOr +".customerid=" + tableCus + ".customerid AND " +
				tableOr +".isid=" + tableColl + ".isid AND (orl.ordatetrans>='" + year + "-01-01' AND orl.ordatetrans<='" + year +"-12-31') AND orl.orstatus=4"; //4=encoded 
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		System.out.println("SQL retrieveTotal " + ps.toString());
		rs = ps.executeQuery();
		
		double amount = 0d;
		while(rs.next()){
			
			sql = " AND nameL.isactiveol=1 AND nameL.orid="+rs.getLong("orid");
			for(ORNameList o : ORNameList.retrieve(sql, new String[0])) {
				amount += o.getAmount();
			}
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		return amount;
		}catch(Exception e){e.getMessage();}
		
		return 0.00;
	}
	
	public static ORListing save(ORListing is){
		if(is!=null){
			
			long id = ORListing.getInfo(is.getId() ==0? ORListing.getLatestId()+1 : is.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				is = ORListing.insertData(is, "1");
			}else if(id==2){
				LogU.add("update Data ");
				is = ORListing.updateData(is);
			}else if(id==3){
				LogU.add("added new Data ");
				is = ORListing.insertData(is, "3");
			}
			
		}
		return is;
	}
	
	public void save(){
		
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		LogU.add("checking for new added data");
		if(id==1){
			LogU.add("insert new Data ");
			insertData("1");
		}else if(id==2){
			LogU.add("update Data ");
			updateData();
		}else if(id==3){
			LogU.add("added new Data ");
			insertData("3");
		}
		
 }
	
	public static ORListing insertData(ORListing name, String type){
		String sql = "INSERT INTO orlisting ("
				+ "orid,"
				+ "ordatetrans,"
				+ "ornumber,"
				+ "aform,"
				+ "isactiveor,"
				+ "customerid,"
				+ "isid,"
				+ "orstatus,"
				+ "forminfo)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table orlisting");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			name.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			name.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, name.getDateTrans());
		ps.setString(cnt++, name.getOrNumber());
		ps.setInt(cnt++, name.getFormType());
		ps.setInt(cnt++, name.getIsActive());
		ps.setLong(cnt++, name.getCustomer()==null? 0 : name.getCustomer().getId());
		ps.setInt(cnt++, name.getCollector().getId());
		ps.setInt(cnt++, name.getStatus());
		ps.setString(cnt++, name.getForminfo());
		
		LogU.add(name.getDateTrans());
		LogU.add(name.getOrNumber());
		LogU.add(name.getFormType());
		LogU.add(name.getIsActive());
		LogU.add(name.getCustomer()==null? 0 : name.getCustomer().getId());
		LogU.add(name.getCollector().getId());
		LogU.add(name.getStatus());
		LogU.add(name.getForminfo());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to orlisting : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return name;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO orlisting ("
				+ "orid,"
				+ "ordatetrans,"
				+ "ornumber,"
				+ "aform,"
				+ "isactiveor,"
				+ "customerid,"
				+ "isid,"
				+ "orstatus,"
				+ "forminfo)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table orlisting");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getOrNumber());
		ps.setInt(cnt++, getFormType());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setInt(cnt++, getCollector().getId());
		ps.setInt(cnt++, getStatus());
		ps.setString(cnt++, getForminfo());
		
		LogU.add(getDateTrans());
		LogU.add(getOrNumber());
		LogU.add(getFormType());
		LogU.add(getIsActive());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getCollector().getId());
		LogU.add(getStatus());
		LogU.add(getForminfo());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to orlisting : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static ORListing updateData(ORListing name){
		String sql = "UPDATE orlisting SET "
				+ "ordatetrans=?,"
				+ "ornumber=?,"
				+ "aform=?,"
				+ "customerid=?,"
				+ "isid=?,"
				+ "orstatus=?,"
				+ "forminfo=?" 
				+ " WHERE orid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table orlisting");
		
		ps.setString(cnt++, name.getDateTrans());
		ps.setString(cnt++, name.getOrNumber());
		ps.setInt(cnt++, name.getFormType());
		ps.setLong(cnt++, name.getCustomer()==null? 0 : name.getCustomer().getId());
		ps.setInt(cnt++, name.getCollector().getId());
		ps.setInt(cnt++, name.getStatus());
		ps.setString(cnt++, name.getForminfo());
		ps.setLong(cnt++, name.getId());
		
		LogU.add(name.getDateTrans());
		LogU.add(name.getOrNumber());
		LogU.add(name.getFormType());
		LogU.add(name.getCustomer()==null? 0 : name.getCustomer().getId());
		LogU.add(name.getCollector().getId());
		LogU.add(name.getStatus());
		LogU.add(name.getForminfo());
		LogU.add(name.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to orlisting : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return name;
	}
	
	public void updateData(){
		String sql = "UPDATE orlisting SET "
				+ "ordatetrans=?,"
				+ "ornumber=?,"
				+ "aform=?,"
				+ "customerid=?,"
				+ "isid=?,"
				+ "orstatus=?,"
				+ "forminfo=?" 
				+ " WHERE orid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table orlisting");
		
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getOrNumber());
		ps.setInt(cnt++, getFormType());
		ps.setLong(cnt++, getCustomer()==null? 0 : getCustomer().getId());
		ps.setInt(cnt++, getCollector().getId());
		ps.setInt(cnt++, getStatus());
		ps.setString(cnt++, getForminfo());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getOrNumber());
		LogU.add(getFormType());
		LogU.add(getCustomer()==null? 0 : getCustomer().getId());
		LogU.add(getCollector().getId());
		LogU.add(getStatus());
		LogU.add(getForminfo());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to orlisting : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT orid FROM orlisting  ORDER BY orid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("orid");
		}
		
		rs.close();
		prep.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestId();	
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement("SELECT orid FROM orlisting WHERE orid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE orlisting set isactiveor=0 WHERE orid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(SQLException s){}
		
	}

	
	
}
