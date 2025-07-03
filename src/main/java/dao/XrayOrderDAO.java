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
    public boolean createXrayOrder(int consultationId) {
        String sql = "INSERT INTO xray_orders (consultation_id, order_status) VALUES (?, '指示済み')";
        Connection con = null;
        PreparedStatement ps = null;
        boolean result = false;

        try {
            con = DBManager.getConnection();
            ps = con.prepareStatement(sql);

            ps.setInt(1, consultationId);

            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                result = true; // 1行以上更新されたら成功
            }
        } catch (SQLException e) {
            e.printStackTrace(); // エラーログを出力
        } finally {
            // ResultSetは使っていないので、nullを渡す
            DBManager.close(con, ps, null);
        }
        
        return result;
    }
    
    // 今後、レントゲン技師が指示一覧を取得するためのメソッドなどをここに追加していきます。
}
