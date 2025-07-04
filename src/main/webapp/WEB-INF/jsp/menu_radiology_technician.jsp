<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.EmployeeBean" %>

<%
    EmployeeBean user = (EmployeeBean) session.getAttribute("loggedInUser");
    if (user == null || user.getRole() != 4) { // レントゲン技師(4)でなければアクセス不可
        response.sendRedirect("LoginServlet");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>レントゲン技師メニュー</title>
    <link rel="stylesheet" href="css/menu_style.css">
</head>
<body class="role-radiology"> <%-- CSSで色分けするためのクラス --%>
    <div class="menu-container">
        <header class="menu-header">
            <h1>レントゲン技師メニュー</h1>
            <div class="user-info">
                ようこそ、<span class="user-name"><c:out value="${sessionScope.loggedInUser.emplname} ${sessionScope.loggedInUser.empfname}"/></span> さん
                <a href="LogoutServlet" class="logout-button">ログアウト</a>
            </div>
        </header>
        <nav class="menu-nav">
            <ul>
                <li><a href="RadiologyOrderListServlet">レントゲン指示一覧・写真登録</a></li>
                <li><a href="EmployeeChangeOwnPasswordServlet">自身のパスワード変更</a></li>
            </ul>
        </nav>
        <main class="menu-content"><p>操作を選択してください。</p></main>
        <footer class="menu-footer"><p>&copy; 2025 医療機関向けシステム</p></footer>
    </div>
</body>
</html>
