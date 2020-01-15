package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.municipality.lakesebu.database.CashBookConnect;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 02/10/2017
 * @version 1.0
 *
 */
public class Voucher {

	private long id;
	private String dateTrans;
	private String checkNo;
	private String payee;
	private String naturePayment;
	private int isActive;
	private int transType;
	private double amount;
	private Timestamp timestamp;
	private UserDtls userDtls;
	private BankAccounts accounts;
	private Department department;
	
	private int cnt;
	private String transactionName;
	private String dAmount;
	private String departmentCode;
	private String departmentName;
	
	public Voucher(){}
	
	public Voucher(
			long id,
			String dateTrans,
			String checkNo,
			String payee,
			String naturePayment,
			int isActive,
			int transType,
			double amount,
			UserDtls userDtls,
			BankAccounts accounts,
			Department department
			){
		this.id = id;
		this.dateTrans = dateTrans;
		this.checkNo = checkNo;
		this.payee = payee;
		this.naturePayment = naturePayment;
		this.isActive = isActive;
		this.transType = transType;
		this.amount = amount;
		this.userDtls = userDtls;
		this.accounts = accounts;
		this.department = department;
	}
	
	public static List<Voucher> retrieve(String sql, String[] params){
		List<Voucher> trans = Collections.synchronizedList(new ArrayList<Voucher>());
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = CashBookConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
		
			Voucher tran = new Voucher();
			tran.setId(rs.getLong("vid"));
			tran.setDateTrans(rs.getString("vDate"));
			tran.setCheckNo(rs.getString("checkno"));
			tran.setPayee(rs.getString("vpayee"));
			tran.setNaturePayment(rs.getString("naturepayment"));
			tran.setIsActive(rs.getInt("visactive"));
			tran.setTransType(rs.getInt("vtranstype"));
			tran.setAmount(rs.getDouble("vamount"));
			tran.setTimestamp(rs.getTimestamp("vtimestamp"));
			
			UserDtls user = new UserDtls();
			user.setUserdtlsid(rs.getLong("userdtlsid"));
			tran.setUserDtls(user);
			
			BankAccounts accounts = new BankAccounts();
			accounts.setBankId(rs.getInt("bank_id"));
			tran.setAccounts(accounts);
			
			Department dep = new Department();
			dep.setDepid(rs.getInt("departmentid"));
			tran.setDepartment(dep);
			
			trans.add(tran);
		}
		
