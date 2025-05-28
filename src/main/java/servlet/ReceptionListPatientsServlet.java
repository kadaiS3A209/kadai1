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
        boolean listExpiredOnlyPrimitive = "true".equals(showExpiredOnlyParam); // P104 (boolean型)
        Boolean listExpiredOnlyWrapper = listExpiredOnlyPrimitive; // オートボクシング、または Boolean.valueOf(listExpiredOnlyPrimitive);

        PatientDAO dao = new PatientDAO();
        // ★★★ 修正箇所 ★★★
        // 第1引数に患者ID検索用の値 (今回はnull) を追加
        List<PatientBean> patientList = dao.getPatients(null, searchName, listExpiredOnlyWrapper);

        request.setAttribute("patientList", patientList);
        request.setAttribute("searchedName", searchName);
        request.setAttribute("showExpiredOnlyChecked", listExpiredOnlyPrimitive); // JSPでのチェックボックス表示用にはbooleanのままが良い

        request.getRequestDispatcher("/WEB-INF/jsp/reception_list_patients.jsp").forward(request, response);
    }
}
