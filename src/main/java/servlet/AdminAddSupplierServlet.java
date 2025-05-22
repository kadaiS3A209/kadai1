// (パッケージ名は適切に設定してください例: com.example.controller)
package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.ShiiregyoshaDAO;   // パスを合わせる
import model.ShiiregyoshaBean; // パスを合わせる

@WebServlet("/AdminAddSupplierServlet")
public class AdminAddSupplierServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GETリクエストは入力フォームを表示
        // 確認画面から「修正」で戻ってきた場合はセッションに tempSupplier があるはず
        request.getRequestDispatcher("/WEB-INF/jsp/admin_add_supplier_form.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        ShiiregyoshaDAO dao = new ShiiregyoshaDAO(); // 実際にはDIやContextから取得を検討

        if ("confirm".equals(action)) {
            // --- 入力画面からの確認処理 ---
            String shiireId = request.getParameter("shiireid");
            String shiireMei = request.getParameter("shiiremei");
            String shiireAddress = request.getParameter("shiireaddress");
            String shiireTel = request.getParameter("shiiretel");
            String shihonkinStr = request.getParameter("shihonkin");
            String noukiStr = request.getParameter("nouki");

            ShiiregyoshaBean supplier = new ShiiregyoshaBean();
            supplier.setShiireId(shiireId);
            supplier.setShiireMei(shiireMei);
            supplier.setShiireAddress(shiireAddress);
            supplier.setShiireTel(shiireTel);

            // --- サーバーサイドバリデーション ---
            StringBuilder errors = new StringBuilder();
            // 必須チェック (テストケース: 全項目空欄で登録できない )
            if (shiireId == null || shiireId.trim().isEmpty()) errors.append("仕入先IDは必須です。<br>");
            else if (shiireId.trim().length() > 8) errors.append("仕入先IDは8文字以内です。<br>");
            else if (dao.isShiireIdExists(shiireId.trim())) errors.append("その仕入先IDは既に使用されています。<br>"); // テストケース: 重複ID 

            if (shiireMei == null || shiireMei.trim().isEmpty()) errors.append("仕入先名は必須です。<br>");
            if (shiireAddress == null || shiireAddress.trim().isEmpty()) errors.append("住所は必須です。<br>");
            if (shiireTel == null || shiireTel.trim().isEmpty()) errors.append("電話番号は必須です。<br>");
            else if (!shiireTel.trim().matches("^[0-9()-]+$")) errors.append("電話番号の形式が正しくありません。(使用可能な文字: 数字, -, (, ) )<br>"); // テストケース: 電話番号形式 

            int shihonkin = 0;
            if (shihonkinStr == null || shihonkinStr.trim().isEmpty()) errors.append("資本金は必須です。<br>");
            else {
                try {
                    shihonkin = Integer.parseInt(shihonkinStr.trim().replace(",", "")); // カンマ除去
                    if (shihonkin < 0) errors.append("資本金は0以上の値を入力してください。<br>");
                    supplier.setShihonkin(shihonkin);
                } catch (NumberFormatException e) {
                    errors.append("資本金は数値で入力してください。<br>"); // テストケース: 資本金形式 
                }
            }

            int nouki = 0;
            if (noukiStr == null || noukiStr.trim().isEmpty()) errors.append("納期は必須です。<br>");
            else {
                try {
                    nouki = Integer.parseInt(noukiStr.trim());
                    if (nouki < 0) errors.append("納期は0以上の値を入力してください。<br>");
                    supplier.setNouki(nouki);
                } catch (NumberFormatException e) {
                    errors.append("納期は数値（日数）で入力してください。<br>"); // テストケース: 納期形式 
                }
            }

            if (errors.length() > 0) {
                request.setAttribute("formError", errors.toString());
                request.setAttribute("prevSupplierInput", supplier); // 入力値をBeanで戻す
                request.getRequestDispatcher("/WEB-INF/jsp/admin_add_supplier_form.jsp").forward(request, response);
                return;
            }

            session.setAttribute("tempSupplier", supplier);
            request.getRequestDispatcher("/WEB-INF/jsp/admin_add_supplier_confirm.jsp").forward(request, response);

        } else if ("register".equals(action)) {
            // --- 確認画面からの登録実行処理 ---
            ShiiregyoshaBean supplierToRegister = (ShiiregyoshaBean) session.getAttribute("tempSupplier");

            if (supplierToRegister == null) {
                request.setAttribute("formError", "セッション情報が見つかりません。最初からやり直してください。");
                request.getRequestDispatcher("/WEB-INF/jsp/admin_add_supplier_form.jsp").forward(request, response);
                return;
            }

            boolean success = dao.registerSupplier(supplierToRegister);
            session.removeAttribute("tempSupplier");

            if (success) {
                session.setAttribute("message", "仕入先を登録しました。"); // テストケース: 「登録しました」と表示される 
                request.getRequestDispatcher("/WEB-INF/jsp/admin_add_supplier_complete.jsp").forward(request, response); // PRGパターンで完了画面へ
            } else {
                request.setAttribute("formError", "データベースへの登録に失敗しました。");
                request.setAttribute("prevSupplierInput", supplierToRegister); // 失敗時は入力値を戻す
                request.getRequestDispatcher("/WEB-INF/jsp/admin_add_supplier_form.jsp").forward(request, response);
            }
        } else if ("complete".equals(action)) {
             // 登録完了画面表示
             request.getRequestDispatcher("/WEB-INF/jsp/admin_add_supplier_complete.jsp").forward(request, response);
        }
        else {
            response.sendRedirect("AdminAddSupplierServlet"); // 不明なアクションは入力画面へ
        }
    }
}