		rs.close();
		ps.close();
		CashBookConnect.close(conn);
		}catch(SQLException sl){sl.getMessage();}
		return trans;
	}
	
	public static Voucher save(Voucher tran){
		if(tran!=null){
			long id = getInfo(tran.getId()==0? getLatestId()+1 : tran.getId());
			if(id==1){
				tran = Voucher.insertData(tran, "1");
			}else if(id==2){
				tran = Voucher.updateData(tran);
			}else if(id==3){
				tran = Voucher.insertData(tran, "3");
			}
			
		}
		return tran;
	}
	
	public void save(){
			long id = getInfo(getId()==0? getLatestId()+1 : getId());
			if(id==1){
				insertData("1");
			}else if(id==2){
				updateData();
			}else if(id==3){
				insertData("3");
			}
	}
	
	public static Voucher insertData(Voucher tran, String type){
		String sql = "INSERT INTO voucher ("
				+ "vid,"
				+ "vDate,"
				+ "checkno,"
				+ "vpayee,"
				+ "naturepayment,"
				+ "visactive,"
				+ "vtranstype,"
				+ "vamount,"
				+ "userdtlsid,"
				+ "bank_id,"
				+ "departmentid) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = CashBookConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt=1;
		LogU.add("start inserting data to voucher");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			tran.setId(id);
			LogU.add(id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			tran.setId(id);
			ps.setLong(1, id);
			ps.setLong(cnt++, id);
			LogU.add(id);
		}
		
		ps.setString(cnt++, tran.getDateTrans());
		ps.setString(cnt++, tran.getCheckNo());
		ps.setString(cnt++, tran.getPayee());
		ps.setString(cnt++, tran.getNaturePayment());
		ps.setInt(cnt++, tran.getIsActive());
		ps.setInt(cnt++, tran.getTransType());
		ps.setDouble(cnt++, tran.getAmount());
		ps.setLong(cnt++, tran.getUserDtls()==null? 0 : tran.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, tran.getAccounts()==null? 0 : tran.getAccounts().getBankId());
		ps.setInt(cnt++, tran.getDepartment()==null? 0 : tran.getDepartment().getDepid());
		
		
		LogU.add(tran.getDateTrans());
		LogU.add(tran.getCheckNo());
		LogU.add(tran.getPayee());
		LogU.add(tran.getNaturePayment());
		LogU.add(tran.getIsActive());
		LogU.add(tran.getTransType());
		LogU.add(tran.getAmount());
		LogU.add(tran.getUserDtls()==null? 0 : tran.getUserDtls().getUserdtlsid());
		LogU.add(tran.getAccounts()==null? 0 : tran.getAccounts().getBankId());
		LogU.add(tran.getDepartment()==null? 0 : tran.getDepartment().getDepid());
		
		ps.execute();
		ps.close();
		CashBookConnect.close(conn);
		LogU.add("end inserting data to voucher");
		}catch(SQLException s){
			LogU.add("error inserting data to voucher : " + s.getMessage());
		}
		return tran;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO voucher ("
				+ "vid,"
				+ "vDate,"
				+ "checkno,"
				+ "vpayee,"
				+ "naturepayment,"
				+ "visactive,"
				+ "vtranstype,"
				+ "vamount,"
				+ "userdtlsid,"
				+ "bank_id,"
				+ "departmentid) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = CashBookConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt=1;
		LogU.add("start inserting data to voucher");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add(id);
			System.out.println("if id " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			setId(id);
			ps.setLong(1, id);
			ps.setLong(cnt++, id);
			LogU.add(id);
			System.out.println("else id " + id);
		}
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getCheckNo());
		ps.setString(cnt++, getPayee());
		ps.setString(cnt++, getNaturePayment());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getTransType());
		ps.setDouble(cnt++, getAmount());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getAccounts()==null? 0 : getAccounts().getBankId());
		ps.setInt(cnt++, getDepartment()==null? 0 : getDepartment().getDepid());
		
		LogU.add(getDateTrans());
		LogU.add(getCheckNo());
		LogU.add(getPayee());
		LogU.add(getNaturePayment());
		LogU.add(getIsActive());
		LogU.add(getTransType());
		LogU.add(getAmount());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getAccounts()==null? 0 : getAccounts().getBankId());
		LogU.add(getDepartment()==null? 0 : getDepartment().getDepid());
		
		ps.execute();
		ps.close();
		CashBookConnect.close(conn);
		LogU.add("end inserting data to voucher");
		}catch(SQLException s){
			LogU.add("error inserting data to voucher : " + s.getMessage());
			System.out.println(s.getMessage());
		}
	}
	
	public static Voucher updateData(Voucher tran){
		String sql = "UPDATE voucher SET "
				+ "vDate=?,"
				+ "checkno=?,"
				+ "vpayee=?,"
				+ "naturepayment=?,"
				+ "vtranstype=?,"
				+ "vamount=?,"
				+ "userdtlsid=?,"
				+ "bank_id=?,"
				+ "departmentid=? " 
				+ " WHERE vid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = CashBookConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("start updating data to voucher");
		
		ps.setString(cnt++, tran.getDateTrans());
		ps.setString(cnt++, tran.getCheckNo());
		ps.setString(cnt++, tran.getPayee());
		ps.setString(cnt++, tran.getNaturePayment());
		ps.setInt(cnt++, tran.getTransType());
		ps.setDouble(cnt++, tran.getAmount());
		ps.setLong(cnt++, tran.getUserDtls()==null? 0 : tran.getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, tran.getAccounts()==null? 0 : tran.getAccounts().getBankId());
		ps.setInt(cnt++, tran.getDepartment()==null? 0 : tran.getDepartment().getDepid());
		ps.setLong(cnt++, tran.getId());
		
		
		LogU.add(tran.getDateTrans());
		LogU.add(tran.getCheckNo());
		LogU.add(tran.getPayee());
		LogU.add(tran.getNaturePayment());
		LogU.add(tran.getIsActive());
		LogU.add(tran.getTransType());
		LogU.add(tran.getAmount());
		LogU.add(tran.getUserDtls()==null? 0 : tran.getUserDtls().getUserdtlsid());
		LogU.add(tran.getAccounts()==null? 0 : tran.getAccounts().getBankId());
		LogU.add(tran.getDepartment()==null? 0 : tran.getDepartment().getDepid());
		LogU.add(tran.getId());
		
		ps.execute();
		ps.close();
		CashBookConnect.close(conn);
		LogU.add("end updating data to voucher");
		}catch(SQLException s){
			LogU.add("error updating data to voucher : " + s.getMessage());
		}
		return tran;
	}
	
	public void updateData(){
		String sql = "UPDATE voucher SET "
				+ "vDate=?,"
				+ "checkno=?,"
				+ "vpayee=?,"
				+ "naturepayment=?,"
				+ "vtranstype=?,"
				+ "vamount=?,"
				+ "userdtlsid=?,"
				+ "bank_id=?,"
				+ "departmentid=? " 
				+ " WHERE vid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = CashBookConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt=1;
		LogU.add("start updating data to voucher");
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getCheckNo());
		ps.setString(cnt++, getPayee());
		ps.setString(cnt++, getNaturePayment());
		ps.setInt(cnt++, getTransType());
		ps.setDouble(cnt++, getAmount());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setInt(cnt++, getAccounts()==null? 0 : getAccounts().getBankId());
		ps.setInt(cnt++, getDepartment()==null? 0 : getDepartment().getDepid());
		ps.setLong(cnt++, getId());
		
		
		LogU.add(getDateTrans());
		LogU.add(getCheckNo());
		LogU.add(getPayee());
		LogU.add(getNaturePayment());
		LogU.add(getIsActive());
		LogU.add(getTransType());
		LogU.add(getAmount());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getAccounts()==null? 0 : getAccounts().getBankId());
		LogU.add(getDepartment()==null? 0 : getDepartment().getDepid());
		LogU.add(getId());
		
		ps.execute();
		ps.close();
		CashBookConnect.close(conn);
		LogU.add("end updating data to voucher");
		}catch(SQLException s){
			LogU.add("error updating data to voucher : " + s.getMessage());
		}
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT vid FROM voucher  ORDER BY vid DESC LIMIT 1";	
		conn = CashBookConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("vid");
		}
		
		rs.close();
		prep.close();
		CashBookConnect.close(conn);
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
		conn = CashBookConnect.getConnection();
		ps = conn.prepareStatement("SELECT vid FROM voucher WHERE vid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		CashBookConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
			
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = CashBookConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		ps.executeUpdate();
		ps.close();
		CashBookConnect.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
		
	}
	
	public void delete(){
		String sql = "update voucher set visactive=0, userdtlsid="+ Login.getUserLogin().getUserDtls().getUserdtlsid() +" WHERE vid=?";
		String params[] = new String[1];
		params[0] = getId()+"";
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = CashBookConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		ps.executeUpdate();
		ps.close();
		CashBookConnect.close(conn);
		}catch(SQLException s){
			s.getMessage();
		}
		
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
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public int getTransType() {
		return transType;
	}
	public void setTransType(int transType) {
		this.transType = transType;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}

	public BankAccounts getAccounts() {
		return accounts;
	}

	public void setAccounts(BankAccounts accounts) {
		this.accounts = accounts;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public String getdAmount() {
		return dAmount;
	}

	public void setdAmount(String dAmount) {
		this.dAmount = dAmount;
	}
	
	public String getCheckNo() {
		return checkNo;
	}

	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}

	public String getNaturePayment() {
		return naturePayment;
	}

	public void setNaturePayment(String naturePayment) {
		this.naturePayment = naturePayment;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public static void main(String[] args) {
		
		Voucher v = new Voucher();
		v.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		v.setPayee("mark");
		v.setNaturePayment("nature");
		v.setAmount(100);
		v.setTransType(11);
		v.setIsActive(1);
		
		Department department = new Department();
		department.setDepid(1);
		v.setDepartment(department);
		
		UserDtls user = new UserDtls();
		user.setUserdtlsid(1l);
		v.setUserDtls(user);
		
		BankAccounts acc = new BankAccounts();
		acc.setBankId(1);
		v.setAccounts(acc);
		
		v.save();
	}
	
}

