package servlet;

@WebServlet("/DoctorPrescriptionServlet")
public class DoctorPrescriptionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("selectConsultation".equals(action)) {
            // ステップ1: 患者の確定診断済み診察リストを表示
            int patientId = Integer.parseInt(request.getParameter("patId"));
            PatientDAO patientDao = new PatientDAO();
            ConsultationDAO consultationDao = new ConsultationDAO();
            
            request.setAttribute("patient", patientDao.getPatientById(String.valueOf(patientId)));
            request.setAttribute("consultationList", consultationDao.findCompletedConsultationsByPatientId(patientId));
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_select_consultation.jsp").forward(request, response);

        } else if ("showPrescriptionForm".equals(action)) {
            // ステップ2: 選択された診察に対する処方入力画面を表示
            int consultationId = Integer.parseInt(request.getParameter("consultationId"));
            ConsultationDAO consultationDao = new ConsultationDAO();
            TreatmentDAO treatmentDao = new TreatmentDAO();
            
            request.setAttribute("consultation", consultationDao.getConsultationById(consultationId));
            request.setAttribute("prescribedList", treatmentDao.getTreatmentsByConsultationId(consultationId));
            request.setAttribute("medicineList", MasterDataManager.getAllMedicines()); // 薬剤マスタ
            request.getRequestDispatcher("/WEB-INF/jsp/doctor_prescription_form.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("addPrescription".equals(action)) {
            // ステップ3: 処方をDBに登録
            int consultationId = Integer.parseInt(request.getParameter("consultationId"));
            String medicineId = request.getParameter("medicineId");
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            TreatmentDAO treatmentDao = new TreatmentDAO();
            treatmentDao.addTreatment(consultationId, medicineId, quantity);
            
            // 登録後、同じ処方入力画面にリダイレクトして結果を反映
            response.sendRedirect("DoctorPrescriptionServlet?action=showPrescriptionForm&consultationId=" + consultationId);
        }
    }
}
