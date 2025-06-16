package dao; // ご自身のパッケージ名に合わせてください

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoDAO {

    /**
     * ★修正: 「名前を付けて保存」し、自動採番されたIDを返します。
     * @return 成功した場合は新しいID、失敗した場合は -1
     */
    public static int saveMemoAs(String title, String memoText, String whiteboardData) {
        String sql = "INSERT INTO memos (title, memo_text, whiteboard_data) VALUES (?, ?, ?)";
        int newId = -1;
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, memoText);
            pstmt.setString(3, whiteboardData);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    newId = rs.getInt(1);
                }
            }
            System.out.println("データが正常に保存されました: " + title);

        } catch (SQLException e) {
            System.err.println("データベースへの保存中にエラーが発生しました。");
            e.printStackTrace();
        }
        return newId;
    }

    /**
     * ★追加: 既存のメモを更新（上書き保存）します。
     * @return 更新に成功した場合は true
     */
    public boolean update(int id, String title, String text, String imageData) {
        // ▼▼▼ SQL文から "updated_at = NOW()" を削除 ▼▼▼
        String sql = "UPDATE memos SET title = ?, memo_text = ?, whiteboard_data = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, text);
            pstmt.setString(3, imageData);
            pstmt.setInt(4, id);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBManager.close(conn, pstmt);
        }
    }

    /**
     * IDを指定して特定のメモを読み込みます。
     * @return ID、タイトル、テキスト、画像データを含むMap
     */
    public static Map<String, Object> loadMemoById(int id) {
        String sql = "SELECT id, title, memo_text, whiteboard_data FROM memos WHERE id = ?";
        Map<String, Object> data = null;
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
            	if (rs.next()) {
                    data = new HashMap<>();
                    data.put("id", rs.getInt("id"));         // int型
                    data.put("title", rs.getString("title")); // String型
                    data.put("text", rs.getString("memo_text")); // String型
                    data.put("image", rs.getString("whiteboard_data")); // String型
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 保存されているメモの一覧を取得します。
     * @return ID、タイトル、作成日時を含むMapのリスト
     */
    public static List<Map<String, Object>> getMemoList() {
        String sql = "SELECT id, title, created_at FROM memos ORDER BY id DESC";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("title", rs.getString("title"));
                item.put("createdAt", rs.getTimestamp("created_at").toString());
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}