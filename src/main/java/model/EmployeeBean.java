package model;

import java.io.Serializable;

public class EmployeeBean implements Serializable{
	private String empid;
	private String empfname;
	private String emplname;
	private String emppasswd;
	private String salt;
	private int role;
	
	public EmployeeBean(String empid, String empfname, String emplname, String emppasswd, String salt, int role) {
		super();
		this.empid = empid;
		this.empfname = empfname;
		this.emplname = emplname;
		this.emppasswd = emppasswd;
		this.salt = salt;
		this.role = role;
	}

	public EmployeeBean() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getEmpfname() {
		return empfname;
	}

	public void setEmpfname(String empfname) {
		this.empfname = empfname;
	}

	public String getEmplname() {
		return emplname;
	}

	public void setEmplname(String emplname) {
		this.emplname = emplname;
	}

	public String getEmppasswd() {
		return emppasswd;
	}

	public void setEmppasswd(String emppasswd) {
		this.emppasswd = emppasswd;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
	
	
	
	
	
	
	
}