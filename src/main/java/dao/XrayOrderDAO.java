package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// DBManagerは同じパッケージにあるか、あるいは適宜importしてください

public class XrayOrderDAO {

    /**
     * 新しいレントゲン指示を登録します。
     * @param consultationId どの診察に紐づく指示かを示すID
     * @return 登録に成功した場合は true、失敗した場合は false
     */
    public boolean createXrayOrder(int consultationId, Connection con) throws SQLException {
    String sql = "INSERT INTO xray_orders (consultation_id, order_status) VALUES (?, '指示済み')";
    PreparedStatement ps = null;
    try {
        ps = con.prepareStatement(sql);
        ps.setInt(1, consultationId);
        return ps.executeUpdate() > 0;
    } finally {
        if (ps != null) ps.close();
    }
}
    
    // 今後、レントゲン技師が指示一覧を取得するためのメソッドなどをここに追加していきます。
// dao/XrayOrderDAO.java

    /**
     * 未完了のレントゲン指示を全て取得します。
     * 患者名も一緒に取得するために、テーブルを結合(JOIN)します。
     * @return 未完了のレントゲン指示リスト
     */
    public List<Map<String, Object>> getPendingXrayOrders() {
        List<Map<String, Object>> orderList = new ArrayList<>();
        // xray_orders, consultations, patients の3つのテーブルを結合
        String sql = "SELECT xo.xray_order_id, xo.ordered_at, p.patid, p.patlname, p.patfname " +
                     "FROM xray_orders xo " +
                     "JOIN consultations c ON xo.consultation_id = c.consultation_id " +
                     "JOIN patients p ON c.patient_id = p.patid " +
                     "WHERE xo.order_status = '指示済み' " +
                     "ORDER BY xo.ordered_at ASC"; // 指示が古い順

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("xray_order_id", rs.getInt("xray_order_id"));
                order.put("ordered_at", rs.getTimestamp("ordered_at"));
                order.put("patient_id", rs.getString("patid"));
                order.put("patient_name", rs.getString("patlname") + " " + rs.getString("patfname"));
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    /**
     * レントゲン画像のファイル名を登録し、指示を「撮影完了」に更新します。
     * @param xrayOrderId 対象のレントゲン指示ID
     * @param technicianId 担当したレントゲン技師のID
     * @param fileNames 登録するファイル名のリスト
     * @return 成功した場合は true
     */
    public boolean completeXrayOrder(int xrayOrderId, int technicianId, List<String> fileNames) {
        Connection con = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;
        boolean result = false;

        String sqlUpdate = "UPDATE xray_orders SET order_status = '撮影完了', technician_id = ?, completed_at = NOW() WHERE xray_order_id = ?";
        String sqlInsert = "INSERT INTO xray_images (xray_order_id, file_name) VALUES (?, ?)";

        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false); // トランザクション開始

            // 1. xray_orders テーブルを更新
            psUpdate = con.prepareStatement(sqlUpdate);
            psUpdate.setInt(1, technicianId);
            psUpdate.setInt(2, xrayOrderId);
            psUpdate.executeUpdate();

            // 2. xray_images テーブルにファイル名を登録
            psInsert = con.prepareStatement(sqlInsert);
            for (String fileName : fileNames) {
                psInsert.setInt(1, xrayOrderId);
                psInsert.setString(2, fileName);
                psInsert.addBatch();
            }
            psInsert.executeBatch();
            
            con.commit(); // コミット
            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // エラー時はロールバック
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBManager.close(con, psUpdate); // psInsertも内部で閉じられる
            DBManager.close(null, psInsert);
        }
        return result;
    }


    /**
     * 未完了のレントゲン指示を全て取得します。
     * 患者名も一緒に取得するために、テーブルを結合(JOIN)します。
     * @return 未完了のレントゲン指示リスト (各指示は情報を格納したMap)
     */
    public List<Map<String, Object>> getPendingXrayOrders() {
        List<Map<String, Object>> orderList = new ArrayList<>();
        // xray_orders, consultations, patients の3つのテーブルを結合
        String sql = "SELECT xo.xray_order_id, xo.ordered_at, p.patid, p.patlname, p.patfname " +
                     "FROM xray_orders xo " +
                     "JOIN consultations c ON xo.consultation_id = c.consultation_id " +
                     "JOIN patients p ON c.patient_id = p.patid " +
                     "WHERE xo.order_status = '指示済み' " +
                     "ORDER BY xo.ordered_at ASC"; // 指示が古い順に表示

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("xray_order_id", rs.getInt("xray_order_id"));
                order.put("ordered_at", rs.getTimestamp("ordered_at"));
                order.put("patient_id", rs.getString("patid"));
                order.put("patient_name", rs.getString("patlname") + " " + rs.getString("patfname"));
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // エラーログを出力
        }
        return orderList;
    }

    /**
     * ★追加: 特定のレントゲン指示IDに紐づく詳細情報（特に患者名）を取得します。
     * @param xrayOrderId 詳細を取得したいレントゲン指示のID
     * @return 指示ID、患者ID、患者名を含むMapオブジェクト。見つからなければnull。
     */
    public Map<String, Object> getXrayOrderDetailsById(int xrayOrderId) {
        Map<String, Object> orderDetails = null;
        String sql = "SELECT xo.xray_order_id, p.patid, p.patlname, p.patfname " +
                     "FROM xray_orders xo " +
                     "JOIN consultations c ON xo.consultation_id = c.consultation_id " +
                     "JOIN patients p ON c.patient_id = p.patid " +
                     "WHERE xo.xray_order_id = ?";
        
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, xrayOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    orderDetails = new HashMap<>();
                    orderDetails.put("xray_order_id", rs.getInt("xray_order_id"));
                    orderDetails.put("patient_id", rs.getString("patid"));
                    orderDetails.put("patient_name", rs.getString("patlname") + " " + rs.getString("patfname"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }

    /**
     * ★追加: レントゲン画像のファイル名を登録し、指示を「撮影完了」に更新します。
     * @param xrayOrderId 対象のレントゲン指示ID
     * @param technicianId 担当したレントゲン技師のID
     * @param fileNames 登録するファイル名のリスト（空でないリスト）
     * @return 成功した場合は true
     */
    public boolean completeXrayOrder(int xrayOrderId, int technicianId, List<String> fileNames) {
        Connection con = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;
        boolean result = false;

        String sqlUpdate = "UPDATE xray_orders SET order_status = '撮影完了', technician_id = ?, completed_at = NOW() WHERE xray_order_id = ?";
        String sqlInsert = "INSERT INTO xray_images (xray_order_id, file_name) VALUES (?, ?)";

        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false); // トランザクション開始

            // 1. xray_orders テーブルのステータスを更新
            psUpdate = con.prepareStatement(sqlUpdate);
            psUpdate.setInt(1, technicianId);
            psUpdate.setInt(2, xrayOrderId);
            psUpdate.executeUpdate();

            // 2. xray_images テーブルにファイル名を複数登録
            psInsert = con.prepareStatement(sqlInsert);
            for (String fileName : fileNames) {
                if (fileName != null && !fileName.trim().isEmpty()) {
                    psInsert.setInt(1, xrayOrderId);
                    psInsert.setString(2, fileName.trim());
                    psInsert.addBatch();
                }
            }
            psInsert.executeBatch();
            
            con.commit(); // 全て成功したらコミット
            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // エラー時はロールバック
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBManager.close(con, psUpdate, null);
            DBManager.close(null, psInsert, null);
        }
        return result;
    }



}
