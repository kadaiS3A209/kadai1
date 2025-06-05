package servlet; // パッケージは適宜変更

import java.io.IOException;
import java.text.NumberFormat;      // ICU4J (資本金パース用)
import java.util.List;
import java.util.Locale; // ICU4J用 (資本金パース)

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.TabyouinDAO;
import model.EmployeeBean; // ログインユーザーのロールチェック用
import model.TabyouinBean;

@WebServlet("/AdminManageTabyouinServlet")
public class AdminManageTabyouinServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, "GET");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, "POST");
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, String method) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // ログインチェックと管理者ロールチェック (管理者ロールを3と仮定)
        if (session == null || session.getAttribute("loggedInUser") == null ||
            ((EmployeeBean) session.getAttribute("loggedInUser")).getRole() != 3) {
            response.sendRedirect("LoginServlet");
            return;
        }

        String action = request.getParameter("action");
        TabyouinDAO dao = new TabyouinDAO();
        String forwardPage = "/WEB-INF/jsp/admin_list_tabyouin.jsp"; // デフォルトのフォワード先
        String errorMessage = null;
        String successMessage = null;

        // 電話番号変更成功時のメッセージをセッションから取得 (PRGパターン用)
        if (session.getAttribute("tabyouinTelUpdateSuccessMsg") != null) {
            successMessage = (String) session.getAttribute("tabyouinTelUpdateSuccessMsg");
            session.removeAttribute("tabyouinTelUpdateSuccessMsg");
        }
        if (successMessage != null) request.setAttribute("listSuccessMessage_tabyouin", successMessage);


        if ("showTelChangeForm".equals(action) && "GET".equals(method)) {
            String tabyouinId = request.getParameter("tabyouinId");
            TabyouinBean tabyouin = dao.getTabyouinById(tabyouinId);
            if (tabyouin != null) {
                request.setAttribute("tabyouinToChange", tabyouin);
                // String sourceList = request.getParameter("sourceList"); // どのリストから来たかの情報
                // request.setAttribute("sourceList", sourceList);
                forwardPage = "/WEB-INF/jsp/admin_change_tabyouin_tel_form.jsp";
            } else {
                errorMessage = "指定された他病院ID「" + tabyouinId + "」が見つかりません。";
                request.setAttribute("listErrorMessage_tabyouin", errorMessage);
                // listTabyouin(request, response, dao, null, null); // エラー時は一覧表示メソッド呼び出し
                // return;
            }
        } else if ("updateTel".equals(action) && "POST".equals(method)) {
            String tabyouinId = request.getParameter("tabyouinIdToChange");
            String newTel = request.getParameter("newTel");
            // String sourceListForTelUpdate = request.getParameter("sourceList");

            if (newTel == null || newTel.trim().isEmpty()) {
                errorMessage = "新しい電話番号を入力してください。";
            } else if (!newTel.trim().matches("^[0-9\\-()]{10,15}$")) {
                errorMessage = "電話番号の形式が正しくありません。(例: 000-0000-0000)";
            }

            if (errorMessage != null) {
                TabyouinBean tabyouin = dao.getTabyouinById(tabyouinId); // 再表示用に取得
                request.setAttribute("tabyouinToChange", tabyouin);
                request.setAttribute("errorMessage_telChange", errorMessage);
                // request.setAttribute("sourceList", sourceListForTelUpdate);
                forwardPage = "/WEB-INF/jsp/admin_change_tabyouin_tel_form.jsp";
            } else {
                boolean dbSuccess = dao.updateTabyouinTel(tabyouinId, newTel.trim());
                if (dbSuccess) {
                    session.setAttribute("tabyouinTelUpdateSuccessMsg", "他病院ID: " + tabyouinId + " の電話番号を変更しました。");
                    response.sendRedirect("AdminManageTabyouinServlet"); // PRGパターンで一覧へリダイレクト
                    return; // リダイレクトしたらメソッド終了
                } else {
                    errorMessage = "電話番号の変更に失敗しました。";
                    TabyouinBean tabyouin = dao.getTabyouinById(tabyouinId);
                    request.setAttribute("tabyouinToChange", tabyouin);
                    request.setAttribute("errorMessage_telChange", errorMessage);
                    // request.setAttribute("sourceList", sourceListForTelUpdate);
                    forwardPage = "/WEB-INF/jsp/admin_change_tabyouin_tel_form.jsp";
                }
            }
        }

        // --- 一覧表示または検索処理 (actionなし、または "searchAddress", "searchCapital") ---
        // "updateTel" や "showTelChangeForm" 以外のアクション、またはaction指定なしの場合に実行
        if (!"showTelChangeForm".equals(action) && !"updateTel".equals(action) ) {
             listTabyouin(request, dao); //検索と一覧表示処理
        }

        request.getRequestDispatcher(forwardPage).forward(request, response);
    }

    private void listTabyouin(HttpServletRequest request, TabyouinDAO dao) {
        List<TabyouinBean> tabyouinList;
        String searchAddress = request.getParameter("searchAddress");
        String minCapitalStr = request.getParameter("minCapital");
        String currentAction = request.getParameter("action"); // actionパラメータを保持
        String errorMessage = null; // このスコープのエラーメッセージ

        if ("searchAddress".equals(currentAction) && searchAddress != null) {
            tabyouinList = dao.searchTabyouinByAddress(searchAddress.trim());
            request.setAttribute("searchedAddress", searchAddress);
            request.setAttribute("isSearchResult", true);
        } else if ("searchCapital".equals(currentAction) && minCapitalStr != null && !minCapitalStr.trim().isEmpty()) {
            request.setAttribute("isSearchResult", true);
            try {
                String normalizedNumberStr = normalizeNumberString(minCapitalStr.trim());
                int capital = Integer.parseInt(normalizedNumberStr);
                if (capital < 0) {
                    errorMessage = "資本金は0以上の値を入力してください。";
                    tabyouinList = dao.getAllTabyouin(); // エラー時は全件
                } else {
                    tabyouinList = dao.searchTabyouinByCapital(capital);
                    request.setAttribute("searchedMinCapital", capital);
                }
                request.setAttribute("minCapitalInput", minCapitalStr);
            } catch (NumberFormatException | java.text.ParseException e) {
                errorMessage = "資本金は有効な数値で入力してください。";
                request.setAttribute("minCapitalInput", minCapitalStr);
                tabyouinList = dao.getAllTabyouin(); // エラー時は全件
            }
        } else { // デフォルトは全件表示
            tabyouinList = dao.getAllTabyouin();
            request.setAttribute("isSearchResult", false);
        }
        if(errorMessage != null) request.setAttribute("listErrorMessage_tabyouin", errorMessage);
        request.setAttribute("tabyouinList", tabyouinList);
    }


    // 資本金パース用の normalizeNumberString メソッド (ICU4Jを使用)
    private String normalizeNumberString(String numberStr) throws java.text.ParseException {
        if (numberStr == null) return null;
        String processedStr = numberStr.replace("¥", "").replace("￥", "").replace("\\", "").replace("，","");
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.JAPAN); // または適切なロケール
        Number num = nf.parse(processedStr);
        return String.valueOf(num.intValue());
    }
}