package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.PatientDAO;
import model.PatientBean;

/**
 * Servlet implementation class Afs
 */
@WebServlet("/ReceptionListPatientsServlet")
public class ReceptionListPatientsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String searchName = request.getParameter("searchName");
        String showExpiredOnlyParam = request.getParameter("showExpiredOnly");
        boolean listExpiredOnly = "true".equals(showExpiredOnlyParam); // P104

        PatientDAO dao = new PatientDAO();
        List<PatientBean> patientList = dao.getPatients(searchName, listExpiredOnly);

        request.setAttribute("patientList", patientList);
        request.setAttribute("searchedName", searchName); // 検索フォームに値を再表示するため
        request.setAttribute("showExpiredOnlyChecked", listExpiredOnly); // チェックボックスの状態を再表示するため

        request.getRequestDispatcher("/WEB-INF/jsp/reception_list_patients.jsp").forward(request, response);
    }
}
