package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.LabTestItemBean; // 新しくBeanを作成します

// DBManagerは同じパッケージにあるか、あるいは適宜importしてください

public class LabTestOrderDAO {

    /**
     * 親となる新しい臨床検査指示を登録し、自動採番されたIDを返します。
     * @param consultationId どの診察に紐づく指示かを示すID
     * @return 成功した場合は、新しく作成された検査指示ID (lab_test_order_id)。失敗した場合は -1。
     */
    public int createLabTestOrder(int consultationId) {
        String sql = "INSERT INTO lab_test_orders (consultation_id, order_status) VALUES (?, '指示済み')";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int newLabTestOrderId = -1;

        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, consultationId);

            int result = ps.executeUpdate();
            if (result > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    newLabTestOrderId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.close(con, ps, rs);
        }
        return newLabTestOrderId;
    }

    /**
     * 複数の臨床検査項目をまとめて登録します。
     * @param labTestOrderId 親となる検査指示のID
     * @param testCodes 登録する検査コードの配列
     * @return 全ての登録に成功した場合は true、失敗した場合は false
     */
    public boolean createLabTestItems(int labTestOrderId, String[] testCodes) {
        String sql = "INSERT INTO lab_test_items (lab_test_order_id, test_code, status) VALUES (?, ?, '指示済み')";
        Connection con = null;
        PreparedStatement ps = null;
        boolean result = false;

        try {
            con = DBManager.getConnection();
            // ★トランザクション開始：複数INSERTの途中で失敗した場合に、処理を巻き戻すため
            con.setAutoCommit(false);

            ps = con.prepareStatement(sql);

            // 選択された検査コードの数だけINSERTを繰り返す
            for (String testCode : testCodes) {
                ps.setInt(1, labTestOrderId);
                ps.setString(2, testCode);
                ps.addBatch(); // 複数のSQLをまとめて実行するバッチ処理に追加
            }
            
            ps.executeBatch(); // バッチ処理を実行
            con.commit(); // 全て成功したらコミット
            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) {
                    con.rollback(); // エラーが発生した場合はロールバック
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                 if (con != null) {
                    con.setAutoCommit(true); // 自動コミットモードに戻す
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBManager.close(con, ps, null);
        }
        return result;
    }

    /**
     * ★追加: 未完了の臨床検査指示を全て取得します。
     * 患者名も一緒に取得するために、テーブルを結合(JOIN)します。
     * @return 未完了の検査指示リスト (各指示は情報を格納したMap)
     */
    public List<Map<String, Object>> getPendingLabTestOrders() {
        List<Map<String, Object>> orderList = new ArrayList<>();
        // lab_test_orders, consultations, patients の3つのテーブルを結合
        String sql = "SELECT lto.lab_test_order_id, lto.ordered_at, p.patid, p.patlname, p.patfname " +
                     "FROM lab_test_orders lto " +
                     "JOIN consultations c ON lto.consultation_id = c.consultation_id " +
                     "JOIN patients p ON c.patient_id = p.patid " +
                     "WHERE lto.order_status = '指示済み' OR lto.order_status = '一部完了' " +
                     "ORDER BY lto.ordered_at ASC"; // 指示が古い順に表示

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("lab_test_order_id", rs.getInt("lab_test_order_id"));
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
     * ★追加: 特定の検査指示の詳細（含まれる全検査項目）を取得します。
     * @param labTestOrderId 詳細を取得したい検査指示のID
     * @return 検査項目Beanのリスト
     */
    public List<LabTestItemBean> getLabTestItemsByOrderId(int labTestOrderId) {
        List<LabTestItemBean> itemList = new ArrayList<>();
        String sql = "SELECT lab_test_item_id, test_code, result_value, status FROM lab_test_items WHERE lab_test_order_id = ?";
        
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, labTestOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LabTestItemBean item = new LabTestItemBean();
                    item.setLabTestItemId(rs.getInt("lab_test_item_id"));
                    item.setTestCode(rs.getString("test_code"));
                    item.setResultValue(rs.getString("result_value"));
                    item.setStatus(rs.getString("status"));
                    // LabTestBeanから検査名などを補完するのはサーブレットの役割
                    itemList.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemList;
    }

    /**
     * ★追加: 個別の検査項目の結果を更新します。
     * @param labTestItemId 更新する検査項目のID
     * @param resultValue 登録する結果の値
     * @return 更新に成功した場合は true
     */
    public boolean updateTestItemResult(int labTestItemId, String resultValue) {
        String sql = "UPDATE lab_test_items SET result_value = ?, status = '完了' WHERE lab_test_item_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, resultValue);
            ps.setInt(2, labTestItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ★追加: 指定された検査指示に含まれる全ての項目が完了したかチェックし、
     * もし完了していれば親の検査指示のステータスを更新します。
     * @param labTestOrderId チェック対象の親の検査指示ID
     */
    public void checkAndUpdateOrderStatus(int labTestOrderId) {
        String checkSql = "SELECT COUNT(*) FROM lab_test_items WHERE lab_test_order_id = ? AND status != '完了'";
        String updateSql = "UPDATE lab_test_orders SET order_status = '完了', completed_at = NOW() WHERE lab_test_order_id = ?";

        try (Connection con = DBManager.getConnection()) {
            int pendingItemCount = 0;
            // 1. 未完了の項目が残っているかチェック
            try (PreparedStatement psCheck = con.prepareStatement(checkSql)) {
                psCheck.setInt(1, labTestOrderId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        pendingItemCount = rs.getInt(1);
                    }
                }
            }
            // 2. 未完了項目が0件なら、親オーダーのステータスを更新
            if (pendingItemCount == 0) {
                try (PreparedStatement psUpdate = con.prepareStatement(updateSql)) {
                    psUpdate.setInt(1, labTestOrderId);
                    psUpdate.executeUpdate();
                    System.out.println("検査指示ID: " + labTestOrderId + " は全て完了しました。");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * ★★★ このメソッドを追加します ★★★
     * 特定の検査指示IDに紐づく親情報（患者IDと患者名）を取得します。
     * @param labTestOrderId 詳細を取得したい検査指示のID
     * @return 患者ID、患者名を含むMapオブジェクト。見つからなければnull。
     */
    public Map<String, Object> getLabTestOrderParentDetails(int labTestOrderId) {
        Map<String, Object> orderDetails = null;
        // lab_test_orders, consultations, patients の3つのテーブルを結合
        String sql = "SELECT p.patid, p.patlname, p.patfname " +
                     "FROM lab_test_orders lto " +
                     "JOIN consultations c ON lto.consultation_id = c.consultation_id " +
                     "JOIN patients p ON c.patient_id = p.patid " +
                     "WHERE lto.lab_test_order_id = ?";

        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, labTestOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    orderDetails = new HashMap<>();
                    orderDetails.put("patient_id", rs.getString("patid"));
                    orderDetails.put("patient_name", rs.getString("patlname") + " " + rs.getString("patfname"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }
    
    
    // 今後、臨床検査技師が指示一覧を取得するためのメソッドなどをここに追加していきます。
}
