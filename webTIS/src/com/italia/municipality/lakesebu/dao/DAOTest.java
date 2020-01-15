package com.italia.municipality.lakesebu.dao;

import java.sql.Connection;
import java.sql.SQLException;
import com.italia.municipality.lakesebu.dao.DAOFactory;
public class DAOTest {

	public static void main(String[] args) throws Exception{
		
		DAOFactory fc = DAOFactory.getInstance("italia.jdbc");
		System.out.println("Connecting..... " + fc);
	}
	
}
