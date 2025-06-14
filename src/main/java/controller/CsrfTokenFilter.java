package controller; // パッケージ名はご自身のものに合わせてください

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*") // 全てのリクエストを対象とする
public class CsrfTokenFilter extends HttpFilter implements Filter {
    private static final long serialVersionUID = 1L;
    private static final String CSRF_TOKEN_SESSION_ATTR = "csrf_token";
    private static final String CSRF_TOKEN_REQUEST_PARAM = "csrf_token";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // ▼▼▼ ここからが修正・改善されたロジックです ▼▼▼

        // --- ステップ1: ログイン処理のPOSTリクエストは、トークンチェックを免除する ---
        // ログイン処理自体はCSRFの対象外と考えてよいため、フィルタを通過させる
        if (httpRequest.getRequestURI().endsWith("/LoginServlet") && "POST".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // --- ステップ2: ログイン処理以外のPOSTリクエストは、トークンを検証する ---
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            HttpSession session = httpRequest.getSession(false); // 既存のセッションを取得

            String sessionToken = (session == null) ? null : (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
            String requestToken = request.getParameter(CSRF_TOKEN_REQUEST_PARAM);

            // セッションにトークンがない、またはフォームからのトークンと一致しない場合は不正なリクエスト
            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "不正なリクエストです。"); // 403 Forbidden
                return;
            }
        }

        // --- ステップ3: 全てのリクエストに対し、次のフォーム表示に備えてトークンを準備する ---
        // セッションがなければ作成し、トークンがなければ生成してセッションに保存する
        HttpSession session = httpRequest.getSession(true); // セッションを取得（なければ新規作成）
        if (session.getAttribute(CSRF_TOKEN_SESSION_ATTR) == null) {
            session.setAttribute(CSRF_TOKEN_SESSION_ATTR, generateToken());
        }
        
        // JSPで使えるように、常にリクエスト属性に現在のトークンをセットする
        request.setAttribute(CSRF_TOKEN_REQUEST_PARAM, session.getAttribute(CSRF_TOKEN_SESSION_ATTR));

        // 全てのチェックを通過したら、本来の処理を続行
        chain.doFilter(request, response);
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // initとdestroyメソッドはHttpFilterに実装があるので、ここでは省略可能
}