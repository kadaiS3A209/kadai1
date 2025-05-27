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

import dao.ShiiregyoshaDAO;
import model.ShiiregyoshaBean;

/**
 * Servlet implementation class AdminListSuppliersServlet
 */
@WebServlet("/AdminListSuppliersServlet") // URLマッピングは変更なし
public class AdminListSuppliersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // POST時の文字化け対策

        ShiiregyoshaDAO dao = new ShiiregyoshaDAO();
        List<ShiiregyoshaBean> supplierList = null;
        String errorMessage = null;
        String minCapitalStr = request.getParameter("minCapital"); // 検索条件を取得
        Integer searchedMinCapital = null; // 検索に使った資本金を保持するためInteger型

        try {
            if (minCapitalStr != null && !minCapitalStr.trim().isEmpty()) {
                // --- 資本金検索が指定された場合 ---
                request.setAttribute("isSearchResult", true); // 検索結果であることをJSPに伝えるフラグ
                try {
                    String normalizedNumberStr = normalizeNumberString(minCapitalStr.trim());
                    int capital = Integer.parseInt(normalizedNumberStr);

                    if (capital < 0) {
                        errorMessage = "資本金は0以上の値を入力してください。";
                        // エラー時は全件表示に戻すか、検索条件を保持したままフォームを表示するか選択
                        // ここでは検索条件を保持し、エラーメッセージと共にフォームに戻すことを想定
                        request.setAttribute("minCapitalInput", minCapitalStr); // 入力された文字列をそのまま戻す
                    } else {
                        supplierList = dao.searchSuppliersByCapital(capital);
                        searchedMinCapital = capital; // 正常にパースできた検索値を保持
                        request.setAttribute("minCapitalInput", minCapitalStr); // 検索フォームに値を再表示するため
                    }
                } catch (NumberFormatException e) {
                    errorMessage = "資本金は有効な数値で入力してください。";
                    request.setAttribute("minCapitalInput", minCapitalStr);
                } catch (java.text.ParseException e) { // ICU4JのParseException
                    e.printStackTrace();
                    errorMessage = "資本金の入力値の解析に失敗しました。";
                    request.setAttribute("minCapitalInput", minCapitalStr);
                }
            } else {
                // --- 検索条件がない場合 (または空の場合) は全件表示 ---
                supplierList = dao.getAllSuppliers();
                request.setAttribute("isSearchResult", false);
            }

            if (supplierList == null && errorMessage == null) { // DAOがエラー時にnullを返す設計で、他のエラーもない場合
                errorMessage = "仕入先情報の取得中にエラーが発生しました。";
            }

        } catch (Exception e) { // その他の予期せぬ例外 (DAO内で発生しうるRuntimeExceptionなど)
            e.printStackTrace(); // ログ記録
            errorMessage = "データベース処理中に予期せぬエラーが発生しました。";
        }

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        request.setAttribute("supplierList", supplierList);
        if (searchedMinCapital != null) {
             request.setAttribute("searchedMinCapital", searchedMinCapital); // 検索条件もJSPへ
        }

        request.getRequestDispatcher("/WEB-INF/jsp/admin_list_suppliers.jsp").forward(request, response);
    }

    // 前回の回答にあった normalizeNumberString メソッド (ICU4Jを使用)
    private String normalizeNumberString(String numberStr) throws java.text.ParseException {
        if (numberStr == null) return null;
        String processedStr = numberStr.replace("¥", "").replace("￥", "").replace("\\", "").replace("，","");
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.JAPAN); // または適切なロケール
        Number num = nf.parse(processedStr);
        return String.valueOf(num.intValue());
    }
}
