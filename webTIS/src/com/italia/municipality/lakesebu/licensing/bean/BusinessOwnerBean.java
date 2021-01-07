package com.italia.municipality.lakesebu.licensing.bean;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Named;

import org.primefaces.event.CaptureEvent;
import org.primefaces.event.FileUploadEvent;

import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.enm.CivilStatus;
import com.italia.municipality.lakesebu.enm.Relationships;
import com.italia.municipality.lakesebu.global.GlobalVar;
import com.italia.municipality.lakesebu.licensing.controller.Barangay;
import com.italia.municipality.lakesebu.licensing.controller.Customer;
import com.italia.municipality.lakesebu.licensing.controller.Municipality;
import com.italia.municipality.lakesebu.licensing.controller.Province;
import com.italia.municipality.lakesebu.licensing.controller.Purok;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;

@Named
@ViewScoped
public class BusinessOwnerBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1345465766767564534L;
	private String firstname;
	private String middlename;
	private String lastname;
	private String gender;
	private int age;
	private String address; 
	private String contactno;
	private String dateregistered;
	private String cardnumber;
	private String genderId;
	private List genderList = Collections.synchronizedList(new ArrayList<>());
	
	private List<Customer> customers = Collections.synchronizedList(new ArrayList<Customer>());
	private String searchCustomer;
	private Customer customer; 
	
	private boolean enableAdditionalButton;
	
	private int clivilId;
	private List civils;
	
	private int relationshipId;
	private List relationships;
	
	private Date birthdate;
	private String emergencyContactPersonName;
	private Customer emergencyContactPerson;
	private String searchEmergencyPerson;
	private List<Customer> contactPersons = Collections.synchronizedList(new ArrayList<Customer>());
	
	private String photoId="camera";
	
	private final String sep = File.separator;
	private final String PRIMARY_DRIVE = System.getenv("SystemDrive");
	private final String IMAGE_PATH = PRIMARY_DRIVE + sep + "bls" + sep + "img" + sep;
	private List<String> shots = new ArrayList<>();
	private String keyPress;
	
	private String searchProvince;
	private String searchMunicipal;
	private String searchBarangay;
	private String searchPurok;
	
	private List<Province> provinces = Collections.synchronizedList(new ArrayList<Province>());
	private List<Municipality> municipals = Collections.synchronizedList(new ArrayList<Municipality>());
	private List<Barangay> barangays = Collections.synchronizedList(new ArrayList<Barangay>());
	private List<Purok> puroks = Collections.synchronizedList(new ArrayList<Purok>());
	
	private Province provinceSelected;
	private Municipality municipalSelected;
	private Barangay barangaySelected;
	private Purok purokSelected;
	
	
	private boolean male;
	private boolean female;
	private boolean filteredBarangay;
	
	private long purokId;
	private List purokList;
	
	private String signature;
	
	@PostConstruct
	public void init(){
		
		try{
		String editProfileName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("editProfile");
		System.out.println("Check pass name: " + editProfileName);
		if(editProfileName!=null && !editProfileName.isEmpty() && !"null".equalsIgnoreCase(editProfileName)){
			setSearchCustomer(editProfileName);
		}
		}catch(Exception e){}
		
		
		String barangayCode = GlobalVar.BARANGAY_CODE;
		
		String sql = " AND cus.cusisactive=1 ";
		
		if(getPurokId()!=0){
			sql += " AND cus.purid=" + getPurokId();
		}
		
		if(getMale() && !getFemale()){
			sql += " AND cus.cusgender='1' ";
		}else if(!getMale() && getFemale()){
			sql += " AND cus.cusgender='2' ";
		}
		
		if(getFilteredBarangay()){
			sql += " AND bar.bgid=" + getBarangaySelected().getId();
		}
		
		if(getSearchCustomer()!=null && !getSearchCustomer().isEmpty()){
			
			String sanitize = getSearchCustomer();
			sanitize = sanitize.replace("--", "");
			sanitize = sanitize.replace("%", "");
			sanitize = sanitize.replace("/", "");
			sanitize = sanitize.replace("drop", "");
			sanitize = sanitize.replace("table", "");
			sanitize = sanitize.replace("select", "");
			sanitize = sanitize.replace(";", "");
			sanitize = sanitize.replace("'", "");
			
			int size = getSearchCustomer().length();
			if(size>=5){
				customers = Collections.synchronizedList(new ArrayList<Customer>());
				if(getSearchCustomer().contains(barangayCode)){
					String code = getSearchCustomer().toUpperCase();
					sql += " AND cus.cuscardno like '%" + code +"%'";
					customers = Customer.retrieve(sql, new String[0]);
				}else{
					if(sanitize!=null || !sanitize.isEmpty()) {
						sql += " AND cus.fullname like '%" + sanitize +"%'";
						customers = Customer.retrieve(sql, new String[0]);
					}
				}
				
			}
		}else{
			customers = Collections.synchronizedList(new ArrayList<Customer>());
			sql += " order by cus.customerid DESC limit 100;";
			customers = Customer.retrieve(sql, new String[0]);
		}
		
		
		
		if(customers!=null && customers.size()==1){
			clickItem(customers.get(0));
		}else{
			clearFields();
		}
		
	}
	
	public void loadAll(){
		clearFields();
		setFilteredBarangay(false);
		setMale(false);
		setFemale(false);
		customers = Collections.synchronizedList(new ArrayList<Customer>());
		
		String sql = " AND cus.cusisactive=1 ORDER BY cus.customerid ASC";
		customers = Customer.retrieve(sql, new String[0]);
		
	}
	
	public void loadFilter(){
		clearFields();
		
		customers = Collections.synchronizedList(new ArrayList<Customer>());
		
		String sql = " AND cus.cusisactive=1 ";
		
		if(getPurokId()!=0){
			sql += " AND cus.purid=" + getPurokId();
		}
		
		if(getMale() && !getFemale()){
			sql += " AND cus.cusgender='1' ";
		}else if(!getMale() && getFemale()){
			sql += " AND cus.cusgender='2' ";
		}
		
		if(getFilteredBarangay()){
			sql += " AND bar.bgid=" + getBarangaySelected().getId();
		}
		
		sql+=" ORDER BY cus.customerid ASC";
		customers = Customer.retrieve(sql, new String[0]);
		
	}
	
	public void loadContactPerson(){
		contactPersons = Collections.synchronizedList(new ArrayList<Customer>());
		String sql = " AND cus.cusisactive=1 ";
		
		if(getSearchEmergencyPerson()!=null && !getSearchEmergencyPerson().isEmpty()){
			int size = getSearchEmergencyPerson().length();
			if(size>=5){
				sql += " AND cus.fullname like '%" + getSearchEmergencyPerson() +"%'";
				contactPersons = Customer.retrieve(sql, new String[0]);
			}
		}else{
			sql += " order by cus.customerid DESC limit 10;";
			contactPersons = Customer.retrieve(sql, new String[0]);
		}
		
		
	}
	
	public void loadProvince(){
		provinces = Collections.synchronizedList(new ArrayList<Province>());
		try{
		String sql = " AND prv.provisactive=1";
		String[] params = new String[0];
		if(getSearchProvince()!=null){
			sql += " AND prv.provname like '%"+ getSearchProvince().replace("--", "") +"%'";
		}
		provinces = Province.retrieve(sql, params);
		Collections.reverse(provinces);
		}catch(Exception e){}
	}
	
	public void loadMunicipal(){
		municipals = Collections.synchronizedList(new ArrayList<Municipality>());
		try{
			String[] params = new String[1];
			String sql = " AND mun.munisactive=1 AND prv.provid=?";
			params[0] = getProvinceSelected().getId()+"";
			if(getSearchMunicipal()!=null){
				sql += " AND mun.munname like '%"+ getSearchMunicipal().replace("--", "") +"%'";
			}
			municipals = Municipality.retrieve(sql, params);
			Collections.reverse(municipals);
		}catch(Exception e){}
	}
	
	public void loadBarangay(){
		barangays = Collections.synchronizedList(new ArrayList<Barangay>());
		try{
		String[] params = new String[2];
		String sql = " AND bgy.bgisactive=1 AND prv.provid=? AND mun.munid=?";
		params[0] = getProvinceSelected().getId()+"";
		params[1] = getMunicipalSelected().getId()+"";
		if(getSearchBarangay()!=null){
			sql += " AND bgy.bgname like '%"+ getSearchBarangay().replace("--", "") +"%'";
		}
		barangays = Barangay.retrieve(sql, params);
		Collections.reverse(barangays);
		}catch(Exception e){}
	}
	
	public void loadPurok(){
		puroks = Collections.synchronizedList(new ArrayList<Purok>());
		try{
		String[] params = new String[2];
		String sql = " AND pur.isactivepurok=1 AND mun.munid=? AND bgy.bgid=? ";
		params[0] = getMunicipalSelected().getId()+"";
		params[1] = getBarangaySelected().getId()+"";
		if(getSearchPurok()!=null){
			sql += " AND pur.purokname like '%"+ getSearchPurok().replace("--", "") +"%'";
		}
		puroks = Purok.retrieve(sql, params);
		Collections.reverse(puroks);
		}catch(Exception e){}
	}
	
	public void clickItemPopup(Object obj){
		if(obj instanceof Province){
			Province prov = (Province)obj;
			setProvinceSelected(prov);
			setMunicipalSelected(null);
			setBarangaySelected(null);
			setPurokSelected(null);
		}else if(obj instanceof Municipality){
			Municipality mun = (Municipality)obj;
			setMunicipalSelected(mun);
			setBarangaySelected(null);
			setPurokSelected(null);
		}else if(obj instanceof Barangay){
			Barangay bar = (Barangay)obj;
			setBarangaySelected(bar);
			setPurokSelected(null);
		}else if(obj instanceof Purok){
			Purok pur = (Purok)obj;
			setPurokSelected(pur);
		}
		
	}
	
	public void clickContact(Customer person){
		setEmergencyContactPerson(person);
		setEmergencyContactPersonName(person.getFullname());
	}
	
	private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);
         
        return String.valueOf(i);
    }
	
	public void selectedPhoto(String photoId){
		setPhotoId(photoId);
	}
	
	public void deleteTmpImages(){
		
		if(getShots()!=null && getShots().size()>0){
			deletingImages();
		}
	}
	
	public void oncapture(CaptureEvent captureEvent) {
        //photoId = getRandomImageName();
		
		photoId = getRandomImageName() + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
		shots.add(photoId);
    	//filename ="cam";
        System.out.println("Set picture name " + photoId);
        byte[] data = captureEvent.getData();
 
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        
        String driveImage =  GlobalVar.OWNER_IMG_FOLDER + photoId + ".jpg";
        String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
        
        try{
        
        
        }catch(Exception e){}
        
        FileImageOutputStream imageOutput;
        try {
            imageOutput = new FileImageOutputStream(new File(driveImage));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();    
            
            
            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            File file = new File(driveImage);
            try{
    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
            //capturedImagePathName = contextImageLoc + photoId + ".jpg";
           // System.out.println("capture path " + capturedImagePathName.replace("\\", "/"));
           // setCapturedImagePathName(capturedImagePathName.replace("\\", "/"));
        }
        catch(IOException e) {
            throw new FacesException("Error in writing captured image.", e);
        }
    }
	
	public void printAll(){
		
	}
	
	public String save(){
		
		if(Login.getUserLogin().checkUserStatus()){
			
			Customer cus = new Customer();
			if(getCustomer()!=null){
				cus = getCustomer();
			}else{
				cus.setDateregistered(DateUtils.getCurrentDateYYYYMMDD());
			}
			
			boolean isOk = true;
			
			if(getFirstname()==null || getFirstname().isEmpty()){
				Application.addMessage(3, "Please provide firstname.", "");
				isOk= false;
			}
			
			if(getMiddlename()==null || getMiddlename().isEmpty()){
				Application.addMessage(3, "Please provide middlename.", "");
				isOk= false;
			}
			
			if(getLastname()==null || getLastname().isEmpty()){
				Application.addMessage(3, "Please provide lastname.", "");
				isOk= false;
			}
			
			if(getBirthdate()==null){
				Application.addMessage(3, "Please provide birthday.", "");
				isOk= false;
			}
			
			if(getAge()<0){
				Application.addMessage(3, "Please provide age.", "");
				isOk= false;
			}
			
			if(getEmergencyContactPerson()!=null && getEmergencyContactPerson().getCustomerid()!=0){
				if(getRelationshipId()==0){
					Application.addMessage(3, "Please provide relationship.", "");
					isOk= false;
				}
			}
			
			if("camera".equalsIgnoreCase(getPhotoId())){
				copyDefaultImage();
			}
			
			if(getCustomer()==null && getFirstname()!=null && getMiddlename()!=null && getLastname()!=null){
				
				boolean isExist = Customer.validateNameEntry(getFirstname(), getMiddlename(), getLastname());
				if(isExist){
					Application.addMessage(3, getFirstname() + " " + getLastname() + " is already recorded.", "");
					isOk= false;
				}
			}
			
			isOk = addressCondition(isOk);
			
			if(isOk){
				
				if(getContactno()==null){
					cus.setContactno("0");
				}else{
					cus.setContactno(getContactno());
				}
				
			
				
			//cus.setAddress(getAddress());	
			cus.setPhotoid(getPhotoId());	
			cus.setFirstname(getFirstname().trim());
			cus.setMiddlename(getMiddlename().trim());
			cus.setLastname(getLastname().trim());
			cus.setFullname(getFirstname().trim() + " " + getLastname().trim());
			cus.setGender(getGenderId());
			cus.setAge(getAge());
			cus.setCivilStatus(getClivilId());
			
			cus.setBirthdate(DateUtils.convertDate(getBirthdate(),"yyyy-MM-dd"));
			cus.setEmergencyContactPerson(getEmergencyContactPerson());
			cus.setRelationship(getRelationshipId());
			
			cus.setIsactive(1);
			cus.setUserDtls(Login.getUserLogin().getUserDtls());
			
			cus.setPurok(getPurokSelected());
			
			//Barangay bg = getBarMap().get(getBarangayId());
			cus.setBarangay(getBarangaySelected());
			
			//Municipality mun = getMunMap().get(getMunicipalityId());
			cus.setMunicipality(getMunicipalSelected());
			
			//Province prov = getProvMap().get(getProvinceId());
			cus.setProvince(getProvinceSelected());
			
			
			
			cus = Customer.save(cus);
			clickItem(cus);
			setCustomer(cus);
			
			//saveFinalImage();
			deletingImages();
			//clearFields();
			init();
			Application.addMessage(1, "Successfully saved.", "");
			}
		}
		
		return "save";
	}
	
	private boolean addressCondition(boolean isOk){
		
		
		
		if(getProvinceSelected()==null){
			Application.addMessage(3, "Please provide province.", "");
			isOk= false;
		}
		
		if(getProvinceSelected()!=null && getMunicipalSelected()==null && getBarangaySelected()==null && getPurokSelected()==null){
			
			Municipality mun = new Municipality();
			mun.setId(0);
			setMunicipalSelected(mun);
			
			Barangay bar = new Barangay();
			bar.setId(0);
			setBarangaySelected(bar);
			
			Purok pur = new Purok();
			pur.setId(0);
			setPurokSelected(pur);
		}
		
		if(getProvinceSelected()!=null && getMunicipalSelected()!=null && getBarangaySelected()==null && getPurokSelected()==null){
			
			Barangay bar = new Barangay();
			bar.setId(0);
			setBarangaySelected(bar);
			
			Purok pur = new Purok();
			pur.setId(0);
			setPurokSelected(pur);
		}
		
		if(getProvinceSelected()!=null && getMunicipalSelected()!=null && getBarangaySelected()!=null && getPurokSelected()==null){
			
			Purok pur = new Purok();
			pur.setId(0);
			setPurokSelected(pur);
		}
		
		
		return isOk;
	}
	
	private void copyDefaultImage(){
		
				photoId = getRandomImageName() + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
		        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		        
		        String cameralogo = IMAGE_PATH + "default.jpg";
		        String driveImage =  GlobalVar.OWNER_IMG_FOLDER + photoId + ".jpg";
		        
	            File file = new File(cameralogo);
	            try{
	    			Files.copy(file.toPath(), (new File(driveImage)).toPath(),
	    			        StandardCopyOption.REPLACE_EXISTING);
	    		}catch(IOException e){}
		        
		        
		        	String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
		            
		            
		            
		            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
		            File fileNew = new File(driveImage);
		            try{
		    			Files.copy(fileNew.toPath(), (new File(pathToSave + fileNew.getName())).toPath(),
		    			        StandardCopyOption.REPLACE_EXISTING);
		    		}catch(IOException e){}
		        
		        
		
	}
	
	public void removeEmergencyPerson(){
		setEmergencyContactPerson(null);
		setEmergencyContactPersonName(null);
		setRelationshipId(0);
		Application.addMessage(1, "Successfully removed.", "");
	}
	
	private void saveFinalImage(){
		
		if(!"camera".equalsIgnoreCase(getPhotoId())){
			String photoDestination = GlobalVar.OWNER_IMG_FOLDER + "photo" + File.separator;
			//check log directory
	        File logdirectory = new File(photoDestination);
	        if(!logdirectory.isDirectory()){
	        	logdirectory.mkdir();
	        }
			
			File file = new File(GlobalVar.OWNER_IMG_FOLDER + getPhotoId() + ".jpg");
			String pathToSave = photoDestination + getPhotoId() + ".jpg";
	         try{
	 			Files.copy(file.toPath(), (new File(pathToSave)).toPath(),
	 			        StandardCopyOption.REPLACE_EXISTING);
	 			
	 			file.delete();
	 			}catch(IOException e){}
	         
	         deletingImages();
		}
		
		 
	}
	
	/**
	 * Deleting old picture/s for the selected customer
	 */
	private void deletingImages(){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        
        String deleteImg = externalContext.getRealPath("") + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;// + photoId + ".jpg";
        
        try{
        
       getShots().remove(getPhotoId());	
       for(String name : shots){ 	
        	if(!getPhotoId().equalsIgnoreCase(name)){
	        File img = new File(GlobalVar.OWNER_IMG_FOLDER + name + ".jpg");
	        img.delete();
	       
	        img = new File(deleteImg + name + ".jpg");
	        img.delete();
        	}
       	}
        }catch(Exception e){}
	}
	
	public String close(){
		clearFields();
		init();
		return "close";
	}
	
	public void print(){
		
	}
	
	public void clickItem(Customer cus){
		shots = new ArrayList<>();//clearing picture
		setCustomer(cus);
		setCardnumber(cus.getCardno());
		setDateregistered(cus.getDateregistered());
		setFirstname(cus.getFirstname());
		setMiddlename(cus.getMiddlename()==null? "Middle Name" : cus.getMiddlename());
		setLastname(cus.getLastname());
		setGenderId(cus.getGender());
		try{setAge(DateUtils.calculateAge(cus.getBirthdate()));}catch(Exception e){setAge(cus.getAge());}
		setContactno(cus.getContactno());
		setClivilId(cus.getCivilStatus());
		
		setBirthdate(DateUtils.convertDateString(cus.getBirthdate(),"yyyy-MM-dd"));
		setEmergencyContactPerson(cus.getEmergencyContactPerson());
		if(cus.getEmergencyContactPerson()!=null){
		Customer person = Customer.customer(cus.getEmergencyContactPerson().getCustomerid());	
		setEmergencyContactPersonName(person.getFullname());
		setRelationshipId(cus.getRelationship());
		}else{
			setRelationshipId(0);
		}
		if(cus.getPhotoid()!=null){
			copyPhoto(cus.getPhotoid()); 
			getShots().add(cus.getPhotoid());
		}
		
		setProvinceSelected(cus.getProvince());
		setMunicipalSelected(cus.getMunicipality());
		setBarangaySelected(cus.getBarangay());
		setPurokSelected(cus.getPurok());
		
	}
	
	private String copyPhoto(String photoId){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String driveImage =  GlobalVar.OWNER_IMG_FOLDER + photoId + ".jpg";
        setPhotoId(photoId);
        String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            File file = new File(driveImage);
            try{
    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
            return driveImage;
	}
	
	public void calculateAge(){
		String dateBirth = DateUtils.convertDate(getBirthdate(),"yyyy-MM-dd");
		setAge(Integer.valueOf(Math.round(DateUtils.calculateAgeNow(dateBirth))+""));
	}
	
	public void closePopup(){
		setEnableAdditionalButton(true);
		clearFields();
		init();
	}
	
	public void clearFields(){
		setFirstname(null);
		setMiddlename(null);
		setLastname(null);
		setGender(null);
		setAge(0);
		setAddress(null);
		setContactno(null);
		setDateregistered(null);
		setSearchCustomer(null);
		setCardnumber(null);
		setCustomer(null);
		setGenderId(null);
		
		setClivilId(1);
		setPhotoId(null);
		shots = new ArrayList<>();
		setEmergencyContactPerson(null);
		setBirthdate(null);
		setRelationshipId(0);
		setEmergencyContactPersonName(null);
		
		setProvinceSelected(null);
		setMunicipalSelected(null);
		setBarangaySelected(null);
		setPurokSelected(null);
		
		setSearchProvince(null);
		setSearchMunicipal(null);
		setSearchBarangay(null);
		setSearchPurok(null);
		
		setEnableAdditionalButton(true);
		
		loadDefaultAddress();
	}
	
	private void loadDefaultAddress(){
		try{
		String sql = " AND prv.provisactive=1 AND prv.provname=?";
		String[] params = new String[1];
		params[0] = GlobalVar.PROVINCE;
		provinces = Province.retrieve(sql, params);
		setProvinceSelected(provinces.get(0));
		
		params = new String[2];
		sql = " AND mun.munisactive=1 AND prv.provid=? AND mun.munname=?";
		params[0] = getProvinceSelected().getId()+"";
		params[1] = GlobalVar.MUNICIPALITY;
		municipals = Municipality.retrieve(sql, params);
		setMunicipalSelected(municipals.get(0));
		
		params = new String[3];
		sql = " AND bgy.bgisactive=1 AND prv.provid=? AND mun.munid=? AND bgy.bgname=?";
		params[0] = getProvinceSelected().getId()+"";
		params[1] = getMunicipalSelected().getId()+"";
		params[2] = GlobalVar.BARANGAY;
		barangays = Barangay.retrieve(sql, params);
		setBarangaySelected(barangays.get(0));
		
		}catch(Exception e){}
	}
	
	public void deleteRow(Customer cus){
		if(Login.getUserLogin().checkUserStatus()){
			cus.setUserDtls(Login.getUserLogin().getUserDtls());
			cus.delete();
			init();
			Application.addMessage(1, "Successfully deleted", "");
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
			File fileImg = new File(GlobalVar.OWNER_IMG_FOLDER + photoId + ".jpg");
			System.out.println(photoId + ".jpg");
			if(image==null){
				fileImg = new File(GlobalVar.OWNER_IMG_PATH + "noimageproduct.jpg");
				 image = ImageIO.read(fileImg);
			}
			
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			String contextImageLoc = "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
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
	
	public List<String> autoFirst(String query){
		int size = query.length();
		if(size>=2){
			return Customer.retrieve(query, "cusfirstname"," limit 10");
		}else{
			return Collections.synchronizedList(new ArrayList<String>());
		}
	}
	public List<String> autoMiddle(String query){
		int size = query.length();
		if(size>=2){
			return Customer.retrieve(query, "cusmiddlename"," limit 10");
		}else{
			return Collections.synchronizedList(new ArrayList<String>());
		}
	}
	public List<String> autoLast(String query){
		int size = query.length();
		if(size>=2){	
			return Customer.retrieve(query, "cuslastname"," limit 10");
		}else{
			return Collections.synchronizedList(new ArrayList<String>());
		}	
	}
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContactno() {
		return contactno;
	}
	public void setContactno(String contactno) {
		this.contactno = contactno;
	}
	public List<Customer> getCustomers() {
		return customers;
	}
	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}
	public String getSearchCustomer() {
		return searchCustomer;
	}
	public void setSearchCustomer(String searchCustomer) {
		this.searchCustomer = searchCustomer;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getDateregistered() {
		if(dateregistered==null){
			dateregistered = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateregistered;
	}

	public void setDateregistered(String dateregistered) {
		this.dateregistered = dateregistered;
	}

	public String getCardnumber() {
		if(cardnumber==null){
			cardnumber = Customer.cardNumber();
		}
		return cardnumber;
	}

	public void setCardnumber(String cardnumber) {
		this.cardnumber = cardnumber;
	}

	public String getGenderId() {
		if(genderId==null){
			genderId = "1";
		}
		return genderId;
	}

	public void setGenderId(String genderId) {
		this.genderId = genderId;
	}

	public List getGenderList() {
		
		genderList = Collections.synchronizedList(new ArrayList<>());
		genderList.add(new SelectItem("1","Male"));
		genderList.add(new SelectItem("2","Female"));
		
		return genderList;
	}

	public void setGenderList(List genderList) {
		this.genderList = genderList;
	}

	/*public List getBarangay() {
		
		barMap = Collections.synchronizedMap(new HashMap<Integer, Barangay>());
		String sql = " AND bgy.bgisactive=1";
		if(getMunicipalityId()!=0 && getProvinceId()!=0){
			sql += " AND mun.munid="+getMunicipalityId() + " AND prv.provid=" + getProvinceId();
		}
		
		barangay = new ArrayList<>();
		List<Barangay> bars = Barangay.retrieve(sql, new String[0]);
		if(bars!=null && bars.size()>0){
			for(Barangay bg : bars){
				barangay.add(new SelectItem(bg.getId(), bg.getName()));
				barMap.put(bg.getId(), bg);
			}
		}else{
			Barangay bar = new Barangay();
			bar.setId(0);
			bar.setName("N/A");
			barangay.add(new SelectItem(bar.getId(), bar.getName()));
			barMap.put(bar.getId(), bar);
		}
		
		return barangay;
	}

	public void setBarangay(List barangay) {
		this.barangay = barangay;
	}

	public int getBarangayId() {
		if(barangayId==0){
			barangayId =1;
		}
		return barangayId;
	}

	public void setBarangayId(int barangayId) {
		this.barangayId = barangayId;
	}

	public List getMunicipality() {
		
		munMap = Collections.synchronizedMap(new HashMap<Integer, Municipality>());
		String sql = " AND mun.munisactive=1";
		if(getProvinceId()!=0){
			sql += " AND prv.provid=" + getProvinceId();
		}
		municipality = new ArrayList<>();
		List<Municipality> muns = Municipality.retrieve(sql, new String[0]);
		
		if(muns!=null && muns.size()>0){
			for(Municipality bg : muns){
				municipality.add(new SelectItem(bg.getId(), bg.getName()));
				munMap.put(bg.getId(), bg);
			}
		}else{
			Municipality mun = new Municipality();
			mun.setId(0);
			mun.setName("N/A");
			munMap.put(mun.getId(), mun);
			municipality.add(new SelectItem(mun.getId(), mun.getName()));
		}
		
		return municipality;
	}

	public void setMunicipality(List municipality) {
		this.municipality = municipality;
	}

	public int getMunicipalityId() {
		if(municipalityId==0){
			municipalityId =1;
		}
		return municipalityId;
	}

	public void setMunicipalityId(int municipalityId) {
		this.municipalityId = municipalityId;
	}

	public List getProvince() {
		
		provMap = Collections.synchronizedMap(new HashMap<Integer, Province>());
		
		province = new ArrayList<>();
		for(Province bg : Province.retrieve(" AND prv.provisactive=1", new String[0])){
			province.add(new SelectItem(bg.getId(), bg.getName()));
			provMap.put(bg.getId(), bg);
		}
		
		return province;
	}

	public void setProvince(List province) {
		this.province = province;
	}

	public int getProvinceId() {
		if(provinceId==0){
			provinceId = 1;
		}
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public Map<Integer, Barangay> getBarMap() {
		return barMap;
	}

	public void setBarMap(Map<Integer, Barangay> barMap) {
		this.barMap = barMap;
	}

	public Map<Integer, Municipality> getMunMap() {
		return munMap;
	}

	public void setMunMap(Map<Integer, Municipality> munMap) {
		this.munMap = munMap;
	}

	public Map<Integer, Province> getProvMap() {
		return provMap;
	}

	public void setProvMap(Map<Integer, Province> provMap) {
		this.provMap = provMap;
	}*/

	public int getClivilId() {
		if(clivilId==0){
			clivilId = 1;
		}
		return clivilId;
	}

	public void setClivilId(int clivilId) {
		this.clivilId = clivilId;
	}

	public List getCivils() {
		civils = new ArrayList<>();
		
		for(CivilStatus cv : CivilStatus.values()){
			civils.add(new SelectItem(cv.getId(), cv.getName()));
		}
		
		return civils;
	}

	public void setCivils(List civils) {
		this.civils = civils;
	}

	public int getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(int relationshipId) {
		this.relationshipId = relationshipId;
	}

	public List getRelationships() {
		relationships = new ArrayList<>();
		for(Relationships rel : Relationships.values()){
			relationships.add(new SelectItem(rel.getId(), rel.getName()));
		}
		return relationships;
	}

	public void setRelationships(List relationships) {
		this.relationships = relationships;
	}

	public Date getBirthdate() {
		if(birthdate==null){
			birthdate = DateUtils.getDateToday();
		}
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public Customer getEmergencyContactPerson() {
		return emergencyContactPerson;
	}

	public void setEmergencyContactPerson(Customer emergencyContactPerson) {
		this.emergencyContactPerson = emergencyContactPerson;
	}

	public String getSearchEmergencyPerson() {
		return searchEmergencyPerson;
	}

	public void setSearchEmergencyPerson(String searchEmergencyPerson) {
		this.searchEmergencyPerson = searchEmergencyPerson;
	}

	public List<Customer> getContactPersons() {
		return contactPersons;
	}

	public String getEmergencyContactPersonName() {
		return emergencyContactPersonName;
	}

	public void setEmergencyContactPersonName(String emergencyContactPersonName) {
		this.emergencyContactPersonName = emergencyContactPersonName;
	}

	public void setContactPersons(List<Customer> contactPersons) {
		this.contactPersons = contactPersons;
	}

	public String getPhotoId() {
		if(photoId==null){
			photoId="camera";
		}
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public List<String> getShots() {
		return shots;
	}

	public void setShots(List<String> shots) {
		this.shots = shots;
	}

	public String getKeyPress() {
		keyPress = "idFind";
		return keyPress;
	}

	public void setKeyPress(String keyPress) {
		this.keyPress = keyPress;
	}

	public String getSearchProvince() {
		return searchProvince;
	}

	public void setSearchProvince(String searchProvince) {
		this.searchProvince = searchProvince;
	}

	public String getSearchMunicipal() {
		return searchMunicipal;
	}

	public void setSearchMunicipal(String searchMunicipal) {
		this.searchMunicipal = searchMunicipal;
	}

	public String getSearchBarangay() {
		return searchBarangay;
	}

	public void setSearchBarangay(String searchBarangay) {
		this.searchBarangay = searchBarangay;
	}

	public List<Province> getProvinces() {
		return provinces;
	}

	public void setProvinces(List<Province> provinces) {
		this.provinces = provinces;
	}

	public List<Municipality> getMunicipals() {
		return municipals;
	}

	public void setMunicipals(List<Municipality> municipals) {
		this.municipals = municipals;
	}

	public List<Barangay> getBarangays() {
		return barangays;
	}

	public void setBarangays(List<Barangay> barangays) {
		this.barangays = barangays;
	}

	public Province getProvinceSelected() {
		return provinceSelected;
	}

	public void setProvinceSelected(Province provinceSelected) {
		this.provinceSelected = provinceSelected;
	}

	public Municipality getMunicipalSelected() {
		return municipalSelected;
	}

	public void setMunicipalSelected(Municipality municipalSelected) {
		this.municipalSelected = municipalSelected;
	}

	public Barangay getBarangaySelected() {
		return barangaySelected;
	}

	public void setBarangaySelected(Barangay barangaySelected) {
		this.barangaySelected = barangaySelected;
	}

	public Purok getPurokSelected() {
		return purokSelected;
	}

	public void setPurokSelected(Purok purokSelected) {
		this.purokSelected = purokSelected;
	}

	public String getSearchPurok() {
		return searchPurok;
	}

	public void setSearchPurok(String searchPurok) {
		this.searchPurok = searchPurok;
	}

	public List<Purok> getPuroks() {
		return puroks;
	}

	public void setPuroks(List<Purok> puroks) {
		this.puroks = puroks;
	}

	public boolean isEnableAdditionalButton() {
		if(getCustomer()!=null){
			enableAdditionalButton = false;
		}else{
			enableAdditionalButton = true;
		}
		return enableAdditionalButton;
	}

	public void setEnableAdditionalButton(boolean enableAdditionalButton) {
		this.enableAdditionalButton = enableAdditionalButton;
	}

	public boolean getMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public boolean getFemale() {
		return female;
	}

	public void setFemale(boolean female) {
		this.female = female;
	}

	public long getPurokId() {
		return purokId;
	}

	public void setPurokId(long purokId) {
		this.purokId = purokId;
	}

	public List getPurokList() {
		//list loaded in loadDefaultAddress();
		try{
		String[] params = new String[2];
		String sql = " AND pur.isactivepurok=1 AND mun.munid=? AND bgy.bgid=? ORDER BY pur.purokname";
		params[0] = getMunicipalSelected().getId()+"";
		params[1] = getBarangaySelected().getId()+"";
		
		purokList = Collections.synchronizedList(new ArrayList<Purok>());
		purokList.add(new SelectItem(0, "All Purok"));
		for(Purok p : Purok.retrieve(sql, params)){
			purokList.add(new SelectItem(p.getId(), p.getPurokName()));
		}
		}catch(Exception e){}
		
		return purokList;
	}

	public void setPurokList(List purokList) {
		this.purokList = purokList;
	}

	public boolean getFilteredBarangay() {
		return filteredBarangay;
	}

	public void setFilteredBarangay(boolean filteredBarangay) {
		this.filteredBarangay = filteredBarangay;
	}

	public String getSignature() {
		
		/**
		 * <p:signature style="width:250px;height:100px" value="#{customerBean.signature}" guideline="true" guidelineOffset="15" guidelineIndent="20" guidelineColor="black" />
		 */
		
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
