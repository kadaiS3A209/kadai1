package websocket; // ご自身のパッケージ名に合わせてください

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // WebSocketのハンドシェイク（接続確立処理）中に、HTTPセッションを取得する
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        
        if (httpSession != null) {
            // 取得したHTTPセッションを、WebSocketのエンドポイントで後から利用できるように保管する
            sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
        }
    }
}