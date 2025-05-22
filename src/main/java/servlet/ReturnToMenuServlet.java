package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.EmployeeBean;

/**
 * Servlet implementation class AdminMenuServlet
 */
@WebServlet("/ReturnToMenuServlet")
public class ReturnToMenuServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // 既存のセッションを取得（なければnull）

        if (session != null && session.getAttribute("loggedInUser") != null) {
            EmployeeBean loggedInUser = (EmployeeBean) session.getAttribute("loggedInUser");
            int userRole = loggedInUser.getRole(); // EmployeeBeanからロールを取得

            // ロールに基づいてリダイレクト先を決定
            switch (userRole) {
            	case 3: // 管理者
            		request.getRequestDispatcher("/WEB-INF/jsp/menu_admin.jsp").forward(request, response);
            		break;
            	case 1: // 受付
            		request.getRequestDispatcher("/WEB-INF/jsp/menu_reception.jsp").forward(request, response);
            		break;
            	case 2: // 医師
            		request.getRequestDispatcher("/WEB-INF/jsp/menu_doctor.jsp").forward(request, response);
                    break;
                default:
                    // 不明なロール、または予期せぬ状態
                    session.invalidate(); // セッションを無効化
                    response.sendRedirect("LoginServlet"); // 念のためログインページへ
                    break;
            }
        } else {
            // セッションが存在しない、またはログイン情報がない場合はログインページへ
            response.sendRedirect("LoginServlet");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 通常、メニューに戻る操作はGETで行われることが多いですが、POSTで呼び出された場合も同じ処理をします。
        doGet(request, response);
    }
}

