package websocket; // ご自身のパッケージ名に合わせてください

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import dao.MemoDAO; // DAOのパッケージに合わせてください

// ▼▼▼ 修正点(1): maxTextMessageBufferSize を追加して、大きなデータ(画像)を扱えるようにする ▼▼▼
@ServerEndpoint(value = "/whiteboard")
public class WhiteboardSocket {

    private static Set<Session> clients = new CopyOnWriteArraySet<>();
    private static final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) { // ★ EndpointConfig を引数に追加
        System.out.println("WebSocket connected: " + session.getId());
        clients.add(session);

        // ★ コンフィギュレータからHttpSessionを取得
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());

        if (httpSession != null && httpSession.getAttribute("loggedInUser") != null) {
            // EmployeeBean user = (EmployeeBean) httpSession.getAttribute("loggedInUser");
            // String userName = user.getEmplname() + " " + user.getEmpfname();
            // System.out.println("接続ユーザー: " + userName);
            // 誰かが接続したことを全員に通知する、などの処理も可能
            // broadcast("{\"type\":\"user_connected\", \"userName\":\"" + userName + "\"}");
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Connection closed: " + session.getId());
        clients.remove(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // ▼▼▼ 修正点(2): 処理全体をtry-catchで囲み、エラーで接続が切れるのを防ぐ ▼▼▼
        try {
            JsonObject msg = JsonParser.parseString(message).getAsJsonObject();
            String type = msg.get("type").getAsString();

            MemoDAO memoDAO = new MemoDAO();

            switch (type) {
                case "text":
                case "draw":
                case "clear":
                    // これらのメッセージは単純に全クライアントにブロードキャスト
                    broadcast(message);
                    break;

                case "save_as": {
                    String title = msg.get("title").getAsString();
                    String text = msg.get("text").getAsString();
                    String image = msg.get("image").getAsString();
                    
                    int newId = memoDAO.saveMemoAs(title, text, image); // ★戻り値(int)を受け取るように修正
                    if (newId != -1) { // 成功した場合 (IDが返ってくる)
                        String successMsg = "{\"type\":\"save_success\", \"newId\":" + newId + ", \"newTitle\":\"" + title + "\"}";
                        sendToSession(session, successMsg);
                        broadcast("{\"type\":\"list_updated\", \"data\":" + gson.toJson(memoDAO.getMemoList()) + "}");
                    } else {
                        sendToSession(session, "{\"type\":\"error\", \"message\":\"名前を付けて保存に失敗しました。\"}");
                    }
                    break;
                }
                
             // ▼▼▼ この "save_overwrite" の case ブロックを新しく追加します ▼▼▼
                case "save_overwrite": {
                    int id = msg.get("id").getAsInt();
                    String title = msg.get("title").getAsString();
                    String text = msg.get("text").getAsString();
                    String image = msg.get("image").getAsString();

                    // DAOの新しいupdateメソッドを呼び出す
                    boolean success = memoDAO.update(id, title, text, image);
                    
                    if (success) {
                        // 上書き保存成功をクライアントに通知
                        sendToSession(session, "{\"type\":\"save_success\"}"); // newIdは不要
                        // 他のクライアントの一覧表示も更新するためにブロードキャスト
                        broadcast("{\"type\":\"list_updated\", \"data\":" + gson.toJson(memoDAO.getMemoList()) + "}");
                    } else {
                        // (任意) 更新失敗をクライアントに通知
                        sendToSession(session, "{\"type\":\"error\", \"message\":\"上書き保存に失敗しました。\"}");
                    }
                    break;
                }
                // ▲▲▲ ここまで追加 ▲▲▲

                case "get_list": {
                    List<Map<String, Object>> list = memoDAO.getMemoList();
                    sendToSession(session, Map.of("type", "list_updated", "data", list));
                    break;
                }
                
                case "load_memo": {
                    int id = msg.get("id").getAsInt();
                    Map<String, Object> dataMap = memoDAO.loadMemoById(id); // DAOの戻り値の型に合わせる

                    if (dataMap != null) {
                        // ▼▼▼ この部分を修正 ▼▼▼
                        // MapからJsonObjectを構築し、typeを追加
                        JsonObject responseJson = new JsonObject();
                        responseJson.addProperty("type", "load_memo_success");
                        responseJson.addProperty("id", (Integer) dataMap.get("id"));
                        responseJson.addProperty("title", (String) dataMap.get("title"));
                        responseJson.addProperty("text", (String) dataMap.get("text"));
                        responseJson.addProperty("image", (String) dataMap.get("image"));

                        // 構築したJsonObjectをブロードキャスト
                        broadcast(gson.toJson(responseJson));
                        // ▲▲▲ ここまで修正 ▲▲▲
                    }
                    break;
                }
            }
        } catch (JsonSyntaxException e) {
            System.err.println("JSONの解析に失敗しました: " + message);
        } catch (Exception e) {
            // ★重要: DBエラーなど、予期せぬ例外がここでキャッチされ、接続断を防ぎます
            System.err.println("メッセージ処理中に予期せぬエラーが発生しました。");
            e.printStackTrace();
            // (任意) エラーが発生したことをクライアントに通知する
            try {
                if(session.isOpen()) {
                    session.getBasicRemote().sendText("{\"type\":\"error\", \"message\":\"サーバーエラーが発生しました。\"}");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        // ▲▲▲ try-catchブロックの終わり ▲▲▲
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
    	System.err.println("★ WebSocket onError が呼び出されました！ Session ID: " + session.getId());
        System.err.println("★ エラー内容: " + throwable.getMessage());
        System.err.println("Error on session " + session.getId());
        throwable.printStackTrace();
        clients.remove(session);
    }

    // ... (broadcast, sendToSession, handleFailedTransmission メソッドは変更なし) ...
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

    private void sendToSession(Session session, Map<String, Object> data) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(gson.toJson(data));
            } catch (IOException e) {
                handleFailedTransmission(session, e);
            }
        }
    }
    
    private void sendToSession(Session session, String jsonMessage) {
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(jsonMessage);
            } catch (IOException e) {
                handleFailedTransmission(session, e);
            }
        }
    }
    
    private void handleFailedTransmission(Session client, IOException e) {
        System.err.println("Error sending message to " + client.getId() + ", removing session.");
        clients.remove(client);
    }
}