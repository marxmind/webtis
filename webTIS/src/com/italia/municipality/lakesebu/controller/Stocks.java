package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.enm.FormType;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @since 03/08/2019
 * @version 1.0
 *
 */
public class Stocks {

	private long id;
	private String dateTrans;
	private String seriesFrom;
	private String seriesTo;
	private int formType;
	private int status;
	private int isActive;
	private Timestamp timestamp;
	private int quantity;
	private int stabNo;
	private Collector collector;
	
	private String statusName;
	private String formTypeName;
	
	private int count;
	
	public static List<Stocks> retrieve(String sqlAdd, String[] params){
		List<Stocks> stocks = new ArrayList<Stocks>();
		
		String tableStock ="st";
		String tableCol = "cl";
		String sql = "SELECT * FROM stockreceipt "+ tableStock +",issuedcollector "+tableCol +"  WHERE "+tableStock+".isactivestock=1 AND " + 
		tableStock +".isid=" + tableCol + ".isid ";
				
		sql = sql + sqlAdd;		
				
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
		//System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		int count = 1;
		while(rs.next()){
			
			Stocks st = new Stocks();
			st.setId(rs.getLong("stockid"));
			st.setDateTrans(rs.getString("datetrans"));
			st.setSeriesFrom(rs.getString("seriesfrom"));
			st.setSeriesTo(rs.getString("seriesto"));
			st.setStatus(rs.getInt("statusstock"));
			st.setIsActive(rs.getInt("isactivestock"));
			st.setFormType(rs.getInt("formType"));
			st.setQuantity(rs.getInt("qty"));
			st.setStabNo(rs.getInt("stabno"));
			st.setCount(count++);
			
			if(rs.getInt("statusstock")==1) {
				st.setStatusName("NOT ISSUED");
			}else {
				st.setStatusName("ISSUED");
			}
			
			st.setFormTypeName(FormType.nameId(rs.getInt("formType")));
			
			Collector col = new Collector();
			try{col.setId(rs.getInt("isid"));}catch(NullPointerException e){}
			try{col.setName(rs.getString("collectorname"));}catch(NullPointerException e){}
			try{col.setIsActive(rs.getInt("isactivecollector"));}catch(NullPointerException e){}
			try{col.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			st.setCollector(col);
			
			stocks.add(st);
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return stocks;
	}
	
	public static Stocks save(Stocks st){
		if(st!=null){
			
			long id = Stocks.getInfo(st.getId() ==0? Stocks.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = Stocks.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = Stocks.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = Stocks.insertData(st, "3");
			}
			
		}
		return st;
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
	
	public static Stocks insertData(Stocks st, String type){
		String sql = "INSERT INTO stockreceipt ("
				+ "stockid,"
				+ "datetrans,"
				+ "seriesfrom,"
				+ "seriesto,"
				+ "statusstock,"
				+ "isactivestock,"
				+ "isid,"
				+ "formType,"
				+ "qty,"
				+ "stabno)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table stockreceipt");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			st.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			st.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, st.getDateTrans());
		ps.setString(cnt++, st.getSeriesFrom());
		ps.setString(cnt++, st.getSeriesTo());
		ps.setInt(cnt++, st.getStatus());
		ps.setInt(cnt++, st.getIsActive());
		ps.setInt(cnt++, st.getCollector()==null? 0 : st.getCollector().getId());
		ps.setInt(cnt++, st.getFormType());
		ps.setInt(cnt++, st.getQuantity());
		ps.setInt(cnt++, st.getStabNo());
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getSeriesFrom());
		LogU.add(st.getSeriesTo());
		LogU.add(st.getStatus());
		LogU.add(st.getIsActive());
		LogU.add(st.getCollector()==null? 0 : st.getCollector().getId());
		LogU.add(st.getFormType());
		LogU.add(st.getQuantity());
		LogU.add(st.getStabNo());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to stockreceipt : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO stockreceipt ("
				+ "stockid,"
				+ "datetrans,"
				+ "seriesfrom,"
				+ "seriesto,"
				+ "statusstock,"
				+ "isactivestock,"
				+ "isid,"
				+ "formType,"
				+ "qty,"
				+ "stabno)" 
				+ "values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table stockreceipt");
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
		ps.setString(cnt++, getSeriesFrom());
		ps.setString(cnt++, getSeriesTo());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getCollector()==null? 0 : getCollector().getId());
		ps.setInt(cnt++, getFormType());
		ps.setInt(cnt++, getQuantity());
		ps.setInt(cnt++, getStabNo());
		
