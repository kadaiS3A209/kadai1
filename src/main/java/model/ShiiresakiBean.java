package model;

import java.io.Serializable;

public class ShiiresakiBean implements Serializable{
	private String shiireid,shiiremei,shiireaddress,shiiretel;
	private int shihoxnkixn,nouki;
	public ShiiresakiBean(String shiireid, String shiiremei, String shiireaddress, String shiiretel, int shihoxnkixn,
			int nouki) {
		super();
		this.shiireid = shiireid;
		this.shiiremei = shiiremei;
		this.shiireaddress = shiireaddress;
		this.shiiretel = shiiretel;
		this.shihoxnkixn = shihoxnkixn;
		this.nouki = nouki;
	}
	public String getShiireid() {
		return shiireid;
	}
	public void setShiireid(String shiireid) {
		this.shiireid = shiireid;
	}
	public String getShiiremei() {
		return shiiremei;
	}
	public void setShiiremei(String shiiremei) {
		this.shiiremei = shiiremei;
	}
	public String getShiireaddress() {
		return shiireaddress;
	}
	public void setShiireaddress(String shiireaddress) {
		this.shiireaddress = shiireaddress;
	}
	public String getShiiretel() {
		return shiiretel;
	}
	public void setShiiretel(String shiiretel) {
		this.shiiretel = shiiretel;
	}
	public int getShihoxnkixn() {
		return shihoxnkixn;
	}
	public void setShihoxnkixn(int shihoxnkixn) {
		this.shihoxnkixn = shihoxnkixn;
	}
	public int getNouki() {
		return nouki;
	}
	public void setNouki(int nouki) {
		this.nouki = nouki;
	}

}
