package servlet;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.ShiiregyoshaDAO;
import model.EmployeeBean;
import model.ShiiregyoshaBean;

/**
 * Servlet implementation class AdminListSuppliersServlet
 */
@WebServlet("/AdminListSuppliersServlet")
public class AdminListSuppliersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // doGetはprocessRequestを呼び出す
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, "GET");
    }

    // doPostもprocessRequestを呼び出す
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
        ShiiregyoshaDAO dao = new ShiiregyoshaDAO();
        List<ShiiregyoshaBean> supplierList = null;
        String errorMessage = null;
        String successMessage = null; // 電話番号変更成功メッセージ用

        // 電話番号変更成功時のメッセージをセッションから取得 (PRGパターン用)
        if (session.getAttribute("supplierTelUpdateSuccessMsg") != null) {
            successMessage = (String) session.getAttribute("supplierTelUpdateSuccessMsg");
            session.removeAttribute("supplierTelUpdateSuccessMsg"); // 一度表示したら消す
        }


        if ("showTelChangeForm".equals(action)) { // doGetからのみ来る想定
            String shiireId = request.getParameter("shiireId");
            ShiiregyoshaBean supplier = dao.getShiiregyoshaById(shiireId);
            if (supplier != null) {
                request.setAttribute("supplierToChange", supplier);
                String sourceList = request.getParameter("sourceList"); // どのリストから来たか
                request.setAttribute("sourceList", sourceList);
                request.getRequestDispatcher("/WEB-INF/jsp/admin_change_supplier_tel_form.jsp").forward(request, response);
                return; // JSPへフォワードしたらメソッド終了
            } else {
                errorMessage = "指定された仕入先ID「" + shiireId + "」が見つかりません。";
                // フォールバックして一覧表示
            }
        } else if ("updateTel".equals(action) && "POST".equals(method)) { // doPostからのみ来る想定
            String shiireId = request.getParameter("shiireIdToChange");
            String newTel = request.getParameter("newTel");
            String sourceListForTelUpdate = request.getParameter("sourceList"); // 戻り先情報

            if (newTel == null || newTel.trim().isEmpty()) {
                errorMessage = "新しい電話番号を入力してください。";
            } else if (!newTel.trim().matches("^[0-9\\-()]{10,15}$")) { // 少し緩めの電話番号形式チェック
                errorMessage = "電話番号の形式が正しくありません。(例: 000-0000-0000)";
            }

            if (errorMessage != null) {
                ShiiregyoshaBean supplier = dao.getShiiregyoshaById(shiireId);
                request.setAttribute("supplierToChange", supplier);
                request.setAttribute("errorMessage_telChange", errorMessage);
                request.setAttribute("sourceList", sourceListForTelUpdate);
                request.getRequestDispatcher("/WEB-INF/jsp/admin_change_supplier_tel_form.jsp").forward(request, response);
                return;
            } else {
                boolean dbSuccess = dao.updateShiiregyoshaTel(shiireId, newTel.trim());
                if (dbSuccess) {
                    session.setAttribute("supplierTelUpdateSuccessMsg", "仕入先ID: " + shiireId + " の電話番号を変更しました。");
                    // PRGパターンで一覧表示サーブレットにリダイレクト (sourceListも引き継ぐ)
                    String redirectUrl = "AdminListSuppliersServlet";
                    if (sourceListForTelUpdate != null && !sourceListForTelUpdate.isEmpty()) {
                        // 必要であれば、検索条件などを復元するためにsourceListの値を元にパラメータを追加
                        // 例: if ("searchAddressResult".equals(sourceListForTelUpdate) && session.getAttribute("lastSearchAddress") != null) {
                        // redirectUrl += "?searchAddress=" + java.net.URLEncoder.encode((String)session.getAttribute("lastSearchAddress"), "UTF-8");
                        // }
                    }
                    response.sendRedirect(redirectUrl);
                    return;
                } else {
                    errorMessage = "電話番号の変更に失敗しました。";
                    ShiiregyoshaBean supplier = dao.getShiiregyoshaById(shiireId);
                    request.setAttribute("supplierToChange", supplier);
                    request.setAttribute("errorMessage_telChange", errorMessage);
                    request.setAttribute("sourceList", sourceListForTelUpdate);
                    request.getRequestDispatcher("/WEB-INF/jsp/admin_change_supplier_tel_form.jsp").forward(request, response);
                    return;
                }
            }
        }


        // --- 一覧表示または検索処理 ---
        String minCapitalStr = request.getParameter("minCapital");
        String searchAddress = request.getParameter("searchAddress");
        Integer searchedMinCapital = null;

        if (searchAddress != null) { // 住所検索が優先されるか、資本金検索と両立するかは要件次第
            supplierList = dao.searchShiiregyoshaByAddress(searchAddress.trim());
            request.setAttribute("searchedAddress", searchAddress);
            session.setAttribute("lastSearchAddress", searchAddress.trim()); // 検索条件をセッションに保存（電話番号変更後の戻り用）
            request.setAttribute("isSearchResult", true);
        } else if (minCapitalStr != null && !minCapitalStr.trim().isEmpty()) {
            request.setAttribute("isSearchResult", true);
            try {
                String normalizedNumberStr = normalizeNumberString(minCapitalStr.trim());
                int capital = Integer.parseInt(normalizedNumberStr);
                if (capital < 0) {
                    errorMessage = "資本金は0以上の値を入力してください。";
                    request.setAttribute("minCapitalInput", minCapitalStr);
                    supplierList = dao.getAllSuppliers(); // エラー時は全件表示
                } else {
                    supplierList = dao.searchSuppliersByCapital(capital);
                    searchedMinCapital = capital;
                    request.setAttribute("minCapitalInput", minCapitalStr);
                }
            } catch (NumberFormatException | java.text.ParseException e) {
                errorMessage = "資本金は有効な数値で入力してください。";
                request.setAttribute("minCapitalInput", minCapitalStr);
                supplierList = dao.getAllSuppliers(); // エラー時は全件表示
            }
            session.removeAttribute("lastSearchAddress"); // 他の検索をしたらクリア
        } else {
            supplierList = dao.getAllSuppliers();
            request.setAttribute("isSearchResult", false);
            session.removeAttribute("lastSearchAddress");
        }

        if (errorMessage != null) request.setAttribute("listErrorMessage_supplier", errorMessage);
        if (successMessage != null) request.setAttribute("listSuccessMessage_supplier", successMessage); // 電話番号変更成功メッセージ

        request.setAttribute("supplierList", supplierList);
        if (searchedMinCapital != null) {
             request.setAttribute("searchedMinCapital", searchedMinCapital);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/admin_list_suppliers.jsp").forward(request, response);
    }

    // 資本金パース用の normalizeNumberString メソッド (ICU4Jを使用)
    private String normalizeNumberString(String numberStr) throws java.text.ParseException {
        if (numberStr == null) return null;
        String processedStr = numberStr.replace("¥", "").replace("￥", "").replace("\\", "").replace("，","");
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.JAPAN);
        Number num = nf.parse(processedStr);
        return String.valueOf(num.intValue());
    }
}
