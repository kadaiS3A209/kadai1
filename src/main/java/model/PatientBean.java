package model;

import java.io.Serializable;
import java.util.Date;

public class PatientBean implements Serializable{
	private String patid,patfname,patlname,hokexnmei;
	private Date hokexnexp;
	public PatientBean(String patid, String patfname, String patlname, String hokexnmei, Date hokexnexp) {
		super();
		this.patid = patid;
		this.patfname = patfname;
		this.patlname = patlname;
		this.hokexnmei = hokexnmei;
		this.hokexnexp = hokexnexp;
	}
	public String getPatid() {
		return patid;
	}
	public void setPatid(String patid) {
		this.patid = patid;
	}
	public String getPatfname() {
		return patfname;
	}
	public void setPatfname(String patfname) {
		this.patfname = patfname;
	}
	public String getPatlname() {
		return patlname;
	}
	public void setPatlname(String patlname) {
		this.patlname = patlname;
	}
	public String getHokexnmei() {
		return hokexnmei;
	}
	public void setHokexnmei(String hokexnmei) {
		this.hokexnmei = hokexnmei;
	}
	public Date getHokexnexp() {
		return hokexnexp;
	}
	public void setHokexnexp(Date hokexnexp) {
		this.hokexnexp = hokexnexp;
	}

}
