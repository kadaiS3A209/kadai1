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
import java.sql.Connection; // ★追加
import java.sql.SQLException; // ★追加
import dao.DBManager; // ★追加

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

        EmployeeBean doctor = (EmployeeBean) session.getAttribute("loggedInUser");
        int doctorId = Integer.parseInt(doctor.getEmpid());

        int patientId = Integer.parseInt(request.getParameter("patientId"));
        boolean xrayOrdered = "true".equals(request.getParameter("xrayOrder"));
        String[] selectedTestCodes = request.getParameterValues("testCodes");

        // ★要件: 「レントゲン撮影」「検査指示」が必ず付くものとする
        if (!xrayOrdered || selectedTestCodes == null || selectedTestCodes.length == 0) {
            request.setAttribute("errorMessage_consultation", "レントゲン撮影と、1つ以上の検査指示の両方が必須です。");
            // エラーメッセージと共にフォームを再表示するために必要なデータを再セット
            request.setAttribute("patient", new PatientDAO().getPatientById(String.valueOf(patientId)));
            request.setAttribute("labTestList", MasterDataManager.getAllLabTests());
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_consultation_form.jsp").forward(request, response);
            return;
        }

        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false); // ★トランザクション開始

            ConsultationDAO consultationDao = new ConsultationDAO();
            XrayOrderDAO xrayOrderDao = new XrayOrderDAO();
            LabTestOrderDAO labTestOrderDao = new LabTestOrderDAO();
            
            // 1. 新しい診察レコードを作成
            int newConsultationId = consultationDao.createConsultation(patientId, doctorId, con);
            if (newConsultationId == -1) throw new SQLException("診察レコードの作成に失敗しました。");

            // 2. レントゲン指示を登録
            if (!xrayOrderDao.createXrayOrder(newConsultationId, con)) {
                throw new SQLException("レントゲン指示の作成に失敗しました。");
            }
            
            // 3. 検査指示を登録
            int newLabTestOrderId = labTestOrderDao.createLabTestOrder(newConsultationId, con);
            if (newLabTestOrderId == -1) throw new SQLException("検査指示の作成に失敗しました。");

            // 4. 検査項目を登録
            if (!labTestOrderDao.createLabTestItems(newLabTestOrderId, selectedTestCodes, con)) {
                throw new SQLException("検査項目の作成に失敗しました。");
            }

            con.commit(); // ★全ての処理が成功したらコミット
            session.setAttribute("message_doctor_menu", "患者ID: " + patientId + " の指示を登録しました。");
            response.sendRedirect("ReturnToMenuServlet");

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // ★エラーが発生したらロールバック
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            request.setAttribute("errorMessage_consultation", "指示の登録中にデータベースエラーが発生しました。");
            request.setAttribute("patient", new PatientDAO().getPatientById(String.valueOf(patientId)));
            request.setAttribute("labTestList", MasterDataManager.getAllLabTests());
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_consultation_form.jsp").forward(request, response);
        } finally {
            try {
                if (con != null) con.setAutoCommit(true); // 自動コミットモードに戻す
                if (con != null) con.close(); // コネクションを閉じる
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
