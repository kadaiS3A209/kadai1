package dao; // パッケージは適宜変更


//DBManagerのパッケージに合わせて修正してください

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 
 
 /**
  * 患者情報を検索条件に基づいて取得します。
  * @param searchName 検索する患者名（姓または名、部分一致）。nullまたは空の場合は名前で絞り込まない。
  * @param listExpiredOnly 有効期限切れの患者のみをリストアップする場合はtrue。
  * @return 条件に一致する患者情報のリスト。
  */
 public List<PatientBean> getPatients(String searchPatId, String searchName, Boolean listExpiredOnly) {
     List<PatientBean> patientList = new ArrayList<>();
     StringBuilder sql = new StringBuilder("SELECT patid, patfname, patlname, hokenmei, hokenexp FROM patient WHERE 1=1");
     List<Object> params = new ArrayList<>();

     if (searchPatId != null && !searchPatId.trim().isEmpty()) {
         sql.append(" AND patid LIKE ?");
         params.add("%" + searchPatId.trim() + "%");
     }

     if (searchName != null && !searchName.trim().isEmpty()) {
         sql.append(" AND (patfname LIKE ? OR patlname LIKE ?)");
         String searchTerm = "%" + searchName.trim() + "%";
         params.add(searchTerm);
         params.add(searchTerm);
     }

     if (listExpiredOnly != null && listExpiredOnly) {
         sql.append(" AND hokenexp < CURDATE()");
     }

     sql.append(" ORDER BY patid ASC"); // ID順で表示

     Connection con = null;
     PreparedStatement ps = null;
     ResultSet rs = null;

     try {
         con = DBManager.getConnection();
         ps = con.prepareStatement(sql.toString());

         for (int i = 0; i < params.size(); i++) {
             ps.setObject(i + 1, params.get(i));
         }

         rs = ps.executeQuery();
         while (rs.next()) {
             PatientBean patient = new PatientBean();
             patient.setPatId(rs.getString("patid"));
             patient.setPatFname(rs.getString("patfname"));
             patient.setPatLname(rs.getString("patlname"));
             patient.setHokenmei(rs.getString("hokenmei"));
             patient.setHokenexp(rs.getDate("hokenexp"));
             patientList.add(patient);
         }
     } catch (SQLException e) {
         e.printStackTrace(); // 適切なエラーハンドリング
     } finally {
         DBManager.close(con, ps, rs);
     }
     return patientList;
 }

 /**
  * 指定された患者IDに対応する患者情報を取得します。
  * @param patId 検索する患者ID
  * @return 患者情報を持つPatientBean、見つからなければnull。
  */
 public PatientBean getPatientById(String patId) {
     PatientBean patient = null;
     String sql = "SELECT patid, patfname, patlname, hokenmei, hokenexp FROM patient WHERE patid = ?";
     Connection con = null;
     PreparedStatement ps = null;
     ResultSet rs = null;

     try {
         con = DBManager.getConnection();
         ps = con.prepareStatement(sql);
         ps.setString(1, patId);
         rs = ps.executeQuery();

         if (rs.next()) {
             patient = new PatientBean();
             patient.setPatId(rs.getString("patid"));
             patient.setPatFname(rs.getString("patfname"));
             patient.setPatLname(rs.getString("patlname"));
             patient.setHokenmei(rs.getString("hokenmei"));
             patient.setHokenexp(rs.getDate("hokenexp"));
         }
     } catch (SQLException e) {
         e.printStackTrace();
     } finally {
         DBManager.close(con, ps, rs);
     }
     return patient;
 }

 /**
  * 患者の保険証情報（記号番号および/または有効期限）を更新します。
  * @param patId 対象の患者ID
  * @param newHokenmei 新しい保険証記号番号。変更しない場合は現在の値を渡すか、nullを考慮する設計に。
  * @param finalHokenexp 新しい有効期限。変更しない場合は現在の値を渡すか、nullを考慮する設計に。
  * @return 更新に成功した場合はtrue、失敗した場合はfalse。
  */
 public boolean updatePatientInsurance(String patId, String newHokenmei, Date newHokenexp) {
	    if (patId == null || patId.trim().isEmpty()) return false;
	    // 変更するフィールドがあるかどうかのチェック
	    boolean hokenmeiToUpdate = (newHokenmei != null && !newHokenmei.trim().isEmpty()); // 空文字は変更なしとみなすか、要件次第
	    boolean hokenexpToUpdate = (newHokenexp != null);

	    if (!hokenmeiToUpdate && !hokenexpToUpdate) {
	        return false; // 更新するものが何もない
	    }

	    StringBuilder sql = new StringBuilder("UPDATE patient SET ");
	    List<Object> params = new ArrayList<>();
	    boolean firstField = true;

	    if (hokenmeiToUpdate) {
	        sql.append("hokenmei = ?");
	        params.add(newHokenmei.trim());
	        firstField = false;
	    }

	    if (hokenexpToUpdate) {
	        if (!firstField) {
	            sql.append(", ");
	        }
	        sql.append("hokenexp = ?");
	        params.add(new java.sql.Date(newHokenexp.getTime()));
	    }

	    sql.append(" WHERE patid = ?");
	    params.add(patId);

	    Connection con = null;
	    PreparedStatement ps = null;
	    boolean success = false;
	    try {
	        con = DBManager.getConnection();
	        ps = con.prepareStatement(sql.toString());

	        for (int i = 0; i < params.size(); i++) {
	            ps.setObject(i + 1, params.get(i));
	        }
	        int result = ps.executeUpdate();
	        success = (result > 0);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        DBManager.close(con, ps);
	    }
	    return success;
	}
 
//PatientDAO.java
 public List<PatientBean> getAllPatients() {
     return getPatients(null, null, null); // ID検索なし、名前検索なし、期限切れフィルタなし
 }
 
 
 
}