package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// DBManagerは同じパッケージにあるか、あるいは適宜importしてください

public class ConsultationDAO {

    /**
     * 新しい診察情報を登録し、自動採番された診察IDを返します。
     * @param patientId 診察を受ける患者のID
     * @param doctorId 診察を行う医師のID
     * @return 成功した場合は、新しく作成された診察ID (consultation_id)。失敗した場合は -1。
     */
    public int createConsultation(int patientId, int doctorId) {
        String sql = "INSERT INTO consultations (patient_id, doctor_id, consultation_date, status) VALUES (?, ?, CURDATE(), '指示待ち')";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int newConsultationId = -1;

        try {
            con = DBManager.getConnection();
            // 第2引数に Statement.RETURN_GENERATED_KEYS を指定することで、自動採番されたIDを取得できる
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);

            int result = ps.executeUpdate();

            if (result > 0) {
                // INSERT成功後、生成されたキー(ID)を取得
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    newConsultationId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // エラーログを出力
        } finally {
            DBManager.close(con, ps, rs);
        }
        
        return newConsultationId;
    }

    // 今後、診察情報を更新・取得するためのメソッドをここに追加していきます。
}
