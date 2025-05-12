package model;

import java.io.Serializable;

public class MedicineBean implements Serializable{
	private  String medicineid,medicinename,unit;
	
	
	public MedicineBean() {}

	public MedicineBean(String medicineid, String medicinename, String unit) {
		super();
		this.medicineid = medicineid;
		this.medicinename = medicinename;
		this.unit = unit;
	}

	public String getMedicineid() {
		return medicineid;
	}

	public void setMedicineid(String medicineid) {
		this.medicineid = medicineid;
	}

	public String getMedicinename() {
		return medicinename;
	}

	public void setMedicinename(String medicinename) {
		this.medicinename = medicinename;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	
}
