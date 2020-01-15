package com.italia.municipality.lakesebu.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.italia.municipality.lakesebu.controller.BankAccounts;
import com.italia.municipality.lakesebu.controller.CashTransactionTreasury;
import com.italia.municipality.lakesebu.controller.CashTransactions;
import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.enm.AppConf;

/**
 * 
 * @author mark italia
 * @since 02/01/2017
 * @version 1.0
 *
 */
public class BookCashBook {

	private static final String PATH = AppConf.PRIMARY_DRIVE.getValue() + AppConf.SEPERATOR.getValue();
	private static final String APP_FOLDER = AppConf.APP_CONFIG_FOLDER_NAME.getValue();
	
	private static final String BACKUPBANK = PATH +  APP_FOLDER + AppConf.SEPERATOR.getValue() + AppConf.BACKUPCASHBOOKBANKXML.getValue() + AppConf.SEPERATOR.getValue(); 		
	private static final String LOAD_BANK = PATH +  APP_FOLDER + AppConf.SEPERATOR.getValue() + AppConf.LOADCASHBOOKBANKXML.getValue() + AppConf.SEPERATOR.getValue();
	
	private static final String BACKUPTREASURY = PATH +  APP_FOLDER + AppConf.SEPERATOR.getValue() + AppConf.BACKUPCASHBOOKTREASURYXML.getValue() + AppConf.SEPERATOR.getValue(); 		
	private static final String LOAD_TREASURY = PATH +  APP_FOLDER + AppConf.SEPERATOR.getValue() + AppConf.LOADCASHBOOKTREASURYXML.getValue() + AppConf.SEPERATOR.getValue();
	
