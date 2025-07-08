package model; // パッケージは適宜合わせてください

import java.io.Serializable;

public class LabTestBean implements Serializable {
    private String jlac11Code;
    private String jlacTestName;
    private String salesName;
    private String measurementCode;
    private String measurement;
    
    
    

    public LabTestBean(String jlac11Code, String jlacTestName, String salesName, String measurementCode,
			String measurement) {
		this.jlac11Code = jlac11Code;
		this.jlacTestName = jlacTestName;
		this.salesName = salesName;
		this.measurementCode = measurementCode;
		this.measurement = measurement;
	}

	public String getJlac11Code() {
		return jlac11Code;
	}

	public String getJlacTestName() {
		return jlacTestName;
	}

	public String getSalesName() {
		return salesName;
	}

	public String getMeasurementCode() {
		return measurementCode;
	}

	public String getMeasurement() {
		return measurement;
	}

	public String getUnit() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String getReferenceValue() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	
}
