package com.italia.municipality.lakesebu.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.italia.municipality.lakesebu.controller.NumberToWords;
import com.italia.municipality.lakesebu.reports.ReportCompiler;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@ManagedBean(name="dBPCheckBean", eager=true)
@ViewScoped
public class DBPCheckBean implements Serializable{

	private String inputAmount;
	private String words;
	private String dateTime;
	
	private final Path PATH_APP = Paths.get("");
	private final String sep = File.separator;
	private final String REPORT_PATH = "C:" + sep + "reports" + sep;
			//FacesContext.getCurrentInstance().getExternalContext().getRealPath(sep +"resources"+sep+"reports")+sep;
	private final String REPORT_NAME = "BankChequeReport";
	
	public void setInputAmount(String inputAmount){
		this.inputAmount = inputAmount;
	}
	public String getInputAmount(){
		return inputAmount;
	}
	public void getGenerate(){
		String s = getNumberInToWords();
	}
	public String setGenerate(String s){
		return "";
	}
	public String getNumberInToWords(){
		String result = "";
		try{
		com.italia.municipality.lakesebu.controller.NumberToWords numberToWords =
				new NumberToWords();
		System.out.println("check input amount: " + inputAmount);
		result = numberToWords.changeToWords(inputAmount).toUpperCase();
		String[] val = new String[2];
		if(inputAmount!=null){
			val[0] = inputAmount;
			val[1] = result;
			
			System.out.println(val[0] + "----" + val[1]);
			
			//compileReport(val);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(result.equalsIgnoreCase(" ONLY.")) return "";
		return result.trim();
	}
	public void setNumberInToWords(String words){
		this.words = words;
	}
	
	public void setDateTime(String dateTime){
		this.dateTime = dateTime;
	}
	public String getDateTime(){
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");//new SimpleDateFormat("yyyy/MM/dd hh:mm: a");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		return dateFormat.format(date);
	}
	
	private void compileReport(String[] values){
		
		System.out.println("CheckReport path: " + REPORT_PATH);
		HashMap paramMap = new HashMap();
		
		ReportCompiler compiler = new ReportCompiler();
		System.out.println("REPORT_NAME: " +REPORT_NAME + " REPORT_PATH: " + REPORT_PATH);
		String reportLocation = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		System.out.println("Check report path: " + reportLocation);
		HashMap params = new HashMap();
		params.put("PARAM_AMOUNT", values[0]);
		params.put("PARAM_AMOUNT_INWORDS", values[1]);
		JasperPrint print = compiler.report(reportLocation, params);
		File pdf = null;
		try{
		pdf = new File(REPORT_PATH+REPORT_NAME+".pdf");
		pdf.createNewFile();
		JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(pdf));
		System.out.println("pdf successfully created...");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	public void printReport(){
		try{
		String pdfLocation = REPORT_PATH + REPORT_NAME + ".pdf";
		FacesContext faces = FacesContext.getCurrentInstance();
		ExternalContext context = faces.getExternalContext();
		HttpServletResponse response = (HttpServletResponse)context.getResponse();
		
		 File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
	     BufferedInputStream input = null;
	     BufferedOutputStream output = null;
		
		
	     try{
	    	 
	    	 // Open file.
	            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

	            // Init servlet response.
	            response.reset();
	            response.setHeader("Content-Type", "application/pdf");
	            response.setHeader("Content-Length", String.valueOf(file.length()));
	            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
	            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

	            // Write file contents to response.
	            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
	            int length;
	            while ((length = input.read(buffer)) > 0) {
	                output.write(buffer, 0, length);
	            }

	            // Finalize task.
	            output.flush();
	    	 
	     }finally{
	    	// Gently close streams.
	            close(output);
	            close(input);
	     }
	     
	     // Inform JSF that it doesn't need to handle response.
	        // This is very important, otherwise you will get the following exception in the logs:
	        // java.lang.IllegalStateException: Cannot forward after response has been committed.
	        faces.responseComplete();
	     
	     
		}catch(Exception ioe){
			ioe.printStackTrace();
		}
	}
	private void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                // Do your thing with the exception. Print it, log it or mail it. It may be useful to 
                // know that this will generally only be thrown when the client aborted the download.
                e.printStackTrace();
            }
        }
    }
	
}
