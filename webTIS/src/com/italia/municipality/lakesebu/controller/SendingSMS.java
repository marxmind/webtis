package com.italia.municipality.lakesebu.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class SendingSMS {

	private static final String API_KEY = "";
	
	public static void main(String[] args) {
		String command = "curl -X POST https://semaphore.co/api/v4/messages --data apikey=9840b879e62328ea67f19beebe3c39c1&number=0921424443&message=This is your data to be sent";
		try {
			Process process = Runtime.getRuntime().exec(command);
			
			String result = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
			System.out.println("Result: " + result);
			
			process.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * String sMsg = "test sms from api"; String sURL =
		 * "http://sample.onewaysms.com.au:xxxx/api.aspx?apiusername=xyz&apipassword=xyz&mobileno=639175121252&senderid=onewaysms&languagetype=1&message="
		 * + URLEncoder.encode(sMsg); String result = ""; HttpURLConnection conn = null;
		 * try { URL url = new URL(sURL); conn =
		 * (HttpURLConnection)url.openConnection(); conn.setDoOutput(false);
		 * conn.setRequestMethod("GET"); conn.connect(); int iResponseCode =
		 * conn.getResponseCode(); if ( iResponseCode == 200 ) { BufferedReader oIn =
		 * new BufferedReader(new InputStreamReader(conn.getInputStream())); String
		 * sInputLine = ""; String sResult = ""; while ((sInputLine = oIn.readLine()) !=
		 * null) { sResult = sResult + sInputLine; } if (Long.parseLong(sResult) > 0) {
		 * System.out.println("success - MT ID : " + sResult); } else {
		 * System.out.println("fail - Error code : " + sResult); } } else {
		 * System.out.println("fail "); } } catch (Exception e){ e.printStackTrace(); }
		 * finally { if (conn != null) { conn.disconnect(); } }
		 */
	    }
	
}
