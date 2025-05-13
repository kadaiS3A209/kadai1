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
 * Servlet implementation class AdminRegisterEmployeeServlet
 */
@WebServlet("/AdminRegisterEmployeeServlet")
public class AdminRegisterEmployeeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminRegisterEmployeeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// GETリクエストは基本的に入力フォームを表示
        // 確認画面から「修正する」で戻ってきた場合もここに来る想定
        // セッションに一時データがあれば、それをJSPで表示するためにフォワードする
        request.getRequestDispatcher("/WEB-INF/jsp/E100/admin_register_employee_form.jsp").forward(request, response);
         // JSPをWEB-INF配下に置くことで直接アクセスを防ぐ
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if ("confirm".equals(action)) {
            // --- 入力画面からの確認処理 ---
            String lname = request.getParameter("emplname");
            String fname = request.getParameter("empfname");
            String roleStr = request.getParameter("emprole");
            String password = request.getParameter("password");
            String passwordConfirm = request.getParameter("passwordConfirm");

            // 簡単なサーバーサイドバリデーション (クライアント側チェックに加えて)
            if (lname == null || lname.trim().isEmpty() ||
                fname == null || fname.trim().isEmpty() ||
                roleStr == null || roleStr.isEmpty() ||
                password == null || password.isEmpty() ||
                !password.equals(passwordConfirm)) {

                request.setAttribute("formError", "入力内容に誤りがあります。");
                // 入力値をリクエスト属性に再設定してフォームに戻す（またはセッションを使う）
                // 簡単な例としてエラーメッセージのみ設定
                 request.getRequestDispatcher("/WEB-INF/jsp/E100/admin_register_employee_form.jsp").forward(request, response);
                return;
            }

            int role = Integer.parseInt(roleStr);

            // --- 従業員IDの自動生成 ---
            String rolePrefix = "";
            if (role == 1) { // 受付
                rolePrefix = "RC";
            } else if (role == 2) { // 医師
                rolePrefix = "DR";
            } else if (role == 3){
            	rolePrefix = "MG";
            } else{
                 request.setAttribute("formError", "無効なロールです。");
                 request.getRequestDispatcher("/WEB-INF/jsp/E100/admin_register_employee_form.jsp").forward(request, response);
                return;
            }

            EmployeeDAO dao = new EmployeeDAO(); // DAOのインスタンス化 (実際には依存性注入などを検討)
            String lastId = dao.findLastEmployeeIdForRole(rolePrefix);
            int nextSeq = 1;
            if (lastId != null && lastId.length() == 8) {
                try {
                    // "RC000015" の "000015" 部分を取得して数値に変換
                    nextSeq = Integer.parseInt(lastId.substring(2)) + 1;
                } catch (NumberFormatException e) {
                    // ID形式が不正な場合のエラーハンドリング (通常は起こらないはず)
                    e.printStackTrace(); // ログ記録
                     request.setAttribute("formError", "従業員IDの生成に失敗しました。");
                     request.getRequestDispatcher("/WEB-INF/jsp/E100/admin_register_employee_form.jsp").forward(request, response);
                    return;
                }
            }
            // 6桁のゼロ埋め文字列を生成 ("000001", "000016" など)
            String newSeqStr = String.format("%06d", nextSeq);
            String newEmpId = rolePrefix + newSeqStr;
            // --- ID生成完了 ---

            // EmployeeBeanに情報を詰めてセッションに保存 (パスワードはまだ生)
            EmployeeBean tempEmployee = new EmployeeBean();
            tempEmployee.setEmpid(newEmpId);
            tempEmployee.setEmplname(lname);
            tempEmployee.setEmpfname(fname);
            tempEmployee.setRole(role);
            tempEmployee.setEmppasswd(password); // ★注意: この時点では生のパスワード

            session.setAttribute("tempEmployee", tempEmployee);

            // 確認画面へフォワード
            request.getRequestDispatcher("/WEB-INF/jsp/E100/admin_register_employee_confirm.jsp").forward(request, response);

        } else if ("register".equals(action)) {
            // --- 確認画面からの登録実行処理 ---
            EmployeeBean employeeToRegister = (EmployeeBean) session.getAttribute("tempEmployee");

            if (employeeToRegister == null) {
                // セッション切れなどのエラー
                request.setAttribute("formError", "セッション情報が見つかりません。最初からやり直してください。");
                request.getRequestDispatcher("/WEB-INF/jsp/E100/admin_register_employee_form.jsp").forward(request, response);
                return;
            }

            try {
                // パスワードをハッシュ化
                String rawPassword = employeeToRegister.getEmppasswd(); // セッションから生のパスワード取得
                 if (rawPassword == null) { // 念のためチェック
                    throw new InvalidKeySpecException("Password not found in session bean.");
                }

                byte[] saltBytes = PasswordUtils.generateSalt();
                byte[] hashedPasswordBytes = PasswordUtils.hashPassword(rawPassword.toCharArray(), saltBytes);

                String saltHex = PasswordUtils.toHexString(saltBytes);
                String hashedPasswordHex = PasswordUtils.toHexString(hashedPasswordBytes);

                // Beanのパスワードとソルトを更新
                employeeToRegister.setEmppasswd(hashedPasswordHex);
                employeeToRegister.setSalt(saltHex);

                // DAOを使ってDBに登録
                EmployeeDAO dao = new EmployeeDAO(); // 再度インスタンス化
                boolean success = dao.registerEmployee(employeeToRegister);

                // セッションから一時情報を削除
                session.removeAttribute("tempEmployee");

                if (success) {
                    // 登録成功ページへリダイレクト (PRGパターン)
                    response.sendRedirect("admin_register_complete.jsp"); // 成功画面を作成
                } else {
                    // 登録失敗 (例: DBエラー)
                    request.setAttribute("formError", "データベースへの登録に失敗しました。");
                    request.getRequestDispatcher("/WEB-INF/jsp/E100/admin_register_employee_form.jsp").forward(request, response);
                }

            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                 // ハッシュ化処理の例外
                 e.printStackTrace(); // ログ記録
                 session.removeAttribute("tempEmployee"); // セッションクリア
                 request.setAttribute("formError", "パスワード処理中にエラーが発生しました。");
                 request.getRequestDispatcher("/WEB-INF/jsp/E100/admin_register_employee_form.jsp").forward(request, response);
            } catch (Exception e) {
                 // その他の予期せぬ例外
                 e.printStackTrace();
                 session.removeAttribute("tempEmployee");
                 request.setAttribute("formError", "予期せぬエラーが発生しました。");
                 request.getRequestDispatcher("/WEB-INF/jsp/E100/admin_register_employee_form.jsp").forward(request, response);
            }
        } else {
            // actionパラメータがない、または不正な場合
            response.sendRedirect("AdminRegisterEmployeeServlet"); // とりあえず入力画面へ
        }
    
	}

}
