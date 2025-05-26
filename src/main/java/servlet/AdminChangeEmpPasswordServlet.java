package servlet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import controller.PasswordUtils;
import dao.EmployeeDAO;
import model.EmployeeBean;

/**
 * Servlet implementation class AdminChangeEmpPasswordServlet
 */
@WebServlet("/AdminChangeEmpPasswordServlet")
public class AdminChangeEmpPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String empId = request.getParameter("empId");

        if ("showForm".equals(action) && empId != null && !empId.isEmpty()) {
            EmployeeDAO dao = new EmployeeDAO();
            EmployeeBean employee = dao.getEmployeeById(empId);

            if (employee != null) {
                request.setAttribute("employeeToChange", employee);
                request.getRequestDispatcher("/WEB-INF/jsp/admin_change_emp_password_form.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "指定された従業員IDが見つかりません。");
                // Redirect or forward to an error page or the employee list
                request.getRequestDispatcher("AdminListEmployeesServlet").forward(request, response);
            }
        } else {
            // Invalid action or missing empId
            response.sendRedirect("AdminListEmployeesServlet");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String empId = request.getParameter("empIdToChange"); // From hidden field
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String formActionMessage = null;
        String formErrorMessage = null;

        EmployeeDAO dao = new EmployeeDAO();
        EmployeeBean employeeToChange = dao.getEmployeeById(empId); // For displaying info again if error

        // Validation
        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            formErrorMessage = "新しいパスワードと確認用パスワードの両方を入力してください。"; // Test case: empty fields [cite: 2]
        } else if (!newPassword.equals(confirmPassword)) {
            formErrorMessage = "新しいパスワードと確認用パスワードが一致しません。"; // E103 requirement [cite: 30]
        }

        if (formErrorMessage != null) {
            request.setAttribute("errorMessage", formErrorMessage);
            request.setAttribute("employeeToChange", employeeToChange); // Keep displaying whose password it is
            request.getRequestDispatcher("/WEB-INF/jsp/admin_change_emp_password_form.jsp").forward(request, response);
            return;
        }

        try {
            byte[] saltBytes = PasswordUtils.generateSalt();
            byte[] hashedPasswordBytes = PasswordUtils.hashPassword(newPassword.toCharArray(), saltBytes);
            String saltHex = PasswordUtils.toHexString(saltBytes);
            String hashedPasswordHex = PasswordUtils.toHexString(hashedPasswordBytes);

            boolean success = dao.updatePassword(empId, hashedPasswordHex, saltHex);

            if (success) {
                formActionMessage = "従業員ID: " + empId + " のパスワードを変更しました。"; // Test case: "「変更しました」と表示される" [cite: 2]
                request.setAttribute("successMessage", formActionMessage);
            } else {
                formErrorMessage = "パスワードの変更に失敗しました。データベースエラーが発生した可能性があります。";
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            formErrorMessage = "パスワードのハッシュ化中にエラーが発生しました。";
        }

        request.setAttribute("employeeToChange", employeeToChange); // To show whose password was (or attempted to be) changed
        if(formErrorMessage != null) request.setAttribute("errorMessage", formErrorMessage);
        // Forward back to the form to display success or error message
        request.getRequestDispatcher("/WEB-INF/jsp/admin_change_emp_password_form.jsp").forward(request, response);
    }
}
