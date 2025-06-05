package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.EmployeeDAO;
import model.EmployeeBean;

/**
 * Servlet implementation class C
 */
@WebServlet("/AdminManageEmployeesServlet")
public class AdminManageEmployeesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // ログインチェックと管理者ロールチェック (管理者ロールIDを3と仮定)
        if (session == null || session.getAttribute("loggedInUser") == null ||
            ((EmployeeBean) session.getAttribute("loggedInUser")).getRole() != 3) {
            response.sendRedirect("LoginServlet"); // 不正アクセスはログインページへ
            return;
        }

        String action = request.getParameter("action");
        EmployeeDAO dao = new EmployeeDAO();

        if ("showNameChangeForm".equals(action)) {
            String empId = request.getParameter("empId");
            String source = request.getParameter("source"); // ★ source パラメータを取得
            EmployeeBean employee = dao.getEmployeeById(empId);
            if (employee != null) {
                request.setAttribute("employeeToChange", employee);
                request.setAttribute("sourcePage", source); // ★ source をリクエスト属性にセット
                request.getRequestDispatcher("/WEB-INF/jsp/admin_change_employee_name_form.jsp").forward(request, response);
            } else {
                request.setAttribute("listErrorMessage", "指定された従業員IDが見つかりません。");
                // 遷移元に応じて戻り先を変える場合はここも考慮が必要だが、一旦listAllEmployeesへ
                if ("staffList".equals(source)) {
                    response.sendRedirect("AdminListStaffServlet");
                } else if ("adminList".equals(source)) {
                    response.sendRedirect("AdminListAdministratorsServlet");
                } else {
                response.sendRedirect("ReturnToMenuServlet");
                }
            } 
        }else { // デフォルトは一覧表示
        	response.sendRedirect("ReturnToMenuServlet");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null ||
            ((EmployeeBean) session.getAttribute("loggedInUser")).getRole() != 3) {
            response.sendRedirect("LoginServlet");
            return;
        }

        String action = request.getParameter("action");
        EmployeeDAO dao = new EmployeeDAO();

        if ("updateName".equals(action)) {
            // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
            // ここからが、ご提示いただいたコードブロックで置き換える部分です。
            // もし既存の "updateName" アクションの処理があれば、それを全て削除し、
            // ご提示のコードをここに挿入します。
            // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼

            String empId = request.getParameter("empIdToChange");
            String newLname = request.getParameter("newLname");
            String newFname = request.getParameter("newFname");
            // String errorMessage = null; // ご提示のコードではここで宣言されていないが、下のelseブロックで使われている
            // String successMessage = null; // ご提示のコードではここで宣言されていないが、下のif(success)ブロックで使われている
            // 必要に応じて、これらの変数はこのスコープの先頭で宣言してください。
            // 例えば、以下のようにします。
            String errorMessage = null;
            String successMessage = null;


            // バリデーション (ご提示のコードには明示的なバリデーションが少ないですが、
            // DAOのupdateEmployeeName内や、この前に必要に応じて追加してください)
            if (empId == null || empId.trim().isEmpty() ||
                newLname == null || newLname.trim().isEmpty() ||
                newFname == null || newFname.trim().isEmpty()) {
                
                errorMessage = "従業員ID、姓、名のすべてを入力してください。";
                EmployeeBean employee = dao.getEmployeeById(empId); // フォーム再表示用に現在の情報を取得
                request.setAttribute("employeeToChange", employee);
                request.setAttribute("errorMessage_nameChange", errorMessage);
                // sourcePageもフォームに渡す必要があるため、リクエストから取得してセット
                String sourcePageFromForm = request.getParameter("sourcePage");
                request.setAttribute("sourcePage", sourcePageFromForm);
                request.getRequestDispatcher("/WEB-INF/jsp/admin_change_employee_name_form.jsp").forward(request, response);
                return; // バリデーションエラーなのでここで処理終了
            }


            boolean success = dao.updateEmployeeName(empId, newLname.trim(), newFname.trim());
            String sourcePage = request.getParameter("sourcePage"); // hidden fieldから取得

            if (success) {
                successMessage = "従業員ID: " + empId + " の氏名を変更しました。";
                // セッションにフラッシュメッセージとして成功メッセージを格納
                session.setAttribute("listSuccessMessage_employee_management", successMessage); // 属性名をより具体的に

                if ("staffList".equals(sourcePage)) {
                    response.sendRedirect("AdminListStaffServlet");
                } else if ("adminList".equals(sourcePage)) {
                    response.sendRedirect("AdminListAdministratorsServlet");
                } else {
                    // デフォルトの戻り先（例: 管理者メニューや、もしあれば全従業員管理のトップなど）
                    // ここでは、sourcePageが不明な場合はAdminListStaffServletに戻す例
                    // (あるいは、より汎用的な全従業員一覧 AdminManageEmployeesServlet のdoGetへリダイレクトなど)
                    response.sendRedirect("AdminListStaffServlet"); // または AdminManageEmployeesServlet の一覧表示アクションへ
                }
            } else {
                errorMessage = "氏名の変更に失敗しました。データベースエラーの可能性があります。";
                EmployeeBean employee = dao.getEmployeeById(empId); // フォーム再表示用に現在の情報を取得
                request.setAttribute("employeeToChange", employee);
                request.setAttribute("errorMessage_nameChange", errorMessage);
                request.setAttribute("sourcePage", sourcePage); // sourcePageも再度セット
                request.getRequestDispatcher("/WEB-INF/jsp/admin_change_employee_name_form.jsp").forward(request, response);
            }
            // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲
            // ご提示いただいたコードブロックの終わりはここまでです。
            // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

        } else {
            // 他のPOSTアクションがあればここに記述 (例: 削除処理など)
            // 不明なPOSTアクションの場合は、デフォルトの一覧表示などにフォールバック
        	response.sendRedirect("ReturnToMenuServlet"); // listAllEmployeesはdoGet内のものを想定、適宜調整
        }
    }
}
