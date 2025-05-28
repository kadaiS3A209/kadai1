package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.TreatmentBean;
import model.TreatmentHistoryViewBean;

public class TreatmentDAO {

    /**
     * 新しい処置IDを生成します (例: "TR" + 6桁連番)。
     * 注意: この方法は同時アクセスが多い環境では重複のリスクがあります。
     * より堅牢な採番方法はDBのシーケンスやUUID、採番専用テーブルなどを検討してください。
     * @return 生成された新しい処置ID
     */
    public synchronized String getNextTreatmentId() { // 簡単な同期処理
        String prefix = "TR";
        String lastId = null;
        int nextSeq = 1;
        // 現在の最大のtreatmentidを取得 (TRで始まるもの)
        String sqlMax = "SELECT MAX(treatmentid) FROM treatment WHERE treatmentid LIKE ?";
        Connection con = null;
        PreparedStatement psMax = null;
        ResultSet rsMax = null;

        try {
            con = DBManager.getConnection();
            psMax = con.prepareStatement(sqlMax);
            psMax.setString(1, prefix + "%");
            rsMax = psMax.executeQuery();
            if (rsMax.next()) {
                lastId = rsMax.getString(1);
            }

            if (lastId != null && lastId.startsWith(prefix) && lastId.length() == 8) {
                try {
                    nextSeq = Integer.parseInt(lastId.substring(prefix.length())) + 1;
                } catch (NumberFormatException e) {
                    // 既存IDの形式が不正な場合、ログを残してデフォルトの1から開始
                    System.err.println("Error parsing treatment ID sequence: " + lastId);
                    nextSeq = 1; // フォールバック
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // エラーログ
            // エラー発生時は採番に失敗したとしてnullを返すか、例外をスロー
            return prefix + String.format("%06d", (int)(Math.random() * 1000000)); // 簡易的なエラー回避 (非推奨)
        } finally {
            DBManager.close(null, psMax, rsMax); // rsMaxのみ閉じる (conは次の処理で使う場合があるため、メソッド全体で管理)
                                                  // ただし、このメソッド内で完結させるならconもここで閉じる
            if (con != null && psMax != null) { // PreparedStatementとResultSetは閉じたので、コネクションは別途
                 DBManager.close(con, null, null); // コネクションのみ閉じる
            }
        }
        return prefix + String.format("%06d", nextSeq);
    }


    /**
     * 1件の処置情報をtreatmentテーブルに登録します。
     * @param treatment 登録する処置情報を持つTreatmentBean
     * @return 登録に成功した場合は true、失敗した場合は false
     */
    public boolean registerTreatment(TreatmentBean treatment) {
        String sql = "INSERT INTO treatment (treatmentid, patientid, medicineid, quantity, empid, treatmentdate) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);

            ps.setString(1, treatment.getTreatmentId());
            ps.setString(2, treatment.getPatientId());
            ps.setString(3, treatment.getMedicineId());
            ps.setInt(4, treatment.getQuantity());
            ps.setString(5, treatment.getEmpId());
            if (treatment.getTreatmentDate() != null) {
                ps.setDate(6, new java.sql.Date(treatment.getTreatmentDate().getTime()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }

            int result = ps.executeUpdate();
            success = (result > 0);

        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリング
        } finally {
            DBManager.close(con, ps);
        }
        return success;
    }

    // 処置履歴確認機能 (D104) で使用するメソッドもここに追加していく
    
    public List<TreatmentHistoryViewBean> getTreatmentHistoryByPatientId(String patId) {
        List<TreatmentHistoryViewBean> historyList = new ArrayList<>();
        // SQLクエリ: treatmentテーブルを主軸に、patient, medicine, employeeテーブルをJOIN
        String sql = "SELECT " +
                     "p.patfname, p.patlname, " +       // 患者名 (姓、名)
                     "m.medicinename, m.unit, " +       // 薬剤名, 単位
                     "t.quantity, t.treatmentdate, " +  // 数量, 処置日
                     "e.empfname AS doctor_fname, e.emplname AS doctor_lname " + // 担当医名 (姓、名)
                     "FROM treatment t " +
                     "JOIN patient p ON t.patientid = p.patid " +
                     "JOIN medicine m ON t.medicineid = m.medicineid " +
                     "JOIN employee e ON t.empid = e.empid " +
                     "WHERE t.patientid = ? " +
                     "ORDER BY t.treatmentdate DESC, t.treatmentid DESC"; // 処置日の降順、次にIDの降順

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, patId);
            rs = ps.executeQuery();

            while (rs.next()) {
                TreatmentHistoryViewBean item = new TreatmentHistoryViewBean();
                item.setPatientName(rs.getString("patlname") + " " + rs.getString("patfname"));
                item.setMedicineName(rs.getString("medicinename"));
                item.setUnit(rs.getString("unit"));
                item.setQuantity(rs.getInt("quantity"));
                item.setTreatmentDate(rs.getDate("treatmentdate"));
                item.setDoctorName(rs.getString("doctor_lname") + " " + rs.getString("doctor_fname"));
                historyList.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 適切なエラーハンドリング
        } finally {
            DBManager.close(con, ps, rs);
        }
        return historyList;
    }
}