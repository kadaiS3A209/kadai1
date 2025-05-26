package dao; // パッケージは適宜変更


//DBManagerのパッケージに合わせて修正してください

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.PatientBean; // パスを合わせる
//import java.util.Date; // PatientBeanでDate型を使う場合

public class PatientDAO {
 // DB接続情報はDBManagerに集約されるため、ここでは不要

 /**
  * 指定された患者IDが既に存在するかを確認します。
  * @param patId 確認する患者ID
  * @return 存在する場合は true、存在しない場合は false
  */
 public boolean isPatientIdExists(String patId) {
     String sql = "SELECT 1 FROM patient WHERE patid = ? LIMIT 1";
     Connection con = null;
     PreparedStatement ps = null;
     ResultSet rs = null;
     boolean exists = false;
     try {
         con = DBManager.getConnection(); // DBManagerからConnectionを取得
         ps = con.prepareStatement(sql);
         ps.setString(1, patId);
         rs = ps.executeQuery();
         if (rs.next()) {
             exists = true;
         }
     } catch (SQLException e) {
         e.printStackTrace(); // 適切なエラーハンドリング
     } finally {
         DBManager.close(con, ps, rs); // DBManagerでリソースを解放
     }
     return exists;
 }

 /**
  * 新しい患者情報をデータベースに登録します。
  * @param patient 登録する患者情報を持つPatientBean
  * @return 登録に成功した場合は true、失敗した場合は false
  */
 public boolean registerPatient(PatientBean patient) {
     String sql = "INSERT INTO patient (patid, patfname, patlname, hokenmei, hokenexp) VALUES (?, ?, ?, ?, ?)";
     Connection con = null;
     PreparedStatement ps = null;
     boolean success = false;
     try {
         con = DBManager.getConnection(); // DBManagerからConnectionを取得
         ps = con.prepareStatement(sql);

         ps.setString(1, patient.getPatId());
         ps.setString(2, patient.getPatFname());
         ps.setString(3, patient.getPatLname());
         ps.setString(4, patient.getHokenmei());

         if (patient.getHokenexp() != null) {
             ps.setDate(5, new java.sql.Date(patient.getHokenexp().getTime()));
         } else {
             ps.setNull(5, java.sql.Types.DATE);
         }

         int result = ps.executeUpdate();
         success = (result > 0);

     } catch (SQLException e) {
         e.printStackTrace(); // 適切なエラーハンドリング
     } finally {
         DBManager.close(con, ps); // DBManagerでリソースを解放 (ResultSetなし)
     }
     return success;
 }
 // 他のPatientDAOメソッドも同様に修正
}