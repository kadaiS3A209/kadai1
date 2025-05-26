package servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.PatientDAO;
import model.PatientBean;

/**
 * Servlet implementation class ReceptionRegisterPatientServlet
 */
@WebServlet("/ReceptionRegisterPatientServlet")
public class ReceptionRegisterPatientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GETリクエストは入力フォームを表示 (修正で戻る場合もセッションの値をJSPで参照)
        request.getRequestDispatcher("/WEB-INF/jsp/reception_register_patient_form.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        PatientDAO dao = new PatientDAO(); // 実際には依存性注入などを検討

        if ("confirm".equals(action)) {
            // --- 入力画面からの確認処理 ---
            String patId = request.getParameter("patid");
            String patLname = request.getParameter("patlname");
            String patFname = request.getParameter("patfname");
            String hokenmei = request.getParameter("hokenmei");
            String hokenexpStr = request.getParameter("hokenexp"); // input type="date" からは yyyy-MM-dd 形式

            StringBuilder errors = new StringBuilder();
            // 必須チェックと形式チェック
            if (patId == null || patId.trim().isEmpty()) errors.append("患者IDは必須です。<br>");
            else if (patId.trim().length() > 8) errors.append("患者IDは8文字以内です。<br>");
            else if (dao.isPatientIdExists(patId.trim())) errors.append("その患者IDは既に使用されています。<br>");

            if (patLname == null || patLname.trim().isEmpty()) errors.append("患者姓は必須です。<br>");
            if (patFname == null || patFname.trim().isEmpty()) errors.append("患者名は必須です。<br>");
            if (hokenmei == null || hokenmei.trim().isEmpty()) errors.append("保険証記号番号は必須です。<br>");

            Date hokenexpDate = null;
            if (hokenexpStr == null || hokenexpStr.trim().isEmpty()) {
                errors.append("有効期限は必須です。<br>");
            } else {
                try {
                    // 有効期限のパース (HTML5の<input type="date">はyyyy-MM-dd形式で送ってくる)
                    hokenexpDate = DATE_FORMAT.parse(hokenexpStr.trim());
                    // ここでさらに日付の妥当性チェック（例: 未来の日付かなど）も可能
                } catch (ParseException e) {
                    errors.append("有効期限の形式が正しくありません (YYYY-MM-DD)。<br>");
                }
            }

            PatientBean tempPatient = new PatientBean(); // エラー時にも値を保持するためBeanに一旦格納
            tempPatient.setPatId(patId);
            tempPatient.setPatLname(patLname);
            tempPatient.setPatFname(patFname);
            tempPatient.setHokenmei(hokenmei);
            tempPatient.setHokenexp(hokenexpDate); // パース後のDateオブジェクト

            if (errors.length() > 0) {
                request.setAttribute("formError", errors.toString());
                request.setAttribute("prevPatientInput", tempPatient); // 入力値をBeanで戻す
                request.getRequestDispatcher("/WEB-INF/jsp/reception_register_patient_form.jsp").forward(request, response);
                return;
            }

            session.setAttribute("tempPatient", tempPatient);
            request.getRequestDispatcher("/WEB-INF/jsp/reception_register_patient_confirm.jsp").forward(request, response);

        } else if ("register".equals(action)) {
            // --- 確認画面からの登録実行処理 ---
            PatientBean patientToRegister = (PatientBean) session.getAttribute("tempPatient");

            if (patientToRegister == null) {
                request.setAttribute("formError", "セッション情報が見つかりません。最初からやり直してください。");
                request.getRequestDispatcher("/WEB-INF/jsp/reception_register_patient_form.jsp").forward(request, response);
                return;
            }

            boolean success = dao.registerPatient(patientToRegister);
            session.removeAttribute("tempPatient");

            if (success) {
                request.setAttribute("message", "患者情報を登録しました。");
                request.getRequestDispatcher("/WEB-INF/jsp/reception_register_patient_complete.jsp").forward(request, response);
            } else {
                request.setAttribute("formError", "データベースへの登録に失敗しました。");
                request.setAttribute("prevPatientInput", patientToRegister);
                request.getRequestDispatcher("/WEB-INF/jsp/reception_register_patient_form.jsp").forward(request, response);
            }
        } else {
            // 不明なアクションは入力フォームへ
            response.sendRedirect("ReceptionRegisterPatientServlet");
        }
    }
}
