package servlet;

import java.io.IOException;
import java.util.ArrayList;
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
@WebServlet("/AdminListEmployeesServlet")
public class AdminListEmployeesServlet extends HttpServlet {
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
        List<EmployeeBean> employeeList = new ArrayList<>();
        String searchEmpId = request.getParameter("searchEmpId");
        String errorMessage = null;

        try {
            if (searchEmpId != null && !searchEmpId.trim().isEmpty()) {
                EmployeeBean employee = dao.getEmployeeById(searchEmpId.trim());
                if (employee != null) {
                    employeeList.add(employee); // Add to list for consistent JSP handling
                } else {
                    errorMessage = "従業員ID '" + searchEmpId + "' は見つかりませんでした。";
                }
                request.setAttribute("searchedEmpId", searchEmpId); // To pre-fill search box
            } else {
                employeeList = dao.getAllEmployees();
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "従業員情報の取得中にエラーが発生しました。";
        }

        request.setAttribute("employeeList", employeeList);
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin_list_employees.jsp").forward(request, response);
    }
}
