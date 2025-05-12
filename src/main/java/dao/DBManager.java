package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    // データベース接続情報 (環境変数や設定ファイルから読み込むのが望ましい)
    private static final String DB_URL = "jdbc:mysql://localhost/s3a209"; // JSTを指定
    private static final String DB_USER = "root"; // MySQLのユーザー名
    private static final String DB_PASSWORD = "password"; // MySQLのパスワード
    //private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    // データベース接続を取得するメソッド
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // 実際にはより適切なログ処理を
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // closeメソッド群 (Connection, PreparedStatement, ResultSetをそれぞれ閉じる)
    public static void close(Connection conn, java.sql.PreparedStatement pstmt, java.sql.ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
     // オーバーロードされたcloseメソッド (ResultSetがない場合など)
    public static void close(Connection conn, java.sql.PreparedStatement pstmt) {
        close(conn, pstmt, null);
    }

}
