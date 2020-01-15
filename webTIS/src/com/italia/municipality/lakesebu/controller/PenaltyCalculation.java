package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.italia.municipality.lakesebu.database.TaxDatabaseConnect;
import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.enm.PenalyMonth;
import com.italia.municipality.lakesebu.utils.DateUtils;

public class PenaltyCalculation {

	private long id;
	private int year;
	private double january;
	private double february;
	private double march;
	private double april;
	private double may;
	private double june;
	private double july;
	private double august;
	private double september;
	private double october;
	private double november;
	private double december;
	
	public PenaltyCalculation(){}
	
	public PenaltyCalculation(
			long id,
			int year,
			double january,
			double february,
			double march,
			double april,
			double may,
			double june,
			double july,
			double august,
			double september,
			double october,
			double november,
			double december
			){
		this.id = id;
		this.year = year;
		this.january = january;
		this.february = february;
		this.march = march;
		this.april = april;
		this.may = may;
		this.june = june;
		this.july = july;
		this.august = august;
		this.september = september;
		this.october = october;
		this.november = november;
		this.december = december;
	}
	
	
	public static List<PenaltyCalculation> retrieve(String sql, String[] params){
		List<PenaltyCalculation> calcs = Collections.synchronizedList(new ArrayList<>());
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = TaxDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		System.out.println("SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			PenaltyCalculation cal = new PenaltyCalculation();
			cal.setId(rs.getLong("calid"));
			cal.setYear(rs.getInt("year"));
			cal.setJanuary(rs.getDouble("jana"));
			cal.setFebruary(rs.getDouble("feba"));
			cal.setMarch(rs.getDouble("mara"));
			cal.setApril(rs.getDouble("apra"));
			cal.setMay(rs.getDouble("maya"));
			cal.setJune(rs.getDouble("juna"));
			cal.setJuly(rs.getDouble("jula"));
			cal.setAugust(rs.getDouble("auga"));
			cal.setSeptember(rs.getDouble("sepa"));
			cal.setNovember(rs.getDouble("nova"));
			cal.setDecember(rs.getDouble("deca"));
		}
		
		TaxDatabaseConnect.close(conn);
		rs.close();
		ps.close();
		
		}catch(SQLException e){}
		
		return calcs;
	}
	
	public static double monthPenalty(int year, PenalyMonth month){
		String sql = "SELECT " + month.getName() + " FROM landtaxpenaltiescal WHERE year=?";
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = TaxDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setInt(1, year);
		
		
		System.out.println("SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return rs.getDouble(month.getName());
		}
		
		TaxDatabaseConnect.close(conn);
		rs.close();
		ps.close();
		
		}catch(SQLException e){}
		
		
		return 0;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getJanuary() {
		return january;
	}
	public void setJanuary(double january) {
		this.january = january;
	}
	public double getFebruary() {
		return february;
	}
	public void setFebruary(double february) {
		this.february = february;
	}
	public double getMarch() {
		return march;
	}
	public void setMarch(double march) {
		this.march = march;
	}
	public double getApril() {
		return april;
	}
	public void setApril(double april) {
		this.april = april;
	}
	public double getMay() {
		return may;
	}
	public void setMay(double may) {
		this.may = may;
	}
	public double getJune() {
		return june;
	}
	public void setJune(double june) {
		this.june = june;
	}
	public double getJuly() {
		return july;
	}
	public void setJuly(double july) {
		this.july = july;
	}
	public double getAugust() {
		return august;
	}
	public void setAugust(double august) {
		this.august = august;
	}
	public double getSeptember() {
		return september;
	}
	public void setSeptember(double september) {
		this.september = september;
	}
	public double getOctober() {
		return october;
	}
	public void setOctober(double october) {
		this.october = october;
	}
	public double getNovember() {
		return november;
	}
	public void setNovember(double november) {
		this.november = november;
	}
	public double getDecember() {
		return december;
	}
	public void setDecember(double december) {
		this.december = december;
	}
	
	public static void main(String[] args) {
		int month = DateUtils.getCurrentMonth();
		System.out.println(PenaltyCalculation.monthPenalty(2015,PenalyMonth.month(month)));
		
	}
	
}
