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
public class SummaryCollectionsCollector {
	
	private long id;
	private double year;
	private double January;
	private double February;
	private double March;
	private double April;
	private double May;
	private double June;
	private double July;
	private double August;
	private double September;
	private double October;
	private double November;
	private double December;
	private int isActive;
	
	private Collector collector;
	private double total;
	
	public static List<SummaryCollectionsCollector> retrive(String sql, String[] params){
		
		List<SummaryCollectionsCollector> cols = Collections.synchronizedList(new ArrayList<SummaryCollectionsCollector>());
		
		String colTable = "col";
		String issTable = "iss";
		
		String tmpsql = "SELECT * FROM summarycollectionscollector "+colTable+", issuedcollector "+ issTable +" WHERE "+colTable+".isactivecol=1 AND "
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
			
			SummaryCollectionsCollector col = new SummaryCollectionsCollector();
			try{col.setId(rs.getLong("colid"));}catch(NullPointerException e){}
			try{col.setYear(rs.getInt("colyear"));}catch(NullPointerException e){}
			try{col.setJanuary(rs.getDouble("coljan"));}catch(NullPointerException e){}
			try{col.setFebruary(rs.getDouble("colfeb"));}catch(NullPointerException e){}
			try{col.setMarch(rs.getDouble("colmar"));}catch(NullPointerException e){}
			try{col.setApril(rs.getDouble("colapr"));}catch(NullPointerException e){}
			try{col.setMay(rs.getDouble("colmay"));}catch(NullPointerException e){}
			try{col.setJune(rs.getDouble("coljun"));}catch(NullPointerException e){}
			try{col.setJuly(rs.getDouble("coljul"));}catch(NullPointerException e){}
			try{col.setAugust(rs.getDouble("colaug"));}catch(NullPointerException e){}
			try{col.setSeptember(rs.getDouble("colsep"));}catch(NullPointerException e){}
			try{col.setOctober(rs.getDouble("coloct"));}catch(NullPointerException e){}
			try{col.setNovember(rs.getDouble("colnov"));}catch(NullPointerException e){}
			try{col.setDecember(rs.getDouble("coldec"));}catch(NullPointerException e){}
			try{col.setIsActive(rs.getInt("isactivecol"));}catch(NullPointerException e){}
			
			Collector cc = new Collector();
			try{cc.setId(rs.getInt("isid"));}catch(NullPointerException e){}
			try{cc.setName(rs.getString("collectorname"));}catch(NullPointerException e){}
			try{cc.setIsActive(rs.getInt("isactivecollector"));}catch(NullPointerException e){}
			try{cc.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			
			Department dep = new Department();
			try{dep.setDepid(rs.getInt("departmentid"));}catch(NullPointerException e){}
			cc.setDepartment(dep);
			col.setCollector(cc);
			
			try{double amount = col.getJanuary() + col.getFebruary() + col.getMarch() + col.getApril() + col.getMay() + col.getJune() + col.getJuly() + col.getAugust() + col.getSeptember() + col.getOctober() + col.getNovember() + col.getDecember();
			col.setTotal(amount);}catch(Exception e) {}
			
			cols.add(col);
		}
		
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cols;
	}
	
