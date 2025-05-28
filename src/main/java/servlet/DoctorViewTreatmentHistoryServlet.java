package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.TreatmentDAO;
import model.EmployeeBean;
import model.TreatmentHistoryViewBean;

/**
 * Servlet implementation class A
 */
@WebServlet("/DoctorViewTreatmentHistoryServlet")
public class DoctorViewTreatmentHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GETリクエストは患者ID入力フォームを表示
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null ||
            ((EmployeeBean) session.getAttribute("loggedInUser")).getRole() != 2) { // 医師ロールを2と仮定
            response.sendRedirect("LoginServlet");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/jsp/doctor_select_patient_for_history.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null ||
            ((EmployeeBean) session.getAttribute("loggedInUser")).getRole() != 2) {
            response.sendRedirect("LoginServlet");
            return;
        }

        String action = request.getParameter("action");
        String patientId = request.getParameter("patientIdForHistory");

        if ("viewHistory".equals(action)) {
            if (patientId == null || patientId.trim().isEmpty()) {
                request.setAttribute("errorMessage_selectPatient", "患者IDを入力してください。");
                request.getRequestDispatcher("/WEB-INF/jsp/doctor_select_patient_for_history.jsp").forward(request, response);
                return;
            }

            TreatmentDAO dao = new TreatmentDAO();
            List<TreatmentHistoryViewBean> historyList = dao.getTreatmentHistoryByPatientId(patientId.trim());

            // 患者名を取得して表示するため（リストが空でも患者名は表示したい場合）
            // もしTreatmentHistoryViewBeanにpatientNameがセットされていれば、最初の要素から取るか、別途PatientDAOで取得
            String displayPatientName = "";
            if (!historyList.isEmpty()) {
                displayPatientName = historyList.get(0).getPatientName(); // 最初の履歴アイテムから患者名を取得
            } else {
                // 履歴がない場合でも患者名は表示したいなら、PatientDAOで取得
                // com.example.dao.PatientDAO patientDao = new com.example.dao.PatientDAO();
                // com.example.model.PatientBean patient = patientDao.getPatientById(patientId.trim());
                // if (patient != null) {
                //     displayPatientName = patient.getPatLname() + " " + patient.getPatFname();
                // } else {
                //     displayPatientName = "ID: " + patientId + " (患者情報なし)";
                // }
                 // historyListが空の場合、TreatmentHistoryViewBeanにpatientNameは入らないので、
                 // 患者IDだけをJSPに渡して「患者ID: XXX の処置履歴」と表示させるか、
                 // PatientDAOで別途患者情報を取得する必要がある。
                 // ここでは、historyListが空＝患者情報なし、とは限らないので、IDのみ渡す。
            }

            request.setAttribute("treatmentHistoryList", historyList);
            request.setAttribute("searchedPatientIdForHistory", patientId.trim());
            request.setAttribute("displayPatientNameForHistory", displayPatientName); // リストが空でも患者名を表示する場合
            
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_treatment_history_view.jsp").forward(request, response);

        } else {
            // 不明なアクションの場合は入力フォームへ
            response.sendRedirect("DoctorViewTreatmentHistoryServlet");
        }
    }
}
