package model;

import java.io.Serializable;
import java.util.Date;

public class TreatmentHistoryViewBean implements Serializable{
    private String patientName; // 患者姓 + 患者名
    private String medicineName;
    private int quantity;
    private String unit;
    private Date treatmentDate;
    private String doctorName; // 担当医姓 + 担当医名

    public TreatmentHistoryViewBean() {}
    
    

    public TreatmentHistoryViewBean(String patientName, String medicineName, int quantity, String unit,
			Date treatmentDate, String doctorName) {
		
		this.patientName = patientName;
		this.medicineName = medicineName;
		this.quantity = quantity;
		this.unit = unit;
		this.treatmentDate = treatmentDate;
		this.doctorName = doctorName;
	}



	// Getters and Setters
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Date getTreatmentDate() { return treatmentDate; }
    public void setTreatmentDate(Date treatmentDate) { this.treatmentDate = treatmentDate; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
}