		LogU.add(getDateTrans());
		LogU.add(getSeriesFrom());
		LogU.add(getSeriesTo());
		LogU.add(getStatus());
		LogU.add(getIsActive());
		LogU.add(getCollector()==null? 0 : getCollector().getId());
		LogU.add(getFormType());
		LogU.add(getQuantity());
		LogU.add(getStabNo());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to stockreceipt : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Stocks updateData(Stocks st){
		String sql = "UPDATE stockreceipt SET "
				+ "datetrans=?,"
				+ "seriesfrom=?,"
				+ "seriesto=?,"
				+ "statusstock=?,"
				+ "isid=?,"
				+ "formType=?,"
				+ "qty=?,"
				+ "stabno=?" 
				+ " WHERE stockid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table stockreceipt");
		
		ps.setString(cnt++, st.getDateTrans());
		ps.setString(cnt++, st.getSeriesFrom());
		ps.setString(cnt++, st.getSeriesTo());
		ps.setInt(cnt++, st.getStatus());
		ps.setInt(cnt++, st.getCollector()==null? 0 : st.getCollector().getId());
		ps.setInt(cnt++, st.getFormType());
		ps.setInt(cnt++, st.getQuantity());
		ps.setInt(cnt++, st.getStabNo());
		ps.setLong(cnt++, st.getId());
		
		LogU.add(st.getDateTrans());
		LogU.add(st.getSeriesFrom());
		LogU.add(st.getSeriesTo());
		LogU.add(st.getStatus());
		LogU.add(st.getCollector()==null? 0 : st.getCollector().getId());
		LogU.add(st.getFormType());
		LogU.add(st.getQuantity());
		LogU.add(st.getStabNo());
		LogU.add(st.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to stockreceipt : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public void updateData(){
		String sql = "UPDATE stockreceipt SET "
				+ "datetrans=?,"
				+ "seriesfrom=?,"
				+ "seriesto=?,"
				+ "statusstock=?,"
				+ "isid=?,"
				+ "formType=?,"
				+ "qty=?,"
				+ "stabno=?" 
				+ " WHERE stockid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table stockreceipt");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getSeriesFrom());
		ps.setString(cnt++, getSeriesTo());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getCollector()==null? 0 : getCollector().getId());
		ps.setInt(cnt++, getFormType());
		ps.setInt(cnt++, getQuantity());
		ps.setInt(cnt++, getStabNo());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getSeriesFrom());
		LogU.add(getSeriesTo());
		LogU.add(getStatus());
		LogU.add(getCollector()==null? 0 : getCollector().getId());
		LogU.add(getFormType());
		LogU.add(getQuantity());
		LogU.add(getStabNo());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to stockreceipt : " + s.getMessage());
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
		sql="SELECT stockid FROM stockreceipt  ORDER BY stockid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("stockid");
		}
		
		rs.close();
		prep.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static long getInfo(long id){
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
		ps = conn.prepareStatement("SELECT stockid FROM stockreceipt WHERE stockid=?");
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
		String sql = "UPDATE stockreceipt set isactivestock=0 WHERE stockid=?";
		
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

	public String getSeriesFrom() {
		return seriesFrom;
	}

	public void setSeriesFrom(String seriesFrom) {
		this.seriesFrom = seriesFrom;
	}

	public String getSeriesTo() {
		return seriesTo;
	}

	public void setSeriesTo(String seriesTo) {
		this.seriesTo = seriesTo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Collector getCollector() {
		return collector;
	}

	public void setCollector(Collector collector) {
		this.collector = collector;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public int getFormType() {
		return formType;
	}

	public void setFormType(int formType) {
		this.formType = formType;
	}

	public String getFormTypeName() {
		return formTypeName;
	}

	public void setFormTypeName(String formTypeName) {
		this.formTypeName = formTypeName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getStabNo() {
		return stabNo;
	}

	public void setStabNo(int stabNo) {
		this.stabNo = stabNo;
	}
	
}
