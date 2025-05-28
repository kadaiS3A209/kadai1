package model; // パッケージは適宜変更

import java.io.Serializable;
import java.util.Date;

public class TreatmentBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String treatmentId;
    private String patientId;
    private String medicineId;
    private int quantity;
    private String empId; // 処置を行った医師のID
    private Date treatmentDate;

    public TreatmentBean() {
    }

    // Getter and Setter methods
    public String getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public Date getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(Date treatmentDate) {
        this.treatmentDate = treatmentDate;
    }
}
