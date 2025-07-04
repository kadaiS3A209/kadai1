package servlet;

import java.io.IOException;
import java.util.List;

import dao.ConsultationDAO;
import dao.PatientDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import listener.MasterDataManager;
import model.LabTestBean;
import model.PatientBean;
import model.ConsultationBean; // 作成したBeanをインポート

@WebServlet("/DoctorConsultationServlet")
public class DoctorConsultationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String patientIdStr = request.getParameter("patId");
        if (patientIdStr == null || patientIdStr.isEmpty()) {
            response.sendRedirect("DoctorListAllPatientsServlet");
            return;
        }
        int patientId = Integer.parseInt(patientIdStr);

        ConsultationDAO consultationDao = new ConsultationDAO();
        // ★手順1: 患者に未完了の診察があるかDBに問い合わせる
        ConsultationBean incompleteConsultation = consultationDao.findIncompleteConsultationByPatientId(patientId);

        if (incompleteConsultation != null) {
            // ★手順2: 未完了の診察がある場合
            System.out.println("未完了の診察が見つかりました。ID: " + incompleteConsultation.getConsultationId());
            // → 今後作成する「既存の指示状況を確認する画面」へフォワード
            // (この画面では、既存の指示の検査結果などを表示する)
            request.setAttribute("consultation", incompleteConsultation);
            // request.setAttribute("existingOrders", ...); // 既存の指示を取得する処理を後で追加
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_consultation_status.jsp").forward(request, response);

        } else {
            // ★手順3: 未完了の診察がない場合
            System.out.println("未完了の診察はありません。新規診察を開始します。");
            // → これまで通り「新しい指示を出すフォーム」へフォワード
            PatientDAO patientDao = new PatientDAO();
            PatientBean patient = patientDao.getPatientById(patientIdStr);
            request.setAttribute("patient", patient);
            
            List<LabTestBean> labTestList = MasterDataManager.getAllLabTests();
            request.setAttribute("labTestList", labTestList);
            
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_consultation_form.jsp").forward(request, response);
        }
    }

    // doPostメソッドは、新規指示の登録処理や、疾病名の登録処理などを今後実装
}
