package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.XrayOrderDAO;
import model.EmployeeBean;

// ★URLパターンを "/Radiology..." とすることで、AuthenticationFilterの保護対象になります
@WebServlet("/RadiologyRegisterImageServlet")
public class RadiologyRegisterImageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * GETリクエストは、写真登録フォームを表示します。
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String xrayOrderIdStr = request.getParameter("xrayOrderId");
        if (xrayOrderIdStr == null) {
            response.sendRedirect("RadiologyOrderListServlet");
            return;
        }

        XrayOrderDAO dao = new XrayOrderDAO();
        int xrayOrderId = Integer.parseInt(xrayOrderIdStr);
        Map<String, Object> orderDetails = dao.getXrayOrderDetailsById(xrayOrderId);

        request.setAttribute("orderDetails", orderDetails);
        request.getRequestDispatcher("/WEB-INF/jsp/radiology_register_image_form.jsp").forward(request, response);
    }

    /**
     * POSTリクエストは、入力されたファイル名をデータベースに登録します。
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // ログイン中の技師IDを取得
        EmployeeBean technician = (EmployeeBean) session.getAttribute("loggedInUser");
        int technicianId = Integer.parseInt(technician.getEmpid());

        // フォームから送信されたデータを取得
        int xrayOrderId = Integer.parseInt(request.getParameter("xrayOrderId"));
        String[] fileNames = request.getParameterValues("fileName");
        
        // 空の入力を除外したリストを作成
        List<String> validFileNames = new ArrayList<>();
        if (fileNames != null) {
            for (String fileName : fileNames) {
                if (fileName != null && !fileName.trim().isEmpty()) {
                    validFileNames.add(fileName.trim());
                }
            }
        }
        
        // 1つもファイル名が入力されていない場合はエラー
        if (validFileNames.isEmpty()) {
            request.setAttribute("formError", "ファイル名を1つ以上入力してください。");
            // エラー時もフォームを再表示するために必要な情報を再度セット
            XrayOrderDAO dao = new XrayOrderDAO();
            request.setAttribute("orderDetails", dao.getXrayOrderDetailsById(xrayOrderId));
            request.getRequestDispatcher("/WEB-INF/jsp/radiology_register_image_form.jsp").forward(request, response);
            return;
        }
        
        // DAOを呼び出してDBに登録
        XrayOrderDAO dao = new XrayOrderDAO();
        boolean success = dao.completeXrayOrder(xrayOrderId, technicianId, validFileNames);
        
        if (success) {
            session.setAttribute("listSuccessMessage", "指示ID: " + xrayOrderId + " の写真登録を完了しました。");
            response.sendRedirect("RadiologyOrderListServlet");
        } else {
            request.setAttribute("formError", "登録処理中にエラーが発生しました。");
            request.setAttribute("orderDetails", dao.getXrayOrderDetailsById(xrayOrderId));
            request.getRequestDispatcher("/WEB-INF/jsp/radiology_register_image_form.jsp").forward(request, response);
        }
    }
}
