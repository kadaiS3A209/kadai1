@WebServlet("/RadiologyOrderListServlet")
public class RadiologyOrderListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 認証・認可フィルタでレントゲン技師(例:ロールID 4)であることはチェック済みと想定
        XrayOrderDAO dao = new XrayOrderDAO();
        List<Map<String, Object>> orderList = dao.getPendingXrayOrders();
        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("/WEB-INF/jsp/radiology_order_list.jsp").forward(request, response);
    }
}
