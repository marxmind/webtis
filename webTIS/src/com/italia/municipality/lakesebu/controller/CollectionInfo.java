package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.enm.FormStatus;
import com.italia.municipality.lakesebu.enm.FormType;
import com.italia.municipality.lakesebu.enm.FundType;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @since 08-11-2018
 * @version 1.0
 *
 */
public class CollectionInfo {

	private long id;
	private String receivedDate;
	private int formType;
	private long beginningNo;
	private long endingNo;
	private int pcs;
	private int isActive;
	private int status;
	private int fundId;
	private Collector collector;
	private IssuedForm issuedForm;
	private int prevPcs;
	
	private double amount;
	private int rptGroup;
	
	private String formTypeName;
	private String statusName;
	
	private String startNo;
	private String endNo;
	
	private String rptFormat;
	private String fundName;
	private boolean hasCashTicket;
	
	private int isRts;
	
	public static int getNewReportGroup(long colloctorId, int fundId) {
		int rpt=0;
		
		String sql = "SELECT rptgroup FROM collectioninfo WHERE isactivecol=1 AND isid="+ colloctorId +" AND fundid="+ fundId +" ORDER BY colid DESC limit 1";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			rpt = rs.getInt("rptgroup");
		}
		
		rpt +=1;
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}	
		
		System.out.println("new group id >> " + rpt);
		
		return rpt;
	}
	
	public static boolean isGroupLatest(int groupNumber, long collectorId, int fundId) {
		int dbGroup=0;
		
		String sql = "SELECT rptgroup FROM collectioninfo WHERE isactivecol=1 AND isid="+ collectorId +" AND fundid="+ fundId +" ORDER BY colid DESC limit 1";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			dbGroup = rs.getInt("rptgroup");
			
			System.out.println("Access group >> " + groupNumber);
			System.out.println("dbGroup group >> " + dbGroup);
			
			if(groupNumber>=dbGroup) {
				return true;
			}else {
				return false;
			}
			
		}
		
		
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}	
		
		
		return false;
	}
	
	public static List<CollectionInfo> retrieve(String sqlAdd, String[] params){
		List<CollectionInfo> forms = new ArrayList<CollectionInfo>();
		
		String tableForm = "frm";
		String tableCol = "cl";
		String tableIssued = "sud";
		String sql = "SELECT * FROM collectioninfo "+ tableForm +", issuedcollector "+ tableCol  + ", logissuedform "+ tableIssued +"  WHERE  ";
		sql += tableForm + ".isid=" + tableCol + ".isid AND " + tableForm + ".logid=" + tableIssued + ".logid AND " + tableForm +".isactivecol=1 ";
		
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
		System.out.println("SQL Retrieve: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			CollectionInfo form = new CollectionInfo();
			try{form.setId(rs.getLong("colid"));}catch(NullPointerException e){}
			try{form.setReceivedDate(rs.getString("receiveddate"));}catch(NullPointerException e){}
			try{form.setFormType(rs.getInt("formtypecol"));}catch(NullPointerException e){}
			try{form.setBeginningNo(rs.getLong("beginningNoCol"));}catch(NullPointerException e){}
			try{form.setEndingNo(rs.getLong("endingNoCol"));}catch(NullPointerException e){}
			try{form.setPcs(rs.getInt("colpcs"));}catch(NullPointerException e){}
			try{form.setIsActive(rs.getInt("isactivecol"));}catch(NullPointerException e){}
			try{form.setStatus(rs.getInt("colstatus"));}catch(NullPointerException e){}
			try{form.setFormTypeName(FormType.nameId(rs.getInt("formtypecol")));}catch(NullPointerException e){}
			try{form.setStatusName(FormStatus.nameId(rs.getInt("colstatus")));}catch(NullPointerException e){}
			try{form.setPrevPcs(rs.getInt("prevPcs"));}catch(NullPointerException e){}
			try{form.setFundId(rs.getInt("fundid"));}catch(NullPointerException e){}
			try{form.setFundName(FundType.typeName(rs.getInt("fundid")));}catch(NullPointerException e){}
			
			try{form.setAmount(rs.getDouble("amount"));}catch(NullPointerException e){}
			try{form.setRptGroup(rs.getInt("rptgroup"));}catch(NullPointerException e){}
			try{form.setIsRts(rs.getInt("rts"));}catch(NullPointerException e){}
			
			IssuedForm issued = new IssuedForm();
			try{issued.setId(rs.getLong("logid"));}catch(NullPointerException e){}
			try{issued.setIssuedDate(rs.getString("issueddate"));}catch(NullPointerException e){}
			try{issued.setFormType(rs.getInt("formtypelog"));}catch(NullPointerException e){}
			try{issued.setBeginningNo(rs.getLong("beginningNoLog"));}catch(NullPointerException e){}
			try{issued.setEndingNo(rs.getLong("endingNoLog"));}catch(NullPointerException e){}
			try{issued.setPcs(rs.getInt("logpcs"));}catch(NullPointerException e){}
			try{issued.setIsActive(rs.getInt("isactivelog"));}catch(NullPointerException e){}
			try{issued.setStatus(rs.getInt("formstatus"));}catch(NullPointerException e){}
			try{issued.setFormTypeName(FormType.nameId(rs.getInt("formtypelog")));}catch(NullPointerException e){}
			try{issued.setStatusName(FormStatus.nameId(rs.getInt("formstatus")));}catch(NullPointerException e){}
			form.setIssuedForm(issued);
			
			Collector col = new Collector();
			try{col.setId(rs.getInt("isid"));}catch(NullPointerException e){}
			try{col.setName(rs.getString("collectorname"));}catch(NullPointerException e){}
			try{col.setIsActive(rs.getInt("isactivecollector"));}catch(NullPointerException e){}
			try{col.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			form.setCollector(col);
			
			forms.add(form);
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return forms;
	}
	
	public CollectionInfo retrieve(long id){
		CollectionInfo form = new CollectionInfo();
		
		String tableForm = "frm";
		String tableCol = "cl";
		String tableIssued = "sud";
		String sql = "SELECT * FROM collectioninfo "+ tableForm +", issuedcollector "+ tableCol  + ", logissuedform "+ tableIssued +"  WHERE  ";
		sql += tableForm + ".isid=" + tableCol + ".isid AND " + tableForm + ".logid=" + tableIssued + ".logid AND " + tableForm +".isactivecol=1 AND " + tableForm +".colid="+id;
		
		
				
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{form.setId(rs.getLong("colid"));}catch(NullPointerException e){}
			try{form.setReceivedDate(rs.getString("receiveddate"));}catch(NullPointerException e){}
			try{form.setFormType(rs.getInt("formtypecol"));}catch(NullPointerException e){}
			try{form.setBeginningNo(rs.getLong("beginningNoCol"));}catch(NullPointerException e){}
			try{form.setEndingNo(rs.getLong("endingNoCol"));}catch(NullPointerException e){}
			try{form.setPcs(rs.getInt("colpcs"));}catch(NullPointerException e){}
			try{form.setIsActive(rs.getInt("isactivecol"));}catch(NullPointerException e){}
			try{form.setStatus(rs.getInt("colstatus"));}catch(NullPointerException e){}
			try{form.setFormTypeName(FormType.nameId(rs.getInt("formtypecol")));}catch(NullPointerException e){}
			try{form.setStatusName(FormStatus.nameId(rs.getInt("colstatus")));}catch(NullPointerException e){}
			try{form.setPrevPcs(rs.getInt("prevPcs"));}catch(NullPointerException e){}
			try{form.setFundId(rs.getInt("fundid"));}catch(NullPointerException e){}
			try{form.setFundName(FundType.typeName(rs.getInt("fundid")));}catch(NullPointerException e){}
			try{form.setAmount(rs.getDouble("amount"));}catch(NullPointerException e){}
			try{form.setRptGroup(rs.getInt("rptgroup"));}catch(NullPointerException e){}
			try{form.setIsRts(rs.getInt("rts"));}catch(NullPointerException e){}
			
			IssuedForm issued = new IssuedForm();
			try{issued.setId(rs.getLong("logid"));}catch(NullPointerException e){}
			try{issued.setIssuedDate(rs.getString("issueddate"));}catch(NullPointerException e){}
			try{issued.setFormType(rs.getInt("formtypelog"));}catch(NullPointerException e){}
			try{issued.setBeginningNo(rs.getLong("beginningNoLog"));}catch(NullPointerException e){}
			try{issued.setEndingNo(rs.getLong("endingNoLog"));}catch(NullPointerException e){}
			try{issued.setPcs(rs.getInt("logpcs"));}catch(NullPointerException e){}
			try{issued.setIsActive(rs.getInt("isactivelog"));}catch(NullPointerException e){}
			try{issued.setStatus(rs.getInt("formstatus"));}catch(NullPointerException e){}
			try{issued.setFormTypeName(FormType.nameId(rs.getInt("formtypelog")));}catch(NullPointerException e){}
			try{issued.setStatusName(FormStatus.nameId(rs.getInt("formstatus")));}catch(NullPointerException e){}
			form.setIssuedForm(issued);
			
			Collector col = new Collector();
			try{col.setId(rs.getInt("isid"));}catch(NullPointerException e){}
			try{col.setName(rs.getString("collectorname"));}catch(NullPointerException e){}
			try{col.setIsActive(rs.getInt("isactivecollector"));}catch(NullPointerException e){}
			try{col.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			form.setCollector(col);
			
			
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return form;
	}
	
	public static CollectionInfo save(CollectionInfo is){
		if(is!=null){
			
			long id = CollectionInfo.getInfo(is.getId() ==0? CollectionInfo.getLatestId()+1 : is.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				is = CollectionInfo.insertData(is, "1");
			}else if(id==2){
				LogU.add("update Data ");
				is = CollectionInfo.updateData(is);
			}else if(id==3){
				LogU.add("added new Data ");
				is = CollectionInfo.insertData(is, "3");
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
	
	public static CollectionInfo insertData(CollectionInfo col, String type){
		String sql = "INSERT INTO collectioninfo ("
				+ "colid,"
				+ "receiveddate,"
				+ "formtypecol,"
				+ "beginningNoCol,"
				+ "endingNoCol,"
				+ "colpcs,"
				+ "isactivecol,"
				+ "isid,"
				+ "logid,"
				+ "colstatus,"
				+ "prevPcs,"
				+ "amount,"
				+ "rptgroup,"
				+ "fundid,"
				+ "rts)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table collectioninfo");
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
		
		ps.setString(cnt++, col.getReceivedDate());
		ps.setInt(cnt++, col.getFormType());
		ps.setLong(cnt++, col.getBeginningNo());
		ps.setLong(cnt++, col.getEndingNo());
		ps.setInt(cnt++, col.getPcs());
		ps.setInt(cnt++, col.getIsActive());
		ps.setInt(cnt++, col.getCollector().getId());
		ps.setLong(cnt++, col.getIssuedForm().getId());
		ps.setInt(cnt++, col.getStatus());
		ps.setInt(cnt++, col.getPrevPcs());
		ps.setDouble(cnt++, col.getAmount());
		ps.setInt(cnt++, col.getRptGroup());
		ps.setInt(cnt++, col.getFundId());
		ps.setInt(cnt++, col.getIsRts());
		
		LogU.add(col.getReceivedDate());
		LogU.add(col.getFormType());
		LogU.add(col.getBeginningNo());
		LogU.add(col.getEndingNo());
		LogU.add(col.getPcs());
		LogU.add(col.getIsActive());
		LogU.add(col.getCollector().getId());
		LogU.add(col.getIssuedForm().getId());
		LogU.add(col.getStatus());
		LogU.add(col.getPrevPcs());
		LogU.add(col.getAmount());
		LogU.add(col.getRptGroup());
		LogU.add(col.getFundId());
		LogU.add(col.getIsRts());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to collectioninfo : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return col;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO collectioninfo ("
				+ "colid,"
				+ "receiveddate,"
				+ "formtypecol,"
				+ "beginningNoCol,"
				+ "endingNoCol,"
				+ "colpcs,"
				+ "isactivecol,"
				+ "isid,"
				+ "logid,"
				+ "colstatus,"
				+ "prevPcs,"
				+ "amount,"
				+ "rptgroup,"
				+ "fundid,"
				+ "rts)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table collectioninfo");
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
		
		ps.setString(cnt++, getReceivedDate());
		ps.setInt(cnt++, getFormType());
		ps.setLong(cnt++, getBeginningNo());
		ps.setLong(cnt++, getEndingNo());
		ps.setInt(cnt++, getPcs());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getCollector().getId());
		ps.setLong(cnt++, getIssuedForm().getId());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getPrevPcs());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getRptGroup());
		ps.setInt(cnt++, getFundId());
		ps.setInt(cnt++, getIsRts());
		
		LogU.add(getReceivedDate());
		LogU.add(getFormType());
		LogU.add(getBeginningNo());
		LogU.add(getEndingNo());
		LogU.add(getPcs());
		LogU.add(getIsActive());
		LogU.add(getCollector().getId());
		LogU.add(getIssuedForm().getId());
		LogU.add(getStatus());
		LogU.add(getPrevPcs());
		LogU.add(getAmount());
		LogU.add(getRptGroup());
		LogU.add(getFundId());
		LogU.add(getIsRts());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to collectioninfo : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static CollectionInfo updateData(CollectionInfo col){
		String sql = "UPDATE collectioninfo SET "
				+ "receiveddate=?,"
				+ "formtypecol=?,"
				+ "beginningNoCol=?,"
				+ "endingNoCol=?,"
				+ "colpcs=?,"
				+ "isid=?,"
				+ "logid=?,"
				+ "colstatus=?,"
				+ "prevPcs=?,"
				+ "amount=?,"
				+ "rptgroup=?,"
				+ "fundid=?,"
				+ "rts=? " 
				+ " WHERE colid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table collectioninfo");
		
		
		ps.setString(cnt++, col.getReceivedDate());
		ps.setInt(cnt++, col.getFormType());
		ps.setLong(cnt++, col.getBeginningNo());
		ps.setLong(cnt++, col.getEndingNo());
		ps.setInt(cnt++, col.getPcs());
		ps.setInt(cnt++, col.getCollector().getId());
		ps.setLong(cnt++,col.getIssuedForm().getId());
		ps.setInt(cnt++, col.getStatus());
		ps.setInt(cnt++, col.getPrevPcs());
		ps.setDouble(cnt++, col.getAmount());
		ps.setInt(cnt++, col.getRptGroup());
		ps.setInt(cnt++, col.getFundId());
		ps.setInt(cnt++, col.getIsRts());
		ps.setLong(cnt++, col.getId());
		
		LogU.add(col.getReceivedDate());
		LogU.add(col.getFormType());
		LogU.add(col.getBeginningNo());
		LogU.add(col.getEndingNo());
		LogU.add(col.getPcs());
		LogU.add(col.getCollector().getId());
		LogU.add(col.getIssuedForm().getId());
		LogU.add(col.getStatus());
		LogU.add(col.getPrevPcs());
		LogU.add(col.getAmount());
		LogU.add(col.getRptGroup());
		LogU.add(col.getFundId());
		LogU.add(col.getIsRts());
		LogU.add(col.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to collectioninfo : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return col;
	}
	
	public void updateData(){
		String sql = "UPDATE collectioninfo SET "
				+ "receiveddate=?,"
				+ "formtypecol=?,"
				+ "beginningNoCol=?,"
				+ "endingNoCol=?,"
				+ "colpcs=?,"
				+ "isid=?,"
				+ "logid=?,"
				+ "colstatus=?,"
				+ "prevPcs=?,"
				+ "amount=?,"
				+ "rptgroup=?,"
				+ "fundid=?,"
				+ "rts=? " 
				+ " WHERE colid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table collectioninfo");
		
		
		ps.setString(cnt++, getReceivedDate());
		ps.setInt(cnt++, getFormType());
		ps.setLong(cnt++, getBeginningNo());
		ps.setLong(cnt++, getEndingNo());
		ps.setInt(cnt++, getPcs());
		ps.setInt(cnt++, getCollector().getId());
		ps.setLong(cnt++,getIssuedForm().getId());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getPrevPcs());
		ps.setDouble(cnt++, getAmount());
		ps.setInt(cnt++, getRptGroup());
		ps.setInt(cnt++, getFundId());
		ps.setInt(cnt++, getIsRts());
		ps.setLong(cnt++, getId());
		
		LogU.add(getReceivedDate());
		LogU.add(getFormType());
		LogU.add(getBeginningNo());
		LogU.add(getEndingNo());
		LogU.add(getPcs());
		LogU.add(getCollector().getId());
		LogU.add(getIssuedForm().getId());
		LogU.add(getStatus());
		LogU.add(getPrevPcs());
		LogU.add(getAmount());
		LogU.add(getRptGroup());
		LogU.add(getFundId());
		LogU.add(getIsRts());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to collectioninfo : " + s.getMessage());
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
		sql="SELECT colid FROM collectioninfo  ORDER BY colid DESC LIMIT 1";	
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
		ps = conn.prepareStatement("SELECT colid FROM collectioninfo WHERE colid=?");
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
		String sql = "UPDATE collectioninfo set isactivecol=0 WHERE colid=?";
		
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
	public String getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}
	public int getFormType() {
		return formType;
	}
	public void setFormType(int formType) {
		this.formType = formType;
	}
	public long getBeginningNo() {
		return beginningNo;
	}
	public void setBeginningNo(long beginningNo) {
		this.beginningNo = beginningNo;
	}
	public long getEndingNo() {
		return endingNo;
	}
	public void setEndingNo(long endingNo) {
		this.endingNo = endingNo;
	}
	public int getPcs() {
		return pcs;
	}
	public void setPcs(int pcs) {
		this.pcs = pcs;
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

	public String getFormTypeName() {
		return formTypeName;
	}

	public void setFormTypeName(String formTypeName) {
		this.formTypeName = formTypeName;
	}

	public IssuedForm getIssuedForm() {
		return issuedForm;
	}

	public void setIssuedForm(IssuedForm issuedForm) {
		this.issuedForm = issuedForm;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public int getPrevPcs() {
		return prevPcs;
	}

	public void setPrevPcs(int prevPcs) {
		this.prevPcs = prevPcs;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getRptGroup() {
		return rptGroup;
	}

	public void setRptGroup(int rptGroup) {
		this.rptGroup = rptGroup;
	}

	public String getStartNo() {
		return startNo;
	}

	public void setStartNo(String startNo) {
		this.startNo = startNo;
	}

	public String getEndNo() {
		return endNo;
	}

	public void setEndNo(String endNo) {
		this.endNo = endNo;
	}

	public String getRptFormat() {
		return rptFormat;
	}

	public void setRptFormat(String rptFormat) {
		this.rptFormat = rptFormat;
	}

	public boolean isHasCashTicket() {
		return hasCashTicket;
	}

	public void setHasCashTicket(boolean hasCashTicket) {
		this.hasCashTicket = hasCashTicket;
	}

	public int getFundId() {
		return fundId;
	}

	public void setFundId(int fundId) {
		this.fundId = fundId;
	}

	public String getFundName() {
		return fundName;
	}

	public void setFundName(String fundName) {
		this.fundName = fundName;
	}

	public int getIsRts() {
		return isRts;
	}

	public void setIsRts(int isRts) {
		this.isRts = isRts;
	}
	
}
