package servlet;

import java.io.IOException;
import java.util.List;

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
@WebServlet("/AdminListSuppliersServlet")
public class AdminListSuppliersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ShiiregyoshaDAO dao = new ShiiregyoshaDAO();
        List<ShiiregyoshaBean> supplierList = null;
        String errorMessage = null;

        try {
            supplierList = dao.getAllSuppliers();
            if (supplierList == null) { // DAOがエラー時にnullを返す設計の場合
                errorMessage = "仕入先情報の取得中にエラーが発生しました。";
            }
        } catch (Exception e) { // DAO内で例外をスローする設計の場合
            e.printStackTrace(); // ログ記録
            errorMessage = "データベース接続エラーが発生しました。";
        }

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
             // テストケース「仕入れ先一覧表示不良」: エラーメッセージが表示される
        }
        request.setAttribute("supplierList", supplierList);
        // JSPは WEB-INF 配下と想定
        request.getRequestDispatcher("/WEB-INF/jsp/admin_list_suppliers.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 一覧表示は通常GETで行うため、POSTリクエストもdoGetに流す
        doGet(request, response);
    }
}
