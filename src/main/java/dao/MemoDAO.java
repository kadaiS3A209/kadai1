package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoDAO {
	/**
     * ★「名前を付けて保存」機能のためのメソッド
     * @param title 保存するタイトル
     * @param memoText メモのテキスト
     * @param whiteboardData ホワイトボードの画像データ (Base64)
     */
    public static void saveMemoAs(String title, String memoText, String whiteboardData) {
        String sql = "INSERT INTO memos (title, memo_text, whiteboard_data) VALUES (?, ?, ?)";
        
        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, memoText);
            pstmt.setString(3, whiteboardData);
            pstmt.executeUpdate();
            System.out.println("データが正常に保存されました: " + title);

        } catch (SQLException e) {
            System.err.println("データベースへの保存中にエラーが発生しました。");
            e.printStackTrace();
        }
    }

    /**
     * ★IDを指定して特定のメモを読み込むメソッド
     * @param id 読み込むメモのID
     * @return テキストと画像データを含むMap。データがない場合はnull。
     */
    public static Map<String, String> loadMemoById(int id) {
        String sql = "SELECT memo_text, whiteboard_data FROM memos WHERE id = ?";
        Map<String, String> data = null;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    data = new HashMap<>();
                    data.put("text", rs.getString("memo_text"));
                    data.put("image", rs.getString("whiteboard_data"));
                    System.out.println("ID:" + id + " のデータを読み込みました。");
                }
            }

        } catch (SQLException e) {
            System.err.println("ID:" + id + " のデータ読み込み中にエラーが発生しました。");
            e.printStackTrace();
        }
        return data;
    }

    /**
     * ★保存されているメモの一覧を取得するメソッド
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
            System.out.println(list.size() + "件の保存済みデータを取得しました。");

        } catch (SQLException e) {
            System.err.println("保存リストの取得中にエラーが発生しました。");
            e.printStackTrace();
        }
        return list;
    }
}



