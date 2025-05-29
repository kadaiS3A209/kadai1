package servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.EmployeeDAO;
import model.EmployeeBean;

/**
 * Servlet implementation class AdminListAdministratorsServlet
 */
@WebServlet("/AdminListAdministratorsServlet")
public class AdminListAdministratorsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        EmployeeDAO dao = new EmployeeDAO();
        String searchAdminId = request.getParameter("searchAdminId");

        // 管理者 (ロールID: 0) のみを対象とする
        List<Integer> adminRoles = Arrays.asList(3); // 管理者のロールIDを0と仮定
        List<EmployeeBean> adminList = dao.getEmployees(adminRoles, searchAdminId);

        request.setAttribute("adminList", adminList);
        if (searchAdminId != null) {
            request.setAttribute("searchedAdminId", searchAdminId);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin_list_administrators.jsp").forward(request, response);
    }
}
