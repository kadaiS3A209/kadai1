package controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.LabTestOrderDAO;

// ★URLパターンを "/Lab..." とすることで、AuthenticationFilterの保護対象になります
@WebServlet("/LabOrderListServlet")
public class LabOrderListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 認証・認可フィルタで臨床検査技師(例:ロールID 5)であることはチェック済みと想定
        
        LabTestOrderDAO dao = new LabTestOrderDAO();
        List<Map<String, Object>> orderList = dao.getPendingLabTestOrders();
        
        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("/WEB-INF/jsp/lab_order_list.jsp").forward(request, response);
    }
}
