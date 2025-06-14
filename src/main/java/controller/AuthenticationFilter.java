package controller;

import java.io.IOException;

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

/**
 * ログイン認証を確認するフィルタ
 */
// ★保護したいURLパターンをここに記述します
@WebFilter(urlPatterns = {
	    "/Admin/*",      // Adminで始まるサーブレットすべて
	    "/Reception/*",  // Receptionで始まるサーブレットすべて
	    "/Doctor/*",     // Doctorで始まるサーブレットすべて
	    "/ReturnToMenuServlet",
	    "/EmployeeChangeOwnPasswordServlet",
	    "/WEB-INF/jsp/*" // WEB-INF/jsp/ 以下のすべてのファイル
	})
public class AuthenticationFilter extends HttpFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
         
        
        String requestURI = httpRequest.getRequestURI();

        
        // ★★★ 除外設定を追加 ★★★
        // ログインページ、ログアウト処理、CSSやJSなどのリソースはフィルタをバイパスさせる
        if (requestURI.endsWith("/LoginServlet") || 
            requestURI.endsWith("/LogoutServlet") || 
            requestURI.contains("/css/") || 
            requestURI.contains("/js/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);// 既存のセッションを取得（なければnull）

        // ログインしているかどうかをチェック
        boolean isLoggedIn = (session != null && session.getAttribute("loggedInUser") != null);

        if (isLoggedIn) {
            // ログインしている場合は、リクエストを続行させる
            // ★ブラウザキャッシュ無効化ヘッダーを追加
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            httpResponse.setDateHeader("Expires", 0); // Proxies.
            
            chain.doFilter(request, response);
        } else {
            // ログインしていない場合は、ログインページにリダイレクト
            System.out.println("認証フィルタ: 未ログインのためログインページにリダイレクトします。");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/LoginServlet");
        }
    }
}
