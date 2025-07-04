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
        PatientDAO patientDao = new PatientDAO();

        // ★手順1: 患者に未完了の診察があるかDBに問い合わせる
        ConsultationBean incompleteConsultation = consultationDao.findIncompleteConsultationByPatientId(patientId);
        PatientBean patient = patientDao.getPatientById(patientIdStr);
        request.setAttribute("patient", patient);

        if (incompleteConsultation != null) {
            // ★手順2: 未完了の診察がある場合 → 状況確認画面へ
            int consultationId = incompleteConsultation.getConsultationId();
            
            // 関連する指示情報を取得
            XrayOrderDAO xrayDao = new XrayOrderDAO();
            XrayOrderBean xrayOrder = xrayDao.findByConsultationId(consultationId);

            LabTestOrderDAO labDao = new LabTestOrderDAO();
            LabTestOrderBean labOrder = labDao.findParentOrderByConsultationId(consultationId);
            
            if (labOrder != null) {
                // 検査項目リストを取得し、マスタ情報（検査名など）を補完する
                List<LabTestItemBean> items = labDao.getLabTestItemsByOrderId(labOrder.getLabTestOrderId());
                for (LabTestItemBean item : items) {
                    LabTestBean masterData = MasterDataManager.findLabTestByCode(item.getTestCode());
                    if (masterData != null) {
                        item.setTestName(masterData.getJlacTestName());
                        item.setUnit(masterData.getUnit());
                        item.setReferenceValue(masterData.getReferenceValue());
                    }
                }
                labOrder.setTestItems(items); // 親オーダーBeanに項目リストをセット
            }

            // 取得した情報をリクエスト属性にセット
            request.setAttribute("consultation", incompleteConsultation);
            request.setAttribute("xrayOrder", xrayOrder);
            request.setAttribute("labOrder", labOrder);
            
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_consultation_status.jsp").forward(request, response);

        } else {
            // ★手順3: 未完了の診察がない場合 → 新規指示フォームへ
            List<LabTestBean> labTestList = MasterDataManager.getAllLabTests();
            request.setAttribute("labTestList", labTestList);
            
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_consultation_form.jsp").forward(request, response);
        }
    }

    // doPostメソッドは、新規指示の登録処理や、疾病名の登録処理などを今後実装
}
