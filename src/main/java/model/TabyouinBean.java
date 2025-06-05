package model; // パッケージ名は適宜変更してください

import java.io.Serializable;

public class TabyouinBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tabyouinId;      // 他病院ID (tabyouinid VARCHAR(8))
    private String tabyouinMei;     // 他病院名 (tabyouinmei VARCHAR(64))
    private String tabyouinAddrss;  // 他病院住所 (tabyouinaddrss VARCHAR(64))
    private String tabyouinTel;     // 他病院電話番号 (tabyouintel VARCHAR(13))
    private int tabyouinShihonkin;  // 資本金 (tabyouinshihonkin INT)
    private int kyukyu;             // 救急対応 (kyukyu INT) (救急なら1、そうでなければ0など)

    public TabyouinBean() {
    }

    // Getters and Setters
    public String getTabyouinId() {
        return tabyouinId;
    }

    public void setTabyouinId(String tabyouinId) {
        this.tabyouinId = tabyouinId;
    }

    public String getTabyouinMei() {
        return tabyouinMei;
    }

    public void setTabyouinMei(String tabyouinMei) {
        this.tabyouinMei = tabyouinMei;
    }

    public String getTabyouinAddrss() {
        return tabyouinAddrss;
    }

    public void setTabyouinAddrss(String tabyouinAddrss) {
        this.tabyouinAddrss = tabyouinAddrss;
    }

    public String getTabyouinTel() {
        return tabyouinTel;
    }

    public void setTabyouinTel(String tabyouinTel) {
        this.tabyouinTel = tabyouinTel;
    }

    public int getTabyouinShihonkin() {
        return tabyouinShihonkin;
    }

    public void setTabyouinShihonkin(int tabyouinShihonkin) {
        this.tabyouinShihonkin = tabyouinShihonkin;
    }

    public int getKyukyu() {
        return kyukyu;
    }

    public void setKyukyu(int kyukyu) {
        this.kyukyu = kyukyu;
    }
}
