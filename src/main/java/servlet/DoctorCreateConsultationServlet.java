package servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import listener.MasterDataManager; // マスタデータを取得
import model.EmployeeBean;
import model.LabTestBean;
import model.PatientBean;
import dao.ConsultationDAO;
import dao.LabTestOrderDAO;
import dao.PatientDAO;
import dao.XrayOrderDAO;

@WebServlet("/DoctorCreateConsultationServlet")
public class DoctorCreateConsultationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * GETリクエストは、診察・指示フォームを表示します。
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String patientIdStr = request.getParameter("patId");
        if (patientIdStr == null || patientIdStr.isEmpty()) {
            response.sendRedirect("DoctorListAllPatientsServlet"); // 患者IDがなければ一覧へ
            return;
        }

        // 患者情報を取得
        PatientDAO patientDao = new PatientDAO();
        PatientBean patient = patientDao.getPatientById(patientIdStr);
        request.setAttribute("patient", patient);

        // 検査項目マスタを取得
        List<LabTestBean> labTestList = MasterDataManager.getAllLabTests();
        request.setAttribute("labTestList", labTestList);

        request.getRequestDispatcher("/WEB-INF/jsp/doctor_consultation_form.jsp").forward(request, response);
    }

    /**
     * POSTリクエストは、診察と指示をデータベースに登録します。
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // セッションからログイン中の医師情報を取得
        EmployeeBean doctor = (EmployeeBean) session.getAttribute("loggedInUser");
        int doctorId = Integer.parseInt(doctor.getEmpid());

        // フォームから送信されたデータを取得
        int patientId = Integer.parseInt(request.getParameter("patientId"));
        boolean xrayOrdered = "true".equals(request.getParameter("xrayOrder"));
        String[] selectedTestCodes = request.getParameterValues("testCodes");

        // ★要件: レントゲンか検査のどちらかは必ず指示する
        if (!xrayOrdered && (selectedTestCodes == null || selectedTestCodes.length == 0)) {
            request.setAttribute("errorMessage_consultation", "レントゲン撮影または検査指示のいずれか、または両方を指示してください。");
            // エラーメッセージと共にフォームを再表示するために、再度データをセット
            PatientDAO patientDao = new PatientDAO();
            request.setAttribute("patient", patientDao.getPatientById(String.valueOf(patientId)));
            request.setAttribute("labTestList", MasterDataManager.getAllLabTests());
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_consultation_form.jsp").forward(request, response);
            return;
        }

        // --- データベース登録処理 (トランザクションを考慮するのが理想) ---
        ConsultationDAO consultationDao = new ConsultationDAO();
        XrayOrderDAO xrayOrderDao = new XrayOrderDAO();
        LabTestOrderDAO labTestOrderDao = new LabTestOrderDAO();
        
        // 1. 新しい診察レコードを作成
        int newConsultationId = consultationDao.createConsultation(patientId, doctorId);

        if (newConsultationId != -1) { // 診察レコード作成成功
            // 2. レントゲン指示があれば登録
            if (xrayOrdered) {
                xrayOrderDao.createXrayOrder(newConsultationId);
            }
            // 3. 検査指示があれば登録
            if (selectedTestCodes != null && selectedTestCodes.length > 0) {
                int newLabTestOrderId = labTestOrderDao.createLabTestOrder(newConsultationId);
                if (newLabTestOrderId != -1) {
                    labTestOrderDao.createLabTestItems(newLabTestOrderId, selectedTestCodes);
                }
            }
            // 成功したら医師メニューにリダイレクト（成功メッセージを添えて）
            session.setAttribute("message_doctor_menu", "患者ID: " + patientId + " の指示を登録しました。");
            response.sendRedirect("ReturnToMenuServlet");
        } else {
            // 診察レコード作成失敗時のエラー処理
            request.setAttribute("errorMessage_consultation", "診察の開始に失敗しました。データベースエラーの可能性があります。");
            PatientDAO patientDao = new PatientDAO();
            request.setAttribute("patient", patientDao.getPatientById(String.valueOf(patientId)));
            request.setAttribute("labTestList", MasterDataManager.getAllLabTests());
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_consultation_form.jsp").forward(request, response);
        }
    }
}
