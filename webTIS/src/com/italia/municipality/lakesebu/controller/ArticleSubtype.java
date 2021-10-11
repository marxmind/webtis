package com.italia.municipality.lakesebu.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.utils.LogU;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mark Italia
 * @since 10/07/2021
 * @version 1.0
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class ArticleSubtype {

	private long id;
	private String name;
	private int isActive;
	
	private long articleId;
	private List articles;
	
	private Article article;
	
	public static List<ArticleSubtype> retrieve(String sqlAdd, String[] params){
		List<ArticleSubtype> subs = new ArrayList<ArticleSubtype>();
		
		String tableArt ="art";
		String tableSub = "sub";
		String sql = "SELECT * FROM articlesubtype "+ tableSub +", article "+ tableArt + "  WHERE "+tableSub+".subisactive=1 "
				+ " AND " + tableSub +".artid=" + tableArt + ".artid ";  
		
				
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
		System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		while(rs.next()){
			
			Article tax = Article.builder()
					.id(rs.getLong("artid"))
					.code(rs.getString("artcode"))
					.name(rs.getString("artname"))
					.description(rs.getString("artdesc"))
					.isActive(rs.getInt("artisactive"))
					.build();
			
			ArticleSubtype sub = ArticleSubtype.builder()
					.id(rs.getLong("subid"))
					.name(rs.getString("subname"))
					.isActive(rs.getInt("subisactive"))
					.article(tax)
					.build();
			
			
			
			subs.add(sub);
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return subs;
	}
	
	public static ArticleSubtype save(ArticleSubtype st){
		if(st!=null){
			
			long id = ArticleSubtype.getInfo(st.getId() ==0? ArticleSubtype.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = ArticleSubtype.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = ArticleSubtype.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = ArticleSubtype.insertData(st, "3");
			}
			
		}
		return st;
	}
	
	public void save(){
		
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		LogU.add("checking for new added data");
		if(id==1){
			LogU.add("insert new Data ");
			ArticleSubtype.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			ArticleSubtype.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			ArticleSubtype.insertData(this, "3");
		}
		
	}
	
	public static ArticleSubtype insertData(ArticleSubtype st, String type){
		String sql = "INSERT INTO articlesubtype ("
				+ "subid,"
				+ "subname,"
				+ "subisactive,"
				+ "artid)" 
				+ " VALUES(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table articlesubtype");
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
		
		ps.setString(cnt++, st.getName());
		ps.setInt(cnt++, st.getIsActive());
		ps.setLong(cnt++, st.getArticle().getId());
		
		LogU.add(st.getName());
		LogU.add(st.getIsActive());
		LogU.add(st.getArticle().getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to articlesubtype : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static ArticleSubtype updateData(ArticleSubtype st){
		String sql = "UPDATE articlesubtype SET "
				+ "subname=?,"
				+ "artid=?"
				+ " WHERE subid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table articlesubtype");
		
		ps.setString(cnt++, st.getName());
		ps.setLong(cnt++, st.getArticle().getId());
		ps.setLong(cnt++, st.getId());
		
		LogU.add(st.getName());
		LogU.add(st.getArticle().getId());
		LogU.add(st.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to articlesubtype : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT subid FROM articlesubtype  ORDER BY subid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("subid");
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
		ps = conn.prepareStatement("SELECT subid FROM articlesubtype WHERE subid=?");
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
		String sql = "UPDATE articlesubtype set subisactive=0 WHERE subid=?";
		
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
