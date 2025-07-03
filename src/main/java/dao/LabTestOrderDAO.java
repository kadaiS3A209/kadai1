package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    
    // 今後、臨床検査技師が指示一覧を取得するためのメソッドなどをここに追加していきます。
}
