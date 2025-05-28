<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.EmployeeBean" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>受付メニュー</title>
</head>
<body>
    <h1>受付メニュー</h1>
    <% EmployeeBean user = (EmployeeBean) session.getAttribute("loggedInUser");
       if (user == null || user.getRole() != 1) { // 1を受付ロールと仮定
           response.sendRedirect("LoginServlet");
           return;
       }
       String userName = (String) session.getAttribute("userName");
    %>
    <p>ようこそ、<%= userName != null ? userName : "受付担当" %> さん</p>
    <ul>
        <li><a href="ReceptionRegisterPatientServlet">患者登録</a></li>
        <li><a href="ReceptionListPatientsServlet">患者一覧・検索・保険証変更</a></li>
        <%-- 他の受付機能へのリンク --%>
        <li><a href="EmployeeChangeOwnPasswordServlet">自身のパスワード変更</a></li>
        <li><a href="LogoutServlet">ログアウト</a></li>
    </ul>
</body>
</html>
