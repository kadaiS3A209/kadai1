package model; // パッケージは適宜変更

import java.io.Serializable;

public class MedicineBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String medicineId;    // 薬剤ID (medicineid)
    private String medicineName;  // 薬剤名 (medicinename)
    private String unit;          // 単位 (unit)

    // デフォルトコンストラクタ
    public MedicineBean() {
    }

    // 全フィールドコンストラクタ (任意)
    public MedicineBean(String medicineId, String medicineName, String unit) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // (任意で toString() メソッドなど)
    @Override
    public String toString() {
        return "MedicineBean{" +
               "medicineId='" + medicineId + '\'' +
               ", medicineName='" + medicineName + '\'' +
               ", unit='" + unit + '\'' +
               '}';
    }

}
