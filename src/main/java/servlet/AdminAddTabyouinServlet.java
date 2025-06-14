package servlet; // パッケージは適宜変更

import java.io.IOException;
// import java.text.NumberFormat; // ICU4Jなどを使う場合
// import java.util.Locale;     // ICU4Jなどを使う場合

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.TabyouinDAO;
import model.EmployeeBean; // ログインチェック用
import model.TabyouinBean;

@WebServlet("/AdminAddTabyouinServlet")
public class AdminAddTabyouinServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GETリクエストは入力フォームを表示 (修正で戻る場合もセッションの値をJSPで参照)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null ||
            ((EmployeeBean)session.getAttribute("loggedInUser")).getRole() != 3) { // 管理者ロールを3と仮定
            response.sendRedirect("LoginServlet");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin_add_tabyouin_form.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        if (session.getAttribute("loggedInUser") == null ||
            ((EmployeeBean)session.getAttribute("loggedInUser")).getRole() != 3) {
            response.sendRedirect("LoginServlet");
            return;
        }

        String action = request.getParameter("action");
        TabyouinDAO dao = new TabyouinDAO();

        if ("confirm".equals(action)) {
            String tabyouinId = request.getParameter("tabyouinId");
            String tabyouinMei = request.getParameter("tabyouinMei");
            String tabyouinAddrss = request.getParameter("tabyouinAddrss");
            String tabyouinTel = request.getParameter("tabyouinTel");
            String shihonkinStr = request.getParameter("tabyouinShihonkin");
            String kyukyuStr = request.getParameter("kyukyu");

            TabyouinBean tabyouin = new TabyouinBean();
            tabyouin.setTabyouinId(tabyouinId);
            tabyouin.setTabyouinMei(tabyouinMei);
            tabyouin.setTabyouinAddrss(tabyouinAddrss);
            tabyouin.setTabyouinTel(tabyouinTel);

            StringBuilder errors = new StringBuilder();
            // バリデーション (基本設計書H101、テスト仕様書「他病院新規登録」参照)
            if (tabyouinId == null || tabyouinId.trim().isEmpty()) errors.append("他病院IDは必須です。<br>");
            else if (tabyouinId.trim().length() > 8) errors.append("他病院IDは8文字以内です。<br>");
            else if (dao.isTabyouinIdExists(tabyouinId.trim())) errors.append("その他病院IDは既に使用されています。<br>");

            if (tabyouinMei == null || tabyouinMei.trim().isEmpty()) errors.append("他病院名は必須です。<br>");
            if (tabyouinAddrss == null || tabyouinAddrss.trim().isEmpty()) errors.append("住所は必須です。<br>");
            if (tabyouinTel == null || tabyouinTel.trim().isEmpty()) errors.append("電話番号は必須です。<br>");
            else if (!tabyouinTel.trim().matches("^[0-9\\-]{10,15}$")) errors.append("電話番号の形式が正しくありません。<br>");


            int shihonkin = 0;
            if (shihonkinStr == null || shihonkinStr.trim().isEmpty()) errors.append("資本金は必須です。<br>");
            else {
                try {
                    // 全角やカンマを考慮する場合は、仕入先の資本金パースで使ったnormalizeNumberStringのような関数が必要
                    shihonkin = Integer.parseInt(shihonkinStr.trim().replace(",", ""));
                    if (shihonkin < 0) errors.append("資本金は0以上の値を入力してください。<br>");
                    tabyouin.setTabyouinShihonkin(shihonkin);
                } catch (NumberFormatException e) {
                    errors.append("資本金は数値で入力してください。<br>");
                }
            }

            int kyukyu = 0;
            if (kyukyuStr == null || kyukyuStr.isEmpty()) errors.append("救急対応を選択してください。<br>");
            else {
                try {
                    kyukyu = Integer.parseInt(kyukyuStr);
                    if (kyukyu != 0 && kyukyu != 1) errors.append("救急対応の値が不正です。<br>");
                    tabyouin.setKyukyu(kyukyu);
                } catch (NumberFormatException e) {
                    errors.append("救急対応の値が不正です。<br>");
                }
            }


            if (errors.length() > 0) {
                request.setAttribute("formError_tabyouin_register", errors.toString());
                request.setAttribute("prevTabyouinInput", tabyouin); // 入力値をBeanで戻す
                request.getRequestDispatcher("/WEB-INF/jsp/admin_add_tabyouin_form.jsp").forward(request, response);
                return;
            }

            session.setAttribute("tempTabyouin", tabyouin);
            request.getRequestDispatcher("/WEB-INF/jsp/admin_add_tabyouin_confirm.jsp").forward(request, response); // 確認画面へ

        } else if ("register".equals(action)) {
            TabyouinBean tabyouinToRegister = (TabyouinBean) session.getAttribute("tempTabyouin");

            if (tabyouinToRegister == null) {
                request.setAttribute("formError_tabyouin_register", "セッション情報が見つかりません。最初からやり直してください。");
                request.getRequestDispatcher("/WEB-INF/jsp/admin_add_tabyouin_form.jsp").forward(request, response);
                return;
            }

            boolean success = dao.registerTabyouin(tabyouinToRegister);
            session.removeAttribute("tempTabyouin");

            if (success) {
                session.setAttribute("message_tabyouin_management", "他病院情報を登録しました。");
                response.sendRedirect(request.getContextPath() + "/AdminAddTabyouinServlet?action=complete");
            } else {
                request.setAttribute("formError_tabyouin_register", "データベースへの登録に失敗しました。");
                request.setAttribute("prevTabyouinInput", tabyouinToRegister);
                request.getRequestDispatcher("/WEB-INF/jsp/admin_add_tabyouin_form.jsp").forward(request, response);
            }
        } else if ("complete".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/jsp/admin_add_tabyouin_complete.jsp").forward(request, response);
        } else {
            response.sendRedirect("AdminAddTabyouinServlet"); // 不明なアクションは入力フォームへ
        }
    }
}