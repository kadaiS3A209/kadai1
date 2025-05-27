package model;

import java.io.Serializable;
import java.util.Date;

public class PatientBean implements Serializable{
	
    private String patId;       // 患者ID (VARCHAR(8))
    private String patFname;    // 患者名 (VARCHAR(64))
    private String patLname;    // 患者姓 (VARCHAR(64))
    private String hokenmei;    // 保険証名記号番号 (VARCHAR(64))
    private Date hokenexp;      // 有効期限 (DATE)
    // または String hokenexpStr; として文字列で扱い、DAOでDateに変換

    // デフォルトコンストラクタ
    public PatientBean() {
    }

    // 全フィールドコンストラクタ (任意)
    public PatientBean(String patId, String patFname, String patLname, String hokenmei, Date hokenexp) {
        this.patId = patId;
        this.patFname = patFname;
        this.patLname = patLname;
        this.hokenmei = hokenmei;
        this.hokenexp = hokenexp;
    }

    // Getter and Setter
    public String getPatId() {
        return patId;
    }

    public void setPatId(String patId) {
        this.patId = patId;
    }

    public String getPatFname() {
        return patFname;
    }

    public void setPatFname(String patFname) {
        this.patFname = patFname;
    }

    public String getPatLname() {
        return patLname;
    }

    public void setPatLname(String patLname) {
        this.patLname = patLname;
    }

    public String getHokenmei() {
        return hokenmei;
    }

    public void setHokenmei(String hokenmei) {
        this.hokenmei = hokenmei;
    }

    public Date getHokenexp() {
        return hokenexp;
    }

    public void setHokenexp(Date hokenexp) {
        this.hokenexp = hokenexp;
    }

    // 有効期限を文字列で扱う場合のGetter/Setter (例: yyyy-MM-dd)
    // private String hokenexpStr;
    // public String getHokenexpStr() { return hokenexpStr; }
    // public void setHokenexpStr(String hokenexpStr) { this.hokenexpStr = hokenexpStr; }
}

