package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;

import com.italia.municipality.lakesebu.controller.FishcageBillingStatment;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.PaymentName;
import com.italia.municipality.lakesebu.controller.UserDtls;
import com.italia.municipality.lakesebu.da.controller.FishCage;
import com.italia.municipality.lakesebu.utils.Application;
import com.italia.municipality.lakesebu.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Mark Italia
 * @since 10/14/2021
 * @version 1.0
 *
 */
@Named
@ViewScoped
public class FishcageBillingBean extends ORListingBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 43543646565541L;
	
	@Setter @Getter private List<FishcageBillingStatment> statements;
	@Setter @Getter private String searchBill;
	@Setter @Getter private boolean hasbeencalled;
	@Setter @Getter private List owns;
	@Setter @Getter private Map<Long, FishCage> mapOwner;
	@Setter @Getter private List<PaymentName> paymentSelected;
	@Setter @Getter private List<PaymentName> payments;
	@Setter @Getter private Map<Long, PaymentName> mapPaymentData;
	
	
	public void openParticulars() {
		loadParticulars();
		PrimeFaces pf = PrimeFaces.current();
		pf.executeScript("PF('dlgfBillPay').show(1000)");
	}
	
	public void loadParticulars() {
		payments = new ArrayList<PaymentName>();
		String sql = "";
		if(getSearchBill()!=null && !getSearchBill().isEmpty()) {
			sql = " AND pyname like '%"+ getSearchBill() +"%'";
		}
		payments.addAll(PaymentName.retrieve(sql +" ORDER BY pyname", new String[0]));
	}
	
	public void openBilling() {	
		loadData();
		PrimeFaces pf = PrimeFaces.current();
		pf.executeScript("PF('dlgfishbill').show(1000)");
	}
	
	public void loadData() {
		statements = new ArrayList<FishcageBillingStatment>();
		
		String sql = "";
		
		if(getSearchBill()!=null && !getSearchBill().isEmpty()) {
			sql = " AND (st.fbdatebill like '%"+ getSearchBill()+"%'";
			sql += " OR st.fbcontrolno like '%"+ getSearchBill() +"%'";
			sql += " OR ow.owner like '%"+ getSearchBill() +"%'";
			sql += " OR ow.watersurveyno like '%"+ getSearchBill() +"%'";
			sql += " OR ow.arealocation like '%"+ getSearchBill() +"%')";
		}
		
		if(!hasbeencalled) {//called once only
			getOwners();
			hasbeencalled = true;
		}
		
		FishcageBillingStatment st = FishcageBillingStatment.builder()
				.dateTrans(DateUtils.getCurrentDateYYYYMMDD())
				.date(DateUtils.getDateToday())
				.controlNo(FishcageBillingStatment.getControlNewNo())
				.remarks("Add Remarks")
				.isActive(1)
				.owner(FishCage.builder().ownerName("Select Owner").build())
				.ownerListId(0)
				.owners(getOwns())
				.userDtls(getUser())
				.build();
		statements.add(st);
		for(FishcageBillingStatment fs :  FishcageBillingStatment.retrieve(sql, new String[0])) {
			fs.setOwnerListId(fs.getOwner().getId());
			fs.setOwners(getOwns());
			statements.add(fs);
		}
		
	}
	
	private void getOwners(){
		List own = new ArrayList<>();
		mapOwner = new LinkedHashMap<Long, FishCage>();
		own.add(new SelectItem(0, "Select Name"));
		for(FishCage cage : FishCage.retrieve(" ORDER BY owner", new String[0])) {
			own.add(new SelectItem(cage.getId(), cage.getOwnerName()));
			mapOwner.put(cage.getId(), cage);
		}
		setOwns(own);
	}
	
	private UserDtls getUser() {
		return Login.getUserLogin().getUserDtls();
	}
	
	public void onCellEdit(CellEditEvent event) {
		   Object oldValue = event.getOldValue();
		   Object newValue = event.getNewValue();
		   int index = event.getRowIndex();
		   String column =  event.getColumn().getHeaderText();
		   
		   
		   if("Date".equalsIgnoreCase(column)) {
			   getStatements().get(index).setDateTrans(DateUtils.convertDate((Date)newValue, "yyyy-MM-dd"));
		   }else if("Fish Owner".equalsIgnoreCase(column)) {
			  long id = (Long)event.getNewValue();
			   getStatements().get(index).setOwner(getMapOwner().get(id));
		   }
		   
	}	   
	
	public void onCellEditPay(CellEditEvent event) {
		Object oldValue = event.getOldValue();
		Object newValue = event.getNewValue();
		int index = event.getRowIndex();
		if(getPaymentSelected()==null) {
			paymentSelected = new ArrayList<PaymentName>();
			paymentSelected.add(getPayments().get(index));
		}else {
			paymentSelected.add(getPayments().get(index));
		}
		
	}
	
	public void saveBill(FishcageBillingStatment fs) {
		if(fs.getOwner().getId()!=0 && getPaymentSelected()!=null && getPaymentSelected().size()>0) {
			
			int count = 1;
			String val = "";
			for(PaymentName py : getPaymentSelected()) {
				if(count==1) {
					val = py.getId() + ":" + py.getAmount();
				}else {
					val += "<@>"+ py.getId() + ":" + py.getAmount();
				}
				count++;
			}
			
			String rem = "Add Remarks".equalsIgnoreCase(fs.getRemarks())? "" : fs.getRemarks();
			fs.setParticulars(val);
			fs.setRemarks(rem);
			fs.save();
			loadData();
			paymentSelected = new ArrayList<PaymentName>();
			Application.addMessage(1, "Success", "Data was successfully saved.");
		}else {
			Application.addMessage(2, "Error", "Saving was not successfully please check your data.");
		}
	}
	
}
