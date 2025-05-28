<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.EmployeeBean" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>医師メニュー</title>
</head>
<body>
    <h1>医師メニュー</h1>
    <% EmployeeBean user = (EmployeeBean) session.getAttribute("loggedInUser");
       if (user == null || user.getRole() != 2) { // 2を医師ロールと仮定
           response.sendRedirect("LoginServlet");
           return;
       }
       String userName = (String) session.getAttribute("userName");
    %>
    <p>ようこそ、<%= userName != null ? userName : "医師" %> さん</p>
    <ul>
        <li><a href="DoctorListAllPatientsServlet">患者一覧表示</a></li>
        <li><a href="doctor_select_patient_for_treatment.jsp">薬剤投与指示</a></li> <%-- 薬剤投与の起点となる画面 --%>
        <li><a href="doctor_treatment_history_search.jsp">処置履歴確認</a></li> <%-- 処置履歴確認の起点 --%>
        <%-- 他の医師機能へのリンク --%>
        <li><a href="EmployeeChangeOwnPasswordServlet">自身のパスワード変更</a></li>
        <li><a href="LogoutServlet">ログアウト</a></li>
    </ul>
</body>
</html>
