package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.PatientDAO;
import model.EmployeeBean;
import model.PatientBean;

/**
 * Servlet implementation class SSsa
 */
@WebServlet("/DoctorListAllPatientsServlet")
public class DoctorListAllPatientsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null ||
            ((EmployeeBean) session.getAttribute("loggedInUser")).getRole() != 2) { // 医師ロールを2と仮定
            response.sendRedirect("LoginServlet");
            return;
        }

        String searchPatId = request.getParameter("searchPatId");
        String searchName = request.getParameter("searchName");
        // 医師向け一覧では、有効期限切れフィルタは現時点では不要と判断し、nullを渡します。
        // もし必要なら、受付向けと同様にパラメータで受け取ります。

        PatientDAO dao = new PatientDAO();
        List<PatientBean> patientList = dao.getPatients(searchPatId, searchName, null);

        request.setAttribute("patientList", patientList);
        request.setAttribute("searchedPatId", searchPatId); // 検索フォームに値を再表示するため
        request.setAttribute("searchedName", searchName);   // 同上

        request.getRequestDispatcher("/WEB-INF/jsp/doctor_list_all_patients.jsp").forward(request, response);
    }
}
