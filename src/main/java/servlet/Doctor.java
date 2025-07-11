package servlet;

import java.io.IOException;
import java.util.List;

import dao.ConsultationDAO;
import dao.PatientDAO;
import dao.TreatmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import listener.MasterDataManager;
import model.ConsultationBean;
import model.MedicineBean; // 薬剤マスタ用のBean (要作成)
import model.PatientBean;
import model.TreatmentBean;

@WebServlet("/DoctorPrescriptionServlet")
public class DoctorPrescriptionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "selectConsultation"; // デフォルトのアクション
        }

        switch (action) {
            case "selectConsultation":
                showSelectConsultationPage(request, response);
                break;
            case "showPrescriptionForm":
                showPrescriptionFormPage(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "無効なアクションです。");
                break;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("addPrescription".equals(action)) {
            addPrescription(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "無効なアクションです。");
        }
    }

    private void showSelectConsultationPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int patientId = Integer.parseInt(request.getParameter("patId"));
            PatientDAO patientDao = new PatientDAO();
            ConsultationDAO consultationDao = new ConsultationDAO();

            PatientBean patient = patientDao.getPatientById(String.valueOf(patientId));
            List<ConsultationBean> consultationList = consultationDao.findCompletedConsultationsByPatientId(patientId);

            request.setAttribute("patient", patient);
            request.setAttribute("consultationList", consultationList);
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_select_consultation.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "患者IDが無効です。");
        }
    }

    private void showPrescriptionFormPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int consultationId = Integer.parseInt(request.getParameter("consultationId"));
            ConsultationDAO consultationDao = new ConsultationDAO();
            TreatmentDAO treatmentDao = new TreatmentDAO();

            // JSPで患者名や疾病名を表示するために診察情報を取得
            ConsultationBean consultation = consultationDao.getConsultationById(consultationId);
            // MasterDataManagerから補完した方が良い
            if(consultation.getDiseaseName() == null){
                 consultation.setDiseaseName(MasterDataManager.findDiseaseByCode(consultation.getDiseaseCode()).getName());
            }
            if(consultation.getPatientName() == null){
                PatientBean patient = new PatientDAO().getPatientById(String.valueOf(consultation.getPatientId()));
                consultation.setPatientName(patient.getPatLname() + " " + patient.getPatFname());
            }


            List<TreatmentBean> prescribedList = treatmentDao.getTreatmentsByConsultationId(consultationId);
            List<MedicineBean> medicineList = MasterDataManager.getAllMedicines(); // ★薬剤マスタを取得

            request.setAttribute("consultation", consultation);
            request.setAttribute("prescribedList", prescribedList);
            request.setAttribute("medicineList", medicineList);
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_prescription_form.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "診察IDが無効です。");
        }
    }

    private void addPrescription(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int consultationId = Integer.parseInt(request.getParameter("consultationId"));
            String medicineId = request.getParameter("medicineId");
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            // CSRF対策やバリデーションをここに追加するのが望ましい

            TreatmentDAO treatmentDao = new TreatmentDAO();
            boolean success = treatmentDao.addTreatment(consultationId, medicineId, quantity);

            if(success){
                 HttpSession session = request.getSession();
                 session.setAttribute("message", "処方を追加しました。");
            }

            // PRGパターン：処理後はリダイレクト
            response.sendRedirect("DoctorPrescriptionServlet?action=showPrescriptionForm&consultationId=" + consultationId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "入力値が無効です。");
        }
    }
}