	public static void loadXML(Object obj){
		
		if(obj instanceof CashTransactions){
			
		File folder = new File(LOAD_BANK);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	CashBookXML xml = readXML(obj,file.getName());
		    	CashTransactions tran = null;
		    	
		    	try{tran = CashTransactions.retrieve("SELECT * FROM cashtransactions WHERE voucherno='"+xml.getVoucherNo()+"'", new String[0]).get(0);}catch(Exception e){}
		    	
		    	if(tran==null){
		    		tran = new CashTransactions();
		    		tran.setVoucherNo(CashTransactions.voucherNumber(xml.getAccounts().getBankId()));
		    		tran.setOrNumber(xml.getOrNumber());
		    	}else{
		    		tran.setVoucherNo(tran.getVoucherNo());
		    		tran.setOrNumber(tran.getOrNumber());
		    	}
		    	
		    	tran.setDateTrans(xml.getDateTrans());
		    	tran.setCheckNo(xml.getCheckNo());
		    	tran.setParticulars(xml.getPayee());
		    	tran.setNaturePayment(xml.getNaturePayment());
		    	tran.setIsActive(xml.getIsActive());
		    	tran.setTransType(xml.getTransType());
		    	tran.setCreditAmount(xml.getCreditAmount());
		    	tran.setDepartment(xml.getDepartment());
		    	tran.setAccounts(xml.getAccounts());
		    	tran.setUserDtls(xml.getUserDtls());
		    	tran.save();
		    }
		}
		
		}else if(obj instanceof CashTransactionTreasury){
			
			File folder = new File(LOAD_TREASURY);
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
			    if (file.isFile()) {
			    	CashBookXML xml = readXML(obj,file.getName());
			    	CashTransactionTreasury tran = null;
			    	
			    	try{tran = CashTransactionTreasury.retrieve("SELECT * FROM cashtransactionstreasury WHERE voucherno='"+xml.getVoucherNo()+"'", new String[0]).get(0);}catch(Exception e){}
			    	
			    	if(tran==null){
			    		tran = new CashTransactionTreasury();
			    		tran.setVoucherNo(CashTransactionTreasury.voucherNumber(xml.getAccounts().getBankId()));
			    		tran.setOrNumber(xml.getOrNumber());
			    	}else{
			    		tran.setVoucherNo(tran.getVoucherNo());
			    		tran.setOrNumber(tran.getOrNumber());
			    	}
			    	
			    	tran.setDateTrans(xml.getDateTrans());
			    	tran.setCheckNo(xml.getCheckNo());
			    	tran.setParticulars(xml.getPayee());
			    	tran.setNaturePayment(xml.getNaturePayment());
			    	tran.setIsActive(xml.getIsActive());
			    	tran.setTransType(xml.getTransType());
			    	tran.setCreditAmount(xml.getCreditAmount());
			    	tran.setDepartment(xml.getDepartment());
			    	tran.setAccounts(xml.getAccounts());
			    	tran.setUserDtls(xml.getUserDtls());
			    	tran.save();
			    }
			}
			
		}
	}
	
	public static void createXML(Object obj, CashBookXML xml, String fileName){
		
		 try {
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("book");
		doc.appendChild(rootElement);
		
		// firstname elements
				Element cashDate = doc.createElement("cashDate");
				cashDate.appendChild(doc.createTextNode(xml.getDateTrans()));
				rootElement.appendChild(cashDate);

				Element departmentid = doc.createElement("departmentid");
				departmentid.appendChild(doc.createTextNode(xml.getDepartment().getDepid()+""));
				rootElement.appendChild(departmentid);
				
				Element orno = doc.createElement("orno");
				orno.appendChild(doc.createTextNode(xml.getOrNumber()));
				rootElement.appendChild(orno);
		
				Element checkno = doc.createElement("checkno");
				checkno.appendChild(doc.createTextNode(xml.getCheckNo()));
				rootElement.appendChild(checkno);
				
				Element voucherno = doc.createElement("voucherno");
				voucherno.appendChild(doc.createTextNode(xml.getVoucherNo()));
				rootElement.appendChild(voucherno);
				
				Element cashParticulars = doc.createElement("cashParticulars");
				cashParticulars.appendChild(doc.createTextNode(xml.getPayee()));
				rootElement.appendChild(cashParticulars);
				
				Element naturepayment = doc.createElement("naturepayment");
				naturepayment.appendChild(doc.createTextNode(xml.getNaturePayment()));
				rootElement.appendChild(naturepayment);
				
				Element cashisactive = doc.createElement("cashisactive");
				cashisactive.appendChild(doc.createTextNode(xml.getIsActive()+""));
				rootElement.appendChild(cashisactive);
				
				Element cashtranstype = doc.createElement("cashtranstype");
				cashtranstype.appendChild(doc.createTextNode(xml.getTransType()+""));
				rootElement.appendChild(cashtranstype);
				
				Element bankid = doc.createElement("bankid");
				bankid.appendChild(doc.createTextNode(xml.getAccounts().getBankId()+""));
				rootElement.appendChild(bankid);
				
				Element cashDebit = doc.createElement("cashDebit");
				cashDebit.appendChild(doc.createTextNode(xml.getDebitAmount()+""));
				rootElement.appendChild(cashDebit);
				
				Element cashCredit = doc.createElement("cashCredit");
				cashCredit.appendChild(doc.createTextNode(xml.getCreditAmount()+""));
				rootElement.appendChild(cashCredit);
				
				Element userdtlsid = doc.createElement("userdtlsid");
				userdtlsid.appendChild(doc.createTextNode(xml.getUserDtls().getUserdtlsid()+""));
				rootElement.appendChild(userdtlsid);
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				
				
				String backupfolder = "";
				if(obj instanceof CashTransactions){
					backupfolder = BACKUPBANK;
				}else if(obj instanceof CashTransactionTreasury){
					backupfolder = BACKUPTREASURY;
				}
				
				File folder = new File(backupfolder);
				if(folder.isDirectory()){
					//do nothing
				}else{
					folder.mkdirs();
				}
				
				StreamResult result = new StreamResult(new File(backupfolder + fileName));
				
				// Output to console for testing
				//StreamResult result = new StreamResult(System.out);
				//System.out.println(result);
				transformer.transform(source, result);
				
				
				

				System.out.println("File saved!");
				
		 } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
		} catch (TransformerException tfe) {
				tfe.printStackTrace();
		}
		
		
	}
	
	/**
	 * 
	 * @Read and load xml data to database
	 */
	public static CashBookXML readXML(Object obj, String fileName){
		CashBookXML xml = new CashBookXML();
		String loadXml = "";
		if(obj instanceof CashTransactions){
			loadXml = LOAD_BANK;
		}else if(obj instanceof CashTransactionTreasury){
			loadXml = LOAD_TREASURY;
		}
		
		File xmlFile = new File(loadXml + fileName);
		System.out.println("file load " + xmlFile.getAbsolutePath());
		if(xmlFile.exists()){
			try{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(xmlFile);
			
			/////////////normalize
			doc.getDocumentElement().normalize();
			//System.out.println("Reading conf......");
			
			NodeList ls = doc.getElementsByTagName("book");
			int size=ls.getLength();
			
			for(int i=0; i<size; i++){
				Node n = ls.item(i);
				//System.out.println("Current Node: "+ n.getNodeName());
				
				if(n.getNodeType() == Node.ELEMENT_NODE){
					Element e = (Element)n;
					
					xml.setDateTrans(e.getElementsByTagName(Book.DATE_TRANS.getTagName()).item(0).getTextContent());
					xml.setVoucherNo(e.getElementsByTagName(Book.VOUCHER.getTagName()).item(0).getTextContent());
					xml.setOrNumber(e.getElementsByTagName(Book.OR_NUMBER.getTagName()).item(0).getTextContent());
					xml.setCheckNo(e.getElementsByTagName(Book.CHECK_NO.getTagName()).item(0).getTextContent());
					xml.setPayee(e.getElementsByTagName(Book.PAYEE.getTagName()).item(0).getTextContent());
					xml.setNaturePayment(e.getElementsByTagName(Book.NATURE_PAYMENT.getTagName()).item(0).getTextContent());
					xml.setTransType(Integer.valueOf(e.getElementsByTagName(Book.TRANS_TYPE.getTagName()).item(0).getTextContent()));
					xml.setDebitAmount(Double.valueOf(e.getElementsByTagName(Book.DEBIT.getTagName()).item(0).getTextContent()));
					xml.setCreditAmount(Double.valueOf(e.getElementsByTagName(Book.CREDIT.getTagName()).item(0).getTextContent()));
					
					Department dep = new Department();
					dep.setDepid(Integer.valueOf(e.getElementsByTagName(Book.DEPARTMENT.getTagName()).item(0).getTextContent()));
					xml.setDepartment(dep);
					
					BankAccounts account = new  BankAccounts();
					account.setBankId(Integer.valueOf(e.getElementsByTagName(Book.BANK.getTagName()).item(0).getTextContent()));
					xml.setAccounts(account);
					
					UserDtls user = new UserDtls();
					user.setUserdtlsid(Long.valueOf(e.getElementsByTagName(Book.USER.getTagName()).item(0).getTextContent()));
					xml.setUserDtls(user);
					
					xml.setIsActive(Integer.valueOf(e.getElementsByTagName(Book.ACTIVE.getTagName()).item(0).getTextContent()));
					
				}
				
			}
			
			
			}catch(Exception e){
				
			}
			
			//delete the file after use
			xmlFile.delete();
			System.out.println("File successfully deleted...");
		}else{
			System.out.println("File is not exist");
		}
		return xml;
	}
	
}
