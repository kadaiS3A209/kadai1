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
        <li><a href="ReceptionSearchPatientByNameServlet">患者名検索</a></li> <%-- S3A209担当 --%>
        <li><a href="ReceptionCheckExpiredInsuranceServlet">保険証期限切れ確認</a></li> <%-- S3A209担当 --%>
        <%-- 他の受付機能へのリンク --%>
        <li><a href="EmployeeChangePasswordServlet">自身のパスワード変更</a></li>
        <li><a href="LogoutServlet">ログアウト</a></li>
    </ul>
</body>
</html>
