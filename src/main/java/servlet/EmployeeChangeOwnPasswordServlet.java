package servlet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import controller.PasswordUtils;
import dao.EmployeeDAO;
import model.EmployeeBean;

/**
 * Servlet implementation class Afaf
 */
@WebServlet("/EmployeeChangeOwnPasswordServlet")
public class EmployeeChangeOwnPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GETリクエストはパスワード変更フォームを表示
        // ログインチェックはフィルタで行うか、ここで行う
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect("LoginServlet"); // 未ログインならログインページへ
            return;
        }
        request.getRequestDispatcher("/WEB-INF/jsp/employee_change_password_form.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // ログインチェック
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }

        EmployeeBean loggedInUser = (EmployeeBean) session.getAttribute("loggedInUser");
        String empId = loggedInUser.getEmpid(); // セッションからログイン中ユーザーのID取得

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String successMessage = null;
        String errorMessage = null;

        // バリデーション (基本設計書E103, テストケース「受付/医師パスワード変更異常」参照)
        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            errorMessage = "新しいパスワードと確認用パスワードの両方を入力してください。";
        } else if (!newPassword.equals(confirmPassword)) {
            errorMessage = "新しいパスワードと確認用パスワードが一致しません。";
        }
        // ここにパスワードの複雑性要件（例: 最低n文字以上、英数字混在など）があれば追加

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/WEB-INF/jsp/employee_change_password_form.jsp").forward(request, response);
            return;
        }

        EmployeeDAO dao = new EmployeeDAO();
        try {
            byte[] saltBytes = PasswordUtils.generateSalt();
            byte[] hashedPasswordBytes = PasswordUtils.hashPassword(newPassword.toCharArray(), saltBytes);
            String saltHex = PasswordUtils.toHexString(saltBytes);
            String hashedPasswordHex = PasswordUtils.toHexString(hashedPasswordBytes);

            boolean success = dao.updatePassword(empId, hashedPasswordHex, saltHex);

            if (success) {
                successMessage = "パスワードを変更しました。"; // テストケース「受付/医師パスワード変更正常」
            } else {
                errorMessage = "パスワードの変更に失敗しました。データベースエラーの可能性があります。";
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace(); // ログ記録
            errorMessage = "パスワードのハッシュ化処理中にエラーが発生しました。";
        } catch (Exception e) {
            e.printStackTrace(); // ログ記録
            errorMessage = "パスワード変更処理中に予期せぬエラーが発生しました。";
        }

        if (successMessage != null) request.setAttribute("successMessage", successMessage);
        if (errorMessage != null) request.setAttribute("errorMessage", errorMessage);
        
        // メッセージを表示するために同じフォームJSPにフォワード
        request.getRequestDispatcher("/WEB-INF/jsp/employee_change_password_form.jsp").forward(request, response);
    }
}