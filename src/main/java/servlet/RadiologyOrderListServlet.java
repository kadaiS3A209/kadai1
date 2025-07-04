package controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.XrayOrderDAO;

// ★URLパターンを "/Radiology..." とすることで、AuthenticationFilterの保護対象になります
@WebServlet("/RadiologyOrderListServlet")
public class RadiologyOrderListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 認証・認可フィルタでレントゲン技師(ロールID 4)であることはチェック済みと想定
        
        XrayOrderDAO dao = new XrayOrderDAO();
        List<Map<String, Object>> orderList = dao.getPendingXrayOrders();
        
        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("/WEB-INF/jsp/radiology_order_list.jsp").forward(request, response);
    }
}
