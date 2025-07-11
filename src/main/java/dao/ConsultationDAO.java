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


    /**
     * ★追加: 診察に疾病名と新しいステータスを登録（更新）します。
     * @param consultationId 更新対象の診察ID
     * @param diseaseCode 登録する疾病コード
     * @param newStatus 新しい診察ステータス（例: "完了"）
     * @return 更新に成功した場合は true
     */
    public boolean updateDiseaseAndStatus(int consultationId, String diseaseCode, String newStatus) {
        String sql = "UPDATE consultations SET disease_code = ?, status = ? WHERE consultation_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, diseaseCode);
            ps.setString(2, newStatus);
            ps.setInt(3, consultationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ConsultationBean getConsultationById(int consultationId) {
        ConsultationBean consultation = null;
        String sql = "SELECT * FROM consultations WHERE consultation_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    consultation = new ConsultationBean();
                    // ▼▼▼ ここからが具体的なセット処理です ▼▼▼
                    consultation.setConsultationId(rs.getInt("consultation_id"));
                    consultation.setPatientId(rs.getInt("patient_id"));
                    consultation.setDoctorId(rs.getInt("doctor_id"));
                    consultation.setConsultationDate(rs.getDate("consultation_date"));
                    consultation.setDiseaseCode(rs.getString("disease_code"));
                    consultation.setStatus(rs.getString("status"));
                    // ▲▲▲ ここまで ▲▲▲
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultation;
    }

    /**
     * ★追加: 特定の患者の、確定診断済みの診察（疾病名あり）を全て取得します。
     * 疾病名も一緒に取得するためにdiseasesマスタ（の代わりのMasterDataManager）と連携します。
     * @param patientId 患者ID
     * @return 診察情報のリスト
     */
    public List<ConsultationBean> findCompletedConsultationsByPatientId(int patientId) {
        List<ConsultationBean> list = new ArrayList<>();
        // disease_codeがNULLでないものを取得
        String sql = "SELECT * FROM consultations WHERE patient_id = ? AND disease_code IS NOT NULL ORDER BY consultation_date DESC";
        
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ConsultationBean c = new ConsultationBean();
                    c.setConsultationId(rs.getInt("consultation_id"));
                    c.setPatientId(rs.getInt("patient_id"));
                    c.setDoctorId(rs.getInt("doctor_id"));
                    c.setConsultationDate(rs.getDate("consultation_date"));
                    c.setDiseaseCode(rs.getString("disease_code"));
                    // ★疾病名をMasterDataManagerから補完
                    DiseaseBean disease = MasterDataManager.findDiseaseByCode(c.getDiseaseCode());
                    if (disease != null) {
                        c.setDiseaseName(disease.getName());
                    }
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

// dao/ConsultationDAO.java

    /**
     * ★追加: 特定の患者の、確定診断済みの診察（疾病名あり）を全て取得します。
     * @param patientId 患者ID
     * @return 診察情報のリスト (ConsultationBean)
     */
    public List<ConsultationBean> findCompletedConsultationsByPatientId(int patientId) {
        List<ConsultationBean> list = new ArrayList<>();
        // disease_codeがNULLでない（＝疾病名が確定している）診察を取得
        String sql = "SELECT * FROM consultations WHERE patient_id = ? AND disease_code IS NOT NULL ORDER BY consultation_date DESC";
        
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ConsultationBean c = new ConsultationBean();
                    c.setConsultationId(rs.getInt("consultation_id"));
                    c.setPatientId(rs.getInt("patient_id"));
                    c.setDoctorId(rs.getInt("doctor_id"));
                    c.setConsultationDate(rs.getDate("consultation_date"));
                    c.setDiseaseCode(rs.getString("disease_code"));
                    
                    // MasterDataManagerから疾病名を検索してBeanにセット
                    DiseaseBean disease = MasterDataManager.findDiseaseByCode(c.getDiseaseCode());
                    if (disease != null) {
                        c.setDiseaseName(disease.getName()); // JSPで表示するためにセット
                    } else {
                        c.setDiseaseName("不明な疾病コード");
                    }
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }




    // 今後、診察情報を更新・取得するためのメソッドをここに追加していきます。
}
