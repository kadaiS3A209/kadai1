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
import model.EmployeeBean; // セッションから医師IDを取得するために使用

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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // ログイン・権限チェック
        if (session == null || session.getAttribute("loggedInUser") == null || ((EmployeeBean) session.getAttribute("loggedInUser")).getRole() != 2) {
            response.sendRedirect("LoginServlet");
            return;
        }

        String action = request.getParameter("action");

        if ("registerDisease".equals(action)) {
            // フォームから送信されたデータを取得
            int consultationId = Integer.parseInt(request.getParameter("consultationId"));
            String diseaseCode = request.getParameter("diseaseCode");

            // バリデーション: 疾病名が選択されているか
            if (diseaseCode == null || diseaseCode.trim().isEmpty()) {
                request.setAttribute("formError_disease", "疾病名を選択してください。");
                
                // ★エラーメッセージと共に、状況確認画面を再表示するためのデータを再度取得・セット
                // (doGetのロジックを再利用するか、ヘルパーメソッド化するのが望ましい)
                // ここでは、簡単のため主要な情報のみを再セットする例を示します。
                // この部分はdoGetのロジックを参考に、必要な情報を全てセットしてください。
                ConsultationDAO consultationDao = new ConsultationDAO();
                request.setAttribute("consultation", consultationDao.getConsultationById(consultationId)); // consultationIdで診察情報を再取得するメソッドがDAOに必要
                request.setAttribute("xrayOrder", new XrayOrderDAO().findByConsultationId(consultationId));
                request.setAttribute("labOrder", new LabTestOrderDAO().findParentOrderByConsultationId(consultationId));
                //...
                request.getRequestDispatcher("/WEB-INF/jsp/doctor_consultation_status.jsp").forward(request, response);
                return;
            }

            // DAOを呼び出してデータベースを更新
            ConsultationDAO consultationDao = new ConsultationDAO();
            // 診察のステータスを「完了」にし、疾病コードを登録
            boolean success = consultationDao.updateDiseaseAndStatus(consultationId, diseaseCode, "完了");

            if (success) {
                session.setAttribute("message_doctor_menu", "診察(ID: " + consultationId + ")を完了しました。");
                response.sendRedirect("ReturnToMenuServlet");
            } else {
                request.setAttribute("formError_disease", "データベースの更新に失敗しました。");
                // エラー時も同様に、状況確認画面を再表示するためのデータ準備が必要
                request.getRequestDispatcher("/WEB-INF/jsp/doctor_consultation_status.jsp").forward(request, response);
            }
        }
        // 他のPOSTアクションがあれば、ここに else if を追加
    }


    // doPostメソッドは、新規指示の登録処理や、疾病名の登録処理などを今後実装
}
