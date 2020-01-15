package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @since 04/01/2019
 * @version 1.0
 *
 *
 */
public class CollectorCollections {

	private long id;
	private String dateTrans;
	private int formType;
	private double amount;
	private int isActive;
	private Collector collector;
	
	private String formName;
	
public static List<CollectorCollections> retrive(String sql, String[] params){
		
		List<CollectorCollections> cols = Collections.synchronizedList(new ArrayList<CollectorCollections>());
		
		String colTable = "col";
		String issTable = "iss";
		
		String tmpsql = "SELECT * FROM collectorcollections "+colTable+", issuedcollector "+ issTable +" WHERE "+colTable+".isactivecc=1 AND "
				+ colTable + ".isid=" + issTable + ".isid ";
		
		sql = tmpsql + sql;
		
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
		System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			CollectorCollections col = new CollectorCollections();
			col.setId(rs.getLong("ccid"));
			col.setDateTrans(rs.getString("ccdate"));
			col.setFormType(rs.getInt("formtype"));
			col.setAmount(rs.getDouble("ccamount"));
			col.setIsActive(rs.getInt("isactivecc"));
			
			Collector cc = new Collector();
			try{cc.setId(rs.getInt("isid"));}catch(NullPointerException e){}
			try{cc.setName(rs.getString("collectorname"));}catch(NullPointerException e){}
			try{cc.setIsActive(rs.getInt("isactivecollector"));}catch(NullPointerException e){}
			try{cc.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			
			Department dep = new Department();
			try{dep.setDepid(rs.getInt("departmentid"));}catch(NullPointerException e){}
			cc.setDepartment(dep);
			col.setCollector(cc);
			
			
			cols.add(col);
		}
		
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cols;
	}
	
	
	
	public static CollectorCollections save(CollectorCollections is){
		if(is!=null){
			
			long id = CollectorCollections.getInfo(is.getId() ==0? CollectorCollections.getLatestId()+1 : is.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				is = CollectorCollections.insertData(is, "1");
			}else if(id==2){
				LogU.add("update Data ");
				is = CollectorCollections.updateData(is);
			}else if(id==3){
				LogU.add("added new Data ");
				is = CollectorCollections.insertData(is, "3");
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
	
	public static CollectorCollections insertData(CollectorCollections col, String type){
		String sql = "INSERT INTO collectorcollections ("
				+ "ccid,"
				+ "ccdate,"
				+ "isid,"
				+ "formtype,"
				+ "ccamount,"
				+ "isactivecc)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table collectorcollections");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			col.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			col.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, col.getDateTrans());
		ps.setLong(cnt++, col.getCollector()==null? 0 : col.getCollector().getId());
		ps.setInt(cnt++, col.getFormType());
		ps.setDouble(cnt++, col.getAmount());
		ps.setInt(cnt++, col.getIsActive());
		
		
		LogU.add(col.getDateTrans());
		LogU.add(col.getCollector()==null? 0 : col.getCollector().getId());
		LogU.add(col.getFormType());
		LogU.add(col.getAmount());
		LogU.add(col.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to collectorcollections : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return col;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO collectorcollections ("
				+ "ccid,"
				+ "ccdate,"
				+ "isid,"
				+ "formtype,"
				+ "ccamount,"
				+ "isactivecc)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table collectorcollections");
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
		ps.setLong(cnt++, getCollector()==null? 0 : getCollector().getId());
		ps.setInt(cnt++, getFormType());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getIsActive());
		
		
		LogU.add(getDateTrans());
		LogU.add(getCollector()==null? 0 : getCollector().getId());
		LogU.add(getFormType());
		LogU.add(getAmount());
		LogU.add(getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to collectorcollections : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static CollectorCollections updateData(CollectorCollections col){
		String sql = "UPDATE collectorcollections SET "
				+ "ccdate=?,"
				+ "isid=?,"
				+ "formtype=?,"
				+ "ccamount=?" 
				+ " WHERE ccid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table collectorcollections");
		
		ps.setString(cnt++, col.getDateTrans());
		ps.setLong(cnt++, col.getCollector()==null? 0 : col.getCollector().getId());
		ps.setInt(cnt++, col.getFormType());
		ps.setDouble(cnt++, col.getAmount());
		ps.setLong(cnt++, col.getId());
		
		LogU.add(col.getDateTrans());
		LogU.add(col.getCollector()==null? 0 : col.getCollector().getId());
		LogU.add(col.getFormType());
		LogU.add(col.getAmount());
		LogU.add(col.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to collectorcollections : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return col;
	}
	
	public void updateData(){
		String sql = "UPDATE collectorcollections SET "
				+ "ccdate=?,"
				+ "isid=?,"
				+ "formtype=?,"
				+ "ccamount=?" 
				+ " WHERE ccid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table collectorcollections");
		
		ps.setString(cnt++, getDateTrans());
		ps.setLong(cnt++, getCollector()==null? 0 : getCollector().getId());
		ps.setInt(cnt++, getFormType());
		ps.setDouble(cnt++, getAmount());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getCollector()==null? 0 : getCollector().getId());
		LogU.add(getFormType());
		LogU.add(getAmount());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to collectorcollections : " + s.getMessage());
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
		sql="SELECT ccid FROM collectorcollections  ORDER BY ccid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("ccid");
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
		ps = conn.prepareStatement("SELECT ccid FROM collectorcollections WHERE ccid=?");
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
		String sql = "UPDATE collectorcollections set isactivecc=0 WHERE ccid=?";
		
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDateTrans() {
		return dateTrans;
	}

	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}

	public int getFormType() {
		return formType;
	}

	public void setFormType(int formType) {
		this.formType = formType;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public Collector getCollector() {
		return collector;
	}

	public void setCollector(Collector collector) {
		this.collector = collector;
	}



	public String getFormName() {
		return formName;
	}



	public void setFormName(String formName) {
		this.formName = formName;
	}
}
