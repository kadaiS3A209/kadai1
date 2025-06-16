package controller; // パッケージ名は適宜変更してください


import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
// import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread; // ★この行は削除またはコメントアウト
import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpointConfig;

import websocket.GetHttpSessionConfigurator;
import websocket.WhiteboardSocket;

@WebListener
public class AppServletContextListener implements ServletContextListener {
	
	
	
	
	

    /**
     * アプリケーションのシャットダウン時に呼び出されるメソッド
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("アプリケーションを停止します。JDBCドライバのクリーンアップを開始します...");

        // ▼▼▼ このブロックをコメントアウトまたは削除します ▼▼▼
        // MySQLのAbandonedConnectionCleanupThreadを明示的に停止させる処理は、
        // 環境によっては不安定になるため、より安全な下の解放処理に任せます。
        /*
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
            System.out.println("AbandonedConnectionCleanupThreadのシャットダウンを試みました。");
        } catch (Exception e) {
            System.err.println("AbandonedConnectionCleanupThreadのシャットダウン中にエラーが発生しました。");
            e.printStackTrace();
        }
        */
        // ▲▲▲ ここまでコメントアウトまたは削除 ▲▲▲


        // WebアプリケーションのクラスローダによってロードされたJDBCドライバを解放する
        // この処理だけでも多くの場合でメモリリーク警告は解消されます。
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            // アプリケーションのクラスローダによってロードされたドライバのみを対象とする
            if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
                try {
                    DriverManager.deregisterDriver(driver);
                    System.out.println(String.format("JDBCドライバ %s を解放しました。", driver));
                } catch (SQLException e) {
                    System.err.println(String.format("JDBCドライバ %s の解放中にエラーが発生しました。", driver));
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * アプリケーションの起動時に呼び出されるメソッド (今回は特に処理なし)
     */
    @Override
	public void contextInitialized(ServletContextEvent sce) {
        System.out.println("アプリケーションが起動しました。WebSocketエンドポイントを設定します。");

        // WebSocketのエンドポイントをプログラム的に登録
        ServletContext context = sce.getServletContext();
        ServerContainer serverContainer = (ServerContainer) context.getAttribute(ServerContainer.class.getName());

        try {
            // カスタムコンフィギュレータを使用してServerEndpointConfigを作成
            ServerEndpointConfig config = ServerEndpointConfig.Builder
                    .create(WhiteboardSocket.class, "/whiteboard")
                    .configurator(new GetHttpSessionConfigurator())
                    .build();

            // WebSocketコンテナにエンドポイント設定を追加
            serverContainer.addEndpoint(config);
            System.out.println("Whiteboard WebSocketエンドポイントがプログラム的に登録されました。");

            // メッセージバッファサイズを設定 (Tomcat 10.1の場合)
            // この設定は ServerContainer のレベルで行う
            // デフォルトのバッファサイズを変更 (単位: byte)
            serverContainer.setDefaultMaxTextMessageBufferSize(1048576); // 1MB
            System.out.println("WebSocketのテキストメッセージバッファサイズが1MBに設定されました。");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
