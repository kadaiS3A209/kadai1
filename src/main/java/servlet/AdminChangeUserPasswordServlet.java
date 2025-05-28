package servlet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import controller.PasswordUtils;
import dao.EmployeeDAO;
import model.EmployeeBean;

/**
 * Servlet implementation class AdminChangeEmpPasswordServlet
 */
@WebServlet("/AdminChangeUserPasswordServlet") // URLマッピング名を変更する場合
public class AdminChangeUserPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String empId = request.getParameter("empId"); // ここではユーザーIDをempIdで統一

        if ("showForm".equals(action) && empId != null && !empId.isEmpty()) {
            EmployeeDAO dao = new EmployeeDAO();
            EmployeeBean userToChange = dao.getEmployeeById(empId); // 共通のメソッドで取得

            if (userToChange != null) {
                request.setAttribute("userToChange", userToChange); // JSPで表示するユーザー情報
                request.getRequestDispatcher("/WEB-INF/jsp/admin_change_user_password_form.jsp").forward(request, response);
            } else {
                request.setAttribute("listErrorMessage", "指定されたユーザーIDが見つかりません。"); // エラーメッセージ属性名変更
                // 元のリスト（従業員か管理者か）を特定するのは難しいため、メニューに戻すか汎用エラーページへ
                request.getRequestDispatcher("ReturnToMenuServlet").forward(request, response);
            }
        } else {
            response.sendRedirect("ReturnToMenuServlet"); // 不正なアクセスはメニューへ
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String empIdToChange = request.getParameter("empIdToChange");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String successMessage = null;
        String errorMessage = null;

        EmployeeDAO dao = new EmployeeDAO();
        EmployeeBean userToChange = dao.getEmployeeById(empIdToChange); // エラー時/成功時表示用

        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            errorMessage = "新しいパスワードと確認用パスワードの両方を入力してください。";
        } else if (!newPassword.equals(confirmPassword)) {
            errorMessage = "新しいパスワードと確認用パスワードが一致しません。";
        }

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("userToChange", userToChange);
            request.getRequestDispatcher("/WEB-INF/jsp/admin_change_user_password_form.jsp").forward(request, response);
            return;
        }

        try {
            byte[] saltBytes = PasswordUtils.generateSalt();
            byte[] hashedPasswordBytes = PasswordUtils.hashPassword(newPassword.toCharArray(), saltBytes);
            String saltHex = PasswordUtils.toHexString(saltBytes);
            String hashedPasswordHex = PasswordUtils.toHexString(hashedPasswordBytes);

            boolean success = dao.updatePassword(empIdToChange, hashedPasswordHex, saltHex);

            if (success) {
                successMessage = "ユーザーID: " + empIdToChange + " のパスワードを変更しました。";
            } else {
                errorMessage = "パスワードの変更に失敗しました。";
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            errorMessage = "パスワードのハッシュ化処理中にエラーが発生しました。";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "パスワード変更処理中に予期せぬエラーが発生しました。";
        }

        request.setAttribute("userToChange", userToChange);
        if (successMessage != null) request.setAttribute("successMessage", successMessage);
        if (errorMessage != null) request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/WEB-INF/jsp/admin_change_user_password_form.jsp").forward(request, response);
    }
}