	public static SummaryCollectionsCollector save(SummaryCollectionsCollector is){
		if(is!=null){
			
			long id = SummaryCollectionsCollector.getInfo(is.getId() ==0? SummaryCollectionsCollector.getLatestId()+1 : is.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				is = SummaryCollectionsCollector.insertData(is, "1");
			}else if(id==2){
				LogU.add("update Data ");
				is = SummaryCollectionsCollector.updateData(is);
			}else if(id==3){
				LogU.add("added new Data ");
				is = SummaryCollectionsCollector.insertData(is, "3");
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
	
	public static SummaryCollectionsCollector insertData(SummaryCollectionsCollector col, String type){
		String sql = "INSERT INTO summarycollectionscollector ("
				+ "colid,"
				+ "isid,"
				+ "colyear,"
				+ "coljan,"
				+ "colfeb,"
				+ "colmar,"
				+ "colapr,"
				+ "colmay,"
				+ "coljun,"
				+ "coljul,"
				+ "colaug,"
				+ "colsep,"
				+ "coloct,"
				+ "colnov,"
				+ "coldec,"
				+ "isactivecol)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table summarycollectionscollector");
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
		
		ps.setLong(cnt++, col.getCollector()==null? 0 : col.getCollector().getId());
		ps.setDouble(cnt++, col.getYear());
		ps.setDouble(cnt++, col.getJanuary());
		ps.setDouble(cnt++, col.getFebruary());
		ps.setDouble(cnt++, col.getMarch());
		ps.setDouble(cnt++, col.getApril());
		ps.setDouble(cnt++, col.getMay());
		ps.setDouble(cnt++, col.getJune());
		ps.setDouble(cnt++, col.getJuly());
		ps.setDouble(cnt++, col.getAugust());
		ps.setDouble(cnt++, col.getSeptember());
		ps.setDouble(cnt++, col.getOctober());
		ps.setDouble(cnt++, col.getNovember());
		ps.setDouble(cnt++, col.getDecember());
		ps.setInt(cnt++, col.getIsActive());
		
		LogU.add(col.getCollector()==null? 0 : col.getCollector().getId());
		LogU.add(col.getYear());
		LogU.add(col.getJanuary());
		LogU.add(col.getFebruary());
		LogU.add(col.getMarch());
		LogU.add(col.getApril());
		LogU.add(col.getMay());
		LogU.add(col.getJune());
		LogU.add(col.getJuly());
		LogU.add(col.getAugust());
		LogU.add(col.getSeptember());
		LogU.add(col.getOctober());
		LogU.add(col.getNovember());
		LogU.add(col.getDecember());
		LogU.add(col.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to summarycollectionscollector : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return col;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO summarycollectionscollector ("
				+ "colid,"
				+ "isid,"
				+ "colyear,"
				+ "coljan,"
				+ "colfeb,"
				+ "colmar,"
				+ "colapr,"
				+ "colmay,"
				+ "coljun,"
				+ "coljul,"
				+ "colaug,"
				+ "colsep,"
				+ "coloct,"
				+ "colnov,"
				+ "coldec,"
				+ "isactivecol)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table summarycollectionscollector");
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
		
		ps.setLong(cnt++, getCollector()==null? 0 : getCollector().getId());
		ps.setDouble(cnt++, getYear());
		ps.setDouble(cnt++, getJanuary());
		ps.setDouble(cnt++, getFebruary());
		ps.setDouble(cnt++, getMarch());
		ps.setDouble(cnt++, getApril());
		ps.setDouble(cnt++, getMay());
		ps.setDouble(cnt++, getJune());
		ps.setDouble(cnt++, getJuly());
		ps.setDouble(cnt++, getAugust());
		ps.setDouble(cnt++, getSeptember());
		ps.setDouble(cnt++, getOctober());
		ps.setDouble(cnt++, getNovember());
		ps.setDouble(cnt++, getDecember());
		ps.setInt(cnt++, getIsActive());
		
		LogU.add(getCollector()==null? 0 : getCollector().getId());
		LogU.add(getYear());
		LogU.add(getJanuary());
		LogU.add(getFebruary());
		LogU.add(getMarch());
		LogU.add(getApril());
		LogU.add(getMay());
		LogU.add(getJune());
		LogU.add(getJuly());
		LogU.add(getAugust());
		LogU.add(getSeptember());
		LogU.add(getOctober());
		LogU.add(getNovember());
		LogU.add(getDecember());
		LogU.add(getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to summarycollectionscollector : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static SummaryCollectionsCollector updateData(SummaryCollectionsCollector col){
		String sql = "UPDATE summarycollectionscollector SET "
				+ "isid=?,"
				+ "colyear=?,"
				+ "coljan=?,"
				+ "colfeb=?,"
				+ "colmar=?,"
				+ "colapr=?,"
				+ "colmay=?,"
				+ "coljun=?,"
				+ "coljul=?,"
				+ "colaug=?,"
				+ "colsep=?,"
				+ "coloct=?,"
				+ "colnov=?,"
				+ "coldec=?" 
				+ " WHERE colid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table summarycollectionscollector");
		
		ps.setLong(cnt++, col.getCollector()==null? 0 : col.getCollector().getId());
		ps.setDouble(cnt++, col.getYear());
		ps.setDouble(cnt++, col.getJanuary());
		ps.setDouble(cnt++, col.getFebruary());
		ps.setDouble(cnt++, col.getMarch());
		ps.setDouble(cnt++, col.getApril());
		ps.setDouble(cnt++, col.getMay());
		ps.setDouble(cnt++, col.getJune());
		ps.setDouble(cnt++, col.getJuly());
		ps.setDouble(cnt++, col.getAugust());
		ps.setDouble(cnt++, col.getSeptember());
		ps.setDouble(cnt++, col.getOctober());
		ps.setDouble(cnt++, col.getNovember());
		ps.setDouble(cnt++, col.getDecember());
		ps.setLong(cnt++, col.getId());
		
		LogU.add(col.getCollector()==null? 0 : col.getCollector().getId());
		LogU.add(col.getYear());
		LogU.add(col.getJanuary());
		LogU.add(col.getFebruary());
		LogU.add(col.getMarch());
		LogU.add(col.getApril());
		LogU.add(col.getMay());
		LogU.add(col.getJune());
		LogU.add(col.getJuly());
		LogU.add(col.getAugust());
		LogU.add(col.getSeptember());
		LogU.add(col.getOctober());
		LogU.add(col.getNovember());
		LogU.add(col.getDecember());
		LogU.add(col.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to summarycollectionscollector : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return col;
	}
	
	public void updateData(){
		String sql = "UPDATE summarycollectionscollector SET "
				+ "isid=?,"
				+ "colyear=?,"
				+ "coljan=?,"
				+ "colfeb=?,"
				+ "colmar=?,"
				+ "colapr=?,"
				+ "colmay=?,"
				+ "coljun=?,"
				+ "coljul=?,"
				+ "colaug=?,"
				+ "colsep=?,"
				+ "coloct=?,"
				+ "colnov=?,"
				+ "coldec=?" 
				+ " WHERE colid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table summarycollectionscollector");
		
		ps.setLong(cnt++, getCollector()==null? 0 : getCollector().getId());
		ps.setDouble(cnt++, getYear());
		ps.setDouble(cnt++, getJanuary());
		ps.setDouble(cnt++, getFebruary());
		ps.setDouble(cnt++, getMarch());
		ps.setDouble(cnt++, getApril());
		ps.setDouble(cnt++, getMay());
		ps.setDouble(cnt++, getJune());
		ps.setDouble(cnt++, getJuly());
		ps.setDouble(cnt++, getAugust());
		ps.setDouble(cnt++, getSeptember());
		ps.setDouble(cnt++, getOctober());
		ps.setDouble(cnt++, getNovember());
		ps.setDouble(cnt++, getDecember());
		ps.setLong(cnt++, getId());
		
		LogU.add(getCollector()==null? 0 : getCollector().getId());
		LogU.add(getYear());
		LogU.add(getJanuary());
		LogU.add(getFebruary());
		LogU.add(getMarch());
		LogU.add(getApril());
		LogU.add(getMay());
		LogU.add(getJune());
		LogU.add(getJuly());
		LogU.add(getAugust());
		LogU.add(getSeptember());
		LogU.add(getOctober());
		LogU.add(getNovember());
		LogU.add(getDecember());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to summarycollectionscollector : " + s.getMessage());
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
		sql="SELECT colid FROM summarycollectionscollector  ORDER BY colid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("colid");
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
		ps = conn.prepareStatement("SELECT colid FROM summarycollectionscollector WHERE colid=?");
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
		String sql = "UPDATE summarycollectionscollector set isactivecol=0 WHERE colid=?";
		
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
	public double getYear() {
		return year;
	}
	public void setYear(double year) {
		this.year = year;
	}
	public double getJanuary() {
		return January;
	}
	public void setJanuary(double january) {
		January = january;
	}
	public double getFebruary() {
		return February;
	}
	public void setFebruary(double february) {
		February = february;
	}
	public double getMarch() {
		return March;
	}
	public void setMarch(double march) {
		March = march;
	}
	public double getApril() {
		return April;
	}
	public void setApril(double april) {
		April = april;
	}
	public double getMay() {
		return May;
	}
	public void setMay(double may) {
		May = may;
	}
	public double getJune() {
		return June;
	}
	public void setJune(double june) {
		June = june;
	}
	public double getJuly() {
		return July;
	}
	public void setJuly(double july) {
		July = july;
	}
	public double getAugust() {
		return August;
	}
	public void setAugust(double august) {
		August = august;
	}
	public double getSeptember() {
		return September;
	}
	public void setSeptember(double september) {
		September = september;
	}
	public double getOctober() {
		return October;
	}
	public void setOctober(double october) {
		October = october;
	}
	public double getNovember() {
		return November;
	}
	public void setNovember(double november) {
		November = november;
	}
	public double getDecember() {
		return December;
	}
	public void setDecember(double december) {
		December = december;
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

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}
	
	
}

