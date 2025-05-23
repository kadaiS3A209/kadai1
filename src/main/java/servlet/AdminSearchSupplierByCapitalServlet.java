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
 * Servlet implementation class AdminSearchSupplierByCapitalServlet
 */
@WebServlet("/AdminSearchSupplierByCapitalServlet")
public class AdminSearchSupplierByCapitalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GETリクエストは検索フォームを表示
        request.getRequestDispatcher("/WEB-INF/jsp/admin_search_supplier_capital_form.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String minCapitalStr = request.getParameter("minCapital");
        ShiiregyoshaDAO dao = new ShiiregyoshaDAO();
        List<ShiiregyoshaBean> supplierList = null;
        String errorMessage = null;
        int minCapital = 0;

        if (minCapitalStr == null || minCapitalStr.trim().isEmpty()) {
            errorMessage = "検索する資本金額を入力してください。";
        } else {
            try {
                // 全角数字・カンマ・円記号対応のパース処理
                String normalizedNumberStr = normalizeNumberString(minCapitalStr.trim());
                minCapital = Integer.parseInt(normalizedNumberStr);

                if (minCapital < 0) {
                    errorMessage = "資本金は0以上の値を入力してください。";
                } else {
                    supplierList = dao.searchSuppliersByCapital(minCapital);
                }
            } catch (NumberFormatException e) {
                // テストケース「資本金に数字・(カンマ)以外の半角文字を入力し検索する」などに対応 [cite: 4, 6]
                errorMessage = "資本金は有効な数値で入力してください。";
            } catch (Exception e) { // その他の予期せぬパースエラー
                 e.printStackTrace();
                 errorMessage = "入力値の処理中にエラーが発生しました。";
            }
        }

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/WEB-INF/jsp/admin_search_supplier_capital_form.jsp").forward(request, response);
        } else {
            request.setAttribute("supplierList", supplierList);
            request.setAttribute("searchedMinCapital", minCapital); // 検索条件も渡す
            request.getRequestDispatcher("/WEB-INF/jsp/admin_search_supplier_capital_results.jsp").forward(request, response);
        }
    }

    /**
     * 全角数字、カンマ、円記号を含む可能性のある文字列を正規化して半角数字文字列に変換します。
     * @param numberStr 入力文字列
     * @return 正規化された数値文字列
     * @throws ParseException パースに失敗した場合
     */
    private String normalizeNumberString(String numberStr) throws java.text.ParseException {
        if (numberStr == null) return null;

        // 1. "¥"記号を除去
        String processedStr = numberStr.replace("¥", "").replace("￥", "");

        // 2. ICU4Jを使用して全角数字と全角カンマを半角に、そして数値として解釈
        //    NumberFormatはロケールに依存するため、日本の数字解釈を期待する場合はLocale.JAPAN
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.JAPAN);
        Number num = nf.parse(processedStr); // カンマが含まれていてもパースできる

        // 3. パースされた数値から整数部分のみを取得
        return String.valueOf(num.intValue());
    }

}
