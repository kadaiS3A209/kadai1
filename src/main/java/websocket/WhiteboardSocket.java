package websocket; // ご自身のパッケージに合わせてください

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.EndpointConfig; // onOpenで使うので残します
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dao.MemoDAO;
import model.EmployeeBean;

// ▼▼▼ configuratorは使わない方式に変更します ▼▼▼
@ServerEndpoint(value = "/whiteboard",configurator = GetHttpSessionConfigurator.class )
public class WhiteboardSocket {

    private static final Map<Session, String> clients = new ConcurrentHashMap<>();
    private static String currentMemoText = "";
    private static String currentWhiteboardImage = "";
    private static final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        String userName = "ゲスト";
        
        // ★★★ configurator経由でHttpSessionを取得する正しい方法 ★★★
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());

        if (httpSession != null && httpSession.getAttribute("loggedInUser") != null) {
            EmployeeBean user = (EmployeeBean) httpSession.getAttribute("loggedInUser");
            userName = user.getEmplname() + " " + user.getEmpfname();
        } else {
             System.out.println("onOpen: HttpSessionまたはログイン情報が見つかりません。ゲストとして扱います。");
        }
        
        clients.put(session, userName);
        System.out.println(userName + " が接続しました。現在の接続数: " + clients.size());
        
        sendCurrentState(session);
        updateUserList();
    }

    @OnClose
    public void onClose(Session session) {
        String userName = clients.remove(session);
        System.out.println((userName != null ? userName : "不明なユーザー") + " の接続が切れました。現在の接続数: " + clients.size());
        updateUserList();
    }
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocketエラー発生: " + session.getId());
        throwable.printStackTrace();
        clients.remove(session);
        updateUserList();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // ★このメソッドの中身は変更なし。以前のままでOK
        try {
            JsonObject msg = JsonParser.parseString(message).getAsJsonObject();
            String type = msg.get("type").getAsString();
            
            switch (type) {
                case "text":
                case "draw":
                case "update_image":
                case "clear":
                    updateStateAndBroadcast(msg, session);
                    break;
                case "save_as": 
                case "save_overwrite":
                    handleSave(msg, session);
                    break;
                case "load_memo":
                    handleLoad(msg);
                    break;
                case "get_list":
                    MemoDAO dao = new MemoDAO();
                    List<Map<String, Object>> list = dao.getMemoList();
                    sendToSession(session, "{\"type\":\"list_updated\", \"data\":" + gson.toJson(list) + "}");
                    break;
                case "get_user_list":
                    updateUserList();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    /**
     * 新規接続者に現在のメモとホワイトボードの状態を送信する
     */
    private static void handleLoad(JsonObject msg) {
        MemoDAO dao = new MemoDAO();
        int id = msg.get("id").getAsInt();
        Map<String, Object> data = dao.loadMemoById(id);
        if (data != null) {
            currentMemoText = (String) data.get("text");
            currentWhiteboardImage = (String) data.get("image");
            data.put("type", "load_memo_success");
            broadcast(gson.toJson(data), null);
        }
    }

    private static void sendCurrentState(Session session) {
        sendToSession(session, "{\"type\":\"text\", \"data\":" + gson.toJson(currentMemoText) + "}");
        if (currentWhiteboardImage != null && !currentWhiteboardImage.isEmpty()) {
            sendToSession(session, "{\"type\":\"load_image\", \"image\":\"" + currentWhiteboardImage + "\"}");
        }
    }

    private static void updateUserList() {
        List<String> userNames = new ArrayList<>(clients.values());
        System.out.println("現在の接続者リストを更新します: " + userNames); // ★デバッグログ追加
        String jsonMessage = "{\"type\":\"user_list_update\", \"users\":" + gson.toJson(userNames) + "}";
        broadcast(jsonMessage, null);
    }
    
    private static void broadcast(String jsonMessage, Session senderSession) {
        System.out.println("ブロードキャスト実行 (送信元: " + (senderSession != null ? senderSession.getId() : "全員") + ")"); // ★デバッグログ追加
        clients.keySet().forEach(client -> {
            if (senderSession != null && client.equals(senderSession)) {
                return;
            }
            sendToSession(client, jsonMessage);
        });
    }
    
    private  static void sendToSession(Session session, String jsonMessage) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(jsonMessage);
            } catch (IOException e) {
                handleFailedTransmission(session, e);
            }
        }
    }
    
    private static void handleFailedTransmission(Session client, IOException e) {
        System.err.println("メッセージ送信失敗: " + client.getId());
        clients.remove(client);
    }
    
	/*   private static String getHttpSessionIdFromQuery(Session session) {
	    String queryString = session.getQueryString();
	    if (queryString != null && queryString.startsWith("sessionId=")) {
	        return queryString.substring("sessionId=".length());
	    }
	    return null;
	}*/
    
    /**
     * ★★★ 新しく追加する handleSave メソッド ★★★
     * 「名前を付けて保存」と「上書き保存」の共通処理を行います。
     * @param msg クライアントから受信したJSONオブジェクト
     * @param session メッセージを送信したクライアントのセッション
     */
    private static  void handleSave(JsonObject msg, Session session) {
        String type = msg.get("type").getAsString();
        String title = msg.get("title").getAsString();
        String text = msg.get("text").getAsString();
        String image = msg.get("image").getAsString();
        MemoDAO dao = new MemoDAO();
        boolean dbSuccess = false;
        int newId = -1;

        if ("save_as".equals(type)) {
            newId = dao.saveMemoAs(title, text, image); // DAOの戻り値(int)を受け取る
            dbSuccess = (newId != -1);
        } else if ("save_overwrite".equals(type)) {
            int id = msg.get("id").getAsInt();
            dbSuccess = dao.update(id, title, text, image); // DAOの戻り値(boolean)を受け取る
        }

        if (dbSuccess) {
            // 成功メッセージを生成
            String successMsg = ("save_as".equals(type))
                ? "{\"type\":\"save_success\", \"newId\":" + newId + ", \"newTitle\":\"" + title + "\"}"
                : "{\"type\":\"save_success\"}";
            
            // 操作した本人に成功を通知
            sendToSession(session, successMsg);
            
            // 全員にリストの更新を通知
            broadcast("{\"type\":\"list_updated\", \"data\":" + gson.toJson(dao.getMemoList()) + "}", null);
        } else {
            // 失敗を通知
            sendToSession(session, "{\"type\":\"error\", \"message\":\"保存に失敗しました。\"}");
        }
    }

    /**
     * ★ onMessageをシンプルにするため、編集系メッセージの処理をまとめたメソッド
     */
    private static  void updateStateAndBroadcast(JsonObject msg, Session session) {
        String type = msg.get("type").getAsString();
        
        switch (type) {
            case "text":
                currentMemoText = msg.get("data").getAsString();
                break;
            case "update_image":
                currentWhiteboardImage = msg.get("image").getAsString();
                break;
            case "clear":
                currentMemoText = "";
                currentWhiteboardImage = "";
                break;
        }
        // "draw" は状態を保持しないので、ここでは何もしない
        
        // 送信者以外にブロードキャスト
        broadcast(gson.toJson(msg), session);
    }
}