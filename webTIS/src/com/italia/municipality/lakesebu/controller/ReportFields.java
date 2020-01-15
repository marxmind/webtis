package com.italia.municipality.lakesebu.controller;

public class ReportFields {

	private int id;
	private String accntNumber;
	private String checkNo;
	private String date_disbursement;
	private String bankName;
	private String accntName;
	private String amount;
	private String payToTheOrderOf;
	private String amountInWOrds;
	private String signatory1;
	private String signatory2;
	private String processBy;
	private String date_edited;
	private String date_created;
	private String status;
	private String remarks;
	
	
	public ReportFields(){}
	
	public ReportFields(
			String accntNumber,
			String checkNo
			){
		this.accntNumber = accntNumber;
		this.checkNo = checkNo;
	}
	
	public ReportFields(
			String accntNumber,
			String checkNo,
			String date_disbursement,
			String bankName,
			String accntName,
			String amount,
			String payToTheOrderOf,
			String amountInWOrds,
			String signatory1,
			String signatory2,
			String processBy,
			String date_edited,
			String date_created,
			String status,
			String remarks
			){
		
		this.accntNumber = accntNumber;
		this.checkNo = checkNo;
		this.date_disbursement = date_disbursement;
		this.bankName = bankName;
		this.accntName = accntName;
		this.amount = amount;
		this.payToTheOrderOf = payToTheOrderOf;
		this.amountInWOrds = amountInWOrds;
		this.signatory1 = signatory1;
		this.signatory2 = signatory2;
		this.processBy = processBy;
		this.date_created = date_created;
		this.date_edited = date_edited;
		this.status = status;
		this.remarks = remarks;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAccntNumber() {
		return accntNumber;
	}
	public void setAccntNumber(String accntNumber) {
		this.accntNumber = accntNumber;
	}
	public String getCheckNo() {
		return checkNo;
	}
	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}
	public String getDate_disbursement() {
		return date_disbursement;
	}
	public void setDate_disbursement(String date_disbursement) {
		this.date_disbursement = date_disbursement;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAccntName() {
		return accntName;
	}
	public void setAccntName(String accntName) {
		this.accntName = accntName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPayToTheOrderOf() {
		return payToTheOrderOf;
	}
	public void setPayToTheOrderOf(String payToTheOrderOf) {
		this.payToTheOrderOf = payToTheOrderOf;
	}
	public String getAmountInWOrds() {
		return amountInWOrds;
	}
	public void setAmountInWOrds(String amountInWOrds) {
		this.amountInWOrds = amountInWOrds;
	}
	public String getSignatory1() {
		return signatory1;
	}
	public void setSignatory1(String signatory1) {
		this.signatory1 = signatory1;
	}
	public String getSignatory2() {
		return signatory2;
	}
	public void setSignatory2(String signatory2) {
		this.signatory2 = signatory2;
	}
	public String getProcessBy() {
		return processBy;
	}
	public void setProcessBy(String processBy) {
		this.processBy = processBy;
	}
	public String getDate_edited() {
		return date_edited;
	}
	public void setDate_edited(String date_edited) {
		this.date_edited = date_edited;
	}
	public String getDate_created() {
		return date_created;
	}
	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
}
