package model; // パッケージは適宜変更

import java.io.Serializable;

public class DrugOrderItemBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String medicineId;
    private String medicineName;
    private int quantity;
    private String unit;
    // 必要であれば、単価や合計金額などのフィールドも追加可能

    public DrugOrderItemBean() {
    }

    public DrugOrderItemBean(String medicineId, String medicineName, int quantity, String unit) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getter and Setter methods
    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}