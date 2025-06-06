<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.EmployeeBean" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>管理者メニュー</title>
<style> /* 共通スタイルを外部CSSにすると良い */ </style>
</head>
<body>
    <h1>管理者メニュー</h1>
    <% EmployeeBean user = (EmployeeBean) session.getAttribute("loggedInUser");
       if (user == null || user.getRole() != 3) { // 3を管理者ロール
           response.sendRedirect("LoginServlet"); // 不正アクセスはログインへ
           return;
       }
       String userName = (String) session.getAttribute("userName");
    %>
    <p>ようこそ、<%= userName != null ? userName : "管理者" %> さん</p>
    <ul>
        <li><a href="AdminRegisterEmployeeServlet">従業員登録</a></li>
        <li><a href="AdminListStaffServlet">従業員(受付・医師)一覧・管理</a></li>
		<li><a href="AdminListAdministratorsServlet">管理者一覧・管理</a></li>
        
        <li><a href="AdminAddSupplierServlet">仕入先登録</a></li>   <%-- S3A209担当の仕入先登録 --%>
        <li><a href="AdminListSuppliersServlet">仕入先一覧/検索</a></li> <%-- S3A209担当の仕入先一覧・検索 --%>
        <%-- 他の管理者機能へのリンク --%>
        <%-- menu_admin.jsp のリスト項目に追加 --%>
        <li><a href="AdminAddTabyouinServlet">他病院 新規登録</a></li>
        <li><a href="AdminManageTabyouinServlet">他病院管理</a></li>

        <li><a href="EmployeeChangePasswordServlet">自身のパスワード変更</a></li>
        <li><a href="LogoutServlet">ログアウト</a></li>
    </ul>
</body>
</html>
