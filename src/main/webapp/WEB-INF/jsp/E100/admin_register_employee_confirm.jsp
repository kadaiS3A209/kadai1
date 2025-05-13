<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.EmployeeBean" %> <%-- EmployeeBeanのパスに合わせて変更 --%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>従業員登録 - 確認</title>
<style>
    /* 入力画面と同様のスタイル */
    body { font-family: sans-serif; }
    .confirm-item { margin-bottom: 10px; }
    .label { font-weight: bold; display: inline-block; width: 150px; }
    .value { display: inline-block; }
    .button-group { margin-top: 20px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; margin-right: 10px;}
    .button:hover { background-color: #0056b3; }
    .back-button { background-color: #6c757d; }
    .back-button:hover { background-color: #5a6268; }
</style>
</head>
<body>

    <h1>従業員登録 - 確認</h1>

    <%
        // サーブレットから渡された（セッションに保存された）従業員情報を取得
        EmployeeBean employee = (EmployeeBean) session.getAttribute("tempEmployee");
        if (employee == null) {
            // エラー処理: セッション情報がない場合は入力画面に戻すなど
            response.sendRedirect("AdminRegisterEmployeeServlet"); // GETリクエストで入力画面へ
            return;
        }

        // ロールの表示名を決定 (例)
        String roleName = "";
        if (employee.getRole() == 1) {
            roleName = "受付";
        } else if (employee.getRole() == 2) {
            roleName = "医師";
        } else if (employee.getRole() == 3 ){
        	roleName = "管理者";
        }else {
            roleName = "不明なロール"; // エラーケース
        }
    %>

    <p>以下の内容で登録します。よろしいですか？</p>

    <div class="confirm-item">
        <span class="label">従業員ID:</span>
        <span class="value"><%= employee.getEmpid() %></span> <%-- 自動生成されたID --%>
    </div>
    <div class="confirm-item">
        <span class="label">姓:</span>
        <span class="value"><%= employee.getEmplname() %></span>
    </div>
    <div class="confirm-item">
        <span class="label">名:</span>
        <span class="value"><%= employee.getEmpfname() %></span>
    </div>
    <div class="confirm-item">
        <span class="label">ロール:</span>
        <span class="value"><%= roleName %> (<%= employee.getRole() %>)</span>
    </div>
    <div class="confirm-item">
        <span class="label">パスワード:</span>
        <span class="value">********</span> <%-- パスワードは表示しない --%>
    </div>

    <div class="button-group">
        <%-- 登録実行用フォーム --%>
        <form action="AdminRegisterEmployeeServlet" method="post" style="display: inline;">
             <%-- この隠しフィールドで、確認画面からの登録実行であることを示す --%>
            <input type="hidden" name="action" value="register">
            <button type="submit" class="button">登録する</button>
        </form>

        <%-- 修正用フォーム (入力画面に戻る) --%>
        <form action="AdminRegisterEmployeeServlet" method="get" style="display: inline;">
            <%-- セッション情報は残っているので、GETでサーブレットを呼び出すだけで入力画面が復元される想定 --%>
             <%-- あるいは、action=backのようなパラメータをPOSTするなど、実装方法は複数考えられます --%>
            <button type="submit" class="button back-button">修正する</button>
        </form>
    </div>

</body>
</html>