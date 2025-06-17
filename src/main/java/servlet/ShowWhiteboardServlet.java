package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ShowWhiteboardServlet")
public class ShowWhiteboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ★このサーブレット自体が認証フィルタの対象になっている必要がある
        // ログインしていない場合はフィルタがログインページにリダイレクトしてくれる
        
        // 共有機能のJSPにフォワードする
        request.getRequestDispatcher("/WEB-INF/jsp/whiteboard.jsp").forward(request, response);
        // (JSPファイル名が異なる場合は、ここのパスを修正してください)
    }
}
