
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionTester {

    // ★★★ context.xmlに記述したものと全く同じ情報を入力してください ★★★
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/s3a209?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";

    public static void main(String[] args) {
        Connection conn = null;
        try {
            // JDBCドライバをロード
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("データベースへの接続を試みます...");
            System.out.println("URL: " + JDBC_URL);
            System.out.println("User: " + DB_USER);

            // データベースへ接続
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);

            System.out.println("------------------------------------");
            System.out.println("★★★★★ 接続に成功しました！ ★★★★★");
            System.out.println("------------------------------------");
            System.out.println("これで、ユーザー名、パスワード、DB名、URLパラメータが正しいことが確認できました。");


        } catch (ClassNotFoundException e) {
            System.err.println("JDBCドライバが見つかりません。");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("------------------------------------");
            System.err.println("XXXXX 接続に失敗しました。 XXXXX");
            System.err.println("------------------------------------");
            System.err.println("Tomcatで発生しているエラーの根本原因は、このエラーです。");
            System.err.println("エラーメッセージをよく読んで、原因を確認してください。（例: Access denied, Unknown databaseなど）");
            // エラーの詳細を出力
            e.printStackTrace();

        } finally {
            // 接続を閉じる
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
