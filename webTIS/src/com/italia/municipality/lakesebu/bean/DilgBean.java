package com.italia.municipality.lakesebu.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.italia.municipality.lakesebu.controller.DilgForms;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @since 11-16-2020
 * @version 1.0
 *
 */
@Named
@RequestScoped
public class DilgBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 132425356565756L;
	@Setter @Getter private List<DilgForms> forms;
	private StreamedContent tempPdfFile;
	private static final String DOCLOC = System.getenv("SystemDrive") + File.separator + "webtis" + File.separator + "dilgdocs";
	
	@PostConstruct
	public void init() {
		loadFiles();
	}
	public String generateRandomIdForNotCaching() {
		return java.util.UUID.randomUUID().toString();
	}
	
	private void loadFiles() {
		forms = new ArrayList<DilgForms>();
		File file = new File(DOCLOC); 
		for(File f : file.listFiles()) {
			if("pdf".equalsIgnoreCase(f.getName().split("\\.")[1])) {
				forms.add(DilgForms.builder().fileName(f.getName()).build());
			}
		}
	}
	
	public void clickFile(DilgForms f) {
		try {
		/*tempPdfFile = DefaultStreamedContent.builder()
				 .contentType("application/pdf")
				 .name(f.getFileName())
				 .stream(()-> this.getClass().getResourceAsStream(pdf))
				 .build();*/
		
		File pdfFile = new File(DOCLOC + File.separator + f.getFileName());
  	    
		tempPdfFile = DefaultStreamedContent.builder()
				 .contentType("application/pdf")
				 .name(f.getFileName())
				 .stream(()-> {
					try {
						return new FileInputStream(pdfFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				})
				 .build();
		
		}catch(Exception e) {e.printStackTrace();}
	}
	public void setTempPdfFile(StreamedContent tempPdfFile) {
		this.tempPdfFile = tempPdfFile;
	}
	public StreamedContent getTempPdfFile() throws IOException {
		
		if(tempPdfFile==null) {
			System.out.println("tempt is " + tempPdfFile);
			File pdfFile = new File(DOCLOC + File.separator + "UCA.pdf");
	  	    
		    return DefaultStreamedContent.builder()
					 .contentType("application/pdf")
					 .name("UCA.pdf")
					 .stream(()-> {
						try {
							return new FileInputStream(pdfFile);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return null;
						}
					})
					 .build();
		}else {
			return tempPdfFile;
		}
	  }
}
