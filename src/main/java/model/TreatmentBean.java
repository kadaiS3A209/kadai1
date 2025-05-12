package model;

import java.io.Serializable;
import java.util.Date;

public class TreatmentBean implements Serializable{
	private String treatmentid,patientid,medicineid;
	private int quantity;
	private String empid;
	private Date treatmentdate;
	public TreatmentBean(String treatmentid, String patientid, String medicineid, int quantity, String empid,
			Date treatmentdate) {
		super();
		this.treatmentid = treatmentid;
		this.patientid = patientid;
		this.medicineid = medicineid;
		this.quantity = quantity;
		this.empid = empid;
		this.treatmentdate = treatmentdate;
	}
	public String getTreatmentid() {
		return treatmentid;
	}
	public void setTreatmentid(String treatmentid) {
		this.treatmentid = treatmentid;
	}
	public String getPatientid() {
		return patientid;
	}
	public void setPatientid(String patientid) {
		this.patientid = patientid;
	}
	public String getMedicineid() {
		return medicineid;
	}
	public void setMedicineid(String medicineid) {
		this.medicineid = medicineid;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getEmpid() {
		return empid;
	}
	public void setEmpid(String empid) {
		this.empid = empid;
	}
	public Date getTreatmentdate() {
		return treatmentdate;
	}
	public void setTreatmentdate(Date treatmentdate) {
		this.treatmentdate = treatmentdate;
	}
	
	
}
