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
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GETリクエストはログインページを表示
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");

        //[span_1](start_span)// テストケース: アカウント・パスワードとも空欄[span_1](end_span)
        //[span_2](start_span)// テストケース: 指定したアカウント・パスワード空欄[span_2](end_span)
        if (userId == null || userId.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "ユーザーIDとパスワードを入力してください。");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeBean employee = employeeDAO.getEmployeeById(userId.trim());

        boolean isAuthenticated = false;

        if (employee != null && employee.getEmpid() != null) { // ユーザーが存在する場合
            String storedPassword = employee.getEmppasswd();
            String saltHex = employee.getSalt(); // DBから取得したソルト (16進数文字列)

            // ★★★ 修正点: 管理者も含め、ソルトが存在すれば必ずハッシュ比較を行う ★★★
            if (storedPassword != null && saltHex != null && !saltHex.trim().isEmpty()) {
                try {
                    byte[] saltBytes = PasswordUtils.fromHexString(saltHex);
                    byte[] hashedPasswordBytes = PasswordUtils.hashPassword(password.toCharArray(), saltBytes);
                    String inputHashedPasswordHex = PasswordUtils.toHexString(hashedPasswordBytes);

                    if (inputHashedPasswordHex.equals(storedPassword)) {
                        isAuthenticated = true;
                    }
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace(); // サーバーログに記録
                    request.setAttribute("errorMessage", "ログイン処理中にエラーが発生しました。");
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                    return;
                }
            } else {
                // ソルトが存在しない、またはパスワードがDBにない場合は認証失敗
                // (新規登録時に必ずソルトとハッシュ化パスワードを保存する運用であれば、
                //  このelseに来るのは異常ケースか、パスワード未設定のユーザーなど)
                System.out.println("認証失敗: DBにソルトまたはハッシュ化パスワードが存在しません。UserID: " + userId);
            }
        }

        if (isAuthenticated) {
            // ... (セッション作成とメニュー画面へのリダイレクト処理は変更なし) ...
        	// ★★★ ここからが修正箇所 ★★★
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate(); // 古いセッションを破棄
            }
            HttpSession newSession = request.getSession(true); // 新しいセッションを強制的に作成
            // ▲▲▲ ここまで修正 ▲▲▲
            
            newSession.setAttribute("loggedInUser", employee);
            newSession.setAttribute("userId", employee.getEmpid());
            newSession.setAttribute("userName", employee.getEmplname() + " " + employee.getEmpfname());
            newSession.setAttribute("userRole", employee.getRole());

            switch (employee.getRole()) {
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
                    newSession.invalidate();
                    request.setAttribute("errorMessage", "不明なユーザーロールです。");
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                    break;
            }
        } else {
            request.setAttribute("errorMessage", "ユーザーIDまたはパスワードが正しくありません。");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }

    }
}

