package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.MedicineDAO;
import dao.PatientDAO;
import dao.TreatmentDAO;
import model.DrugOrderItemBean;
import model.EmployeeBean;
import model.MedicineBean;
import model.PatientBean;
import model.TreatmentBean;

/**
 * Servlet implementation class DoctorDrugAdministrationServlet
 */
@WebServlet("/DoctorDrugAdministrationServlet")
public class DoctorDrugAdministrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // ログインおよびロールチェック (医師ロールを2と仮定)
        if (session == null || session.getAttribute("loggedInUser") == null ||
            ((EmployeeBean) session.getAttribute("loggedInUser")).getRole() != 2) {
            response.sendRedirect("LoginServlet"); // 不正アクセスはログインページへ
            return;
        }

        String action = request.getParameter("action");
        String patId = request.getParameter("patId");

        if ("start".equals(action) && patId != null && !patId.isEmpty()) {
            // D101 機能説明1: 対象となる患者への処置画面を表示させる。 [cite: 61]
            PatientDAO patientDAO = new PatientDAO();
            PatientBean patient = patientDAO.getPatientById(patId);

            MedicineDAO medicineDAO = new MedicineDAO();
            List<MedicineBean> allMedicines = medicineDAO.getAllMedicines(); // D101 備考: 薬剤名は一覧表示。 [cite: 61]

            if (patient != null) {
                request.setAttribute("patient", patient);
                request.setAttribute("allMedicines", allMedicines);

                // 患者ごとのカート情報をセッションから取得
                String cartSessionKey = "drugCart_" + patId;
                List<DrugOrderItemBean> drugCart = (List<DrugOrderItemBean>) session.getAttribute(cartSessionKey);
                if (drugCart == null) {
                    drugCart = new ArrayList<>(); // まだカートがなければ空リスト
                }
                request.setAttribute("drugCart", drugCart);

                request.getRequestDispatcher("/WEB-INF/jsp/doctor_drug_selection_form.jsp").forward(request, response);
            } else {
                request.setAttribute("listErrorMessage", "指定された患者IDが見つかりません。");
                request.getRequestDispatcher("DoctorListAllPatientsServlet").forward(request, response);
            }
        } else {
            response.sendRedirect("DoctorListAllPatientsServlet"); // 不正なアクセス
        }
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
        String patId = request.getParameter("patientId"); // hidden fieldから取得

        if (patId == null || patId.isEmpty()) {
             response.sendRedirect("DoctorListAllPatientsServlet"); // 患者IDがない場合は一覧へ
             return;
        }
        String cartSessionKey = "drugCart_" + patId; // 患者ごとのカートセッションキー

        if ("addDrugToCart".equals(action)) {
            // D101 機能説明2: 投薬する薬を選択し、投薬量を入力する。 [cite: 61]
            String medicineId = request.getParameter("medicineId");
            String quantityStr = request.getParameter("quantity"); // 数量はプルダウンから
            int quantity = 0;
            String formError = null;

            if (medicineId == null || medicineId.isEmpty()) {
                formError = "薬剤を選択してください。";
            } else if (quantityStr == null || quantityStr.isEmpty()) {
                formError = "数量を選択してください。";
            } else {
                try {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0) {
                        formError = "数量は1以上を選択してください。";
                    }
                } catch (NumberFormatException e) {
                    formError = "数量の形式が正しくありません。";
                }
            }

            if (formError != null) {
                request.setAttribute("formError", formError);
                reloadFormAttributes(request, response, patId); // エラー時もフォーム情報を再表示
                request.getRequestDispatcher("/WEB-INF/jsp/doctor_drug_selection_form.jsp").forward(request, response);
                return;
            }

            List<DrugOrderItemBean> drugCart = (List<DrugOrderItemBean>) session.getAttribute(cartSessionKey);
            if (drugCart == null) {
                drugCart = new ArrayList<>();
            }

            MedicineDAO medicineDAO = new MedicineDAO();
            MedicineBean selectedMedicine = medicineDAO.getMedicineById(medicineId);

            if (selectedMedicine != null) {
                // D101 事後条件: 確定までの間はDBへの登録をしないこと [cite: 61]
                // カートに同じ薬剤が既にあれば数量を更新、なければ新規追加（今回は単純追加の例、必要なら重複チェックと数量加算）
                boolean foundInCart = false;
                for(DrugOrderItemBean item : drugCart){
                    if(item.getMedicineId().equals(selectedMedicine.getMedicineId())){
                        item.setQuantity(item.getQuantity() + quantity); // 既存なら数量加算
                        foundInCart = true;
                        break;
                    }
                }
                if(!foundInCart){
                    DrugOrderItemBean orderItem = new DrugOrderItemBean(
                        selectedMedicine.getMedicineId(),
                        selectedMedicine.getMedicineName(),
                        quantity,
                        selectedMedicine.getUnit()
                    );
                    drugCart.add(orderItem);
                }
                session.setAttribute(cartSessionKey, drugCart);
            } else {
                request.setAttribute("formError", "選択された薬剤情報が見つかりませんでした。");
            }
            // カート追加後は同じ画面を再表示（カート内容が更新される）
            response.sendRedirect(request.getContextPath() + "/DoctorDrugAdministrationServlet?action=start&patId=" + patId);

        } else if ("goToConfirm".equals(action)){
             // D101 機能説明3: 確認画面を表示させる。 [cite: 61]
             List<DrugOrderItemBean> drugCart = (List<DrugOrderItemBean>) session.getAttribute(cartSessionKey);
             if (drugCart == null || drugCart.isEmpty()) {
                 request.setAttribute("formError", "カートに薬剤がありません。薬剤を指示に追加してください。");
                 reloadFormAttributes(request, response, patId);
                 request.getRequestDispatcher("/WEB-INF/jsp/doctor_drug_selection_form.jsp").forward(request, response);
                 return;
             }
             // 確認画面へ必要な情報を渡す (主に患者ID、カートはセッションにある)
             PatientDAO patientDAO = new PatientDAO();
             PatientBean patient = patientDAO.getPatientById(patId);
             request.setAttribute("patient", patient);
             // request.setAttribute("drugCart", drugCart); // JSPでセッションスコープから直接取得も可

             request.getRequestDispatcher("/WEB-INF/jsp/doctor_drug_confirm_page.jsp").forward(request, response);

        } else if ("removeDrugFromCart".equals(action) && patId != null && !patId.isEmpty()) {
            String medicineIdToRemove = request.getParameter("medicineIdToRemove");

            if (medicineIdToRemove != null && !medicineIdToRemove.isEmpty()) {
                List<DrugOrderItemBean> drugCart = (List<DrugOrderItemBean>) session.getAttribute(cartSessionKey);
                if (drugCart != null && !drugCart.isEmpty()) {
                    // 削除対象の薬剤をカートから見つけて削除
                    // ConcurrentModificationExceptionを避けるため、イテレータを使うか、削除対象のインデックスを記録して後で削除する
                    DrugOrderItemBean itemToRemove = null;
                    for (DrugOrderItemBean item : drugCart) {
                        if (item.getMedicineId().equals(medicineIdToRemove)) {
                            itemToRemove = item;
                            break;
                        }
                    }
                    if (itemToRemove != null) {
                        drugCart.remove(itemToRemove);
                        session.setAttribute(cartSessionKey, drugCart); // 更新されたカートをセッションに再保存
                    }
                }
            }
            // 削除後は同じ薬剤選択画面を再表示（カート内容が更新される）
            response.sendRedirect(request.getContextPath() + "/DoctorDrugAdministrationServlet?action=start&patId=" + patId);

        } else if ("confirmTreatment".equals(action) && patId != null && !patId.isEmpty()) {
            // D103 機能説明3: 処置情報を登録する
            cartSessionKey = "drugCart_" + patId;
            List<DrugOrderItemBean> drugCart = (List<DrugOrderItemBean>) session.getAttribute(cartSessionKey);
            EmployeeBean loggedInDoctor = (EmployeeBean) session.getAttribute("loggedInUser");

            if (drugCart == null || drugCart.isEmpty()) {
                request.setAttribute("formError", "カートに処置内容がありません。");
                reloadFormAttributes(request, response, patId); // 薬剤選択画面に戻す
                request.getRequestDispatcher("/WEB-INF/jsp/doctor_drug_selection_form.jsp").forward(request, response);
                return;
            }

            if (loggedInDoctor == null) {
                response.sendRedirect("LoginServlet"); // 医師情報がないのは異常
                return;
            }

            TreatmentDAO treatmentDAO = new TreatmentDAO();
            boolean allSuccess = true;
            Date treatmentDate = new Date(); // 現在日時を処置日とする

            for (DrugOrderItemBean item : drugCart) {
                TreatmentBean treatment = new TreatmentBean();
                treatment.setTreatmentId(treatmentDAO.getNextTreatmentId()); // 新しい処置IDを生成
                treatment.setPatientId(patId);
                treatment.setMedicineId(item.getMedicineId());
                treatment.setQuantity(item.getQuantity());
                treatment.setEmpId(loggedInDoctor.getEmpid()); // ログイン中の医師のID
                treatment.setTreatmentDate(treatmentDate);

                if (!treatmentDAO.registerTreatment(treatment)) {
                    allSuccess = false;
                    // 1件でも登録に失敗したらループを抜けるか、エラーを記録して続けるか
                    // ここでは、1件でも失敗したら全体を失敗とみなす例
                    break;
                }
            }

            if (allSuccess) {
                session.removeAttribute(cartSessionKey); // 成功したらカートをクリア
                request.setAttribute("successMessage", "処置を確定し、登録しました。");
                request.getRequestDispatcher("/WEB-INF/jsp/doctor_treatment_complete.jsp").forward(request, response);
            } else {
                // DB登録失敗時のエラー処理
                request.setAttribute("confirmPageError", "処置の登録中にエラーが発生しました。一部または全ての処置が登録されていない可能性があります。");
                // エラー発生時は、カート情報をクリアせずに確認画面に戻すか、あるいは薬剤選択画面に戻す
                // ここでは確認画面に戻す（カートはクリアしない）
                PatientDAO patientDAO = new PatientDAO(); // 患者情報を再度渡すため
                PatientBean patient = patientDAO.getPatientById(patId);
                request.setAttribute("patient", patient);
                // request.setAttribute("drugCart", drugCart); // JSPでセッションから取得する場合は不要
                request.getRequestDispatcher("/WEB-INF/jsp/doctor_drug_confirm_page.jsp").forward(request, response);
            }
        } else {
            response.sendRedirect("DoctorListAllPatientsServlet"); // 不明なアクション
        }
    } // doPostメソッドの終わり

    
    

    // フォーム再表示のために必要な属性をリクエストにセットするヘルパーメソッド
    private void reloadFormAttributes(HttpServletRequest request, HttpServletResponse response, String patId) throws ServletException, IOException {
        PatientDAO patientDAO = new PatientDAO();
        PatientBean patient = patientDAO.getPatientById(patId);
        MedicineDAO medicineDAO = new MedicineDAO();
        List<MedicineBean> allMedicines = medicineDAO.getAllMedicines();

        request.setAttribute("patient", patient);
        request.setAttribute("allMedicines", allMedicines);

        HttpSession session = request.getSession(false);
        if (session != null) {
            String cartSessionKey = "drugCart_" + patId;
            List<DrugOrderItemBean> drugCart = (List<DrugOrderItemBean>) session.getAttribute(cartSessionKey);
            if (drugCart == null) {
                drugCart = new ArrayList<>();
            }
            request.setAttribute("drugCart", drugCart); // JSPで表示するためにリクエストスコープにもセット
        }
    }
}
