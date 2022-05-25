package com.italia.municipality.lakesebu.bean;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;

import com.italia.municipality.lakesebu.controller.Card;
import com.italia.municipality.lakesebu.controller.Department;
import com.italia.municipality.lakesebu.controller.EmployeeLoan;
import com.italia.municipality.lakesebu.controller.EmployeeMain;
import com.italia.municipality.lakesebu.enm.CivilStatus;
import com.italia.municipality.lakesebu.enm.EmployeeType;
import com.italia.municipality.lakesebu.enm.Gender;
import com.italia.municipality.lakesebu.enm.RateType;
import com.italia.municipality.lakesebu.global.GlobalVar;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;
import com.italia.municipality.lakesebu.utils.SignatureImageConverter;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 03/29/2022
 *
 */
@Named
@ViewScoped
public class EmployeeMainBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 14564678754534L;

	@Getter @Setter private EmployeeMain employee;
	
	@Getter @Setter private List types;
	@Getter @Setter private int typeId;
	@Getter @Setter private List genders;
	@Getter @Setter private int genderId;
	@Getter @Setter private List civils;
	@Getter @Setter private int civilId;
	@Getter @Setter private List deps;
	@Getter @Setter private int depId;
	@Getter @Setter private String searchParam;
	@Getter @Setter private List<EmployeeMain> employees;
	
	@Getter @Setter private List rateTypes;
	@Getter @Setter private int rateId;
	
	@Getter @Setter private List taxables;
	@Getter @Setter private List<String> shots = new ArrayList<>();
	@Getter @Setter private String photoId="employee";
	
	@Getter @Setter private List<EmployeeLoan> loans;
	@Getter @Setter private EmployeeLoan loan;
	
	@Getter @Setter private List<Card> cards;
	@Getter @Setter private Card card;
	
	@PostConstruct
	public void init() {
		System.out.println("init...");
		defaultValue();
		loadData();
	}
	
	private void convertSignatureToPng(EmployeeMain employee) {
		File file = new File(GlobalVar.EMPLOYEE_IMAGE_PATH_SIG);
		file.mkdir(); //create file for directory
		try {
			OutputStream out = new FileOutputStream(GlobalVar.EMPLOYEE_IMAGE_PATH_SIG + employee.getEmployeeId() + ".png");
			SignatureImageConverter.generateSignature(employee.getSignatureid(), out, 400, 200, "png");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void fileUploadListener(FileUploadEvent event) {

        try {
            BufferedImage image = ImageIO
                    .read(event.getFile().getInputStream());
            if (image != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", outputStream);
                byte[] bytes = outputStream.toByteArray();
	            System.out.println("Bytes -> " + bytes.length);
	            writeImageToFile(image);
            } else {
                throw new IOException("FAILED TO CONVERT PICTURE");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
	
	private void writeImageToFile(BufferedImage image){
		try{
			photoId = getRandomImageName() + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
			shots.add(photoId);
			File dir = new File(GlobalVar.EMPLOYEE_IMAGE_PATH_PHOTO);
			dir.mkdir();
			File fileImg = new File(GlobalVar.EMPLOYEE_IMAGE_PATH_PHOTO + photoId + ".jpg");
			System.out.println(photoId + ".jpg");
			if(image==null){
				fileImg = new File(GlobalVar.EMPLOYEE_IMAGE_PATH_PHOTO + "noimageproduct.jpg");
				 image = ImageIO.read(fileImg);
			}
			
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			String contextImageLoc = "resources" + File.separator + "images" + File.separator + "employee" + File.separator;
			String pathToSave = externalContext.getRealPath("") + contextImageLoc;
           // File file = new File(driveImage);
            try{
            	ImageIO.write(image, "jpg", fileImg);
    			Files.copy(fileImg.toPath(), (new File(pathToSave + fileImg.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			System.out.println("writing images....." + pathToSave);
    			}catch(IOException e){}
		
		}catch(IOException e){}
	}
	
	private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);
        return String.valueOf(i);
    }
	
	private String copyPhoto(String photoId){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String driveImage =  GlobalVar.EMPLOYEE_IMAGE_PATH_PHOTO + photoId + ".jpg";
        setPhotoId(photoId);
        String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "employee" + File.separator;
            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            File file = new File(driveImage);
            try{
    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
            return driveImage;
	}
	
	public void defaultValue() {
		
		taxables = new ArrayList<>();
		taxables.add(new SelectItem(0, "None"));
		taxables.add(new SelectItem(1, "Whole Year Paid"));
		
		types = new ArrayList<>();
		typeId = 1;
		for(EmployeeType t : EmployeeType.values()) {
			types.add(new SelectItem(t.getId(), t.getName()));
		}
		
		rateTypes = new ArrayList<>();
		//rateTypes.add(new SelectItem(0, "Select Type"));
		for(RateType t : RateType.values()) {
			rateTypes.add(new SelectItem(t.getId(), t.getName()));
		}
		
		genders = new ArrayList<>();
		genderId = 1;
		for(Gender g : Gender.values()) {
			genders.add(new SelectItem(g.getId(), g.getName()));
		}
		
		civils = new ArrayList<>();
		civilId = 1;
		for(CivilStatus c : CivilStatus.values()) {
			civils.add(new SelectItem(c.getId(), c.getName()));
		}
		
		deps = new ArrayList<>();
		depId = 1;
		for(Department d : Department.retrieve("SELECT * FROM department WHERE isactivedep=1 ORDER BY departmentname ASC", new String[0])) {
			deps.add(new SelectItem(d.getDepid(), d.getDepartmentName()));
		}
		
		addDefaultEmployee();
	}
	
	private void addDefaultEmployee() {
		
		setPhotoId("employee");
		setDepId(1);
		setCivilId(1);
		
		EmployeeMain emp = EmployeeMain.builder()
				.employeeId(EmployeeMain.getLatestEmloyeeId(EmployeeType.REGULAR))
				.regDate(DateUtils.getCurrentDateYYYYMMDD())
				.tempRegDate(DateUtils.getDateToday())
				.firstName(null)
				.middleName(null)
				.lastName(null)
				.fullName("")
				.birthDate(DateUtils.getCurrentDateYYYYMMDD())
				.tempBirthDate(DateUtils.getDateToday())
				.civilStatus(1)
				.position("")
				.department(Department.department("1"))
				.cctsId("")
				.employeeType(getTypeId())
				.address("")
				.gender(1)
				.contactNo("")
				.signatureid("")
				.isResigned(0)
				.isActiveEmployee(1)
				.emergecnyContactDtls("")
				.photoid("employee")
				.build();
		
		setEmployee(emp);
		
		
		List completes = new ArrayList<>();
		completes.add(new SelectItem(0, "No Loan"));
		completes.add(new SelectItem(1, "In-Flight"));
		completes.add(new SelectItem(2, "Completed"));
		EmployeeLoan ln = EmployeeLoan.builder()
				.tempApprovedDate(new Date())
				.completes(completes)
				.build();
		setLoan(ln);
		
		Card cd = Card.builder()
				.tempDateFrom(new Date())
				.tempDateTo(new Date())
				.isActive(1)
				.build();
		card = cd;
		
	}
	
	public void loadData() {
		employees = new ArrayList<EmployeeMain>();
		String sql = "";
		String[] params = new String[0];
		
		if(getSearchParam()!=null && !getSearchParam().isBlank() && getSearchParam().length()>3) {
			sql = " AND emp.fullname like '%"+ getSearchParam() +"%'";
		}
		
		sql += " ORDER BY emp.eid DESC";
		
		employees = EmployeeMain.retrieve(sql, params);
	}
	
	
	public void updateEmployeeIDSeries() {
		getEmployee().setEmployeeId(EmployeeMain.getLatestEmloyeeId(EmployeeType.value(getTypeId())));
	}
	
	public void clear() {
		setPhotoId("employee");
		setEmployee(null);
		setLoan(null);
		setCard(null);
		addDefaultEmployee();
	}
	
	public void clickItem(EmployeeMain em) {
		setCivilId(em.getCivilStatus());
		setDepId(em.getDepartment().getDepid());
		em.setTempRegDate(DateUtils.convertDateString(em.getRegDate(), "yyyy-MM-dd"));
		em.setTempBirthDate(DateUtils.convertDateString(em.getBirthDate(), "yyyy-MM-dd"));
		setEmployee(em);
		
		if(em.getPhotoid()!=null) {
			copyPhoto(em.getPhotoid());
		}
		
		loadLoans(em);
		loadCards(em);
	}
	
	private void loadLoans(EmployeeMain em) {
		loans = EmployeeLoan.retrieve(" AND emp.eid="+em.getId(), new String[0]);
	}
	
	private void loadCards(EmployeeMain em) {
		cards = Card.retrieve(" AND emp.eid="+em.getId(), new String[0]);
	}
	
	
	public void delete(EmployeeMain em) {
		em.delete();
		loadData();
		clear();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void save() {
		boolean isOk = true;
		EmployeeMain em = getEmployee();
		
		if(em.getFirstName()==null | em.getFirstName().isBlank()) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in first name");
		}
		if(em.getMiddleName()==null || em.getMiddleName().isBlank()) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in middle name");
		}
		if(em.getLastName()==null || em.getLastName().isBlank()) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in last name");
		}
		if(em.getAddress()==null || em.getAddress().isBlank()) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in address");
		}
		if(em.getPosition()==null || em.getPosition().isBlank()) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in position");
		}
		if(em.getEmergecnyContactDtls()==null) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in emergency contact details");
		}
		if(em.getContactNo()==null) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in contact details");
		}
		
		if(em.getRate()==0) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in salary details");
		}
		
		if(em.getRateType()==0) {
			isOk=false;
			Application.addMessage(2, "Error", "Please fill in salary type details");
		}
		
		boolean hasLoan = false;
		if(getLoan()!=null && getLoan().getLoanAmount()>0) {
			
			if(getLoan().getMonthlyDeduction()==0) {
				isOk=false;
				Application.addMessage(2, "Error", "Pleas add montly deduction");
			}
			
			if(getLoan().getIsCompleted()==0) {
				isOk=false;
				Application.addMessage(2, "Error", "Pleas fill in flight or completed");
			}
			if(isOk) {
				hasLoan = true;
			}
		}
		
		boolean hasCard=false;
		if(card!=null) {
			boolean requiredField=true;
			if(!card.getName().isEmpty() && card.getName().isEmpty()) {
				requiredField=false;
			}
			if(card.getName().isEmpty() && !card.getName().isEmpty()) {
				requiredField=false;
			}
			if(requiredField) {
				if(card.getNumber().isEmpty()) {
					isOk=false;
					Application.addMessage(2, "Error", "Pleas fill in required field");
				}
			}
			if(isOk) {
				hasCard=true;
			}
		}
			
		if(isOk) {	
			em.setRegDate(DateUtils.convertDate(em.getTempRegDate(), "yyyy-MM-dd"));
			em.setEmployeeType(getTypeId());
			em.setBirthDate(DateUtils.convertDate(em.getTempBirthDate(), "yyyy-MM-dd"));
			em.setCivilStatus(getCivilId());
			em.setGender(getGenderId());
			em.setFullName(em.getLastName() + ", " + em.getFirstName() + " " + em.getMiddleName());
			em.setPhotoid(getPhotoId());
			
			Department dep = new Department();
			dep.setDepid(getDepId());
			em.setDepartment(dep);
			em = EmployeeMain.save(em);
			
			if(hasLoan) {
				loan.setEmployeeMain(em);
				loan.setApprovedDate(DateUtils.convertDate(loan.getTempApprovedDate(), "yyyy-MM-dd"));
				loan.setIsActive(1);
				loan.save();
				loadLoans(em);
			}
			
			if(hasCard) {
				card.setValidFrom(DateUtils.convertDate(card.getTempDateFrom(), "yyyy-MM-dd"));
				card.setValidTo(DateUtils.convertDate(card.getTempDateTo(), "yyyy-MM-dd"));
				card.setEmployee(em);
				card.setIsActive(1);
				card.save();
				loadCards(em);
			}
			
			
			addDefaultEmployee();
			loadData();
			Application.addMessage(1, "Success", "Successfully saved.");
			
			convertSignatureToPng(em);
		}
	}
	
	/////loan function
	public void clickLoan(EmployeeLoan ln) {
		System.out.println(ln.getIsCompleted()==1? "In-flight":"Completed");
		loan.setId(ln.getId());
		loan.setTempApprovedDate(DateUtils.convertDateString(ln.getApprovedDate(),"yyyy-MM-dd"));
		loan.setApprovedDate(ln.getApprovedDate());
		loan.setName(ln.getName());
		loan.setIsActive(1);
		loan.setIsCompleted(ln.getIsCompleted());
		loan.setLoanAmount(ln.getLoanAmount());
		loan.setMonthlyDeduction(ln.getMonthlyDeduction());
	}
	public void deleteLoanRow(EmployeeLoan ln) {
		ln.delete();
		loadLoans(ln.getEmployeeMain());
		Application.addMessage(1,"Success", "Successfully deleted");
	}
	/////card function
	public void clickCard(Card cd) {
		card = cd;
		card.setTempDateFrom(DateUtils.convertDateString(cd.getValidFrom(), "yyyy-MM-dd"));
		card.setTempDateTo(DateUtils.convertDateString(cd.getValidTo(), "yyyy-MM-dd"));
	}
	public void deleteCardRow(Card cd) {
		cd.delete();
		loadCards(cd.getEmployee());
		Application.addMessage(1,"Success", "Successfully deleted");
	}
	
	
}








