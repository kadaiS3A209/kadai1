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
 * Servlet implementation class ReceptionChangeInsuranceServlet
 */
@WebServlet("/ReceptionChangeInsuranceServlet")
public class ReceptionChangeInsuranceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String patId = request.getParameter("patId");

        if ("showForm".equals(action) && patId != null && !patId.isEmpty()) {
            PatientDAO dao = new PatientDAO();
            PatientBean patient = dao.getPatientById(patId);
            if (patient != null) {
                request.setAttribute("patientToChange", patient);
                request.getRequestDispatcher("/WEB-INF/jsp/reception_change_insurance_form.jsp").forward(request, response);
            } else {
                // エラー処理: 患者が見つからない
                response.sendRedirect("ReceptionListPatientsServlet?error=patientNotFound");
            }
        } else {
            response.sendRedirect("ReceptionListPatientsServlet"); // 不正なアクセス
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(); // P102の基本設計書には確認画面の指示あり
        String action = request.getParameter("action");
        String patId = request.getParameter("patIdToChange"); // hidden field

        PatientDAO dao = new PatientDAO();
        PatientBean currentPatient = dao.getPatientById(patId); // 現在の情報を取得（エラー時表示や比較用）
        request.setAttribute("patientToChange", currentPatient); // 常にフォームに元の患者情報を渡す

        if (currentPatient == null) {
            request.setAttribute("formError", "対象の患者が見つかりません。");
            request.getRequestDispatcher("/WEB-INF/jsp/reception_change_insurance_form.jsp").forward(request, response);
            return;
        }

        if ("confirmChange".equals(action)) { // 基本設計書通りの確認ステップを挟む場合
        	
        	String newHokenmeiInput = request.getParameter("newHokenmei");
            String newHokenexpStrInput = request.getParameter("newHokenexp");

            StringBuilder errors = new StringBuilder();
            Date parsedNewHokenexp = null; // フォームから入力された新しい有効期限をパースしたもの

            // フォームからの入力値をトリム（nullチェックも兼ねて）
            String trimmedNewHokenmei = (newHokenmeiInput != null) ? newHokenmeiInput.trim() : "";
            String trimmedNewHokenexpStr = (newHokenexpStrInput != null) ? newHokenexpStrInput.trim() : "";

            boolean hokenmeiFieldSubmitted = !trimmedNewHokenmei.isEmpty();
            boolean hokenexpFieldSubmitted = !trimmedNewHokenexpStr.isEmpty();

            // --- 有効期限文字列のパース ---
            if (hokenexpFieldSubmitted) {
                try {
                    parsedNewHokenexp = DATE_FORMAT.parse(trimmedNewHokenexpStr); // DATE_FORMATは "yyyy-MM-dd"
                } catch (ParseException e) {
                    errors.append("有効期限の形式が正しくありません (YYYY-MM-DD)。<br>");
                }
            }

            // --- 変更が意図されているかどうかの判定 ---
            // 記号番号が現在のものと異なり、かつ入力されていれば変更意図あり
            boolean hokenmeiChangeIntended = hokenmeiFieldSubmitted && !trimmedNewHokenmei.equals(currentPatient.getHokenmei());
            // 有効期限が現在のものと異なり、かつ入力・パース成功していれば変更意図あり
            boolean hokenexpChangeIntended = parsedNewHokenexp != null &&
                                            (currentPatient.getHokenexp() == null ||
                                             !areDatesEqualIgnoringTime(parsedNewHokenexp, currentPatient.getHokenexp()));

            // ★★★ バリデーションチェック開始 ★★★

            // 何も変更がなければエラー（または「変更なし」として処理）
            if (!hokenmeiChangeIntended && !hokenexpChangeIntended) {
                errors.append("変更する新しい情報が入力されていないか、現在の情報と同じです。<br>");
            } else {
                // --- 1. 保険証記号番号が変更される場合のチェック ---
                if (hokenmeiChangeIntended) {
                    // Test Case: 保健証記号番号が入力され、日付が入力されない場合変更できない [cite: 6]
                    if (!hokenexpFieldSubmitted) { // 日付文字列の提出がそもそもない
                        errors.append("保険証記号番号を変更する場合、新しい有効期限も入力してください。<br>");
                    } else if (parsedNewHokenexp == null) { // 日付文字列は提出されたがパース失敗
                        // (既に上記「有効期限の形式が正しくありません」エラーが追加されているはず)
                    } else {
                        // 日付も入力されている場合: DBの期限より新しくなければならない
                        // Test Case: 保険証記号番号が入力され、日付がDB登録の期限より古い場合変更できない [cite: 6]
                        // Test Case: 保険証記号番号が入力され、日付がDB登録の期限と等しい場合変更できない [cite: 6]
                        if (currentPatient.getHokenexp() != null && !isNewDateStrictlyAfterOldDate(parsedNewHokenexp, currentPatient.getHokenexp())) {
                            errors.append("保険証記号番号を変更する場合、有効期限は現在登録されている有効期限より新しい日付にしてください。<br>");
                        }
                        // Test Case: 保健証記号番号が入力され、かつDB登録の期限より新しい日付で変更できる (これは成功パターンなのでエラーチェックはなし) [cite: 6]
                    }
                }
                // --- 2. 有効期限のみが変更される場合のチェック ---
                else if (hokenexpChangeIntended) { // hokenmeiChangeIntended は false で、hokenexpChangeIntended は true
                    // Test Case: 有効期限が、DB登録の期限より古い日付で変更できない [cite: 6]
                    // Test Case: 有効期限が、DB登録の期限と同一日付で変更できない [cite: 6]
                    if (currentPatient.getHokenexp() != null && !isNewDateStrictlyAfterOldDate(parsedNewHokenexp, currentPatient.getHokenexp())) {
                        errors.append("有効期限を変更する場合、現在登録されている有効期限より新しい日付にしてください。<br>");
                    }
                    // Test Case: 有効期限が、DB登録の期限より新しい日付で変更できる (成功パターン) [cite: 6]
                    // Test Case: うるう年のうるう日 (成功パターン、日付パースとDateオブジェクトが正しく扱えればOK) [cite: 6]
                }
            }
            // ★★★ バリデーションチェック終了 ★★★

            if (errors.length() > 0) {
                request.setAttribute("formError", errors.toString());
                // エラー時にユーザーが入力した値をフォームに戻すための準備
                PatientBean userInputBean = new PatientBean(); // 新しいBeanか、currentPatientをコピーして使う
                userInputBean.setPatId(currentPatient.getPatId()); // IDは変わらない
                userInputBean.setHokenmei(hokenmeiFieldSubmitted ? trimmedNewHokenmei : currentPatient.getHokenmei());
                userInputBean.setHokenexp(parsedNewHokenexp); // パースできた日付、またはエラーならnull
                request.setAttribute("userInput", userInputBean); // JSPでこのBeanから値を表示

                request.getRequestDispatcher("/WEB-INF/jsp/reception_change_insurance_form.jsp").forward(request, response);
                return;
            }

            // バリデーションOKの場合、確認画面に進むためのデータを準備
            PatientBean patientForConfirm = new PatientBean();
            patientForConfirm.setPatId(currentPatient.getPatId());
            patientForConfirm.setPatFname(currentPatient.getPatFname());
            patientForConfirm.setPatLname(currentPatient.getPatLname());

            // 実際に変更する値を設定
            String finalNewHokenmei = hokenmeiChangeIntended ? trimmedNewHokenmei : currentPatient.getHokenmei();
            Date finalNewHokenexp = hokenexpChangeIntended ? parsedNewHokenexp : currentPatient.getHokenexp();

            patientForConfirm.setHokenmei(finalNewHokenmei);
            patientForConfirm.setHokenexp(finalNewHokenexp);

            session.setAttribute("patientForInsuranceConfirm", patientForConfirm);
            // 変更後の値をセッションに保持（executeUpdateアクションで使用）
            session.setAttribute("newHokenmeiForConfirm", finalNewHokenmei);
            session.setAttribute("newHokenexpForConfirm", finalNewHokenexp);


            request.getRequestDispatcher("/WEB-INF/jsp/reception_change_insurance_confirm.jsp").forward(request, response);

    // ... (executeUpdate アクションのロジックは前回提示したものをベースに、
//          セッションから newHokenmeiForConfirm と newHokenexpForConfirm を取得してDAOに渡す)
//          DAOの updatePatientInsurance は、渡された値で更新するように修正が必要かもしれません。
//          (nullでない方のフィールドだけを更新する、あるいは両方更新する)
//          前回のDAOはnullでないフィールドだけを更新するようになっていました。

    // ... (以下、executeUpdateアクション内のDAO呼び出し部分の修正イメージ)
    // PatientBean patientToUpdateData = (PatientBean) session.getAttribute("patientForInsuranceConfirm");
    // String confirmedNewHokenmei = (String) session.getAttribute("newHokenmeiForConfirm");
    // Date confirmedNewHokenexp = (Date) session.getAttribute("newHokenexpForConfirm");
    //
    // boolean success = dao.updatePatientInsurance(patId, confirmedNewHokenmei, confirmedNewHokenexp);
    // このままだと、変更しなかったフィールドも現在の値（上記finalHokenmei/finalHokenexp）で上書きされる。
    // DAOのupdatePatientInsuranceが、nullの引数は無視するように作られていれば、
    // 変更しないフィールドにはnullを渡す。
    // String hokenmeiForDb = hokenmeiChangeIntended ? finalNewHokenmei : null;
    // Date hokenexpForDb = hokenexpChangeIntended ? finalNewHokenexp : null;
    // boolean success = dao.updatePatientInsurance(patId, hokenmeiForDb, hokenexpForDb);
        	
        	
            
            
            
            

            

        } else if ("executeUpdate".equals(action)) { // 確認画面からの最終実行
            PatientBean patientToUpdate = (PatientBean) session.getAttribute("patientForInsuranceConfirm");
            String confirmedNewHokenmei = (String) session.getAttribute("newHokenmeiForConfirm");
            Date confirmedNewHokenexp = (Date) session.getAttribute("newHokenexpForConfirm");

            if (patientToUpdate == null) {
                request.setAttribute("formError", "セッション情報が無効です。やり直してください。");
                request.getRequestDispatcher("/WEB-INF/jsp/reception_change_insurance_form.jsp").forward(request, response);
                return;
            }
            
            // 実際にDBに渡す値を決定（変更が意図されたフィールドのみ）
            String finalHokenmei = null;
            Date finalHokenexp = null;
            boolean hokenmeiActuallyChanged = confirmedNewHokenmei != null && !confirmedNewHokenmei.equals(currentPatient.getHokenmei());
            boolean hokenexpActuallyChanged = confirmedNewHokenexp != null && !confirmedNewHokenexp.equals(currentPatient.getHokenexp());

            if (hokenmeiActuallyChanged) finalHokenmei = confirmedNewHokenmei;
            if (hokenexpActuallyChanged) finalHokenexp = confirmedNewHokenexp;

            // どちらも変更がない場合は何もしないか、メッセージを出す
            if (finalHokenmei == null && finalHokenexp == null) {
                 request.setAttribute("successMessage", "変更はありませんでした。");
                 request.getRequestDispatcher("/WEB-INF/jsp/reception_change_insurance_form.jsp").forward(request, response);
                 session.removeAttribute("patientForInsuranceConfirm");
                 session.removeAttribute("newHokenmeiForConfirm");
                 session.removeAttribute("newHokenexpForConfirm");
                 return;
            }


            boolean success = dao.updatePatientInsurance(patId, finalHokenmei, finalHokenexp);

            session.removeAttribute("patientForInsuranceConfirm");
            session.removeAttribute("newHokenmeiForConfirm");
            session.removeAttribute("newHokenexpForConfirm");

            if (success) {
                request.setAttribute("successMessage", "保険証情報を変更しました。"); // テストケース: 「変更完了と表示される」
            } else {
                request.setAttribute("formError", "保険証情報の変更に失敗しました。");
            }
            // 完了後、フォーム画面にメッセージを表示して戻すか、一覧にリダイレクト
            request.getRequestDispatcher("/WEB-INF/jsp/reception_change_insurance_form.jsp").forward(request, response);
        } else {
             response.sendRedirect("ReceptionListPatientsServlet"); // 不明なアクション
        }
    }
    
    private java.text.SimpleDateFormat DATE_ONLY_FORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd");

    /**
     * 二つの日付が同じ日であるかを確認します（時刻部分は無視）。
     * @param d1 日付1
     * @param d2 日付2
     * @return 同じ日であればtrue
     */
    private boolean areDatesEqualIgnoringTime(Date d1, Date d2) {
        if (d1 == null && d2 == null) return true;
        if (d1 == null || d2 == null) return false;
        return DATE_ONLY_FORMAT.format(d1).equals(DATE_ONLY_FORMAT.format(d2));
    }

    /**
     * newDateがoldDateより厳密に後であるかを確認します（時刻部分は無視）。
     * @param newDate 新しい日付
     * @param oldDate 古い日付
     * @return newDateがoldDateより後であればtrue
     */
    private boolean isNewDateStrictlyAfterOldDate(Date newDate, Date oldDate) {
        if (newDate == null || oldDate == null) {
            // 要件に応じて、nullの場合の比較を定義 (例: 新しい日付がnullなら常にfalse)
            return false;
        }
        try {
            Date newD = DATE_ONLY_FORMAT.parse(DATE_ONLY_FORMAT.format(newDate));
            Date oldD = DATE_ONLY_FORMAT.parse(DATE_ONLY_FORMAT.format(oldDate));
            return newD.after(oldD);
        } catch (ParseException e) { // 通常発生しないはず
            e.printStackTrace();
            return false;
        }
    }
}
