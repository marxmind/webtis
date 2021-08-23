package com.italia.municipality.lakesebu.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.italia.municipality.lakesebu.controller.ORListing;
import com.italia.municipality.lakesebu.controller.ORNameList;
import com.italia.municipality.lakesebu.controller.PaymentName;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.database.ServerDatabase;
import com.italia.municipality.lakesebu.database.WebTISDatabaseConnect;
import com.italia.municipality.lakesebu.global.GlobalVar;
import com.italia.municipality.lakesebu.licensing.controller.Barangay;
import com.italia.municipality.lakesebu.licensing.controller.Customer;
import com.italia.municipality.lakesebu.licensing.controller.Municipality;
import com.italia.municipality.lakesebu.licensing.controller.Province;
import com.italia.municipality.lakesebu.licensing.controller.Purok;
import com.italia.municipality.lakesebu.xml.Check;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrlistingXML {

	private String reg;
	private String firstName;
	private String middleName;
	private String lastName;
	private String fullName;
	private String birthDate;
	private String civilStatus;
	
	private String userId;
	
	private String dateTrans;
	private String formType;
	private String isActive;
	private String customerId;
	private String collectorId;
	private String orStatus;
	private String orNumber;
	private String formInfo;
	
	//private List<ORNameList> orlisting;
	private List<PaymentName> paynames;
	
	public static void main(String[] args) {
		retrieveXMLforServerSaving();
	}
	
	public static void retrieveXMLforServerSaving() {
		File folder = new File(GlobalVar.UPLOAD_XML);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	OrlistingXML xml = readXML(file.getName());
		    	//System.out.println("Fullname:" + xml.getFullName());
		    	//for(PaymentName p : xml.getPaynames()) {
		    		//System.out.println("id: " + p.getId() + " amount: " + p.getAmount());
		    	//}
		    }
		}    
		
	}
	
	private static void saveOrlisting() {
		 
	}
	
	private static Customer getCustomerInfo(String fullname) {
		Customer cus = null;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ServerDatabase.getConnection();
		ps = conn.prepareStatement("SELECT * FROM customer WHERE cusisactive=1 AND fullname="+fullname);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			try{cus.setId(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{cus.setCivilStatus(rs.getInt("civilstatus"));}catch(NullPointerException e){}
			try{cus.setPhotoid(rs.getString("photoid"));}catch(NullPointerException e){}
			try{cus.setBornplace(rs.getString("bornplace"));}catch(NullPointerException e){}
			try{cus.setWeight(rs.getString("weight"));}catch(NullPointerException e){}
			try{cus.setHeight(rs.getString("heigt"));}catch(NullPointerException e){}
			try{cus.setWork(rs.getString("work"));}catch(NullPointerException e){}
			try{cus.setCitizenship(rs.getString("citizenship"));}catch(NullPointerException e){}
			
			try{cus.setQrcode(rs.getString("qrcode"));}catch(NullPointerException e){}
			try{cus.setNationalId(rs.getString("nationalid"));}catch(NullPointerException e){}
			if("1".equalsIgnoreCase(cus.getGender())){
				cus.setGenderName("Male");
			}else{
				cus.setGenderName("Female");
			}
			
			try{cus.setBirthdate(rs.getString("borndate"));}catch(NullPointerException e){}
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			cus.setUserDtls(user);
			
			Purok pur = new Purok();
			try{pur.setId(rs.getLong("purid"));}catch(NullPointerException e){}
			cus.setPurok(pur);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
			String address="";
			try{
				address = pur.getId()==0?"" : pur.getPurokName()+", ";
				address += bar.getId()==0?"" : bar.getName()+", ";
				address += mun.getId()==0?"" : mun.getName()+", ";
				address += prov.getName();
			}catch(Exception e){}
			cus.setCompleteAddress(address);
			
		}
		
		rs.close();
		ps.close();
		ServerDatabase.close(conn);
		
		}catch(Exception e){e.getMessage();}
		
		
		return cus;
	}
	
	private static OrlistingXML readXML(String fileName) {
		OrlistingXML xml = new OrlistingXML();
		File file = new File(GlobalVar.UPLOAD_XML + fileName);
		if(file.exists()){
			try{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(file);
			
			/////////////normalize
			doc.getDocumentElement().normalize();
			//System.out.println("Reading conf......");
			
			NodeList ls = doc.getElementsByTagName("orlisting");
			int size=ls.getLength();
			
			for(int i=0; i<size; i++){
				Node n = ls.item(i);
				//System.out.println("Current Node: "+ n.getNodeName());
				
				if(n.getNodeType() == Node.ELEMENT_NODE){
					Element e = (Element)n;
					
					xml.setReg(e.getElementsByTagName("reg").item(0).getTextContent());
					xml.setFirstName(e.getElementsByTagName("firstname").item(0).getTextContent());
					xml.setMiddleName(e.getElementsByTagName("middlename").item(0).getTextContent());
					xml.setLastName(e.getElementsByTagName("lastname").item(0).getTextContent());
					xml.setFullName(e.getElementsByTagName("fullname").item(0).getTextContent());
					xml.setBirthDate(e.getElementsByTagName("birthdate").item(0).getTextContent());
					xml.setCivilStatus(e.getElementsByTagName("civilstatus").item(0).getTextContent());
					xml.setUserId(e.getElementsByTagName("userid").item(0).getTextContent());
					
					xml.setDateTrans(e.getElementsByTagName("datetrans").item(0).getTextContent());
					xml.setFormType(e.getElementsByTagName("formtype").item(0).getTextContent());
					xml.setOrNumber(e.getElementsByTagName("ornumber").item(0).getTextContent());
					xml.setOrStatus(e.getElementsByTagName("status").item(0).getTextContent());
					xml.setCollectorId(e.getElementsByTagName("collectorid").item(0).getTextContent());
					xml.setIsActive(e.getElementsByTagName("isactive").item(0).getTextContent());
					xml.setFormInfo(e.getElementsByTagName("forminfo").item(0).getTextContent());
					
					List<PaymentName> pynames = new ArrayList<PaymentName>();
					NodeList lss = e.getElementsByTagName("details");
					for(int x=0; x<lss.getLength(); x++) {
						Node nn = lss.item(x);
						PaymentName oname = new PaymentName();
						if(nn.getNodeType() == Node.ELEMENT_NODE) {
							Element ee = (Element)nn;
							oname.setId(Long.valueOf(ee.getElementsByTagName("pyid").item(0).getTextContent()));
							oname.setAmount(Double.valueOf(ee.getElementsByTagName("amount").item(0).getTextContent()));
							pynames.add(oname);
						}
					}
					xml.setPaynames(pynames);
					
				}
			}
			}catch(Exception e){}
		}	
		
		return xml;
	}
	
	public static void saveForUploadXML(ORListing ors) {
		
		Customer cus = ors.getCustomer();
		
		//create directory if not present
		File dir = new File(GlobalVar.UPLOAD_XML);
		dir.mkdir();
		File file = new File(GlobalVar.UPLOAD_XML + cus.getFullname() + ".xml");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(file));
			StringBuilder sb = new StringBuilder();
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			sb.append(xml);sb.append("\n");
			
			sb.append("<orlisting>");sb.append("\n");
			
			//customer info
			sb.append("<reg>"+ DateUtils.getCurrentDateYYYYMMDD() +"</reg>");sb.append("\n");
			sb.append("<firstname>"+ cus.getFirstname() +"</firstname>");sb.append("\n");
			sb.append("<middlename>"+ cus.getMiddlename() +"</middlename>");sb.append("\n");
			sb.append("<lastname>"+ cus.getLastname() +"</lastname>");sb.append("\n");
			sb.append("<fullname>"+ cus.getFullname() +"</fullname>");sb.append("\n");
			sb.append("<birthdate>"+ cus.getBirthdate() +"</birthdate>");sb.append("\n");
			sb.append("<civilstatus>"+ cus.getCivilStatus() +"</civilstatus>");sb.append("\n");
			//user
			sb.append("<userid>"+ cus.getUserDtls().getUserdtlsid() +"</userid>");sb.append("\n");
			
			//forms details
			sb.append("<status>"+ ors.getStatus() +"</status>");sb.append("\n");
			sb.append("<datetrans>"+ ors.getDateTrans() +"</datetrans>");sb.append("\n");
			sb.append("<formtype>"+ ors.getFormType() +"</formtype>");sb.append("\n");
			sb.append("<ornumber>"+ ors.getOrNumber() +"</ornumber>");sb.append("\n");
			sb.append("<collectorid>"+ ors.getCollector().getId() +"</collectorid>");sb.append("\n");
			sb.append("<isactive>"+ ors.getIsActive() +"</isactive>");sb.append("\n");
			
			
			
			if(ors.getForminfo()!=null && !ors.getForminfo().isEmpty() && ors.getForminfo().contains("<->")) {
				sb.append("<forminfo>"+ ors.getForminfo().replace("<->", "@") +"</forminfo>");sb.append("\n");
			}else {
				sb.append("<forminfo>"+ ors.getForminfo() +"</forminfo>");sb.append("\n");
			}
			
			//details
			sb.append("<details>");sb.append("\n");
			
			int id=0;
			for(ORNameList name : ors.getOrNameList()) {
				//orid will be get when done uploading each Official Receipt to the server
				//same with id of customer
				sb.append("\t\t<payname id='"+ id++ +"'>");sb.append("\n");
				sb.append("\t\t\t<pyid>"+ name.getPaymentName().getId() +"</pyid>");sb.append("\n");
				sb.append("\t\t\t<amount>"+ name.getAmount() +"</amount>");sb.append("\n");
				sb.append("\t\t</payname>");sb.append("\n");
			}
			
			sb.append("</details>");sb.append("\n");
			
			
			sb.append("</orlisting>");sb.append("\n");
			
			pw.println(sb.toString());
			pw.flush();
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
