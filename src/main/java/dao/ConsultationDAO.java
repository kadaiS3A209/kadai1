package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.ConsultationBean; // ★新しく作成するBeanをインポート

// DBManagerは同じパッケージにあるか、あるいは適宜importしてください

public class ConsultationDAO {

    /**
     * 新しい診察情報を登録し、自動採番された診察IDを返します。
     * @param patientId 診察を受ける患者のID
     * @param doctorId 診察を行う医師のID
     * @return 成功した場合は、新しく作成された診察ID (consultation_id)。失敗した場合は -1。
     */
   public int createConsultation(int patientId, int doctorId, Connection con) throws SQLException {
    String sql = "INSERT INTO consultations (patient_id, doctor_id, consultation_date, status) VALUES (?, ?, CURDATE(), '指示待ち')";
    PreparedStatement ps = null;
    ResultSet rs = null;
    int newConsultationId = -1;

    try {
        // ★ DBManagerから直接取得するのではなく、渡されたconオブジェクトを使う
        ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, patientId);
        ps.setInt(2, doctorId);
        int result = ps.executeUpdate();
        if (result > 0) {
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                newConsultationId = rs.getInt(1);
            }
        }
    } finally {
        // ★ ここではConnectionを閉じない！サーブレット側で一括して閉じる
        if (rs != null) rs.close();
        if (ps != null) ps.close();
    }
    return newConsultationId;
}

    /**
     * ★追加: 指定された患者IDに紐づく、未完了の診察（疾病名がNULL）を探します。
     * @param patientId 患者ID
     * @return 未完了の診察情報を持つConsultationBean。見つからない場合はnull。
     */
    public ConsultationBean findIncompleteConsultationByPatientId(int patientId) {
        ConsultationBean consultation = null;
        // 疾病名がまだ登録されておらず、最も新しい診察レコードを1件取得する
        String sql = "SELECT * FROM consultations WHERE patient_id = ? AND disease_code IS NULL ORDER BY consultation_date DESC, consultation_id DESC LIMIT 1";
        
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    consultation = new ConsultationBean();
                    consultation.setConsultationId(rs.getInt("consultation_id"));
                    consultation.setPatientId(rs.getInt("patient_id"));
                    consultation.setDoctorId(rs.getInt("doctor_id"));
                    consultation.setConsultationDate(rs.getDate("consultation_date"));
                    consultation.setDiseaseCode(rs.getString("disease_code"));
                    consultation.setStatus(rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultation;
    }

    // 今後、診察情報を更新・取得するためのメソッドをここに追加していきます。
}
