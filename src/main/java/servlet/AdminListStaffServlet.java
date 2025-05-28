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
 * Servlet implementation class AdminListEmployeesServlet
 */
@WebServlet("/AdminListStaffServlet")
public class AdminListStaffServlet extends HttpServlet {
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
        String searchEmpId = request.getParameter("searchEmpId");

        // 受付 (ロールID: 1) と 医師 (ロールID: 2) のみを対象とする
        List<Integer> staffRoles = Arrays.asList(1, 2);
        List<EmployeeBean> staffList = dao.getEmployees(staffRoles, searchEmpId);

        request.setAttribute("staffList", staffList);
        if (searchEmpId != null) {
            request.setAttribute("searchedEmpId", searchEmpId);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin_list_staff.jsp").forward(request, response);
    }
}
