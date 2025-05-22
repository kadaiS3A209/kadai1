package model;

import java.io.Serializable;



public class ShiiregyoshaBean implements Serializable{
 private String shiireId;      // 仕入先ID (VARCHAR(8))
 private String shiireMei;     // 仕入先名 (VARCHAR(64))
 private String shiireAddress; // 仕入先住所 (VARCHAR(64))
 private String shiireTel;     // 仕入先電話番号 (VARCHAR(13))
 private int shihonkin;        // 資本金 (INT)
 private int nouki;            // 納期 (INT)

 // コンストラクタ (デフォルトと全フィールド)
 public ShiiregyoshaBean() {
 }

 public ShiiregyoshaBean(String shiireId, String shiireMei, String shiireAddress, String shiireTel, int shihonkin, int nouki) {
     this.shiireId = shiireId;
     this.shiireMei = shiireMei;
     this.shiireAddress = shiireAddress;
     this.shiireTel = shiireTel;
     this.shihonkin = shihonkin;
     this.nouki = nouki;
 }

 // Getter and Setter methods
 public String getShiireId() {
     return shiireId;
 }

 public void setShiireId(String shiireId) {
     this.shiireId = shiireId;
 }

 public String getShiireMei() {
     return shiireMei;
 }

 public void setShiireMei(String shiireMei) {
     this.shiireMei = shiireMei;
 }

 public String getShiireAddress() {
     return shiireAddress;
 }

 public void setShiireAddress(String shiireAddress) {
     this.shiireAddress = shiireAddress;
 }

 public String getShiireTel() {
     return shiireTel;
 }

 public void setShiireTel(String shiireTel) {
     this.shiireTel = shiireTel;
 }

 public int getShihonkin() {
     return shihonkin;
 }

 public void setShihonkin(int shihonkin) {
     this.shihonkin = shihonkin;
 }

 public int getNouki() {
     return nouki;
 }

 public void setNouki(int nouki) {
     this.nouki = nouki;
 }

 // (任意で toString() メソッドなど)
}
