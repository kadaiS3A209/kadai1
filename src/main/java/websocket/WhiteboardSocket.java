package websocket;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import com.google.gson.Gson; // ★GSONをインポート
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dao.MemoDAO;

@ServerEndpoint("/whiteboard")
public class WhiteboardSocket {

    private static Set<Session> clients = new CopyOnWriteArraySet<>();
    private static final Gson gson = new Gson(); // JSON処理用のGSONインスタンス
    

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connected: " + session.getId());
        clients.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Connection closed: " + session.getId());
        clients.remove(session);
    }

    /**
     * ★クライアントからのメッセージ処理を機能拡張
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();
        String type = jsonMessage.get("type").getAsString();

        switch (type) {
            case "draw":
            case "text":
                // 描画とテキストは全員にブロードキャスト
                broadcast(message);
                break;
            
            case "save_as":
                // ★名前を付けて保存
                String title = jsonMessage.get("title").getAsString();
                String memoText = jsonMessage.get("text").getAsString();
                String imageData = jsonMessage.get("image").getAsString();
                MemoDAO.saveMemoAs(title, memoText, imageData);
                break;

            case "get_list":
                // ★保存済みリストの取得要求
                List<Map<String, Object>> memoList = MemoDAO.getMemoList();
                // "list_updated"というtypeで、要求元のクライアントにだけリストを送信
                sendToSession(session, Map.of("type", "list_updated", "data", memoList));
                break;

            case "load_memo":
                // ★IDを指定してメモを読み込み、全員にブロードキャスト
                int id = jsonMessage.get("id").getAsInt();
                Map<String, String> data = MemoDAO.loadMemoById(id);
                if (data != null) {
                    // "load_memo_success"というtypeで、全員の画面を更新
                    broadcast(gson.toJson(Map.of("type", "load_memo_success", "data", data)));
                }
                break;
            
            case "clear":
                // ★「新規作成」のために、全員の画面をクリアするメッセージをブロードキャスト
                broadcast(message);
                break;
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error on session " + session.getId());
        throwable.printStackTrace();
        clients.remove(session); 
    }

    /**
     * 接続している全クライアントにメッセージを送信する
     * @param jsonMessage 送信するJSON文字列
     */
    private void broadcast(String jsonMessage) {
        for (Session client : clients) {
            if (client.isOpen()) {
                try {
                    client.getBasicRemote().sendText(jsonMessage);
                } catch (IOException e) {
                    handleFailedTransmission(client, e);
                }
            }
        }
    }

    /**
     * 特定のセッションにメッセージを送信する
     * @param session 送信先のセッション
     * @param data 送信するデータ (Map形式)
     */
    private void sendToSession(Session session, Map<String, Object> data) {
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(gson.toJson(data));
            } catch (IOException e) {
                handleFailedTransmission(session, e);
            }
        }
    }
    
    /**
     * メッセージ送信失敗時の共通エラーハンドリング
     */
    private void handleFailedTransmission(Session client, IOException e) {
        System.err.println("Error sending message to " + client.getId() + ", removing session.");
        e.printStackTrace();
        clients.remove(client);
        try {
            client.close();
        } catch (IOException e1) {
            // ignore
        }
    }
}
